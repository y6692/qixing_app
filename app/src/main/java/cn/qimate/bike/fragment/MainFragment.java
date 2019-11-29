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
import com.bumptech.glide.Glide;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.inter.OnConnectionListener;
import com.sunshine.blelibrary.inter.OnDeviceSearchListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.zxing.lib.scaner.activity.ActivityScanerCode;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jpush.android.api.JPushInterface;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.ActionCenterActivity;
import cn.qimate.bike.activity.CouponActivity;
import cn.qimate.bike.activity.CrashHandler;
import cn.qimate.bike.activity.CurRoadBikedActivity;
import cn.qimate.bike.activity.CurRoadBikingActivity;
import cn.qimate.bike.activity.FeedbackActivity;
import cn.qimate.bike.activity.HistoryRoadDetailActivity;
import cn.qimate.bike.activity.InsureanceActivity;
import cn.qimate.bike.activity.LoginActivity;
import cn.qimate.bike.activity.MainActivity;
import cn.qimate.bike.activity.MyPurseActivity;
import cn.qimate.bike.activity.PayMontCartActivity;
import cn.qimate.bike.activity.PersonAlterActivity;
import cn.qimate.bike.activity.RealNameAuthActivity;
import cn.qimate.bike.activity.ServiceCenter0Activity;
import cn.qimate.bike.activity.ServiceCenterActivity;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.base.BaseFragment;
import cn.qimate.bike.ble.utils.ParseLeAdvData;
import cn.qimate.bike.core.common.AppManager;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.lock.utils.ToastUtils;
import cn.qimate.bike.model.CardinfoBean;
import cn.qimate.bike.model.CurRoadBikingBean;
import cn.qimate.bike.model.NearbyBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.TabEntity;
import cn.qimate.bike.model.TabTopEntity;
import cn.qimate.bike.model.UserMsgBean;
import cn.qimate.bike.util.HtmlTagHandler;
import cn.qimate.bike.util.ToastUtil;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static cn.qimate.bike.activity.CurRoadBikingActivity.bytes2hex03;

@SuppressLint("NewApi")
public class MainFragment extends BaseFragment implements View.OnClickListener, OnBannerListener {

    private View v;
    Unbinder unbinder;

    private static MainFragment mainFragment;
    private final static int SCANNIN_GREQUEST_CODE = 1;

    private LinearLayout scanLock, myCommissionLayout, myLocationLayout, linkLayout;

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

    private LinearLayout rl_ad;
    private LinearLayout refreshLayout;
    private LinearLayout slideLayout;
    private RelativeLayout rl_authBtn;
    private TextView tv_authBtn;


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
        activity.registerReceiver(broadcastReceiver, filter);

