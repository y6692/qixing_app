package cn.qimate.bike.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
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
import com.autonavi.ae.pos.Feedback;
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
import com.sofi.blelocker.library.protocol.ITemporaryActionResponse;
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
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.ActionCenterActivity;
import cn.qimate.bike.activity.CarFaultActivity;
import cn.qimate.bike.activity.ClientManager;
import cn.qimate.bike.activity.CurRoadBikedActivity;
import cn.qimate.bike.activity.CurRoadStartActivity;
import cn.qimate.bike.activity.EndBikeFeedBackActivity;
import cn.qimate.bike.activity.FeedbackActivity;
import cn.qimate.bike.activity.LoginActivity;
import cn.qimate.bike.activity.MyOrderDetailActivity;
import cn.qimate.bike.activity.PersonAlterActivity;
import cn.qimate.bike.activity.RealNameAuthActivity;
import cn.qimate.bike.activity.ServiceCenter0Activity;
import cn.qimate.bike.activity.ServiceCenterActivity;
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
import cn.qimate.bike.lock.utils.ToastUtils;
import cn.qimate.bike.model.BannerBean;
import cn.qimate.bike.model.CarAuthorityBean;
import cn.qimate.bike.model.CarBean;
import cn.qimate.bike.model.CurRoadBikingBean;
import cn.qimate.bike.model.KeyBean;
import cn.qimate.bike.model.OrderBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.TabTopEntity;
import cn.qimate.bike.util.ByteUtil;
import cn.qimate.bike.util.HtmlTagHandler;
import cn.qimate.bike.util.IoBuffer;
import cn.qimate.bike.util.ToastUtil;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;
import cn.qimate.bike.view.RoundImageView;
import permissions.dispatcher.NeedsPermission;

import static android.app.Activity.RESULT_OK;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.sofi.blelocker.library.Constants.STATUS_CONNECTED;

@SuppressLint("NewApi")
public class MainFragment extends BaseFragment implements View.OnClickListener, OnBannerListener, OnConnectionListener, BleStateChangeListener, ScanResultCallback, AMap.OnMapClickListener, AMapNaviListener {

    private View v;
    Unbinder unbinder;

    private static MainFragment mainFragment;

    private final static int SCANNIN_GREQUEST_CODE = 1;
    static private final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private static final int PRIVATE_CODE = 1315;//开启GPS权限

    private static final String TAG = MainFragment.class.getSimpleName();

    BluetoothAdapter mBluetoothAdapter;
    private XiaoanBleApiClient apiClient;

    public static String codenum = "";
    private String m_nowMac = "";
    private String type = "";
    private String lock_no = "";
    private String bleid = "";
    private String deviceuuid = "";
    private String price = "";
    private String electricity = "";
    private String mileage = "";

    private String keySource = "";
    //密钥索引
    int encryptionKey= 0;
    //开锁密钥
    String keys = null;
    //服务器时间戳，精确到秒，用于锁同步时间
    long serverTime;
    private String tel = "13188888888";

    private LinearLayout ll_payBtn, scanLock, myCommissionLayout, myLocationLayout, linkLayout, rl_ad, ll_top, ll_top_navi, refreshLayout, slideLayout,
            ll_top_biking, ll_biking_openAgain, ll_biking_endBtn, ll_biking_errorEnd, ll_estimated_cost, ll_electricity, ll_bike, ll_ebike,
            ll_top_pay;

    private RelativeLayout rl_authBtn;
    private TextView tv_authBtn, tv_navi_name, tv_navi_distance,
            tv_biking_codenum, tv_againBtn, tv_estimated_cost, tv_car_start_time, tv_car_start_time2, tv_estimated_cost2, tv_car_mileage, tv_car_electricity,
            tv_pay_codenum, tv_order_amount, tv_pay_car_start_time, tv_pay_car_end_time, tv_payBtn;

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
    private Dialog advDialog;
    private TextView advAgainBtn;
    private TextView advCloseBtn;

    private AMapNavi mAMapNavi;
    private RouteOverLay routeOverLay;

    PopupWindow popupwindow;

    LocationManager locationManager;
    String provider = LocationManager.GPS_PROVIDER;

    private ScanManager scanManager;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;

    private List<Object> macList;
    private List<Object> macList2;
    public List<LatLng> centerList = new ArrayList<LatLng>();

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
    int tz = 0;
    String transtype = "";
    int major = 0;
    int minor = 0;
    private boolean isGPS_Lo = false;
    private boolean scan = false;
    private boolean isTemp = false;
    private boolean isLookPsdBtn = false;
    private boolean isAgain = false;
    private String backType = "";
    private boolean isOpenLock = false;
    private int order_type = 1;
    private boolean isWaitEbikeInfo = true;
    private Thread ebikeInfoThread;

