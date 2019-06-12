package com.zxing.lib.scaner.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.zxing.Result;
import com.sofi.blelocker.library.Code;
import com.sofi.blelocker.library.connect.listener.BleConnectStatusListener;
import com.sofi.blelocker.library.connect.options.BleConnectOptions;
import com.sofi.blelocker.library.model.BleGattProfile;
import com.sofi.blelocker.library.protocol.IConnectResponse;
import com.sofi.blelocker.library.protocol.IEmptyResponse;
import com.sofi.blelocker.library.protocol.IGetRecordResponse;
import com.sofi.blelocker.library.protocol.IGetStatusResponse;
import com.sofi.blelocker.library.search.SearchRequest;
import com.sofi.blelocker.library.search.SearchResult;
import com.sofi.blelocker.library.search.response.SearchResponse;
import com.sofi.blelocker.library.utils.BluetoothLog;
import com.sofi.blelocker.library.utils.StringUtils;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.config.LockType;
import com.sunshine.blelibrary.inter.OnConnectionListener;
import com.sunshine.blelibrary.inter.OnDeviceSearchListener;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;
import com.vondear.rxtools.RxAnimationTool;
import com.vondear.rxtools.RxBeepTool;
import com.vondear.rxtools.RxPhotoTool;
import com.vondear.rxtools.interfaces.OnRxScanerListener;
import com.vondear.rxtools.view.dialog.RxDialogSure;
import com.zxing.lib.scaner.CameraManager;
import com.zxing.lib.scaner.CaptureActivityHandler;
import com.zxing.lib.scaner.decoding.InactivityTimer;

import org.apache.http.Header;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.OnClick;
import cn.http.OkHttpClientManager;
import cn.http.ResultCallback;
import cn.http.rdata.RRent;
import cn.loopj.android.http.RequestHandle;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.ClientManager;
import cn.qimate.bike.activity.CurRoadBikedActivity;
import cn.qimate.bike.activity.CurRoadBikingActivity;
import cn.qimate.bike.activity.CurRoadStartActivity;
import cn.qimate.bike.activity.DeviceDetailActivity;
import cn.qimate.bike.activity.FeedbackActivity;
import cn.qimate.bike.activity.HistoryRoadDetailActivity;
import cn.qimate.bike.activity.LoginActivity;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.ble.BLEService;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.CurRoadBikingBean;
import cn.qimate.bike.model.EbikeInfoBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.AESUtil;
import cn.qimate.bike.util.ByteUtil;
import cn.qimate.bike.util.Globals;
import cn.qimate.bike.util.IoBuffer;
import cn.qimate.bike.util.PublicWay;
import cn.qimate.bike.util.SharePreUtil;
import cn.qimate.bike.util.SystemUtil;
import cn.qimate.bike.util.ToastUtil;
import okhttp3.Request;

import static com.sofi.blelocker.library.Constants.STATUS_CONNECTED;

/**
 * @author vondear
 */
public class ActivityScanerCode extends SwipeBackActivity implements View.OnClickListener, OnConnectionListener {

    BluetoothAdapter mBluetoothAdapter;
    /**
     * 选中的蓝牙设备
     */
    BluetoothDevice mDevice;
    /**
     * BLE蓝牙通信状态广播
     */
    BroadcastReceiver mBLEStateChangeBroadcast;
    /**
     * 蓝牙数据接收广播
     */
    BroadcastReceiver mDataValueBroadcast;

    private Activity mActivity = this;
    private Context context = this;
    private ImageView top_mask_bcg;
    /**
     * 扫描结果监听
     */
    private static OnRxScanerListener mScanerListener;

    private InactivityTimer inactivityTimer;

    /**
     * 扫描处理
     */
    private CaptureActivityHandler handler;

    /**
     * 整体根布局
     */
    private RelativeLayout mContainer = null;

    /**
     * 扫描框根布局
     */
    private RelativeLayout mCropLayout = null;

    /**
     * 扫描边界的宽度
     */
    private int mCropWidth = 0;

    /**
     * 扫描边界的高度
     */
    private int mCropHeight = 0;

    /**
     * 是否有预览
     */
    private boolean hasSurface;

    /**
     * 扫描成功后是否震动
     */
    private boolean vibrate = true;

    /**
     * 闪光灯开启状态
     */
    private boolean mFlashing = true;

    /**
     * 生成二维码 & 条形码 布局
     */
    private LinearLayout mLlScanHelp;

    /**
     * 闪光灯 按钮
     */
    private TextView mIvLight;

    /**
     * 扫描结果显示框
     */
    private RxDialogSure rxDialogSure;

    /**
     * 设置扫描信息回调
     */
    public static void setScanerListener(OnRxScanerListener scanerListener) {
        mScanerListener = scanerListener;
    }

    private LoadingDialog loadingDialog;
    volatile String m_nowMac = "";
    private String codenum = "";
    // 输入法
    private Dialog dialog;
    private EditText bikeNumEdit;
    private Button positiveButton, negativeButton;
    private int Tag = 0;

    private String quantity = "";
    private int num = 30;//扫描时间
    private boolean isStop = false;
    private boolean isOpen = false;
    private int n = 0;
    private int cn = 0;

    //    BLEService bleService = new BLEService();
    private String tel = "13188888888";
    private String bleid = "";

    private String keySource = "";
    //密钥索引
    int encryptionKey= 0;
    //开锁密钥
    String keys = null;
    //服务器时间戳，精确到秒，用于锁同步时间
    long serverTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scaner_code);
        context = this;

