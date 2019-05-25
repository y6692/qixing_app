package cn.qimate.bike.activity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.sofi.blelocker.library.connect.listener.BluetoothStateListener;
import com.sofi.blelocker.library.search.SearchRequest;

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
import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.base.BaseFragmentActivity;
import cn.qimate.bike.core.common.AppManager;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.UpdateManager;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.fragment.BikeFragment;
import cn.qimate.bike.fragment.EbikeFragment;
import cn.qimate.bike.model.TabTopEntity;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.Globals;
import cn.qimate.bike.util.ToastUtil;
import okhttp3.Request;

import static com.sofi.blelocker.library.Constants.STATUS_CONNECTED;

@SuppressLint("NewApi")
public class MainActivity extends BaseFragmentActivity implements View.OnClickListener{

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
    private ImageView leftBtn, rightBtn;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private String[] mTitles = { "单车", "电单车"};
//    private int[] mIconUnselectIds = {
//            R.drawable.bike, R.drawable.near, R.drawable.purse, R.drawable.mine
//    };
//    private int[] mIconSelectIds = {
//            R.drawable.bike2, R.drawable.near2, R.drawable.purse2, R.drawable.mine2
//    };
    private BikeFragment bikeFragment;
    private EbikeFragment ebikeFragment;

    public AMap aMap;
    public BitmapDescriptor successDescripter;
    public MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);
        ButterKnife.bind(this);

        IntentFilter filter = new IntentFilter("data.broadcast.action");
        registerReceiver(mReceiver, filter);

        mapView = (MapView) findViewById(R.id.mainUI_map);
        mapView.onCreate(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                m_myHandler.sendEmptyMessage(1);
            }
        }).start();

        initData();
        initView();
        initListener();
//        initLocation();
//        AppApplication.getApp().scan();

        type = SharedPreferencesUrls.getInstance().getString("type", "");

        if("4".equals(type)){
            changeTab(1);
        }else{
            changeTab(0);
        }


        OkHttpClientManager.getInstance().UserLogin("99920170623", "123456", new ResultCallback<RUserLogin>() {
            @Override
            public void onError(Request request, Exception e) {
                Log.e("UserLogin===", "====Error");
//                UIHelper.showToast(this, e.getMessage());
//                failLogin();
            }

            @Override
            public void onResponse(RUserLogin rUserLogin) {
                if (rUserLogin.getResult() < 0) {
//                    failLogin();
                    Log.e("UserLogin===", "====fail");
                }
                else {
                    Globals.USERNAME = "99920170623";
//                    Globals.BLE_NAME = "GpDTxe7<a";
//                    successLogin();
                    Log.e("UserLogin===", "====success");
                }
            }
        });

    }

    protected Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 0:
                    break;
                case 1:
                    getNetTime();

                    break;
                default:
                    break;
            }
            return false;
        }
    });

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
                            UpdateManager.getUpdateManager().checkAppUpdate(context, true);
                            SharedPreferencesUrls.getInstance().putString("date",""+format);
                        }
                    }else {
                        // 版本更新
                        UpdateManager.getUpdateManager().checkAppUpdate(context, true);
                        SharedPreferencesUrls.getInstance().putString("date",""+format);
                    }
                }
            });
        } catch (Exception e) {
            Log.e("getNetTime==1", "==="+SharedPreferencesUrls.getInstance().getString("date",""));

            String date = formatter.format(new Date());

            Log.e("getNetTime==1_2", "==="+SharedPreferencesUrls.getInstance().getString("date",""));

            if (SharedPreferencesUrls.getInstance().getString("date","") != null &&
                    !"".equals(SharedPreferencesUrls.getInstance().getString("date",""))){

                Log.e("getNetTime==2", "==="+date);

                if (!date.equals(SharedPreferencesUrls.getInstance().getString("date",""))){
                    Log.e("getNetTime==2_2", "===");

                    UpdateManager.getUpdateManager().checkAppUpdate(context, true);
                    SharedPreferencesUrls.getInstance().putString("date",""+date);
                }
            }else {

                Log.e("getNetTime==23", "===");
                // 版本更新
                UpdateManager.getUpdateManager().checkAppUpdate(context, true);
                SharedPreferencesUrls.getInstance().putString("date",""+date);
            }
            e.printStackTrace();
        }
    }

    @Override protected void onResume() {
        super.onResume();
        Log.e("main===onResume", "===");

        mapView.onResume();

        ClientManager.getClient().registerBluetoothStateListener(mBluetoothStateListener);


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
    public void onDestroy() {

        super.onDestroy();

        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
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

    private void initData() {
        mContext = this;
        bikeFragment = new BikeFragment();
        ebikeFragment = new EbikeFragment();
        mFragments.add(bikeFragment);
        mFragments.add(ebikeFragment);

        Log.e("main===initData", "===");

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabTopEntity(mTitles[i]));
        }
    }

    private void initView() {

        tab = findViewById(R.id.tab);

        tab.setTabData(mTabEntities, MainActivity.this, R.id.fl_change, mFragments);
        tab.setCurrentTab(0);


        leftBtn = (ImageView) findViewById(R.id.mainUI_leftBtn);
        rightBtn = (ImageView) findViewById(R.id.mainUI_rightBtn);

        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
    }

    public void changeTab(int index) {
        tab.setCurrentTab(index);
    }

    private void initListener() {
    }



    @Override
    public void onClick(View view) {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (view.getId()){
            case R.id.mainUI_leftBtn:
                UIHelper.goToAct(context, ActionCenterActivity.class);

                break;
            case R.id.mainUI_rightBtn:
                if (SharedPreferencesUrls.getInstance().getString("uid","") == null || "".equals(
                        SharedPreferencesUrls.getInstance().getString("access_token",""))){
                    UIHelper.goToAct(context, LoginActivity.class);
                    ToastUtil.showMessageApp(context,"请先登录你的账号");
                    return;
                }
                UIHelper.goToAct(context, PersonAlterActivity.class);
                break;

            default:
                break;
        }
    }

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