    private boolean first = true;



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

    }

    public void car_authority() {
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");

        Log.e("mf===car_authority", "==="+access_token);

        if (access_token == null || "".equals(access_token)) {
            ToastUtil.showMessageApp(context, "请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        } else {
            HttpHelper.get(context, Urls.car_authority, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在加载");
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    onFailureCommon("mf===car_authority", throwable.toString());
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        Log.e("mf===car_authority1", "==="+responseString);

                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                        CarAuthorityBean bean = JSON.parseObject(result.getData(), CarAuthorityBean.class);

                        SharedPreferencesUrls.getInstance().putString("iscert", ""+bean.getUnauthorized_code());

                        Log.e("mf===car_authority2", bean.getUnauthorized_code()+"==="+bean.getOrder());
//                        Log.e("mf===car_authority2", bean.getUnauthorized_code()+"==="+bean.getOrder()+"==="+new JSONObject(bean.getOrder()).getInt("order_id"));


                        int unauthorized_code = bean.getUnauthorized_code();

//                      未授权码 0（有权限时为0）1需要登录 2未认证 3认证中 4认证被驳回 5需要充值余额或购买骑行卡 6有进行中行程 7有待支付行程 8有待支付调度费 9有待支付赔偿费
                        unauthorized_code = bean.getUnauthorized_code();

                        rl_ad.setVisibility(View.VISIBLE);
                        ll_top_biking.setVisibility(View.GONE);
                        ll_top_pay.setVisibility(View.GONE);
                        tv_authBtn.setText("");

                        if(unauthorized_code==1) {
                            tv_authBtn.setText("您还未登录，点我快速登录");
                        }else if(unauthorized_code==2) {
                            tv_authBtn.setText("您还未认证，点我快速认证");
                        }else if(unauthorized_code==3) {
                            tv_authBtn.setText("认证审核中");
                        }else if(unauthorized_code==4) {
                            tv_authBtn.setText("认证被驳回，请重新认证");
                        }else if(unauthorized_code==5) {
                            tv_authBtn.setText("需要充值余额或购买骑行卡");   //TODO
                        }else if(unauthorized_code==6) {
                            ll_top_navi.setVisibility(View.GONE);
                            ll_top.setVisibility(View.VISIBLE);
                            rl_ad.setVisibility(View.GONE);
                            ll_top_biking.setVisibility(View.VISIBLE);

                            order_id = new JSONObject(bean.getOrder()).getInt("order_id");

                            cycling();
                            cyclingThread();
                        }else if(unauthorized_code>=7){
                            ll_top_biking.setVisibility(View.GONE);
                            ll_top_navi.setVisibility(View.GONE);
                            ll_top.setVisibility(View.VISIBLE);
                            rl_ad.setVisibility(View.GONE);
                            ll_top_pay.setVisibility(View.VISIBLE);

                            if(unauthorized_code==7){
                                order_type = 1;
                                tv_payBtn.setText("骑行支付");

                                cycling2();
                            }else if(unauthorized_code==8 || unauthorized_code==9){
                                order_type = 3;

                                JSONObject jsonObject = new JSONObject(bean.getOrder());

                                tv_pay_codenum.setText(jsonObject.getString("car_number"));
                                tv_pay_car_start_time.setText(jsonObject.getString("car_start_time"));
                                tv_pay_car_end_time.setText(jsonObject.getString("car_end_time"));
                                tv_order_amount.setText(jsonObject.getString("order_amount"));

                                if(unauthorized_code==8){
                                    tv_payBtn.setText("调度费支付");
                                }else{
                                    tv_payBtn.setText("赔偿费支付");
                                }
                            }
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
    }

    private void cyclingThread() {

        Log.e("cyclingThread===", "==="+ebikeInfoThread);

        if (ebikeInfoThread == null) {
            Runnable ebikeInfoRunnable = new Runnable() {
                @Override
                public void run() {
                    while (isWaitEbikeInfo) {

                        m_myHandler.sendEmptyMessage(4);

                        try {
                            Thread.sleep(30 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            };

            ebikeInfoThread = new Thread(ebikeInfoRunnable);
            ebikeInfoThread.start();
        }
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
                onFailureCommon("mf===cycling", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            Log.e("mf===cycling_1", responseString + "===" + result.data);

                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);

                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                            if(null != bean.getOrder_sn()){
                                Log.e("mf===cycling_2", bean.getOrder_sn()+"===" + bean.getCar_number()+"===" + bean.getLock_id());

                                oid = bean.getOrder_sn();
                                codenum = bean.getCar_number();
                                type = ""+bean.getLock_id();
                                m_nowMac = bean.getCar_lock_mac();

                                carInfo();

                                SharedPreferencesUrls.getInstance().putString("type", type);

//                                tv_biking_codenum.setText(codenum);
//                                tv_estimated_cost.setText("¥"+bean.getEstimated_cost());
//                                tv_estimated_cost2.setText("¥"+bean.getEstimated_cost());
//                                tv_car_start_time.setText(bean.getCar_start_time());
//                                tv_car_start_time2.setText(bean.getCar_start_time());
//                                tv_car_mileage.setText(mileage);
//                                tv_car_electricity.setText(electricity);
//
//                                tv_pay_codenum.setText(bean.getCar_number());
//                                tv_pay_car_start_time.setText(bean.getCar_start_time());
//                                tv_pay_car_end_time.setText(bean.getCar_end_time());
//                                tv_order_amount.setText(bean.getOrder_amount());

                                Log.e("mf===cycling_22", type+"===" + bean.getOrder_sn()+"===" + bean.getCar_number()+"===" + bean.getLock_id());

                                if ("4".equals(type) || "7".equals(type)) {
                                    Log.e("mf===cycling_3", bean.getOrder_sn()+"===" + bean.getCar_number()+"===" + bean.getLock_id());

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
                                    Log.e("mf===cycling_4", bean.getOrder_sn()+"===" + bean.getCar_number()+"===" + bean.getLock_id());

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

                                                closeBroadcast();     //TODO
                                                activity.registerReceiver(broadcastReceiver, Config.initFilter());
                                                GlobalParameterUtils.getInstance().setLockType(LockType.MTS);
                                            }else if("4".equals(type)){
                                                isLookPsdBtn = true;
                                                BLEService.bluetoothAdapter = mBluetoothAdapter;

                                                bleService.view = context;
                                                bleService.showValue = true;
                                            }else if ("5".equals(type)  || "6".equals(type)) {
                                                Log.e("initView===5", m_nowMac+"==="+isLookPsdBtn);

                                                if(!SharedPreferencesUrls.getInstance().getBoolean("isKnow", false)){
                                                    WindowManager windowManager = activity.getWindowManager();
                                                    Display display = windowManager.getDefaultDisplay();
                                                    WindowManager.LayoutParams lp = advDialog.getWindow().getAttributes();
                                                    lp.width = (int) (display.getWidth() * 1);
                                                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                                    advDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                                                    advDialog.getWindow().setAttributes(lp);
                                                    advDialog.show();
                                                }

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
                                                                    ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener2);

//                                                                scrollToFinishActivity();
                                                                }
                                                            }
                                                        }, 10 * 1000);

                                                    }
                                                }, 2 * 1000);
                                            }else if ("7".equals(type)) {
                                                isLookPsdBtn = true;

                                            }

                                        }

                                        refreshLayout.setVisibility(View.VISIBLE);
                                    }
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

    private void carInfo() {
        Log.e("carInfo===000", "===" + codenum);

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

//                        "<p style=\"color: #666666; font-size: 16px;\">1\u5c0f\u65f6\u5185\u514d\u8d39\uff0c\u8d85\u8fc71\u5c0f\u65f6<span style=\"color: #FF0000;\">\uffe5<span style=\"font-size: 24px;\">1.00<\/span><\/span>\/30\u5206\u949f<\/p>"

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            Log.e("carInfo===", responseString + "===" + result.data);

                            CarBean bean = JSON.parseObject(result.getData(), CarBean.class);

                            Log.e("carInfo===2", bean.getNumber()+"===" + bean.getLock_mac());

                            lock_no = bean.getLock_no();
                            bleid = bean.getLock_secretkey();
                            deviceuuid = bean.getVendor_lock_id();
//                            force_backcar = bean.getForce_backcar();  //TODO
//                            String price = bean.getPrice();
//                            String electricity = bean.getElectricity();
//                            String mileage = bean.getMileage();


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

    private void cycling2() {
        Log.e("mf===cycling2", "===");

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

                            Log.e("mf===cycling2_1", responseString + "===" + result.data);

                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);

//                            if (loadingDialog != null && loadingDialog.isShowing()) {
//                                loadingDialog.dismiss();
//                            }

                            if(null != bean.getOrder_sn()){
                                Log.e("mf===cycling2_2", bean.getOrder_sn()+"===" + bean.getCar_number()+"===" + bean.getLock_id());

                                order_id = bean.getOrder_id();
                                oid = bean.getOrder_sn();
                                codenum = bean.getCar_number();
                                type = ""+bean.getLock_id();
                                m_nowMac = bean.getCar_lock_mac();
                                mileage = bean.getMileage();
                                electricity = bean.getElectricity();

                                SharedPreferencesUrls.getInstance().putString("type", type);

                                if ("4".equals(type) || "7".equals(type)) {
                                    changeTab(1);

                                    ll_estimated_cost.setVisibility(View.GONE);
                                    ll_electricity.setVisibility(View.VISIBLE);
                                    ll_bike.setVisibility(View.GONE);
                                    ll_ebike.setVisibility(View.VISIBLE);

                                }else{
                                    changeTab(0);

                                    ll_estimated_cost.setVisibility(View.VISIBLE);
                                    ll_electricity.setVisibility(View.GONE);
                                    ll_bike.setVisibility(View.VISIBLE);
                                    ll_ebike.setVisibility(View.GONE);
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
                                tv_order_amount.setText(bean.getOrder_amount());

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

            if (broadcastReceiver != null) {
                activity.unregisterReceiver(broadcastReceiver);
//                broadcastReceiver = null;
            }

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
                ll_top.setVisibility(View.GONE);
                ll_top_navi.setVisibility(View.VISIBLE);


//              Log.e("onMarkerClick===", marker.getTitle()+"==="+marker.getTitle().split("-")[0]);
                Log.e("onMarkerClick===", mAMapNavi+"==="+referLatitude+"==="+referLongitude+"==="+marker.getPosition().latitude+"==="+marker.getPosition().longitude);

                tv_navi_name.setText(marker.getTitle());
                mAMapNavi.calculateRideRoute(new NaviLatLng(referLatitude, referLongitude), new NaviLatLng(marker.getPosition().latitude, marker.getPosition().longitude));

                return true;
            }
        });

        loadingDialog = new LoadingDialog(activity);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        advDialog = new Dialog(context, R.style.Theme_AppCompat_Dialog);
        View advDialogView = LayoutInflater.from(context).inflate(R.layout.ui_adv_view3, null);
        advDialog.setContentView(advDialogView);
        advDialog.setCanceledOnTouchOutside(false);

        advAgainBtn = (TextView)advDialogView.findViewById(R.id.ui_adv_againBtn);
        advCloseBtn = (TextView)advDialogView.findViewById(R.id.ui_adv_closeBtn);

//        customBuilder = new CustomDialog.Builder(context);    //TODO
//        customBuilder.setType(1).setTitle("温馨提示").setMessage("当前行程已停止计费，客服正在加紧处理，请稍等\n客服电话：0519—86999222");
//        customDialog = customBuilder.create();

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("连接失败").setMessage("蓝牙连接失败，请靠近车锁，重启软件后再试")
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        customDialog2 = customBuilder.create();

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("温馨提示").setMessage("请前往最近的还车点还车")
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        customDialog3 = customBuilder.create();

//        customBuilder = new CustomDialog.Builder(context);
//        customBuilder.setTitle("温馨提示").setMessage("不在还车点，请至校内地图红色区域停车")
//                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        customDialog3 = customBuilder.create();

//        customBuilder = new CustomDialog.Builder(context);
//        customBuilder.setTitle("温馨提示").setMessage("不在还车点，请至校内地图绿色区域停车")
//                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        customDialog4 = customBuilder.create();
//
//        customBuilder = new CustomDialog.Builder(context);
//        customBuilder.setTitle("温馨提示").setMessage("上锁失败，请联系客服\n客服电话：0519—86999222")
//                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        customDialog5 = customBuilder.create();
//
//        customBuilder = new CustomDialog.Builder(context);
//        customBuilder.setTitle("温馨提示").setMessage("连接失败，请重试")
//                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        customDialog6 = customBuilder.create();
//
//        customBuilder = new CustomDialog.Builder(context);
//        customBuilder.setTitle("温馨提示").setMessage("关锁失败，请重试")
//                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        customDialog7 = customBuilder.create();
//
//        customBuilder = new CustomDialog.Builder(context);
//        customBuilder.setTitle("温馨提示").setMessage("开锁失败，请重试")
//                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        customDialog8 = customBuilder.create();
//
//        customBuilder = new CustomDialog.Builder(context);
//        customBuilder.setTitle("温馨提示").setMessage("请确认锁已关闭")
//                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        customDialog9 = customBuilder.create();

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
        ll_biking_openAgain = activity.findViewById(R.id.ll_biking_openAgain);
        ll_biking_endBtn = activity.findViewById(R.id.ll_biking_endBtn);
        ll_biking_errorEnd = activity.findViewById(R.id.ll_biking_errorEnd);
        tv_againBtn = activity.findViewById(R.id.tv_againBtn);

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
        ll_biking_endBtn.setOnClickListener(this);
        ll_biking_errorEnd.setOnClickListener(this);
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

        mMyImageLoader = new MyImageLoader();
        mBanner = activity.findViewById(R.id.banner);
        //设置样式，里面有很多种样式可以自己都看看效果
        mBanner.setBannerStyle(0);
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
        advAgainBtn.setOnClickListener(this);
        advCloseBtn.setOnClickListener(this);


    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e("mf===onResume", SharedPreferencesUrls.getInstance().getString("iscert", "")+"==="+type);

        mapView.onResume();

        car_authority();
        banner();

