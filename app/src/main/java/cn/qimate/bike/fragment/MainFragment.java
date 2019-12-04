package cn.qimate.bike.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.aprbrother.aprilbeaconscansdk.Beacon;
import com.aprbrother.aprilbeaconscansdk.ScanManager;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.bumptech.glide.Glide;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.sofi.blelocker.library.Code;
import com.sofi.blelocker.library.connect.listener.BleConnectStatusListener;
import com.sofi.blelocker.library.connect.options.BleConnectOptions;
import com.sofi.blelocker.library.model.BleGattProfile;
import com.sofi.blelocker.library.protocol.ICloseListener;
import com.sofi.blelocker.library.protocol.IConnectResponse;
import com.sofi.blelocker.library.protocol.IEmptyResponse;
import com.sofi.blelocker.library.protocol.IGetRecordResponse;
import com.sofi.blelocker.library.protocol.IGetStatusResponse;
import com.sofi.blelocker.library.protocol.IQueryOpenStateResponse;
import com.sofi.blelocker.library.search.SearchRequest;
import com.sofi.blelocker.library.search.SearchResult;
import com.sofi.blelocker.library.search.response.SearchResponse;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.config.LockType;
import com.sunshine.blelibrary.inter.OnConnectionListener;
import com.sunshine.blelibrary.inter.OnDeviceSearchListener;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;
import com.xiaoantech.sdk.XiaoanBleApiClient;
import com.xiaoantech.sdk.ble.model.Response;
import com.xiaoantech.sdk.ble.scanner.ScanResult;
import com.xiaoantech.sdk.listeners.BleCallback;
import com.xiaoantech.sdk.listeners.BleStateChangeListener;
import com.xiaoantech.sdk.listeners.ScanResultCallback;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.zxing.lib.scaner.activity.ActivityScanerCode;
import com.zxing.lib.scaner.activity.MainFragmentPermissionsDispatcher;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.http.rdata.RRent;
import cn.jpush.android.api.JPushInterface;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.ActionCenterActivity;
import cn.qimate.bike.activity.ClientManager;
import cn.qimate.bike.activity.CouponActivity;
import cn.qimate.bike.activity.CrashHandler;
import cn.qimate.bike.activity.CurRoadBikedActivity;
import cn.qimate.bike.activity.CurRoadBikingActivity;
import cn.qimate.bike.activity.CurRoadStartActivity;
import cn.qimate.bike.activity.FeedbackActivity;
import cn.qimate.bike.activity.HistoryRoadDetailActivity;
import cn.qimate.bike.activity.InsureanceActivity;
import cn.qimate.bike.activity.LoginActivity;
import cn.qimate.bike.activity.MyPurseActivity;
import cn.qimate.bike.activity.PayMontCartActivity;
import cn.qimate.bike.activity.PersonAlterActivity;
import cn.qimate.bike.activity.RealNameAuthActivity;
import cn.qimate.bike.activity.ServiceCenter0Activity;
import cn.qimate.bike.activity.ServiceCenterActivity;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.base.BaseFragment;
import cn.qimate.bike.ble.BLEService;
import cn.qimate.bike.ble.utils.ParseLeAdvData;
import cn.qimate.bike.core.common.AppManager;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.Md5Helper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.core.widget.LoadingDialogWithHelp;
import cn.qimate.bike.lock.utils.ToastUtils;
import cn.qimate.bike.model.CarBean;
import cn.qimate.bike.model.CardinfoBean;
import cn.qimate.bike.model.CurRoadBikingBean;
import cn.qimate.bike.model.KeyBean;
import cn.qimate.bike.model.NearbyBean;
import cn.qimate.bike.model.OrderBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.TabEntity;
import cn.qimate.bike.model.TabTopEntity;
import cn.qimate.bike.model.UserMsgBean;
import cn.qimate.bike.util.ByteUtil;
import cn.qimate.bike.util.HtmlTagHandler;
import cn.qimate.bike.util.IoBuffer;
import cn.qimate.bike.util.ToastUtil;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;
import permissions.dispatcher.NeedsPermission;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static cn.qimate.bike.activity.CurRoadBikingActivity.bytes2hex03;
import static com.sofi.blelocker.library.Constants.STATUS_CONNECTED;

@SuppressLint("NewApi")
public class MainFragment extends BaseFragment implements View.OnClickListener, OnBannerListener, OnConnectionListener, BleStateChangeListener, ScanResultCallback, AMap.OnMapClickListener, AMapNaviListener {



    private View v;
    Unbinder unbinder;

    private static MainFragment mainFragment;
    private final static int SCANNIN_GREQUEST_CODE = 1;

    private static final String TAG = MainFragment.class.getSimpleName();

    BluetoothAdapter mBluetoothAdapter;
    private XiaoanBleApiClient apiClient;

    private boolean isConnect = false;

    private String codenum = "";
    private String m_nowMac = "";
    private String type = "";
    private String bleid = "";
    private String deviceuuid = "";
    private String price = "";

    private String keySource = "";
    //密钥索引
    int encryptionKey= 0;
    //开锁密钥
    String keys = null;
    //服务器时间戳，精确到秒，用于锁同步时间
    long serverTime;

    private boolean isStop = false;
    private boolean isOpen = false;
    private boolean isFinish = false;

    private int n = 0;
    private int cn = 0;

    private String tel = "13188888888";

    private LinearLayout scanLock, myCommissionLayout, myLocationLayout, linkLayout, ll_top_biking, ll_biking_openAgain, ll_biking_endBtn, ll_biking_errorEnd;

    public static  MainFragment getInstance() {
        return mainFragment;
    }

    CommonTabLayout tab;
//    TabLayout tab;
//    ViewPager vp;

    private ImageView leftBtn, rightBtn;

    private MyPagerAdapter myPagerAdapter;

    private TextView title;

    private Context context;
    private Activity activity;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private String[] mTitles = { "单车", "助力车"};
    private BikeFragment bikeFragment;
    private EbikeFragment ebikeFragment;

    public AMap aMap;
    public BitmapDescriptor successDescripter;
    public MapView mapView;

    private Banner mBanner;
    private MyImageLoader mMyImageLoader;
    private ArrayList<Integer> imagePath;
    private ArrayList<String> imageTitle;

    CustomDialog.Builder customBuilder;
    private CustomDialog customDialog;
    private CustomDialog customDialog3;
    private CustomDialog customDialog4;
    private CustomDialog customDialog5;
    private CustomDialog customDialog6;
    private CustomDialog customDialog7;
    private CustomDialog customDialog8;
    private CustomDialog customDialog9;
    protected LoadingDialogWithHelp loadingDialogWithHelp;

    private LinearLayout rl_ad;
    private LinearLayout refreshLayout;
    private LinearLayout slideLayout;
    private RelativeLayout rl_authBtn;
    private TextView tv_authBtn;

    private LinearLayout ll_top;
    private LinearLayout ll_top_navi;
    private TextView tv_navi_distance;
    private TextView tv_biking_codenum;

    private AMapNavi mAMapNavi;
    private RouteOverLay routeOverLay;

    PopupWindow popupwindow;


    static private final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private static final int PRIVATE_CODE = 1315;//开启GPS权限

    LocationManager locationManager;
    String provider = LocationManager.GPS_PROVIDER;

    private List<Polygon> pOptions;
    private List<Object> macList;
    private List<Object> macList2;
    public List<LatLng> centerList = new ArrayList<LatLng>();


    private int force_backcar = 0;
    private boolean isTwo = false;
    private boolean first3 = true;
    private boolean isEndBtn = false;
    public static int flagm = 0;
    boolean isFrist1 = true;
    boolean stopScan = false;
    private int clickCount = 0;
    int tz = 0;
    String transtype = "";
    int major = 0;
    int minor = 0;
    private boolean scan = false;
    private boolean isTemp = false;
    private boolean isLookPsdBtn = false;
    private boolean isAgain = false;

    private ScanManager scanManager;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;

    private boolean isOpenLock = false;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main, null);
        unbinder = ButterKnife.bind(this, v);

        mainFragment = this;

        return v;
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        activity = getActivity();

//        activity.registerReceiver(broadcastReceiver, Config.initFilter());
//        GlobalParameterUtils.getInstance().setLockType(LockType.MTS);

        mapView = activity.findViewById(R.id.mainUI_map);
        mapView.onCreate(savedInstanceState);

//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_SCREEN_ON);
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_USER_PRESENT);
//        activity.registerReceiver(mScreenReceiver, filter);

        IntentFilter filter = new IntentFilter("data.broadcast.action");
        activity.registerReceiver(broadcastReceiver2, filter);

        scanManager = new ScanManager(context);
//        scanManager.setUuid("00000000-0000-0000-0000-000000000000");//添加需要扫描的uuid 只有符合的才会返回 不设置返回所有
//        scanManager.setMajor(222);//添加需要扫描的beacon的major 只有符合的major才会返回 不设置返回所有
//        scanManager.setMinor(111);//添加需要扫描的beacon的minor 只有符合的minor才会返回 不设置返回所有
        scanManager.setScanPeriod(100);//设置beacon扫描 反馈结果时间间隔  时间越久 扫描丢失率越低 默认3000ms
//        scanManager.startScan();//启动扫描
//        scanManager.stopScan();//停止扫描
//        scanManager.setScanListener(new ScanManager.MyScanListener());//设置扫描监听 监听扫描返回数据
        scanManager.setScanListener(new ScanManager.MyScanListener() {
            @Override
            public void onScanListenre(ArrayList<Beacon> beacons) {
                Log.e("biking===scanM", "---beacons.size = " + beacons.size());

                for (Beacon beacon : beacons) {

                    Log.e("biking===scanM", beacon.getName()+"=====" +beacon.getRssi()+"=====" + beacon.getMacAddress());

//                    macList.add(""+beacon.getMacAddress());
                    macList.add(""+beacon.getName());

                    scan = true;
                }

            }
        });

        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
//           if (!macList.contains(parseAdvData(rssi,scanRecord))){
//               macList.add(parseAdvData(rssi,scanRecord));
//           }


                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

//                        k++;

//                      device.fetchUuidsWithSdp();

                        Log.e("biking===LeScan", new String(scanRecord)+ "====" +scanRecord.length+ "====" +device.getName() + "====" +device.fetchUuidsWithSdp()+ "====" +device.describeContents() + "====" + device.getAddress() + "====" + device.getUuids() + "====" + rssi);

//                        test_xinbiao += parseAdvData(rssi,scanRecord)+ "====" +device.getName()+ "====" + device.getAddress()+"\n";
//                        tv_test_xinbiao.setText(test_xinbiao);

                        if ("BC01".equals(device.getName()) && !macList.contains(""+device.getAddress())){
                            macList.add(""+device.getName());
//                            macList.add(""+device.getAddress());
//                          title.setText(isContainsList.contains(true) + "》》》" + near + "===" + macList.size() + "===" + k);
                        }

                        scan = true;

                    }
                });
            }
        };

        macList = new ArrayList<>();
        macList2 = new ArrayList<>();
        initView();

        cycling();
    }

    private void cycling() {
        Log.e("mf===cycling", "===");

        HttpHelper.get(context, Urls.cycling, new TextHttpResponseHandler() {
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

                            Log.e("mf===cycling1", responseString + "===" + result.data);

                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);

                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                            if(null != bean.getOrder_sn()){
                                Log.e("mf===cycling2", bean.getOrder_sn()+"===" + bean.getCar_number()+"===" + bean.getLock_id());

                                oid = bean.getOrder_sn();
                                codenum = bean.getCar_number();
                                type = ""+bean.getLock_id();
                                m_nowMac = bean.getCar_lock_mac();

                                rl_ad.setVisibility(View.GONE);
                                ll_top_biking.setVisibility(View.VISIBLE);
                                tv_biking_codenum.setText(codenum);     //TODO

                                uid = SharedPreferencesUrls.getInstance().getString("uid","");
                                access_token = SharedPreferencesUrls.getInstance().getString("access_token","");

                                if (access_token == null || "".equals(access_token)){
                                    refreshLayout.setVisibility(View.GONE);
                                    ToastUtil.showMessageApp(context,"请先登录账号");
                                    UIHelper.goToAct(context,LoginActivity.class);
                                }else {
                                    if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                        ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                    }
                                    //蓝牙锁
                                    if (mBluetoothAdapter == null) {
                                        BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
                                        mBluetoothAdapter = bluetoothManager.getAdapter();
                                    }

                                    if (mBluetoothAdapter == null) {
                                        ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                                      finish();
                                        return;
                                    }

                                    if (!mBluetoothAdapter.isEnabled()) {
                                        flagm = 1;
                                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                        startActivityForResult(enableBtIntent, 188);
                                    } else {
                                        if ("2".equals(type) || "3".equals(type)){

                                            if (loadingDialog != null && !loadingDialog.isShowing()){
                                                loadingDialog.setTitle("正在唤醒车锁");
                                                loadingDialog.show();
                                            }

                                            isOpenLock = false;
                                            connect();

//                                            m_myHandler.postDelayed(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    if (!isLookPsdBtn){
//                                                        if (loadingDialog != null && loadingDialog.isShowing()) {
//                                                            loadingDialog.dismiss();
//                                                        }
//                                                        Toast.makeText(context,"唤醒失败，重启手机蓝牙试试吧！",Toast.LENGTH_LONG).show();
//                                                        BaseApplication.getInstance().getIBLE().refreshCache();
//                                                        BaseApplication.getInstance().getIBLE().disconnect();
//                                                        BaseApplication.getInstance().getIBLE().close();
//
////                                                      scrollToFinishActivity();
//                                                    }
//                                                }
//                                            }, 15 * 1000);

//                                            m_myHandler.postDelayed(new Runnable() {
//                                                @Override
//                                                public void run() {
//
//                                                }
//                                            }, 2 * 1000);

                                            closeBroadcast();     //TODO
                                            activity.registerReceiver(broadcastReceiver, Config.initFilter());
                                            GlobalParameterUtils.getInstance().setLockType(LockType.MTS);
                                        }else if("4".equals(type)){
                                            isLookPsdBtn = true;

                                            BLEService.bluetoothAdapter = mBluetoothAdapter;

                                            bleService.view = context;
                                            bleService.showValue = true;
                                        }else if ("5".equals(type)  || "6".equals(type)) {
//                                          ClientManager.getClient().disconnect(m_nowMac);

                                            Log.e("initView===5", "==="+isLookPsdBtn);

//                                          ll_1.setVisibility(View.VISIBLE);

                                            if (loadingDialogWithHelp != null && !loadingDialogWithHelp.isShowing()){
                                                loadingDialogWithHelp.setTitle("正在唤醒车锁");
                                                loadingDialogWithHelp.show();
                                            }

                                            ClientManager.getClient().stopSearch();
                                            ClientManager.getClient().disconnect(m_nowMac);
                                            ClientManager.getClient().disconnect(m_nowMac);
                                            ClientManager.getClient().disconnect(m_nowMac);
                                            ClientManager.getClient().disconnect(m_nowMac);
                                            ClientManager.getClient().disconnect(m_nowMac);
                                            ClientManager.getClient().disconnect(m_nowMac);
                                            ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
                                            ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener2);

                                            m_myHandler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    connectDeviceLP();
                                                    ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener2);
                                                    ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);

                                                    m_myHandler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (!isLookPsdBtn){
                                                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                                                    loadingDialog.dismiss();
                                                                }
                                                                Toast.makeText(context,"唤醒失败，重启手机蓝牙试试吧！",Toast.LENGTH_LONG).show();

                                                                ClientManager.getClient().stopSearch();
                                                                ClientManager.getClient().disconnect(m_nowMac);
                                                                ClientManager.getClient().disconnect(m_nowMac);
                                                                ClientManager.getClient().disconnect(m_nowMac);
                                                                ClientManager.getClient().disconnect(m_nowMac);
                                                                ClientManager.getClient().disconnect(m_nowMac);
                                                                ClientManager.getClient().disconnect(m_nowMac);
                                                                ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
                                                                ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);

//                                                                scrollToFinishActivity();
                                                            }
                                                        }
                                                    }, 15 * 1000);

                                                }
                                            }, 2 * 1000);
                                        }else if ("7".equals(type)) {
                                            isLookPsdBtn = true;

                                        }

                                    }