//        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
//
//        BLEService.bluetoothAdapter = mBluetoothAdapter;
//
//        bleService.view = this;
//        bleService.showValue = true;

        registerReceiver(broadcastReceiver, Config.initFilter());
        GlobalParameterUtils.getInstance().setLockType(LockType.MTS);
        BaseApplication.getInstance().getIBLE().refreshCache();
        BaseApplication.getInstance().getIBLE().close();
        BaseApplication.getInstance().getIBLE().disconnect();
        //界面控件初始化
        initView();
        //权限初始化
        initPermission();
        //扫描动画初始化
        initScanerAnimation();
        //初始化 CameraManager
        CameraManager.init(mActivity);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    public void btn(View view) {
        int viewId = view.getId();
        if (viewId == R.id.top_mask) {
            light();

//            bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});
//            Log.e("light===4_3", "===");
//            button3();

        } else if (viewId == R.id.top_back) {
            scrollToFinishActivity();
        } else if (viewId == R.id.top_openpicture) {
            RxPhotoTool.openLocalImage(mActivity);
        } else if (viewId == R.id.loca_show_btnBikeNum) {
//            button4();

            //关闭二维码扫描
            if (handler != null) {
                handler.quitSynchronously();
                handler = null;
            }
            CameraManager.get().closeDriver();

            WindowManager windowManager = getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.width = (int) (display.getWidth() * 0.8); // 设置宽度0.6
            lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
            dialog.show();
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            manager.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
            manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    @Override
    public void oncall() {
        super.oncall();
        button9();
    }


    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        ToastUtil.showMessage(this, "scaner====" + referLatitude);

//        bleService.connect("34:03:DE:54:E6:C6");
//
//        bleService.sleep(2000);
//
//        bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});
//
//        button9();
//
//        bleService.sleep(2000);
//
//        button3();

//        try {
//            if (internalReceiver != null) {
//                unregisterReceiver(internalReceiver);
//                internalReceiver = null;
//            }
//        } catch (Exception e) {
//            ToastUtil.showMessage(this, "eee===="+e);
//        }

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            //Camera初始化
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    if (!hasSurface) {
                        hasSurface = true;
                        initCamera(holder);
                    }
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    hasSurface = false;

                }
            });
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    @Override
    protected void onPause() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }

        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        mScanerListener = null;
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        super.onDestroy();


        Log.e("scan===onDestroy", "===");

        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
        m_myHandler.removeCallbacksAndMessages(null);
    }

    private void initView() {
        loadingDialog = new LoadingDialog(mActivity);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        mIvLight = (TextView) findViewById(R.id.top_mask);
        mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
        top_mask_bcg = (ImageView) findViewById(R.id.top_mask_bcg);
        mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);
        mLlScanHelp = (LinearLayout) findViewById(R.id.ll_scan_help);

        dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.pop_circles_menu, null);
        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);

        bikeNumEdit = (EditText) dialogView.findViewById(R.id.pop_circlesMenu_bikeNumEdit);
        positiveButton = (Button) dialogView.findViewById(R.id.pop_circlesMenu_positiveButton);
        negativeButton = (Button) dialogView.findViewById(R.id.pop_circlesMenu_negativeButton);
        positiveButton.setOnClickListener(this);
        negativeButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pop_circlesMenu_positiveButton:
                String bikeNum = bikeNumEdit.getText().toString().trim();
                if (bikeNum == null || "".equals(bikeNum)) {
                    ToastUtil.showMessageApp(this, "请输入单车编号");
                    return;
                }
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Tag = 1;
//                useBike(bikeNum);

                ebikeInfo(bikeNum);

                break;

            case R.id.pop_circlesMenu_negativeButton:
                InputMethodManager manager1 = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager1.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏
                BaseApplication.getInstance().getIBLE().refreshCache();
                BaseApplication.getInstance().getIBLE().close();
                BaseApplication.getInstance().getIBLE().disconnect();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                scrollToFinishActivity();

                break;
        }
    }

    private void initPermission() {
        //请求Camera权限 与 文件读写 权限
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void initScanerAnimation() {
        ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
        RxAnimationTool.ScaleUpDowm(mQrLineView);
    }

    public int getCropWidth() {
        return mCropWidth;
    }

    public void setCropWidth(int cropWidth) {
        mCropWidth = cropWidth;
        CameraManager.FRAME_WIDTH = mCropWidth;

    }

    public int getCropHeight() {
        return mCropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.mCropHeight = cropHeight;
        CameraManager.FRAME_HEIGHT = mCropHeight;
    }


    private void light() {
        if (mFlashing) {
            mFlashing = false;
            // 开闪光灯
            CameraManager.get().openLight();
        } else {
            mFlashing = true;
            // 关闪光灯
            CameraManager.get().offLight();
        }

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            Point point = CameraManager.get().getCameraResolution();
            AtomicInteger width = new AtomicInteger(point.y);
            AtomicInteger height = new AtomicInteger(point.x);
            int cropWidth = mCropLayout.getWidth() * width.get() / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height.get() / mContainer.getHeight();
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);
        } catch (IOException | RuntimeException ioe) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(ActivityScanerCode.this);
        }
    }

    //--------------------------------------打开本地图片识别二维码 start---------------------------------
    private void initDialogResult(Result result) {
        useBike(result.toString());
    }

    public void handleDecode(Result result) {
        inactivityTimer.onActivity();
        //扫描成功之后的振动与声音提示
        RxBeepTool.playBeep(mActivity, vibrate);
        String result1 = result.getText();
        if (mScanerListener == null) {
            initDialogResult(result);
        } else {
            mScanerListener.onSuccess("From to Camera", result);
        }
    }

    public Handler getHandler() {
        return handler;
    }

    private void useBike(final String result) {
        Log.e("scan===useBike", result.indexOf('&') + "<===>" + result);

        int code = 0;
        if (result.split("&").length == 1) {
            if (result.split("\\?")[1].split("&")[0].split("=")[1].matches(".*[a-zA-z].*")) {
                useCar(result);
                return;
            } else {
                code = Integer.parseInt(result.split("\\?")[1].split("&")[0].split("=")[1]);
            }
        } else {
            code = Integer.parseInt(result.split("\\?")[1].split("&")[0].split("=")[1]);
        }

        if (result.indexOf('&') != -1 || (code >= 80001651 && code <= 80002000)) {  //电单车

            ebikeInfo(result);

        } else {
            useCar(result);
        }

    }

    private void ebikeInfo(final String tokencode) {
        Log.e("scan===000", "ebikeInfo====" + tokencode);

        final String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");

        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("access_token", access_token);
        params.put("tokencode", tokencode);
//        params.put("version", "");
        HttpHelper.get(this, Urls.ebikeInfo, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在加载");
                    loadingDialog.show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                    Log.e("scan===ebikeInfo", "====" + result.data);

                    if (result.getFlag().equals("Success")) {
//                        ToastUtil.showMessageApp(context,"电量更新成功");
                        Log.e("scan===ebikeInfo", "====" + responseString);

                        if ("[]".equals(result.getData()) || 0 == result.getData().length()) {
                            useCar(tokencode);
                        } else {

//                            if()

                            EbikeInfoBean bean = JSON.parseObject(result.getData(), EbikeInfoBean.class);

                            Log.e("scan===ebikeInfo2", bean.getElectricity().substring(0, bean.getElectricity().length() - 1) + "====" + responseString);

                            if (Integer.parseInt(bean.getElectricity().substring(0, bean.getElectricity().length() - 1)) <= 10) {
                                ToastUtil.showMessageApp(context, "电量低，请换辆车");

                                scrollToFinishActivity();
                            } else {
                                CustomDialog.Builder customBuilder = new CustomDialog.Builder(ActivityScanerCode.this);
                                customBuilder.setMessage("电单车必须在校内停车线还车");

                                customBuilder.setType(4).setElectricity(bean.getElectricity()).setMileage(bean.getMileage()).setFee(bean.getFee()).setTitle("这是一辆电单车").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        scrollToFinishActivity();
                                    }
                                }).setPositiveButton("开锁", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();

                                        useCar(tokencode);
                                    }
                                }).setHint(false);
                                customBuilder.create().show();
                            }


                        }


                    } else {
                        ToastUtil.showMessageApp(context, result.getMsg());
                    }
                } catch (Exception e) {
                }
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }
        });
    }

    void useCar(String result) {

        final String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)) {
            ToastUtil.showMessageApp(context, "请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        } else {
            RequestParams params = new RequestParams();
            params.put("uid", uid);
            params.put("access_token", access_token);
            params.put("tokencode", result);
            params.put("latitude", SharedPreferencesUrls.getInstance().getString("latitude", ""));
            params.put("longitude", SharedPreferencesUrls.getInstance().getString("longitude", ""));
            params.put("telprama", "手机型号：" + SystemUtil.getSystemModel() + ", Android系统版本号：" + SystemUtil.getSystemVersion());
            params.put("can_use_ebike", 1);
            HttpHelper.post(context, Urls.useCar, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("正在连接");
                        loadingDialog.show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }

                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                    Log.e("scan===useCar", "Fail===" + result);
                    UIHelper.ToastError(context, throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                        if (result.getFlag().equals("Success")) {
                            Log.e("scan===000", result.getFlag() + "===" + statusCode);

                            JSONObject jsonObject = new JSONObject(result.getData());

                            Log.e("scan===", jsonObject.getString("bleid") + "===" + responseString + "statusCode===" + result.getFlag() + "===" + statusCode + "===" + jsonObject.getString("type"));

                            bleid = jsonObject.getString("bleid");
                            type = jsonObject.getString("type");
                            codenum = jsonObject.getString("codenum");
                            m_nowMac = jsonObject.getString("macinfo");

                            if(BaseApplication.getInstance().isTest()){
                                type = "5";
                            }


                            if ("1".equals(type)) {          //机械锁
                                UIHelper.goToAct(context, CurRoadStartActivity.class);
                                scrollToFinishActivity();
                            } else if ("2".equals(type)) {    //蓝牙锁

                                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                    scrollToFinishActivity();
                                }
                                //蓝牙锁
                                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

                                mBluetoothAdapter = bluetoothManager.getAdapter();

                                if (mBluetoothAdapter == null) {
                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                    scrollToFinishActivity();
                                    return;
                                }
                                if (!mBluetoothAdapter.isEnabled()) {
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 188);
                                } else {
                                    if (loadingDialog != null && loadingDialog.isShowing()) {
                                        loadingDialog.dismiss();
                                    }

                                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                                        loadingDialog.setTitle("正在唤醒车锁");
                                        loadingDialog.show();
                                    }

                                    if (!TextUtils.isEmpty(m_nowMac)) {
                                        connect();
                                    }
                                }
                            } else if ("3".equals(type)) {    //3合1锁

                                if ("200".equals(jsonObject.getString("code"))) {
                                    Log.e("useBike===", "====" + jsonObject);

                                    getCurrentorder(uid, access_token);
                                } else if ("404".equals(jsonObject.getString("code"))) {
                                    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                        ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                        scrollToFinishActivity();
                                    }
                                    BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                                    mBluetoothAdapter = bluetoothManager.getAdapter();

                                    if (mBluetoothAdapter == null) {
                                        ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                        scrollToFinishActivity();
                                        return;
                                    }
                                    if (!mBluetoothAdapter.isEnabled()) {
                                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                        startActivityForResult(enableBtIntent, 188);
                                    } else {
                                        if (!TextUtils.isEmpty(m_nowMac)) {
                                            connect();
                                        }
                                    }
                                }
                            } else if ("4".equals(type)) {

                                if ("200".equals(jsonObject.getString("code"))) {
                                    Log.e("useBike===4", "====" + jsonObject);

                                    ToastUtil.showMessageApp(context, "恭喜您,开锁成功!");

                                    SharedPreferencesUrls.getInstance().putBoolean("isStop", false);
                                    SharedPreferencesUrls.getInstance().putString("m_nowMac", m_nowMac);
                                    SharedPreferencesUrls.getInstance().putBoolean("switcher", false);
                                    SharedPreferencesUrls.getInstance().putString("type", type);
                                    SharedPreferencesUrls.getInstance().putString("tempStat", "0");
                                    SharedPreferencesUrls.getInstance().putString("bleid", bleid);

                                    UIHelper.goToAct(context, CurRoadBikingActivity.class);
                                    scrollToFinishActivity();

                                } else {
                                    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                        ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                        scrollToFinishActivity();
                                    }
                                    BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                                    mBluetoothAdapter = bluetoothManager.getAdapter();

                                    BLEService.bluetoothAdapter = mBluetoothAdapter;

                                    bleService.view = context;
                                    bleService.showValue = true;

                                    if (mBluetoothAdapter == null) {
                                        ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                        scrollToFinishActivity();
                                        return;
                                    }
                                    if (!mBluetoothAdapter.isEnabled()) {
                                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                        startActivityForResult(enableBtIntent, 188);
                                    } else {

                                        Log.e("scan===4_1", bleid + "===" + jsonObject.getString("macinfo"));

                                        bleService.connect(m_nowMac);

                                        checkConnect();

//                                        bleService.sleep(2000);
//
//                                        Log.e("scan===4_2", "==="+m_nowMac);
//
//                                        bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});
//
//                                        bleService.sleep(500);
//
//                                        Log.e("scan===4_3", "==="+m_nowMac);
//
//                                        button8();
//                                        button9();
//
//                                        button3();
//
//                                        getCurrentorder2(uid, access_token);
//
//                                        Log.e("scan===4_4", "==="+m_nowMac);
                                    }
                                }

                            } else if ("5".equals(type)) {

                                if(BaseApplication.getInstance().isTest()){
                                    if("40001101".equals(codenum)){
                                        m_nowMac = "3C:A3:08:AE:BE:24";
                                    }else{
                                        m_nowMac = "A4:34:F1:7B:BF:9A";
                                    }

                                }


                                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                    scrollToFinishActivity();
                                }
                                //蓝牙锁
                                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

                                mBluetoothAdapter = bluetoothManager.getAdapter();

                                if (mBluetoothAdapter == null) {
                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                    scrollToFinishActivity();
                                    return;
                                }
                                if (!mBluetoothAdapter.isEnabled()) {
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 188);
                                } else {
                                    if (loadingDialog != null && loadingDialog.isShowing()) {
                                        loadingDialog.dismiss();
                                    }

                                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                                        loadingDialog.setTitle("正在唤醒车锁");
                                        loadingDialog.show();
                                    }

                                    if (!TextUtils.isEmpty(m_nowMac)) {

                                        SearchRequest request = new SearchRequest.Builder()      //duration为0时无限扫描
                                                .searchBluetoothLeDevice(0)
                                                .build();

                                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                            return;
                                        }
                                        ClientManager.getClient().search(request, mSearchResponse);

                                    }
                                }
                            }

