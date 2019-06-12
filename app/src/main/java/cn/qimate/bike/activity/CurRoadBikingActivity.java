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
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.aprilbrother.aprilbrothersdk.BeaconManager;
import com.aprilbrother.aprilbrothersdk.EddyStone;
import com.sofi.blelocker.library.Code;
import com.sofi.blelocker.library.connect.listener.BleConnectStatusListener;
import com.sofi.blelocker.library.connect.options.BleConnectOptions;
import com.sofi.blelocker.library.model.BleGattProfile;
import com.sofi.blelocker.library.protocol.ICloseListener;
import com.sofi.blelocker.library.protocol.IConnectResponse;
import com.sofi.blelocker.library.protocol.IEmptyResponse;
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
import com.sunshine.blelibrary.utils.GlobalParameterUtils;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import cn.http.OkHttpClientManager;
import cn.http.ResultCallback;
import cn.http.rdata.RRent;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseApplication;
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
import cn.qimate.bike.fragment.BikeFragment;
import cn.qimate.bike.jpush.ServiceReceiver;
import cn.qimate.bike.model.CurRoadBikingBean;
import cn.qimate.bike.model.EbikeInfoBean;
import cn.qimate.bike.model.NearbyBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.ByteUtil;
import cn.qimate.bike.util.Globals;
import cn.qimate.bike.util.IoBuffer;
import cn.qimate.bike.util.PublicWay;
import cn.qimate.bike.util.ToastUtil;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;
import okhttp3.Request;

import static cn.qimate.bike.core.common.Urls.schoolrangeList;
import static com.sofi.blelocker.library.Constants.STATUS_CONNECTED;

/**
 * Created by Administrator on 2017/2/12 0012.
 */
@SuppressLint("NewApi")
public class CurRoadBikingActivity extends SwipeBackActivity implements View.OnClickListener,
        LocationSource,AMapLocationListener
        ,OnConnectionListener
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
    private  LoadingDialog loadingDialog;
    private  LoadingDialog loadingDialog2;
    private  LoadingDialog lockLoading;
    private LinearLayout mainLayout;
    private ImageView backImg;
    private TextView title;
    private TextView rightBtn;

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

    public static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    public static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstFix = true;
    private LatLng myLocation = null;
    private Circle mCircle;
    private BitmapDescriptor successDescripter;
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

        if(BaseApplication.getInstance().isTest()){
            type = "5";
        }


//        ClientManager.getClient().stopSearch();
//        ClientManager.getClient().disconnect(m_nowMac);
//        ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
//        ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);


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

//        customBuilder = new CustomDialog.Builder(context);
//        customBuilder.setTitle("温馨提示").setMessage("不在还车点，请至校内地图红色区域停车")
//                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        customDialog4 = customBuilder.create();
    }

    //连接泺平锁
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
//                refreshData(true);

                if (Globals.bType == 1) {
                    ToastUtil.showMessageApp(context, "正在关锁中");
//                    getBleRecord();
                }
            }
        });
    }

    //监听当前连接状态
    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            BluetoothLog.v(String.format(Locale.getDefault(), "DeviceDetailActivity onConnectStatusChanged %d in %s", status, Thread.currentThread().getName()));

            Log.e("ConnectStatus===biking", (status == STATUS_CONNECTED)+"===="+m_nowMac);

//            Globals.isBleConnected = mConnected = (status == STATUS_CONNECTED);
//            refreshData(mConnected);
//            connectDeviceIfNeeded();
        }
    };

    //监听当前连接状态
    private final BleConnectStatusListener mConnectStatusListener2 = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            BluetoothLog.v(String.format(Locale.getDefault(), "DeviceDetailActivity onConnectStatusChanged %d in %s", status, Thread.currentThread().getName()));

            Log.e("ConnectStatus2===biking", (status == STATUS_CONNECTED)+"===="+m_nowMac);

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
                    ToastUtil.showMessageApp(context, Code.toString(code));
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
            public void onResponse(RRent rRent) {

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

            @Override
            public void onError(Request request, Exception e) {
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }

                ToastUtil.showMessageApp(context, e.getMessage());
            }

        });
    }

    //与设备，开锁
    private void openBleLock(RRent.ResultBean resultBean) {
//        UIHelper.showProgress(this, "open_bike_status");
//        ClientManager.getClient().openLock(mac, "18112348925", resultBean.getServerTime(),

        Log.e("openBleLock===", resultBean.getServerTime()+"==="+resultBean.getKeys()+"==="+resultBean.getEncryptionKey());

        ClientManager.getClient().openLock(m_nowMac,"000000000000", resultBean.getServerTime(),
                resultBean.getKeys(), resultBean.getEncryptionKey(), new IEmptyResponse(){
                    @Override
                    public void onResponseFail(int code) {
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }

                        Log.e("openLock===Fail", Code.toString(code));
                        ToastUtil.showMessageApp(context, Code.toString(code));
                    }

                    @Override
                    public void onResponseSuccess() {
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }

                        Log.e("openLock===Success", "===");

                        ToastUtil.showMessageApp(context, "开锁成功");
                        SharedPreferencesUrls.getInstance().putString("tempStat","0");

//                        ClientManager.getClient().stopSearch();
//                        ClientManager.getClient().disconnect(m_nowMac);
//                      ClientManager.getClient().unnotifyClose(mac, mCloseListener);
//                        ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener2);

                    }
                });
    }

    //监听锁关闭事件
    private final ICloseListener mCloseListener = new ICloseListener() {
        @Override
        public void onNotifyClose() {

            Log.e("onNotifyClose===", "====");

            ToastUtil.showMessageApp(context,"锁已关闭");

//            submit(uid, access_token);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lookPsdBtn.setText("开锁用车");
                }
            });

