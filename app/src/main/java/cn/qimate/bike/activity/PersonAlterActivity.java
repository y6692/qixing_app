package cn.qimate.bike.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
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
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
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

import cn.jpush.android.api.JPushInterface;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.nostra13.universalimageloader.core.ImageLoader;
import cn.qimate.bike.BuildConfig;
import cn.qimate.bike.R;
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
import cn.qimate.bike.model.UserIndexBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;

/**
 * Created by Administrator1 on 2017/3/15.
 */
@SuppressLint("NewApi")
public class PersonAlterActivity extends SwipeBackActivity implements View.OnClickListener{

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;

    private Context context;
    private LoadingDialog loadingDialog;
    private PullToZoomScrollViewEx scrollView;
    private ImageView backImage;
    private ImageView settingImage;
    private ImageView headerImageView;
    private ImageView authState;
    private TextView userName;
    private LinearLayout curRouteLayout, hisRouteLayout, myPurseLayout;
    private RelativeLayout myIntegralLayout, myMsgLayout, changePsdLayout,
            helpCenterLayout, aboutUsLayout,billing_ruleLayout,questionLayout,insuranceLayout;
    private RelativeLayout checkUpdataLayout;
    private TextView myPurse, myIntegral;

    private Button takePhotoBtn, pickPhotoBtn, cancelBtn;
    private String imgUrl = Urls.uploadsheadImg;
    private String imageurl = "";
    private Uri imageUri;
    private final String IMAGE_FILE_NAME = "picture.jpg";// 照片文件名称
    private String urlpath; // 图片本地路径
    private String resultStr = ""; // 服务端返回结果集
    private final int REQUESTCODE_PICK = 0; // 相册选图标记
    private final int REQUESTCODE_TAKE = 1; // 相机拍照标记
    private final int REQUESTCODE_CUTTING = 2; // 图片裁切标记

    private LinearLayout logoutLayout;
    /**
     * 弹窗背景
     */
    private ImageView iv_popup_window_back;
    /**
     * 弹窗容器
     */
    private RelativeLayout rl_popup_window;

    private Dialog dialog;
    private ImageView titleImage;
    private ImageView exImage_1;
    private ImageView exImage_2;
    private ImageView exImage_3;

    private ImageView closeBtn;

    private ImageView superVip;
    private String rule = "";

    private int imageWith = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_person_alter);
        context = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(context, "您的设备不支持蓝牙4.0", Toast.LENGTH_SHORT).show();
                scrollToFinishActivity();
            }
            //蓝牙锁
            BluetoothManager bluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

            BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(context, "获取蓝牙失败", Toast.LENGTH_SHORT).show();
                scrollToFinishActivity();
                return;
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 188);
            }
        }
        scrollView = (PullToZoomScrollViewEx) findViewById(R.id.scroll_view);
        loadViewForCode();
        imageWith = (int)(getWindowManager().getDefaultDisplay().getWidth() * 0.8);
        initView();
    }

    private void initView() {

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        imageUri = Uri.parse("file:///sdcard/temp.jpg");
        iv_popup_window_back = (ImageView) findViewById(R.id.popupWindow_back);
        rl_popup_window = (RelativeLayout) findViewById(R.id.popupWindow);

        takePhotoBtn = (Button) findViewById(R.id.takePhotoBtn);
        pickPhotoBtn = (Button) findViewById(R.id.pickPhotoBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);

        takePhotoBtn.setOnClickListener(itemsOnClick);
        pickPhotoBtn.setOnClickListener(itemsOnClick);
        cancelBtn.setOnClickListener(itemsOnClick);

        backImage = scrollView.getPullRootView().findViewById(R.id.personUI_backImage);
        settingImage = scrollView.getPullRootView().findViewById(R.id.personUI_title_settingBtn);
        headerImageView =scrollView.getPullRootView().findViewById(R.id.personUI_bottom_header);
        authState = scrollView.getPullRootView().findViewById(R.id.personUI_bottom_authState);
        userName = scrollView.getPullRootView().findViewById(R.id.personUI_userName);
        superVip = (ImageView)findViewById(R.id.personUI_superVip);
//        myPurse = scrollView.getPullRootView().findViewById(R.id.personUI_bottom_myPurse);
        myIntegral = scrollView.getPullRootView().findViewById(R.id.personUI_bottom_myIntegral);

        curRouteLayout = scrollView.getPullRootView().findViewById(R.id.personUI_bottom_curRouteLayout);
        hisRouteLayout = scrollView.getPullRootView().findViewById(R.id.personUI_bottom_hisRouteLayout);
        myPurseLayout = scrollView.getPullRootView().findViewById(R.id.personUI_bottom_myPurseLayout);

        myIntegralLayout = scrollView.getPullRootView().findViewById(R.id.personUI_bottom_myIntegralLayout);
        myMsgLayout = scrollView.getPullRootView().findViewById(R.id.personUI_bottom_myMsgLayout);
        changePsdLayout = scrollView.getPullRootView().findViewById(R.id.personUI_bottom_changePsdLayout);
        helpCenterLayout = scrollView.getPullRootView().findViewById(R.id.personUI_bottom_helpCenterLayout);
        aboutUsLayout = scrollView.getPullRootView().findViewById(R.id.personUI_bottom_aboutUsLayout);
        billing_ruleLayout = scrollView.getPullRootView().findViewById(R.id.personUI_bottom_billing_ruleLayout);
        questionLayout = scrollView.getPullRootView().findViewById(R.id.personUI_bottom_billing_questionLayout);
        insuranceLayout = scrollView.getPullRootView().findViewById(R.id.personUI_bottom_billing_insuranceLayout);
        checkUpdataLayout = scrollView.getPullRootView().findViewById(R.id.personUI_bottom_checkUpdataLayout);
        logoutLayout = scrollView.getPullRootView().findViewById(R.id.personUI_logoutLayout);

        dialog = new Dialog(context, R.style.Theme_AppCompat_Dialog);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.ui_frist_view, null);
        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);

