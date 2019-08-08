package cn.qimate.bike.activity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.apache.http.Header;

import java.util.Set;
import java.util.UUID;

import cn.jpush.android.api.JPushInterface;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.Md5Helper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.StringUtil;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UserMsgBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.ToastUtil;

import static android.text.TextUtils.isEmpty;

/**
 * Created by Administrator on 2017/2/15 0015.
 */

public class LoginActivity extends SwipeBackActivity implements View.OnClickListener {

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;

    private Context context;
    private LoadingDialog loadingDialog;
    private ImageView backImg;
    private TextView title;
    private TextView rightBtn;

    private LinearLayout headLayout;
    private EditText userNameEdit;
    private EditText passwordEdit;
    private Button loginBtn;
    private TextView noteLogin;
    private TextView findPsd;
    private ImageView checkBox;
    private boolean isHidepsd = true;

    private TelephonyManager tm;
    public static boolean isForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_login);
        context = this;
        initView();
    }

    @Override
    public void onResume() {
        isForeground = true;
        super.onResume();
    }

    @Override
    public void onPause() {
        isForeground = false;
        super.onPause();
    }

    private void initView() {

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("登录");
        rightBtn = (TextView) findViewById(R.id.mainUI_title_rightBtn);
        rightBtn.setText("注册");

        headLayout = (LinearLayout) findViewById(R.id.loginUI_headLayout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) headLayout.getLayoutParams();
        params.height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.4);
        headLayout.setLayoutParams(params);

        userNameEdit = (EditText) findViewById(R.id.loginUI_userName);
        passwordEdit = (EditText) findViewById(R.id.LoginUI_password);
        loginBtn = (Button) findViewById(R.id.loginUI_btn);
        noteLogin = (TextView) findViewById(R.id.loginUI_noteLogin);
        findPsd = (TextView) findViewById(R.id.loginUI_findPsd);
        checkBox = (ImageView) findViewById(R.id.LoginUI_checkBox);

        if (SharedPreferencesUrls.getInstance().getString("userName", "") != null &&
                !"".equals(SharedPreferencesUrls.getInstance().getString("userName", ""))) {
            userNameEdit.setText(SharedPreferencesUrls.getInstance().getString("userName", ""));
        }
        userNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtil.isPhoner(userNameEdit.getText().toString().trim())) {
                    SharedPreferencesUrls.getInstance().putString("userName", userNameEdit.getText().toString().trim());
                }
            }
        });

        backImg.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        noteLogin.setOnClickListener(this);
        findPsd.setOnClickListener(this);
        checkBox.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.mainUI_title_rightBtn:
                UIHelper.goToAct(context, RegisterActivity.class);
                scrollToFinishActivity();
                break;
            case R.id.loginUI_btn:
                String telphone = userNameEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                if (telphone == null || "".equals(telphone)) {
                    Toast.makeText(context, "请输入您的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!StringUtil.isPhoner(telphone)) {
                    Toast.makeText(context, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password == null || "".equals(password)) {
                    Toast.makeText(context, "请输入您的密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                LoginHttp(telphone, password);
                break;
            case R.id.loginUI_noteLogin:
                UIHelper.goToAct(context, NoteLoginActivity.class);
                scrollToFinishActivity();
                break;
            case R.id.loginUI_findPsd:
                UIHelper.goToAct(context, FindPsdActivity.class);
                break;
            case R.id.LoginUI_checkBox:
                if (isHidepsd) {
                    isHidepsd = false;
                    checkBox.setImageResource(R.drawable.checkbox_pressed);
                    // 设置EditText文本为可见的
                    passwordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    isHidepsd = true;
                    checkBox.setImageResource(R.drawable.checkbox_normal);
                    passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                // 切换后将EditText光标置于末尾
                CharSequence charSequence = passwordEdit.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
                break;
        }
    }

    private void LoginHttp(String telphone, String password) {

        Md5Helper Md5Helper = new Md5Helper();
        String passwordmd5 = Md5Helper.encode(password);
//        String passwordmd5 = "";
        RequestParams params = new RequestParams();
        params.add("telphone", telphone);
        params.add("password", passwordmd5);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
//        Log.e("LoginHttp===", tm.getDeviceId() + "===" + tm.getSubscriberId());
//
//        String id;
//        if (tm.getDeviceId() != null && !"".equals(tm.getDeviceId())) {
////            id = tm.getDeviceId();
//
//            id = tm.getSubscriberId();
//
//            id = "";
//
//        } else {
//            id = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
//        }

        params.add("UUID", "1");
//        params.add("UUID", getDeviceId());

//        params.add("UUID", tm.getDeviceId());

        HttpHelper.post(context, Urls.loginNormal, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在登录");
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
                                UserMsgBean bean = JSON.parseObject(result.getData(), UserMsgBean.class);
                                // 极光标记别名
                                setAlias(bean.getUid());
                                SharedPreferencesUrls.getInstance().putString("uid", bean.getUid());
                                SharedPreferencesUrls.getInstance().putString("access_token", bean.getAccess_token());
                                SharedPreferencesUrls.getInstance().putString("nickname", bean.getNickname());
                                SharedPreferencesUrls.getInstance().putString("realname", bean.getRealname());
                                SharedPreferencesUrls.getInstance().putString("sex", bean.getSex());
                                SharedPreferencesUrls.getInstance().putString("headimg", bean.getHeadimg());
                                SharedPreferencesUrls.getInstance().putString("points", bean.getPoints());
                                SharedPreferencesUrls.getInstance().putString("money", bean.getMoney());
                                SharedPreferencesUrls.getInstance().putString("bikenum", bean.getBikenum());
                                SharedPreferencesUrls.getInstance().putString("specialdays", bean.getSpecialdays());
                                SharedPreferencesUrls.getInstance().putString("iscert", bean.getIscert());
                                Toast.makeText(context, "恭喜您,登录成功", Toast.LENGTH_SHORT).show();
                                scrollToFinishActivity();
                            } else {
                                Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                        }
                    }
                });
            }
        });
    }


    public String getDeviceId() {
        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
        deviceId.append("a");
        try {
            //wifi mac地址
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            String wifiMac = info.getMacAddress();
            if (!isEmpty(wifiMac)) {
                deviceId.append("wifi");
                deviceId.append(wifiMac);
                Log.e("getDeviceId : ", deviceId.toString());
                return deviceId.toString();
            }
            //IMEI（imei）
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            String imei = tm.getDeviceId();
            if(!isEmpty(imei)){
                deviceId.append("imei");
                deviceId.append(imei);
                Log.e("getDeviceId : ", deviceId.toString());
                return deviceId.toString();
            }
            //序列号（sn）
            String sn = tm.getSimSerialNumber();
            if(!isEmpty(sn)){
                deviceId.append("sn");
                deviceId.append(sn);
                Log.e("getDeviceId : ", deviceId.toString());
                return deviceId.toString();
            }
            //如果上面都没有， 则生成一个id：随机码
            String uuid = UUID.randomUUID().toString();
            if(!isEmpty(uuid)){
                deviceId.append("id");
                deviceId.append(uuid);
                Log.e("getDeviceId : ", deviceId.toString());
                return deviceId.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("getDeviceId : ", deviceId.toString());
        return deviceId.toString();
    }

//    public static String getUUID(Context context){
//        SharedPreferences mShare = getSysShare(context, "sysCacheMap");
//        if(mShare != null){
//            uuid = mShare.getString("uuid", "");
//        }
//        if(isEmpty(uuid)){
//            uuid = UUID.randomUUID().toString();
//            saveSysMap(context, "sysCacheMap", "uuid", uuid);
//        }
//        Log.e(tag, "getUUID : " + uuid);
//        return uuid;
//    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 极光推送===================================================================
    private void setAlias(String uid) {
        // 调用JPush API设置Alias
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, uid));
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
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
