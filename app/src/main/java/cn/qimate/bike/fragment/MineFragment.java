package cn.qimate.bike.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jpush.android.api.JPushInterface;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.nostra13.universalimageloader.core.ImageLoader;
import cn.qimate.bike.BuildConfig;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.ActionCenterActivity;
import cn.qimate.bike.activity.AuthCenterActivity;
import cn.qimate.bike.activity.ChangePasswordPhoneActivity;
import cn.qimate.bike.activity.ChangePhoneActivity;
import cn.qimate.bike.activity.CurRoadBikedActivity;
import cn.qimate.bike.activity.CurRoadBikingActivity;
import cn.qimate.bike.activity.HistoryRoadActivity;
import cn.qimate.bike.activity.InsureanceActivity;
import cn.qimate.bike.activity.LoginActivity;
import cn.qimate.bike.activity.MainActivity;
import cn.qimate.bike.activity.MyMessageActivity;
import cn.qimate.bike.activity.MyOrderActivity;
import cn.qimate.bike.activity.ServiceCenterActivity;
import cn.qimate.bike.activity.SettingActivity;
import cn.qimate.bike.activity.SuperVipActivity;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.base.BaseFragment;
import cn.qimate.bike.core.common.BitmapUtils1;
import cn.qimate.bike.core.common.DisplayUtil;
import cn.qimate.bike.core.common.GetImagePath;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.UpdateManager;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.img.NetUtil;
import cn.qimate.bike.model.CurRoadBikingBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UserBean;
import cn.qimate.bike.model.UserIndexBean;
import cn.qimate.bike.util.FileUtil;
import cn.qimate.bike.util.ToastUtil;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;

import static android.app.Activity.RESULT_OK;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.provider.DocumentsContract.getDocumentId;

@SuppressLint("NewApi")
public class MineFragment extends BaseFragment implements View.OnClickListener{

    private View v;
    Unbinder unbinder;

    private Context context;
    private Activity activity;

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;

    private LoadingDialog loadingDialog;
    private PullToZoomScrollViewEx scrollView;
    private ImageView rightBtn, iv_isRead;
    private ImageView backImage;
    private ImageView settingImage;
    private ImageView headerImageView;
    private ImageView authState;
    private TextView userName;
    private LinearLayout curRouteLayout, hisRouteLayout;
    private RelativeLayout  myOrderLayout, myMsgLayout, creditLayout, serviceCenterLayout, changePhoneLayout, authLayout, inviteLayout;

    private ImageView iv_popup_window_back;
    private RelativeLayout rl_popup_window;

    private Button takePhotoBtn, pickPhotoBtn, cancelBtn;
    private Bitmap upBitmap;

    private String imgUrl = Urls.uploadsheadImg;
    private String imageurl = "";
    private Uri imageUri;
    private final String IMAGE_FILE_NAME = "picture.jpg";// 照片文件名称
    private String urlpath; // 图片本地路径
    private String resultStr = ""; // 服务端返回结果集
    private final int REQUESTCODE_PICK = 0; // 相册选图标记
    private final int REQUESTCODE_TAKE = 1; // 相机拍照标记
    private final int REQUESTCODE_CUTTING = 2; // 图片裁切标记

