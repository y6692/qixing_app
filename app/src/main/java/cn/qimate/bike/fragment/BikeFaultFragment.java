package cn.qimate.bike.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadOptions;
import com.zxing.lib.scaner.activity.ActivityScanerCode;
import com.zxing.lib.scaner.activity.ActivityScanerCode2;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.nostra13.universalimageloader.core.ImageLoader;
import cn.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import cn.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import cn.qimate.bike.BuildConfig;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.CrashHandler;
import cn.qimate.bike.activity.LoginActivity;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.base.BaseFragment;
import cn.qimate.bike.base.BaseViewHolder;
import cn.qimate.bike.core.common.BitmapUtils1;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.NetworkUtils;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.core.widget.MyGridView;
import cn.qimate.bike.img.NetUtil;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UpTokenBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.QiNiuInitialize;
import cn.qimate.bike.util.ToastUtil;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2017/2/14 0014.
 */
@SuppressLint("NewApi")
public class BikeFaultFragment extends BaseFragment implements View.OnClickListener{

    private View v;
    Unbinder unbinder;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();

    private Context context;
    private Activity activity;
//    private LoadingDialog loadingDialog;
    private ImageView backImg;
    private TextView title;
    private Button takePhotoBtn,pickPhotoBtn,cancelBtn;

    private EditText bikeCodeEdit;
    private ImageView Tag1,Tag2,Tag3,Tag4,Tag5,Tag6;
    private TextView Tag1_1,Tag1_2,Tag1_3;
    private LinearLayout ll_restCauseEdit;
    private EditText restCauseEdit;
    private EditText addressEdit;
    private PhotoGridviewAdapter myAdapter;
    private MyGridView photoMyGridview;
    private Button submitBtn;

    private boolean isSelected1 = false;
    private boolean isSelected1_1 = false;
    private boolean isSelected1_2 = false;
    private boolean isSelected1_3 = false;
    private boolean isSelected2 = false;
    private boolean isSelected3 = false;
    private boolean isSelected4 = false;
    private boolean isSelected5 = false;
    private boolean isSelected6 = false;



    private List<String> TagsList;
    private List<String> TagsList1;
    private List<Bitmap> imageUrlList;
    private List<String> imageList = new ArrayList<>();
    final static int MAX = 4;

//    private List<Bitmap> bitmapList;

    /**
     * 弹窗背景
     */
    private ImageView iv_popup_window_back;
    /**
     * 弹窗容器
     */
    private RelativeLayout rl_popup_window;

    private String imgUrl = Urls.uploadsImg;
    private Uri imageUri;
    private final String IMAGE_FILE_NAME = "picture.jpg";// 照片文件名称
    private String urlpath; // 图片本地路径
    private String resultStr = ""; // 服务端返回结果集
    private final int REQUESTCODE_PICK = 0; // 相册选图标记
    private final int REQUESTCODE_TAKE = 1; // 相机拍照标记
    private final int REQUESTCODE_CUTTING = 2; // 图片裁切标记

    private double latitude = 0.0;
    private double longitude = 0.0;

    public static boolean isForeground = false;
    private String type = "";

    private boolean isComplete = false;

    private ImageView iv_scan;
    private int pos;
    private boolean isSubmit = false;

    private String bikeCode = "";
    private String fid = "";

    private String upToken = "";
    private Bitmap upBitmap;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_bike_fault, null);
        unbinder = ButterKnife.bind(this, v);

        return v;
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        activity = getActivity();
//        datas = new ArrayList<>();

        TagsList = new ArrayList<>();
        TagsList1 = new ArrayList<>();
        imageUrlList = new ArrayList<Bitmap>();
//        bitmapList = new ArrayList<Bitmap>();

        type = SharedPreferencesUrls.getInstance().getString("type", "");
        m_nowMac = SharedPreferencesUrls.getInstance().getString("m_nowMac", "");
