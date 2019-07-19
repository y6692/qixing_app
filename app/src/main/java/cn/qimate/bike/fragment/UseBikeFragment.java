package cn.qimate.bike.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.zbar.lib.ScanCaptureAct;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.ActionCenterActivity;
import cn.qimate.bike.activity.LoginActivity;
import cn.qimate.bike.activity.PersonAlterActivity;
import cn.qimate.bike.base.BaseFragment;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.model.TabTopEntity;
import cn.qimate.bike.util.ToastUtil;

@SuppressLint("NewApi")
public class UseBikeFragment extends BaseFragment implements View.OnClickListener
//        , LocationSource, AMapLocationListener
{

    private View v;
    Unbinder unbinder;

    private ImageView leftBtn, rightBtn;

//    @BindView(R.id.tab)
    CommonTabLayout tab;

    public static final String INTENT_INDEX = "INTENT_INDEX";

//    TabLayout tab;
    ViewPager vp;

    private MyPagerAdapter myPagerAdapter;

    private TextView title;

    private Context context;

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private String[] mTitles = { "单车", "电单车"};

    public AMap aMap;
    public BitmapDescriptor successDescripter;
    public MapView mapView;

    LocationManager locationManager;
    String provider = LocationManager.GPS_PROVIDER;
    private static final int PRIVATE_CODE = 1315;//开启GPS权限
    static private final int REQUEST_CODE_ASK_PERMISSIONS = 101;

    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private LocationSource.OnLocationChangedListener mListener;
    private LatLng myLocation = null;
    private double latitude = 0.0;
    private double longitude = 0.0;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_use_bike, null);

        unbinder = ButterKnife.bind(this, v);

        return v;
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();

        mapView = getActivity().findViewById(R.id.mainUI_map);
        mapView.onCreate(savedInstanceState);

        Log.e("UBF===", "==="+mapView);

        initView();
    }



    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("onHiddenChanged===UBF", "==="+hidden);

        if(hidden){
            //pause
            mapView.setVisibility(View.GONE);

//            mapView.onPause();
//            deactivate();
        }else{
            //resume
            mapView.setVisibility(View.VISIBLE);

//            mapView.onResume();
//
//            if (aMap != null) {
//                setUpMap();
//            }
        }
    }


    private void initView(){

        leftBtn = getActivity().findViewById(R.id.mainUI_leftBtn);
        rightBtn = getActivity().findViewById(R.id.mainUI_rightBtn);

        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

//        openGPSSettings();
//
//        if (Build.VERSION.SDK_INT >= 23) {
//            int checkPermission = getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
//            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
//                    requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE_ASK_PERMISSIONS);
//                } else {
//                    requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE_ASK_PERMISSIONS);
//                }
//                return;
//            }
//        }
//
//        loadingDialog = new LoadingDialog(context);
//        loadingDialog.setCancelable(false);
//        loadingDialog.setCanceledOnTouchOutside(false);
//
//
//        if(aMap==null){
//            aMap = mapView.getMap();
//            setUpMap();
//        }

//        aMap.setMapType(AMap.MAP_TYPE_NAVI);
//        aMap.getUiSettings().setZoomControlsEnabled(false);
//        aMap.getUiSettings().setMyLocationButtonEnabled(false);
//        aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);// 设置地图logo显示在右下方
//        aMap.getUiSettings().setLogoBottomMargin(-50);
//
//        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(18f);// 设置缩放监听
//        aMap.moveCamera(cameraUpdate);
//        successDescripter = BitmapDescriptorFactory.fromResource(R.drawable.icon_usecarnow_position_succeed);
////        freeDescripter = BitmapDescriptorFactory.fromResource(R.drawable.free_icon);
////        bikeDescripter = BitmapDescriptorFactory.fromResource(R.drawable.bike_icon);
//
////        aMap.setOnMapTouchListener(this);
//        setUpLocationStyle();



        Log.e("UBF===initData", "===");

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabTopEntity(mTitles[i]));
        }

        BikeFragment bikeFragment = new BikeFragment();
        EbikeFragment ebikeFragment = new EbikeFragment();

        mFragments.add(bikeFragment);
        mFragments.add(ebikeFragment);

        tab = getActivity().findViewById(R.id.tab);
        tab.setTabData(mTabEntities, getActivity(), R.id.fl_change2, mFragments);
//        tab.setTabData(mTabEntities);
        tab.setCurrentTab(0);


//        vp = (ViewPager)getActivity().findViewById(R.id.vp);
//
//        myPagerAdapter = new MyPagerAdapter(getChildFragmentManager());
////        myPagerAdapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
//        vp.setAdapter(myPagerAdapter);
//        vp.setOffscreenPageLimit(2);
////        tab.setupWithViewPager(vp);
////        vp.setCurrentItem(0);
//
//        tab.setCurrentTab(0);

//        tab.setOnTabSelectListener(new OnTabSelectListener() {
//            @Override
//            public void onTabSelect(int position) {
//
//                Log.e("tab===", "==="+position);
//
//                vp.setCurrentItem(position);
//            }
//
//            @Override
//            public void onTabReselect(int position) {
//
//            }
//        });
//
//        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override public void onPageSelected(int position) {
////                vp.setCurrentItem(position);
//                Log.e("vp===", "==="+position);
//
//                tab.setCurrentTab(position);
//            }
//
//            @Override public void onPageScrollStateChanged(int state) {
//
//            }
//        });
    }

