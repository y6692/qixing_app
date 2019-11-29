package cn.qimate.bike.activity;

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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.sofi.blelocker.library.Code;
import com.sofi.blelocker.library.connect.listener.BleConnectStatusListener;
import com.sofi.blelocker.library.connect.listener.BluetoothStateListener;
import com.sofi.blelocker.library.connect.options.BleConnectOptions;
import com.sofi.blelocker.library.model.BleGattProfile;
import com.sofi.blelocker.library.protocol.IConnectResponse;
import com.sofi.blelocker.library.protocol.IEmptyResponse;
import com.sofi.blelocker.library.protocol.IGetRecordResponse;
import com.sofi.blelocker.library.protocol.IGetStatusResponse;
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
import com.zxing.lib.scaner.activity.MainActivityPermissionsDispatcher;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.http.OkHttpClientManager;
import cn.http.ResultCallback;
import cn.http.rdata.RRent;
import cn.http.rdata.RUserLogin;
import cn.jpush.android.api.JPushInterface;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.base.BaseFragmentActivity;
import cn.qimate.bike.ble.BLEService;
import cn.qimate.bike.core.common.AppManager;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.Md5Helper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Update;
import cn.qimate.bike.core.common.UpdateManager;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.fragment.BikeFragment;
import cn.qimate.bike.fragment.EbikeFragment;
import cn.qimate.bike.fragment.MainFragment;
import cn.qimate.bike.fragment.MineFragment;
import cn.qimate.bike.fragment.PurseFragment;
import cn.qimate.bike.model.CarBean;
import cn.qimate.bike.model.KeyBean;
import cn.qimate.bike.model.OrderBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.TabEntity;
import cn.qimate.bike.model.TabTopEntity;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.ByteUtil;
import cn.qimate.bike.util.Globals;
import cn.qimate.bike.util.HtmlTagHandler;
import cn.qimate.bike.util.IoBuffer;
import cn.qimate.bike.util.ToastUtil;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;
import okhttp3.Request;
import permissions.dispatcher.NeedsPermission;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.sofi.blelocker.library.Constants.STATUS_CONNECTED;

@SuppressLint("NewApi")
public class MainActivity extends BaseFragmentActivity implements View.OnClickListener, OnConnectionListener, BleStateChangeListener, ScanResultCallback {

    private Activity mActivity = this;
    private static final String TAG = MainActivity.class.getSimpleName();

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

    public static final String INTENT_MSG_COUNT = "INTENT_MSG_COUNT";
    public final static String MESSAGE_RECEIVED_ACTION = "io.yunba.example.msg_received_action";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    static private final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private static final int PRIVATE_CODE = 1315;//开启GPS权限

    LocationManager locationManager;
    String provider = LocationManager.GPS_PROVIDER;

    //    @BindView(R.id.fl_change) FrameLayout flChange;
//    @BindView(R.id.tab)
    CommonTabLayout tab;
//    @BindView(R.id.ll_tab) LinearLayout llTab;

    private Context mContext;

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private String[] mTitles = { "首页", "钱包", "会员中心" };
    private int[] mIconUnselectIds = {
            R.drawable.bike, R.drawable.purse, R.drawable.mine
    };
    private int[] mIconSelectIds = {
            R.drawable.bike2, R.drawable.purse2, R.drawable.mine2
    };
    private MainFragment mainFragment;
    private PurseFragment purseFragment;
    private MineFragment mineFragment;

//    public AMap aMap;
//    public BitmapDescriptor successDescripter;
//    public MapView mapView;


    private Dialog dialog;
    private View dialogView;

    private ImageView titleImage;
    private ImageView exImage_1;
    private ImageView exImage_2;
    private ImageView exImage_3;

    private String imageUrl;
    private String ad_link;
    private String app_type;
    private String app_id;


    private ImageView closeBtn;
    private Dialog advDialog;
    private ImageView advImageView;
    private ImageView advCloseBtn;



    PopupWindow popupwindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);
//        ButterKnife.bind(this);

        CrashHandler.getInstance().setmContext(this);

        type = SharedPreferencesUrls.getInstance().getString("type", "");

        registerReceiver(broadcastReceiver, Config.initFilter());
        GlobalParameterUtils.getInstance().setLockType(LockType.MTS);

