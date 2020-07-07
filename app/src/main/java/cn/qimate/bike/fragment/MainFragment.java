package cn.qimate.bike.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
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
import com.autonavi.ae.pos.Feedback;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.bumptech.glide.Glide;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
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
import com.sofi.blelocker.library.protocol.ITemporaryActionResponse;
import com.sofi.blelocker.library.search.SearchRequest;
import com.sofi.blelocker.library.search.SearchResult;
import com.sofi.blelocker.library.search.response.SearchResponse;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.config.LockType;
import com.sunshine.blelibrary.inter.OnConnectionListener;
import com.sunshine.blelibrary.inter.OnDeviceSearchListener;
import com.sunshine.blelibrary.mode.GetLockStatusTxOrder;
import com.sunshine.blelibrary.mode.GetTokenTxOrder;
import com.sunshine.blelibrary.mode.OpenLockTxOrder;
import com.sunshine.blelibrary.mode.XinbiaoTxOrder;
import com.sunshine.blelibrary.utils.ConvertUtils;
import com.sunshine.blelibrary.utils.EncryptUtils;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;
import com.tbit.tbitblesdk.Bike.TbitBle;
import com.tbit.tbitblesdk.Bike.model.BikeState;
import com.tbit.tbitblesdk.Bike.services.command.callback.StateCallback;
import com.tbit.tbitblesdk.protocol.Packet;
import com.tbit.tbitblesdk.protocol.callback.PacketCallback;
import com.tbit.tbitblesdk.protocol.callback.ResultCallback;
import com.tbit.tbitblesdk.user.entity.W206State;
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
//import com.youth.banner.loader.ImageLoader;
import com.zxing.lib.scaner.QRCodeUtil;
import com.zxing.lib.scaner.activity.ActivityScanerCode;
import com.zxing.lib.scaner.activity.MainFragmentPermissionsDispatcher;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.http.rdata.RRent;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.nostra13.universalimageloader.core.DisplayImageOptions;
import cn.nostra13.universalimageloader.core.ImageLoader;
import cn.nostra13.universalimageloader.core.assist.ImageScaleType;
import cn.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.ActionCenterActivity;
import cn.qimate.bike.activity.AuthCenterActivity;
import cn.qimate.bike.activity.BillActivity;
import cn.qimate.bike.activity.CarFaultActivity;
import cn.qimate.bike.activity.CarFaultProActivity;
import cn.qimate.bike.activity.ChangePhoneActivity;
import cn.qimate.bike.activity.ClientManager;
import cn.qimate.bike.activity.CrashHandler;
import cn.qimate.bike.activity.CurRoadBikedActivity;
import cn.qimate.bike.activity.CurRoadStartActivity;
import cn.qimate.bike.activity.DepositFreeAuthActivity;
import cn.qimate.bike.activity.EndBikeFeedBackActivity;
import cn.qimate.bike.activity.ExchangeActivity;
import cn.qimate.bike.activity.FeedbackActivity;
import cn.qimate.bike.activity.LoginActivity;
import cn.qimate.bike.activity.MainActivity;
import cn.qimate.bike.activity.MyCartActivity;
import cn.qimate.bike.activity.MyMessageActivity;
import cn.qimate.bike.activity.MyOrderActivity;
import cn.qimate.bike.activity.MyOrderDetailActivity;
import cn.qimate.bike.activity.PayCartActivity;
import cn.qimate.bike.activity.PersonAlterActivity;
import cn.qimate.bike.activity.RealNameAuthActivity;
import cn.qimate.bike.activity.RechargeActivity;
import cn.qimate.bike.activity.SecretProtocolAdapter;
import cn.qimate.bike.activity.ServiceCenter0Activity;
import cn.qimate.bike.activity.ServiceCenterActivity;
import cn.qimate.bike.activity.SettingActivity;
import cn.qimate.bike.activity.SettlementPlatformActivity;
import cn.qimate.bike.activity.UnpayOtherActivity;
import cn.qimate.bike.activity.UnpayRouteActivity;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.base.BaseFragment;
import cn.qimate.bike.ble.BLEService;
import cn.qimate.bike.core.common.AppManager;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.Md5Helper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.core.widget.LoadingDialogWithHelp;
import cn.qimate.bike.core.widget.MyScrollView;
import cn.qimate.bike.lock.utils.ToastUtils;
import cn.qimate.bike.lock.utils.Utils;
import cn.qimate.bike.model.BannerBean;
import cn.qimate.bike.model.CarAuthorityBean;
import cn.qimate.bike.model.CarBean;
import cn.qimate.bike.model.CurRoadBikingBean;
import cn.qimate.bike.model.KeyBean;
import cn.qimate.bike.model.LocationBean;
import cn.qimate.bike.model.OrderBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.TabTopEntity;
import cn.qimate.bike.model.UserBean;
import cn.qimate.bike.util.ByteUtil;
import cn.qimate.bike.util.HtmlTagHandler;
import cn.qimate.bike.util.IoBuffer;
import cn.qimate.bike.util.LogUtil;
import cn.qimate.bike.util.ToastUtil;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;
import cn.qimate.bike.view.RoundImageView;
import permissions.dispatcher.NeedsPermission;

import static android.app.Activity.RESULT_OK;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.sofi.blelocker.library.Constants.STATUS_CONNECTED;
import static com.sunshine.blelibrary.utils.EncryptUtils.Encrypt;

@SuppressLint("NewApi")
public class MainFragment extends BaseFragment implements View.OnClickListener, OnBannerListener, LocationSource, OnConnectionListener, BleStateChangeListener, ScanResultCallback, AMap.OnMapClickListener, AMap.OnMapLongClickListener, AMapNaviListener {

    private View v;
    Unbinder unbinder;

    UUID[] uuids = {Config.xinbiaoUUID, Config.xinbiaoUUID2};

    private static MainFragment mainFragment;

    private final static int SCANNIN_GREQUEST_CODE = 1;
    static private final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private static final int PRIVATE_CODE = 1315;//开启GPS权限

    private static final String TAG = MainFragment.class.getSimpleName();

    BluetoothAdapter mBluetoothAdapter;
    private XiaoanBleApiClient apiClient;


    public String codenum = "";
    private String m_nowMac = "";
    private int carmodel_id;
//    private String type = "";
    private String lock_no = "";
    private String bleid = "";
    private String deviceuuid = "";
    private String electricity = "";
    private String mileage = "";

    private int order_id2;

    private String carmodel_name;
    private String each_free_time;
    private String first_price;
    private String first_time;
    private String continued_price;
    private String continued_time;
    private String credit_score_desc;
    private Boolean isMac;

    private String keySource = "";
    //密钥索引
    int encryptionKey= 0;
    //开锁密钥
    String keys = null;
    //服务器时间戳，精确到秒，用于锁同步时间
    long serverTime;
    private String tel = "13188888888";

    private LinearLayout ll_payBtn, scanLock, myCommissionLayout, myLocationLayout, linkLayout, rl_ad, ll_top, ll_top_navi, refreshLayout, slideLayout,
            ll_top_biking, ll_biking_openAgain, ll_biking_endBtn, ll_biking_errorEnd, ll_estimated_cost, ll_electricity, ll_bike, ll_ebike, ll_top_pay,
            ll_oprate, ll_oprate_auto, ll_biking_openAgain_auto, ll_biking_errorEnd_auto, ll_tv_oprate_auto;

    private RelativeLayout rl_authBtn;
    private TextView tv_authBtn, tv_navi_name, tv_navi_distance,
            tv_biking_codenum, tv_againBtn, tv_againBtn_auto, tv_estimated_cost, tv_car_start_time, tv_car_start_time2, tv_estimated_cost2, tv_car_mileage, tv_car_electricity,
            tv_pay_codenum, tv_order_amount, tv_pay_car_start_time, tv_pay_car_end_time, tv_payBtn;

    private LinearLayout ll_test;
    private RelativeLayout rl_test;
    public static MyScrollView sv_test;
    public static TextView tv_test0;
    public static TextView tv_test;
    private Button btn_clear;
    private Button btn_log;
    public static String testLog = "";
    private boolean isLog;
    private boolean isNetSuc;

//    ll_oprate = activity.findViewById(R.id.ll_oprate);
//    ll_biking_openAgain = activity.findViewById(R.id.ll_biking_openAgain);
//    ll_biking_endBtn = activity.findViewById(R.id.ll_biking_endBtn);
//    ll_biking_errorEnd = activity.findViewById(R.id.ll_biking_errorEnd);
//    tv_againBtn = activity.findViewById(R.id.tv_againBtn);

//    ll_oprate_auto = activity.findViewById(R.id.ll_oprate_auto);
//    ll_biking_openAgain_auto = activity.findViewById(R.id.ll_biking_openAgain_auto);
//    ll_biking_errorEnd_auto = activity.findViewById(R.id.ll_biking_errorEnd_auto);
//    tv_againBtn_auto = activity.findViewById(R.id.tv_againBtn);


    public static  MainFragment getInstance() {
        return mainFragment;
    }

    CommonTabLayout tab;

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
    private ArrayList<String> imagePath;
    private ArrayList<String> imageTitle;
    private ArrayList<String> urlPath;
    private ArrayList<String> typeList;

    protected LoadingDialog loadingDialog2;

    CustomDialog.Builder customBuilder;
    private CustomDialog customDialog;
    private CustomDialog customDialog2;
    private CustomDialog customDialog3;
    private CustomDialog customDialog4;
    private CustomDialog customDialog5;
    private CustomDialog customDialog6;
    private CustomDialog customDialog7;
    private CustomDialog customDialog8;
    private CustomDialog customDialog9;
    protected LoadingDialogWithHelp loadingDialogWithHelp;
    private Dialog advDialog0;
    private Dialog advDialog;
    private Dialog advDialog2;
    private TextView advAgainBtn0;
    private TextView advCloseBtn0;
    private TextView advAgainBtn;
    private TextView advCloseBtn;
    private ImageView advCloseBtn2;

    private AMapNavi mAMapNavi;
    private RouteOverLay routeOverLay;

    PopupWindow popupwindow;

    PopupWindow popupwindow2;

    LocationManager locationManager;
    String provider = LocationManager.GPS_PROVIDER;

    private ScanManager scanManager;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private ScanCallback scanCallback;

    private List<Object> macList = new ArrayList<>();
    private List<Object> macList2 = new ArrayList<>();

    private boolean isConnect = false;
    private boolean isStop = false;
    private boolean isOpen = false;
    private boolean isFinish = false;
    private int n = 0;
    private int cn = 0;
    private int force_backcar = 0;
    private boolean isTwo = false;
    private boolean first3 = true;
    private boolean isEndBtn = false;
    public static int flagm = 0;
    boolean isFrist1 = true;
    boolean stopScan = false;
    private int clickCount = 0;
    private int tz = 0;
    private String transtype = "";
    private int major = 0;
    private int minor = 0;
    private boolean isGPS_Lo = false;
    private boolean scan = false;
    private boolean isTemp = false;
    private boolean isLookPsdBtn = false;
    private boolean isAgain = false;
    private String backType = "";
    private boolean isOpenLock = false;
    private int order_type = 0;
    private boolean isWaitEbikeInfo = true;
    private Thread ebikeInfoThread;
    public String oid = "";
    private int notice_code = 0;
    private int open = 0;
    private boolean isBleInit = false;
    private int loopTime = 1 * 1000;

    private int temporary_lock;	//0未临时上锁 1临时上锁中 3临时上锁完毕
    private int refresh_interval;
    private int order_refresh_interval;
    private int temp_lock_refresh_interval;

    private boolean first = true;
    private boolean isMin = false;

    private Polyline polyline;

    private String tipRange = "您还未到还车点，请按照导航到还车点还车";

//    private boolean isConnect = false;
    private BleDevice bleDevice;
    private String token;

    private LatLng markerPosition;

    private int timeout = 10000;

    private boolean isPermission = true;
    private boolean isFind = false;

    private boolean isNavi = false;

    long startTime;

    private int state;
    private int lockStatus;
    private String remark;

    private Handler m_myHandler2 = new Handler();

    private boolean isOrder = false;
    private boolean isEndClick = false;

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

        CrashHandler.getInstance().setmContext(context);



//        try {
//            int i = 1/0;
//        } catch (Exception e) {
////          memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
//        }

        mapView = activity.findViewById(R.id.mainUI_map);
        mapView.onCreate(savedInstanceState);

//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_SCREEN_ON);
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_USER_PRESENT);
//        activity.registerReceiver(mScreenReceiver, filter);

//        IntentFilter filter = new IntentFilter("data.broadcast.action");
//        activity.registerReceiver(broadcastReceiver2, filter);

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
                LogUtil.e("biking===scanM", "---beacons.size = " + beacons.size());

                for (Beacon beacon : beacons) {

                    LogUtil.e("biking===scanM", beacon.getName()+"=====" +beacon.getRssi()+"=====" + beacon.getMacAddress());

//                    macList.add(""+beacon.getMacAddress());
                    macList.add(""+beacon.getName());

                    scan = true;
                }

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) {
                    super.onScanResult(callbackType, result);
                    BluetoothDevice device = result.getDevice();
                    int rssi = result.getRssi();//获取rssi

                    LogUtil.e("scanCallback===", device+"==="+device.getName());

                    if (device.getName()!=null && (device.getName().startsWith("abeacon_") || "BC01".equals(device.getName()))){
                        macList.add(""+device.getName());
                    }
                }
            };
        }else{
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

                            LogUtil.e("biking===LeScan", new String(scanRecord)+ "====" +scanRecord.length+ "====" +device.getName() + "====" +device.fetchUuidsWithSdp()+ "====" +device.describeContents() + "====" + device.getAddress() + "====" + device.getUuids() + "====" + rssi);

//                        test_xinbiao += parseAdvData(rssi,scanRecord)+ "====" +device.getName()+ "====" + device.getAddress()+"\n";
//                        tv_test_xinbiao.setText(test_xinbiao);

                            if (device.getName()!=null && (device.getName().startsWith("abeacon_") || "BC01".equals(device.getName()))){
                                macList.add(""+device.getName());
                            }

//                        if ("BC01".equals(device.getName()) && !macList.contains(""+device.getAddress())){
//                            macList.add(""+device.getName());
////                            macList.add(""+device.getAddress());
////                          title.setText(isContainsList.contains(true) + "》》》" + near + "===" + macList.size() + "===" + k);
//                        }

                            scan = true;

                        }
                    });
                }
            };

        }


        initView();

        String h5_title = activity.getIntent().getStringExtra("h5_title");
        String action_content = activity.getIntent().getStringExtra("action_content");

        LogUtil.e("mf===onResume", h5_title+"==="+action_content+"==="+SharedPreferencesUrls.getInstance().getString("access_token", "")+"==="+type);

        if(h5_title!=null && !"".equals(h5_title)){
            UIHelper.goWebViewAct(context, h5_title, action_content);
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        LogUtil.e("onHiddenChanged===mf", hidden+"==="+isNavi+"==="+SharedPreferencesUrls.getInstance().getString("iscert", "")+"==="+access_token);

        if(hidden){
            //pause

//            if(routeOverLay!=null && ll_top!=null && !ll_top.isShown()){
//                routeOverLay.removeFromMap();
//
//                ll_top.setVisibility(View.VISIBLE);
//                ll_top_navi.setVisibility(View.GONE);
//            }

        }else{
            //resume
            if(!isNavi){
                banner(false);
                car_authority();

                if(bikeFragment!=null && !bikeFragment.isHidden()){
                    bikeFragment.initNearby(referLatitude, referLongitude);
                }else if(ebikeFragment!=null && !ebikeFragment.isHidden()){
                    ebikeFragment.initNearby(referLatitude, referLongitude);
                }

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        closeLoadingDialog2();

        boolean flag = activity.getIntent().getBooleanExtra("flag", false);

        access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        LogUtil.e("mf===onResume", isNavi+"==="+isPermission+"==="+flag+"==="+access_token+"==="+type);

        mapView.onResume();

        if(flag){
            activity.getIntent().putExtra("flag", false);

            if(!isNavi){
                banner(false);
                car_authority();

                if(!bikeFragment.isHidden()){
                    bikeFragment.initNearby(referLatitude, referLongitude);
                }else{
                    ebikeFragment.initNearby(referLatitude, referLongitude);
                }
            }
        }

        if(isPermission){

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
            setUpMap();
        }

//        aMap.setMapType(AMap.MAP_TYPE_NAVI);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);// 设置地图logo显示在右下方
        aMap.getUiSettings().setLogoBottomMargin(-100);

        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(18f);// 设置缩放监听
        aMap.moveCamera(cameraUpdate);

        aMap.setOnMapClickListener(this);
        aMap.setOnMapLongClickListener(this);

        mAMapNavi = AMapNavi.getInstance(context);
        mAMapNavi.addAMapNaviListener(this);

//        aMap.getUiSettings().setAllGesturesEnabled(false);
//        aMap.getUiSettings().setTiltGesturesEnabled(false);//禁止倾斜手势.

//        aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
//            @Override
//            public void onTouch(MotionEvent motionEvent) {
////                if(marker.getTitle()!=null && !"".equals(marker.getTitle())){
////
////                }
//                LogUtil.e("onMarkerTouch===", motionEvent.getAction()+"==="+motionEvent.getX()+"==="+motionEvent.getY());
//
//
//                if(motionEvent.getAction()==0){
//                    aMap.getUiSettings().setAllGesturesEnabled(false);
//                }else if(motionEvent.getAction()==1){
//                    aMap.getUiSettings().setAllGesturesEnabled(true);
//                }
//
//            }
//        });

        aMap.setOnMarkerDragListener(new AMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                LogUtil.e("onMarkerDragStart===", marker+"==="+marker.getPosition());

//                marker.setPosition(myLocation);
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                LogUtil.e("onMarkerDrag===", marker+"==="+marker.getPosition());
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LogUtil.e("onMarkerDragEnd===", marker+"==="+marker.getPosition());
            }
        });

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                LogUtil.e("onMarkerClick===", ll_top_navi.isShown()+"==="+marker.getTitle()+"==="+mAMapNavi+"==="+referLatitude+"==="+referLongitude+"==="+marker.getPosition().latitude+"==="+marker.getPosition().longitude);

                if(marker.getTitle()!=null && !"".equals(marker.getTitle())){
                    ll_top.setVisibility(View.GONE);
                    ll_top_navi.setVisibility(View.VISIBLE);
                    isNavi = true;

                    LogUtil.e("onMarkerClick===1", ll_top_navi.isShown()+"==="+marker.getTitle()+"==="+marker.getTitle().split("-")[0]);

                    markerPosition = marker.getPosition();
                    tv_navi_name.setText(marker.getTitle());
                    mAMapNavi.calculateWalkRoute(new NaviLatLng(referLatitude, referLongitude), new NaviLatLng(marker.getPosition().latitude, marker.getPosition().longitude));
//                    mAMapNavi.calculateRideRoute(new NaviLatLng(referLatitude, referLongitude), new NaviLatLng(marker.getPosition().latitude, marker.getPosition().longitude));
//                    mAMapNavi.calculateDriveRoute(new NaviLatLng(referLatitude, referLongitude), new NaviLatLng(marker.getPosition().latitude, marker.getPosition().longitude));
//                    mAMapNavi.c
                }


                return true;
            }
        });

        loadingDialog = new LoadingDialog(activity);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        loadingDialog2 = new LoadingDialog(activity);
        loadingDialog2.setCancelable(false);
        loadingDialog2.setCanceledOnTouchOutside(false);

        advDialog0 = new Dialog(context, R.style.Theme_AppCompat_Dialog);
        View advDialogView0 = LayoutInflater.from(context).inflate(R.layout.ui_adv_view2, null);
        advDialog0.setContentView(advDialogView0);
        advDialog0.setCanceledOnTouchOutside(false);

        advDialog = new Dialog(context, R.style.Theme_AppCompat_Dialog);
        View advDialogView = LayoutInflater.from(context).inflate(R.layout.ui_adv_view3, null);
        advDialog.setContentView(advDialogView);
        advDialog.setCanceledOnTouchOutside(false);

        advDialog2 = new Dialog(context, R.style.Theme_AppCompat_Dialog);
        View advDialogView2 = LayoutInflater.from(context).inflate(R.layout.ui_adv_view4, null);
        advDialog2.setContentView(advDialogView2);
        advDialog2.setCanceledOnTouchOutside(false);

        advAgainBtn0 = (TextView)advDialogView0.findViewById(R.id.ui_adv_againBtn0);
        advCloseBtn0 = (TextView)advDialogView0.findViewById(R.id.ui_adv_closeBtn0);

        advAgainBtn = (TextView)advDialogView.findViewById(R.id.ui_adv_againBtn);
        advCloseBtn = (TextView)advDialogView.findViewById(R.id.ui_adv_closeBtn);

        advCloseBtn2 = (ImageView)advDialogView2.findViewById(R.id.ui_adv_closeBtn2);

//        customBuilder = new CustomDialog.Builder(context);
//        customBuilder.setType(1).setTitle("温馨提示").setMessage("当前行程已停止计费，客服正在加紧处理，请稍等\n客服电话：0519—86999222");
//        customDialog = customBuilder.create();

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setType(3).setTitle("温馨提示").setMessage("如需临时上锁，请点击确定后关闭车锁")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        temp_lock(1);
                        dialog.cancel();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        customDialog = customBuilder.create();

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("连接失败").setMessage("蓝牙连接失败，请靠近车锁，重启软件后再试")
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        customDialog2 = customBuilder.create();

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("温馨提示").setMessage("请前往最近的还车点还车")        //TODO
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        customDialog3 = customBuilder.create();

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setType(2).setTitle("温馨提示").setMessage("关锁后将自动还车，如有延迟，可尝试手动还车")
                .setPositiveButton("手动还车", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        btnLog("A1");

                        cycling4();

                        dialog.cancel();
                    }
                }).setNegativeButton("联系客服", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        cycling6();
                    }
                }
        );
        customDialog4 = customBuilder.create();


        loadingDialogWithHelp = new LoadingDialogWithHelp(context);
        loadingDialogWithHelp.setCancelable(false);
        loadingDialogWithHelp.setCanceledOnTouchOutside(false);

        rl_authBtn = activity.findViewById(R.id.rl_authBtn);
        tv_authBtn = activity.findViewById(R.id.tv_authBtn);
        refreshLayout = activity.findViewById(R.id.mainUI_refreshLayout);
        myLocationLayout =  activity.findViewById(R.id.mainUI_myLocationLayout);
        slideLayout = activity.findViewById(R.id.mainUI_slideLayout);
        linkLayout = activity.findViewById(R.id.mainUI_linkServiceLayout);
        scanLock = activity.findViewById(R.id.mainUI_scanCode_lock);

        ll_top = activity.findViewById(R.id.ll_top);
        ll_top_navi = activity.findViewById(R.id.ll_top_navi);
        tv_navi_name = activity.findViewById(R.id.tv_navi_name);
        tv_navi_distance = activity.findViewById(R.id.tv_navi_distance);

        ll_top_biking = activity.findViewById(R.id.ll_top_biking);
        tv_biking_codenum = activity.findViewById(R.id.tv_biking_codenum);
        ll_estimated_cost = activity.findViewById(R.id.ll_estimated_cost);
        ll_electricity = activity.findViewById(R.id.ll_electricity);
        ll_bike = activity.findViewById(R.id.ll_bike);
        ll_ebike = activity.findViewById(R.id.ll_ebike);
        tv_estimated_cost = activity.findViewById(R.id.tv_estimated_cost);
        tv_car_start_time = activity.findViewById(R.id.tv_car_start_time);
        tv_car_start_time2 = activity.findViewById(R.id.tv_car_start_time2);
        tv_estimated_cost2 = activity.findViewById(R.id.tv_estimated_cost2);
        tv_car_mileage = activity.findViewById(R.id.tv_car_mileage);
        tv_car_electricity = activity.findViewById(R.id.tv_car_electricity);

        ll_oprate = activity.findViewById(R.id.ll_oprate);
        ll_biking_openAgain = activity.findViewById(R.id.ll_biking_openAgain);
        ll_biking_endBtn = activity.findViewById(R.id.ll_biking_endBtn);
        ll_biking_errorEnd = activity.findViewById(R.id.ll_biking_errorEnd);
        tv_againBtn = activity.findViewById(R.id.tv_againBtn);

        ll_oprate_auto = activity.findViewById(R.id.ll_oprate_auto);
        ll_tv_oprate_auto = activity.findViewById(R.id.ll_tv_oprate_auto);
        ll_biking_openAgain_auto = activity.findViewById(R.id.ll_biking_openAgain_auto);
        ll_biking_errorEnd_auto = activity.findViewById(R.id.ll_biking_errorEnd_auto);
        tv_againBtn_auto = activity.findViewById(R.id.tv_againBtn_auto);

        ll_top_pay = activity.findViewById(R.id.ll_top_pay);
        tv_pay_codenum = activity.findViewById(R.id.tv_pay_codenum);
        tv_order_amount = activity.findViewById(R.id.tv_order_amount);
        tv_pay_car_start_time = activity.findViewById(R.id.tv_pay_car_start_time);
        tv_pay_car_end_time = activity.findViewById(R.id.tv_pay_car_end_time);
        ll_payBtn = activity.findViewById(R.id.ll_payBtn);
        tv_payBtn = activity.findViewById(R.id.tv_payBtn);


        rl_authBtn.setOnClickListener(this);
        refreshLayout.setOnClickListener(this);
        myLocationLayout.setOnClickListener(this);
        slideLayout.setOnClickListener(this);
        linkLayout.setOnClickListener(this);
        scanLock.setOnClickListener(this);
        ll_biking_openAgain.setOnClickListener(this);
        ll_biking_openAgain_auto.setOnClickListener(this);
        ll_biking_endBtn.setOnClickListener(this);
        ll_biking_errorEnd.setOnClickListener(this);
        ll_biking_errorEnd_auto.setOnClickListener(this);
        ll_payBtn.setOnClickListener(this);

        bikeFragment = new BikeFragment();
        ebikeFragment = new EbikeFragment();
        mFragments.add(bikeFragment);
        mFragments.add(ebikeFragment);


        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabTopEntity(mTitles[i]));
//            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }

        tab = getActivity().findViewById(R.id.tab);

        tab.setTabData(mTabEntities, getActivity(), R.id.fl_change2, mFragments);

//        changeTab(1);
//        tab.setCurrentTab(1);


        leftBtn = activity.findViewById(R.id.mainUI_leftBtn);
        rightBtn = activity.findViewById(R.id.mainUI_rightBtn);

        imagePath = new ArrayList<>();
        imageTitle = new ArrayList<>();
        urlPath = new ArrayList<>();
        typeList = new ArrayList<>();

        mMyImageLoader = new MyImageLoader();
        mBanner = activity.findViewById(R.id.banner);
        //设置样式，里面有很多种样式可以自己都看看效果
//        mBanner.setBannerStyle(0);
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        mBanner.setImageLoader(mMyImageLoader);
        //设置轮播的动画效果,里面有很多种特效,可以都看看效果。
        mBanner.setBannerAnimation(Transformer.ZoomOutSlide);
        //轮播图片的文字
