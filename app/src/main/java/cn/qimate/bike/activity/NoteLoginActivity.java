package cn.qimate.bike.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.xylitolz.androidverificationcode.view.VerificationCodeView;

import org.apache.http.Header;

import java.util.Set;
import java.util.UUID;

import cn.jpush.android.api.JPushInterface;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.StringUtil;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.lock.utils.ToastUtils;
import cn.qimate.bike.model.CurRoadBikingBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UserMsgBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

import static android.text.TextUtils.isEmpty;

/**
 * Created by Administrator1 on 2017/2/16.
 */

public class NoteLoginActivity extends SwipeBackActivity implements View.OnClickListener {

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;

    private Context context;
    private LoadingDialog loadingDialog;
    private ImageView backImg;
    private TextView title;

    private LinearLayout headLayout;
    private EditText userNameEdit;
    private EditText codeEdit;
    private Button codeBtn;
    private Button loginBtn;
    private TextView findPsd;

    private boolean isCode;
    private int num;
    private TelephonyManager tm;

//    private LinearLayout content;
    private VerificationCodeView icv;

    private TextView tv_telphone;
    private TextView time;
    private TextView no_note;

    private String telphone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_login);
        context = this;

        telphone = getIntent().getStringExtra("telphone");

        initView();


//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        width = metrics.widthPixels;
//        height = metrics.heightPixels;
//
//        Log.e("NoteLogin===", metrics.density+"==="+width+"==="+height);

    }

    private void initView() {

        tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);

//        userNameEdit = (EditText) findViewById(R.id.noteLoginUI_userName);
//        codeEdit = (EditText) findViewById(R.id.noteLoginUI_phoneNum_code);

        tv_telphone = (TextView) findViewById(R.id.tv_telphone);
        time = (TextView) findViewById(R.id.noteLoginUI_time);
        no_note = (TextView) findViewById(R.id.noteLoginUI_no_note);

        tv_telphone.setText(telphone);


//        content = (LinearLayout) findViewById(R.id.note_content);
        icv = (VerificationCodeView) findViewById(R.id.icv);


        backImg.setOnClickListener(this);
        time.setOnClickListener(this);
        no_note.setOnClickListener(this);

        icv.setInputCompleteListener(new VerificationCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                Log.e("icv_input===", icv.getContent()+"===");

                if(icv.getContent().length()==6){
                    loginHttp(icv.getContent());
                }

            }

            @Override
            public void deleteContent() {
                Log.e("icv_delete===", icv.getContent());
            }
        });


//        sendCode();

        handler.sendEmptyMessage(2);

        // 开始60秒倒计时
        handler.sendEmptyMessageDelayed(1, 1000);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