//            BluetoothLog.v(String.format("DeviceDetailActivity onNotifyClose"));
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    tvOpen.setText("开锁");
//                }
//            });
//            getBleRecord();
        }
    };

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

//        loadingDialog2 = new LoadingDialog(this);
//        loadingDialog2.setCancelable(false);
//        loadingDialog2.setCanceledOnTouchOutside(false);

        lockLoading = new LoadingDialog(this);
        lockLoading.setCancelable(false);
        lockLoading.setCanceledOnTouchOutside(false);

        dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.ui_frist_view, null);
        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);

        hintText = (TextView)findViewById(R.id.curRoadUI_biking_hintText);

        titleImage = (ImageView)dialogView.findViewById(R.id.ui_fristView_title);
//        exImage_1 = (ImageView)dialogView.findViewById(R.id.ui_fristView_exImage_1);
//        exImage_2 = (ImageView)dialogView.findViewById(R.id.ui_fristView_exImage_2);
//        exImage_3 = (ImageView)dialogView.findViewById(R.id.ui_fristView_exImage_3);
        closeBtn = (ImageView)dialogView.findViewById(R.id.ui_fristView_closeBtn);

        mainLayout = (LinearLayout)findViewById(R.id.mainUI_title_mainLayout);
        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
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
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        aMap.setMapType(AMap.MAP_TYPE_NAVI);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);// 设置地图logo显示在右下方
        aMap.getUiSettings().setLogoBottomMargin(-50);

        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(20);// 设置缩放监听
        aMap.moveCamera(cameraUpdate);
        successDescripter = BitmapDescriptorFactory.fromResource(R.drawable.icon_usecarnow_position_succeed);
        siteDescripter = BitmapDescriptorFactory.fromResource(R.drawable.site_mark_icon);
        setUpLocationStyle();

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) titleImage.getLayoutParams();
        params.height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.16);
        titleImage.setLayoutParams(params);

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

//        exImage_1.setOnClickListener(myOnClickLister);
//        exImage_2.setOnClickListener(myOnClickLister);
        closeBtn.setOnClickListener(myOnClickLister);

        initSite();

        uid = SharedPreferencesUrls.getInstance().getString("uid","");
        access_token = SharedPreferencesUrls.getInstance().getString("access_token","");

        ToastUtil.showMessage(this, uid+"===="+access_token);
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

        Log.e("biking===", "biking====flagm==="+flagm);

        if (flagm == 1) {
            flagm = 0;
            return;
        }

        Log.e("biking===", "biking====onResume==="+type+"==="+m_nowMac);

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

            BLEService.bluetoothAdapter = mBluetoothAdapter;

            bleService.view = context;
            bleService.showValue = true;

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

                BLEService.bluetoothAdapter = mBluetoothAdapter;

                bleService.view = context;
                bleService.showValue = true;

                if("5".equals(type)){
                    SearchRequest request = new SearchRequest.Builder()      //duration为0时无限扫描
                            .searchBluetoothLeDevice(0)
                            .build();

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    ClientManager.getClient().search(request, mSearchResponse);

                }

//                connectDevice();
//                ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
//                ClientManager.getClient().notifyClose(m_nowMac, mCloseListener); //监听锁关闭事件