//      mBanner.setBannerTitles(imageTitle);
        //设置轮播间隔时间
        mBanner.setDelayTime(3000);
        //设置是否为自动轮播，默认是true
        mBanner.isAutoPlay(true);
        //设置指示器的位置，小点点，居中显示
        mBanner.setIndicatorGravity(BannerConfig.CENTER);

        rl_ad = activity.findViewById(R.id.rl_ad);

        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        rl_ad.setOnClickListener(this);
        advAgainBtn0.setOnClickListener(this);
        advCloseBtn0.setOnClickListener(this);
        advAgainBtn.setOnClickListener(this);
        advCloseBtn.setOnClickListener(this);
        advCloseBtn2.setOnClickListener(this);

        if(!"https://newmapi.7mate.cn/api".equals(Urls.host2)){
            ll_test = activity.findViewById(R.id.ll_test);
            rl_test = activity.findViewById(R.id.rl_test);
            sv_test = activity.findViewById(R.id.sv_test);
            tv_test0 = activity.findViewById(R.id.tv_test0);
            tv_test = activity.findViewById(R.id.tv_test);
            btn_clear = activity.findViewById(R.id.btn_clear);
            btn_log = activity.findViewById(R.id.btn_log);

            btn_clear.setOnClickListener(this);
            btn_log.setOnClickListener(this);

            btn_clear.setVisibility(View.VISIBLE);
            btn_log.setVisibility(View.VISIBLE);
            ll_test.setVisibility(View.VISIBLE);
            rl_test.setVisibility(View.VISIBLE);
            sv_test.setVisibility(View.GONE);

//            testLog+="log:\n";
//            tv_test.setText(testLog);
            LogUtil.e("log", "");

//            m_myHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    sv_test.fullScroll(ScrollView.FOCUS_DOWN); //滚动到底部
//                }
//            });
        }

    }

    private void setUpMap() {
//        aMap.setLocationSource(this);// 设置定位监听
//        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
//        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
//        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
//        aMap.setLoadOfflineData(true);



        final String STYLE_NAME = "style.data";
        final String STYLE_EXTRA_NAME = "style_extra.data";


        final String filePath = context.getFilesDir().getAbsolutePath() + "/" + STYLE_NAME;
        final String filePathExtra = context.getFilesDir().getAbsolutePath() + "/" + STYLE_EXTRA_NAME;
        final File file = new File(filePath);
        final File fileExtra = new File(filePathExtra);

        LogUtil.e("setUpMap===000", context.getAssets()+"==="+file+"==="+file.exists()+"==="+fileExtra.exists());

        if (file.exists()) {
//            aMap.setCustomMapStyle(
//                    new com.amap.api.maps.model.CustomMapStyleOptions()
//                            .setEnable(true)
//                            .setStyleDataPath(filePath)
//                            .setStyleExtraPath(filePathExtra)
//            );
//            return;//本地已有，不再下载
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            LogUtil.e("setUpMap===00", context.getAssets()+"==="+inputStream);

            inputStream = context.getAssets().open(STYLE_NAME);

            LogUtil.e("setUpMap===0", context.getAssets()+"==="+inputStream);

            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            outputStream = new FileOutputStream(file);
            outputStream.write(b);

//                    String filePath = context.getFilesDir().getAbsolutePath() + "/" + STYLE_NAME;

//                    aMap.setCustomMapStylePath(filePath);
//                    aMap.setMapCustomEnable(true);
//                    aMap.showMapText(true);

            LogUtil.e("setUpMap===", "==="+filePath);

//            aMap.setCustomMapStyle(
//                    new com.amap.api.maps.model.CustomMapStyleOptions()
//                            .setEnable(true)
//                            .setStyleDataPath(filePath)
////                                    .setStyleExtraPath(filePath)
////                                    .setStyleId("1b319281566f48d9ce57449b30be3b6e")//官网控制台-自定义样式 获取
//
////                                    .setStyleExtraPath("/mnt/sdcard/amap/style_extra.data")
////                                    .setStyleTexturePath("/mnt/sdcard/amap/textures.zip")
//            );

        } catch (Exception e) {
            e.printStackTrace();
            closeLoadingDialog();
            LogUtil.e("setUpMap===e", context.getAssets()+"==="+e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (fileExtra.exists()) {
//            aMap.setCustomMapStyle(
//                    new com.amap.api.maps.model.CustomMapStyleOptions()
//                            .setEnable(true)
//                            .setStyleDataPath(filePath)
//                            .setStyleExtraPath(filePathExtra)
//            );
//            return;//本地已有，不再下载
            fileExtra.delete();
        }
        try {
            fileExtra.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        outputStream = null;
        inputStream = null;
        try {
            LogUtil.e("setUpMap===100", context.getAssets()+"==="+inputStream);

            inputStream = context.getAssets().open(STYLE_EXTRA_NAME);

            LogUtil.e("setUpMap===10", context.getAssets()+"==="+inputStream);

            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            outputStream = new FileOutputStream(fileExtra);
            outputStream.write(b);

//                    String filePath = context.getFilesDir().getAbsolutePath() + "/" + STYLE_NAME;

//                    aMap.setCustomMapStylePath(filePath);
//                    aMap.setMapCustomEnable(true);
//                    aMap.showMapText(true);

            LogUtil.e("setUpMap===1", "==="+filePathExtra);

            aMap.setCustomMapStyle(
                    new com.amap.api.maps.model.CustomMapStyleOptions()
                            .setEnable(true)
                            .setStyleDataPath(filePath)
                            .setStyleExtraPath(filePathExtra)
//                                    .setStyleId("1b319281566f48d9ce57449b30be3b6e")//官网控制台-自定义样式 获取
//                                    .setStyleExtraPath("/mnt/sdcard/amap/style_extra.data")
//                                    .setStyleTexturePath("/mnt/sdcard/amap/textures.zip")
            );

        } catch (Exception e) {
            e.printStackTrace();
            closeLoadingDialog();
            LogUtil.e("setUpMap===1e", context.getAssets()+"==="+e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }).start();



        LogUtil.e("setUpMap===2", "==="+context.getFilesDir().getAbsolutePath());


    }

    @SuppressLint("MissingPermission")
    private boolean checkGPSIsOpen() {
        boolean isOpen;
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setCostAllowed(true);
//        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
//        provider = locationManager.getBestProvider(criteria, true);
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return false;
//        }

//        locationManager.requestLocationUpdates(provider, 1000, 10, locationListener);
//        locationManager.requestLocationUpdates(provider, 2000, 500, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);

        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    private void openGPSSettings() {
        if (checkGPSIsOpen()) {
        } else {

            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
            customBuilder.setType(3).setTitle("温馨提示").setMessage("请在手机设置打开应用的位置权限并选择最精准的定位模式")
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


    public void car_authority() {
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");

//        LogUtil.e("mf===car_authority", "==="+access_token);

        HttpHelper.get(context, Urls.car_authority, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                    onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, final String responseString, Throwable throwable) {
                onFailureCommon("mf===car_authority", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
//                            LogUtil.e("mf===car_authority1", "==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            CarAuthorityBean bean = JSON.parseObject(result.getData(), CarAuthorityBean.class);

//                          LogUtil.e("mf===car_authority2", bean.getUnauthorized_code()+"==="+bean.getOrder()+"==="+new JSONObject(bean.getOrder()).getInt("order_id"));

                            refresh_interval = bean.getRefresh_interval();

//                          未授权码 0（有权限时为0）1需要登录 2未认证 3认证中 4认证被驳回 5需要充值余额或购买骑行卡 6有进行中行程 7有待支付行程 8有待支付调度费 9有待支付赔偿费
                            unauthorized_code = bean.getUnauthorized_code();
                            notice_code = bean.getNotice_code();

                            OrderBean orderBean = JSON.parseObject(bean.getOrder(), OrderBean.class);
                            if(notice_code==8 || notice_code==9){
                                order_id = orderBean.getOrder_id();
                            }
                            temporary_lock = orderBean.getTemporary_lock();	//0未临时上锁 1临时上锁中 3临时上锁完毕
                            order_refresh_interval = orderBean.getOrder_refresh_interval(); //当前行程刷新频率 单位：毫秒
                            temp_lock_refresh_interval = orderBean.getTemp_lock_refresh_interval(); //临时上锁中刷新频率 单位：毫秒


                            if(notice_code==6) {
                                if("10".equals(type)){
                                    if(temporary_lock==0){
                                        loopTime = temp_lock_refresh_interval;

//                                    customDialog.show();
                                        tv_againBtn_auto.setText("临时上锁");
                                        ll_biking_openAgain_auto.setBackgroundResource(R.drawable.btn_bcg_biking2);
                                        ll_biking_openAgain_auto.setEnabled(true);
                                        ll_tv_oprate_auto.setVisibility(View.GONE);
                                    }else if(temporary_lock==1){
                                        loopTime = order_refresh_interval;

                                        tv_againBtn_auto.setText("再次开锁");
                                        ll_biking_openAgain_auto.setBackgroundResource(R.drawable.btn_bcg_biking3);
                                        ll_biking_openAgain_auto.setEnabled(false);
                                        ll_tv_oprate_auto.setVisibility(View.VISIBLE);
                                    }else if(temporary_lock==3){
                                        loopTime = order_refresh_interval;

                                        tv_againBtn_auto.setText("再次开锁");
                                        ll_biking_openAgain_auto.setBackgroundResource(R.drawable.btn_bcg_biking2);
                                        ll_biking_openAgain_auto.setEnabled(true);
                                        ll_tv_oprate_auto.setVisibility(View.GONE);
                                    }
                                }else{
                                    loopTime = order_refresh_interval;
                                }


                            }else{
                                loopTime = refresh_interval;

//                                View view = View.inflate(context, R.layout.marker_info_layout, null);
//                                iv_marker = view.findViewById(R.id.iv);
//                                iv_marker.setImageResource(R.drawable.marker1);
//                                tv_car_count = view.findViewById(R.id.tv_car_count);
//                                tv_car_count.setText((car_count>99?99:car_count)+"辆");
//                                centerMarkerOption = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromView(view));
//
//                                if(centerMarker!=null){
//                                    centerMarker.remove();
//                                }
//
//                                centerMarker = aMap.addMarker(centerMarkerOption);
                            }


                            SharedPreferencesUrls.getInstance().putString("iscert", ""+notice_code);
//                            LogUtil.e("mf===car_authority2", ebikeInfoThread+"==="+notice_code+"==="+unauthorized_code+"==="+temporary_lock+"==="+order_refresh_interval+"==="+temp_lock_refresh_interval+"==="+refresh_interval+"==="+loopTime);


                            ll_top.setVisibility(View.VISIBLE);
                            ll_top_biking.setVisibility(View.GONE);
                            ll_top_pay.setVisibility(View.GONE);
                            rl_authBtn.setVisibility(View.GONE);
                            tv_authBtn.setText("");

//                            if(!bikeFragment.isHidden()){
//                                bikeFragment.initNearby(referLatitude, referLongitude);
//                            }else{
//                                ebikeFragment.initNearby(referLatitude, referLongitude);
//                            }


                            if (ebikeInfoThread == null) {
                                cyclingThread();

                                if(notice_code==6) {
                                    cycling();
                                }
                            }else{
                                if(notice_code==6) {
                                    cycling2();
                                }
                            }

                            if(notice_code==0) {

                            }else{
                                if(notice_code<6) {
                                    rl_authBtn.setVisibility(View.VISIBLE);
                                }

                                if(notice_code==1) {
                                    tv_authBtn.setText("您还未登录，请点击进行登录！");
                                }else if(notice_code==2) {
                                    tv_authBtn.setText("您还未认证，请点击进行认证！");
                                }else if(notice_code==3) {
                                    tv_authBtn.setText("您处于认证中，请点击进行刷新！");
                                }else if(notice_code==4) {
                                    tv_authBtn.setText("您认证未通过，请点击重新认证！");
                                }else if(notice_code==5) {
                                    tv_authBtn.setText("您还未充值或购买套餐卡，请点击进行操作");
                                }else if(notice_code==6) {
                                    ll_top_navi.setVisibility(View.GONE);
                                    ll_top.setVisibility(View.VISIBLE);
                                    rl_ad.setVisibility(View.GONE);
                                    ll_top_biking.setVisibility(View.VISIBLE);

//                                    order_id = new JSONObject(bean.getOrder()).getInt("order_id");

//                                    LogUtil.e("mf===car_authority3", ebikeInfoThread+"==="+order_id);

//                                    if (ebikeInfoThread == null) {
//                                        cycling();
//                                        cyclingThread();
//                                    }else{
//                                        cycling2();
//                                    }

//                                    if (ebikeInfoThread == null) {
//                                        cycling();
//                                    }else{
//                                        cycling2();
//                                    }

                                }else if(notice_code>=7){
//                                    isWaitEbikeInfo = false;
//                                    if (ebikeInfoThread != null) {
//                                        ebikeInfoThread.interrupt();
//                                        ebikeInfoThread = null;
//                                    }

                                    rl_ad.setVisibility(View.GONE);
                                    ll_top_biking.setVisibility(View.GONE);
                                    ll_top_navi.setVisibility(View.GONE);
                                    ll_top.setVisibility(View.VISIBLE);
                                    ll_top_pay.setVisibility(View.VISIBLE);

                                    if(notice_code==7){
                                        order_type = 1;
                                        tv_payBtn.setText("骑行支付");

                                        cycling2();
                                    }else if(notice_code==8 || notice_code==9){
                                        order_type = 3;

                                        JSONObject jsonObject = new JSONObject(bean.getOrder());

                                        tv_pay_codenum.setText(jsonObject.getString("car_number"));
                                        tv_pay_car_start_time.setText(jsonObject.getString("car_start_time"));
                                        tv_pay_car_end_time.setText(jsonObject.getString("car_end_time"));
                                        tv_order_amount.setText("¥"+jsonObject.getString("order_amount"));

                                        if(notice_code==8){
                                            tv_payBtn.setText("调度费支付");
                                        }else{
                                            tv_payBtn.setText("赔偿费支付");
                                        }
                                    }

                                }
                            }

                            if(isMin){
                                if(notice_code==6){
                                    rl_authBtn.setVisibility(View.VISIBLE);
                                    tv_authBtn.setText(tipRange);
                                }else{
                                    clearRoute();
                                }
                            }

                            if(notice_code!=6){
//                                isWaitEbikeInfo = false;
//                                if (ebikeInfoThread != null) {
//                                    ebikeInfoThread.interrupt();
//                                    ebikeInfoThread = null;
//                                }

                                banner(false);
                            }

//                            LogUtil.e("mf===car_authority4", notice_code+"==="+isMin);

                        } catch (Exception e) {
                            closeLoadingDialog();
                            LogUtil.e("mf===car_authority_e", "==="+e);
                            e.printStackTrace();
                        }

//                        closeLoadingDialog();
                    }
                });




//                    m_myHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//
//
//                        }
//                    });
            }

        });



//        if (access_token == null || "".equals(access_token)) {
//            ToastUtil.showMessageApp(context, "请先登录账号");
//            UIHelper.goToAct(context, LoginActivity.class);
//        } else {
//
//
//        }
    }

    public void car_authority_auto() {
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");

        LogUtil.e("mf===car_authority_auto", "==="+access_token);

        HttpHelper.get(context, Urls.car_authority, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                    onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, final String responseString, Throwable throwable) {
                onFailureCommon("mf===car_authority", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtil.e("mf=car_authority_auto1", "==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            CarAuthorityBean bean = JSON.parseObject(result.getData(), CarAuthorityBean.class);

//                          LogUtil.e("mf===car_authority2", bean.getUnauthorized_code()+"==="+bean.getOrder()+"==="+new JSONObject(bean.getOrder()).getInt("order_id"));

//                          未授权码 0（有权限时为0）1需要登录 2未认证 3认证中 4认证被驳回 5需要充值余额或购买骑行卡 6有进行中行程 7有待支付行程 8有待支付调度费 9有待支付赔偿费
                            unauthorized_code = bean.getUnauthorized_code();
                            notice_code = bean.getNotice_code();

                            OrderBean orderBean = JSON.parseObject(bean.getOrder(), OrderBean.class);
                            temporary_lock = orderBean.getTemporary_lock();	//0未临时上锁 1临时上锁中 3临时上锁完毕
                            order_refresh_interval = orderBean.getOrder_refresh_interval();
                            temp_lock_refresh_interval = orderBean.getTemp_lock_refresh_interval();

                            if(notice_code==6) {
//                                if("10".equals(type)){
//                                    if(temporary_lock==0){
//                                        customDialog.show();
//                                    }
//                                }

                                if("10".equals(type)){
                                    if(temporary_lock==0){
                                        customDialog.show();
                                        tv_againBtn_auto.setText("临时上锁");
                                        ll_biking_openAgain_auto.setBackgroundResource(R.drawable.btn_bcg_biking2);
                                        ll_biking_openAgain_auto.setEnabled(true);
                                        ll_tv_oprate_auto.setVisibility(View.GONE);
                                    }else if(temporary_lock==1){
                                        tv_againBtn_auto.setText("再次开锁");
                                        ll_biking_openAgain_auto.setBackgroundResource(R.drawable.btn_bcg_biking3);
                                        ll_biking_openAgain_auto.setEnabled(false);
                                        ll_tv_oprate_auto.setVisibility(View.VISIBLE);

                                    }else if(temporary_lock==3){
                                        tv_againBtn_auto.setText("再次开锁");
                                        ll_biking_openAgain_auto.setBackgroundResource(R.drawable.btn_bcg_biking2);
                                        ll_biking_openAgain_auto.setEnabled(true);
                                        ll_tv_oprate_auto.setVisibility(View.GONE);
                                    }
                                }
                            }




                            SharedPreferencesUrls.getInstance().putString("iscert", ""+notice_code);
                            LogUtil.e("mf=car_authority_auto2", notice_code+"==="+unauthorized_code+"==="+temporary_lock+"==="+order_refresh_interval+"==="+temp_lock_refresh_interval);


                        } catch (Exception e) {
                            closeLoadingDialog();
                            LogUtil.e("car_authority_auto_e", "==="+e);
                            e.printStackTrace();
                        }

                        closeLoadingDialog();
                    }
                });


            }

        });

    }

    public void car_authority2() {
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");

//        LogUtil.e("mf===car_authority2", "==="+access_token);

        HttpHelper.get(context, Urls.car_authority, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon("mf===car_authority2", throwable.toString());
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
//                    LogUtil.e("mf===car_authority21", "==="+responseString);

                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                    CarAuthorityBean bean = JSON.parseObject(result.getData(), CarAuthorityBean.class);

                    closeLoadingDialog();



//                    LogUtil.e("mf===car_authority22", bean.getUnauthorized_code()+"==="+bean.getOrder());
//                        LogUtil.e("mf===car_authority2", bean.getUnauthorized_code()+"==="+bean.getOrder()+"==="+new JSONObject(bean.getOrder()).getInt("order_id"));

//                      未授权码 0（有权限时为0）1需要登录 2未认证 3认证中 4认证被驳回 5需要充值余额或购买骑行卡 6有进行中行程 7有待支付行程 8有待支付调度费 9有待支付赔偿费
                    unauthorized_code = bean.getUnauthorized_code();
                    notice_code = bean.getNotice_code();

                    SharedPreferencesUrls.getInstance().putString("iscert", ""+notice_code);

                    switch (unauthorized_code){

                        case 0:
                            if (Build.VERSION.SDK_INT >= 23) {
                                int checkPermission = activity.checkSelfPermission(Manifest.permission.CAMERA);
                                if (checkPermission != PERMISSION_GRANTED) {
//                                    flag = 1;

                                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                        requestPermissions(new String[] { Manifest.permission.CAMERA }, 100);
                                    } else {
                                        CustomDialog.Builder customBuilder1 = new CustomDialog.Builder(context);
                                        customBuilder1.setType(3).setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")
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


//                            if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                                popupwindow.dismiss();
//                            }
//
//                            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
//                            mBluetoothAdapter = bluetoothManager.getAdapter();
//
//                            if (mBluetoothAdapter == null) {
//                                ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                                popupwindow.dismiss();
//                                return;
//                            }
//                            if (!mBluetoothAdapter.isEnabled()) {
//                                isPermission = false;
//                                    closeLoadingDialog2();
//                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                                startActivityForResult(enableBtIntent, 188);
//                            } else {
//
//                            }

                            //                                      closeBroadcast();
//                                      deactivate();

                            if(routeOverLay!=null && ll_top!=null && !ll_top.isShown()){
                                routeOverLay.removeFromMap();

                                ll_top.setVisibility(View.VISIBLE);
                                ll_top_navi.setVisibility(View.GONE);
                            }

                            end2();

                            Intent intent = new Intent();
                            intent.setClass(context, ActivityScanerCode.class);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, SCANNIN_GREQUEST_CODE);


                            break;

                        case 1:
                            ToastUtil.showMessageApp(context,"您还未登录");
//                                UIHelper.goToAct(context, RealNameAuthActivity.class);
                            break;
                        case 2:
                            ToastUtil.showMessageApp(context,"您还未认证");
//                                UIHelper.goToAct(context, RealNameAuthActivity.class);
                            break;
                        case 3:
                            ToastUtil.showMessageApp(context,"您处于认证中");
                            break;
                        case 4:
                            ToastUtil.showMessageApp(context,"您认证未通过");
//                                UIHelper.goToAct(context,RealNameAuthActivity.class);
                            break;
                        case 5:
                            ToastUtil.showMessageApp(context,"您还未充值或购买套餐卡");
                            break;
                        case 6:
                            ToastUtil.showMessageApp(context,"您有进行中的行程");
                            break;
                        case 7:
                            ToastUtil.showMessageApp(context,"您有待支付的行程");
                            break;
                        case 8:
                            ToastUtil.showMessageApp(context,"您有待支付的调度费");
                            break;
                        case 9:
                            ToastUtil.showMessageApp(context,"您有待支付的赔偿费");
                            break;
                    }


                } catch (Exception e) {
                    closeLoadingDialog();
                    LogUtil.e("car_authority2_e", "==="+e);
                    UIHelper.showToastMsg(context, e.getMessage(), R.drawable.ic_error);
                }

                closeLoadingDialog();

            }

        });