//        IntentFilter filter = new IntentFilter("data.broadcast.action");
//        registerReceiver(mReceiver, filter);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mScreenReceiver, filter);

        m_myHandler.sendEmptyMessage(4);

        Log.e("main===onCreate", "===");

        initData();
        initView();
//        initListener();
//        initLocation();
//        AppApplication.getApp().scan();

    }

    private void initData() {
        mContext = this;

        mainFragment = new MainFragment();
        purseFragment = new PurseFragment();
        mineFragment = new MineFragment();
        mFragments.add(mainFragment);
        mFragments.add(purseFragment);
        mFragments.add(mineFragment);


        Log.e("main===initData", "===");

        for (int i = 0; i < mTitles.length; i++) {
//            mTabEntities.add(new TabTopEntity(mTitles[i]));
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }


    }

    private boolean checkGPSIsOpen() {
        boolean isOpen;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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
                            finish();
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

    private void initView() {
        openGPSSettings();

        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE_ASK_PERMISSIONS);
                } else {
                    requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE_ASK_PERMISSIONS);
                }
                return;
            }
        }

        tab = findViewById(R.id.tab);

        tab.setTabData(mTabEntities, MainActivity.this, R.id.fl_change, mFragments);
        tab.setCurrentTab(0);


        loadingDialog = new LoadingDialog(mActivity);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);


        dialog = new Dialog(context, R.style.Theme_AppCompat_Dialog);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.ui_frist_view, null);
        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);

        advDialog = new Dialog(context, R.style.Theme_AppCompat_Dialog);
        View advDialogView = LayoutInflater.from(context).inflate(R.layout.ui_adv_view, null);
        advDialog.setContentView(advDialogView);
        advDialog.setCanceledOnTouchOutside(false);



//        titleImage = (ImageView)dialogView.findViewById(R.id.ui_fristView_title);
        exImage_1 = (ImageView)dialogView.findViewById(R.id.ui_fristView_exImage_1);
//        exImage_2 = (ImageView)dialogView.findViewById(R.id.ui_fristView_exImage_2);
//        exImage_3 = (ImageView)dialogView.findViewById(R.id.ui_fristView_exImage_3);
        closeBtn = (ImageView)dialogView.findViewById(R.id.ui_fristView_closeBtn);

        advImageView = (ImageView)advDialogView.findViewById(R.id.ui_adv_image);
        advCloseBtn = (ImageView)advDialogView.findViewById(R.id.ui_adv_closeBtn);



//        LinearLayout.LayoutParams params4 = (LinearLayout.LayoutParams) advImageView.getLayoutParams();
//        params4.height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.8);
//        advImageView.setLayoutParams(params4);

//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) titleImage.getLayoutParams();
//        params.height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.16);
//        titleImage.setLayoutParams(params);

        if (SharedPreferencesUrls.getInstance().getBoolean("ISFRIST",true)){
            SharedPreferencesUrls.getInstance().putBoolean("ISFRIST",false);
            WindowManager windowManager = getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.width = (int) (display.getWidth() * 0.8); // 设置宽度0.6
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
            dialog.getWindow().setAttributes(lp);
            dialog.show();
        }
        else {
//            initHttp();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    m_myHandler.sendEmptyMessage(5);
                }
            }).start();
        }
//        exImage_1.setOnClickListener(myOnClickLister);
//        exImage_2.setOnClickListener(myOnClickLister);



        advImageView.setOnClickListener(this);
        advCloseBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(myOnClickLister);

    }

    BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
        private String action = null;

        @Override
        public void onReceive(final Context context, final Intent intent) {


            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    action = intent.getAction();

                    Log.e("mScreenReceiver===mf", "===Screen");

//			if (!screen) return;

                    if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
//                        screen = false;
//                        change = false;


                        ToastUtil.showMessage(context, "===off");
                        Log.e("main===off", "===");

                    } else if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏

                        ToastUtil.showMessage(context, "===on");
                        Log.e("main===on", "===");

                    } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁

//                        ToastUtil.showMessage(context, "===present");
//                        Log.e("main===", tz + ">>>present===" + m_nowMac);
//
//                        if (tz == 0) {
//                            if (!"".equals(m_nowMac) && !SharedPreferencesUrls.getInstance().getBoolean("switcher",false)) {
//
//                                if (CurRoadBikingActivity.flagm == 1) return;
//
//                                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                                    activity.finish();
//                                }
//                                //蓝牙锁
//                                if (mBluetoothAdapter == null) {
//                                    BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
//                                    mBluetoothAdapter = bluetoothManager.getAdapter();
//                                }
//
//                                Log.e("main===", "present===1");
//
//                                if (mBluetoothAdapter == null) {
//                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                                    activity.finish();
//                                    return;
//                                }
//
//                                Log.e("main===", "present===2==="+CurRoadBikingActivity.flagm);
//
//                                if (!mBluetoothAdapter.isEnabled()) {
//                                    Log.e("main===", "present===3==="+CurRoadBikingActivity.flagm);
//
//                                    flag = 1;
//                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                                    startActivityForResult(enableBtIntent, 188);
//                                }else{
//
//                                }
//                            }
//                        } else if (tz == 1 && !FeedbackActivity.isForeground) {
//                            UIHelper.goToAct(context, FeedbackActivity.class);
//                            Log.e("main===", "main===Feedback");
//                        } else if (tz == 2 && !HistoryRoadDetailActivity.isForeground) {
//                            Intent intent = new Intent(context, HistoryRoadDetailActivity.class);
//                            intent.putExtra("oid", oid);
//                            startActivity(intent);
//                            Log.e("main===", "main===HistoryRoadDetail");
//                        } else if (tz == 3 && !CurRoadBikedActivity.isForeground && !HistoryRoadDetailActivity.isForeground) {
//                            UIHelper.goToAct(context, CurRoadBikedActivity.class);
//                            Log.e("main===", "main===CurRoadBiked");
//                        }
                    }
                }
            });

        }
    };




    /**
     * 获取广告
     * */
    private void initHttp(){
        RequestParams params = new RequestParams();
        params.put("adsid","11");

        Log.e("initHttp===main", SharedPreferencesUrls.getInstance().getString("uid","")+"==="+SharedPreferencesUrls.getInstance().getString("access_token",""));

        if (SharedPreferencesUrls.getInstance().getString("uid","") != null && !"".equals(SharedPreferencesUrls.getInstance().getString("uid",""))){
            params.put("uid",SharedPreferencesUrls.getInstance().getString("uid",""));
        }
        if (SharedPreferencesUrls.getInstance().getString("access_token","") != null && !"".equals(SharedPreferencesUrls.getInstance().getString("access_token",""))){
            params.put("access_token",SharedPreferencesUrls.getInstance().getString("access_token",""));
        }
        HttpHelper.get(context, Urls.getIndexAd, params, new TextHttpResponseHandler() {
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
                                JSONArray jsonArray = new JSONArray(result.getData());
                                for (int i = 0; i < jsonArray.length();i++){
                                    imageUrl = jsonArray.getJSONObject(i).getString("ad_file");
                                    ad_link = jsonArray.getJSONObject(i).getString("ad_link");
                                    app_type = jsonArray.getJSONObject(i).getString("app_type");
                                    app_id = jsonArray.getJSONObject(i).getString("app_id");

                                }

//                              m_myHandler.sendEmptyMessage(5);

                                Log.e("initHttp===", "==="+imageUrl);

                                if (!SharedPreferencesUrls.getInstance().getBoolean("ISFRIST",false)){
                                    if (imageUrl != null && !"".equals(imageUrl)){
                                        WindowManager windowManager = getWindowManager();
                                        Display display = windowManager.getDefaultDisplay();

                                        Log.e("display===", "==="+display.getWidth());

                                        WindowManager.LayoutParams lp = advDialog.getWindow().getAttributes();
                                        lp.width = (int) (display.getWidth() * 1);
                                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                        advDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                                        advDialog.getWindow().setAttributes(lp);
                                        advDialog.show();
                                        // 加载图片
                                        if(imageUrl.endsWith(".gif")){
                                            Glide.with(context).load(imageUrl).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(advImageView);
                                        }else{
                                            Glide.with(context).load(imageUrl).into(advImageView);
                                        }
                                    }
                                }
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


    @Override
    public void onPause() {
        super.onPause();
        JPushInterface.onPause(context);
    }


    @Override
    public void onStop() {
        super.onStop();

        Log.e("main===", "===onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("main===", "===onDestroy");

//        if (mapView != null) {
//            mapView.onDestroy();
//        }
    }


    //获取网络时间
    private void getNetTime() {

        Log.e("getNetTime==", "===");

        URL url = null;//取得资源对象
        final DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        try {
//			url = new URL("http://www.baidu.com");
            url = new URL("http://www.ntsc.ac.cn");//中国科学院国家授时中心
            //url = new URL("http://www.bjtime.cn");
            URLConnection uc = url.openConnection();//生成连接对象
            uc.connect(); //发出连接

            Log.e("getNetTime==>>>", "===");

            long ld = uc.getDate(); //取得网站日期时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ld);
            final String format = formatter.format(calendar.getTime());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Log.e("date===", "==="+SharedPreferencesUrls.getInstance().getString("date",""));

                    if (SharedPreferencesUrls.getInstance().getString("date","") != null &&
                            !"".equals(SharedPreferencesUrls.getInstance().getString("date",""))){
                        if (!format.equals(SharedPreferencesUrls.getInstance().getString("date",""))){
                            UpdateManager.getUpdateManager().checkAppUpdate(MainActivity.this, context, true);
                            SharedPreferencesUrls.getInstance().putString("date",""+format);
                        }
                    }else {
                        // 版本更新
                        UpdateManager.getUpdateManager().checkAppUpdate(MainActivity.this, context, true);
                        SharedPreferencesUrls.getInstance().putString("date",""+format);
                    }
                }
            });
        } catch (Exception e) {
            Log.e("getNetTime==1", "==="+SharedPreferencesUrls.getInstance().getString("date",""));

            String date = formatter.format(new Date());

            Log.e("getNetTime==1_2", "==="+SharedPreferencesUrls.getInstance().getString("date",""));

            if (SharedPreferencesUrls.getInstance().getString("date","") != null && !"".equals(SharedPreferencesUrls.getInstance().getString("date",""))){
                Log.e("getNetTime==2", "==="+date);

                if (!date.equals(SharedPreferencesUrls.getInstance().getString("date",""))){
                    Log.e("getNetTime==2_2", "===");

                    UpdateManager.getUpdateManager().checkAppUpdate(MainActivity.this, context, true);
                    SharedPreferencesUrls.getInstance().putString("date",""+date);
                }
            }else {
                Log.e("getNetTime==23", "===");
                // 版本更新
                UpdateManager.getUpdateManager().checkAppUpdate(MainActivity.this, context, true);
                SharedPreferencesUrls.getInstance().putString("date",""+date);
            }
            e.printStackTrace();
        }
    }

    @Override protected void onResume() {


        super.onResume();

        JPushInterface.onResume(context);

//        mapView.onResume();

        Log.e("main===onResume", "==="+type);



//        ClientManager.getClient().registerBluetoothStateListener(mBluetoothStateListener);


//        aMap = mapView.getMap();
//
//        aMap.clear();

        Log.e("main===onResume2", "===");

    }

    private BluetoothStateListener mBluetoothStateListener = new BluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {
            if (openOrClosed) {
//                searchDevice();

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    SearchRequest request = new SearchRequest.Builder()      //duration为0时无限扫描
//                    .searchBluetoothClassicDevice(3000, 5)
                            .searchBluetoothLeDevice(0)
                            .build();

//                    ClientManager.getClient().search(request, mSearchResponse);
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 188);
                }
            }
            else {
//                UIHelper.showAlertDialog(DeviceListActivity.this, R.string.ble_disable);
                Log.e("mBluetoothStateL=", "===");
            }
        }
    };



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);

        Log.e("main===onSIS", "==="+type);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();