//        bikeCode = getIntent().getStringExtra("bikeCode");

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            int checkPermission = activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
//            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
//                    requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION },0);
//                } else {
//                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开位置权限！")
//                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                    finishMine();
//                                }
//                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                            BikeFaultFragment.this.requestPermissions(
//                                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },0);
//                        }
//                    });
//                    customBuilder.create().show();
//                }
//                return;
//            }
//        }
        initView();

    }


    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        backImg = activity.findViewById(R.id.mainUI_title_backBtn);

        imageUri = Uri.parse("file:///sdcard/temp.jpg");
        iv_popup_window_back = activity.findViewById(R.id.popupWindow_back);
        rl_popup_window = activity.findViewById(R.id.popupWindow);

        takePhotoBtn = activity.findViewById(R.id.takePhotoBtn);
        pickPhotoBtn = activity.findViewById(R.id.pickPhotoBtn);
        cancelBtn = activity.findViewById(R.id.cancelBtn);

        pickPhotoBtn.setVisibility(View.GONE);
        takePhotoBtn.setOnClickListener(itemsOnClick);
        pickPhotoBtn.setOnClickListener(itemsOnClick);
        cancelBtn.setOnClickListener(itemsOnClick);

        bikeCodeEdit = activity.findViewById(R.id.bikeFaultUI_codenum);
        iv_scan = activity.findViewById(R.id.bikeFaultUI_scan);

        Tag1 = activity.findViewById(R.id.bikeFaultUI_type_Tag1);
        Tag1_1 = activity.findViewById(R.id.bikeFaultUI_type_Tag1_1);
        Tag1_2 = activity.findViewById(R.id.bikeFaultUI_type_Tag1_2);
        Tag1_3 = activity.findViewById(R.id.bikeFaultUI_type_Tag1_3);
        Tag2 = activity.findViewById(R.id.bikeFaultUI_type_Tag2);
        Tag3 = activity.findViewById(R.id.bikeFaultUI_type_Tag3);
        Tag4 = activity.findViewById(R.id.bikeFaultUI_type_Tag4);
        Tag5 = activity.findViewById(R.id.bikeFaultUI_type_Tag5);
        Tag6 = activity.findViewById(R.id.bikeFaultUI_type_Tag6);


        ll_restCauseEdit = activity.findViewById(R.id.bikeFaultUI_ll_restCause);
        restCauseEdit = activity.findViewById(R.id.bikeFaultUI_restCause);
        addressEdit = activity.findViewById(R.id.bikeFaultUI_address);
        photoMyGridview = activity.findViewById(R.id.bikeFaultUI_photoGridView);
        submitBtn = activity.findViewById(R.id.bikeFaultUI_submitBtn);

        myAdapter = new PhotoGridviewAdapter(context);
        photoMyGridview.setAdapter(myAdapter);

        backImg.setOnClickListener(this);
        iv_scan.setOnClickListener(this);
        Tag1.setOnClickListener(this);
        Tag1_1.setOnClickListener(this);
        Tag1_2.setOnClickListener(this);
        Tag1_3.setOnClickListener(this);
        Tag2.setOnClickListener(this);
        Tag3.setOnClickListener(this);
        Tag4.setOnClickListener(this);
        Tag5.setOnClickListener(this);
        Tag6.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        bikeCodeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((TagsList.size() == 0 || TagsList.isEmpty())&&(
                        restCauseEdit.getText().toString().trim() == null
                                || "".equals(restCauseEdit.getText().toString().trim()))){
                    submitBtn.setEnabled(false);
                }else if(imageUrlList.size() == 0 || imageUrlList.isEmpty()) {
                    submitBtn.setEnabled(false);
                }else{
                    if (bikeCodeEdit.getText().toString().trim() != null &&
                            !"".equals(bikeCodeEdit.getText().toString().trim())){
                        submitBtn.setEnabled(true);
                    }else {
                        submitBtn.setEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if ((TagsList.size() == 0 || TagsList.isEmpty())&&(
                        restCauseEdit.getText().toString().trim() == null
                                || "".equals(restCauseEdit.getText().toString().trim()))){
                    submitBtn.setEnabled(false);
                }else if(imageUrlList.size() == 0 || imageUrlList.isEmpty()) {
                    submitBtn.setEnabled(false);
                }else{
                    if (bikeCodeEdit.getText().toString().trim() != null &&
                            !"".equals(bikeCodeEdit.getText().toString().trim())){
                        submitBtn.setEnabled(true);
                    }else {
                        submitBtn.setEnabled(false);
                    }
                }
            }
        });

        restCauseEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((TagsList.size() == 0 || TagsList.isEmpty())&&(
                        restCauseEdit.getText().toString().trim() == null
                                || "".equals(restCauseEdit.getText().toString().trim()))){
                    submitBtn.setEnabled(false);
                }else if(imageUrlList.size() == 0 || imageUrlList.isEmpty()) {
                    submitBtn.setEnabled(false);
                }else{
                    if (bikeCodeEdit.getText().toString().trim() != null &&
                            !"".equals(bikeCodeEdit.getText().toString().trim())){
                        submitBtn.setEnabled(true);
                    }else {
                        submitBtn.setEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if ((TagsList.size() == 0 || TagsList.isEmpty())&&(
                        restCauseEdit.getText().toString().trim() == null
                        || "".equals(restCauseEdit.getText().toString().trim()))){
                    submitBtn.setEnabled(false);
                }else if(imageUrlList.size() == 0 || imageUrlList.isEmpty()) {
                    submitBtn.setEnabled(false);
                }else{
                    if (bikeCodeEdit.getText().toString().trim() != null &&
                            !"".equals(bikeCodeEdit.getText().toString().trim())){
                        submitBtn.setEnabled(true);
                    }else {
                        submitBtn.setEnabled(false);
                    }
                }
            }
        });

        photoMyGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {// 查看某个照片

            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(photoMyGridview.getWindowToken(), 0);

                Log.e("photoMyGridview===b", position+"==="+imageUrlList.size());

                if (position == imageUrlList.size()) {
                    clickPopupWindow();
                }else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                    customBuilder.setTitle("温馨提示").setMessage("确认删除图片吗?")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            imageUrlList.remove(position);

                            if ((TagsList.size() == 0 || TagsList.isEmpty())&&(
                                    restCauseEdit.getText().toString().trim() == null
                                            || "".equals(restCauseEdit.getText().toString().trim()))){
                                submitBtn.setEnabled(false);
                            }else if(imageUrlList.size() == 0 || imageUrlList.isEmpty()) {
                                submitBtn.setEnabled(false);
                            }else{
                                if (bikeCodeEdit.getText().toString().trim() != null &&
                                        !"".equals(bikeCodeEdit.getText().toString().trim())){
                                    submitBtn.setEnabled(true);
                                }else {
                                    submitBtn.setEnabled(false);
                                }
                            }

                            myAdapter.notifyDataSetChanged();
                        }
                    });
                    customBuilder.create().show();
                }
            }
        });
        initLocation();

        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
//            if (!"1".equals(SharedPreferencesUrls.getInstance().getString("iscert",""))
//                    && SharedPreferencesUrls.getInstance().getString("iscert","") != null &&
//                    !"".equals(SharedPreferencesUrls.getInstance().getString("iscert",""))){
//                initHttp(uid,access_token);
//            }

