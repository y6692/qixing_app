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
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.xiaoantech.sdk.XiaoanBleApiClient;
import com.xiaoantech.sdk.ble.model.Response;
import com.xiaoantech.sdk.ble.scanner.ScanResult;
import com.xiaoantech.sdk.listeners.BleCallback;
import com.xiaoantech.sdk.listeners.BleStateChangeListener;
import com.xiaoantech.sdk.listeners.ScanResultCallback;
import com.zbar.lib.camera.CameraPreview;
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
import cn.qimate.bike.activity.CouponActivity;
import cn.qimate.bike.activity.CrashHandler;
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
import cn.qimate.bike.core.common.Md5Helper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.fragment.BikeFragment;
import cn.qimate.bike.model.CurRoadBikingBean;
import cn.qimate.bike.model.EbikeInfoBean;
import cn.qimate.bike.model.KeyBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UserIndexBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.AESUtil;
import cn.qimate.bike.util.ByteUtil;
import cn.qimate.bike.util.Constants;
import cn.qimate.bike.util.Globals;
import cn.qimate.bike.util.IoBuffer;
import cn.qimate.bike.util.PublicWay;
import cn.qimate.bike.util.SharePreUtil;
import cn.qimate.bike.util.SystemUtil;
import cn.qimate.bike.util.ToastUtil;
import okhttp3.Request;
import permissions.dispatcher.NeedsPermission;

import static com.sofi.blelocker.library.Constants.STATUS_CONNECTED;

/**
 * @author vondear
 */
public class ActivityScanerCode extends SwipeBackActivity implements View.OnClickListener, OnConnectionListener, ScanResultCallback {

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
    private LinearLayout iv_help;
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
    private boolean isClose = true;
    private boolean isFinish = false;
    private int n = 0;
    private int cn = 0;

    //    BLEService bleService = new BLEService();
    private String tel = "13188888888";
    private String bleid = "";

    private String deviceuuid = "";

    private String keySource = "";
    //密钥索引
    int encryptionKey= 0;
    //开锁密钥
    String keys = null;
    //服务器时间戳，精确到秒，用于锁同步时间
    long serverTime;

    private CameraManager mCameraManager;
    private Camera mCamera;

    private boolean previewing = true;
//    boolean first=true;

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    private XiaoanBleApiClient apiClient;

    private boolean isConnect = false;
    private boolean isTz = false;

    private Dialog advDialog;
    private ImageView advCloseBtn;

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

        CrashHandler.getInstance().setmContext(this);

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

        mCameraManager = CameraManager.get();

        surfaceView = (SurfaceView) findViewById(R.id.capture_preview);

        initHttp();

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


//        if(surfaceView != null) return;


//        surfaceView.clearAnimation();

        Log.e("surface===0", "==="+hasSurface);

        if (!hasSurface) {
            //Camera初始化
//            initCamera(surfaceHolder);
//            resetCamera(surfaceHolder);

            if(surfaceHolder==null){
                surfaceHolder = surfaceView.getHolder();

                surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                    }

                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {
                        Log.e("surface===1", "==="+hasSurface);

                        if (!hasSurface) {
                            hasSurface = true;

                            initCamera(holder);
                        }
                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder holder) {
                        Log.e("surface===2", "==="+hasSurface);

                        hasSurface = false;

                    }
                });
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            }



        } else {
            initCamera(surfaceHolder);
        }


//        try {
//
//            mCamera = Camera.open();
//
//            mCamera.setPreviewDisplay(surfaceHolder);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private void releaseCamera() {
        if (mCamera != null) {

            Log.e("0===releaseCamera", "==="+mCamera);

//            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();

            previewing = false;
            mCamera.setPreviewCallback(null);
            Log.e("1===releaseCamera", "==="+mCamera);
            mCamera.release();
            Log.e("2===releaseCamera", "==="+mCamera);
            mCamera = null;
        }
    }

    private void resetCamera(SurfaceHolder surfaceHolder){
        Log.e("===resetCamera", "==="+mCamera);

        previewing = true;

        try {

            mCameraManager.openDriver(surfaceHolder);
            mCamera = mCameraManager.getCamera();
            Point point = mCameraManager.getCameraResolution();
            AtomicInteger width = new AtomicInteger(point.y);
            AtomicInteger height = new AtomicInteger(point.x);
            int cropWidth = mCropLayout.getWidth() * width.get() / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height.get() / mContainer.getHeight();
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);
        } catch (Exception e) {
            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
            return;
        }

        if (handler == null) {
            handler = new CaptureActivityHandler(ActivityScanerCode.this);
        }

