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
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
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
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.scan.BleScanRuleConfig;
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
import java.net.URLEncoder;
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
import cn.qimate.bike.core.widget.LoadingDialog2;
import cn.qimate.bike.fragment.BikeFragment;
import cn.qimate.bike.lock.utils.Utils;
import cn.qimate.bike.model.CarBean;
import cn.qimate.bike.model.CurRoadBikingBean;
import cn.qimate.bike.model.EbikeInfoBean;
import cn.qimate.bike.model.KeyBean;
import cn.qimate.bike.model.OrderBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UserIndexBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.AESUtil;
import cn.qimate.bike.util.ByteUtil;
import cn.qimate.bike.util.Constants;
import cn.qimate.bike.util.Globals;
import cn.qimate.bike.util.IoBuffer;
import cn.qimate.bike.util.LogUtil;
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
public class ActivityScanerCode extends SwipeBackActivity implements View.OnClickListener
//        , OnConnectionListener
//        , BleStateChangeListener
//        , ScanResultCallback
        {

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
    private RelativeLayout scanCropView;

    /**
     * 扫描框根布局
     */
    private RelativeLayout mCropLayout = null;
    private RelativeLayout mCropLayout2 = null;

    private LinearLayout ll_input;

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
//    private LinearLayout mLlScanHelp;

    /**
     * 闪光灯 按钮
     */
    private TextView mIvLight;

    private TextView btnBikeNum;

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


    private String m_nowMac = "";
    private String codenum = "";
    private String lock_no = "";
    private String electricity = "";
    private String mileage = "";
    private String carmodel_name = "";
    private String each_free_time = "";
    private int today_free_times;
    private String first_price = "";
    private String first_time = "";
    private String continued_price = "";
    private String continued_time = "";
    private String credit_score_desc = "";
    private int allow_temporary_lock;

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
    private TextView advCloseBtn;
    private ImageView cancelBtn;
    private LinearLayout ll_positiveButton;
    private ImageView cancelBtn2;
    private LinearLayout ll_positiveButton2;

    private boolean isHide = false;
    private boolean isHand = false;
    private boolean isLight = false;

    private CustomDialog customDialog;
    private CustomDialog customDialog2;

    InputMethodManager inputMethodManager;

    private List<String> macList = new ArrayList<String>();

    private boolean isPermission = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scaner_code);
        context = this;

//        SharedPreferencesUrls.getInstance().putString("uid", "251540");

//        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
//
//        BLEService.bluetoothAdapter = mBluetoothAdapter;
//
//        bleService.view = this;
//        bleService.showValue = true;

        CrashHandler.getInstance().setmContext(this);

        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

//        registerReceiver(broadcastReceiver, Config.initFilter());
//        GlobalParameterUtils.getInstance().setLockType(LockType.MTS);
//
//        BaseApplication.getInstance().getIBLE().refreshCache();
//        BaseApplication.getInstance().getIBLE().close();
//        BaseApplication.getInstance().getIBLE().disconnect();

        //界面控件初始化

        initView();
        //权限初始化
        initPermission();
        //扫描动画初始化
        initScanerAnimation();
        //初始化 CameraManager

        CameraManager.init(context);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

        mCameraManager = CameraManager.get();

//        surfaceView = (SurfaceView) findViewById(R.id.capture_preview);


//        initHttp();
//        cycling();
    }

    public void btn(View view) {
        int viewId = view.getId();
        if (viewId == R.id.top_mask) {
            if(isLight){
                isLight = false;
                mIvLight.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.top_light), null, null);
            }else{
                isLight = true;
                mIvLight.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.top_light2), null, null);
            }


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
            inputMethodManager.hideSoftInputFromWindow(bikeNumEdit.getWindowToken(), 0);
            scrollToFinishActivity();
//            CameraManager.init(mActivity);
//            hasSurface = false;
//            inactivityTimer = new InactivityTimer(this);
//
//            mCameraManager = CameraManager.get();
//
//            surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
//
//
//            if (!hasSurface) {
//
//                if(surfaceHolder==null){
//                    surfaceHolder = surfaceView.getHolder();
//
//                    Log.e("surface===0_1", isPermission+"==="+hasSurface+"==="+surfaceHolder);
//
//                    surfaceHolder.addCallback(new SurfaceHolder.Callback() {
//                        @Override
//                        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//                        }
//
//                        @Override
//                        public void surfaceCreated(SurfaceHolder holder) {
//                            Log.e("surface===1", "==="+hasSurface);
//
//                            if (!hasSurface) {
//                                hasSurface = true;
//                                initCamera(holder);
//                            }
//                        }
//
//                        @Override
//                        public void surfaceDestroyed(SurfaceHolder holder) {
//                            Log.e("surface===2", "==="+hasSurface);
//
//                            hasSurface = false;
//
//                            if (loadingDialog != null && loadingDialog.isShowing()) {
//                                loadingDialog.dismiss();
//                            }
//                        }
//                    });
//                    surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//                }
//
//            } else {
//                Log.e("surface===3", "==="+hasSurface);
//
//                initCamera(surfaceHolder);
//            }



        } else if (viewId == R.id.ll_confirm) {
            String bikeNum = bikeNumEdit.getText().toString().trim();

            Log.e("bikeNum===", "==="+bikeNum);



            if (bikeNum == null || "".equals(bikeNum)) {

                ToastUtil.showMessageApp(context, "请输入单车编号");

                return;
            }

            mCropLayout.setVisibility(View.VISIBLE);
            ll_input.setVisibility(View.GONE);

            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0); // 隐藏
            if (dialog!=null && dialog.isShowing()) {
                dialog.dismiss();
            }
            Tag = 1;
            order_authority(bikeNum);
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

            if(isHand){
                isHand = false;
                btnBikeNum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.top_hand), null, null);

                previewing = true;
                initCamera(surfaceHolder);
//                mCamera.startPreview();

//                mCamera.autoFocus(new Camera.AutoFocusCallback() {
//                    @Override
//                    public void onAutoFocus(boolean success, Camera camera) {
//                        camera.cancelAutoFocus();
//                    }
//                });

                mCropLayout2.setVisibility(View.VISIBLE);
                ll_input.setVisibility(View.GONE);

                inputMethodManager.hideSoftInputFromWindow(bikeNumEdit.getWindowToken(), 0);
            }else{
                isHand = true;
                btnBikeNum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.top_hand2), null, null);

//                mCamera.stopPreview();
//                previewing = false;
//                mCamera.setPreviewCallback(null);
                if (mCamera!=null){
//                    mCamera.cancelAutoFocus();

                    previewing = false;
//                    mCamera.stopPreview();

//                    m_myHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//
//
//                        }
//                    }, 2 * 1000);


//                    mCamera.setPreviewCallback(null);
//                    mCamera.release();
//                    mCamera = null;
                }

//                inactivityTimer.shutdown();

                isHide = true;

                mCropLayout2.setVisibility(View.GONE);
                ll_input.setVisibility(View.VISIBLE);

//            bikeNumEdit.setText("");
//            bikeNumEdit.findFocus();
//            bikeNumEdit.focusSearch(View.FOCUS_LEFT);
//            bikeNumEdit.forceLayout();

                bikeNumEdit.requestFocus();

//            WindowManager windowManager = getWindowManager();
//            Display display = windowManager.getDefaultDisplay();
//            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//            lp.width = (int) (display.getWidth() * 0.8); // 设置宽度0.6
//            lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//            dialog.getWindow().setAttributes(lp);
//            dialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
//            dialog.show();

                inputMethodManager.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            }

//            InputMethodHolder.registerListener(onInputMethodListener);
        }
    }

    private void setScanRule() {
//        String[] uuids;
//        String str_uuid = et_uuid.getText().toString();
//        if (TextUtils.isEmpty(str_uuid)) {
//            uuids = null;
//        } else {
//            uuids = str_uuid.split(",");
//        }
//        UUID[] serviceUuids = null;
//        if (uuids != null && uuids.length > 0) {
//            serviceUuids = new UUID[uuids.length];
//            for (int i = 0; i < uuids.length; i++) {
//                String name = uuids[i];
//                String[] components = name.split("-");
//                if (components.length != 5) {
//                    serviceUuids[i] = null;
//                } else {
//                    serviceUuids[i] = UUID.fromString(uuids[i]);
//                }
//            }
//        }

//        String[] names;
//        String str_name = et_name.getText().toString();
//        if (TextUtils.isEmpty(str_name)) {
//            names = null;
//        } else {
//            names = str_name.split(",");
//        }
//
//        String mac = et_mac.getText().toString();
//
//        boolean isAutoConnect = sw_auto.isChecked();

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
//                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
//                .setDeviceName(true, names)   // 只扫描指定广播名的设备，可选
//				  .setDeviceMac(address)                  // 只扫描指定mac的设备，可选
//                .setAutoConnect(true)                 // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    void scan(){
//        loadingDialog = DialogUtils.getLoadingDialog(context, "正在搜索...");
//		loadingDialog.setTitle("正在搜索");
//		loadingDialog.show();

        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
//                mDeviceAdapter.clearScanDevice();
//                mDeviceAdapter.notifyDataSetChanged();
//                img_loading.startAnimation(operatingAnim);
//                img_loading.setVisibility(View.VISIBLE);
//                btn_scan.setText(getString(R.string.stop_scan));
                Log.e("asc===onScanStarted", "==="+success);

            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);