//            initHttp(uid, access_token);

            getUpToken();
        }
    }
    public void getUpToken() {
        RequestParams params = new RequestParams();
//        params.put("uid",uid);
//        params.put("access_token",access_token);
        HttpHelper.get(context, Urls.uploadtoken, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e("uploadtoken===", "==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

//                            Log.e("uploadtoken===1", result.getData()+"==="+result.getStatus_code());

                            UpTokenBean bean = JSON.parseObject(result.getData(), UpTokenBean.class);

                            Log.e("uploadtoken===2", bean+"==="+bean.getToken());

                            if (null != bean.getToken()) {

                                upToken = bean.getToken();

//                                SharedPreferencesUrls.getInstance().putString("access_token", "Bearer "+bean.getToken());
//                                Toast.makeText(context,"恭喜您,获取成功",Toast.LENGTH_SHORT).show();
//                                scrollToFinishActivity();

//                                uploadImage();
                            }else{
                                Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                });

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
//                closeBle();

                scrollToFinishActivity();
                break;

            case R.id.bikeFaultUI_scan:
                Intent intent = new Intent();
                intent.setClass(context, ActivityScanerCode2.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("isChangeKey",false);
                startActivityForResult(intent, 101);

                break;

            case R.id.bikeFaultUI_type_Tag1:
                if (isSelected1){
                    isSelected1 = false;
                    if (TagsList.contains("车锁")){
                        TagsList.remove("车锁");
                    }
//                    Tag1.setTextColor(Color.parseColor("#666666"));

                    isSelected1_1 = false;
                    isSelected1_2 = false;
                    isSelected1_3 = false;
                    Tag1_1.setTextColor(Color.parseColor("#FD555B"));
                    Tag1_1.setBackgroundResource(R.drawable.block_fault_bcg);
                    Tag1_2.setTextColor(Color.parseColor("#FD555B"));
                    Tag1_2.setBackgroundResource(R.drawable.block_fault_bcg);
                    Tag1_3.setTextColor(Color.parseColor("#FD555B"));
                    Tag1_3.setBackgroundResource(R.drawable.block_fault_bcg);

                    Tag1.setImageResource(R.drawable.lock_icon);    //未选中
                }else {
                    isSelected1 = true;
                    if (!TagsList.contains("车锁")){
                        TagsList.add("车锁");
                    }
//                    Tag1.setTextColor(Color.parseColor("#f57752"));

                    isSelected1_1 = true;
                    isSelected1_2 = true;
                    isSelected1_3 = true;
                    Tag1_1.setTextColor(Color.parseColor("#FFFFFF"));
                    Tag1_1.setBackgroundResource(R.drawable.block_fault_bcg2);
                    Tag1_2.setTextColor(Color.parseColor("#FFFFFF"));
                    Tag1_2.setBackgroundResource(R.drawable.block_fault_bcg2);
                    Tag1_3.setTextColor(Color.parseColor("#FFFFFF"));
                    Tag1_3.setBackgroundResource(R.drawable.block_fault_bcg2);

                    Tag1.setImageResource(R.drawable.lock_icon2);   //选中
                }
                pd();
                break;

            case R.id.bikeFaultUI_type_Tag1_1:
                if (isSelected1_1){
                    isSelected1_1 = false;
                    if (TagsList1.contains("锁未弹开")){
                        TagsList1.remove("锁未弹开");
                    }
                    Tag1_1.setTextColor(Color.parseColor("#FD555B"));
                    Tag1_1.setBackgroundResource(R.drawable.block_fault_bcg);


                }else {
                    isSelected1_1 = true;
                    if (!TagsList1.contains("锁未弹开")){
                        TagsList1.add("锁未弹开");
                    }
                    Tag1_1.setTextColor(Color.parseColor("#FFFFFF"));
                    Tag1_1.setBackgroundResource(R.drawable.block_fault_bcg2);

                }

                px1();

                pd();
                break;

            case R.id.bikeFaultUI_type_Tag1_2:
                if (isSelected1_2){
                    isSelected1_2 = false;
                    if (TagsList1.contains("无法关锁")){
                        TagsList1.remove("无法关锁");
                    }
                    Tag1_2.setTextColor(Color.parseColor("#FD555B"));
                    Tag1_2.setBackgroundResource(R.drawable.block_fault_bcg);
                }else {
                    isSelected1_2 = true;
                    if (!TagsList1.contains("无法关锁")){
                        TagsList1.add("无法关锁");
                    }
                    Tag1_2.setTextColor(Color.parseColor("#FFFFFF"));
                    Tag1_2.setBackgroundResource(R.drawable.block_fault_bcg2);
                }

                px1();

                pd();
                break;

            case R.id.bikeFaultUI_type_Tag1_3:
                if (isSelected1_3){
                    isSelected1_3 = false;
                    if (TagsList.contains("外形破损")){
                        TagsList.remove("外形破损");
                    }
                    Tag1_3.setTextColor(Color.parseColor("#FD555B"));
                    Tag1_3.setBackgroundResource(R.drawable.block_fault_bcg);
                }else {
                    isSelected1_3 = true;
                    if (!TagsList.contains("外形破损")){
                        TagsList.add("外形破损");
                    }
                    Tag1_3.setTextColor(Color.parseColor("#FFFFFF"));
                    Tag1_3.setBackgroundResource(R.drawable.block_fault_bcg2);
                }

                px1();

                pd();
                break;

            case R.id.bikeFaultUI_type_Tag2:
                if (isSelected2){
                    isSelected2 = false;
                    if (TagsList.contains("刹车")){
                        TagsList.remove("刹车");
                    }
//                    Tag2.setTextColor(Color.parseColor("#666666"));
                    Tag2.setImageResource(R.drawable.brake_icon);
                }else {
                    isSelected2 = true;
                    if (!TagsList.contains("刹车")){
                        TagsList.add("刹车");
                    }
//                    Tag2.setTextColor(Color.parseColor("#f57752"));
                    Tag2.setImageResource(R.drawable.brake_icon2);
                }
                pd();
                break;

            case R.id.bikeFaultUI_type_Tag3:
                if (isSelected3){
                    isSelected3 = false;
                    if (TagsList.contains("链条")){
                        TagsList.remove("链条");
                    }
//                    Tag3.setTextColor(Color.parseColor("#666666"));
                    Tag3.setImageResource(R.drawable.chain_icon);
                }else {
                    isSelected3 = true;
                    if (!TagsList.contains("链条")){
                        TagsList.add("链条");
                    }
//                    Tag3.setTextColor(Color.parseColor("#f57752"));
                    Tag3.setImageResource(R.drawable.chain_icon2);
                }
                pd();
                break;

            case R.id.bikeFaultUI_type_Tag4:
                if (isSelected4){
                    isSelected4 = false;
                    if (TagsList.contains("车座")){
                        TagsList.remove("车座");
                    }
//                    Tag4.setTextColor(Color.parseColor("#666666"));
                    Tag4.setImageResource(R.drawable.saddle_icon);
                }else {
                    isSelected4 = true;
                    if (!TagsList.contains("车座")){
                        TagsList.add("车座");
                    }
//                    Tag4.setTextColor(Color.parseColor("#f57752"));
                    Tag4.setImageResource(R.drawable.saddle_icon2);
                }
                pd();
                break;

            case R.id.bikeFaultUI_type_Tag5:
                if (isSelected5){
                    isSelected5 = false;
                    if (TagsList.contains("脚踏")){
                        TagsList.remove("脚踏");
                    }
//                    Tag5.setTextColor(Color.parseColor("#666666"));
                    Tag5.setImageResource(R.drawable.pedal_icon);
                }else {
                    isSelected5 = true;
                    if (!TagsList.contains("脚踏")){
                        TagsList.add("脚踏");
                    }
//                    Tag5.setTextColor(Color.parseColor("#f57752"));
                    Tag5.setImageResource(R.drawable.pedal_icon2);
                }
                pd();
                break;

            case R.id.bikeFaultUI_type_Tag6:
                if (isSelected6){
                    isSelected6 = false;
                    if (TagsList.contains("其他")){
                        TagsList.remove("其他");
                    }
//                    Tag6.setTextColor(Color.parseColor("#666666"));

                    ll_restCauseEdit.setVisibility(View.GONE);
                    Tag6.setImageResource(R.drawable.other_icon);
                }else {
                    isSelected6 = true;
                    if (!TagsList.contains("其他")){
                        TagsList.add("其他");
                    }
//                    Tag6.setTextColor(Color.parseColor("#f57752"));

                    ll_restCauseEdit.setVisibility(View.VISIBLE);
                    Tag6.setImageResource(R.drawable.other_icon2);
                }
                pd();
                break;

            case R.id.bikeFaultUI_submitBtn:
                submit();
                break;
            default:
                break;
        }
    }

    private void px1(){
        if(isSelected1_1 || isSelected1_2 || isSelected1_3){
            isSelected1 = true;
            if (!TagsList.contains("车锁")){
                TagsList.add("车锁");
            }
            Tag1.setImageResource(R.drawable.lock_icon2);
        }else{
            isSelected1 = false;
            if (TagsList.contains("车锁")){
                TagsList.remove("车锁");
            }
            Tag1.setImageResource(R.drawable.lock_icon);
        }
    }

    private void pd(){
        if ((TagsList.size() == 0 || TagsList.isEmpty())&&(restCauseEdit.getText().toString().trim() == null || "".equals(restCauseEdit.getText().toString().trim()))){
            submitBtn.setEnabled(false);
        }else if(imageUrlList.size() == 0 || imageUrlList.isEmpty()) {
            submitBtn.setEnabled(false);
        }else{
            if (bikeCodeEdit.getText().toString().trim() != null && !"".equals(bikeCodeEdit.getText().toString().trim())){
                submitBtn.setEnabled(true);
            }else {
                submitBtn.setEnabled(false);
            }
        }
    }

    Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 0:
                    break;
                case 1:

                    if(BaseApplication.getInstance().getIBLE().isEnable()){
                        BaseApplication.getInstance().getIBLE().refreshCache();
                        BaseApplication.getInstance().getIBLE().close();
                        BaseApplication.getInstance().getIBLE().disconnect();
//                        BaseApplication.getInstance().getIBLE().disableBluetooth();
                    }

                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private void submit(){
        if(isSubmit) return;

        isSubmit = true;

        String address = addressEdit.getText().toString().trim();
        String other = restCauseEdit.getText().toString().trim();
        String bikeCode = bikeCodeEdit.getText().toString();

        RequestParams params = new RequestParams();

        Log.e("submit===0", TagsList+"==="+TagsList1);

        String content = "";
        if (TagsList.size() != 0 && !TagsList.isEmpty()){
            if ((isSelected6) && other != null && !"".equals(other)){
                for (int i = 0; i < TagsList.size(); i++){
                    content = content + TagsList.get(i)+",";
                }
                content = content + other+ "。";
            }else {
                for (int i = 0;i<TagsList.size();i++){
                    if (i != TagsList.size() - 1){
                        content = content + TagsList.get(i)+",";
                    }else {
                        content = content + TagsList.get(i)+ "。";
                    }
                }
            }
        }else {
            content = other + "。";
        }

        Log.e("submit===00", "==="+content);


        String content1 = "";
        if (isSelected1){
            for (int i = 0;i<TagsList1.size();i++){
                if (i != TagsList1.size() - 1){
                    content1 = content1 + TagsList1.get(i)+",";
                }else {
                    content1 = content1 + TagsList1.get(i)+ "。";
                }
            }
        }

        Log.e("submit===000", "==="+content1);

        if (content == null || "".equals(content)){
            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showMessageApp(context,"请选择问题类型");
                }
            });

            return;
        }
        if (imageList.size() == 0 || imageList.isEmpty()){
            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showMessageApp(context,"请上传照片");
                }
            });

            return;
        }

        Log.e("submit===", bikeCode+"==="+TagsList+"==="+TagsList1+"==="+latitude+"==="+longitude+"==="+content+"==="+content1+"==="+address+"==="+imageList);

        params.put("type", 2);      //类型(必传) 1无法还车 2车辆故障
        params.put("car_number", bikeCode);     //车辆编号(必传)
        params.put("damaged_part", content);     //损坏部件(提交车辆故障时必传)
        params.put("damaged_part_desc", content1);     //损坏部件(提交车辆故障时必传)
        params.put("position", address);     //位置信息(选传)
        params.put("images", imageList);     //图片key数组，json字符串
        params.put("latitude", ""+latitude);
        params.put("longitude", ""+longitude);

        HttpHelper.post(context, Urls.feedback, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在提交");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        isSubmit = false;

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            Log.e("feedback===", result.getData()+"==="+responseString);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                });

            }
        });
    }

    void memberEvent() {
        RequestParams params = new RequestParams();
        try {
            Log.e("feedback===memberEvent0", new Build().MANUFACTURER.toUpperCase()+"==="+new Build().MODEL+"==="+Build.VERSION.RELEASE+"==="+activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName);

            params.put("uid", uid);
            params.put("access_token", access_token);
            params.put("phone_brand", new Build().MANUFACTURER.toUpperCase());
            params.put("phone_model", new Build().MODEL);
            params.put("phone_system", "Android");
            params.put("phone_system_version", Build.VERSION.RELEASE);     //手机系统版本 必传 如：13.1.2
            params.put("app_version", activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName);      //应用版本 必传 如：1.8.2
            params.put("event", "4");
            params.put("event_id", fid);
            params.put("event_content", "submit_feedback");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        HttpHelper.post(context, Urls.memberEvent, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Log.e("feedback===memberEvent1", "==="+responseString);

                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().toString().equals("Success")) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
        });
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (requestCode) {

                    case 101:
                        if (resultCode == RESULT_OK) {
                            String codenum = data.getStringExtra("codenum");

                            Log.e("bff===101_b", type+"==="+codenum);

                            bikeCodeEdit.setText(codenum);

                        } else {
                            Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                        }

                        Log.e("requestCode===1", "==="+resultCode);
                        break;

                    case REQUESTCODE_PICK:// 直接从相册获取
                        if (data != null){
                            try {
                                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                                    if (imageUri != null) {
                                        urlpath = getRealFilePath(context, data.getData());
                                        if (loadingDialog != null && !loadingDialog.isShowing()) {
                                            loadingDialog.setTitle("请稍等");
                                            loadingDialog.show();
                                        }
                                        new Thread(uploadImageRunnable).start();
                                    }
//                            }
                                }else {
                                    ToastUtil.showMessageApp(context,"未找到存储卡，无法存储照片！");
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();// 用户点击取消操作
                            }
                        }
                        break;
                    case REQUESTCODE_TAKE:// 调用相机拍照

//                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//                            File temp = new File(Environment.getExternalStorageDirectory() + "/images/" + IMAGE_FILE_NAME);
//                            if (Uri.fromFile(temp) != null) {
//                                urlpath = getRealFilePath(context, Uri.fromFile(temp));
//                                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                                    loadingDialog.setTitle("请稍等");
//                                    loadingDialog.show();
//                                }
//                                new Thread(uploadImageRunnable).start();
//                            }
//                        }else {
//                            Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
//                        }

                        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){

                            File temp = new File(Environment.getExternalStorageDirectory() + "/images/" + IMAGE_FILE_NAME);
                            if (Uri.fromFile(temp) != null) {
                                urlpath = getRealFilePath(context, Uri.fromFile(temp));
                                Log.e("REQUESTCODE_TAKE===", temp+"==="+urlpath);

//                                File picture = new File(urlpath);
                                Uri filepath = Uri.fromFile(temp);
//                                upBitmap = BitmapFactory.decodeFile(urlpath);


                                compress(); //压缩图片

//                                if(photo == 1){
//                                    uploadImage.setImageBitmap(upBitmap);
//                                }else{
//                                    uploadImage2.setImageBitmap(upBitmap);
//                                }

                                Log.e("REQUESTCODE_TAKE===3", "==="+filepath.getPath());

                                uploadImage();
                            }

                        }else {
                            Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                        if (data != null) {
                            setPicToView(data);
                        }
                        break;
                }

            }
        });

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadImage() {
        //定义数据上传结束后的处理动作
        final UpCompletionHandler upCompletionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {

//                JSONObject jsonObject = new JSONObject(info.timeStamp);

                Log.e("uploadImage===0", "==="+response);

                try {
                    JSONObject jsonObject = new JSONObject(response.getString("image"));

                    //TODO
//                    if(photo == 1){
//                        imageurl = jsonObject.getString("key");
//                    }else{
//                        imageurl2 = jsonObject.getString("key");
//                    }

                    imageList.add(jsonObject.getString("key"));

                    pd();
                    Log.e("UpCompletion===", imageList+"==="+jsonObject+"==="+jsonObject.getString("key")+"==="+key+"==="+info+"==="+response+"==="+info.timeStamp+"==="+"http://q0xo2if8t.bkt.clouddn.com/" + key+"?e="+info.timeStamp+"&token="+upToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }





//                {ver:7.3.3,ResponseInfo:1574237736489492,status:200, reqId:HpgAAAAlr6vh0NgV, xlog:X-Log, xvia:, host:upload.qiniu.com, path:/, ip:/180.101.136.11:80, port:80, duration:183.000000 s, time:1574237736, sent:25256,error:null}==={"image":null,"ret":"success"}

//                http://q0xo2if8t.bkt.clouddn.com/y2.png?e=1574241198&token=FXDJS_lmH1Gfs-Ni9I9kpPf6MZFTGz5U5BP1CgNu:q2uGajiCFq6t7E-9CxrYXWF0bIQ=&attname=

//                Glide.with(context)
//                        .load("http://q0xo2if8t.bkt.clouddn.com/" + key+"?e="+info.timeStamp+"&token="+upToken)
//                        .crossFade()
//                        .into(uploadImage);

//                Glide.with(context)
//                        .load("/storage/emulated/0/com.gamefox.samecity.fish/activity/bill1.png")
//                        .crossFade()
//                        .into(uploadImage);

//                ImageLoader.getInstance().displayImage("/storage/emulated/0/com.gamefox.samecity.fish/activity/bill1.png", uploadImage);

//                Glide.with(context)
//                        .load("http://q0xo2if8t.bkt.clouddn.com/y3.png?e=1574242008&token=FXDJS_lmH1Gfs-Ni9I9kpPf6MZFTGz5U5BP1CgNu:cbzS4BHzqrRF8-iENlvHv7v8i94=&attname=")
////                        .fitCenter()
////                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .crossFade()
//                        .into(uploadImage);

//                Glide.with(context).load(Urls.host+"/Public/uploads/201911/201911201609453660.jpg").crossFade().into(uploadImage);
            }
        };
        final UploadOptions uploadOptions = new UploadOptions(null, null, false, new UpProgressHandler() {
            @Override
            public void progress(String key, final double percent) {
                //百分数格式化
                NumberFormat fmt = NumberFormat.getPercentInstance();
                fmt.setMaximumFractionDigits(2);//最多两位百分小数，如25.23%

                Log.e("progress===", "==="+fmt.format(percent));

//                tv.setText("图片已经上传:" + fmt.format(percent));
            }
        }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return false;
            }
        });
        try {
            //上传图片jjj
            Log.e("uploadImage===", "==="+upToken);

//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            Log.e("uploadImage===1", upBitmap+"===");
//            upBitmap.compress(Bitmap.CompressFormat.PNG, 10, baos);
//
//            Log.e("uploadImage===2", upBitmap+"==="+baos.toByteArray().length);



//            int bytes = upBitmap.getByteCount();
//            ByteBuffer buf = ByteBuffer.allocate(bytes);
//            upBitmap.copyPixelsToBuffer(buf);
//            byte[] byteArray = buf.array();

//            QiNiuInitialize.getSingleton().put(buf.array(), null, upToken, upCompletionHandler, uploadOptions);
//            QiNiuInitialize.getSingleton().put(baos.toByteArray(), null, upToken, upCompletionHandler, uploadOptions);
            QiNiuInitialize.getSingleton().put(getByte(), null, upToken, upCompletionHandler, uploadOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取资源文件中的图片
    public byte[] getByte() {
//        Resources res = getResources();
//        Bitmap bm = BitmapFactory.decodeResource(res, R.drawable.bike3);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.PNG, 80, baos);
        Log.e("getByte===1", upBitmap+"===");
        upBitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
//        upBitmap.compress(Bitmap.CompressFormat.PNG, 10, baos);
        Log.e("getByte===2", upBitmap+"==="+baos.toByteArray().length);

//        QiNiuInitialize.getSingleton().put(getByte(), null, upToken, upCompletionHandler, uploadOptions);

        return baos.toByteArray();
    }

    void compress(){
        // 设置参数
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
        BitmapFactory.decodeFile(urlpath, options);
        int height = options.outHeight;
        int width= options.outWidth;
        int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2
        int minLen = Math.min(height, width); // 原图的最小边长
        if(minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
            float ratio = (float)minLen / 100.0f; // 计算像素压缩比例
            inSampleSize = (int)ratio;
        }
        options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
        options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
        upBitmap = BitmapFactory.decodeFile(urlpath, options); // 解码文件

        imageUrlList.add(upBitmap);
        myAdapter.notifyDataSetChanged();
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 600);
        intent.putExtra("outputY", 600);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null,
                    null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (imageUri != null) {
            urlpath = getRealFilePath(context, imageUri);
            if (loadingDialog != null && !loadingDialog.isShowing()) {
                loadingDialog.setTitle("请稍等");
                loadingDialog.show();
            }
            new Thread(uploadImageRunnable).start();
        }
    }

    /**
     * 使用HttpUrlConnection模拟post表单进行文件 上传平时很少使用，比较麻烦 原理是：
     * 分析文件上传的数据格式，然后根据格式构造相应的发送给服务器的字符串。
     */
    Runnable uploadImageRunnable = new Runnable() {
        @Override
        public void run() {

            if (TextUtils.isEmpty(imgUrl)) {
                ToastUtil.showMessageApp(context, "还没有设置上传服务器的路径！");
                return;
            }
            Map<String, String> textParams = new HashMap<>();
            Map<String, File> fileparams = new HashMap<>();
            try {
                // 创建一个URL对象
                URL url = new URL(imgUrl);
                textParams = new HashMap<>();
                fileparams = new HashMap<>();
                // 要上传的图片文件
                File file = new File(urlpath);
                if (file.length() >= 2097152 / 2) {
                    file = new File(BitmapUtils1.compressImageUpload(urlpath,480f,800f));
                }
                fileparams.put("key1", file);
                textParams.put("uid", SharedPreferencesUrls.getInstance().getString("uid", ""));
                textParams.put("access_token", SharedPreferencesUrls.getInstance().getString("access_token", ""));
                // 利用HttpURLConnection对象从网络中获取网页数据
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 设置连接超时（记得设置连接超时,如果网络不好,Android系统在超过默认时间会收回资源中断操作）
                conn.setConnectTimeout(5000);
                // 设置允许输出（发送POST请求必须设置允许输出）
                conn.setDoOutput(true);
                // 设置使用POST的方式发送
                conn.setRequestMethod("POST");
                // 设置不使用缓存（容易出现问题）
                conn.setUseCaches(false);
                conn.setRequestProperty("Charset", "UTF-8");// 设置编码
                // 在开始用HttpURLConnection对象的setRequestProperty()设置,就是生成HTML文件头
                conn.setRequestProperty("ser-Agent", "Fiddler");
                // 设置contentType
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + NetUtil.BOUNDARY);
                OutputStream os = conn.getOutputStream();
                DataOutputStream ds = new DataOutputStream(os);
                NetUtil.writeStringParams(textParams, ds);
                NetUtil.writeFileParams(fileparams, ds);
                NetUtil.paramsEnd(ds);
                // 对文件流操作完,要记得及时关闭
                os.close();
                // 服务器返回的响应吗
                int code = conn.getResponseCode(); // 从Internet获取网页,发送请求,将网页以流的形式读回来
                // 对响应码进行判断
                if (code == 200) {// 返回的响应码200,是成功
                    // 得到网络返回的输入流
                    InputStream is = conn.getInputStream();
                    resultStr = NetUtil.readString(is);

                    Log.e("upload===", imgUrl+"==="+urlpath+"==="+resultStr);
                } else {
                    ToastUtil.showMessageApp(context, "请求URL失败！");
                }
            } catch (Exception e) {

                Log.e("Test", "异常：：：" + e);

            }
            mHandler.sendEmptyMessage(0);// 执行耗时的方法之后发送消给handler
        }
    };

    Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
