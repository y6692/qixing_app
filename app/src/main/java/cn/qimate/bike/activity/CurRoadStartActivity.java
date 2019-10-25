package cn.qimate.bike.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.model.Polygon;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.config.LockType;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;

import org.apache.http.Header;

import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.ble.utils.ParseLeAdvData;
import cn.qimate.bike.core.common.AppManager;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CircleProgressView;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.CurRoadBikingBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.PublicWay;
import cn.qimate.bike.util.ToastUtil;

/**
 * Created by Administrator on 2017/2/11 0011.
 */
@SuppressLint("NewApi")
public class CurRoadStartActivity extends SwipeBackActivity implements View.OnClickListener{

    private Context context;
    private LoadingDialog loadingDialog;
    private LoadingDialog lockLoading;
    private ImageView backImg;
    private TextView title;

    private RelativeLayout circleProgressbarLayout;
    private CircleProgressView mCircleBar;
    private TextView bikeCode;
    private TextView unlockCode;

    public static boolean isEnd = false;
    private int num = 85;
    private Button linkServiceBtn,unlockHelpBtn;
    private LinearLayout unlockCodeLayout;
    private TextView hintText;
    private TextView hintText1;
    private TextView hintText2;
    public static CurRoadStartActivity instance;
//    protected InternalReceiver internalReceiver = null;

    private String oid = "";
    private String osn = "";
    private String type = "";
//    private double referLatitude = 0.0;
//    private double referLongitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_cur_road_start);
        context = this;
        instance = this;

//        registerReceiver(broadcastReceiver, Config.initFilter());
//        registerReceiver(Config.initFilter());
//        GlobalParameterUtils.getInstance().setLockType(LockType.MTS);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                scrollToFinishActivity();
            }
            //蓝牙锁
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

            BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
            if (mBluetoothAdapter == null) {
                ToastUtil.showMessageApp(context, "获取蓝牙失败");
                scrollToFinishActivity();
                return;
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 188);
            }
        }
        initView();

//        registerReceiver(Config.initFilter());
//        GlobalParameterUtils.getInstance().setLockType(LockType.MTS);

//        ToastUtil.showMessageApp(this, BaseApplication.getInstance().getIBLE().getConnectStatus()+"==="+BaseApplication.getInstance().getIBLE().getLockStatus());

    }

    @Override
    protected void onResume() {
        isEnd = false;
        super.onResume();

        ToastUtil.showMessage(this, "start===="+m_nowMac);
        Log.e("start===", "start====onResume==="+m_nowMac);

//        try {
//            if (internalReceiver != null) {
//                unregisterReceiver(internalReceiver);
//                internalReceiver = null;
//            }
//        } catch (Exception e) {
//            ToastUtil.showMessage(this, "eee===="+e);
//        }
//
//        registerReceiver(Config.initFilter());
//        GlobalParameterUtils.getInstance().setLockType(LockType.MTS);

    }

    @Override
    protected void onPause() {
        if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
        super.onPause();

        ToastUtil.showMessage(this, "start====onPause");
        Log.e("start===", "start====onPause");
    }

    @Override
    protected void onDestroy() {
        isEnd = true;
        if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }

        super.onDestroy();

        Log.e("start===", "start====onDestroy");

//        try {
//            if (internalReceiver != null) {
//                unregisterReceiver(internalReceiver);
//                internalReceiver = null;
//            }
//        } catch (Exception e) {
//            ToastUtil.showMessage(this, "eee===="+e);
//        }
    }

    private void initView(){

        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        lockLoading = new LoadingDialog(this);
        lockLoading.setCancelable(false);
        lockLoading.setCanceledOnTouchOutside(false);

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("租车成功");
        hintText = (TextView) findViewById(R.id.curRoadUI_start_hintText);
        hintText1 = (TextView)findViewById(R.id.curRoadUI_start_hintText1);
        hintText2 = (TextView)findViewById(R.id.curRoadUI_start_hintText2);

        circleProgressbarLayout = (RelativeLayout)findViewById(R.id.curRoadUI_start_circleProgressbarLayout);
        mCircleBar = (CircleProgressView)findViewById(R.id.curRoadUI_start_circleProgressbar);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) circleProgressbarLayout.getLayoutParams();
        params.height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.5);
        circleProgressbarLayout.setLayoutParams(params);

        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) mCircleBar.getLayoutParams();
        params1.width = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.4);
        params1.height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.4);
        mCircleBar.setLayoutParams(params1);

        mCircleBar.setProgress(num);
        mCircleBar.setMaxProgress(num);
        bikeCode = (TextView)findViewById(R.id.curRoadUI_start_bikeCode);
        unlockCodeLayout = (LinearLayout)findViewById(R.id.curRoadUI_start_unlockCodeLayout);
        unlockCode = (TextView)findViewById(R.id.curRoadUI_start_unlockCode);

        linkServiceBtn = (Button)findViewById(R.id.curRoadUI_start_linkServiceBtn);
        unlockHelpBtn = (Button)findViewById(R.id.curRoadUI_start_unlockHelpBtn);

        handler.sendEmptyMessageDelayed(1, 1000);

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if ((uid == null || "".equals(uid)) || (access_token == null || "".equals(access_token))){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context,LoginActivity.class);
        }else {
            getCurrentorderStart(uid, access_token);
        }
        backImg.setOnClickListener(this);
        linkServiceBtn.setOnClickListener(this);
        unlockHelpBtn.setOnClickListener(this);
    }