        initView();
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

        }
    }


    private void initView(){

        if(aMap==null){
            aMap = mapView.getMap();
        }

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setType(1).setTitle("温馨提示").setMessage("当前行程已停止计费，客服正在加紧处理，请稍等\n客服电话：0519—86999222");
        customDialog = customBuilder.create();

        rl_authBtn = activity.findViewById(R.id.rl_authBtn);
        tv_authBtn = activity.findViewById(R.id.tv_authBtn);
        refreshLayout = (LinearLayout) activity.findViewById(R.id.mainUI_refreshLayout);
        myLocationLayout =  (LinearLayout) activity.findViewById(R.id.mainUI_myLocationLayout);
        slideLayout = (LinearLayout)activity.findViewById(R.id.mainUI_slideLayout);
        linkLayout = (LinearLayout) activity.findViewById(R.id.mainUI_linkServiceLayout);
        scanLock = (LinearLayout) activity.findViewById(R.id.mainUI_scanCode_lock);

        rl_authBtn.setOnClickListener(this);
        refreshLayout.setOnClickListener(this);
        myLocationLayout.setOnClickListener(this);
        slideLayout.setOnClickListener(this);
        linkLayout.setOnClickListener(this);
        scanLock.setOnClickListener(this);







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

                initmPopupPayWindowView();

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
                Log.e("refreshLayout===0", "==="+SharedPreferencesUrls.getInstance().getString("iscert", ""));

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

            case R.id.ll_rent:
                Log.e("ll_rent===onClick", uid+"==="+access_token+"==="+SharedPreferencesUrls.getInstance().getString("iscert",""));


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
                                activity.startActivityForResult(intent, SCANNIN_GREQUEST_CODE);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("mf===requestCode", requestCode+"==="+resultCode);

        switch (requestCode) {

            case 1:
                if (resultCode == RESULT_OK) {
                    String price = data.getStringExtra("price");

                    Log.e("mf===requestCode1", "==="+price);

//                    String result = data.getStringExtra("QR_CODE");
//                    upcarmap(result);
//                    lock(result);

                    price = "<b><font color=\"#000000\">my html text</font></b>";
                    price = "<p style=\"color: #000000; font-size: 26px;\">my html text</p>";
                    price = "<p style=\"color: #00ff00\">my html text</p>";
                    price = "<p style=\"font-size:26px\">my html text</p>";
                    price = "<p style=\"color:#00ff00\"><font size=\"20\">my html text</font></p>";
                    price = "<font size=\"5\">my html text</font>";

                    price = "<p><font color=\"#000000\" size=\"20px\">" + "要显示的数据" + "</font></p>";


                    initmPopupRentWindowView(price);
//                    initmPopupRentWindowView("<html>"+price+"<\\/html>");
                } else {
                    Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                }

                Log.e("requestCode===1", "==="+resultCode);
                break;

            default:

                break;

        }

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

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

    protected Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {


//                case 33:
//                    ImageView iv_myLocation =  activity.findViewById(R.id.mainUI_myLocation7);
//                    AnimationDrawable animDrawable = (AnimationDrawable) iv_myLocation.getBackground();
//                    iv_myLocation.setBackground(animDrawable);
//
//                    if (animDrawable != null && !animDrawable.isRunning()) {
//                        animDrawable.start();
//                    }
//                    break;
//
//                case 0:
//                    if (!BaseApplication.getInstance().getIBLE().isEnable()){
//
//                        break;
//                    }
//                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, BikeFragment.this);
//                    break;
//                case 1:
//                    break;
//                case 2:
//                    if (lockLoading != null && lockLoading.isShowing()){
//                        lockLoading.dismiss();
//                    }
//
//                    stopXB();
//
//                    Log.e("main===", "type==="+type);
//
//                    if("3".equals(type)){
//                        endBtn3();
//                    }else{
//                        endBtn();
//                    }
//
//                    break;
//                case 3:
//                    if (lockLoading != null && lockLoading.isShowing()){
//                        lockLoading.dismiss();
//                    }
//                    stopXB();
//
//                    if(macList.size()>0  || isContainsList.contains(true)){
//                        if (lockLoading != null && !lockLoading.isShowing()){
//                            lockLoading.setTitle("正在连接");
//                            lockLoading.show();
//                        }
//
//                        isConnect = false;
//                        m_myHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (lockLoading != null && lockLoading.isShowing()){
//                                    lockLoading.dismiss();
//                                }
//
//                                if(!isConnect){
//                                    if("3".equals(type)){
//                                        if(first3){
//                                            first3 = false;
//                                            customDialog4.show();
//                                        }else{
//                                            carClose();
//                                        }
//                                    }else{
//                                        if (!BaseApplication.getInstance().getIBLE().getConnectStatus()){
//                                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//                                            customBuilder.setTitle("连接失败").setMessage("关锁后，请离车1米内重试或在右上角提交")
//                                                    .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                                                        public void onClick(DialogInterface dialog, int which) {
//                                                            dialog.cancel();
//                                                        }
//                                                    });
//                                            customBuilder.create().show();
//                                        }
//                                    }
//                                }
//
//                            }
//                        }, 10 * 1000);
//
//                        connect();
//                    }else{
//                        if("3".equals(type)){
//                            customDialog4.show();
//                        }else{
//                            customDialog3.show();
//                        }
//                    }
//
//                    break;
//                case 4:
//                    animMarker();
//                    break;
//
//                case 0x99://搜索超时
//                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, BikeFragment.this);
//                    break;
                default:
                    break;
            }
            return false;
        }
    });


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



    public void initmPopupRentWindowView(String price){

        // 获取自定义布局文件的视图
        View customView = getLayoutInflater().inflate(R.layout.pop_rent_bike, null, false);
        // 创建PopupWindow宽度和高度
        RelativeLayout pop_win_bg = (RelativeLayout) customView.findViewById(R.id.pop_rent_bg);
        ImageView iv_popup_window_back = (ImageView) customView.findViewById(R.id.popupWindow_rent_back);
        ImageView iv_rent_cancelBtn = (ImageView) customView.findViewById(R.id.iv_rent_cancelBtn);
        TextView tv_price = (TextView) customView.findViewById(R.id.tv_price);
        LinearLayout ll_change_car = (LinearLayout) customView.findViewById(R.id.ll_change_car);
        LinearLayout ll_rent = (LinearLayout) customView.findViewById(R.id.ll_rent);

//        tv_price.setText(Html.fromHtml(price));
        tv_price.setText(Html.fromHtml(price, null, new HtmlTagHandler("font")));

        iv_rent_cancelBtn.setOnClickListener(this);
        ll_change_car.setOnClickListener(this);
        ll_rent.setOnClickListener(this);

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
}
