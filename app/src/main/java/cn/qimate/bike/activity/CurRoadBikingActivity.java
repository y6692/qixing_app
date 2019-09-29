package cn.qimate.bike.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
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
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
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
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.aprbrother.aprilbeaconscansdk.Beacon;
import com.aprbrother.aprilbeaconscansdk.ScanManager;
import com.aprilbrother.aprilbrothersdk.BeaconManager;
import com.aprilbrother.aprilbrothersdk.EddyStone;
import com.autonavi.amap.mapcore.AMapEngineUtils;
import com.jsoniter.JsonIterator;
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
import com.sofi.blelocker.library.utils.BluetoothLog;
import com.sofi.blelocker.library.utils.StringUtils;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.config.LockType;
import com.sunshine.blelibrary.inter.OnConnectionListener;
import com.sunshine.blelibrary.inter.OnDeviceSearchListener;
import com.sunshine.blelibrary.utils.ConvertUtils;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;
import com.xiaoantech.sdk.XiaoanBleApiClient;
import com.xiaoantech.sdk.ble.model.Response;
import com.xiaoantech.sdk.ble.scanner.ScanResult;
import com.xiaoantech.sdk.listeners.BleCallback;
import com.xiaoantech.sdk.listeners.BleStateChangeListener;
import com.xiaoantech.sdk.listeners.ScanResultCallback;
import com.zxing.lib.scaner.activity.ActivityScanerCode;
import com.zxing.lib.scaner.activity.MainActivityPermissionsDispatcher;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import cn.http.OkHttpClientManager;
import cn.http.ResultCallback;
import cn.http.rdata.RRent;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.base.BaseFragment;
import cn.qimate.bike.ble.BLEService;
import cn.qimate.bike.ble.utils.ParseLeAdvData;
import cn.qimate.bike.core.common.AppManager;
import cn.qimate.bike.core.common.DisplayUtil;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.core.widget.MyScrollView;
import cn.qimate.bike.fragment.BikeFragment;
import cn.qimate.bike.jpush.ServiceReceiver;
import cn.qimate.bike.model.CurRoadBikingBean;
import cn.qimate.bike.model.EbikeInfoBean;
import cn.qimate.bike.model.KeyBean;
import cn.qimate.bike.model.LocationBean;
import cn.qimate.bike.model.NearbyBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.ResultConsel2;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.AESUtil;
import cn.qimate.bike.util.ByteUtil;
import cn.qimate.bike.util.Constants;
import cn.qimate.bike.util.Globals;
import cn.qimate.bike.util.IoBuffer;
import cn.qimate.bike.util.PublicWay;
import cn.qimate.bike.util.ToastUtil;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;
import okhttp3.Request;
import permissions.dispatcher.NeedsPermission;

import static cn.qimate.bike.core.common.Urls.schoolrangeList;
import static com.sofi.blelocker.library.Constants.STATUS_CONNECTED;

/**
 * Created by Administrator on 2017/2/12 0012.
 */
@SuppressLint("NewApi")
public class CurRoadBikingActivity extends SwipeBackActivity implements View.OnClickListener,
        LocationSource,AMapLocationListener
        , AMap.OnCameraChangeListener
        , AMap.OnMapTouchListener
        ,OnConnectionListener
        , BleStateChangeListener
        , ScanResultCallback
    {
    private final static String TAG = "BLEService";

//    public static CurRoadBikingActivity instance;

    /**
     * 选中的蓝牙设备
     */
    BluetoothDevice mDevice;
    BeaconManager manager;

    public static BluetoothAdapter mBluetoothAdapter;
    private String m_nowMac = "";

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    public static boolean isForeground = false;
    private Context context;
//    private  LoadingDialog loadingDialog;
    private  LoadingDialog loadingDialog2;
    private  LoadingDialog lockLoading;
    private LinearLayout mainLayout;
    private LinearLayout backImg;
    private TextView title;
    private TextView rightBtn;

    private TextView macText;
    private TextView bikeCodeText;
    private TextView time;
    private TextView electricity;
    private TextView mileage;
    private Button lookPsdBtn;
    private Button endBtn;

    private AMap aMap;
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private RelativeLayout rl_msg;

    public static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    public static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstFix = true;
    private LatLng myLocation = null;
    private Circle mCircle;
    private BitmapDescriptor successDescripter;
    private BitmapDescriptor freeDescripter;
    private Marker centerMarker;
    private boolean isMovingMarker = false;
    private String oid = "";
    private String osn = "";
    private String password = "";

    private List<Boolean> isContainsList;
    private List<Polygon> pOptions;
    private String type = "";
    private String uid = "";
    private String access_token = "";
    private boolean isLock = false;
    private ImageView linkServiceBtn;
    private LinearLayout ebikeInfoLayout;
    private LinearLayout linkServiceLayout;
    private boolean isFrist = true;
    private Dialog dialog;
    private ImageView titleImage;
    private ImageView exImage_1;
    private ImageView exImage_2;
    private ImageView exImage_3;
    private Switch switcher;

    private ImageView closeBtn;

    private ImageView myLocationBtn;
    private LinearLayout myLocationLayout;
    private TextView hintText;

    private double referLatitude = 0.0;
    private double referLongitude = 0.0;

    private String bikeCode = "";
    private LinearLayout refreshLayout;
    private BitmapDescriptor siteDescripter;
    private List<Marker> siteMarkerList;

    private boolean isStop = false;
    private boolean isRefresh = false;

    private LinearLayout slideLayout;
    private int imageWith = 0;
    private List<Object> macList;
    private List<Object> macList2;
    public List<LatLng> centerList = new ArrayList<LatLng>();
//    private ArrayList<EddyStone> eddyStones;

    private int flag = 0;
    public static int flagm = 0;
    boolean isFrist1 = true;
    boolean stopScan = false;
    private CustomDialog customDialog;
    private CustomDialog customDialog3;
    private CustomDialog customDialog4;
    private CustomDialog customDialog5;
    private CustomDialog customDialog6;
    private CustomDialog customDialog7;
    private CustomDialog customDialog8;
    private CustomDialog customDialog9;

    int near = 1;
    protected InternalReceiver internalReceiver = null;

//    public static boolean screen = true;
    public static boolean start = false;
    private boolean scan = false;
    private long k = 0;
    private long p = -1;
    private int xb = 0;
    private int n = 0;
    private int cn = 0;
    private int ccn = 0;
    private boolean isClickCount = false;
    private int clickCount = 0;
    private boolean first3 = true;
    private boolean isEndBtn = false;

    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    AMapLocation amapLocation;
    LinearLayout roleLayout;

    private boolean isWaitEbikeInfo = true;
    private Thread ebikeInfoThread;

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

    private Polyline polyline;

    private Dialog testDialog;
    private Dialog discountDialog;
    private TextView tv_discount;
    private LinearLayout confirmLayout;

    private TextView tv_test;
    private LinearLayout testLayout;

    private MyScrollView view_test;
    private TextView tv_test_xinbiao;

    int tz = 0;
    String transtype = "";
    int major = 0;
    int minor = 0;

    String test_xinbiao = "";
    private boolean isUp = false;

    private PolylineOptions mPolyoptions;
    private PolylineOptions mPolyoptions2;
    private Polyline polyline2;

    private XiaoanBleApiClient apiClient;
    private int xa_state = 0;
    private boolean isConnect = false;

    private ScanManager scanManager;
    private boolean isGPS_Lo = false;

    List<LatLng> listPoint = new ArrayList<>();
    private boolean  isLookPsdBtn = false;

    private int force_backcar = 0;
    private boolean isTwo = false;

    int tn=0;

    private boolean isTemp = false;
    private String info = "";
    private boolean isTz = false;

    @Override
    @TargetApi(23)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_cur_road_biking);
        context = this;
//        instance = this;
        BikeFragment.tz = 0;

        m_nowMac = SharedPreferencesUrls.getInstance().getString("m_nowMac", "");
        type = SharedPreferencesUrls.getInstance().getString("type", "");
        bleid = SharedPreferencesUrls.getInstance().getString("bleid", "");
        deviceuuid = SharedPreferencesUrls.getInstance().getString("deviceuuid", "");
        major = SharedPreferencesUrls.getInstance().getInt("major", 0);

        isTz = getIntent().getBooleanExtra("isTz", false);

        if("4".equals(type) || "7".equals(type)){
            jsonArray = BaseFragment.jsonArray2;
        }else{
            jsonArray = BaseFragment.jsonArray;
        }

        if(BaseApplication.getInstance().isTest()){
            type = "5";
            if("50:F1:4A:52:6A:DF".equals(m_nowMac)){
//                m_nowMac = "3C:A3:08:AE:BE:24";
                m_nowMac = "3C:A3:08:CD:9F:47";
            }else{
                type = "6";
                m_nowMac = "A4:34:F1:7B:BF:9A";
            }
        }
//        type = "7";

        Log.e("biking===onCreate", m_nowMac+"==="+type+"==="+bleid+"==="+deviceuuid+"==="+major);


        //注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
        IntentFilter filter = new IntentFilter("data.broadcast.action");
        registerReceiver(broadcastReceiver1, filter);

        mapView = (MapView) findViewById(R.id.curRoadUI_biking_map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写


        isContainsList = new ArrayList<>();
        pOptions = new ArrayList<>();
        siteMarkerList = new ArrayList<>();
        SharedPreferencesUrls.getInstance().putBoolean("isStop",false);
        imageWith = (int)(getWindowManager().getDefaultDisplay().getWidth() * 0.8);
        macList = new ArrayList<>();
        macList2 = new ArrayList<>();
        initView();

        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
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


//        customBuilder = new CustomDialog.Builder(context);
//        customBuilder.setTitle("温馨提示").setMessage("不在还车点，请至校内地图红色区域停车")
//                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        customDialog4 = customBuilder.create();
    }

    private void initView(){
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                            REQUEST_CODE_ASK_PERMISSIONS);
                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开位置权限！")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    scrollToFinishActivity();
                                }
                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            CurRoadBikingActivity.this.requestPermissions(
                                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                                    REQUEST_CODE_ASK_PERMISSIONS);
                        }
                    });
                    customBuilder.create().show();
                }
                return;
            }
        }
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        loadingDialog2 = new LoadingDialog(this);
        loadingDialog2.setCancelable(false);
        loadingDialog2.setCanceledOnTouchOutside(false);

        lockLoading = new LoadingDialog(this);
        lockLoading.setCancelable(false);
        lockLoading.setCanceledOnTouchOutside(false);

        dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.ui_frist_view, null);
        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);

        discountDialog = new Dialog(context, R.style.Theme_AppCompat_Dialog);
        View discountDialogView = LayoutInflater.from(context).inflate(R.layout.ui_discount_view, null);
        discountDialog.setContentView(discountDialogView);
        discountDialog.setCanceledOnTouchOutside(false);
        discountDialog.setCancelable(false);

        tv_discount = (TextView)discountDialogView.findViewById(R.id.ui_discount_text);
        confirmLayout = (LinearLayout)discountDialogView.findViewById(R.id.ui_discount_confirm);

        testDialog = new Dialog(context, R.style.Theme_AppCompat_Dialog);
        View testDialogView = LayoutInflater.from(context).inflate(R.layout.ui_test_view, null);
        testDialog.setContentView(testDialogView);
        testDialog.setCanceledOnTouchOutside(false);

        tv_test = (TextView)testDialogView.findViewById(R.id.ui_test_text);
        testLayout = (LinearLayout)testDialogView.findViewById(R.id.ui_test_confirm);

        view_test = (MyScrollView)findViewById(R.id.view_test);
        tv_test_xinbiao = (TextView)findViewById(R.id.tv_test_xinbiao);
        hintText = (TextView)findViewById(R.id.curRoadUI_biking_hintText);

        if(BaseApplication.getInstance().isTestLog()){
            view_test.setVisibility(View.VISIBLE);
        }else{
            view_test.setVisibility(View.GONE);
        }


//        titleImage = (ImageView)dialogView.findViewById(R.id.ui_fristView_title);
//        exImage_1 = (ImageView)dialogView.findViewById(R.id.ui_fristView_exImage_1);
//        exImage_2 = (ImageView)dialogView.findViewById(R.id.ui_fristView_exImage_2);
//        exImage_3 = (ImageView)dialogView.findViewById(R.id.ui_fristView_exImage_3);
        closeBtn = (ImageView)dialogView.findViewById(R.id.ui_fristView_closeBtn);

        mainLayout = (LinearLayout)findViewById(R.id.mainUI_title_mainLayout);
        backImg = (LinearLayout) findViewById(R.id.ll_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("骑行中");
        rightBtn = (TextView)findViewById(R.id.mainUI_title_rightBtn);
        rightBtn.setText("无法结束用车?");
        RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams)rightBtn.getLayoutParams();
        params4.setMargins(0,DisplayUtil.dip2px(context,8),DisplayUtil.dip2px(context,10),DisplayUtil.dip2px(context,8));
        rightBtn.setLayoutParams(params4);
        rightBtn.setBackgroundColor(getResources().getColor(R.color.white));
        rightBtn.setTextColor(getResources().getColor(R.color.ui_main));
        rightBtn.setOnClickListener(this);
        rightBtn.setTextSize(16);

        switcher = (Switch) findViewById(R.id.switcher);

        if(SharedPreferencesUrls.getInstance().getBoolean("switcher", false)){
            switcher.setChecked(true);
        }else{
            switcher.setChecked(false);
        }

        roleLayout = (LinearLayout) findViewById(R.id.ll_role);

        refreshLayout = (LinearLayout)findViewById(R.id.curRoadUI_biking_refreshLayout);

        macText = (TextView)findViewById(R.id.curRoadUI_biking_mac);
        bikeCodeText = (TextView)findViewById(R.id.curRoadUI_biking_code);
        time = (TextView)findViewById(R.id.curRoadUI_biking_time);
        ebikeInfoLayout = (LinearLayout)findViewById(R.id.curRoadUI_biking_ebikeInfoLayout);
        electricity = (TextView)findViewById(R.id.curRoadUI_biking_electricity);
        mileage = (TextView)findViewById(R.id.curRoadUI_biking_mileage);
        lookPsdBtn = (Button)findViewById(R.id.curRoadUI_biking_lookPsdBtn);
        endBtn = (Button)findViewById(R.id.curRoadUI_biking_endBtn);
        linkServiceBtn = (ImageView)findViewById(R.id.curRoadUI_biking_linkService_btn);
        myLocationBtn = (ImageView)findViewById(R.id.curRoadUI_biking_myLocation);
        linkServiceLayout = (LinearLayout)findViewById(R.id.curRoadUI_biking_linkServiceLayout);
        myLocationLayout = (LinearLayout)findViewById(R.id.curRoadUI_biking_myLocationLayout);
        slideLayout = (LinearLayout)findViewById(R.id.curRoadUI_biking_slideLayout);
        rl_msg = (RelativeLayout)findViewById(R.id.rl_msg);

        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        aMap.setMapType(AMap.MAP_TYPE_NAVI);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);// 设置地图logo显示在右下方
        aMap.getUiSettings().setLogoBottomMargin(-50);

        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(18f);// 设置缩放监听
        aMap.moveCamera(cameraUpdate);
        successDescripter = BitmapDescriptorFactory.fromResource(R.drawable.icon_usecarnow_position_succeed);
        freeDescripter = BitmapDescriptorFactory.fromResource(R.drawable.free_icon);
        siteDescripter = BitmapDescriptorFactory.fromResource(R.drawable.site_mark_icon);

        aMap.setOnMapTouchListener(this);
        setUpLocationStyle();

//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) titleImage.getLayoutParams();
//        params.height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.16);
//        titleImage.setLayoutParams(params);

//        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) exImage_1.getLayoutParams();
//        params1.height = (imageWith - DisplayUtil.dip2px(context,20)) * 2 / 5;
//        exImage_1.setLayoutParams(params1);
//
//        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) exImage_2.getLayoutParams();
//        params2.height = (imageWith - DisplayUtil.dip2px(context,20)) * 2 / 5;
//        exImage_2.setLayoutParams(params2);
//
//        LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) exImage_3.getLayoutParams();
//        params3.height = (imageWith - DisplayUtil.dip2px(context,20)) * 2 / 5;
//        exImage_3.setLayoutParams(params3);

        backImg.setOnClickListener(this);
        lookPsdBtn.setOnClickListener(this);
        switcher.setOnClickListener(this);
        roleLayout.setOnClickListener(this);
        endBtn.setOnClickListener(this);
        linkServiceBtn.setOnClickListener(this);
        myLocationBtn.setOnClickListener(this);
        myLocationLayout.setOnClickListener(this);
        linkServiceLayout.setOnClickListener(this);
        refreshLayout.setOnClickListener(this);
        slideLayout.setOnClickListener(this);
        confirmLayout.setOnClickListener(this);
        testLayout.setOnClickListener(this);

//        lookPsdBtn.setEnabled(false);  //

//        exImage_1.setOnClickListener(myOnClickLister);
//        exImage_2.setOnClickListener(myOnClickLister);
        closeBtn.setOnClickListener(myOnClickLister);

        initSite();

        mPolyoptions = new PolylineOptions();
        mPolyoptions.width(40f);
        mPolyoptions.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.grasp_trace_line));

        mPolyoptions2 = new PolylineOptions();
        mPolyoptions2.width(10f);
        mPolyoptions2.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.grasp_trace_line));

        uid = SharedPreferencesUrls.getInstance().getString("uid","");
        access_token = SharedPreferencesUrls.getInstance().getString("access_token","");

        ToastUtil.showMessage(this, uid+"===="+access_token);

        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            refreshLayout.setVisibility(View.GONE);
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context,LoginActivity.class);
        }else {
//            if(!SharedPreferencesUrls.getInstance().getBoolean("switcher",false)) {
//            }

            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                finish();
            }
            //蓝牙锁
            if (mBluetoothAdapter == null) {
                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = bluetoothManager.getAdapter();
            }



            if (mBluetoothAdapter == null) {
                ToastUtil.showMessageApp(context, "获取蓝牙失败");
                finish();
                return;
            }

            if (!mBluetoothAdapter.isEnabled()) {
                flagm = 1;
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 188);
            } else {
                if ("2".equals(type) || "3".equals(type)){

                    if(isTz){
                        isLookPsdBtn = true;

                    }else{
                        if (lockLoading != null && !lockLoading.isShowing()){
                            lockLoading.setTitle("正在唤醒车锁");
                            lockLoading.show();
                        }

                        m_myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                connect();

//                            BaseApplication.getInstance().getIBLE().disconnect();

//                            BaseApplication.getInstance().getIBLE().connect(m_nowMac, CurRoadBikingActivity.this);
//                            resetLock();
                                m_myHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isStop){
                                            if (lockLoading != null && lockLoading.isShowing()) {
                                                lockLoading.dismiss();
                                            }
                                            Toast.makeText(context,"唤醒失败，重启手机蓝牙试试吧！",Toast.LENGTH_LONG).show();
                                            BaseApplication.getInstance().getIBLE().refreshCache();
                                            BaseApplication.getInstance().getIBLE().disconnect();
                                            BaseApplication.getInstance().getIBLE().close();

                                            scrollToFinishActivity();
                                        }
                                    }
                                }, 15 * 1000);
                            }
                        }, 2 * 1000);
                    }


                    closeBroadcast();
                    registerReceiver(Config.initFilter());
                    GlobalParameterUtils.getInstance().setLockType(LockType.MTS);
                }else if("4".equals(type)){
                    isLookPsdBtn = true;

                    BLEService.bluetoothAdapter = mBluetoothAdapter;

                    bleService.view = context;
                    bleService.showValue = true;
                }else if ("5".equals(type)  || "6".equals(type)) {
//                    ClientManager.getClient().disconnect(m_nowMac);

                    Log.e("initView===5", isTz+"==="+isLookPsdBtn);

                    if(isTz){
                        isLookPsdBtn = true;
                        ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
                        ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);
                    }else{
                        if (lockLoading != null && !lockLoading.isShowing()){
                            lockLoading.setTitle("正在唤醒车锁");
                            lockLoading.show();
                        }

                        m_myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                SearchRequest request = new SearchRequest.Builder()      //duration为0时无限扫描
//                                        .searchBluetoothLeDevice(0)
//                                        .build();
//
//                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                    return;
//                                }
//                                ClientManager.getClient().search(request, mSearchResponse);


                                connectDeviceLP();
                                ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
                                ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);