//                                    cycling();
                                    refreshLayout.setVisibility(View.VISIBLE);
                                }

                            }

                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });
            }
        });

    }

    private void closeBroadcast() {
        try {
            stopXB();

//            first = true;
//            macList.clear();
//            macList2.clear();

//            if (internalReceiver != null) {
//                activity.unregisterReceiver(internalReceiver);
//                internalReceiver = null;
//            }

            ToastUtil.showMessage(context, "main====closeBroadcast===");
            Log.e("main====", "closeBroadcast===");

        } catch (Exception e) {
            ToastUtil.showMessage(context, "eee====" + e);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        Log.e("onHiddenChanged===mf", hidden+"==="+SharedPreferencesUrls.getInstance().getString("iscert", "")+"==="+access_token);


        if(hidden){
            //pause
        }else{
            //resume

            String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
            String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
            String specialdays = SharedPreferencesUrls.getInstance().getString("specialdays", "");



            if (access_token == null || "".equals(access_token)) {
                rl_authBtn.setEnabled(true);
                rl_authBtn.setVisibility(View.VISIBLE);
                tv_authBtn.setText("您还未登录，点我快速登录");

                refreshLayout.setVisibility(View.GONE);
//                cartBtn.setVisibility(View.GONE);
//                rechargeBtn.setVisibility(View.GONE);
            } else {
                refreshLayout.setVisibility(View.VISIBLE);
                if (SharedPreferencesUrls.getInstance().getString("iscert", "") != null && !"".equals(SharedPreferencesUrls.getInstance().getString("iscert", ""))) {
                    switch (Integer.parseInt(SharedPreferencesUrls.getInstance().getString("iscert", ""))) {
                        case 1:
                            rl_authBtn.setEnabled(true);
                            rl_authBtn.setVisibility(View.VISIBLE);
                            tv_authBtn.setText("您还未登录，点我快速登录");

                            break;
                        case 2:

                            rl_authBtn.setEnabled(true);
                            rl_authBtn.setVisibility(View.VISIBLE);
                            tv_authBtn.setText("您还未认证，点我快速认证");
                            break;
                        case 3:

                            rl_authBtn.setEnabled(false);
                            rl_authBtn.setVisibility(View.VISIBLE);
                            tv_authBtn.setText("认证审核中");
                            break;
                        case 4:
                            rl_authBtn.setEnabled(true);
                            rl_authBtn.setVisibility(View.VISIBLE);
                            tv_authBtn.setText("认证被驳回，请重新认证");

                            break;
                    }
                } else {
                    rl_authBtn.setVisibility(View.GONE);
                }

            }


            Log.e("onHiddenChanged===mf2", isContainsList+"===");
        }
    }

    private boolean checkGPSIsOpen() {
        boolean isOpen;
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
        provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        locationManager.requestLocationUpdates(provider, 2000, 500, locationListener);

        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    private final LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onProviderDisabled(String arg0) {

        }

        @Override
        public void onProviderEnabled(String arg0) {

        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

        }

    };


    private void openGPSSettings() {
        if (checkGPSIsOpen()) {
        } else {

            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
            customBuilder.setTitle("温馨提示").setMessage("请在手机设置打开应用的位置权限并选择最精准的定位模式")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            activity.finish();
                        }
                    })
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, PRIVATE_CODE);
                        }
                    });
            customBuilder.create().show();

        }
    }

    private void initView(){
        openGPSSettings();

        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE_ASK_PERMISSIONS);
                } else {
                    requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE_ASK_PERMISSIONS);
                }
                return;
            }
        }

        if(aMap==null){
            aMap = mapView.getMap();
        }

        aMap.setOnMapClickListener(this);

        mAMapNavi = AMapNavi.getInstance(context);
        mAMapNavi.addAMapNaviListener(this);

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

//                    curMarker = marker;
//                    marker.setTitle(marker.getTitle());

                ll_top.setVisibility(View.GONE);
                ll_top_navi.setVisibility(View.VISIBLE);


//                    Log.e("onMarkerClick===", marker.getTitle()+"==="+marker.getTitle().split("-")[0]);
                Log.e("onMarkerClick===", mAMapNavi+"==="+referLatitude+"==="+referLongitude+"==="+marker.getPosition().latitude+"==="+marker.getPosition().longitude);

//                    31.764391===119.920551===31.765937===119.921452
                mAMapNavi.calculateRideRoute(new NaviLatLng(referLatitude, referLongitude), new NaviLatLng(marker.getPosition().latitude, marker.getPosition().longitude));


//                    codenum = marker.getTitle().split("-")[0];
//                    quantity = marker.getTitle().split("-")[1];
//
//                    initmPopupWindowView();
                return true;
            }
        });

        loadingDialog = new LoadingDialog(activity);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setType(1).setTitle("温馨提示").setMessage("当前行程已停止计费，客服正在加紧处理，请稍等\n客服电话：0519—86999222");
        customDialog = customBuilder.create();

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("温馨提示").setMessage("不在还车点，请至校内地图红色区域停车")
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        customDialog3 = customBuilder.create();

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("温馨提示").setMessage("不在还车点，请至校内地图绿色区域停车")
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        customDialog4 = customBuilder.create();

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("温馨提示").setMessage("上锁失败，请联系客服\n客服电话：0519—86999222")
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        customDialog5 = customBuilder.create();

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("温馨提示").setMessage("连接失败，请重试")
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        customDialog6 = customBuilder.create();

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("温馨提示").setMessage("关锁失败，请重试")
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        customDialog7 = customBuilder.create();

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("温馨提示").setMessage("开锁失败，请重试")
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        customDialog8 = customBuilder.create();

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("温馨提示").setMessage("请确认锁已关闭")
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        customDialog9 = customBuilder.create();

        loadingDialogWithHelp = new LoadingDialogWithHelp(context);
        loadingDialogWithHelp.setCancelable(false);
        loadingDialogWithHelp.setCanceledOnTouchOutside(false);

        rl_authBtn = activity.findViewById(R.id.rl_authBtn);
        tv_authBtn = activity.findViewById(R.id.tv_authBtn);
        refreshLayout = (LinearLayout) activity.findViewById(R.id.mainUI_refreshLayout);
        myLocationLayout =  (LinearLayout) activity.findViewById(R.id.mainUI_myLocationLayout);
        slideLayout = (LinearLayout)activity.findViewById(R.id.mainUI_slideLayout);
        linkLayout = (LinearLayout) activity.findViewById(R.id.mainUI_linkServiceLayout);
        scanLock = (LinearLayout) activity.findViewById(R.id.mainUI_scanCode_lock);

        ll_top = activity.findViewById(R.id.ll_top);
        ll_top_navi = activity.findViewById(R.id.ll_top_navi);
        tv_navi_distance = activity.findViewById(R.id.tv_navi_distance);
        ll_top_biking = activity.findViewById(R.id.ll_top_biking);
        tv_biking_codenum = activity.findViewById(R.id.tv_biking_codenum);

        ll_biking_openAgain = activity.findViewById(R.id.ll_biking_openAgain);
        ll_biking_endBtn = activity.findViewById(R.id.ll_biking_endBtn);
        ll_biking_errorEnd = activity.findViewById(R.id.ll_biking_errorEnd);

        rl_authBtn.setOnClickListener(this);
        refreshLayout.setOnClickListener(this);
        myLocationLayout.setOnClickListener(this);
        slideLayout.setOnClickListener(this);
        linkLayout.setOnClickListener(this);
        scanLock.setOnClickListener(this);
        ll_biking_openAgain.setOnClickListener(this);
        ll_biking_endBtn.setOnClickListener(this);
        ll_biking_errorEnd.setOnClickListener(this);

        bikeFragment = new BikeFragment();
        ebikeFragment = new EbikeFragment();
        mFragments.add(bikeFragment);
        mFragments.add(ebikeFragment);

        Log.e("main===initData", "===");

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabTopEntity(mTitles[i]));
//            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }

        tab = getActivity().findViewById(R.id.tab);

        tab.setTabData(mTabEntities, getActivity(), R.id.fl_change2, mFragments);
        tab.setCurrentTab(0);

//        FragmentManager fragmentManager = getChildFragmentManager();
//        FragmentTransaction ft= fragmentManager.beginTransaction();
////        ft.replace(R.id.fl_change2, bikeFragment,"bikeFragment");
//        ft.add(R.id.fl_change2, bikeFragment,"bikeFragment");
//        ft.add(R.id.fl_change2, ebikeFragment,"ebikeFragment");
//        ft.commit();

