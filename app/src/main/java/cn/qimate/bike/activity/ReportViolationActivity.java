package cn.qimate.bike.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.vondear.rxtools.RxFileTool;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.nostra13.universalimageloader.core.ImageLoader;
import cn.qimate.bike.BuildConfig;
import cn.qimate.bike.R;
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
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by Administrator1 on 2017/7/20.
 */
@SuppressLint("NewApi")
public class ReportViolationActivity extends SwipeBackActivity implements View.OnClickListener {

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();

    private TextView Tag1,Tag2,Tag3,Tag4;
    private EditText restCauseEdit;

    private boolean isSelected1 = false;
    private boolean isSelected2 = false;
    private boolean isSelected3 = false;
    private boolean isSelected4 = false;
    private boolean isSelected5 = false;
    private boolean isSelected6 = false;

    private Context context;
    private LoadingDialog loadingDialog;
    private LinearLayout ll_back;
    private TextView title;
    private Button takePhotoBtn,pickPhotoBtn,cancelBtn;

    private TextView codeNum;

    private PhotoGridviewAdapter myAdapter;
    private MyGridView photoMyGridview;
    private LinearLayout submitBtn;

    private List<String> TagsList;
    private List<String> imageUrlList;
    final static int MAX = 4;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_violation);
        context = this;
        TagsList = new ArrayList<>();
        imageUrlList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkPermission = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkPermission != PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION },0);
                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开位置权限！")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    finishMine();
                                }
                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ReportViolationActivity.this.requestPermissions(
                                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },0);
                        }
                    });
                    customBuilder.create().show();
                }
                return;
            }
        }
        initView();
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        Tag1 = (TextView)findViewById(R.id.feedbackUI_type_Tag1);
        Tag2 = (TextView)findViewById(R.id.feedbackUI_type_Tag2);
        Tag3 = (TextView)findViewById(R.id.feedbackUI_type_Tag3);
        Tag4 = (TextView)findViewById(R.id.feedbackUI_type_Tag4);

        restCauseEdit = (EditText)findViewById(R.id.feedbackUI_restCause);

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("举报违规");

        imageUri = Uri.parse("file:///sdcard/temp.jpg");
        iv_popup_window_back = (ImageView)findViewById(R.id.popupWindow_back);
        rl_popup_window = (RelativeLayout)findViewById(R.id.popupWindow);

        takePhotoBtn = (Button)findViewById(R.id.takePhotoBtn);
        pickPhotoBtn = (Button)findViewById(R.id.pickPhotoBtn);
        cancelBtn = (Button)findViewById(R.id.cancelBtn);
        pickPhotoBtn.setVisibility(View.GONE);
        takePhotoBtn.setOnClickListener(itemsOnClick);
//        pickPhotoBtn.setOnClickListener(itemsOnClick);
        cancelBtn.setOnClickListener(itemsOnClick);
