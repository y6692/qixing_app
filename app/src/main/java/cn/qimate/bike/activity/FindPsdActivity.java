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
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.apache.http.Header;

import java.util.UUID;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.Md5Helper;
import cn.qimate.bike.core.common.StringUtil;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator1 on 2017/2/16.
 */

public class FindPsdActivity extends SwipeBackActivity implements View.OnClickListener {

    private Context context;
    private LoadingDialog loadingDialog;
    private ImageView backImg;
    private TextView title;

    private EditText userNameEdit;
    private EditText codeEdit;
    private EditText passwordEdit;
    private EditText againPsdEdit;
    private Button codeBtn;
    private Button submitBtn;

    private boolean isCode;
    private int num;
    private TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_find_password);
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
        title.setText("找回密码");

        userNameEdit = (EditText) findViewById(R.id.findPsdUI_userName);
        codeEdit = (EditText) findViewById(R.id.findPsdUI_phoneNum_code);
        passwordEdit = (EditText) findViewById(R.id.findPsdUI_password);
        againPsdEdit = (EditText) findViewById(R.id.findPsdUI_againPsd);

        codeBtn = (Button) findViewById(R.id.findPsdUI_noteCode);
        submitBtn = (Button) findViewById(R.id.findPsdUI_btn);

        backImg.setOnClickListener(this);
        codeBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String telphone = userNameEdit.getText().toString();
        switch (v.getId()) {
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.findPsdUI_noteCode:
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
            case R.id.findPsdUI_btn:
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
                if (code == null || "".equals(code)) {
                    Toast.makeText(context, "请输入您的验证码", Toast.LENGTH_SHORT).show();
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
                submit(telphone, code, pass);
                break;
        }
    }

    /**
     * 发送验证码
     *
     * */
    private void sendCode(String telphone) {

        RequestParams params = new RequestParams();
        params.add("telphone", telphone);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String uuid = UUID.randomUUID().toString();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            UUID = tm.getImei();
//        } else {
//            UUID = tm.getDeviceId();
//        }

        params.add("UUID", uuid);
        params.add("type", "2");
        HttpHelper.post(context, Urls.sendcode, params, new TextHttpResponseHandler() {
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
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {

                        handler.sendEmptyMessage(2);
                        // 开始60秒倒计时
                        handler.sendEmptyMessageDelayed(1, 1000);
                    } else {
                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                    }
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
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
            }
        });
    }

    /**
     * 找回密码
     */
    private void submit(String phoneNum, String code, String newPwd) {
        RequestParams params = new RequestParams();
        params.put("telphone", phoneNum);
        params.put("telephonecode", code);
        params.put("password", Md5Helper.encode(newPwd));
        HttpHelper.post(context, Urls.forgetpwd, params, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在提交");
                    loadingDialog.show();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        Toast.makeText(context, "密码重置成功,请登录", Toast.LENGTH_SHORT).show();
                        finishMine();
                    } else {
                        UIHelper.showToastMsg(context, result.getMsg(), R.drawable.ic_error);
                    }
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
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
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
            }else{
                num = 60;
                isCode = true;
                codeBtn.setText(num + "秒");
                codeBtn.setEnabled(false);
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

}
