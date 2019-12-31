package cn.qimate.bike.activity;

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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
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
import com.bumptech.glide.Glide;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadOptions;

import org.apache.http.Header;
import org.json.JSONArray;
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

import cn.jock.pickerview.view.view.OptionsPickerView;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.nostra13.universalimageloader.core.ImageLoader;
import cn.qimate.bike.BuildConfig;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.BitmapUtils1;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.StringUtil;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.img.NetUtil;
import cn.qimate.bike.model.AuthStateBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.SchoolListBean;
import cn.qimate.bike.model.UpTokenBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.FileUtil;
import cn.qimate.bike.util.QiNiuInitialize;
import cn.qimate.bike.util.ToastUtil;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by yuanyi on 2019/12/19.
 */

public class ComplainActivity extends SwipeBackActivity implements View.OnClickListener {

    private Context context;
//    private LoadingDialog loadingDialog;
    private ImageView backImg;
    private TextView title, rightBtn, getCodeText;
    private Button takePhotoBtn,pickPhotoBtn,cancelBtn;

    private LinearLayout ll_1, ll_2, ll_3;
    private RelativeLayout schoolLayout;
    private EditText oldPhoneEdit, pwdEdit, newPhoneEdit, codeEdit;
    private Button submitBtn;

//    private RelativeLayout uploadImageLayout;
    private ImageView uploadImage, uploadImage2;
    private int photo = 1;

//    private RelativeLayout addImageLayout;
//    private LinearLayout headLayout;

    private String imgUrl = Urls.uploadsImg;

    private String imageurl = "";
    private String imageurl2 = "";
    private Uri imageUri;
    private final String IMAGE_FILE_NAME = "picture.jpg";// 照片文件名称
    private String urlpath; // 图片本地路径
    private String resultStr = ""; // 服务端返回结果集
    private final int REQUESTCODE_PICK = 0; // 相册选图标记
    private final int REQUESTCODE_TAKE = 1; // 相机拍照标记
    private final int REQUESTCODE_CUTTING = 2; // 图片裁切标记

    /**
     * 弹窗背景
     */
    private ImageView iv_popup_window_back;
    /**
     * 弹窗容器
     */
    private RelativeLayout rl_popup_window;

    private OptionsPickerView pvOptions;
//    private OptionsPickerView pvOptions1;
//    private OptionsPickerView pvOptions2;
//    private String sex = "";


    // 输入法
    private List<SchoolListBean> schoolList;
    static ArrayList<String> item = new ArrayList<>();
    static ArrayList<String[]> item1 = new ArrayList<>();
    static ArrayList<String> item2 = new ArrayList<>();
    static ArrayList<String> item3 = new ArrayList<>();

    private String cert_method = "";
    private  boolean flag = false;
    private  boolean isVisible = false;

    private String upToken = "";

    private Bitmap upBitmap;
    File picture;

    private String oldPhone = "";
    private String pwd = "";
    private String newPhone = "";
    private String code = "";

    private boolean isCode;
    private int num;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);
        context = this;

        CrashHandler.getInstance().setmContext(this);

        schoolList = new ArrayList<>();
        initView();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void initView(){
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        imageUri = Uri.parse("file:///sdcard/temp.jpg");
        iv_popup_window_back = (ImageView)findViewById(R.id.popupWindow_back);
        rl_popup_window = (RelativeLayout)findViewById(R.id.popupWindow);

        // 选项选择器
        pvOptions = new OptionsPickerView(context,false);

        pvOptions.setTitle("选择入学时间");

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
//        rightBtn = (TextView) findViewById(R.id.mainUI_title_rightBtn);
//        rightBtn.setText("联系客服");

        takePhotoBtn = (Button)findViewById(R.id.takePhotoBtn);
        pickPhotoBtn = (Button)findViewById(R.id.pickPhotoBtn);
        cancelBtn = (Button)findViewById(R.id.cancelBtn);

        takePhotoBtn.setOnClickListener(itemsOnClick);
        pickPhotoBtn.setOnClickListener(itemsOnClick);
        cancelBtn.setOnClickListener(itemsOnClick);


        oldPhoneEdit = (EditText)findViewById(R.id.ui_complain_old_phone);
        pwdEdit = (EditText)findViewById(R.id.ui_complain_pwd);
        newPhoneEdit = (EditText)findViewById(R.id.ui_complain_new_phone);
        codeEdit = (EditText)findViewById(R.id.ui_complain_code);
        getCodeText = (TextView)findViewById(R.id.ui_complain_getCode);
        submitBtn = (Button) findViewById(R.id.ui_complain_submitBtn);

        ll_1 = (LinearLayout)findViewById(R.id.ll_1);
        ll_2 = (LinearLayout)findViewById(R.id.ll_2);
        ll_3 = (LinearLayout)findViewById(R.id.ll_3);
//        uploadImageLayout = (RelativeLayout)findViewById(R.id.ui_realNameAuth_uploadImageLayout);
        uploadImage = (ImageView)findViewById(R.id.ui_complain_uploadImage);
        uploadImage2 = (ImageView)findViewById(R.id.ui_complain_uploadImage2);
//        addImageLayout = (RelativeLayout)findViewById(R.id.ui_realNameAuth_addImageLayout);


        if (schoolList.isEmpty() || item1.isEmpty()){
            getSchoolList();
        }
        backImg.setOnClickListener(this);
        getCodeText.setOnClickListener(this);
        uploadImage.setOnClickListener(this);
        uploadImage2.setOnClickListener(this);
        submitBtn.setOnClickListener(this);


        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");

        Log.e("RNA===initView", uid+"==="+access_token+"==="+SharedPreferencesUrls.getInstance().getString("iscert",""));

        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context,LoginActivity.class);
        }else {
            getUpToken();
        }