//        Log.e("111====resetCamera", mCamera+"==="+previewCb+"==="+autoFocusCB);
//
//        surfaceView.r
//        mPreview = new CameraPreview(context, mCamera, previewCb, autoFocusCB);
//        scanPreview.addView(mPreview);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            mCameraManager = CameraManager.get();
            mCameraManager.openDriver(surfaceHolder);
            mCamera = mCameraManager.getCamera();
            Point point = mCameraManager.getCameraResolution();
            AtomicInteger width = new AtomicInteger(point.y);
            AtomicInteger height = new AtomicInteger(point.x);
            int cropWidth = mCropLayout.getWidth() * width.get() / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height.get() / mContainer.getHeight();
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);

//            if (handler == null) {
//                handler = new CaptureActivityHandler(ActivityScanerCode.this);
//            }

            handler = new CaptureActivityHandler(ActivityScanerCode.this);
        } catch (Exception e) {
            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
            Log.e("initCamera===ASC_e", "===="+e);
//            return;
        }

    }

    public void btn(View view) {
        int viewId = view.getId();
        if (viewId == R.id.top_mask) {
            light();

//            bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});
//            Log.e("light===4_3", "===");
//            button3();

//            XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
//            builder.setBleStateChangeListener(ActivityScanerCode.this);
//            builder.setScanResultCallback(ActivityScanerCode.this);
//            apiClient = builder.build();
//
//            MainActivityPermissionsDispatcher.connectDeviceWithPermissionCheck(ActivityScanerCode.this, deviceuuid);

        } else if (viewId == R.id.top_back) {
            scrollToFinishActivity();
        } else if (viewId == R.id.top_openpicture) {
            RxPhotoTool.openLocalImage(mActivity);
        } else if (viewId == R.id.loca_show_btnBikeNum) {
//            button4();

            //关闭二维码扫描
//            if (handler != null) {
//                handler.quitSynchronously();
//                handler = null;
//            }
//            CameraManager.get().closeDriver();


//            releaseCamera();
//            mCameraManager.closeDriver();

            bikeNumEdit.setText("");

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
    protected void onPause() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }

//        hasSurface = false;

        Log.e("scan===onPause", "===");

        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
//            handler = null;
        }

//        if(!first){
//            releaseCamera();
//        }