//                case 0:
//
//                    try {
//                        // 返回数据示例，根据需求和后台数据灵活处理
//                        JSONObject jsonObject = new JSONObject(resultStr);
//                        // 服务端以字符串“1”作为操作成功标记
//                        if (jsonObject.optString("flag").equals("Success")) {
//                            BitmapFactory.Options option = new BitmapFactory.Options();
//                            // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图，3为三分之一
//                            option.inSampleSize = 1;
//                            imageUrlList.add(jsonObject.optString("data"));
//                            ToastUtil.showMessageApp(context, "图片上传成功");
//
//                            Log.e("picture===", "==="+imageUrlList);
//
//                            if ((TagsList.size() == 0 || TagsList.isEmpty())&&(
//                                    restCauseEdit.getText().toString().trim() == null
//                                            || "".equals(restCauseEdit.getText().toString().trim()))){
//                                submitBtn.setEnabled(false);
//                            }else if(imageUrlList.size() == 0 || imageUrlList.isEmpty()) {
//                                submitBtn.setEnabled(false);
//                            }else{
//                                if (bikeCodeEdit.getText().toString().trim() != null &&
//                                        !"".equals(bikeCodeEdit.getText().toString().trim())){
//                                    submitBtn.setEnabled(true);
//                                }else {
//                                    submitBtn.setEnabled(false);
//                                }
//                            }
//
//                            isComplete = false;
//                            myAdapter.notifyDataSetChanged();
//                        } else {
//                            if (loadingDialog != null && loadingDialog.isShowing()) {
//                                loadingDialog.dismiss();
//                            }
//                            ToastUtil.showMessageApp(context, jsonObject.optString("msg"));
//                        }
//
//                    } catch (JSONException e) {
//                        if (loadingDialog != null && loadingDialog.isShowing()) {
//                            loadingDialog.dismiss();
//                        }
//                    }
//
//                    break;