//    protected void handleReceiver(Context context, Intent intent) {
//        String action = intent.getAction();
//        String data = intent.getStringExtra("data");
//        switch (action) {
//            case Config.TOKEN_ACTION:
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        BaseApplication.getInstance().getIBLE().getBattery();
//                    }
//                }, 500);
////					if (null != lockLoading && lockLoading.isShowing()) {
////						lockLoading.dismiss();
////					}
////					isStop = true;
//                ToastUtil.showMessageApp(context,"设备连接成功");
//
//                break;
//            case Config.BATTERY_ACTION:
//                break;
//            case Config.OPEN_ACTION:
//                break;
//            case Config.CLOSE_ACTION:
//                break;
//            case Config.LOCK_STATUS_ACTION:
//                //					if (loadingDialog != null && loadingDialog.isShowing()){
////						loadingDialog.dismiss();
////					}
////					if (lockLoading != null && lockLoading.isShowing()){
////						lockLoading.dismiss();
////					}
//                if (TextUtils.isEmpty(data)) {
//
//                    ToastUtil.showMessageApp(context,"锁已关闭");
//
//                    //锁已关闭
//                    submit(uid, access_token);
//
//                } else {
//                    //锁已开启
//                    ToastUtil.showMessageApp(context,"您还未上锁，请给车上锁后还车");
//                }
//                break;
//            case Config.LOCK_RESULT:
////                    if (loadingDialog != null && loadingDialog.isShowing()){
////                        loadingDialog.dismiss();
////                    }
////                    if (lockLoading != null && lockLoading.isShowing()){
////                        lockLoading.dismiss();
////                    }
//                ToastUtil.showMessageApp(context,"恭喜您，您已成功上锁");
//
//                if(MainActivity.screen){
//                    endBtn();
//                }
//
//
//
//                break;
//        }
//    }

//    protected void submit(String uid, String access_token){
//
//        RequestParams params = new RequestParams();
//        params.put("uid", uid);
//        params.put("access_token", access_token);
//        params.put("oid", oid);
//        params.put("latitude", referLatitude);
//        params.put("longitude", referLongitude);
//        if (macList.size() > 0){
//            params.put("xinbiao",macList.get(0));
//        }
//        HttpHelper.post(this, Urls.backBikescan, params, new TextHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                    loadingDialog.setTitle("正在提交");
//                    loadingDialog.show();
//                }
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
//                UIHelper.ToastError(context, throwable.toString());
//            }
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                Log.e("Test","结束用车:"+responseString);
//                try {
//                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                    if (result.getFlag().equals("Success")) {
//
//                        SharedPreferencesUrls.getInstance().putString("type","");
//                        SharedPreferencesUrls.getInstance().putString("m_nowMac","");
//                        SharedPreferencesUrls.getInstance().putBoolean("isStop",true);
//                        SharedPreferencesUrls.getInstance().putString("biking_latitude","");
//                        SharedPreferencesUrls.getInstance().putString("biking_longitude","");
//
//                        if ("1".equals(result.getData())){
//                            ToastUtil.showMessageApp(context, result.getMsg());
//                            if ("已为您免单,欢迎反馈问题".equals(result.getMsg())){
//                                MainActivity.tz = 1;
//
//                                ToastUtil.showMessage(context,"context==="+context);
//
//
//                                isEnd = true;
//                                UIHelper.goToAct(context, FeedbackActivity.class);
//                                scrollToFinishActivity();
//                            }else {
//                                MainActivity.tz = 2;
//                                Intent intent = new Intent(context, HistoryRoadDetailActivity.class);
//                                intent.putExtra("oid", oid);
//                                startActivity(intent);
//                            }
//                        }else {
//                            MainActivity.tz = 3;
//                            ToastUtil.showMessageApp(context,"恭喜您,还车成功,请支付!");
//                            UIHelper.goToAct(context,CurRoadBikedActivity.class);
//                        }
//
//                    }else {
//                        ToastUtil.showMessageApp(context, result.getMsg());
//                    }
//                }catch (Exception e){
//
//                }
//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
//            }
//        });
//    }

