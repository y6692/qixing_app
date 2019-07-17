package cn.qimate.bike.activity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

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

import butterknife.ButterKnife;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.nostra13.universalimageloader.core.ImageLoader;
import cn.qimate.bike.BuildConfig;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.BitmapUtils1;
import cn.qimate.bike.core.common.DisplayUtil;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.full.EnterActivity;
import cn.qimate.bike.img.NetUtil;
import cn.qimate.bike.lock.utils.ToastUtils;
import cn.qimate.bike.model.CardinfoBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;

/**
 * Created by 123 on 2017/12/3.
 */

public class InsureanceActivity extends SwipeBackActivity implements View.OnClickListener {

    private String imgUrl = Urls.uploadsImg;
    private Uri imageUri;
    private final String IMAGE_FILE_NAME = "picture.jpg";// 照片文件名称
    private String urlpath; // 图片本地路径
    private String resultStr = ""; // 服务端返回结果集
    private final int REQUESTCODE_PICK = 0; // 相册选图标记
    private final int REQUESTCODE_TAKE = 1; // 相机拍照标记
    private final int REQUESTCODE_CUTTING = 2; // 图片裁切标记

    private Context context;
    private LoadingDialog loadingDialog;
    private boolean isAgree = true;
    private String cardfile = "";

    private ImageView backBtn;
    private TextView titleText;
    private TextView realNameText;
    private EditText certificateNumEdit;
    private TextView certificateNumText;
    private ImageView certificateImage;
    private ImageView dealImage;
    private TextView dealText;
    private Button submitBtn;
    private TextView remarkText;
    private ImageView iv_popup_window_back;
    private RelativeLayout rl_popup_window;
    private Button takePhotoBtn,pickPhotoBtn,cancelBtn;