//                case 1:
//                    pos = 0;
//
//                    ImageLoader.getInstance().displayImage(Urls.host + imageUrlList.get(pos), imageView, new SimpleImageLoadingListener(){
//
//                        @Override
//                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                            Log.e("Complete===", "==="+isComplete);
//
//                            isComplete = true;
//
//                            if (loadingDialog != null) {
//                                loadingDialog.dismiss();
//                            }
//                        }
//
//                    }, new ImageLoadingProgressListener(){
//
//                        @Override
//                        public void onProgressUpdate(String imageUri, View view, int current, int total) {
//                            Log.e("Update===", "==="+current);
//                        }
//                    });
//
//                    break;

                default:
                    break;
            }
            return false;
        }
    });

    public class PhotoGridviewAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public PhotoGridviewAdapter(Context context) {

            inflater = LayoutInflater.from(context);
        }

//        public void clearAll() {
//            imageUrlList.clear();
//        }
//
//
//        public void addALL(List lists){
//            if(lists==null||lists.size()==0){
//                return ;
//            }
//            imageUrlList.addAll(lists);
//        }

        @Override
        public int getCount() {
            if (imageUrlList.size() < MAX) {
                return (imageUrlList.size() + 1);
            } else {
                return MAX;
            }
        }

        @Override
        public Object getItem(int position) {

            return position;
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {



//            pos = position;
//
//            int childCount = parent.getChildCount();
//
//            Log.e("ImageLoader===", position+"==="+imageUrlList.size()+"==="+childCount);
//
//            if (position == 0 && convertView != null) {
//                return convertView;
//            }
//
//            Log.e("ImageLoader===2", position+"==="+imageUrlList.size()+"==="+isComplete);


            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_photo_mygridview, parent, false);
            }

            if(!((MyGridView) parent).isOnMeasure()) return convertView;

            if(imageUrlList.size()>0){
                Log.e("ImageLoader===3", "==="+position+"==="+imageUrlList.size()+"==="+isComplete+"==="+convertView+"===");
            }

            ImageView imageView = BaseViewHolder.get(convertView, R.id.item_photo_gridView_image);
            if (position == imageUrlList.size()) {
                imageView.setImageResource(R.drawable.icon_addpic_focused);
                if (MAX == position) {
                    imageView.setVisibility(View.GONE);
                }
            } else {

                imageView.setImageBitmap(imageUrlList.get(position));

//                if(!isComplete){
//                    ImageLoader.getInstance().displayImage(Urls.host + imageUrlList.get(position), imageView, new SimpleImageLoadingListener(){
//
//                        @Override
//                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                            Log.e("Complete===", "==="+isComplete);
//
//                            isComplete = true;
//
//                            if (loadingDialog != null) {
//                                loadingDialog.dismiss();
//                            }
//                        }
//
//                    }, new ImageLoadingProgressListener(){
//
//                        @Override
//                        public void onProgressUpdate(String imageUri, View view, int current, int total) {
//                            Log.e("Update===", "==="+current);
//                        }
//                    });
//                }
            }