//        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
//
//        Log.e("mf===onResume", access_token+"===");
//
//        if (access_token == null || "".equals(access_token)) {
//            rl_authBtn.setEnabled(true);
//            rl_authBtn.setVisibility(View.VISIBLE);
//            tv_authBtn.setText("您还未登录，点我快速登录");
//
//            refreshLayout.setVisibility(View.GONE);
////                cartBtn.setVisibility(View.GONE);
////                rechargeBtn.setVisibility(View.GONE);
//        } else {
//            refreshLayout.setVisibility(View.VISIBLE);
//            if (SharedPreferencesUrls.getInstance().getString("iscert", "") != null && !"".equals(SharedPreferencesUrls.getInstance().getString("iscert", ""))) {
//                switch (Integer.parseInt(SharedPreferencesUrls.getInstance().getString("iscert", ""))) {
//                    case 1:
//                        rl_authBtn.setEnabled(true);
//                        rl_authBtn.setVisibility(View.VISIBLE);
//                        tv_authBtn.setText("您还未登录，点我快速登录");
//
//                        break;
//                    case 2:
//
//                        rl_authBtn.setEnabled(true);
//                        rl_authBtn.setVisibility(View.VISIBLE);
//                        tv_authBtn.setText("您还未认证，点我快速认证");
//                        break;
//                    case 3:
//
//                        rl_authBtn.setEnabled(false);
//                        rl_authBtn.setVisibility(View.VISIBLE);
//                        tv_authBtn.setText("认证审核中");
//                        break;
//                    case 4:
//                        rl_authBtn.setEnabled(true);
//                        rl_authBtn.setVisibility(View.VISIBLE);
//                        tv_authBtn.setText("认证被驳回，请重新认证");
//
//                        break;
//                }
//            } else {
//                rl_authBtn.setVisibility(View.GONE);
//            }
//
//        }

//        getFeedbackStatus();
    }

    private void banner() {
        Log.e("mf===banner", "===" + codenum);

        HttpHelper.get(context, Urls.banner + 3, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("mf===banner=fail", "===" + throwable.toString());
                onFailureCommon("mf===banner", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e("mf===banner0", responseString + "===");

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            JSONArray ja_banners = new JSONArray(new JSONObject(result.getData()).getString("banners"));

                            Log.e("mf===banner1", ja_banners.length() + "===" + result.data);

                            for (int i = 0; i < ja_banners.length(); i++) {
                                BannerBean bean = JSON.parseObject(ja_banners.get(i).toString(), BannerBean.class);

//                                Log.e("mf===banner2", bean.getImage_url()+"===");

                                imagePath.add(bean.getImage_url());

                                imageTitle.add(bean.getH5_title());
//                                imageTitle.add("");
                            }

                            mBanner.setBannerTitles(imageTitle);
                            mBanner.setImages(imagePath).setOnBannerListener(MainFragment.this).start();

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

    @Override
    public void onClick(View view) {
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");

        Log.e("onClick===mf", "==="+access_token);

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
                        case "1":
//                            closeBroadcast();
//                            deactivate();

                            UIHelper.goToAct(context, LoginActivity.class);
                            break;

                        case "2":   //TODO
                            UIHelper.goToAct(context, RealNameAuthActivity.class);
                            break;

                        case "3":   //TODO
                            UIHelper.goToAct(context, RealNameAuthActivity.class);
                            break;

                        case "4":   //TODO
                            UIHelper.goToAct(context, RealNameAuthActivity.class);
                            break;

                        default:
                            break;
                    }
                }
                break;


            case R.id.mainUI_refreshLayout:
                Log.e("refreshLayout===0", parking()+"==="+isConnect+"==="+isLookPsdBtn+"==="+isContainsList+"==="+SharedPreferencesUrls.getInstance().getString("iscert", ""));

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
                Log.e("fragment===", bikeFragment.isHidden()+"==="+ebikeFragment.isHidden());

                if(!bikeFragment.isHidden()){
                    UIHelper.goWebViewAct(context,"停车须知",Urls.phtml5 + uid);    //TODO
                }else{
                    UIHelper.goWebViewAct(context,"停车须知",Urls.ebike_phtml5 + uid);
                }

                break;

            case R.id.mainUI_linkServiceLayout:
                initmPopupWindowView();
                break;

            case R.id.ll_change_car:
            case R.id.iv_rent_cancelBtn:
                Log.e("ll_rent_cancelB=onClick", "==="+access_token+"==="+SharedPreferencesUrls.getInstance().getString("iscert",""));

                popupwindow.dismiss();

                break;

            case R.id.ll_rent:
                Log.e("ll_rent===onClick", "==="+access_token+"==="+SharedPreferencesUrls.getInstance().getString("iscert",""));

                //TODO 单车月卡能用电单车吗
                order();

                break;

            case R.id.ll_biking_openAgain:
                String tvAgain = tv_againBtn.getText().toString().trim();

                Log.e("ll_openAgain===onClick", tvAgain+"==="+type+"==="+access_token+"==="+SharedPreferencesUrls.getInstance().getString("iscert",""));

                isAgain = true;
                isEndBtn = false;

                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在唤醒车锁");
                    loadingDialog.show();
                }

                if ("临时上锁".equals(tvAgain)){

                    if("4".equals(type)){
                        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                            ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                            popupwindow.dismiss();
                        }
                        BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
                        mBluetoothAdapter = bluetoothManager.getAdapter();

                        BLEService.bluetoothAdapter = mBluetoothAdapter;

                        bleService.view = context;
                        bleService.showValue = true;

                        if (mBluetoothAdapter == null) {
                            ToastUtil.showMessageApp(context, "获取蓝牙失败");
                            popupwindow.dismiss();
                            return;
                        }
                        if (!mBluetoothAdapter.isEnabled()) {
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, 188);
                        } else {
                            Log.e("openAgain===onClick_4", "临时上锁==="+bleid + "==="+m_nowMac);

                            bleService.connect(m_nowMac);

                            checkConnect2();
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
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, 188);
                        } else {
                            Log.e("openAgain===onClick_7", "临时上锁===" + isConnect + "===" + deviceuuid + "===" + apiClient);

                            if(!isConnect){
                                XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                                builder.setBleStateChangeListener(MainFragment.this);
                                builder.setScanResultCallback(MainFragment.this);
                                apiClient = builder.build();

                                MainFragmentPermissionsDispatcher.connectDeviceWithPermissionCheck(MainFragment.this, deviceuuid);

                                isConnect = false;
                                m_myHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isConnect){
//                                            if (loadingDialog != null && loadingDialog.isShowing()) {
//                                                loadingDialog.dismiss();
//                                            }

                                            Log.e("closeAgain===7==timeout", "临时上锁==="+isConnect + "==="+activity.isFinishing());

                                            lock();
                                        }
                                    }
                                }, 10 * 1000);
                            }else{
                                xiaoanClose_blue();
                            }
                        }
                    }else{
                        temporaryAction();
                    }

                }else if ("再次开锁".equals(tvAgain)){
                    if("2".equals(type)){

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
                            if(!isLookPsdBtn){   //没连上
                                isTemp = true;
                                isOpenLock = true;
                                connect();
                            }else{
                                BaseApplication.getInstance().getIBLE().openLock();
                            }
                        }
                    }else if("3".equals(type)){
//                      unlock();
                        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                            ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                            popupwindow.dismiss();
                        }
                        BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
                        mBluetoothAdapter = bluetoothManager.getAdapter();

                        if (mBluetoothAdapter == null) {
                            ToastUtil.showMessageApp(context, "获取蓝牙失败");
                            popupwindow.dismiss();
                            return;
                        }
                        if (!mBluetoothAdapter.isEnabled()) {
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, 188);
                        } else {
                            if (!TextUtils.isEmpty(m_nowMac)) {
                                Log.e("scan===3_1", isLookPsdBtn+"==="+m_nowMac);

                                if(!isLookPsdBtn){   //没连上
                                    isTemp = true;
                                    isOpenLock = true;
                                    connect();
                                }else{
                                    BaseApplication.getInstance().getIBLE().openLock();
                                }

                            }
                        }
                    }else if("4".equals(type)){
                        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                            ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                            popupwindow.dismiss();
                        }
                        BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
                        mBluetoothAdapter = bluetoothManager.getAdapter();

                        BLEService.bluetoothAdapter = mBluetoothAdapter;

                        bleService.view = context;
                        bleService.showValue = true;

                        if (mBluetoothAdapter == null) {
                            ToastUtil.showMessageApp(context, "获取蓝牙失败");
                            popupwindow.dismiss();
                            return;
                        }
                        if (!mBluetoothAdapter.isEnabled()) {
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, 188);
                        } else {
                            Log.e("openAgain===onClick_4", "再次开锁==="+bleid + "==="+m_nowMac);

                            bleService.connect(m_nowMac);

                            checkConnect();
                        }
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
                            Log.e("lookPsdBtn===", "==="+isLookPsdBtn+"==="+m_nowMac);

                            if(!isLookPsdBtn){   //没连上
                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                    loadingDialog.dismiss();
                                }

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

                                ClientManager.getClient().getStatus(m_nowMac, new IGetStatusResponse() {
                                    @Override
                                    public void onResponseSuccess(String version, String keySerial, String macKey, String vol) {
                                        Log.e("getStatus===Success", "==="+keySerial);

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
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, 188);
                        } else {

                            Log.e("openAgain===onClick_7", "再次开锁===" + isConnect + "===" + deviceuuid + "===" + apiClient);

                            if(!isConnect){
                                XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                                builder.setBleStateChangeListener(MainFragment.this);
                                builder.setScanResultCallback(MainFragment.this);
                                apiClient = builder.build();

                                MainFragmentPermissionsDispatcher.connectDeviceWithPermissionCheck(MainFragment.this, deviceuuid);

                                isConnect = false;
                                m_myHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isConnect){
//                                            if (loadingDialog != null && loadingDialog.isShowing()) {
//                                                loadingDialog.dismiss();
//                                            }

                                            Log.e("openAgain===7==timeout", "再次开锁==="+isConnect + "==="+activity.isFinishing());

                                            unlock();
                                        }
                                    }
                                }, 10 * 1000);
                            }else{
                                xiaoanOpen_blue();
                            }
                        }
                    }
                }

                break;

            case R.id.ll_biking_endBtn:
                Log.e("ll_endBtn===onClick", access_token+"==="+SharedPreferencesUrls.getInstance().getString("iscert",""));

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
                    Log.e("queryState===1", "===");

                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 188);

                    return;
                }

                clickCount++;