    private String credit_scores_h5_title;
    private String credit_scores_h5_url;
    private String invite_h5_title;
    private String invite_h5_url;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_mine, null);
        unbinder = ButterKnife.bind(this, v);

        return v;
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        activity = getActivity();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
//            if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                Toast.makeText(context, "您的设备不支持蓝牙4.0", Toast.LENGTH_SHORT).show();
//                getActivity().finish();
//            }
//            //蓝牙锁
//            BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
//
//            BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
//            if (mBluetoothAdapter == null) {
//                Toast.makeText(context, "获取蓝牙失败", Toast.LENGTH_SHORT).show();
//                scrollToFinishActivity();
//                return;
//            }
//            if (!mBluetoothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, 188);
//            }
//        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        scrollView = (PullToZoomScrollViewEx) getActivity().findViewById(R.id.scroll_view);
//        loadViewForCode();
//        imageWith = (int)(getActivity().getWindowManager().getDefaultDisplay().getWidth() * 0.8);

        initView();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        Log.e("minef===onHiddenChanged", "==="+hidden);

        if(hidden){
            //pause
        }else{
            //resume

            String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
            if("".equals(access_token)){
                ToastUtil.showMessageApp(context, "请先登录");

                Intent intent = new Intent(BaseApplication.context, LoginActivity.class);
                startActivity(intent);
            }else{
                initHttp();
            }

        }
    }

    private void loadViewForCode() {
        View headView = LayoutInflater.from(context).inflate(R.layout.profile_head_view, null, false);
        View zoomView = LayoutInflater.from(context).inflate(R.layout.profile_zoom_view, null, false);
        View contentView = LayoutInflater.from(context).inflate(R.layout.profile_content_view, null, false);

        scrollView.setHeaderView(headView);
        scrollView.setZoomView(zoomView);
        scrollView.setScrollContentView(contentView);
    }


    private void initView(){
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        imageUri = Uri.parse("file:///sdcard/temp.jpg");
        iv_popup_window_back = getActivity().findViewById(R.id.popupWindow_back);
        rl_popup_window = getActivity().findViewById(R.id.popupWindow);

        takePhotoBtn = getActivity().findViewById(R.id.takePhotoBtn);
        pickPhotoBtn = getActivity().findViewById(R.id.pickPhotoBtn);
        cancelBtn = getActivity().findViewById(R.id.cancelBtn);

        takePhotoBtn.setOnClickListener(itemsOnClick);
        pickPhotoBtn.setOnClickListener(itemsOnClick);
        cancelBtn.setOnClickListener(itemsOnClick);

        rightBtn = getActivity().findViewById(R.id.personUI_rightBtn);
        headerImageView = getActivity().findViewById(R.id.personUI_header);
        userName = getActivity().findViewById(R.id.personUI_userName);

        iv_isRead = getActivity().findViewById(R.id.iv_isRead);

//        hisRouteLayout = getActivity().findViewById(R.id.personUI_bottom_hisRouteLayout);
//        myPurseLayout = getActivity().findViewById(R.id.personUI_bottom_myPurseLayout);
//        myRouteLayout = getActivity().findViewById(R.id.personUI_bottom_myRouteLayout);
//        actionCenterLayout = getActivity().findViewById(R.id.personUI_bottom_actionCenterLayout);

//        settingLayout = getActivity().findViewById(R.id.personUI_bottom_settingLayout);
//
        myOrderLayout = getActivity().findViewById(R.id.personUI_myOrderLayout);
        myMsgLayout = getActivity().findViewById(R.id.personUI_myMeaaageLayout);
        creditLayout = getActivity().findViewById(R.id.personUI_creditLayout);
        serviceCenterLayout = getActivity().findViewById(R.id.personUI_serviceCenterLayout);
        changePhoneLayout = getActivity().findViewById(R.id.personUI_changePhoneLayout);
        authLayout = getActivity().findViewById(R.id.personUI_authLayout);
        inviteLayout = getActivity().findViewById(R.id.personUI_inviteLayout);

        rightBtn.setOnClickListener(this);
//        headerImageView.setOnClickListener(this);
        myOrderLayout.setOnClickListener(this);
        myMsgLayout.setOnClickListener(this);
        creditLayout.setOnClickListener(this);
        serviceCenterLayout.setOnClickListener(this);
        changePhoneLayout.setOnClickListener(this);
        authLayout.setOnClickListener(this);
        inviteLayout.setOnClickListener(this);


//        billRule();
    }


    @Override
    public void onResume() {
        super.onResume();
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (access_token == null || "".equals(access_token)) {
//            superVip.setVisibility(View.GONE);
        } else {

            boolean flag = activity.getIntent().getBooleanExtra("flag", false);

            Log.e("minef===onResume", flag+"==="+SharedPreferencesUrls.getInstance().getString("access_token", "")+"==="+type);

            if(flag){

            }

            initHttp();

//            if (("0".equals(bikenum) || bikenum == null || "".equals(bikenum))
//                    && ("0".equals(specialdays) || specialdays == null || "".equals(specialdays))){
//                superVip.setVisibility(View.GONE);
//            }else {
//                superVip.setVisibility(View.VISIBLE);
//            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("minef=onActivityResult", requestCode+"==="+resultCode);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {
//                    codenum = data.getStringExtra("codenum");
//                    m_nowMac = data.getStringExtra("m_nowMac");



                    ((MainActivity)getActivity()).changeTab(0);

                } else {
//                    Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUESTCODE_PICK:// 直接从相册获取


                if (resultCode == RESULT_OK) {

                    if (data != null) {
                        try {
                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                                Log.e("minef=REQUESTCODE_PICK", Build.VERSION.SDK_INT+"==="+data.getData());

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    File imgUri = new File(GetImagePath.getPath(context, data.getData()));

                                    Log.e("minef=REQUESTCODE_PICK2", imgUri+"==="+BuildConfig.APPLICATION_ID);

                                    Uri dataUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", imgUri);

                                    Log.e("minef=REQUESTCODE_PICK3", imgUri+"==="+dataUri);

                                    startPhotoZoom(dataUri);
                                } else {
                                    startPhotoZoom(data.getData());
                                }
                            } else {
                                Toast.makeText(context, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();// 用户点击取消操作
                        }
                    }

//                    if (data != null){
//                        try {
//                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//                                if (imageUri != null) {
//                                urlpath = getRealFilePath(data.getData());
////                                urlpath  = FileUtil.getFilePathByUri(context, data.getData());
////                                urlpath = getRealFilePath(context, imageUri);
//
////                                urlpath  = "/zhuanke/firstBottomMenu/4321681icon_w_1_ORIGIN_0kjM.png";
//
////                                    Cursor cursor = null;
////
//////                                String[] proj = { MediaStore.Images.Media.DATA};
//////                                cursor = getActivity().getContentResolver().query(data.getData(), proj, null, null, null);
////                                    cursor = getActivity().getContentResolver().query(data.getData(), null, null, null, null);
////
////                                    if (cursor.moveToFirst()) {
//////                                      urlpath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
////                                        urlpath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
////
////                                        Log.e("minef=REQUESTCODE_PICK0", cursor+"==="+urlpath);
////
////                                        //api>=19时，photo_path的值为null，此时再做处理
////                                        if(urlpath == null) {
////                                            String wholeID = getDocumentId(data.getData());
////                                            String id = wholeID.split(":")[1];
////                                            String[] column = { MediaStore.Images.Media.DATA };
////                                            String sel = MediaStore.Images.Media._ID +"=?";
////                                            cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[] { id }, null);
////                                            int columnIndex = cursor.getColumnIndex(column[0]);
////                                            if (cursor.moveToFirst()){
////                                                urlpath = cursor.getString(columnIndex);//此时的路径为照片路径
////                                            }
////                                        }
////                                    }
////                                    cursor.close();
//
////                                    mHandler.sendEmptyMessage(1);
//
//                                    compress(); //压缩图片
//
//                                    //                                Bitmap bitmap = BitmapFactory.decodeFile(filepath.getPath());
//                                    //                                upBitmap = BitmapFactory.decodeFile(urlpath);
//
//                                    Log.e("minef=REQUESTCODE_PICK", urlpath+"==="+imageUri+"==="+upBitmap);
//
//                                    //                                upBitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uriImageview));
//
//                                    headerImageView.setImageBitmap(upBitmap);
//
//                                    Log.e("minef=REQUESTCODE_PICK3", "===");
//
//
////                                uploadImage();
//                                }
//                            }else {
//                                Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (NullPointerException e) {
//                            e.printStackTrace();// 用户点击取消操作
//                        }
//                    }

                } else {
//                    Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                }



                break;
            case REQUESTCODE_TAKE:// 调用相机拍照
//                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        //通过FileProvider创建一个content类型的Uri
//                        Uri inputUri = FileProvider.getUriForFile(context,
//                                BuildConfig.APPLICATION_ID + ".provider",
//                                new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME));
//                        startPhotoZoom(inputUri);//设置输入类型
//                    } else {
//                        File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
//                        startPhotoZoom(Uri.fromFile(temp));
//                    }
//                } else {
//                    Toast.makeText(context, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
//                }

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){

                    File temp = new File(Environment.getExternalStorageDirectory() + "/images/" + IMAGE_FILE_NAME);
                    if (Uri.fromFile(temp) != null) {
                        urlpath = getRealFilePath(Uri.fromFile(temp));
                        Log.e("REQUESTCODE_TAKE===", temp+"==="+urlpath);

//                        Uri filepath = Uri.fromFile(temp);

                        compress(); //压缩图片

                        headerImageView.setImageBitmap(upBitmap);

                        Log.e("REQUESTCODE_TAKE===3", upBitmap+"===");

//                        uploadImage();
                    }

//                            File temp = new File(Environment.getExternalStorageDirectory() + "/images/" + IMAGE_FILE_NAME);
//                            if (Uri.fromFile(temp) != null) {
//                                urlpath = getRealFilePath(context, Uri.fromFile(temp));
//
//                                Log.e("REQUESTCODE_TAKE===", temp+"==="+urlpath);
//
//                                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                                    loadingDialog.setTitle("请稍等");
//                                    loadingDialog.show();
//                                }
//
//                                new Thread(uploadImageRunnable).start();
//                            }
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

        Log.e("minef=startPhotoZoom", imageUri+"==="+uri);

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

    public String getRealFilePath(final Uri uri) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
//            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);

            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);

                        Log.e("getRealFilePath===", cursor+"==="+data);
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
     * @param data
     */
    private void setPicToView(Intent data) {
        Bundle extras = data.getExtras();
        if (imageUri != null) {
            urlpath = getRealFilePath(imageUri);
//            if (loadingDialog != null && !loadingDialog.isShowing()) {
//                loadingDialog.setTitle("请稍等");
//                loadingDialog.show();
//            }
//            new Thread(uploadImageRunnable).start();

            Bitmap bitmap = BitmapFactory.decodeFile(urlpath);
            headerImageView.setImageBitmap(bitmap);

//            urlpath  = FileUtil.getFilePathByUri(context, data.getData());
//            urlpath  = FileUtil.getFilePathByUri(context, extras);

            Log.e("minef===setPicToView", data.getData()+"==="+urlpath);

//            compress(); //压缩图片
//            headerImageView.setImageBitmap(upBitmap);

        }

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
    }

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
                            imageurl = jsonObject.optString("data");
//                            Glide.with(context).load(Urls.host + imageurl).asBitmap().into(headerImageView);
                            ImageLoader.getInstance().displayImage(Urls.host + imageurl, headerImageView);
                            Toast.makeText(context, "照片上传成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                    }

                    break;

                case 1:
                    Bitmap bitmap = BitmapFactory.decodeFile(urlpath);
                    headerImageView.setImageBitmap(bitmap);

//                    compress(); //压缩图片
//
//                    //                                Bitmap bitmap = BitmapFactory.decodeFile(filepath.getPath());
//                    //                                upBitmap = BitmapFactory.decodeFile(urlpath);
//
//                    Log.e("minef=REQUESTCODE_PICK", urlpath+"==="+imageUri+"==="+upBitmap);
//
//                    //                                upBitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uriImageview));
//
//                    headerImageView.setImageBitmap(upBitmap);
//
//                    Log.e("minef=REQUESTCODE_PICK3", "===");

                    break;

                default:
                    break;
            }
            return false;
        }
    });

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
            Map<String, String> textParams;
            Map<String, File> fileparams;
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

            }
            mHandler.sendEmptyMessage(0);// 执行耗时的方法之后发送消给handler
        }
    };




    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if (permissions[0].equals(Manifest.permission.CAMERA)) {

                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context,
                                        BuildConfig.APPLICATION_ID + ".fileprovider",
                                        new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                                takeIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                takeIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            } else {
                                // 下面这句指定调用相机拍照后的照片存储的路径
                                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                            }
                            startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                        } else {
                            Toast.makeText(context, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                    customBuilder.setType(3).setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")
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
                            localIntent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
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
    public void onClick(View v) {

        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (access_token == null || "".equals(access_token)) {
            Toast.makeText(context, "请先登录账号", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.personUI_backImage:
                scrollToFinishActivity();
                break;

            case R.id.personUI_rightBtn:
//                UIHelper.goToAct(context, SettingActivity.class);

                Intent intent = new Intent();
                intent.setClass(context, SettingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 10);
                break;

            case R.id.personUI_header:
                clickPopupWindow();
//                UIHelper.goToAct(context, PersonInfoActivity.class);

                break;

            case R.id.personUI_myOrderLayout:
                UIHelper.goToAct(context, MyOrderActivity.class);
                break;

            case R.id.personUI_myMeaaageLayout:
                UIHelper.goToAct(context, MyMessageActivity.class);
                break;

            case R.id.personUI_creditLayout:
                Log.e("personUI_creditLayout", credit_scores_h5_url+"==="+access_token.split(" ")[1]);
                UIHelper.goWebViewAct(context, credit_scores_h5_title, credit_scores_h5_url+"?token="+access_token.split(" ")[1]);
                break;

            case R.id.personUI_serviceCenterLayout:
                Log.e("personUI_serviceCenterL", "===");

                intent = new Intent(context, ServiceCenterActivity.class);
//                intent.putExtra("bikeCode", MainFragment.codenum);
                startActivity(intent);
                break;


            case R.id.personUI_changePhoneLayout:
                UIHelper.goToAct(context, ChangePhoneActivity.class);
                break;

            case R.id.personUI_authLayout:
//                UIHelper.goToAct(context, AuthCenterActivity.class);
                intent = new Intent();
                intent.setClass(context, AuthCenterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 10);
                break;

            case R.id.personUI_inviteLayout:
                UIHelper.goWebViewAct(context, invite_h5_title, invite_h5_url+"?token="+access_token.split(" ")[1]);
                break;


            default:
                break;
        }
    }

    private void clickPopupWindow() {
        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(getActivity());

        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
            UtilBitmap.blurImageView(context, iv_popup_window_back, 5, 0xAA000000);
        } else {
            // 获取的Bitmap为null时，用半透明代替
            iv_popup_window_back.setBackgroundColor(0x77000000);
        }

        // 打开弹窗
        UtilAnim.showToUp(rl_popup_window, iv_popup_window_back);

    }

    private void clickClosePopupWindow() {
        UtilAnim.hideToDown(rl_popup_window, iv_popup_window_back);
    }

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
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
                            } else {
                                CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                customBuilder.setType(3).setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                                101);

                                    }
                                });
                                customBuilder.create().show();
                            }
                            return;
                        }
                    }