//    public void endBtn(){
//        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
//        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
//        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
//            ToastUtil.showMessageApp(context,"请先登录账号");
//            UIHelper.goToAct(context,LoginActivity.class);
//        }else {
//            ToastUtil.showMessage(context,uid+"==="+access_token);
//            ToastUtil.showMessage(context,macList+"==="+isContainsList);
//            ToastUtil.showMessage(context,macList.size()+"==="+isContainsList.contains(true));
//
//            Log.e("start===", "start====" + isContainsList.size()+"==="+isContainsList.contains(true));
//
//            if (isContainsList.contains(true)){
//                if ("1".equals(type)){
//                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
//                    customBuilder.setTitle("温馨提示").setMessage("还车必须到校内关锁并拨乱数字密码，距车一米内在APP点击结束!")
//                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                }
//                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                            submit(uid,access_token);
//                        }
//                    });
//                    customBuilder.create().show();
//                }else {
//                    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                        ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                        finish();
//                    }
//                    //蓝牙锁
//                    if (!BaseApplication.getInstance().getIBLE().isEnable()){
//                        BaseApplication.getInstance().getIBLE().enableBluetooth();
//                        return;
//                    }
//                    if (BaseApplication.getInstance().getIBLE().getConnectStatus()){
//
//                        if (loadingDialog != null && !loadingDialog.isShowing()){
//                            loadingDialog.setTitle("请稍等");
//                            loadingDialog.show();
//                        }
//
//                        BaseApplication.getInstance().getIBLE().getLockStatus();
//                    }else {
//
//                        if (lockLoading != null && !lockLoading.isShowing()){
//                            lockLoading.setTitle("正在连接");
//                            lockLoading.show();
//                        }
//
//                        m_myHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                ToastUtil.showMessage(context, BaseApplication.getInstance().getIBLE().getConnectStatus()+"==="+BaseApplication.getInstance().getIBLE().getLockStatus());
//
//                                if (lockLoading != null && lockLoading.isShowing()){
//                                    lockLoading.dismiss();
//                                }
//
//                                if (!BaseApplication.getInstance().getIBLE().getConnectStatus()){
//                                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//                                    customBuilder.setTitle("连接失败").setMessage("关锁后，请离车1米内重试或在右上角提交")
//                                            .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.cancel();
//                                                }
//                                            });
//                                    customBuilder.create().show();
//                                }
//
//                            }
//                        }, 10 * 1000);
//
//                        connect();
//
//                    }
//                }
//            }else {
//                if (macList.size() > 0 && !"1".equals(type)){
//                    if (!TextUtils.isEmpty(m_nowMac)) {
//                        //蓝牙锁
//                        if (!BaseApplication.getInstance().getIBLE().isEnable()){
//                            BaseApplication.getInstance().getIBLE().enableBluetooth();
//                            return;
//                        }
//                        if (BaseApplication.getInstance().getIBLE().getConnectStatus()){
//                            BaseApplication.getInstance().getIBLE().getLockStatus();
//                        }else {
//                            if (lockLoading != null && !lockLoading.isShowing()){
//                                lockLoading.setTitle("正在连接");
//                                lockLoading.show();
//                            }
//                            connect();
//                        }
//                    }
//                }else {
//                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//                    customBuilder.setTitle("温馨提示").setMessage("还车须至校内地图红色区域，或打开手机GPS并重启软件再试")
//                            .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                }
//                            });
//                    customBuilder.create().show();
//
//                }
//            }
//        }
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.curRoadUI_start_linkServiceBtn:
                if (Build.VERSION.SDK_INT >= 23) {
                    int checkPermission = this.checkSelfPermission(Manifest.permission.CALL_PHONE);
                    if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                            requestPermissions(new String[] { Manifest.permission.CALL_PHONE }, 0);
                        } else {
                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                            customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开拨打电话权限！")
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    CurRoadStartActivity.this.requestPermissions(
                                            new String[] { Manifest.permission.CALL_PHONE }, 0);
                                }
                            });
                            customBuilder.create().show();
                        }
                        return;
                    }
                }
                CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//                customBuilder.setTitle("温馨提示").setMessage("确认拨打" + "0519-86999222" + "吗?")
                customBuilder.setTitle("点击号码一键拨打").setType(7)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