//                    if (n > 0) {
//                        startXB();
//
//                        if (lockLoading != null && !lockLoading.isShowing()) {
//                            lockLoading.setTitle("还车点确认中");
//                            lockLoading.show();
//                        }
//
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    int n = 0;
//                                    while (macList.size() == 0) {
//                                        Thread.sleep(1000);
//                                        n++;
//
//                                        Log.e("main===", "n====" + n);
//
//                                        if (n >= 6) break;
//                                    }
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//
//                                m_myHandler.sendEmptyMessage(3);
//                            }
//                        }).start();
//                    } else {
//                        n++;
//
//                        if (lockLoading != null && !lockLoading.isShowing()) {
//                            lockLoading.setTitle("正在连接");
//                            lockLoading.show();
//                        }
//
//                        isStop = false;
//                        m_myHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (lockLoading != null && lockLoading.isShowing()) {
//                                    lockLoading.dismiss();
//                                }
//
//                                Log.e("biking===", "3===" + isStop);
//
//                                if (!isStop) {
//                                    stopScan = true;
//                                    BaseApplication.getInstance().getIBLE().refreshCache();
//                                    BaseApplication.getInstance().getIBLE().close();
//                                    BaseApplication.getInstance().getIBLE().disconnect();
//
//                                    if ("3".equals(type)) {
//                                        if (first3) {
//                                            first3 = false;
//                                            customDialog4.show();
//                                        } else {
//                                            carClose();
//                                        }
//                                    } else {
//                                        customDialog3.show();
//                                    }
//                                }
//                            }
//                        }, 10 * 1000);
//
//                        connect();
//                    }

                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在唤醒车锁");
                    loadingDialog.show();
                }

                connect();

            }

            getCurrentorderBiking(uid, access_token);
            refreshLayout.setVisibility(View.VISIBLE);
        }

        ToastUtil.showMessage(this, "biking===="+internalReceiver);

        closeBroadcast();

        registerReceiver(Config.initFilter());
        GlobalParameterUtils.getInstance().setLockType(LockType.MTS);

        getFeedbackStatus();
    }


    protected void handleReceiver(Context context, Intent intent) {
        String action = intent.getAction();
        String data = intent.getStringExtra("data");

        switch (action) {
            case Config.TOKEN_ACTION:
                Log.e("biking===", "TOKEN_ACTION==="+stopScan);

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

                ToastUtil.showMessageApp(CurRoadBikingActivity.this,"设备连接成功");
                switch (flag){
                    case 0:
                        break;
                    case 1:
                        //开锁
                        if (loadingDialog != null && !loadingDialog.isShowing()) {
                            loadingDialog.setTitle("正在开锁");
                            loadingDialog.show();
                        }
                        BaseApplication.getInstance().getIBLE().openLock();
                        break;
                    case 2:
                        macList2 = new ArrayList<> (macList);
                        BaseApplication.getInstance().getIBLE().getLockStatus();
                        break;
                    default:
                        break;
                }
                flag = 0;
                break;
            case Config.BATTERY_ACTION:
                Log.e("biking===", "BATTERY_ACTION==="+stopScan);

                macList2 = new ArrayList<> (macList);
                BaseApplication.getInstance().getIBLE().getLockStatus();
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

                    //锁已关闭
                    if (mBluetoothAdapter == null) {
                        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                        mBluetoothAdapter = bluetoothManager.getAdapter();
                    }

                    if (mBluetoothAdapter == null) {
                        ToastUtil.showMessageApp(CurRoadBikingActivity.this, "获取蓝牙失败");
                        scrollToFinishActivity();
                        return;
                    }

                    if (!mBluetoothAdapter.isEnabled()) {
                        flagm = 1;
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, 188);
                    }else{

                        if(!isEndBtn) break;

//                        if("3".equals(type)){
//                            if (!isContainsList.contains(true) && macList2.size() <= 0) {
//                                customDialog4.show();
//                            } else {
//                                submit(uid, access_token);
//                            }
//                        }else{
//
//                        }

                        if (!isContainsList.contains(true) && macList2.size() <= 0) {
                            customDialog3.show();

                            clickCountDeal();
                        } else {
//                            submit(uid, access_token);

                            m_myHandler.sendEmptyMessage(6);

                        }

                        Log.e("biking===", "biking2===锁已关闭"+macList2.size());

                    }

                } else {
                    //锁已开启
                    ToastUtil.showMessageApp(context,"车锁未关，请手动关锁");

                    clickCountDeal();

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


    private void getCurrentorderBiking(final String uid, final String access_token){
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
                        ToastUtil.showMessageApp(context,"数据更新成功");
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

                            if(BaseApplication.getInstance().isTest()){
                                type = "5";
                            }

//                            hintText.setText("校内地图红色区域关锁，并点击结束");

                            if ("1".equals(bean.getType())){
                                hintText.setText("还车须至校园地图"+("4".equals(type)?"绿色":"红色")+"区域，关锁并拨乱密码后点击结束！");
                                lookPsdBtn.setText("查看密码");
                            }else {
                                hintText.setText("还车须至校园地图"+("4".equals(type)?"绿色":"红色")+"区域，距车1米内点击结束！");
                                m_nowMac = bean.getMacinfo();


                                if ("4".equals(type)){

                                    ebikeInfoLayout.setVisibility(View.VISIBLE);

                                    if(ebikeInfoThread ==null){
                                        Runnable ebikeInfoRunnable = new Runnable() {
                                            @Override
                                            public void run() {
                                                while(isWaitEbikeInfo){

                                                    m_myHandler.sendEmptyMessage(4);

                                                    try {
                                                        Thread.sleep(5*60*1000);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }
                                        };

                                        ebikeInfoThread = new Thread(ebikeInfoRunnable);
                                        ebikeInfoThread.start();
                                    }

                                    if("0".equals(SharedPreferencesUrls.getInstance().getString("tempStat","0"))){
                                        lookPsdBtn.setText("临时上锁");
                                    }else{
                                        lookPsdBtn.setText("开锁用车");
                                    }


                                }else if ("5".equals(type)){
                                    if(BaseApplication.getInstance().isTest()){
//                                        m_nowMac = "A4:34:F1:7B:BF:9A";
                                        m_nowMac = "3C:A3:08:AE:BE:24";
                                    }

                                    if("0".equals(SharedPreferencesUrls.getInstance().getString("tempStat","0"))){
                                        lookPsdBtn.setText("临时上锁");
                                    }else{
                                        lookPsdBtn.setText("开锁用车");
                                    }


                                }else{
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
                        ToastUtil.showMessageApp(context,"电量更新成功");
                        Log.e("biking===", "ebikeInfo===="+result.getData());

                        EbikeInfoBean bean = JSON.parseObject(result.getData(), EbikeInfoBean.class);
                        electricity.setText(bean.getElectricity());
                        mileage.setText(bean.getMileage());

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

        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//           if (!macList.contains(parseAdvData(rssi,scanRecord))){
//               macList.add(parseAdvData(rssi,scanRecord));
//           }

                k++;

                Log.e("biking===LeScan", device + "====" + rssi + "====" + k);

//                hintText.setText(device + "====" + rssi + "====" + k);

                if (!macList.contains(""+device)){
                    macList.add(""+device);
//                    title.setText(isContainsList.contains(true) + "》》》" + near + "===" + macList.size() + "===" + k);
                }

                scan = true;

            }
        };

        manager = new BeaconManager(this);
        manager.setEddyStoneListener(new BeaconManager.EddyStoneListener() {

            @Override
            public void onEddyStoneDiscovered(EddyStone eddyStone) {
                refreshList(eddyStone);
            }

            private void refreshList(EddyStone eddyStone) {

                k++;

                Log.e("EddyStoneListener===", k+"==="+eddyStone);

                if (!macList.contains(eddyStone)) {
                    macList.add(eddyStone);
                }

                scan = true;

            }
        });


//        startXB();

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

        Log.e("biking====onDestroy", "==="+m_nowMac);


        ClientManager.getClient().stopSearch();
        ClientManager.getClient().disconnect(m_nowMac);
        ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
        ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);

//        if(BaseApplication.getInstance().getIBLE().isEnable()){
//            BaseApplication.getInstance().getIBLE().refreshCache();
//            BaseApplication.getInstance().getIBLE().close();
//            BaseApplication.getInstance().getIBLE().disconnect();
//            BaseApplication.getInstance().getIBLE().disableBluetooth();
//        }



        super.onDestroy();
        mapView.onDestroy();



        ToastUtil.showMessage(this, "biking====onDestroy==="+m_nowMac);

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
//						ToastUtil.showMessageApp(context,"数据更新成功==="+SharedPreferencesUrls.getInstance().getBoolean("isStop",true));

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

    //泺平锁临时上锁
    private void temporaryAction() {
//        UIHelper.showProgress(this, R.string.temporaryAction);
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.setTitle("正在加载");
            loadingDialog.show();
        }
        ClientManager.getClient().temporaryAction(m_nowMac, "000000000000", new ITemporaryActionResponse() {
            @Override
            public void onResponseSuccess() {
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }

                m_myHandler.sendEmptyMessage(7);


            }

            @Override
            public void onResponseFail(int code) {
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, Code.toString(code));
            }
        });
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
                            lookPsdBtn.setText("开锁用车");
                            ToastUtil.showMessageApp(context,"关锁成功");

                            SharedPreferencesUrls.getInstance().putString("tempStat","1");
                        } else {
                            ToastUtil.showMessageApp(context,"关锁失败");

                            bleService.connect(m_nowMac);
                            cn = 0;

                            temporaryLock();

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

                    lookPsdBtn.setText("开锁用车");
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

                        Log.e("biking===", "openEbike===="+result.getData());

                        lookPsdBtn.setText("临时上锁");
                        ToastUtil.showMessageApp(context,"开锁成功");

                        SharedPreferencesUrls.getInstance().putString("tempStat","0");
                    } else {
                        ToastUtil.showMessageApp(context,"开锁失败");

                        bleService.connect(m_nowMac);

                        cn=0;

                        openLock();


//                        bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});
//
//                        m_myHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                Log.e("openLock===4_3", "==="+m_nowMac);
//
//                                button9();
//                                button3();
//
//                                openLock2();
//                            }
//                        }, 500);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.mainUI_title_rightBtn:
                Intent intent = new Intent(context,ClientServiceActivity.class);
                intent.putExtra("bikeCode",bikeCode);
                startActivity(intent);
                scrollToFinishActivity();
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
                if ("查看密码".equals(lookPsdBtn.getText().toString().trim())){
                    customBuilder = new CustomDialog.Builder(this);
                    customBuilder.setTitle("查看密码").setMessage("解锁码："+password)
                            .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    customBuilder.create().show();
                }else if ("临时上锁".equals(lookPsdBtn.getText().toString().trim())){

                    Log.e("biking===lookPsdBtn", "onClick==="+m_nowMac);

                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("正在加载");
                        loadingDialog.show();
                    }

//                    bleService.connect(m_nowMac);
//                    cn = 0;
//                    temporaryLock();

                    if("4".equals(type)){
                        closeEbikeTemp();
                    }else{
                        temporaryAction();
                    }


//                    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                        ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                        scrollToFinishActivity();
//                    }
//
//                    if (mBluetoothAdapter == null) {
//                        ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                        scrollToFinishActivity();
//                        return;
//                    }
//                    if (!mBluetoothAdapter.isEnabled()) {
//                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                        startActivityForResult(enableBtIntent, 188);
//                    }else{
//
//                        Log.e("endBtn4===4_1", "==="+m_nowMac);
//
//                        bleService.connect(m_nowMac);
//
//                        cn=0;
//                        checkConnectCloseTemp();
//
//                    }


                }else if ("开锁用车".equals(lookPsdBtn.getText().toString().trim())){

                    Log.e("biking===lookPsdBtn", "onClick==="+m_nowMac);

//                    if (loadingDialog != null && !loadingDialog.isShowing()) {
//                        loadingDialog.setTitle("正在加载");
//                        loadingDialog.show();
//                    }

//                    bleService.connect(m_nowMac);
//                    cn = 0;
//                    openLock();

                    if("4".equals(type)){
                        openEbike();
                    }else{
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
                                loadingDialog.setTitle("正在加载");
                                loadingDialog.show();
                            }

                            if (!TextUtils.isEmpty(m_nowMac)) {

                                SearchRequest request = new SearchRequest.Builder()      //duration为0时无限扫描
                                        .searchBluetoothLeDevice(0)
                                        .build();

                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                ClientManager.getClient().search(request, mSearchResponse2);

                            }
                        }
                    }


