package cn.qimate.bike.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.sunshine.blelibrary.inter.OnConnectionListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.jpush.android.api.JPushInterface;
import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseFragment;
import cn.qimate.bike.base.BaseFragmentActivity;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.ToastUtil;

public class Main2Activity extends BaseFragmentActivity implements
        LocationSource,
        AMapLocationListener,
        AMap.OnCameraChangeListener,
        AMap.OnMapTouchListener,
        OnConnectionListener{

    //显示地图需要的变量
    private MapView mapView;//地图控件
    private AMap aMap;//地图对象


    //定位需要的声明
    private AMapLocationClient mLocationClient = null;//定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    private LocationSource.OnLocationChangedListener mListener = null;//定位监听器

    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;

    private BitmapDescriptor bikeDescripter;

    private UiSettings mUiSettings;
    private MyLocationStyle myLocationStyle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

//        WindowManager.LayoutParams winParams = getWindow().getAttributes();
//        winParams.flags |= (WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();

        mUiSettings = aMap.getUiSettings();
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
        //是否显示地图中放大缩小按钮
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
        //mUiSettings.setCompassEnabled(true);// 是否显示指南针
        mUiSettings.setRotateGesturesEnabled(false);
        mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        aMap.setTrafficEnabled(false);// 显示实时交通状况
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);

        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.strokeColor(android.R.color.transparent);//设置定位蓝点精度圆圈的边框颜色的方法。
        myLocationStyle.radiusFillColor(0x558291b6);//设置定位蓝点精度圆圈的填充颜色的方法。
        myLocationStyle.interval(10 * 1000);


//        //设置显示定位按钮 并且可以点击
//        UiSettings settings = aMap.getUiSettings();
//        //设置定位监听
//        aMap.setLocationSource(this);
//        // 是否显示定位按钮
//        settings.setMyLocationButtonEnabled(true);
//        // 是否可触发定位并显示定位层
//        aMap.setMyLocationEnabled(true);
//
//        aMap.setMapType(AMap.MAP_TYPE_NAVI);
//        aMap.getUiSettings().setZoomControlsEnabled(false);
//        aMap.getUiSettings().setMyLocationButtonEnabled(false);
//        aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);// 设置地图logo显示在右下方
//        aMap.getUiSettings().setLogoBottomMargin(-50);
//
//        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(18);// 设置缩放监听
//        aMap.moveCamera(cameraUpdate);
//        successDescripter = BitmapDescriptorFactory.fromResource(R.drawable.icon_usecarnow_position_succeed);
//        bikeDescripter = BitmapDescriptorFactory.fromResource(R.drawable.bike_icon);
//
//
//        //定位的小图标 默认是蓝点 这里自定义一团火，其实就是一张图片
//        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));
//        myLocationStyle.radiusFillColor(android.R.color.transparent);
//        myLocationStyle.strokeColor(android.R.color.transparent);
//        aMap.setMyLocationStyle(myLocationStyle);
//
//        //开始定位
//        initLoc();

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();

        mapView.onResume();
//        JPushInterface.onResume(this);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();

        mapView.onPause();
//        JPushInterface.onPause(this);
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
    }



    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

    }

    @Override
    public void onTouch(MotionEvent motionEvent) {

    }


    //定位
    private void initLoc() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }


    //定位回调函数
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {

        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码

                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(amapLocation);
                    //添加图钉
                    aMap.addMarker(getMarkerOptions(amapLocation));
                    //获取定位信息
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getProvince() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
                    Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
                    isFirstLoc = false;
                }


            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());

                Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    //自定义一个图钉，并且设置图标，当我们点击图钉时，显示设置的信息
    private MarkerOptions getMarkerOptions(AMapLocation amapLocation) {
        //设置图钉选项
        MarkerOptions options = new MarkerOptions();
        //图标
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_icon));

        //位置
        options.position(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()));
        StringBuffer buffer = new StringBuffer();
        buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() +  "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
        //标题
        options.title(buffer.toString());
        //子标题
        options.snippet("这里好火");
        //设置多少帧刷新一次图片资源
        options.period(60);

        return options;

    }
    //激活定位
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;

    }


}
