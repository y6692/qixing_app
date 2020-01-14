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
import android.view.inputmethod.InputMethodManager;
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
import cn.qimate.bike.model.BannerBean;
import cn.qimate.bike.model.CarBean;
import cn.qimate.bike.model.KeyBean;
import cn.qimate.bike.model.OrderBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.TabEntity;
import cn.qimate.bike.model.TabTopEntity;
import cn.qimate.bike.model.UserBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.ByteUtil;
import cn.qimate.bike.util.Globals;
import cn.qimate.bike.util.HtmlTagHandler;
import cn.qimate.bike.util.IoBuffer;
import cn.qimate.bike.util.ToastUtil;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;
import cn.qimate.bike.view.RoundImageView;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.sofi.blelocker.library.Constants.STATUS_CONNECTED;

@SuppressLint("NewApi")
public class MainActivity extends BaseFragmentActivity implements View.OnClickListener, OnBannerListener {

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


    CommonTabLayout tab;

    private Context mContext;

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private String[] mTitles = { "首页", "钱包", "会员中心" };
    private int[] mIconUnselectIds = {
            R.mipmap.bike, R.mipmap.purse, R.mipmap.mine
    };
    private int[] mIconSelectIds = {
            R.mipmap.bike2, R.mipmap.purse2, R.mipmap.mine2
    };
    private MainFragment mainFragment;
    private PurseFragment purseFragment;
    private MineFragment mineFragment;


    private Dialog dialog;
    private View dialogView;

    private ImageView titleImage;
    private ImageView exImage_1;
    private ImageView exImage_2;
    private ImageView exImage_3;

    private String imageUrl;
    private String h5_title;
    private String ad_link;
    private String app_type;
    private String app_id;
    private String action_content;
    private String action_type;

    private ImageView closeBtn;
    private Dialog advDialog;
    private ImageView advImageView;
    private ImageView advCloseBtn;

    private LinearLayout rl_ad;

    private Banner mBanner;
    private MyImageLoader mMyImageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);
