package cn.qimate.bike.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
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

/**
 * Created by Administrator1 on 2017/2/16.
 */

public class RegisterActivity extends SwipeBackActivity implements View.OnClickListener {

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
    private EditText againPsdEdit;
    private EditText codeEdit;
    private Button codeBtn;
    private Button registerBtn;
    private TextView registerDeal;

    private boolean isCode;
    private int num;
    private TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_register);
        context = this;
        initView();
    }

    private void initView() {

        tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("注册");
        rightBtn = (TextView) findViewById(R.id.mainUI_title_rightBtn);
        rightBtn.setText("登录");

        headLayout = (LinearLayout) findViewById(R.id.registerUI_headLayout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) headLayout.getLayoutParams();
        params.height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.4);
        headLayout.setLayoutParams(params);

        userNameEdit = (EditText) findViewById(R.id.registerUI_userName);
        passwordEdit = (EditText) findViewById(R.id.registernUI_password);
        againPsdEdit = (EditText) findViewById(R.id.registerUI_againpassword);
        codeEdit = (EditText) findViewById(R.id.registerUI_phoneNum_code);

        codeBtn = (Button) findViewById(R.id.registerUI_noteCode);
        registerBtn = (Button) findViewById(R.id.registerUI_submitBtn);
        registerDeal = (TextView) findViewById(R.id.registerUI_registerDeal);

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
        codeBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        registerDeal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        String telphone = userNameEdit.getText().toString();
        switch (v.getId()) {
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.mainUI_title_rightBtn:
                UIHelper.goToAct(context, LoginActivity.class);
                break;
            case R.id.registerUI_noteCode:
                if (telphone == null || "".equals(telphone)) {
                    Toast.makeText(context, "请输入您的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!StringUtil.isPhoner(telphone)) {
                    Toast.makeText(context, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendCode(telphone);
                break;
            case R.id.registerUI_submitBtn:
                String pass = passwordEdit.getText().toString();
                String againPsd = againPsdEdit.getText().toString();
                String code = codeEdit.getText().toString();
                if (telphone == null || "".equals(telphone)) {
                    Toast.makeText(context, "请输入您的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!StringUtil.isPhoner(telphone)) {
                    Toast.makeText(context, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass == null || "".equals(pass)) {
                    Toast.makeText(context, "请输入您的密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (6 > pass.length()) {
                    Toast.makeText(context, "请输入至少6位数密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (againPsd == null || "".equals(againPsd)) {
                    Toast.makeText(context, "请再次输入您的密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pass.equals(againPsd)) {
                    Toast.makeText(context, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (code == null || "".equals(code)) {
                    Toast.makeText(context, "请输入您的验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                RegisterHttp(telphone, code, pass);
                break;
            case R.id.registerUI_registerDeal:
                UIHelper.goWebViewAct(context, "使用协议", Urls.useragreement);
                break;
        }
    }

    /**
     * 发送验证码
     *
     * */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendCode(String telphone) {

        RequestParams params = new RequestParams();
        params.add("telphone", telphone);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String UUID = tm.getDeviceId();

        if("".equals(UUID)){
            UUID = tm.getImei();
        }

        if("".equals(UUID)){
            UUID = tm.getMeid();
        }

        params.add("UUID", UUID);
        params.add("type", "1");
        HttpHelper.post(context, Urls.sendcode, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("请稍等");
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
                                num = 60;
                                isCode = true;
                                codeBtn.setText(num + "秒");
                                codeBtn.setEnabled(false);
                                // 开始60秒倒计时
                                handler.sendEmptyMessageDelayed(1, 1000);
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

    // 注册处理
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void RegisterHttp(String telphone, String telcode, String password) {

        Md5Helper Md5Helper = new Md5Helper();
        String passwordmd5 = Md5Helper.encode(password);
        RequestParams params = new RequestParams();
        params.put("telphone", telphone);
        params.put("telcode", telcode);
        params.put("password", passwordmd5);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String UUID = tm.getDeviceId();

        if("".equals(UUID)){
            UUID = tm.getImei();
        }

        if("".equals(UUID)){
            UUID = tm.getMeid();
        }

        params.add("UUID", UUID);

        HttpHelper.post(context, Urls.register, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("请稍等");
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
                                SharedPreferencesUrls.getInstance().putString("access_token",
                                        bean.getAccess_token());
                                SharedPreferencesUrls.getInstance().putString("nickname", bean.getNickname());
                                SharedPreferencesUrls.getInstance().putString("realname", bean.getRealname());
                                SharedPreferencesUrls.getInstance().putString("sex", bean.getSex());
                                SharedPreferencesUrls.getInstance().putString("headimg", bean.getHeadimg());
                                SharedPreferencesUrls.getInstance().putString("points", bean.getPoints());
                                SharedPreferencesUrls.getInstance().putString("money", bean.getMoney());
                                SharedPreferencesUrls.getInstance().putString("bikenum", bean.getBikenum());
                                SharedPreferencesUrls.getInstance().putString("specialdays", bean.getSpecialdays());
                                SharedPreferencesUrls.getInstance().putString("iscert", bean.getIscert());
                                Toast.makeText(context,"恭喜您,注册成功",Toast.LENGTH_SHORT).show();
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
        });
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if (num != 1) {
                    codeBtn.setText((--num) + "秒");
                } else {
                    codeBtn.setText("重新获取");
                    codeBtn.setEnabled(true);
                    isCode = false;
                }
                if (isCode) {
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        };
    };

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