//    private void setUpMap() {
//        aMap.setLocationSource(this);// 设置定位监听
//        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
//        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
//        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
////        aMap.setLoadOfflineData(true);
//    }

    public void changeTab(int index) {

        tab.setCurrentTab(index);

    }

    private void setUpLocationStyle() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));
        myLocationStyle.radiusFillColor(android.R.color.transparent);
        myLocationStyle.strokeColor(android.R.color.transparent);
        aMap.setMyLocationStyle(myLocationStyle);
    }

    private void openGPSSettings() {
        if (checkGPSIsOpen()) {
        } else {

            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
            customBuilder.setTitle("温馨提示").setMessage("请在手机设置打开应用的位置权限并选择最精准的定位模式")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            getActivity().finish();
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

    private boolean checkGPSIsOpen() {
        boolean isOpen;
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

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

//    @Override
//    public void onLocationChanged(AMapLocation amapLocation) {
//        if (mListener != null && amapLocation != null) {
//            if (amapLocation != null
//                    && amapLocation.getErrorCode() == 0) {
//                if (mListener != null) {
//                    mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
//                }
//                myLocation = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
//                latitude = amapLocation.getLatitude();
//                longitude = amapLocation.getLongitude();
//                //保存位置到本地
//                SharedPreferencesUrls.getInstance().putString("latitude",""+latitude);
//                SharedPreferencesUrls.getInstance().putString("longitude",""+longitude);
////                if (mFirstFix){
////                    initNearby(amapLocation.getLatitude(),amapLocation.getLongitude());
////                    mFirstFix = false;
////                    addChooseMarker();
////                } else {
////                    centerMarker.remove();
////                    mCircle.remove();
////                    addChooseMarker();
////                }
////                addCircle(myLocation, amapLocation.getAccuracy());//添加定位精度圆
//                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18f));
//            }
//        }
//    }
//
//
//
//    @Override
//    public void activate(OnLocationChangedListener listener) {
////	    super.activate(listener);
//
//        mListener = listener;
//
//        Log.e("main===b", "===listener===");
//
//
//        if (mlocationClient != null) {
//
//            mlocationClient.setLocationListener(this);
//            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//            mLocationOption.setInterval(2 * 1000);
//            mlocationClient.setLocationOption(mLocationOption);
//            mlocationClient.startLocation();
//
////			mListener.onLocationChanged(amapLocation);
//        }
//
////		if (mListener != null) {
////			mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
////		}
//
//        if (mlocationClient == null) {
//            mlocationClient = new AMapLocationClient(context);
//            mLocationOption = new AMapLocationClientOption();
//            //设置定位监听
//            mlocationClient.setLocationListener(this);
//            //设置为高精度定位模式
//            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//
//            // 关闭缓存机制
//            mLocationOption.setLocationCacheEnable(false);
//            //设置是否只定位一次,默认为false
//            mLocationOption.setOnceLocation(false);
//            //设置是否强制刷新WIFI，默认为强制刷新
//            mLocationOption.setWifiActiveScan(true);
//            //设置是否允许模拟位置,默认为false，不允许模拟位置
//            mLocationOption.setMockEnable(false);
//
//            mLocationOption.setInterval(2 * 1000);
//            //设置定位参数
//            mlocationClient.setLocationOption(mLocationOption);
//            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
//            // 在定位结束后，在合适的生命周期调用onDestroy()方法
//            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//            mlocationClient.startLocation();
//        }
//    }
//
//    @Override
//    public void deactivate() {
//        mListener = null;
//        if (mlocationClient != null) {
//            mlocationClient.stopLocation();
//            mlocationClient.onDestroy();
//        }
//        mlocationClient = null;
//    }

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

    class MyPagerAdapter extends FragmentPagerAdapter {
        private String[] titles = new String[]{"单车", "电单车"};
        private List<Fragment> fragmentList;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);

            BikeFragment bikeFragment = new BikeFragment();
            EbikeFragment ebikeFragment = new EbikeFragment();

            fragmentList = new ArrayList<>();
            fragmentList.add(bikeFragment);
            fragmentList.add(ebikeFragment);
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
    }

    @Override
    public void onPause() {
        super.onPause();
//        mapView.onPause();
//        deactivate();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);

        Log.e("UBF===onSIS", "==="+type);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case 100:
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if (permissions[0].equals(Manifest.permission.CAMERA)){
                        try {
                            Intent intent = new Intent();
                            intent.setClass(context, ScanCaptureAct.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("isChangeKey",false);
                            startActivityForResult(intent, 101);
                        } catch (Exception e) {
                            UIHelper.showToastMsg(context, "相机打开失败,请检查相机是否可正常使用", R.drawable.ic_error);
                        }
                    }
                }else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里允许获取相机权限！")
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
                            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
                            startActivity(localIntent);
//                            finishMine();
                        }
                    });
                    customBuilder.create().show();
                }
                break;
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    initView();
                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里允许获取定位权限！")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
//                                    context.finishMine();
                                }
                            }).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent localIntent = new Intent();
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
                            startActivity(localIntent);
//                            context.finishMine();
                        }
                    });
                    customBuilder.create().show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