//        fragmentManager = getChildFragmentManager();
//        ft= fragmentManager.beginTransaction();
//        ft.replace(R.id.fl_change2, ebikeFragment,"ebikeFragment");
//        ft.commit();

        leftBtn = activity.findViewById(R.id.mainUI_leftBtn);
        rightBtn = activity.findViewById(R.id.mainUI_rightBtn);

        imagePath = new ArrayList<>();
        imageTitle = new ArrayList<>();
        imagePath.add(R.drawable.bike_month_cart_icon);
        imagePath.add(R.drawable.ebike_month_cart_icon);
        imagePath.add(R.drawable.frist_view_title);
        imageTitle.add("我是海鸟一号");
        imageTitle.add("我是海鸟二号");
        imageTitle.add("我是海鸟三号");

        mMyImageLoader = new MyImageLoader();
        mBanner = activity.findViewById(R.id.banner);
        //设置样式，里面有很多种样式可以自己都看看效果
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        //设置图片加载器
        mBanner.setImageLoader(mMyImageLoader);
        //设置轮播的动画效果,里面有很多种特效,可以都看看效果。
        mBanner.setBannerAnimation(Transformer.ZoomOutSlide);
        //轮播图片的文字
        mBanner.setBannerTitles(imageTitle);
        //设置轮播间隔时间
        mBanner.setDelayTime(3000);
        //设置是否为自动轮播，默认是true
        mBanner.isAutoPlay(true);
        //设置指示器的位置，小点点，居中显示
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        //设置图片加载地址
        mBanner.setImages(imagePath)
                //轮播图的监听
                .setOnBannerListener(this)
                //开始调用的方法，启动轮播图。
                .start();

        rl_ad = activity.findViewById(R.id.rl_ad);

        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        rl_ad.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");

        Log.e("onClick===mf", uid+"==="+access_token);

        switch (view.getId()){
            case R.id.rl_ad:
//                initmPopupWindowView();

//                initmPopupPayWindowView();

                Log.e("rl_ad===onclick", isContainsList+"===");

                break;

            case R.id.mainUI_leftBtn:
                UIHelper.goToAct(context, ActionCenterActivity.class);

//                UIHelper.goToAct(context, RealNameAuthActivity.class);

                break;
            case R.id.mainUI_rightBtn:
                if ("".equals(SharedPreferencesUrls.getInstance().getString("access_token",""))){
                    UIHelper.goToAct(context, LoginActivity.class);
                    ToastUtil.showMessageApp(context,"请先登录你的账号");
                    return;
                }
                UIHelper.goToAct(context, PersonAlterActivity.class);
                break;

            case R.id.rl_authBtn:
                if (access_token == null || "".equals(access_token)){
                    UIHelper.goToAct(context,LoginActivity.class);
                }else {
//                    if ("2".equals(SharedPreferencesUrls.getInstance().getString("iscert",""))){
//                        switch (Tag){
//                            case 0:
//                                closeBroadcast();
//                                deactivate();
//
//                                UIHelper.goToAct(context, CurRoadBikingActivity.class);
//                                break;
//                            case 1:
//                                UIHelper.goToAct(context, CurRoadBikedActivity.class);
//                                break;
//                            default:
//                                break;
//                        }
//                    }else {
//                        UIHelper.goToAct(context,RealNameAuthActivity.class);
//                    }
                }
                break;


            case R.id.mainUI_refreshLayout:
                Log.e("refreshLayout===0", isLookPsdBtn+"==="+isContainsList+"==="+SharedPreferencesUrls.getInstance().getString("iscert", ""));

                RefreshLogin();

                break;

            case R.id.mainUI_myLocationLayout:
                if(!bikeFragment.isHidden()){
                    if (bikeFragment.myLocation != null) {
                        CameraUpdate update = CameraUpdateFactory.changeLatLng(bikeFragment.myLocation);
                        aMap.animateCamera(update);
                    }
                }else{
                    if (ebikeFragment.myLocation != null) {
                        CameraUpdate update = CameraUpdateFactory.changeLatLng(ebikeFragment.myLocation);
                        aMap.animateCamera(update);
                    }
                }

                break;

            case R.id.mainUI_slideLayout:

//                @SuppressLint("ResourceType")
//                EbikeFragment fragment = (EbikeFragment)getChildFragmentManager().findFragmentById(1);
//                BikeFragment bikeFragment = (BikeFragment)getChildFragmentManager().findFragmentByTag("bikeFragment");
//                EbikeFragment ebikeFragment = (EbikeFragment)getChildFragmentManager().findFragmentByTag("ebikeFragment");
//                usingFragment.initData2(false);

                Log.e("fragment===", bikeFragment.isHidden()+"==="+ebikeFragment.isHidden());

                if(!bikeFragment.isHidden()){
                    UIHelper.goWebViewAct(context,"停车须知",Urls.phtml5 + uid);
                }else{
                    UIHelper.goWebViewAct(context,"停车须知",Urls.ebike_phtml5 + uid);
                }

                break;

            case R.id.mainUI_linkServiceLayout:
                initmPopupWindowView();
                break;

            case R.id.ll_change_car:
            case R.id.iv_rent_cancelBtn:
                Log.e("ll_rent_cancelB=onClick", uid+"==="+access_token+"==="+SharedPreferencesUrls.getInstance().getString("iscert",""));

                popupwindow.dismiss();

                break;

            case R.id.ll_rent:
                Log.e("ll_rent===onClick", uid+"==="+access_token+"==="+SharedPreferencesUrls.getInstance().getString("iscert",""));

                order();

                break;

            case R.id.ll_biking_openAgain:
                Log.e("ll_openAgain===onClick", uid+"==="+access_token+"==="+SharedPreferencesUrls.getInstance().getString("iscert",""));

                isAgain = true;

                if("3".equals(type)){
//                    unlock();   //TODO
                    if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                        ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                        popupwindow.dismiss();
//                      scrollToFinishActivity();
                    }
                    BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
                    mBluetoothAdapter = bluetoothManager.getAdapter();

                    if (mBluetoothAdapter == null) {
                        ToastUtil.showMessageApp(context, "获取蓝牙失败");
                        popupwindow.dismiss();
//                      scrollToFinishActivity();
                        return;
                    }
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, 188);
                    } else {
                        if (!TextUtils.isEmpty(m_nowMac)) {
                            Log.e("scan===3_1", isLookPsdBtn+"==="+m_nowMac);

                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                            if (loadingDialog != null && !loadingDialog.isShowing()) {
                                loadingDialog.setTitle("正在唤醒车锁");
                                loadingDialog.show();
                            }

                            if(!isLookPsdBtn){   //没连上
                                isTemp = true;
                                isOpenLock = true;
                                connect();
                            }else{
                                BaseApplication.getInstance().getIBLE().openLock();
                            }

                        }
                    }
                }else if("4".equals(type) || "7".equals(type)){
                    unlock();
                }else if("5".equals(type) || "6".equals(type)){
                    if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                        ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                        scrollToFinishActivity();
                    }
                    //蓝牙锁
                    BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
                    mBluetoothAdapter = bluetoothManager.getAdapter();

                    if (mBluetoothAdapter == null) {
                        ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                        scrollToFinishActivity();
                        return;
                    }
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, 188);
                    } else {
                        Log.e("lookPsdBtn===", "==="+isLookPsdBtn);

                        if(!isLookPsdBtn){   //没连上
                            if (loadingDialogWithHelp != null && !loadingDialogWithHelp.isShowing()) {
                                loadingDialogWithHelp.setTitle("正在唤醒车锁");
                                loadingDialogWithHelp.show();
                            }

                            ClientManager.getClient().stopSearch();
                            ClientManager.getClient().disconnect(m_nowMac);
                            ClientManager.getClient().disconnect(m_nowMac);
                            ClientManager.getClient().disconnect(m_nowMac);
                            ClientManager.getClient().disconnect(m_nowMac);
                            ClientManager.getClient().disconnect(m_nowMac);
                            ClientManager.getClient().disconnect(m_nowMac);
//                            ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
//                            ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);

                            SearchRequest request = new SearchRequest.Builder().searchBluetoothLeDevice(0).build();      //duration为0时无限扫描

                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            ClientManager.getClient().search(request, mSearchResponse);
                        }else{
                            if (loadingDialog != null && !loadingDialog.isShowing()){
                                loadingDialog.setTitle("正在连接");
                                loadingDialog.show();
                            }

                            ClientManager.getClient().getStatus(m_nowMac, new IGetStatusResponse() {
                                @Override
                                public void onResponseSuccess(String version, String keySerial, String macKey, String vol) {
                                    Log.e("getStatus===Success", "===");

                                    keySource = keySerial;
                                    m_myHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            rent();
                                        }
                                    });
                                }

                                @Override
                                public void onResponseFail(final int code) {
                                    Log.e("getStatus===Fail", "===");

                                    m_myHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            ToastUtil.showMessageApp(context, Code.toString(code));

                                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                                loadingDialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            });
                        }

                    }
                }else{

                    try{

                        Log.e("again===", "==="+isLookPsdBtn);

                        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                            ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                            scrollToFinishActivity();
                        }
                        //蓝牙锁
                        BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

                        mBluetoothAdapter = bluetoothManager.getAdapter();

                        if (mBluetoothAdapter == null) {
                            ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                            scrollToFinishActivity();
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

                            if(!isLookPsdBtn){   //没连上
                                isTemp = true;
                                isOpenLock = true;
                                connect();
                            }else{
                                BaseApplication.getInstance().getIBLE().openLock();
                            }
                        }

                    }catch (Exception e){
                        ToastUtil.showMessageApp(context, "请重试");
                    }
                }

                break;

            case R.id.ll_biking_endBtn:
                Log.e("ll_endBtn===onClick", uid+"==="+access_token+"==="+SharedPreferencesUrls.getInstance().getString("iscert",""));

                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                    scrollToFinishActivity();
                }
                //蓝牙锁
                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = bluetoothManager.getAdapter();

                if (mBluetoothAdapter == null) {
                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                    scrollToFinishActivity();
                    return;
                }
                if (!mBluetoothAdapter.isEnabled()) {
                    Log.e("queryState===1", "===");

                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 188);

                    return;
                }

                isEndBtn = true;

                clickCount++;

//                test_xinbiao += major+"==="+minor+"\n";   //TODO
//                tv_test_xinbiao.setText(test_xinbiao);

                Log.e("biking_endBtn===", type+"==="+major+"==="+isContainsList.contains(true)+"==="+referLatitude+"==="+referLongitude);

                if(major !=0){
                    queryState();
                }
                else if("7".equals(type)){
//                    location();       //TODO
                }
                else{
                    startXB();

                    if (loadingDialog != null && !loadingDialog.isShowing()){
                        loadingDialog.setTitle("还车点确认中");
                        loadingDialog.show();
                    }

                    if(!isContainsList.contains(true)){
                        minPoint(referLatitude, referLongitude);
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                int n=0;
                                while(macList.size() == 0 && !isContainsList.contains(true)){
                                    Thread.sleep(100);
                                    n++;

                                    Log.e("biking===n",macList.size()+"=="+n);

                                    if(n>=101) break;
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            m_myHandler.sendEmptyMessage(3);
                        }
                    }).start();
                }

                break;

            case R.id.ll_biking_errorEnd:
                Log.e("ll_errorEnd===onClick", uid+"==="+access_token+"==="+SharedPreferencesUrls.getInstance().getString("iscert",""));

                break;

            case R.id.mainUI_scanCode_lock:
                Log.e("scanCode_lock===mf", SharedPreferencesUrls.getInstance().getString("iscert","")+"==="+uid+"==="+access_token);

                if (access_token == null || "".equals(access_token)){
                    ToastUtil.showMessageApp(context,"请先登录账号");
                    UIHelper.goToAct(context,LoginActivity.class);
                    return;
                }
                if (SharedPreferencesUrls.getInstance().getString("iscert","") != null && !"".equals(SharedPreferencesUrls.getInstance().getString("iscert",""))){
                    switch (Integer.parseInt(SharedPreferencesUrls.getInstance().getString("iscert",""))){

                        case 1:
//                            getCurrentorder2(uid,access_token);

                            if (Build.VERSION.SDK_INT >= 23) {
                                int checkPermission = activity.checkSelfPermission(Manifest.permission.CAMERA);
                                if (checkPermission != PERMISSION_GRANTED) {
//                                    flag = 1;

                                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                        requestPermissions(new String[] { Manifest.permission.CAMERA }, 100);
                                    } else {
                                        CustomDialog.Builder customBuilder1 = new CustomDialog.Builder(context);
                                        customBuilder1.setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")
                                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                requestPermissions(
                                                        new String[] { Manifest.permission.CAMERA },
                                                        100);
                                            }
                                        });
                                        customBuilder1.create().show();
                                    }
//                                    if (loadingDialog1 != null && loadingDialog1.isShowing()){
//                                        loadingDialog1.dismiss();
//                                    }
                                    return;
                                }
                            }
                            try {

//                                closeBroadcast();
//                                deactivate();

                                Intent intent = new Intent();
                                intent.setClass(context, ActivityScanerCode.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);

                            } catch (Exception e) {
                                UIHelper.showToastMsg(context, "相机打开失败,请检查相机是否可正常使用", R.drawable.ic_error);
                            }