//                    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                        ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                        scrollToFinishActivity();
//                    }
//
//                    if (mBluetoothAdapter == null) {
//                        ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                        scrollToFinishActivity();
//                        return;
//                    }
//                    if (!mBluetoothAdapter.isEnabled()) {
//                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                        startActivityForResult(enableBtIntent, 188);
//                    }else{
//
//                        Log.e("endBtn4===4_1", "==="+m_nowMac);
//
//                        bleService.connect(m_nowMac);
//
//                        cn=0;
//                        checkConnectOpenTemp();
//
//                    }

                }else {
                    flag = 1;

                    if("3".equals(type)){
                        openAgain();
                    }else{
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
                }
                break;
            case R.id.curRoadUI_biking_endBtn:

                isEndBtn = true;

                clickCount++;

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

                                Thread.sleep(1000);
                                n++;

                                Log.e("biking===","biking=n=="+n);


                                if(n>=6) break;

                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        m_myHandler.sendEmptyMessage(3);
                    }
                }).start();


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

                break;
            case R.id.curRoadUI_biking_slideLayout:
                if("4".equals(type)){
                    UIHelper.goWebViewAct(context,"停车须知",Urls.ebike_phtml5 + SharedPreferencesUrls.getInstance().getString("uid",""));
                }else{
                    UIHelper.goWebViewAct(context,"停车须知",Urls.phtml5 + SharedPreferencesUrls.getInstance().getString("uid",""));

                }


                break;
            default:
                break;
        }
    }




    public void openAgain(){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        HttpHelper.post(this, Urls.openAgain, params, new TextHttpResponseHandler() {
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
//                              BaseApplication.getInstance().getIBLE().stopScan();
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

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
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

    public void carClose2(){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        HttpHelper.post(this, Urls.carClose, params, new TextHttpResponseHandler() {
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


    public void carClose(){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        HttpHelper.post(this, Urls.carClose, params, new TextHttpResponseHandler() {
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

                        Log.e("biking===", "carClose===="+result.getData());

                        if ("0".equals(result.getData())){
                            submit(uid, access_token);
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



    protected void submit(String uid,String access_token){

//        if(SharedPreferencesUrls.getInstance().getBoolean("isStop",true)){  //骑行结束才能关蓝牙
//
//        }

        Log.e("submit===", uid+"==="+access_token);

        new Thread(new Runnable() {
            @Override
            public void run() {
                m_myHandler.sendEmptyMessage(5);
            }
        }).start();

        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("oid",oid);
        params.put("latitude",referLatitude);
        params.put("longitude",referLongitude);
        if (macList2.size() > 0){
            params.put("xinbiao",macList2.get(0));
        }
        HttpHelper.post(this, Urls.backBikescan, params, new TextHttpResponseHandler() {
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
                Log.e("Test","结束用车:"+responseString);
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        SharedPreferencesUrls.getInstance().putString("m_nowMac","");
                        SharedPreferencesUrls.getInstance().putString("oid","");
                        SharedPreferencesUrls.getInstance().putString("osn","");
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
                                scrollToFinishActivity();
                            }else {
                                BikeFragment.tz = 2;
                                Intent intent = new Intent(context, HistoryRoadDetailActivity.class);
                                intent.putExtra("oid",oid);
                                startActivity(intent);
                            }
                        }else {
                            BikeFragment.tz = 3;
                            ToastUtil.showMessageApp(context,"恭喜您,还车成功,请支付!");
                            UIHelper.goToAct(context,CurRoadBikedActivity.class);
                        }
//                        finish();
                        scrollToFinishActivity();


                    }else {
                        ToastUtil.showMessageApp(context, result.getMsg());
                    }
                }catch (Exception e){

                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
                if (customDialog3 != null && customDialog3.isShowing()){
                    customDialog3.dismiss();
                }
            }
        });
    }

    public void endBtn5(){
        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context,LoginActivity.class);
        }else {
//            ToastUtil.showMessage(context,macList.size()+"==="+isContainsList.contains(true));
            Log.e("biking===endBtn5",macList.size()+"==="+isContainsList.contains(true)+"==="+type);

            if (macList.size() > 0){
                //蓝牙锁
                flag = 2;
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    ToastUtil.showMessageApp(CurRoadBikingActivity.this, "您的设备不支持蓝牙4.0");
                    scrollToFinishActivity();
                }

//                //蓝牙锁
//                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//                mBluetoothAdapter = bluetoothManager.getAdapter();

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

            if(BikeFragment.screen){
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
                    customDialog3.show();

                    clickCountDeal();
                }
            }

        }
    }

    private void queryOpenState() {
        Log.e("queryOpenState===0", "====");

//        UIHelper.showProgress(this, R.string.collectState);
        ClientManager.getClient().queryOpenState(m_nowMac, new IQueryOpenStateResponse() {
            @Override
            public void onResponseSuccess(boolean open) {
//                UIHelper.dismiss();

                Log.e("queryOpenState===", "===="+open);

                if(open) {
                    ToastUtil.showMessageApp(context,"车锁未关，请手动关锁");

                }else {

                    m_myHandler.sendEmptyMessage(6);
//                    submit(uid, access_token);
                }
            }

            @Override
            public void onResponseFail(int code) {
//                Log.e(TAG, Code.toString(code));
//                UIHelper.dismiss();
                ToastUtil.showMessageApp(context,Code.toString(code));
            }
        });
    }

    public void endBtn(){
        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context,LoginActivity.class);
        }else {
//            ToastUtil.showMessage(context,macList.size()+"==="+isContainsList.contains(true));
            Log.e("biking===endBtn",macList.size()+"==="+isContainsList.contains(true)+"==="+type);

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
//                              BaseApplication.getInstance().getIBLE().stopScan();
                                BaseApplication.getInstance().getIBLE().refreshCache();
                                BaseApplication.getInstance().getIBLE().close();
                                BaseApplication.getInstance().getIBLE().disconnect();

                                if (!BaseApplication.getInstance().getIBLE().getConnectStatus()){
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
                        }
                    }, 10 * 1000);

                    connect();
                }

                return;
            }

            if(BikeFragment.screen){
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
//                                         BaseApplication.getInstance().getIBLE().stopScan();
                                        BaseApplication.getInstance().getIBLE().refreshCache();
                                        BaseApplication.getInstance().getIBLE().close();
                                        BaseApplication.getInstance().getIBLE().disconnect();

                                        if (!BaseApplication.getInstance().getIBLE().getConnectStatus()){
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
                                }
                            }, 10 * 1000);

                            connect();

                        }
                    }
                }else {
                    customDialog3.show();

                    clickCountDeal();
                }
            }

        }
    }

    public void clickCountDeal(){
        if(isClickCount && clickCount>=3){
            Intent intent = new Intent(context,ClientServiceActivity.class);
            intent.putExtra("bikeCode",bikeCode);
            startActivity(intent);
            scrollToFinishActivity();
        }
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

            Log.e("biking===startXB",mBluetoothAdapter+"==="+mLeScanCallback);
            UUID[] uuids = {Config.xinbiaoUUID};
            mBluetoothAdapter.startLeScan(uuids, mLeScanCallback);

            manager.startEddyStoneScan();

        }

    }

    private void stopXB() {
        if (!"1".equals(type)) {
            if (mLeScanCallback != null && mBluetoothAdapter != null) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }

            manager.stopEddyStoneScan();

        }
    }



    @Override
    public void onLocationChanged(final AMapLocation amapLocation) {
//        super.onLocationChanged(amapLocation);

        this.amapLocation = amapLocation;

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
                    String latitude = SharedPreferencesUrls.getInstance().getString("biking_latitude","");
                    String longitude = SharedPreferencesUrls.getInstance().getString("biking_longitude","");

                    if (latitude != null && !"".equals(latitude) && longitude != null && !"".equals(longitude)){

                        ToastUtil.showMessage(context, latitude+"==="+longitude);

                        if (AMapUtils.calculateLineDistance(new LatLng(
                                Double.parseDouble(latitude),Double.parseDouble(longitude)
                        ), new LatLng(amapLocation.getLatitude(),amapLocation.getLongitude())) > 10){
                            SharedPreferencesUrls.getInstance().putString("biking_latitude",""+amapLocation.getLatitude());
                            SharedPreferencesUrls.getInstance().putString("biking_longitude",""+amapLocation.getLongitude());
                            addMaplocation(amapLocation.getLatitude(),amapLocation.getLongitude());
                        }
                    }
                    if (mListener != null) {
                        mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                    }
                    referLatitude = amapLocation.getLatitude();
                    referLongitude = amapLocation.getLongitude();
                    myLocation = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                    if (mFirstFix) {
                        mFirstFix = false;
                        schoolRange();
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));
                    } else {
                        centerMarker.remove();
                        mCircle.remove();
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


    Handler m_myHandler = new Handler(new Handler.Callback() {
    @Override
    public boolean handleMessage(Message mes) {
        switch (mes.what) {
            case 0:
                if (!BaseApplication.getInstance().getIBLE().isEnable()){

                    break;
                }
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

                if(macList.size()>0 || isContainsList.contains(true)){
//                if(!isContainsList.contains(true)){

                    if("4".equals(type)){
                        endBtn4();
                    }else if("5".equals(type)){
                        endBtn5();
                    }else{
                        if(BaseApplication.getInstance().getIBLE().getConnectStatus()){
                            if("3".equals(type)){
                                endBtn3();
                            }else{
                                endBtn();
                            }
                        }else{
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

                                        Log.e("biking===", "3==="+isStop);

                                        if("3".equals(type)){
                                            if(first3){
                                                first3 = false;
                                                customDialog3.show();

                                                clickCountDeal();
                                            }else{
                                                carClose();
                                            }
                                        }else{
                                            customDialog3.show();

                                            clickCountDeal();

                                        }
                                    }
                                }
                            }, 10 * 1000);

                            connect();
                        }
                    }

                }else{
                    customDialog3.show();

                    clickCountDeal();
                }




                break;

            case 4:
                ebikeInfo(uid, access_token);
                break;

            case 5:


                if(BaseApplication.getInstance().getIBLE().isEnable()){
                    BaseApplication.getInstance().getIBLE().refreshCache();
                    BaseApplication.getInstance().getIBLE().close();
                    BaseApplication.getInstance().getIBLE().disconnect();
                    BaseApplication.getInstance().getIBLE().disableBluetooth();
                }
                break;

            case 6:
                submit(uid, access_token);
                break;

            case 7:

                ToastUtil.showMessageApp(context,"关锁成功5");
                SharedPreferencesUrls.getInstance().putString("tempStat","1");
                break;

            case 9:
                break;
            case 0x99://搜索超时
                BaseApplication.getInstance().getIBLE().connect(m_nowMac, CurRoadBikingActivity.this);
                break;
            default:
                break;
        }
        return false;
        }
    });

    void checkConnect(){
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("checkConnect===", "===");

                if(!bleService.connect){
                    cn++;

                    if(cn<5){
                        checkConnect();
                    }else{
                        if (lockLoading != null && lockLoading.isShowing()){
                            lockLoading.dismiss();
                        }

                        customDialog4.show();

                        ccn++;

                        return;
                    }
                }else{
                    if (lockLoading != null && lockLoading.isShowing()){
                        lockLoading.dismiss();
                    }

                    closeEbike();
                }
            }
        }, 2 * 1000);
    }

    public void closeEbike2(){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("oid",oid);
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
                            ToastUtil.showMessageApp(context,"关锁失败");

                            bleService.connect(m_nowMac);
                            cn = 0;

                            closeLock3();
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

    void closeLock3(){
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("closeLock3===", "===");

                if(!bleService.connect){
                    cn++;

                    if(cn<5){
                        closeLock3();
                    }else{
                        customDialog4.show();
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


    public void closeEbike(){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("oid",oid);
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
                            ToastUtil.showMessageApp(context,"关锁失败");

                            bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});

                            m_myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("endBtn4===4_3", "==="+m_nowMac);

                                    button8();
                                    button9();
                                    button2();

                                    cn=0;
                                    closeLock2();
                                }
                            }, 500);

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
                        customDialog5.show();

                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                }

                Log.e("closeLock2===4_6", "==="+bleService.cc);

            }
        }, 500);
    }


    public void endBtn4(){
        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            Log.e("biking===endBtn4",macList.size()+"==="+type+"==="+first3);

            if (macList.size() > 0){
//                flag = 2;

//                if(ccn==0){
//                    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                        ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                        scrollToFinishActivity();
//                    }
//
//                    if (mBluetoothAdapter == null) {
//                        ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                        scrollToFinishActivity();
//                        return;
//                    }
//                    if (!mBluetoothAdapter.isEnabled()) {
//                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                        startActivityForResult(enableBtIntent, 188);
//                    }else{
//                        Log.e("endBtn4===4_1", "==="+m_nowMac);
//
//                        bleService.connect(m_nowMac);
//
//                        cn=0;
//                        checkConnect();
//                    }
//                }else{
//                    closeEbike2();
//                }

                closeEbike2();

                return;
            }

            if(BikeFragment.screen){
                if (isContainsList.contains(true)){

//                    if(ccn==0){
//                        flag = 2;
//                        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                            ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                            scrollToFinishActivity();
//                        }
//
//                        if (mBluetoothAdapter == null) {
//                            ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                            scrollToFinishActivity();
//                            return;
//                        }
//                        if (!mBluetoothAdapter.isEnabled()) {
//                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                            startActivityForResult(enableBtIntent, 188);
//                        }else{
//                            Log.e("endBtn4===4_1", "==="+m_nowMac);
//
//                            bleService.connect(m_nowMac);
//
//                            cn=0;
//                            checkConnect();
//                        }
//                    }else{
//                        closeEbike2();
//                    }

                    closeEbike2();

                }else {
                    customDialog4.show();
                }
            }

        }
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

    public void endBtn3(){
        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            Log.e("biking===endBtn3",macList.size()+"==="+type+"==="+first3);

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

            if(BikeFragment.screen){
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

                }else {
                    customDialog3.show();

                    clickCountDeal();
                }
            }

        }
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
                        UIHelper.goToAct(context,FeedbackActivity.class);
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
                        if (Build.VERSION.SDK_INT >= 23) {
                            int checkPermission = CurRoadBikingActivity.this.checkSelfPermission(Manifest.permission.CALL_PHONE);
                            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                                if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                                    requestPermissions(new String[] { Manifest.permission.CALL_PHONE }, 0);
                                } else {
                                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(CurRoadBikingActivity.this);
                                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开拨打电话权限！")
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            CurRoadBikingActivity.this.requestPermissions(
                                                    new String[] { Manifest.permission.CALL_PHONE }, 0);
                                        }
                                    });
                                    customBuilder.create().show();
                                }
                                return;
                            }
                        }
                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(CurRoadBikingActivity.this);
                        customBuilder.setTitle("温馨提示").setMessage("确认拨打" + "0519-86999222" + "吗?")
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Intent intent=new Intent();
                                intent.setAction(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + "0519-86999222"));
                                startActivity(intent);
                            }
                        });
                        customBuilder.create().show();
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
        MarkerOptions centerMarkerOption = new MarkerOptions().position(myLocation).icon(successDescripter);
        centerMarker = aMap.addMarker(centerMarkerOption);
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

        Log.e("biking===schoolRange0", "==="+type);

        RequestParams params = new RequestParams();
        params.put("type", "4".equals(type)?2:1);

        HttpHelper.get(context, Urls.schoolRange, params, new TextHttpResponseHandler() {
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
                    Log.e("biking===schoolRange1", "==="+type);

                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        JSONArray jsonArray = new JSONArray(result.getData());
                        if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
                            isContainsList.clear();
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            List<LatLng> list = new ArrayList<>();
                            for (int j = 0; j < jsonArray.getJSONArray(i).length(); j ++){
                                JSONObject jsonObject = jsonArray.getJSONArray(i).getJSONObject(j);
                                LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")),
                                        Double.parseDouble(jsonObject.getString("longitude")));
                                list.add(latLng);
                            }
                            Polygon polygon = null;
                            PolygonOptions pOption = new PolygonOptions();
                            pOption.addAll(list);

                            if("4".equals(type)){
                                polygon = aMap.addPolygon(pOption.strokeWidth(2)
                                        .strokeColor(Color.argb(255, 0, 255, 0))
                                        .fillColor(Color.argb(255, 0, 255, 0)));
                            }else{
                                polygon = aMap.addPolygon(pOption.strokeWidth(2)
                                        .strokeColor(Color.argb(160, 255, 0, 0))
                                        .fillColor(Color.argb(160, 255, 0, 0)));
                            }


//                            polygon = aMap.addPolygon(pOption.strokeWidth(2)
//                                    .strokeColor(Color.argb(160, 0, 0, 255))
//                                    .fillColor(Color.argb(160, 0, 0, 255)));



                            pOptions.add(polygon);
                            isContainsList.add(polygon.contains(myLocation));
                        }
                    }else {
                        ToastUtil.showMessageApp(context,result.getMsg());
                    }
                }catch (Exception e){
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });
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
     *
     * 上传骑行坐标
     * */
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
                centerMarker.setIcon(successDescripter);
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

