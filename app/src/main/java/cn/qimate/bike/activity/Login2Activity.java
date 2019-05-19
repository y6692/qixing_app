package cn.qimate.bike.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sofi.blelocker.library.utils.StringUtils;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.http.OkHttpClientManager;
import cn.http.ResultCallback;
import cn.http.rdata.RUserRegister;
import cn.http.rdata.RetData;
import cn.qimate.bike.R;
import cn.qimate.bike.util.Globals;
import cn.qimate.bike.util.Installation;
import cn.qimate.bike.util.UIHelper;
import okhttp3.Request;


/**
 * Created by dell on 2016/7/4.
 */
public class Login2Activity extends Activity implements View.OnClickListener{
    private final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQ_WRITE_STORAGE = 1;

//    @BindView(R.id.etName)
    EditText editUsername;
//    @BindView(R.id.etPwd)
    EditText editPassword;
//    @BindView(R.id.btnLogin)
    TextView btnLogin;
//    @BindView(R.id.btnCode)
    TextView btnCode;

    private static final int MSG_SMS = 1;
    private TimerHandler mHandler;
    private long sTime = 60;         //验证码剩余时间，单位秒
    private String username = "", code = "", deviceId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_ui_login);
        ButterKnife.bind(this);

        editUsername = (EditText) findViewById(R.id.etName);
        editPassword = (EditText) findViewById(R.id.etPwd);
        btnLogin = (TextView) findViewById(R.id.btnLogin);
        btnCode = (TextView) findViewById(R.id.btnCode);

        btnLogin .setOnClickListener(this);
        btnCode.setOnClickListener(this);

        bindData();

        bindView();
    }

    protected void bindData() {
        mHandler = new TimerHandler(this);
//        username = PreferenceUtil.getUsername();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            deviceId = Installation.id(this);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_WRITE_STORAGE);
        }
    }

    protected void bindView() {
        if (!StringUtils.isEmpty(username)) {
            editUsername.setText(username);
        }

        btnLogin.setEnabled(false);
    }

    /**
     * 验证是否合法
     */
    public boolean checkVaild() {
        username = editUsername.getText().toString().trim();
        code = editPassword.getText().toString().trim();

        if (StringUtils.isEmpty(username)) {
            UIHelper.showToast(this, "login_username_empty");
            return false;
        } else if (StringUtils.isEmpty(code) || code.length() != 6) {
            UIHelper.showToast(this, "login_code_empty");
            return false;
        } else if (StringUtils.isEmpty(deviceId)) {
            UIHelper.showToast(this, "login_devideIc_empty");
            return false;
        }

        return true;
    }

    /**
     * 点击事件
     * @param v
     */
//    @OnClick({R.id.btnLogin, R.id.btnCode})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                if (checkVaild()) {
                    UIHelper.showProgress(this, "login_progress");

                    OkHttpClientManager.getInstance().UserRegister(username, deviceId, code, new ResultCallback<RUserRegister>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            UIHelper.dismiss();
                            UIHelper.showToast(Login2Activity.this, e.getMessage());
                        }

                        @Override
                        public void onResponse(RUserRegister rUserRegister) {
                            UIHelper.dismiss();
                            if (rUserRegister.getResult() < 0) {
                                UIHelper.showToast(Login2Activity.this, rUserRegister.getResult()+"");
                            }
                            else {
//                                PreferenceUtil.setUsername(username);

                                Globals.USERNAME = username;
//                                showActivity(DeviceListActivity.class);
                                Intent intent = new Intent(Login2Activity.this, DeviceDetailActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
                break;
            case R.id.btnCode:
                username = editUsername.getText().toString().trim();
                if (StringUtils.isEmpty(username)) {
                    UIHelper.showToast(Login2Activity.this, "login_username_empty");
                } else {
                    btnCode.setEnabled(false);
                    editUsername.setEnabled(false);
                    timeTick();

                    OkHttpClientManager.getInstance().GetCaptcha(username, new ResultCallback<RetData>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            UIHelper.showToast(Login2Activity.this, e.getMessage());
                            cancelTick();
                            sendMsg(0);
                        }

                        @Override
                        public void onResponse(RetData retData) {
                            if (retData.getResult() < 0) {
                                UIHelper.showToast(Login2Activity.this, retData.getResult()+"");
                                cancelTick();
                                sendMsg(0);
                            }
                            else {
                                editPassword.requestFocus();
                                btnLogin.setEnabled(true);
                            }
                        }
                    });
                }
                break;
            default:
                break;

        }
    };

    private static class TimerHandler extends Handler {
        private static WeakReference<Login2Activity> reference;

        public TimerHandler(Login2Activity activity) {
            reference = new WeakReference<Login2Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SMS:
                    long time = (long)msg.obj;

                    if(reference.get() != null) {
                        Login2Activity activity = reference.get();

                        if (time == 0) {
                            activity.btnCode.setText("获取验证码");
                            activity.btnCode.setEnabled(true);
                            activity.editUsername.setEnabled(true);
                        } else {
                            String str = "已发送（"+time+"）";
                            activity.btnCode.setText(str);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void sendMsg(long time) {
        Message m = mHandler.obtainMessage();
        m.what = MSG_SMS;
        m.obj = time;
        mHandler.sendMessage(m);
    }

    private Timer timer;
    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            sTime--;
            sendMsg(sTime);

            if(sTime == 0) {
                sTime = 60;
                cancelTick();
            }
        }
    }
    private void timeTick() {
        cancelTick();
        timer = new Timer();
        timer.schedule(new MyTimerTask(), 0, 1000);//5秒后开始，每隔0.2秒
    }

    private void cancelTick() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onStop() {
        cancelTick();
        UIHelper.dismiss();
        super.onStop();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_WRITE_STORAGE :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    deviceId = Installation.id(this);
                }
                else {
                    UIHelper.showAlertDialog(this, "storage_permission_deny");
                }
                break;
        }
    }

//    @Override
//    protected int getLayoutRes() {
//        return R.layout.ac_ui_login;
//    }
}