//                Log.e("asc===onLeScan", bleDevice+"==="+bleDevice.getMac());
            }

            @Override
            public void onScanning(final BleDevice bleDevice) {
//                mDeviceAdapter.addDevice(bleDevice);
//                mDeviceAdapter.notifyDataSetChanged();

                Log.e("asc===onScanning", bleDevice+"==="+bleDevice.getMac());

                macList.add(bleDevice.getMac());


//				m_myHandler.post(new Runnable() {
//					@Override
//					public void run() {
//						if(address.equals(bleDevice.getMac())){
//							//                            if (loadingDialog != null && loadingDialog.isShowing()) {
////                                loadingDialog.dismiss();
////                            }
//
//							BleManager.getInstance().cancelScan();
//
//							Log.e("onScanning===2", isConnect+"==="+bleDevice+"==="+bleDevice.getMac());
//
//							Toast.makeText(context, "搜索成功", Toast.LENGTH_LONG).show();
//
//							connect();
//
////                            m_myHandler.postDelayed(new Runnable() {
////                                @Override
////                                public void run() {
////                                    if(!isConnect)
////                                        connect();
////                                }
////                            }, 5 * 1000);
//						}
//					}
//				});

            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
//                img_loading.clearAnimation();
//                img_loading.setVisibility(View.INVISIBLE);
//                btn_scan.setText(getString(R.string.start_scan));

                Log.e("asc===onScanFinished", scanResultList+"==="+scanResultList.size());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ToastUtil.showMessage(this, "scaner====" + referLatitude);


        Log.e("surface===0", isPermission+"==="+hasSurface+"==="+surfaceHolder);

//        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//        }
//        //蓝牙锁
//        if (mBluetoothAdapter == null) {
//            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//            mBluetoothAdapter = bluetoothManager.getAdapter();
//        }
//
//        if (mBluetoothAdapter == null) {
//            ToastUtil.showMessageApp(context, "获取蓝牙失败");
//            return;
//        }
//
//        if (!mBluetoothAdapter.isEnabled()) {
//            isPermission = true;
//
//            previewing = false;
//            mCropLayout2.setVisibility(View.GONE);
//
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, 188);
//        } else {
//        }


        if(!isPermission){
            if (!hasSurface) {

                if(surfaceHolder==null){
                    surfaceHolder = surfaceView.getHolder();

                    Log.e("surface===0_1", isPermission+"==="+hasSurface+"==="+surfaceHolder);

                    surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                        @Override
                        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                        }

                        @Override
                        public void surfaceCreated(SurfaceHolder holder) {
                            Log.e("surface===1", "==="+hasSurface);

                            if (!hasSurface) {
                                hasSurface = true;


                                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                }
                                //蓝牙锁
                                if (mBluetoothAdapter == null) {
                                    BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                                    mBluetoothAdapter = bluetoothManager.getAdapter();
                                }

                                if (mBluetoothAdapter == null) {
                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                    return;
                                }

                                if (!mBluetoothAdapter.isEnabled()) {
                                    isPermission = true;

//                                    previewing = false;       //TODO
//                                    mCropLayout2.setVisibility(View.GONE);

                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 188);
                                } else {
                                    initCamera(holder);

                                    BleManager.getInstance().init(getApplication());
                                    BleManager.getInstance()
                                            .enableLog(true)
                                            .setReConnectCount(10, 5000)
                                            .setConnectOverTime(20000)
                                            .setOperateTimeout(10000);

                                    setScanRule();
                                    scan();

                                    Log.e("asc===onCreate", "==="+mCamera);

                                }


                            }
                        }

                        @Override
                        public void surfaceDestroyed(SurfaceHolder holder) {
                            Log.e("surface===2", "==="+hasSurface);

                            hasSurface = false;

                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                        }
                    });
                    surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                }

            } else {
                Log.e("surface===3", "==="+hasSurface);

                initCamera(surfaceHolder);
            }

//            previewing = false;
//            hasSurface = false;
//            mCropLayout2.setVisibility(View.GONE);


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

    @Override
    protected void onPause() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }

//        hasSurface = false;

        LogUtil.e("ASC===onPause", surfaceHolder+"==="+hasSurface+"==="+handler);


//		surfaceHolder.removeCallback(sf);

        if (handler != null) {
            handler.quitSynchronously();
//			handler = null;
        }
////		inactivityTimer.onPause();
//
        mCameraManager.closeDriver();

//        if (!hasSurface) {
////			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
////			SurfaceHolder surfaceHolder = surfaceView.getHolder();
//            surfaceHolder.removeCallback(sf);
//        }

//		surfaceHolder.removeCallback(sf);

        LogUtil.e("ASC===onPause2", "==="+hasSurface);

        super.onPause();

//        if(isPermission){
//
//        }
//
//        if (handler != null) {
//            handler.quitSynchronously();
//        }
//
//        mCameraManager.closeDriver();

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

    private Handler autoFocusHandler;
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
//            autoFocusHandler.postDelayed(doAutoFocus, 1000);

            mCamera.cancelAutoFocus();
        }
    };

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            mCameraManager = CameraManager.get();
            mCameraManager.openDriver(surfaceHolder);
            mCamera = mCameraManager.getCamera();
            Point point = mCameraManager.getCameraResolution();
            AtomicInteger width = new AtomicInteger(point.y);
            AtomicInteger height = new AtomicInteger(point.x);
            int cropWidth = scanCropView.getWidth() * width.get() / mContainer.getWidth();
            int cropHeight = scanCropView.getHeight() * height.get() / mContainer.getHeight();
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);

//            mCamera.startPreview();
//            mCamera.autoFocus(autoFocusCB);

//            if (handler == null) {
//                handler = new CaptureActivityHandler(ActivityScanerCode.this);
//            }

            handler = new CaptureActivityHandler(this);

            Log.e("initCamera===", "====");

        } catch (Exception e) {
            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
            Log.e("initCamera===ASC_e", "===="+e);
//            return;
        }

    }



//    onInputMethodListener = new OnInputMethodListener() {
//        @Override
//        public void onShow(boolean result) {
//            Toast.makeText(context, "Show input method! " + result, Toast.LENGTH_SHORT).show();
//        }
//        @Override
//        public void onHide(boolean result) {
//            Toast.makeText(context, "Hide input method! " + result, Toast.LENGTH_SHORT).show();
//        }
//    };

    @Override
    public void oncall() {
        super.oncall();
        button9();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try{
            if(isPermission){
                inactivityTimer.shutdown();
                mScanerListener = null;
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                Log.e("scan===onDestroy", m_nowMac+"==="+type+"==="+isTz);

//            if(!isTz){
//                if("5".equals(type)  || "6".equals(type)){
//                    ClientManager.getClient().stopSearch();
//
//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().disconnect(m_nowMac);
//
//                    ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);
//
//                }else if("7".equals(type)){
//                    if (apiClient != null) {
//                        apiClient.onDestroy();
//                    }
//                }else{
//                    BaseApplication.getInstance().getIBLE().stopScan();
//                    BaseApplication.getInstance().getIBLE().refreshCache();
//                    BaseApplication.getInstance().getIBLE().close();
//                    BaseApplication.getInstance().getIBLE().disconnect();
//                }
//            }



                inputMethodManager.hideSoftInputFromWindow(bikeNumEdit.getWindowToken(), 0);

//                if (broadcastReceiver != null) {
//                    unregisterReceiver(broadcastReceiver);
//                    broadcastReceiver = null;
//                }
                m_myHandler.removeCallbacksAndMessages(null);
            }

        }catch (Exception e){
//            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
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

        surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
        mIvLight = (TextView) findViewById(R.id.top_mask);
        btnBikeNum = (TextView) findViewById(R.id.loca_show_btnBikeNum);
        mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
        scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        top_mask_bcg = (ImageView) findViewById(R.id.top_mask_bcg);
        mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);
        mCropLayout2 = (RelativeLayout) findViewById(R.id.capture_crop_layout2);
        ll_input = (LinearLayout) findViewById(R.id.ll_input);
//        mLlScanHelp = (LinearLayout) findViewById(R.id.ll_scan_help);

        iv_help = (LinearLayout) findViewById(R.id.ll_help);

        dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.pop_circles_menu, null);
        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);

        advDialog = new Dialog(context, R.style.Theme_AppCompat_Dialog);
        View advDialogView = LayoutInflater.from(context).inflate(R.layout.ui_adv_view2, null);
        advDialog.setContentView(advDialogView);
        advDialog.setCanceledOnTouchOutside(false);

        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
        customBuilder.setType(5).setTitle("温馨提示").setMessage("该车已被占用…");
        customDialog = customBuilder.create();
//        customDialog.show();

        cancelBtn = (ImageView)customDialog.findViewById(R.id.scan_cancelBtn);
        ll_positiveButton = (LinearLayout)customDialog.findViewById(R.id.ll_scan_positiveButton);
        cancelBtn.setOnClickListener(this);
        ll_positiveButton.setOnClickListener(this);


        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setType(9).setTitle("温馨提示").setMessage("该车正在维修中…");
        customDialog2 = customBuilder.create();
//        customDialog2.show();

        cancelBtn2 = (ImageView)customDialog2.findViewById(R.id.scan_cancelBtn2);
        ll_positiveButton2 = (LinearLayout)customDialog2.findViewById(R.id.ll_scan_positiveButton2);
        cancelBtn2.setOnClickListener(this);
        ll_positiveButton2.setOnClickListener(this);


        advCloseBtn = (TextView)advDialogView.findViewById(R.id.ui_adv_closeBtn0);
        advCloseBtn.setOnClickListener(this);

        bikeNumEdit = (EditText) findViewById(R.id.pop_circlesMenu_bikeNumEdit);
        positiveButton = (Button) dialogView.findViewById(R.id.pop_circlesMenu_positiveButton);
        negativeButton = (Button) dialogView.findViewById(R.id.pop_circlesMenu_negativeButton);
        positiveButton.setOnClickListener(this);
        negativeButton.setOnClickListener(this);
        findViewById(R.id.top_mask_bcg).setOnClickListener(this);
        findViewById(R.id.left_mask).setOnClickListener(this);
        findViewById(R.id.right_mask).setOnClickListener(this);
        findViewById(R.id.bottom_mask).setOnClickListener(this);

        bikeNumEdit.setKeyListener(new TextKeyListener(TextKeyListener.Capitalize.NONE, false) {
            @Override
//                public int getInputType() {
//                    优先弹出数字键盘
//                    return InputType.TYPE_CLASS_PHONE;
//                }

            public int getInputType() {
                //优先弹出字母键盘
//				return InputType.TYPE_MASK_CLASS;
                return InputType.TYPE_CLASS_NUMBER;
            }
        });