//                            if (loadingDialog != null && loadingDialog.isShowing()){
//                                loadingDialog.dismiss();
//                            }

                        } else {

                            Toast.makeText(context,result.getMsg(),10 * 1000).show();
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                            scrollToFinishActivity();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }

//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }

                }
            });
        }
    }

    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            Log.e("scan===","DeviceListActivity.onSearchStarted");
//            mDevices.clear();
//            mAdapter.notifyDataChanged();
        }

        @Override
        public void onDeviceFounded(SearchResult device) {

            Log.e("scan===onDeviceFounded",device.device.getName() + "===" + device.device.getAddress());

            if(m_nowMac.equals(device.device.getAddress())){
                ClientManager.getClient().stopSearch();

                connectDevice();

                ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
            }



//            if (!mDevices.contains(device)) {
//                mDevices.add(device);
//                mAdapter.notifyItemInserted(mDevices.size());
//            } else {
//                int index = mDevices.indexOf(device);
//                mDevices.set(index, device);
//
//                if (StringUtils.checkBikeTag(device.getName())) {
//                    mAdapter.notifyDataChanged();
//                } else {
//                    mAdapter.notifyItemChanged(index);
//                }
//            }
        }

        @Override
        public void onSearchStopped() {
            Log.e("scan===","DeviceListActivity.onSearchStopped");

        }

        @Override
        public void onSearchCanceled() {
            Log.e("scan===","DeviceListActivity.onSearchCanceled");

        }
    };

    //监听当前连接状态
    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