//
                                m_myHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isLookPsdBtn){
                                            if (lockLoading != null && lockLoading.isShowing()) {
                                                lockLoading.dismiss();
                                            }
                                            Toast.makeText(context,"唤醒失败，重启手机蓝牙试试吧！",Toast.LENGTH_LONG).show();

                                            ClientManager.getClient().stopSearch();
                                            ClientManager.getClient().disconnect(m_nowMac);
                                            ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
                                            ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);

                                            scrollToFinishActivity();
                                        }
                                    }
                                }, 15 * 1000);

                            }
                        }, 2 * 1000);
                    }





                }else if ("7".equals(type)) {
                    isLookPsdBtn = true;

//                    XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
//                    builder.setBleStateChangeListener(this);
//                    builder.setScanResultCallback(this);
//                    apiClient = builder.build();
//
//                    Log.e("initView===", "==="+deviceuuid);
//
//                    CurRoadBikingActivityPermissionsDispatcher.connectDeviceWithPermissionCheck(CurRoadBikingActivity.this, deviceuuid);
                }

            }

            getCurrentorderBiking(uid, access_token);
            refreshLayout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onResume() {
        isForeground = true;
        isStop = false;
        if (isFrist1){
            isFrist1 = false;
            isRefresh = false;
        }else {
            isRefresh = true;
        }

        super.onResume();
        mapView.onResume();


//        Log.e("biking===", "biking====flagm==="+flagm);

        if (flagm == 1) {
            flagm = 0;
            return;
        }

        Log.e("biking===", "biking====onResume==="+type+"==="+m_nowMac);



        ToastUtil.showMessage(this, "biking===="+internalReceiver);

        getFeedbackStatus();

    }

    /**
     * 链接设备
     * 1. 先进行搜索设备，匹配mac地址是否能搜索到
     * 2. 搜索到就链接设备，取消倒计时；
     * 3. 搜索不到就执行直接连接设备
     */
    protected void connect() {


        Log.e("biking====", "connect===="+m_nowMac+"==="+type);

//        BaseApplication.getInstance().getIBLE().connect(m_nowMac, CurRoadBikingActivity.this);

//        m_myHandler.sendEmptyMessage(0x99);

        try{
//            BaseApplication.getInstance().getIBLE().stopScan();
//            BaseApplication.getInstance().getIBLE().refreshCache();
//            BaseApplication.getInstance().getIBLE().disconnect();
//            BaseApplication.getInstance().getIBLE().close();

//            BaseApplication.getInstance().getIBLE().isEnable();

//            BaseApplication.getInstance().getIBLE().stopScan();
            m_myHandler.sendEmptyMessage(0x99);
            BaseApplication.getInstance().getIBLE().startScan(new OnDeviceSearchListener() {
                @Override
                public void onScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {

//                Log.e("biking===connect", device.getAddress()+"==="+m_nowMac);

//                if (device==null || TextUtils.isEmpty(device.getAddress()) || stopScan){
//                    stopScan = false;
//
//                    Log.e("biking====", "connect===2==="+stopScan);
//
//                    return;
//                }
                    if (device==null||TextUtils.isEmpty(device.getAddress()))return;
                    if (m_nowMac.equalsIgnoreCase(device.getAddress())){

                        Log.e("biking====", "connect===3==="+stopScan);

                        m_myHandler.removeMessages(0x99);

                        BaseApplication.getInstance().getIBLE().connect(m_nowMac, CurRoadBikingActivity.this);
                    }
                }
            });

//            m_myHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            }, 2 * 1000);

        }catch (Exception e){
            ToastUtil.showMessageApp(context,"连接异常，请重试");
        }

    }


    //连接泺平锁
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
            public void onResponseFail(final int code) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("connectDevice===", Code.toString(code));
//                        ToastUtil.showMessageApp(context, Code.toString(code));

                        if (lockLoading != null && lockLoading.isShowing()) {
                            lockLoading.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onResponseSuccess(BleGattProfile profile) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("connectDevice===", "Success==="+Globals.bType);
//                      refreshData(true);

                        getBleRecord();

                        if (lockLoading != null && lockLoading.isShowing()) {
                            lockLoading.dismiss();
                        }

//                        if (Globals.bType == 1) {
//                            ToastUtil.showMessageApp(context, "正在关锁中");
//                            getBleRecord();
//                        }
                    }
                });

            }
        });
    }

    //监听当前连接状态
    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
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

                        ToastUtil.showMessageApp(CurRoadBikingActivity.this,"设备连接成功");
                    }else{
                        isLookPsdBtn = false;

                        ToastUtil.showMessageApp(CurRoadBikingActivity.this,"设备断开连接");
                    }



                    connectDeviceIfNeeded();

                }
            });


//            Globals.isBleConnected = mConnected = (status == STATUS_CONNECTED);
//            refreshData(mConnected);
//            connectDeviceIfNeeded();
        }
    };

    private void connectDeviceIfNeeded() {
        if (!isLookPsdBtn) {
            connectDeviceLP();
        } else {
            ClientManager.getClient().stopSearch();
        }
    }

    //监听当前连接状态
    private final BleConnectStatusListener mConnectStatusListener2 = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
//            BluetoothLog.v(String.format(Locale.getDefault(), "DeviceDetailActivity onConnectStatusChanged %d in %s", status, Thread.currentThread().getName()));

            Log.e("ConnectStatus2===biking", mac+"==="+(status == STATUS_CONNECTED)+"===="+m_nowMac);

            ClientManager.getClient().getStatus(mac, new IGetStatusResponse() {
                @Override
                public void onResponseSuccess(String version, String keySerial, String macKey, String vol) {
//                    cn.qimate.bike.util.UIHelper.dismiss();
                    queryStatusServer(version, keySerial, macKey, vol);
                }

                @Override
                public void onResponseFail(int code) {
                    Log.e("getStatus===", Code.toString(code));
//                    cn.qimate.bike.util.UIHelper.dismiss();
//                    ToastUtil.showMessageApp(context, Code.toString(code));
                }

            });

        }
    };

    //获取服务器的加密信息
    private void queryStatusServer(String version, String keySerial, String macKey, String vol) {
        Log.e("queryStatusServer===", "version:" + version + " keySerial:" + keySerial + " macKey:" + macKey + " vol:" + vol);
//        this.version = version;
        int timestamp = (int) StringUtils.getCurrentTimestamp();

//        UIHelper.showProgress(this, "get_bike_server");
        OkHttpClientManager.getInstance().Rent(macKey, keySerial, timestamp, new ResultCallback<RRent>() {

            @Override
            public void onResponse(final RRent rRent) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (rRent.getResult() >= 0) {
                            RRent.ResultBean resultBean = rRent.getInfo();
                            openBleLock(resultBean);
                        }
                        else {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                            ToastUtil.showMessageApp(context, ""+rRent.getResult());
                        }
                    }
                });

            }

            @Override
            public void onError(Request request, final Exception e) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }

                        ToastUtil.showMessageApp(context, e.getMessage());
                    }
                });

            }

        });
    }

    //与设备，开锁
    private void openBleLock(RRent.ResultBean resultBean) {
//        UIHelper.showProgress(this, "open_bike_status");
//        ClientManager.getClient().openLock(mac, "18112348925", resultBean.getServerTime(),

//        Log.e("openBleLock===", resultBean.getServerTime()+"==="+resultBean.getKeys()+"==="+resultBean.getEncryptionKey());

        Log.e("biking===openBleLock", serverTime+"==="+keys+"==="+encryptionKey);

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

                                Log.e("openLock===Fail", Code.toString(code));
//                                ToastUtil.showMessageApp(context, Code.toString(code));
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

                                getBleRecord();

                                Log.e("openLock===Success", "===");


                                ToastUtil.showMessageApp(context, "开锁成功");

//                                if("6".equals(type)){
//                                    lookPsdBtn.setText("临时上锁");
//                                    SharedPreferencesUrls.getInstance().putString("tempStat","0");
//                                }
                            }
                        });


//                        ClientManager.getClient().stopSearch();
//                        ClientManager.getClient().disconnect(m_nowMac);
//                      ClientManager.getClient().unnotifyClose(mac, mCloseListener);
//                        ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener2);

                    }
                });
    }

//    //与设备，获取记录
//    private void getBleRecord() {
//        ClientManager.getClient().getRecord(m_nowMac, new IGetRecordResponse() {
//            @Override
//            public void onResponseSuccess(String phone, String bikeTradeNo, String timestamp,
//                                          String transType, String mackey, String index, String cap, String vol) {
//                Log.e("biking===getBleRecord", "Success===");
//
//                deleteBleRecord(bikeTradeNo);
//            }
//
//            @Override
//            public void onResponseSuccessEmpty() {
////                ToastUtil.showMessageApp(context, "record empty");
//                Log.e("biking===getBleRecord", "Success===Empty");
//            }
//
//            @Override
//            public void onResponseFail(int code) {
//                Log.e("biking===getBleRecord", Code.toString(code));
//                ToastUtil.showMessageApp(context, Code.toString(code));
//            }
//        });
//    }

    //与设备，获取记录
    private void getBleRecord() {
//        UIHelper.showProgress(this, R.string.get_bike_record);
        ClientManager.getClient().getRecord(m_nowMac, new IGetRecordResponse() {

            @Override
            public void onResponseSuccess(String phone, final String bikeTradeNo, String timestamp, final String transType, final String mackey, String index, final int Major, final int Minor, String vol) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("getBleRecord===###", transType+"==Major:"+ Major +"---Minor:"+Minor+"---mackey:"+mackey);

                        if(BaseApplication.getInstance().isTestLog()){
                            macText.setText(Major+"==="+Minor+"==="+macList);
                        }

//                      ToastUtil.showMessageApp(context, "Major:"+ Major +"---Minor:"+Minor);

                        transtype = transType;
                        major = Major;
                        minor = Minor;

                        SharedPreferencesUrls.getInstance().putInt("major", major);

//                      m_myHandler.sendEmptyMessage(9);

                        deleteBleRecord(bikeTradeNo);
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
//                        ToastUtil.showMessageApp(context, Code.toString(code));

                        if (lockLoading != null && lockLoading.isShowing()) {
                            lockLoading.dismiss();
                        }
                    }
                });

            }

            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("getBleRecord===", Code.toString(code));
//                        ToastUtil.showMessageApp(context, Code.toString(code));

                        if (lockLoading != null && lockLoading.isShowing()) {
                            lockLoading.dismiss();
                        }
                    }
                });
            }
        });
    }


    //与设备，删除记录
    private void deleteBleRecord(String tradeNo) {
        ClientManager.getClient().deleteRecord(m_nowMac, tradeNo, new IGetRecordResponse() {
//            @Override
//            public void onResponseSuccess(String phone, String bikeTradeNo, String timestamp, String transType, String mackey, String index, String cap, String vol) {
//
//                Log.e("biking=deleteBleRecord", "Success===");
//
//                deleteBleRecord(bikeTradeNo);
//            }

            @Override
            public void onResponseSuccess(String phone, final String bikeTradeNo, String timestamp, final String transType, String mackey, String index, final int Major, final int Minor, String vol) {

//                m_myHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        deleteBleRecord(bikeTradeNo);
//                    }
//                });

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("deleteBleRecord===", transType+"==Major:"+ Major +"---Minor:"+Minor);

                        transtype = transType;
                        major = Major;
                        minor = Minor;

                        deleteBleRecord(bikeTradeNo);
                    }
                });
            }

            @Override
            public void onResponseSuccessEmpty() {
                Log.e("biking=deleteBleRecord", "Success===Empty");

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
//                        ToastUtil.showMessageApp(context, Code.toString(code));

                        if (lockLoading != null && lockLoading.isShowing()) {
                            lockLoading.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("biking=deleteBleRecord", Code.toString(code));
//                        ToastUtil.showMessageApp(context, Code.toString(code));

                        if (lockLoading != null && lockLoading.isShowing()) {
                            lockLoading.dismiss();
                        }
                    }
                });

            }
        });
    }

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

                    getBleRecord();

//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
//                    ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);
                }
            });

//            m_myHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e("onNotifyClose===", "====");
//
//                    ToastUtil.showMessageApp(context,"锁已关闭");
//
//                    if("6".equals(type)){
//                        lookPsdBtn.setText("再次开锁");
//                        SharedPreferencesUrls.getInstance().putString("tempStat","1");
//                    }
//
//                    getBleRecord();
//                }
//            }, 2*1000);

        }
    };

    @Override
    public void onTimeOut() {

    }

    //type2
    @Override
    public void onDisconnect(int state) {
        Log.e("biking===onDisconnect", "==="+state);

        isLookPsdBtn = false;

        ToastUtil.showMessageApp(CurRoadBikingActivity.this,"设备断开连接");

//        m_myHandler.sendEmptyMessageDelayed(0, 1000);
        if (!BaseApplication.getInstance().getIBLE().isEnable()){
           return;
        }
//                BaseApplication.getInstance().getIBLE().resetLock();
//                BaseApplication.getInstance().getIBLE().resetBluetoothAdapter();
        BaseApplication.getInstance().getIBLE().connect(m_nowMac, CurRoadBikingActivity.this);
    }

    //type2
    @Override
    public void onServicesDiscovered(String name, String address) {
        Log.e("biking===onServicesDis", name+"==="+address);

        isLookPsdBtn = true;

        ToastUtil.showMessageApp(CurRoadBikingActivity.this,"设备连接成功");

        BaseApplication.getInstance().getIBLE().stopScan();

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


    protected void handleReceiver(final Context context, final Intent intent) {

        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                String action = intent.getAction();
                String data = intent.getStringExtra("data");

                switch (action) {
                    case Config.TOKEN_ACTION:
                        Log.e("biking===TOKEN_ACTION", isEndBtn+"==="+stopScan);
//                        lookPsdBtn.setEnabled(true);


                        isEndBtn = false;
                        isStop = true;

                        if (stopScan){
                            stopScan = false;
                            break;
                        }

                        if (customDialog3 != null && customDialog3.isShowing()){
                            customDialog3.dismiss();
                        }
//                if (customDialog4 != null && customDialog4.isShowing()){
//                    customDialog4.dismiss();
//                }

                        m_myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                BaseApplication.getInstance().getIBLE().getBattery();
                            }
                        }, 500);
                        if (null != lockLoading && lockLoading.isShowing()) {
                            lockLoading.dismiss();
//                    lockLoading = null;
                        }


//                        switch (flag){
//                            case 0:
//                                break;
//                            case 1:
//                                //开锁
//                                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                                    loadingDialog.setTitle("正在开锁");
//                                    loadingDialog.show();
//                                }
//                                BaseApplication.getInstance().getIBLE().openLock();
//                                break;
//                            case 2:
//                                macList2 = new ArrayList<> (macList);
//                                BaseApplication.getInstance().getIBLE().getLockStatus();
//                                break;
//                            default:
//                                break;
//                        }
                        flag = 0;
                        break;
                    case Config.BATTERY_ACTION:
                        Log.e("biking===", "BATTERY_ACTION==="+stopScan);

                        macList2 = new ArrayList<> (macList);

                        if(isTemp){
                            isTemp = false;
                            BaseApplication.getInstance().getIBLE().openLock();
                        }else{
                            BaseApplication.getInstance().getIBLE().getLockStatus();
                        }

                        break;
                    case Config.OPEN_ACTION:
                        isStop = true;

                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                        if (lockLoading != null && lockLoading.isShowing()){
                            lockLoading.dismiss();
                        }
                        if (TextUtils.isEmpty(data)) {
                            ToastUtil.showMessageApp(context,"开锁失败,请重试");
                        } else {
                            ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");
                        }
                        break;
                    case Config.CLOSE_ACTION:
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                        if (lockLoading != null && lockLoading.isShowing()){
                            lockLoading.dismiss();
                        }
                        if (TextUtils.isEmpty(data)) {

                        } else {

                        }
                        break;
                    case Config.LOCK_STATUS_ACTION:
                        Log.e("biking===", "LOCK_STATUS_ACTION==="+isStop);

                        isStop = true;

                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                        if (lockLoading != null && lockLoading.isShowing()){
                            lockLoading.dismiss();
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

//                            clickCountDeal();
                        }
                        break;
                    case Config.LOCK_RESULT:
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                        if (lockLoading != null && lockLoading.isShowing()){
                            lockLoading.dismiss();
                        }
                        ToastUtil.showMessageApp(context,"恭喜您，您已成功上锁");
                        Log.e("biking===","biking==="+BikeFragment.screen);