//        LoadingDialog2 lockLoading = new LoadingDialog2(this);
//        lockLoading.setCancelable(false);
//        lockLoading.setCanceledOnTouchOutside(false);
//
//        lockLoading.setTitle("正在连接");
//        lockLoading.show();
    }

    private void initHttp(){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token != null && !"".equals(access_token)){
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
            case R.id.top_mask_bcg:
            case R.id.left_mask:
            case R.id.right_mask:
            case R.id.bottom_mask:
                Log.e("onClick===1", "==="+advDialog.isShowing());
                isHand = false;
                btnBikeNum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.top_hand), null, null);

                previewing = true;
                initCamera(surfaceHolder);

                mCropLayout2.setVisibility(View.VISIBLE);
                ll_input.setVisibility(View.GONE);

                inputMethodManager.hideSoftInputFromWindow(bikeNumEdit.getWindowToken(), 0);
                break;

            case R.id.ui_adv_closeBtn:
                Log.e("onClick===", advDialog+"==="+advDialog.isShowing());

                if (advDialog != null && advDialog.isShowing()) {
                    advDialog.dismiss();
                }

                break;
            case R.id.scan_cancelBtn:
                Log.e("onClick===customDialog", customDialog+"==="+customDialog.isShowing());

                isHand = false;
                btnBikeNum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.top_hand), null, null);

                if (customDialog != null && customDialog.isShowing()) {
                    customDialog.dismiss();
                }

                mCropLayout2.setVisibility(View.VISIBLE);
                ll_input.setVisibility(View.GONE);

                initCamera(surfaceHolder);

                break;
            case R.id.ll_scan_positiveButton:
                Log.e("onClick=ll_positiveB", customDialog+"==="+customDialog.isShowing());

                isHand = false;
                btnBikeNum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.top_hand), null, null);

                if (customDialog != null && customDialog.isShowing()) {
                    customDialog.dismiss();
                }

                mCropLayout2.setVisibility(View.VISIBLE);
                ll_input.setVisibility(View.GONE);

                initCamera(surfaceHolder);

                break;
            case R.id.scan_cancelBtn2:
                Log.e("onClick===customDialog", customDialog+"==="+customDialog.isShowing());

                isHand = false;
                btnBikeNum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.top_hand), null, null);

                if (customDialog2 != null && customDialog2.isShowing()) {
                    customDialog2.dismiss();
                }

                mCropLayout2.setVisibility(View.VISIBLE);
                ll_input.setVisibility(View.GONE);

                initCamera(surfaceHolder);

                break;
            case R.id.ll_scan_positiveButton2:
                Log.e("onClick=ll_positiveB", customDialog+"==="+customDialog.isShowing());

                isHand = false;
                btnBikeNum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.top_hand), null, null);

                if (customDialog2 != null && customDialog2.isShowing()) {
                    customDialog2.dismiss();
                }

                mCropLayout2.setVisibility(View.VISIBLE);
                ll_input.setVisibility(View.GONE);

                initCamera(surfaceHolder);

                break;
            case R.id.pop_circlesMenu_positiveButton:
//                try {
                    String bikeNum = bikeNumEdit.getText().toString().trim();

                    Log.e("bikeNum===", "==="+bikeNum);



                    if (bikeNum == null || "".equals(bikeNum)) {

                        ToastUtil.showMessageApp(context, "请输入单车编号");

                        return;
                    }

                    mCropLayout2.setVisibility(View.VISIBLE);
                    ll_input.setVisibility(View.GONE);

                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏
                    if (dialog!=null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Tag = 1;
//                useBike(bikeNum);

//                    int n = 1/0;

                order_authority(bikeNum);
//                }catch (Exception e){
////                    ToastUtil.showMessageApp(context, "==="+e);
//                }


                break;

            case R.id.pop_circlesMenu_negativeButton:

                Log.e("bikeNum===2", "==="+type);

                mCropLayout2.setVisibility(View.VISIBLE);
                ll_input.setVisibility(View.GONE);

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

//    @Override
//    public void onWindowHidden() {
//        super.onWindowHidden();
//        print("on window hidden");
//    }
//
//    @Override
//    public void onFinishInput() {
//        super.onFinishInput();
//        print("on finishinput");
//    }

    private void initPermission() {
        //请求Camera权限 与 文件读写 权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
                    Log.e("handleDecode===", "==="+previewing);

                    if(!previewing) return;

                    if(previewing)
                    inactivityTimer.onActivity();

                    //扫描成功之后的振动与声音提示
                    if(previewing)
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
//        try {
//            String s = Utils.decompress(result);
//
//            Log.e("scan===useBike", "===" + s);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Log.e("scan===useBike", "===" + result);



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



        order_authority(result);

//        ebikeInfo(result);

//        useCar(result);
    }

    private void closeBtnBikeNum(){
        isHand = false;
        btnBikeNum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.top_hand), null, null);

        previewing = true;
        initCamera(surfaceHolder);

        mCropLayout2.setVisibility(View.VISIBLE);
        ll_input.setVisibility(View.GONE);
    }

    private void order_authority(final String tokencode) {
        Log.e("order_authority===", "==="+tokencode);

        RequestParams params = new RequestParams();
        params.put("car_number", URLEncoder.encode(tokencode));

        HttpHelper.get(this, Urls.order_authority, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {


                onFailureCommon(throwable.toString());

                closeBtnBikeNum();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            Log.e("order_authority===1", responseString + "====" + result.data);

                            if(result.getStatus_code()==200){
                                JSONObject jsonObject = new JSONObject(result.getData());

                                Log.e("order_authority===2",  "====" + jsonObject.getInt("code"));

                                int code = jsonObject.getInt("code");

                                if(code==0){
                                    car(tokencode);
//                                    customDialog2.show();

//                                    if (loadingDialog != null && loadingDialog.isShowing()) {
//                                        loadingDialog.dismiss();
//                                    }
                                }else if(code==1){
                                    customDialog2.show();

                                    if (loadingDialog != null && loadingDialog.isShowing()) {
                                        loadingDialog.dismiss();
                                    }

                                }else if(code==2){
                                    customDialog.show();

                                    if (loadingDialog != null && loadingDialog.isShowing()) {
                                        loadingDialog.dismiss();
                                    }

                                }
                            }else{
                                Log.e("order_authority===3",  "====" + result.getMessage());

                                ToastUtil.showMessageApp(context, result.getMessage());

                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                    loadingDialog.dismiss();
                                }

                                closeBtnBikeNum();

                            }


                        } catch (Exception e) {

                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                            closeBtnBikeNum();

//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });


            }
        });
    }

    private void car(String tokencode) {
        Log.e("scan===000", "car===" + tokencode);

        HttpHelper.get(this, Urls.car + URLEncoder.encode(tokencode), new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon(throwable.toString());
                closeBtnBikeNum();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            Log.e("scan===car", responseString + "===" + result.data);

                            CarBean bean = JSON.parseObject(result.getData(), CarBean.class);

                            Log.e("scan===car2", bean.getNumber()+"===" +bean.getCarmodel_id()+"===" + bean.getLock_mac());

                            carmodel_id = bean.getCarmodel_id();
                            type = ""+bean.getLock_id();
                            lock_no = bean.getLock_no();
                            bleid = bean.getLock_secretkey();
                            deviceuuid = bean.getVendor_lock_id();
                            codenum = bean.getNumber();
                            m_nowMac = bean.getLock_mac();
                            electricity = bean.getElectricity();
                            mileage = bean.getMileage();
                            carmodel_name = bean.getCarmodel_name();
                            each_free_time = bean.getEach_free_time();
                            today_free_times = bean.getToday_free_times();
                            first_price = bean.getFirst_price();
                            first_time = bean.getFirst_time();
                            continued_price = bean.getContinued_price();
                            continued_time = bean.getContinued_time();
                            credit_score_desc = bean.getCredit_score_desc();
                            allow_temporary_lock = bean.getAllow_temporary_lock();


                            String lock_secretkey = bean.getLock_secretkey();
                            String lock_password = bean.getLock_password();

                            if("9".equals(type) || "10".equals(type) || "12".equals(type)){
                                Config.key = hexStringToByteArray(lock_secretkey);
                                Config.password = hexStringToByteArray(lock_password);
                            }else if("2".equals(type) || "3".equals(type)){
                                Config.key = Config.key2;
                                Config.password = Config.password2;
                            }

//                            if("2".equals(type) || "3".equals(type)){
//                                n=0;
//                                macLoop();
//                            }else{
//
//                            }

                            Intent rIntent = new Intent();
                            rIntent.putExtra("codenum", codenum);
                            rIntent.putExtra("m_nowMac", m_nowMac);
                            rIntent.putExtra("carmodel_id", carmodel_id);
                            rIntent.putExtra("type", type);
                            rIntent.putExtra("lock_no", lock_no);
                            rIntent.putExtra("bleid", bleid);
                            rIntent.putExtra("deviceuuid", deviceuuid);
                            rIntent.putExtra("electricity", electricity);
                            rIntent.putExtra("mileage", mileage);
                            rIntent.putExtra("carmodel_name", carmodel_name);
                            rIntent.putExtra("each_free_time", each_free_time);
                            rIntent.putExtra("today_free_times", today_free_times);
                            rIntent.putExtra("first_price", first_price);
                            rIntent.putExtra("first_time", first_time);
                            rIntent.putExtra("continued_price", continued_price);
                            rIntent.putExtra("continued_time", continued_time);
                            rIntent.putExtra("credit_score_desc", credit_score_desc);
                            rIntent.putExtra("allow_temporary_lock", allow_temporary_lock);
                            rIntent.putExtra("isMac",false);


                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

//                            BleManager.getInstance().cancelScan();

                            setResult(RESULT_OK, rIntent);
                            scrollToFinishActivity();


//                            order(codenum);

//                            if (result.getFlag().equals("Success")) {
//                                if ("[]".equals(result.getData()) || 0 == result.getData().length()) {
//                                    useCar(tokencode);
//                                } else {
//                                    EbikeInfoBean bean = JSON.parseObject(result.getData(), EbikeInfoBean.class);
//
//                                    Log.e("scan===ebikeInfo2", bean.getElectricity().substring(0, bean.getElectricity().length() - 1) + "====");
//
//                                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(ActivityScanerCode.this);
//                                    customBuilder.setMessage("电单车必须在校内停车线还车");
//
//                                    customBuilder.setType(4).setElectricity(bean.getElectricity()).setMileage(bean.getMileage()).setFee(bean.getFee()).setTitle("这是一辆电单车").setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.cancel();
//                                            scrollToFinishActivity();
//                                        }
//                                    }).setPositiveButton("开锁", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.cancel();
//
//                                            useCar(tokencode);
//                                        }
//                                    }).setHint(false);
//                                    customBuilder.create().show();
//
//                                }
//                            } else {
//                                ToastUtil.showMessageApp(context, result.getMsg());
//                            }

                        } catch (Exception e) {
                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                            closeBtnBikeNum();
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });


            }
        });

    }

    public static byte[] hexStringToByteArray(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        Log.e("StringToByte===1", bytes+"==="+bytes[0]);


        return bytes;
    }





    private void order(final String tokencode) {
        Log.e("order===", "===");

        RequestParams params = new RequestParams();
        params.put("order_type", 1);
        params.put("car_number", URLEncoder.encode(tokencode));

        HttpHelper.post(this, Urls.order, params, new TextHttpResponseHandler() {
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

                            Log.e("order===1", responseString + "====" + result.data);


                            JSONObject jsonObject = new JSONObject(result.getData());

                            Log.e("order_authority===2",  "====" + jsonObject.getString("order_sn"));

                            oid = jsonObject.getString("order_sn");

//
//                            if("0".equals(jsonObject.getString("code"))){
//                                car(tokencode);
//                            }

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
                            }else if ("3".equals(type)) {    //单车3合1锁

                                unlock();

//                                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                                    scrollToFinishActivity();
//                                }
//                                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//                                mBluetoothAdapter = bluetoothManager.getAdapter();
//
//                                if (mBluetoothAdapter == null) {
//                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                                    scrollToFinishActivity();
//                                    return;
//                                }
//                                if (!mBluetoothAdapter.isEnabled()) {
//                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                                    startActivityForResult(enableBtIntent, 188);
//                                } else {
//                                    if (!TextUtils.isEmpty(m_nowMac)) {
//                                        Log.e("scan===3_1", "==="+m_nowMac);
//
//                                        connect();
//                                    }
//                                }

                            } else if ("4".equals(type)) {

//                                unlock();

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

                                    Log.e("scan===4_1", bleid + "==="+m_nowMac);

                                    bleService.connect(m_nowMac);

                                    checkConnect();

                                }

                            }else if ("5".equals(type) || "6".equals(type)) {      //泺平单车蓝牙锁

                                if(BaseApplication.getInstance().isTest()){
                                    if("40001101".equals(codenum)){
//                                                  m_nowMac = "3C:A3:08:AE:BE:24";
                                        m_nowMac = "3C:A3:08:CD:9F:47";
                                    }else if("50007528".equals(codenum)){

                                    }else{
                                        type = "6";
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

                                    iv_help.setVisibility(View.VISIBLE);

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        ClientManager.getClient().stopSearch();
                                                        ClientManager.getClient().disconnect(m_nowMac);
                                                        ClientManager.getClient().disconnect(m_nowMac);
                                                        ClientManager.getClient().disconnect(m_nowMac);
                                                        ClientManager.getClient().disconnect(m_nowMac);
                                                        ClientManager.getClient().disconnect(m_nowMac);
                                                        ClientManager.getClient().disconnect(m_nowMac);
                                                        ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);

                                                        SearchRequest request = new SearchRequest.Builder()      //duration为0时无限扫描
                                                                .searchBluetoothLeDevice(0)
                                                                .build();

                                                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                            Log.e("usecar===1", "===");

                                                            return;
                                                        }

                                                        Log.e("usecar===2", "===");

//                                                                ClientManager.getClient().stopSearch();
                                                        m_myHandler.sendEmptyMessage(0x98);
                                                        ClientManager.getClient().search(request, mSearchResponse);
                                                    }
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();

                                }
                            }else if ("7".equals(type)) {

//                                unlock();

//                                "<p style=\"color: #666666; font-size: 16px;\">1\u5c0f\u65f6\u5185\u514d\u8d39\uff0c\u8d85\u8fc71\u5c0f\u65f6<span style=\"color: #FF0000;\">\uffe5<span style=\"font-size: 24px;\">1.00<\/span><\/span>\/30\u5206\u949f<\/p>"

                                Log.e("scan===7_1", deviceuuid + "==="+m_nowMac);

//                                XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
//                                builder.setBleStateChangeListener(ActivityScanerCode.this);
//                                builder.setScanResultCallback(ActivityScanerCode.this);
//                                apiClient = builder.build();
//
//                                ActivityScanerCodePermissionsDispatcher.connectDeviceWithPermissionCheck(ActivityScanerCode.this, deviceuuid);
//
//                                m_myHandler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (!isConnect){
//                                            if (loadingDialog != null && loadingDialog.isShowing()) {
//                                                loadingDialog.dismiss();
//                                            }
//
////                                          closeEbike();
//
//                                            if(!isFinishing()){
////                                                tzEnd();
////                                                car_notification(0, 1, 0, 1,1,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
//
//                                                car_notification(1, 2, 2, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
//
//                                            }
//                                        }
//                                    }
//                                }, 15 * 1000);



//                                if ("200".equals(jsonObject.getString("code"))) {
//                                    Log.e("useBike===4", "====" + jsonObject);
//
//                                    ToastUtil.showMessageApp(context, "恭喜您,开锁成功!");
//
//                                    oid = jsonObject.getString("orderid");
//                                    memberEvent();
//
//                                    if(!isFinishing()){
//                                        tzEnd();
//                                    }
//
//                                } else {
//
////                                            if (loadingDialog != null && loadingDialog.isShowing()){
////                                                loadingDialog.dismiss();
////                                            }
//
////                                            XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
////                                            builder.setBleStateChangeListener(ActivityScanerCode.this);
////                                            builder.setScanResultCallback(ActivityScanerCode.this);
////                                            apiClient = builder.build();
////
////                                            MainActivityPermissionsDispatcher.connectDeviceWithPermissionCheck(ActivityScanerCode.this, deviceuuid);
//
//
//                                    getCurrentorder2(uid, access_token);
//
//                                    if (loadingDialog != null && !loadingDialog.isShowing()) {
//                                        loadingDialog.setTitle("开锁中");
//                                        loadingDialog.show();
//                                    }
//                                }
                            }

//                            BaseApplication.getInstance().getIBLE().openLock();