//                test_xinbiao += major+"==="+minor+"\n";   //TODO
//                tv_test_xinbiao.setText(test_xinbiao);

                Log.e("biking_endBtn===", type+"==="+major+"==="+isContainsList.contains(true)+"==="+referLatitude+"==="+referLongitude);

                if (loadingDialog != null && !loadingDialog.isShowing()){
//                        loadingDialog.setTitle("还车点确认中");
                    loadingDialog.setTitle("正在还车中，请勿离开");
                    loadingDialog.show();
                }

                if(major !=0){
                    queryState();
                }else if("7".equals(type)){
                    car();       //TODO
                }else{
                    startXB();



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
                Log.e("ll_errorEnd===onClick", access_token+"==="+SharedPreferencesUrls.getInstance().getString("iscert",""));
                Intent intent = new Intent(context, EndBikeFeedBackActivity.class);
                intent.putExtra("bikeCode", codenum);
                context.startActivity(intent);
                break;

            case R.id.mainUI_scanCode_lock:
                Log.e("scanCode_lock===mf", SharedPreferencesUrls.getInstance().getString("iscert","")+"==="+access_token);

                if (access_token == null || "".equals(access_token)){
                    ToastUtil.showMessageApp(context,"请先登录账号");
                    UIHelper.goToAct(context,LoginActivity.class);
                    return;
                }
                if (SharedPreferencesUrls.getInstance().getString("iscert","") != null && !"".equals(SharedPreferencesUrls.getInstance().getString("iscert",""))){
                    switch (Integer.parseInt(SharedPreferencesUrls.getInstance().getString("iscert",""))){

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


                            if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                popupwindow.dismiss();
                            }

                            bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
                            mBluetoothAdapter = bluetoothManager.getAdapter();

                            if (mBluetoothAdapter == null) {
                                ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                popupwindow.dismiss();
                                return;
                            }
                            if (!mBluetoothAdapter.isEnabled()) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, 188);
                            } else {
                                try {
//                                      closeBroadcast();
//                                      deactivate();

                                    intent = new Intent();
                                    intent.setClass(context, ActivityScanerCode.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivityForResult(intent, SCANNIN_GREQUEST_CODE);

                                } catch (Exception e) {
                                    UIHelper.showToastMsg(context, "相机打开失败,请检查相机是否可正常使用", R.drawable.ic_error);
                                }
                            }


                            break;
                        case 1:
                            ToastUtil.showMessageApp(context,"需要登录");
                            UIHelper.goToAct(context, RealNameAuthActivity.class);
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
                            break;
                        case 5:
                            ToastUtil.showMessageApp(context,"需要充值余额或购买骑行卡");
                            break;
                        case 6:
                            ToastUtil.showMessageApp(context,"有进行中行程");
                            break;
                        case 7:
                            ToastUtil.showMessageApp(context,"有待支付行程");
                            break;
                        case 8:
                            ToastUtil.showMessageApp(context,"有待支付调度费");
                            break;
                        case 9:
                            ToastUtil.showMessageApp(context,"有待支付赔偿费");
                            break;
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

                    //TODO
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

        Log.e("main===changeTab", index+"==="+tab);

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
        TextView tv_electricity = (TextView) customView.findViewById(R.id.tv_electricity);
        TextView tv_mileage= (TextView) customView.findViewById(R.id.tv_mileage);
        LinearLayout ll_electricity = (LinearLayout) customView.findViewById(R.id.ll_electricity);
        LinearLayout ll_mileage = (LinearLayout) customView.findViewById(R.id.ll_mileage);
        LinearLayout ll_change_car = (LinearLayout) customView.findViewById(R.id.ll_change_car);
        LinearLayout ll_rent = (LinearLayout) customView.findViewById(R.id.ll_rent);

        if("4".equals(type) || "7".equals(type)){
            ll_electricity.setVisibility(View.VISIBLE);
            ll_mileage.setVisibility(View.VISIBLE);

            tv_electricity.setText(electricity);
            tv_mileage.setText(mileage);
        }else{
            ll_electricity.setVisibility(View.GONE);
            ll_mileage.setVisibility(View.GONE);
        }

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
        //设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
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
        //设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
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
        final PopupWindow popupwindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

        //设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
        popupwindow.setAnimationStyle(R.style.PopupAnimation);
        popupwindow.setOutsideTouchable(false);

        LinearLayout feedbackLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_feedbackLayout);
        LinearLayout helpLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_helpLayout);
        final LinearLayout callLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_callLayout);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.pop_menu_feedbackLayout:
                        UIHelper.goToAct(context, EndBikeFeedBackActivity.class);
                        break;
                    case R.id.pop_menu_helpLayout:
                        UIHelper.goToAct(context, CarFaultActivity.class);

                        break;
                    case R.id.pop_menu_callLayout:
//                        UIHelper.goToAct(context, ServiceCenterActivity.class);
                        Intent intent = new Intent(context, ServiceCenterActivity.class);
                        intent.putExtra("bikeCode", codenum);
                        startActivity(intent);

                        break;
                }
                popupwindow.dismiss();
            }
        };

        feedbackLayout.setOnClickListener(listener);
        helpLayout.setOnClickListener(listener);
        callLayout.setOnClickListener(listener);

        popupwindow.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void car() {
        Log.e("mf===car0", "===" + codenum);

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

                            Log.e("mf===car1", responseString + "===" + result.data);

                            CarBean bean = JSON.parseObject(result.getData(), CarBean.class);

//                            if (loadingDialog != null && loadingDialog.isShowing()) {
//                                loadingDialog.dismiss();
//                            }

                            bean.setLatitude("31.764387");      //TODO
                            bean.setLongitude("119.92056");

                            Log.e("mf===car2", isContainsList + "===" + isContainsList.contains(true) + "===" + pOptions + "===" + bean.getLatitude() + "===" + bean.getLongitude() + "===" +bean.getNumber() + "===" + bean.getLock_mac());

//                            type = ""+bean.getLock_id();
//                            bleid = bean.getLock_secretkey();
//                            deviceuuid = bean.getVendor_lock_id();
//                            codenum = bean.getNumber();
//                            m_nowMac = bean.getLock_mac();

                            if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
                                isContainsList.clear();
                            }

                            Log.e("mf===car2_1", isContainsList + "===" + isContainsList.contains(true) + "===" + pOptions + "===" + bean.getLatitude() + "===" + bean.getLongitude() + "===" +bean.getNumber() + "===" + bean.getLock_mac());


                            for ( int i = 0; i < pOptions.size(); i++){

                                Log.e("mf===car2_2", isContainsList + "===" + isContainsList.contains(true) + "===" + pOptions + "===" + bean.getLatitude() + "===" + bean.getLongitude() + "===" +bean.getNumber() + "===" + bean.getLock_mac());

                                isContainsList.add(pOptions.get(i).contains(new LatLng(Double.parseDouble(bean.getLatitude()), Double.parseDouble(bean.getLongitude()))));
                            }

                            Log.e("mf===car3", isContainsList + "===" + isContainsList.contains(true)  + "===" + bean.getLatitude()+"===="+bean.getLongitude());

                            if(!isContainsList.contains(true)){
                                minPoint(Double.parseDouble(bean.getLatitude()), Double.parseDouble(bean.getLongitude()));
                            }

                            Log.e("mf===car4", isContainsList + "===" + isContainsList.contains(true)  + "===" + bean.getLatitude()+"===="+bean.getLongitude());

                            if(isContainsList.contains(true)){
                                isGPS_Lo = true;

                                endBtn7();
                            }else{
                                isGPS_Lo = false;

                                startXB();

//                                if (loadingDialog  != null && !loadingDialog.isShowing()){
//                                    loadingDialog .setTitle("还车点确认中");
//                                    loadingDialog .show();
//                                }

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            int n=0;
                                            while(macList.size() == 0){

                                                Thread.sleep(100);
                                                n++;

                                                Log.e("biking===","biking=n=="+n);

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
                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });


            }
        });

    }

    private void order() {
        Log.e("order===", "==="+codenum);

        RequestParams params = new RequestParams();
        params.put("order_type", 1);        //订单类型 1骑行订单 2套餐卡订单 3充值订单 4认证充值订单
        params.put("car_number", URLEncoder.encode(codenum));
//        params.put("card_code", "");        //套餐卡券码（order_type为2时必传）
//        params.put("price", "");        //传价格数值 例如：20.00(order_type为3、4时必传)

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

                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                            SharedPreferencesUrls.getInstance().putString("type", type);

                            Log.e("order===2",  isLookPsdBtn + "===" + type+"===" + jsonObject.getString("order_sn"));

                            order_id = jsonObject.getInt("order_id");
                            oid = jsonObject.getString("order_sn");

                            isLookPsdBtn = false;

                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                            if (loadingDialog != null && !loadingDialog.isShowing()) {
                                loadingDialog.setTitle("正在唤醒车锁");
                                loadingDialog.show();
                            }

                            if ("1".equals(type)) {          //单车机械锁
                                UIHelper.goToAct(context, CurRoadStartActivity.class);
                                popupwindow.dismiss();
                            } else if ("2".equals(type)) {    //单车蓝牙锁

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
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 188);
                                } else {
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
                                }
                                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
                                mBluetoothAdapter = bluetoothManager.getAdapter();

                                if (mBluetoothAdapter == null) {
                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                    popupwindow.dismiss();
                                    return;
                                }
                                if (!mBluetoothAdapter.isEnabled()) {
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 188);
                                } else {
                                    if (!TextUtils.isEmpty(m_nowMac)) {
                                        Log.e("scan===3_1", "==="+m_nowMac);

                                        if (!TextUtils.isEmpty(m_nowMac)) {
                                            isOpenLock = true;
                                            connect();
                                        }
                                    }
                                }

                            }else if ("4".equals(type)) {

                                unlock();

                                //TODO
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
//                                    Log.e("mf===4_1", bleid + "==="+m_nowMac);
//
//                                    bleService.connect(m_nowMac);
//
//                                    checkConnect();
//                                }

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
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 188);
                                } else {
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
                                                        ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener2);

                                                        SearchRequest request = new SearchRequest.Builder().searchBluetoothLeDevice(0).build();      //duration为0时无限扫描

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
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 188);
                                } else {
                                    XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                                    builder.setBleStateChangeListener(MainFragment.this);
                                    builder.setScanResultCallback(MainFragment.this);
                                    apiClient = builder.build();

                                    MainFragmentPermissionsDispatcher.connectDeviceWithPermissionCheck(MainFragment.this, deviceuuid);

                                    isConnect = false;
                                    m_myHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!isConnect){
                                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                                    loadingDialog.dismiss();
                                                }

                                                Log.e("mf===7==timeout", isConnect + "==="+activity.isFinishing());

                                                unlock();
                                            }
                                        }
                                    }, 10 * 1000);
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


    //助力车关锁
    private void lock() {
        Log.e("mf===lock", isAgain+"===");


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
                Log.e("mf===lock1", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                params.put("back_type", "4");     // 4锁与信标
            }else if(isGPS_Lo){
                Log.e("mf===lock2", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                params.put("back_type", "2");     // 2锁gps在电子围栏
            }else if(macList.size() > 0){
                Log.e("mf===lock3", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                params.put("back_type", "3");     // 3信标
            }else if(force_backcar==1 && isTwo){
                Log.e("mf===lock4", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                params.put("back_type", "5");     // 没锁第二次强制还车
            }else{
//                  }else if(isContainsList.contains(true)){
                Log.e("mf===lock5", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                params.put("back_type", "1");     // 1手机gps在电子围栏
            }
        }

        HttpHelper.post(context, Urls.lock, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                if(isAgain){
//                    onStartCommon("正在加载");
//                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                Log.e("mf===lock_fail", responseString + "===" + throwable.toString());
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            Log.e("mf===lock_1", responseString + "===" + result.data);

                            if(isAgain){
                                tv_againBtn.setText("再次开锁");
                                SharedPreferencesUrls.getInstance().putString("tempStat","1");

                                ToastUtil.showMessageApp(context,"恭喜您,关锁成功!");

                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                    loadingDialog.dismiss();
                                }
                            }else{
                                n=0;
                                carLoopClose();
                            }




                        } catch (Exception e) {
                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });
            }
        });
    }

    //助力车开锁
    private void unlock() {
        Log.e("mf===unlock", "===");

        HttpHelper.post(context, Urls.unlock, null, new TextHttpResponseHandler() {
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

                            Log.e("mf===unlock1", type + "===" + codenum + "===" + responseString + "===" + result.data);


                            if(isAgain){
                                tv_againBtn.setText("临时上锁");
                                SharedPreferencesUrls.getInstance().putString("tempStat","0");

                                ToastUtil.showMessageApp(context,"恭喜您,关锁成功!");

                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                    loadingDialog.dismiss();
                                }
                            }else{
                                popupwindow.dismiss();

//                                cycling2();
//                                cyclingThread();

                                n=0;
                                carLoopOpen();
                            }



                        } catch (Exception e) {
                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });
            }
        });
    }

    //助力车开锁_轮询
    private void carLoopOpen() {
        Log.e("mf===carLoopOpen", "===" + codenum);

        HttpHelper.get(context, Urls.order_detail+order_id, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("mf===carLoopClose_fail", responseString + "===" + throwable.toString());
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            Log.e("mf===carLoopOpen1", responseString + "===" + result.data);

                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);



                            if(20 != bean.getOrder_state()){
                                queryCarStatusOpen();
                            }else{
//                                isConnect = true;

//                                car_notification(3, 0,  isAgain?0:1);

                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                    loadingDialog.dismiss();
                                }

                                ll_top_navi.setVisibility(View.GONE);
                                ll_top.setVisibility(View.VISIBLE);
                                rl_ad.setVisibility(View.GONE);
                                ll_top_biking.setVisibility(View.VISIBLE);

                                cyclingThread();

//                                car_authority();
                            }

                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());

                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                        }

                    }
                });
            }
        });
    }

    //助力车开锁_轮询
    private void queryCarStatusOpen() {
        if(n<5){
            n++;

            m_myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("mf===queryCarStatusOpen", "===");

                    carLoopOpen();
                }
            }, 1 * 1000);
        }else{
            ToastUtil.showMessageApp(context, "开锁失败");

            car_notification(2, 4, 0);

            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    //助力车关锁_轮询
    private void carLoopClose() {
        Log.e("mf===carLoopClose", order_id+"===" + isAgain+"===" + codenum);

        HttpHelper.get(context, Urls.order_detail+order_id, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if(isAgain){
                    onStartCommon("正在加载");
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("mf===carLoopClose_fail", responseString + "===" + throwable.toString());
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            Log.e("mf===carLoopClose1", responseString + "===" + result.data);

                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);

                            if(isAgain){
                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                    loadingDialog.dismiss();
                                }
                            }


                            if(bean.getOrder_state() < 30){
                                queryCarStatusClose();
                            }else{
//                                isConnect = true;

//                                car_notification(3, 0,  isAgain?0:1);

                                order_type = 1;
                                end();
                            }

                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());

                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                        }

                    }
                });
            }
        });
    }

    private void end() {
        closeDialog();

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
            ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener2);

        }else if("7".equals(type)){
            if (apiClient != null) {
                apiClient.onDestroy();
            }
        }else{
            BaseApplication.getInstance().getIBLE().stopScan();
            BaseApplication.getInstance().getIBLE().refreshCache();
            BaseApplication.getInstance().getIBLE().close();
            BaseApplication.getInstance().getIBLE().disconnect();

            if (broadcastReceiver != null) {
                activity.unregisterReceiver(broadcastReceiver);
//                broadcastReceiver = null;
            }
        }

        isWaitEbikeInfo = false;
        if (ebikeInfoThread != null) {
            ebikeInfoThread.interrupt();
            ebikeInfoThread = null;
        }

        Log.e("mf==end", order_id+"==="+order_type);

        if(order_type==1 && isEndBtn){
            ToastUtil.showMessageApp(context,"恭喜您,还车成功,请支付!");
        }

        if(order_type==3){
            UIHelper.goToAct(context, UnpayOtherActivity.class);
        }else{
            Intent intent = new Intent(context, SettlementPlatformActivity.class);
            intent.putExtra("order_type", order_type);
            intent.putExtra("order_id", order_id);
            startActivity(intent);
        }


    }

    //助力车关锁_轮询2
    private void queryCarStatusClose() {
        if(n<5){
            n++;

            m_myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("mf=queryCarStatusClose", "===");

                    carLoopClose();

                }
            }, 1 * 1000);
        }else{
            ToastUtil.showMessageApp(context, "关锁失败");

            car_notification(2, 4, 0);

            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    //泺平_开锁
    protected void rent(){

        Log.e("rent===000",lock_no+"==="+m_nowMac+"==="+keySource);

        RequestParams params = new RequestParams();
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

//                            if (loadingDialog != null && loadingDialog.isShowing()){
//                                loadingDialog.dismiss();
//                            }

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

                        }catch (Exception e){
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }


                    }
                });



            }
        });
    }

    private String parking(){
        Log.e("mf===parking", jsonArray+"==="+jsonArray2);


        if(jsonArray != null){

            try{

                Log.e("mf===parking1", jsonArray+"==="+jsonArray.length());

                for ( int i = 0; i < pOptions.size(); i++){

                    Log.e("mf===parking12", i+"==="+pOptions.get(i)+"==="+pOptions.get(i).contains(new LatLng(referLatitude, referLongitude)));

                    if(pOptions.get(i).contains(new LatLng(referLatitude, referLongitude))){
                        Log.e("mf===parking13", "==="+jsonArray.getJSONObject(i));

                        return ""+jsonArray.getJSONObject(i);
                    }
                }

                return "";

            }catch (Exception e){
                Log.e("mf===parking_e", "==="+e);
                return "";
            }
        }

        if(jsonArray2 != null){

            Log.e("mf===parking2", "===");

            try{

                Log.e("mf===parking21", jsonArray2+"==="+jsonArray2.length());

                for ( int i = 0; i < pOptions.size(); i++){

                    Log.e("mf===parking22", i+"==="+pOptions.get(i)+"==="+pOptions.get(i).contains(new LatLng(referLatitude, referLongitude)));

                    if(pOptions.get(i).contains(new LatLng(referLatitude, referLongitude))){
                        Log.e("mf===parking23", "==="+jsonArray2.getJSONObject(i));

                        return ""+jsonArray2.getJSONObject(i);
                    }
                }

                return "";

            }catch (Exception e){
                Log.e("mf===parking_e", "==="+e);
                return "";
            }
        }

        return "";
    }

    private void car_notification(final int action_type, int lock_status, int back_type) {
        Log.e("mf===car_notification", isAgain + "===" + action_type +"==="+ lock_status+"==="+ back_type +"==="+ oid+"==="+ referLatitude+"==="+referLongitude);

        RequestParams params = new RequestParams();
//        params.put("scene", isAgain?2:1); //场景值 必传 1借还车上报 2再次开(关)锁上报
        params.put("action_type", Md5Helper.encode(oid+":action_type:"+action_type));   //操作类型 1开锁 2临时上锁 3还车
        params.put("lock_status", Md5Helper.encode(oid+":lock_status:"+lock_status));     //车锁状态 必传1成功 2连接不上蓝牙 3蓝牙操作失败
        params.put("parking", parking());
        params.put("longitude", referLongitude);
        params.put("latitude", referLatitude);
//        params.put("report_type", Md5Helper.encode(oid+":report_type:"+report_type));     //1蓝牙开锁 2网络开锁 3蓝牙上锁 4网络上锁 【需加密，加密方式同上，例如md5('191004143208756404:report_type:1')】
        if(back_type!=0){
//            params.put("back_type", Md5Helper.encode(oid+":back_type:"+(!"1".equals(backType)?backType:back_type)));      //1手机gps在电子围栏 2锁gps在电子围栏 3信标 4锁与信标【需加密，加密方式同上，例如md5('191004143208756404:back_type:1')】

            if(major!=0){
                Log.e("mf===car_notification1", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                params.put("back_type", Md5Helper.encode(oid+":back_type:"+4));     // 4锁与信标
            }else if(isGPS_Lo){
                Log.e("mf===car_notification2", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                params.put("back_type", Md5Helper.encode(oid+":back_type:"+2));     // 2锁gps在电子围栏
            }else if(macList.size() > 0){
                Log.e("mf===car_notification3", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                params.put("back_type", Md5Helper.encode(oid+":back_type:"+3));     // 3信标
            }else if(force_backcar==1 && isTwo){
                Log.e("mf===car_notification4", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                params.put("back_type", Md5Helper.encode(oid+":back_type:"+5));     // 没锁第二次强制还车
            }else{
//                  }else if(isContainsList.contains(true)){
                Log.e("mf===car_notification5", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                params.put("back_type", Md5Helper.encode(oid+":back_type:"+1));     // 1手机gps在电子围栏
            }

//            params.put("back_type", Md5Helper.encode(oid+":back_type:"+backType));
        }

        HttpHelper.post(context, Urls.car_notification, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("mf=car_notification_f", responseString + "====" + throwable.toString());
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            Log.e("mf===car_notification0", responseString + "====" + action_type);

                            if(action_type == 1) {
                                ToastUtil.showMessageApp(context, "恭喜您,开锁成功!");

                                if(!isAgain){
                                    popupwindow.dismiss();

                                    ll_top_navi.setVisibility(View.GONE);
                                    ll_top.setVisibility(View.VISIBLE);
                                    rl_ad.setVisibility(View.GONE);
                                    ll_top_biking.setVisibility(View.VISIBLE);

                                    cyclingThread();
//                                    cyclingThread();


                                    if("5".equals(type)  || "6".equals(type)){
                                        if(!SharedPreferencesUrls.getInstance().getBoolean("isKnow", false)){
                                            WindowManager windowManager = activity.getWindowManager();     //TODO
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

                            }else if(action_type == 3){

                                if(!isAgain){

                                    order_type = 1;
                                    end();

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
        public void onConnectStatusChanged(final String mac, final int status) {

//            Log.e("ConnectStatus===", mac+"===="+(status == STATUS_CONNECTED));

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("ConnectStatus===biking", mac+"==="+(status == STATUS_CONNECTED)+"==="+m_nowMac);

                    if(status == STATUS_CONNECTED){
                        isLookPsdBtn = true;

//                        ToastUtil.showMessageApp(context,"设备连接成功");
                    }else{
                        isLookPsdBtn = false;

//                        ToastUtil.showMessageApp(context,"设备断开连接");
                    }

//                    connectDeviceIfNeeded();
                }
            });

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
                            Log.e("mf===", "scan====1");

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

    //泺平--》监听当前连接状态
    private final BleConnectStatusListener mConnectStatusListener2 = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(final String mac, final int status) {
//            BluetoothLog.v(String.format(Locale.getDefault(), "DeviceDetailActivity onConnectStatusChanged %d in %s", status, Thread.currentThread().getName()));

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("ConnectStatus===biking2", mac+"==="+(status == STATUS_CONNECTED)+"==="+m_nowMac);
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
        Log.e("mf===openBleLock", serverTime+"==="+keys+"==="+encryptionKey);

        ClientManager.getClient().openLock(m_nowMac,"000000000000", (int) serverTime, keys, encryptionKey, new IEmptyResponse(){
            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("openLock===Fail", m_nowMac+"==="+Code.toString(code));

                        getBleRecord();
//                      deleteBleRecord(null);

                        if("锁已开".equals(Code.toString(code))){
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

                            isFinish = true;

                            ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

                            car_notification(1, 1, 0);
                        }else{
                            car_notification(1, 3, 0);
                        }

//                      ToastUtil.showMessageApp(context, Code.toString(code));
                    }
                });

            }

            @Override
            public void onResponseSuccess() {
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
        Log.e("biking=openAgainBleLock", serverTime+"==="+keys+"==="+encryptionKey);

        ClientManager.getClient().openLock(m_nowMac,"000000000000", (int) serverTime, keys, encryptionKey, new IEmptyResponse(){
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

                        car_notification(1, 1, 0);

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

                        car_notification(1, 1, 0);
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
//                            macText.setText(Major+"==="+Minor+"==="+macList);
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

    //泺平===还车_查锁是否关闭
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

                            car_notification(3, 1, 1);

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
                isLookPsdBtn = false;

                Log.e("connectDeviceLP===", "Fail==="+Code.toString(code));
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
                isLookPsdBtn = true;

                Log.e("connectDeviceLP===", "Success==="+profile);

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

                                        car_notification(1, 0, 0);

                                        if(isAgain){
                                            tv_againBtn.setText("临时上锁");
                                            SharedPreferencesUrls.getInstance().putString("tempStat","0");
                                        }

                                    }else{
                                        unlock();
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

    void checkConnect2(){
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("checkConnect2===", cn+"==="+bleService.connect);

                if(!bleService.connect){
                    cn++;

                    if(cn<10){
                        checkConnect2();
                    }else{

                        if("".equals(oid)){
//                                scrollToFinishActivity();

                            Toast.makeText(context,"扫码唤醒失败，重启手机蓝牙换辆车试试吧！",Toast.LENGTH_LONG).show();
                        }else{
                            unlock();
                        }

                    }

                }else{
                    bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});

                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("checkConnect2===2", tel+"==="+bleid+"==="+bleService+"==="+m_nowMac);

                            button8();
                            button9();
                            button2();

                            m_myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("checkConnect2===3", bleService.cc+"==="+"B1 2A 80 00 00 5B ".equals(bleService.cc));

                                    if("B1 2A 80 00 00 5B ".equals(bleService.cc)){
                                        Log.e("checkConnect2===4", "==="+bleService.cc);



                                        car_notification(3, 0, isAgain?0:1);

                                        if(isAgain){
                                            tv_againBtn.setText("再次开锁");
                                            SharedPreferencesUrls.getInstance().putString("tempStat","1");

                                            ToastUtil.showMessageApp(context,"恭喜您,关锁成功!");
                                        }

                                        if (loadingDialog != null && loadingDialog.isShowing()){
                                            loadingDialog.dismiss();
                                        }
                                    }else{
//                                        ToastUtil.showMessageApp(context,"关锁失败，请重试");

                                        lock();
                                    }

                                    Log.e("checkConnect2===5", "==="+bleService.cc);

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
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }

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
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
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

            Log.e("connectDevice===", "==="+imei);
        }
    }

    //小安
    @Override
    public void onConnect(BluetoothDevice bluetoothDevice) {
        Log.e("mf===Xiaoan", "Connect==="+isConnect);

        isConnect = true;

        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.e("mf===", "scan====1");

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
        }, 2 * 1000);

    }



    //小安
    @Override
    public void onDisConnect(BluetoothDevice bluetoothDevice) {
        Log.e("mf===Xiaoan", "DisConnect==="+isConnect);


        if(isConnect){
            isConnect = false;

            Log.e("mf===Xiaoan2", "DisConnect==="+isConnect);
            return;
        }

        isConnect = false;

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
        apiClient.setDefend(false, new BleCallback() {
            @Override
            public void onResponse(final Response response) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("xiaoanOpen===", response.toString());

                        if(response.code==0){
                            isFinish = true;

                            car_notification(1, 1, 0);

                            if(isAgain){
                                tv_againBtn.setText("临时上锁");
                                SharedPreferencesUrls.getInstance().putString("tempStat","0");

                                ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }
                            }


                        }else{
//                          ToastUtil.showMessageApp(context,"开锁失败");

                            unlock();
                        }

                    }
                });

            }
        });
    }

    public void xiaoanClose_blue() {
        apiClient.setDefend(true, new BleCallback() {
            @Override
            public void onResponse(final Response response) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("xiaoanClose===", type+"==="+deviceuuid+"==="+response.toString());

                        if(response.code==0){

                            macList2 = new ArrayList<> (macList);

                            car_notification(isAgain?2:3, 1, isAgain?0:1);

                            if(isAgain){
                                tv_againBtn.setText("再次开锁");
                                SharedPreferencesUrls.getInstance().putString("tempStat","1");

                                ToastUtil.showMessageApp(context,"恭喜您,关锁成功!");

                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }
                            }
                        }else if(response.code==6){
                            ToastUtil.showMessageApp(context,"车辆未停止，请停止后再试");

                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }else{
//                            ToastUtil.showMessageApp(context,"关锁失败");

                            lock();

//                            if("108".equals(info)){       //TODO
//                                Log.e("biking_defend===1", "====");
//
//                            }else{
//                                Log.e("biking_defend===2", "====");
//
//                                ToastUtil.showMessageApp(context,"关锁失败，请重试");
//                            }
                        }


                    }
                });
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
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    void closeDialog(){
        if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }

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
            ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener2);

        }else if("7".equals(type)){
            if (apiClient != null) {
                apiClient.onDestroy();
            }
        }else{
            BaseApplication.getInstance().getIBLE().stopScan();
            BaseApplication.getInstance().getIBLE().refreshCache();
            BaseApplication.getInstance().getIBLE().close();
            BaseApplication.getInstance().getIBLE().disconnect();

            if (broadcastReceiver != null) {
                activity.unregisterReceiver(broadcastReceiver);
                broadcastReceiver = null;
            }
        }

        super.onDestroy();

        Log.e("mf===onDestroy", isLookPsdBtn+"===");

        if (mapView != null) {
            mapView.onDestroy();
        }



        if (broadcastReceiver2 != null) {
            activity.unregisterReceiver(broadcastReceiver2);
            broadcastReceiver2 = null;
        }

        isWaitEbikeInfo = false;
        if (ebikeInfoThread != null) {
            ebikeInfoThread.interrupt();
            ebikeInfoThread = null;
        }

    }

    @Override
    public void OnBannerClick(int position) {
        Toast.makeText(context, "你点了第" + (position + 1) + "张轮播图", Toast.LENGTH_SHORT).show();

//        initmPopupWindowView();
    }

    private class MyImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context.getApplicationContext())
                    .load(path)
                    .into(imageView);
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
            car_notification(isOpenLock?1:isAgain?2:3, 2, 0);
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
            car_notification(isOpenLock?1:isAgain?2:3, 2, 0);
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

                                    car_notification(1, 3, 0);
                                }
                            } else {
                                isOpen = true;

                                ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");
                                Log.e("mf===", "OPEN_ACTION==="+isOpen);

                                m_myHandler.sendEmptyMessage(7);
                            }
                            break;
                        case Config.CLOSE_ACTION:
                            Log.e("mf===", "CLOSE_ACTION===");

                            break;
                        case Config.RESET_ACTION:
                            Log.e("mf===", "RESET_ACTION===");

                            break;
                        case Config.LOCK_STATUS_ACTION:
                            Log.e("biking===", "LOCK_STATUS_ACTION==="+isStop);

                            isStop = true;
                            isLookPsdBtn = true;

                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

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

