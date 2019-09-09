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
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.StringUtil;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by LDY on 2017/2/13.
 */

public class ChangePhoneNumActivity extends SwipeBackActivity implements View.OnClickListener {

    private Context context;
    private LoadingDialog loadingDialog;
    private ImageView backImg;
    private TextView title;

    private EditText phoneNumEdit;
    private EditText noteCodeEdit;
    private Button codeBtn;
    private Button submitBtn;
    private TelephonyManager tm;

    private boolean isCode;
    private int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_change_phone_num);
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
        title.setText("修改手机号");

        phoneNumEdit = (EditText) findViewById(R.id.change_phoneNumUI_phoneNum);
        noteCodeEdit = (EditText) findViewById(R.id.change_phoneNumUI_code);

        codeBtn = (Button) findViewById(R.id.change_phoneNumUI_noteCode);
        submitBtn = (Button) findViewById(R.id.change_phoneNumUI_submitBtn);

        backImg.setOnClickListener(this);
        codeBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        String telphone = phoneNumEdit.getText().toString();
        switch (v.getId()) {
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.change_phoneNumUI_noteCode:
                if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)) {
                    Toast.makeText(context, "请先登录账号", Toast.LENGTH_SHORT).show();
                    return;
                }
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
            case R.id.change_phoneNumUI_submitBtn:
                String code = noteCodeEdit.getText().toString();
                if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)) {
                    Toast.makeText(context, "请先登录账号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (telphone == null || "".equals(telphone)) {
                    Toast.makeText(context, "请输入您的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!StringUtil.isPhoner(telphone)) {
                    Toast.makeText(context, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (code == null || "".equals(code)) {
                    Toast.makeText(context, "请输入手机验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                submit(uid, access_token, telphone, code);
                break;
            default:
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
                        num = 60;
                        isCode = true;
                        codeBtn.setText(num + "秒");
                        codeBtn.setEnabled(false);
                        // 开始60秒倒计时
                        handler.sendEmptyMessageDelayed(1, 1000);
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

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
            }
        });
    }

    private void submit(String uid,String access_token,String telphone,String telcode){

        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("telphone", telphone);
        params.put("telcode", telcode);
        HttpHelper.post(context, Urls.changetel, params, new TextHttpResponseHandler() {
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
                        Toast.makeText(context,"恭喜您,变更成功",Toast.LENGTH_SHORT).show();
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
}
