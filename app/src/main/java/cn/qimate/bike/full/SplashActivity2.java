/*
 * Copyright © 2017 Hubcloud.com.cn. All rights reserved.
 * SplashActivity.java
 * AdHubSDK
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 */

package cn.qimate.bike.full;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hubcloud.adhubsdk.AdHub;
import com.hubcloud.adhubsdk.AdListener;
import com.hubcloud.adhubsdk.SplashAd;

import org.apache.http.Header;

import cn.jpush.android.api.JPushInterface;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.ActionCenterActivity;
import cn.qimate.bike.activity.CrashHandler;
import cn.qimate.bike.activity.Main4Activity;
import cn.qimate.bike.activity.MainActivity;
import cn.qimate.bike.base.BaseActivity;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.Md5Helper;
import cn.qimate.bike.core.common.NetworkUtils;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.util.ToastUtil;

/**
 * 实时开屏，广告实时请求并且立即展现
 */
public class SplashActivity2 extends BaseActivity implements View.OnClickListener{

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();

    SplashAd splashAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        String app_id = (String) SharedPreferencesUtils.get(this, "app_id", "277");
//        String splashAdUnitId = (String) SharedPreferencesUtils.get(this, "splash_ad_unit_id", "974");

        CrashHandler.getInstance().init(this);

        if (SharedPreferencesUrls.getInstance().getBoolean("isStop", true)) {
            SharedPreferencesUrls.getInstance().putString("m_nowMac", "");
        }
        if ("".equals(SharedPreferencesUrls.getInstance().getString("m_nowMac", ""))) {
            SharedPreferencesUrls.getInstance().putBoolean("isStop", true);
            SharedPreferencesUrls.getInstance().putBoolean("switcher", false);
        }

        ToastUtil.showMessage(this, SharedPreferencesUrls.getInstance().getBoolean("isStop", true) + "===" + SharedPreferencesUrls.getInstance().getString("m_nowMac", ""));

        init();
    }


    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkPermission = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
//                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
//                } else {
//                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
//                }
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                return;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkPermission = this.checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
//                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
//                } else {
//                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
//                }
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
                return;
            }
        }
        // <!-- 写入扩展存储，向扩展卡写入数据，用于写入离线定位数据 -->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkPermission = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//                } else {
//                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

                return;
            }
        }

		if (null == locationOption) {
			locationOption = new AMapLocationClientOption();
		}
        initjpush();
        registerMessageReceiver();
		initLocation();

//        jump();

//        String app_id = "2597";
        String splashAdUnitId = "7502";

//        AdHub.initialize(this, app_id);
        // adUnitContainer
        FrameLayout adsParent = (FrameLayout) this.findViewById(R.id.adsFl);


        // the observer of AD
        AdListener listener = new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.i("SplashActivity", "onAdLoaded");
//                Toast.makeText(SplashActivity2.this, "onAdLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdShown() {
                Log.i("SplashActivity", "onAdShown");
//                Toast.makeText(SplashActivity2.this, "onAdShown", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.i("SplashActivity", "onAdFailedToLoad");
//                Toast.makeText(SplashActivity2.this, "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("SplashActivity", "onAdClosed");
                        jumpWhenCanClick();
                    }
                });

                 // 跳转至您的应用主界面