//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }
//
//                    if (loadingDialog != null && !loadingDialog.isShowing()){
//                        loadingDialog.setTitle("正在还车");
//                        loadingDialog.show();
//                    }

//                    rl_msg.setVisibility(View.GONE);  //TODO
//                    if (polyline != null) {
//                        polyline.remove();
//                    }

                    if(macList.size()>0 || isContainsList.contains(true)){
                        isTemp = false;

                        if("2".equals(type)){
                            endBtn();
                        }else if("3".equals(type)){
                            endBtn3();
                        }else if("4".equals(type)){
                            endBtn4();
                        }else if("5".equals(type) || "6".equals(type)){
                            endBtn5();
                        }else if("7".equals(type)){
                            endBtn7();
                        }

                    }else{
//                        rl_msg.setVisibility(View.VISIBLE);
//                        minPolygon();
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }

                        customDialog3.show();
                    }


                    break;
                case 4:
                    //蓝牙还车成功
                    cycling2();

                    break;
                case 6:
                    //蓝牙还车成功
                    car_notification(3, 1, 1);

                    break;

                case 7:
                    //蓝牙开锁成功
                    car_notification(1, 1, 0);

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
                    }, 10 * 1000);
                    break;

                case 0x99://搜索超时
                    Log.e("0x99===", "==="+isStop);