//                            dialog.cancel();
//                            if (loadingDialog1 != null && loadingDialog1.isShowing()){
//                                loadingDialog1.dismiss();
//                            }

                            break;
                        case 2:
                            ToastUtil.showMessageApp(context,"您还未认证,请先认证");
                            UIHelper.goToAct(context, RealNameAuthActivity.class);
                            break;
                        case 3:
                            ToastUtil.showMessageApp(context,"认证审核中，请点击刷新");
                            break;
                        case 4:
                            ToastUtil.showMessageApp(context,"认证被驳回，请重新认证");
                            UIHelper.goToAct(context,RealNameAuthActivity.class);
                    }
                }else {
                    ToastUtil.showMessageApp(context,"您还未认证,请先认证");
                }
                break;

            default:
                break;
        }
    }

    private void startXB() {
        if (mBluetoothAdapter == null) {
            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (mBluetoothAdapter == null) {
            ToastUtil.showMessageApp(context, "获取蓝牙失败");
//            scrollToFinishActivity();
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            flagm = 1;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 188);
        }else{
            if (macList.size() != 0) {
                macList.clear();
            }

            scanManager.startScan();
//            manager.startEddyStoneScan();

            Log.e("biking===startXB",mBluetoothAdapter+"==="+mLeScanCallback);
            UUID[] uuids = {Config.xinbiaoUUID};
            mBluetoothAdapter.startLeScan(uuids, mLeScanCallback);

        }
    }

    private void stopXB() {
        if (!"1".equals(type)) {
            scanManager.stopScan();
//            manager.stopEddyStoneScan();

            Log.e("biking===stopXB",mBluetoothAdapter+"==="+mLeScanCallback);

            if (mLeScanCallback != null && mBluetoothAdapter != null) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }


        }
    }

    public LatLng minPoint(double lat, double lon) {
        double s=0;
        double s1=0;

        double x = 0;
        double y = 0;

        for (int i = 0; i < listPoint.size(); i++) {

//            Log.e("minPoint===", listPoint.get(i).latitude+"==="+referLatitude+">>>"+listPoint.get(i).longitude+"==="+referLongitude);

            s1 = (listPoint.get(i).latitude-lat)*(listPoint.get(i).latitude-lat) + (listPoint.get(i).longitude-lon)*(listPoint.get(i).longitude-lon);

            if(i==0 || s1<s){
                s = s1;

                x = listPoint.get(i).longitude;
                y = listPoint.get(i).latitude;
            }else{
                continue;
            }
        }

        float dis = AMapUtils.calculateLineDistance(new LatLng(referLatitude, referLongitude), new LatLng(y, x));

        Log.e("minPoint===2", s+"==="+dis);

        if(!isContainsList.contains(true) && dis < 30){
            isContainsList.add(true);
        }

        return null;
    }

    BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, final Intent intent) {
            Log.e("broadcastReceiver===0", "==="+intent);

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
//                    if(!isHidden && !"4".equals(type) && !"7".equals(type)){
//
//                    }

                    Log.e("broadcastReceiver===mf", "==="+intent);

                    getCurrentorder1(SharedPreferencesUrls.getInstance().getString("uid", ""), SharedPreferencesUrls.getInstance().getString("access_token", ""));
                    getFeedbackStatus();
                }
            });
        }
    };

    private void getCurrentorder1(String uid, String access_token) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("access_token", access_token);
        HttpHelper.post(context, Urls.getCurrentorder, params, new TextHttpResponseHandler() {
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
                                if ("2".equals(SharedPreferencesUrls.getInstance().getString("iscert", ""))) {
                                    if ("[]".equals(result.getData()) || 0 == result.getData().length()) {
                                        rl_authBtn.setEnabled(false);
                                        rl_authBtn.setVisibility(View.GONE);

                                        SharedPreferencesUrls.getInstance().putBoolean("isStop", true);
                                        SharedPreferencesUrls.getInstance().putString("m_nowMac", "");

                                    } else {
                                        CurRoadBikingBean bean = JSON.parseObject(result.getData(), CurRoadBikingBean.class);

                                        m_nowMac = bean.getMacinfo();

                                        Log.e("main===bike", "getMacinfo====" + bean.getMacinfo());

                                        if (!"".equals(m_nowMac)) {
                                            oid = bean.getOid();
                                            osn = bean.getOsn();
                                            type = bean.getType();

                                            SharedPreferencesUrls.getInstance().putString("m_nowMac", m_nowMac);
                                            SharedPreferencesUrls.getInstance().putString("oid", oid);
                                            SharedPreferencesUrls.getInstance().putString("osn", osn);
                                            SharedPreferencesUrls.getInstance().putString("type", type);
                                            SharedPreferencesUrls.getInstance().putString("deviceuuid", bean.getDeviceuuid());
                                        }

                                        Log.e("main===bike", "getStatus====" + bean.getStatus());

//                                        if ("1".equals(bean.getStatus())) {
//                                            SharedPreferencesUrls.getInstance().putBoolean("isStop", false);
//
//                                            tv_authBtn.setText("您有一条进行中的行程，点我查看");
//                                            Tag = 0;
//                                        } else {
//                                            SharedPreferencesUrls.getInstance().putBoolean("isStop", true);
//                                            SharedPreferencesUrls.getInstance().putString("m_nowMac", "");
//
//                                            tv_authBtn.setText("您有一条未支付的行程，点我查看");
//                                            Tag = 1;
//                                        }
                                        rl_authBtn.setVisibility(View.VISIBLE);
                                        rl_authBtn.setEnabled(true);
                                    }
                                }
                            } else {
                                ToastUtils.show(result.getMsg());
                            }
                        } catch (Exception e) {
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }
                });

            }
        });
    }

    private void getFeedbackStatus() {
        RequestParams params = new RequestParams();
        params.put("telphone", SharedPreferencesUrls.getInstance().getString("userName", ""));
        HttpHelper.get(context, Urls.getFeedbackStatus, params, new TextHttpResponseHandler() {

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
//						ToastUtil.showMessageApp(context,"数据更新成功==="+SharedPreferencesUrls.getInstance().getBoolean("isStop",true));

                                Log.e("getFeedbackStatus===1", result.data +"===" + SharedPreferencesUrls.getInstance().getBoolean("isStop", true));

                                if ("2".equals(result.data) && !SharedPreferencesUrls.getInstance().getBoolean("isStop", true)) {
                                    customDialog.show();
                                } else {
                                    customDialog.dismiss();
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
        });
    }


    public void changeTab(int index) {


//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.commitAllowingStateLoss();

//        if(tab==null){
//            tab = getActivity().findViewById(R.id.tab);
//        }

        Log.e("main===changeTab", "==="+tab);

        tab.setCurrentTab(index);



//        if (dialogTag == null) {
//            dialogParent.setCancelable(false);
//            transaction.add(dialogParent, "dialog_event");
//            transaction.commitAllowingStateLoss();
//        }
//        transaction.show(ebikeFragment);

    }

//    private void changeFragment(Fragment fromFragment, Fragment toFragment) {
//
////        if (nowFragment != toFragment) {
////            nowFragment = toFragment;
////        }
//
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//
//        if (toFragment.isAdded() == false) {
//            ft.hide(fromFragment).add(R.id.center_view_main_activity, toFragment).commit();
//        } else {
//            ft.hide(fromFragment).show(toFragment).commit();
//        }
//
//    }





    public void initmPopupRentWindowView(){

        // 获取自定义布局文件的视图
        View customView = getLayoutInflater().inflate(R.layout.pop_rent_bike, null, false);
        // 创建PopupWindow宽度和高度
        RelativeLayout pop_win_bg = (RelativeLayout) customView.findViewById(R.id.pop_rent_bg);
        ImageView iv_popup_window_back = (ImageView) customView.findViewById(R.id.popupWindow_rent_back);
        ImageView iv_rent_cancelBtn = (ImageView) customView.findViewById(R.id.iv_rent_cancelBtn);
        TextView tv_codenum = (TextView) customView.findViewById(R.id.tv_codenum);
        TextView tv_price = (TextView) customView.findViewById(R.id.tv_price);
        LinearLayout ll_change_car = (LinearLayout) customView.findViewById(R.id.ll_change_car);
        LinearLayout ll_rent = (LinearLayout) customView.findViewById(R.id.ll_rent);

        tv_codenum.setText(codenum);
//        tv_price.setText(Html.fromHtml(price));
        tv_price.setText(Html.fromHtml(price, null, new HtmlTagHandler("font")));


        iv_rent_cancelBtn.setOnClickListener(this);
        ll_change_car.setOnClickListener(this);
        ll_rent.setOnClickListener(this);

        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(activity);
        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
            UtilBitmap.blurImageView(context, iv_popup_window_back, 10,0xAA000000);
        } else {
            // 获取的Bitmap为null时，用半透明代替
            iv_popup_window_back.setBackgroundColor(0x77000000);
        }
        // 打开弹窗
        UtilAnim.showToUp(pop_win_bg, iv_popup_window_back);
        // 创建PopupWindow宽度和高度
        popupwindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        /**
         * 设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
         */
        popupwindow.setAnimationStyle(R.style.PopupAnimation);
        popupwindow.setOutsideTouchable(false);

        popupwindow.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        Log.e("initmPopup===", "===");
    }

    public void initmPopupPayWindowView(){

        // 获取自定义布局文件的视图
        View customView = getLayoutInflater().inflate(R.layout.pop_pay_car, null, false);
        // 创建PopupWindow宽度和高度
        RelativeLayout pop_win_bg = (RelativeLayout) customView.findViewById(R.id.pop_rent_bg);
        ImageView iv_popup_window_back = (ImageView) customView.findViewById(R.id.popupWindow_rent_back);
        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(getActivity());
        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
            UtilBitmap.blurImageView(context, iv_popup_window_back, 10,0xAA000000);
        } else {
            // 获取的Bitmap为null时，用半透明代替
            iv_popup_window_back.setBackgroundColor(0x77000000);
        }
        // 打开弹窗
        UtilAnim.showToUp(pop_win_bg, iv_popup_window_back);
        // 创建PopupWindow宽度和高度
        final PopupWindow popupwindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        /**
         * 设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
         */
        popupwindow.setAnimationStyle(R.style.PopupAnimation);
        popupwindow.setOutsideTouchable(false);

        popupwindow.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        Log.e("initmPopup===", "===");
    }

    public void initmPopupWindowView(){

        // 获取自定义布局文件的视图
        View customView = getLayoutInflater().inflate(R.layout.pop_menu, null, false);
        // 创建PopupWindow宽度和高度
        RelativeLayout pop_win_bg = (RelativeLayout) customView.findViewById(R.id.pop_win_bg);
        ImageView iv_popup_window_back = (ImageView) customView.findViewById(R.id.popupWindow_back);
        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(activity);
        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
            UtilBitmap.blurImageView(context, iv_popup_window_back, 10,0xAA000000);
        } else {
            // 获取的Bitmap为null时，用半透明代替
            iv_popup_window_back.setBackgroundColor(0x77000000);
        }
        // 打开弹窗
        UtilAnim.showToUp(pop_win_bg, iv_popup_window_back);
        // 创建PopupWindow宽度和高度
        final PopupWindow popupwindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, true);
        /**
         * 设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
         */
        popupwindow.setAnimationStyle(R.style.PopupAnimation);
        popupwindow.setOutsideTouchable(false);

        LinearLayout feedbackLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_feedbackLayout);
        LinearLayout helpLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_helpLayout);
        final LinearLayout callLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_callLayout);
        TextView cancleBtn = (TextView)customView.findViewById(R.id.pop_menu_cancleBtn);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.pop_menu_feedbackLayout:
                        UIHelper.goToAct(context, ServiceCenter0Activity.class);
                        break;
                    case R.id.pop_menu_helpLayout:
//                        WindowManager windowManager = activity.getWindowManager();
//                        Display display = windowManager.getDefaultDisplay();
//                        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//                        lp.width = (int) (display.getWidth() * 0.8); // 设置宽度0.6
//                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                        dialog.getWindow().setAttributes(lp);

//                        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
//                        dialog.show();

                        break;
                    case R.id.pop_menu_callLayout:
                        UIHelper.goToAct(context, ServiceCenterActivity.class);

                        break;
                    case R.id.pop_menu_cancleBtn:

                        break;
                }
                popupwindow.dismiss();
            }
        };

        feedbackLayout.setOnClickListener(listener);
        helpLayout.setOnClickListener(listener);
        callLayout.setOnClickListener(listener);
        cancleBtn.setOnClickListener(listener);

        popupwindow.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void order() {
        Log.e("order===", "===");

        RequestParams params = new RequestParams();
        params.put("order_type", 1);
        params.put("car_number", URLEncoder.encode(codenum));

        HttpHelper.post(context, Urls.order, params, new TextHttpResponseHandler() {
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

                            Log.e("order===1", responseString + "===" + result.data);

                            JSONObject jsonObject = new JSONObject(result.getData());

                            Log.e("order===2",  isLookPsdBtn + "===" + type+"===" + jsonObject.getString("order_sn"));

                            oid = jsonObject.getString("order_sn");

//                            if("0".equals(jsonObject.getString("code"))){
//                                car(tokencode);
//                            }

                            isLookPsdBtn = false;

                            if ("1".equals(type)) {          //单车机械锁
                                UIHelper.goToAct(context, CurRoadStartActivity.class);
                                popupwindow.dismiss();
//                                scrollToFinishActivity();
                            } else if ("2".equals(type)) {    //单车蓝牙锁

//                                unlock();

                                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                    popupwindow.dismiss();
//                                    scrollToFinishActivity();
                                }
                                //蓝牙锁
                                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

                                mBluetoothAdapter = bluetoothManager.getAdapter();

                                if (mBluetoothAdapter == null) {
                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                    popupwindow.dismiss();
//                                    scrollToFinishActivity();
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
                                        isOpenLock = true;
                                        connect();
                                    }
                                }
                            }else if ("3".equals(type)) {    //单车3合1锁

//                                unlock();

                                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                    popupwindow.dismiss();
//                                    scrollToFinishActivity();
                                }
                                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
                                mBluetoothAdapter = bluetoothManager.getAdapter();

                                if (mBluetoothAdapter == null) {
                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                    popupwindow.dismiss();
//                                    scrollToFinishActivity();
                                    return;
                                }
                                if (!mBluetoothAdapter.isEnabled()) {
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 188);
                                } else {
                                    if (!TextUtils.isEmpty(m_nowMac)) {
                                        Log.e("scan===3_1", "==="+m_nowMac);

                                        isOpenLock = true;
                                        connect();
                                    }
                                }

                            }else if ("4".equals(type)) {

//                                unlock();

                                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                    popupwindow.dismiss();
//                                    scrollToFinishActivity();
                                }
                                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
                                mBluetoothAdapter = bluetoothManager.getAdapter();

                                BLEService.bluetoothAdapter = mBluetoothAdapter;

                                bleService.view = context;
                                bleService.showValue = true;

                                if (mBluetoothAdapter == null) {
                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                    popupwindow.dismiss();
//                                    scrollToFinishActivity();
                                    return;
                                }
                                if (!mBluetoothAdapter.isEnabled()) {
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 188);
                                } else {

                                    Log.e("mf===4_1", bleid + "==="+m_nowMac);

                                    bleService.connect(m_nowMac);

                                    checkConnect();
                                }

                            }else if ("5".equals(type) || "6".equals(type)) {      //泺平单车蓝牙锁

                                Log.e("mf===5_1", deviceuuid + "==="+m_nowMac);

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


                                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                    popupwindow.dismiss();
//                                    scrollToFinishActivity();
                                }
                                //蓝牙锁
                                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

                                mBluetoothAdapter = bluetoothManager.getAdapter();

                                if (mBluetoothAdapter == null) {
                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                    popupwindow.dismiss();
//                                    scrollToFinishActivity();
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

//                                    iv_help.setVisibility(View.VISIBLE);

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                activity.runOnUiThread(new Runnable() {
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
                                Log.e("mf===7_1", deviceuuid + "==="+m_nowMac);

//                                unlock();

                                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                    popupwindow.dismiss();
//                                    scrollToFinishActivity();
                                }
                                //蓝牙锁
                                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

                                mBluetoothAdapter = bluetoothManager.getAdapter();

                                if (mBluetoothAdapter == null) {
                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                    popupwindow.dismiss();
//                                    scrollToFinishActivity();
                                    return;
                                }
                                if (!mBluetoothAdapter.isEnabled()) {
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 188);
                                } else {
                                    XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                                    builder.setBleStateChangeListener(MainFragment.this);
                                    builder.setScanResultCallback(MainFragment.this);
                                    apiClient = builder.build();

                                    MainFragmentPermissionsDispatcher.connectDeviceWithPermissionCheck(MainFragment.this, deviceuuid);

                                    m_myHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!isConnect){
                                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                                    loadingDialog.dismiss();
                                                }

                                                Log.e("mf===7==timeout", isConnect + "==="+activity.isFinishing());


//                                          closeEbike();

                                                if(!activity.isFinishing()){
//                                                tzEnd();
//                                                    car_notification(1, 2, 2, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
//                                                    popupwindow.dismiss();

                                                    unlock();
                                                }
                                            }
                                        }
                                    }, 15 * 1000);
                                }


                            }


                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }
//                        if (loadingDialog != null && loadingDialog.isShowing()) {
//                            loadingDialog.dismiss();
//                        }
                    }
                });


            }
        });
    }



    private void lock() {
        Log.e("mf===lock", "===");

        HttpHelper.post(context, Urls.lock, null, new TextHttpResponseHandler() {
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

                            Log.e("mf===lock1", responseString + "===" + result.data);

                            car_notification(1, 3, 0, 4,1,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));


//                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);
//
//                            Log.e("scan===cycling2", bean.getOrder_sn()+"===" + bean.getCar_number());
//
//                            oid = bean.getOrder_sn();

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

    private void unlock() {
        Log.e("mf===unlock", "===");

        HttpHelper.post(context, Urls.unlock, null, new TextHttpResponseHandler() {
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

                            Log.e("mf===unlock1", type + "===" + codenum + "===" + responseString + "===" + result.data);

                            carLoop();

//                            car_notification();

//                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);
//
//                            Log.e("scan===cycling2", bean.getOrder_sn()+"===" + bean.getCar_number());
//
//                            oid = bean.getOrder_sn();

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

    private void carLoop() {
        Log.e("mf===carLoop", "===" + codenum);

        HttpHelper.get(context, Urls.car + URLEncoder.encode(codenum), new TextHttpResponseHandler() {
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

                            Log.e("mf===carLoop1", responseString + "===" + result.data);

                            CarBean bean = JSON.parseObject(result.getData(), CarBean.class);

                            Log.e("mf===carLoop2", bean.getNumber()+"===" + bean.getLock_status());

                            if(2 != bean.getLock_status()){
                                queryCarStatus();
                            }else{
                                if(!isAgain){
                                    car_notification(1, 1, 0, 2,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

                                    popupwindow.dismiss();

                                    rl_ad.setVisibility(View.GONE);
                                    ll_top_biking.setVisibility(View.VISIBLE);

                                    tv_biking_codenum.setText(codenum);

                                }else{
                                    car_notification(2, 1, 0, 2,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

                                }

                            }

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

    private void queryCarStatus() {
        if(n<5){
            n++;

            m_myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("mf===queryCarStatus", "===");

                    carLoop();

                }
            }, 1 * 1000);
        }else{
            ToastUtil.showMessageApp(context, "开锁失败");

            if(!isAgain){
                car_notification(1, 2, 4, 2,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

                popupwindow.dismiss();
            }else{
                car_notification(2, 2, 4, 2,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
            }


        }

    }

    protected void rent(){

        Log.e("rent===000",m_nowMac+"==="+keySource);

        RequestParams params = new RequestParams();
        params.put("lock_mac", m_nowMac);
        params.put("keySource",keySource);
        HttpHelper.get(context, Urls.rent, params, new TextHttpResponseHandler() {
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

//                            iv_help.setVisibility(View.GONE);

                            if(isAgain){
                                openAgainBleLock(null);
                            }else{
                                openBleLock(null);
                            }



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

    private void car_notification(int scene, final int lock_status, int failure_code, int report_type, int back_type, String failure_desc, String parking, String longitude, String latitude) {
        Log.e("mf===car_notification", scene+"==="+ lock_status+"==="+ report_type +"==="+ back_type +"==="+ oid+"==="+ Md5Helper.encode(oid+":lock_status:"+lock_status));

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

        HttpHelper.post(context, Urls.car_notification, params, new TextHttpResponseHandler() {
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

                            Log.e("mf===car_notification1", responseString + "====" + lock_status);

                            if(lock_status == 1) {
                                ToastUtil.showMessageApp(context, "恭喜您,开锁成功!");

                            }else  if(lock_status == 3){
                                rl_ad.setVisibility(View.VISIBLE);
                                ll_top_biking.setVisibility(View.GONE);


                                ToastUtil.showMessageApp(context,"恭喜您,还车成功,请支付!");
//                                UIHelper.goToAct(context, CurRoadBikedActivity.class);        //TODO
                            }



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

    //泺平
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

    ////泺平===监听当前连接状态
    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {

            Log.e("ConnectStatus===", mac+"===="+(status == STATUS_CONNECTED));


            if(status != STATUS_CONNECTED){
                return;
            }

            ClientManager.getClient().stopSearch();

            ClientManager.getClient().getStatus(mac, new IGetStatusResponse() {
                @Override
                public void onResponseSuccess(String version, String keySerial, String macKey, String vol) {
//                    quantity = vol+"";

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

    //泺平===监听当前连接状态
    private final BleConnectStatusListener mConnectStatusListener2 = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(final String mac, final int status) {
//            BluetoothLog.v(String.format(Locale.getDefault(), "DeviceDetailActivity onConnectStatusChanged %d in %s", status, Thread.currentThread().getName()));

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("ConnectStatus===biking", mac+"==="+(status == STATUS_CONNECTED)+"==="+m_nowMac);
//                    lookPsdBtn.setEnabled(true);

                    if(status == STATUS_CONNECTED){
                        isLookPsdBtn = true;

                        ToastUtil.showMessageApp(context,"设备连接成功");
                    }else{
                        isLookPsdBtn = false;

                        ToastUtil.showMessageApp(context,"设备断开连接");
                    }

                    connectDeviceIfNeeded();
                }
            });


//            Globals.isBleConnected = mConnected = (status == STATUS_CONNECTED);
//            refreshData(mConnected);
//            connectDeviceIfNeeded();
        }
    };

    //泺平
    private void connectDeviceIfNeeded() {
        if (!isLookPsdBtn) {
            connectDeviceLP();
        } else {
            ClientManager.getClient().stopSearch();
        }
    }

    //泺平===与设备，开锁
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
//                      deleteBleRecord(null);

                        if("锁已开".equals(Code.toString(code))){
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

                            isFinish = true;

                            ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

                            if(!activity.isFinishing()){
//                              tzEnd();
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

            }
        });
    }

    //与设备，开锁
    private void openAgainBleLock(RRent.ResultBean resultBean) {
//        UIHelper.showProgress(this, "open_bike_status");
//        ClientManager.getClient().openLock(mac, "18112348925", resultBean.getServerTime(),

//        Log.e("openBleLock===", resultBean.getServerTime()+"==="+resultBean.getKeys()+"==="+resultBean.getEncryptionKey());

        Log.e("biking=openAgainBleLock", serverTime+"==="+keys+"==="+encryptionKey);

        ClientManager.getClient().openLock(m_nowMac,"000000000000", (int) serverTime, keys, encryptionKey, new IEmptyResponse(){
            //      ClientManager.getClient().openLock(m_nowMac,"000000000000", resultBean.getServerTime(), resultBean.getKeys(), resultBean.getEncryptionKey(), new IEmptyResponse(){
            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }

                        ToastUtil.showMessageApp(context, Code.toString(code));
                    }
                });


            }

            @Override
            public void onResponseSuccess() {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }

                        getBleRecord2();

                        Log.e("openAgainLock===Success", "===");

                        ToastUtil.showMessageApp(context, "开锁成功");

                        //TODO
                        car_notification(2, 1, 0, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

                    }
                });

            }
        });
    }

    //泺平===与设备，获取记录
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

    //泺平===与设备，删除记录
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

                        if(!activity.isFinishing()){
//                            tzEnd();
                            car_notification(1, 1, 0, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
                            popupwindow.dismiss();
                        }
                    }
                });

            }

            @Override
            public void onResponseFail(int code) {
                Log.e("scan===deleteBleRecord", Code.toString(code));
//                ToastUtil.showMessageApp(context, Code.toString(code));
                popupwindow.dismiss();
            }
        });
    }

    private final SearchResponse mSearchResponse3 = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            Log.e("biking===","DeviceListActivity.onSearchStarted");
        }

        @Override
        public void onDeviceFounded(final SearchResult device) {

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("biking===","DeviceListActivity.onDeviceFounded2 " + device.device.getAddress()+"==="+m_nowMac);

                    if(m_nowMac.equals(device.device.getAddress())){
                        ClientManager.getClient().stopSearch();

                        connectDeviceLP();
//                        ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
//                        ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);


                        ClientManager.getClient().getStatus(m_nowMac, new IGetStatusResponse() {
                            @Override
                            public void onResponseSuccess(String version, String keySerial, String macKey, String vol) {
                                keySource = keySerial;
                                m_myHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        queryOpenState();
                                    }
                                });
                            }

                            @Override
                            public void onResponseFail(final int code) {
                                m_myHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (loadingDialog != null && loadingDialog.isShowing()){
                                            loadingDialog.dismiss();
                                        }

                                        Log.e("getStatus===", Code.toString(code));
//                                        ToastUtil.showMessageApp(context, Code.toString(code));
                                    }
                                });
                            }
                        });

                        Log.e("biking===","DeviceListActivity.onDeviceFounded2_2 " + device.device.getAddress()+"==="+m_nowMac);
                    }
                }
            });
        }

        @Override
        public void onSearchStopped() {
            Log.e("biking===","DeviceListActivity.onSearchStopped");
        }

        @Override
        public void onSearchCanceled() {
            Log.e("biking===","DeviceListActivity.onSearchCanceled");

        }
    };

    //监听锁关闭事件
    private final ICloseListener mCloseListener = new ICloseListener() {
        @Override
        public void onNotifyClose() {

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("onNotifyClose===", "====");

                    ToastUtil.showMessageApp(context,"锁已关闭");

//                    if("6".equals(type)){
//                        lookPsdBtn.setText("再次开锁");
//                        SharedPreferencesUrls.getInstance().putString("tempStat","1");
//                    }

                    getBleRecord2();

//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
//                    ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);
                }
            });
        }
    };

    //与设备，获取记录
    private void getBleRecord2() {
        ClientManager.getClient().getRecord(m_nowMac, new IGetRecordResponse() {
            @Override
            public void onResponseSuccess(String phone, final String bikeTradeNo, String timestamp, final String transType, final String mackey, String index, final int Major, final int Minor, String vol) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("getBleRecord===###", transType+"==Major:"+ Major +"---Minor:"+Minor+"---mackey:"+mackey);

                        if(BaseApplication.getInstance().isTestLog()){
//                            macText.setText(Major+"==="+Minor+"==="+macList); //TODO
                        }

//                      ToastUtil.showMessageApp(context, "Major:"+ Major +"---Minor:"+Minor);

                        transtype = transType;
                        major = Major;
                        minor = Minor;

                        SharedPreferencesUrls.getInstance().putInt("major", major);

//                      m_myHandler.sendEmptyMessage(9);

                        deleteBleRecord2(bikeTradeNo);
                    }
                });


            }

            @Override
            public void onResponseSuccessEmpty() {
//                ToastUtil.showMessageApp(context, "record empty");
                Log.e("getBleRecord===", "Success===Empty");

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });

            }

            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("getBleRecord===fail", Code.toString(code));
                    }
                });
            }
        });
    }


    //与设备，删除记录
    private void deleteBleRecord2(String tradeNo) {
        ClientManager.getClient().deleteRecord(m_nowMac, tradeNo, new IGetRecordResponse() {
            @Override
            public void onResponseSuccess(String phone, final String bikeTradeNo, String timestamp, final String transType, String mackey, String index, final int Major, final int Minor, String vol) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("deleteBleRecord2===", transType+"==Major:"+ Major +"---Minor:"+Minor);

                        transtype = transType;
                        major = Major;
                        minor = Minor;

                        deleteBleRecord2(bikeTradeNo);
                    }
                });
            }

            @Override
            public void onResponseSuccessEmpty() {
                Log.e("biking=deleteBleRecord2", "Success===Empty");

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }

            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("biking=deleteBleRecord2", Code.toString(code));
                    }
                });

            }
        });
    }

    private void queryOpenState() {
        Log.e("queryOpenState===0", "===="+m_nowMac);

//        UIHelper.showProgress(this, R.string.collectState);
        ClientManager.getClient().queryOpenState(m_nowMac, new IQueryOpenStateResponse() {
            @Override
            public void onResponseSuccess(final boolean open) {
//                UIHelper.dismiss();

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("queryOpenState===", "===="+open);

//                        getBleRecord();

                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                        if (loadingDialogWithHelp != null && loadingDialogWithHelp.isShowing()){
                            loadingDialogWithHelp.dismiss();
                        }

                        if(open) {
                            ToastUtil.showMessageApp(context,"车锁未关，请手动关锁");

//                            customDialog10.show();
                        }else {
//                            submit(uid, access_token);

                            car_notification(1, 3, 0, 3,1,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

                        }


                    }
                });
            }

            @Override
            public void onResponseFail(final int code) {
                Log.e("queryOpenState===f",  Code.toString(code));
//                UIHelper.dismiss();

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
//                        ToastUtil.showMessageApp(context,Code.toString(code));

                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                });

            }
        });
    }

    //泺平===连接设备
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

                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
                if (loadingDialogWithHelp != null && loadingDialogWithHelp.isShowing()){
                    loadingDialogWithHelp.dismiss();
                }
            }

            @Override
            public void onResponseSuccess(BleGattProfile profile) {
//                BluetoothLog.v(String.format("profile:\n%s", profile));
//                refreshData(true);

                isStop = true;

                Log.e("connectDevice===", "Success==="+profile);

                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
                if (loadingDialogWithHelp != null && loadingDialogWithHelp.isShowing()){
                    loadingDialogWithHelp.dismiss();
                }

//                if (Globals.bType == 1) {
//                    ToastUtil.showMessageApp(context, "正在关锁中");
////                    getBleRecord();
//                }
            }
        });
    }


    //行运兔===type4
    void checkConnect(){
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("checkConnect===", cn+"==="+bleService.connect);

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

//                        if("".equals(oid)){
////                            memberEvent2();
//
//                            Toast.makeText(context,"扫码唤醒失败，重启手机蓝牙换辆车试试吧！",Toast.LENGTH_LONG).show();
////                            scrollToFinishActivity();
//                        }else{
//                            if(!isFinishing()){
//                                tzEnd();
//                            }
//                        }

                        if("".equals(oid)){
//                                scrollToFinishActivity();

                            Toast.makeText(context,"扫码唤醒失败，重启手机蓝牙换辆车试试吧！",Toast.LENGTH_LONG).show();
                        }else{
                            if(!activity.isFinishing()){
//                                    tzEnd();

//                                car_notification(1, 2, 2, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

                                unlock();
                            }
                        }

//                        popupwindow.dismiss();

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

                                    if("B1 25 80 00 00 56 1".equals(bleService.cc)){
                                        if (loadingDialog != null && loadingDialog.isShowing()){
                                            loadingDialog.dismiss();
                                        }

                                        Log.e("checkConnect===5", oid+"==="+bleService.cc);
                                        ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

                                        if(!activity.isFinishing()){
//                                            tzEnd();
                                            car_notification(1, 1, 0, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
                                        }
                                    }else{
//                                        closeEbike();
                                        if(!activity.isFinishing()){
//                                            tzEnd();

//                                            car_notification(1, 2, 1, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

                                            unlock();
                                        }
                                    }

//                                    popupwindow.dismiss();

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

    //小安
    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH})
    public void connectDevice(String imei) {
        if (apiClient != null) {
            apiClient.connectToIMEI(imei);

            Log.e("connectDevice===", "==="+imei);
        }

    }

    //小安
    @Override
    public void onConnect(BluetoothDevice bluetoothDevice) {
        isConnect = true;
        Log.e("mf===Xiaoan", "===Connect");

        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.e("mf===", "scan====1");

//                getCurrentorder2(uid, access_token);
//
//                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                    loadingDialog.setTitle("开锁中");
//                    loadingDialog.show();
//                }


                apiClient.setDefend(false, new BleCallback() {
                    @Override
                    public void onResponse(final Response response) {
                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("defend===", response.toString());

                                if(response.code==0){
                                    isFinish = true;

                                    ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

                                    if(!activity.isFinishing()){
//                                        tzEnd();
                                        car_notification(1, 1, 0, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
                                        popupwindow.dismiss();
                                    }
                                }else{
//                                    ToastUtil.showMessageApp(context,"开锁失败");

//                                                            submit(uid, access_token);
//                                    closeEbike();
                                    if(!activity.isFinishing()){
//                                        tzEnd();
//                                        car_notification(1, 2, 1, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
                                        unlock();
                                    }
                                }



                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }
                            }
                        });

                    }
                });