//            BluetoothLog.v(String.format(Locale.getDefault(), "DeviceDetailActivity onConnectStatusChanged %d in %s", status, Thread.currentThread().getName()));

            Log.e("ConnectStatus===", "===="+(status == STATUS_CONNECTED));

//            keySource = "9A88005D";
//            rent();

            ClientManager.getClient().getStatus(mac, new IGetStatusResponse() {
                @Override
                public void onResponseSuccess(String version, String keySerial, String macKey, String vol) {
//                    queryStatusServer(version, keySerial, macKey, vol);

                    keySource = keySerial;
                    rent();
                }

                @Override
                public void onResponseFail(int code) {
                    Log.e("getStatus===", Code.toString(code));
                    ToastUtil.showMessageApp(context, Code.toString(code));
                }

            });

//            Globals.isBleConnected = mConnected = (status == STATUS_CONNECTED);
//            refreshData(mConnected);
//            connectDeviceIfNeeded();
        }
    };

    //获取服务器的加密信息
    private void queryStatusServer(String version, String keySerial, String macKey, String vol) {
        Log.e("queryStatusServer===", "version:" + version + " keySerial:" + keySerial + " macKey:" + macKey + " vol:" + vol);
//        this.version = version;
        int timestamp = (int) StringUtils.getCurrentTimestamp();

        Log.e("scan===qSS", macKey+"==="+keySerial+"==="+timestamp);

        keySource = keySerial;

        rent();

//        UIHelper.showProgress(this, "get_bike_server");
//        OkHttpClientManager.getInstance().Rent(macKey, "9A88005D", 1560316059, new ResultCallback<RRent>() {
//        OkHttpClientManager.getInstance().Rent(macKey, keySerial, timestamp, new ResultCallback<RRent>() {
//
//            @Override
//            public void onResponse(RRent rRent) {
////                UIHelper.dismiss();
//                if (rRent.getResult() >= 0) {
//
//                    getCurrentorder2(uid, access_token);
//
//
//                    RRent.ResultBean resultBean = rRent.getInfo();
//                    openBleLock(resultBean);
//                }
//                else {
////                    UIHelper.showToast(DeviceDetailActivity.this, ""+rRent.getResult());
//                    ToastUtil.showMessageApp(context, ""+rRent.getResult());
//                }
//            }
//
//            @Override
//            public void onError(Request request, Exception e) {
////                UIHelper.dismiss();
////                UIHelper.showToast(DeviceDetailActivity.this, e.getMessage());
//                ToastUtil.showMessageApp(context, e.getMessage());
//            }
//
//        });
    }

    private void rent(){
//        Map<String, Object> result = new HashMap();
//
//        //APP蓝牙搜到的锁号
//        String lockNo = params.getString("MAC");
//        //APP通过蓝牙命令从锁获取到的8位随机数
//        String keySource = params.getString("keySource");
//        //命令发起时间戳
//        long timestamp = params.getLong("timestamp");
//
//        //根据锁号查找到锁实体类
//        LockInfo lockInfo = getLockInfoModel(lockNo);
//        String secretKey = lockInfo.getSecretKey();

        String secretKey = "";

        if("40001101".equals(codenum)){
            secretKey = "eralHInialHInitwokPs5138m9pleBLEPeri4herzat4on8L0P1functP1functi7onSim9pInit2ali"; //"3C:A3:08:AE:BE:24";
        }else{
            secretKey = "NDQzMDMyMzYzOTM5MzkzMzQxNDQzMzQxMzMzOTMyMzE0NTM3NDEzMzQzMzU0NTM4MzYzMDM1Mzk0MzAw";  // "A4:34:F1:7B:BF:9A";
        }

//        String secretKey = "NDQzMDMyMzYzOTM5MzkzMzQxNDQzMzQxMzMzOTMyMzE0NTM3NDEzMzQzMzU0NTM4MzYzMDM1Mzk0MzAw";
//        String secretKey = "eralHInialHInitwokPs5138m9pleBLEPeri4herzat4on8L0P1functP1functi7onSim9pInit2ali";

        //AES加解密密钥
        String pwd = null;

        Random random = new Random();
        //随机数取80位密钥中，前64位的某一位
        encryptionKey = random.nextInt(secretKey.length()-16);
        //从随机数位数开始截取16位字符作为AES加解密密钥
        pwd = secretKey.substring(encryptionKey, encryptionKey+16);
        encryptionKey = encryptionKey + 128;

        //补8个0，AES加密需要16位数据
        keySource = keySource.toUpperCase()+"00000000";
        //加密
        byte[] encryptResultStr = AESUtil.encrypt(keySource.getBytes(), pwd);
        //转成hexString
        keys = AESUtil.bytesToHexString(encryptResultStr).toUpperCase();
        //服务器时间戳，精确到秒，用于锁同步时间
        serverTime = Calendar.getInstance().getTimeInMillis()/1000;

        Log.e("rent===", encryptionKey+"==="+pwd);


        getCurrentorder2(uid, access_token);

        openBleLock(null);

//        //密钥索引
//        result.put("encryptionKey", encryptionKey);
//        //开锁密钥
//        result.put("keys", keys);
//        //返回服务器时间戳
//        result.put("serverTime", serverTime);
//        return result;
    }

    //与设备，开锁
    private void openBleLock(RRent.ResultBean resultBean) {
//        UIHelper.showProgress(this, "open_bike_status");
//        ClientManager.getClient().openLock(mac, "18112348925", resultBean.getServerTime(),

//        Log.e("scan===openBleLock", resultBean.getServerTime()+"==="+resultBean.getKeys()+"==="+resultBean.getEncryptionKey());

        Log.e("scan===openBleLock", serverTime+"==="+keys+"==="+encryptionKey);

        ClientManager.getClient().openLock(m_nowMac,"000000000000", (int) serverTime, keys, encryptionKey, new IEmptyResponse(){
//        ClientManager.getClient().openLock(m_nowMac,"000000000000", 1560316059, "EFD72E14D4F1CAA9F919B6FE0066579F", 179, new IEmptyResponse(){
//        ClientManager.getClient().openLock(m_nowMac,"000000000000", resultBean.getServerTime(), resultBean.getKeys(), resultBean.getEncryptionKey(), new IEmptyResponse(){
                    @Override
                    public void onResponseFail(int code) {
                        Log.e("openLock===Fail", Code.toString(code));
//                        UIHelper.dismiss();
//                        UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));

//                        getBleRecord();

                        ToastUtil.showMessageApp(context, Code.toString(code));
                    }

                    @Override
                    public void onResponseSuccess() {
//                        UIHelper.dismiss();
                        getBleRecord();

                        Log.e("openLock===Success", "===");

//                        ClientManager.getClient().stopSearch();
//                        ClientManager.getClient().disconnect(m_nowMac);
//                      ClientManager.getClient().unnotifyClose(mac, mCloseListener);
                        ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);

//                        if (loadingDialog != null && loadingDialog.isShowing()){
//                            loadingDialog.dismiss();
//                        }
//
//                        ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");
//
//                        SharedPreferencesUrls.getInstance().putBoolean("isStop",false);
//                        SharedPreferencesUrls.getInstance().putString("m_nowMac", m_nowMac);
//                        SharedPreferencesUrls.getInstance().putBoolean("switcher", false);
//                        SharedPreferencesUrls.getInstance().putString("type", type);
//                        SharedPreferencesUrls.getInstance().putString("tempStat","0");
//                        SharedPreferencesUrls.getInstance().putString("bleid",bleid);
//
//                        UIHelper.goToAct(context, CurRoadBikingActivity.class);
//                        scrollToFinishActivity();

                    }
                });
    }

    //与设备，获取记录
    private void getBleRecord() {
        ClientManager.getClient().getRecord(m_nowMac, new IGetRecordResponse() {
            @Override
            public void onResponseSuccess(String phone, String bikeTradeNo, String timestamp,
                                          String transType, String mackey, String index, String cap, String vol) {
                Log.e("getBleRecord===", "Success===");

                deleteBleRecord(bikeTradeNo);
            }

            @Override
            public void onResponseSuccessEmpty() {
//                ToastUtil.showMessageApp(context, "record empty");
                Log.e("getBleRecord===", "Success===Empty");
            }

            @Override
            public void onResponseFail(int code) {
                Log.e("getBleRecord===", Code.toString(code));
                ToastUtil.showMessageApp(context, Code.toString(code));
            }
        });
    }

    //与设备，删除记录
    private void deleteBleRecord(String tradeNo) {
//        UIHelper.showProgress(this, R.string.delete_bike_record);
        ClientManager.getClient().deleteRecord(m_nowMac, tradeNo, new IGetRecordResponse() {
            @Override
            public void onResponseSuccess(String phone, String bikeTradeNo, String timestamp, String transType, String mackey, String index, String cap, String vol) {

                Log.e("scan===deleteBleRecord", "Success===");

                deleteBleRecord(bikeTradeNo);
            }

            @Override
            public void onResponseSuccessEmpty() {
//                UIHelper.dismiss();
                Log.e("scan===deleteBleRecord", "Success===Empty");

                SharedPreferencesUrls.getInstance().putBoolean("isStop",false);
                SharedPreferencesUrls.getInstance().putString("m_nowMac", m_nowMac);
                SharedPreferencesUrls.getInstance().putBoolean("switcher", false);
                SharedPreferencesUrls.getInstance().putString("type", type);
                SharedPreferencesUrls.getInstance().putString("tempStat","0");
                SharedPreferencesUrls.getInstance().putString("bleid",bleid);

                UIHelper.goToAct(context, CurRoadBikingActivity.class);
                scrollToFinishActivity();
            }

            @Override
            public void onResponseFail(int code) {
                Log.e("scan===deleteBleRecord", Code.toString(code));
                ToastUtil.showMessageApp(context, Code.toString(code));
            }
        });
    }


    //连接设备
    private void connectDevice() {
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(20000)
                .setServiceDiscoverRetry(1)
                .setServiceDiscoverTimeout(10000)
                .setEnableNotifyRetry(1)
                .setEnableNotifyTimeout(10000)
                .build();

        ClientManager.getClient().connect(m_nowMac, options, new IConnectResponse() {
            @Override
            public void onResponseFail(int code) {
                Log.e("connectDevice===", Code.toString(code));
                ToastUtil.showMessageApp(context, Code.toString(code));
            }

            @Override
            public void onResponseSuccess(BleGattProfile profile) {
                BluetoothLog.v(String.format("profile:\n%s", profile));
                refreshData(true);

                if (Globals.bType == 1) {
                    ToastUtil.showMessageApp(context, "正在关锁中");
//                    getBleRecord();
                }
            }
        });
    }



    private void refreshData(boolean refresh) {
//        if (refresh) {
//            tvState.setText("");
//            tvName.setText(Globals.BLE_NAME);
//            layLock.setClickable(true);
//        } else {
//            tvState.setText("开始蓝牙扫描");
//            tvName.setText("");
//            layLock.setClickable(false);
//        }
    }


    void checkConnect(){
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("checkConnect===", "===");

                if(!bleService.connect){
                    cn++;

                    if(cn<15){
                        checkConnect();
                    }else{
                        Toast.makeText(context,"扫码唤醒失败，换辆车试试吧！",5 * 1000).show();
                        scrollToFinishActivity();
                    }

                }else{
                    getCurrentorder2(uid, access_token);

                    bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});

                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("checkConnect===3", "==="+m_nowMac);

                            button8();
                            button9();
                            button3();

                            m_myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("checkConnect===4", bleService.cc+"==="+"B1 25 80 00 00 56 ".equals(bleService.cc));

                                    if("B1 25 80 00 00 56 ".equals(bleService.cc)){
                                        Log.e("checkConnect===5", oid+"==="+bleService.cc);
                                        ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");
                                        Log.e("scan===", "OPEN_ACTION===="+isOpen);

                                        SharedPreferencesUrls.getInstance().putBoolean("isStop",false);
                                        SharedPreferencesUrls.getInstance().putString("m_nowMac", m_nowMac);
                                        SharedPreferencesUrls.getInstance().putBoolean("switcher", false);
                                        SharedPreferencesUrls.getInstance().putString("type", type);
                                        SharedPreferencesUrls.getInstance().putString("tempStat","0");
                                        SharedPreferencesUrls.getInstance().putString("bleid",bleid);

                                        UIHelper.goToAct(context, CurRoadBikingActivity.class);
                                        scrollToFinishActivity();
                                    }else{
                                        closeEbike();
                                    }

                                    Log.e("checkConnect===6", "==="+bleService.cc);

                                }
                            }, 500);
                        }
                    }, 500);
                }

            }
        }, 1 * 1000);
    }


    //注册
    void button8() {
        IoBuffer ioBuffer = IoBuffer.allocate(20);
        ioBuffer.writeByte((byte) 0x82);
        ByteUtil.log("tel-->" + tel);
        String str = tel;

        byte[] bb = new byte[11];
        for (int i = 0; i < str.length(); i++) {
            char a = str.charAt(i);
            bb[i] = (byte) a;
        }
        ioBuffer.writeBytes(bb);

//        String bleID = "";
//
//        if("34:03:DE:54:E6:C6".equals(m_nowMac)){
//            bleID = "G3AF600000922E2XH";
//        }else if("34:03:DE:54:E4:34".equals(m_nowMac)){
//            bleID = "G3A1800000629BCXH";
//        }

        int crc = (int) ByteUtil.crc32(getfdqId(bleid));
        byte cc[] = ByteUtil.intToByteArray(crc);
        ioBuffer.writeByte(cc[0] ^ cc[3]);
        ioBuffer.writeByte(cc[1] ^ cc[2]);
        ioBuffer.writeInt(0);
        bleService.write(toBody(ioBuffer.readableBytes()));
    }

    //认证
    void button9() {
        IoBuffer ioBuffer = IoBuffer.allocate(20);
        ioBuffer.writeByte((byte) 0x83);
        ioBuffer.writeBytes(getfdqId(tel));
//        ioBuffer.writeBytes(getfdqId(tel.trim().toUpperCase()));
        bleService.write(toBody(ioBuffer.readableBytes()));
//        SharePreUtil.getPreferences("FDQID").putString("ID", tel);
//        SharedPreferencesUrls.getInstance().putBoolean("isStop", true);
    }

    byte[] getfdqId(String str) {

        IoBuffer ioBuffer = IoBuffer.allocate(17);
        for (int i = 0; i < str.length(); i++) {
            char a = str.charAt(i);
            ioBuffer.writeByte((byte) a);
        }
        return ioBuffer.array();
    }

    //启动
    void button3() {
        IoBuffer ioBuffer = IoBuffer.allocate(20);
        byte[] cmd = sendCmd("00000101", "00000000");
        ioBuffer.writeBytes(cmd);
        bleService.write(toBody(ioBuffer.readableBytes()));
    }

    //关闭
    void button4() {
        IoBuffer ioBuffer = IoBuffer.allocate(20);
        byte[] cmd = sendCmd("00000010", "00000000");
        ioBuffer.writeBytes(cmd);
        bleService.write(toBody(ioBuffer.readableBytes()));
    }


    public byte[] sendCmd(String s1, String s2) {
        IoBuffer ioBuffer = IoBuffer.allocate(5);
        ioBuffer.writeByte(0XA1);
        ioBuffer.writeByte(ByteUtil.BitToByte(s1));
        ioBuffer.writeByte(ByteUtil.BitToByte(s2));

        ioBuffer.writeByte(0);
        ioBuffer.writeByte(0);

        return ioBuffer.array();
    }

    IoBuffer toBody(byte[] bb) {
        IoBuffer buffer = IoBuffer.allocate(20);
        buffer.writeByte(bb.length + 1);
        buffer.writeBytes(bb);
        buffer.writeByte((int) ByteUtil.SumCheck(bb));


        return buffer.flip();
    }

    private void getCurrentorder(final String uid, final String access_token){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        HttpHelper.post(this, Urls.getCurrentorder, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("开锁中");
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
                        Log.e("scan===getCurrentorder", n+"===="+result.getData());

                        if ("[]".equals(result.getData()) || 0 == result.getData().length()){
                            if(n<5){
                                n++;

                                m_myHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getCurrentorder(uid, access_token);
                                    }
                                }, 2 * 1000);
                            }else{
                                n=0;

                                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                    scrollToFinishActivity();
                                }
                                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                                mBluetoothAdapter = bluetoothManager.getAdapter();

                                if (mBluetoothAdapter == null) {
                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                    scrollToFinishActivity();
                                    return;
                                }
                                if (!mBluetoothAdapter.isEnabled()) {
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 188);
                                }else{
                                    if (!TextUtils.isEmpty(m_nowMac)) {
                                        connect();
                                    }
                                }
                            }

                        }else {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

                            ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

                            SharedPreferencesUrls.getInstance().putBoolean("isStop",false);
                            SharedPreferencesUrls.getInstance().putString("m_nowMac", m_nowMac);
                            SharedPreferencesUrls.getInstance().putBoolean("switcher", false);
                            SharedPreferencesUrls.getInstance().putString("type", type);
                            SharedPreferencesUrls.getInstance().putString("tempStat","0");
                            SharedPreferencesUrls.getInstance().putString("bleid",bleid);

                            UIHelper.goToAct(context, CurRoadBikingActivity.class);
                            scrollToFinishActivity();
                        }
                    } else {
                        ToastUtil.showMessageApp(context,result.getMsg());

                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                } catch (Exception e) {

                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                }

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            switch (requestCode) {
                case 288:{
                    break;
                }
                case 188:{
                    if (null != loadingDialog && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }

                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("正在唤醒车锁");
                        loadingDialog.show();
                    }

                    if (!TextUtils.isEmpty(m_nowMac)) {
                        if("4".equals(type)){
                            bleService.connect(m_nowMac);
                            checkConnect();

                        }else if("5".equals(type)){
                            SearchRequest request = new SearchRequest.Builder()      //duration为0时无限扫描
                                    .searchBluetoothLeDevice(0)
                                    .build();

                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            ClientManager.getClient().search(request, mSearchResponse);

                        }else{
                            connect();
                        }

                    }
                    break;
                }
                default:{
                    break;
                }
            }
        }else if( requestCode == 188){
            ToastUtil.showMessageApp(this, "需要打开蓝牙");
            scrollToFinishActivity();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishMine();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBLEStateChangeBroadcast != null) {
            unregisterReceiver(mBLEStateChangeBroadcast);
        }
        if (mDataValueBroadcast != null) {
            unregisterReceiver(mDataValueBroadcast);
        }
    }
    /**
     * 链接设备
     * 1. 先进行搜索设备，匹配mac地址是否能搜索到
     * 2. 搜索到就链接设备，取消倒计时；
     * 3. 搜索不到就执行直接连接设备
     */
    protected void connect() {
        BaseApplication.getInstance().getIBLE().stopScan();
        m_myHandler.sendEmptyMessage(0x99);
        BaseApplication.getInstance().getIBLE().startScan(new OnDeviceSearchListener() {
            @Override
            public void onScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (device==null||TextUtils.isEmpty(device.getAddress()))return;
                if (m_nowMac.equalsIgnoreCase(device.getAddress())){
                    m_myHandler.removeMessages(0x99);
                    BaseApplication.getInstance().getIBLE().stopScan();
                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, ActivityScanerCode.this);
                }
            }
        });
    }
    @Override
    public void onTimeOut() {

    }
    @Override
    public void onDisconnect(int state) {
        m_myHandler.sendEmptyMessageDelayed(0, 1000);
    }
    @Override
    public void onServicesDiscovered(String name, String address) {
        isStop = true;
        getToken();
    }
    /**
     * 获取token
     */
    private void getToken() {
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BaseApplication.getInstance().getIBLE().getToken();
            }
        }, 500);
    }
    /**
     * 广播
     * */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            String data = intent.getStringExtra("data");
            switch (action) {
                case Config.TOKEN_ACTION:

                    if (null != loadingDialog && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }

                    if(isOpen){
                        break;
                    }else{
                        isOpen=true;
                    }

                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            BaseApplication.getInstance().getIBLE().getBattery();
                        }
                    }, 1000);

                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(ActivityScanerCode.this);
                    if (0 == Tag){
                        customBuilder.setMessage("扫码成功,是否开锁?");
                    }else {
                        customBuilder.setMessage("输号成功,是否开锁?");
                    }
                    customBuilder.setTitle("温馨提示").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    m_myHandler.sendEmptyMessage(1);
                                }
                            }).start();

                        }
                    }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                            Log.e("scan===", "scan====1");

                            getCurrentorder2(uid, access_token);


                            if (loadingDialog != null && !loadingDialog.isShowing()) {
                                loadingDialog.setTitle("开锁中");
                                loadingDialog.show();
                            }

                            m_myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