//        getGradeList();

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
//            case R.id.mainUI_title_rightBtn:
//                UIHelper.goToAct(context, ServiceCenterActivity.class);
//                break;

            case R.id.ui_complain_getCode:

                newPhone = newPhoneEdit.getText().toString();
                if (newPhone == null || "".equals(newPhone)) {
                    Toast.makeText(context, "请输入您的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!StringUtil.isPhoner(newPhone)) {
                    Toast.makeText(context, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendCode();
                break;

            case R.id.ui_complain_uploadImage:
                photo = 1;
                clickPopupWindow();
                break;
            case R.id.ui_complain_uploadImage2:
                photo = 2;
                clickPopupWindow();
                break;
            case R.id.ui_complain_submitBtn:
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
                oldPhone = oldPhoneEdit.getText().toString();
                pwd = pwdEdit.getText().toString();
                newPhone = newPhoneEdit.getText().toString();
                code = codeEdit.getText().toString();
                if (access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                }else {

                    if (oldPhone == null || "".equals(oldPhone)){
                        Toast.makeText(context,"请填写您的原手机号",Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    if (pwd == null || "".equals(pwd)){
//                        Toast.makeText(context,"请填写您的原密码",Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                    if (newPhone == null || "".equals(newPhone)){
                        Toast.makeText(context,"请填写您的新手机号",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (code == null || "".equals(code)){
                        Toast.makeText(context,"请输入验证码",Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    if (isVisible){
//                    }
                    if (imageurl == null || "".equals(imageurl)){
                        Toast.makeText(context,"请上传您的证件照片",Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    if(!"0".equals(cert_method)){
//                    }
                    if (imageurl2 == null || "".equals(imageurl2)){
                        Toast.makeText(context,"请上传您的手持证件照片",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.e("onClick===", imageurl+"==="+imageurl2);

//                    if("0".equals(cert_method)){
//                        SubmitBtn2(uid, access_token, realname, identityNumber);
//                    }else{
//                        SubmitBtn();
//                    }

                    SubmitBtn();
                }
                break;
        }
    }

    private void sendCode() {

        Log.e("verificationcode===0", "==="+newPhone);

        try{
            RequestParams params = new RequestParams();
            params.add("phone", newPhone);
            params.add("scene", 2);

            HttpHelper.post(context, Urls.verificationcode, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("请稍等");
                        loadingDialog.show();
                    }
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {

//                        Toast.makeText(context, "=="+responseString, Toast.LENGTH_LONG).show();

                        Log.e("verificationcode===", "==="+responseString);

                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                        mHandler.sendEmptyMessage(2);

                        // 开始60秒倒计时
                        mHandler.sendEmptyMessageDelayed(1, 1000);

//                        Intent intent = new Intent();
//                        intent.setClass(context, NoteLoginActivity.class);
//                        intent.putExtra("telphone",telphone);
//                        startActivity(intent);



//                        if (result.getFlag().equals("Success")) {
//
//
//                        } else {
//                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(context, "fail=="+responseString, Toast.LENGTH_LONG).show();

                    Log.e("verificationcode===fail", throwable.toString()+"==="+responseString);

                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                    UIHelper.ToastError(context, throwable.toString());
                }
            });
        }catch (Exception e){
            Toast.makeText(context, "==="+e, Toast.LENGTH_SHORT).show();
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
                onFailureCommon("ca===getUpToken", throwable.toString());
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

    public void uploadImage() {
        //定义数据上传结束后的处理动作
        final UpCompletionHandler upCompletionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {

//                JSONObject jsonObject = new JSONObject(info.timeStamp);

                Log.e("uploadImage===0", photo+"==="+response);

                try {
                    JSONObject jsonObject = new JSONObject(response.getString("image"));

                    if(photo == 1){
                        imageurl = jsonObject.getString("key");
                    }else{
                        imageurl2 = jsonObject.getString("key");
                    }

                    Log.e("UpCompletion===", jsonObject+"==="+jsonObject.getString("key")+"==="+key+"==="+info+"==="+response+"==="+info.timeStamp+"==="+"http://q0xo2if8t.bkt.clouddn.com/" + key+"?e="+info.timeStamp+"&token="+upToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

    private void initHttp(String uid, String access_token){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        HttpHelper.get(context, Urls.getCertification, params, new TextHttpResponseHandler() {
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

                            Log.e("getCertification===", "==="+responseString);

                            if (result.getFlag().equals("Success")) {
                                AuthStateBean bean = JSON.parseObject(result.getData(),AuthStateBean.class);
//                                school = bean.getSchool();
//                                schoolText.setText(school);
//                                realNameEdit.setText(bean.getRealname());
//                                studentEdit.setText(bean.getStunum());
                                imageurl = bean.getStunumfile();
                                imageurl2 = bean.getStunumfile2();

                                cert_method = bean.getCert_method();

                                if("1".equals(bean.getIscert())){
//                                    schoolText.setText("");
//                                    realNameEdit.setText("");
//                                    studentEdit.setText("");
//                                    imageurl = "";
//                                    imageurl2 = "";
                                    isVisible = false;
                                    ll_1.setVisibility(View.GONE);
                                }else{
                                    isVisible = true;
                                    ll_1.setVisibility(View.VISIBLE);
                                }

                                if("0".equals(cert_method)){
                                    isVisible = true;
                                    ll_1.setVisibility(View.VISIBLE);
                                    ll_2.setVisibility(View.GONE);
                                    ll_3.setVisibility(View.GONE);
                                }else if("1".equals(cert_method) || "2".equals(cert_method)){
                                    isVisible = false;
                                    ll_1.setVisibility(View.GONE);
                                }else{
                                    isVisible = true;
                                    ll_1.setVisibility(View.VISIBLE);
                                    ll_2.setVisibility(View.VISIBLE);
                                    ll_3.setVisibility(View.VISIBLE);
                                }

//                                getSchoolList();

                                Log.e("getCertification===1", "==="+Urls.host+imageurl);

                                if (bean.getStunumfile() == null || "".equals(bean.getStunumfile()) || "/Public/stunumfile.png".equals(bean.getStunumfile())){
//                                    addImageLayout.setVisibility(View.VISIBLE);
//                                    uploadImage.setVisibility(View.GONE);
                                }else {
//                                    uploadImage.setVisibility(View.VISIBLE);
//                                    addImageLayout.setVisibility(View.GONE);
                                    Glide.with(context).load(Urls.host+imageurl).crossFade().into(uploadImage);
                                }

                                if (bean.getStunumfile2() == null || "".equals(bean.getStunumfile2()) || "/Public/stunumfile2.png".equals(bean.getStunumfile2())){
//                                    addImageLayout.setVisibility(View.VISIBLE);
//                                    uploadImage.setVisibility(View.GONE);
                                }else {
//                                    uploadImage.setVisibility(View.VISIBLE);
//                                    addImageLayout.setVisibility(View.GONE);
                                    Glide.with(context).load(Urls.host+imageurl2).crossFade().into(uploadImage2);
                                }
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
        });
    }


    private void SubmitBtn(){

        Log.e("SubmitBtn===0", oldPhone+"==="+pwd+"==="+newPhone+"==="+code+"==="+imageurl+"==="+imageurl2);

        RequestParams params = new RequestParams();
        params.put("old_phone", oldPhone);
        params.put("old_password", pwd);
        params.put("new_phone", newPhone);
        params.put("verification_code", code);
        params.put("cert_photo", imageurl);
        params.put("holding_cert_photo", imageurl2);

        HttpHelper.post(context, Urls.appeal, params, new TextHttpResponseHandler() {     //TODO
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

                            Log.e("SubmitBtn===", "==="+responseString);

                            ToastUtil.showMessageApp(context, result.getMessage());

                            if(result.getStatus_code()==200){
                                scrollToFinishActivity();
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

    /**
     * 获取学校
     * */
    private void getSchoolList(){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);

        HttpHelper.get(context, Urls.schoolList, params, new TextHttpResponseHandler() {
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

                            Log.e("getSchoolList===", "==="+responseString);

                            if (result.getFlag().equals("Success")) {
                                JSONArray JSONArray = new JSONArray(result.getData());
                                if (schoolList.size() != 0 || !schoolList.isEmpty()){
                                    schoolList.clear();
                                }
//                                if (item.size() != 0 || !item.isEmpty()){
//                                    item.clear();
//                                }
//                                if (item1.size() != 0 || !item1.isEmpty()){
//                                    item1.clear();
//                                }
                                for (int i = 0; i < JSONArray.length();i++){
                                    SchoolListBean bean = JSON.parseObject(JSONArray.getJSONObject(i).toString(),SchoolListBean.class);
                                    schoolList.add(bean);
//                                    item.add(bean.getSchool()+"_"+bean.getCert_method());
//                                    item.add(bean.getSchool());
//                                    item1.add(new String[]{bean.getSchool(), bean.getCert_method()});

                                }
//                                handler.sendEmptyMessage(0x123);
                            }else {
                                Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e) {
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
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (requestCode) {

                    case 10:
                        if (resultCode == RESULT_OK) {

//                            school_id = data.getStringExtra("school_id");
//                            String school_name = data.getStringExtra("school_name");
//
//                            Log.e("requestCode===10", school_id+"==="+school_name);
//
//                            schoolText.setText(school_name);

                        } else {
//                            Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                        }


                        break;

                    case REQUESTCODE_PICK:// 直接从相册获取
                        if (data != null){
                            try {
                                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                                    if (imageUri != null) {
//                                        urlpath = getRealFilePath(context, data.getData());
                                        urlpath  = FileUtil.getFilePathByUri(context, data.getData());
//                                        if (loadingDialog != null && !loadingDialog.isShowing()) {
//                                            loadingDialog.setTitle("请稍等");
//                                            loadingDialog.show();
//                                        }

//                                        RequestOptions requestOptions1 = new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);

                                        Log.e("REQUESTCODE_PICK===", data.getData()+"==="+urlpath);

//                                        Glide.with(context)
//                                        .load(urlpath)
//                                                .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
//                                        .crossFade()
//                                        .into(uploadImage);

//                                        Bitmap bitmap = BitmapFactory.decodeFile(urlpath);
//

//                                        File picture = new File(Environment.getExternalStorageDirectory(), "com.gamefox.samecity.fish/activity/bill1.png");
                                        picture = new File(urlpath);
//                                        Uri filepath;
                                        Uri filepath = Uri.fromFile(picture);
//                                        Bitmap bitmap = BitmapFactory.decodeFile(filepath.getPath());
//                                        upBitmap = BitmapFactory.decodeFile(urlpath);

                                        compress(); //压缩图片

                                        if(photo == 1){
                                            uploadImage.setImageBitmap(upBitmap);
                                        }else{
                                            uploadImage2.setImageBitmap(upBitmap);
                                        }

                                        Log.e("REQUESTCODE_PICK===3", data.getData()+"==="+filepath.getPath());

                                        uploadImage();
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
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){

//                            if (imageUri != null) {
//                                Log.e("REQUESTCODE_TAKE===", data+"===");
//                                urlpath  = FileUtil.getFilePathByUri(context, data.getData());

                            File temp = new File(Environment.getExternalStorageDirectory() + "/images/" + IMAGE_FILE_NAME);
                            if (Uri.fromFile(temp) != null) {
                                urlpath = getRealFilePath(context, Uri.fromFile(temp));
                                Log.e("REQUESTCODE_TAKE===", temp+"==="+urlpath);

//                                File picture = new File(urlpath);
                                Uri filepath = Uri.fromFile(temp);
//                                upBitmap = BitmapFactory.decodeFile(urlpath);


                                compress(); //压缩图片

                                if(photo == 1){
                                    uploadImage.setImageBitmap(upBitmap);
                                }else{
                                    uploadImage2.setImageBitmap(upBitmap);
                                }

                                Log.e("REQUESTCODE_TAKE===3", photo+"==="+upBitmap+"==="+filepath.getPath());

                                uploadImage();
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
            }
        });

        super.onActivityResult(requestCode, resultCode, data);
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
        intent.putExtra("aspectX", 3);
        intent.putExtra("aspectY", 2);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 900);
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

                Log.e("urlpath===", "==="+urlpath);

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

                            Log.e("mHandler===", urlpath+"==="+Urls.host + imageurl);

//                            addImageLayout.setVisibility(View.GONE);
//                            uploadImage.setVisibility(View.VISIBLE);
//                            Glide.with(context).load(Urls.host+imageurl).crossFade().into(uploadImage);
                            if(photo==1){
                                imageurl = jsonObject.optString("data");
                                ImageLoader.getInstance().displayImage(Urls.host + imageurl, uploadImage);
                            }else{
                                imageurl2 = jsonObject.optString("data");
                                ImageLoader.getInstance().displayImage(Urls.host + imageurl2, uploadImage2);
                            }

                            Log.e("mHandler===2", imageurl+"==="+imageurl2);

                            Toast.makeText(context, "照片上传成功", Toast.LENGTH_SHORT).show();
                        } else {
//                            uploadImage.setVisibility(View.GONE);
//                            addImageLayout.setVisibility(View.VISIBLE);
                            Toast.makeText(context, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
//                        addImageLayout.setVisibility(View.VISIBLE);
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                    break;

                case 1:
                    if (num != 1) {
                        getCodeText.setText((--num) + "s 后点击重新获取");
                    } else {
                        getCodeText.setText("重新获取");
                        getCodeText.setEnabled(true);
                        isCode = false;
                    }
                    if (isCode) {
                        mHandler.sendEmptyMessageDelayed(1, 1000);
                    }
                    break;

                case 2:
                    num = 60;
                    isCode = true;
                    getCodeText.setText(num + "s 后点击重新获取");
                    getCodeText.setEnabled(false);
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
                                customBuilder.setType(3).setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();

                                        ComplainActivity.this.requestPermissions(new String[] { Manifest.permission.CAMERA },
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
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, RxFileTool.getUriForFile(context,
//                                    new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
//                            takeIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            takeIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                        }else {
//                            // 下面这句指定调用相机拍照后的照片存储的路径
//                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                                    Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
//                        }

                        File file = new File(Environment.getExternalStorageDirectory()+"/images/", IMAGE_FILE_NAME);
                        if(!file.getParentFile().exists()){
                            file.getParentFile().mkdirs();
                        }


//                        File file = new File(Environment.getExternalStorageDirectory()+"/images/", IMAGE_FILE_NAME);
//                        File file = new File(Environment.getExternalStorageDirectory()+"/", IMAGE_FILE_NAME);


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(ComplainActivity.this,
                                    BuildConfig.APPLICATION_ID + ".fileprovider", file));

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
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        Intent intent;
                        if (Build.VERSION.SDK_INT < 19) {
                            intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                        } else {
                            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        }
                        startActivityForResult(intent, REQUESTCODE_PICK);
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
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (requestCode) {
                    case 0:
                        if (grantResults[0] == PERMISSION_GRANTED) {
                            // Permission Granted
                            if (permissions[0].equals(Manifest.permission.CALL_PHONE)){
                                Intent intent=new Intent();
                                intent.setAction(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + "0519-86999222"));
                                startActivity(intent);
                            }
                        }else {
                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                            customBuilder.setType(3).setTitle("温馨提示").setMessage("您需要在设置里打开电话权限！")
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Intent localIntent = new Intent();
                                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                                    startActivity(localIntent);
                                    finish();
                                }
                            });
                            customBuilder.create().show();
                        }
                        break;
                    case 101:
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            // Permission Granted
                            if (permissions[0].equals(Manifest.permission.CAMERA)) {

                                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


//                            String path = getFilesDir() + File.separator + "images" + File.separator;
//                            File file = new File(path, "test.jpg");
//                            if(!file.getParentFile().exists())
//                                file.getParentFile().mkdirs();

//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                //步骤二：Android 7.0及以上获取文件 Uri
//                                mUri = FileProvider.getUriForFile(RealNameAuthActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", file);
//                            }

//                            File file = new File(Environment.getExternalStorageDirectory()+"/images/", IMAGE_FILE_NAME);

                                    File file = new File(Environment.getExternalStorageDirectory()+"/images/", IMAGE_FILE_NAME);
                                    if(!file.getParentFile().exists()){
                                        file.getParentFile().mkdirs();
                                    }

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(ComplainActivity.this,
                                                BuildConfig.APPLICATION_ID + ".fileprovider",
//                                        "com.vondear.rxtools.fileprovider",
                                                file));


//                                takeIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                takeIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


//                                ContentValues contentValues = new ContentValues(1);
//                                contentValues.put(MediaStore.Images.Media.DATA, new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME).getAbsolutePath());
//                                Uri uri = getApplication().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//                                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);


                                    }else {
                                        // 下面这句指定调用相机拍照后的照片存储的路径
//                                      takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));

                                        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                                    }
                                    startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                                }else {
                                    Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
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
}