//                CustomDialog.Builder customBuilder = new CustomDialog.Builder(ActivityScanerCode.this);
//                if (0 == Tag){
//                    customBuilder.setMessage("扫码成功,是否开锁?");
//                }else {
//                    customBuilder.setMessage("输号成功,是否开锁?");
//                }
//                customBuilder.setTitle("温馨提示").setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//
//                        if (apiClient != null) {
//                            apiClient.onDestroy();
//                        }
//
//                        scrollToFinishActivity();
//
//                    }
//                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//
//
//
//                    }
//                }).setHint(false);
//                customBuilder.create().show();

            }
        }, 2 * 1000);

    }

    //小安
    @Override
    public void onDisConnect(BluetoothDevice bluetoothDevice) {
        isConnect = false;
        Log.e("mf===Xiaoan", "===DisConnect");

        m_myHandler.post(new Runnable() {
            @Override
            public void run() {

//                Toast.makeText(context,"扫码唤醒失败，重启手机蓝牙换辆车试试吧！",Toast.LENGTH_LONG).show();
//                scrollToFinishActivity();

//                if("".equals(oid)){
////                    memberEvent2();
//
//                    Toast.makeText(context,"扫码唤醒失败，重启手机蓝牙换辆车试试吧！",Toast.LENGTH_LONG).show();
////                    scrollToFinishActivity();
//                }else{
//                    if(!isFinishing()){
//                        tzEnd();
//                    }
//                }

                if("".equals(oid)){
//                                scrollToFinishActivity();

                    Toast.makeText(context,"扫码唤醒失败，重启手机蓝牙换辆车试试吧！",Toast.LENGTH_LONG).show();
                }else{
                    if(!activity.isFinishing()){
//                                    tzEnd();

                        car_notification(1, 2, 2, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
                    }
                }

                popupwindow.dismiss();

//                closeEbike();
//                submit(uid, access_token);
            }
        });


    }

    @Override
    public void onDeviceReady(BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void onReadRemoteRssi(int i) {

    }

    @Override
    public void onError(BluetoothDevice bluetoothDevice, String s, int i) {

    }

    @Override
    public void onBleAdapterStateChanged(int i) {

    }

    @Override
    public void onResult(ScanResult scanResult) {

    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        private String[] titles = new String[]{"用车券", "商家券"};
        private List<Fragment> fragmentList;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);

            CarCouponFragment carCouponFragment = new CarCouponFragment();
            MerchantCouponFragment merchantCouponFragment = new MerchantCouponFragment();

            fragmentList = new ArrayList<>();
            fragmentList.add(carCouponFragment);
            fragmentList.add(merchantCouponFragment);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return titles[position];
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mapView.onResume();

//        getFeedbackStatus();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }

        if (broadcastReceiver != null) {
            activity.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }

        if (broadcastReceiver2 != null) {
            activity.unregisterReceiver(broadcastReceiver2);
            broadcastReceiver2 = null;
        }

    }

    /**
     * 轮播图的监听
     *
     * @param position
     */
    @Override
    public void OnBannerClick(int position) {
        Toast.makeText(context, "你点了第" + (position + 1) + "张轮播图", Toast.LENGTH_SHORT).show();

//        initmPopupWindowView();
    }


    /**
     * 图片加载类
     */
    private class MyImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context.getApplicationContext())
                    .load(path)
                    .into(imageView);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (requestCode) {
                    case 0:
                        if (grantResults[0] == PERMISSION_GRANTED) {
                            // Permission Granted
                            if (permissions[0].equals(Manifest.permission.CALL_PHONE)){
                                Intent intent=new Intent();
                                intent.setAction(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + "0519-86999222"));
                                startActivity(intent);
                            }
                        }else {
                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
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
                                    localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                                    startActivity(localIntent);
                                    activity.finish();
                                }
                            });
                            customBuilder.create().show();
                        }
                        break;
                    case 100:
                        break;
                    case REQUEST_CODE_ASK_PERMISSIONS:
                        if (grantResults[0] == PERMISSION_GRANTED) {

                            initView();
                        } else {
                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                            customBuilder.setTitle("温馨提示").setMessage("您需要在设置里允许获取定位权限！")
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            activity.finish();
                                        }
                                    }).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Intent localIntent = new Intent();
                                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                                    startActivity(localIntent);
                                    activity.finish();
                                }
                            });
                            customBuilder.create().show();
                        }
                        break;
                    default:
                        onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        });


    }

    protected void connect() {
        Log.e("connect===0", isLookPsdBtn+"==="+m_nowMac+"==="+Build.VERSION.SDK_INT);

        isLookPsdBtn = false;
        BaseApplication.getInstance().getIBLE().stopScan();
        m_myHandler.sendEmptyMessage(0x99);     //直连
        BaseApplication.getInstance().getIBLE().startScan(new OnDeviceSearchListener() {
            @Override
            public void onScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {

                Log.e("connect===", isLookPsdBtn+"==="+isStop+"==="+m_nowMac+"==="+device.getName()+"==="+device.getAddress());

                if(isLookPsdBtn){
                    Log.e("connect===1", isLookPsdBtn+"==="+isStop+"==="+m_nowMac+"==="+device.getName()+"==="+device.getAddress());

                    BaseApplication.getInstance().getIBLE().stopScan();
                    m_myHandler.removeMessages(0x99);
                    return;
                }

                if (device==null || TextUtils.isEmpty(device.getAddress())) return;

                if (m_nowMac.equalsIgnoreCase(device.getAddress())){
                    Log.e("connect===2", m_nowMac+"==="+device.getName()+"==="+device.getAddress());

                    BaseApplication.getInstance().getIBLE().stopScan();

                    m_myHandler.removeMessages(0x99);
                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, MainFragment.this);
//                    m_myHandler.sendEmptyMessage(0x99);
                }
            }
        });
    }

    //type2、3
    @Override
    public void onTimeOut() {
        Log.e("onTimeOut===", isLookPsdBtn+"==="+type+"==="+m_nowMac+"===");

        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }

        if(isLookPsdBtn){
            isLookPsdBtn = false;

            Log.e("onTimeOut===1", isLookPsdBtn+"==="+type+"==="+m_nowMac+"===");
            return;
        }

        isLookPsdBtn = false;

        if("3".equals(type)){
            if(isOpenLock){
                unlock();
            }
        }else{
            car_notification(isAgain?2:1, 2, 2, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
        }
    }

    //type2、3
    @Override
    public void onDisconnect(int state) {
        Log.e("onDisconnect===", isLookPsdBtn+"==="+type+"==="+m_nowMac+"==="+state);

        m_myHandler.sendEmptyMessageDelayed(0, 1000);

        if(isLookPsdBtn){
            isLookPsdBtn = false;

            Log.e("onDisconnect===1", isLookPsdBtn+"==="+type+"==="+m_nowMac+"==="+state);
            return;
        }

        isLookPsdBtn = false;

        if("3".equals(type)){
            if(isOpenLock){
                unlock();
            }
        }else{
            car_notification(isAgain?2:1, 2, 1, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
        }
    }

    //type2、3
    @Override
    public void onServicesDiscovered(String name, String address) {
        Log.e("onServicesDiscovered===", isLookPsdBtn+"==="+type+"==="+m_nowMac+"==="+name+"==="+address);

        isLookPsdBtn = true;

        BaseApplication.getInstance().getIBLE().stopScan();

        getToken();
    }

    private void getToken() {
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BaseApplication.getInstance().getIBLE().getToken();
            }
        }, 500);
    }

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

//                            if (loadingDialog != null && loadingDialog.isShowing()) {
//                                loadingDialog.dismiss();
//                            }

                            m_myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    BaseApplication.getInstance().getIBLE().getBattery();
                                }
                            }, 1000);

                            Log.e("mf===TOKEN_ACTION", isLookPsdBtn+"===="+isOpenLock);


                            break;
                        case Config.BATTERY_ACTION:
                            Log.e("mf===BATTERY_ACTION", isLookPsdBtn+"===="+isOpenLock);

                            if (!TextUtils.isEmpty(data)) {
//                                quantity = String.valueOf(Integer.parseInt(data, 16));

                                if(isOpenLock){
                                    BaseApplication.getInstance().getIBLE().openLock();

                                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                                        loadingDialog.setTitle("开锁中");
                                        loadingDialog.show();
                                    }
                                }else{
                                    BaseApplication.getInstance().getIBLE().getLockStatus();
                                }
                            }else {
//                                quantity = "";
                            }
                            break;
                        case Config.OPEN_ACTION:
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

                            Log.e("mf===OPEN_ACTION0", TextUtils.isEmpty(data)+"==="+type+"==="+isAgain+"==="+isLookPsdBtn);

                            if (TextUtils.isEmpty(data)) {
                                ToastUtil.showMessageApp(context,"开锁失败,请重试");

                                if("3".equals(type)){
                                    if(isOpenLock){
                                        unlock();
                                    }
                                }else{
                                    if(!isAgain){
                                        popupwindow.dismiss();
                                    }

                                    car_notification(isAgain?2:1, 2, 1, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
                                }
                            } else {
                                isOpen = true;

                                ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");
                                Log.e("mf===", "OPEN_ACTION==="+isOpen);

                                if(!isAgain){
                                    popupwindow.dismiss();

//                                  ll_top.setVisibility(View.GONE);
                                    rl_ad.setVisibility(View.GONE);
                                    ll_top_biking.setVisibility(View.VISIBLE);

                                    tv_biking_codenum.setText(codenum);
                                }else{

                                }

                                m_myHandler.sendEmptyMessage(7);
                            }
                            break;
                        case Config.CLOSE_ACTION:
                            if (TextUtils.isEmpty(data)) {
                            } else {
                            }

                            Log.e("mf===", "CLOSE_ACTION===");

                            break;
                        case Config.RESET_ACTION:
                            if (TextUtils.isEmpty(data)) {
                            } else {
                            }

                            Log.e("mf===", "RESET_ACTION===");

                            break;
                        case Config.LOCK_STATUS_ACTION:
                            Log.e("biking===", "LOCK_STATUS_ACTION==="+isStop);

                            isStop = true;
                            isLookPsdBtn = true;

                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

//                            if(isEndBtn){
//                                m_myHandler.sendEmptyMessage(6);
//                            }

//                            if (lockLoading != null && lockLoading.isShowing()){
//                                lockLoading.dismiss();
//                            }
                            if (TextUtils.isEmpty(data)) {
                                ToastUtil.showMessageApp(context,"锁已关闭");
                                Log.e("biking===", "biking===锁已关闭==="+first3);

                                if(!isEndBtn) break;

                                m_myHandler.sendEmptyMessage(6);

                                Log.e("biking===", "biking2===锁已关闭" + macList2.size());

                            } else {
                                //锁已开启
                                ToastUtil.showMessageApp(context,"车锁未关，请手动关锁");
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

    protected Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 0:
                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, MainFragment.this);
                    break;
                case 1:
                    BaseApplication.getInstance().getIBLE().refreshCache();
                    BaseApplication.getInstance().getIBLE().close();
                    BaseApplication.getInstance().getIBLE().disconnect();
//                    BaseApplication.getInstance().getIBLE().disableBluetooth();
//                    scrollToFinishActivity();

                    break;
                case 2:
                    break;
                case 3:
                    stopXB();

                    Log.e("biking===3", type+"==="+macList.size()+"==="+isContainsList.contains(true));

                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }

//                    rl_msg.setVisibility(View.GONE);
//                    if (polyline != null) {
//                        polyline.remove();
//                    }

                    if(macList.size()>0 || isContainsList.contains(true)){
                        isTemp = false;

                        if("4".equals(type)){
                            endBtn4();
                        }else if("5".equals(type) || "6".equals(type)){


                            endBtn5();
                        }else if("7".equals(type)){
                            endBtn7();
                        }else{

                            if("3".equals(type)){
                                endBtn3();
                            }else{
                                endBtn();
                            }
                        }

                    }else{
//                        rl_msg.setVisibility(View.VISIBLE);   //TODO
//                        minPolygon();
                    }


                    break;
                case 6:
                    //蓝牙还车成功
                    car_notification(1, 3, 0, 3,1,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

                    break;

                case 7:
                    //蓝牙开锁成功
                    car_notification(isAgain?2:1, 1, 0, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

                    break;


                case 9:
                    break;

                case 0x98://搜索超时
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

//                                memberEvent2();

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

//                                scrollToFinishActivity();
                            }
                        }
                    }, 15 * 1000);
                    break;

                case 0x99://搜索超时
                    Log.e("0x99===", "==="+isStop);

//                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, MainFragment.this);
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("0x99===timeout0", isLookPsdBtn+"==="+isStop+"==="+type);

                            if (!isLookPsdBtn){
                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                    loadingDialog.dismiss();
                                }