//                    if(n>0){
//                        startXB();
//
//                        if (lockLoading != null && !lockLoading.isShowing()){
//                            lockLoading.setTitle("还车点确认中");
//                            lockLoading.show();
//                        }
//
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    int n=0;
//                                    while(macList.size() == 0){
//                                        Thread.sleep(1000);
//                                        n++;
//
//                                        Log.e("main===", "n====" + n);
//
//                                        if(n>=6) break;
//                                    }
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//
//                                m_myHandler.sendEmptyMessage(3);
//                            }
//                        }).start();
//                    }else{
//                        n++;
//
//                        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//                        mBluetoothAdapter = bluetoothManager.getAdapter();
//
//                        if (mBluetoothAdapter == null) {
//                            ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                            scrollToFinishActivity();
//                            return;
//                        }
//                        if (!mBluetoothAdapter.isEnabled()) {
//                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                            startActivityForResult(enableBtIntent, 188);
//                        }else{
//                            if (lockLoading != null && !lockLoading.isShowing()){
//                                lockLoading.setTitle("正在连接");
//                                lockLoading.show();
//                            }
//
//                            isStop = false;
//                            m_myHandler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (lockLoading != null && lockLoading.isShowing()){
//                                        lockLoading.dismiss();
//                                    }
//
//                                    Log.e("biking===", "3==="+isStop);
//
//                                    if(!isStop){
//                                        stopScan = true;
//                                        BaseApplication.getInstance().getIBLE().refreshCache();
//                                        BaseApplication.getInstance().getIBLE().close();
//                                        BaseApplication.getInstance().getIBLE().disconnect();
//
//                                        if("3".equals(type)){
//                                            if(first3){
//                                                first3 = false;
//                                                customDialog4.show();
//                                            }else{
//                                                carClose();
//                                            }
//                                        }else{
//                                            customDialog3.show();
//                                        }
//                                    }
//                                }
//                            }, 10 * 1000);
//
//                            connect();
//                        }
//                    }