//                //自动还车
//                if(SharedPreferencesUrls.getInstance().getBoolean("switcher", false)) break;
//
//                startXB();
//
//                if (lockLoading != null && !lockLoading.isShowing()){
//                    lockLoading.setTitle("还车点确认中");
//                    lockLoading.show();
//                }
//
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            int n=0;
//                            while(macList.size() == 0){
//
//                                Thread.sleep(1000);
//                                n++;
//
//                                Log.e("biking===","biking=n=="+n);
//
//                                if(n>=6) break;
//
//                            }
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                        m_myHandler.sendEmptyMessage(3);
//                    }
//                }).start();

                        break;
                }
            }
        });


    }


    private void getCurrentorderBiking(final String uid, final String access_token){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        HttpHelper.post(this, Urls.getCurrentorder, params, new TextHttpResponseHandler() {
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
//                              ToastUtil.showMessageApp(context,"数据更新成功");
                                Log.e("biking===", "getCurrentorderBiking===="+result.getData());

                                if ("[]".equals(result.getData()) || 0 == result.getData().length()){
                                    ToastUtil.showMessageApp(context,"当前无行程");
                                    BaseApplication.getInstance().getIBLE().refreshCache();
                                    BaseApplication.getInstance().getIBLE().close();
                                    BaseApplication.getInstance().getIBLE().disconnect();

                                    SharedPreferencesUrls.getInstance().putBoolean("isStop", true);
                                    SharedPreferencesUrls.getInstance().putString("m_nowMac", "");

                                    scrollToFinishActivity();
                                }else {
                                    CurRoadBikingBean bean = JSON.parseObject(result.getData(),CurRoadBikingBean.class);
                                    bikeCode = bean.getCodenum();
                                    bikeCodeText.setText(bikeCode);
                                    time.setText(bean.getSt_time());
                                    oid = bean.getOid();
                                    osn = bean.getOsn();
                                    password = bean.getPassword();
                                    type = bean.getType();
                                    m_nowMac = bean.getMacinfo();
                                    force_backcar = bean.getForce_backcar();

//                                    force_backcar = 1;

                                    if(BaseApplication.getInstance().isTest()){
                                        type = "5";
                                        if("40001101".equals(bikeCode)){
//                                          m_nowMac = "3C:A3:08:AE:BE:24"; //"3C:A3:08:AE:BE:24";
                                            m_nowMac = "3C:A3:08:CD:9F:47";
                                        }else{
                                            type = "6";
                                            m_nowMac = "A4:34:F1:7B:BF:9A";  // "A4:34:F1:7B:BF:9A";
                                        }
                                    }
//                                    type = "7";

                                    Log.e("getCurrentBiking===", SharedPreferencesUrls.getInstance().getString("tempStat","0")+"==="+type+"==="+oid);

//                                  hintText.setText("校内地图红色区域关锁，并点击结束");

                                    if ("1".equals(bean.getType())){
                                        hintText.setText("还车须至校园地图"+(("4".equals(type) || "7".equals(type))?"绿色":"红色")+"区域，关锁并拨乱密码后点击结束！");
                                        lookPsdBtn.setText("查看密码");
                                    }else {
                                        hintText.setText("还车须至校园地图"+(("4".equals(type) || "7".equals(type))?"绿色":"红色")+"区域，距车1米内点击结束！");

                                        if ("4".equals(type) || "7".equals(type)) {

                                            ebikeInfoLayout.setVisibility(View.VISIBLE);

                                            tn=0;
                                            if (ebikeInfoThread == null) {
                                                Runnable ebikeInfoRunnable = new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        while (isWaitEbikeInfo) {

                                                            m_myHandler.sendEmptyMessage(4);

//                                                            if(tn%10==0){
//                                                                m_myHandler.sendEmptyMessage(4);
//                                                            }

                                                            try {
                                                                Thread.sleep(30 * 1000);
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }

//                                                            tn++;

                                                        }
                                                    }
                                                };

                                                ebikeInfoThread = new Thread(ebikeInfoRunnable);
                                                ebikeInfoThread.start();
                                            }

                                            if ("0".equals(SharedPreferencesUrls.getInstance().getString("tempStat", "0"))) {
                                                lookPsdBtn.setText("临时上锁");
                                            } else {
                                                lookPsdBtn.setText("再次开锁");
                                            }
                                        }
//                                        else if ("5".equals(type) || "6".equals(type)){
//
//                                            if ("5".equals(type)){
//                                                lookPsdBtn.setText("再次开锁");
//                                            }else{
//                                                if("0".equals(SharedPreferencesUrls.getInstance().getString("tempStat","0"))){
//                                                    lookPsdBtn.setText("临时上锁");
//                                                }else{
//                                                    lookPsdBtn.setText("再次开锁");
//                                                }
//                                            }
//
//                                        }
                                        else{
                                            lookPsdBtn.setText("再次开锁");
                                        }

                                        Log.e("biking===2", "getCurrentorderBiking===="+mBluetoothAdapter.isEnabled()+"==="+m_nowMac);

                                    }
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

    private void ebikeInfo(String uid, String access_token){
        Log.e("biking===000", "ebikeInfo===="+bikeCode);

        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("access_token",access_token);
        params.put("tokencode", bikeCode);
//        params.put("version", "");
        HttpHelper.get(this, Urls.ebikeInfo, params, new TextHttpResponseHandler() {
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
                            if (result.getFlag().equals("Success")) {
//                                ToastUtil.showMessageApp(context,"电量更新成功");
                                Log.e("biking===", "ebikeInfo===="+result.getData());

                                EbikeInfoBean bean = JSON.parseObject(result.getData(), EbikeInfoBean.class);
                                electricity.setText(bean.getElectricity());
                                mileage.setText(bean.getMileage());

                                if("1".equals(bean.getIs_locked())){
                                    lookPsdBtn.setText("再次开锁");
//                                    ToastUtil.showMessageApp(context,"关锁成功");

                                    SharedPreferencesUrls.getInstance().putString("tempStat","1");
                                }else{
                                    lookPsdBtn.setText("临时上锁");
//                                    ToastUtil.showMessageApp(context,"关锁成功");

                                    SharedPreferencesUrls.getInstance().putString("tempStat","0");
                                }

                            } else {
                                ToastUtil.showMessageApp(context, result.getMsg());
                            }
                        } catch (Exception e) {
                        }
//                        if (loadingDialog != null && loadingDialog.isShowing()){
//                            loadingDialog.dismiss();
//                        }
                    }
                });

            }
        });
    }

    @Override
    protected void onPause() {
        isForeground = false;
        if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
        if (lockLoading != null && lockLoading.isShowing()){
            lockLoading.dismiss();
        }
        super.onPause();
        mapView.onPause();

        ToastUtil.showMessage(this, "biking====onPause");

    }



    @Override
    protected void onStop() {
        super.onStop();
//        screen = false;
//        change = false;

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
//          isStop = true;
        isForeground = false;
        if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
        if (lockLoading != null && lockLoading.isShowing()){
            lockLoading.dismiss();
        }

        Log.e("biking====onDestroy", type+"==="+m_nowMac);

        if("5".equals(type)  || "6".equals(type)){


            ClientManager.getClient().stopSearch();
            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
            ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);

        }else if("7".equals(type)){
            if (apiClient != null) {
                apiClient.onDestroy();
            }
        }else{
            BaseApplication.getInstance().getIBLE().stopScan();
            BaseApplication.getInstance().getIBLE().refreshCache();
            BaseApplication.getInstance().getIBLE().close();
            BaseApplication.getInstance().getIBLE().disconnect();



        }



        super.onDestroy();
        mapView.onDestroy();


        ToastUtil.showMessage(this, "biking===onDestroy==="+m_nowMac);

        if (customDialog != null){
            customDialog.dismiss();
        }
        if (customDialog3 != null){
            customDialog3.dismiss();
        }
        if (customDialog4 != null){
            customDialog4.dismiss();
        }
        if (customDialog5 != null){
            customDialog5.dismiss();
        }
        if (customDialog6 != null){
            customDialog6.dismiss();
        }
        if (customDialog7 != null){
            customDialog7.dismiss();
        }
        if (customDialog8 != null){
            customDialog8.dismiss();
        }
        if (customDialog9 != null){
            customDialog9.dismiss();
        }

        if(testDialog != null){
            testDialog.dismiss();
        }
        if(discountDialog != null){
            discountDialog.dismiss();
        }

        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }


        stopXB();

        if (broadcastReceiver1 != null) {
          unregisterReceiver(broadcastReceiver1);
          broadcastReceiver1 = null;
        }

        closeBroadcast();

        isWaitEbikeInfo = false;
        if (ebikeInfoThread != null) {
            ebikeInfoThread.interrupt();
            ebikeInfoThread = null;
        }

        m_myHandler.removeCallbacksAndMessages(null);
    }

    private void closeBroadcast() {
        try {
            if (internalReceiver != null) {
                unregisterReceiver(internalReceiver);
                internalReceiver = null;
            }

            ToastUtil.showMessage(this, "main====closeBroadcast===" + internalReceiver);

        } catch (Exception e) {
            ToastUtil.showMessage(this, "eee====" + e);
        }
    }


    BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("biking=bReceiver1", "==="+m_nowMac);

            getCurrentorderBiking(SharedPreferencesUrls.getInstance().getString("uid",""), SharedPreferencesUrls.getInstance().getString("access_token",""));
            getFeedbackStatus();
        }
    };


    private void getFeedbackStatus(){
        RequestParams params = new RequestParams();
        params.put("telphone",SharedPreferencesUrls.getInstance().getString("userName",""));
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
//						        ToastUtil.showMessageApp(context,"数据更新成功==="+SharedPreferencesUrls.getInstance().getBoolean("isStop",true));

                                Log.e("biking=", "getFeedbackStatus==="+result.data+"==="+SharedPreferencesUrls.getInstance().getBoolean("isStop",true));

                                if("2".equals(result.data) && !SharedPreferencesUrls.getInstance().getBoolean("isStop",true)){
                                    customDialog.show();
                                }else{
                                    customDialog.dismiss();
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


    private View.OnClickListener myOnClickLister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.ui_fristView_exImage_1:
//                    if (dialog != null && dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                    UIHelper.goWebViewAct(context,"使用说明",Urls.bluecarisee);
//                    break;
//                case R.id.ui_fristView_exImage_2:
//                    if (dialog != null && dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                    UIHelper.goWebViewAct(context,"使用说明",Urls.useHelp);
//                    break;
                case R.id.ui_fristView_closeBtn:
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void closeEbike_XA(){
//        closeLock_XA();

//        m_myHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                xa_state=4;
//
//                XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
//                builder.setBleStateChangeListener(CurRoadBikingActivity.this);
//                builder.setScanResultCallback(CurRoadBikingActivity.this);
//                apiClient = builder.build();
//
//
//                CurRoadBikingActivityPermissionsDispatcher.connectDeviceWithPermissionCheck(CurRoadBikingActivity.this, deviceuuid);
//
//            }
//        });

        Log.e("closeEbike_X===", "==="+deviceuuid);

        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("oid",oid);
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

                            Log.e("biking===", "closeEbike_XA0===="+responseString);

                            if (result.getFlag().equals("Success")) {
//                              ToastUtil.showMessage(context,"数据更新成功");

                                info = result.getInfo();

                                if ("0".equals(result.getData())){
                                    if (loadingDialog != null && loadingDialog.isShowing()){
                                        loadingDialog.dismiss();
                                    }

                                    Log.e("biking===", "closeEbike_XA1===="+deviceuuid+"==="+result.getData());

                                    ToastUtil.showMessageApp(context,"关锁成功");

                                    submit(uid, access_token);
                                }else{
//                                    ToastUtil.showMessageApp(context,"关锁失败");

                                    Log.e("biking===2", "closeEbike_XA2===="+deviceuuid+"==="+result.getData());

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        xa_state=4;

                                                        XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                                                        builder.setBleStateChangeListener(CurRoadBikingActivity.this);
                                                        builder.setScanResultCallback(CurRoadBikingActivity.this);
                                                        apiClient = builder.build();


                                                        CurRoadBikingActivityPermissionsDispatcher.connectDeviceWithPermissionCheck(CurRoadBikingActivity.this, deviceuuid);

                                                    }
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();


                                }
                            } else {
                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }

                                Log.e("biking===", "closeEbike_XA2===="+result.getData());

                                ToastUtil.showMessageApp(context,result.getMsg());
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

    void closeLock_XA(){

        apiClient.setDefend(true, new BleCallback() {
            @Override
            public void onResponse(final Response response) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("biking_defend===", type+"==="+deviceuuid+"==="+response.toString());

                        if(response.code==0){

                            macList2 = new ArrayList<> (macList);
                            submit(uid, access_token);

                        }else if(response.code==6){
                            ToastUtil.showMessageApp(context,"车辆未停止，请停止后再试");
                        }else{
//                            ToastUtil.showMessageApp(context,"关锁失败");

                            if("108".equals(info)){
                                Log.e("biking_defend===1", "====");

//                                ToastUtil.showMessageApp(context,"关锁成功");

                                submit(uid, access_token);
                            }else{
                                Log.e("biking_defend===2", "====");

                                ToastUtil.showMessageApp(context,"关锁失败，请重试");
                            }
                        }

                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                });
            }
        });

//        m_myHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.e("closeLock3===", "===");
//
//                if(!bleService.connect){
//                    cn++;
//
//                    if(cn<5){
//                        closeLock3();
//                    }else{
//                        customDialog4.show();
//                        return;
//                    }
//
//                }else{
//                    bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});
//
//                    m_myHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.e("closeLock3===4_3", "==="+m_nowMac);
//
//                            button8();
//                            button9();
//                            button2();    //设防
//
//                            closeLock2();
//                        }
//                    }, 500);
//
//                }
//
//            }
//        }, 2 * 1000);
    }

    void closeLock_XA_temp() {

        apiClient.setDefend(true, new BleCallback() {
            @Override
            public void onResponse(final Response response) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("biking_defend===ct" + type, response.toString());

                        if(response.code==0){
                            lookPsdBtn.setText("再次开锁");
                            ToastUtil.showMessageApp(context,"关锁成功");

                            SharedPreferencesUrls.getInstance().putString("tempStat","1");
                        }else{
                            ToastUtil.showMessageApp(context,"关锁失败");
                        }


                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    void openLock_XA_temp() {

        apiClient.setDefend(false, new BleCallback() {
            @Override
            public void onResponse(final Response response) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("biking_defend===ot" + type, response.toString());

                        if(response.code==0){
                            lookPsdBtn.setText("临时上锁");
                            ToastUtil.showMessageApp(context,"开锁成功");

                            SharedPreferencesUrls.getInstance().putString("tempStat","0");
                        }else{
                            ToastUtil.showMessageApp(context,"开锁失败");
                        }

                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH})
    public void connectDevice(String imei) {
        if (apiClient != null) {
            apiClient.connectToIMEI(imei);
        }
    }

    //type7
    @Override
    public void onConnect(BluetoothDevice bluetoothDevice) {
        isConnect = true;
        Log.e("biking===Xiaoan", "===Connect==="+xa_state);

        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                isLookPsdBtn = true;
                ToastUtil.showMessageApp(CurRoadBikingActivity.this,"设备连接成功");
            }
        });


        if(xa_state>0){
            m_myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                apiClient.setAcc(false, new BleCallback() {
//                    @Override
//                    public void onResponse(final Response response) {
//                        Log.e("acc===", response.toString());
//                    }
//                });
//                getCurrentorder2(uid, access_token);

                    if (lockLoading != null && lockLoading.isShowing()){
                        lockLoading.dismiss();
                    }

                    if(xa_state==1){
                        closeEbike_XA();
                    }else if(xa_state==2){
                        closeLock_XA_temp();
                    }else if(xa_state==3){
                        openLock_XA_temp();
                    }else if(xa_state==4){
                        closeLock_XA();
                    }

                }
            }, 2 * 1000);
        }

    }

    //type7
    @Override
    public void onDisConnect(BluetoothDevice bluetoothDevice) {
        isConnect = false;

        isLookPsdBtn = false;
        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showMessageApp(CurRoadBikingActivity.this,"设备断开连接");
                Log.e("biking===Xiaoan", "===DisConnect");

                if(xa_state==4){
                    if("108".equals(info)){
                        Log.e("biking===Xiaoan1", "====");

                        ToastUtil.showMessageApp(context,"关锁成功");

                        submit(uid, access_token);
                    }else{
                        Log.e("biking===Xiaoan2", "====");

                        ToastUtil.showMessageApp(context,"蓝牙连接失败，请重试");
                    }
                }


            }
        });


//        apiClient.disConnect();


//        m_myHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                CurRoadBikingActivityPermissionsDispatcher.connectDeviceWithPermissionCheck(CurRoadBikingActivity.this, deviceuuid);
//            }
//        }, 1000);

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

    void checkConnectCloseTemp(){
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("checkConnect===", "===");

                if(!bleService.connect){
                    cn++;

                    if(cn<5){
                        checkConnectCloseTemp();
                    }else{
                        if (lockLoading != null && lockLoading.isShowing()){
                            lockLoading.dismiss();
                        }

                        customDialog6.show();
                        return;
                    }

                }else{
                    if (lockLoading != null && lockLoading.isShowing()){
                        lockLoading.dismiss();
                    }

                    closeEbikeTemp();
                }

            }
        }, 2 * 1000);
    }

    public void closeEbikeTemp(){

        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("oid",oid);
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

                                Log.e("biking===", "closeEbike===="+responseString);

                                if ("0".equals(result.getData())){
                                    lookPsdBtn.setText("再次开锁");
                                    ToastUtil.showMessageApp(context,"关锁成功");

                                    SharedPreferencesUrls.getInstance().putString("tempStat","1");
                                } else {
                                    ToastUtil.showMessageApp(context,"关锁失败");

                                    if("4".equals(type)){
                                        bleService.connect(m_nowMac);
                                        cn = 0;

                                        temporaryLock();
                                    }else{

                                        if(isConnect){
                                            closeLock_XA_temp();
                                        }else{
                                            xa_state = 2;

                                            XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                                            builder.setBleStateChangeListener(CurRoadBikingActivity.this);
                                            builder.setScanResultCallback(CurRoadBikingActivity.this);
                                            apiClient = builder.build();

                                            m_myHandler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (lockLoading != null && lockLoading.isShowing()){
                                                        lockLoading.dismiss();

                                                        if(!isConnect){
                                                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                                            customBuilder.setTitle("连接失败").setMessage("蓝牙连接失败，请靠近车锁，重启软件后再试")
                                                                    .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.cancel();
                                                                        }
                                                                    });
                                                            customBuilder.create().show();
                                                        }
                                                    }
                                                }
                                            }, 10 * 1000);

                                            CurRoadBikingActivityPermissionsDispatcher.connectDeviceWithPermissionCheck(CurRoadBikingActivity.this, deviceuuid);
                                        }
                                    }

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
        });
    }

    void temporaryLock(){
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("temporaryLock===", "===");

                if(!bleService.connect){
                    cn++;

                    if(cn<5){
                        temporaryLock();
                    }else{
                        customDialog6.show();
                        return;
                    }

                }else{

                    bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});

                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("temporaryLock===4_3", "==="+m_nowMac);

                            button8();
                            button9();
                            button2();    //设防

                            closeLock();
                        }
                    }, 500);