//        String telphone = userNameEdit.getText().toString();
        switch (v.getId()) {
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.noteLoginUI_time:
//                if (telphone == null || "".equals(telphone)) {
//                    Toast.makeText(context, "请输入您的手机号码", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (!StringUtil.isPhoner(telphone)) {
//                    Toast.makeText(context, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                sendCode();

                break;
            case R.id.noteLoginUI_no_note:
//                if (telphone == null || "".equals(telphone)) {
//                    Toast.makeText(context, "请输入您的手机号码", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (!StringUtil.isPhoner(telphone)) {
//                    Toast.makeText(context, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                sendCode(telphone);

                break;
        }
    }

    private String getMyUUID() {

        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, tmPhone, androidId;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        tmDevice = "" + tm.getDeviceId();

        tmSerial = "" + tm.getSimSerialNumber();

        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

        String uniqueId = deviceUuid.toString();

        Log.d("debug", "uuid=" + uniqueId);

        return uniqueId;

    }

    private void sendCode() {

//        "Accept": "application/vnd.ws.v1+json",
//        "Phone_Brand": "IPHONE",
//        "Phone_Model": "iPhone X",
//        "Phone_System": "iOS",
//        "Phone_System_Version": "13.1.2",
//        "App_Version": "1.0.0",
//        "Device_UUID": "B45A95F3-E1DB-44CA-989D-971618140D5E"

        Log.e("verificationcode===0", "==="+telphone);

        try{
            RequestParams params = new RequestParams();
            params.add("phone", telphone);
            params.add("scene", "1");

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


                        handler.sendEmptyMessage(2);

                        // 开始60秒倒计时
                        handler.sendEmptyMessageDelayed(1, 1000);

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

    private void loginHttp(String telcode) {
        Log.e("loginHttp===", telphone+"==="+telcode);

        RequestParams params = new RequestParams();
        params.put("phone", telphone);
        params.put("verification_code", telcode);

        HttpHelper.post(context, Urls.authorizations, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon("nla===authorizations", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e("authorizations===", "==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            Log.e("authorizations===1", result.getData()+"==="+result.getStatus_code());

                            UserMsgBean bean = JSON.parseObject(result.getData(), UserMsgBean.class);

                            Log.e("authorizations===2", bean+"==="+bean.getToken());

                            if (null != bean.getToken()) {
                                SharedPreferencesUrls.getInstance().putString("access_token", "Bearer "+bean.getToken());
                                Toast.makeText(context,"恭喜您,登录成功",Toast.LENGTH_SHORT).show();

//                                UIHelper.goToAct(context, MainActivity.class);

                                Intent intent = new Intent(context, MainActivity.class);
                                intent.putExtra("flag", true);
                                startActivity(intent);

                                scrollToFinishActivity();

                            }else{
                                Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
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

    /**
     * 发送验证码
     *
     * */
    //@RequiresApi(api = Build.VERSION_CODES.O)
    private void sendCode2(String telphone) {

        try{
            RequestParams params = new RequestParams();
            params.add("telphone", telphone);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            String uuid = UUID.randomUUID().toString();

            params.add("UUID", uuid);
            params.add("type", "2");

            HttpHelper.post(context, Urls.sendcode, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在加载");
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    onFailureCommon("nla===sendcode", throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {

//                        Toast.makeText(context, "=="+responseString, Toast.LENGTH_LONG).show();

                                Log.e("verificationcode===", "==="+responseString);

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
                            }
                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                        }
                    });

                }


            });
        }catch (Exception e){
            Toast.makeText(context, "==="+e, Toast.LENGTH_SHORT).show();
        }

    }

    private void loginHttp2(String telphone, String telcode) {

        RequestParams params = new RequestParams();
        params.put("telphone", telphone);
        params.put("telcode", telcode);

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
//        String id;
//        if (tm.getDeviceId() != null && !"".equals(tm.getDeviceId())) {
//            id = tm.getDeviceId();
//        } else {
//            id = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
//        }
//
//        params.add("UUID", id);
//
//        params.put("UUID", getDeviceId());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//        String UUID = tm.getDeviceId();
//
//        if("".equals(UUID)){
//            UUID = tm.getImei();
//        }
//
//        if("".equals(UUID)){
//            UUID = tm.getMeid();
//        }

        String uuid = UUID.randomUUID().toString();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            UUID = tm.getImei();
//        } else {
//            UUID = tm.getDeviceId();
//        }

        params.add("UUID", uuid);

        HttpHelper.post(context, Urls.loginCode, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在登录");
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
                        SharedPreferencesUrls.getInstance().putString("iscert", bean.getIscert());
                        Toast.makeText(context,"恭喜您,登录成功",Toast.LENGTH_SHORT).show();
                        scrollToFinishActivity();
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
        });
    }

    public static String getUniqueID() {
        //获得独一无二的Psuedo ID
        String serial = null;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位

        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
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

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if (num != 1) {
                    time.setText((--num) + "s 后点击重新获取");
                } else {
                    time.setText("重新获取");
                    time.setEnabled(true);
                    isCode = false;
                }
                if (isCode) {
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }else{
                num = 60;
                isCode = true;
                time.setText(num + "s 后点击重新获取");
                time.setEnabled(false);
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