//                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, MainFragment.this);
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("0x99===timeout0", isLookPsdBtn+"==="+isStop+"==="+type);

                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                            if (!isLookPsdBtn){

//                                memberEvent2();

                                if(!isOpenLock){
                                    Toast.makeText(context,"唤醒失败，重启手机蓝牙试试吧！",Toast.LENGTH_LONG).show();
                                }

                                BaseApplication.getInstance().getIBLE().stopScan();
                                BaseApplication.getInstance().getIBLE().refreshCache();
                                BaseApplication.getInstance().getIBLE().close();
                                BaseApplication.getInstance().getIBLE().disconnect();

                                Log.e("0x99===timeout", isLookPsdBtn+"==="+isStop+"==="+type);

                                if("3".equals(type)){
                                    if(isOpenLock){
                                        unlock();
                                    }
                                }else{
                                    car_notification(isOpenLock?1:isAgain?2:3, 2, 0);
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
        Log.e("biking===endBtn_0", isLookPsdBtn+"==="+force_backcar+"==="+isTwo+"==="+macList.size()+"==="+isContainsList.contains(true));

        if(force_backcar==1 && isTwo){
            ToastUtil.showMessageApp(context,"锁已关闭");
            Log.e("biking===", "endBtn===锁已关闭==="+first3);

            if(!isEndBtn) return;

            m_myHandler.sendEmptyMessage(6);

            return;
        }

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
                                    customDialog2.show();
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
                                        customDialog2.show();
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
                                    customDialog2.show();
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

                    isOpenLock = false;
                    connect();

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
                                        customDialog2.show();
                                    }else{
                                        clickCountDeal();
                                    }
                                }
                            }
                        }
                    }, 10 * 1000);



                }
            }else {
//                rl_msg.setVisibility(View.VISIBLE);
//                minPolygon();
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }

                customDialog3.show();
            }

        }
    }

    public void endBtn3(){
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            Log.e("biking===endBtn3",isLookPsdBtn+"==="+macList.size()+"==="+type+"==="+first3);

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

                    macList2 = new ArrayList<> (macList);
                    BaseApplication.getInstance().getIBLE().getLockStatus();

                    isLookPsdBtn = false;
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();

                                Log.e("biking===endBtn3_1",isLookPsdBtn+"==="+type+"==="+first3);

                                if(!isLookPsdBtn){
                                    if(first3){
                                        first3 = false;
                                        customDialog2.show();

                                        clickCountDeal();
                                    }else{
//                                        carClose();
//                                        lock(); //TODO
                                        car_notification(3, 2, 1);
                                    }
                                }
                            }
                        }
                    }, 10 * 1000);


                } else {
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
                                    car_notification(3, 2, 1);

                                }
                            }
                        }
                    }, 10 * 1000);


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

                    macList2 = new ArrayList<> (macList);
                    BaseApplication.getInstance().getIBLE().getLockStatus();

                    isLookPsdBtn = false;
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();

                                if(!isLookPsdBtn){
                                    if(first3){
                                        first3 = false;
                                        customDialog2.show();

                                        clickCountDeal();
                                    }else{
//                                        lock();     //TODO
                                        car_notification(3, 2, 1);
                                    }
                                }

                            }
                        }
                    }, 10 * 1000);

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
//                              BaseApplication.getInstance().getIBLE().stopScan();
                                BaseApplication.getInstance().getIBLE().refreshCache();
                                BaseApplication.getInstance().getIBLE().close();
                                BaseApplication.getInstance().getIBLE().disconnect();

                                if(first3){
                                    first3 = false;

                                    customDialog2.show();

                                    clickCountDeal();
                                }else{
//                                    lock();     //TODO
                                    car_notification(3, 2, 1);
                                }
                            }
                        }
                    }, 10 * 1000);

                }
            }else {
//                rl_msg.setVisibility(View.VISIBLE);
//                minPolygon();
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }

                customDialog3.show();
            }
        }
    }

    public void endBtn4(){
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            Log.e("biking===endBtn4",macList.size()+"==="+isContainsList.contains(true)+"==="+type+"==="+first3);

//            rl_msg.setVisibility(View.GONE);
//            if (polyline != null) {
//                polyline.remove();
//            }

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
//                    Log.e("mf===4_1", bleid + "==="+m_nowMac);
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
//                    Log.e("mf===4_2", bleid + "==="+m_nowMac);
//
//                    bleService.connect(m_nowMac);
//
//                    checkConnect2();
//                }

            }else {
//                rl_msg.setVisibility(View.VISIBLE);
//                minPolygon();
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }

                customDialog3.show();
            }


        }
    }

    public void endBtn5(){
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
//                rl_msg.setVisibility(View.VISIBLE);
//                minPolygon();
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }

                customDialog3.show();
            }


        }
    }

    public void endBtn7(){
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            Log.e("biking===endBtn7",macList.size()+"==="+type+"==="+deviceuuid+"==="+isConnect);

//            rl_msg.setVisibility(View.GONE);
//            if (polyline != null) {
//                polyline.remove();
//            }

            if (macList.size() > 0){
//                flag = 2;

//                lock();

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
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 188);
                } else {

                    Log.e("biking===endBtn7_1", "===" + isConnect + "===" + deviceuuid + "===" + apiClient);

                    if(!isConnect){
                        XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                        builder.setBleStateChangeListener(MainFragment.this);
                        builder.setScanResultCallback(MainFragment.this);
                        apiClient = builder.build();

                        MainFragmentPermissionsDispatcher.connectDeviceWithPermissionCheck(MainFragment.this, deviceuuid);

                        isConnect = false;
                        m_myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!isConnect){
//                                    if (loadingDialog != null && loadingDialog.isShowing()) {
//                                        loadingDialog.dismiss();
//                                    }

                                    Log.e("endBtn===7==timeout", "临时上锁==="+isConnect + "==="+activity.isFinishing());

                                    lock();
                                }
                            }
                        }, 10 * 1000);
                    }else{
                        xiaoanClose_blue();
                    }
                }

                return;
            }

            if (isContainsList.contains(true)){

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
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 188);
                } else {

                    Log.e("biking===endBtn7_2", "===" + isConnect + "===" + deviceuuid + "===" + apiClient);

                    if(!isConnect){
                        XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                        builder.setBleStateChangeListener(MainFragment.this);
                        builder.setScanResultCallback(MainFragment.this);
                        apiClient = builder.build();

                        MainFragmentPermissionsDispatcher.connectDeviceWithPermissionCheck(MainFragment.this, deviceuuid);

                        isConnect = false;
                        m_myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!isConnect){
//                                    if (loadingDialog != null && loadingDialog.isShowing()) {
//                                        loadingDialog.dismiss();
//                                    }

                                    Log.e("biking===endBtn7_3", "timeout==="+isConnect + "==="+activity.isFinishing());

                                    lock();
                                }
                            }
                        }, 10 * 1000);
                    }else{
                        xiaoanClose_blue();
                    }
                }

            }else {
//                rl_msg.setVisibility(View.VISIBLE);
//                minPolygon();
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }

                customDialog3.show();
            }


        }
    }

    public void backCar_r(){

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
        }
        //蓝牙锁
        BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            ToastUtil.showMessageApp(context, "获取蓝牙失败");
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
                }, 10 * 1000);
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
                            lock_no = data.getStringExtra("lock_no");
                            bleid = data.getStringExtra("bleid");
                            deviceuuid = data.getStringExtra("deviceuuid");
                            price = data.getStringExtra("price");
                            electricity = data.getStringExtra("electricity");
                            mileage = data.getStringExtra("mileage");

                            Log.e("mf===requestCode1", type+"==="+bleid +"==="+deviceuuid+"==="+price);

                            price = "<b><font color=\"#000000\">my html text</font></b>";
                            price = "<p style=\"color: #000000; font-size: 26px;\">my html text</p>";
                            price = "<p style=\"color: #00ff00\">my html text</p>";
                            price = "<p style=\"font-size:26px\">my html text</p>";
                            price = "<p style=\"color:#00ff00\"><font size=\"20\">my html text</font></p>";
                            price = "<font size=\"5\">my html text</font>";

                            price = "<p><font color=\"#000000\" size=\"20px\">" + "要显示的数据" + "</font></p>";

                            if("4".equals(type) || "7".equals(type)){
                                changeTab(1);
                            }else{
                                changeTab(0);
                            }

                            initmPopupRentWindowView();