//        if (access_token == null || "".equals(access_token)) {
//            ToastUtil.showMessageApp(context, "请先登录账号");
//            UIHelper.goToAct(context, LoginActivity.class);
//        } else {
//
//
//        }
    }

    private void cyclingThread() {

        LogUtil.e("cyclingThread===", "==="+ebikeInfoThread);
        Runnable ebikeInfoRunnable = new Runnable() {
            @Override
            public void run() {
                while (isWaitEbikeInfo) {

//                    LogUtil.e("cyclingThread===1", notice_code+"==="+ebikeInfoThread+"==="+loopTime);

                    m_myHandler.sendEmptyMessage(4);

                    try {
                        Thread.sleep(loopTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        ebikeInfoThread = new Thread(ebikeInfoRunnable);
        ebikeInfoThread.start();

        LogUtil.e("cyclingThread===2", notice_code+"==="+ebikeInfoThread+"==="+loopTime);
    }

    private void cycling() {
//        LogUtil.e("mf===cycling", "===");

        HttpHelper.get(context, Urls.cycling, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon("mf===cycling", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("mf===cycling_1", responseString + "===" + result.data);

                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);

//                            closeLoadingDialog();

                            if(null != bean.getOrder_sn()){
//                                LogUtil.e("mf===cycling_2", bean.getOrder_sn()+"===" + bean.getCar_number()+"===" + bean.getLock_id());

                                order_id = bean.getOrder_id();
                                oid = bean.getOrder_sn();
                                codenum = bean.getCar_number();
                                carmodel_id = bean.getCarmodel_id();
                                type = ""+bean.getLock_id();
                                m_nowMac = bean.getCar_lock_mac();


                                carInfo();

                                SharedPreferencesUrls.getInstance().putString("type", type);


//                                LogUtil.e("mf===cycling_22", carmodel_id+"===" + type+"===" + bean.getOrder_sn()+"===" + bean.getCar_number()+"===" + bean.getLock_id());

                                if (carmodel_id==2) {
//                                    LogUtil.e("mf===cycling_3", bean.getOrder_sn()+"===" + bean.getCar_number()+"===" + bean.getLock_id());

                                    changeTab(1);

                                    ll_estimated_cost.setVisibility(View.GONE);
                                    ll_electricity.setVisibility(View.VISIBLE);
                                    ll_bike.setVisibility(View.GONE);
                                    ll_ebike.setVisibility(View.VISIBLE);

                                    if ("0".equals(SharedPreferencesUrls.getInstance().getString("tempStat", "0"))) {
                                        tv_againBtn.setText("临时上锁");
                                    } else {
                                        tv_againBtn.setText("再次开锁");
                                    }
                                }else{
//                                    LogUtil.e("mf===cycling_4", bean.getOrder_sn()+"===" + bean.getCar_number()+"===" + bean.getLock_id());

                                    changeTab(0);

                                    ll_estimated_cost.setVisibility(View.VISIBLE);
                                    ll_electricity.setVisibility(View.GONE);
                                    ll_bike.setVisibility(View.VISIBLE);
                                    ll_ebike.setVisibility(View.GONE);
                                }

                                if(first){
                                    first = false;

                                    access_token = SharedPreferencesUrls.getInstance().getString("access_token","");

                                    if (access_token == null || "".equals(access_token)){
                                        refreshLayout.setVisibility(View.GONE);
                                        ToastUtil.showMessageApp(context,"请先登录账号");
                                        UIHelper.goToAct(context,LoginActivity.class);
                                    }else {
                                        if ("2".equals(type) || "3".equals(type) || "9".equals(type) || "10".equals(type)){

//                                            LogUtil.e("mf===requestCode2", codenum+"==="+type);


//                                            closeBroadcast();     //TODO    3
//                                            activity.registerReceiver(broadcastReceiver, Config.initFilter());
//                                            GlobalParameterUtils.getInstance().setLockType(LockType.MTS);

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
                                                return;
                                            }

                                            if (!mBluetoothAdapter.isEnabled()) {
                                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                                startActivityForResult(enableBtIntent, 189);
                                            } else {
                                                bleInit();

                                                setScanRule();
                                                scan();
                                            }


                                        }else if("4".equals(type) || "8".equals(type)){
                                        }else if ("5".equals(type) || "6".equals(type)) {

                                            if(!SharedPreferencesUrls.getInstance().getBoolean("isKnow0", false)){
                                                WindowManager windowManager = activity.getWindowManager();
                                                Display display = windowManager.getDefaultDisplay();
                                                WindowManager.LayoutParams lp = advDialog0.getWindow().getAttributes();
                                                lp.width = (int) (display.getWidth() * 1);
                                                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                                advDialog0.getWindow().setBackgroundDrawableResource(R.color.transparent);
                                                advDialog0.getWindow().setAttributes(lp);
                                                advDialog0.show();
                                            }

                                        }else if ("7".equals(type)) {
                                        }else if ("11".equals(type)) {
                                            TbitBle.initialize(context, new SecretProtocolAdapter());
                                        }

                                    }
                                }


                            }

                        } catch (Exception e) {
                            closeLoadingDialog();
                            LogUtil.e("carInfo===e", "==="+e);
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });
            }
        });

    }

    private void cycling2() {
//        LogUtil.e("mf===cycling2", "===");

        HttpHelper.get(context, Urls.cycling, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon("mf===cycling2", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

//                            LogUtil.e("mf===cycling2_1", responseString + "===" + result.data);

                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);

//                            if (loadingDialog != null && loadingDialog.isShowing()) {
//                                loadingDialog.dismiss();
//                            }

                            if(null != bean.getOrder_sn()){
//                                LogUtil.e("mf===cycling2_2", bean.getOrder_sn()+"===" + bean.getCar_number()+"===" + bean.getLock_id());

                                order_id = bean.getOrder_id();
                                oid = bean.getOrder_sn();
                                codenum = bean.getCar_number();
                                carmodel_id = bean.getCarmodel_id();
                                type = ""+bean.getLock_id();
                                m_nowMac = bean.getCar_lock_mac();
                                mileage = bean.getMileage();
                                electricity = bean.getElectricity();

                                SharedPreferencesUrls.getInstance().putString("type", type);

                                if (carmodel_id==2) {
//                                    changeTab(1);

                                    ll_estimated_cost.setVisibility(View.GONE);
                                    ll_electricity.setVisibility(View.VISIBLE);
                                    ll_bike.setVisibility(View.GONE);
                                    ll_ebike.setVisibility(View.VISIBLE);

                                    if("10".equals(type)){
                                        ll_oprate.setVisibility(View.GONE);
                                        ll_oprate_auto.setVisibility(View.VISIBLE);
                                    }else{
                                        ll_oprate.setVisibility(View.VISIBLE);
                                        ll_oprate_auto.setVisibility(View.GONE);
                                    }

                                }else{
//                                    changeTab(0);

                                    ll_estimated_cost.setVisibility(View.VISIBLE);
                                    ll_electricity.setVisibility(View.GONE);
                                    ll_bike.setVisibility(View.VISIBLE);
                                    ll_ebike.setVisibility(View.GONE);

                                    if("10".equals(type)){
                                        ll_oprate.setVisibility(View.GONE);
                                        ll_oprate_auto.setVisibility(View.VISIBLE);
                                    }else{
                                        ll_oprate.setVisibility(View.VISIBLE);
                                        ll_oprate_auto.setVisibility(View.GONE);
                                    }

                                }


                                tv_biking_codenum.setText(codenum);
                                tv_estimated_cost.setText("¥"+bean.getEstimated_cost());
                                tv_estimated_cost2.setText("¥"+bean.getEstimated_cost());
                                tv_car_start_time.setText(bean.getCar_start_time());
                                tv_car_start_time2.setText(bean.getCar_start_time());
                                tv_car_mileage.setText(mileage);
                                tv_car_electricity.setText(electricity);

                                tv_pay_codenum.setText(bean.getCar_number());
                                tv_pay_car_start_time.setText(bean.getCar_start_time());
                                tv_pay_car_end_time.setText(bean.getCar_end_time());
                                tv_order_amount.setText("¥"+bean.getOrder_amount());

                            }

                        } catch (Exception e) {
                            closeLoadingDialog();
                            LogUtil.e("cycling2===e", "==="+e);
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });
            }
        });

    }



    private void carInfo() {
        LogUtil.e("carInfo===000", "===" + codenum);

        HttpHelper.get(context, Urls.car + URLEncoder.encode(codenum), new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                onStartCommon("正在加载");
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

                            LogUtil.e("carInfo===", responseString + "===" + result.data);

                            CarBean bean = JSON.parseObject(result.getData(), CarBean.class);

                            LogUtil.e("carInfo===2", bean.getNumber()+"===" + bean.getLock_mac());

                            lock_no = bean.getLock_no();
                            bleid = bean.getLock_secretkey();
                            deviceuuid = bean.getVendor_lock_id();
//                            force_backcar = bean.getForce_backcar();  //TODO  3
//                            String price = bean.getPrice();
//                            String electricity = bean.getElectricity();
//                            String mileage = bean.getMileage();

                            String lock_secretkey = bean.getLock_secretkey();
                            String lock_password = bean.getLock_password();

                            if("9".equals(type) || "10".equals(type)){
                                Config.key = hexStringToByteArray(lock_secretkey);
                                Config.password = hexStringToByteArray(lock_password);
                            }else if("2".equals(type) || "3".equals(type)){
                                Config.key = Config.key2;
                                Config.password = Config.password2;
                            }


                        } catch (Exception e) {
                            closeLoadingDialog();
                            LogUtil.e("carInfo===e", "==="+e);
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }
//                        closeLoadingDialog();
                    }
                });


            }
        });
    }

    private void carInfo_open() {
        LogUtil.e("carInfo_open===000", "===" + codenum);

        HttpHelper.get(context, Urls.car + URLEncoder.encode(codenum), new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                onStartCommon("正在加载");
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

//                        "<p style=\"color: #666666; font-size: 16px;\">1\u5c0f\u65f6\u5185\u514d\u8d39\uff0c\u8d85\u8fc71\u5c0f\u65f6<span style=\"color: #FF0000;\">\uffe5<span style=\"font-size: 24px;\">1.00<\/span><\/span>\/30\u5206\u949f<\/p>"

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("carInfo_open===", responseString + "===" + result.data);

                            CarBean bean = JSON.parseObject(result.getData(), CarBean.class);

                            LogUtil.e("carInfo_open===2", bean.getNumber()+"===" + bean.getLock_mac());

                            int lock_status = bean.getLock_status();    //0未知 1已上锁 2已开锁 3离线

                            if(lock_status==2){
                                ToastUtil.showMessageApp(context, "锁已开");
                                closeLoadingDialog();
                            }else{
                                car_can_unlock();
                            }


                        } catch (Exception e) {
                            closeLoadingDialog();
                            LogUtil.e("carInfo_open===e", "==="+e);
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });


            }
        });
    }

    //轮询查车开关状态_临时关锁
    private void carInfo_close_loop() {
        LogUtil.e("carInfo_close_loop=", type + "===" + codenum);

//        if("11".equals(type)){
//            if(TbitBle.getBleConnectionState()!=0)return;
//        }


        HttpHelper.get(context, Urls.car + URLEncoder.encode(codenum), new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                onStartCommon("正在加载");
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

//                        "<p style=\"color: #666666; font-size: 16px;\">1\u5c0f\u65f6\u5185\u514d\u8d39\uff0c\u8d85\u8fc71\u5c0f\u65f6<span style=\"color: #FF0000;\">\uffe5<span style=\"font-size: 24px;\">1.00<\/span><\/span>\/30\u5206\u949f<\/p>"

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("carInfo_close_loop===", responseString + "===" + result.data);

                            CarBean bean = JSON.parseObject(result.getData(), CarBean.class);

                            int lock_status = bean.getLock_status();    //0未知 1已上锁 2已开锁 3离线

                            LogUtil.e("carInfo_close_loop===2", bean.getNumber()+"===" + lock_status);

                            if(lock_status!=1){
//                                ToastUtil.showMessageApp(context, "锁已开");
//                                closeLoadingDialog();

                                queryCarInfoClose();

                            }else{
                                tv_againBtn.setText("再次开锁");
                                SharedPreferencesUrls.getInstance().putString("tempStat","1");

                                ToastUtil.showMessageApp(context,"恭喜您,关锁成功!");

                                closeLoadingDialog();
                            }


                        } catch (Exception e) {
                            closeLoadingDialog();
                            LogUtil.e("carInfo_close_loop===e", "==="+e);
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });


            }
        });
    }

    //助力车关锁轮询
    private void queryCarInfoClose() {
        if(n<7){
            n++;

            m_myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtil.e("mf===queryCarInfoClose", "===");

                    carInfo_close_loop();
                }
            }, 1 * 1000);
        }else{
            if("11".equals(type)){
//                if(TbitBle.getBleConnectionState()==0){
//                    ToastUtil.showMessageApp(context, "关锁超时");
//
//                    closeLoadingDialog();
//                }

                if(TbitBle.getBleConnectionState()==0){
                    tbtble_connect();
                }else{
                    tbtble_lock_temp();
                }
            }else{
                ToastUtil.showMessageApp(context, "关锁超时");

                closeLoadingDialog();
            }


        }
    }

    //轮询查车开关状态_临时开锁
    private void carInfo_open_loop() {
        LogUtil.e("carInfo_open_loop===", type + "===" + codenum);

//        if("11".equals(type)){
//            LogUtil.e("carInfo_open_loop_1===", type + "===" + TbitBle.getBleConnectionState() + "===" + codenum);
//            if(TbitBle.getBleConnectionState()!=0)return;
//        }


        HttpHelper.get(context, Urls.car + URLEncoder.encode(codenum), new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                onStartCommon("正在加载");
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

                            LogUtil.e("carInfo_open_loop===", responseString + "===" + result.data);

                            CarBean bean = JSON.parseObject(result.getData(), CarBean.class);

                            int lock_status = bean.getLock_status();    //0未知 1已上锁 2已开锁 3离线

                            LogUtil.e("carInfo_open_loop===2", bean.getNumber()+"===" + lock_status);

                            if(lock_status!=2){
//                                ToastUtil.showMessageApp(context, "锁已开");
//                                closeLoadingDialog();

                                queryCarInfoOpen();

                            }else{

                                tv_againBtn.setText("临时上锁");
                                SharedPreferencesUrls.getInstance().putString("tempStat","0");

                                ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

                                closeLoadingDialog();
                            }


                        } catch (Exception e) {
                            closeLoadingDialog();
                            LogUtil.e("carInfo_open_loop===e", "==="+e);
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });


            }
        });
    }

    //助力车临时开锁_轮询
    private void queryCarInfoOpen() {
        if(n<7){
            n++;

            m_myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtil.e("mf===queryCarInfoOpen", "==="+n);

                    carInfo_open_loop();
                }
            }, 1 * 1000);
        }else{
            LogUtil.e("mf===queryCarInfoOpen2", type+"==="+n);

            if("11".equals(type)){
                if(TbitBle.getBleConnectionState()==0){
                    tbtble_connect();
                }else{
                    tbtble_unlock_temp();
                }

//                try{
//                    if(isNetSuc){
//                        return;
//                    }
//
//                    // 连接回应
//                    final String tvAgain = tv_againBtn.getText().toString().trim();
//                    LogUtil.e("tbtble_connect===", resultCode+"==="+isOpenLock+"==="+isEndBtn+"==="+isAgain+"==="+tvAgain);
//
//                    if(resultCode==0){
//                        if(isAgain){
//                            if("再次开锁".equals(tvAgain)){
//                                tbtble_unlock_temp();
//                            }else{
//                                tbtble_lock_temp();
//                            }
//                        }else{
//                            if(isEndBtn){
//                                tbtble_lock();
//                            }else{
//                                tbtble_unlock();
//                            }
//                        }
//
//
//                    }else{
//                    }
//                }catch(Exception e){
//                    closeLoadingDialog();
//                    LogUtil.e("tbtble_connect===e", "==="+e);
//
//                }

            }else{
                ToastUtil.showMessageApp(context, "开锁超时");

                closeLoadingDialog();
            }


        }
    }

    private void carInfo_close() {
        LogUtil.e("carInfo_close===000", "===" + codenum);

        HttpHelper.get(context, Urls.car + URLEncoder.encode(codenum), new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在唤醒车锁");
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

//                        "<p style=\"color: #666666; font-size: 16px;\">1\u5c0f\u65f6\u5185\u514d\u8d39\uff0c\u8d85\u8fc71\u5c0f\u65f6<span style=\"color: #FF0000;\">\uffe5<span style=\"font-size: 24px;\">1.00<\/span><\/span>\/30\u5206\u949f<\/p>"

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("carInfo_close===", responseString + "===" + result.data);

                            CarBean bean = JSON.parseObject(result.getData(), CarBean.class);

                            LogUtil.e("carInfo_close===2", bean.getNumber()+"===" + bean.getLock_mac());

                            int lock_status = bean.getLock_status();    //0未知 1已上锁 2已开锁 3离线

                            if(lock_status==1){
                                ToastUtil.showMessageApp(context, "锁已关");
                                closeLoadingDialog();
                            }else{
                                car_can_lock();
                            }


                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                            closeLoadingDialog();
                            LogUtil.e("carInfo_close===e", "==="+e);
                        }

                    }
                });


            }
        });
    }



    private void cycling3() {
        LogUtil.e("mf===cycling3", "==="+access_token);

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");

        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
            return;
        }

        HttpHelper.get(context, Urls.cycling, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon("mf===cycling3", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("mf===cycling3_1", responseString + "===" + result.data);

                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);



                            if(null == bean.getOrder_sn() || bean.getOrder_state()>20){
                                ToastUtil.showMessageApp(context, "当前无进行中的行程");
                                car_authority();
                            }else{
                                LogUtil.e("mf===cycling3_2", bean.getOrder_sn()+"===" + bean.getCar_number()+"===" + bean.getLock_id());

                                closeLoadingDialog();

                                Intent intent = new Intent(context, EndBikeFeedBackActivity.class);
                                intent.putExtra("type", type);
                                intent.putExtra("bikeCode", codenum);
                                startActivity(intent);
                            }

                        } catch (Exception e) {
                            closeLoadingDialog();
                            LogUtil.e("mf===cycling3===e", "==="+e);
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }


                    }
                });
            }
        });

    }

    private void cycling4() {
        LogUtil.e("mf===cycling4", "===");

//        loadingDialog.setTitle("正在还车中，请勿离开");
//        loadingDialog.show();

        try{
            HttpHelper.get(context, Urls.cycling, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在还车中，请勿离开");
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    onFailureCommon("mf===cycling4", throwable.toString());
                    isEndClick = false;
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                    isEndClick = false;
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                                LogUtil.e("mf===cycling4_1", responseString + "===" + result.data);

                                OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);

                                if(null == bean.getOrder_sn() || bean.getOrder_state()>20){
                                    ToastUtil.showMessageApp(context, "当前行程已结束");

                                    end2();

                                    car_authority();
                                }else{
                                    order_id2 = bean.getOrder_id();
                                    endCar();
                                }

                            } catch (Exception e) {
                                closeLoadingDialog();
                                LogUtil.e("mf===cycling4_suc_e", "==="+e);

//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                            }



                        }
                    });
                }
            });
        }catch (Exception e){
            LogUtil.e("mf===cycling4_e", "==="+e);
            isEndClick = false;
        }


    }


    private void cycling5() {
        LogUtil.e("mf===cycling5", "==="+loadingDialog.isShowing());

//        loadingDialog.setTitle("正在还车中，请勿离开");
//        loadingDialog.show();

        HttpHelper.get(context, Urls.cycling, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                onStartCommon("正在还车中，请勿离开");
                onStartCommon("正在唤醒车锁");


            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon("mf===cycling5", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("mf===cycling5_1", responseString + "===" + result.data);

                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);

                            if(null == bean.getOrder_sn() || bean.getOrder_state()>20){
                                ToastUtil.showMessageApp(context, "当前行程已结束");
                                end2();
                                car_authority();
                            }else{
                                if(carmodel_id==2){
                                    carInfo_open();
                                }else{
                                    car_can_unlock();
                                }
                            }

                        } catch (Exception e) {
                            closeLoadingDialog();
                            LogUtil.e("mf===cycling5===e", "==="+e);

//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }



                    }
                });
            }
        });

    }


    private void cycling6() {
        LogUtil.e("mf===cycling6", "===");

//        loadingDialog.setTitle("正在还车中，请勿离开");
//        loadingDialog.show();

        HttpHelper.get(context, Urls.cycling, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在还车中，请勿离开");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon("mf===cycling6", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("mf===cycling6_1", responseString + "===" + result.data);

                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);


                            if(null == bean.getOrder_sn() || bean.getOrder_state()>20){
                                ToastUtil.showMessageApp(context, "当前行程已结束");

                                end2();

                                car_authority();
                            }else{
                                //TODO
                                Intent intent = new Intent(context, EndBikeFeedBackActivity.class);
                                intent.putExtra("bikeCode", codenum);
                                intent.putExtra("carmodel_id", carmodel_id);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);
                            }

                        } catch (Exception e) {
                            closeLoadingDialog();
                            LogUtil.e("mf===cycling6===e", "==="+e);
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

            if (broadcastReceiver != null) {
                activity.unregisterReceiver(broadcastReceiver);
//                broadcastReceiver = null;
            }

            ToastUtil.showMessage(context, "main====closeBroadcast===");
            LogUtil.e("mf====closeBroadcast", "===");

        } catch (Exception e) {
            ToastUtil.showMessage(context, "eee====" + e);
        }
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



    @Override
    public void OnBannerClick(int position) {
//        Toast.makeText(context, "你点了第" + (position + 1) + "张轮播图", Toast.LENGTH_SHORT).show();

        LogUtil.e("mf===OnBannerClick", imageTitle.get(position)+"==="+urlPath.get(position)+"==="+typeList.get(position)+"==="+("car_bad".equals(urlPath.get(position))));

//        home 首页
//        wallet 我的钱包
//        member 会员中心
//        recharge 充值页面
//        cycling_card 购买套餐卡页面
//        my_cycling_card 我的套餐卡页面
//        cycling_card_exchange 套餐卡兑换页面
//        bill 账单
//        order 我的订单
//        notice 我的消息
//        service 客服中心
//        phone_change 换绑手机
//        setting 设置中心
//        cert 认证中心
//        cert1 免押金认证
//        cert2 充值认证
//        car_bad 上报故障

        bannerTz(imageTitle.get(position), typeList.get(position), urlPath.get(position));




//        initmPopupWindowView();
    }

    private void bannerTz(String title, String type, String url) {
        if("app".equals(type)){
            if("home".equals(url)){
                ((MainActivity)activity).changeTab(0);
            }else if("wallet".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    ((MainActivity)activity).changeTab(1);
                }
            }else if("member".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    ((MainActivity)activity).changeTab(2);
                }
            }else if("recharge".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context, RechargeActivity.class);
                }
            }else if("cycling_card".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    user(url);
                }
            }else if("my_cycling_card".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    user(url);
                }
            }else if("cycling_card_exchange".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    user(url);
                }
            }else if("bill".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context, BillActivity.class);
                }
            }else if("order".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context, MyOrderActivity.class);
                }
            }else if("notice".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context, MyMessageActivity.class);
                }
            }else if("service".equals(url)){
                UIHelper.goToAct(context, ServiceCenterActivity.class);
            }else if("phone_change".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context, ChangePhoneActivity.class);
                }
            }else if("setting".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context, SettingActivity.class);
                }
            }else if("cert".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context, AuthCenterActivity.class);
                }
            }else if("cert1".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context, DepositFreeAuthActivity.class);
                }
            }else if("cert2".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context,RealNameAuthActivity.class);
                }
            }else if("car_bad".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context, CarFaultProActivity.class);
                }
            }
        }else if("h5".equals(type)){
            UIHelper.goWebViewAct(context, title, url);
        }
    }

    private void user(final String url) {
        LogUtil.e("pf===user", "==="+isHidden());

        HttpHelper.get2(context, Urls.user, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon("pf===user", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("pf===user1", responseString + "===" + result.data);

                            UserBean bean = JSON.parseObject(result.getData(), UserBean.class);

                            int cert1_status = bean.getCert1_status();

                            if(cert1_status!=3){
                                ToastUtil.showMessageApp(context, "请先进行免押金认证");
                            }else {

                                if ("cycling_card".equals(url)) {
                                    UIHelper.goToAct(context, PayCartActivity.class);
                                } else if ("my_cycling_card".equals(url)) {
                                    UIHelper.goToAct(context, MyCartActivity.class);
                                } else if ("cycling_card_exchange".equals(url)) {
                                    UIHelper.goToAct(context, ExchangeActivity.class);
                                }
                            }


                        } catch (Exception e) {
                            closeLoadingDialog();
                            LogUtil.e("pf===user_e", "==="+e);
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


    private void banner(final boolean fresh) {
//        LogUtil.e("mf===banner", "===" + codenum);

        HttpHelper.get2(context, Urls.banner + 3, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if(fresh){
                    onStartCommon("正在加载");
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                LogUtil.e("mf===banner=fail", "===" + throwable.toString());
                onFailureCommon("mf===banner", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
//                            LogUtil.e("mf===banner0", responseString + "===");

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            JSONArray ja_banners = new JSONArray(new JSONObject(result.getData()).getString("banners"));

//                            LogUtil.e("mf===banner1", ja_banners.length() + "===" + result.data);



                            if(ja_banners.length()==0){
                                rl_ad.setVisibility(View.GONE);
                            }else{
                                if(unauthorized_code<6){
                                    rl_ad.setVisibility(View.VISIBLE);
                                }

                                if(imagePath.size()>0){
                                    imagePath.clear();
                                    imageTitle.clear();
                                    urlPath.clear();
                                    typeList.clear();
                                }

                                for (int i = 0; i < ja_banners.length(); i++) {
                                    BannerBean bean = JSON.parseObject(ja_banners.get(i).toString(), BannerBean.class);

                                    imagePath.add(bean.getImage_url());
                                    imageTitle.add(bean.getH5_title());

                                    String action_content = bean.getAction_content();
                                    String action_type = bean.getAction_type();
                                    if("h5".equals(action_type)){
                                        if(action_content.contains("?")){
                                            if(access_token.contains(" ")){
                                                action_content += "&client=android&token="+access_token.split(" ")[1];
                                            }else{
                                                action_content += "&client=android&token="+access_token;
                                            }
                                        }else{
                                            if(access_token.contains(" ")){
                                                action_content += "?client=android&token="+access_token.split(" ")[1];
                                            }else{
                                                action_content += "?client=android&token="+access_token;
                                            }
                                        }
                                    }
                                    urlPath.add(action_content);
                                    typeList.add(action_type);

//                                urlPath.add(URLEncoder.encode(action_content));
//                                imageTitle.add("");
                                }

                                mBanner.setBannerTitles(imageTitle);
                                mBanner.setImages(imagePath).setOnBannerListener(MainFragment.this).start();
                            }


                        } catch (Exception e) {
                            closeLoadingDialog();
                            LogUtil.e("mf===banner_e", "==="+e);
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                        if(fresh){
                            closeLoadingDialog();
                        }


                    }
                });
            }
        });
    }

    private void car_can_unlock() {
        LogUtil.e("mf===car_can_unlock", loadingDialog.isShowing() + "===" + codenum);

        HttpHelper.get(context, Urls.car_can_unlock, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                LogUtil.e("mf===car_can_unlock=f", "===" + throwable.toString());
                onFailureCommon("mf===car_can_unlock", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtil.e("mf===car_can_unlock0", responseString + "==="+loadingDialog.isShowing());
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            int unauthorized = new JSONObject(result.getData()).getInt("unauthorized");

                            if(unauthorized==0){

                                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                }
                                //蓝牙锁
                                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

                                mBluetoothAdapter = bluetoothManager.getAdapter();

                                if (mBluetoothAdapter == null) {
                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                    return;
                                }
                                if (!mBluetoothAdapter.isEnabled()) {
                                    isPermission = false;
                                    closeLoadingDialog2();
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 188);
                                } else {

                                    openAgain();

                                }
                            }else if(unauthorized==1){
                                ToastUtil.showMessageApp(context, "您已提交问题反馈，暂时无法开锁");
                                closeLoadingDialog();
                            }else if(unauthorized==2){
                                ToastUtil.showMessageApp(context, "当前行程已结束");
                                car_authority();
                            }

//                            JSONArray ja_banners = new JSONArray(new JSONObject(result.getData()).getString("banners"));

                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                            closeLoadingDialog();
                            LogUtil.e("mf===car_can_unlock-e", "==="+e);
                        }

                    }
                });
            }
        });
    }

    private void car_can_lock() {
        LogUtil.e("mf===car_can_lock", "===" + codenum);

        HttpHelper.get(context, Urls.car_can_unlock, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                LogUtil.e("mf===car_can_lock=f", "===" + throwable.toString());
                onFailureCommon("mf===car_can_lock", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtil.e("mf===car_can_lock0", responseString + "===");
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            int unauthorized = new JSONObject(result.getData()).getInt("unauthorized");

                            if(unauthorized==2){
                                ToastUtil.showMessageApp(context, "当前行程已结束");
                                car_authority();
                            }else{
                                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                }
                                //蓝牙锁
                                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

                                mBluetoothAdapter = bluetoothManager.getAdapter();

                                if (mBluetoothAdapter == null) {
                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                    return;
                                }
                                if (!mBluetoothAdapter.isEnabled()) {
                                    isPermission = false;
                                    closeLoadingDialog2();
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 188);
                                } else {
                                    if("10".equals(type)){
                                        car_authority_auto();
                                    }else{
                                        closeAgain();
                                    }
                                }

                            }

                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                            closeLoadingDialog();
                            LogUtil.e("mf===car_can_lock-e", "==="+e);
                        }
                    }
                });
            }
        });
    }

    private void car_nearby() {
        LogUtil.e("mf===car_nearby", "===" + codenum);

        HttpHelper.get(context, Urls.car_nearby, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                LogUtil.e("mf===car_nearby=f", "===" + throwable.toString());
                onFailureCommon("mf===car_nearby", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtil.e("mf===car_nearby1", responseString + "===");
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

//                            int unauthorized = new JSONObject(result.getData()).getInt("unauthorized");
//
//                            if(unauthorized==0){
//                                openAgain();
//                            }else {
//                                ToastUtil.showMessageApp(context, "问题反馈中，不能开锁");
//                            }

//                            JSONArray ja_banners = new JSONArray(new JSONObject(result.getData()).getString("banners"));

                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                            closeLoadingDialog();
                            LogUtil.e("mf===car_nearby_e", "==="+e);
                        }

                        closeLoadingDialog();

                    }
                });
            }
        });
    }

    private void openAgain(){

        if("2".equals(type) || "3".equals(type)  || "9".equals(type) || "10".equals(type)){

            LogUtil.e("openAgain===2", loadingDialog.isShowing()+"==="+isLookPsdBtn+"==="+m_nowMac);

            if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//              scrollToFinishActivity();
            }
            //蓝牙锁
            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

            mBluetoothAdapter = bluetoothManager.getAdapter();

            if (mBluetoothAdapter == null) {
                ToastUtil.showMessageApp(context, "获取蓝牙失败");
//              scrollToFinishActivity();
                return;
            }
            if (!mBluetoothAdapter.isEnabled()) {
                isPermission = false;
                closeLoadingDialog2();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 188);
            } else {

                if(!"3".equals(type)){
                    m_myHandler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            LogUtil.e("openAgain===2_timeout", type + "===" + state + "===" + lockStatus + "===" + remark);

                            if(lockStatus==2 || lockStatus==3){
                                ToastUtil.showMessageApp(context, "开锁超时");
                                car_notification(4, lockStatus, 0, type+"===开锁超时");
                            }else{
                                closeLoadingDialog();
                                endBle();
                            }

                        }
                    }, 10 * 1000);

                    state = 0;
                    lockStatus = 2;
                }


                LogUtil.e("openAgain===2_1", isLookPsdBtn + "===");

                if(!isLookPsdBtn){   //没连上
                    isTemp = true;
                    isOpenLock = true;
                    connect();
//                    scan2();
                }else{
//                    BaseApplication.getInstance().getIBLE().openLock();

                    if(token==null || "".equals(token)){
                        getBleToken();
                    }else{
                        openLock();
                    }
                }

            }
        }else if("4".equals(type) || "8".equals(type)){
            unlock();

//            if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                popupwindow.dismiss();
//            }
//            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
//            mBluetoothAdapter = bluetoothManager.getAdapter();
//
//            BLEService.bluetoothAdapter = mBluetoothAdapter;
//
//            bleService.view = context;
//            bleService.showValue = true;
//
//            if (mBluetoothAdapter == null) {
//                ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                popupwindow.dismiss();
//                return;
//            }
//            if (!mBluetoothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, 188);
//            } else {
//                LogUtil.e("openAgain===onClick_4", "再次开锁==="+bleid + "==="+m_nowMac);
//
//                bleService.connect(m_nowMac);
//
//                checkConnect();
//            }
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
                isPermission = false;
                closeLoadingDialog2();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 188);
            } else {
                LogUtil.e("openAgain===5", "==="+isLookPsdBtn+"==="+m_nowMac);

                m_myHandler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        LogUtil.e("openAgain===5_timeout", type + "===" + state + "===" + lockStatus + "===" + remark);

                        if(lockStatus==2 || lockStatus==3){
                            getBleRecord();
                            ToastUtil.showMessageApp(context, "开锁超时");
                            car_notification(4, lockStatus, 0, type+"===开锁超时");
                        }else{
                            closeLoadingDialog();
                        }

                    }
                }, 10 * 1000);

                state = 0;
                lockStatus = 2;
                m_myHandler.sendEmptyMessage(0x98);

            }
        }else if("7".equals(type)){
            if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                popupwindow.dismiss();
            }
            //蓝牙锁
            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

            mBluetoothAdapter = bluetoothManager.getAdapter();

            if (mBluetoothAdapter == null) {
                ToastUtil.showMessageApp(context, "获取蓝牙失败");
                popupwindow.dismiss();
                return;
            }
            if (!mBluetoothAdapter.isEnabled()) {
                isPermission = false;
                closeLoadingDialog2();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 188);
            } else {
                LogUtil.e("openAgain===onClick_7", "再次开锁===" + isConnect + "===" + deviceuuid + "===" + apiClient);

                if(apiClient==null){
                    XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                    builder.setBleStateChangeListener(MainFragment.this);
                    builder.setScanResultCallback(MainFragment.this);
                    apiClient = builder.build();
                }

                if(!isConnect){


                    MainFragmentPermissionsDispatcher.connectDeviceWithPermissionCheck(MainFragment.this, deviceuuid);

                    isConnect = false;
                    m_myHandler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isConnect){

                                LogUtil.e("openAgain===7==timeout", "再次开锁==="+isConnect + "==="+activity.isFinishing());

                                if (apiClient != null) {
                                    apiClient.disConnect();
                                    apiClient.onDestroy();
                                    apiClient=null;
                                }

                                unlock();
                            }
                        }
                    }, timeout);
                }else{
                    xiaoanOpen_blue();
                }
            }
        }else if("11".equals(type)){
            if(!TbitBle.hasInitialized()){
                TbitBle.initialize(context, new SecretProtocolAdapter());
            }

            LogUtil.e("openAgain===onClick_11", "再次开锁===" + TbitBle.getBleConnectionState() + "===" + isConnect + "===" + deviceuuid + "===" + bleid);    //865532045872401


            if(TbitBle.getBleConnectionState()==0){
                unlock();

                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                    popupwindow.dismiss();
                }
                //蓝牙锁
                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

                mBluetoothAdapter = bluetoothManager.getAdapter();

                if (mBluetoothAdapter == null) {
                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                    popupwindow.dismiss();
                    return;
                }
                if (!mBluetoothAdapter.isEnabled()) {
                    isPermission = false;
                    closeLoadingDialog2();
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 188);
                } else {
                    tbtble_connect2();
                }
            }else{
                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                    popupwindow.dismiss();
                }
                //蓝牙锁
                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

                mBluetoothAdapter = bluetoothManager.getAdapter();

                if (mBluetoothAdapter == null) {
                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                    popupwindow.dismiss();
                    return;
                }
                if (!mBluetoothAdapter.isEnabled()) {
                    isPermission = false;
                    closeLoadingDialog2();
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 188);
                } else {

                    tbtble_unlock_temp();

                }
            }

        }


    }

    private void closeAgain(){
        if("4".equals(type) || "8".equals(type)){
            lock();

//                        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                            ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                            popupwindow.dismiss();
//                        }
//                        BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
//                        mBluetoothAdapter = bluetoothManager.getAdapter();
//
//                        BLEService.bluetoothAdapter = mBluetoothAdapter;
//
//                        bleService.view = context;
//                        bleService.showValue = true;
//
//                        if (mBluetoothAdapter == null) {
//                            ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                            popupwindow.dismiss();
//                            return;
//                        }
//                        if (!mBluetoothAdapter.isEnabled()) {
//                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                            startActivityForResult(enableBtIntent, 188);
//                        } else {
//                            LogUtil.e("openAgain===onClick_4", "临时上锁==="+bleid + "==="+m_nowMac);
//
//                            bleService.connect(m_nowMac);
//
//                            checkConnect2();
//                        }
        }else if("7".equals(type)){
            if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                popupwindow.dismiss();
            }
            //蓝牙锁
            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

            mBluetoothAdapter = bluetoothManager.getAdapter();

            if (mBluetoothAdapter == null) {
                ToastUtil.showMessageApp(context, "获取蓝牙失败");
                popupwindow.dismiss();
                return;
            }
            if (!mBluetoothAdapter.isEnabled()) {
                isPermission = false;
                closeLoadingDialog2();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 188);
            } else {
                LogUtil.e("closeAgain===onClick_7", "临时上锁===" + isConnect + "===" + deviceuuid + "===" + apiClient);

                if(apiClient==null){
                    XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                    builder.setBleStateChangeListener(MainFragment.this);
                    builder.setScanResultCallback(MainFragment.this);
                    apiClient = builder.build();
                }

                if(!isConnect){


                    MainFragmentPermissionsDispatcher.connectDeviceWithPermissionCheck(MainFragment.this, deviceuuid);

                    isConnect = false;
                    m_myHandler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isConnect){
//                                            if (loadingDialog != null && loadingDialog.isShowing()) {
//                                                loadingDialog.dismiss();
//                                            }

                                LogUtil.e("closeAgain===7==timeout", "临时上锁==="+isConnect + "==="+activity.isFinishing());

                                if (apiClient != null) {
                                    apiClient.disConnect();
                                    apiClient.onDestroy();
                                    apiClient=null;
                                }

                                lock();
                            }
                        }
                    }, timeout);
                }else{
                    xiaoanClose_blue();
                }
            }
        }else if("11".equals(type)){
            if(!TbitBle.hasInitialized()){
                TbitBle.initialize(context, new SecretProtocolAdapter());
            }

            LogUtil.e("closeAgain===onClick_11", "临时上锁===" + TbitBle.getBleConnectionState()+"===" + isConnect + "===" + deviceuuid + "===" + bleid);    //865532045872401


            if(TbitBle.getBleConnectionState()==0){
                lock();

                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                    popupwindow.dismiss();
                }
                //蓝牙锁
                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

                mBluetoothAdapter = bluetoothManager.getAdapter();

                if (mBluetoothAdapter == null) {
                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                    popupwindow.dismiss();
                    return;
                }
                if (!mBluetoothAdapter.isEnabled()) {
                    isPermission = false;
                    closeLoadingDialog2();
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 188);
                } else {
                    tbtble_connect2();
                }
            }else{
                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                    popupwindow.dismiss();
                }
                //蓝牙锁
                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

                mBluetoothAdapter = bluetoothManager.getAdapter();

                if (mBluetoothAdapter == null) {
                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                    popupwindow.dismiss();
                    return;
                }
                if (!mBluetoothAdapter.isEnabled()) {
                    isPermission = false;
                    closeLoadingDialog2();
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 188);
                } else {
                    tbtble_lock_temp();
                }
            }