//                    bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});
//
//                    m_myHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.e("temporaryLock===4_3", "==="+m_nowMac);
//
//                            button8();
//                            button9();
//                            button2();
//
//                            closeLock();
//                        }
//                    }, 500);
                }

            }
        }, 2 * 1000);
    }

    void closeLock(){
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("temporaryLock===4_4", bleService.cc+"==="+"B1 2A 80 00 00 5B ".equals(bleService.cc));

                if("B1 2A 80 00 00 5B ".equals(bleService.cc)){
                    Log.e("temporaryLock===4_5", oid+"==="+bleService.cc);

                    lookPsdBtn.setText("再次开锁");
                    ToastUtil.showMessageApp(context,"关锁成功");

                    SharedPreferencesUrls.getInstance().putString("tempStat","1");



                }else{
//                    button9();
//                    button2();
//
//                    closeLock();
//
//                    if (loadingDialog != null && !loadingDialog.isShowing()) {
//                        loadingDialog.setTitle("正在加载");
//                        loadingDialog.show();
//                    }


//                    bleService.connect(m_nowMac);
//
//                    cn = 0;
//                    temporaryLock();

//                    ToastUtil.showMessageApp(context,"关锁失败，请重试");
                      customDialog7.show();
                }

                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }


                Log.e("temporaryLock===4_6", "==="+bleService.cc);

            }
        }, 500);
    }

    void checkConnectOpenTemp(){
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("checkConnect===", "===");

                if(!bleService.connect){
                    cn++;

                    if(cn<5){
                        checkConnectOpenTemp();
                    }else{
                        if (lockLoading != null && lockLoading.isShowing()){
                            lockLoading.dismiss();
                        }

                        customDialog6.show();
                        return;
                    }

                }else{
                    if (lockLoading != null && lockLoading.isShowing()){
                        lockLoading.dismiss();
                    }

//                  openEbike();
                }

            }
        }, 2 * 1000);
    }

    public void openEbike(){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        HttpHelper.post(this, Urls.openEbike, params, new TextHttpResponseHandler() {
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
//                        ToastUtil.showMessage(context,"数据更新成功");

                                Log.e("biking===", "openEbike===="+result.getData());

                                lookPsdBtn.setText("临时上锁");
                                ToastUtil.showMessageApp(context,"开锁成功");

                                SharedPreferencesUrls.getInstance().putString("tempStat","0");
                            } else {
                                ToastUtil.showMessageApp(context,"开锁失败");

                                if ("4".equals(type)) {
                                    bleService.connect(m_nowMac);

                                    cn=0;

                                    openLock();
                                }else{
                                    if(isConnect){
                                        openLock_XA_temp();
                                    }else{
                                        xa_state = 3;

                                        XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                                        builder.setBleStateChangeListener(CurRoadBikingActivity.this);
                                        builder.setScanResultCallback(CurRoadBikingActivity.this);
                                        apiClient = builder.build();

                                        CurRoadBikingActivityPermissionsDispatcher.connectDeviceWithPermissionCheck(CurRoadBikingActivity.this, deviceuuid);
                                    }
                                }

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
        });
    }

    void openLock(){
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("checkConnect===", "===");

                if(!bleService.connect){
                    cn++;

                    if(cn<5){
                        openLock();
                    }else{
                        customDialog6.show();
                        return;
                    }

                }else{
                    bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});

                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("openLock===4_3", "==="+m_nowMac);

                            button8();
                            button9();
                            button3();  //启动

                            openLock2();
                        }
                    }, 500);
                }

            }
        }, 2 * 1000);
    }

    void openLock2() {
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("openLock2===4_4", bleService.cc+"==="+"B1 25 80 00 00 56 ".equals(bleService.cc));

                if("B1 25 80 00 00 56 ".equals(bleService.cc)){
                    Log.e("openLock2===4_5", oid+"==="+bleService.cc);
                    lookPsdBtn.setText("临时上锁");
                    ToastUtil.showMessageApp(context,"开锁成功");

                    SharedPreferencesUrls.getInstance().putString("tempStat","0");
                }else{
//                  openLock2();

                    customDialog8.show();

//                  ToastUtil.showMessageApp(context,"开锁失败，请重试");
                }

                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }

                Log.e("openLock2===4_6", "==="+bleService.cc);

            }
        }, 500);
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

        int crc = (int) ByteUtil.crc32(getfdqId(bleid));
        byte cc[] = ByteUtil.intToByteArray(crc);
        ioBuffer.writeByte(cc[0] ^ cc[3]);
        ioBuffer.writeByte(cc[1] ^ cc[2]);
        ioBuffer.writeInt(0);
        bleService.write(toBody(ioBuffer.readableBytes()));
    }

    //启动
    void button3() {
        IoBuffer ioBuffer = IoBuffer.allocate(20);
        byte[] cmd = sendCmd("00000101", "00000000");
        ioBuffer.writeBytes(cmd);
        bleService.write(toBody(ioBuffer.readableBytes()));
    }

    //泺平锁临时上锁
    private void temporaryAction() {
//        UIHelper.showProgress(this, R.string.temporaryAction);


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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.mainUI_title_rightBtn:
                Intent intent = new Intent(context,ClientServiceActivity.class);
//                intent.putExtra("bikeCode",bikeCode);
//                startActivity(intent);
//                scrollToFinishActivity();

                xa_state=4;

                XiaoanBleApiClient.Builder xbuilder = new XiaoanBleApiClient.Builder(context);
                xbuilder.setBleStateChangeListener(CurRoadBikingActivity.this);
                xbuilder.setScanResultCallback(CurRoadBikingActivity.this);
                apiClient = xbuilder.build();


                CurRoadBikingActivityPermissionsDispatcher.connectDeviceWithPermissionCheck(CurRoadBikingActivity.this, deviceuuid);


                break;
            case R.id.switcher:
                SharedPreferencesUrls.getInstance().putBoolean("switcher", switcher.isChecked());

                if(switcher.isChecked()){
                    Log.e("biking===switcher1", "onClick==="+switcher.isChecked());
                }else{
                    Log.e("biking===switcher2", "onClick==="+switcher.isChecked());
                }
                break;
            case R.id.ll_role:
                CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                customBuilder.setType(2).setTitle("临时停车说明").setMessage("①默认选择“否”，还车点关锁后打开软件，订单将自动结束；\n②如选择“是”，还车点关锁，订单将不能自动结束，每次还车需要点击“结束用车”。\n③无论选择“是”或“否”，非还车点关锁订单都不能结束，可以点击“再次开锁”骑回还车点。")
                        .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                customBuilder.create().show();
                break;
            case R.id.curRoadUI_biking_lookPsdBtn:
                String tvAgain = lookPsdBtn.getText().toString().trim();

                Log.e("biking===lookPsdBtn", tvAgain+"==="+m_nowMac);

                if("7".equals(type)){
                    xa_state = 0;
                    XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                    builder.setBleStateChangeListener(this);
                    builder.setScanResultCallback(this);
                    apiClient = builder.build();
                    CurRoadBikingActivityPermissionsDispatcher.connectDeviceWithPermissionCheck(CurRoadBikingActivity.this, deviceuuid);
                }else{
//                    if(!isLookPsdBtn){
//                        ToastUtil.showMessageApp(context, "蓝牙未连接，请重试");
//                        break;
//                    }
                }

                if ("查看密码".equals(tvAgain)){
                    customBuilder = new CustomDialog.Builder(this);
                    customBuilder.setTitle("查看密码").setMessage("解锁码："+password)
                            .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    customBuilder.create().show();
                }else if ("临时上锁".equals(tvAgain)){

                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("正在加载");
                        loadingDialog.show();
                    }

                    if("4".equals(type) || "7".equals(type)){
                        closeEbikeTemp();
                    }else{
                        temporaryAction();
                    }

                }else if ("再次开锁".equals(tvAgain)){
                    flag = 1;

                    if("3".equals(type)){
                        openAgain();
                    }else if("4".equals(type) || "7".equals(type)){
                        openEbike();
                    }else if("5".equals(type) || "6".equals(type)){
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
                            if (loadingDialog != null && !loadingDialog.isShowing()) {
                                loadingDialog.setTitle("正在唤醒车锁");
                                loadingDialog.show();
                            }

                            Log.e("lookPsdBtn===", "==="+isLookPsdBtn);

                            if(!isLookPsdBtn){   //没连上
                                ClientManager.getClient().stopSearch();
                                ClientManager.getClient().disconnect(m_nowMac);
                                ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
                                ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);

                                SearchRequest request = new SearchRequest.Builder()      //duration为0时无限扫描
                                        .searchBluetoothLeDevice(0)
                                        .build();

                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                ClientManager.getClient().search(request, mSearchResponse2);
                            }else{
                                ClientManager.getClient().getStatus(m_nowMac, new IGetStatusResponse() {
                                    @Override
                                    public void onResponseSuccess(String version, String keySerial, String macKey, String vol) {
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
                                        m_myHandler.post(new Runnable() {
                                            @Override
                                            public void run() {

                                                ClientManager.getClient().disconnect(m_nowMac);
                                                ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
                                                ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);

                                                m_myHandler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (loadingDialog != null && loadingDialog.isShowing()){
                                                            loadingDialog.dismiss();
                                                        }

                                                        connectDeviceLP();
                                                        ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
                                                        ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);

                                                    }
                                                }, 2 * 1000);


                                                Log.e("getStatus===", Code.toString(code));
                                                ToastUtil.showMessageApp(context, "蓝牙连接失败，请重试");
                                            }
                                        });
                                    }
                                });
                            }

                        }
                    }else{

                        try{
//                            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                                ToastUtil.showMessageApp(CurRoadBikingActivity.this, "您的设备不支持蓝牙4.0");
//                                scrollToFinishActivity();
//                            }
//                            //蓝牙锁
//                            if (!BaseApplication.getInstance().getIBLE().isEnable()){
//                                BaseApplication.getInstance().getIBLE().enableBluetooth();
//                                return;
//                            }
//                            if (BaseApplication.getInstance().getIBLE().getConnectStatus()){
//                                if (loadingDialog != null && !loadingDialog.isShowing()){
//                                    loadingDialog.setTitle("正在开锁");
//                                    loadingDialog.show();
//                                }
//
//                                BaseApplication.getInstance().getIBLE().openLock();
//
//                                isStop = false;
//                                m_myHandler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (loadingDialog != null && loadingDialog.isShowing()){
//                                            loadingDialog.dismiss();
//                                        }
//
//                                        if (!isStop){
//                                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//                                            customBuilder.setTitle("连接失败").setMessage("蓝牙连接失败，请靠近车锁，重启软件后再试")
//                                                    .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                                                        public void onClick(DialogInterface dialog, int which) {
//                                                            dialog.cancel();
//                                                        }
//                                                    });
//                                            customBuilder.create().show();
//                                        }
//
//                                    }
//                                }, 10 * 1000);
//
//                            }else {
//                                if (lockLoading != null && !lockLoading.isShowing()){
//                                    lockLoading.setTitle("正在连接");
//                                    lockLoading.show();
//                                }
//
//                                isStop = false;
//                                m_myHandler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//
//                                        if (lockLoading != null && lockLoading.isShowing()){
//                                            lockLoading.dismiss();
//                                        }
//
//                                        if (!isStop){
//                                            stopScan = true;
////                                          BaseApplication.getInstance().getIBLE().stopScan();
//                                            BaseApplication.getInstance().getIBLE().refreshCache();
//                                            BaseApplication.getInstance().getIBLE().close();
//                                            BaseApplication.getInstance().getIBLE().disconnect();
//
//                                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//                                            customBuilder.setTitle("连接失败").setMessage("蓝牙连接失败，请靠近车锁，重启软件后再试")
//                                                    .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                                                        public void onClick(DialogInterface dialog, int which) {
//                                                            dialog.cancel();
//                                                        }
//                                                    });
//                                            customBuilder.create().show();
//                                        }
//
//                                    }
//                                }, 10 * 1000);
//
//                                connect();
//                            }

                            Log.e("again===", "===");

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

                                if(!isLookPsdBtn){   //没连上
                                    isTemp = true;
                                    connect();
                                }else{
                                    BaseApplication.getInstance().getIBLE().openLock();

                                    isStop = false;
                                    m_myHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (loadingDialog != null && loadingDialog.isShowing()){
                                                loadingDialog.dismiss();
                                            }

                                            if (!isStop){
                                                CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                                customBuilder.setTitle("连接失败").setMessage("蓝牙连接失败，请靠近车锁，重启软件后再试")
                                                        .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.cancel();
                                                            }
                                                        });
                                                customBuilder.create().show();
                                            }

                                        }
                                    }, 10 * 1000);
                                }


                            }

                        }catch (Exception e){
                            ToastUtil.showMessageApp(context, "请重试");
                        }

                    }
                }
                break;
            case R.id.curRoadUI_biking_endBtn:

                if("7".equals(type)){
//                    xa_state = 0;
//                    XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
//                    builder.setBleStateChangeListener(this);
//                    builder.setScanResultCallback(this);
//                    apiClient = builder.build();
//                    CurRoadBikingActivityPermissionsDispatcher.connectDeviceWithPermissionCheck(CurRoadBikingActivity.this, deviceuuid);
                }else{
                    if(!isLookPsdBtn){
                        ToastUtil.showMessageApp(context, "蓝牙未连接，请重试");
                        break;
                    }
                }


                isEndBtn = true;

                clickCount++;

                test_xinbiao += major+"==="+minor+"\n";
                tv_test_xinbiao.setText(test_xinbiao);

//                getBleRecord();

                Log.e("biking_endBtn===", type+"==="+major);