//                          initmPopupRentWindowView("<html>"+price+"<\\/html>");

                            isStop = false;
                            isOpen = false;
                            isFinish = false;
                            n = 0;
                            cn = 0;
                            force_backcar = 0;
                            isTwo = false;
                            first3 = true;
                            isEndBtn = false;
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
                            isAgain = false;
                            order_type = 1;
                            isWaitEbikeInfo = true;
                            ebikeInfoThread = null;

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
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, 188);
                            } else {
                                if ("2".equals(type) || "3".equals(type)){

                                    closeBroadcast();     //TODO
                                    activity.registerReceiver(broadcastReceiver, Config.initFilter());
                                    GlobalParameterUtils.getInstance().setLockType(LockType.MTS);
                                }else if("4".equals(type)){

                                    BLEService.bluetoothAdapter = mBluetoothAdapter;

                                    bleService.view = context;
                                    bleService.showValue = true;
                                }else if ("5".equals(type)  || "6".equals(type)) {
                                    Log.e("initView===5", "==="+isLookPsdBtn);

                                    ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
                                    ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);
                                }else if ("7".equals(type)) {
                                }

                                SharedPreferencesUrls.getInstance().putString("tempStat", "0");
                                if ("4".equals(type) || "7".equals(type)) {
                                    tv_againBtn.setText("临时上锁");

                                }else{
                                    tv_againBtn.setText("再次开锁");
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

                        if (resultCode == RESULT_OK) {
                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                            Log.e("188===", isConnect+"==="+isLookPsdBtn+"==="+m_nowMac+"==="+type+"===");

                            if("".equals(m_nowMac) || "".equals(type)){
//                                if (loadingDialog != null && loadingDialog.isShowing()) {
//                                    loadingDialog.dismiss();
//                                }
                            }else{
                                if (loadingDialog != null && !loadingDialog.isShowing()) {
                                    loadingDialog.setTitle("正在唤醒车锁");
                                    loadingDialog.show();
                                }
                            }

                            if("2".equals(type) || "3".equals(type)) {
                                isOpenLock = true;
                                connect();
                            }else if("4".equals(type)){

                                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
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
                                                    ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener2);

                                                    SearchRequest request = new SearchRequest.Builder()      //duration为0时无限扫描
                                                            .searchBluetoothLeDevice(0)
                                                            .build();

                                                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                        Log.e("usecar===1", "===");

                                                        return;
                                                    }

                                                    Log.e("usecar===2", "===");

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

                                if(!isConnect){
                                    XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                                    builder.setBleStateChangeListener(MainFragment.this);
                                    builder.setScanResultCallback(MainFragment.this);
                                    apiClient = builder.build();

                                    MainFragmentPermissionsDispatcher.connectDeviceWithPermissionCheck(MainFragment.this, deviceuuid);

                                    isConnect = false;
                                    m_myHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                        if (!isConnect){
                                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                                loadingDialog.dismiss();
                                            }

                                            Log.e("188===timeout", "==="+isConnect + "==="+activity.isFinishing());

                                            String tvAgain = tv_againBtn.getText().toString().trim();
                                            if(isAgain){
                                                if("再次开锁".equals(tvAgain)){
                                                    unlock();
                                                }else{
                                                    lock();
                                                }
                                            }else{
                                                if(isEndBtn){
                                                    lock();
                                                }else{
                                                    unlock();
                                                }
                                            }

                                        }
                                        }
                                    }, 10 * 1000);
                                }else{
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


                            }
                        }else{
                            ToastUtil.showMessageApp(context, "需要打开蓝牙");

                            Log.e("188===fail", oid+"===");

                            if("".equals(oid)){
//                              scrollToFinishActivity();
                            }else{
//                                car_notification(1, 1, 0);
                            }
                        }
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