//            if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                popupwindow.dismiss();
//            }
//            //蓝牙锁
//            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
//
//            mBluetoothAdapter = bluetoothManager.getAdapter();
//
//            if (mBluetoothAdapter == null) {
//                ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                popupwindow.dismiss();
//                return;
//            }
//            if (!mBluetoothAdapter.isEnabled()) {
//                isPermission = false;
//                closeLoadingDialog2();
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, 188);
//            } else {
//                LogUtil.e("closeAgain===onClick_11", "临时上锁===" + isConnect + "===" + deviceuuid + "===" + bleid + "===" + TbitBle.getBleConnectionState() + "===" + TbitBle.getState());
//
////                TbitBle.initialize(context, new SecretProtocolAdapter());
//
////                // 获得当前蓝牙连接状态
////                TbitBle.getBleConnectionState();
////                // 获得最后一次更新的车辆状态信息(需要更新请执行更新操作并等待相应回调)
////                TbitBle.getState();
//
//                if(TbitBle.getBleConnectionState()==0){
//                    tbtble_connect();
//
////                    m_myHandler.postDelayed(new Runnable() {
////                        @Override
////                        public void run() {
////
////                            LogUtil.e("biking===closeAgain11_3", "timeout==="+isConnect+"==="+TbitBle.getBleConnectionState());
////                            TbitBle.disConnect();
////                            lock();
////
//////                            if (TbitBle.getBleConnectionState()==0){
//////
//////                            }
////                        }
////                    }, timeout);
//                }else{
//                    tbtble_lock_temp();
//                }
//
//
//            }
        }else{
//            temporaryAction();  //泺平锁临时上锁
        }
    }

    //泰比特209D蓝牙临时上锁
    private void tbtble_lock_temp(){
        TbitBle.commonCommand((byte)0x03, (byte)0x01, new Byte[]{0x30},
                new ResultCallback() {
                    @Override
                    public void onResult(final int resultCode) {
                        // 发送状态回复

                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                try{
                                    LogUtil.e("commonCommand===lock", isAgain+"==="+resultCode);

                                    if(resultCode==0){

                                        macList2 = new ArrayList<> (macList);

                                        car_notification(isAgain?2:3, 1, isAgain?0:1, "");

                                        if(isAgain){
                                            tv_againBtn.setText("再次开锁");
                                            SharedPreferencesUrls.getInstance().putString("tempStat","1");

                                            ToastUtil.showMessageApp(context,"恭喜您,关锁成功!");

//                                            closeLoadingDialog();
                                        }
                                    }else{
                                        car_notification(2, 2, 0, type+"===tbtble_lock_temp==="+resultCode);
//                                        closeLoadingDialog();
                                        LogUtil.e("tbtble_lock_temp_f", "==="+resultCode);

//                                        lock();
                                    }
                                }catch(Exception e){
                                    closeLoadingDialog();
                                    LogUtil.e("commonCommand===lock_e", "==="+e);

                                }



                            }
                        });


                    }
                }, new PacketCallback() {
                    @Override
                    public void onPacketReceived(Packet packet) {
                        // 收到packet回复
                    }
                });
    }

    //泰比特209D蓝牙临时开锁
    private void tbtble_unlock_temp(){
        TbitBle.commonCommand((byte)0x03, (byte)0x01, new Byte[]{0x31},
            new ResultCallback() {
                @Override
                public void onResult(final int resultCode) {
                    // 发送状态回复

                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            try{
                                LogUtil.e("commonCommand===unlock", isAgain+"==="+resultCode);

                                if(resultCode==0){
                                    car_notification(isAgain?4:1, 1, 0, "");

                                    if(isAgain){
                                        tv_againBtn.setText("临时上锁");
                                        SharedPreferencesUrls.getInstance().putString("tempStat","0");

                                        ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

//                                        closeLoadingDialog();
                                    }

                                }else{
                                    car_notification(4, 2, 0, type+"===tbtble_unlock_temp==="+resultCode);
//                                    closeLoadingDialog();
                                    LogUtil.e("tbtble_unlock_temp_f", "==="+resultCode);

//                                    unlock();
                                }
                            }catch(Exception e){
                                closeLoadingDialog();
                                LogUtil.e("commonCommand===unlock_e", "==="+e);
                            }

                        }
                    });


                }
            }, new PacketCallback() {
                @Override
                public void onPacketReceived(Packet packet) {
                    // 收到packet回复
                }
            });
    }


    @Override
    public void onClick(View view) {
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");

//        LogUtil.e("onClick===mf", "==="+access_token);

        switch (view.getId()){
            case R.id.rl_ad:
//                initmPopupWindowView();

//                initmPopupPayWindowView();

                LogUtil.e("rl_ad===onclick", isContainsList+"===");

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

            case R.id.ui_adv_againBtn0:
                if (advDialog0 != null && advDialog0.isShowing()) {
                    advDialog0.dismiss();
                }

                SharedPreferencesUrls.getInstance().putBoolean("isKnow0", true);

                break;

            case R.id.ui_adv_closeBtn0:
                if (advDialog0 != null && advDialog0.isShowing()) {
                    advDialog0.dismiss();
                }
                break;

            case R.id.ui_adv_againBtn:
                if (advDialog != null && advDialog.isShowing()) {
                    advDialog.dismiss();
                }

                SharedPreferencesUrls.getInstance().putBoolean("isKnow", true);

                break;

            case R.id.ui_adv_closeBtn:
                if (advDialog != null && advDialog.isShowing()) {
                    advDialog.dismiss();
                }
                break;

            case R.id.ui_adv_closeBtn2:
                if (advDialog2 != null && advDialog2.isShowing()) {
                    advDialog2.dismiss();
                }
                break;

            case R.id.ll_payBtn:
                if ("".equals(SharedPreferencesUrls.getInstance().getString("access_token",""))){
                    UIHelper.goToAct(context, LoginActivity.class);
                    ToastUtil.showMessageApp(context,"请先登录你的账号");
                    return;
                }

                end();
                break;

            case R.id.rl_authBtn:
                if (access_token == null || "".equals(access_token)){
                    UIHelper.goToAct(context,LoginActivity.class);
                }else {
//                    if ("2".equals(SharedPreferencesUrls.getInstance().getString("iscert",""))){
//
//                    }else {
//                        UIHelper.goToAct(context,RealNameAuthActivity.class);
//                    }


//                    未授权码 0（有权限时为0）1需要登录 2未认证 3认证中 4认证被驳回 5需要充值余额或购买骑行卡 6有进行中行程 7有待支付行程 8有待支付调度费 9有待支付赔偿费
                    switch (SharedPreferencesUrls.getInstance().getString("iscert","")){
                        case "1":   //1未登录
//                            closeBroadcast();
//                            deactivate();

                            UIHelper.goToAct(context, LoginActivity.class);
                            break;

                        case "2":   //2未认证
                            UIHelper.goToAct(context, AuthCenterActivity.class);
                            break;

                        case "3":   //3认证中
                            UIHelper.goToAct(context, AuthCenterActivity.class);
                            break;

                        case "4":   //4认证被驳回
                            UIHelper.goToAct(context, AuthCenterActivity.class);
                            break;

                        case "5":   //5未充值或购买骑行卡
                            ((MainActivity)getActivity()).changeTab(1);
                            break;

                        default:
                            break;
                    }
                }
                break;


            case R.id.mainUI_refreshLayout:
                LogUtil.e("refreshLayout===0", notice_code+"==="+isNavi+"==="+isMin+"==="+isConnect+"==="+isLookPsdBtn+"==="+isContainsList+"==="+SharedPreferencesUrls.getInstance().getString("iscert", ""));
                LogUtil.e("refreshLayout===1", isContainsList.size()+"==="+isContainsList.contains(true)+"==="+isContainsList);


                first = false;


                if(!isNavi){
                    banner(true);
                    car_authority();
                }


//                bikeFragment.sr();
//                ebikeFragment.sr();

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
                LogUtil.e("fragment===", bikeFragment.isHidden()+"==="+ebikeFragment.isHidden());

//                if(!bikeFragment.isHidden()){
//                    UIHelper.goWebViewAct(context,"停车须知",Urls.phtml5 + uid);
//                }else{
//                    UIHelper.goWebViewAct(context,"停车须知",Urls.ebike_phtml5 + uid);
//                }

                advDialog2.getWindow().setBackgroundDrawableResource(R.color.transparent);
                advDialog2.show();

                break;

            case R.id.mainUI_linkServiceLayout:
                if(popupwindow2==null || (popupwindow2!=null && !popupwindow2.isShowing())){
                    initmPopupWindowView();
                }

                break;

            case R.id.ll_change_car:
                popupwindow.dismiss();

                Intent intent = new Intent();
                intent.setClass(context, ActivityScanerCode.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                break;

            case R.id.iv_rent_cancelBtn:
                LogUtil.e("ll_rent_cancelB=onClick", "==="+access_token+"==="+SharedPreferencesUrls.getInstance().getString("iscert",""));

                popupwindow.dismiss();

                break;

            case R.id.ll_rent:

                LogUtil.e("ll_rent===onClick", "==="+isOrder);

                if(!isOrder){
                    isOrder = true;
                    LogUtil.e("ll_rent===", loadingDialog.isShowing()+"==="+type+"==="+access_token+"==="+SharedPreferencesUrls.getInstance().getString("iscert",""));

                    isOpenLock = false;
                    isConnect = false;
                    isLookPsdBtn = false;
                    isEndBtn = false;
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
                        return;
                    }

                    if (!mBluetoothAdapter.isEnabled()) {
                        flagm = 1;
                        isPermission = false;
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, 188);
                    } else {
                        isStop = false;
                        isOpen = false;
                        isFinish = false;
                        n = 0;
                        cn = 0;
                        force_backcar = 0;
                        isTwo = false;
                        first3 = true;
                        flagm = 0;
                        isFrist1 = true;
                        stopScan = false;
                        clickCount = 0;
                        tz = 0;
                        transtype = "";
                        major = 0;
                        minor = 0;
                        isGPS_Lo = false;
                        scan = false;
                        isTemp = false;
                        backType = "";
                        open = 0;
                        isBleInit = false;
                        loopTime = 1 * 1000;

                        order_type = 0;
                        isWaitEbikeInfo = true;
                        ebikeInfoThread = null;
                        oid = "";

                        if ("2".equals(type) || "3".equals(type) || "9".equals(type) || "10".equals(type)){

//                      closeBroadcast();     //TODO    3
//                      activity.registerReceiver(broadcastReceiver, Config.initFilter());
//                      GlobalParameterUtils.getInstance().setLockType(LockType.MTS);

                            bleInit();

                        }else if("4".equals(type) || "8".equals(type)){

//                        BLEService.bluetoothAdapter = mBluetoothAdapter;
//                        bleService.view = context;
//                        bleService.showValue = true;
                        }else if ("5".equals(type)  || "6".equals(type)) {
                            LogUtil.e("ll_rent===5", "==="+isLookPsdBtn);

//                          ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
//                          ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);
                        }else if ("7".equals(type)) {
                        }

                        SharedPreferencesUrls.getInstance().putString("tempStat", "0");
                        if (carmodel_id==2) {
                            tv_againBtn.setText("临时上锁");

                        }else{
                            tv_againBtn.setText("再次开锁");
                        }

                        refreshLayout.setVisibility(View.VISIBLE);

                        order();
                    }
                }



                break;

            case R.id.ll_biking_openAgain:
            case R.id.ll_biking_openAgain_auto:



                String tvAgain;
                if("10".equals(type)){
                    tvAgain = tv_againBtn_auto.getText().toString().trim();
                }else{
                    tvAgain = tv_againBtn.getText().toString().trim();
                }

                LogUtil.e("ll_openAgain===onClick", carmodel_id+"==="+tvAgain+"==="+type+"==="+access_token+"==="+SharedPreferencesUrls.getInstance().getString("iscert",""));

                isAgain = true;
                isEndBtn = false;

//                closeLoadingDialog();
//
//                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                    loadingDialog.setTitle("正在唤醒车锁");
//                    loadingDialog.show();
//                }

                if ("临时上锁".equals(tvAgain)){
                    btnLog("A2");
//                    car_can_lock();
                    carInfo_close();
                }else if ("再次开锁".equals(tvAgain)){
                    btnLog("A3");
//                    car_can_unlock();

                    cycling5();
                }

                break;

            case R.id.ll_biking_endBtn:
                btnLog("A1");

                if(!isEndClick){
                    isEndClick = true;
                    cycling4();
                }

                break;

            case R.id.ll_biking_errorEnd:
                cycling6();
                break;

            case R.id.ll_biking_errorEnd_auto:
                LogUtil.e("ll_errorEnd===onClick", access_token+"==="+SharedPreferencesUrls.getInstance().getString("iscert",""));
                customDialog4.show();
                break;

            case R.id.mainUI_scanCode_lock:
                LogUtil.e("scanCode_lock===mf", SharedPreferencesUrls.getInstance().getString("iscert","")+"==="+access_token);

//                if (access_token == null || "".equals(access_token)){
//                    ToastUtil.showMessageApp(context,"请先登录账号");
//                    UIHelper.goToAct(context,LoginActivity.class);
//                    return;
//                }

                car_authority2();

                break;

            case R.id.btn_clear:
                testLog="";
                tv_test.setText("");
                break;

            case R.id.btn_log:

                if(!isLog){
                    isLog = true;
                    sv_test.setVisibility(View.VISIBLE);

                    scrollToBottom(sv_test, tv_test);
                }else{
                    isLog = false;
                    sv_test.setVisibility(View.GONE);
                }

                break;

            default:
                break;
        }
    }

    private void btnLog(String event) {
        LogUtil.e("mf===btnLog", event+"===");

        RequestParams params = new RequestParams();
        params.put("event", event); //0上报未临时上锁(再次开锁成功后上报) 1上报临时上锁中

        HttpHelper.post(context, Urls.log, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                LogUtil.e("mf===btnLog_fail", responseString + "===" + throwable.toString());
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("mf===btnLog_1", "===" + responseString);


                        } catch (Exception e) {
//                            closeLoadingDialog();
                            LogUtil.e("mf===btnLog_e", "==="+e);
                        }

                    }
                });
            }
        });
    }

    private void bleInit(){
        BleManager.getInstance().init(activity.getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(4, 2000)
                .setConnectOverTime(10000)
                .setOperateTimeout(10000)
                ;
    }

    public static void scrollToBottom(final View scroll, final View inner) {

        Handler mHandler = new Handler();

        mHandler.post(new Runnable() {
            public void run() {
                if (scroll == null || inner == null) {
                    return;
                }
                int offset = inner.getMeasuredHeight() - scroll.getHeight();
                if (offset < 0) {
                    offset = 0;
                }

                scroll.scrollTo(0, offset);
            }
        });
    }


    private void endCar(){
        try {
            LogUtil.e("ll_endBtn===onClick", loadingDialog.isShowing()+"==="+access_token+"==="+SharedPreferencesUrls.getInstance().getString("iscert",""));

            isAgain = false;
            isEndBtn = true;

            if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
            }
            //蓝牙锁
            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();

            if (mBluetoothAdapter == null) {
                ToastUtil.showMessageApp(context, "获取蓝牙失败");
                return;
            }
            if (!mBluetoothAdapter.isEnabled()) {
                LogUtil.e("queryState===1", "===");
                isPermission = false;
                closeLoadingDialog();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 188);

                return;
            }

            clickCount++;

    //                test_xinbiao += major+"==="+minor+"\n";   //TODO  3
    //                tv_test_xinbiao.setText(test_xinbiao);

            LogUtil.e("biking_endBtn===", loadingDialog.isShowing()+"==="+type+"==="+isLookPsdBtn+"==="+major+"==="+isContainsList.contains(true)+"==="+referLatitude+"==="+referLongitude);

            if(major !=0){
                if("5".equals(type) || "6".equals(type)){
                    queryState();
                }else{
    //                flag = 2;
                    if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                        ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
    //                    scrollToFinishActivity();
                    }

                    if (!BaseApplication.getInstance().getIBLE().isEnable()){
                        BaseApplication.getInstance().getIBLE().enableBluetooth();
                        return;
                    }
    //                if (BaseApplication.getInstance().getIBLE().getConnectStatus()){

                    if (isLookPsdBtn){
                        if (loadingDialog != null && !loadingDialog.isShowing()){
                            loadingDialog.setTitle("请稍等");
                            loadingDialog.show();
                        }

                        LogUtil.e("biking===endBtn_2",macList.size()+"==="+isLookPsdBtn+"==="+force_backcar);


                        macList2 = new ArrayList<> (macList);
    //                    BaseApplication.getInstance().getIBLE().getLockStatus();
                        getLockStatus();
                    } else {
                        LogUtil.e("biking===endBtn_3",macList.size()+"==="+isLookPsdBtn+"==="+force_backcar);

                        if (loadingDialog != null && !loadingDialog.isShowing()){
                            loadingDialog.setTitle("正在连接");
                            loadingDialog.show();
                        }

                        isOpenLock = false;
                        connect();
    //                    scan2();
                    }

                }
            }else if(carmodel_id==2){
                car();
            }else{
                startXB();

                if(!isContainsList.contains(true)){
                    minPoint(referLatitude, referLongitude);    //30米补偿
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int n=0;

                            LogUtil.e("mf===endBtn_4", macList.size()+"==="+isContainsList.contains(true)+"==="+customDialog.isShowing());

                            while(macList.size() == 0 && !isContainsList.contains(true)){
                                Thread.sleep(100);
                                n++;

    //                            if(n%20==0 && n%40!=0){
    ////                                scanManager.stopScan();
    ////                                scanManager.startScan();
    //
    //                                stopXB();
    ////                                startXB();
    //
    //                                LogUtil.e("biking===n2",macList.size()+"=="+n);
    //                            }
    //
    //                            if(n%40==0){
    ////                                scanManager.stopScan();
    ////                                scanManager.startScan();
    //
    ////                                stopXB();
    //                                startXB();
    //
    //                                LogUtil.e("biking===n3",macList.size()+"=="+n);
    //                            }

                                LogUtil.e("mf===n",macList.size()+"=="+n);

                                if(n>=101) break;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        m_myHandler.sendEmptyMessage(3);
                    }
                }).start();
            }

        } catch (Exception e) {
            closeLoadingDialog();
            LogUtil.e("mf===endBtn_e", "==="+e);
            e.printStackTrace();
        }
    }

    private void order() {
//        LogUtil.e("order===", "==="+codenum);
        LogUtil.e("order===", "==="+codenum);

        RequestParams params = new RequestParams();
        params.put("order_type", 1);        //订单类型 1骑行订单 2套餐卡订单 3充值订单 4认证充值订单
        params.put("car_number", URLEncoder.encode(codenum));
//        params.put("card_code", "");        //套餐卡券码（order_type为2时必传）
//        params.put("price", "");        //传价格数值 例如：20.00(order_type为3、4时必传)

        HttpHelper.post(context, Urls.order, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在唤醒车锁");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                isOrder = false;
                onFailureCommon(throwable.toString());
                closeLoadingDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                isOrder = false;
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("order===1", responseString + "===" + result.data);

                            JSONObject jsonObject = new JSONObject(result.getData());

                            SharedPreferencesUrls.getInstance().putString("type", type);

                            order_id2 = jsonObject.getInt("order_id");
                            oid = jsonObject.getString("order_sn");

                            LogUtil.e("order===1_2",  order_id2 + "===" + isLookPsdBtn + "===" + type + "===" + jsonObject.getString("order_sn"));

                            isLookPsdBtn = false;
                            isOpenLock = true;

//                            closeLoadingDialog();
//
//                            if (loadingDialog != null && !loadingDialog.isShowing()) {
//                                loadingDialog.setTitle("正在唤醒车锁");
//                                loadingDialog.show();
//                            }

                            if ("1".equals(type)) {          //单车机械锁
                                UIHelper.goToAct(context, CurRoadStartActivity.class);
                                popupwindow.dismiss();
                            } else if ("2".equals(type) || "3".equals(type) || "9".equals(type) || "10".equals(type)) {    //单车蓝牙锁

                                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                    popupwindow.dismiss();
                                }
                                //蓝牙锁
                                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

                                mBluetoothAdapter = bluetoothManager.getAdapter();

                                if (mBluetoothAdapter == null) {
                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                    popupwindow.dismiss();
                                    return;
                                }
                                if (!mBluetoothAdapter.isEnabled()) {
                                    isPermission = false;
                                    closeLoadingDialog2();
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 188);
                                } else {

                                    LogUtil.e("order===2",  isMac + "===" + isLookPsdBtn + "===" + type + "===" + jsonObject.getString("order_sn"));

                                    if (!TextUtils.isEmpty(m_nowMac)) {
                                        isOpenLock = true;

//                                        if(isMac){
//                                            connect();
//                                        }else{
//                                            setScanRule();
//                                            scan2();
//                                        }

                                        if(!"3".equals(type)){
                                            m_myHandler2.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {


                                                    LogUtil.e("mf===2_timeout", type + "===" + state + "===" + lockStatus + "===" + remark);

                                                    if(lockStatus==2 || lockStatus==3){
                                                        ToastUtil.showMessageApp(context, "开锁超时");
                                                        car_notification(1, lockStatus, 0, type+"===开锁超时");
                                                    }else{
                                                        closeLoadingDialog();

                                                        endBle();
                                                    }

                                                }
                                            }, 10 * 1000);

                                            state = 0;
                                            lockStatus = 2;
                                        }

                                        connect();
                                    }
                                }
                            }else if ("4".equals(type) || "8".equals(type)) {

                                unlock();

                                //TODO  2
//                                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                                    popupwindow.dismiss();
//                                }
//                                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
//                                mBluetoothAdapter = bluetoothManager.getAdapter();
//
//                                BLEService.bluetoothAdapter = mBluetoothAdapter;
//
//                                bleService.view = context;
//                                bleService.showValue = true;
//
//                                if (mBluetoothAdapter == null) {
//                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                                    popupwindow.dismiss();
//                                    return;
//                                }
//                                if (!mBluetoothAdapter.isEnabled()) {
//                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                                    startActivityForResult(enableBtIntent, 188);
//                                } else {
//                                    LogUtil.e("mf===4_1", bleid + "==="+m_nowMac);
//
//                                    bleService.connect(m_nowMac);
//
//                                    checkConnect();
//                                }

                            }else if ("5".equals(type) || "6".equals(type)) {      //泺平单车蓝牙锁

                                LogUtil.e("mf===5_1", deviceuuid + "==="+m_nowMac);

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
                                }
                                //蓝牙锁
                                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

                                mBluetoothAdapter = bluetoothManager.getAdapter();

                                if (mBluetoothAdapter == null) {
                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                    popupwindow.dismiss();
                                    return;
                                }
                                if (!mBluetoothAdapter.isEnabled()) {
                                    isPermission = false;
                                    closeLoadingDialog2();
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 188);
                                } else {
//                                    iv_help.setVisibility(View.VISIBLE);


//                                    m_myHandler2.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            if (loadingDialog != null && loadingDialog.isShowing()){
//                                                loadingDialog.dismiss();
//                                            }
//
//                                            LogUtil.e("mf===5_timeout", type + "===" + state + "===" + lockStatus + "===" + remark);
//
//                                            if(lockStatus==2 || lockStatus==3){
//                                                getBleRecord();
//                                                ToastUtil.showMessageApp(context, "开锁超时");
//                                                car_notification(1, lockStatus, 0, type+"===开锁超时");
//                                            }
//
//                                        }
//                                    }, 10 * 1000);

                                    state = 0;
                                    lockStatus = 2;
                                    m_myHandler.sendEmptyMessage(0x98);

                                }
                            }else if ("7".equals(type)) {
                                LogUtil.e("mf===7_1", deviceuuid + "==="+m_nowMac);

//                                unlock();

                                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                    popupwindow.dismiss();
                                }
                                //蓝牙锁
                                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

                                mBluetoothAdapter = bluetoothManager.getAdapter();

                                if (mBluetoothAdapter == null) {
                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                    popupwindow.dismiss();
                                    return;
                                }
                                if (!mBluetoothAdapter.isEnabled()) {
                                    isPermission = false;
                                    closeLoadingDialog2();
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 188);
                                } else {
                                    XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                                    builder.setBleStateChangeListener(MainFragment.this);
                                    builder.setScanResultCallback(MainFragment.this);
                                    apiClient = builder.build();

                                    MainFragmentPermissionsDispatcher.connectDeviceWithPermissionCheck(MainFragment.this, deviceuuid);

                                    isConnect = false;
                                    m_myHandler2.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!isConnect){
//                                                closeLoadingDialog();

                                                LogUtil.e("mf===7==timeout", loadingDialog.isShowing() + "==="+isConnect + "==="+activity.isFinishing()+ "==="+apiClient);

                                                if (apiClient != null) {
                                                    apiClient.disConnect();
                                                    apiClient.onDestroy();
                                                    apiClient = null;
                                                }

                                                unlock();
                                            }
                                        }
                                    }, timeout);
                                }
                            }else if ("11".equals(type)) {
                                LogUtil.e("mf===11_1", deviceuuid + "==="+ bleid + "==="+ m_nowMac);

                                unlock();

                                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                    popupwindow.dismiss();
                                }
                                //蓝牙锁
                                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

                                mBluetoothAdapter = bluetoothManager.getAdapter();

                                if (mBluetoothAdapter == null) {
                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                    popupwindow.dismiss();
                                    return;
                                }
                                if (!mBluetoothAdapter.isEnabled()) {
                                    isPermission = false;
                                    closeLoadingDialog2();
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 188);
                                } else {
                                    TbitBle.initialize(context, new SecretProtocolAdapter());
                                    tbtble_connect2();

                                }
                            }


                        } catch (Exception e) {
                            closeLoadingDialog();
                            LogUtil.e("order===e", "==="+e);
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });


            }
        });
    }

    //泰比特连接
    private void tbtble_connect() {

        TbitBle.connect(deviceuuid, bleid, new ResultCallback() {
            @Override
            public void onResult(final int resultCode) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            if(isNetSuc){
                                return;
                            }

                            // 连接回应
                            final String tvAgain = tv_againBtn.getText().toString().trim();
                            LogUtil.e("tbtble_connect===", resultCode+"==="+isOpenLock+"==="+isEndBtn+"==="+isAgain+"==="+tvAgain);

                            if(resultCode==0){
                                if(isAgain){
                                    if("再次开锁".equals(tvAgain)){
                                        tbtble_unlock_temp();
                                    }else{
                                        tbtble_lock_temp();
                                    }
                                }else{
                                    if(isEndBtn){
                                        tbtble_lock();
                                    }else{
                                        tbtble_unlock();
                                    }
                                }


                            }else{

                                if(isAgain){
                                    if("再次开锁".equals(tvAgain)){
                                        car_notification(4, 2, 0, type+"===tbtble_connect_f==="+resultCode);
                                    }else{
                                        car_notification(2, 2, 0, type+"===tbtble_connect_f===\""+resultCode);
                                    }


                                }else{
                                    if(isEndBtn){
                                        car_notification(3, 2, 0, type+"===tbtble_connect_f===\""+resultCode);
                                    }else{
                                        car_notification(1, 2, 0, type+"===tbtble_connect_f===\""+resultCode);
                                    }
                                }

//                                closeLoadingDialog();
                                LogUtil.e("tbtble_connect_f", "==="+resultCode);

//                                if(isAgain){
//                                    if("再次开锁".equals(tvAgain)){
//                                        action_type=4;
//                                    }else{
//                                        action_type=2;
//                                    }
//                                }else{
//                                    if(isEndBtn){
//                                        action_type=3;
//                                    }else{
//                                        action_type=1;
//                                    }
//                                }


//                                TbitBle.disConnect();

                            }
                        }catch(Exception e){
                            closeLoadingDialog();
                            LogUtil.e("tbtble_connect===e", "==="+e);

                        }

                    }
                });



            }
        }, new StateCallback() {
            @Override
            public void onStateUpdated(BikeState bikeState) {
                LogUtil.e("tbtble_connect=onStateU", bikeState+"===");

                // 连接成功状态更新
                // 通过车辆状态获取设备特定信息
                W206State w206State = (W206State) TbitBle.getConfig().getResolver().resolveCustomState(bikeState);
            }
        });
    }

    //泰比特连接
    private void tbtble_connect2() {
        TbitBle.connect(deviceuuid, bleid, new ResultCallback() {
            @Override
            public void onResult(int resultCode) {
                // 连接回应
                final String tvAgain = tv_againBtn.getText().toString().trim();
                LogUtil.e("tbtble_connect2===", resultCode+"==="+isOpenLock+"==="+isEndBtn+"==="+isAgain+"==="+tvAgain);


            }
        }, new StateCallback() {
            @Override
            public void onStateUpdated(BikeState bikeState) {
                LogUtil.e("tbtble_connect2=onStateU", bikeState+"===");

                // 连接成功状态更新
                // 通过车辆状态获取设备特定信息
                W206State w206State = (W206State) TbitBle.getConfig().getResolver().resolveCustomState(bikeState);
            }
        });
    }

    // 泰比特209D蓝牙开锁
    private void tbtble_unlock() {
        TbitBle.unlock(new ResultCallback() {
            @Override
            public void onResult(final int resultCode) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            // 解锁回应
                            LogUtil.e("tbtble_unlock===", resultCode+"==="+isEndBtn+"==="+isNetSuc);

                            if(resultCode==0){
//                              isOpened = true;

                                if(isNetSuc){
                                    return;
                                }
                                m_myHandler.sendEmptyMessage(7);
                            }else{

                                String tvAgain = tv_againBtn.getText().toString().trim();
                                int action_type;

                                if(isAgain){
                                    if("再次开锁".equals(tvAgain)){
                                        action_type=4;
                                    }else{
                                        action_type=2;
                                    }
                                }else{
                                    if(isEndBtn){
                                        action_type=3;
                                    }else{
                                        action_type=1;
                                    }
                                }

//                                closeLoadingDialog();
                                LogUtil.e("tbtble_unlock_f", "==="+resultCode);
                                car_notification(action_type, 3, 0, type+"===tbtble_unlock_f==="+resultCode);

//                                unlock();
                                ToastUtil.showMessageApp(context, "开锁失败");
                            }

                        }catch(Exception e){
                            closeLoadingDialog();
                            LogUtil.e("tbtble_unlock===e", "==="+e);

                        }



                    }
                });



            }
        });
    }

    // 泰比特209D蓝牙上锁
    private void tbtble_lock() {
        TbitBle.lock(new ResultCallback() {
            @Override
            public void onResult(final int resultCode) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            // 上锁回应
                            LogUtil.e("tbtble_lock===", resultCode+"==="+isEndBtn+"==="+isFinish);

                            if(resultCode==0){
                                if(isEndBtn){
                                    if(!isFinish){
                                        isFinish = true;
                                        car_notification(3, 1, 1, "");
                                    }
                                }
                            }else{
                                String tvAgain = tv_againBtn.getText().toString().trim();
                                int action_type;

                                if(isAgain){
                                    if("再次开锁".equals(tvAgain)){
                                        action_type=4;
                                    }else{
                                        action_type=2;
                                    }
                                }else{
                                    if(isEndBtn){
                                        action_type=3;
                                    }else{
                                        action_type=1;
                                    }
                                }

//                                closeLoadingDialog();
                                LogUtil.e("tbtble_lock_f", "==="+resultCode);
                                car_notification(action_type, 3, 0, type+"===tbtble_lock_f==="+resultCode);

//                                unlock();
                                ToastUtil.showMessageApp(context, "开锁失败");

//                                if(isEndBtn){
//                                    lock();
//                                }
                            }
                        }catch(Exception e){
                            closeLoadingDialog();
                            LogUtil.e("tbtble_lock===e", "==="+e);
                        }

                    }
                });
            }
        });
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
            isPermission = false;
            closeLoadingDialog2();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 188);
        }else{
            if (macList.size() != 0) {
                macList.clear();
            }

//            scanManager = new ScanManager(context);
////        scanManager.setUuid("00000000-0000-0000-0000-000000000000");//添加需要扫描的uuid 只有符合的才会返回 不设置返回所有
////        scanManager.setMajor(222);//添加需要扫描的beacon的major 只有符合的major才会返回 不设置返回所有
////        scanManager.setMinor(111);//添加需要扫描的beacon的minor 只有符合的minor才会返回 不设置返回所有
//            scanManager.setScanPeriod(100);//设置beacon扫描 反馈结果时间间隔  时间越久 扫描丢失率越低 默认3000ms
////        scanManager.startScan();//启动扫描
////        scanManager.stopScan();//停止扫描
////        scanManager.setScanListener(new ScanManager.MyScanListener());//设置扫描监听 监听扫描返回数据
//            scanManager.setScanListener(new ScanManager.MyScanListener() {
//                @Override
//                public void onScanListenre(ArrayList<Beacon> beacons) {
//                    LogUtil.e("biking===scanM2", "---beacons.size = " + beacons.size());
//
//                    for (Beacon beacon : beacons) {
//
//                        LogUtil.e("biking===scanM2", beacon.getName()+"=====" +beacon.getRssi()+"=====" + beacon.getMacAddress());
//
////                    macList.add(""+beacon.getMacAddress());
//                        macList.add(""+beacon.getName());
//
//                        scan = true;
//                    }
//
//                }
//            });

//            BleManager.getInstance().init(activity.getApplication());
//            BleManager.getInstance()
//                    .enableLog(true)
//                    .setReConnectCount(10, 5000)
//                    .setConnectOverTime(timeout)
//                    .setOperateTimeout(10000);
//
//            setScanRule();
//            scan_end();

////            scanManager.setScanPeriod(100);
//            scanManager.startScan();
////            manager.startEddyStoneScan();
//
//            LogUtil.e("biking===startXB",mBluetoothAdapter+"==="+mLeScanCallback);






            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                mBluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
            }else{
//                mBluetoothAdapter.startLeScan(uuids, mLeScanCallback);
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }



        }
    }



    private void stopXB() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        }else{
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }


//        BleManager.getInstance().cancelScan();
//        BleManager.getInstance().disconnectAllDevice();
//        BleManager.getInstance().destroy();

////        scanManager.setScanPeriod(0);
//        scanManager.stopScan();
//
////      manager.stopEddyStoneScan();
//
//        LogUtil.e("biking===stopXB",mBluetoothAdapter+"==="+mLeScanCallback);
//
//        if (mLeScanCallback != null && mBluetoothAdapter != null) {
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//        }
    }

    public LatLng minPoint(double lat, double lon) {
        double s=0;
        double s1=0;

        double x = 0;
        double y = 0;

        for (int i = 0; i < listPoint.size(); i++) {

//            LogUtil.e("minPoint===", listPoint.get(i).latitude+"==="+referLatitude+">>>"+listPoint.get(i).longitude+"==="+referLongitude);

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

        LogUtil.e("minPoint===2", s+"==="+dis);

        if(!isContainsList.contains(true) && dis < 30){     //30米
            isContainsList.add(true);
        }

        return null;
    }

    BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, final Intent intent) {
//            LogUtil.e("broadcastReceiver===0", "==="+intent);

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
//                    if(!isHidden && !"4".equals(type) && !"7".equals(type)){
//
//                    }

//                    LogUtil.e("broadcastReceiver===mf", "==="+intent);

                    //TODO  1   推送
//                    getCurrentorder1(SharedPreferencesUrls.getInstance().getString("uid", ""), SharedPreferencesUrls.getInstance().getString("access_token", ""));
//                    getFeedbackStatus();
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

                                        LogUtil.e("main===bike", "getMacinfo====" + bean.getMacinfo());

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

                                        LogUtil.e("main===bike", "getStatus====" + bean.getStatus());

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
                            closeLoadingDialog();
                            LogUtil.e("getCurrentorder1_e", "==="+e);
                        }
                        closeLoadingDialog();
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

                                LogUtil.e("getFeedbackStatus===1", result.data +"===" + SharedPreferencesUrls.getInstance().getBoolean("isStop", true));

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
                        closeLoadingDialog();
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

        LogUtil.e("main===changeTab", index+"==="+tab);

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
        TextView tv_carmodel_name = (TextView) customView.findViewById(R.id.tv_carmodel_name);
        TextView tv_each_free_time = (TextView) customView.findViewById(R.id.tv_each_free_time);
        TextView tv_credit_score_desc = (TextView) customView.findViewById(R.id.tv_credit_score_desc);
        TextView tv_first_price = (TextView) customView.findViewById(R.id.tv_first_price);
        TextView tv_first_time = (TextView) customView.findViewById(R.id.tv_first_time);
        TextView tv_continued_price = (TextView) customView.findViewById(R.id.tv_continued_price);
        TextView tv_continued_time = (TextView) customView.findViewById(R.id.tv_continued_time);
        TextView tv_electricity = (TextView) customView.findViewById(R.id.tv_electricity);
        TextView tv_mileage= (TextView) customView.findViewById(R.id.tv_mileage);
        LinearLayout ll_ebike = (LinearLayout) customView.findViewById(R.id.ll_ebike);
        LinearLayout ll_change_car = (LinearLayout) customView.findViewById(R.id.ll_change_car);
        LinearLayout ll_rent = (LinearLayout) customView.findViewById(R.id.ll_rent);

        tv_credit_score_desc.setText(credit_score_desc);

        if(carmodel_id==2){
            ll_ebike.setVisibility(View.VISIBLE);

            tv_electricity.setText(electricity);
            tv_mileage.setText(mileage);
        }else{
            ll_ebike.setVisibility(View.GONE);
        }

        tv_codenum.setText(codenum);
        tv_carmodel_name.setText(carmodel_name);
        tv_first_price.setText(first_price+"元");
        tv_first_time.setText("/"+first_time+"分钟");
        tv_continued_price.setText(continued_price+"元");
        tv_continued_time.setText("/"+continued_time+"分钟");

        if("0".equals(each_free_time)){
            tv_each_free_time.setVisibility(View.GONE);
        }else{
            tv_each_free_time.setVisibility(View.VISIBLE);
            tv_each_free_time.setText(each_free_time+"分钟免费");
        }

        LogUtil.e("initmPopupRent===", credit_score_desc+"===");

        if("".equals(credit_score_desc)){
            tv_credit_score_desc.setVisibility(View.GONE);
            tv_first_price.setTextColor(0xFF666666);
            tv_first_time.setTextColor(0xFF666666);
            tv_continued_price.setTextColor(0xFF666666);
            tv_continued_time.setTextColor(0xFF666666);
        }else{
            tv_credit_score_desc.setVisibility(View.VISIBLE);
            tv_first_price.setTextColor(0xFFFD555B);
            tv_first_time.setTextColor(0xFFFD555B);
            tv_continued_price.setTextColor(0xFFFD555B);
            tv_continued_time.setTextColor(0xFFFD555B);
        }

//        tv_price.setText(Html.fromHtml(price));
//        tv_price.setText(Html.fromHtml(price, null, new HtmlTagHandler("font")));


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
        //设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
        popupwindow.setAnimationStyle(R.style.PopupAnimation);
        popupwindow.setOutsideTouchable(false);

        popupwindow.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        LogUtil.e("initmPopup===", "===");

        if("5".equals(type)  || "6".equals(type)){
            if(!SharedPreferencesUrls.getInstance().getBoolean("isKnow0", false)){
                WindowManager windowManager = activity.getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                WindowManager.LayoutParams lp = advDialog0.getWindow().getAttributes();
                lp.width = (int) (display.getWidth() * 1);
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                advDialog0.getWindow().setBackgroundDrawableResource(R.color.transparent);
                advDialog0.getWindow().setAttributes(lp);
                advDialog0.show();
            }
        }
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
        //设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
        popupwindow.setAnimationStyle(R.style.PopupAnimation);
        popupwindow.setOutsideTouchable(false);

        popupwindow.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        LogUtil.e("initmPopup===", "===");
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
        popupwindow2 = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

        //设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
        popupwindow2.setAnimationStyle(R.style.PopupAnimation);
        popupwindow2.setOutsideTouchable(true);


        LinearLayout feedbackLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_feedbackLayout);
        LinearLayout helpLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_helpLayout);
        final LinearLayout callLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_callLayout);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.pop_menu_feedbackLayout:
//                        UIHelper.goToAct(context, EndBikeFeedBackActivity.class);

                        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                        if("".equals(access_token)){
                            ToastUtil.showMessageApp(context, "请先登录");

                            Intent intent = new Intent(BaseApplication.context, LoginActivity.class);
                            startActivity(intent);
                        }else{
                            cycling3();
                        }



                        break;
                    case R.id.pop_menu_helpLayout:
//                        UIHelper.goToAct(context, CarFaultActivity.class);

                        access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                        if("".equals(access_token)){
                            ToastUtil.showMessageApp(context, "请先登录");

                            Intent intent = new Intent(BaseApplication.context, LoginActivity.class);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(context, CarFaultProActivity.class);
                            intent.putExtra("type", carmodel_id);
                            intent.putExtra("bikeCode", codenum);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                        break;
                    case R.id.pop_menu_callLayout:
//                        UIHelper.goToAct(context, ServiceCenterActivity.class);
                        Intent intent = new Intent(context, ServiceCenterActivity.class);
//                        intent.putExtra("bikeCode", codenum);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        break;

                    case R.id.popupWindow_back:
                        popupwindow2.dismiss();

                        break;

                }
                popupwindow2.dismiss();
            }
        };

        iv_popup_window_back.setOnClickListener(listener);
        feedbackLayout.setOnClickListener(listener);
        helpLayout.setOnClickListener(listener);
        callLayout.setOnClickListener(listener);

        popupwindow2.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void car() {
        LogUtil.e("mf===car0", loadingDialog.isShowing() + "===" + codenum);

        HttpHelper.get(context, Urls.car + URLEncoder.encode(codenum)+"/location", new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                closeLoadingDialog();
//                onStartCommon("正在加载");
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

                            LogUtil.e("mf===car1", responseString + "===" + result.data);

                            final LocationBean bean = JSON.parseObject(result.getData(), LocationBean.class);


                            LogUtil.e("mf===car2", isContainsList.contains(true) + "===" + pOptions + "===" + bean.getLatitude() + "===" + bean.getLongitude() + "===" + isContainsList);


                            if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
                                isContainsList.clear();
                            }

//                            LogUtil.e("mf===car2_1", isContainsList + "===" + isContainsList.contains(true) + "===" + pOptions + "===" + bean.getLatitude() + "===" + bean.getLongitude());

//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                }
//                            }).start();

                            for ( int i = 0; i < pOptions.size(); i++){
                                isContainsList.add(pOptions.get(i).contains(new LatLng(Double.parseDouble(bean.getLatitude()), Double.parseDouble(bean.getLongitude()))));
                            }

                            LogUtil.e("mf===car3", isContainsList.contains(true)  + "===" + bean.getLatitude()+"==="+bean.getLongitude() + "===" + isContainsList);

                            if(!isContainsList.contains(true)){
                                minPoint(Double.parseDouble(bean.getLatitude()), Double.parseDouble(bean.getLongitude()));
                            }

                            LogUtil.e("mf===car4", loadingDialog.isShowing() + "===" + isContainsList.contains(true)  + "===" + type + "===" + bean.getLatitude()+"==="+bean.getLongitude());

                            if(isContainsList.contains(true)){
                                isGPS_Lo = true;

//                                        m_myHandler.post(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                            }
//                                        });

                                if("4".equals(type) || "8".equals(type)){
                                    endBtn4();
                                }else if("7".equals(type)){
                                    endBtn7();
                                }else if("11".equals(type)){
                                    endBtn11();
                                }

                            }else{
                                isGPS_Lo = false;

                                startXB();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            int n=0;
                                            while(macList.size() == 0){

                                                Thread.sleep(100);
                                                n++;

                                                LogUtil.e("biking===","biking=n=="+n);

//                                                        if(n%20==0){
//                                                            scanManager.stopScan();
//                                                            scanManager.startScan();
//                                                        }


                                                if(n>=101) break;

                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        m_myHandler.sendEmptyMessage(3);
                                    }
                                }).start();
                            }

                        } catch (Exception e) {
                            closeLoadingDialog();
                            LogUtil.e("mf===car_e", loadingDialog.isShowing() + "===" + e);

//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });


            }
        });

    }

    //type10
    private void temp_lock(final int status) {
        LogUtil.e("mf===temp_lock", status+"===");


        RequestParams params = new RequestParams();
        params.put("status", status); //0上报未临时上锁(再次开锁成功后上报) 1上报临时上锁中

        HttpHelper.post(context, Urls.temp_lock, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if(isAgain){

                }
//                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                LogUtil.e("mf===temp_lock_fail", responseString + "===" + throwable.toString());
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("mf===temp_lock_1", status+"==="+type+ "===" + responseString);

                            if(status==1){
                                car_authority_auto();
                            }else{
                                car_authority();
                            }


//                            if(isAgain){
//                                tv_againBtn.setText("再次开锁");
//                                SharedPreferencesUrls.getInstance().putString("tempStat","1");
//
//                                ToastUtil.showMessageApp(context,"恭喜您,关锁成功!");
//
//                                closeLoadingDialog();
//                            }else{
//                                n=0;
//                                carLoopClose();
//                            }

                        } catch (Exception e) {
                            closeLoadingDialog();
                            LogUtil.e("mf===temp_lock_e", "==="+e);
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });
            }
        });
    }


    //助力车关锁
    private void lock() {
        LogUtil.e("mf===lock", isAgain+"==="+isFinish);

//        if(isFinish){
//            return;
//        }

        RequestParams params = new RequestParams();
        params.put("action_type", isAgain?1:2); //操作类型 1临时上锁 2还车
        if(!isAgain){
            params.put("parking", parking());    //电子围栏json字符串 操作类型为还车时必传
        }
        params.put("longitude", referLongitude);   //0代表成功 1连接不上蓝牙 2蓝牙开锁超时 3网络开锁请求失败(接口无响应或异常) 4网络开锁超时（接口有响应但返回超时码） 5网络开锁失败
        params.put("latitude", referLatitude);

        //还车类型 操作类型为还车时必传 1手机gps在电子围栏 2锁gps在电子围栏 3信标 4锁与信标
        if(!isAgain){
            if(major!=0){
                LogUtil.e("mf===lock1", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                params.put("back_type", "4");     // 4锁与信标
            }else if(isGPS_Lo){
                LogUtil.e("mf===lock2", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                params.put("back_type", "2");     // 2锁gps在电子围栏
            }else if(macList.size() > 0){
                LogUtil.e("mf===lock3", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                params.put("back_type", "3");     // 3信标
            }else if(force_backcar==1 && isTwo){
                LogUtil.e("mf===lock4", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                params.put("back_type", "5");     // 没锁第二次强制还车
            }else{
//                  }else if(isContainsList.contains(true)){
                LogUtil.e("mf===lock5", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                params.put("back_type", "1");     // 1手机gps在电子围栏
            }
        }

//        if(isFinish){
//            return;
//        }

        HttpHelper.post(context, Urls.lock, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if(isAgain){
                }
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                LogUtil.e("mf===lock_fail", responseString + "===" + throwable.toString());
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("mf===lock_1", loadingDialog.isShowing() + "===" + carmodel_id + "===" + responseString + "===" + result.data);

                            if(isAgain){
                                n=0;
                                carInfo_close_loop();
                            }else{
                                n=0;
                                carLoopClose();
                            }

                        } catch (Exception e) {
                            closeLoadingDialog();
                            LogUtil.e("mf===lock_e", "===" + e);
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });
            }
        });
    }

    //助力车开锁
    private void unlock() {
        LogUtil.e("mf===unlock", isAgain+"==="+isNetSuc);

        isNetSuc = false;

//        isOpenLock = false;

        RequestParams params = new RequestParams();
        params.put("action_type", isAgain?1:2);

        HttpHelper.post(context, Urls.unlock, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                onStartCommon("正在加载");
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

                            LogUtil.e("mf===unlock1", loadingDialog.isShowing()+ "===" + isAgain+ "===" + carmodel_id+ "===" + type + "===" + codenum + "===" + responseString + "===" + result.data);

                            if(isAgain){
                                n=0;
                                carInfo_open_loop();
                            }else{
                                n=0;
                                startTime = System.nanoTime();
                                carLoopOpen();
                            }

                        } catch (Exception e) {
                            closeLoadingDialog();
                            LogUtil.e("mf===unlock_e", "===" + e);

//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });
            }
        });
    }

    //助力车开锁_轮询
    private void carLoopOpen() {
        long consumingTime = System.nanoTime() - startTime;

        LogUtil.e("mf===carLoopOpen", order_id2 + "===" + order_id + "===" + codenum + "===" + consumingTime + "===" + (consumingTime>10000000000l));

        if(consumingTime>10000000000l){
            ToastUtil.showMessageApp(context, "开锁超时");
            closeLoadingDialog();

            return;
        }



//        if("11".equals(type)){
//            LogUtil.e("mf===carLoopOpen_1", order_id2 + "===" + order_id + "===" + codenum + "===" + TbitBle.getBleConnectionState());
//            if(TbitBle.getBleConnectionState()!=0) return;
//        }



        HttpHelper.get(context, Urls.order_detail+order_id2, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                LogUtil.e("mf===carLoopOpen_fail", responseString + "===" + throwable.toString());
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("mf===carLoopOpen1", loadingDialog.isShowing() + "===" + responseString + "===" + result.data);

                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);

                            if(20 != bean.getOrder_state()){
                                queryCarStatusOpen();
                            }else{
//                                isConnect = true;

                                isNetSuc = true;

                                closeLoadingDialog();

                                ll_top_navi.setVisibility(View.GONE);
                                ll_top.setVisibility(View.VISIBLE);
                                rl_ad.setVisibility(View.GONE);
                                ll_top_biking.setVisibility(View.VISIBLE);

                                popupwindow.dismiss();
                                ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

                                cyclingThread();
                            }

                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());

                            closeLoadingDialog();
                            LogUtil.e("mf===carLoopOpen_e", "===" + e);
                        }

                    }
                });
            }
        });
    }

    //助力车开锁_轮询
    private void queryCarStatusOpen() {
        if(n<7){
            n++;

            m_myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtil.e("mf===queryCarStatusOpen", "==="+n);

                    carLoopOpen();
                }
            }, 1 * 1000);
        }else{
            isNetSuc = false;

            LogUtil.e("mf===queryCarStatusOpen2", "===");

            if("11".equals(type)){
//                if(TbitBle.getBleConnectionState()==0){
//                    ToastUtil.showMessageApp(context, "开锁超时");
//
//                    closeLoadingDialog();
//                }

                if(TbitBle.getBleConnectionState()==0){
                    tbtble_connect();
                }else{
                    tbtble_unlock();
                }

            }else{
                ToastUtil.showMessageApp(context, "开锁超时");

                closeLoadingDialog();
            }
        }
    }

    //助力车关锁_轮询w
    private void carLoopClose() {
        LogUtil.e("mf===carLoopClose", isFinish+"===" +order_id2+"===" +order_id+"===" + isAgain+"===" + codenum);

//        if(isFinish){
//            return;
//        }

//        if("11".equals(type)){
//            LogUtil.e("mf===carLoopClose_1", isFinish+"===" +order_id2+"===" +order_id+"===" + isAgain+"===" + codenum + "===" + TbitBle.getBleConnectionState());
//            if(TbitBle.getBleConnectionState()!=0) return;
//        }

        HttpHelper.get(context, Urls.order_detail+order_id2, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if(isAgain){

                }
//                onStartCommon("正在加载");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                LogUtil.e("mf===carLoopClose_fail", responseString + "===" + throwable.toString());
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("mf===carLoopClose1",  loadingDialog.isShowing()+ "===" +isFinish+ "===" +responseString + "===" + result.data);

                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);

                            if(isAgain){
                                closeLoadingDialog();
                            }else{
                                if(bean.getOrder_state() < 30){
                                    queryCarStatusClose();
                                }else{
//                                  isConnect = true;

//                                    if(!isFinish){
//                                        isFinish = true;
//
//                                    }

                                    order_type = 1;
                                    end();
                                }
                            }

                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());

                            closeLoadingDialog();
                            LogUtil.e("mf===carLoopClose_e", "===" + e);
                        }

                    }
                });
            }
        });
    }

    //助力车关锁_轮询2
    private void queryCarStatusClose() {
        if(n<7){
            n++;

            m_myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtil.e("mf=queryCarStatusClose", "==="+n);

                    carLoopClose();

                }
            }, 1 * 1000);
        }else{
            LogUtil.e("mf=queryCarStatusClose2", "==="+type);

            if("11".equals(type)){
//                if(TbitBle.getBleConnectionState()==0){
//                    ToastUtil.showMessageApp(context, "关锁超时");
//
//                    closeLoadingDialog();
//                }

                if(TbitBle.getBleConnectionState()==0){
                    tbtble_connect();
                }else{
                    tbtble_lock();
                }
            }else{
                ToastUtil.showMessageApp(context, "关锁超时");

                closeLoadingDialog();
            }

        }
    }

    private void endBle() {

        try {
            LogUtil.e("mf==endBle", loadingDialog.isShowing()+"==="+type+"==="+order_id+"==="+order_type);


            if("5".equals(type)  || "6".equals(type)){
                ClientManager.getClient().stopSearch();

                ClientManager.getClient().disconnect(m_nowMac);
//                ClientManager.getClient().disconnect(m_nowMac);
//                ClientManager.getClient().disconnect(m_nowMac);
//                ClientManager.getClient().disconnect(m_nowMac);
//                ClientManager.getClient().disconnect(m_nowMac);
//                ClientManager.getClient().disconnect(m_nowMac);

                ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);
//                ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener2);

            }else if("4".equals(type) || "8".equals(type)){
            }else if("7".equals(type)){
//                if (apiClient != null) {
//                    apiClient.disConnect();
//                    apiClient.onDestroy();
//                    apiClient = null;
//                }
            }else if("11".equals(type)){
//                if(TbitBle.hasInitialized()){
//                    TbitBle.disConnect();
//                }

            }else{
                BleManager.getInstance().disconnectAllDevice();
                BleManager.getInstance().destroy();
            }

        } catch (Exception e) {
            closeLoadingDialog();
            LogUtil.e("mf===end2_e", "===" + e);
            UIHelper.showToastMsg(context, e.getMessage(), R.drawable.ic_error);
        }


    }

    private void end2() {

        try {
            LogUtil.e("mf==end2", unauthorized_code+"==="+bikeFragment.isHidden()+"==="+type+"==="+order_id+"==="+order_type);

            if(!bikeFragment.isHidden()){
                bikeFragment.initNearby(referLatitude, referLongitude);
            }else{
                ebikeFragment.initNearby(referLatitude, referLongitude);
            }


            if("5".equals(type)  || "6".equals(type)){
                ClientManager.getClient().stopSearch();

                ClientManager.getClient().disconnect(m_nowMac);
                ClientManager.getClient().disconnect(m_nowMac);
                ClientManager.getClient().disconnect(m_nowMac);
                ClientManager.getClient().disconnect(m_nowMac);
                ClientManager.getClient().disconnect(m_nowMac);
                ClientManager.getClient().disconnect(m_nowMac);

                ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);
//                ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener2);

            }else if("4".equals(type) || "8".equals(type)){
            }else if("7".equals(type)){
                if (apiClient != null) {
                    apiClient.disConnect();
                    apiClient.onDestroy();
                    apiClient = null;
                }
            }else if("11".equals(type)){
                if(TbitBle.hasInitialized()){
                    TbitBle.disConnect();
                }

            }else{
                BleManager.getInstance().disconnectAllDevice();
                BleManager.getInstance().destroy();
            }

        } catch (Exception e) {
            closeLoadingDialog();
            LogUtil.e("mf===end2_e", "===" + e);
            UIHelper.showToastMsg(context, e.getMessage(), R.drawable.ic_error);
        }


    }

    private void end() {

        try {
            closeDialog();

            LogUtil.e("mf==end", unauthorized_code+"==="+bikeFragment.isHidden()+"==="+type+"==="+order_id+"==="+order_type);

            if(!bikeFragment.isHidden()){
                bikeFragment.initNearby(referLatitude, referLongitude);
            }else{
                ebikeFragment.initNearby(referLatitude, referLongitude);
            }

            m_myHandler.removeCallbacksAndMessages(null);

            ll_top.setVisibility(View.VISIBLE);
            rl_ad.setVisibility(View.VISIBLE);
            ll_top_biking.setVisibility(View.GONE);
            ll_top_pay.setVisibility(View.GONE);

            if("5".equals(type)  || "6".equals(type)){
                ClientManager.getClient().stopSearch();

                ClientManager.getClient().disconnect(m_nowMac);
                ClientManager.getClient().disconnect(m_nowMac);
                ClientManager.getClient().disconnect(m_nowMac);
                ClientManager.getClient().disconnect(m_nowMac);
                ClientManager.getClient().disconnect(m_nowMac);
                ClientManager.getClient().disconnect(m_nowMac);

                ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);
//                ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener2);

            }else if("4".equals(type) || "8".equals(type)){
            }else if("7".equals(type)){
                if (apiClient != null) {
                    apiClient.onDestroy();
                    apiClient = null;
                }
            }else if("11".equals(type)){
                if(TbitBle.hasInitialized()){
                    TbitBle.disConnect();
                }
            }else{
//            BaseApplication.getInstance().getIBLE().stopScan();
//            BaseApplication.getInstance().getIBLE().refreshCache();
//            BaseApplication.getInstance().getIBLE().close();
//            BaseApplication.getInstance().getIBLE().disconnect();

//            if(order_type==0){
//                if (broadcastReceiver != null) {
//                    activity.unregisterReceiver(broadcastReceiver);
////                broadcastReceiver = null;
//                }
//            }

//            byte[] bb=new byte[3];
//
//            BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb",
//                    bb, true, new BleWriteCallback() {
//                        @Override
//                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
//                            LogUtil.e("onWriteSuccess===", current+"==="+total+"==="+justWrite);
//                        }
//
//                        @Override
//                        public void onWriteFailure(BleException exception) {
//                            LogUtil.e("onWriteFailure===", "==="+exception);
//                        }
//                    });

                BleManager.getInstance().disconnectAllDevice();
                BleManager.getInstance().destroy();
            }

            isWaitEbikeInfo = false;
            if (ebikeInfoThread != null) {
                ebikeInfoThread.interrupt();
                ebikeInfoThread = null;
            }

            if(order_type==1 && isEndBtn){
                codenum = "";
                isMin = false;

                tv_biking_codenum.setText("");
                tv_estimated_cost.setText("");
                tv_estimated_cost2.setText("");
                tv_car_start_time.setText("");
                tv_car_start_time2.setText("");
                tv_car_mileage.setText("");
                tv_car_electricity.setText("");

                tv_pay_codenum.setText("");
                tv_pay_car_start_time.setText("");
                tv_pay_car_end_time.setText("");
                tv_order_amount.setText("");

                ToastUtil.showMessageApp(context,"恭喜您,还车成功,请支付!");
            }

            if(order_type==3){
//            unauthorized_code==8 || unauthorized_code==9

//            UIHelper.goToAct(context, UnpayOtherActivity.class);
                Intent intent = new Intent(context, UnpayOtherActivity.class);
                intent.putExtra("type", unauthorized_code-7);
                startActivity(intent);
            }else{
                Intent intent = new Intent(context, SettlementPlatformActivity.class);
                intent.putExtra("order_type", order_type);
                intent.putExtra("order_id", order_id);
                startActivityForResult(intent, 11);
            }

        } catch (Exception e) {
            closeLoadingDialog();
            LogUtil.e("mf===end_e", "===" + e);
            UIHelper.showToastMsg(context, e.getMessage(), R.drawable.ic_error);
        }




    }



    //泺平_开锁
    protected void rent(){

        try{
            LogUtil.e("rent===000", loadingDialog.isShowing()+"==="+isAgain+"==="+lock_no+"==="+m_nowMac+"==="+keySource);

            RequestParams params = new RequestParams();
//            params.put("lock_no", "123");
            params.put("lock_no", lock_no);
            params.put("keySource",keySource);
            HttpHelper.get(context, Urls.rent, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
//                onStartCommon("正在提交");
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    onFailureCommon(throwable.toString());
                    getBleRecord();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LogUtil.e("rent===1","==="+responseString);

                                ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

//                                int i = 1/0;

                                if(result.getMessage()==null || "".equals(result.getMessage())){
                                    remark = "";

                                    KeyBean bean = JSON.parseObject(result.getData(), KeyBean.class);

                                    encryptionKey = bean.getEncryptionKey();
                                    keys = bean.getKeys();
                                    serverTime = bean.getServerTime();

                                    LogUtil.e("rent===2", m_nowMac+"==="+encryptionKey+"==="+keys);

//                                    deleteBleRecord("");
//                                  getBleRecord();
//                                  iv_help.setVisibility(View.GONE);

                                    if(isAgain){
                                        openAgainBleLock(null);
                                    }else{
                                        openBleLock(null);
                                    }
                                }else{
                                    remark = type+"===rent_suc==="+result.getMessage();
                                    LogUtil.e("mf===rent_suc", "==="+remark);

                                    ToastUtil.showMessageApp(context, "开锁失败");

                                    getBleRecord();
                                    car_notification(isAgain?4:1, 3, 0, remark);
                                }

                            }catch (Exception e){
//                                closeLoadingDialog();
                                LogUtil.e("mf===rent_suc_e", "==="+e);

                                ToastUtil.showMessageApp(context, "开锁失败");

                                getBleRecord();
                                car_notification(isAgain?4:1, 3, 0, type+"===rent_suc_e==="+e);
                            }
                        }
                    });
                }
            });
        }catch (Exception e){
//            closeLoadingDialog();

            String tvAgain = tv_againBtn.getText().toString().trim();
            int action_type;
            LogUtil.e("rent===e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

            if(isAgain){
                if("再次开锁".equals(tvAgain)){
                    action_type=4;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }else{
                    action_type=2;
                    ToastUtil.showMessageApp(context,"关锁失败");
                }
            }else{
                if(isEndBtn){
                    action_type=3;
                    ToastUtil.showMessageApp(context,"还车失败");
                }else{
                    action_type=1;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }
            }
            getBleRecord();
            car_notification(action_type, 3, 0, type+"===rent_e==="+e);
        }


    }

    private String parking(){
//        LogUtil.e("mf===parking", jsonArray+"==="+jsonArray2);


        if(jsonArray != null){

            try{

//                LogUtil.e("mf===parking1", jsonArray+"==="+jsonArray.length());

                for ( int i = 0; i < pOptions.size(); i++){

//                    LogUtil.e("mf===parking12", i+"==="+pOptions.get(i)+"==="+pOptions.get(i).contains(new LatLng(referLatitude, referLongitude)));

                    if(pOptions.get(i).contains(new LatLng(referLatitude, referLongitude))){
//                        LogUtil.e("mf===parking13", "==="+jsonArray.getJSONObject(i));

//                        LogUtil.e("mf===parking2", "==="+jsonArray.getJSONObject(i));

                        return ""+jsonArray.getJSONObject(i);
                    }
                }

                return "";

            }catch (Exception e){
                closeLoadingDialog();
                LogUtil.e("mf===parking_e", "==="+e);
                return "";
            }
        }

        if(jsonArray2 != null){

//            LogUtil.e("mf===parking2", "===");

            try{

//                LogUtil.e("mf===parking21", jsonArray2+"==="+jsonArray2.length());

                for ( int i = 0; i < pOptions.size(); i++){

//                    LogUtil.e("mf===parking22", i+"==="+pOptions.get(i)+"==="+pOptions.get(i).contains(new LatLng(referLatitude, referLongitude)));

                    if(pOptions.get(i).contains(new LatLng(referLatitude, referLongitude))){
//                        LogUtil.e("mf===parking23", "==="+jsonArray2.getJSONObject(i));

                        return ""+jsonArray2.getJSONObject(i);
                    }
                }

                return "";

            }catch (Exception e){
                closeLoadingDialog();
                LogUtil.e("mf===parking_e", "==="+e);
                return "";
            }
        }

        return "";
    }

    private void car_notification(final int action_type, final int lock_status, final int back_type, final String remark) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    m_myHandler2.removeCallbacksAndMessages(null);

                    LogUtil.e("mf===car_notification_0", loadingDialog.isShowing() + "===" + type + "===" + isAgain + "===" + action_type + "===" + lock_status + "===" + back_type + "===" + remark + "===" + oid + "===" + referLatitude + "===" + referLongitude + "===" + Md5Helper.encode(oid + ":action_type:" + action_type) + "===" + Md5Helper.encode(oid + ":lock_status:" + lock_status));

                    if (lock_status == 2 || lock_status == 3) {
                        endBle();
                    }


                    LogUtil.e("mf===car_notification_1", loadingDialog.isShowing() + "===" + isOpenLock + "===" + isAgain + "===" + isEndBtn);

                    if (!isOpenLock && !isAgain && !isEndBtn) return;

                    if (action_type == 3) {
                        isEndBtn = false;
                    }

                    LogUtil.e("mf===car_notification_2", type + "===" + isAgain + "===" + action_type + "===" + lock_status + "===" + back_type + "===" + remark + "===" + oid + "===" + referLatitude + "===" + referLongitude + "===" + Md5Helper.encode(oid + ":action_type:" + action_type) + "===" + Md5Helper.encode(oid + ":lock_status:" + lock_status));

                    final RequestParams params = new RequestParams();
                    params.put("action_type", Md5Helper.encode(oid + ":action_type:" + action_type));   //操作类型 1开锁 2临时上锁 3还车 4临时开锁(为type11时使用)
                    params.put("lock_status", Md5Helper.encode(oid + ":lock_status:" + lock_status));     //车锁状态 必传 1成功 2连接不上蓝牙 3蓝牙操作失败 4还车时不在停车点(蓝牙、助力车都得上报) 5车锁未关
                    params.put("parking", parking());
                    params.put("longitude", referLongitude);
                    params.put("latitude", referLatitude);
                    params.put("remark", remark);
                    if (back_type != 0) {

                        if (major != 0) {
                            LogUtil.e("mf===car_notification1", major + "===" + macList + "===" + macList2 + "===" + isContainsList.contains(true) + "===" + uid + "===" + access_token);
                            params.put("back_type", Md5Helper.encode(oid + ":back_type:" + 4));     // 4锁与信标
                        } else if (isGPS_Lo) {
                            LogUtil.e("mf===car_notification2", major + "===" + macList + "===" + macList2 + "===" + isContainsList.contains(true) + "===" + uid + "===" + access_token);
                            params.put("back_type", Md5Helper.encode(oid + ":back_type:" + 2));     // 2锁gps在电子围栏
                        } else if (macList.size() > 0) {
                            LogUtil.e("mf===car_notification3", major + "===" + macList + "===" + macList2 + "===" + isContainsList.contains(true) + "===" + uid + "===" + access_token);
                            params.put("back_type", Md5Helper.encode(oid + ":back_type:" + 3));     // 3信标
                        } else if (force_backcar == 1 && isTwo) {
                            LogUtil.e("mf===car_notification4", major + "===" + macList + "===" + macList2 + "===" + isContainsList.contains(true) + "===" + uid + "===" + access_token);
                            params.put("back_type", Md5Helper.encode(oid + ":back_type:" + 5));     // 没锁第二次强制还车
                        } else {
//                              }else if(isContainsList.contains(true)){
                            LogUtil.e("mf===car_notification5", major + "===" + macList + "===" + macList2 + "===" + isContainsList.contains(true) + "===" + uid + "===" + access_token);
                            params.put("back_type", Md5Helper.encode(oid + ":back_type:" + 1));     // 1手机gps在电子围栏
                        }

//                          params.put("back_type", Md5Helper.encode(oid+":back_type:"+backType));
                    }

                    LogUtil.e("mf===car_notification_2_1", loadingDialog.isShowing() + "===" +type + "===" + isAgain + "===" + action_type + "===" + lock_status + "===" + back_type + "===" + remark + "===" + oid + "===" + referLatitude + "===" + referLongitude + "===" + Md5Helper.encode(oid + ":action_type:" + action_type) + "===" + Md5Helper.encode(oid + ":lock_status:" + lock_status));


                    Looper.prepare();
                    HttpHelper.post(context, Urls.car_notification, params, new TextHttpResponseHandler() {
                        @Override
                        public void onStart() {
//                        onStartCommon("正在加载");
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            LogUtil.e("mf=car_notification_f", responseString + "====" + throwable.toString());
                            onFailureCommon(throwable.toString());
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                            m_myHandler.post(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                                        LogUtil.e("mf===car_notification6", responseString + "====" + action_type + "====" + lock_status);

                                        if (action_type == 1 || action_type == 4) {
                                            if (lock_status == 1) {
                                                ToastUtil.showMessageApp(context, "恭喜您,开锁成功!");
                                            }

                                            isOpenLock = false;

                                            if (!isAgain) {
                                                if (lock_status == 1) {
                                                    popupwindow.dismiss();

                                                    ll_top_navi.setVisibility(View.GONE);
                                                    ll_top.setVisibility(View.VISIBLE);
                                                    rl_ad.setVisibility(View.GONE);
                                                    ll_top_biking.setVisibility(View.VISIBLE);

                                                    if (!bikeFragment.isHidden()) {
                                                        bikeFragment.initNearby(referLatitude, referLongitude);
                                                    } else {
                                                        ebikeFragment.initNearby(referLatitude, referLongitude);
                                                    }

                                                    cyclingThread();

                                                    if ("5".equals(type) || "6".equals(type)) {
                                                        if (!SharedPreferencesUrls.getInstance().getBoolean("isKnow", false)) {
                                                            WindowManager windowManager = activity.getWindowManager();
                                                            Display display = windowManager.getDefaultDisplay();
                                                            WindowManager.LayoutParams lp = advDialog.getWindow().getAttributes();
                                                            lp.width = (int) (display.getWidth() * 1);
                                                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                                            advDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                                                            advDialog.getWindow().setAttributes(lp);
                                                            advDialog.show();
                                                        }
                                                    }
                                                }
                                            } else {
                                                if (lock_status == 1) {
                                                    if ("10".equals(type)) {
                                                        temp_lock(0);
                                                    }
                                                }
                                            }

                                        } else if (action_type == 3) {

                                            if (!isAgain && lock_status == 1) {

                                                car_authority();

                                                order_type = 1;
                                                end();

                                            }
                                        }

                                        if (action_type == 3 && lock_status == 5 && open > 1) {
                                            m_myHandler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    LogUtil.e("closeLoadingDialog===", "===");

                                                    ToastUtil.showMessageApp(context, "车锁未关，请手动关锁");

                                                    closeLoadingDialog();

                                                }
                                            }, 0 * 1000);
                                        } else {
                                            closeLoadingDialog();
                                        }

                                    } catch (Exception e) {
                                        closeLoadingDialog();
                                        LogUtil.e("mf===car_notification_suc_e", "===" + e);
//                                  memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());

                                    }


                                }
                            });


                        }
                    });
                    Looper.loop();
                } catch (Exception e) {
                    closeLoadingDialog();
                    LogUtil.e("mf===car_notification_e", "==="+e);
//                                  memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());

                }

            }
        }).start();



    }

    //泺平
    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            LogUtil.e("scan===","DeviceListActivity.onSearchStarted");
//            mDevices.clear();
//            mAdapter.notifyDataChanged();
        }

        @Override
        public void onDeviceFounded(final SearchResult device) {

            LogUtil.e("scan===onDeviceFounded",device.device.getName() + "===" + device.device.getAddress());

//            bike:GpDTxe8DGN412
//            bike:LUPFKsrUyR405
//            bike:LUPFKsrUyK405
//            bike:L6OsRAiviK289===E8:EB:11:02:2B:E2

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(m_nowMac.equals(device.device.getAddress())){

                        LogUtil.e("scan===stop",device.device.getName() + "===" + device.device.getAddress());

                        ClientManager.getClient().stopSearch();

//                        connectDeviceLP();
//
//                        ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
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
            LogUtil.e("scan===","DeviceListActivity.onSearchStopped");

        }

        @Override
        public void onSearchCanceled() {
            LogUtil.e("scan===","DeviceListActivity.onSearchCanceled");

        }
    };

    ////泺平===监听当前连接状态
    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(final String mac, final int status) {

//            LogUtil.e("ConnectStatus===", mac+"===="+(status == STATUS_CONNECTED));

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    LogUtil.e("ConnectStatus===biking", isLookPsdBtn+"==="+mac+"==="+(status == STATUS_CONNECTED)+"==="+m_nowMac);

                    if(status == STATUS_CONNECTED){
                        isLookPsdBtn = true;

//                        ToastUtil.showMessageApp(context,"设备连接成功");
                    }else{
                        isLookPsdBtn = false;

//                        ToastUtil.showMessageApp(context,"蓝牙连接已断开");
//                        ToastUtil.showMessageApp(context,"设备断开连接");
                    }

//                    connectDeviceIfNeeded();
                }
            });

            if(status != STATUS_CONNECTED){
                return;
            }

            ClientManager.getClient().stopSearch();