//            Log.e("main===mReceiver", "==="+action);

            if ("data.broadcast.action".equals(action)) {
                int count = intent.getIntExtra("count", 0);
                if (count > 0) {
                    tab.showMsg(1, count);
                    tab.setMsgMargin(1, -8, 5);
                } else {
                    tab.hideMsg(0);
                }
            }
        }
    };





    public void changeTab(int index) {


//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.commitAllowingStateLoss();
        tab.setCurrentTab(index);



//        if (dialogTag == null) {
//            dialogParent.setCancelable(false);
//            transaction.add(dialogParent, "dialog_event");
//            transaction.commitAllowingStateLoss();
//        }
//        transaction.show(ebikeFragment);

    }




    @Override
    public void onClick(View view) {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (view.getId()){
//            case R.id.mainUI_leftBtn:
//                UIHelper.goToAct(context, ActionCenterActivity.class);
//
////                UIHelper.goToAct(context, RealNameAuthActivity.class);
//
//                break;
//            case R.id.mainUI_rightBtn:
//                if (SharedPreferencesUrls.getInstance().getString("uid","") == null || "".equals(
//                        SharedPreferencesUrls.getInstance().getString("access_token",""))){
//                    UIHelper.goToAct(context, LoginActivity.class);
//                    ToastUtil.showMessageApp(context,"请先登录你的账号");
//                    return;
//                }
//                UIHelper.goToAct(context, PersonAlterActivity.class);
//                break;
            case R.id.ui_adv_closeBtn:
                if (advDialog != null && advDialog.isShowing()) {
                    advDialog.dismiss();
                }
                break;
            case R.id.ui_adv_image:

                Log.e("main===", "ui_adv==="+app_type+"==="+app_id+"==="+ad_link);

                UIHelper.bannerGoAct(context,app_type,app_id,ad_link);
                break;

            case R.id.ll_change_car:
            case R.id.iv_rent_cancelBtn:
                Log.e("ll_rent_cancelB=onClick", uid+"==="+access_token+"==="+SharedPreferencesUrls.getInstance().getString("iscert",""));

                popupwindow.dismiss();

                break;

            case R.id.ll_rent:
                Log.e("ll_rent===onClick", uid+"==="+access_token+"==="+SharedPreferencesUrls.getInstance().getString("iscert",""));

                order(codenum);

                break;

            default:
                break;
        }
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

                            Log.e("order_authority===2",  type+"====" + jsonObject.getString("order_sn"));

                            oid = jsonObject.getString("order_sn");

//                            if("0".equals(jsonObject.getString("code"))){
//                                car(tokencode);
//                            }

                            if ("1".equals(type)) {          //单车机械锁
                                UIHelper.goToAct(context, CurRoadStartActivity.class);
                                popupwindow.dismiss();
//                                scrollToFinishActivity();
                            } else if ("2".equals(type)) {    //单车蓝牙锁

                                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                    popupwindow.dismiss();
//                                    scrollToFinishActivity();
                                }
                                //蓝牙锁
                                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

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
                                        connect();
                                    }
                                }
                            }else if ("3".equals(type)) {    //单车3合1锁

                                unlock();

//                                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                                    popupwindow.dismiss();
////                                    scrollToFinishActivity();
//                                }
//                                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//                                mBluetoothAdapter = bluetoothManager.getAdapter();
//
//                                if (mBluetoothAdapter == null) {
//                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                                    popupwindow.dismiss();
////                                    scrollToFinishActivity();
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

                            }else if ("4".equals(type)) {

//                                unlock();

                                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                    popupwindow.dismiss();
//                                    scrollToFinishActivity();
                                }
                                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
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

                                    Log.e("ma===4_1", bleid + "==="+m_nowMac);

                                    bleService.connect(m_nowMac);

                                    checkConnect();

                                }

                            }else if ("5".equals(type) || "6".equals(type)) {      //泺平单车蓝牙锁

                                Log.e("ma===5_1", deviceuuid + "==="+m_nowMac);

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
                                    popupwindow.dismiss();
//                                    scrollToFinishActivity();
                                }
                                //蓝牙锁
                                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

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
                                Log.e("ma===7_1", deviceuuid + "==="+m_nowMac);