//
//        codeNum = (TextView)findViewById(R.id.ui_client_service_codeNum);
//        codeNum.setText("单车编号："+getIntent().getExtras().getString("bikeCode"));
//
        photoMyGridview = (MyGridView)findViewById(R.id.ui_client_service_photoGridView);
        submitBtn = (LinearLayout)findViewById(R.id.ui_client_service_submitBtn);

        myAdapter = new PhotoGridviewAdapter(context);
        photoMyGridview.setAdapter(myAdapter);

        ll_back.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        Tag1.setOnClickListener(this);
        Tag2.setOnClickListener(this);
        Tag3.setOnClickListener(this);
        Tag4.setOnClickListener(this);

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
                    submitBtn.setEnabled(true);
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
                    submitBtn.setEnabled(true);
                }
            }
        });

        photoMyGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {// 查看某个照片

            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(photoMyGridview.getWindowToken(), 0);
                if (position == imageUrlList.size()) {
                    clickPopupWindow();
                }else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(ReportViolationActivity.this);
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
                                submitBtn.setEnabled(true);
                            }

                            myAdapter.notifyDataSetChanged();
                        }
                    });
                    customBuilder.create().show();
                }
            }
        });
        initLocation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                scrollToFinishActivity();
                break;

            case R.id.feedbackUI_type_Tag1:

                Log.e("TagsList===", TagsList.size()+"==="+TagsList);

                if (isSelected1){
                    isSelected1 = false;
                    if (TagsList.contains(Tag1.getText().toString())){
                        TagsList.remove(Tag1.getText().toString());
                    }
                    Tag1.setTextColor(Color.parseColor("#666666"));
                    Tag1.setBackgroundResource(R.drawable.shape_feedback);
                }else {
                    isSelected1 = true;
                    if (!TagsList.contains(Tag1.getText().toString())){
                        TagsList.add(Tag1.getText().toString());
                    }
                    Tag1.setTextColor(Color.parseColor("#f57752"));
                    Tag1.setBackgroundResource(R.drawable.shape_feedback_selectd);
                }

                Log.e("TagsList===111", TagsList.size()+"==="+TagsList);

                if ((TagsList.size() == 0 || TagsList.isEmpty())&&(
                        restCauseEdit.getText().toString().trim() == null
                                || "".equals(restCauseEdit.getText().toString().trim()))){
                    submitBtn.setEnabled(false);
                }else if(imageUrlList.size() == 0 || imageUrlList.isEmpty()) {
                    submitBtn.setEnabled(false);
                }else{
                    submitBtn.setEnabled(true);
                }
                break;
            case R.id.feedbackUI_type_Tag2:
                if (isSelected2){
                    isSelected2 = false;
                    if (TagsList.contains(Tag2.getText().toString())){
                        TagsList.remove(Tag2.getText().toString());
                    }
                    Tag2.setTextColor(Color.parseColor("#666666"));
                    Tag2.setBackgroundResource(R.drawable.shape_feedback);
                }else {
                    isSelected2 = true;
                    if (!TagsList.contains(Tag2.getText().toString())){
                        TagsList.add(Tag2.getText().toString());
                    }
                    Tag2.setTextColor(Color.parseColor("#f57752"));
                    Tag2.setBackgroundResource(R.drawable.shape_feedback_selectd);
                }
                if ((TagsList.size() == 0 || TagsList.isEmpty())&&(
                        restCauseEdit.getText().toString().trim() == null
                                || "".equals(restCauseEdit.getText().toString().trim()))){
                    submitBtn.setEnabled(false);
                }else if(imageUrlList.size() == 0 || imageUrlList.isEmpty()) {
                    submitBtn.setEnabled(false);
                }else{
                    submitBtn.setEnabled(true);
                }
                break;
            case R.id.feedbackUI_type_Tag3:
                if (isSelected3){
                    isSelected3 = false;
                    if (TagsList.contains(Tag3.getText().toString())){
                        TagsList.remove(Tag3.getText().toString());
                    }
                    Tag3.setTextColor(Color.parseColor("#666666"));
                    Tag3.setBackgroundResource(R.drawable.shape_feedback);
                }else {
                    isSelected3 = true;
                    if (!TagsList.contains(Tag3.getText().toString())){
                        TagsList.add(Tag3.getText().toString());
                    }
                    Tag3.setTextColor(Color.parseColor("#f57752"));
                    Tag3.setBackgroundResource(R.drawable.shape_feedback_selectd);
                }
                if ((TagsList.size() == 0 || TagsList.isEmpty())&&(
                        restCauseEdit.getText().toString().trim() == null
                                || "".equals(restCauseEdit.getText().toString().trim()))){
                    submitBtn.setEnabled(false);
                }else if(imageUrlList.size() == 0 || imageUrlList.isEmpty()) {
                    submitBtn.setEnabled(false);
                }else{
                    submitBtn.setEnabled(true);
                }
                break;
            case R.id.feedbackUI_type_Tag4:
                if (isSelected4){
                    isSelected4 = false;
                    if (TagsList.contains(Tag4.getText().toString())){
                        TagsList.remove(Tag4.getText().toString());
                    }
                    Tag4.setTextColor(Color.parseColor("#666666"));
                    Tag4.setBackgroundResource(R.drawable.shape_feedback);
                }else {
                    isSelected4 = true;
                    if (!TagsList.contains(Tag4.getText().toString())){
                        TagsList.add(Tag4.getText().toString());
                    }
                    Tag4.setTextColor(Color.parseColor("#f57752"));
                    Tag4.setBackgroundResource(R.drawable.shape_feedback_selectd);
                }
                if ((TagsList.size() == 0 || TagsList.isEmpty())&&(
                        restCauseEdit.getText().toString().trim() == null
                                || "".equals(restCauseEdit.getText().toString().trim()))){
                    submitBtn.setEnabled(false);
                }else if(imageUrlList.size() == 0 || imageUrlList.isEmpty()) {
                    submitBtn.setEnabled(false);
                }else{
                    submitBtn.setEnabled(true);
                }
                break;


            case R.id.ui_client_service_submitBtn:
                submit();
                break;
            default:
                break;
        }
    }

    private void submit(){
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        RequestParams params = new RequestParams();
        if (uid != null && !"".equals(uid)){
            params.put("uid",uid);
        }
        if (access_token != null && !"".equals(access_token)){
            params.put("access_token",access_token);
        }
        if (imageUrlList.size() == 0 || imageUrlList.isEmpty()){
            Toast.makeText(context,"至少上传一张图片",Toast.LENGTH_SHORT).show();
            return;
        }
        params.put("content","关锁后无法结束");
        params.put("bike_code",getIntent().getExtras().getString("bikeCode"));
        params.put("desc_img",imageUrlList);
        params.put("latitude", latitude);
        params.put("longitude",longitude);
        HttpHelper.post(context, Urls.feedback, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在提交");
                    loadingDialog.show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        Toast.makeText(context,"提交成功，工作人员将很快处理。",Toast.LENGTH_SHORT).show();
                        scrollToFinishActivity();
                    } else {
                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
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
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_photo_mygridview, parent, false);
            }
            ImageView imageView = BaseViewHolder.get(convertView, R.id.item_photo_gridView_image);
            if (position == imageUrlList.size()) {
                imageView.setImageResource(R.drawable.icon_addpic_focused);
                if (MAX == position) {
                    imageView.setVisibility(View.GONE);
                }
            } else {
                ImageLoader.getInstance().displayImage(Urls.host + imageUrlList.get(position), imageView);
            }
            notifyDataSetChanged();
            return convertView;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
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
                            Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();// 用户点击取消操作
                    }
                }
                break;
            case REQUESTCODE_TAKE:// 调用相机拍照
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                    if (Uri.fromFile(temp) != null) {
                        urlpath = getRealFilePath(context, Uri.fromFile(temp));
                        if (loadingDialog != null && !loadingDialog.isShowing()) {
                            loadingDialog.setTitle("请稍等");
                            loadingDialog.show();
                        }
                        new Thread(uploadImageRunnable).start();
                    }