//                major = 1;

                if(major !=0){
//                    m_myHandler.sendEmptyMessage(3);
//                    endBtn5();
                    queryOpenState();

                }
                else if("7".equals(type)){
                    location();
                }
                else{
                    startXB();

                    if (lockLoading != null && !lockLoading.isShowing()){
                        lockLoading.setTitle("还车点确认中");
                        lockLoading.show();
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
//                              while(macList.size() < 1000){

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



                break;
            case R.id.curRoadUI_biking_linkServiceLayout:
            case R.id.curRoadUI_biking_linkService_btn:
                initmPopupWindowView();

                break;
            case R.id.curRoadUI_biking_myLocationLayout:
            case R.id.curRoadUI_biking_myLocation:
                if (myLocation != null) {
                    CameraUpdate update = CameraUpdateFactory.changeLatLng(myLocation);
                    aMap.animateCamera(update);
                }
                break;
            case R.id.curRoadUI_biking_refreshLayout:
                isRefresh = true;
                RefreshLogin();
                String uid1 = SharedPreferencesUrls.getInstance().getString("uid","");
                String access_token1 = SharedPreferencesUrls.getInstance().getString("access_token","");
                if (uid1 == null || "".equals(uid1) || access_token1 == null || "".equals(access_token1)){
                    UIHelper.goToAct(context,LoginActivity.class);
                }else {
                    getCurrentorderBiking(SharedPreferencesUrls.getInstance().getString("uid",""), SharedPreferencesUrls.getInstance().getString("access_token",""));
                }

                if (mlocationClient != null) {
                    mlocationClient.startLocation();
                }

//                schoolRange();

                break;
            case R.id.curRoadUI_biking_slideLayout:
                if(("4".equals(type) || "7".equals(type))){
                    UIHelper.goWebViewAct(context,"停车须知",Urls.ebike_phtml5 + SharedPreferencesUrls.getInstance().getString("uid",""));
                }else{
                    UIHelper.goWebViewAct(context,"停车须知",Urls.phtml5 + SharedPreferencesUrls.getInstance().getString("uid",""));
                }

                break;

            case R.id.ui_discount_confirm:
                if(tz == 3){
                    BikeFragment.tz = 2;
                    intent = new Intent(context, HistoryRoadDetailActivity.class);
                    intent.putExtra("oid",oid);
                    startActivity(intent);
                }else if(tz == 4){
                    BikeFragment.tz = 3;
                    ToastUtil.showMessageApp(context,"恭喜您,还车成功,请支付!");
                    UIHelper.goToAct(context, CurRoadBikedActivity.class);
                }

                scrollToFinishActivity();
                break;

            case R.id.ui_test_confirm:

                RequestParams params = new RequestParams();
                params.put("uid",uid);
                params.put("access_token",access_token);
                params.put("oid",oid);
                params.put("latitude",referLatitude);
                params.put("longitude",referLongitude);

                params.put("xinbiao_name", "");
                params.put("xinbiao_mac", macList.size() > 0?macList.get(0):"");

                if(major!=0){
                    Log.e("submit===221", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                    params.put("back_type", "iBeacon_Lo");
                }else if(isGPS_Lo){
                    params.put("back_type", "GPS_Lo");
                }else if(macList.size() > 0){
                    Log.e("submit===222", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                    params.put("back_type", "iBeacon_Pho");
                }else if(force_backcar==1 && isTwo){
                    Log.e("submit===223", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                    params.put("back_type", "no_lock");
                }else{
//                  }else if(isContainsList.contains(true)){
                    params.put("back_type", "GPS");
                }

                if (macList2.size() > 0){
                    params.put("xinbiao", macList.get(0));
                }
                HttpHelper.post(this, Urls.backBikescan, params, new TextHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingDialog != null && !loadingDialog.isShowing()) {
                                    loadingDialog.setTitle("正在提交");
                                    loadingDialog.show();
                                }
                            }
                        });
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, final Throwable throwable) {
                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }
                                UIHelper.ToastError(context, throwable.toString());

                                if(!"5".equals(type) && !"6".equals(type) && BaseApplication.getInstance().getIBLE().isEnable()){
                                    BaseApplication.getInstance().getIBLE().refreshCache();
                                    BaseApplication.getInstance().getIBLE().close();
                                    BaseApplication.getInstance().getIBLE().disconnect();
//                                  BaseApplication.getInstance().getIBLE().disableBluetooth();
                                }
                            }
                        });

                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("Test","结束用车:"+responseString);
                                try {
                                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                                    if (result.getFlag().equals("Success")) {
                                        SharedPreferencesUrls.getInstance().putString("m_nowMac","");
                                        SharedPreferencesUrls.getInstance().putString("oid","");
                                        SharedPreferencesUrls.getInstance().putString("osn","");
                                        SharedPreferencesUrls.getInstance().putString("bleid","");
                                        SharedPreferencesUrls.getInstance().putString("deviceuuid","");
                                        SharedPreferencesUrls.getInstance().putString("type",type);
                                        SharedPreferencesUrls.getInstance().putBoolean("isStop",true);
                                        SharedPreferencesUrls.getInstance().putBoolean("switcher", false);
                                        SharedPreferencesUrls.getInstance().putString("biking_latitude","");
                                        SharedPreferencesUrls.getInstance().putString("biking_longitude","");

//                        if (myLocation != null){
//                            addMaplocation(myLocation.latitude,myLocation.longitude);
//                        }

                                        if (loadingDialog != null && loadingDialog.isShowing()){
                                            loadingDialog.dismiss();
                                        }

                                        if ("1".equals(result.getData())){
                                            ToastUtil.showMessageApp(context, result.getMsg());
                                            if ("已为您免单,欢迎反馈问题".equals(result.getMsg())){
                                                BikeFragment.tz = 1;
                                                UIHelper.goToAct(context, FeedbackActivity.class);
                                            }else {
                                                BikeFragment.tz = 2;
                                                Intent intent = new Intent(context, HistoryRoadDetailActivity.class);
                                                intent.putExtra("oid",oid);
                                                startActivity(intent);
                                            }

                                            scrollToFinishActivity();
                                        }else if ("2".equals(result.getData())){
                                            BikeFragment.tz = 3;
                                            ToastUtil.showMessageApp(context,"恭喜您,还车成功,请支付!");
                                            UIHelper.goToAct(context, CurRoadBikedActivity.class);

                                            scrollToFinishActivity();
                                        }else if ("3".equals(result.getData()) || "4".equals(result.getData())){
                                            if ("3".equals(result.getData())){
                                                tz = 3;
                                            }else{
                                                tz = 4;
                                            }

                                            WindowManager windowManager = getWindowManager();
                                            Display display = windowManager.getDefaultDisplay();
                                            WindowManager.LayoutParams lp = discountDialog.getWindow().getAttributes();
//                                          lp.width = (int) (display.getWidth() * 0.8);
//                                          lp.height= (int) (display.getHeight() * 0.4);
//                                          lp.width = (int) (display.getWidth() * 0.4);

//                                          lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//                                          lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                            discountDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
//                                          discountDialog.getWindow().setAttributes(lp);
                                            discountDialog.show();

                                            tv_discount.setText(result.getMsg());
                                        }

//                                      finish();
                                    }else {
                                        ToastUtil.showMessageApp(context, result.getMsg());
                                    }
                                }catch (Exception e){
                                    ToastUtil.showMessageApp(context, e.getMessage());
                                }
                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }
                                if (customDialog3 != null && customDialog3.isShowing()){
                                    customDialog3.dismiss();
                                }

                                if(!"5".equals(type) && !"6".equals(type) && BaseApplication.getInstance().getIBLE().isEnable()){
                                    BaseApplication.getInstance().getIBLE().refreshCache();
                                    BaseApplication.getInstance().getIBLE().close();
                                    BaseApplication.getInstance().getIBLE().disconnect();
//                                  BaseApplication.getInstance().getIBLE().disableBluetooth();
                                }
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                m_myHandler.sendEmptyMessage(5);
//                            }
//                        }).start();
                            }
                        });

                    }
                });
                break;

            default:
                break;
        }
    }

    private void location(){
        Log.e("biking===location0", "===="+bikeCode);

        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("access_token",access_token);
        params.put("tokencode", bikeCode);
//        params.put("version", "");
        HttpHelper.get(this, Urls.location, params, new TextHttpResponseHandler() {
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
                                ToastUtil.showMessageApp(context,"电量更新成功");

                                LocationBean bean = JSON.parseObject(result.getData(), LocationBean.class);

                                if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
                                    isContainsList.clear();
                                }
                                for ( int i = 0; i < pOptions.size(); i++){
                                    isContainsList.add(pOptions.get(i).contains(new LatLng(Double.parseDouble(bean.getLatitude()), Double.parseDouble(bean.getLongitude()))));
                                }

                                if(!isContainsList.contains(true)){
                                    minPoint(Double.parseDouble(bean.getLatitude()), Double.parseDouble(bean.getLongitude()));
                                }

                                Log.e("biking===location", bean.getLatitude()+"===="+bean.getLongitude()+"===="+isContainsList.contains(true));

                                if(isContainsList.contains(true)){
                                    isGPS_Lo = true;

                                    closeEbike_XA();
//                                    submit(uid, access_token);
                                }else{
                                    isGPS_Lo = false;

                                    startXB();

                                    if (lockLoading != null && !lockLoading.isShowing()){
                                        lockLoading.setTitle("还车点确认中");
                                        lockLoading.show();
                                    }

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

    protected void onStart() {
        super.onStart();
//        screen = true;
        start = true;

        Log.e("biking===", "biking====onStart");

        mapView.onResume();
        if (mlocationClient != null) {
            mlocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(5 * 1000);
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }

        scanManager = new ScanManager(this);
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

//        manager = new BeaconManager(this);
//        manager.setEddyStoneListener(new BeaconManager.EddyStoneListener() {
//
//            @Override
//            public void onEddyStoneDiscovered(EddyStone eddyStone) {
//                refreshList(eddyStone);
//            }
//
//            private void refreshList(EddyStone eddyStone) {
//
//                k++;
//
//                Log.e("EddyStoneListener===", k+"==="+eddyStone.getName()+"==="+eddyStone.getMacAddress()+"==="+eddyStone.getMajor()+"==="+eddyStone.getMinor()+"==="+eddyStone.getUuid()+"==="+eddyStone.getUrl()+"==="+eddyStone.getModel());
//
//                if (eddyStone.getName()!=null && eddyStone.getName().contains("abeacon") && !macList.contains(eddyStone.getMacAddress())) {
//                    macList.add(""+eddyStone.getMacAddress());
//                }
//
//                scan = true;
//
//            }
//        });


        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
//           if (!macList.contains(parseAdvData(rssi,scanRecord))){
//               macList.add(parseAdvData(rssi,scanRecord));
//           }


                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        k++;

//                      device.fetchUuidsWithSdp();

                        Log.e("biking===LeScan", new String(scanRecord)+ "====" +scanRecord.length+ "====" +parseAdvData(rssi,scanRecord)+ "====" +device.getName() + "====" +device.fetchUuidsWithSdp()+ "====" +device.describeContents() + "====" + device.getAddress() + "====" + device.getUuids() + "====" + rssi + "====" + k);

                        test_xinbiao += parseAdvData(rssi,scanRecord)+ "====" +device.getName()+ "====" + device.getAddress()+"\n";
                        tv_test_xinbiao.setText(test_xinbiao);

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

    }

    private void startXB() {
        if (mBluetoothAdapter == null) {
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (mBluetoothAdapter == null) {
            ToastUtil.showMessageApp(context, "获取蓝牙失败");
            scrollToFinishActivity();
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

    private String parseAdvData(int rssi, byte[] scanRecord) {
        byte[] bytes = ParseLeAdvData.adv_report_parse(ParseLeAdvData.BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA, scanRecord);

        Log.e("parseAdvData===", bytes.length+"==="+bytes[0]);

        if(null == bytes || bytes.length < 11){
            return "";
        }

        if (bytes[0] == 0x01 && bytes[1] == 0x02) {
            byte[] mac = new byte[6];
            byte[] time = new byte[4];
            System.arraycopy(bytes, 2, mac, 0, mac.length);
            System.arraycopy(bytes, 8, time, 0, time.length);

            int max = 0x00;
            int current;
            for (int i =0; i < 4; i++){
                current = time[i] & 0xFF;
                max = max < current ? current : max;
            }
            if(max == 0xFF){
                max = (byte)0xAA;
            }else if(max == 0x00){
                max = 0x55;
            }

            for (int i =0; i <mac.length; i++){
                mac[i] = (byte)(mac[i] ^ max);
            }

            String mDeviceAddrss = ConvertUtils.bytes2HexString(mac);
            mDeviceAddrss = mDeviceAddrss.substring(0, 2) + ":"
                    + mDeviceAddrss.substring(2, 4) + ":"
                    + mDeviceAddrss.substring(4, 6) + ":"
                    + mDeviceAddrss.substring(6, 8) + ":"
                    + mDeviceAddrss.substring(8, 10) + ":"
                    + mDeviceAddrss.substring(10, 12);

            return mDeviceAddrss;
        }
        return "";
    }

//    private String parseAdvData(int rssi, byte[] scanRecord) {
//        byte[] bytes = ParseLeAdvData.adv_report_parse(ParseLeAdvData.BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA, scanRecord);
//
//        Log.e("parseAdvData===", bytes.length+"==="+bytes[0]);
//
//        if (bytes[0] == 0x01 && bytes[1] == 0x02) {
//            return bytes2hex03(bytes);
//        }
//        return "";
//    }
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


    public void openAgain(){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        HttpHelper.post(this, Urls.openAgain, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingDialog != null && !loadingDialog.isShowing()) {
                            loadingDialog.setTitle("正在加载");
                            loadingDialog.show();
                        }
                    }
                });

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, final Throwable throwable) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                        UIHelper.ToastError(context, throwable.toString());

                        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                            ToastUtil.showMessageApp(CurRoadBikingActivity.this, "您的设备不支持蓝牙4.0");
                            scrollToFinishActivity();
                        }

                        if (!BaseApplication.getInstance().getIBLE().isEnable()){
                            BaseApplication.getInstance().getIBLE().enableBluetooth();
                            return;
                        }
                        if (BaseApplication.getInstance().getIBLE().getConnectStatus()){
                            if (loadingDialog != null && !loadingDialog.isShowing()){
                                loadingDialog.setTitle("正在开锁");
                                loadingDialog.show();
                            }

                            BaseApplication.getInstance().getIBLE().openLock();

                            isStop = false;
                            m_myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (loadingDialog != null && loadingDialog.isShowing()){
                                        loadingDialog.dismiss();
                                    }

                                    if (!isStop){
                                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                        customBuilder.setTitle("连接失败").setMessage("蓝牙连接失败，请靠近车锁，重启软件后再试")
                                                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        customBuilder.create().show();

                                    }

                                }
                            }, 10 * 1000);

                        }else {
                            if (lockLoading != null && !lockLoading.isShowing()){
                                lockLoading.setTitle("正在连接");
                                lockLoading.show();
                            }

                            isStop = false;
                            m_myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (lockLoading != null && lockLoading.isShowing()){
                                        lockLoading.dismiss();
                                    }

                                    if (!isStop){
                                        stopScan = true;
//                                      BaseApplication.getInstance().getIBLE().stopScan();
                                        BaseApplication.getInstance().getIBLE().refreshCache();
                                        BaseApplication.getInstance().getIBLE().close();
                                        BaseApplication.getInstance().getIBLE().disconnect();

                                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                        customBuilder.setTitle("连接失败").setMessage("蓝牙连接失败，请靠近车锁，重启软件后再试")
                                                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        customBuilder.create().show();
                                    }
                                }
                            }, 10 * 1000);

                            connect();
                        }
                    }
                });

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e("biking===0", "openAgain====");

                            carClose2();

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

    public void carClose2(){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        HttpHelper.post(this, Urls.carClose, params, new TextHttpResponseHandler() {
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
//                        ToastUtil.showMessage(context,"数据更新成功");

                                Log.e("biking===", "carClose2===="+result.getData());

                                if ("0".equals(result.getData())){
                                    ToastUtil.showMessageApp(context,"开锁成功");
                                } else {
                                    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                        ToastUtil.showMessageApp(CurRoadBikingActivity.this, "您的设备不支持蓝牙4.0");
                                        scrollToFinishActivity();
                                    }

                                    if (!BaseApplication.getInstance().getIBLE().isEnable()){
                                        BaseApplication.getInstance().getIBLE().enableBluetooth();
                                        return;
                                    }
                                    if (BaseApplication.getInstance().getIBLE().getConnectStatus()){
                                        if (loadingDialog != null && !loadingDialog.isShowing()){
                                            loadingDialog.setTitle("正在开锁");
                                            loadingDialog.show();
                                        }

                                        BaseApplication.getInstance().getIBLE().openLock();

                                        isStop = false;
                                        m_myHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (loadingDialog != null && loadingDialog.isShowing()){
                                                    loadingDialog.dismiss();
                                                }

                                                if (!isStop){
                                                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                                    customBuilder.setTitle("连接失败").setMessage("蓝牙连接失败，请靠近车锁，重启软件后再试")
                                                            .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.cancel();
                                                                }
                                                            });
                                                    customBuilder.create().show();

                                                    clickCountDeal();

                                                }

                                            }
                                        }, 10 * 1000);

                                    }else {
                                        if (lockLoading != null && !lockLoading.isShowing()){
                                            lockLoading.setTitle("正在连接");
                                            lockLoading.show();
                                        }

                                        isStop = false;
                                        m_myHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                if (lockLoading != null && lockLoading.isShowing()){
                                                    lockLoading.dismiss();
                                                }

                                                if (!isStop){
                                                    stopScan = true;
//                                          BaseApplication.getInstance().getIBLE().stopScan();
                                                    BaseApplication.getInstance().getIBLE().refreshCache();
                                                    BaseApplication.getInstance().getIBLE().close();
                                                    BaseApplication.getInstance().getIBLE().disconnect();

                                                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                                    customBuilder.setTitle("连接失败").setMessage("蓝牙连接失败，请靠近车锁，重启软件后再试")
                                                            .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.cancel();
                                                                }
                                                            });
                                                    customBuilder.create().show();

                                                    clickCountDeal();
                                                }

                                            }
                                        }, 10 * 1000);

                                        connect();
                                    }
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
        });
    }


    public void carClose(){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        HttpHelper.post(this, Urls.carClose, params, new TextHttpResponseHandler() {
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
//                        ToastUtil.showMessage(context,"数据更新成功");

                                Log.e("biking===", "carClose===="+result.getData());

                                if ("0".equals(result.getData())){
                                    m_myHandler.sendEmptyMessage(6);
//                                  submit(uid, access_token);
                                } else {
                                    ToastUtil.showMessageApp(context,"车锁未关，请手动关锁");

                                    clickCountDeal();
                                }
                            } else {
                                ToastUtil.showMessageApp(context,result.getMsg());

                                clickCountDeal();
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

    protected void submit(String uid,String access_token){
        Log.e("submit===", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);

        if(BaseApplication.getInstance().isTestLog()){
            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                    if (lockLoading != null && lockLoading.isShowing()){
                        lockLoading.dismiss();
                    }

                    if(BaseApplication.getInstance().isTestLog()){
                        macText.setText(""+macList);
                    }


//                      ToastUtil.showMessageApp(context, ""+macList);
                    Toast.makeText(context,""+macList,Toast.LENGTH_LONG).show();

                    WindowManager windowManager = getWindowManager();
                    Display display = windowManager.getDefaultDisplay();
                    WindowManager.LayoutParams lp = testDialog.getWindow().getAttributes();
                    testDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                    testDialog.show();

                    tv_test.setText(""+macList);
                }
            });
        }else{
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("oid",oid);
            params.put("latitude",referLatitude);
            params.put("longitude",referLongitude);

            params.put("xinbiao_name", "");
            params.put("xinbiao_mac", macList.size() > 0?macList.get(0):"");

            if(major!=0){
                Log.e("submit===221", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                params.put("back_type", "iBeacon_Lo");
            }else if(isGPS_Lo){
                params.put("back_type", "GPS_Lo");
            }else if(macList.size() > 0){
                Log.e("submit===222", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                params.put("back_type", "iBeacon_Pho");
            }else if(force_backcar==1 && isTwo){
                Log.e("submit===223", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
                params.put("back_type", "no_lock");
            }else{
//            }else if(isContainsList.contains(true)){
                params.put("back_type", "GPS");
            }

            if (macList.size() > 0){
                params.put("xinbiao", macList.get(0));
            }
            HttpHelper.post(this, Urls.backBikescan, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (loadingDialog != null && !loadingDialog.isShowing()) {
                                loadingDialog.setTitle("正在提交");
                                loadingDialog.show();
                            }
                        }
                    });
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, final Throwable throwable) {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                            UIHelper.ToastError(context, throwable.toString());

                            if(!"5".equals(type) && !"6".equals(type) && BaseApplication.getInstance().getIBLE().isEnable()){
                                BaseApplication.getInstance().getIBLE().refreshCache();
                                BaseApplication.getInstance().getIBLE().close();
                                BaseApplication.getInstance().getIBLE().disconnect();
//                              BaseApplication.getInstance().getIBLE().disableBluetooth();
                            }
                        }
                    });

                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("Test","结束用车:"+responseString);
                            try {
                                ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                                if (result.getFlag().equals("Success")) {
                                    SharedPreferencesUrls.getInstance().putString("m_nowMac","");
                                    SharedPreferencesUrls.getInstance().putString("oid","");
                                    SharedPreferencesUrls.getInstance().putString("osn","");
                                    SharedPreferencesUrls.getInstance().putString("bleid","");
                                    SharedPreferencesUrls.getInstance().putString("deviceuuid","");
                                    SharedPreferencesUrls.getInstance().putString("type",type);
                                    SharedPreferencesUrls.getInstance().putBoolean("isStop",true);
                                    SharedPreferencesUrls.getInstance().putBoolean("switcher", false);
                                    SharedPreferencesUrls.getInstance().putString("biking_latitude","");
                                    SharedPreferencesUrls.getInstance().putString("biking_longitude","");

//                                  if (myLocation != null){
//                                      addMaplocation(myLocation.latitude,myLocation.longitude);
//                                  }

                                    if (loadingDialog != null && loadingDialog.isShowing()){
                                        loadingDialog.dismiss();
                                    }

                                    if ("1".equals(result.getData())){
                                        ToastUtil.showMessageApp(context, result.getMsg());
                                        if ("已为您免单,欢迎反馈问题".equals(result.getMsg())){
                                            BikeFragment.tz = 1;
                                            UIHelper.goToAct(context, FeedbackActivity.class);
                                        }else {
                                            BikeFragment.tz = 2;
                                            Intent intent = new Intent(context, HistoryRoadDetailActivity.class);
                                            intent.putExtra("oid", oid);
                                            startActivity(intent);
                                        }

                                        scrollToFinishActivity();
                                    }else if ("2".equals(result.getData())){
                                        BikeFragment.tz = 3;
                                        ToastUtil.showMessageApp(context,"恭喜您,还车成功,请支付!");
                                        UIHelper.goToAct(context, CurRoadBikedActivity.class);

                                        scrollToFinishActivity();
                                    }else if ("3".equals(result.getData()) || "4".equals(result.getData())){
                                        if ("3".equals(result.getData())){
                                            tz = 3;
                                        }else{
                                            tz = 4;
                                        }

                                        WindowManager windowManager = getWindowManager();
                                        Display display = windowManager.getDefaultDisplay();
                                        WindowManager.LayoutParams lp = discountDialog.getWindow().getAttributes();
//                                      lp.width = (int) (display.getWidth() * 0.8);
//                                      lp.height= (int) (display.getHeight() * 0.4);
//                                      lp.width = (int) (display.getWidth() * 0.4);

//                                      lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//                                      lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                        discountDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
//                                      discountDialog.getWindow().setAttributes(lp);
                                        discountDialog.show();

                                        tv_discount.setText(result.getMsg());
                                    }

//                                  finish();

                                }else {
                                    ToastUtil.showMessageApp(context, result.getMsg());
                                }
                            }catch (Exception e){
                                ToastUtil.showMessageApp(context, e.getMessage());
                            }
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                            if (customDialog3 != null && customDialog3.isShowing()){
                                customDialog3.dismiss();
                            }

                            if(!"5".equals(type) && !"6".equals(type) && BaseApplication.getInstance().getIBLE().isEnable()){
                                BaseApplication.getInstance().getIBLE().refreshCache();
                                BaseApplication.getInstance().getIBLE().close();
                                BaseApplication.getInstance().getIBLE().disconnect();
//                              BaseApplication.getInstance().getIBLE().disableBluetooth();
                            }
                        }
                    });

                }
            });
        }

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

                        getBleRecord();

                        if(open) {
                            ToastUtil.showMessageApp(context,"车锁未关，请手动关锁");
                        }else {
                            submit(uid, access_token);
                        }

                        if (lockLoading != null && lockLoading.isShowing()){
                            lockLoading.dismiss();
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

                        if (lockLoading != null && lockLoading.isShowing()){
                            lockLoading.dismiss();
                        }
                    }
                });

            }
        });
    }

    public void endBtn(){

        Log.e("biking===endBtn0", force_backcar+"==="+isTwo+"==="+isEndBtn+"==="+macList.size()+"==="+isContainsList.contains(true));


        if(force_backcar==1 && isTwo){
            ToastUtil.showMessageApp(context,"锁已关闭");
            Log.e("biking===", "endBtn===锁已关闭==="+first3);

            if(!isEndBtn) return;

            m_myHandler.sendEmptyMessage(6);

            return;
        }

        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context,LoginActivity.class);
        }else {
//            ToastUtil.showMessage(context,macList.size()+"==="+isContainsList.contains(true));
            Log.e("biking===endBtn",macList.size()+"==="+isContainsList.contains(true)+"==="+type+"==="+isContainsList.contains(true));

            rl_msg.setVisibility(View.GONE);
            if (polyline != null) {
                polyline.remove();
            }

            if (macList.size() > 0 && !"1".equals(type)){
                //蓝牙锁

                flag = 2;
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    ToastUtil.showMessageApp(CurRoadBikingActivity.this, "您的设备不支持蓝牙4.0");
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

                    Log.e("biking===endBtn_2",macList.size()+"===");

                    isStop = false;
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

                            if(!isStop){
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
                    Log.e("biking===endBtn_3",macList.size()+"===");

                    if (lockLoading != null && !lockLoading.isShowing()){
                        lockLoading.setTitle("正在连接");
                        lockLoading.show();
                    }

                    isStop = false;
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (lockLoading != null && lockLoading.isShowing()){
                                lockLoading.dismiss();
                            }

                            if(!isStop){
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

                    connect();
                }

                return;
            }

            if (isContainsList.contains(true)){
                if ("1".equals(type)){
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                    customBuilder.setTitle("温馨提示").setMessage("还车必须到校内关锁并拨乱数字密码，距车一米内在APP点击结束!")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            submit(uid, access_token);
                        }
                    });
                    customBuilder.create().show();

                    clickCountDeal();
                }else {
                    flag = 2;
                    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                        ToastUtil.showMessageApp(CurRoadBikingActivity.this, "您的设备不支持蓝牙4.0");
                        scrollToFinishActivity();
                    }
                    //蓝牙锁
                    if (!BaseApplication.getInstance().getIBLE().isEnable()){
                        BaseApplication.getInstance().getIBLE().enableBluetooth();
                        return;
                    }
                    if (BaseApplication.getInstance().getIBLE().getConnectStatus()){
                        Log.e("biking===endBtn_4",macList.size()+"===");

                        if (loadingDialog != null && !loadingDialog.isShowing()){
                            loadingDialog.setTitle("请稍等");
                            loadingDialog.show();
                        }

                        isStop = false;
                        m_myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }

                                if(!isStop){
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
                        Log.e("biking===endBtn_5",macList.size()+"===");

                        if (lockLoading != null && !lockLoading.isShowing()){
                            lockLoading.setTitle("正在连接");
                            lockLoading.show();
                        }

                        isStop = false;
                        m_myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (lockLoading != null && lockLoading.isShowing()){
                                    lockLoading.dismiss();
                                }

                                if(!isStop){
                                    stopScan = true;
//                                         BaseApplication.getInstance().getIBLE().stopScan();
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

                        connect();

                    }
                }
            }else {
//                customDialog3.show();

                rl_msg.setVisibility(View.VISIBLE);

                minPolygon();

//                clickCountDeal();
            }