//        releaseCamera();
        mCameraManager.closeDriver();
    }

    @Override
    protected void onDestroy() {
        try{
            inactivityTimer.shutdown();
            mScanerListener = null;
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }

            Log.e("scan===onDestroy", m_nowMac+"==="+type+"==="+isTz);

            if(!isTz){
                if("5".equals(type)  || "6".equals(type)){

                }else{
                    BaseApplication.getInstance().getIBLE().stopScan();
                    BaseApplication.getInstance().getIBLE().refreshCache();
                    BaseApplication.getInstance().getIBLE().close();
                    BaseApplication.getInstance().getIBLE().disconnect();
                }
            }

            super.onDestroy();


            if (broadcastReceiver != null) {
                unregisterReceiver(broadcastReceiver);
                broadcastReceiver = null;
            }
            m_myHandler.removeCallbacksAndMessages(null);
        }catch (Exception e){
            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
        }



    }

    void tzEnd(){

        isTz = true;

        Log.e("tzEnd===", oid+"==="+type);

        SharedPreferencesUrls.getInstance().putBoolean("isStop", false);
        SharedPreferencesUrls.getInstance().putString("m_nowMac", m_nowMac);
        SharedPreferencesUrls.getInstance().putBoolean("switcher", false);
        SharedPreferencesUrls.getInstance().putString("type", type);
        SharedPreferencesUrls.getInstance().putString("tempStat", "0");
        SharedPreferencesUrls.getInstance().putString("bleid", bleid);
        SharedPreferencesUrls.getInstance().putString("deviceuuid", deviceuuid);
        SharedPreferencesUrls.getInstance().putInt("major", 0);

//        SharedPreferencesUrls.getInstance().putBoolean("isTz", true);
//        UIHelper.goToAct(context, CurRoadBikingActivity.class);

        Intent intent = new Intent(context, CurRoadBikingActivity.class);
        intent.putExtra("isTz",true);
        startActivity(intent);

        scrollToFinishActivity();
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

        iv_help = (LinearLayout) findViewById(R.id.ll_help);

        dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.pop_circles_menu, null);
        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);

        advDialog = new Dialog(context, R.style.Theme_AppCompat_Dialog);
        View advDialogView = LayoutInflater.from(context).inflate(R.layout.ui_adv_view2, null);
        advDialog.setContentView(advDialogView);
        advDialog.setCanceledOnTouchOutside(false);

        advCloseBtn = (ImageView)advDialogView.findViewById(R.id.ui_adv_closeBtn);
        advCloseBtn.setOnClickListener(this);

        bikeNumEdit = (EditText) dialogView.findViewById(R.id.pop_circlesMenu_bikeNumEdit);
        positiveButton = (Button) dialogView.findViewById(R.id.pop_circlesMenu_positiveButton);
        negativeButton = (Button) dialogView.findViewById(R.id.pop_circlesMenu_negativeButton);
        positiveButton.setOnClickListener(this);
        negativeButton.setOnClickListener(this);


    }

    private void initHttp(){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid != null && !"".equals(uid) && access_token != null && !"".equals(access_token)){
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            HttpHelper.get(context, Urls.userIndex, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在加载");
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


                                    UserIndexBean bean = JSON.parseObject(result.getData(), UserIndexBean.class);
                                    String school = bean.getSchool();

                                    Log.e("initHttp===", "==="+school);

                                    if("河北工程大学".equals(school) || "西安交通大学（创新港）".equals(school) || "南京信息工程大学".equals(school)){
                                        WindowManager windowManager = getWindowManager();
                                        Display display = windowManager.getDefaultDisplay();

                                        Log.e("display===", "==="+display.getWidth());

                                        WindowManager.LayoutParams lp = advDialog.getWindow().getAttributes();
                                        lp.width = (int) (display.getWidth() * 1);
                                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                        advDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                                        advDialog.getWindow().setAttributes(lp);
                                        advDialog.show();
                                    }


                                } else {
                                    Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                                e.printStackTrace();
                            }
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }
                    });

                }
            });
        }else {
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context,LoginActivity.class);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ui_adv_closeBtn:
                Log.e("onClick===", advDialog+"==="+advDialog.isShowing());

                if (advDialog != null && advDialog.isShowing()) {
                    advDialog.dismiss();
                }
                break;
            case R.id.pop_circlesMenu_positiveButton:
//                try {
                    String bikeNum = bikeNumEdit.getText().toString().trim();

                    Log.e("bikeNum===", "==="+bikeNum);



                    if (bikeNum == null || "".equals(bikeNum)) {

                        ToastUtil.showMessageApp(context, "请输入单车编号");

                        return;
                    }
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏
                    if (dialog!=null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Tag = 1;
//                useBike(bikeNum);

//                    int n = 1/0;

                    ebikeInfo(bikeNum);
//                }catch (Exception e){
////                    ToastUtil.showMessageApp(context, "==="+e);
//                }


                break;

            case R.id.pop_circlesMenu_negativeButton:

                Log.e("bikeNum===2", "==="+type);

                InputMethodManager manager1 = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager1.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏

//                if("5".equals(type)  || "6".equals(type)){
//                    ClientManager.getClient().stopSearch();
//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);
//
//                }else{
//                    BaseApplication.getInstance().getIBLE().refreshCache();
//                    BaseApplication.getInstance().getIBLE().close();
//                    BaseApplication.getInstance().getIBLE().disconnect();
//                }

                BaseApplication.getInstance().getIBLE().refreshCache();
                BaseApplication.getInstance().getIBLE().close();
                BaseApplication.getInstance().getIBLE().disconnect();

                if (dialog!=null && dialog.isShowing()) {
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
            mCameraManager.openLight();
        } else {
            mFlashing = true;
            // 关闪光灯
            mCameraManager.offLight();
        }

    }





    public void handleDecode(final Result result) {
        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                try{
                    if(!previewing) return;

                    Log.e("handleDecode===", "===");

                    inactivityTimer.onActivity();
                    //扫描成功之后的振动与声音提示
                    RxBeepTool.playBeep(mActivity, vibrate);


//        mCamera.setPreviewCallback(null);
//        mCamera.stopPreview();
//
//        releaseCamera();


                    String result1 = result.getText();
                    if (mScanerListener == null) {
                        initDialogResult(result);
                    } else {
                        mScanerListener.onSuccess("From to Camera", result);
                    }
                } catch (Exception e) {
                    ToastUtil.showMessageApp(context, "扫码异常，请重试");
                    scrollToFinishActivity();
                }

            }
        });

    }

    //--------------------------------------打开本地图片识别二维码 start---------------------------------
    private void initDialogResult(final Result result) {
        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                useBike(result.toString());


            }
        });

    }

    public Handler getHandler() {
        return handler;
    }

    private void useBike(final String result) {
        Log.e("scan===useBike", result.indexOf('&') + "<===>" + result);

//        int code = 0;
//        if (result.split("&").length == 1) {
//            if (result.split("\\?")[1].split("&")[0].split("=")[1].matches(".*[a-zA-z].*")) {
//                useCar(result);
//                return;
//            } else {
//                code = Integer.parseInt(result.split("\\?")[1].split("&")[0].split("=")[1]);
//            }
//        } else {
//            code = Integer.parseInt(result.split("\\?")[1].split("&")[0].split("=")[1]);
//        }
//
//        if (result.indexOf('&') != -1 || (code >= 80001651 && code <= 80002050)) {  //电单车
//            ebikeInfo(result);
//        } else {
//            useCar(result);
//        }

        ebikeInfo(result);

//        useCar(result);
    }

    private void ebikeInfo(final String tokencode) {
        Log.e("scan===000", "ebikeInfo====" + tokencode);

//        int t = 1/0;

//        useCar(tokencode);


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
                onStartCommon("正在加载");
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

                            Log.e("scan===ebikeInfo", responseString + "====" + result.data);

                            if (result.getFlag().equals("Success")) {
                                if ("[]".equals(result.getData()) || 0 == result.getData().length()) {
                                    useCar(tokencode);
                                } else {
                                    EbikeInfoBean bean = JSON.parseObject(result.getData(), EbikeInfoBean.class);

                                    Log.e("scan===ebikeInfo2", bean.getElectricity().substring(0, bean.getElectricity().length() - 1) + "====");

//                                    if (Integer.parseInt(bean.getElectricity().substring(0, bean.getElectricity().length() - 1)) <= 10) {
//                                        ToastUtil.showMessageApp(context, "电量低，请换辆车");
//
//                                        scrollToFinishActivity();
//                                    } else {
//
//
//                                    }

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
                            } else {
                                ToastUtil.showMessageApp(context, result.getMsg());
                            }

                        } catch (Exception e) {
                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }
                });


            }
        });

    }



    void useCar(String result) {

        Log.e("scan===useCar0", "===" + result);

        final String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)) {
            ToastUtil.showMessageApp(context, "请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        } else {


//            result = "11223344";

//            memberEvent();

            RequestParams params = new RequestParams();
            params.put("uid", uid);
            params.put("access_token", access_token);
            params.put("tokencode", result);
            params.put("latitude", SharedPreferencesUrls.getInstance().getString("latitude", ""));
            params.put("longitude", SharedPreferencesUrls.getInstance().getString("longitude", ""));
            params.put("telprama", "手机型号：" + SystemUtil.getSystemModel() + ", Android系统版本号：" + SystemUtil.getSystemVersion());
            params.put("can_use_ebike", 3);


            Log.e("scan===useCar1", uid + "===" + access_token + "===" + SystemUtil.getSystemModel() + "==="+SystemUtil.getSystemVersion() + "===" +SharedPreferencesUrls.getInstance().getString("latitude", "") + "===" +SharedPreferencesUrls.getInstance().getString("longitude", "")+ "===" +result);

            HttpHelper.post(context, Urls.useCar, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    Log.e("scan===useCar2", "===");
                    onStartCommon("正在连接");
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("scan===useCar3", "===");

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
                                    Log.e("scan===000", result.getFlag()  + "===" + responseString);

                                    JSONObject jsonObject = new JSONObject(result.getData());

                                    Log.e("scan===", jsonObject.getString("bleid") + "statusCode===" + result.getFlag()  + "===" + jsonObject.getString("type"));

                                    bleid = jsonObject.getString("bleid");
                                    type = jsonObject.getString("type");
                                    if("7".equals(type)){
                                        deviceuuid = jsonObject.getString("deviceuuid");
                                    }
                                    codenum = jsonObject.getString("codenum");
                                    m_nowMac = jsonObject.getString("macinfo");

                                    if(BaseApplication.getInstance().isTest()){
                                        type = "5";
                                    }
//                                    type = "7";

//                                    m_nowMac = "40:19:79:40:90:03";

                                    if ("1".equals(type)) {          //单车机械锁
                                        UIHelper.goToAct(context, CurRoadStartActivity.class);
                                        scrollToFinishActivity();
                                    } else if ("2".equals(type)) {    //单车蓝牙锁

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
                                    }

                                } else {

                                    Toast.makeText(context,result.getMsg(),Toast.LENGTH_LONG).show();
                                    if (loadingDialog != null && loadingDialog.isShowing()){
                                        loadingDialog.dismiss();
                                    }
                                    scrollToFinishActivity();

                                }

                            } catch (Exception e) {
                                memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                                e.printStackTrace();

                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }
                            }
                        }
                    });



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
                onStartCommon("开锁中");
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

                                    tzEnd();
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

                    Log.e("188===", m_nowMac+"==="+type+"==="+deviceuuid);