//                                unlock();

                                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                                    popupwindow.dismiss();
//                                    scrollToFinishActivity();
                                }
                                //蓝牙锁
                                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

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
                                    builder.setBleStateChangeListener(MainActivity.this);
                                    builder.setScanResultCallback(MainActivity.this);
                                    apiClient = builder.build();

                                    MainActivityPermissionsDispatcher.connectDeviceWithPermissionCheck(MainActivity.this, deviceuuid);

                                    m_myHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!isConnect){
                                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                                    loadingDialog.dismiss();
                                                }

//                                          closeEbike();

                                                if(!isFinishing()){
//                                                tzEnd();
                                                    car_notification(1, 2, 2, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
                                                    popupwindow.dismiss();
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

    private void cycling() {
        Log.e("ma===cycling", "===");

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

                            Log.e("ma===cycling1", responseString + "===" + result.data);

                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);

                            if(null != bean.getOrder_sn()){


                                Log.e("ma===cycling2", bean.getOrder_sn()+"===" + bean.getCar_number()+"===" + bean.getLock_id());

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
        Log.e("ma===lock", "===");

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

                            Log.e("ma===lock1", responseString + "===" + result.data);

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
        Log.e("ma===unlock", "===");

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

                            Log.e("ma===unlock1", responseString + "===" + result.data);

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
        Log.e("ma===carLoop", "===" + codenum);

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

                            Log.e("ma===carLoop1", responseString + "===" + result.data);

                            CarBean bean = JSON.parseObject(result.getData(), CarBean.class);

                            Log.e("ma===carLoop2", bean.getNumber()+"===" + bean.getLock_status());

                            if(2 != bean.getLock_status()){
                                queryCarStatus();
                            }else{
                                car_notification(1, 1, 0, 2,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

//                                car_notification(1, 2, 4, 2,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

                                popupwindow.dismiss();
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
                    Log.e("ma===queryCarStatus", "===");

                    carLoop();

                }
            }, 1 * 1000);
        }else{
            ToastUtil.showMessageApp(context, "开锁失败");

//            car_notification();

            car_notification(1, 2, 4, 2,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

            popupwindow.dismiss();
        }

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

//                            iv_help.setVisibility(View.GONE);
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

    private void car_notification(int scene, int lock_status, int failure_code, int report_type, int back_type, String failure_desc, String parking, String longitude, String latitude) {
        Log.e("ma===car_notification", lock_status+"==="+ oid+"==="+ Md5Helper.encode(oid+":lock_status:"+lock_status));

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

                            Log.e("ma===car_notification1", responseString + "====" + result.data);

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
//                      deleteBleRecord(null);

                        if("锁已开".equals(Code.toString(code))){
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

                            isFinish = true;

                            ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

                            if(!isFinishing()){
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
                            if(!isFinishing()){
//                                    tzEnd();

                                car_notification(1, 2, 2, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
                            }
                        }

                        popupwindow.dismiss();

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

                                    popupwindow.dismiss();

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
        Log.e("ma===Xiaoan", "===Connect");

        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.e("ma===", "scan====1");

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

                                    if(!isFinishing()){
//                                        tzEnd();
                                        car_notification(1, 1, 0, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
                                    }
                                }else{
//                                    ToastUtil.showMessageApp(context,"开锁失败");

//                                                            submit(uid, access_token);
//                                    closeEbike();
                                    if(!isFinishing()){
//                                        tzEnd();
                                        car_notification(1, 2, 1, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
                                    }
                                }

                                popupwindow.dismiss();

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
        Log.e("ma===Xiaoan", "===DisConnect");

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
                    if(!isFinishing()){
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try{
                CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                customBuilder.setTitle("温馨提示").setMessage("确认退出吗?")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        AppManager.getAppManager().AppExit(context);
                    }
                });
                customBuilder.create().show();
                return true;
            }catch (Exception e){

            }
        }
        return super.onKeyDown(keyCode, event);
    }

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
        Bitmap bitmap = UtilScreenCapture.getDrawing(this);
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

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        m_myHandler.post(new Runnable() {
            @Override
            public void run() {

                Log.e("ma===requestCode", requestCode+"==="+resultCode);

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
                                builder.setBleStateChangeListener(MainActivity.this);
                                builder.setScanResultCallback(MainActivity.this);
                                apiClient = builder.build();

                                MainActivityPermissionsDispatcher.connectDeviceWithPermissionCheck(MainActivity.this, deviceuuid);

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

                    default:

                        break;

                }

                /*
                if (resultCode == RESULT_OK) {
                    switch (requestCode) {
                        case 1:
                            String price = data.getStringExtra("price");

                            Log.e("ma===requestCode1", "==="+price);

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
                                    localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                                    startActivity(localIntent);
                                    finish();
                                }
                            });
                            customBuilder.create().show();
                        }
                        break;
                    case 100:
//                        if (loadingDialog != null && loadingDialog.isShowing()){
//                            loadingDialog.dismiss();
//                        }
//                        if (customDialog2 != null && customDialog2.isShowing()){
//                            customDialog2.dismiss();
//                        }
//
//                        if (grantResults[0] == PERMISSION_GRANTED) {
//                            // Permission Granted
//                            if (permissions[0].equals(Manifest.permission.CAMERA)){
//                                try {
//                                    closeBroadcast();
//                                    deactivate();
//
//                                    Intent intent = new Intent();
//                                    intent.setClass(context, ActivityScanerCode.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
//
//                                } catch (Exception e) {
//                                    UIHelper.showToastMsg(context, "相机打开失败,请检查相机是否可正常使用", R.drawable.ic_error);
//                                }
//                            }
//                        }else {
//                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//                            customBuilder.setTitle("温馨提示").setMessage("您需要在设置里允许获取相机权限！")
//                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.cancel();
//                                        }
//                                    }).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                    Intent localIntent = new Intent();
//                                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//                                    localIntent.setData(Uri.fromParts("package", getPackageName(), null));
//                                    startActivity(localIntent);
//                                    finish();
//                                }
//                            });
//                            customBuilder.create().show();
//                        }
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
                                            finish();
                                        }
                                    }).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Intent localIntent = new Intent();
                                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                                    startActivity(localIntent);
                                    finish();
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
        Log.e("connect===", m_nowMac+"==="+Build.VERSION.SDK_INT);

        BaseApplication.getInstance().getIBLE().stopScan();
        m_myHandler.sendEmptyMessage(0x99);
        BaseApplication.getInstance().getIBLE().startScan(new OnDeviceSearchListener() {
            @Override
            public void onScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {

                Log.e("connect===", isStop+"==="+m_nowMac+"==="+device.getName()+"==="+device.getAddress());

                if(isStop){
                    m_myHandler.removeMessages(0x99);
                    BaseApplication.getInstance().getIBLE().stopScan();
                    return;
                }

                if (device==null||TextUtils.isEmpty(device.getAddress()))return;
                if (m_nowMac.equalsIgnoreCase(device.getAddress())){
                    Log.e("connect===2", m_nowMac+"==="+device.getName()+"==="+device.getAddress());

                    m_myHandler.removeMessages(0x99);
                    BaseApplication.getInstance().getIBLE().stopScan();
                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, MainActivity.this);
                }
            }
        });
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
//                            if (!TextUtils.isEmpty(data)) {
//                                quantity = String.valueOf(Integer.parseInt(data, 16));
//                            }else {
//                                quantity = "";
//                            }
                            break;
                        case Config.OPEN_ACTION:
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

                            if (TextUtils.isEmpty(data)) {
                                ToastUtil.showMessageApp(context,"开锁失败,请重试");

//                                submit(uid, access_token);

//                                scrollToFinishActivity();
                            } else {
                                isOpen = true;

                                ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");
                                Log.e("scan===", "OPEN_ACTION===="+isOpen+"==="+isFinishing());

                                isFinish = true;



                                if(!isFinishing()){
//                                    tzEnd();

                                    car_notification(1, 1, 0, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));
                                }


//                                else{
//                                    addOrderbluelock2();
//                                }

                            }

                            popupwindow.dismiss();

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

    protected Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {

                case 4:
                    getNetTime();

                    break;

                case 5:
                    initHttp();

                    break;

                case 0:
                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, MainActivity.this);
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
                    break;
                case 9:
                    break;

                case 0x98://搜索超时
//                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, ActivityScanerCode.this);

//                    connectDeviceLP();
//                    ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
//
//                    Log.e("0x98===", "==="+isStop);
//
//                    m_myHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (!isStop){
//                                if (loadingDialog != null && loadingDialog.isShowing()) {
//                                    loadingDialog.dismiss();
//                                }
//
////                                memberEvent2();
//
////                                Toast.makeText(context,"请重启软件，开启定位服务,输编号用车",5 * 1000).show();
//                                Toast.makeText(context,"扫码唤醒失败，重启手机蓝牙换辆车试试吧！",Toast.LENGTH_LONG).show();
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
////                                scrollToFinishActivity();
//                            }
//                        }
//                    }, 15 * 1000);
                    break;

                case 0x99://搜索超时
//                    BaseApplication.getInstance().getIBLE().resetLock();
//                    BaseApplication.getInstance().getIBLE().resetBluetoothAdapter();

//                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

//                    mBluetoothAdapter..disable();


                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, MainActivity.this);
//                    resetLock();
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
                                BaseApplication.getInstance().getIBLE().refreshCache();
                                BaseApplication.getInstance().getIBLE().close();
                                BaseApplication.getInstance().getIBLE().disconnect();
//                                scrollToFinishActivity();

                                car_notification(1, 2, 2, 1,0,"", "", SharedPreferencesUrls.getInstance().getString("longitude", ""), SharedPreferencesUrls.getInstance().getString("latitude", ""));

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