//                                memberEvent2();

                                if(!isOpenLock){
                                    Toast.makeText(context,"唤醒失败，重启手机蓝牙试试吧！",Toast.LENGTH_LONG).show();
                                }

                                BaseApplication.getInstance().getIBLE().stopScan();
                                BaseApplication.getInstance().getIBLE().refreshCache();
                                BaseApplication.getInstance().getIBLE().close();
                                BaseApplication.getInstance().getIBLE().disconnect();
//                                scrollToFinishActivity();

                                Log.e("0x99===timeout", isLookPsdBtn+"==="+isStop+"==="+type);

                                if("3".equals(type)){
                                    if(isOpenLock){
                                        unlock();
                                    }
                                }else{
                                    car_notification(isAgain?2:1, 2, 2, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
                                }

                            }
                        }
                    }, 10 * 1000);
                    break;

                default:
                    break;
            }
            return false;
        }
    });

    //蓝牙锁type2
    public void endBtn(){
//        oid = "19120309293774228796";
//        m_nowMac = "50:8C:B1:87:1E:FA";
        Log.e("biking===endBtn_0", isLookPsdBtn+"==="+force_backcar+"==="+isTwo+"==="+macList.size()+"==="+isContainsList.contains(true));

        if(force_backcar==1 && isTwo){
            ToastUtil.showMessageApp(context,"锁已关闭");
            Log.e("biking===", "endBtn===锁已关闭==="+first3);

            if(!isEndBtn) return;

            m_myHandler.sendEmptyMessage(6);

            return;
        }

        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context,LoginActivity.class);
        }else {
            Log.e("biking===endBtn",macList.size()+"==="+isContainsList.contains(true)+"==="+type+"==="+isContainsList);

//            rl_msg.setVisibility(View.GONE);
//            if (polyline != null) {
//                polyline.remove();
//            }

            if (macList.size() > 0){
//                flag = 2;
                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                    scrollToFinishActivity();
                }

                if (!BaseApplication.getInstance().getIBLE().isEnable()){
                    BaseApplication.getInstance().getIBLE().enableBluetooth();
                    return;
                }
                if (BaseApplication.getInstance().getIBLE().getConnectStatus()){
                    if (loadingDialog != null && !loadingDialog.isShowing()){
                        loadingDialog.setTitle("请稍等");
                        loadingDialog.show();
                    }

                    Log.e("biking===endBtn_2",macList.size()+"==="+isLookPsdBtn+"==="+force_backcar);

                    isLookPsdBtn = false;
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

                            if(!isLookPsdBtn){
                                if(force_backcar==0){
                                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                    customBuilder.setTitle("连接失败").setMessage("蓝牙连接失败，请靠近车锁，重启软件后再试")
                                            .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                    customBuilder.create().show();
                                }else{
                                    clickCountDeal();
                                }
                            }
                        }
                    }, 10 * 1000);

                    macList2 = new ArrayList<> (macList);
                    BaseApplication.getInstance().getIBLE().getLockStatus();
                } else {
                    Log.e("biking===endBtn_3",macList.size()+"==="+isLookPsdBtn+"==="+force_backcar);

                    if (loadingDialog != null && !loadingDialog.isShowing()){
                        loadingDialog.setTitle("正在连接");
                        loadingDialog.show();
                    }

                    isLookPsdBtn = false;
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

                            if(!isLookPsdBtn){
                                stopScan = true;
//                              BaseApplication.getInstance().getIBLE().stopScan();
                                BaseApplication.getInstance().getIBLE().refreshCache();
                                BaseApplication.getInstance().getIBLE().close();
                                BaseApplication.getInstance().getIBLE().disconnect();

                                if (!BaseApplication.getInstance().getIBLE().getConnectStatus()){
                                    if(force_backcar==0){
                                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                        customBuilder.setTitle("连接失败").setMessage("蓝牙连接失败，请靠近车锁，重启软件后再试")
                                                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        customBuilder.create().show();
                                    }else{
                                        clickCountDeal();
                                    }
                                }
                            }
                        }
                    }, 10 * 1000);

                    isOpenLock = false;
                    connect();
                }

                return;
            }

            if (isContainsList.contains(true)){
//                flag = 2;
                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                    scrollToFinishActivity();
                }
                //蓝牙锁
                if (!BaseApplication.getInstance().getIBLE().isEnable()){
                    BaseApplication.getInstance().getIBLE().enableBluetooth();
                    return;
                }
                if (BaseApplication.getInstance().getIBLE().getConnectStatus()){
                    Log.e("biking===endBtn_4",macList.size()+"==="+isLookPsdBtn+"==="+force_backcar);

                    if (loadingDialog != null && !loadingDialog.isShowing()){
                        loadingDialog.setTitle("请稍等");
                        loadingDialog.show();
                    }

                    isLookPsdBtn = false;
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

                            if(!isLookPsdBtn){
                                if(force_backcar==0){
                                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                    customBuilder.setTitle("连接失败").setMessage("蓝牙连接失败，请靠近车锁，重启软件后再试")
                                            .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                    customBuilder.create().show();
                                }else{
                                    clickCountDeal();
                                }
                            }
                        }
                    }, 10 * 1000);

                    macList2 = new ArrayList<> (macList);
                    BaseApplication.getInstance().getIBLE().getLockStatus();
                }else {
                    Log.e("biking===endBtn_5",macList.size()+"==="+isLookPsdBtn+"==="+force_backcar);

                    if (loadingDialog != null && !loadingDialog.isShowing()){
                        loadingDialog.setTitle("正在连接");
                        loadingDialog.show();
                    }

                    isLookPsdBtn = false;
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

                            if(!isLookPsdBtn){
                                stopScan = true;
//                                BaseApplication.getInstance().getIBLE().stopScan();
                                BaseApplication.getInstance().getIBLE().refreshCache();
                                BaseApplication.getInstance().getIBLE().close();
                                BaseApplication.getInstance().getIBLE().disconnect();

                                if (!BaseApplication.getInstance().getIBLE().getConnectStatus()){
                                    if(force_backcar==0){
                                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                        customBuilder.setTitle("连接失败").setMessage("蓝牙连接失败，请靠近车锁，重启软件后再试")
                                                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        customBuilder.create().show();
                                    }else{
                                        clickCountDeal();
                                    }
                                }
                            }
                        }
                    }, 15 * 1000);

                    isOpenLock = false;
                    connect();

                }
            }
//            else {        //TODO
//                rl_msg.setVisibility(View.VISIBLE);
//
//                minPolygon();
//            }

        }
    }

    public void endBtn3(){
        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            Log.e("biking===endBtn3",macList.size()+"==="+type+"==="+first3);

//            rl_msg.setVisibility(View.GONE);
//            if (polyline != null) {
//                polyline.remove();
//            }

            if (macList.size() > 0){
//                flag = 2;
                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                    scrollToFinishActivity();
                }
                if (!BaseApplication.getInstance().getIBLE().isEnable()){
                    BaseApplication.getInstance().getIBLE().enableBluetooth();
                    return;
                }
                if (BaseApplication.getInstance().getIBLE().getConnectStatus()){
                    if (loadingDialog != null && !loadingDialog.isShowing()){
                        loadingDialog.setTitle("请稍等");
                        loadingDialog.show();
                    }

                    isLookPsdBtn = false;
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();

                                if(!isLookPsdBtn){
                                    if(first3){
                                        first3 = false;
                                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                        customBuilder.setTitle("连接失败").setMessage("蓝牙连接失败，请靠近车锁，重启软件后再试")
                                                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        customBuilder.create().show();

                                        clickCountDeal();
                                    }else{
//                                        carClose();
//                                        lock(); //TODO
                                        car_notification(1, 3, 0, 3,3,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

                                    }
                                }
                            }
                        }
                    }, 10 * 1000);

                    macList2 = new ArrayList<> (macList);
                    BaseApplication.getInstance().getIBLE().getLockStatus();
                } else {
                    if (loadingDialog != null && !loadingDialog.isShowing()){
                        loadingDialog.setTitle("正在连接");
                        loadingDialog.show();
                    }

                    isLookPsdBtn = false;
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

                            Log.e("biking===endBtn3_1",isLookPsdBtn+"==="+type+"==="+first3);

                            if(!isLookPsdBtn){
                                stopScan = true;
                                BaseApplication.getInstance().getIBLE().refreshCache();
                                BaseApplication.getInstance().getIBLE().close();
                                BaseApplication.getInstance().getIBLE().disconnect();

                                if(first3){
                                    first3 = false;
                                    customDialog3.show();

                                    clickCountDeal();
                                }else{
//                                    carClose();
//                                    lock();     //TODO
                                    car_notification(1, 3, 0, 3,3,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

                                }
                            }
                        }
                    }, 10 * 1000);

                    isOpenLock = false;
                    connect();
                }

                return;
            }

            if (isContainsList.contains(true)){

//                flag = 2;
                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                    scrollToFinishActivity();
                }
                //蓝牙锁
                if (!BaseApplication.getInstance().getIBLE().isEnable()){
                    BaseApplication.getInstance().getIBLE().enableBluetooth();
                    return;
                }
                if (BaseApplication.getInstance().getIBLE().getConnectStatus()){
                    if (loadingDialog != null && !loadingDialog.isShowing()){
                        loadingDialog.setTitle("请稍等");
                        loadingDialog.show();
                    }

                    isLookPsdBtn = false;
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();

                                if(!isLookPsdBtn){
                                    if(first3){
                                        first3 = false;
                                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                        customBuilder.setTitle("连接失败").setMessage("蓝牙连接失败，请靠近车锁，重启软件后再试")
                                                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        customBuilder.create().show();

                                        clickCountDeal();
                                    }else{
//                                        lock();     //TODO
                                        car_notification(1, 3, 0, 3,1,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
                                    }
                                }

                            }
                        }
                    }, 10 * 1000);

                    macList2 = new ArrayList<> (macList);
                    BaseApplication.getInstance().getIBLE().getLockStatus();
                }else {
                    if (loadingDialog != null && !loadingDialog.isShowing()){
                        loadingDialog.setTitle("正在连接");
                        loadingDialog.show();
                    }

                    isOpenLock = false;
                    connect();

                    isLookPsdBtn = false;
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

                            Log.e("biking===endBtn3_2",isLookPsdBtn+"==="+type+"==="+first3);

                            if(!isLookPsdBtn){
                                stopScan = true;
//                                  BaseApplication.getInstance().getIBLE().stopScan();
                                BaseApplication.getInstance().getIBLE().refreshCache();
                                BaseApplication.getInstance().getIBLE().close();
                                BaseApplication.getInstance().getIBLE().disconnect();

                                if(first3){
                                    first3 = false;

                                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                    customBuilder.setTitle("连接失败").setMessage("蓝牙连接失败，请靠近车锁，重启软件后再试")
                                            .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                    customBuilder.create().show();

                                    clickCountDeal();
                                }else{
//                                    lock();     //TODO
                                    car_notification(1, 3, 0, 3,1,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
                                }
                            }
                        }
                    }, 10 * 1000);

                }
            }
//            else {        //TODO
//                rl_msg.setVisibility(View.GONE);
//
//                minPolygon();
//
//                clickCountDeal();
//            }
        }
    }

    public void backCar_r(){

    }

    public void endBtn4(){
        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
//            Log.e("biking===endBtn4",macList.size()+"==="+type+"==="+first3);
//
//            rl_msg.setVisibility(View.GONE);
//            if (polyline != null) {
//                polyline.remove();
//            }
//
//            if (macList.size() > 0){
//                closeEbike2();
//
//                return;
//            }
//
//            if (isContainsList.contains(true)){
//
//                closeEbike2();
//
//            }else {
//                rl_msg.setVisibility(View.GONE);
//
//                minPolygon();
//            }


        }
    }

    public void endBtn5(){
        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context,LoginActivity.class);
        }else {
            Log.e("biking===endBtn5",macList.size()+"==="+isContainsList.contains(true)+"==="+type+"==="+m_nowMac);

//            rl_msg.setVisibility(View.GONE);
//            if (polyline != null) {
//                polyline.remove();
//            }


            if (macList.size() > 0){
                //泺平锁
                queryState();

                return;
            }

            if (isContainsList.contains(true)){
                queryState();

            }else {
//                rl_msg.setVisibility(View.GONE);
//
//                minPolygon();
//
//                clickCountDeal();
            }


        }
    }

    public void endBtn7(){
        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
//            Log.e("biking===endBtn7",macList.size()+"==="+type+"==="+deviceuuid+"==="+isConnect);
//
//            rl_msg.setVisibility(View.GONE);
//            if (polyline != null) {
//                polyline.remove();
//            }
//
//            if (macList.size() > 0){
////                flag = 2;
//
//                closeEbike_XA();
//
//                return;
//            }
//
//            if (isContainsList.contains(true)){
//
//                closeEbike_XA();
//
//            }else {
//                rl_msg.setVisibility(View.GONE);
//
//                minPolygon();
//            }


        }
    }


    public void clickCountDeal(){
        if("2".equals(type)){
            if(force_backcar==1){
                isTwo = true;
                customDialog9.show();
            }
        }

    }

    public void queryState(){
        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//            scrollToFinishActivity();
        }
        //蓝牙锁
        BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            ToastUtil.showMessageApp(context, "获取蓝牙失败");