//                            CarBean bean = JSON.parseObject(result.getData(), CarBean.class);


                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }
                });


            }
        });
    }

    private void cycling() {
        Log.e("scan===cycling", "===");

        HttpHelper.get(this, Urls.cycling, new TextHttpResponseHandler() {
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

                            Log.e("scan===cycling1", responseString + "===" + result.data);

                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);

                            if(null != bean.getOrder_sn()){


                                Log.e("scan===cycling2", bean.getOrder_sn()+"===" + bean.getCar_number()+"===" + bean.getLock_id());

                                oid = bean.getOrder_sn();
                                type = ""+bean.getLock_id();

                                if(!"".equals(oid)){

                                    if("4".equals(type) || "7".equals(type)){
                                        lock();
                                    }else{
//                                        car_notification2();
                                        car_notification(1, 3, 0, 3,1,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
                                    }

                                }
                            }


//                            bleid = jsonObject.getString("bleid");
//                            if("7".equals(type)){
//                                deviceuuid = jsonObject.getString("deviceuuid");
//                            }
//                            type = ""+bean.getCarmodel_id();
//
//                            codenum = bean.getNumber();
//                            m_nowMac = bean.getLock_mac();
//
//                            order(codenum);


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

    private void car_notification2() {
        Log.e("car_notification===", "==="+Md5Helper.encode(oid+":lock_status:"+3));

        RequestParams params = new RequestParams();
        params.put("scene", 1); //场景值 必传 1借还车上报 2再次开(关)锁上报
        params.put("lock_status", Md5Helper.encode(oid+":lock_status:"+3));    //车锁状态 1开锁成功 2开锁失败 3上锁成功 4上锁失败【上传时order_sn:拼接上lock_status:再拼接车锁状态码md5加密后上传，例如md5('191004143208756404:lock_status:1')】
        params.put("failure_code", 0);   //0代表成功 1连接不上蓝牙 2蓝牙开锁超时 3网络开锁请求失败(接口无响应或异常) 4网络开锁超时（接口有响应但返回超时码） 5网络开锁失败
        params.put("failure_desc", "");
        params.put("parking", "");
        params.put("longitude", "");
        params.put("latitude", "");
        params.put("report_type", Md5Helper.encode(oid+":report_type:"+3));     //1蓝牙开锁 2网络开锁 3蓝牙上锁 4网络上锁 【需加密，加密方式同上，例如md5('191004143208756404:report_type:1')】
        params.put("back_type", Md5Helper.encode(oid+":back_type:"+1));      //1手机gps在电子围栏 2锁gps在电子围栏 3信标 4锁与信标【需加密，加密方式同上，例如md5('191004143208756404:back_type:1')】

        HttpHelper.post(this, Urls.car_notification, params, new TextHttpResponseHandler() {
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

                            Log.e("car_notification===1", responseString + "====" + result.data);

//                            JSONObject jsonObject = new JSONObject(result.getData());
//
//                            Log.e("order_authority===2",  "====" + jsonObject.getString("code"));
//
//                            if("0".equals(jsonObject.getString("code"))){
//                                car(tokencode);
//                            }

//                            if ("1".equals(type)) {          //单车机械锁
//                                UIHelper.goToAct(context, CurRoadStartActivity.class);
//                                scrollToFinishActivity();
//                            } else if ("2".equals(type)) {    //单车蓝牙锁
//
//                                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                                    scrollToFinishActivity();
//                                }
//                                //蓝牙锁
//                                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//
//                                mBluetoothAdapter = bluetoothManager.getAdapter();
//
//                                if (mBluetoothAdapter == null) {
//                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                                    scrollToFinishActivity();
//                                    return;
//                                }
//                                if (!mBluetoothAdapter.isEnabled()) {
//                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                                    startActivityForResult(enableBtIntent, 188);
//                                } else {
//                                    if (loadingDialog != null && loadingDialog.isShowing()) {
//                                        loadingDialog.dismiss();
//                                    }
//
//                                    if (loadingDialog != null && !loadingDialog.isShowing()) {
//                                        loadingDialog.setTitle("正在唤醒车锁");
//                                        loadingDialog.show();
//                                    }
//
//                                    if (!TextUtils.isEmpty(m_nowMac)) {
//                                        connect();
//                                    }
//                                }
//                            }

//                            BaseApplication.getInstance().getIBLE().openLock();

//                            CarBean bean = JSON.parseObject(result.getData(), CarBean.class);


                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }
                });


            }
        });
    }

    private void lock() {
        Log.e("scan===lock", "===");

        HttpHelper.post(this, Urls.lock, null, new TextHttpResponseHandler() {
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

                            Log.e("scan===lock1", responseString + "===" + result.data);

                            car_notification(1, 3, 0, 4,1,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));


//                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);
//
//                            Log.e("scan===cycling2", bean.getOrder_sn()+"===" + bean.getCar_number());
//
//                            oid = bean.getOrder_sn();

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

    private void unlock() {
        Log.e("scan===unlock", "===");

        HttpHelper.post(this, Urls.unlock, null, new TextHttpResponseHandler() {
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

                            Log.e("scan===unlock1", responseString + "===" + result.data);

                            carLoop();

//                            car_notification();

//                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);
//
//                            Log.e("scan===cycling2", bean.getOrder_sn()+"===" + bean.getCar_number());
//
//                            oid = bean.getOrder_sn();

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

    private void carLoop() {
        Log.e("scan===carLoop", "===" + codenum);

        HttpHelper.get(this, Urls.car + URLEncoder.encode(codenum), new TextHttpResponseHandler() {
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

                            Log.e("scan===carLoop1", responseString + "===" + result.data);

                            CarBean bean = JSON.parseObject(result.getData(), CarBean.class);

                            Log.e("scan===carLoop2", bean.getNumber()+"===" + bean.getLock_status());

                            if(2 != bean.getLock_status()){
                                queryCarStatus();
                            }else{
                                car_notification(1, 1, 0, 2,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

//                                car_notification(1, 2, 4, 2,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));


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

    private void queryCarStatus() {
        if(n<5){
            n++;

            m_myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("queryCarStatus===", "===");

                    carLoop();

                }
            }, 1 * 1000);
        }else{
            ToastUtil.showMessageApp(context, "开锁失败");

//            car_notification();

            car_notification(1, 2, 4, 2,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

        }

    }

    private void car_notification(int scene, int lock_status, int failure_code, int report_type, int back_type, String failure_desc, String parking, String longitude, String latitude) {
        Log.e("car_notification===", oid+"==="+Md5Helper.encode(oid+":lock_status:"+lock_status));

        RequestParams params = new RequestParams();
        params.put("scene", scene); //场景值 必传 1借还车上报 2再次开(关)锁上报
        params.put("lock_status", Md5Helper.encode(oid+":lock_status:"+lock_status));    //车锁状态 1开锁成功 2开锁失败 3上锁成功 4上锁失败【上传时order_sn:拼接上lock_status:再拼接车锁状态码md5加密后上传，例如md5('191004143208756404:lock_status:1')】
        params.put("failure_code", failure_code);   //0代表成功 1连接不上蓝牙 2蓝牙开锁超时 3网络开锁请求失败(接口无响应或异常) 4网络开锁超时（接口有响应但返回超时码） 5网络开锁失败
        params.put("failure_desc", failure_desc);
        params.put("parking", parking);
        params.put("longitude", longitude);
        params.put("latitude", latitude);
        params.put("report_type", Md5Helper.encode(oid+":report_type:"+report_type));     //1蓝牙开锁 2网络开锁 3蓝牙上锁 4网络上锁 【需加密，加密方式同上，例如md5('191004143208756404:report_type:1')】
        if(back_type!=0){
            params.put("back_type", Md5Helper.encode(oid+":back_type:"+back_type));      //1手机gps在电子围栏 2锁gps在电子围栏 3信标 4锁与信标【需加密，加密方式同上，例如md5('191004143208756404:back_type:1')】
        }

        HttpHelper.post(this, Urls.car_notification, params, new TextHttpResponseHandler() {
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

                            Log.e("car_notification===1", responseString + "====" + result.data);

//                            JSONObject jsonObject = new JSONObject(result.getData());
//
//                            Log.e("order_authority===2",  "====" + jsonObject.getString("code"));
//
//                            if("0".equals(jsonObject.getString("code"))){
//                                car(tokencode);
//                            }

//                            if ("1".equals(type)) {          //单车机械锁
//                                UIHelper.goToAct(context, CurRoadStartActivity.class);
//                                scrollToFinishActivity();
//                            } else if ("2".equals(type)) {    //单车蓝牙锁
//
//                                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                                    scrollToFinishActivity();
//                                }
//                                //蓝牙锁
//                                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//
//                                mBluetoothAdapter = bluetoothManager.getAdapter();
//
//                                if (mBluetoothAdapter == null) {
//                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                                    scrollToFinishActivity();
//                                    return;
//                                }
//                                if (!mBluetoothAdapter.isEnabled()) {
//                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                                    startActivityForResult(enableBtIntent, 188);
//                                } else {
//                                    if (loadingDialog != null && loadingDialog.isShowing()) {
//                                        loadingDialog.dismiss();
//                                    }
//
//                                    if (loadingDialog != null && !loadingDialog.isShowing()) {
//                                        loadingDialog.setTitle("正在唤醒车锁");
//                                        loadingDialog.show();
//                                    }
//
//                                    if (!TextUtils.isEmpty(m_nowMac)) {
//                                        connect();
//                                    }
//                                }
//                            }

//                            BaseApplication.getInstance().getIBLE().openLock();

//                            CarBean bean = JSON.parseObject(result.getData(), CarBean.class);


                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }
                });


            }
        });
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
        if (access_token == null || "".equals(access_token)) {
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
                                    } else if ("3".equals(type)) {    //单车3合1锁

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
                                    } else if ("4".equals(type)) {   //行运兔电单车3合1锁

                                        if ("200".equals(jsonObject.getString("code"))) {
                                            Log.e("useBike===4", "====" + jsonObject);

                                            ToastUtil.showMessageApp(context, "恭喜您,开锁成功!");

                                            oid = jsonObject.getString("orderid");
                                            memberEvent();

                                            if(!isFinishing()){
                                                tzEnd();
                                            }

                                        } else {

                                            getCurrentorder2(uid, access_token);

                                            if (loadingDialog != null && !loadingDialog.isShowing()) {
                                                loadingDialog.setTitle("开锁中");
                                                loadingDialog.show();
                                            }
                                        }

                                    } else if ("5".equals(type) || "6".equals(type)) {      //泺平单车蓝牙锁

                                        if(BaseApplication.getInstance().isTest()){
                                            if("40001101".equals(codenum)){
//                                                  m_nowMac = "3C:A3:08:AE:BE:24";
                                                m_nowMac = "3C:A3:08:CD:9F:47";
                                            }else if("50007528".equals(codenum)){

                                            }else{
                                                type = "6";
                                                m_nowMac = "A4:34:F1:7B:BF:9A";
                                            }

//                                              m_nowMac = "A4:34:F1:7B:BF:31";
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

                                            iv_help.setVisibility(View.VISIBLE);

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {

                                                                ClientManager.getClient().stopSearch();
                                                                ClientManager.getClient().disconnect(m_nowMac);
                                                                ClientManager.getClient().disconnect(m_nowMac);
                                                                ClientManager.getClient().disconnect(m_nowMac);
                                                                ClientManager.getClient().disconnect(m_nowMac);
                                                                ClientManager.getClient().disconnect(m_nowMac);
                                                                ClientManager.getClient().disconnect(m_nowMac);
                                                                ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);

                                                                SearchRequest request = new SearchRequest.Builder()      //duration为0时无限扫描
                                                                        .searchBluetoothLeDevice(0)
                                                                        .build();

                                                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                                    Log.e("usecar===1", "===");

                                                                    return;
                                                                }

                                                                Log.e("usecar===2", "===");

//                                                                ClientManager.getClient().stopSearch();
                                                                m_myHandler.sendEmptyMessage(0x98);
                                                                ClientManager.getClient().search(request, mSearchResponse);
                                                            }
                                                        });
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }).start();

                                        }
                                    }else if ("7".equals(type)) {

                                        if ("200".equals(jsonObject.getString("code"))) {
                                            Log.e("useBike===4", "====" + jsonObject);

                                            ToastUtil.showMessageApp(context, "恭喜您,开锁成功!");

                                            oid = jsonObject.getString("orderid");
                                            memberEvent();

                                            if(!isFinishing()){
                                                tzEnd();
                                            }

                                        } else {

//                                            if (loadingDialog != null && loadingDialog.isShowing()){
//                                                loadingDialog.dismiss();
//                                            }

//                                            XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
//                                            builder.setBleStateChangeListener(ActivityScanerCode.this);
//                                            builder.setScanResultCallback(ActivityScanerCode.this);
//                                            apiClient = builder.build();
//
//                                            MainActivityPermissionsDispatcher.connectDeviceWithPermissionCheck(ActivityScanerCode.this, deviceuuid);


                                            getCurrentorder2(uid, access_token);

                                            if (loadingDialog != null && !loadingDialog.isShowing()) {
                                                loadingDialog.setTitle("开锁中");
                                                loadingDialog.show();
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



    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            Log.e("scan===","DeviceListActivity.onSearchStarted");
//            mDevices.clear();
//            mAdapter.notifyDataChanged();
        }

        @Override
        public void onDeviceFounded(final SearchResult device) {

            Log.e("scan===onDeviceFounded",device.device.getName() + "===" + device.device.getAddress());

//            bike:GpDTxe8DGN412
//            bike:LUPFKsrUyR405
//            bike:LUPFKsrUyK405
//            bike:L6OsRAiviK289===E8:EB:11:02:2B:E2

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(m_nowMac.equals(device.device.getAddress())){

                        Log.e("scan===stop",device.device.getName() + "===" + device.device.getAddress());

                        ClientManager.getClient().stopSearch();

                        connectDeviceLP();

                        ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
                    }
                }
            });

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