//            if(BikeFragment.screen){
//            }

        }
    }

    public void endBtn3(){
        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            Log.e("biking===endBtn3",macList.size()+"==="+type+"==="+first3);

            rl_msg.setVisibility(View.GONE);
            if (polyline != null) {
                polyline.remove();
            }

            if (macList.size() > 0){
                flag = 2;
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    ToastUtil.showMessageApp(CurRoadBikingActivity.this, "您的设备不支持蓝牙4.0");
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

                    isStop = false;
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();

                                if(!isStop){
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
                                        carClose();
                                    }
                                }
                            }
                        }
                    }, 10 * 1000);

                    macList2 = new ArrayList<> (macList);
                    BaseApplication.getInstance().getIBLE().getLockStatus();
                } else {
                    if (lockLoading != null && !lockLoading.isShowing()){
                        lockLoading.setTitle("正在连接");
                        lockLoading.show();
                    }

                    isStop = false;
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (lockLoading != null && lockLoading.isShowing()){
                                lockLoading.dismiss();
                            }

                            if(!isStop){
                                stopScan = true;
                                BaseApplication.getInstance().getIBLE().refreshCache();
                                BaseApplication.getInstance().getIBLE().close();
                                BaseApplication.getInstance().getIBLE().disconnect();

                                if(first3){
                                    first3 = false;
                                    customDialog3.show();

                                    clickCountDeal();
                                }else{
                                    carClose();
                                }
                            }
                        }
                    }, 10 * 1000);

                    connect();
                }

//                if("3".equals(type)){
//                    carClose();
//                }else{
//                    submit(uid, access_token);
//                }

                return;
            }

            if (isContainsList.contains(true)){

                flag = 2;
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    ToastUtil.showMessageApp(CurRoadBikingActivity.this, "您的设备不支持蓝牙4.0");
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

                    isStop = false;
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();

                                if(!isStop){
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
                                        carClose();
                                    }
                                }

                            }
                        }
                    }, 10 * 1000);

                    macList2 = new ArrayList<> (macList);
                    BaseApplication.getInstance().getIBLE().getLockStatus();
                }else {
                    if (lockLoading != null && !lockLoading.isShowing()){
                        lockLoading.setTitle("正在连接");
                        lockLoading.show();
                    }

                    isStop = false;
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (lockLoading != null && lockLoading.isShowing()){
                                lockLoading.dismiss();
                            }

                            if(!isStop){
                                stopScan = true;
//                                  BaseApplication.getInstance().getIBLE().stopScan();
                                BaseApplication.getInstance().getIBLE().refreshCache();
                                BaseApplication.getInstance().getIBLE().close();
                                BaseApplication.getInstance().getIBLE().disconnect();

                                if(first3){
                                    first3 = false;
//                                    customDialog3.show();

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
                                    carClose();
                                }
                            }
                        }
                    }, 10 * 1000);

                    connect();

                }

            }else {
//                customDialog3.show();

                rl_msg.setVisibility(View.GONE);

                minPolygon();

                clickCountDeal();
            }

//            if(BikeFragment.screen){
//            }

        }
    }

    public void endBtn4(){
        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            Log.e("biking===endBtn4",macList.size()+"==="+type+"==="+first3);

            rl_msg.setVisibility(View.GONE);
            if (polyline != null) {
                polyline.remove();
            }

            if (macList.size() > 0){
//                flag = 2;

                closeEbike2();

//                bleService.connect(m_nowMac);
//                cn = 0;
//
//                closeLock3();

                return;
            }

            if (isContainsList.contains(true)){

                closeEbike2();

            }else {
//                customDialog4.show();

                rl_msg.setVisibility(View.GONE);

                minPolygon();
            }

//            if(BikeFragment.screen){
//            }

        }
    }

    public void endBtn5(){
        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context,LoginActivity.class);
        }else {
//            ToastUtil.showMessage(context,macList.size()+"==="+isContainsList.contains(true));
            Log.e("biking===endBtn5",macList.size()+"==="+isContainsList.contains(true)+"==="+type+"==="+BikeFragment.screen);

            rl_msg.setVisibility(View.GONE);
            if (polyline != null) {
                polyline.remove();
            }

//            if("01".equals(transtype)){
//                flag = 2;
//                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                    ToastUtil.showMessageApp(CurRoadBikingActivity.this, "您的设备不支持蓝牙4.0");
//                    scrollToFinishActivity();
//                }
//
//                if (mBluetoothAdapter == null) {
//                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                    scrollToFinishActivity();
//                    return;
//                }
//                if (!mBluetoothAdapter.isEnabled()) {
//                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableBtIntent, 188);
//                } else {
//                    queryOpenState();
//                }
//
//                return;
//            }

            if (macList.size() > 0){
                //泺平锁
                flag = 2;
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    ToastUtil.showMessageApp(CurRoadBikingActivity.this, "您的设备不支持蓝牙4.0");
                    scrollToFinishActivity();
                }

                if (mBluetoothAdapter == null) {
                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                    scrollToFinishActivity();
                    return;
                }
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 188);
                } else {
                    queryOpenState();
                }

                return;
            }

            if (isContainsList.contains(true)){
                flag = 2;
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    ToastUtil.showMessageApp(CurRoadBikingActivity.this, "您的设备不支持蓝牙4.0");
                    scrollToFinishActivity();
                }

                if (mBluetoothAdapter == null) {
                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
                    scrollToFinishActivity();
                    return;
                }
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 188);
                } else {
                    queryOpenState();
                }


            }else {
//                customDialog3.show();

                rl_msg.setVisibility(View.GONE);

                minPolygon();

                clickCountDeal();
            }

//            if(BikeFragment.screen){
//            }

        }
    }

    public void endBtn7(){
        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            Log.e("biking===endBtn7",macList.size()+"==="+type+"==="+deviceuuid+"==="+isConnect);

            rl_msg.setVisibility(View.GONE);
            if (polyline != null) {
                polyline.remove();
            }

            if (macList.size() > 0){
//                flag = 2;

                closeEbike_XA();

//                if(isConnect){
//                    closeEbike_XA();
//                }else{
//                    if (lockLoading != null && !lockLoading.isShowing()){
//                        lockLoading.setTitle("正在连接");
//                        lockLoading.show();
//                    }
//
//                    xa_state = 1;
//
//                    XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
//                    builder.setBleStateChangeListener(CurRoadBikingActivity.this);
//                    builder.setScanResultCallback(CurRoadBikingActivity.this);
//                    apiClient = builder.build();
//
//                    m_myHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (lockLoading != null && lockLoading.isShowing()){
//                                lockLoading.dismiss();
//
//                                if(!isConnect){
//                                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//                                    customBuilder.setTitle("连接失败").setMessage("蓝牙连接失败，请靠近车锁，重启软件后再试")
//                                            .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.cancel();
//                                                }
//                                            });
//                                    customBuilder.create().show();
//                                }
//                            }
//                        }
//                    }, 10 * 1000);
//
//                    CurRoadBikingActivityPermissionsDispatcher.connectDeviceWithPermissionCheck(CurRoadBikingActivity.this, deviceuuid);
//                }

                return;
            }

            if (isContainsList.contains(true)){

                closeEbike_XA();

//                if(isConnect){
//                    closeEbike_XA();
//                }else{
//                    if (lockLoading != null && !lockLoading.isShowing()){
//                        lockLoading.setTitle("正在连接");
//                        lockLoading.show();
//                    }
//
//                    xa_state = 1;
//
//                    XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
//                    builder.setBleStateChangeListener(CurRoadBikingActivity.this);
//                    builder.setScanResultCallback(CurRoadBikingActivity.this);
//                    apiClient = builder.build();
//
//                    m_myHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (lockLoading != null && lockLoading.isShowing()){
//                                lockLoading.dismiss();
//
//                                if(!isConnect){
//                                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//                                    customBuilder.setTitle("连接失败").setMessage("蓝牙连接失败，请靠近车锁，重启软件后再试")
//                                            .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.cancel();
//                                                }
//                                            });
//                                    customBuilder.create().show();
//                                }
//                            }
//                        }
//                    }, 10 * 1000);
//
//                    CurRoadBikingActivityPermissionsDispatcher.connectDeviceWithPermissionCheck(CurRoadBikingActivity.this, deviceuuid);
//                }

            }else {
//                customDialog4.show();

                rl_msg.setVisibility(View.GONE);

                minPolygon();
            }

//            if(BikeFragment.screen){
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


//        if(isClickCount && clickCount>=3){
//            Intent intent = new Intent(context,ClientServiceActivity.class);
//            intent.putExtra("bikeCode", bikeCode);
//            startActivity(intent);
//            scrollToFinishActivity();
//
//        }
    }






    @Override
    public void onLocationChanged(final AMapLocation amapLocation) {
//        super.onLocationChanged(amapLocation);

        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
//                this.amapLocation = amapLocation;

//        title.setText(isContainsList.contains(true)+"》》"+k+"=="+macList.size()+"=="+xb);

//        if(1==1) return;

                if (mListener != null && amapLocation != null && xb==0) {

                    if((referLatitude == amapLocation.getLatitude()) && (referLongitude == amapLocation.getLongitude())) return;
//
//            hintText.setText(isContainsList.contains(true)+"》》》"+near+"==="+macList.size()+"==="+amapLocation.getLatitude());

                    Log.e("biking===Changed", isContainsList.contains(true) + "》》》" + near + "===" + macList.size()+"==="+amapLocation.getLatitude());

//            title.setText(isContainsList.contains(true)+"》》"+near+"=="+macList.size()+"=="+k);

                    if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                        if (0.0 != amapLocation.getLatitude() && 0.0 != amapLocation.getLongitude()){
                            String latitude = SharedPreferencesUrls.getInstance().getString("biking_latitude","0.0");
                            String longitude = SharedPreferencesUrls.getInstance().getString("biking_longitude","0.0");

                            if (mListener != null) {
                                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                            }
                            referLatitude = amapLocation.getLatitude();
                            referLongitude = amapLocation.getLongitude();
                            myLocation = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());

//                    Log.e("biking===Changed2", latitude+"==="+amapLocation.getLatitude());

//                    if (latitude != null && !"".equals(latitude) && longitude != null && !"".equals(longitude)){
//                        macText.setText(latitude+"==="+amapLocation.getLatitude()+"==="+AMapUtils.calculateLineDistance(new LatLng(
//                                Double.parseDouble(latitude),Double.parseDouble(longitude)
//                        ), new LatLng(amapLocation.getLatitude(),amapLocation.getLongitude())));
//                    }


                            if (latitude != null && !"".equals(latitude) && longitude != null && !"".equals(longitude)){

                                if (AMapUtils.calculateLineDistance(new LatLng(
                                        Double.parseDouble(latitude),Double.parseDouble(longitude)
                                ), new LatLng(amapLocation.getLatitude(),amapLocation.getLongitude())) > 10){

                                    SharedPreferencesUrls.getInstance().putString("biking_latitude",""+amapLocation.getLatitude());
                                    SharedPreferencesUrls.getInstance().putString("biking_longitude",""+amapLocation.getLongitude());
//                            addMaplocation(amapLocation.getLatitude(),amapLocation.getLongitude());

//                            mPolyoptions.add(myLocation);
//
//                            if (mPolyoptions.getPoints().size() > 1) {
//                                aMap.addPolyline(mPolyoptions);
//                            }

                                }
                            }

                            if (mFirstFix) {
                                mFirstFix = false;
                                schoolRange();
                                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18f));
                            } else {
                                centerMarker.remove();
                                mCircle.remove();
                                centerMarker=null;

                                if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
                                    isContainsList.clear();
                                }
                                for ( int i = 0; i < pOptions.size(); i++){
                                    isContainsList.add(pOptions.get(i).contains(myLocation));
                                }


                            }
                            addChooseMarker();
                            addCircle(myLocation, amapLocation.getAccuracy());//添加定位精度圆