//        titleImage = dialogView.findViewById(R.id.ui_fristView_title);
        exImage_1 = dialogView.findViewById(R.id.ui_fristView_exImage_1);
//        exImage_2 = dialogView.findViewById(R.id.ui_fristView_exImage_2);
//        exImage_3 = dialogView.findViewById(R.id.ui_fristView_exImage_3);
        closeBtn = dialogView.findViewById(R.id.ui_fristView_closeBtn);

//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) titleImage.getLayoutParams();
//        params.height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.16);
//        titleImage.setLayoutParams(params);

//        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) exImage_1.getLayoutParams();
//        params1.height = (imageWith - DisplayUtil.dip2px(context,20)) * 2 / 5;
//        exImage_1.setLayoutParams(params1);
//
//        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) exImage_2.getLayoutParams();
//        params2.height = (imageWith - DisplayUtil.dip2px(context,20)) * 2 / 5;
//        exImage_2.setLayoutParams(params2);
//
//        LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) exImage_3.getLayoutParams();
//        params3.height = (imageWith - DisplayUtil.dip2px(context,20)) * 2 / 5;
//        exImage_3.setLayoutParams(params3);

        backImage.setOnClickListener(this);
        settingImage.setOnClickListener(this);
        headerImageView.setOnClickListener(this);
        curRouteLayout.setOnClickListener(this);
        hisRouteLayout.setOnClickListener(this);
        myPurseLayout.setOnClickListener(this);
        myIntegralLayout.setOnClickListener(this);
        myMsgLayout.setOnClickListener(this);
        changePsdLayout.setOnClickListener(this);
        helpCenterLayout.setOnClickListener(this);
        aboutUsLayout.setOnClickListener(this);
        logoutLayout.setOnClickListener(this);
        superVip.setOnClickListener(this);
        billing_ruleLayout.setOnClickListener(this);
        questionLayout.setOnClickListener(this);
        insuranceLayout.setOnClickListener(this);
        checkUpdataLayout.setOnClickListener(this);
//        myCommissionLayout.setOnClickListener(this);

//        exImage_1.setOnClickListener(myOnClickLister);
//        exImage_2.setOnClickListener(myOnClickLister);
        closeBtn.setOnClickListener(myOnClickLister);
        billRule();
    }

    private View.OnClickListener myOnClickLister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.ui_fristView_exImage_1:
//                    if (dialog != null && dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                    UIHelper.goWebViewAct(context,"使用说明",Urls.bluecarisee);
//                    break;
//                case R.id.ui_fristView_exImage_2:
//                    if (dialog != null && dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                    UIHelper.goWebViewAct(context,"使用说明",Urls.useHelp);
//                    break;
                case R.id.ui_fristView_closeBtn:
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        String bikenum = SharedPreferencesUrls.getInstance().getString("bikenum","");
        String specialdays = SharedPreferencesUrls.getInstance().getString("specialdays","");
        String ebike_specialdays = SharedPreferencesUrls.getInstance().getString("ebike_specialdays", "");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)) {
            settingImage.setVisibility(View.GONE);
            superVip.setVisibility(View.GONE);
            billing_ruleLayout.setVisibility(View.GONE);
        } else {
            settingImage.setVisibility(View.VISIBLE);
            initHttp();
            if (("0".equals(bikenum) || bikenum == null || "".equals(bikenum))
                    && ("0".equals(specialdays) || specialdays == null || "".equals(specialdays))
                    && ("0".equals(ebike_specialdays) || ebike_specialdays == null || "".equals(ebike_specialdays))){
                superVip.setVisibility(View.GONE);
            }else {
                superVip.setVisibility(View.VISIBLE);
            }
            if ("2".equals(SharedPreferencesUrls.getInstance().getString("iscert",""))){
                billing_ruleLayout.setVisibility(View.VISIBLE);
            }else {
                billing_ruleLayout.setVisibility(View.GONE);
            }
        }

    }
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (requestCode) {
                    case REQUESTCODE_PICK:// 直接从相册获取
                        if (data != null) {
                            try {
                                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        File imgUri = new File(GetImagePath.getPath(context, data.getData()));
                                        Uri dataUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", imgUri);

//                                        Uri contentUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", apkfile);

                                        startPhotoZoom(dataUri);
                                    } else {
                                        startPhotoZoom(data.getData());
                                    }
                                } else {
                                    Toast.makeText(context, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();// 用户点击取消操作

                                Log.e("REQUESTCODE_PICK===e", "==="+e);
                            }
                        }
                        break;
                    case REQUESTCODE_TAKE:// 调用相机拍照
//                File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
//                startPhotoZoom(Uri.fromFile(temp));
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                //通过FileProvider创建一个content类型的Uri
                                Uri inputUri = FileProvider.getUriForFile(context,
                                        BuildConfig.APPLICATION_ID + ".fileprovider",
                                        new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME));
                                startPhotoZoom(inputUri);//设置输入类型
                            } else {
                                File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                                startPhotoZoom(Uri.fromFile(temp));
                            }
                        } else {
                            Toast.makeText(context, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
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
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null,
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
                        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
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
                                        PersonAlterActivity.this.requestPermissions(new String[]{Manifest.permission.CAMERA},
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
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(PersonAlterActivity.this,
//                                    BuildConfig.APPLICATION_ID + ".provider",
//                                    new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, RxFileTool.getUriForFile(context,
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
                        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                        // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(pickIntent, REQUESTCODE_PICK);
                    } else {
                        Toast.makeText(context, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                    }
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
                    case 101:
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            // Permission Granted
                            if (permissions[0].equals(Manifest.permission.CAMERA)) {

                                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(PersonAlterActivity.this,
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
                        onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        });

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
            UtilBitmap.blurImageView(this, iv_popup_window_back, 5, 0xAA000000);
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

    @Override
    public void onClick(View v) {

        final String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)) {
            Toast.makeText(context, "请先登录账号", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.personUI_backImage:
                scrollToFinishActivity();
                break;
            case R.id.personUI_title_settingBtn:
                UIHelper.goToAct(context, SettingActivity.class);
                break;
            case R.id.personUI_bottom_header:
                clickPopupWindow();
                break;
            case R.id.personUI_bottom_curRouteLayout:
                getCurrentorder(uid, access_token);
                break;
            case R.id.personUI_bottom_hisRouteLayout:
                UIHelper.goToAct(context, HistoryRoadActivity.class);
                break;
            case R.id.personUI_bottom_vipCenterLayout:
                UIHelper.goToAct(context, ActionCenterActivity.class);
                break;
            case R.id.personUI_bottom_myPurseLayout:
                UIHelper.goToAct(context, MyPurseActivity.class);
                break;
            case R.id.personUI_bottom_myIntegralLayout:
                UIHelper.goToAct(context, MyIntegralActivity.class);
                break;
            case R.id.personUI_bottom_myMsgLayout:
                UIHelper.goToAct(context, MyMessageActivity.class);
                break;
            case R.id.personUI_bottom_changePsdLayout:
                UIHelper.goToAct(context, ChangePasswordPhoneActivity.class);
                break;
            case R.id.personUI_bottom_helpCenterLayout:
                WindowManager windowManager = getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.width = (int) (display.getWidth() * 0.8); // 设置宽度0.6
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                dialog.getWindow().setAttributes(lp);
                dialog.show();
                break;
            case R.id.personUI_bottom_aboutUsLayout:
                UIHelper.goWebViewAct(context, "关于我们", Urls.aboutUs);
                break;
            case R.id.personUI_bottom_billing_ruleLayout:
                CustomDialog.Builder builder = new CustomDialog.Builder(this);
                builder.setTitle("计费规则").setMessage(rule)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                         }
                    });
                builder.create().show();
                break;
            case R.id.personUI_logoutLayout:
                CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                customBuilder.setTitle("温馨提示").setMessage("确认退出吗?")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        logout(uid, access_token);
                    }
                });
                customBuilder.create().show();
                break;
            case R.id.personUI_superVip:
                UIHelper.goToAct(context,SuperVipActivity.class);
                break;
            case R.id.personUI_bottom_billing_questionLayout:
//                UIHelper.goWebViewAct(context,"常见问题", "http://www.7mate.cn/App/Helper/index.html");

                UIHelper.goToAct(context, ServiceCenter0Activity.class);

                break;
            case R.id.personUI_bottom_billing_insuranceLayout:
                Intent intent1 = new Intent(context,InsureanceActivity.class);
                intent1.putExtra("isBack",true);
                context.startActivity(intent1);
                break;
            case R.id.personUI_bottom_checkUpdataLayout:
                // 版本更新
                UpdateManager.getUpdateManager().setType(0).checkAppUpdate(this, context, 3, null);
                break;
            default:
                break;
        }
    }

    private void logout(String uid, String access_token) {

        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("access_token", access_token);
        HttpHelper.post(context, Urls.logout, params, new TextHttpResponseHandler() {
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
                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                            if (result.getFlag().equals("Success")) {
                                SharedPreferencesUrls.getInstance().putString("uid", "");
                                SharedPreferencesUrls.getInstance().putString("access_token", "");
                                SharedPreferencesUrls.getInstance().putString("nickname", "");
                                SharedPreferencesUrls.getInstance().putString("realname", "");
                                SharedPreferencesUrls.getInstance().putString("sex", "");
                                SharedPreferencesUrls.getInstance().putString("headimg", "");
                                SharedPreferencesUrls.getInstance().putString("points", "");
                                SharedPreferencesUrls.getInstance().putString("money", "");
                                SharedPreferencesUrls.getInstance().putString("bikenum", "");
                                SharedPreferencesUrls.getInstance().putString("iscert", "");
//                              setAlias("");
                                Toast.makeText(context, "恭喜您,您已安全退出!", Toast.LENGTH_SHORT).show();
                                scrollToFinishActivity();
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
        });
    }

    private void getCurrentorder(String uid, String access_token) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("access_token", access_token);
        HttpHelper.post(context, Urls.getCurrentorder, params, new TextHttpResponseHandler() {
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
        });
    }

    private void initHttp() {

        String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (uid != null && !"".equals(uid) && access_token != null && !"".equals(access_token)) {
            RequestParams params = new RequestParams();
            params.put("uid", uid);
            params.put("access_token", access_token);
            HttpHelper.get(context, Urls.userIndex, params, new TextHttpResponseHandler() {
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
                                ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                                if (result.getFlag().equals("Success")) {
                                    UserIndexBean bean = JSON.parseObject(result.getData(), UserIndexBean.class);
//                                  myPurse.setText(bean.getMoney());
                                    myIntegral.setText(bean.getPoints());
                                    userName.setText(bean.getTelphone());
                                    if (bean.getHeadimg() != null && !"".equals(bean.getHeadimg())) {
                                        if ("gif".equalsIgnoreCase(bean.getHeadimg().substring(bean.getHeadimg().lastIndexOf(".") + 1,
                                                bean.getHeadimg().length()))) {
                                            Glide.with(PersonAlterActivity.this).load(Urls.host + bean.getHeadimg())
                                                    .asGif().centerCrop().into(headerImageView);
                                        } else {
                                            Glide.with(PersonAlterActivity.this).load(Urls.host + bean.getHeadimg())
                                                    .asBitmap().centerCrop().into(headerImageView);
                                        }
                                    }
                                    if ("2".equals(bean.getIscert())) {
                                        authState.setVisibility(View.VISIBLE);
                                    } else {
                                        authState.setVisibility(View.GONE);
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
            });
        } else {
            Toast.makeText(context, "请先登录账号", Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }
    }
    private void loadViewForCode() {
        View headView = LayoutInflater.from(this).inflate(R.layout.profile_head_view, null, false);
        View zoomView = LayoutInflater.from(this).inflate(R.layout.profile_zoom_view, null, false);
        View contentView = LayoutInflater.from(this).inflate(R.layout.profile_content_view, null, false);

        scrollView.setHeaderView(headView);
        scrollView.setZoomView(zoomView);
        scrollView.setScrollContentView(contentView);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);

            TextView version = contentView.findViewById(R.id.version);
            version.setText("检查更新 V"+info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }



    }
    private void billRule(){
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            HttpHelper.get(context, Urls.account_rules, params, new TextHttpResponseHandler() {
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
                                ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                                if (result.getFlag().equals("Success")) {
                                    rule = result.getData();
                                }else {
                                    Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                            }
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }
                    });

                }
            });
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
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, null);
                    break;

                case MSG_SET_TAGS:
                    JPushInterface.setAliasAndTags(getApplicationContext(), null, (Set<String>) msg.obj, null);
                    break;

                default:
            }
        }
    };
}