//                                    ToastUtil.showMessage(context, BaseApplication.getInstance().getIBLE().getConnectStatus()+"==="+BaseApplication.getInstance().getIBLE().getLockStatus());

                                    if (loadingDialog != null && loadingDialog.isShowing()){
                                        loadingDialog.dismiss();
                                    }

                                    String uid = SharedPreferencesUrls.getInstance().getString("uid","");
                                    String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");

//                                    Log.e("scan===1", BaseApplication.getInstance().getIBLE().getLockStatus()+"===="+oid+"===="+referLatitude);

//                                    if (BaseApplication.getInstance().getIBLE().getLockStatus()){
                                        Log.e("scan===2", oid+"===="+referLatitude);
                                        submit(uid, access_token);
//                                    }
                                }
                            }, 10 * 1000);

//                          BaseApplication.getInstance().getIBLE().getLockStatus();

                            Log.e("scan===", "scan===="+loadingDialog);

                        }
                    }).setHint(false);
                    customBuilder.create().show();

                    break;
                case Config.BATTERY_ACTION:
                    if (!TextUtils.isEmpty(data)) {
                        quantity = String.valueOf(Integer.parseInt(data, 16));
                    }else {
                        quantity = "";
                    }
                    break;
                case Config.OPEN_ACTION:
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }

                    if (TextUtils.isEmpty(data)) {
                        ToastUtil.showMessageApp(context,"开锁失败,请重试");
                        scrollToFinishActivity();
                    } else {
                        ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");
                        Log.e("scan===", "OPEN_ACTION===="+isOpen);

                        SharedPreferencesUrls.getInstance().putBoolean("isStop",false);
                        SharedPreferencesUrls.getInstance().putString("m_nowMac", m_nowMac);
                        SharedPreferencesUrls.getInstance().putBoolean("switcher", false);
                        SharedPreferencesUrls.getInstance().putString("type", type);
                        SharedPreferencesUrls.getInstance().putString("tempStat","0");
                        SharedPreferencesUrls.getInstance().putString("bleid",bleid);

                        UIHelper.goToAct(context, CurRoadBikingActivity.class);
                        scrollToFinishActivity();
                    }
                    break;
                case Config.CLOSE_ACTION:
                    if (TextUtils.isEmpty(data)) {
                    } else {
                    }

                    break;
                case Config.LOCK_STATUS_ACTION:
                    if (TextUtils.isEmpty(data)) {
                    } else {
                    }

                    break;
                case Config.LOCK_RESULT:
                    if (TextUtils.isEmpty(data)) {
                    } else {
                    }

                    break;
            }
        }
    };


    private void getCurrentorder2(final String uid, final String access_token){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        HttpHelper.post(this, Urls.getCurrentorder, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("开锁中");
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
                        Log.e("scan===", "getCurrentorder===="+result.getData()+"==="+type);

                        if ("[]".equals(result.getData()) || 0 == result.getData().length()){
                            addOrderbluelock();
                        }else {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

                            ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

                            if(!"5".equals(type)){
                                SharedPreferencesUrls.getInstance().putBoolean("isStop",false);
                                SharedPreferencesUrls.getInstance().putString("m_nowMac", m_nowMac);
                                SharedPreferencesUrls.getInstance().putBoolean("switcher", false);
                                SharedPreferencesUrls.getInstance().putString("type", type);
                                SharedPreferencesUrls.getInstance().putString("tempStat","0");
                                SharedPreferencesUrls.getInstance().putString("bleid",bleid);

                                UIHelper.goToAct(context, CurRoadBikingActivity.class);
                                scrollToFinishActivity();
                            }

                        }
                    } else {
                        ToastUtil.showMessageApp(context,result.getMsg());

                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                } catch (Exception e) {

                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                }

            }
        });
    }


    private void addOrderbluelock(){
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("codenum", codenum);

            ;

            if (quantity != null && !"".equals(quantity)){
                params.put("quantity",quantity);
            }

            HttpHelper.post(context, Urls.addOrderbluelock, params, new TextHttpResponseHandler() {
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

                            Log.e("scan===addOrderbluelock", "===" + type);

                            CurRoadBikingBean bean = JSON.parseObject(result.getData(),CurRoadBikingBean.class);
                            oid = bean.getOid();

                            if("4".equals(type) || "5".equals(type)){
                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }

                                ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");
                                Log.e("scan===OPEN_ACTION", m_nowMac+"===="+isOpen);

                                if(!"5".equals(type)){
                                    SharedPreferencesUrls.getInstance().putBoolean("isStop",false);
                                    SharedPreferencesUrls.getInstance().putString("m_nowMac", m_nowMac);
                                    SharedPreferencesUrls.getInstance().putBoolean("switcher", false);
                                    SharedPreferencesUrls.getInstance().putString("type", type);
                                    SharedPreferencesUrls.getInstance().putString("tempStat","0");
                                    SharedPreferencesUrls.getInstance().putString("bleid",bleid);

                                    UIHelper.goToAct(context, CurRoadBikingActivity.class);
                                    scrollToFinishActivity();
                                }

                            }else{


                                Log.e("scan===lock1", "===");

                                BaseApplication.getInstance().getIBLE().openLock();

                                Log.e("scan===lock2", "===");
                            }



                        } else {
                            ToastUtil.showMessageApp(context, result.getMsg());
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                }
            });

        }
    }

    public void closeEbike(){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("oid",oid);

        Log.e("closeEbike===", "==="+oid);

        HttpHelper.post(this, Urls.closeEbike, params, new TextHttpResponseHandler() {
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
                        ToastUtil.showMessage(context,"数据更新成功");

                        Log.e("biking===", "closeEbike===="+result.getData());

                        if ("0".equals(result.getData())){
                            submit(uid, access_token);
                        } else {
//                            ToastUtil.showMessageApp(context,"关锁失败");
//
//                            bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});
//
//                            m_myHandler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Log.e("endBtn4===4_3", "==="+m_nowMac);
//
//                                    button9();
//                                    button2();
//
//                                    m_myHandler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Log.e("endBtn4===4_4", bleService.cc+"==="+"B1 2A 80 00 00 5B ".equals(bleService.cc));
//
//                                            if("B1 2A 80 00 00 5B ".equals(bleService.cc)){
//                                                Log.e("endBtn4===4_5", oid+"==="+bleService.cc);
//                                                macList2 = new ArrayList<> (macList);
//                                                submit(uid, access_token);
//                                            }else{
//                                                customDialog5.show();
//                                            }
//
//                                            Log.e("endBtn4===4_6", "==="+bleService.cc);
//
//                                        }
//                                    }, 500);
//                                }
//                            }, 500);

                        }
                    } else {
                        ToastUtil.showMessageApp(context,result.getMsg());

                    }
                } catch (Exception e) {
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });
    }

    protected void submit(String uid, String access_token){

        Log.e("scan===",SharedPreferencesUrls.getInstance().getBoolean("isStop",true)+"==="+uid+"==="+access_token+"==="+oid+"==="+referLatitude+"==="+referLongitude);

        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("access_token", access_token);
        params.put("oid", oid);
        params.put("latitude", referLatitude);
        params.put("longitude", referLongitude);
//        if (macList.size() > 0){
//            params.put("xinbiao", macList.get(0));
//        }
        HttpHelper.post(context, Urls.backBikescan, params, new TextHttpResponseHandler() {
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
                Log.e("scan===","结束用车:"+responseString);
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        SharedPreferencesUrls.getInstance().putString("m_nowMac","");
                        SharedPreferencesUrls.getInstance().putString("oid","");
                        SharedPreferencesUrls.getInstance().putString("osn","");
                        SharedPreferencesUrls.getInstance().putString("type","");
                        SharedPreferencesUrls.getInstance().putBoolean("isStop",true);
                        SharedPreferencesUrls.getInstance().putString("biking_latitude","");
                        SharedPreferencesUrls.getInstance().putString("biking_longitude","");

                        ToastUtil.showMessageApp(context, "开锁失败，请重试");

                        scrollToFinishActivity();

                    }else {
                        ToastUtil.showMessageApp(context, result.getMsg());
                    }
                }catch (Exception e){

                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });
    }


    Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 0:
                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, ActivityScanerCode.this);
                    break;
                case 1:

                    BaseApplication.getInstance().getIBLE().refreshCache();
                    BaseApplication.getInstance().getIBLE().close();
                    BaseApplication.getInstance().getIBLE().disconnect();
                    BaseApplication.getInstance().getIBLE().disableBluetooth();
                    scrollToFinishActivity();

                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 9:
                    break;
                case 0x99://搜索超时
                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, ActivityScanerCode.this);
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isStop){
                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                    loadingDialog.dismiss();
                                }
//                                Toast.makeText(context,"请重启软件，开启定位服务,输编号用车",5 * 1000).show();
                                Toast.makeText(context,"扫码唤醒失败，换辆车试试吧！",5 * 1000).show();
                                BaseApplication.getInstance().getIBLE().refreshCache();
                                BaseApplication.getInstance().getIBLE().close();
                                BaseApplication.getInstance().getIBLE().disconnect();
                                scrollToFinishActivity();
                            }
                        }
                    }, 15 * 1000);
                    break;
                default:
                    break;
            }
            return false;
        }
    });


}