//                Toast.makeText(SplashActivity2.this, "onAdClosed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked() {
                Log.e("SplashActivity", "onAdClick");
                // 设置开屏可接受点击时，该回调可用

            }
        };

        splashAd = new SplashAd(this, adsParent, listener, splashAdUnitId);
        splashAd.setCloseButtonPadding(10, 20, 10, 10);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    // 初始化极光
    private void initjpush() {

        JPushInterface.init(getApplicationContext()); // 初始化 JPush
    }

    private void initLocation() {
        if (NetworkUtils.getNetWorkType(context) != NetworkUtils.NONETWORK) {
            //初始化client
            locationClient = new AMapLocationClient(this.getApplicationContext());
            //设置定位参数
            locationClient.setLocationOption(getDefaultOption());
            // 设置定位监听
            locationClient.setLocationListener(locationListener);
            startLocation();
        } else {
            Toast.makeText(context, "暂无网络连接，请连接网络", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(20 * 1000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(false);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(true);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(false); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc) {
//				Toast.makeText(context,"===="+loc.getLongitude(),Toast.LENGTH_SHORT).show();

                if (0.0 != loc.getLongitude() && 0.0 != loc.getLongitude()) {
                    PostDeviceInfo(loc.getLatitude(), loc.getLongitude());
                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(SplashActivity2.this);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开定位权限！")
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
                    return;
                }
            } else {
                Toast.makeText(context, "定位失败", Toast.LENGTH_SHORT).show();
                finishMine();
            }
        }
    };

    private void PostDeviceInfo(double latitude, double longitude) {
        if (NetworkUtils.getNetWorkType(context) != NetworkUtils.NONETWORK) {
            try {
                TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                String UUID = tm.getDeviceId();
                String system_version = Build.VERSION.RELEASE;
                String device_model = new Build().MODEL;
                RequestParams params = new RequestParams();
                Md5Helper Md5Helper = new Md5Helper();
                String verify = Md5Helper.encode("7mateapp" + UUID);
                params.put("verify", verify);
                params.put("system_name", "Android");
                params.put("system_version", system_version);
                params.put("device_model", device_model);
                params.put("device_user", new Build().MANUFACTURER + device_model);
                params.put("longitude", ""+longitude);
                params.put("latitude", ""+latitude);
                params.put("UUID", UUID);
                HttpHelper.post(context, Urls.DevicePostUrl, params, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                            if (result.getFlag().toString().equals("Success")) {

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }
                });
//				mThread.start();
            } catch (Exception e) {
//				showDialog();
                return;
            }
        }else{
            Toast.makeText(context,"暂无网络连接，请连接网络",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void startLocation() {
        if (null != locationClient) {
            // 设置定位参数
            locationClient.setLocationOption(locationOption);
            // 启动定位
            locationClient.startLocation();
        }

    }




    // for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }



    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (extras != null && !"".equals(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
            }
        }
    }

    /**
     * 当设置开屏可点击时，需要等待跳转页面关闭后，再切换至您的主窗口。故此时需要增加canJumpImmediately判断。 另外，点击开屏还需要在onResume中调用jumpWhenCanClick接口。
     */
    public boolean canJumpImmediately = false;

    private void jumpWhenCanClick() {
        Log.d("SplashActivity", "canJumpImmediately:" + canJumpImmediately);
        if (canJumpImmediately) {
//            this.startActivity(new Intent(SplashActivity2.this, MainActivity.class));
//            this.finish();

            jump();
        } else {
            canJumpImmediately = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);

        Log.d("SplashActivity", "onPause:" + canJumpImmediately);
        canJumpImmediately = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mMessageReceiver);

        m_myHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
     */
    private void jump() {
//        this.startActivity(new Intent(SplashActivity2.this, MainActivity.class));
//        this.finish();

        if ((!SharedPreferencesUrls.getInstance().getBoolean("isFirst", true) && getVersion() == SharedPreferencesUrls.getInstance().getInt("version", 0))) {
            UIHelper.goToAct(this, MainActivity.class);
        } else {
            SharedPreferencesUrls.getInstance().putBoolean("isFirst", false);
            SharedPreferencesUrls.getInstance().putInt("version", getVersion());
            UIHelper.goToAct(this, EnterActivity.class);
        }

        Log.e("jump===", "===");

//        UIHelper.goToAct(this, EnterActivity.class);
//        UIHelper.goToAct(this, Main4Activity.class);
        finishMine();
    }

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

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);

        Log.d("SplashActivity", "onPause:" + canJumpImmediately);
        if (canJumpImmediately) {
            jumpWhenCanClick();
        }
        canJumpImmediately = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//						int checkPermission = this.checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
//						if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//							if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
//								requestPermissions(new String[] { Manifest.permission.READ_PHONE_STATE }, 100);
//							} else {
//								SplashActivity.this.requestPermissions(
//										new String[] { Manifest.permission.READ_PHONE_STATE }, 100);
//								return;
//							}
//						}
//					}
                    init();
                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开存储空间权限！")
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
                return;
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // <!-- 写入扩展存储，向扩展卡写入数据，用于写入离线定位数据 -->
//					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//						int checkPermission = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
//						if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//							if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//								requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE,
//										Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
//							} else {
//								SplashActivity.this.requestPermissions(
//										new String[] { Manifest.permission.READ_EXTERNAL_STORAGE,
//												Manifest.permission.WRITE_EXTERNAL_STORAGE },
//										0);
//								return;
//							}
//						}
//					}
                    init();
                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里允许设备信息权限！")
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
                return;
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                }else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里定位权限！")
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
                return;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