//                    //补正自动还车
//                    if(!SharedPreferencesUrls.getInstance().getBoolean("switcher",false)){
//                        if (start) {
//                            start = false;
//
//                            if (mlocationClient != null) {
//                                mlocationClient.setLocationListener(CurRoadBikingActivity.this);
//                                mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//                                mLocationOption.setInterval(2 * 1000);
//                                mlocationClient.setLocationOption(mLocationOption);
//                                mlocationClient.startLocation();
//                            }
//
//                            macList2 = new ArrayList<> (macList);
//                            BaseApplication.getInstance().getIBLE().getLockStatus();
//
//                            Log.e("biking===Changed2", isContainsList.contains(true) + "》》》" + near + "===" + macList.size()+"==="+macList2.size());
//                        }else if(!SharedPreferencesUrls.getInstance().getBoolean("isStop",true)){
//
//                            if (isContainsList.contains(true) && near==1){
//                                macList2 = new ArrayList<> (macList);
//                                ToastUtil.showMessage(context,"biking---》》》里");
//                                BaseApplication.getInstance().getIBLE().getLockStatus();
//                            }else if (!isContainsList.contains(true) && near==0){
//                                macList2 = new ArrayList<> (macList);
//                                ToastUtil.showMessage(context,"biking---》》》外");
//                                BaseApplication.getInstance().getIBLE().getLockStatus();
//                            }
//
//                            Log.e("biking===Changed3", isContainsList.contains(true) + "》》》" + near + "===" + macList.size()+"==="+macList2.size());
//                        }
//                    }
//
//
//                    if ((isContainsList.contains(true) || macList.size() > 0) && !"1".equals(type)){
//                        near = 0;
//                    }else{
//                        near = 1;
//                    }

                            Log.e("biking===Changed4", isContainsList.contains(true) + "》》》" + near + "===" + macList.size()+"==="+macList2.size());

                        }else {
                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                            customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开位置权限！")
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            scrollToFinishActivity();
                                        }
                                    }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    CurRoadBikingActivity.this.requestPermissions(
                                            new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                                            REQUEST_CODE_ASK_PERMISSIONS);
                                }
                            });
                            customBuilder.create().show();
                        }
                    }


                }
            }
        });




    }


    Handler m_myHandler = new Handler(new Handler.Callback() {
    @Override
    public boolean handleMessage(Message mes) {
        switch (mes.what) {
            case 0:
                if (!BaseApplication.getInstance().getIBLE().isEnable()){
                    break;
                }
//                BaseApplication.getInstance().getIBLE().resetLock();
//                BaseApplication.getInstance().getIBLE().resetBluetoothAdapter();
                BaseApplication.getInstance().getIBLE().connect(m_nowMac, CurRoadBikingActivity.this);


                break;
            case 1:
                break;
            case 2:
                if (lockLoading != null && lockLoading.isShowing()){
                    lockLoading.dismiss();
                }

                stopXB();

                Log.e("biking===2", "2===" + type);

                if("3".equals(type)){
                    endBtn3();
                }else{
                    endBtn();
                }

                break;
            case 3:

                stopXB();

                Log.e("biking===3", type+"==="+macList.size()+"==="+isContainsList.contains(true));


                if (lockLoading != null && lockLoading.isShowing()){
                    lockLoading.dismiss();
                }

                rl_msg.setVisibility(View.GONE);
                if (polyline != null) {
                    polyline.remove();
                }



                if(macList.size()>0 || isContainsList.contains(true)){
                    isTemp = false;

                    if("4".equals(type)){
                        endBtn4();
                    }else if("5".equals(type) || "6".equals(type)){
                        if (lockLoading != null && !lockLoading.isShowing()){
                            lockLoading.setTitle("正在连接");
                            lockLoading.show();
                        }

                        endBtn5();
                    }else if("7".equals(type)){
//                        if (lockLoading != null && !lockLoading.isShowing()){
//                            lockLoading.setTitle("正在连接");
//                            lockLoading.show();
//                        }

                        endBtn7();
                    }else{

                        if("3".equals(type)){
                            endBtn3();
                        }else{
                            endBtn();
                        }
                    }

                }else{
//                    customDialog3.show();

                    rl_msg.setVisibility(View.VISIBLE);

                    minPolygon();

//                    clickCountDeal();
                }




                break;

            case 4:
                ebikeInfo(uid, access_token);
                break;

            case 5:


                if(!"5".equals(type) && !"6".equals(type) && BaseApplication.getInstance().getIBLE().isEnable()){
                    BaseApplication.getInstance().getIBLE().refreshCache();
                    BaseApplication.getInstance().getIBLE().close();
                    BaseApplication.getInstance().getIBLE().disconnect();
//                    BaseApplication.getInstance().getIBLE().disableBluetooth();
                }
                break;

            case 6:
                submit(uid, access_token);
                break;

            case 7:

                ToastUtil.showMessageApp(context,"请手动关锁");

//                SharedPreferencesUrls.getInstance().putString("tempStat","1");
                break;

            case 8:
                animMarker();
                break;

            case 9:
                time.setText(transtype+">>1="+ major +">>2="+minor);
                break;

//            case 10:
//                ebikeInfo(uid, access_token);
//                break;

            case 0x99://搜索超时
                BaseApplication.getInstance().getIBLE().connect(m_nowMac, CurRoadBikingActivity.this);
//                resetLock();
                m_myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isStop){
                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                            Toast.makeText(context,"唤醒失败，重启手机蓝牙试试吧！",Toast.LENGTH_LONG).show();
                            BaseApplication.getInstance().getIBLE().stopScan();
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
        Log.e("resetLock===", "===0");
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isStop){

                    Log.e("resetLock===", "===1");

                    BaseApplication.getInstance().getIBLE().resetLock();

                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isStop){
                                Log.e("resetLock===", "===2");

                                BaseApplication.getInstance().getIBLE().resetBluetoothAdapter();

                                m_myHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isStop){
                                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                                loadingDialog.dismiss();
                                            }

                                            Toast.makeText(context,"唤醒失败，重启手机蓝牙试试吧！",Toast.LENGTH_LONG).show();
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

//    void checkConnect(){
//        m_myHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.e("checkConnect===", "===");
//
//                if(!bleService.connect){
//                    cn++;
//
//                    if(cn<5){
//                        checkConnect();
//                    }else{
//                        if (lockLoading != null && lockLoading.isShowing()){
//                            lockLoading.dismiss();
//                        }
//
//                        customDialog4.show();
//
//                        ccn++;
//
//                        return;
//                    }
//                }else{
//                    if (lockLoading != null && lockLoading.isShowing()){
//                        lockLoading.dismiss();
//                    }
//
//                    closeEbike();
//                }
//            }
//        }, 2 * 1000);
//    }

    public void closeEbike2(){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("oid",oid);
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
//                              ToastUtil.showMessage(context,"数据更新成功");

                                Log.e("biking===", "closeEbike===="+result.getData());

                                info = result.getInfo();

                                if ("0".equals(result.getData())){
                                    if (loadingDialog != null && loadingDialog.isShowing()){
                                        loadingDialog.dismiss();
                                    }

                                    submit(uid, access_token);

                                    ToastUtil.showMessageApp(context,"关锁成功");
                                }else{
//                                    ToastUtil.showMessageApp(context,"关锁失败");

                                    bleService.connect(m_nowMac);
                                    cn = 0;

                                    closeLock3();
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

    void closeLock3(){
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("closeLock3===", m_nowMac+"==="+bleid+"==="+bleService.connect);

                if(!bleService.connect){
                    cn++;

                    if(cn<5){
                        closeLock3();
                    }else{
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }

                        if("108".equals(info)){
                            Log.e("biking===closeLock3_1", "====");

                            ToastUtil.showMessageApp(context,"关锁成功");

                            submit(uid, access_token);
                        }else{
                            Log.e("biking===closeLock3_2", "====");

//                            ToastUtil.showMessageApp(context,"蓝牙连接失败，请重试");
                            customDialog4.show();
                        }


                        return;
                    }

                }else{
                    bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});

                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("closeLock3===4_3", "==="+m_nowMac);

                            button8();
                            button9();
                            button2();    //设防

                            closeLock2();
                        }
                    }, 500);

                }

            }
        }, 2 * 1000);
    }

//    void closeLock(){
//        m_myHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.e("temporaryLock===4_4", bleService.cc+"==="+"B1 2A 80 00 00 5B ".equals(bleService.cc));
//
//                if("B1 2A 80 00 00 5B ".equals(bleService.cc)){
//                    Log.e("temporaryLock===4_5", oid+"==="+bleService.cc);
//
//                    lookPsdBtn.setText("开锁用车");
//                    ToastUtil.showMessageApp(context,"关锁成功");
//
//                    SharedPreferencesUrls.getInstance().putString("tempStat","1");
//
//
//
//                }else{
//                    customDialog7.show();
//                }
//
//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
//
//
//                Log.e("temporaryLock===4_6", "==="+bleService.cc);
//
//            }
//        }, 500);
//    }


//    public void closeEbike(){
//        RequestParams params = new RequestParams();
//        params.put("uid",uid);
//        params.put("access_token",access_token);
//        params.put("oid",oid);
//        HttpHelper.post(this, Urls.closeEbike, params, new TextHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                onStartCommon("正在加载");
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                onFailureCommon(throwable.toString());
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
//                m_myHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                            if (result.getFlag().equals("Success")) {
////                              ToastUtil.showMessage(context,"数据更新成功");
//
//                                Log.e("biking===", "closeEbike===="+result.getData()+"==="+result.getInfo());
//
//                                if ("0".equals(result.getData())){
//                                    m_myHandler.sendEmptyMessage(6);
////                                  submit(uid, access_token);
//                                } else {
//                                    ToastUtil.showMessageApp(context,"关锁失败");
//
//                                    bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});
//
//                                    m_myHandler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Log.e("endBtn4===4_3", "==="+m_nowMac);
//
//                                            button8();
//                                            button9();
//                                            button2();
//
//                                            cn=0;
//                                            closeLock2();
//                                        }
//                                    }, 500);
//
//                                }
//                            } else {
//                                ToastUtil.showMessageApp(context,result.getMsg());
//
//                            }
//                        } catch (Exception e) {
//                        }
//                        if (loadingDialog != null && loadingDialog.isShowing()){
//                            loadingDialog.dismiss();
//                        }
//                    }
//                });
//
//            }
//        });
//    }

    void closeLock2(){

        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("closeLock2===4_4", bleService.cc+"==="+"B1 2A 80 00 00 5B ".equals(bleService.cc));

                if("B1 2A 80 00 00 5B ".equals(bleService.cc)){
                    Log.e("closeLock2===4_5", oid+"==="+bleService.cc);
                    macList2 = new ArrayList<> (macList);
                    submit(uid, access_token);

                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }

                }else{
                    cn++;

                    if(cn<=10){
                        button9();
                        button2();

                        closeLock2();
                    }else{

                        if("108".equals(info)){
                            Log.e("biking===closeLock2_1", "====");

                            ToastUtil.showMessageApp(context,"关锁成功");

                            submit(uid, access_token);
                        }else{
                            Log.e("biking===closeLock2_2", "====");

//                            ToastUtil.showMessageApp(context,"蓝牙连接失败，请重试");
                            customDialog5.show();
                        }

                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                }

                Log.e("closeLock2===4_6", "==="+bleService.cc);

            }
        }, 500);
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

    //设防
    void button2() {
        IoBuffer ioBuffer = IoBuffer.allocate(20);
        byte[] cmd = sendCmd("00001000", "00000000");
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



    public void initmPopupWindowView() {

        // 获取自定义布局文件的视图
        View customView = getLayoutInflater().inflate(R.layout.pop_menu, null, false);
        // 创建PopupWindow宽度和高度
        RelativeLayout pop_win_bg = (RelativeLayout) customView.findViewById(R.id.pop_win_bg);
        ImageView iv_popup_window_back = (ImageView) customView.findViewById(R.id.popupWindow_back);
        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(this);
        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
            UtilBitmap.blurImageView(this, iv_popup_window_back, 10,0xAA000000);
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
                        WindowManager windowManager = getWindowManager();
                        Display display = windowManager.getDefaultDisplay();
                        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                        lp.width = (int) (display.getWidth() * 0.8); // 设置宽度0.6
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                        dialog.getWindow().setAttributes(lp);
                        dialog.show();
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





    private void addChooseMarker() {
        // 加入自定义标签

        if(centerMarker == null){
            MarkerOptions centerMarkerOption = new MarkerOptions().position(myLocation).icon(BitmapDescriptorFactory.fromView(View.inflate(context, R.layout.marker_info_layout, null)));
            centerMarker = aMap.addMarker(centerMarkerOption);
            m_myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    CameraUpdate update = CameraUpdateFactory.changeLatLng(myLocation);
//                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(myLocation));
                    CameraUpdate update = CameraUpdateFactory.zoomTo(18f);
                    aMap.animateCamera(update, 1000, new AMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            aMap.setOnCameraChangeListener(CurRoadBikingActivity.this);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
            }, 1000);
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    private void setUpLocationStyle() {
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));
        myLocationStyle.strokeWidth(0);
        myLocationStyle.strokeColor(R.color.main_theme_color);
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);
        aMap.setMyLocationStyle(myLocationStyle);
    }

    /**
     * 学校范围电子栅栏
     *
     * */
    private void schoolRange(){

        Log.e("biking===schoolRange0", type+"==="+jsonArray);



        if(jsonArray != null){
            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    try{
                        if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
                            isContainsList.clear();
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            List<LatLng> list = new ArrayList<>();
                            List<LatLng> list2 = new ArrayList<>();
                            int flag = 0;

                            for (int j = 0; j < jsonArray.getJSONArray(i).length(); j++) {

                                JSONObject jsonObject = jsonArray.getJSONArray(i).getJSONObject(j);

                                LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")), Double.parseDouble(jsonObject.getString("longitude")));

                                if (jsonObject.getInt("is_yhq") == 0) {
                                    flag = 0;
                                    list.add(latLng);
                                } else {
                                    flag = 1;
                                    list2.add(latLng);
                                }

                                listPoint.add(latLng);
                            }

                            Polygon polygon = null;
                            PolygonOptions pOption = new PolygonOptions();
                            if (flag == 0) {
                                pOption.addAll(list);
                                if (("4".equals(type) || "7".equals(type))) {
                                    polygon = aMap.addPolygon(pOption.strokeWidth(2)
                                            .strokeColor(Color.argb(255, 48, 191, 186))
                                            .fillColor(Color.argb(75, 18, 237, 226)));
                                } else {

                                    polygon = aMap.addPolygon(pOption.strokeWidth(2)
                                            .strokeColor(Color.argb(255, 228, 59, 74))
                                            .fillColor(Color.argb(75, 230, 0, 18)));
                                }

                                getCenterPoint(list);
                            } else {
                                pOption.addAll(list2);
                                polygon = aMap.addPolygon(pOption.strokeWidth(2)
                                        .strokeColor(Color.argb(255, 255, 80, 23))
                                        .fillColor(Color.argb(75, 255, 80, 23)));

                                getCenterPoint2(list2);
                            }

                            pOptions.add(polygon);

                            isContainsList.add(polygon.contains(myLocation));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e("biking===schoolRange_e", "==="+e);
                    }
                }
            });

        }else{
            RequestParams params = new RequestParams();
            params.put("type", ("4".equals(type)||"7".equals(type))?2:1);

            HttpHelper.get(context, Urls.schoolRange, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在加载");
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    onFailureCommon(throwable.toString());
                }

//                @Override
//                public void onStart() {
//                    m_myHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (loadingDialog2 != null && !loadingDialog2.isShowing()) {
//                                loadingDialog2.setTitle("正在加载");
//                                loadingDialog2.show();
//                            }
//                        }
//                    });
//                }
//                @Override
//                public void onFailure(int statusCode, Header[] headers, String responseString, final Throwable throwable) {
//                    m_myHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (loadingDialog2 != null && loadingDialog2.isShowing()){
//                                loadingDialog2.dismiss();
//                            }
//                            UIHelper.ToastError(context, throwable.toString());
//                        }
//                    });
//                }


                @Override
                public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.e("biking===schoolRange1", "==="+responseString);

//                                    String ss ="{\"flag\":\"Success\",\"errcode\":\"0\",\"msg\":\"\\u5b66\\u6821\\u6805\\u680f\",\"data\":[[{\"longitude\":\"118.28775\",\"latitude\":\"35.11533\",\"is_yhq\":0},{\"longitude\":\"118.28773\",\"latitude\":\"35.11581\",\"is_yhq\":0},{\"longitude\":\"118.28872\",\"latitude\":\"35.11580\",\"is_yhq\":0},{\"longitude\":\"118.28871\",\"latitude\":\"35.11534\",\"is_yhq\":0}],[{\"longitude\":\"118.29537\",\"latitude\":\"35.11433\",\"is_yhq\":0},{\"longitude\":\"118.29410\",\"latitude\":\"35.11433\",\"is_yhq\":0},{\"longitude\":\"118.29413\",\"latitude\":\"35.11339\",\"is_yhq\":0},{\"longitude\":\"118.29538\",\"latitude\":\"35.11338\",\"is_yhq\":0}]]}";

//                                    String ss ="{\"flag\":\"Success\",\"errcode\":\"0\",\"msg\":\"\\u5b66\\u6821\\u6805\\u680f\",\"data\":\"1\",\"status\":\"2\"}";
//                                    com.jsoniter.spi.JsonException: readInt: expect 0~9, head: 29, peek: errcode":", buf: {"flag":"Success"

//                                    ResultConsel2 result = JsonIterator.deserialize(responseString, ResultConsel2.class);
//                                    JsonIterator.deserialize(responseString, ResultConsel.class);

//                                    JsonIterator iter = JsonIterator.parse(responseString.replace('\'', '"'));
                                JsonIterator iter = JsonIterator.parse(responseString);
                                ResultConsel2 result = iter.read(ResultConsel2.class);

//                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                                Log.e("biking===schoolRange2", "==="+result.getData().getClass());
                                if (result.getFlag().equals("Success")) {

//                              IterImplArray.

//                                JSONArray jsonArray = new JSONArray(JSON.toJSONString(result.getData()));
                                    jsonArray = new JSONArray(result.getData().toString());
//                                JSONArray.parseArray(JSON.toJSONString(studentList));
//                                com.alibaba.fastjson.JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(result.getData()));

//                                        iter.readArray()
                                    Log.e("biking===schoolRange3", "==="+jsonArray);
                                    if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
                                        isContainsList.clear();
                                    }

                                    double s=0;
                                    double s1=0;

                                    double x = 0;
                                    double y = 0;

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        List<LatLng> list = new ArrayList<>();
                                        List<LatLng> list2 = new ArrayList<>();
                                        int flag=0;
//                                    if (polyline2 != null) {
//                                        polyline2.remove();
//                                    }
//                                    mPolyoptions2 = new PolylineOptions();
//                                    mPolyoptions2.width(10f);
//                                    mPolyoptions2.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.grasp_trace_line));

//                                  Log.e("main===schoolRange1", i+"==="+jsonArray.length()+"==="+jsonArray.getJSONArray(i).length());

                                        Polyline polyline2 = null;
                                        for (int j = 0; j < jsonArray.getJSONArray(i).length(); j ++){

                                            JSONObject jsonObject = jsonArray.getJSONArray(i).getJSONObject(j);
//                                        com.alibaba.fastjson.JSONObject jsonObject = jsonArray.getJSONArray(i).getJSONObject(j);

//                                      Log.e("main===schoolRange1", "221==="+jsonObject.getInt("is_yhq"));

                                            LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")), Double.parseDouble(jsonObject.getString("longitude")));

                                            if(jsonObject.getInt("is_yhq")==0){
                                                flag=0;
                                                list.add(latLng);

                                            }else{
                                                flag=1;
                                                list2.add(latLng);

                                            }

                                            listPoint.add(latLng);

//                                        mPolyoptions2.add(latLng);
//
////                                        List<GeoPoint> points = new ArrayList<GeoPoint>();
////                                        points.add(new GeoPoint(39907794,116356694));
//
//                                        if (mPolyoptions2.getPoints().size() > 1 && j==jsonArray.getJSONArray(i).length()-1 ) {
//
//                                            Log.e("biking===schoolRange", "==="+mPolyoptions2);
////                                            pOptions.add(polygon);
////                                            mPolyoptions2.addAll()
////                                            polyline2 = aMap.addPolyline(mPolyoptions2);
////                                            polyline2 = new Polyline(mPolyoptions2);
//                                        }
                                        }

//                                    Log.e("biking===schoolRange2", i+"==="+jsonArray.getJSONArray(i).length()+"==="+myLocation+">>>");


//                                    LatLng nearestLatLng = polyline2.getNearestLatLng(myLocation);
//                                    s1 = AMapUtils.calculateLineDistance(nearestLatLng, new LatLng(myLocation.latitude, myLocation.longitude));
//
//                                    if(i==0 || s1<s){
//                                        s = s1;
//
//                                        x = nearestLatLng.longitude;
//                                        y = nearestLatLng.latitude;
//                                    }

//                                    Log.e("main===schoolRange1", "333==="+list2+"==="+flag);

                                        Polygon polygon = null;
                                        PolygonOptions pOption = new PolygonOptions();
                                        if(flag==0){
                                            pOption.addAll(list);
                                            if(("4".equals(type) || "7".equals(type))){
                                                polygon = aMap.addPolygon(pOption.strokeWidth(2)
                                                        .strokeColor(Color.argb(255, 48, 191, 186))
                                                        .fillColor(Color.argb(75, 18, 237, 226)));
                                            }else{
//                                            Log.e("biking===schoolRange3", "==="+mPolyoptions2);

                                                polygon = aMap.addPolygon(pOption.strokeWidth(2)
                                                        .strokeColor(Color.argb(255, 228, 59, 74))
                                                        .fillColor(Color.argb(75, 230, 0, 18)));
                                            }

                                            getCenterPoint(list);
                                        }else{
                                            pOption.addAll(list2);
                                            polygon = aMap.addPolygon(pOption.strokeWidth(2)
                                                    .strokeColor(Color.argb(255, 255, 80, 23))
                                                    .fillColor(Color.argb(75, 255, 80, 23)));

                                            getCenterPoint2(list2);
                                        }

                                        pOptions.add(polygon);

                                        isContainsList.add(polygon.contains(myLocation));

                                    }

