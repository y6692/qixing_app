package cn.qimate.bike.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
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
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.StringUtil;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
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
    private LinearLayout ll_back;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_note_login);
        context = this;
        initView();
    }

    private void initView() {

        tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("快捷登录");

        headLayout = (LinearLayout) findViewById(R.id.noteLoginUI_headLayout);
        headLayout = (LinearLayout) findViewById(R.id.noteLoginUI_headLayout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) headLayout.getLayoutParams();
        params.height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.4);
        headLayout.setLayoutParams(params);

        userNameEdit = (EditText) findViewById(R.id.noteLoginUI_userName);
        codeEdit = (EditText) findViewById(R.id.noteLoginUI_phoneNum_code);

        if (SharedPreferencesUrls.getInstance().getString("userName", "") != null && !"".equals(
                SharedPreferencesUrls.getInstance().getString("userName", ""))) {
            userNameEdit.setText(SharedPreferencesUrls.getInstance().getString("userName", ""));
        }
        codeBtn = (Button) findViewById(R.id.noteLoginUI_noteCode);
        loginBtn = (Button) findViewById(R.id.noteLoginUI_btn);
        findPsd = (TextView) findViewById(R.id.noteLoginUI_findPsd);

        ll_back.setOnClickListener(this);
        codeBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        findPsd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String telphone = userNameEdit.getText().toString();
        switch (v.getId()) {
            case R.id.ll_back:
                scrollToFinishActivity();
                break;
            case R.id.noteLoginUI_noteCode:
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
            case R.id.noteLoginUI_btn:
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
                loginHttp(telphone, code);
                break;
            case R.id.noteLoginUI_findPsd:
                UIHelper.goToAct(context, FindPsdActivity.class);
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

    /**
     * 发送验证码
     *
     * */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendCode(String telphone) {

        RequestParams params = new RequestParams();
        params.add("telphone", telphone);

//        params.put("UUID", getMyUUID());
//        params.put("UUID", getDeviceId());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        params.add("UUID", tm.getDeviceId());

//        if (tm.getDeviceId() != null) {
//            params.add("UUID", tm.getDeviceId());
//        } else {
////            params.add("UUID", Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
//            params.add("UUID", tm.getImei());
//        }
//
//        params.add("UUID", tm.getImei());


//        try {
////            final TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            if(tm.getDeviceId() == null || tm.getDeviceId().equals("")) {
//                if (Build.VERSION.SDK_INT >= 23) {
//                    params.add("UUID", tm.getDeviceId(0));
//                }
//            }else{
//                params.add("UUID", tm.getDeviceId());
//            }
//        }catch (Exception e){
//
//        }

//        Log.e("UUID", tm.getDeviceId(0) + "====" + tm.getDeviceId(1) + "====" + tm.getImei(0) + "====" + tm.getImei(1));

//        params.add("UUID", tm.getImei(0));


//        params.add("UUID", getDeviceId());

//        final String tmDevice, tmSerial, tmPhone, androidId;
//        tmDevice = "" + tm.getDeviceId();
//        tmSerial = "" + tm.getSimSerialNumber();
//        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
//
//        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
//        String uniqueId = deviceUuid.toString();

//        String m_szDevIDShort = "86" + //we make this look like a valid IMEI
//
//                Build.BOARD.length()%10 +
//                Build.BRAND.length()%10 +
//                Build.CPU_ABI.length()%10 +
//                Build.DEVICE.length()%10 +
//                Build.DISPLAY.length()%10 +
//                Build.HOST.length()%10 +
//                Build.ID.length()%10 +
//                Build.MANUFACTURER.length()%10 +
//                Build.MODEL.length()%10 +
//                Build.PRODUCT.length()%10 +
//                Build.TAGS.length()%10 +
//                Build.TYPE.length()%10 +
//                Build.USER.length()%10 ;

//        params.add("UUID", tm.getImei());
//
//        Log.e("UUID", tm.getDeviceId() + "====" + m_szDevIDShort + "====" + UUID.randomUUID().toString());

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

    private void loginHttp(String telphone,String telcode) {

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
        params.add("UUID", tm.getDeviceId());

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