//        ButterKnife.bind(this);

        CrashHandler.getInstance().setmContext(this);

        type = SharedPreferencesUrls.getInstance().getString("type", "");

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mScreenReceiver, filter);

        m_myHandler.sendEmptyMessage(4);

        Log.e("main===onCreate", "===");

        initData();
        initView();
    }

    private void initData() {
        mContext = this;

        mainFragment = new MainFragment();
        purseFragment = new PurseFragment();
        mineFragment = new MineFragment();
        mFragments.add(mainFragment);
        mFragments.add(purseFragment);
        mFragments.add(mineFragment);

        Log.e("ma===initData", "===");

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
    }

    private void initView() {
        tab = findViewById(R.id.tab);

        tab.setTabData(mTabEntities, MainActivity.this, R.id.fl_change, mFragments);
        tab.setCurrentTab(0);

        loadingDialog = new LoadingDialog(this);
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


//        imagePath = new ArrayList<>();
//        imageTitle = new ArrayList<>();
//
//        mMyImageLoader = new MyImageLoader();
//        mBanner = findViewById(R.id.purse_banner);
//        //设置样式，里面有很多种样式可以自己都看看效果
//        mBanner.setBannerStyle(0);
//        //设置图片加载器
//        mBanner.setImageLoader(mMyImageLoader);
//        //设置轮播的动画效果,里面有很多种特效,可以都看看效果。
//        mBanner.setBannerAnimation(Transformer.ZoomOutSlide);
//        //轮播图片的文字
////      mBanner.setBannerTitles(imageTitle);
//        //设置轮播间隔时间
//        mBanner.setDelayTime(3000);
//        //设置是否为自动轮播，默认是true
//        mBanner.isAutoPlay(true);
//        //设置指示器的位置，小点点，居中显示
//        mBanner.setIndicatorGravity(BannerConfig.CENTER);
//
//        rl_ad = findViewById(R.id.rl_purse_ad);
//        rl_ad.setOnClickListener(this);

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
        } else {
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//      must store the new intent unless getIntent() will return the old one
        setIntent(intent);

        Log.e("ma===onNewIntent", SharedPreferencesUrls.getInstance().getString("access_token", "") + "===" + type);

    }

    @Override
    public void onResume() {
        super.onResume();

//        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(context.getWindowToken(), 0); // 隐藏

        JPushInterface.onResume(context);

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");

        boolean flag = getIntent().getBooleanExtra("flag", false);

        Log.e("ma===onResume",  flag + "===" + access_token + "===" + type);

        if("".equals(access_token)){
            tab.setCurrentTab(0);
        }

        if(flag){
            purseFragment.user();
            mineFragment.initHttp();
        }

//        mainFragment.show
//        tab.setCurrentTab(0);
    }



    @Override
    public void OnBannerClick(int position) {
//        Toast.makeText(context, "你点了第" + (position + 1) + "张轮播图", Toast.LENGTH_SHORT).show();

        Log.e("OnBannerClick===", h5_title+"==="+action_content);

        UIHelper.goWebViewAct(context, h5_title, action_content);

//        initmPopupWindowView();
    }

    private class MyImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context.getApplicationContext()).load(path).into(imageView);
        }

        @Override
        public ImageView createImageView(Context context) {
            RoundImageView img = new RoundImageView(context);
            return img;
        }
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


    private void initHttp() {
        Log.e("ma===banner", "===");

        HttpHelper.get2(context, Urls.banner + 2, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("ma===banner=fail", "===" + throwable.toString());
                onFailureCommon("ma===banner", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e("ma===banner0", responseString + "===");

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            JSONArray ja_banners = new JSONArray(new JSONObject(result.getData()).getString("banners"));

                            Log.e("ma===banner1", ja_banners.length() + "===" + result.data);

                            for (int i = 0; i < 1; i++) {
                                BannerBean bean = JSON.parseObject(ja_banners.get(i).toString(), BannerBean.class);

                                Log.e("ma===banner2", bean.getImage_url()+"===");

                                imageUrl = bean.getImage_url();
                                h5_title = bean.getH5_title();


                                action_content = bean.getAction_content();
                                action_type = bean.getAction_type();
                                if("h5".equals(action_type)){
                                    if(action_content.contains("?")){
                                        action_content+="&token="+access_token.split(" ")[1];
                                    }else{
                                        action_content+="?token="+access_token.split(" ")[1];
                                    }
                                }


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

//                            mBanner.setBannerTitles(imageTitle);
//                            mBanner.setImages(imagePath).setOnBannerListener(MainActivity.this).start();

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

    /**
     * 获取广告
     * */
    private void initHttp2(){
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
                onFailureCommon("ma===getIndexAd", throwable.toString());
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

        unregisterReceiver(mScreenReceiver);
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
                            UpdateManager.getUpdateManager().checkAppUpdate(MainActivity.this, context, 2, null);
                            SharedPreferencesUrls.getInstance().putString("date",""+format);
                        }
                    }else {
                        // 版本更新
                        UpdateManager.getUpdateManager().checkAppUpdate(MainActivity.this, context, 2, null);
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

                    UpdateManager.getUpdateManager().checkAppUpdate(MainActivity.this, context, 2, null);
                    SharedPreferencesUrls.getInstance().putString("date",""+date);
                }
            }else {
                Log.e("getNetTime==23", "===");
                // 版本更新
                UpdateManager.getUpdateManager().checkAppUpdate(MainActivity.this, context, 2, null);
                SharedPreferencesUrls.getInstance().putString("date",""+date);
            }
            e.printStackTrace();
        }
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

                Log.e("OnClick===", h5_title+"==="+action_content+"==="+action_type);

                if(!"".equals(action_type)){
                    if (advDialog != null && advDialog.isShowing()) {
                        advDialog.dismiss();
                    }

                    bannerTz(h5_title, action_type, action_content);
                }


                break;

            default:
                break;
        }
    }

    private void bannerTz(String title, String type, String url) {
        if("app".equals(type)){
            if("home".equals(url)){
                changeTab(0);
            }else if("wallet".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    changeTab(1);
                }
            }else if("member".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    changeTab(2);
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
        Log.e("pf===user", "===");

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

                            Log.e("pf===user1", responseString + "===" + result.data);

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
                customBuilder.setType(3).setTitle("温馨提示").setMessage("您将退出7MA出行。")
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

                    default:
                        onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        });


    }



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

                default:
                    break;
            }
            return false;
        }
    });

}