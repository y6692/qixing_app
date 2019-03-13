package com.zxing.lib.scaner.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.zxing.Result;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.config.LockType;
import com.sunshine.blelibrary.inter.OnConnectionListener;
import com.sunshine.blelibrary.inter.OnDeviceSearchListener;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;
import com.vondear.rxtools.RxAnimationTool;
import com.vondear.rxtools.RxBeepTool;
import com.vondear.rxtools.interfaces.OnRxScanerListener;
import com.vondear.rxtools.view.dialog.RxDialogSure;
import com.zxing.lib.scaner.CameraManager;
import com.zxing.lib.scaner.CaptureActivityHandler;
import com.zxing.lib.scaner.decoding.InactivityTimer;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.CurRoadBikingActivity;
import cn.qimate.bike.activity.CurRoadStartActivity;
import cn.qimate.bike.activity.LoginActivity;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.CurRoadBikingBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.SystemUtil;
import cn.qimate.bike.util.ToastUtil;

/**
 * @author vondear
 */
public class ActivityScanerCode extends SwipeBackActivity implements View.OnClickListener,OnConnectionListener{

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
//    private Dialog dialog;
//    private EditText bikeNumEdit;
//    private Button positiveButton,negativeButton;
    private int Tag = 0;

    private String quantity = "";
    private int num = 30;//扫描时间
    private boolean isStop = false;
    private boolean isOpen = false;
    private int n = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scaner_code);
        context = this;

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

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        ToastUtil.showMessage(this, "scaner===="+referLatitude);

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
        if (loadingDialog != null && loadingDialog.isShowing()){
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
        if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
        super.onDestroy();

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
//        top_mask_bcg = (ImageView)findViewById(R.id.top_mask_bcg);
        mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);
//        mLlScanHelp = (LinearLayout) findViewById(R.id.ll_scan_help);

//        dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog);
//        View dialogView = LayoutInflater.from(this).inflate(R.layout.pop_circles_menu, null);
//        dialog.setContentView(dialogView);
//        dialog.setCanceledOnTouchOutside(false);

//        bikeNumEdit = (EditText)dialogView.findViewById(R.id.pop_circlesMenu_bikeNumEdit);
//        positiveButton = (Button)dialogView.findViewById(R.id.pop_circlesMenu_positiveButton);
//        negativeButton = (Button)dialogView.findViewById(R.id.pop_circlesMenu_negativeButton);
//        positiveButton.setOnClickListener(this);
//        negativeButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.pop_circlesMenu_positiveButton:
//                String bikeNum = bikeNumEdit.getText().toString().trim();
//                if (bikeNum == null || "".equals(bikeNum)){
//                    ToastUtil.showMessageApp(this,"请输入单车编号");
//                    return;
//                }
//                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                manager.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏
//                if (dialog.isShowing()) {
//                    dialog.dismiss();
//                }
//                Tag = 1;
//                useBike(bikeNum);
//                break;
//            case R.id.pop_circlesMenu_negativeButton:
//                InputMethodManager manager1= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                manager1.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏
//                BaseApplication.getInstance().getIBLE().refreshCache();
//                BaseApplication.getInstance().getIBLE().close();
//                BaseApplication.getInstance().getIBLE().disconnect();
//                if (dialog.isShowing()) {
//                    dialog.dismiss();
//                }
//
//                scrollToFinishActivity();
//
//                break;
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

    public void btn(View view) {
        int viewId = view.getId();
        if (viewId == R.id.top_mask) {
            light();
        } else if (viewId == R.id.ll_back) {
            scrollToFinishActivity();
//        } else if (viewId == R.id.top_openpicture) {
//            RxPhotoTool.openLocalImage(mActivity);
        }else if(viewId == R.id.loca_show_btnBikeNum){
            //关闭二维码扫描
            if (handler != null) {
                handler.quitSynchronously();
                handler = null;
            }
            CameraManager.get().closeDriver();

            UIHelper.goToAct(context, BikeNumActivity.class);

//            WindowManager windowManager = getWindowManager();
//            Display display = windowManager.getDefaultDisplay();
//            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//            lp.width = (int) (display.getWidth() * 0.8); // 设置宽度0.6
//            lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//            dialog.getWindow().setAttributes(lp);
//            dialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
//            dialog.show();
//            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//            manager.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
//            manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
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

    private void useBike(String result){
        Log.e("scan===", "useBike==="+result);

        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("tokencode",result);
            params.put("latitude",SharedPreferencesUrls.getInstance().getString("latitude",""));
            params.put("longitude",SharedPreferencesUrls.getInstance().getString("longitude",""));
            params.put("telprama","手机型号：" + SystemUtil.getSystemModel() + ", Android系统版本号："+SystemUtil.getSystemVersion());
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
                            JSONObject jsonObject = new JSONObject(result.getData());

                            Log.e("scan===", responseString+"statusCode==="+result.getFlag()+"==="+statusCode+"==="+jsonObject.getString("type"));

                            if ("1".equals(jsonObject.getString("type"))){          //机械锁

                                UIHelper.goToAct(context, CurRoadStartActivity.class);
                                scrollToFinishActivity();
                            }else if ("2".equals(jsonObject.getString("type"))){    //蓝牙锁
                                codenum = jsonObject.getString("codenum");
                                m_nowMac = jsonObject.getString("macinfo");
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
                                }else{
                                    if (loadingDialog != null && loadingDialog.isShowing()){
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
                            }else if ("3".equals(jsonObject.getString("type"))){    //3合1锁
                                codenum = jsonObject.getString("codenum");
                                m_nowMac = jsonObject.getString("macinfo");

                                if ("200".equals(jsonObject.getString("code"))){
                                    Log.e("useBike===", "===="+jsonObject);

                                    getCurrentorder(uid, access_token);
                                }else if ("404".equals(jsonObject.getString("code"))){
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



                }
            });
        }
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
                        Log.e("scan===", "getCurrentorder===="+result.getData());

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
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("正在唤醒车锁");
                        loadingDialog.show();
                    }

                    if (!TextUtils.isEmpty(m_nowMac)) {
                        connect();
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
                        Log.e("scan===", "getCurrentorder===="+result.getData());

                        if ("[]".equals(result.getData()) || 0 == result.getData().length()){
                            addOrderbluelock();
                        }else {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

                            ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

                            SharedPreferencesUrls.getInstance().putBoolean("isStop",false);
                            SharedPreferencesUrls.getInstance().putString("m_nowMac", m_nowMac);
                            SharedPreferencesUrls.getInstance().putBoolean("switcher", false);

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

            Log.e("scan===lock", uid + "===" + access_token + "===" + codenum);

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

                            CurRoadBikingBean bean = JSON.parseObject(result.getData(),CurRoadBikingBean.class);
                            oid = bean.getOid();

                            Log.e("scan===lock1", "===");

                            BaseApplication.getInstance().getIBLE().openLock();

                            Log.e("scan===lock2", "===");


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
                        SharedPreferencesUrls.getInstance().putString("type","");
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