    private boolean isBack = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_insurance);
        ButterKnife.bind(this);
        context = this;
        isBack = getIntent().getExtras().getBoolean("isBack");
        init();
    }

    private void init() {


        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        backBtn = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        titleText = (TextView)findViewById(R.id.mainUI_title_titleText);
        realNameText = (TextView)findViewById(R.id.ui_insurance_realNameText);
        certificateNumEdit = (EditText)findViewById(R.id.ui_insurance_certificateNumEdit);
        certificateNumText = (TextView)findViewById(R.id.ui_insurance_certificateNumText);
        certificateImage = (ImageView)findViewById(R.id.ui_insurance_certificateImage);
        dealImage = (ImageView)findViewById(R.id.ui_insurance_dealImage);
        dealText = (TextView)findViewById(R.id.ui_insurance_dealText);
        submitBtn = (Button)findViewById(R.id.ui_insurance_submitBtn);
        remarkText = (TextView)findViewById(R.id.ui_insurance_remarkText);

        iv_popup_window_back = (ImageView)findViewById(R.id.popupWindow_back);
        rl_popup_window = (RelativeLayout)findViewById(R.id.popupWindow);
        takePhotoBtn = (Button)findViewById(R.id.takePhotoBtn);
        pickPhotoBtn = (Button)findViewById(R.id.pickPhotoBtn);
        cancelBtn = (Button)findViewById(R.id.cancelBtn);

        titleText.setText("骑行保险");

        imageUri = Uri.parse("file:///sdcard/temp.jpg");

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) certificateImage.getLayoutParams();
        params.height = (DisplayUtil.getWindowWidth(this) - DisplayUtil.dip2px(context, 20)) *
                540 / 856;
        certificateImage.setLayoutParams(params);
        initListener();
        initHttp();
    }

    private void initListener() {

        backBtn.setOnClickListener(this);
        certificateImage.setOnClickListener(this);
        dealImage.setOnClickListener(this);
        dealText.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        takePhotoBtn.setOnClickListener(itemsOnClick);
        pickPhotoBtn.setOnClickListener(itemsOnClick);
        cancelBtn.setOnClickListener(itemsOnClick);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.mainUI_title_backBtn:
                if (isBack){
                    scrollToFinishActivity();
                }else {
                    if ((!SharedPreferencesUrls.getInstance().getBoolean("isFirst", true)
                            && getVersion() == SharedPreferencesUrls.getInstance().getInt("version", 0))) {
                        UIHelper.goToAct(context, MainActivity.class);
                    } else {
                        SharedPreferencesUrls.getInstance().putBoolean("isFirst", false);
                        SharedPreferencesUrls.getInstance().putInt("version", getVersion());
                        UIHelper.goToAct(context, EnterActivity.class);
                    }
                }
                break;
            case R.id.ui_insurance_certificateImage:
                clickPopupWindow();
                break;
            case R.id.ui_insurance_dealImage:
                if (isAgree){
                    isAgree = false;
                    dealImage.setImageResource(R.drawable.recharge_normal);
                }else {
                    isAgree = true;
                    dealImage.setImageResource(R.drawable.recharge_selected);
                }
                break;
            case R.id.ui_insurance_dealText:
                UIHelper.goWebViewAct(context,"保险说明","http://www.7mate.cn/App/Helper/insurance.html");
                break;
            case R.id.ui_insurance_submitBtn:
                final String certificateNum = certificateNumEdit.getText().toString().trim();
                if (certificateNum == null || "".equals(certificateNum)){
                    Toast.makeText(context,"请先输入您的证件号码",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cardfile == null || "".equals(cardfile)){
                    Toast.makeText(context,"请先上传您的证件照片",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isAgree){
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                    customBuilder.setTitle("温馨提示").setMessage("是否确认提交?")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            postMsg(certificateNum);
                        }
                    });
                    customBuilder.create().show();
                }else {
                    Toast.makeText(context,"请同意《保险协议》",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void initHttp() {

        String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)) {
            ToastUtils.show("请先登录您的账号");
            UIHelper.goToAct(context, LoginActivity.class);
        } else {
            RequestParams params = new RequestParams();
            params.put("uid", uid);
            params.put("access_token", access_token);
            HttpHelper.get(context, Urls.useinfo, params, new TextHttpResponseHandler() {
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
                            CardinfoBean bean = JSON.parseObject(result.getData(), CardinfoBean.class);
                            realNameText.setText(bean.getRealname());
                            if (bean.getCardnum() != null && !"".equals(bean.getCardnum())) {
                                certificateNumEdit.setText(bean.getCardnum());
                                certificateNumText.setText(bean.getCardnum());
                            }
                            if (bean.getCardfile() != null && !"".equals(bean.getCardfile())) {
                                cardfile = bean.getCardfile();
                                ImageLoader.getInstance().displayImage(Urls.host + cardfile, certificateImage);
                            }
                            if (bean.getCardcheck() != null && !"".equals(bean.getCardcheck())){
                                switch (Integer.parseInt(bean.getCardcheck())){
                                    case 1:
                                        submitBtn.setEnabled(true);
                                        submitBtn.setText("提 交");
                                        dealImage.setEnabled(true);
                                        isAgree = false;
                                        dealImage.setImageResource(R.drawable.recharge_normal);
                                        certificateNumEdit.setEnabled(true);
                                        certificateNumEdit.setVisibility(View.VISIBLE);
                                        certificateNumText.setVisibility(View.GONE);
                                        certificateImage.setEnabled(true);
                                        break;
                                    case 2:
                                        submitBtn.setEnabled(false);
                                        submitBtn.setText("已通过");
                                        dealImage.setEnabled(false);
                                        isAgree = true;
                                        certificateNumEdit.setEnabled(false);
                                        certificateImage.setEnabled(false);
                                        certificateNumEdit.setVisibility(View.GONE);
                                        certificateNumText.setVisibility(View.VISIBLE);
                                        dealImage.setImageResource(R.drawable.recharge_selected);
                                        break;
                                    case 3:
                                        submitBtn.setEnabled(true);
                                        isAgree = false;
                                        certificateNumEdit.setEnabled(true);
                                        certificateImage.setEnabled(true);
                                        dealImage.setImageResource(R.drawable.recharge_normal);
                                        submitBtn.setText("被驳回");
                                        remarkText.setText("*"+bean.getRemark()+"*");
                                        certificateNumEdit.setVisibility(View.VISIBLE);
                                        certificateNumText.setVisibility(View.GONE);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        } else {
                            Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                }
            });
        }
    }

    private void postMsg(String cardnum) {

        String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)) {
            ToastUtils.show("请先登录您的账号");
            UIHelper.goToAct(context, LoginActivity.class);
        } else {
            RequestParams params = new RequestParams();
            params.put("uid", uid);
            params.put("access_token", access_token);
            params.put("cardnum",cardnum);
            params.put("cardfile",cardfile);
            HttpHelper.post(context, Urls.postUseinfo, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("正在提交");
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
                            Toast.makeText(context,"恭喜您,提交成功",Toast.LENGTH_SHORT).show();
                            scrollToFinishActivity();
                        } else {
                            Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                }
            });
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_PICK:// 直接从相册获取
                if (data != null){
                    try {
                        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
                            if (imageUri != null) {
                                urlpath = getRealFilePath(context, data.getData());
                                if (loadingDialog != null && !loadingDialog.isShowing()) {
                                    loadingDialog.setTitle("请稍等");
                                    loadingDialog.show();
                                }
                                new Thread(uploadImageRunnable).start();
                            }
                        }else {
                            Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();// 用户点击取消操作
                    }
                }
                break;
            case REQUESTCODE_TAKE:// 调用相机拍照