//            ClientManager.getClient().disconnect(m_nowMac);
//            ClientManager.getClient().disconnect(m_nowMac);
//            ClientManager.getClient().disconnect(m_nowMac);
//            ClientManager.getClient().disconnect(m_nowMac);
//            ClientManager.getClient().disconnect(m_nowMac);
//            ClientManager.getClient().disconnect(m_nowMac);
//            ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
//            ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);

            Log.e("ConnectStatus===", mac+"===="+(status == STATUS_CONNECTED));


            if(status != STATUS_CONNECTED){
                return;
            }

            ClientManager.getClient().stopSearch();

            ClientManager.getClient().getStatus(mac, new IGetStatusResponse() {
                @Override
                public void onResponseSuccess(String version, String keySerial, String macKey, String vol) {
//                    queryStatusServer(version, keySerial, macKey, vol);

                    quantity = vol+"";

                    Log.e("getStatus===", "===="+macKey);
                    keySource = keySerial;

                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("scan===", "scan====1");



//                                    getBleRecord();
                            rent();

                            if (loadingDialog != null && !loadingDialog.isShowing()) {
                                loadingDialog.setTitle("开锁中");
                                loadingDialog.show();
                            }

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
//                                    if("5".equals(type)  || "6".equals(type)){
//                                        ClientManager.getClient().stopSearch();
//                                        ClientManager.getClient().disconnect(m_nowMac);
//                                        ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);
//
//                                    }else{
//                                        BaseApplication.getInstance().getIBLE().refreshCache();
//                                        BaseApplication.getInstance().getIBLE().close();
//                                        BaseApplication.getInstance().getIBLE().disconnect();
//                                    }
//
//                                    scrollToFinishActivity();
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

                        }
                    });



                }

                @Override
                public void onResponseFail(final int code) {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("getStatus===", Code.toString(code));
//                            ToastUtil.showMessageApp(context, Code.toString(code));
                        }
                    });


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


    protected void rent(){

        Log.e("rent===000",m_nowMac+"==="+keySource);

        RequestParams params = new RequestParams();
        params.put("lock_mac", m_nowMac);
        params.put("keySource",keySource);
        HttpHelper.get(this, Urls.rent, params, new TextHttpResponseHandler() {
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
                            Log.e("rent===","==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            KeyBean bean = JSON.parseObject(result.getData(), KeyBean.class);

                            encryptionKey = bean.getEncryptionKey();
                            keys = bean.getKeys();
                            serverTime = bean.getServerTime();

                            Log.e("rent===", m_nowMac+"==="+encryptionKey+"==="+keys);

//                                getBleRecord();

                            iv_help.setVisibility(View.GONE);
                            openBleLock(null);


//                            if (result.getFlag().equals("Success")) {
//
////                                getCurrentorder2(uid, access_token);
//
//                            }else {
//                                ToastUtil.showMessageApp(context, result.getMsg());
//                            }
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


    private void rent2(){
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

//        if("3C:A3:08:AE:BE:24".equals(m_nowMac)){
//            secretKey = "eralHInialHInitwokPs5138m9pleBLEPeri4herzat4on8L0P1functP1functi7onSim9pInit2ali"; //40001101==="3C:A3:08:AE:BE:24";
//        }else{
//            secretKey = "NDQzMDMyMzYzOTM5MzkzMzQxNDQzMzQxMzMzOTMyMzE0NTM3NDEzMzQzMzU0NTM4MzYzMDM1Mzk0MzAw";  // "A4:34:F1:7B:BF:9A";
//        }

//        secretKey = Constants.keyMap.get(m_nowMac);
        secretKey = "MzkzNDM3MzQ0MzQxMzMzNDMzNDI0MjMyNDQ0NjM2NDE0MjM3MzMzMjM1NDE0NTQ1MzM0NDM4Mzk0MzAw";

//        String secretKey = "NDQzMDMyMzYzOTM5MzkzMzQxNDQzMzQxMzMzOTMyMzE0NTM3NDEzMzQzMzU0NTM4MzYzMDM1Mzk0MzAw";
//        String secretKey = "eralHInialHInitwokPs5138m9pleBLEPeri4herzat4on8L0P1functP1functi7onSim9pInit2ali";

        //AES加解密密钥
        String pwd = null;

        Random random = new Random();
        //随机数取80位密钥中，前64位的某一位
        encryptionKey = random.nextInt(secretKey.length()-16);

//        encryptionKey = 39;
//
//        keySource = "8A090B0A";

//        Log.e("rent===000", keySource+"==="+secretKey+"==="+encryptionKey+"==="+keys+"==="+pwd);

        //从随机数位数开始截取16位字符作为AES加解密密钥
        pwd = secretKey.substring(encryptionKey, encryptionKey+16);
        encryptionKey = encryptionKey + 128;

        encryptionKey = 183;

        //补8个0，AES加密需要16位数据
        keySource = keySource.toUpperCase()+"00000000";
        keySource = "8596885D00000000";
        pwd = "1NDE0NTQ1MzM0NDM";
        //加密
        byte[] encryptResultStr = AESUtil.encrypt(keySource.getBytes(), pwd);
        //转成hexString
        keys = AESUtil.bytesToHexString(encryptResultStr).toUpperCase();
        //服务器时间戳，精确到秒，用于锁同步时间
        serverTime = Calendar.getInstance().getTimeInMillis()/1000;
        serverTime = 1569232507;

        Log.e("rent2===", serverTime+"==="+keySource+"==="+secretKey+"==="+encryptionKey+"==="+encryptResultStr.length+"==="+ new String(encryptResultStr)+"==="+keys+"==="+pwd);

        openBleLock(null);
//        getCurrentorder2(uid, access_token);

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
                    public void onResponseFail(final int code) {
                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("openLock===Fail", m_nowMac+"==="+Code.toString(code));
//                              UIHelper.dismiss();
//                              UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));

                                getBleRecord();
//                                deleteBleRecord(null);

                                if("锁已开".equals(Code.toString(code))){
                                    if (loadingDialog != null && loadingDialog.isShowing()){
                                        loadingDialog.dismiss();
                                    }

                                    isFinish = true;

                                    ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

                                    if(!isFinishing()){
//                                        tzEnd();
                                        car_notification(1, 1, 0, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
                                    }
                                }else{
//                                    submit(uid, access_token);

                                    car_notification(1, 2, 1, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

                                }


//                                ToastUtil.showMessageApp(context, Code.toString(code));
                            }
                        });



//                        submit(uid, access_token);
                    }

                    @Override
                    public void onResponseSuccess() {
//                        UIHelper.dismiss();

                        Log.e("openLock===Success", "===");

                        getBleRecord();

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

        Log.e("getBleRecord===", "###==="+m_nowMac);

        ClientManager.getClient().getRecord(m_nowMac, new IGetRecordResponse() {

            @Override
            public void onResponseSuccess(String phone, String bikeTradeNo, String timestamp, String transType, String mackey, String index, int Major, int Minor, String vol) {
                Log.e("getBleRecord===0", transType + "==Major:"+ Major +"---Minor:"+Minor+"==="+bikeTradeNo);
                deleteBleRecord(bikeTradeNo);

//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
//
//                ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");
//
//                SharedPreferencesUrls.getInstance().putBoolean("isStop",false);
//                SharedPreferencesUrls.getInstance().putString("m_nowMac", m_nowMac);
//                SharedPreferencesUrls.getInstance().putBoolean("switcher", false);
//                SharedPreferencesUrls.getInstance().putString("type", type);
//                SharedPreferencesUrls.getInstance().putString("tempStat","0");
//                SharedPreferencesUrls.getInstance().putString("bleid",bleid);
//
//                UIHelper.goToAct(context, CurRoadBikingActivity.class);
//                scrollToFinishActivity();
            }

            @Override
            public void onResponseSuccessEmpty() {
//                ToastUtil.showMessageApp(context, "record empty");
                Log.e("getBleRecord===1", "Success===Empty");
            }

            @Override
            public void onResponseFail(int code) {
                Log.e("getBleRecord===2", Code.toString(code));
//                ToastUtil.showMessageApp(context, Code.toString(code));
            }
        });
    }

    //与设备，删除记录
    private void deleteBleRecord(String tradeNo) {
//        UIHelper.showProgress(this, R.string.delete_bike_record);
        ClientManager.getClient().deleteRecord(m_nowMac, tradeNo, new IGetRecordResponse() {
//            @Override
//            public void onResponseSuccess(String phone, String bikeTradeNo, String timestamp, String transType, String mackey, String index, String cap, String vol) {
//                Log.e("scan===deleteBleRecord", "Success===");
//                deleteBleRecord(bikeTradeNo);
//            }

            @Override
            public void onResponseSuccess(String phone, String bikeTradeNo, String timestamp, String transType, String mackey, String index, int Major, int Minor, String vol) {
                Log.e("biking=deleteBleRecord", "Major:"+ Major +"---Minor:"+Minor);
                deleteBleRecord(bikeTradeNo);
            }

            @Override
            public void onResponseSuccessEmpty() {
//                UIHelper.dismiss();
                Log.e("scan===deleteBleRecord", "Success===Empty");

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }

                        isFinish = true;

                        ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

                        if(!isFinishing()){
//                            tzEnd();
                            car_notification(1, 1, 0, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
                        }
                    }
                });

            }

            @Override
            public void onResponseFail(int code) {
                Log.e("scan===deleteBleRecord", Code.toString(code));
//                ToastUtil.showMessageApp(context, Code.toString(code));
            }
        });
    }


    //连接设备
    private void connectDeviceLP() {
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
                isStop = false;

                Log.e("connectDevice===", "Fail==="+Code.toString(code));
//                ToastUtil.showMessageApp(context, Code.toString(code));
            }

            @Override
            public void onResponseSuccess(BleGattProfile profile) {
//                BluetoothLog.v(String.format("profile:\n%s", profile));
//                refreshData(true);

                isStop = true;

                Log.e("connectDevice===", "Success==="+profile);

//                if (Globals.bType == 1) {
//                    ToastUtil.showMessageApp(context, "正在关锁中");
////                    getBleRecord();
//                }
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
//                        Toast.makeText(context,"扫码唤醒失败，重启手机蓝牙换辆车试试吧！",Toast.LENGTH_LONG).show();
//                        scrollToFinishActivity();

//                        closeEbike();
//                        submit(uid, access_token);
//                        if(!isFinishing()){
//                            tzEnd();
//                        }

                        if("".equals(oid)){
                            memberEvent2();

                            Toast.makeText(context,"扫码唤醒失败，重启手机蓝牙换辆车试试吧！",Toast.LENGTH_LONG).show();
                            scrollToFinishActivity();
                        }else{
                            if(!isFinishing()){
                                tzEnd();
                            }
                        }
                    }

                }else{
//                    getCurrentorder2(uid, access_token);

                    bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});

                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("checkConnect===3", tel+"==="+bleid+"==="+bleService+"==="+m_nowMac);

                            button8();
                            button9();
                            button3();

                            m_myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("checkConnect===4", bleService.cc+"==="+"B1 25 80 00 00 56 ".equals(bleService.cc));

                                    if("B1 25 80 00 00 56 ".equals(bleService.cc)){
                                        if (loadingDialog != null && loadingDialog.isShowing()){
                                            loadingDialog.dismiss();
                                        }

                                        Log.e("checkConnect===5", oid+"==="+bleService.cc);
                                        ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");
                                        Log.e("scan===", "OPEN_ACTION===="+isOpen);

                                        if(!isFinishing()){
//                                            tzEnd();
                                            car_notification(1, 1, 0, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
                                        }
                                    }else{
//                                        closeEbike();
                                        if(!isFinishing()){
//                                            tzEnd();

                                            car_notification(1, 2, 1, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

                                        }
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

        Log.e("188===", isPermission+"==="+requestCode+"==="+resultCode);

        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case 288:{
                    break;
                }
                case 188:{
                    if (null != loadingDialog && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }

                    isPermission = true;

//                    if(surfaceHolder==null){
//                        surfaceHolder = surfaceView.getHolder();
//
//                        Log.e("surface===0_1", isPermission+"==="+hasSurface+"==="+surfaceHolder);
//
//                        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
//                            @Override
//                            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//                            }
//
//                            @Override
//                            public void surfaceCreated(SurfaceHolder holder) {
//                                Log.e("surface===1", "==="+hasSurface);
//
//                                if (!hasSurface) {
//                                    hasSurface = true;
//
//                                    initCamera(holder);
//
//                                    BleManager.getInstance().init(getApplication());
//                                    BleManager.getInstance()
//                                            .enableLog(true)
//                                            .setReConnectCount(10, 5000)
//                                            .setConnectOverTime(20000)
//                                            .setOperateTimeout(10000);
//
//                                    setScanRule();
//                                    scan();
//
//                                    Log.e("asc===onCreate", "==="+mCamera);
//                                }
//                            }
//
//                            @Override
//                            public void surfaceDestroyed(SurfaceHolder holder) {
//                                Log.e("surface===2", "==="+hasSurface);
//
//                                hasSurface = false;
//
//                                if (loadingDialog != null && loadingDialog.isShowing()) {
//                                    loadingDialog.dismiss();
//                                }
//                            }
//                        });
//                        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//                    }

//                    surfaceHolder.addCallback(new SurfaceHolder.Callback() {
//                        @Override
//                        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//                        }
//
//                        @Override
//                        public void surfaceCreated(SurfaceHolder holder) {
//                            Log.e("surface===1", "==="+hasSurface);
//
//                            if (!hasSurface) {
//                                hasSurface = true;
//
//                                previewing = true;
//                                initCamera(holder);
//                                mCropLayout2.setVisibility(View.VISIBLE);
//
//                                BleManager.getInstance().init(getApplication());
//                                BleManager.getInstance()
//                                        .enableLog(true)
//                                        .setReConnectCount(10, 5000)
//                                        .setConnectOverTime(20000)
//                                        .setOperateTimeout(10000);
//
//                                setScanRule();
//                                scan();
//
//                                Log.e("asc===onCreate", "==="+mCamera);
//                            }
//                        }
//
//                        @Override
//                        public void surfaceDestroyed(SurfaceHolder holder) {
//                            Log.e("surface===2", "==="+hasSurface);
//
//                            hasSurface = false;
//
//                            if (loadingDialog != null && loadingDialog.isShowing()) {
//                                loadingDialog.dismiss();
//                            }
//                        }
//                    });
//                    surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

                    Log.e("188===1", m_nowMac+"==="+type+"==="+deviceuuid+"==="+hasSurface+"==="+surfaceHolder);

                    previewing = true;
                    initCamera(surfaceHolder);
                    mCropLayout2.setVisibility(View.VISIBLE);

                    BleManager.getInstance().init(getApplication());
                    BleManager.getInstance()
                            .enableLog(true)
                            .setReConnectCount(10, 5000)
                            .setConnectOverTime(20000)
                            .setOperateTimeout(10000);

                    setScanRule();
                    scan();


                    break;
                }
                default:{
                    break;
                }
            }
        }else if( requestCode == 188){
            ToastUtil.showMessageApp(this, "需要打开蓝牙");

            if("".equals(oid)){
//                setResult(RESULT_OK);
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

        Log.e("onKeyDown===", "==="+keyCode);

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            inputMethodManager.hideSoftInputFromWindow(bikeNumEdit.getWindowToken(), 0);

            if(isHide){
                isHide = false;

                isHand = false;
                btnBikeNum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.top_hand), null, null);

                previewing = true;
                initCamera(surfaceHolder);

                mCropLayout2.setVisibility(View.VISIBLE);
                ll_input.setVisibility(View.GONE);


            }else{
                finishMine();
            }

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

        Log.e("onStop===", "===");

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
//                BaseApplication.getInstance().getIBLE().openLock();
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

//                            getCurrentorder2(uid, access_token);

                            BaseApplication.getInstance().getIBLE().openLock();

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

//                                submit(uid, access_token);

                                car_notification(1, 2, 1, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));