//                                AMapUtils.s..GeometryUtil.distanceToLine(pos,path)
//
//                                AMapUtils.

                                    Log.e("y===x", listPoint.size()+"==="+myLocation.latitude+","+myLocation.longitude+"==="+y+","+x);

//                                minPoint();

//                                MarkerOptions centerMarkerOption = new MarkerOptions().position(polyline2.getNearestLatLng(myLocation)).icon(freeDescripter);
//                                MarkerOptions centerMarkerOption = new MarkerOptions().position(new LatLng(y, x)).icon(freeDescripter);
//                                aMap.addMarker(centerMarkerOption);
//
//                                if (polyline2 != null) {
//                                    polyline2.remove();
//                                }

                                }else {
                                    ToastUtil.showMessageApp(context,result.getMsg());
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                Log.e("biking===schoolRange_e", "==="+e);
                            }
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }
                    });

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                }).start();
                }
            });
        }

    }

    public LatLng minPolylineToPolygon() {
        double s=0;
        double s1=0;

        double x = 0;
        double y = 0;



        if (AMapUtils.calculateLineDistance(polyline2.getNearestLatLng(myLocation), new LatLng(myLocation.latitude, myLocation.longitude)) > 10)

        for (int i = 0; i < centerList.size(); i++) {

            Log.e("minPolygon===", centerList.get(i).latitude+"==="+referLatitude+">>>"+centerList.get(i).longitude+"==="+referLongitude);

            s1 = (centerList.get(i).latitude-referLatitude)*(centerList.get(i).latitude-referLatitude) + (centerList.get(i).longitude-referLongitude)*(centerList.get(i).longitude-referLongitude);

            if(i==0 || s1<s){
                s = s1;

                x = centerList.get(i).longitude;
                y = centerList.get(i).latitude;
            }else{
                continue;
            }
        }

        PolylineOptions pOption = new PolylineOptions();

        pOption.setDottedLine(true);
        pOption.setDottedLineType(1);
        pOption.width(20);
        pOption.color(Color.argb(255, 0, 0, 255));
        pOption.useGradient(true);
        pOption.add(new LatLng(referLatitude, referLongitude));
        pOption.add(new LatLng(y, x));

        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(18f);// 设置缩放监听
        aMap.moveCamera(cameraUpdate);

        polyline = aMap.addPolyline(pOption);

        Log.e("minPolygon===2", "==="+s);

        return null;
    }

    public LatLng getCenterPoint(List<LatLng> list) {
        double x = 0.0;
        double y = 0.0;
        for (int i = 0; i < list.size(); i++) {
            x = x + list.get(i).longitude;
            y = y + list.get(i).latitude;
        }
        x = x / list.size();
        y = y / list.size();

        centerList.add(new LatLng(y, x));

        //        Log.e("getCenterPoint===2", x+"==="+y);

        return new LatLng(y, x);
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

//        PolylineOptions pOption = new PolylineOptions();
//
//        pOption.setDottedLine(true);
//        pOption.setDottedLineType(1);
//        pOption.width(20);
//        pOption.color(Color.argb(255, 0, 0, 255));
//        pOption.useGradient(true);
//        pOption.add(new LatLng(referLatitude, referLongitude));
//        pOption.add(new LatLng(y, x));
//
//        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(18f);// 设置缩放监听
//        aMap.moveCamera(cameraUpdate);
//
//        polyline = aMap.addPolyline(pOption);

        float dis = AMapUtils.calculateLineDistance(new LatLng(referLatitude, referLongitude), new LatLng(y, x));

        Log.e("minPoint===2", s+"==="+dis);

        if(!isContainsList.contains(true) && dis < 30){
            isContainsList.add(true);
        }

        return null;
    }

    public LatLng minPolygon() {
        double s=0;
        double s1=0;

        double x = 0;
        double y = 0;

        for (int i = 0; i < centerList.size(); i++) {

            Log.e("minPolygon===", centerList.get(i).latitude+"==="+referLatitude+">>>"+centerList.get(i).longitude+"==="+referLongitude);

            s1 = (centerList.get(i).latitude-referLatitude)*(centerList.get(i).latitude-referLatitude) + (centerList.get(i).longitude-referLongitude)*(centerList.get(i).longitude-referLongitude);

            if(i==0 || s1<s){
                s = s1;

                x = centerList.get(i).longitude;
                y = centerList.get(i).latitude;
            }else{
                continue;
            }
        }

        PolylineOptions pOption = new PolylineOptions();

        pOption.setDottedLine(true);
        pOption.setDottedLineType(1);
        pOption.width(20);
        pOption.color(Color.argb(255, 0, 0, 255));
        pOption.useGradient(true);
        pOption.add(new LatLng(referLatitude, referLongitude));
        pOption.add(new LatLng(y, x));

        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(18f);// 设置缩放监听
        aMap.moveCamera(cameraUpdate);

        polyline = aMap.addPolyline(pOption);

        Log.e("minPolygon===2", "==="+s);

        return null;
    }

    public LatLng getCenterPoint2(List<LatLng> list) {
        double x = 0.0;
        double y = 0.0;
        for (int i = 0; i < list.size(); i++) {
            x = x + list.get(i).longitude;
            y = y + list.get(i).latitude;
        }
        x = x / list.size();
        y = y / list.size();

        centerList.add(new LatLng(y, x));

        MarkerOptions centerMarkerOption = new MarkerOptions().position(new LatLng(y, x)).icon(freeDescripter);
        aMap.addMarker(centerMarkerOption);

        return new LatLng(y, x);
    }

//    private void schoolrangeList(){
//        RequestParams params = new RequestParams();
//        HttpHelper.get(context, schoolrangeList, params, new TextHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                    loadingDialog.setTitle("正在加载");
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
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                try {
//                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                    if (result.getFlag().equals("Success")) {
//                        JSONArray jsonArray = new JSONArray(result.getData());
//                        if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
//                            isContainsList.clear();
//                        }
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            List<LatLng> list = new ArrayList<>();
//                            for (int j = 0; j < jsonArray.getJSONArray(i).length(); j ++){
//                                JSONObject jsonObject = jsonArray.getJSONArray(i).getJSONObject(j);
//                                LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")),
//                                        Double.parseDouble(jsonObject.getString("longitude")));
//                                list.add(latLng);
//                            }
//                            Polygon polygon = null;
//                            PolygonOptions pOption = new PolygonOptions();
//                            pOption.addAll(list);
//                            polygon = aMap.addPolygon(pOption.strokeWidth(2)
//                                    .strokeColor(Color.argb(160, 255, 0, 0))
//                                    .fillColor(Color.argb(160, 255, 0, 0)));
//                            pOptions.add(polygon);
//                            isContainsList.add(polygon.contains(myLocation));
//                        }
//                    }else {
//                        ToastUtil.showMessageApp(context, result.getMsg());
//                    }
//                }catch (Exception e){
//                }
//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
//            }
//        });
//    }

    /**
     * 上传骑行坐标
     **/
    private void addMaplocation(double latitude,double longitude){
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid != null && !"".equals(uid) && access_token != null && !"".equals(access_token)){
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("oid",oid);
            params.put("osn",osn);
            params.put("latitude",latitude);
            params.put("longitude",longitude);
            HttpHelper.post(context, Urls.addMaplocation, params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        if (result.getFlag().equals("Success")) {
                            if (myLocation != null){
                                SharedPreferencesUrls.getInstance().putString("biking_latitude",""+myLocation.latitude);
                                SharedPreferencesUrls.getInstance().putString("biking_longitude",""+myLocation.longitude);
                            }
                        }
                    }catch (Exception e){

                    }
                }
            });
        }
    }
    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            mLocationOption.setInterval(2 * 1000);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

            // 关闭缓存机制
            mLocationOption.setLocationCacheEnable(false);
            //设置是否只定位一次,默认为false
            mLocationOption.setOnceLocation(false);
            //设置是否强制刷新WIFI，默认为强制刷新
            mLocationOption.setWifiActiveScan(true);
            //设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(false);

            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 添加Circle
     * @param latlng  坐标
     * @param radius  半径
     */
    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }

    private ValueAnimator animator = null;

    private void animMarker() {
        isMovingMarker = false;
        if (animator != null) {
            animator.start();
            return;
        }
        animator = ValueAnimator.ofFloat(mapView.getHeight() / 2, mapView.getHeight() / 2 - 30);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(150);
        animator.setRepeatCount(1);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                centerMarker.setPositionByPixels(mapView.getWidth() / 2, Math.round(value));
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                centerMarker.setIcon(BitmapDescriptorFactory.fromView(View.inflate(context, R.layout.marker_info_layout, null)));
            }
        });
        animator.start();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case 288:{
                    break;
                }
                case 188:{

                    Log.e("biking===", "188===="+m_nowMac+"==="+type);

                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if("4".equals(type)) {
                                isLookPsdBtn = true;

                                BLEService.bluetoothAdapter = mBluetoothAdapter;

                                bleService.view = context;
                                bleService.showValue = true;
                            }else if("5".equals(type) || "6".equals(type)){

                                if (lockLoading != null && !lockLoading.isShowing()){
                                    lockLoading.setTitle("正在唤醒车锁");
                                    lockLoading.show();
                                }

                                m_myHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
//                                SearchRequest request = new SearchRequest.Builder()      //duration为0时无限扫描
//                                        .searchBluetoothLeDevice(0)
//                                        .build();
//
//                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                    return;
//                                }
//                                ClientManager.getClient().search(request, mSearchResponse);


                                        connectDeviceLP();
                                        ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
                                        ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);
//
                                        m_myHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!isLookPsdBtn){
                                                    if (lockLoading != null && lockLoading.isShowing()) {
                                                        lockLoading.dismiss();
                                                    }
                                                    Toast.makeText(context,"唤醒失败，重启手机蓝牙试试吧！",Toast.LENGTH_LONG).show();

                                                    ClientManager.getClient().stopSearch();
                                                    ClientManager.getClient().disconnect(m_nowMac);
                                                    ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
                                                    ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);

                                                    scrollToFinishActivity();
                                                }
                                            }
                                        }, 15 * 1000);

                                    }
                                }, 2 * 1000);


                            }else if("7".equals(type)) {
                                isLookPsdBtn = true;

//                                XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
//                                builder.setBleStateChangeListener(CurRoadBikingActivity.this);
//                                builder.setScanResultCallback(CurRoadBikingActivity.this);
//                                apiClient = builder.build();
//
//                                CurRoadBikingActivityPermissionsDispatcher.connectDeviceWithPermissionCheck(CurRoadBikingActivity.this, deviceuuid);
                            }else{

                                if (lockLoading != null && !lockLoading.isShowing()){
                                    lockLoading.setTitle("正在唤醒车锁");
                                    lockLoading.show();
                                }

                                m_myHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        connect();

                                        m_myHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!isStop){
                                                    if (lockLoading != null && lockLoading.isShowing()) {
                                                        lockLoading.dismiss();
                                                    }
                                                    Toast.makeText(context,"唤醒失败，重启手机蓝牙试试吧！",Toast.LENGTH_LONG).show();
                                                    BaseApplication.getInstance().getIBLE().refreshCache();
                                                    BaseApplication.getInstance().getIBLE().close();
                                                    BaseApplication.getInstance().getIBLE().disconnect();
                                                    scrollToFinishActivity();
                                                }
                                            }
                                        }, 15 * 1000);
                                    }
                                }, 2 * 1000);

                                closeBroadcast();
                                registerReceiver(Config.initFilter());
                                GlobalParameterUtils.getInstance().setLockType(LockType.MTS);
                            }
                        }
                    });

                    break;
                }
                default:{
                    break;
                }
            }
        }else if( requestCode == 188){
            ToastUtil.showMessageApp(this, "需要打开蓝牙");
//            finish();
//            scrollToFinishActivity();
            AppManager.getAppManager().AppExit(context);
        }
    }

    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            Log.e("biking===","DeviceListActivity.onSearchStarted");
        }

        @Override
        public void onDeviceFounded(final SearchResult device) {
            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("biking=onDeviceFounded",device.device.getName() + "===" + device.device.getAddress()+"==="+m_nowMac);

                    if(m_nowMac.equals(device.device.getAddress())){
                        ClientManager.getClient().stopSearch();

                        connectDeviceLP();

                        ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
                        ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);
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

    private final SearchResponse mSearchResponse2 = new SearchResponse() {
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
                                        rent();
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

//                        m_myHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//
//                            }
//                        }, 2000);


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

    protected void rent(){

        Log.e("rent===000",m_nowMac+"==="+keySource);

        RequestParams params = new RequestParams();
        params.put("macinfo", m_nowMac);
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
                        Log.e("rent===","==="+responseString);
                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                            if (result.getFlag().equals("Success")) {
                                KeyBean bean = JSON.parseObject(result.getData(), KeyBean.class);

                                encryptionKey = bean.getEncryptionKey();
                                keys = bean.getKeys();
                                serverTime = bean.getServerTime();

                                Log.e("rent===", m_nowMac+"==="+encryptionKey+"==="+keys);

                                openBleLock(null);
                            }else {
                                ToastUtil.showMessageApp(context, result.getMsg());

                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }
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

    private void rent2(){
        String secretKey = "";

        secretKey = Constants.keyMap.get(m_nowMac);

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

        Log.e("rent===", encryptionKey+"==="+keys+"==="+pwd);

        openBleLock(null);

    }

    private void initSite(){
        RequestParams params = new RequestParams();
        HttpHelper.get(context, Urls.stopSite, params, new TextHttpResponseHandler() {
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
                                JSONArray array = new JSONArray(result.getData());
                                for (Marker marker : siteMarkerList){
                                    if (marker != null){
                                        marker.remove();
                                    }
                                }
                                if (!siteMarkerList.isEmpty() || 0 != siteMarkerList.size()){
                                    siteMarkerList.clear();
                                }
                                if (0 == array.length()){
                                    ToastUtil.showMessageApp(context,"附近没有停车点");
                                }else {
                                    for (int i = 0; i < array.length(); i++){
                                        NearbyBean bean = JSON.parseObject(array.getJSONObject(i).toString(), NearbyBean.class);
                                        // 加入自定义标签
                                        MarkerOptions siteMarkerOption = new MarkerOptions().position(new LatLng(
                                                Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude()))).icon(siteDescripter);
                                        Marker bikeMarker = aMap.addMarker(siteMarkerOption);
                                        siteMarkerList.add(bikeMarker);
                                    }
                                }
                            } else {
                                ToastUtil.showMessageApp(context, result.getMsg());
                            }
                        } catch (Exception e) {
                            Log.e("Test","异常:"+e);
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                });

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (loadingDialog != null && loadingDialog.isShowing()){
                loadingDialog.dismiss();
            }
            if (lockLoading != null && lockLoading.isShowing()){
                lockLoading.dismiss();
            }


            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        if (aMap == null) {
                            aMap = mapView.getMap();
                            setUpMap();
                        }
                        aMap.getUiSettings().setZoomControlsEnabled(false);
                        aMap.getUiSettings().setMyLocationButtonEnabled(false);
                        aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);// 设置地图logo显示在右下方
                        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(18f);// 设置缩放监听
                        aMap.moveCamera(cameraUpdate);
                        setUpLocationStyle();
                    }
                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里允许获取定位权限！")
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
                break;
            case 188:
//                if (!PublicWay.BLE_CONNECT_STATUS){
//                    if (loadingDialog != null && !loadingDialog.isShowing()) {
//                        loadingDialog.setTitle("正在连接");
//                        loadingDialog.show();
//                    }
//
//                    if (!BaseApplication.getInstance().getIBLE().getConnectStatus()){
//                        connect();
//                    }
//
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (lockLoading != null && lockLoading.isShowing()) {
//                                lockLoading.dismiss();
//                            }
//                        }
//                    }, 10 * 1000);
//
//                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

//    @Override
//    public void onReceive(Context context, Intent intent) {}

    protected void registerReceiver(IntentFilter intentfilter) {
        if (internalReceiver == null) {
            internalReceiver = new InternalReceiver();
        }
        registerReceiver(internalReceiver, intentfilter);
    }




    private class InternalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            handleReceiver(context, intent);

        }
    };

    private void setMovingMarker() {
        if (isMovingMarker)
            return;

        isMovingMarker = true;
        centerMarker.setPositionByPixels(mapView.getWidth() / 2, mapView.getHeight() / 2);
        centerMarker.setIcon(BitmapDescriptorFactory.fromView(View.inflate(context, R.layout.marker_info_layout, null)));
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (centerMarker != null) {
            setMovingMarker();
        }
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        Log.e("biking===ChangeFinish_B", isContainsList.contains(true) + "》》》" + cameraPosition.target.latitude + "===" + macList.size()+">>>"+ cameraPosition.target.latitude);


        if (isUp){
//            initNearby(cameraPosition.target.latitude, cameraPosition.target.longitude);

            if (centerMarker != null) {
//				animMarker();
                m_myHandler.sendEmptyMessage(8);
            }
        }

//        if (macList.size() != 0) {
//            macList.clear();
//        }
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        Log.e("biking===onTouch", "===" + motionEvent.getAction());


        if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                motionEvent.getAction() == MotionEvent.ACTION_CANCEL || motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE
                || motionEvent.getActionMasked() == MotionEvent.ACTION_POINTER_UP){
            isUp = true;
        }else {
            isUp = false;
        }
    }


}