//            Globals.isBleConnected = mConnected = (status == STATUS_CONNECTED);
//            refreshData(mConnected);
//            connectDeviceIfNeeded();
        }
    };



    //泺平--》监听当前连接状态
    private final BleConnectStatusListener mConnectStatusListener2 = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(final String mac, final int status) {
//            BluetoothLog.v(String.format(Locale.getDefault(), "DeviceDetailActivity onConnectStatusChanged %d in %s", status, Thread.currentThread().getName()));

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    LogUtil.e("ConnectStatus===biking2", mac+"==="+(status == STATUS_CONNECTED)+"==="+m_nowMac);
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

    //泺平，临时开锁
    private void openAgainBleLock(RRent.ResultBean resultBean) {

        try{
            LogUtil.e("openAgainBleLock===", serverTime+"==="+keys+"==="+encryptionKey);

//            int i = 1/0;

            ClientManager.getClient().openLock(m_nowMac,"000000000000", (int) serverTime, keys, encryptionKey, new IEmptyResponse(){
                @Override
                public void onResponseFail(final int code) {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
//                            closeLoadingDialog();

                            getBleRecord2();
//                            ToastUtil.showMessageApp(context, "开锁失败");
                            LogUtil.e("openAgainBleLock===fail", code+"==="+Code.toString(code));

                            if("锁已开".equals(Code.toString(code))){
//                                ToastUtil.showMessageApp(context, Code.toString(code));
                                car_notification(4, 1, 0, type+"===openAgainBleLock==="+code+"==="+Code.toString(code));
                            }else{
                                ToastUtil.showMessageApp(context, "开锁失败");
                                car_notification(4, 3, 0, type+"===openAgainBleLock==="+code+"==="+Code.toString(code));
                            }

//                            car_notification(4, 3, 0, type+"===openAgainBleLock==="+code+"==="+Code.toString(code));
//                        ToastUtil.showMessageApp(context, Code.toString(code));
                        }
                    });
                }

                @Override
                public void onResponseSuccess() {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                getBleRecord2();

                                LogUtil.e("openAgainBleLock===Suc", "===");

                                ToastUtil.showMessageApp(context, "开锁成功");

//                              int i = 1/0;

                                car_notification(4, 1, 0, "");
                            }catch (Exception e){
//                                closeLoadingDialog();

                                String tvAgain = tv_againBtn.getText().toString().trim();
                                int action_type;
                                LogUtil.e("openAgainBleLock===onRS_e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                                if(isAgain){
                                    if("再次开锁".equals(tvAgain)){
                                        action_type=4;
                                        ToastUtil.showMessageApp(context,"开锁失败");
                                    }else{
                                        action_type=2;
                                        ToastUtil.showMessageApp(context,"关锁失败");
                                    }
                                }else{
                                    if(isEndBtn){
                                        action_type=3;
                                        ToastUtil.showMessageApp(context,"还车失败");
                                    }else{
                                        action_type=1;
                                        ToastUtil.showMessageApp(context,"开锁失败");
                                    }
                                }
                                getBleRecord2();
                                car_notification(action_type, 3, 0, type+"===openAgainBleLock_onRS_e==="+e);
                            }

                        }
                    });

                }
            });
        }catch (Exception e){
//            closeLoadingDialog();

            String tvAgain = tv_againBtn.getText().toString().trim();
            int action_type;
            LogUtil.e("openAgainBleLock===e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

            if(isAgain){
                if("再次开锁".equals(tvAgain)){
                    action_type=4;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }else{
                    action_type=2;
                    ToastUtil.showMessageApp(context,"关锁失败");
                }
            }else{
                if(isEndBtn){
                    action_type=3;
                    ToastUtil.showMessageApp(context,"还车失败");
                }else{
                    action_type=1;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }
            }
            getBleRecord2();
            car_notification(action_type, 3, 0, type+"===openAgainBleLock_e==="+e);
        }
    }

    //泺平，开锁
    private void openBleLock(RRent.ResultBean resultBean) {
        try{
            LogUtil.e("openBleLock===", loadingDialog.isShowing()+"==="+serverTime+"==="+keys+"==="+encryptionKey);

            ClientManager.getClient().openLock(m_nowMac,"000000000000", (int) serverTime, keys, encryptionKey, new IEmptyResponse(){
                @Override
                public void onResponseFail(final int code) {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.e("openLock===Fail", m_nowMac+"==="+Code.toString(code));
//                          deleteBleRecord(null);

                            getBleRecord();

                            if("锁已开".equals(Code.toString(code))){
//                                ToastUtil.showMessageApp(context, Code.toString(code));
                                car_notification(1, 1, 0, type+"===openBleLock==="+code+"==="+Code.toString(code));
                            }else{
                                ToastUtil.showMessageApp(context, "开锁失败");
                                car_notification(1, 3, 0, type+"===openBleLock==="+code+"==="+Code.toString(code));
                            }


//                            car_notification(1, 3, 0, type+"===openBleLock==="+code+"==="+Code.toString(code));

                        }
                    });

                }

                @Override
                public void onResponseSuccess() {

                    try{
                        LogUtil.e("openBleLock===Success", "===");
                        lockStatus = 1;



//                        closeLoadingDialog();

//                        int i = 1/0;

                        ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

                        getBleRecord();
                        car_notification(1, 1, 0, "");
                    }catch (Exception e){
//                        closeLoadingDialog();

                        String tvAgain = tv_againBtn.getText().toString().trim();
                        int action_type;
                        LogUtil.e("openBleLock===onRS_e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                        if(isAgain){
                            if("再次开锁".equals(tvAgain)){
                                action_type=4;
                                ToastUtil.showMessageApp(context,"开锁失败");
                            }else{
                                action_type=2;
                                ToastUtil.showMessageApp(context,"关锁失败");
                            }
                        }else{
                            if(isEndBtn){
                                action_type=3;
                                ToastUtil.showMessageApp(context,"还车失败");
                            }else{
                                action_type=1;
                                ToastUtil.showMessageApp(context,"开锁失败");
                            }
                        }
                        getBleRecord();
                        car_notification(action_type, 3, 0, type+"===openBleLock_onRS_e==="+e);
                    }

                }
            });
        }catch (Exception e){
//            closeLoadingDialog();

            String tvAgain = tv_againBtn.getText().toString().trim();
            int action_type;
            LogUtil.e("openBleLock===e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

            if(isAgain){
                if("再次开锁".equals(tvAgain)){
                    action_type=4;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }else{
                    action_type=2;
                    ToastUtil.showMessageApp(context,"关锁失败");
                }
            }else{
                if(isEndBtn){
                    action_type=3;
                    ToastUtil.showMessageApp(context,"还车失败");
                }else{
                    action_type=1;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }
            }
            getBleRecord();
            car_notification(action_type, 3, 0, type+"===openBleLock_e==="+e);
        }
    }



    //泺平===与设备，获取记录
    private void getBleRecord() {
        try{
            LogUtil.e("getBleRecord===", type+"==="+m_nowMac);

//            int i = 1/0;

            ClientManager.getClient().getRecord(m_nowMac, new IGetRecordResponse() {

                @Override
                public void onResponseSuccess(String phone, String bikeTradeNo, String timestamp, String transType, String mackey, String index, int Major, int Minor, String vol) {
                    LogUtil.e("getBleRecord===0", transType + "==Major:"+ Major +"---Minor:"+Minor+"==="+bikeTradeNo);
                    deleteBleRecord(bikeTradeNo);

                }

                @Override
                public void onResponseSuccessEmpty() {
//                ToastUtil.showMessageApp(context, "record empty");
                    LogUtil.e("getBleRecord===1", "Success===Empty");
                }

                @Override
                public void onResponseFail(int code) {
                    LogUtil.e("getBleRecord===onResponseFail", loadingDialog.isShowing()+"==="+code+"==="+Code.toString(code));

//                    deleteBleRecord(bikeTradeNo);
//                    closeLoadingDialog();

//                    String tvAgain = tv_againBtn.getText().toString().trim();
//                    int action_type;
////                    LogUtil.e("getBleRecord===e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);
//
//                    if(isAgain){
//                        if("再次开锁".equals(tvAgain)){
//                            action_type=4;
//                            ToastUtil.showMessageApp(context,"开锁失败");
//                        }else{
//                            action_type=2;
//                            ToastUtil.showMessageApp(context,"关锁失败");
//                        }
//                    }else{
//                        if(isEndBtn){
//                            action_type=3;
//                            ToastUtil.showMessageApp(context,"还车失败");
//                        }else{
//                            action_type=1;
//                            ToastUtil.showMessageApp(context,"开锁失败");
//                        }
//                    }
//                    car_notification(action_type, 3, 0, type+"===getBleRecord_onResponseFail==="+code+"==="+Code.toString(code));
                }
            });
        }catch (Exception e){
            LogUtil.e("getBleRecord===e", "==="+e);

//            closeLoadingDialog();

            String tvAgain = tv_againBtn.getText().toString().trim();
            int action_type;
            LogUtil.e("getBleRecord===e2", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

            if(isAgain){
                if("再次开锁".equals(tvAgain)){
                    action_type=4;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }else{
                    action_type=2;
                    ToastUtil.showMessageApp(context,"关锁失败");
                }
            }else{
                if(isEndBtn){
                    action_type=3;
                    ToastUtil.showMessageApp(context,"还车失败");
                }else{
                    action_type=1;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }
            }
            car_notification(action_type, 3, 0, type+"===getBleRecord_e==="+e);
        }
    }

    //泺平===与设备，删除记录
    private void deleteBleRecord(String tradeNo) {

        try{
            ClientManager.getClient().deleteRecord(m_nowMac, tradeNo, new IGetRecordResponse() {

                @Override
                public void onResponseSuccess(String phone, String bikeTradeNo, String timestamp, String transType, String mackey, String index, int Major, int Minor, String vol) {
                    LogUtil.e("biking=deleteBleRecord", "Major:"+ Major +"---Minor:"+Minor);
                    deleteBleRecord(bikeTradeNo);
                }

                @Override
                public void onResponseSuccessEmpty() {
                    LogUtil.e("scan===deleteBleRecord", "Success===Empty");
                }

                @Override
                public void onResponseFail(int code) {
                    LogUtil.e("deleteBleRecord===onResponseFail", Code.toString(code));
                    popupwindow.dismiss();

                    closeLoadingDialog();

//                    String tvAgain = tv_againBtn.getText().toString().trim();
//                    int action_type;
////                    LogUtil.e("deleteBleRecord===e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);
//
//                    if(isAgain){
//                        if("再次开锁".equals(tvAgain)){
//                            action_type=4;
//                            ToastUtil.showMessageApp(context,"开锁失败");
//                        }else{
//                            action_type=2;
//                            ToastUtil.showMessageApp(context,"关锁失败");
//                        }
//                    }else{
//                        if(isEndBtn){
//                            action_type=3;
//                            ToastUtil.showMessageApp(context,"还车失败");
//                        }else{
//                            action_type=1;
//                            ToastUtil.showMessageApp(context,"开锁失败");
//                        }
//                    }
//                    car_notification(action_type, 3, 0, type+"===deleteBleRecord_onResponseFail==="+code+"==="+Code.toString(code));
                }
            });
        }catch (Exception e){
//            closeLoadingDialog();

            String tvAgain = tv_againBtn.getText().toString().trim();
            int action_type;
            LogUtil.e("deleteBleRecord===e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

            if(isAgain){
                if("再次开锁".equals(tvAgain)){
                    action_type=4;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }else{
                    action_type=2;
                    ToastUtil.showMessageApp(context,"关锁失败");
                }
            }else{
                if(isEndBtn){
                    action_type=3;
                    ToastUtil.showMessageApp(context,"还车失败");
                }else{
                    action_type=1;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }
            }
            car_notification(action_type, 3, 0, type+"===deleteBleRecord_e==="+e);
        }
    }

    //与设备，获取记录
    private void getBleRecord2() {
        try{
            LogUtil.e("getBleRecord2===", type+"==="+m_nowMac);

//            int i = 1/0;

            ClientManager.getClient().getRecord(m_nowMac, new IGetRecordResponse() {
                @Override
                public void onResponseSuccess(String phone, final String bikeTradeNo, String timestamp, final String transType, final String mackey, String index, final int Major, final int Minor, String vol) {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.e("getBleRecord2===onResponseS", transType+"==Major:"+ Major +"---Minor:"+Minor+"---mackey:"+mackey);

                            if(BaseApplication.getInstance().isTestLog()){
//                            macText.setText(Major+"==="+Minor+"==="+macList);
                            }

//                          ToastUtil.showMessageApp(context, "Major:"+ Major +"---Minor:"+Minor);

                            transtype = transType;
                            major = Major;
                            minor = Minor;

                            SharedPreferencesUrls.getInstance().putInt("major", major);

//                          m_myHandler.sendEmptyMessage(9);

                            deleteBleRecord2(bikeTradeNo);
                        }
                    });


                }

                @Override
                public void onResponseSuccessEmpty() {

                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.e("getBleRecord2===", "Success===Empty");
                        }
                    });
                }

                @Override
                public void onResponseFail(final int code) {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.e("getBleRecord2===fail", Code.toString(code));

                            closeLoadingDialog();

//                            String tvAgain = tv_againBtn.getText().toString().trim();
//                            int action_type;
//
//                            if(isAgain){
//                                if("再次开锁".equals(tvAgain)){
//                                    action_type=4;
//                                    ToastUtil.showMessageApp(context,"开锁失败");
//                                }else{
//                                    action_type=2;
//                                    ToastUtil.showMessageApp(context,"关锁失败");
//                                }
//                            }else{
//                                if(isEndBtn){
//                                    action_type=3;
//                                    ToastUtil.showMessageApp(context,"还车失败");
//                                }else{
//                                    action_type=1;
//                                    ToastUtil.showMessageApp(context,"开锁失败");
//                                }
//                            }
//                            car_notification(action_type, 3, 0, type+"===getBleRecord2_fail==="+code+"==="+Code.toString(code));
                        }
                    });
                }
            });
        }catch (Exception e){
//            closeLoadingDialog();

            String tvAgain = tv_againBtn.getText().toString().trim();
            int action_type;
            LogUtil.e("getBleRecord2===e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

            if(isAgain){
                if("再次开锁".equals(tvAgain)){
                    action_type=4;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }else{
                    action_type=2;
                    ToastUtil.showMessageApp(context,"关锁失败");
                }
            }else{
                if(isEndBtn){
                    action_type=3;
                    ToastUtil.showMessageApp(context,"还车失败");
                }else{
                    action_type=1;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }
            }
            car_notification(action_type, 3, 0, type+"===getBleRecord2_e==="+e);
        }
    }

    //与设备，删除记录
    private void deleteBleRecord2(String tradeNo) {


        try{
            ClientManager.getClient().deleteRecord(m_nowMac, tradeNo, new IGetRecordResponse() {
                @Override
                public void onResponseSuccess(String phone, final String bikeTradeNo, String timestamp, final String transType, String mackey, String index, final int Major, final int Minor, String vol) {

                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.e("deleteBleRecord2===", transType+"==Major:"+ Major +"---Minor:"+Minor);

                            transtype = transType;
                            major = Major;
                            minor = Minor;

                            deleteBleRecord2(bikeTradeNo);
                        }
                    });
                }

                @Override
                public void onResponseSuccessEmpty() {
                    LogUtil.e("biking=deleteBleRecord2", "Success===Empty");

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
                            LogUtil.e("deleteBleRecord2===onResponseFail", Code.toString(code));

                            closeLoadingDialog();

//                            String tvAgain = tv_againBtn.getText().toString().trim();
//                            int action_type;
////                            LogUtil.e("deleteBleRecord2===onResponseFail", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);
//
//                            if(isAgain){
//                                if("再次开锁".equals(tvAgain)){
//                                    action_type=4;
//                                    ToastUtil.showMessageApp(context,"开锁失败");
//                                }else{
//                                    action_type=2;
//                                    ToastUtil.showMessageApp(context,"关锁失败");
//                                }
//                            }else{
//                                if(isEndBtn){
//                                    action_type=3;
//                                    ToastUtil.showMessageApp(context,"还车失败");
//                                }else{
//                                    action_type=1;
//                                    ToastUtil.showMessageApp(context,"开锁失败");
//                                }
//                            }
//                            car_notification(action_type, 3, 0, type+"===deleteBleRecord2_onResponseFail==="+code+"==="+Code.toString(code));
                        }
                    });

                }
            });
        }catch (Exception e){
//            closeLoadingDialog();

            String tvAgain = tv_againBtn.getText().toString().trim();
            int action_type;
            LogUtil.e("deleteBleRecord2===e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

            if(isAgain){
                if("再次开锁".equals(tvAgain)){
                    action_type=4;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }else{
                    action_type=2;
                    ToastUtil.showMessageApp(context,"关锁失败");
                }
            }else{
                if(isEndBtn){
                    action_type=3;
                    ToastUtil.showMessageApp(context,"还车失败");
                }else{
                    action_type=1;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }
            }
            car_notification(action_type, 3, 0, type+"===deleteBleRecord2_e==="+e);
        }
    }

    //监听锁关闭事件
    private final ICloseListener mCloseListener = new ICloseListener() {
        @Override
        public void onNotifyClose() {

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    LogUtil.e("onNotifyClose===", "====");

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



    //泺平===连接设备
    private void connectDeviceLP() {

        try{
            BleConnectOptions options = new BleConnectOptions.Builder()
                    .setConnectRetry(3)
                    .setConnectTimeout(2000)
//                .setConnectTimeout(timeout)
//                .setServiceDiscoverRetry(1)
//                .setServiceDiscoverTimeout(10000)
//                .setEnableNotifyRetry(1)
//                .setEnableNotifyTimeout(10000)
                    .build();

//            int i = 1/0;

            ClientManager.getClient().connect(m_nowMac, options, new IConnectResponse() {
                @Override
                public void onResponseFail(int code) {
                    isStop = false;
                    isLookPsdBtn = false;

                    m_myHandler2.removeCallbacksAndMessages(null);

                    LogUtil.e("connectDeviceLP===Fail", "==="+code+"==="+Code.toString(code));

                    if(popupwindow!=null){
                        popupwindow.dismiss();
                    }

                    String tvAgain = tv_againBtn.getText().toString().trim();
                    int action_type;
                    LogUtil.e("connectDeviceLP===Fail_1", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                    if(isAgain){
                        if("再次开锁".equals(tvAgain)){
                            action_type=4;
                        }else{
                            action_type=2;
                        }
                    }else{
                        if(isEndBtn){
                            action_type=3;
                        }else{
                            action_type=1;
                        }
                    }

                    Toast.makeText(context,"蓝牙连接失败，重启软件试试吧！",Toast.LENGTH_LONG).show();
                    getBleRecord();
                    car_notification(action_type, 2, 0, type+"===connect_f==="+code+"==="+Code.toString(code));
                }

                @Override
                public void onResponseSuccess(BleGattProfile profile) {

                    try{
                        m_myHandler2.removeCallbacksAndMessages(null);

                        isStop = true;
                        isLookPsdBtn = true;
                        state = 1;  //连接成功
                        lockStatus = 3;

                        LogUtil.e("connectDeviceLP===", "Success==="+isOpenLock+"==="+isEndBtn);

//                        getBleRecord();

                        if(isEndBtn){
                            queryOpenState();
                        }else{
                            getStateLP();
                        }
                    }catch (Exception e){
//                        closeLoadingDialog();

                        String tvAgain = tv_againBtn.getText().toString().trim();
                        int action_type;
                        LogUtil.e("connectDeviceLP===onRS_e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                        if(isAgain){
                            if("再次开锁".equals(tvAgain)){
                                action_type=4;
                                ToastUtil.showMessageApp(context,"开锁失败");
                            }else{
                                action_type=2;
                                ToastUtil.showMessageApp(context,"关锁失败");
                            }
                        }else{
                            if(isEndBtn){
                                action_type=3;
                                ToastUtil.showMessageApp(context,"还车失败");
                            }else{
                                action_type=1;
                                ToastUtil.showMessageApp(context,"开锁失败");
                            }
                        }
                        getBleRecord();
                        car_notification(action_type, 3, 0, type+"===connectDeviceLP_onRS_e==="+e);
                    }

                }
            });
        }catch (Exception e){
//            closeLoadingDialog();

            String tvAgain = tv_againBtn.getText().toString().trim();
            int action_type;
            LogUtil.e("connectDeviceLP===e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

            if(isAgain){
                if("再次开锁".equals(tvAgain)){
                    action_type=4;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }else{
                    action_type=2;
                    ToastUtil.showMessageApp(context,"关锁失败");
                }
            }else{
                if(isEndBtn){
                    action_type=3;
                    ToastUtil.showMessageApp(context,"还车失败");
                }else{
                    action_type=1;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }
            }
            getBleRecord();
            car_notification(action_type, 3, 0, type+"===connectDeviceLP_e==="+e);
        }
    }

    //泺平===还车_查锁是否关闭
    private void queryOpenState() {

        try{
            LogUtil.e("queryOpenState===0", loadingDialog.isShowing()+"===="+m_nowMac);

//            int i = 1/0;

            ClientManager.getClient().queryOpenState(m_nowMac, new IQueryOpenStateResponse() {
                @Override
                public void onResponseSuccess(final boolean isOpen) {

                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                LogUtil.e("queryOpenState===onRS", "===="+isOpen);

//                              int i = 1/0;

                                if(isOpen) {

                                    if(isEndBtn){
                                        open++;
                                    }

                                    if(open<2){
                                        ToastUtil.showMessageApp(context,"车锁未关，请手动关锁");
                                    }

                                    car_notification(3, 5, 0, "");

                                }else {

                                    car_notification(3, 1, 1, "");

                                }
                            }catch (Exception e){
//                                closeLoadingDialog();

                                String tvAgain = tv_againBtn.getText().toString().trim();
                                int action_type;
                                LogUtil.e("queryOpenState===onRS_e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                                if(isAgain){
                                    if("再次开锁".equals(tvAgain)){
                                        action_type=4;
                                        ToastUtil.showMessageApp(context,"开锁失败");
                                    }else{
                                        action_type=2;
                                        ToastUtil.showMessageApp(context,"关锁失败");
                                    }
                                }else{
                                    if(isEndBtn){
                                        action_type=3;
                                        ToastUtil.showMessageApp(context,"还车失败");
                                    }else{
                                        action_type=1;
                                        ToastUtil.showMessageApp(context,"开锁失败");
                                    }
                                }
                                car_notification(action_type, 3, 0, type+"===queryOpenState_onRS_e==="+e);
                            }

                        }
                    });
                }

                @Override
                public void onResponseFail(final int code) {
                    LogUtil.e("queryOpenState===f",  loadingDialog.isShowing()+"===="+Code.toString(code));
                    ToastUtil.showMessageApp(context,"还车失败");
                    getBleRecord();
                    car_notification(3, 3, 1, type+"==="+code+"==="+Code.toString(code));

//                    m_myHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            closeLoadingDialog();
//                        }
//                    });

                }
            });
        }catch (Exception e){
//            closeLoadingDialog();

            String tvAgain = tv_againBtn.getText().toString().trim();
            int action_type;
            LogUtil.e("queryOpenState===e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

            if(isAgain){
                if("再次开锁".equals(tvAgain)){
                    action_type=4;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }else{
                    action_type=2;
                    ToastUtil.showMessageApp(context,"关锁失败");
                }
            }else{
                if(isEndBtn){
                    action_type=3;
                    ToastUtil.showMessageApp(context,"还车失败");
                }else{
                    action_type=1;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }
            }
            getBleRecord();
            car_notification(action_type, 3, 0, type+"===queryOpenState_e==="+e);
        }
    }

    private void getStateLP(){

        try{
//            int i = 1/0;

            LogUtil.e("getStateLP===", type+"===="+m_nowMac);

            ClientManager.getClient().getStatus(m_nowMac, new IGetStatusResponse() {
                @Override
                public void onResponseSuccess(String version, final String keySerial, final String macKey, String vol) {
//                  quantity = vol+"";

                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            try{
                                LogUtil.e("getStateLP===Success", "===="+macKey);
                                keySource = keySerial;

                                LogUtil.e("getStateLP===1", isOpenLock+"==="+isEndBtn);

//                                int i = 1/0;

                                if(isEndBtn){
                                    queryOpenState();
                                }else{
                                    rent();
                                }

                                LogUtil.e("getStateLP===2", "==="+loadingDialog.isShowing());

                            }catch (Exception e){
//                                closeLoadingDialog();

                                String tvAgain = tv_againBtn.getText().toString().trim();
                                int action_type;
                                LogUtil.e("getStateLP===onRS_e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                                if(isAgain){
                                    if("再次开锁".equals(tvAgain)){
                                        action_type=4;
                                        ToastUtil.showMessageApp(context,"开锁失败");
                                    }else{
                                        action_type=2;
                                        ToastUtil.showMessageApp(context,"关锁失败");
                                    }
                                }else{
                                    if(isEndBtn){
                                        action_type=3;
                                        ToastUtil.showMessageApp(context,"还车失败");
                                    }else{
                                        action_type=1;
                                        ToastUtil.showMessageApp(context,"开锁失败");
                                    }
                                }
                                getBleRecord();
                                car_notification(action_type, 3, 0, type+"===getStateLP_onRS_e==="+e);
                            }

                        }
                    });
                }

                @Override
                public void onResponseFail(final int code) {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.e("getStateLP===Fail", code+"==="+Code.toString(code));
                            getBleRecord();
                            if("锁已开".equals(Code.toString(code))){
//                                ToastUtil.showMessageApp(context, Code.toString(code));
                                car_notification(isAgain?4:1, 1, 0, type+"===getStateLP_f==="+code+"==="+Code.toString(code));
                            }else{
                                ToastUtil.showMessageApp(context, "开锁失败");
                                car_notification(isAgain?4:1, 3, 0, type+"===getStateLP_f==="+code+"==="+Code.toString(code));
                            }

//                            closeLoadingDialog();

                        }
                    });
                }

            });
        }catch (Exception e){
//            closeLoadingDialog();

            String tvAgain = tv_againBtn.getText().toString().trim();
            int action_type;
            LogUtil.e("getStateLP===e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

            if(isAgain){
                if("再次开锁".equals(tvAgain)){
                    action_type=4;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }else{
                    action_type=2;
                    ToastUtil.showMessageApp(context,"关锁失败");
                }
            }else{
                if(isEndBtn){
                    action_type=3;
                    ToastUtil.showMessageApp(context,"还车失败");
                }else{
                    action_type=1;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }
            }
            car_notification(action_type, 3, 0, type+"===getStateLP_e==="+e);
        }
    }

    //行运兔===type4
    void checkConnect(){
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("checkConnect===", cn+"==="+bleService.connect);

                if(!bleService.connect){
                    cn++;

                    if(cn<10){
                        checkConnect();
                    }else{

                        if("".equals(oid)){
//                                scrollToFinishActivity();

                            Toast.makeText(context,"扫码唤醒失败，重启手机蓝牙换辆车试试吧！",Toast.LENGTH_LONG).show();
                        }else{
                            unlock();
                        }

//                        popupwindow.dismiss();

                    }

                }else{
                    bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});

                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.e("checkConnect===3", tel+"==="+bleid+"==="+bleService+"==="+m_nowMac);

                            button8();
                            button9();
                            button3();

                            m_myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    LogUtil.e("checkConnect===4", bleService.cc+"==="+"B1 25 80 00 00 56 ".equals(bleService.cc));

                                    if("B1 25 80 00 00 56 ".equals(bleService.cc)){
//                                        closeLoadingDialog();

                                        LogUtil.e("checkConnect===5", oid+"==="+bleService.cc);
                                        ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");



                                        if(isAgain){
                                            car_notification(4, 0, 0, "");
                                            tv_againBtn.setText("临时上锁");
                                            SharedPreferencesUrls.getInstance().putString("tempStat","0");
                                        }else{
                                            car_notification(1, 0, 0, "");
                                        }

                                    }else{
                                        unlock();
                                    }

                                    LogUtil.e("checkConnect===6", "==="+bleService.cc);

                                }
                            }, 500);
                        }
                    }, 500);
                }

            }
        }, 1 * 1000);
    }

    void checkConnect2(){
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("checkConnect2===", cn+"==="+bleService.connect);

                if(!bleService.connect){
                    cn++;

                    if(cn<10){
                        checkConnect2();
                    }else{

                        if("".equals(oid)){
//                                scrollToFinishActivity();

                            Toast.makeText(context,"扫码唤醒失败，重启手机蓝牙换辆车试试吧！",Toast.LENGTH_LONG).show();
                        }else{
                            lock();
                        }

                    }

                }else{
                    bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});

                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.e("checkConnect2===2", tel+"==="+bleid+"==="+bleService+"==="+m_nowMac);

                            button8();
                            button9();
                            button2();

                            m_myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    LogUtil.e("checkConnect2===3", bleService.cc+"==="+"B1 2A 80 00 00 5B ".equals(bleService.cc));

                                    if("B1 2A 80 00 00 5B ".equals(bleService.cc)){
                                        LogUtil.e("checkConnect2===4", "==="+bleService.cc);

                                        if(isAgain){
                                            car_notification(2, 0, isAgain?0:1, "");
                                            tv_againBtn.setText("再次开锁");
                                            SharedPreferencesUrls.getInstance().putString("tempStat","1");

                                            ToastUtil.showMessageApp(context,"恭喜您,关锁成功!");
                                        }else{
                                            car_notification(3, 0, isAgain?0:1, "");
                                        }

//                                        closeLoadingDialog();
                                    }else{
//                                        ToastUtil.showMessageApp(context,"关锁失败，请重试");

                                        lock();
                                    }

                                    LogUtil.e("checkConnect2===5", "==="+bleService.cc);

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

    //泺平锁临时上锁
    private void temporaryAction() {
        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在加载");
                    loadingDialog.show();
                }
            }
        });

        ClientManager.getClient().temporaryAction(m_nowMac, "000000000000", new ITemporaryActionResponse() {
            @Override
            public void onResponseSuccess() {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        closeLoadingDialog();

                        ToastUtil.showMessageApp(context,"请手动关锁");
//                        m_myHandler.sendEmptyMessage(7);
                    }
                });
            }

            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        closeLoadingDialog();
//                        UIHelper.ToastError(context, Code.toString(code));
                    }
                });

            }
        });
    }

    //设防
    void button2() {
        IoBuffer ioBuffer = IoBuffer.allocate(20);
        byte[] cmd = sendCmd("00001000", "00000000");
        ioBuffer.writeBytes(cmd);
        bleService.write(toBody(ioBuffer.readableBytes()));
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

            LogUtil.e("connectDevice===", "==="+imei);
        }
    }

    //小安
    @Override
    public void onConnect(BluetoothDevice bluetoothDevice) {
        LogUtil.e("mf===Xiaoan", "Connect==="+isConnect);

        try {
            isConnect = true;
            m_myHandler2.removeCallbacksAndMessages(null);

            m_myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    String tvAgain = tv_againBtn.getText().toString().trim();

                    LogUtil.e("mf===Xiaoan1", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                    if(isAgain){
                        if("再次开锁".equals(tvAgain)){
                            xiaoanOpen_blue();
                        }else{
                            xiaoanClose_blue();
                        }
                    }else{
                        if(isEndBtn){
                            xiaoanClose_blue();
                        }else{
                            xiaoanOpen_blue();
                        }
                    }

                }
            }, 2 * 1000);
        } catch (Exception e) {
            closeLoadingDialog();
            LogUtil.e("mf===Xiaoan-e", "==="+e);

//          memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
        }
    }



    //小安
    @Override
    public void onDisConnect(BluetoothDevice bluetoothDevice) {
        LogUtil.e("mf===Xiaoan", "DisConnect==="+isConnect);

        try {
            if(isConnect){
                isConnect = false;

//                LogUtil.e("mf===Xiaoan2", "DisConnect==="+isConnect);
                return;
            }

//            if (apiClient != null) {
//                apiClient.onDestroy();
//            }

            isConnect = false;
        } catch (Exception e) {
            closeLoadingDialog();
            LogUtil.e("mf===Xiaoan2-e", "==="+e);

//          memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
        }



//        String tvAgain = tv_againBtn.getText().toString().trim();
//        if(isAgain){
//            if("再次开锁".equals(tvAgain)){
//                unlock();
//            }else{
//                lock();
//            }
//        }else{
//            if(isEndBtn){
//                lock();
//            }else{
//                unlock();
//            }
//        }

    }

    public void xiaoanOpen_blue() {

        if(apiClient==null) return;

        try {
            apiClient.setDefend(false, new BleCallback() {
                @Override
                public void onResponse(final Response response) {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.e("xiaoanOpen===", response.toString());

                            if(response.code==0){
                                car_notification(isAgain?4:1, 1, 0, "");

                                if(isAgain){
                                    tv_againBtn.setText("临时上锁");
                                    SharedPreferencesUrls.getInstance().putString("tempStat","0");

                                    ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

//                                    closeLoadingDialog();
                                }

                            }else{
//                              ToastUtil.showMessageApp(context,"开锁失败");

                                if (apiClient != null) {
                                    apiClient.disConnect();
                                    apiClient.onDestroy();
                                    apiClient=null;
                                }

                                unlock();
                            }

                        }
                    });

                }
            });
        } catch (Exception e) {
            closeLoadingDialog();
            LogUtil.e("mf===xaOpen_blue-e", "==="+e);

//          memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
        }

    }

    public void xiaoanClose_blue() {
        if(apiClient==null) return;

        try {
            apiClient.setDefend(true, new BleCallback() {
                @Override
                public void onResponse(final Response response) {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.e("xiaoanClose===", type+"==="+deviceuuid+"==="+response.toString());

                            if(response.code==0){

                                macList2 = new ArrayList<> (macList);

                                car_notification(isAgain?2:3, 1, isAgain?0:1, "");

                                if(isAgain){
                                    tv_againBtn.setText("再次开锁");
                                    SharedPreferencesUrls.getInstance().putString("tempStat","1");

                                    ToastUtil.showMessageApp(context,"恭喜您,关锁成功!");

//                                    closeLoadingDialog();
                                }
                            }else if(response.code==6){
                                ToastUtil.showMessageApp(context,"车辆未停止，请停止后再试");

                                closeLoadingDialog();
                            }else{
//                            ToastUtil.showMessageApp(context,"关锁失败");

//                            if (apiClient != null) {
//                                apiClient.onDestroy();
//                            }

                                if (apiClient != null) {
                                    apiClient.disConnect();
                                    apiClient.onDestroy();
                                    apiClient=null;
                                }

                                lock();

//                            if("108".equals(info)){       //TODO  2
//                                LogUtil.e("biking_defend===1", "====");
//
//                            }else{
//                                LogUtil.e("biking_defend===2", "====");
//
//                                ToastUtil.showMessageApp(context,"关锁失败，请重试");
//                            }
                            }


                        }
                    });
                }
            });
        } catch (Exception e) {
            closeLoadingDialog();
            LogUtil.e("mf===xaClose_blue=e", "==="+e);

//          memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
        }


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

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

    }

    @Override
    public void deactivate() {

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
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.e("mf===", "===onStop");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e("mf===onSIS", "==="+isProcessExist(context, android.os.Process.myPid()));

        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public static boolean isProcessExist(Context context, int pid) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> lists ;
        if (am != null) {
            lists = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo appProcess : lists) {
                if (appProcess.pid == pid) {
                    Log.e("TAG","333333");
                    return true;
                }
            }
        }
        return false;
    }

    void closeDialog(){
        closeLoadingDialog2();

        if (advDialog != null){
            advDialog.dismiss();
        }
        if (customDialog2 != null){
            customDialog2.dismiss();
        }
        if (customDialog3 != null){
            customDialog3.dismiss();
        }
    }

    void closeLoadingDialog(){
        if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }

    }

    void closeLoadingDialog2(){
        if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
        if (loadingDialog2 != null && loadingDialog2.isShowing()){
            loadingDialog2.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        closeDialog();

        if("5".equals(type)  || "6".equals(type)){
            ClientManager.getClient().stopSearch();

            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);

            ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);
//            ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener2);

        }else if("4".equals(type) || "8".equals(type)){
        }else if("7".equals(type)){
            if (apiClient != null) {
                apiClient.onDestroy();
                apiClient = null;
            }
        }else if("11".equals(type)){
            TbitBle.destroy();
        }else if("2".equals(type) || "3".equals(type) || "9".equals(type) || "10".equals(type)){
//            BaseApplication.getInstance().getIBLE().stopScan();
//            BaseApplication.getInstance().getIBLE().refreshCache();
//            BaseApplication.getInstance().getIBLE().close();
//            BaseApplication.getInstance().getIBLE().disconnect();

//            byte[] bb=new byte[3];
//
//            BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb",
//                    bb, true, new BleWriteCallback() {
//                        @Override
//                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
//                            LogUtil.e("onWriteSuccess===", current+"==="+total+"==="+justWrite);
//                        }
//
//                        @Override
//                        public void onWriteFailure(BleException exception) {
//                            LogUtil.e("onWriteFailure===", "==="+exception);
//                        }
//                    });

            BleManager.getInstance().disconnectAllDevice();
            BleManager.getInstance().destroy();

//            if (broadcastReceiver != null) {
//                activity.unregisterReceiver(broadcastReceiver);
//                broadcastReceiver = null;
//            }
        }

        super.onDestroy();

        LogUtil.e("mf===onDestroy", isLookPsdBtn+"===");

        if (mapView != null) {
            mapView.onDestroy();
        }