//                                scrollToFinishActivity();
                            } else {
                                isOpen = true;

                                ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");
                                Log.e("scan===", "OPEN_ACTION===="+isOpen);

                                isFinish = true;



                                if(!isFinishing()){
//                                    tzEnd();

//                                    car_notification();
                                    car_notification(1, 1, 0, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
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
//        RequestParams params = new RequestParams();
//        try {
//            Log.e("scan===memberEvent0", new Build().MANUFACTURER.toUpperCase()+"==="+new Build().MODEL+"==="+Build.VERSION.RELEASE+"==="+getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
//
//            params.put("uid", uid);
//            params.put("access_token", access_token);
//            params.put("phone_brand", new Build().MANUFACTURER.toUpperCase());
//            params.put("phone_model", new Build().MODEL);
//            params.put("phone_system", "Android");
//            params.put("phone_system_version", Build.VERSION.RELEASE);     //手机系统版本 必传 如：13.1.2
//            params.put("app_version", getPackageManager().getPackageInfo(getPackageName(), 0).versionName);      //应用版本 必传 如：1.8.2
//            params.put("event", "2");
//            params.put("event_id", oid);
//            params.put("event_content", "open_lock");
//        } catch (Exception e) {
//            e.printStackTrace(System.err);
//        }
//
//        HttpHelper.post(context, Urls.memberEvent, params, new TextHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                try {
//                    Log.e("scan===memberEvent1", "==="+responseString);
//
//                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                    if (result.getFlag().toString().equals("Success")) {
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//
//            }
//        });
    }

    void memberEvent2() {
//        RequestParams params = new RequestParams();
//        try {
//            Log.e("scan===memberEvent0", new Build().MANUFACTURER.toUpperCase()+"==="+new Build().MODEL+"==="+Build.VERSION.RELEASE+"==="+getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
//
//            params.put("uid", uid);
//            params.put("access_token", access_token);
//            params.put("phone_brand", new Build().MANUFACTURER.toUpperCase());
//            params.put("phone_model", new Build().MODEL);
//            params.put("phone_system", "Android");
//            params.put("phone_system_version", Build.VERSION.RELEASE);     //手机系统版本 必传 如：13.1.2
//            params.put("app_version", getPackageManager().getPackageInfo(getPackageName(), 0).versionName);      //应用版本 必传 如：1.8.2
//            params.put("event", "4");
////            params.put("event_id", type);
//            params.put("event_content", "type"+type+"唤醒失败");
//        } catch (Exception e) {
//            e.printStackTrace(System.err);
//        }
//
//        HttpHelper.post(context, Urls.memberEvent, params, new TextHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                try {
//                    Log.e("scan===memberEvent1", "==="+responseString);
//
//                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                    if (result.getFlag().toString().equals("Success")) {
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//
//            }
//        });
    }

    void memberEvent(String ex) {
//        RequestParams params = new RequestParams();
//        try {
//            Log.e("scan===memberEvent0_e", new Build().MANUFACTURER.toUpperCase()+"==="+new Build().MODEL+"==="+Build.VERSION.RELEASE+"==="+context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
//
//            String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
//            String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
//
//            params.put("uid", uid);
//            params.put("access_token", access_token);
//            params.put("phone_brand", new Build().MANUFACTURER.toUpperCase());
//            params.put("phone_model", new Build().MODEL);
//            params.put("phone_system", "Android");
//            params.put("phone_system_version", Build.VERSION.RELEASE);     //手机系统版本 必传 如：13.1.2
//            params.put("app_version", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);      //应用版本 必传 如：1.8.2
//            params.put("event", "1");
//            params.put("event_content", ex);
//        } catch (Exception e) {
//            e.printStackTrace(System.err);
//        }
//
//        HttpHelper.post(context, Urls.memberEvent, params, new TextHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                try {
//                    Log.e("scan===memberEvent1", "==="+responseString);
//
//                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                    if (result.getFlag().toString().equals("Success")) {
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//
//            }
//        });
    }

    private void addOrderbluelock(){
        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",   uid);
            params.put("access_token",  access_token);
            params.put("codenum",   codenum);

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

                                            Log.e("scan===4_1", bleid + "===");

                                            bleService.connect(m_nowMac);

                                            checkConnect();

                                        }

                                    }else if("5".equals(type) || "6".equals(type)){

//                                        getBleRecord();

                                        iv_help.setVisibility(View.GONE);
                                        openBleLock(null);

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
//                                                Log.e("scan===21", isFinish+"===="+isOpen+"===="+oid+"===="+referLatitude);
//
//                                                if(!isFinishing() && !isFinish){
//                                                    submit(uid, access_token);
//                                                }
//                                            }
//                                        }, 10 * 1000);

                                    }else if("7".equals(type)){

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

                                            Log.e("scan===7_1", "==="+deviceuuid);

//                                            new Thread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    try {
//                                                        runOnUiThread(new Runnable() {
//                                                            @Override
//                                                            public void run() {
//                                                                XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
//                                                                builder.setBleStateChangeListener(ActivityScanerCode.this);
//                                                                builder.setScanResultCallback(ActivityScanerCode.this);
//                                                                apiClient = builder.build();
//
//                                                                ActivityScanerCodePermissionsDispatcher.connectDeviceWithPermissionCheck(ActivityScanerCode.this, deviceuuid);
//
//                                                            }
//                                                        });
//                                                    } catch (Exception e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                }
//                                            }).start();

                                            m_myHandler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (!isConnect){
                                                        if (loadingDialog != null && loadingDialog.isShowing()) {
                                                            loadingDialog.dismiss();
                                                        }
//                                                        Toast.makeText(context,"扫码唤醒失败，重启手机蓝牙换辆车试试吧！",Toast.LENGTH_LONG).show();
//                                                        BaseApplication.getInstance().getIBLE().refreshCache();
//                                                        BaseApplication.getInstance().getIBLE().close();
//                                                        BaseApplication.getInstance().getIBLE().disconnect();
//                                                        scrollToFinishActivity();

//                                                        closeEbike();

                                                        if(!isFinishing()){
                                                            tzEnd();
                                                        }
                                                    }
                                                }
                                            }, 15 * 1000);

                                        }

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
        if (access_token == null || "".equals(access_token)){
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

    public void closeEbike(){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("oid",oid);

        Log.e("closeEbike===", "==="+oid);

        HttpHelper.post(this, Urls.closeEbike, params, new TextHttpResponseHandler() {
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
                                Log.e("biking===", "closeEbike===="+result.getData());

                                if ("0".equals(result.getData())){
                                    submit(uid, access_token);
                                } else {
                                }
                            } else {
                                ToastUtil.showMessageApp(context, result.getMsg());
                            }
                        } catch (Exception e) {
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                });
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

                    connectDeviceLP();
                    ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);


                    Log.e("0x98===", "==="+isStop);

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

                                ClientManager.getClient().stopSearch();
                                ClientManager.getClient().disconnect(m_nowMac);
                                ClientManager.getClient().disconnect(m_nowMac);
                                ClientManager.getClient().disconnect(m_nowMac);
                                ClientManager.getClient().disconnect(m_nowMac);
                                ClientManager.getClient().disconnect(m_nowMac);
                                ClientManager.getClient().disconnect(m_nowMac);
                                ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);

                                scrollToFinishActivity();
                            }
                        }
                    }, 15 * 1000);
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

    public void resetLock(){
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isStop){

                    BaseApplication.getInstance().getIBLE().resetLock();

                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isStop){

                                BaseApplication.getInstance().getIBLE().resetLock();

                                m_myHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isStop){
                                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                                loadingDialog.dismiss();
                                            }

                                            Toast.makeText(context,"扫码唤醒失败，重启手机蓝牙换辆车试试吧！",Toast.LENGTH_LONG).show();
                                            BaseApplication.getInstance().getIBLE().refreshCache();
                                            BaseApplication.getInstance().getIBLE().close();
                                            BaseApplication.getInstance().getIBLE().disconnect();
                                            scrollToFinishActivity();
                                        }
                                    }
                                }, 5 * 1000);

                            }
                        }
                    }, 5 * 1000);

                }
            }
        }, 5 * 1000);
    }