//                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    // 下面这句指定调用相机拍照后的照片存储的路径
//                    takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
//                    startActivityForResult(takeIntent, REQUESTCODE_TAKE);
//                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, RxFileTool.getUriForFile(context,
//                                    new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
//                            takeIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            takeIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                        } else {
//                            // 下面这句指定调用相机拍照后的照片存储的路径
//                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
//                        }
//                        startActivityForResult(takeIntent, REQUESTCODE_TAKE);
//                    } else {
//                        Toast.makeText(context, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
//                    }

                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        File file = new File(Environment.getExternalStorageDirectory()+"/images/", IMAGE_FILE_NAME);
                        if(!file.getParentFile().exists()){
                            file.getParentFile().mkdirs();
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file));

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
//                    Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            pickIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(PersonAlterActivity.this,
//                                    BuildConfig.APPLICATION_ID + ".provider",
//                                    new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
//                            pickIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                            pickIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        } else {
//                            // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
//                            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                        }
//                        startActivityForResult(pickIntent, REQUESTCODE_PICK);
//                        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
////                        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
//                        // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
//                        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                        startActivityForResult(pickIntent, REQUESTCODE_PICK);
//
//                        Log.e("minef===pickPhotoBtn", "==="+Intent.ACTION_PICK);

                        Intent intent;
                        if (Build.VERSION.SDK_INT < 19) {
                            intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                        } else {
                            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        }
                        startActivityForResult(intent, REQUESTCODE_PICK);
                    } else {
                        Toast.makeText(context, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                    }

                    break;
                default:
                    break;
            }
        }
    };


    private void getCurrentorder(String uid, String access_token) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("access_token", access_token);
        HttpHelper.post(context, Urls.getCurrentorder, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在加载");
                    loadingDialog.show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        if ("[]".equals(result.getData()) || 0 == result.getData().length()) {
                            SharedPreferencesUrls.getInstance().putBoolean("isStop", true);
                            Toast.makeText(context, "暂无当前行程", Toast.LENGTH_SHORT).show();
                        } else {
                            CurRoadBikingBean bean = JSON.parseObject(result.getData(), CurRoadBikingBean.class);
                            if ("1".equals(bean.getStatus())) {
                                SharedPreferencesUrls.getInstance().putBoolean("isStop", false);
                                UIHelper.goToAct(context, CurRoadBikingActivity.class);
                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                    loadingDialog.dismiss();
                                }
                            } else {
                                SharedPreferencesUrls.getInstance().putBoolean("isStop", true);
                                UIHelper.goToAct(context, CurRoadBikedActivity.class);
                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                    loadingDialog.dismiss();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }
        });
    }

    public void initHttp() {
        Log.e("minef===initHttp", "==="+isHidden());

        if(isHidden()) return;

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (access_token != null && !"".equals(access_token)) {
            HttpHelper.get(context, Urls.user, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("正在加载");
                        loadingDialog.show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                    UIHelper.ToastError(context, throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        Log.e("minef===initHttp", "==="+responseString);

                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                        UserBean bean = JSON.parseObject(result.getData(), UserBean.class);
//                            myPurse.setText(bean.getMoney());
//                            myIntegral.setText(bean.getPoints());
                        userName.setText(bean.getPhone());

                        if(bean.getUnread_count()==0){
                            iv_isRead.setVisibility(View.GONE);
                        }else{
                            iv_isRead.setVisibility(View.VISIBLE);
                        }

                        credit_scores_h5_title = bean.getCredit_scores_h5_title();
                        credit_scores_h5_url = bean.getCredit_scores_h5_url();
                        invite_h5_title = bean.getInvite_h5_title();
                        invite_h5_url = bean.getInvite_h5_url();

                        //TODO  3
//                            if (bean.getHeadimg() != null && !"".equals(bean.getHeadimg())) {
//                                if ("gif".equalsIgnoreCase(bean.getHeadimg().substring(bean.getHeadimg().lastIndexOf(".") + 1, bean.getHeadimg().length()))) {
//                                    Glide.with(getActivity()).load(Urls.host + bean.getHeadimg()).asGif().centerCrop().into(headerImageView);
//                                } else {
//                                    Glide.with(getActivity()).load(Urls.host + bean.getHeadimg()).asBitmap().centerCrop().into(headerImageView);
//                                }
//                            }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                }
            });
        } else {
            Toast.makeText(context, "请先登录账号", Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }
    }


    // 极光推送===================================================================
    private void setAlias(String uid) {
        // 调用JPush API设置Alias
        mHandler1.sendMessage(mHandler1.obtainMessage(MSG_SET_ALIAS, uid));
    }

    private final Handler mHandler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    JPushInterface.setAliasAndTags(getContext(), (String) msg.obj, null, null);
                    break;

                case MSG_SET_TAGS:
                    JPushInterface.setAliasAndTags(getContext(), null, (Set<String>) msg.obj, null);
                    break;

                default:
            }
        }
    };




}