//        if (broadcastReceiver2 != null) {
//            activity.unregisterReceiver(broadcastReceiver2);
//            broadcastReceiver2 = null;
//        }

        isWaitEbikeInfo = false;
        if (ebikeInfoThread != null) {
            ebikeInfoThread.interrupt();
            ebikeInfoThread = null;
        }


    }



    private class MyImageLoader extends com.youth.banner.loader.ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {

            DisplayImageOptions options=new DisplayImageOptions.Builder()
                    .cacheInMemory(true)//使用内存缓存
                    .cacheOnDisk(true)//使用磁盘缓存

//                    .showImageOnLoading(R.mipmap.ic_launcher)//设置正在下载的图片
//                    .showImageForEmptyUri(R.mipmap.ic_launcher)//url为空或请求的资源不存在时
//                    .showImageOnFail(R.mipmap.ic_launcher)//下载失败时显示的图片

                    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片色彩模式
                    .imageScaleType(ImageScaleType.EXACTLY)//设置图片的缩放模式===imageView,,ScaleType
                    .displayer(new RoundedBitmapDisplayer(10))//设置图片圆角显示  弧度
                    .build();

//            Glide.with(context.getApplicationContext())
//                    .setDefaultOptions(options)
//
//                    .load(path)
//                    .into(imageView);

            ImageLoader.getInstance().displayImage(path.toString(), imageView, options);


        }

        @Override
        public ImageView createImageView(Context context) {
            RoundImageView img = new RoundImageView(context);
            return img;

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
                            customBuilder.setType(3).setTitle("温馨提示").setMessage("您需要在设置里打开电话权限！")
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
                            customBuilder.setType(3).setTitle("温馨提示").setMessage("您需要在设置里允许获取定位权限！")
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
//				.setDeviceMac(address)                  // 只扫描指定mac的设备，可选
//                .setAutoConnect(true)                 // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(timeout)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    void scan(){
//        loadingDialog = DialogUtils.getLoadingDialog(context, "正在搜索...");
//		loadingDialog.setTitle("正在搜索");
//		loadingDialog.show();

//        if(macList!=null){
//            macList.clear();
//        }

        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
//                mDeviceAdapter.clearScanDevice();
//                mDeviceAdapter.notifyDataSetChanged();
//                img_loading.startAnimation(operatingAnim);
//                img_loading.setVisibility(View.VISIBLE);
//                btn_scan.setText(getString(R.string.stop_scan));
                LogUtil.e("mf===onScanStarted", "==="+success);

            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);

//                D1:E4:39:55:3D:F9

//                if (bleDevice.getName()!=null && bleDevice.getName().startsWith("abeacon_")){
//                    macList.add(""+bleDevice.getName());
//                }

                LogUtil.e("mf===onLeScan", bleDevice.getName()+"==="+bleDevice.getMac());
            }

            @Override
            public void onScanning(final BleDevice bleDevice) {
//                mDeviceAdapter.addDevice(bleDevice);
//                mDeviceAdapter.notifyDataSetChanged();

//                if(macList.size()>0){
//                    BleManager.getInstance().cancelScan();
//                }

                LogUtil.e("mf===onScanning", bleDevice.getName()+"==="+bleDevice.getMac());

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
//							LogUtil.e("onScanning===2", isConnect+"==="+bleDevice+"==="+bleDevice.getMac());
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

                LogUtil.e("mf===onScanFinished", scanResultList+"==="+scanResultList.size());
            }
        });
    }



    void scan2(){
//      loadingDialog = DialogUtils.getLoadingDialog(context, "正在搜索...");
//		loadingDialog.setTitle("正在搜索");
//		loadingDialog.show();

        BleManager.getInstance().cancelScan();

        isFind = false;
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                LogUtil.e("mf===onScanStarted2", "==="+success);
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);

                LogUtil.e("mf===onLeScan2", bleDevice+"==="+bleDevice.getMac());
            }

            @Override
            public void onScanning(final BleDevice bleDevice) {

                LogUtil.e("mf===onScanning2", bleDevice+"==="+bleDevice.getMac());

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(m_nowMac.equals(bleDevice.getMac())){
                            //                            if (loadingDialog != null && loadingDialog.isShowing()) {
//                                loadingDialog.dismiss();
//                            }

                            isFind = true;
                            BleManager.getInstance().cancelScan();

                            LogUtil.e("onScanning===2_1", isConnect+"==="+bleDevice+"==="+bleDevice.getMac());

//							Toast.makeText(context, "搜索成功", Toast.LENGTH_LONG).show();

                            connect();

//                            m_myHandler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if(!isConnect)
//                                        connect();
//                                }
//                            }, 5 * 1000);
                        }
                    }
                });

            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {

                LogUtil.e("mf===onScanFinished2", isFind+"==="+type);

                if(!isFind){
                    if("3".equals(type)){

                        String tvAgain = tv_againBtn.getText().toString().trim();

                        LogUtil.e("mf===onScanFinished2", tvAgain+"==="+isAgain+"==="+isFind+"==="+type);

                        if(isAgain){
                            if("再次开锁".equals(tvAgain)){
                                unlock();
                            }
                        }else{
                            if(isEndBtn){
//                                getLockStatus();
                                car_notification(3, 2, 0, type+"===onScanFinished");
                                Toast.makeText(context,"蓝牙连接失败，请靠近车锁，重启软件试试吧！",Toast.LENGTH_LONG).show();
                            }else{
                                unlock();
                            }
                        }

                    }else{
                        Toast.makeText(context,"蓝牙连接失败，请靠近车锁，重启软件试试吧！",Toast.LENGTH_LONG).show();
                        String tvAgain = tv_againBtn.getText().toString().trim();
                        int action_type;
                        LogUtil.e("mf===Xiaoan1", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                        if(isAgain){
                            if("再次开锁".equals(tvAgain)){
                                action_type=4;
                            }else{
                                action_type=2;
                            }
                        }else{
                            if(isEndBtn){
                                action_type=3;
                            }else{
                                action_type=1;
                            }
                        }
                        car_notification(action_type, 2, 0, type+"===onScanFinished");
                        if(popupwindow!=null){
                            popupwindow.dismiss();
                        }
                    }
                }

//                connect();

            }
        });
    }

    private void connect(){
//        loadingDialog = DialogUtils.getLoadingDialog(this, "正在连接...");
//        loadingDialog.setTitle("正在连接");
//        loadingDialog.show();



        try{
            LogUtil.e("connect===", carmodel_id+"==="+type+"==="+isLookPsdBtn+"==="+isOpenLock+"==="+loadingDialog.isShowing());

            BleManager.getInstance().cancelScan();

//            int i = 1/0;

            BleManager.getInstance().connect(m_nowMac, new BleGattCallback() {
//            BleManager.getInstance().connect("00:00:00:00:00:00", new BleGattCallback() {
                @Override
                public void onStartConnect() {
                    LogUtil.e("onStartConnect===", "===");
                }

                @Override
                public void onConnectFail(BleDevice bleDevice, BleException exception) {
                    LogUtil.e("onConnectFail===", isLookPsdBtn+"==="+isStop+"==="+type+"==="+bleDevice.getMac()+"==="+exception);

                    isLookPsdBtn = false;
                    m_myHandler2.removeCallbacksAndMessages(null);

                    LogUtil.e("onConnectFail===1", isLookPsdBtn+"==="+isStop+"==="+type);

                    if("3".equals(type)){

                        String tvAgain = tv_againBtn.getText().toString().trim();
                        if(isAgain){
                            if("再次开锁".equals(tvAgain)){
                                unlock();
                            }
                        }else{
                            if(isEndBtn){
//                              getLockStatus();
                                car_notification(3, 2, 0, type+"===onConnectFail==="+exception);
                                Toast.makeText(context,"蓝牙连接失败，重启软件试试吧！", Toast.LENGTH_LONG).show();
                            }else{
                                unlock();
                            }
                        }
                    }else{
                        Toast.makeText(context,"蓝牙连接失败，重启软件试试吧！",Toast.LENGTH_LONG).show();
                        String tvAgain = tv_againBtn.getText().toString().trim();
                        int action_type;
                        LogUtil.e("onConnectFail===2", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                        if(isAgain){
                            if("再次开锁".equals(tvAgain)){
                                action_type=4;
                            }else{
                                action_type=2;
                            }
                        }else{
                            if(isEndBtn){
                                action_type=3;
                            }else{
                                action_type=1;
                            }
                        }
                        car_notification(action_type, 2, 0, type+"===onConnectFail==="+exception);
                        if(popupwindow!=null){
                            popupwindow.dismiss();
                        }
                    }

                }

                @Override
                public void onConnectSuccess(final BleDevice device, BluetoothGatt gatt, int status) {
//                if (loadingDialog != null && loadingDialog.isShowing()) {
//                    loadingDialog.dismiss();
//                }
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            try{
                                m_myHandler2.removeCallbacksAndMessages(null);

                                state = 1;
                                lockStatus = 3;
                                isLookPsdBtn = true;
                                bleDevice = device;

//                              BleManager.getInstance().cancelScan();

                                LogUtil.e("onConnectSuccess===", bleDevice.getMac()+"==="+loadingDialog.isShowing());
//                              Toast.makeText(context, "连接成功", Toast.LENGTH_LONG).show();

//                                int i = 1/0;

                                m_myHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        getBleToken();

                                    }
                                }, 500);

                                BleManager.getInstance().notify(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f6-0000-1000-8000-00805f9b34fb", new BleNotifyCallback() {
                                    @Override
                                    public void onNotifySuccess() {
                                        LogUtil.e("onNotifySuccess===", "===");
                                    }

                                    @Override
                                    public void onNotifyFailure(BleException exception) {
                                        LogUtil.e("onNotifyFailure===", "==="+exception);

                                        String tvAgain = tv_againBtn.getText().toString().trim();
                                        int action_type;
                                        LogUtil.e("onNotifyFailure_1", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                                        if(isAgain){
                                            if("再次开锁".equals(tvAgain)){
                                                action_type=4;
                                            }else{
                                                action_type=2;
                                            }
                                        }else{
                                            if(isEndBtn){
                                                action_type=3;
                                            }else{
                                                action_type=1;
                                            }
                                        }
                                        car_notification(action_type, 3, 0, type+"===onNotifyFailure==="+exception);
                                    }

                                    @Override
                                    public void onCharacteristicChanged(byte[] data) {
                                        try{
                                            LogUtil.e("onCharacteristicC", loadingDialog.isShowing()+"===0");

                                            byte[] x = new byte[16];
                                            System.arraycopy(data, 0, x, 0, 16);

                                            byte[] mingwen = EncryptUtils.Decrypt(x, Config.key);    //060207FE02433001010606D41FC9553C  FE024330 01 01 06

                                            LogUtil.e("onCharacteristicC2", x.length+"==="+ ConvertUtils.bytes2HexString(data)+"==="+ConvertUtils.bytes2HexString(mingwen));

//                                            int i = 1/0;

                                            String s1 = ConvertUtils.bytes2HexString(mingwen);

                                            if(s1.startsWith("0602")){      //获取token

                                                token = s1.substring(6, 14);    //0602070C0580E001010406C8D6DC1949
                                                GlobalParameterUtils.getInstance().setToken(ConvertUtils.hexString2Bytes(token));

                                                String tvAgain = tv_againBtn.getText().toString().trim();

                                                LogUtil.e("token===", tvAgain+"==="+isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+token+"==="+s1);

                                                if(isAgain){
                                                    if("再次开锁".equals(tvAgain)){
                                                        openLock();
                                                    }
                                                }else{
                                                    if(isEndBtn){
                                                        getLockStatus();
                                                    }else if(isOpenLock){
                                                        openLock();
                                                    }
                                                }

                                            }else if(s1.startsWith("0502")){    //开锁
                                                LogUtil.e("openLock===", "==="+s1);

                                                if ("00".equals(s1.substring(6, 8))) {
                                                    lockStatus = 1;

//                                                  Toast.makeText(context, "开锁成功", Toast.LENGTH_LONG).show();

                                                    m_myHandler.sendEmptyMessage(7);
                                                }else{
                                                    Toast.makeText(context, "开锁失败", Toast.LENGTH_LONG).show();

                                                    car_notification(isAgain?4:1, 3, 0, type+"===开锁失败");
                                                }

                                            }else if(s1.startsWith("0508")){   //关锁==050801RET：RET取值如下：0x00，锁关闭成功。0x01，锁关闭失败。0x02，锁关闭异常。
                                                if ("00".equals(s1.substring(6, 8))) {
                                                    ToastUtil.showMessageApp(context,"关锁成功");
                                                    LogUtil.e("closeLock===suc", "==="+first3);

                                                } else {
                                                    ToastUtil.showMessageApp(context,"关锁失败");
                                                    LogUtil.e("closeLock===fail", "==="+first3);
                                                }
                                            }else if(s1.startsWith("050F")){   //锁状态
                                                LogUtil.e("lockState===0", isEndBtn+"==="+s1);

                                                isStop = true;
                                                isLookPsdBtn = true;

//                                              查询锁开关状态==050F:0x00表示开启状态；0x01表示关闭状态。
                                                if ("01".equals(s1.substring(6, 8))) {
                                                    ToastUtil.showMessageApp(context,"锁已关闭");
                                                    LogUtil.e("closeLock===1", "锁已关闭==="+first3);

                                                    m_myHandler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            getXinbiao();
                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        int n=0;
                                                                        major=0;
                                                                        LogUtil.e("closeLock===n0",major+"=="+n);
                                                                        while(major==0){

                                                                            Thread.sleep(500);
                                                                            n++;

                                                                            getXinbiao();

                                                                            LogUtil.e("closeLock===n","=="+n);

                                                                            if(n>=11) break;

                                                                        }

                                                                        LogUtil.e("closeLock===n2",major+"=="+n);
                                                                    } catch (InterruptedException e) {
                                                                        e.printStackTrace();
                                                                    }
//                                                                  m_myHandler.sendEmptyMessage(3);
                                                                }
                                                            }).start();
                                                        }
                                                    }, 2000);

                                                    if(isEndBtn){
                                                        LogUtil.e("closeLock===2", "锁已关闭==="+isEndBtn);

//                                                      m_myHandler.sendEmptyMessage(6);
                                                        car_notification(3, 1, 1, "");

                                                        LogUtil.e("closeLock===3", "锁已关闭===" + macList2.size());
                                                    }

                                                } else {
                                                    //锁已开启

                                                    if(isEndBtn){
                                                        open++;

                                                        if(open<2){
                                                            ToastUtil.showMessageApp(context,"车锁未关，请手动关锁");
                                                        }

                                                        car_notification(3, 5, 0, "");
                                                    }

//                                                  isEndBtn = false;
                                                }

                                            }else if(s1.startsWith("058502")){

                                                LogUtil.e("xinbiao===", "当前操作：搜索信标成功"+s1.substring(2*10, 2*10+2)+"==="+s1.substring(2*11, 2*11+2)+"==="+s1);

                                                if("000000000000".equals(s1.substring(2*4, 2*10))){
                                                    major = 0;
                                                }else{
                                                    major = 1;
                                                }
                                            }
                                        }catch(Exception e){
//                                            closeLoadingDialog();

                                            String tvAgain = tv_againBtn.getText().toString().trim();
                                            int action_type;
                                            LogUtil.e("onCharacteristicChanged_e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                                            if(isAgain){
                                                if("再次开锁".equals(tvAgain)){
                                                    action_type=4;
                                                    ToastUtil.showMessageApp(context,"开锁失败");
                                                }else{
                                                    action_type=2;
                                                    ToastUtil.showMessageApp(context,"关锁失败");
                                                }
                                            }else{
                                                if(isEndBtn){
                                                    action_type=3;
                                                    ToastUtil.showMessageApp(context,"还车失败");
                                                }else{
                                                    action_type=1;
                                                    ToastUtil.showMessageApp(context,"开锁失败");
                                                }
                                            }
                                            car_notification(action_type, 3, 0, type+"===onCharacteristicChanged==="+e);
                                        }


                                    }
                                });

                            }catch(Exception e){
//                                closeLoadingDialog();

                                String tvAgain = tv_againBtn.getText().toString().trim();
                                int action_type;
                                LogUtil.e("connect=onConnectSuc_e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                                if(isAgain){
                                    if("再次开锁".equals(tvAgain)){
                                        action_type=4;
                                        ToastUtil.showMessageApp(context,"开锁失败");
                                    }else{
                                        action_type=2;
                                        ToastUtil.showMessageApp(context,"关锁失败");
                                    }
                                }else{
                                    if(isEndBtn){
                                        action_type=3;
                                        ToastUtil.showMessageApp(context,"还车失败");
                                    }else{
                                        action_type=1;
                                        ToastUtil.showMessageApp(context,"开锁失败");
                                    }
                                }
                                car_notification(action_type, 3, 0, type+"===onConnectSuccess==="+e);
//                                LogUtil.e("connect===e", "==="+e);
                            }

                        }
                    });
                }

                @Override
                public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                    isLookPsdBtn = false;
                    LogUtil.e("connect=onDisConnected", status+"==="+isActiveDisConnected);

                    if(status!=0){
                        ToastUtil.showMessageApp(context,"蓝牙连接已断开");

//                        closeLoadingDialog();

                        if(!"3".equals(type)){
                            String tvAgain = tv_againBtn.getText().toString().trim();
                            int action_type;
                            LogUtil.e("connect=onDisConnected1", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                            if(isAgain){
                                if("再次开锁".equals(tvAgain)){
                                    action_type=4;
                                }else{
                                    action_type=2;
                                }
                            }else{
                                if(isEndBtn){
                                    action_type=3;
                                }else{
                                    action_type=1;
                                }
                            }
                            car_notification(action_type, 2, 0, type+"===onDisConnected==="+status);  //TODO
                        }
                    }



                }
            });
        }catch (Exception e){
//            closeLoadingDialog();

            String tvAgain = tv_againBtn.getText().toString().trim();
            int action_type;
            LogUtil.e("connect=e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

            if(isAgain){
                if("再次开锁".equals(tvAgain)){
                    action_type=4;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }else{
                    action_type=2;
                    ToastUtil.showMessageApp(context,"关锁失败");
                }
            }else{
                if(isEndBtn){
                    action_type=3;
                    ToastUtil.showMessageApp(context,"还车失败");
                }else{
                    action_type=1;
                    ToastUtil.showMessageApp(context,"开锁失败");
                }
            }
            car_notification(action_type, 2, 0, type+"===connect_e==="+e);
        }
    }

    private void getBleToken(){
        try{
            String s = new GetTokenTxOrder().generateString();  //06010101490E602E46311640422E5238

            LogUtil.e("getBleToken===1", "==="+s);  //1648395B

            byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.key);

//            int i = 1/0;

            BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb", bb, true, new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    LogUtil.e("getBleToken==onWriteSuc", current+"==="+total+"==="+ConvertUtils.bytes2HexString(justWrite));
                }

                @Override
                public void onWriteFailure(BleException e) {
                    LogUtil.e("getBleToken=onWriteFail", "==="+e);

                    if("3".equals(type)){
                        String tvAgain = tv_againBtn.getText().toString().trim();
                        if(isAgain){
                            if("再次开锁".equals(tvAgain)){
                                unlock();
                            }
                        }else{
                            if(isEndBtn){
                                car_notification(3, 3, 1, type+"===getBleToken_f==="+e);
                                Toast.makeText(context,"还车失败", Toast.LENGTH_LONG).show();
                            }else{
                                unlock();
                            }
                        }
                    }else{
                        String tvAgain = tv_againBtn.getText().toString().trim();
                        int action_type;
                        LogUtil.e("getBleToken===e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                        if(isAgain){
                            if("再次开锁".equals(tvAgain)){
                                action_type=4;
                                ToastUtil.showMessageApp(context,"开锁失败");
                            }else{
                                action_type=2;
                                ToastUtil.showMessageApp(context,"关锁失败");
                            }
                        }else{
                            if(isEndBtn){
                                action_type=3;
                                ToastUtil.showMessageApp(context,"还车失败");
                            }else{
                                action_type=1;
                                ToastUtil.showMessageApp(context,"开锁失败");
                            }
                        }
                        car_notification(action_type, 3, 0, type+"===getBleToken_f==="+e);
                    }

                }
            });
        }catch (Exception e){
            closeLoadingDialog();

            if("3".equals(type)){
                String tvAgain = tv_againBtn.getText().toString().trim();
                if(isAgain){
                    if("再次开锁".equals(tvAgain)){
                        unlock();
                    }
                }else{
                    if(isEndBtn){
                        car_notification(3, 3, 1, type+"===getBleToken_e==="+e);
                        Toast.makeText(context,"还车失败", Toast.LENGTH_LONG).show();
                    }else{
                        unlock();
                    }
                }
            }else{
                String tvAgain = tv_againBtn.getText().toString().trim();
                int action_type;
                LogUtil.e("getBleToken===e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                if(isAgain){
                    if("再次开锁".equals(tvAgain)){
                        action_type=4;
                        ToastUtil.showMessageApp(context,"开锁失败");
                    }else{
                        action_type=2;
                        ToastUtil.showMessageApp(context,"关锁失败");
                    }
                }else{
                    if(isEndBtn){
                        action_type=3;
                        ToastUtil.showMessageApp(context,"还车失败");
                    }else{
                        action_type=1;
                        ToastUtil.showMessageApp(context,"开锁失败");
                    }
                }
                car_notification(action_type, 3, 0, type+"===getBleToken_e==="+e);
            }

        }

    }

    private void getLockStatus(){
        try{
            String s = new GetLockStatusTxOrder().generateString();  //06010101490E602E46311640422E5238

            LogUtil.e("getLockStatus===1", "==="+isLookPsdBtn);  //1648395B

            byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.key);

//            int i = 1/0;

            BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb", bb, true, new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    LogUtil.e("getLockStatus==onWriteS", current+"==="+total+"==="+ConvertUtils.bytes2HexString(justWrite));
                }

                @Override
                public void onWriteFailure(BleException exception) {
                    LogUtil.e("getLockStatus=onWriteFa", "==="+exception);

                    ToastUtil.showMessageApp(context,"还车失败");
                    car_notification(3, 3, 1, type+"===getLockStatus_f==="+exception);
                }
            });
        }catch (Exception e){
//            closeLoadingDialog();

            if("3".equals(type)){
                String tvAgain = tv_againBtn.getText().toString().trim();
                if(isAgain){
                    if("再次开锁".equals(tvAgain)){
                        unlock();
                    }
                }else{
                    if(isEndBtn){
                        car_notification(3, 3, 1, type+"===getLockStatus_e==="+e);
                        Toast.makeText(context,"还车失败", Toast.LENGTH_LONG).show();
                    }else{
                        unlock();
                    }
                }
            }else{
                String tvAgain = tv_againBtn.getText().toString().trim();
                int action_type;
                LogUtil.e("getLockStatus===e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                if(isAgain){
                    if("再次开锁".equals(tvAgain)){
                        action_type=4;
                        ToastUtil.showMessageApp(context,"开锁失败");
                    }else{
                        action_type=2;
                        ToastUtil.showMessageApp(context,"关锁失败");
                    }
                }else{
                    if(isEndBtn){
                        action_type=3;
                        ToastUtil.showMessageApp(context,"还车失败");
                    }else{
                        action_type=1;
                        ToastUtil.showMessageApp(context,"开锁失败");
                    }
                }
                car_notification(action_type, 3, 0, type+"===getLockStatus_e==="+e);
            }

        }

    }

    private void getXinbiao(){
        try{
            String s = new XinbiaoTxOrder().generateString();  //06010101490E602E46311640422E5238

            LogUtil.e("getXinbiao===1", "==="+s);  //1648395B

            byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.key);

//            int i = 1/0;

            BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb", bb, true, new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    LogUtil.e("getXinbiao==onWriteS", current+"==="+total+"==="+ConvertUtils.bytes2HexString(justWrite));
                }

                @Override
                public void onWriteFailure(BleException e) {
                    LogUtil.e("getXinbiao=onWriteFa", "==="+e);

                    if("3".equals(type)){
                        String tvAgain = tv_againBtn.getText().toString().trim();
                        if(isAgain){
                            if("再次开锁".equals(tvAgain)){
                                unlock();
                            }
                        }else{
                            if(isEndBtn){
                                car_notification(3, 3, 1, type+"===getXinbiao_f==="+e);
                                Toast.makeText(context,"还车失败", Toast.LENGTH_LONG).show();
                            }else{
                                unlock();
                            }
                        }
                    }else{
                        String tvAgain = tv_againBtn.getText().toString().trim();
                        int action_type;
                        LogUtil.e("getXinbiao===e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                        if(isAgain){
                            if("再次开锁".equals(tvAgain)){
                                action_type=4;
                                ToastUtil.showMessageApp(context,"开锁失败");
                            }else{
                                action_type=2;
                                ToastUtil.showMessageApp(context,"关锁失败");
                            }
                        }else{
                            if(isEndBtn){
                                action_type=3;
                                ToastUtil.showMessageApp(context,"还车失败");
                            }else{
                                action_type=1;
                                ToastUtil.showMessageApp(context,"开锁失败");
                            }
                        }
                        car_notification(action_type, 3, 0, type+"===getXinbiao_f==="+e);
                    }
                }
            });
        }catch (Exception e){
//            closeLoadingDialog();

            if("3".equals(type)){
                String tvAgain = tv_againBtn.getText().toString().trim();
                if(isAgain){
                    if("再次开锁".equals(tvAgain)){
                        unlock();
                    }
                }else{
                    if(isEndBtn){
                        car_notification(3, 3, 1, type+"===getXinbiao_e==="+e);
                        Toast.makeText(context,"还车失败", Toast.LENGTH_LONG).show();
                    }else{
                        unlock();
                    }
                }
            }else{
                String tvAgain = tv_againBtn.getText().toString().trim();
                int action_type;
                LogUtil.e("getXinbiao===e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                if(isAgain){
                    if("再次开锁".equals(tvAgain)){
                        action_type=4;
                        ToastUtil.showMessageApp(context,"开锁失败");
                    }else{
                        action_type=2;
                        ToastUtil.showMessageApp(context,"关锁失败");
                    }
                }else{
                    if(isEndBtn){
                        action_type=3;
                        ToastUtil.showMessageApp(context,"还车失败");
                    }else{
                        action_type=1;
                        ToastUtil.showMessageApp(context,"开锁失败");
                    }
                }
                car_notification(action_type, 3, 0, type+"===getXinbiao_e==="+e);
            }

        }

    }

    private void openLock() {
        try{
            String s = new OpenLockTxOrder().generateString();

            LogUtil.e("openLock===1", token+"==="+s);     //989C064A===050106323031373135989C064A750217

            byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.key);

//            int i = 1/0;

            BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb", bb, true, new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    LogUtil.e("openLock===onWriteS", current+"==="+total+"==="+justWrite);
                }

                @Override
                public void onWriteFailure(BleException exception) {
                    LogUtil.e("openLock===onWriteF", "==="+exception);

                    ToastUtil.showMessageApp(context,"开锁失败");
                    car_notification(isAgain?4:1, 3, 0, type+"===openLock_f==="+exception);
                }
            });
        }catch (Exception e){
//            closeLoadingDialog();

            if("3".equals(type)){
                String tvAgain = tv_againBtn.getText().toString().trim();
                if(isAgain){
                    if("再次开锁".equals(tvAgain)){
                        unlock();
                    }
                }else{
                    if(isEndBtn){
                        car_notification(3, 3, 1, type+"===openLock_e==="+e);
                        Toast.makeText(context,"开锁失败", Toast.LENGTH_LONG).show();
                    }else{
                        unlock();
                    }
                }
            }else{
                String tvAgain = tv_againBtn.getText().toString().trim();
                int action_type;
                LogUtil.e("openLock===e", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                if(isAgain){
                    if("再次开锁".equals(tvAgain)){
                        action_type=4;
                        ToastUtil.showMessageApp(context,"开锁失败");
                    }else{
                        action_type=2;
                        ToastUtil.showMessageApp(context,"关锁失败");
                    }
                }else{
                    if(isEndBtn){
                        action_type=3;
                        ToastUtil.showMessageApp(context,"还车失败");
                    }else{
                        action_type=1;
                        ToastUtil.showMessageApp(context,"开锁失败");
                    }
                }
                car_notification(action_type, 3, 0, type+"===openLock_e==="+e);
            }

        }


    }

    protected void connect2() {
        LogUtil.e("connect===0", isLookPsdBtn+"==="+m_nowMac+"==="+Build.VERSION.SDK_INT);

        isLookPsdBtn = false;
        BaseApplication.getInstance().getIBLE().stopScan();
        m_myHandler.sendEmptyMessage(0x99);     //直连
//        BaseApplication.getInstance().getIBLE().startScan(new OnDeviceSearchListener() {
//            @Override
//            public void onScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
//
//                LogUtil.e("connect===", isLookPsdBtn+"==="+isStop+"==="+m_nowMac+"==="+device.getName()+"==="+device.getAddress());
//
//                if(isLookPsdBtn){
//                    LogUtil.e("connect===1", isLookPsdBtn+"==="+isStop+"==="+m_nowMac+"==="+device.getName()+"==="+device.getAddress());
//
//                    BaseApplication.getInstance().getIBLE().stopScan();
//                    m_myHandler.removeMessages(0x99);
//                    return;
//                }
//
//                if (device==null || TextUtils.isEmpty(device.getAddress())) return;
//
//                if (m_nowMac.equalsIgnoreCase(device.getAddress())){
//                    LogUtil.e("connect===2", m_nowMac+"==="+device.getName()+"==="+device.getAddress());
//
//                    BaseApplication.getInstance().getIBLE().stopScan();
//
//                    m_myHandler.removeMessages(0x99);
//                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, MainFragment.this);
////                    m_myHandler.sendEmptyMessage(0x99);
//                }
//            }
//        });
    }

    //type2、3
    @Override
    public void onTimeOut() {
        LogUtil.e("onTimeOut===", isLookPsdBtn+"==="+type+"==="+m_nowMac+"===");

//        closeLoadingDialog2();

        if(isLookPsdBtn){
            isLookPsdBtn = false;

            LogUtil.e("onTimeOut===1", isLookPsdBtn+"==="+type+"==="+m_nowMac+"===");
            return;
        }

        isLookPsdBtn = false;

        if("3".equals(type)){
            if(isOpenLock){
                unlock();
            }else{
                closeLoadingDialog();
            }
        }else{
            String tvAgain = tv_againBtn.getText().toString().trim();
            int action_type;
            LogUtil.e("mf===Xiaoan1", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

            if(isAgain){
                if("再次开锁".equals(tvAgain)){
                    action_type=4;
                }else{
                    action_type=2;
                }
            }else{
                if(isEndBtn){
                    action_type=3;
                }else{
                    action_type=1;
                }
            }
            car_notification(action_type, 2, 0, type+"===onTimeOut");
        }
    }

    //type2、3
    @Override
    public void onDisconnect(int state) {
        LogUtil.e("onDisconnect===", isLookPsdBtn+"==="+type+"==="+m_nowMac+"==="+state);

        m_myHandler.sendEmptyMessageDelayed(0, 1000);

        if(isLookPsdBtn){
            isLookPsdBtn = false;

            LogUtil.e("onDisconnect===1", isLookPsdBtn+"==="+type+"==="+m_nowMac+"==="+state);
            return;
        }

        isLookPsdBtn = false;

        if("3".equals(type)){
            if(isOpenLock){
                unlock();
            }else{
                closeLoadingDialog();
            }
        }else{
            String tvAgain = tv_againBtn.getText().toString().trim();
            int action_type;
            LogUtil.e("mf===Xiaoan1", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

            if(isAgain){
                if("再次开锁".equals(tvAgain)){
                    action_type=4;
                }else{
                    action_type=2;
                }
            }else{
                if(isEndBtn){
                    action_type=3;
                }else{
                    action_type=1;
                }
            }
            car_notification(action_type, 2, 0, type+"===onDisconnect==="+state);
        }
    }

    //type2、3
    @Override
    public void onServicesDiscovered(String name, String address) {
        LogUtil.e("onServicesDiscovered===", isLookPsdBtn+"==="+type+"==="+m_nowMac+"==="+name+"==="+address);

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

                            LogUtil.e("mf===TOKEN_ACTION", isLookPsdBtn+"===="+isOpenLock);


                            break;
                        case Config.BATTERY_ACTION:
                            LogUtil.e("mf===BATTERY_ACTION", isLookPsdBtn+"===="+isOpenLock+"===="+isEndBtn);

                            if (!TextUtils.isEmpty(data)) {
//                                quantity = String.valueOf(Integer.parseInt(data, 16));

                                if(isEndBtn){
                                    BaseApplication.getInstance().getIBLE().getLockStatus();
                                }else if(isOpenLock){
                                    BaseApplication.getInstance().getIBLE().openLock();

                                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                                        loadingDialog.setTitle("开锁中");
                                        loadingDialog.show();
                                    }
                                }
                            }else {
//                                quantity = "";
                            }
                            break;
                        case Config.OPEN_ACTION:
//                            closeLoadingDialog2();

                            LogUtil.e("mf===OPEN_ACTION0", TextUtils.isEmpty(data)+"==="+type+"==="+isAgain+"==="+isLookPsdBtn);

                            if (TextUtils.isEmpty(data)) {
                                ToastUtil.showMessageApp(context,"开锁失败,请重试");

                                if("3".equals(type)){
                                    if(isOpenLock){
                                        unlock();
                                    }else {
                                        closeLoadingDialog();
                                    }
                                }else{
                                    if(!isAgain){
                                        popupwindow.dismiss();
                                    }

                                    car_notification(1, 3, 0, type+"===OPEN_ACTION");
                                }
                            } else {
                                isOpen = true;

                                ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");
                                LogUtil.e("mf===", "OPEN_ACTION==="+isOpen);

                                m_myHandler.sendEmptyMessage(7);
                            }
                            break;
                        case Config.CLOSE_ACTION:
                            LogUtil.e("mf===", "CLOSE_ACTION===");

                            break;
                        case Config.RESET_ACTION:
                            LogUtil.e("mf===", "RESET_ACTION===");

                            break;
                        case Config.LOCK_STATUS_ACTION:
                            LogUtil.e("biking===", "LOCK_STATUS_ACTION==="+isStop);

                            isStop = true;
                            isLookPsdBtn = true;

                            closeLoadingDialog2();

                            if (TextUtils.isEmpty(data)) {
                                ToastUtil.showMessageApp(context,"锁已关闭");
                                LogUtil.e("biking===", "biking===锁已关闭==="+first3);

                                if(!isEndBtn) break;

                                m_myHandler.sendEmptyMessage(6);

                                LogUtil.e("biking===", "biking2===锁已关闭" + macList2.size());
                            } else {
                                //锁已开启
                                ToastUtil.showMessageApp(context,"车锁未关，请手动关锁");
                            }

                            break;
                        case Config.LOCK_RESULT:
                            break;
                    }
                }
            });
        }
    };

    private Handler m_myHandler = new Handler(new Handler.Callback() {
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

                    LogUtil.e("biking===3", type+"==="+macList.size()+"==="+isContainsList.contains(true));

//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }
//
//                    if (loadingDialog != null && !loadingDialog.isShowing()){
//                        loadingDialog.setTitle("正在还车");
//                        loadingDialog.show();
//                    }

                    clearRoute();

                    if(macList.size()>0 || isContainsList.contains(true)){
                        isTemp = false;

                        if("2".equals(type) || "9".equals(type) || "10".equals(type)){
                            endBtn();
                        }else if("3".equals(type)){
                            endBtn3();
                        }else if("4".equals(type) || "8".equals(type)){
                            endBtn4();
                        }else if("5".equals(type) || "6".equals(type)){
                            endBtn5();
                        }else if("7".equals(type)){
                            endBtn7();
                        }else if("11".equals(type)){
                            endBtn11();
                        }

                    }else{
                        minPolygon();
                        closeLoadingDialog();
                    }


                    break;
                case 4:
                    //蓝牙还车成功
                    car_authority();

                    break;
                case 6:
                    //蓝牙还车成功
                    car_notification(3, 1, 1, "");

                    break;

                case 7:
                    //蓝牙开锁成功
                    car_notification(isAgain?4:1, 1, 0, "");

                    break;


                case 9:
                    break;

                case 0x98://搜索超时

                    LogUtil.e("0x98===", loadingDialog+"==="+isLookPsdBtn+"==="+isAgain+"==="+isOpenLock+"==="+isEndBtn);

//                    ClientManager.getClient().stopSearch();
//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);
//                    ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
//                    ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);




                    if(!isLookPsdBtn){
//                        SearchRequest request = new SearchRequest.Builder()      //duration为0时无限扫描
//                                .searchBluetoothLeDevice(0)
//                                .build();
//
//
//                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                            LogUtil.e("usecar===1", "===");
//
//                            break;
//                        }
//
//
//                        if(isEndBtn){
//                            ClientManager.getClient().search(request, mSearchResponse3);
//                        }else{
//
//                            ClientManager.getClient().search(request, mSearchResponse);
//                        }

                        connectDeviceLP();
                        ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
                        ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);
                    }else{
                        if(isEndBtn){
                            queryOpenState();
                        }else{
                            getStateLP();
                        }
                    }

//                    m_myHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (!isStop){
//                                closeLoadingDialog();
//
//                                Toast.makeText(context,"蓝牙连接失败，重启软件试试吧！",Toast.LENGTH_LONG).show();
//
//                                ClientManager.getClient().stopSearch();
//                                ClientManager.getClient().disconnect(m_nowMac);
//                                ClientManager.getClient().disconnect(m_nowMac);
//                                ClientManager.getClient().disconnect(m_nowMac);
//                                ClientManager.getClient().disconnect(m_nowMac);
//                                ClientManager.getClient().disconnect(m_nowMac);
//                                ClientManager.getClient().disconnect(m_nowMac);
//                                ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);
//
//                                if(popupwindow!=null){
//                                    popupwindow.dismiss();
//                                }
//
//                                car_notification(isOpenLock?1:isAgain?2:isEndBtn?3:0, 2, 0);
//
//                            }
//                        }
//                    }, 10 * 1000);
                    break;

                case 0x99://搜索超时
                    LogUtil.e("0x99===", m_nowMac+"==="+isStop);

                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, MainFragment.this);
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.e("0x99===timeout0", isLookPsdBtn+"==="+isStop+"==="+type);

                            BaseApplication.getInstance().getIBLE().startScan(new OnDeviceSearchListener() {
                                @Override
                                public void onScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {

                                    LogUtil.e("connect===", isLookPsdBtn+"==="+isStop+"==="+m_nowMac+"==="+device.getName()+"==="+device.getAddress());

                                    if(isLookPsdBtn){
                                        LogUtil.e("connect===1", isLookPsdBtn+"==="+isStop+"==="+m_nowMac+"==="+device.getName()+"==="+device.getAddress());

                                        BaseApplication.getInstance().getIBLE().stopScan();
                                        m_myHandler.removeMessages(0x99);
                                        return;
                                    }

                                    if (device==null || TextUtils.isEmpty(device.getAddress())) return;

                                    if (m_nowMac.equalsIgnoreCase(device.getAddress())){
                                        LogUtil.e("connect===2", m_nowMac+"==="+device.getName()+"==="+device.getAddress());

                                        BaseApplication.getInstance().getIBLE().stopScan();

                                        m_myHandler.removeMessages(0x99);
                                        BaseApplication.getInstance().getIBLE().connect(m_nowMac, MainFragment.this);
                                    }
                                }
                            });

                            m_myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    LogUtil.e("0x99===timeout0", isLookPsdBtn+"==="+isStop+"==="+type);



                                    if (!isLookPsdBtn){

                                        if(!isOpenLock){

                                        }
                                        Toast.makeText(context,"唤醒失败，重启手机蓝牙试试吧！",Toast.LENGTH_LONG).show();

                                        BaseApplication.getInstance().getIBLE().stopScan();
                                        BaseApplication.getInstance().getIBLE().refreshCache();
                                        BaseApplication.getInstance().getIBLE().close();
                                        BaseApplication.getInstance().getIBLE().disconnect();

                                        LogUtil.e("0x99===timeout", isLookPsdBtn+"==="+isStop+"==="+type);

                                        if("3".equals(type)){
                                            if(isOpenLock){
                                                unlock();
                                            }else {
                                                closeLoadingDialog();
                                            }
                                        }else{
                                            String tvAgain = tv_againBtn.getText().toString().trim();
                                            int action_type;
                                            LogUtil.e("mf===Xiaoan1", isAgain+"==="+isEndBtn+"==="+isOpenLock+"==="+tvAgain);

                                            if(isAgain){
                                                if("再次开锁".equals(tvAgain)){
                                                    action_type=4;
                                                }else{
                                                    action_type=2;
                                                }
                                            }else{
                                                if(isEndBtn){
                                                    action_type=3;
                                                }else{
                                                    action_type=1;
                                                }
                                            }
                                            car_notification(action_type, 2, 0, type+"===0x99===timeout");
                                        }

                                    }else{
                                        closeLoadingDialog2();
                                    }
                                }
                            }, timeout);
                        }
                    }, timeout);

                    break;

                default:
                    break;
            }
            return false;
        }
    });



    public void clearRoute(){
        LogUtil.e("clearRoute===", isLookPsdBtn+"===");


        isMin = false;
        tv_authBtn.setText("");
        if(routeOverLay != null){
            routeOverLay.removeFromMap();
        }
    }

    //蓝牙锁type2
    public void endBtn(){
        LogUtil.e("biking===endBtn_0", isLookPsdBtn+"==="+force_backcar+"==="+isTwo+"==="+macList.size()+"==="+isContainsList.contains(true));

        if(force_backcar==1 && isTwo){
            ToastUtil.showMessageApp(context,"锁已关闭");
            LogUtil.e("biking===", "endBtn===锁已关闭==="+first3);

            if(!isEndBtn) return;

            m_myHandler.sendEmptyMessage(6);

            return;
        }

        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context,LoginActivity.class);
        }else {
            LogUtil.e("biking===endBtn", macList.size()+"==="+isContainsList.contains(true)+"==="+type+"==="+isContainsList);

//            clearRoute();

            if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                    scrollToFinishActivity();
            }

            if (!BaseApplication.getInstance().getIBLE().isEnable()){
                BaseApplication.getInstance().getIBLE().enableBluetooth();
                return;
            }

            m_myHandler2.postDelayed(new Runnable() {
                @Override
                public void run() {

                    LogUtil.e("endBtn===2_timeout", type + "===" + state + "===" + lockStatus + "===" + remark);

                    if(lockStatus==2 || lockStatus==3){
                        ToastUtil.showMessageApp(context, "还车超时");
                        car_notification(3, lockStatus, 0, type+"===还车超时");
                    }else{
                        closeLoadingDialog();
                        endBle();
                    }

                }
            }, 20 * 1000);

            state = 0;
            lockStatus = 2;
            if (isLookPsdBtn){
//                if (loadingDialog != null && !loadingDialog.isShowing()){
//                    loadingDialog.setTitle("请稍等");
//                    loadingDialog.show();
//                }

                LogUtil.e("biking===endBtn_2",macList.size()+"==="+isLookPsdBtn+"==="+force_backcar);


                macList2 = new ArrayList<> (macList);
//              BaseApplication.getInstance().getIBLE().getLockStatus();
                getLockStatus();
            } else {
                LogUtil.e("biking===endBtn_3",macList.size()+"==="+isLookPsdBtn+"==="+force_backcar);

//                if (loadingDialog != null && !loadingDialog.isShowing()){
//                    loadingDialog.setTitle("正在连接");
//                    loadingDialog.show();
//                }

                isOpenLock = false;
                connect();
//                scan2();
            }