//    //小安
//    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH})
//    public void connectDevice(String imei) {
//        if (apiClient != null) {
//            apiClient.connectToIMEI(imei);
//
//            Log.e("connectDevice===", "==="+imei);
//        }
//
//    }
//
//    //小安
//    @Override
//    public void onConnect(BluetoothDevice bluetoothDevice) {
//        isConnect = true;
//        Log.e("scan===Xiaoan", "===Connect");
//
//        m_myHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                Log.e("scan===", "scan====1");
//
////                getCurrentorder2(uid, access_token);
////
////                if (loadingDialog != null && !loadingDialog.isShowing()) {
////                    loadingDialog.setTitle("开锁中");
////                    loadingDialog.show();
////                }
//
//
//                apiClient.setDefend(false, new BleCallback() {
//                    @Override
//                    public void onResponse(final Response response) {
//                        m_myHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Log.e("defend===", response.toString());
//
//                                if(response.code==0){
//                                    isFinish = true;
//
//                                    ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");
//
//                                    if(!isFinishing()){
////                                        tzEnd();
//                                        car_notification(1, 1, 0, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
//                                    }
//                                }else{
////                                    ToastUtil.showMessageApp(context,"开锁失败");
//
////                                                            submit(uid, access_token);
////                                    closeEbike();
//                                    if(!isFinishing()){
////                                        tzEnd();
//                                        car_notification(1, 2, 1, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
//                                    }
//                                }
//
//                                if (loadingDialog != null && loadingDialog.isShowing()){
//                                    loadingDialog.dismiss();
//                                }
//                            }
//                        });
//
//                    }
//                });
//
//
////                CustomDialog.Builder customBuilder = new CustomDialog.Builder(ActivityScanerCode.this);
////                if (0 == Tag){
////                    customBuilder.setMessage("扫码成功,是否开锁?");
////                }else {
////                    customBuilder.setMessage("输号成功,是否开锁?");
////                }
////                customBuilder.setTitle("温馨提示").setNegativeButton("取消", new DialogInterface.OnClickListener() {
////                    public void onClick(DialogInterface dialog, int which) {
////                        dialog.cancel();
////
////                        if (apiClient != null) {
////                            apiClient.onDestroy();
////                        }
////
////                        scrollToFinishActivity();
////
////                    }
////                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
////                    public void onClick(DialogInterface dialog, int which) {
////                        dialog.cancel();
////
////
////
////                    }
////                }).setHint(false);
////                customBuilder.create().show();
//
//            }
//        }, 2 * 1000);
//
//    }

//    //小安
//    @Override
//    public void onDisConnect(BluetoothDevice bluetoothDevice) {
//        isConnect = false;
//        Log.e("scan===Xiaoan", "===DisConnect");
//
//        m_myHandler.post(new Runnable() {
//            @Override
//            public void run() {
//
////                Toast.makeText(context,"扫码唤醒失败，重启手机蓝牙换辆车试试吧！",Toast.LENGTH_LONG).show();
////                scrollToFinishActivity();
//
//                if("".equals(oid)){
//                    memberEvent2();
//
//                    Toast.makeText(context,"扫码唤醒失败，重启手机蓝牙换辆车试试吧！",Toast.LENGTH_LONG).show();
//                    scrollToFinishActivity();
//                }else{
//                    if(!isFinishing()){
//                        tzEnd();
//                    }
//                }
//
////                closeEbike();
////                submit(uid, access_token);
//            }
//        });
//
//
//    }
//
//    @Override
//    public void onDeviceReady(BluetoothDevice bluetoothDevice) {
//
//    }
//
//    @Override
//    public void onReadRemoteRssi(int i) {
//
//    }
//
//    @Override
//    public void onError(BluetoothDevice bluetoothDevice, String s, int i) {
//
//    }
//
//    @Override
//    public void onBleAdapterStateChanged(int i) {
//
//    }
//
//    @Override
//    public void onResult(ScanResult scanResult) {
//
//    }

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