//                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                        Intent intent=new Intent();
//                        intent.setAction(Intent.ACTION_CALL);
//                        intent.setData(Uri.parse("tel:" + "0519-86999222"));
//                        startActivity(intent);
//                    }
//                })
                ;
                customBuilder.create().show();
                break;
            case R.id.curRoadUI_start_unlockHelpBtn:
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }


                if ("如何开锁".equals(unlockHelpBtn.getText().toString().trim())){
                    UIHelper.goWebViewAct(context,"开锁帮助",Urls.useHelp);
                }else {
                    isEnd = true;

                    UIHelper.goToAct(context, CurRoadBikingActivity.class);
                    scrollToFinishActivity();
                }
                break;
            default:
                break;
        }
    }


    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if (num != 0) {
                    mCircleBar.setProgress((--num));
                } else {
                    isEnd = true;

                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }

                    if (!CurRoadBikingActivity.isForeground){

                        UIHelper.goToAct(context,CurRoadBikingActivity.class);
                        scrollToFinishActivity();
                    }
                }
                if (!isEnd) {
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        };
    };

    private void getCurrentorderStart(String uid, String access_token){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        HttpHelper.post(this, Urls.getCurrentorder, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在加载");
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
                        CurRoadBikingBean bean = JSON.parseObject(result.getData(),CurRoadBikingBean.class);

                        oid = bean.getOid();
                        osn = bean.getOsn();
                        type = bean.getType();

                        SharedPreferencesUrls.getInstance().putString("oid", oid);
                        SharedPreferencesUrls.getInstance().putString("osn", osn);
                        SharedPreferencesUrls.getInstance().putString("type", type);

                        bikeCode.setText(bean.getCodenum());
                        if ("1".equals(bean.getType())){
                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                            customBuilder.setTitle("温馨提示").setMessage("还车必须到校内关锁并拨乱数字密码，距车一米内在APP点击结束！")
                                    .setNegativeButton("我知道啦", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            customBuilder.create().show();
                            unlockCodeLayout.setVisibility(View.VISIBLE);
                            unlockCode.setText(bean.getPassword());
                            unlockHelpBtn.setText("如何开锁");
                            hintText.setVisibility(View.GONE);
                            hintText1.setText("该车禁止出校，仅限校内骑行！");
                            hintText2.setVisibility(View.GONE);
                        }else {
                            m_nowMac = bean.getMacinfo();

                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                            customBuilder.setTitle("温馨提示").setMessage("还车必须到校内关锁，距车一米内在APP点击结束")
                                    .setNegativeButton("我知道啦", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            customBuilder.create().show();
                            unlockCodeLayout.setVisibility(View.GONE);
                            unlockCode.setText("****");
                            unlockHelpBtn.setText("结束用车");
                            hintText.setVisibility(View.VISIBLE);
                            hintText1.setText("还车要到校内，并在APP点击结束");
                            hintText2.setVisibility(View.VISIBLE);
                        }
                    }else {
                        ToastUtil.showMessageApp(context,result.getMsg());
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if (permissions[0].equals(Manifest.permission.CALL_PHONE)){
                        Intent intent=new Intent();
                        intent.setAction(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + "0519-86999222"));
                        startActivity(intent);
                    }
                }else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开电话权限！")
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

    private String parseAdvData(int rssi, byte[] scanRecord) {
        byte[] bytes = ParseLeAdvData.adv_report_parse(ParseLeAdvData.BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA, scanRecord);
        if (bytes[0] == 0x01 && bytes[1] == 0x02) {
            return bytes2hex03(bytes);
        }
        return "";
    }
    /**
     * 方式三
     *
     * @param bytes
     * @return
     */
    public static String bytes2hex03(byte[] bytes)
    {
        final String HEX = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
        {
            // 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt((b >> 4) & 0x0f));
            // 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt(b & 0x0f));
        }
        return sb.toString();
    }

//    protected void registerReceiver(IntentFilter intentfilter) {
//        if (internalReceiver == null) {
//            internalReceiver = new InternalReceiver();
//        }
//        registerReceiver(internalReceiver, intentfilter);
//    }
//
//    private class InternalReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            handleReceiver(context, intent);
//
//        }
//    };
}