//                if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
//                    File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
//                    if (Uri.fromFile(temp) != null) {
//                        urlpath = getRealFilePath(context, Uri.fromFile(temp));
//                        if (loadingDialog != null && !loadingDialog.isShowing()) {
//                            loadingDialog.setTitle("请稍等");
//                            loadingDialog.show();
//                        }
//                        new Thread(uploadImageRunnable).start();
//                    }
//                }else {
//                    Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
//                }
                if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
                    File temp = new File(Environment.getExternalStorageDirectory() + "/images/" + IMAGE_FILE_NAME);
                    if (Uri.fromFile(temp) != null) {
                        urlpath = getRealFilePath(context, Uri.fromFile(temp));
                        if (loadingDialog != null && !loadingDialog.isShowing()) {
                            loadingDialog.setTitle("请稍等");
                            loadingDialog.show();
                        }
                        new Thread(uploadImageRunnable).start();
                    }
                }else {
                    Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            }
            mHandler.sendEmptyMessage(0);// 执行耗时的方法之后发送消给handler
        }
    };

    Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        // 返回数据示例，根据需求和后台数据灵活处理
                        JSONObject jsonObject = new JSONObject(resultStr);
                        // 服务端以字符串“1”作为操作成功标记
                        if (jsonObject.optString("flag").equals("Success")) {
                            BitmapFactory.Options option = new BitmapFactory.Options();
                            // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图，3为三分之一
                            option.inSampleSize = 1;
                            cardfile = jsonObject.optString("data");
                            Log.e("Test","RRRRR:"+cardfile);
                            ImageLoader.getInstance().displayImage(Urls.host + cardfile, certificateImage);
                            Toast.makeText(context, "照片上传成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
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

                                        InsureanceActivity.this.requestPermissions(new String[] { Manifest.permission.CAMERA },
                                                101);

                                    }
                                });
                                customBuilder.create().show();
                            }
                            return;
                        }
                    }
                    if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
                        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        File file = new File(Environment.getExternalStorageDirectory()+"/images/", IMAGE_FILE_NAME);
                        if(!file.getParentFile().exists()){
                            file.getParentFile().mkdirs();
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(InsureanceActivity.this,
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
                    if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
                        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                        // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(pickIntent, REQUESTCODE_PICK);
                    }else {
                        Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                    }
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
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if (permissions[0].equals(Manifest.permission.CAMERA)) {

//                        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
//                            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(InsureanceActivity.this,
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
//                            Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
//                        }

                        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
                            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                            File file = new File(Environment.getExternalStorageDirectory()+"/images/", IMAGE_FILE_NAME);
                            if(!file.getParentFile().exists()){
                                file.getParentFile().mkdirs();
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(InsureanceActivity.this,
                                        BuildConfig.APPLICATION_ID + ".fileprovider",
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
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isBack){
                scrollToFinishActivity();
            }else {
                if ((!SharedPreferencesUrls.getInstance().getBoolean("isFirst", true)
                        && getVersion() == SharedPreferencesUrls.getInstance().getInt("version", 0))) {
                    UIHelper.goToAct(context, MainActivity.class);
                } else {
                    SharedPreferencesUrls.getInstance().putBoolean("isFirst", false);
                    SharedPreferencesUrls.getInstance().putInt("version", getVersion());
                    UIHelper.goToAct(context, EnterActivity.class);
                }
            }
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
            UtilBitmap.blurImageView(this, iv_popup_window_back, 5,0xAA000000);
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
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public int getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