//                    if (!TextUtils.isEmpty(m_nowMac)) {
//                    }


                    connect();

                    break;
                }
                default:{
                    break;
                }
            }
        }else if( requestCode == 188){
            ToastUtil.showMessageApp(this, "需要打开蓝牙");

            if("".equals(oid)){
                scrollToFinishActivity();
            }else{
                if(!isFinishing()){
                    tzEnd();
                }
            }


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
//        BaseApplication.getInstance().getIBLE().connect(m_nowMac, ActivityScanerCode.this);

        Log.e("connect===", m_nowMac+"==="+Build.VERSION.SDK_INT);

//        if (Build.VERSION.SDK_INT >= 23) {
//            m_myHandler.sendEmptyMessage(0x99);
//        }else{
//
//        }

        BaseApplication.getInstance().getIBLE().stopScan();
        m_myHandler.sendEmptyMessage(0x99);
        BaseApplication.getInstance().getIBLE().startScan(new OnDeviceSearchListener() {
            @Override
            public void onScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {

                Log.e("connect===", m_nowMac+"==="+device.getName()+"==="+device.getAddress());

                if (device==null||TextUtils.isEmpty(device.getAddress()))return;
                if (m_nowMac.equalsIgnoreCase(device.getAddress())){
                    Log.e("connect===2", m_nowMac+"==="+device.getName()+"==="+device.getAddress());

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
        Log.e("onDisconnect===", "==="+state);

        m_myHandler.sendEmptyMessageDelayed(0, 1000);
    }
    @Override
    public void onServicesDiscovered(String name, String address) {
        Log.e("onServicesDiscovered===", name+"==="+address);

        BaseApplication.getInstance().getIBLE().stopScan();

//        if (m_nowMac.equalsIgnoreCase(address)){
//            Log.e("onServicesDiscovered==2", m_nowMac+"==="+address);
//
//            m_myHandler.removeMessages(0x99);
//            BaseApplication.getInstance().getIBLE().connect(m_nowMac, ActivityScanerCode.this);
//        }


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
        public void onReceive(final Context context, final Intent intent) {

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {

                    String action = intent.getAction();
                    String data = intent.getStringExtra("data");
                    switch (action) {
                        case Config.TOKEN_ACTION:
                            isStop = true;

                            if (null != loadingDialog && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

//                    if(isOpen){
//                        break;
//                    }else{
//                        isOpen=true;
//                    }

                            m_myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    BaseApplication.getInstance().getIBLE().getBattery();
                                }
                            }, 1000);


                            Log.e("scan===", "scan====1");

                            getCurrentorder2(uid, access_token);

                            if (loadingDialog != null && !loadingDialog.isShowing()) {
                                loadingDialog.setTitle("开锁中");
                                loadingDialog.show();
                            }

//                          BaseApplication.getInstance().getIBLE().getLockStatus();

                            Log.e("scan===", "scan===="+loadingDialog);

//                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(ActivityScanerCode.this);
//                            if (0 == Tag){
//                                customBuilder.setMessage("扫码成功,是否开锁?");
//                            }else {
//                                customBuilder.setMessage("输号成功,是否开锁?");
//                            }
//                            customBuilder.setTitle("温馨提示").setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//
//                                    m_myHandler.sendEmptyMessage(1);
//
//                                }
//                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//
//
//
//                                }
//                            }).setHint(false);
//                            customBuilder.create().show();

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

                                submit(uid, access_token);

//                                scrollToFinishActivity();
                            } else {
                                isOpen = true;

                                ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");
                                Log.e("scan===", "OPEN_ACTION===="+isOpen);

                                isFinish = true;

                                if(!isFinishing()){
                                    tzEnd();
                                }
//                                else{
//                                    addOrderbluelock2();
//                                }

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
            });


        }
    };


    private void getCurrentorder2(final String uid, final String access_token){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);



        HttpHelper.post(this, Urls.getCurrentorder, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("开锁中");
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
                                Log.e("scan===", "getCurrentorder===="+result.getData()+"==="+type);

                                if ("[]".equals(result.getData()) || 0 == result.getData().length()){

                                    addOrderbluelock();

                                }else {
                                    if (loadingDialog != null && loadingDialog.isShowing()){
                                        loadingDialog.dismiss();
                                    }

                                    ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

                                    if(!"5".equals(type) && !"6".equals(type)){
                                        tzEnd();
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
        });
    }

    void memberEvent() {
        RequestParams params = new RequestParams();
        try {
            Log.e("scan===memberEvent0", new Build().MANUFACTURER.toUpperCase()+"==="+new Build().MODEL+"==="+Build.VERSION.RELEASE+"==="+getPackageManager().getPackageInfo(getPackageName(), 0).versionName);

            params.put("uid", uid);
            params.put("access_token", access_token);
            params.put("phone_brand", new Build().MANUFACTURER.toUpperCase());
            params.put("phone_model", new Build().MODEL);
            params.put("phone_system", "Android");
            params.put("phone_system_version", Build.VERSION.RELEASE);     //手机系统版本 必传 如：13.1.2
            params.put("app_version", getPackageManager().getPackageInfo(getPackageName(), 0).versionName);      //应用版本 必传 如：1.8.2
            params.put("event", "2");
            params.put("event_id", oid);
            params.put("event_content", "open_lock");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        HttpHelper.post(context, Urls.memberEvent, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Log.e("scan===memberEvent1", "==="+responseString);

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
    }

    void memberEvent2() {
        RequestParams params = new RequestParams();
        try {
            Log.e("scan===memberEvent0", new Build().MANUFACTURER.toUpperCase()+"==="+new Build().MODEL+"==="+Build.VERSION.RELEASE+"==="+getPackageManager().getPackageInfo(getPackageName(), 0).versionName);

            params.put("uid", uid);
            params.put("access_token", access_token);
            params.put("phone_brand", new Build().MANUFACTURER.toUpperCase());
            params.put("phone_model", new Build().MODEL);
            params.put("phone_system", "Android");
            params.put("phone_system_version", Build.VERSION.RELEASE);     //手机系统版本 必传 如：13.1.2
            params.put("app_version", getPackageManager().getPackageInfo(getPackageName(), 0).versionName);      //应用版本 必传 如：1.8.2
            params.put("event", "4");
//            params.put("event_id", type);
            params.put("event_content", "type"+type+"唤醒失败");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        HttpHelper.post(context, Urls.memberEvent, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Log.e("scan===memberEvent1", "==="+responseString);

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
    }

    void memberEvent(String ex) {
        RequestParams params = new RequestParams();
        try {
            Log.e("CrashH===memberEvent0", new Build().MANUFACTURER.toUpperCase()+"==="+new Build().MODEL+"==="+Build.VERSION.RELEASE+"==="+context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);

            String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
            String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");

            params.put("uid", uid);
            params.put("access_token", access_token);
            params.put("phone_brand", new Build().MANUFACTURER.toUpperCase());
            params.put("phone_model", new Build().MODEL);
            params.put("phone_system", "Android");
            params.put("phone_system_version", Build.VERSION.RELEASE);     //手机系统版本 必传 如：13.1.2
            params.put("app_version", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);      //应用版本 必传 如：1.8.2
            params.put("event", "1");
            params.put("event_content", ex);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        HttpHelper.post(context, Urls.memberEvent, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Log.e("CrashH===memberEvent1", "==="+responseString);

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
    }

    private void addOrderbluelock(){
        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("codenum", codenum);

            Log.e("addOrderbluelock==", "==="+quantity);

            if (quantity != null && !"".equals(quantity)){
                params.put("quantity",quantity);
            }

            HttpHelper.post(context, Urls.addOrderbluelock, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在加载");
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
                                    CurRoadBikingBean bean = JSON.parseObject(result.getData(),CurRoadBikingBean.class);
                                    oid = bean.getOrderid();

                                    memberEvent();

                                    Log.e("scan===addOrderbluelock", oid + "===" + type);

                                    if("4".equals(type)){


                                    }else{
                                        Log.e("scan===lock1", "===");

                                        BaseApplication.getInstance().getIBLE().openLock();

//                                        m_myHandler.postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//
//                                                if (loadingDialog != null && loadingDialog.isShowing()){
//                                                    loadingDialog.dismiss();
//                                                }
//
//                                                String uid = SharedPreferencesUrls.getInstance().getString("uid","");
//                                                String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
//
//                                                Log.e("scan===22", isFinish+"===="+isOpen+"===="+oid+"===="+referLatitude);
//
//                                                if(!isFinishing() && !isFinish){
//                                                    submit(uid, access_token);
//                                                }
//
//                                            }
//                                        }, 1 * 1000);

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
            });
        }
    }

    private void addOrderbluelock2(){
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

            Log.e("addOrderbluelock==", "==="+quantity);

            if (quantity != null && !"".equals(quantity)){
                params.put("quantity",quantity);
            }

            HttpHelper.post(context, Urls.addOrderbluelock, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在加载");
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
                                    CurRoadBikingBean bean = JSON.parseObject(result.getData(),CurRoadBikingBean.class);
                                    oid = bean.getOrderid();

                                    Log.e("scan==addOrderbluelock2", oid + "===" + type);

                                    ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

//                                    tzEnd();

                                } else {
                                    ToastUtil.showMessageApp(context, result.getMsg());
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
    }

    protected void submit(String uid, String access_token){

        Log.e("scan===",SharedPreferencesUrls.getInstance().getBoolean("isStop",true)+"==="+uid+"==="+access_token+"==="+oid+"==="+referLatitude+"==="+referLongitude);

        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("access_token", access_token);
        params.put("oid", oid);
        params.put("latitude", referLatitude);
        params.put("longitude", referLongitude);

        params.put("xinbiao_name", "");
        params.put("xinbiao_mac", "");

        params.put("back_type", "fail_lock");

//        if (macList.size() > 0){
//            params.put("xinbiao", macList.get(0));
//        }
        HttpHelper.post(context, Urls.backBikescan, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在提交");
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
                            Log.e("scan===submit","结束用车:"+type+"==="+isFinish+"==="+responseString);
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            if (result.getFlag().equals("Success")) {
                                if(isFinish){
                                    addOrderbluelock2();
                                }else{
                                    SharedPreferencesUrls.getInstance().putString("m_nowMac","");
                                    SharedPreferencesUrls.getInstance().putString("oid","");
                                    SharedPreferencesUrls.getInstance().putString("osn","");
                                    SharedPreferencesUrls.getInstance().putString("type",type);
                                    SharedPreferencesUrls.getInstance().putBoolean("isStop",true);
                                    SharedPreferencesUrls.getInstance().putString("biking_latitude","");
                                    SharedPreferencesUrls.getInstance().putString("biking_longitude","");

                                    ToastUtil.showMessageApp(context, "开锁失败，请重试");

                                    scrollToFinishActivity();
                                }

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
//                    BaseApplication.getInstance().getIBLE().disableBluetooth();
                    scrollToFinishActivity();

                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 9:
                    break;

                case 0x98://搜索超时
//                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, ActivityScanerCode.this);

                    break;

                case 0x99://搜索超时
//                    BaseApplication.getInstance().getIBLE().resetLock();
//                    BaseApplication.getInstance().getIBLE().resetBluetoothAdapter();

//                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

//                    mBluetoothAdapter..disable();


                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, ActivityScanerCode.this);
//                    resetLock();
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isStop){
                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                    loadingDialog.dismiss();
                                }

                                memberEvent2();

//                                Toast.makeText(context,"请重启软件，开启定位服务,输编号用车",5 * 1000).show();
                                Toast.makeText(context,"扫码唤醒失败，重启手机蓝牙换辆车试试吧！",Toast.LENGTH_LONG).show();
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


    @Override
    public void onResult(ScanResult scanResult) {

    }

//    public void onStartCommon(final String title) {
//        m_myHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                    loadingDialog.setTitle(title);
//                    loadingDialog.show();
//                }
//            }
//        });
//
//    }
//
//    public void onFailureCommon(final String s) {
//        m_myHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
//                UIHelper.ToastError(context, s);
//            }
//        });
//
//    }

}