//                    BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//                    mBluetoothAdapter = bluetoothManager.getAdapter();
//
//                    if (mBluetoothAdapter == null) {
//                        ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                        scrollToFinishActivity();
//                        return;
//                    }
//                    if (!mBluetoothAdapter.isEnabled()) {
//                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                        startActivityForResult(enableBtIntent, 188);
//                    }else{
//
//                    }

                    if("4".equals(type)) {
//                        BLEService.bluetoothAdapter = mBluetoothAdapter;
//
//                        bleService.view = context;
//                        bleService.showValue = true;
                    }else if("5".equals(type)){
                        SearchRequest request = new SearchRequest.Builder()      //duration为0时无限扫描
                                .searchBluetoothLeDevice(0)
                                .build();

                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        ClientManager.getClient().search(request, mSearchResponse);

                    }else{
                        if (loadingDialog != null && !loadingDialog.isShowing()) {
                            loadingDialog.setTitle("正在唤醒车锁");
                            loadingDialog.show();
                        }

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
        public void onDeviceFounded(SearchResult device) {

            Log.e("biking=onDeviceFounded",device.device.getName() + "===" + device.device.getAddress()+"==="+m_nowMac);

            if(m_nowMac.equals(device.device.getAddress())){
                ClientManager.getClient().stopSearch();

                connectDevice();

                ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
                ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);
            }

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
        public void onDeviceFounded(SearchResult device) {

            Log.e("biking===","DeviceListActivity.onDeviceFounded2 " + device.device.getAddress()+"==="+m_nowMac);

            if(m_nowMac.equals(device.device.getAddress())){
                ClientManager.getClient().stopSearch();

                ClientManager.getClient().getStatus(m_nowMac, new IGetStatusResponse() {
                    @Override
                    public void onResponseSuccess(String version, String keySerial, String macKey, String vol) {
//                    cn.qimate.bike.util.UIHelper.dismiss();
                        queryStatusServer(version, keySerial, macKey, vol);
                    }

                    @Override
                    public void onResponseFail(int code) {
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }

                        Log.e("getStatus===", Code.toString(code));
                        ToastUtil.showMessageApp(context, Code.toString(code));
                    }

                });



//                connectDevice();

//                ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);
//                ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener2);
//                ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);

                Log.e("biking===","DeviceListActivity.onDeviceFounded2_2 " + device.device.getAddress()+"==="+m_nowMac);
            }

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

    private void initSite(){
        RequestParams params = new RequestParams();
        HttpHelper.get(context, Urls.stopSite, params, new TextHttpResponseHandler() {
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
                        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(20);// 设置缩放监听
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
                if (!PublicWay.BLE_CONNECT_STATUS){
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("正在连接");
                        loadingDialog.show();
                    }

                    if (!BaseApplication.getInstance().getIBLE().getConnectStatus()){
                        connect();
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (lockLoading != null && lockLoading.isShowing()) {
                                lockLoading.dismiss();
                            }
                        }
                    }, 10 * 1000);

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

    /**
     * 链接设备
     * 1. 先进行搜索设备，匹配mac地址是否能搜索到
     * 2. 搜索到就链接设备，取消倒计时；
     * 3. 搜索不到就执行直接连接设备
     */
    @Override
    protected void connect() {

        Log.e("biking====", "connect===="+m_nowMac+"==="+type);

        BaseApplication.getInstance().getIBLE().stopScan();
        m_myHandler.sendEmptyMessage(0x99);
        BaseApplication.getInstance().getIBLE().startScan(new OnDeviceSearchListener() {
            @Override
            public void onScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (device==null || TextUtils.isEmpty(device.getAddress()) || stopScan){
                    stopScan = false;

                    Log.e("biking====", "connect===2==="+stopScan);

                    return;
                }
                if (m_nowMac.equalsIgnoreCase(device.getAddress())){
                    m_myHandler.removeMessages(0x99);
                    BaseApplication.getInstance().getIBLE().stopScan();
                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, CurRoadBikingActivity.this);
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


}