//            scrollToFinishActivity();
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Log.e("queryState===1", "===");

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 188);
        } else {
            Log.e("queryState===2", "==="+isLookPsdBtn);

            if(!isLookPsdBtn){   //没连上

                if (loadingDialogWithHelp != null && !loadingDialogWithHelp.isShowing()) {
                    loadingDialogWithHelp.setTitle("正在唤醒车锁");
                    loadingDialogWithHelp.show();
                }

                ClientManager.getClient().stopSearch();
                ClientManager.getClient().disconnect(m_nowMac);
                ClientManager.getClient().disconnect(m_nowMac);
                ClientManager.getClient().disconnect(m_nowMac);
                ClientManager.getClient().disconnect(m_nowMac);
                ClientManager.getClient().disconnect(m_nowMac);
                ClientManager.getClient().disconnect(m_nowMac);
//                ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
//                ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);

                SearchRequest request = new SearchRequest.Builder().searchBluetoothLeDevice(0).build();    //duration为0时无限扫描

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                ClientManager.getClient().search(request, mSearchResponse3);

                m_myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isLookPsdBtn){
                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                            if (loadingDialogWithHelp != null && loadingDialogWithHelp.isShowing()) {
                                loadingDialogWithHelp.dismiss();
                            }
                            Toast.makeText(context,"唤醒失败，重启手机蓝牙试试吧！",Toast.LENGTH_LONG).show();

                            ClientManager.getClient().stopSearch();
                            ClientManager.getClient().disconnect(m_nowMac);
                            ClientManager.getClient().disconnect(m_nowMac);
                            ClientManager.getClient().disconnect(m_nowMac);
                            ClientManager.getClient().disconnect(m_nowMac);
                            ClientManager.getClient().disconnect(m_nowMac);
                            ClientManager.getClient().disconnect(m_nowMac);
                            ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
                            ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);

//                            scrollToFinishActivity();
                        }
                    }
                }, 15 * 1000);
            }else{
                if (loadingDialog != null && !loadingDialog.isShowing()){
                    loadingDialog.setTitle("正在连接");
                    loadingDialog.show();
                }

                ClientManager.getClient().getStatus(m_nowMac, new IGetStatusResponse() {
                    @Override
                    public void onResponseSuccess(String version, String keySerial, String macKey, String vol) {

                        Log.e("getStatus===Success", "===");

                        keySource = keySerial;
                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                queryOpenState();
                            }
                        });
                    }

                    @Override
                    public void onResponseFail(final int code) {
                        Log.e("getStatus===Fail", "===");

                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showMessageApp(context,"车锁未关，请手动关锁");

                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                    loadingDialog.dismiss();
                                }

                            }
                        });
                    }
                });
            }

        }
    }


    @Override
    public void onMapClick(LatLng point) {
        Log.e("onMapClick===", ll_top.isShown()+"===" + routeOverLay+"===" + ll_top_navi);

        if(!ll_top.isShown()){
            routeOverLay.removeFromMap();

            ll_top.setVisibility(View.VISIBLE);
            ll_top_navi.setVisibility(View.GONE);
        }
    }

    @Override
    public void onInitNaviFailure() { }

    @Override
    public void onInitNaviSuccess() {
        Log.e("onInitNaviSuccess===", "===");
    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
//        if(isHidden) return;

        Log.e("onCalculateRouteSuc=", "==="+routeOverLay);

//        routeOverlays.clear();
//        ways.clear();

        if(routeOverLay != null){
            routeOverLay.removeFromMap();
        }

//        mAMapNavi.startNavi(NaviType.EMULATOR);

        AMapNaviPath path = mAMapNavi.getNaviPath();

        drawRoutes(-1, path);   // 单路径不需要进行路径选择，直接传入－1即可
//        showMarkInfo(path);
    }

    private void drawRoutes(int routeId, AMapNaviPath path) {
//        calculateSuccess = true;
        aMap.moveCamera(CameraUpdateFactory.changeTilt(0));
        //路径绘制图层
        routeOverLay = new RouteOverLay(aMap, path, context);
        routeOverLay.setTrafficLine(false);
        routeOverLay.setStartPointBitmap(null);
        routeOverLay.setEndPointBitmap(null);
        routeOverLay.addToMap();


        Log.e("drawRoutes===", "==="+path.getAllLength());

        tv_navi_distance.setText(path.getAllLength()+"米");

//        routeOverlays.put(routeId, routeOverLay);
    }

    @Override
    public void onStartNavi(int i) {}

    @Override
    public void onTrafficStatusUpdate() {}

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {}

    @Override
    public void onGetNavigationText(int i, String s) {}

    @Override
    public void onGetNavigationText(String s) {}

    @Override
    public void onEndEmulatorNavi() {}

    @Override
    public void onArriveDestination() {}

    @Override
    public void onCalculateRouteFailure(int i) {}

    @Override
    public void onReCalculateRouteForYaw() {}

    @Override
    public void onReCalculateRouteForTrafficJam() {}

    @Override
    public void onArrivedWayPoint(int i) {}

    @Override
    public void onGpsOpenStatus(boolean b) {}

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {}

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {}

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {}

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {}

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {}

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {}

    @Override
    public void hideCross() {}

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {}

    @Override
    public void hideModeCross() {}

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {}

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {}

    @Override
    public void hideLaneInfo() {}

    @Override
    public void notifyParallelRoad(int i) {}

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {}

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {}

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {}

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {}

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {}

    @Override
    public void onPlayRing(int i) {}

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {}

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {}

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {}

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        m_myHandler.post(new Runnable() {
            @Override
            public void run() {

                Log.e("mf===requestCode", requestCode+"==="+resultCode);

                switch (requestCode) {

                    case 1:
                        if (resultCode == RESULT_OK) {
                            codenum = data.getStringExtra("codenum");
                            m_nowMac = data.getStringExtra("m_nowMac");
                            type = data.getStringExtra("type");
                            bleid = data.getStringExtra("bleid");
                            deviceuuid = data.getStringExtra("deviceuuid");
                            price = data.getStringExtra("price");

                            Log.e("mf===requestCode1", type+"==="+bleid +"==="+deviceuuid+"==="+price);

//                          String result = data.getStringExtra("QR_CODE");
//                          upcarmap(result);
//                          lock(result);

                            price = "<b><font color=\"#000000\">my html text</font></b>";
                            price = "<p style=\"color: #000000; font-size: 26px;\">my html text</p>";
                            price = "<p style=\"color: #00ff00\">my html text</p>";
                            price = "<p style=\"font-size:26px\">my html text</p>";
                            price = "<p style=\"color:#00ff00\"><font size=\"20\">my html text</font></p>";
                            price = "<font size=\"5\">my html text</font>";

                            price = "<p><font color=\"#000000\" size=\"20px\">" + "要显示的数据" + "</font></p>";


                            initmPopupRentWindowView();
//                          initmPopupRentWindowView("<html>"+price+"<\\/html>");

                            isAgain = false;

                            if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                            }
                            //蓝牙锁
                            if (mBluetoothAdapter == null) {
                                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
                                mBluetoothAdapter = bluetoothManager.getAdapter();
                            }

                            if (mBluetoothAdapter == null) {
                                ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                                      finish();
                                return;
                            }

                            if (!mBluetoothAdapter.isEnabled()) {
                                flagm = 1;
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, 188);
                            } else {
                                if ("2".equals(type) || "3".equals(type)){

                                    isLookPsdBtn = true;

//                                    if (loadingDialog != null && !loadingDialog.isShowing()){
//                                        loadingDialog.setTitle("正在唤醒车锁");
//                                        loadingDialog.show();
//                                    }
//
//                                    m_myHandler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//
//                                            connect();
//
//                                            m_myHandler.postDelayed(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    if (!isStop){
//                                                        if (loadingDialog != null && loadingDialog.isShowing()) {
//                                                            loadingDialog.dismiss();
//                                                        }
//                                                        Toast.makeText(context,"唤醒失败，重启手机蓝牙试试吧！",Toast.LENGTH_LONG).show();
//                                                        BaseApplication.getInstance().getIBLE().refreshCache();
//                                                        BaseApplication.getInstance().getIBLE().disconnect();
//                                                        BaseApplication.getInstance().getIBLE().close();
//
//                                                        scrollToFinishActivity();
//                                                    }
//                                                }
//                                            }, 15 * 1000);
//                                        }
//                                    }, 2 * 1000);

//                                  closeBroadcast();     //TODO
//                                  activity.registerReceiver(Config.initFilter());
                                    GlobalParameterUtils.getInstance().setLockType(LockType.MTS);
                                }else if("4".equals(type)){
                                    isLookPsdBtn = true;

                                    BLEService.bluetoothAdapter = mBluetoothAdapter;

                                    bleService.view = context;
                                    bleService.showValue = true;
                                }else if ("5".equals(type)  || "6".equals(type)) {
                                    Log.e("initView===5", "==="+isLookPsdBtn);

                                    //                        ll_1.setVisibility(View.VISIBLE);

//                                    WindowManager windowManager = getWindowManager();     //TODO
//                                    Display display = windowManager.getDefaultDisplay();
//                                    WindowManager.LayoutParams lp = advDialog.getWindow().getAttributes();
//                                    lp.width = (int) (display.getWidth() * 1);
//                                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                                    advDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
//                                    advDialog.getWindow().setAttributes(lp);
//                                    advDialog.show();

                                    isLookPsdBtn = true;
                                    ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
                                    ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);



//                                    if (loadingDialogWithHelp != null && !loadingDialogWithHelp.isShowing()){
//                                        loadingDialogWithHelp.setTitle("正在唤醒车锁");
//                                        loadingDialogWithHelp.show();
//                                    }
//
//                                    ClientManager.getClient().stopSearch();
//                                    ClientManager.getClient().disconnect(m_nowMac);
//                                    ClientManager.getClient().disconnect(m_nowMac);
//                                    ClientManager.getClient().disconnect(m_nowMac);
//                                    ClientManager.getClient().disconnect(m_nowMac);
//                                    ClientManager.getClient().disconnect(m_nowMac);
//                                    ClientManager.getClient().disconnect(m_nowMac);
//                                    ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
//                                    ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener2);
//
//                                    m_myHandler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            connectDeviceLP();
//                                            ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener2);
//                                            ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);
//
//                                            m_myHandler.postDelayed(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    if (!isLookPsdBtn){
//                                                        if (loadingDialog != null && loadingDialog.isShowing()) {
//                                                            loadingDialog.dismiss();
//                                                        }
//                                                        Toast.makeText(context,"唤醒失败，重启手机蓝牙试试吧！",Toast.LENGTH_LONG).show();
//
//                                                        ClientManager.getClient().stopSearch();
//                                                        ClientManager.getClient().disconnect(m_nowMac);
//                                                        ClientManager.getClient().disconnect(m_nowMac);
//                                                        ClientManager.getClient().disconnect(m_nowMac);
//                                                        ClientManager.getClient().disconnect(m_nowMac);
//                                                        ClientManager.getClient().disconnect(m_nowMac);
//                                                        ClientManager.getClient().disconnect(m_nowMac);
//                                                        ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
//                                                        ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);
//
//                                                        scrollToFinishActivity();
//                                                    }
//                                                }
//                                            }, 15 * 1000);
//
//                                        }
//                                    }, 2 * 1000);

                                }else if ("7".equals(type)) {
                                    isLookPsdBtn = true;

                                }

                            }

                            refreshLayout.setVisibility(View.VISIBLE);

                        } else {
                            Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                        }

                        Log.e("requestCode===1", "==="+resultCode);
                        break;

                    case PRIVATE_CODE:
                        openGPSSettings();
                        break;

                    case 188:
                        /*
                        if (resultCode == RESULT_OK) {
                            if (null != loadingDialog && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                            if (loadingDialog != null && !loadingDialog.isShowing()) {
                                loadingDialog.setTitle("正在唤醒车锁");
                                loadingDialog.show();
                            }

                            Log.e("188===", m_nowMac+"==="+type+"===");


                            if("4".equals(type)){

                                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                                mBluetoothAdapter = bluetoothManager.getAdapter();

                                BLEService.bluetoothAdapter = mBluetoothAdapter;

                                bleService.view = context;
                                bleService.showValue = true;

                                bleService.connect(m_nowMac);
                                checkConnect();

                            }else if("5".equals(type) || "6".equals(type)){


//                                iv_help.setVisibility(View.VISIBLE);

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

                            }else if("7".equals(type)){

                                XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                                builder.setBleStateChangeListener(MainFragment.this);
                                builder.setScanResultCallback(MainFragment.this);
                                apiClient = builder.build();

                                MainActivityPermissionsDispatcher.connectDeviceWithPermissionCheck(MainFragment.this, deviceuuid);

                                m_myHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isConnect){
                                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                                loadingDialog.dismiss();
                                            }

//                                    closeEbike();

                                            if(!isFinishing()){
                                                car_notification(1, 2, 2, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
                                                popupwindow.dismiss();
                                            }
                                        }
                                    }
                                }, 15 * 1000);

                            }else{
                                connect();
                            }
                        }else{
                            ToastUtil.showMessageApp(context, "需要打开蓝牙");

                            Log.e("188===fail", oid+"==="+isFinishing());

                            if("".equals(oid)){
//                                scrollToFinishActivity();

                            }else{
                                if(!isFinishing()){
//                                    tzEnd();

                                    car_notification(1, 2, 1, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

                                }
                            }

                            popupwindow.dismiss();
                        }
                        */

                    default:

                        break;

                }

                /*
                if (resultCode == RESULT_OK) {
                    switch (requestCode) {
                        case 1:
                            String price = data.getStringExtra("price");

                            Log.e("mf===requestCode1", "==="+price);

                            break;

                        case 188:
//                            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
//                            mBluetoothAdapter = bluetoothManager.getAdapter();
//
//                            if (mBluetoothAdapter == null) {
//                                ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                                activity.finish();
//                                return;
//                            }
//                            if (!mBluetoothAdapter.isEnabled()) {
//                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                                startActivityForResult(enableBtIntent, 188);
//                            }else{
//                            }
                            break;

                        default:
                            break;

                    }
                } else {
                    switch (requestCode) {
                        case PRIVATE_CODE:
                            openGPSSettings();
                            break;

                        case 188:
                            ToastUtil.showMessageApp(context, "需要打开蓝牙");
                            AppManager.getAppManager().AppExit(context);
                            break;
                        default:
                            break;
                    }
                }
                */
            }
        });

    }

//    protected void registerReceiver(IntentFilter intentfilter) {
//        if (internalReceiver == null) {
//            internalReceiver = new InternalReceiver();
//        }
//        activity.registerReceiver(internalReceiver, intentfilter);
//    }
//
//    protected class InternalReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            handleReceiver(context, intent);
//
//        }
//    };
}