//                    }
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
        super.onActivityResult(requestCode, resultCode, data);
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
                Toast.makeText(context, "还没有设置上传服务器的路径！", Toast.LENGTH_SHORT).show();
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
                } else {
                    Toast.makeText(context, "请求URL失败！", Toast.LENGTH_SHORT).show();
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
                case 0:
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                    try {
                        // 返回数据示例，根据需求和后台数据灵活处理
                        JSONObject jsonObject = new JSONObject(resultStr);
                        // 服务端以字符串“1”作为操作成功标记
                        if (jsonObject.optString("flag").equals("Success")) {
                            BitmapFactory.Options option = new BitmapFactory.Options();
                            // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图，3为三分之一
                            option.inSampleSize = 1;
                            imageUrlList.add(jsonObject.optString("data"));
                            Toast.makeText(context, "图片上传成功", Toast.LENGTH_SHORT).show();

                            if ((TagsList.size() == 0 || TagsList.isEmpty())&&(
                                    restCauseEdit.getText().toString().trim() == null
                                            || "".equals(restCauseEdit.getText().toString().trim()))){
                                submitBtn.setEnabled(false);
                            }else if(imageUrlList.size() == 0 || imageUrlList.isEmpty()) {
                                submitBtn.setEnabled(false);
                            }else{
                                submitBtn.setEnabled(true);
                            }

                            myAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                    }

                    break;

                default:
                    break;
            }
            return false;
        }
    });

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
                        if (checkPermission != PERMISSION_GRANTED) {
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

                                        ReportViolationActivity.this.requestPermissions(new String[] { Manifest.permission.CAMERA },
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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, RxFileTool.getUriForFile(context,
                                    new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                            takeIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            takeIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        }else {
                            // 下面这句指定调用相机拍照后的照片存储的路径
                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                        }
                        startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    }else {
                        Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                    }
                    break;
                // 相册选择图片
                case R.id.pickPhotoBtn:
//                    if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
//                        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
//                        // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
//                        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                        startActivityForResult(pickIntent, REQUESTCODE_PICK);
//                    }else {
//                        Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
//                    }
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    initView();
                }else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
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
                            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivity(localIntent);
                            finishMine();
                        }
                    });
                    customBuilder.create().show();
                }
                break;
            case 101:
                if (grantResults[0] == PERMISSION_GRANTED) {
                    // Permission Granted
                    if (permissions[0].equals(Manifest.permission.CAMERA)) {

                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(ReportViolationActivity.this,
                                        BuildConfig.APPLICATION_ID + ".provider",
                                        new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                                takeIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                takeIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            }else {
                                // 下面这句指定调用相机拍照后的照片存储的路径
                                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                            }
                            startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                        }else {
                            Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
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
                            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivity(localIntent);
                            finishMine();
                        }
                    });
                    customBuilder.create().show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 显示弹窗
     */
    private void clickPopupWindow() {
        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(this);

        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
            UtilBitmap.blurImageView(this, iv_popup_window_back, 6,0xAA000000);
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
            locationClient = new AMapLocationClient(this.getApplicationContext());
            //设置定位参数
            locationClient.setLocationOption(getDefaultOption());
            // 设置定位监听
            locationClient.setLocationListener(locationListener);
            startLocation();
        }else {
            Toast.makeText(context,"暂无网络连接，请连接网络",Toast.LENGTH_SHORT).show();
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
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(ReportViolationActivity.this);
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
                            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivity(localIntent);
                            finishMine();
                        }
                    });
                    customBuilder.create().show();
                    return;
                }
            } else {
                Toast.makeText(context,"定位失败",Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyLocation();
    }
}