//            notifyDataSetChanged();

            return convertView;
        }

    }

    // 为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @SuppressLint("NewApi")
        @Override
        public void onClick(View v) {
            clickClosePopupWindow();
            switch (v.getId()) {
                // 拍照
                case R.id.takePhotoBtn:
                    if (Build.VERSION.SDK_INT >= 23) {
                        int checkPermission = context.checkSelfPermission(Manifest.permission.CAMERA);
                        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                requestPermissions(new String[] { Manifest.permission.CAMERA }, 101);
                            } else {
                                CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();

                                        BikeFaultFragment.this.requestPermissions(new String[] { Manifest.permission.CAMERA },
                                                101);

                                    }
                                });
                                customBuilder.create().show();
                            }
                            return;
                        }
                    }

                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        File file = new File(Environment.getExternalStorageDirectory()+"/images/", IMAGE_FILE_NAME);
                        if(!file.getParentFile().exists()){
                            file.getParentFile().mkdirs();
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context,
                                    BuildConfig.APPLICATION_ID + ".fileprovider",
                                    file));

                        }else {
                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        }


                        startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    }else {
                        Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                    }

                    break;
                // 相册选择图片
                case R.id.pickPhotoBtn:
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (requestCode) {
                    case 0:
                        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            initView();
                        }else {
                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                            customBuilder.setTitle("温馨提示").setMessage("您需要在设置里定位权限！")
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            finishMine();
                                        }
                                    }).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Intent localIntent = new Intent();
                                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                                    startActivity(localIntent);
                                    finishMine();
                                }
                            });
                            customBuilder.create().show();
                        }
                        break;
                    case 101:
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            // Permission Granted
                            if (permissions[0].equals(Manifest.permission.CAMERA)) {

//                        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
//                            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(FeedbackActivity.this,
//                                        BuildConfig.APPLICATION_ID + ".provider",
//                                        new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
//                                takeIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                takeIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                            }else {
//                                // 下面这句指定调用相机拍照后的照片存储的路径
//                                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
//                            }
//                            startActivityForResult(takeIntent, REQUESTCODE_TAKE);
//                        }else {
//                            ToastUtil.showMessageApp(context,"未找到存储卡，无法存储照片！");
//                        }

                                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                    File file = new File(Environment.getExternalStorageDirectory()+"/images/", IMAGE_FILE_NAME);
                                    if(!file.getParentFile().exists()){
                                        file.getParentFile().mkdirs();
                                    }

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context,
                                                BuildConfig.APPLICATION_ID + ".fileprovider",
//                                        "com.vondear.rxtools.fileprovider",
                                                file));

                                    }else {
                                        // 下面这句指定调用相机拍照后的照片存储的路径
                                        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                                    }
                                    startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                                }else {
                                    Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                            customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            finishMine();
                                        }
                                    }).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Intent localIntent = new Intent();
                                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                                    startActivity(localIntent);
                                    finishMine();
                                }
                            });
                            customBuilder.create().show();
                        }
                        break;
                    default:
                        onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        });

    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//
