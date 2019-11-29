package cn.qimate.bike.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.sofi.blelocker.library.connect.listener.BluetoothStateListener;
import com.sofi.blelocker.library.search.SearchRequest;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.apache.http.Header;
import org.json.JSONArray;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.http.OkHttpClientManager;
import cn.http.ResultCallback;
import cn.http.rdata.RUserLogin;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.base.BaseFragmentActivity;
import cn.qimate.bike.core.common.AppManager;
import cn.qimate.bike.core.common.HttpHelper;
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
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.TabEntity;
import cn.qimate.bike.model.TabTopEntity;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.Globals;
import cn.qimate.bike.util.ToastUtil;
import okhttp3.Request;

import static com.sofi.blelocker.library.Constants.STATUS_CONNECTED;

@SuppressLint("NewApi")
public class MainActivity extends BaseFragmentActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String INTENT_MSG_COUNT = "INTENT_MSG_COUNT";
    public final static String MESSAGE_RECEIVED_ACTION = "io.yunba.example.msg_received_action";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

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

    private String type = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);
//        ButterKnife.bind(this);

        CrashHandler.getInstance().setmContext(this);

//        IntentFilter filter = new IntentFilter("data.broadcast.action");
//        registerReceiver(mReceiver, filter);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mScreenReceiver, filter);

        m_myHandler.sendEmptyMessage(1);

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

    private void initView() {

        tab = findViewById(R.id.tab);

        tab.setTabData(mTabEntities, MainActivity.this, R.id.fl_change, mFragments);
        tab.setCurrentTab(0);




        loadingDialog = new LoadingDialog(context);
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


    protected Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 0:
                    break;
                case 1:
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
        type = SharedPreferencesUrls.getInstance().getString("type", "");

        super.onResume();

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

            default:
                break;
        }
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


}