//            return;



        }
    }

    public void endBtn3(){
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            LogUtil.e("biking===endBtn3",isContainsList.contains(true)+"==="+isLookPsdBtn+"==="+macList.size()+"==="+type+"==="+first3);

//            clearRoute();


            if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                scrollToFinishActivity();
            }
            if (!BaseApplication.getInstance().getIBLE().isEnable()){
                BaseApplication.getInstance().getIBLE().enableBluetooth();
                return;
            }
            if (isLookPsdBtn){
//                if (loadingDialog != null && !loadingDialog.isShowing()){
//                    loadingDialog.setTitle("请稍等");
//                    loadingDialog.show();
//                }

                macList2 = new ArrayList<> (macList);
                getLockStatus();
            } else {
//                if (loadingDialog != null && !loadingDialog.isShowing()){
//                    loadingDialog.setTitle("正在连接");
//                    loadingDialog.show();
//                }

                isOpenLock = false;
                connect();

//                scan2();

            }

//            return;


//            if (macList.size() > 0){
//
//            }
//
//            if (isContainsList.contains(true)){
////                flag = 2;
//                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                    scrollToFinishActivity();
//                }
//                //蓝牙锁
//                if (!BaseApplication.getInstance().getIBLE().isEnable()){
//                    BaseApplication.getInstance().getIBLE().enableBluetooth();
//                    return;
//                }
//                if (isLookPsdBtn){
//                    if (loadingDialog != null && !loadingDialog.isShowing()){
//                        loadingDialog.setTitle("请稍等");
//                        loadingDialog.show();
//                    }
//
//                    macList2 = new ArrayList<> (macList);
//                    getLockStatus();
////                    BaseApplication.getInstance().getIBLE().getLockStatus();
//
////                    isLookPsdBtn = false;
////                    m_myHandler.postDelayed(new Runnable() {
////                        @Override
////                        public void run() {
////                            if (loadingDialog != null && loadingDialog.isShowing()){
////                                loadingDialog.dismiss();
////
////                                if(!isLookPsdBtn){
////                                    if(first3){
////                                        first3 = false;
////                                        customDialog2.show();
////
////                                        clickCountDeal();
////                                    }else{
//////                                        lock();     //TODO    2
////                                        ToastUtil.showMessageApp(context,"还车失败");
////                                        car_notification(3, 2, 1);
////                                    }
////                                }
////
////                            }
////                        }
////                    }, 10 * 1000);
//
//                }else {
//                    if (loadingDialog != null && !loadingDialog.isShowing()){
//                        loadingDialog.setTitle("正在连接");
//                        loadingDialog.show();
//                    }
//
//                    isOpenLock = false;
//                    connect();
//
////                    isLookPsdBtn = false;
////                    m_myHandler.postDelayed(new Runnable() {
////                        @Override
////                        public void run() {
////                            closeLoadingDialog();
////
////                            LogUtil.e("biking===endBtn3_2",isLookPsdBtn+"==="+type+"==="+first3);
////
////                            if(!isLookPsdBtn){
////                                stopScan = true;
//////                              BaseApplication.getInstance().getIBLE().stopScan();
//////                                BaseApplication.getInstance().getIBLE().refreshCache();
//////                                BaseApplication.getInstance().getIBLE().close();
//////                                BaseApplication.getInstance().getIBLE().disconnect();
////
////                                if(first3){
////                                    first3 = false;
////
////                                    customDialog2.show();
////
////                                    clickCountDeal();
////                                }else{
//////                                    lock();     //TODO    2
////                                    ToastUtil.showMessageApp(context,"还车失败");
////                                    car_notification(3, 2, 1);
////                                }
////                            }
////                        }
////                    }, 10 * 1000);
//
//                }
//            }else {
//                minPolygon();
//                closeLoadingDialog();
//
////                customDialog3.show();
//            }


        }
    }

    public void endBtn4(){
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            LogUtil.e("biking===endBtn4",macList.size()+"==="+isContainsList.contains(true)+"==="+type+"==="+first3);

//            clearRoute();

            if (macList.size() > 0){
                lock();

//                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                    popupwindow.dismiss();
//                }
//                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
//                mBluetoothAdapter = bluetoothManager.getAdapter();
//
//                BLEService.bluetoothAdapter = mBluetoothAdapter;
//
//                bleService.view = context;
//                bleService.showValue = true;
//
//                if (mBluetoothAdapter == null) {
//                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                    popupwindow.dismiss();
//                    return;
//                }
//                if (!mBluetoothAdapter.isEnabled()) {
//                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableBtIntent, 188);
//                } else {
//                    LogUtil.e("mf===4_1", bleid + "==="+m_nowMac);
//
//                    bleService.connect(m_nowMac);
//
//                    checkConnect2();
//                }

                return;
            }

            if (isContainsList.contains(true)){

                lock();

//                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                    popupwindow.dismiss();
//                }
//                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
//                mBluetoothAdapter = bluetoothManager.getAdapter();
//
//                BLEService.bluetoothAdapter = mBluetoothAdapter;
//
//                bleService.view = context;
//                bleService.showValue = true;
//
//                if (mBluetoothAdapter == null) {
//                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                    popupwindow.dismiss();
//                    return;
//                }
//                if (!mBluetoothAdapter.isEnabled()) {
//                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableBtIntent, 188);
//                } else {
//                    LogUtil.e("mf===4_2", bleid + "==="+m_nowMac);
//
//                    bleService.connect(m_nowMac);
//
//                    checkConnect2();
//                }

            }
//            else {
//                minPolygon();
//                closeLoadingDialog();
//
////                customDialog3.show();
//            }


        }
    }

    public void endBtn5(){
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context,LoginActivity.class);
        }else {
            LogUtil.e("biking===endBtn5",   macList.size()+"==="+isContainsList.contains(true)+"==="+type+"==="+m_nowMac);

//            clearRoute();

            if (macList.size() > 0){
                //泺平锁
                queryState();

                return;
            }

            if (isContainsList.contains(true)){
                queryState();

            }
//            else {
//                minPolygon();
//                closeLoadingDialog();
//
////                customDialog3.show();
//            }


        }
    }

    public void endBtn7(){
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            LogUtil.e("biking===endBtn7", loadingDialog.isShowing()+"==="+macList.size()+"==="+isContainsList.contains(true)+"==="+type+"==="+deviceuuid+"==="+isConnect);

//            clearRoute();


            if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                popupwindow.dismiss();
            }
            //蓝牙锁
            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

            mBluetoothAdapter = bluetoothManager.getAdapter();

            if (mBluetoothAdapter == null) {
                ToastUtil.showMessageApp(context, "获取蓝牙失败");
                popupwindow.dismiss();
                return;
            }
            if (!mBluetoothAdapter.isEnabled()) {
                isPermission = false;
                closeLoadingDialog2();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 188);
            } else {

                LogUtil.e("biking===endBtn7_2", "===" + isConnect + "===" + deviceuuid + "===" + apiClient);    //865532045872401

                if(apiClient==null){
                    XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                    builder.setBleStateChangeListener(MainFragment.this);
                    builder.setScanResultCallback(MainFragment.this);
                    apiClient = builder.build();
                }


                if(!isConnect){
//                  Looper.prepare();

                    MainFragmentPermissionsDispatcher.connectDeviceWithPermissionCheck(MainFragment.this, deviceuuid);
//                  Looper.loop();

                    isConnect = false;
                    m_myHandler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isConnect){

                                LogUtil.e("biking===endBtn7_3", "timeout==="+isConnect+"==="+loadingDialog.isShowing());

                                if (apiClient != null) {
                                    apiClient.disConnect();
                                    apiClient.onDestroy();
                                    apiClient=null;
                                }

                                lock();
                            }
                        }
                    }, timeout);
                }else{
                    xiaoanClose_blue();
                }

            }


        }
    }

    public void endBtn11(){
        try{
            final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
            if (access_token == null || "".equals(access_token)){
                ToastUtil.showMessageApp(context,"请先登录账号");
                UIHelper.goToAct(context, LoginActivity.class);
            }else {

                if(!TbitBle.hasInitialized()){
                    TbitBle.initialize(context, new SecretProtocolAdapter());
                }

                LogUtil.e("biking===endBtn11", loadingDialog.isShowing()+"==="+TbitBle.getBleConnectionState()+"==="+macList.size()+"==="+isContainsList.contains(true)+"==="+type+"==="+deviceuuid+"==="+isConnect);

//              clearRoute();

                if(TbitBle.getBleConnectionState()==0){
                    lock();

                    if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                        ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                        popupwindow.dismiss();
                    }
                    //蓝牙锁
                    BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

                    mBluetoothAdapter = bluetoothManager.getAdapter();

                    if (mBluetoothAdapter == null) {
                        ToastUtil.showMessageApp(context, "获取蓝牙失败");
                        popupwindow.dismiss();
                        return;
                    }
                    if (!mBluetoothAdapter.isEnabled()) {
                        isPermission = false;
                        closeLoadingDialog2();
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, 188);
                    } else {

                        LogUtil.e("biking===endBtn11_2", TbitBle.getBleConnectionState()+"===" + isConnect + "===" + deviceuuid + "===" + bleid);    //865532045872401

                        tbtble_connect2();
                    }
                }else{
                    if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                        ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                        popupwindow.dismiss();
                    }
                    //蓝牙锁
                    BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

                    mBluetoothAdapter = bluetoothManager.getAdapter();

                    if (mBluetoothAdapter == null) {
                        ToastUtil.showMessageApp(context, "获取蓝牙失败");
                        popupwindow.dismiss();
                        return;
                    }
                    if (!mBluetoothAdapter.isEnabled()) {
                        isPermission = false;
                        closeLoadingDialog2();
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, 188);
                    } else {

                        LogUtil.e("biking===endBtn11_2", TbitBle.getBleConnectionState()+"===" + isConnect + "===" + deviceuuid + "===" + bleid);    //865532045872401

                        tbtble_lock();
                    }
                }

            }
        }catch (Exception e){
            closeLoadingDialog();
            LogUtil.e("mf===endBtn11_e", "===" + e);    //865532045872401
        }

    }

    public LatLng minPolygon() {
        isMin = true;
        rl_authBtn.setVisibility(View.VISIBLE);
        tv_authBtn.setText(tipRange);

        car_notification(3, 4, 0, "");

        double s=0;
        double s1=0;

        double x = 0;
        double y = 0;

        for (int i = 0; i < centerList.size(); i++) {

//            LogUtil.e("minPolygon===", centerList.get(i).latitude+"==="+referLatitude+">>>"+centerList.get(i).longitude+"==="+referLongitude);

            s1 = (centerList.get(i).latitude-referLatitude)*(centerList.get(i).latitude-referLatitude) + (centerList.get(i).longitude-referLongitude)*(centerList.get(i).longitude-referLongitude);

            if(i==0 || s1<s){
                s = s1;

                x = centerList.get(i).longitude;
                y = centerList.get(i).latitude;
            }else{
                continue;
            }
        }

//        PolylineOptions pOption = new PolylineOptions();
//
////        pOption.setDottedLine(true);
////        pOption.setDottedLineType(1);
////        pOption.width(20);
////        pOption.color(Color.argb(255, 0, 0, 255));
////        pOption.useGradient(true);
//        pOption.add(new LatLng(referLatitude, referLongitude));
//        pOption.add(new LatLng(y, x));
//
//        pOption.width(40f);
//        pOption.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.grasp_trace_line));
//
//
//
//
//        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(18f);// 设置缩放监听
//        aMap.moveCamera(cameraUpdate);
//
//        polyline = aMap.addPolyline(pOption);

        markerPosition = new LatLng(y, x);

        isNavi = false;
        mAMapNavi.calculateWalkRoute(new NaviLatLng(referLatitude, referLongitude), new NaviLatLng(y, x));

        LogUtil.e("minPolygon===2", "==="+s);

        return null;
    }

    public void clickCountDeal(){
        if("2".equals(type) || "9".equals(type) || "10".equals(type)){
            if(force_backcar==1){
                isTwo = true;
                customDialog9.show();
            }
        }

    }

    public void queryState(){
        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
        }
        //蓝牙锁
        BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            ToastUtil.showMessageApp(context, "获取蓝牙失败");
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            LogUtil.e("queryState===1", "===");
            isPermission = false;
            closeLoadingDialog2();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 188);
        } else {
            LogUtil.e("queryState===2", "==="+isLookPsdBtn);

            m_myHandler2.postDelayed(new Runnable() {
                @Override
                public void run() {

                    LogUtil.e("queryState===5_timeout", type + "===" + state + "===" + lockStatus + "===" + remark);

                    if(lockStatus==2 || lockStatus==3){
                        getBleRecord();
                        ToastUtil.showMessageApp(context, "还车超时");
                        car_notification(3, lockStatus, 0, type+"===还车超时");
                    }else{
                        closeLoadingDialog();
                    }

                }
            }, 20 * 1000);

            state = 0;
            lockStatus = 2;
            m_myHandler.sendEmptyMessage(0x98);


        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        LogUtil.e("onMapLongClick===", pOptions.size() + "===" + point.latitude+"===" + point.longitude+"===" + point);

        for ( int i = 0; i < pOptions.size(); i++){

//            isContainsList.add(pOptions.get(i).contains(new LatLng(Double.parseDouble(bean.getLatitude()), Double.parseDouble(bean.getLongitude()))));

            if(pOptions.get(i).contains(point)){
                LogUtil.e("onMapLongClick===2", "==="+pOptions.get(i).getPoints());
            }

//            LogUtil.e("onMapClick===1", pOptions.get(i)+"==="+pOptions.get(i).contains(point));
//            LogUtil.e("onMapClick===1", pOptions.get(i).contains(point)+"===");

//            isContainsList.add(pOptions.get(i).contains(point));

        }
    }

    @Override
    public void onMapClick(LatLng point) {
//        LogUtil.e("onMapClick===", ll_top.isShown()+"===" + routeOverLay+"===" + ll_top_navi+"===" + point.latitude+"===" + point.longitude+"===" + point);
        LogUtil.e("onMapClick===", pOptions.size() + "===" + point.latitude+"===" + point.longitude+"===" + point);

        for ( int i = 0; i < pOptions.size(); i++){

//            isContainsList.add(pOptions.get(i).contains(new LatLng(Double.parseDouble(bean.getLatitude()), Double.parseDouble(bean.getLongitude()))));

            if(pOptions.get(i).contains(point)){
                LogUtil.e("onMapClick===2", "==="+pOptions.get(i).getPoints());
            }

//            LogUtil.e("onMapClick===1", pOptions.get(i)+"==="+pOptions.get(i).contains(point));
//            LogUtil.e("onMapClick===1", pOptions.get(i).contains(point)+"===");

//            isContainsList.add(pOptions.get(i).contains(point));

        }



        if(!ll_top.isShown()){
            if(routeOverLay!=null){
                routeOverLay.removeFromMap();
            }

            ll_top.setVisibility(View.VISIBLE);
            ll_top_navi.setVisibility(View.GONE);
            isNavi = false;
        }
    }

    @Override
    public void onInitNaviFailure() { }

    @Override
    public void onInitNaviSuccess() {
        LogUtil.e("onInitNaviSuccess===", "===");
    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
//        if(isHidden) return;

        LogUtil.e("onCalculateRouteSuc=", "==="+routeOverLay);

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

//        if(!bikeFragment.isHidden()){
//            if (bikeFragment.myLocation != null) {
//                CameraUpdate update = CameraUpdateFactory.changeLatLng(bikeFragment.myLocation);
//                aMap.animateCamera(update);
//            }
//        }else{
//            if (ebikeFragment.myLocation != null) {
//                CameraUpdate update = CameraUpdateFactory.changeLatLng(ebikeFragment.myLocation);
//                aMap.animateCamera(update);
//            }
//        }

//        calculateSuccess = true;
        aMap.moveCamera(CameraUpdateFactory.changeTilt(0));
        //路径绘制图层
        routeOverLay = new RouteOverLay(aMap, path, context);
        routeOverLay.setTrafficLine(false);
        routeOverLay.setStartPointBitmap(null);
        routeOverLay.setEndPointBitmap(null);
        routeOverLay.addToMap();

        LogUtil.e("drawRoutes===", "==="+path.getAllLength());

        tv_navi_distance.setText(path.getAllLength()+"米");

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//存放所有点的经纬度

//                for(int i=0;i<markers.size();i++){
//                    boundsBuilder.include(markers.get(i).getPosition());//把所有点都include进去（LatLng类型）
//                }

        boundsBuilder.include(new LatLng(referLatitude, referLongitude));
        boundsBuilder.include(markerPosition);

//                aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 15));
//                aMap.animateCamera(CameraUpdateFactory.newLatLngBoundsRect(boundsBuilder.build(),10,10,10,10), 0L, null);

        aMap.moveCamera(CameraUpdateFactory.newLatLngBoundsRect(boundsBuilder.build(),100,100,230 * Math.round(BaseApplication.density),100 * Math.round(BaseApplication.density)));    //left_right_padding, left_right_padding, top_padding, bottom_padding


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

                LogUtil.e("mf===requestCode", requestCode+"==="+resultCode);

//                closeLoadingDialog2();

                switch (requestCode) {

                    case 1:
                        if (resultCode == RESULT_OK) {
                            codenum = data.getStringExtra("codenum");
                            m_nowMac = data.getStringExtra("m_nowMac");
                            carmodel_id = data.getIntExtra("carmodel_id", 1);
                            type = data.getStringExtra("type");
                            lock_no = data.getStringExtra("lock_no");
                            bleid = data.getStringExtra("bleid");
                            deviceuuid = data.getStringExtra("deviceuuid");
                            electricity = data.getStringExtra("electricity");
                            mileage = data.getStringExtra("mileage");
                            carmodel_name = data.getStringExtra("carmodel_name");
                            each_free_time = data.getStringExtra("each_free_time");
                            first_price = data.getStringExtra("first_price");
                            first_time = data.getStringExtra("first_time");
                            continued_price = data.getStringExtra("continued_price");
                            continued_time = data.getStringExtra("continued_time");
                            credit_score_desc = data.getStringExtra("credit_score_desc");
                            isMac = data.getBooleanExtra("isMac", false);

                            LogUtil.e("mf===requestCode1", isMac+"==="+codenum+"==="+carmodel_id+"==="+type+"==="+bleid +"==="+deviceuuid+"==="+carmodel_name+"==="+each_free_time);

                            initmPopupRentWindowView();

                        } else {
                            Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                        }

                        LogUtil.e("requestCode===1", "==="+resultCode);
                        break;

                    case 11:
                        car_authority();
                        break;

                    case PRIVATE_CODE:
                        openGPSSettings();
                        break;

                    case 188:

                        if (resultCode == RESULT_OK) {
//                            closeLoadingDialog();

                            isPermission = true;

//                            if(!"".equals(oid) && !"".equals(type)){
//                            if(!"".equals(oid)){
//
//                            }else{
//                                closeLoadingDialog2();
//                                break;
//                            }

                            if (loadingDialog != null && !loadingDialog.isShowing()) {
                                loadingDialog.setTitle("正在唤醒车锁");
                                loadingDialog.show();
                            }

                            String tvAgain = tv_againBtn.getText().toString().trim();

                            LogUtil.e("188===", isAgain+"==="+tvAgain+"==="+unauthorized_code+"==="+isConnect+"==="+isLookPsdBtn+"==="+oid+"==="+m_nowMac+"==="+type+">>>"+isOpenLock+"==="+isEndBtn);


                            if(isAgain){
                                if("再次开锁".equals(tvAgain)){
                                    openAgain();
                                }else{
                                    closeAgain();
                                }
                            }else{
                                if(isEndBtn){
                                    cycling4();
                                }else{
                                    isStop = false;
                                    isOpen = false;
                                    isFinish = false;
                                    n = 0;
                                    cn = 0;
                                    force_backcar = 0;
                                    isTwo = false;
                                    first3 = true;
                                    flagm = 0;
                                    isFrist1 = true;
                                    stopScan = false;
                                    clickCount = 0;
                                    tz = 0;
                                    transtype = "";
                                    major = 0;
                                    minor = 0;
                                    isGPS_Lo = false;
                                    scan = false;
                                    isTemp = false;
                                    backType = "";
                                    isOpenLock = false;
                                    isConnect = false;
                                    isLookPsdBtn = false;
                                    isEndBtn = false;
                                    isAgain = false;
                                    order_type = 0;
                                    isWaitEbikeInfo = true;
                                    ebikeInfoThread = null;
                                    oid = "";
                                    open = 0;
                                    isBleInit = false;
                                    loopTime = 1 * 1000;

                                    if ("2".equals(type) || "3".equals(type) || "9".equals(type) || "10".equals(type)){

                                        LogUtil.e("mf===requestCode2", codenum+"==="+type);

//                                      closeBroadcast();     //TODO    3
//                                      activity.registerReceiver(broadcastReceiver, Config.initFilter());
//                                      GlobalParameterUtils.getInstance().setLockType(LockType.MTS);

                                        bleInit();

                                    }else if("4".equals(type) || "8".equals(type)){

//                                      BLEService.bluetoothAdapter = mBluetoothAdapter;
//                                      bleService.view = context;
//                                      bleService.showValue = true;
                                    }else if ("5".equals(type)  || "6".equals(type)) {
                                        LogUtil.e("initView===5", "==="+isLookPsdBtn);

//                                      ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
//                                      ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);
                                    }else if ("7".equals(type)) {
                                    }else if ("11".equals(type)) {
                                    }

                                    SharedPreferencesUrls.getInstance().putString("tempStat", "0");
                                    if (carmodel_id==2) {
                                        tv_againBtn.setText("临时上锁");
                                    }else{
                                        tv_againBtn.setText("再次开锁");
                                    }

                                    refreshLayout.setVisibility(View.VISIBLE);

                                    LogUtil.e("188===order", isAgain+"==="+isLookPsdBtn+"==="+oid+"==="+m_nowMac+"==="+type+">>>"+isOpenLock+"==="+isEndBtn);


                                    order();
                                }
                            }

                        }else{
                            ToastUtil.showMessageApp(context, "需要打开蓝牙");

                            LogUtil.e("188===fail", oid+"===");

                            if(popupwindow!=null){
                                popupwindow.dismiss();
                            }

                            closeLoadingDialog2();


                            if("".equals(oid)){
//                              scrollToFinishActivity();
                            }else{
//                                oid = "";
//                                type = "";
//                                car_notification(1, 2, 0);
                            }
                        }
                        break;

                    case 189:
                        LogUtil.e("189===", oid+"===");

                        bleInit();

                        setScanRule();
                        scan();
                        break;

                    default:
                        break;

                }
            }
        });
    }

    private void ss(){
        String tvAgain = tv_againBtn.getText().toString().trim();
        if(isAgain){
            if("再次开锁".equals(tvAgain)){
                xiaoanOpen_blue();
            }else{
                xiaoanClose_blue();
            }
        }else{
            if(isEndBtn){
                xiaoanClose_blue();
            }else{
                xiaoanOpen_blue();
            }
        }
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