//
//            scrollToFinishActivity();
//
//
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    /**
     * 显示弹窗
     */
    private void clickPopupWindow() {
        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(activity);

        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
            UtilBitmap.blurImageView(context, iv_popup_window_back, 6,0xAA000000);
        } else {
            // 获取的Bitmap为null时，用半透明代替
            iv_popup_window_back.setBackgroundColor(0x77000000);
        }

        // 打开弹窗
        UtilAnim.showToUp(rl_popup_window, iv_popup_window_back);

    }

    /**
     * 关闭弹窗
     */
    private void clickClosePopupWindow() {
        UtilAnim.hideToDown(rl_popup_window, iv_popup_window_back);
    }

    /**
     * 初始化定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void initLocation(){
        if (NetworkUtils.getNetWorkType(context) != NetworkUtils.NONETWORK) {
            //初始化client
            locationClient = new AMapLocationClient(activity.getApplicationContext());
            //设置定位参数
            locationClient.setLocationOption(getDefaultOption());
            // 设置定位监听
            locationClient.setLocationListener(locationListener);
            startLocation();
        }else {
            ToastUtil.showMessageApp(context,"暂无网络连接，请连接网络");
            return;
        }
    }

    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(20 * 1000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(false);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(true);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(false); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }
    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc) {
                if (0.0 != loc.getLongitude() && 0.0 != loc.getLongitude()){
                    latitude = loc.getLatitude();
                    longitude = loc.getLongitude();
                    stopLocation();
                }else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开定位权限！")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    finishMine();
                                }
                            }).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent localIntent = new Intent();
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                            startActivity(localIntent);
                            finishMine();
                        }
                    });
                    customBuilder.create().show();
                    return;
                }
            } else {
                ToastUtil.showMessageApp(context,"定位失败");
                finishMine();
            }
        }
    };

    /**
     * 开始定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void startLocation(){
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void stopLocation(){
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    private void closeBle(){
        if(SharedPreferencesUrls.getInstance().getBoolean("isStop",true)){  //骑行结束才能关蓝牙
            new Thread(new Runnable() {
                @Override
                public void run() {
                    m_myHandler.sendEmptyMessage(1);
                }
            }).start();
        }
    }


}
