package cn.qimate.bike.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
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
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.AMapPara;
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
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.NaviType;
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
import com.autonavi.tbt.TrafficFacilityInfo;
import com.bumptech.glide.Glide;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.inter.OnConnectionListener;
import com.sunshine.blelibrary.inter.OnDeviceSearchListener;
import com.zbar.lib.ScanCaptureAct;
import com.zxing.lib.scaner.activity.ActivityScanerCode;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
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
import cn.qimate.bike.core.common.DisplayUtil;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.UpdateManager;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.core.widget.LoadingDialog2;
import cn.qimate.bike.lock.utils.ToastUtils;
import cn.qimate.bike.model.CarAuthorityBean;
import cn.qimate.bike.model.CardinfoBean;
import cn.qimate.bike.model.CurRoadBikingBean;
import cn.qimate.bike.model.NearbyBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UserMsgBean;
import cn.qimate.bike.util.ToastUtil;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static cn.qimate.bike.activity.CurRoadBikingActivity.bytes2hex03;
import static cn.qimate.bike.base.BaseFragmentActivity.carmodel_id;

@SuppressLint("NewApi")
public class BikeFragment extends BaseFragment implements View.OnClickListener, LocationSource,
        AMapLocationListener, AMap.OnCameraChangeListener, AMap.OnMapTouchListener, OnConnectionListener {

    Unbinder unbinder;

    private Context context;
    private Activity activity;
    private View v;

    static private final int REQUEST_CODE_ASK_PERMISSIONS = 101;

    private LoadingDialog2 lockLoading;
    public static boolean isForeground = false;

//    private ImageView leftBtn, rightBtn;
    private ImageView myLocationBtn, linkBtn;
    private LinearLayout scanLock, myCommissionLayout, myLocationLayout, linkLayout;
    private ImageView closeBtn;

//    private LinearLayout ll_top;
//    private LinearLayout ll_top_navi;
//    private TextView tv_navi_distance;

    protected AMap aMap;
    protected BitmapDescriptor successDescripter;
    protected BitmapDescriptor freeDescripter;
    private MapView mapView;
    //	private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstFix = true;
    public LatLng myLocation = null;
    private Circle mCircle;

    private BitmapDescriptor bikeDescripter;
    private Handler handler = new Handler();
    private Marker centerMarker;
    private boolean isMovingMarker = false;
//    private RelativeLayout rl_authBtn;
////    private TextView tv_authBtn;
//    private Button authBtn;
//    private Button cartBtn;
//    private Button rechargeBtn;
    private int Tag = 0;

    private List<Marker> bikeMarkerList;
    private boolean isUp = false;
//    private LinearLayout refreshLayout;

    private LinearLayout slideLayout;
    private LinearLayout marqueeLayout;
    private int imageWith = 0;
    private ValueAnimator animator = null;



    private static final int BAIDU_READ_PHONE_STATE = 100;//定位权限请求
    private static final int PRIVATE_CODE = 1315;//开启GPS权限

    protected OnLocationChangedListener mListener;
    CustomDialog.Builder customBuilder;
//    private CustomDialog customDialog;
    private CustomDialog customDialog2;
    private CustomDialog customDialog3;
    private CustomDialog customDialog4;
    private boolean isConnect = false;
    private int flag = 0;
    private int flag2 = 0;
    boolean isFrist1 = true;
    private int near = 1;
    public static int tz = 0;
    public static boolean screen = true;
    public static boolean start = false;
    public static boolean change = true;
    private boolean first = true;
    public boolean run = false;
    private long k=0;
    private long p=-1;
    private boolean first3 = true;
//    private boolean isStop = false;

//    private Dialog dialog;

    private TextView marquee;
//    protected InternalReceiver internalReceiver = null;

    private BluetoothAdapter mBluetoothAdapter;
    LocationManager locationManager;
    String provider = LocationManager.GPS_PROVIDER;
//	String provider = LocationManager.NETWORK_PROVIDER;


    public List<String> macList;
    public List<String> macList2;


    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private int n=0;
    private float accuracy = 29.0f;

    private boolean isHidden = true;

    private int carType = 1;

    private Bundle savedIS;
//    private AMapNavi mAMapNavi;
//    private RouteOverLay routeOverLay;
    private MarkerOptions centerMarkerOptionLoading;
    private MarkerOptions centerMarkerOption;

    private TextView tv_car_count;
    int car_count;

    private MarkerOptions marker_park_Option;
    private MarkerOptions marker_tip_Option;
    private MarkerOptions marker_tip_Option2;

    private boolean firstH = true;

    public List<Polygon> pOptionsNear;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_bike, null);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        activity = getActivity();

        CrashHandler.getInstance().setmContext(context);

//        aMap = ((MainFragment)activity).aMap;
//        successDescripter = ((MainFragment)activity).successDescripter;
//        mapView = ((MainFragment)activity).mapView;

        mapView = activity.findViewById(R.id.mainUI_map);

//        WindowManager.LayoutParams winParams = activity.getWindow().getAttributes();
//        winParams.flags |= (WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        savedIS = savedInstanceState;

        isContainsList = new ArrayList<>();
        macList = new ArrayList<>();
        macList2 = new ArrayList<>();
        pOptions = new ArrayList<>();
        bikeMarkerList = new ArrayList<>();
        pOptionsNear= new ArrayList<>();
        imageWith = (int) (activity.getWindowManager().getDefaultDisplay().getWidth() * 0.8);

//        mapView = (MapView) activity.findViewById(R.id.mainUI_map);
//        mapView.onCreate(savedInstanceState);

        initView();

        ToastUtil.showMessage(context, SharedPreferencesUrls.getInstance().getString("userName", "") + "===" + SharedPreferencesUrls.getInstance().getString("uid", "") + "<==>" + SharedPreferencesUrls.getInstance().getString("access_token", ""));

        m_nowMac = SharedPreferencesUrls.getInstance().getString("m_nowMac", "");
        Log.e("main===bike", "m_nowMac====" + m_nowMac);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        Log.e("onHiddenChanged===bike", firstH+"==="+hidden+"==="+type+"==="+referLatitude);

        isHidden = hidden;

        if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }



        if(hidden){
            //pause
//            aMap.clear();
//            mapView.onPause();
//            mapView.setVisibility(View.GONE);
//            deactivate();

//            aMap.clear();

//            if(mAMapNavi != null){
//                mAMapNavi.stopNavi();
//                mAMapNavi.destroy();
//            }

//            if(ll_top!=null){
//                ll_top.setVisibility(View.VISIBLE);
//                ll_top_navi.setVisibility(View.GONE);
//            }

//            schoolRange();
        }else{
            //resume

            if(firstH){
                firstH = false;

                if(carmodel_id==2){
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                    MainFragment.getInstance().changeTab(1);
                    return;
                }
            }

//            mapView.onResume();

            if(aMap==null){
                aMap = mapView.getMap();
            }

            pOptions.clear();
            isContainsList.clear();
            aMap.clear();
            aMap.reloadMap();

//
//            aMap = mapView.getMap();
            setUpMap();


            aMap.setOnMapTouchListener(this);
//            aMap.setOnMapClickListener(this);
            aMap.setOnCameraChangeListener(this);
//            setUpLocationStyle();


            if(centerMarker!=null){
                centerMarker.remove();
                centerMarker=null;
            }
            if(mCircle!=null){
                mCircle.remove();
                mCircle = null;
            }

            if (!isContainsList.isEmpty() || 0 != isContainsList.size()) {
                isContainsList.clear();
            }
            for (int i = 0; i < pOptions.size(); i++) {
                isContainsList.add(pOptions.get(i).contains(myLocation));
            }

            schoolRange();
//            operating_areas();

            if(referLatitude!=0 && referLongitude!=0){
                myLocation = new LatLng(referLatitude, referLongitude);

                initNearby(referLatitude, referLongitude);

                addChooseMarker();
                addCircle(myLocation, accuracy);
            }

        }
    }

    private void operating_areas(){
        if(isHidden) return;

        Log.e("bf===operating_areas0", isHidden+"===");

        RequestParams params = new RequestParams();

        HttpHelper.get2(context, Urls.operating_areas, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if(!isHidden){
                    onStartCommon("正在加载");
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon("bf===operating_areas", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                Log.e("bf===operating_areas", "==="+responseString);

//                responseString = "{\"data\":[[{\"longitude\":\"119.920544\",\"latitude\":\"31.764389\"},{\"longitude\":\"119.921544\",\"latitude\":\"31.765389\"},{\"longitude\":\"119.922544\",\"latitude\":\"31.764389\"}]]}";

                final ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            if (1==1 || result.getFlag().equals("Success")) {
                                JSONArray jsonArray = new JSONArray(result.getData());

                                Log.e("bf===operating_areas1", "==="+jsonArray);

                                if(isHidden) return;

                                if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
                                    isContainsList.clear();
                                }

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    List<LatLng> list = new ArrayList<>();
                                    List<LatLng> list2 = new ArrayList<>();
                                    int flag=0;

                                    for (int j = 0; j < jsonArray.getJSONArray(i).length(); j ++){

                                        JSONObject jsonObject = jsonArray.getJSONArray(i).getJSONObject(j);

                                        LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")), Double.parseDouble(jsonObject.getString("longitude")));

                                        Log.e("bf===operating_areas2", "==="+latLng);

                                        flag=0;
                                        list.add(latLng);


//                                        if(jsonObject.getInt("is_yhq")==0){
//                                            flag=0;
//                                            list.add(latLng);
//
//                                        }else{
//                                            flag=1;
//                                            list2.add(latLng);
//
////                                    MarkerOptions centerMarkerOption = new MarkerOptions().position(latLng).icon(freeDescripter);
////                                    aMap.addMarker(centerMarkerOption);
//
//                                        }
                                    }

                                    Log.e("bf===operating_areas3", "==="+list.size());

                                    Polygon polygon = null;
                                    PolygonOptions pOption = new PolygonOptions();

                                    pOption.addAll(list);
                                    polygon = aMap.addPolygon(pOption.strokeWidth(3)
//                                            .strokeDashStyle()
                                            .strokeColor(Color.argb(255, 0, 135, 255))
                                            .fillColor(Color.argb(0, 0, 0, 0)));
//                                            .fillColor(Color.argb(76, 0, 173, 255)));

                                    Log.e("bf===operating_areas4", "==="+polygon);

                                    getMaxPoint(list);

//                                    if(flag==0){
//                                        pOption.addAll(list);
//                                        polygon = aMap.addPolygon(pOption.strokeWidth(2)
//                                                .strokeColor(Color.argb(255, 228, 59, 74))
//                                                .fillColor(Color.argb(75, 230, 0, 18)));
//                                    }else{
//                                        pOption.addAll(list2);
//                                        polygon = aMap.addPolygon(pOption.strokeWidth(2)
//                                                .strokeColor(Color.argb(255, 255, 80, 23))
//                                                .fillColor(Color.argb(75, 255, 80, 23)));
//
//                                        getCenterPoint(list2);
//                                    }



//                                    if(!isHidden){
//                                        pOptions.add(polygon);
//
//                                        isContainsList.add(polygon.contains(myLocation));
//                                    }else{
//                                    }

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
        });
    }

    public void schoolRange(){
        if(isHidden) return;

//        if(referLatitude==0.0 || referLongitude==0.0) return;

        Log.e("main_b===schoolRange", isHidden+"==="+referLatitude+"==="+referLongitude+"==="+jsonArray);


        if(jsonArray != null){
            Log.e("main_b===schoolRange20", loadingDialog+"==="+loadingDialog.isShowing());
            onStartCommon2("正在加载");

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {

                    try{
                        if(isHidden) return;

                        Log.e("main_b===schoolRange21", loadingDialog+"==="+loadingDialog.isShowing());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
                                        isContainsList.clear();
                                    }

                                    if (!listPoint.isEmpty() || 0 != listPoint.size()){
                                        listPoint.clear();
                                    }

                                    if (!centerList.isEmpty() || 0 != centerList.size()){
                                        centerList.clear();
                                    }

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        List<LatLng> list = new ArrayList<>();
                                        List<LatLng> list2 = new ArrayList<>();
                                        int flag=0;

                                        JSONArray jsonArray2 = new JSONArray(jsonArray.getJSONObject(i).getString("ranges"));;
                                        JSONObject jsonObject = new JSONObject(jsonArray.getJSONObject(i).getString("parking"));

//                                        Log.e("main_b===schoolRange22", jsonArray2.length()+"==="+jsonArray2);

                                        for (int j = 0; j < jsonArray2.length(); j++) {
                                            LatLng latLng = new LatLng(Double.parseDouble(jsonArray2.getJSONObject(j).getString("latitude")), Double.parseDouble(jsonArray2.getJSONObject(j).getString("longitude")));

//                                            Log.e("main_b===schoolRange222", jsonArray2.length()+"==="+jsonArray2);

                                            flag=0;
                                            list.add(latLng);

                                            listPoint.add(latLng);
                                        }

//                                        Log.e("main_b===schoolRange23", "==="+list.size());

                                        Polygon polygon = null;
                                        PolygonOptions pOption = new PolygonOptions();

                                        pOption.addAll(list);

                                        polygon = aMap.addPolygon(pOption
//                                                .strokeWidth(2)
//                                                .strokeColor(Color.argb(255, 0, 135, 255))
//                                                .fillColor(Color.argb(77, 0, 173, 255)));
                                                .strokeColor(Color.argb(0, 255, 255, 255))
                                                .fillColor(Color.argb(0, 255, 255, 255)));

//                                        Log.e("main_b===schoolRange24", jsonObject.getString("name")+"==="+jsonObject.getString("latitude")+"==="+polygon);

                                        LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")), Double.parseDouble(jsonObject.getString("longitude")));
//                                        marker_park_Option.title(jsonObject.getString("name")).position(latLng);
//                                        aMap.addMarker(marker_park_Option);

                                        centerList.add(latLng);


                                        if(!isHidden){
                                            pOptions.add(polygon);

                                            isContainsList.add(polygon.contains(myLocation));
                                        }else{
                                        }
                                    }

                                    closeLoadingDialog2();
                                }catch (Exception e){
                                    closeLoadingDialog2();
                                }
                            }
                        }).start();


                    }catch (Exception e){
                        closeLoadingDialog2();
                    }

                    Log.e("main_b===schoolRange25", isContainsList.size()+"==="+isContainsList.contains(true)+"==="+pOptions.size()+"==="+pOptions);



                }
            });


        }else{
            RequestParams params = new RequestParams();
//            params.put("latitude", referLatitude);
//            params.put("longitude", referLongitude);

//            "latitude")), Double.parseDouble(jsonArray2.getJSONObject(j).getString("longitude"

            HttpHelper.get2(context, Urls.parking_ranges, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if(!isHidden){
                        onStartCommon2("正在加载");
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    onFailureCommon2("bf===schoolRange", throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {

                    Log.e("main_b===schoolRange0", "==="+responseString);

                    final ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (1==1 || result.getFlag().equals("Success")) {
                                    jsonArray = new JSONArray(result.getData());

                                    Log.e("main_b===schoolRange1", jsonArray.length()+"==="+jsonArray);

                                    if(isHidden){
                                        return;
                                    }

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try{
                                                if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
                                                    isContainsList.clear();
                                                }

                                                if (!listPoint.isEmpty() || 0 != listPoint.size()){
                                                    listPoint.clear();
                                                }

                                                if (!centerList.isEmpty() || 0 != centerList.size()){
                                                    centerList.clear();
                                                }

                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    List<LatLng> list = new ArrayList<>();
                                                    List<LatLng> list2 = new ArrayList<>();
                                                    int flag=0;

                                                    JSONArray jsonArray2 = new JSONArray(jsonArray.getJSONObject(i).getString("ranges"));;
                                                    JSONObject jsonObject = new JSONObject(jsonArray.getJSONObject(i).getString("parking"));

//                                                    Log.e("main_b===schoolRange2", jsonArray2.length()+"==="+jsonArray2);

                                                    for (int j = 0; j < jsonArray2.length(); j++) {
                                                        LatLng latLng = new LatLng(Double.parseDouble(jsonArray2.getJSONObject(j).getString("latitude")), Double.parseDouble(jsonArray2.getJSONObject(j).getString("longitude")));

//                                                        Log.e("main_b===schoolRange22", jsonArray2.length()+"==="+jsonArray2);

                                                        flag=0;
                                                        list.add(latLng);

                                                        listPoint.add(latLng);
                                                    }

//                                                    Log.e("main_b===schoolRange3", "==="+list.size());

                                                    Polygon polygon = null;
                                                    PolygonOptions pOption = new PolygonOptions();

                                                    pOption.addAll(list);

                                                    polygon = aMap.addPolygon(pOption
//                                                            .strokeWidth(2)
//                                                            .strokeColor(Color.argb(255, 0, 135, 255))
//                                                            .fillColor(Color.argb(77, 0, 173, 255)));
                                                            .strokeColor(Color.argb(0, 255, 255, 255))
                                                            .fillColor(Color.argb(0, 255, 255, 255)));
//                                                    Log.e("main_b===schoolRange4", jsonObject.getString("name")+"==="+jsonObject.getString("latitude")+"==="+polygon);

                                                    LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")), Double.parseDouble(jsonObject.getString("longitude")));
//                                                    marker_park_Option.title(jsonObject.getString("name")).position(latLng);
//                                                    aMap.addMarker(marker_park_Option);

                                                    centerList.add(latLng);

                                                    if(!isHidden){
                                                        pOptions.add(polygon);
                                                        isContainsList.add(polygon.contains(myLocation));
                                                    }else{
                                                    }
                                                }

                                                Log.e("main_b===schoolRange5", isContainsList.size()+"==="+isContainsList.contains(true)+"==="+pOptions.size()+"==="+pOptions);

                                                closeLoadingDialog2();
                                            }catch (Exception e){
                                                closeLoadingDialog2();
                                            }
                                        }
                                    }).start();

                                }else {
                                    closeLoadingDialog2();
                                    ToastUtil.showMessageApp(context,result.getMsg());
                                }
                            }catch (Exception e){

                                closeLoadingDialog2();
                            }

                        }
                    });

                }
            });
        }

    }

    public void closeLoadingDialog2(){
        if (loadingDialog2 != null && loadingDialog2.isShowing()){
            loadingDialog2.dismiss();
        }
    }

    public void initNearby(final double latitude, final double longitude){

        if(isHidden) return;

        if(latitude==0.0 || longitude==0.0) return;

        Log.e("bf===initNearby0", unauthorized_code+"==="+latitude+"==="+longitude);

        RequestParams params = new RequestParams();
        params.put("latitude", latitude);
        params.put("longitude", longitude);
//        params.put("type", 1);
        HttpHelper.get2(context, Urls.car_nearby+"1/nearby", params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                onStartCommon("正在加载");

//                ArrayList<BitmapDescriptor> iconList = new ArrayList<>();
//                iconList.add(BitmapDescriptorFactory.fromView(View.inflate(context, R.layout.marker_info_layout1, null)));
//                iconList.add(BitmapDescriptorFactory.fromView(View.inflate(context, R.layout.marker_info_layout2, null)));
//                iconList.add(BitmapDescriptorFactory.fromView(View.inflate(context, R.layout.marker_info_layout3, null)));
//
//                MarkerOptions centerMarkerOption = new MarkerOptions();
//                centerMarkerOption.position(myLocation).icons(iconList).period(2);


                if(centerMarker!=null){
                    centerMarker.setMarkerOptions(centerMarkerOptionLoading);

//                    if(unauthorized_code==6){
//                        centerMarker.setAlpha(0);
//                    }else{
//                        centerMarker.setAlpha(255);
//                    }
                }


//                centerMarker.setIcon(iconList);
//                centerMarker.setIcon(BitmapDescriptorFactory.fromView(View.inflate(context, R.layout.marker_info_layout, null)));

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                if(isHidden) return;

                try {
                    Log.e("initNearby===Bike", "==="+responseString);

                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                    car_count = new JSONObject(result.getData()).getInt("count");


                    Log.e("initNearby===Bike1", "==="+car_count);

//                    tv_car_count.setText(count+"辆");

//                    centerMarker.setMarkerOptions(centerMarkerOption);

                    View view = View.inflate(context, R.layout.marker_info_layout, null);
                    tv_car_count = view.findViewById(R.id.tv_car_count);
                    tv_car_count.setText((car_count>99?99:car_count)+"辆");
                    centerMarkerOption = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromView(view));

                    if(centerMarker!=null){
                        centerMarker.remove();
                    }

                    centerMarker = aMap.addMarker(centerMarkerOption);

//                    if(unauthorized_code==6){
//                        centerMarker.setAlpha(0);
//                    }else{
//                        centerMarker.setAlpha(255);
//                    }

//                    if (result.getFlag().equals("Success")) {
//                        JSONArray array = new JSONArray(result.getData());
//
//                        Log.e("initNearby===Bike", "==="+array.length());
//
//                        for (Marker marker : bikeMarkerList){
//                            if (marker != null){
//                                marker.remove();
//                            }
//                        }
//                        if (!bikeMarkerList.isEmpty() || 0 != bikeMarkerList.size()){
//                            bikeMarkerList.clear();
//                        }
//                        if (0 == array.length()){
//                            ToastUtils.show("附近没有单车");
//                        }else {
//                            for (int i = 0; i < array.length(); i++){
//                                NearbyBean bean = JSON.parseObject(array.getJSONObject(i).toString(), NearbyBean.class);
//                                // 加入自定义标签
//
////                                Log.e("initNearby===Bike", bean.getLatitude()+"==="+bean.getLongitude());
//
//                                MarkerOptions bikeMarkerOption = new MarkerOptions().position(new LatLng(Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude()))).icon(bikeDescripter);
//                                Marker bikeMarker = aMap.addMarker(bikeMarkerOption);
//                                bikeMarkerList.add(bikeMarker);
//                            }
//
//                        }
//                    } else {
//                        ToastUtils.show(result.getMsg());
//                    }
                } catch (Exception e) {

                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });


//        params = new RequestParams();
//        params.put("latitude", referLatitude);
//        params.put("longitude", referLongitude);

//            "latitude")), Double.parseDouble(jsonArray2.getJSONObject(j).getString("longitude"

        HttpHelper.get2(context, Urls.parking_ranges, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if(!isHidden){
                    onStartCommon("正在加载");
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon("bf===parking_ranges", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                Log.e("main_b===parking_r0", "==="+responseString);

                final ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (1==1 || result.getFlag().equals("Success")) {
                                final JSONArray jsonArray = new JSONArray(result.getData());

                                Log.e("main_b===parking_r1", jsonArray.length()+"==="+jsonArray);

                                if(isHidden){
                                    return;
                                }

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
//                                            if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
//                                                isContainsList.clear();
//                                            }
//
//                                            if (!listPoint.isEmpty() || 0 != listPoint.size()){
//                                                listPoint.clear();
//                                            }
//
//                                            if (!centerList.isEmpty() || 0 != centerList.size()){
//                                                centerList.clear();
//                                            }

//                                            aMap.clear();

                                            for (Marker marker : bikeMarkerList){
                                                if (marker != null){
                                                    marker.remove();
                                                }
                                            }
                                            if (!bikeMarkerList.isEmpty() || 0 != bikeMarkerList.size()){
                                                bikeMarkerList.clear();
                                            }



                                            for ( int i = 0; i < pOptionsNear.size(); i++){
                                                pOptionsNear.get(i).remove();
                                            }

                                            if (!pOptionsNear.isEmpty() || 0 != pOptionsNear.size()){
                                                pOptionsNear.clear();
                                            }

                                            if (0 == jsonArray.length()){
                                                ToastUtils.show("附近没有停车点");
                                            }else {
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    List<LatLng> list = new ArrayList<>();
                                                    List<LatLng> list2 = new ArrayList<>();
                                                    int flag=0;

                                                    JSONArray jsonArray2 = new JSONArray(jsonArray.getJSONObject(i).getString("ranges"));;
                                                    JSONObject jsonObject = new JSONObject(jsonArray.getJSONObject(i).getString("parking"));

//                                                    Log.e("main_b===schoolRange2", jsonArray2.length()+"==="+jsonArray2);

                                                    for (int j = 0; j < jsonArray2.length(); j++) {
                                                        LatLng latLng = new LatLng(Double.parseDouble(jsonArray2.getJSONObject(j).getString("latitude")), Double.parseDouble(jsonArray2.getJSONObject(j).getString("longitude")));

//                                                        Log.e("main_b===schoolRange22", jsonArray2.length()+"==="+jsonArray2);

                                                        flag=0;
                                                        list.add(latLng);

//                                                    listPoint.add(latLng);
                                                    }

//                                                    Log.e("main_b===schoolRange3", "==="+list.size());

                                                    Polygon polygon = null;
                                                    PolygonOptions pOption = new PolygonOptions();

                                                    pOption.addAll(list);

                                                    polygon = aMap.addPolygon(pOption
                                                            .strokeWidth(2)
                                                            .strokeColor(Color.argb(255, 0, 135, 255))
                                                            .fillColor(Color.argb(77, 0, 173, 255)));

                                                    LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")), Double.parseDouble(jsonObject.getString("longitude")));
                                                    marker_park_Option.title(jsonObject.getString("name")).position(latLng);
                                                    Marker bikeMarker = aMap.addMarker(marker_park_Option);
                                                    bikeMarkerList.add(bikeMarker);

//                                                  centerList.add(latLng);

                                                    if(!isHidden){
                                                        pOptionsNear.add(polygon);
//                                                      isContainsList.add(polygon.contains(myLocation));
                                                    }else{
                                                    }
                                                }
                                            }



                                            Log.e("main_b===parking_r5", isContainsList.size()+"==="+isContainsList.contains(true)+"==="+pOptions.size()+"==="+pOptions);

                                            if (loadingDialog != null && loadingDialog.isShowing()){
                                                loadingDialog.dismiss();
                                            }
                                        }catch (Exception e){
                                            if (loadingDialog != null && loadingDialog.isShowing()){
                                                loadingDialog.dismiss();
                                            }
                                        }
                                    }
                                }).start();

                            }else {
                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }
                                ToastUtil.showMessageApp(context,result.getMsg());
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


    public void sr(){
        if(isHidden) return;

        RequestParams params = new RequestParams();

        HttpHelper.get2(context, Urls.parking_ranges, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if(!isHidden){
                    onStartCommon("正在加载");
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon("bf===schoolRange", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                Log.e("main_b===schoolRange0", "==="+responseString);

                final ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (1==1 || result.getFlag().equals("Success")) {
                                jsonArray = new JSONArray(result.getData());

                                Log.e("main_b===schoolRange1", jsonArray.length()+"==="+jsonArray);

                                if(isHidden){
                                    return;
                                }


                                if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
                                    isContainsList.clear();
                                }

                                if (!listPoint.isEmpty() || 0 != listPoint.size()){
                                    listPoint.clear();
                                }

                                if (!centerList.isEmpty() || 0 != centerList.size()){
                                    centerList.clear();
                                }

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    List<LatLng> list = new ArrayList<>();
                                    List<LatLng> list2 = new ArrayList<>();
                                    int flag=0;

                                    JSONArray jsonArray2 = new JSONArray(jsonArray.getJSONObject(i).getString("ranges"));;
                                    JSONObject jsonObject = new JSONObject(jsonArray.getJSONObject(i).getString("parking"));

//                                    Log.e("main_b===schoolRange2", jsonArray2.length()+"==="+jsonArray2);

                                    for (int j = 0; j < jsonArray2.length(); j++) {
                                        LatLng latLng = new LatLng(Double.parseDouble(jsonArray2.getJSONObject(j).getString("latitude")), Double.parseDouble(jsonArray2.getJSONObject(j).getString("longitude")));

//                                        Log.e("main_b===schoolRange22", jsonArray2.length()+"==="+jsonArray2);

                                        flag=0;
                                        list.add(latLng);

                                        listPoint.add(latLng);
                                    }

//                                    Log.e("main_b===schoolRange3", "==="+list.size());

                                    Polygon polygon = null;
                                    PolygonOptions pOption = new PolygonOptions();

                                    pOption.addAll(list);

                                    polygon = aMap.addPolygon(pOption.strokeColor(Color.argb(0, 255, 255, 255)).fillColor(Color.argb(0, 255, 255, 255)));

//                                    Log.e("main_b===schoolRange4", jsonObject.getString("name")+"==="+jsonObject.getString("latitude")+"==="+polygon);

                                    LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")), Double.parseDouble(jsonObject.getString("longitude")));
                                    marker_park_Option.title(jsonObject.getString("name")).position(latLng);
                                    aMap.addMarker(marker_park_Option);

                                    centerList.add(latLng);

                                    if(!isHidden){
                                        pOptions.add(polygon);
                                        isContainsList.add(polygon.contains(myLocation));
                                    }else{
                                    }
                                }

                                Log.e("main_b===schoolRange5", isContainsList.size()+"==="+isContainsList.contains(true)+"==="+pOptions.size()+"==="+pOptions);

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
        });
    }

    private void schoolRange2(){
        if(isHidden) return;

        Log.e("main===schoolRange0", isHidden+"==="+jsonArray);

        if(jsonArray != null){

            Log.e("biking===schoolRange%", loadingDialog+"==="+loadingDialog.isShowing());

            onStartCommon("正在加载");

//            m_myHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (loadingDialog2 != null && !loadingDialog2.isShowing()) {
//                        loadingDialog2.setTitle("正在加载");
//                        loadingDialog2.show();
//                    }
//                }
//            });


            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(isHidden) return;

                    Log.e("biking===schoolRange%0", loadingDialog+"==="+loadingDialog.isShowing());

                    try{
                        pOptions.clear();
                        if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
                            isContainsList.clear();
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {

//                            if (loadingDialog != null && !loadingDialog.isShowing()) {
//                                loadingDialog.setTitle("正在加载");
//                                loadingDialog.show();
//                            }

                            final List<LatLng> list = new ArrayList<>();
                            final List<LatLng> list2 = new ArrayList<>();
                            int flag=0;

                            for (int j = 0; j < jsonArray.getJSONArray(i).length(); j ++){
                                JSONObject jsonObject = jsonArray.getJSONArray(i).getJSONObject(j);

                                LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")), Double.parseDouble(jsonObject.getString("longitude")));

                                if(jsonObject.getInt("is_yhq")==0){
                                    flag=0;
                                    list.add(latLng);
                                }else{
                                    flag=1;
                                    list2.add(latLng);
                                }
                            }

                            Log.e("biking===schoolRange#", "==="+i);

                            Polygon polygon = null;
                            final PolygonOptions pOption = new PolygonOptions();
                            if(flag==0){
                                pOption.addAll(list);

                                pOptions.add(aMap.addPolygon(pOption.strokeWidth(2)
                                        .strokeColor(Color.argb(255, 228, 59, 74))
                                        .fillColor(Color.argb(75, 230, 0, 18))));

                            }else{
                                pOption.addAll(list2);
                                pOptions.add(aMap.addPolygon(pOption.strokeWidth(2)
                                        .strokeColor(Color.argb(255, 255, 80, 23))
                                        .fillColor(Color.argb(75, 255, 80, 23))));

                                getCenterPoint(list2);
                            }

                            if(!isHidden){
//                                pOptions.add(polygon);

//                                isContainsList.add(polygon.contains(myLocation));
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e("biking===schoolRange_e", "==="+e);
                    }

                    Log.e("biking===schoolRange%1", loadingDialog+"==="+loadingDialog.isShowing());

                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }

                    Log.e("biking===schoolRange%2", loadingDialog+"==="+loadingDialog.isShowing());
                }
            });


        }else{
            RequestParams params = new RequestParams();
            params.put("type", 1);

//            if(1==1) return;

            HttpHelper.get(context, Urls.schoolRange, params, new TextHttpResponseHandler() {
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
                                    jsonArray = new JSONArray(result.getData());

                                    if(isHidden) return;

                                    if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
                                        isContainsList.clear();
                                    }
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        List<LatLng> list = new ArrayList<>();
                                        List<LatLng> list2 = new ArrayList<>();
                                        int flag=0;

//                            Log.e("main===schoolRange1", i+"==="+jsonArray.length()+"==="+jsonArray.getJSONArray(i).length());

                                        for (int j = 0; j < jsonArray.getJSONArray(i).length(); j ++){

                                            JSONObject jsonObject = jsonArray.getJSONArray(i).getJSONObject(j);

//                                Log.e("main===schoolRange1", "221==="+jsonObject.getInt("is_yhq"));

                                            LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")), Double.parseDouble(jsonObject.getString("longitude")));


//                                Log.e("main===schoolRange1", "222==="+Integer.parseInt(jsonObject.getString("is_yhq")));

                                            if(jsonObject.getInt("is_yhq")==0){
                                                flag=0;
                                                list.add(latLng);

//                                    Log.e("main===schoolRange1", "222==="+jsonObject.getInt("is_yhq"));
                                            }else{
                                                flag=1;
                                                list2.add(latLng);

//                                    MarkerOptions centerMarkerOption = new MarkerOptions().position(latLng).icon(freeDescripter);
//                                    aMap.addMarker(centerMarkerOption);

//                                    Log.e("main===schoolRange1", "223==="+jsonObject.getInt("is_yhq"));
                                            }
                                        }

//                            Log.e("main===schoolRange1", "333==="+list2+"==="+flag);

                                        Polygon polygon = null;
                                        PolygonOptions pOption = new PolygonOptions();
                                        if(flag==0){
                                            pOption.addAll(list);
                                            polygon = aMap.addPolygon(pOption.strokeWidth(2)
                                                    .strokeColor(Color.argb(255, 228, 59, 74))
                                                    .fillColor(Color.argb(75, 230, 0, 18)));
//                                polygon = aMap.addPolygon(pOption.strokeWidth(2)
//                                        .strokeColor(Color.argb(160, 0, 0, 255))
//                                        .fillColor(Color.argb(160, 0, 0, 255)));
                                        }else{
                                            pOption.addAll(list2);
                                            polygon = aMap.addPolygon(pOption.strokeWidth(2)
                                                    .strokeColor(Color.argb(255, 255, 80, 23))
                                                    .fillColor(Color.argb(75, 255, 80, 23)));
//                                polygon = aMap.addPolygon(pOption.strokeWidth(2)
//                                        .strokeColor(Color.argb(160, 255, 0, 0))
//                                        .fillColor(Color.argb(160, 255, 0, 0)));

                                            getCenterPoint(list2);
                                        }



                                        if(!isHidden){
                                            pOptions.add(polygon);

                                            isContainsList.add(polygon.contains(myLocation));
                                        }else{
//                                Log.e("pOptions===Bike", isContainsList.size()+"==="+pOptions.size());
                                        }

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
            });
        }


    }

    public LatLng getMaxPoint(List<LatLng> list) {
        double x = 0.0;
        double y = 0.0;

        double m = 0.0;
        double m1 = 0.0;

        for (int i = 0; i < list.size(); i++) {
//            x = x + list.get(i).longitude;
//            y = y + list.get(i).latitude;

            m1 = list.get(i).latitude;

            if(i == 0 || m1 > m){
                m = m1;

                x = list.get(i).longitude;
                y = list.get(i).latitude;
            }else{
                continue;
            }
        }

//        MarkerOptions centerMarkerOption = new MarkerOptions().position(new LatLng(y, x)).icon(freeDescripter);
        marker_tip_Option.position(new LatLng(y, x));
        aMap.addMarker(marker_tip_Option);

//        centerList.add(new LatLng(y, x));
//        Log.e("getCenterPoint===2", x+"==="+y);

        return new LatLng(y, x);
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

        MarkerOptions centerMarkerOption = new MarkerOptions().position(new LatLng(y, x)).icon(freeDescripter);
        aMap.addMarker(centerMarkerOption);

//        centerList.add(new LatLng(y, x));
//        Log.e("getCenterPoint===2", x+"==="+y);

        return new LatLng(y, x);
    }

    public LatLng minPolygon(List<LatLng> centerList) {
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
//        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(30);// 设置缩放监听
//        aMap.moveCamera(cameraUpdate);
//
//        polyline = aMap.addPolyline(pOption);
//
//        Log.e("minPolygon===2", "==="+s);

        return null;
    }

    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        Log.e("main===ChangeFinish_B", isContainsList.contains(true) + "》》》" + cameraPosition.target.latitude + "===" + macList.size()+">>>"+isUp + "===" + cameraPosition.target.latitude);


        if (isUp  && !isHidden){
            initNearby(cameraPosition.target.latitude, cameraPosition.target.longitude);

            if (centerMarker != null) {
//				animMarker();
                m_myHandler.sendEmptyMessage(4);
            }
        }

        if (macList.size() != 0) {
            macList.clear();
        }

    }

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
//                centerMarker.setIcon(successDescripter);
//                centerMarker.setIcon(BitmapDescriptorFactory.fromView(View.inflate(context, R.layout.marker_info_layout, null)));

//                View view = View.inflate(context, R.layout.marker_info_layout, null);
//                MarkerOptions centerMarkerOption = new MarkerOptions().position(myLocation).icon(BitmapDescriptorFactory.fromView(view));
            }
        });
        animator.start();
    }

    private void setMovingMarker() {
        if (isMovingMarker)
            return;

        isMovingMarker = true;
        centerMarker.setPositionByPixels(mapView.getWidth() / 2, mapView.getHeight() / 2);
//        centerMarker.setIcon(successDescripter);
//        centerMarker.setIcon(BitmapDescriptorFactory.fromView(View.inflate(context, R.layout.marker_info_layout, null)));

//        View view = View.inflate(context, R.layout.marker_info_layout, null);
//        MarkerOptions centerMarkerOption = new MarkerOptions().position(myLocation) .icon(BitmapDescriptorFactory.fromView(view));
    }

    public void onCameraChange(CameraPosition cameraPosition) {
        if (centerMarker != null) {
            setMovingMarker();
        }
    }


    private void initView() {
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        loadingDialog2 = new LoadingDialog(context);
        loadingDialog2.setCancelable(false);
        loadingDialog2.setCanceledOnTouchOutside(false);

        lockLoading = new LoadingDialog2(context);
        lockLoading.setCancelable(false);
        lockLoading.setCanceledOnTouchOutside(false);

//        loadingDialog1 = new LoadingDialog(context);
//        loadingDialog1.setCancelable(false);
//        loadingDialog1.setCanceledOnTouchOutside(false);
//
//        loadingDialog2 = new LoadingDialog(context);
//        loadingDialog2.setCancelable(false);
//        loadingDialog2.setCanceledOnTouchOutside(false);


        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("温馨提示").setMessage("还车须至校内地图红色区域，或打开手机GPS并重启软件再试")
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        customDialog3 = customBuilder.create();

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("温馨提示").setMessage("还车须至校内地图红色区域")
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        customDialog4 = customBuilder.create();

//        dialog = new Dialog(context, R.style.Theme_AppCompat_Dialog);
//        View dialogView = LayoutInflater.from(context).inflate(R.layout.ui_frist_view, null);
//        dialog.setContentView(dialogView);
//        dialog.setCanceledOnTouchOutside(false);

//        title = (TextView) activity.findViewById(R.id.mainUI_title);
//        leftBtn = (ImageView) activity.findViewById(R.id.mainUI_leftBtn);
//        rightBtn = (ImageView) activity.findViewById(R.id.mainUI_rightBtn);
        marquee = (TextView) activity.findViewById(R.id.mainUI_marquee);
        myCommissionLayout =  (LinearLayout) activity.findViewById(R.id.personUI_bottom_billing_myCommissionLayout);
        marqueeLayout = (LinearLayout)activity.findViewById(R.id.mainUI_marqueeLayout);
//        myLocationLayout =  (LinearLayout) activity.findViewById(R.id.mainUI_myLocationLayout);
//        linkLayout = (LinearLayout) activity.findViewById(R.id.mainUI_linkServiceLayout);
//        scanLock = (LinearLayout) activity.findViewById(R.id.mainUI_scanCode_lock);
//        linkBtn = (ImageView) activity.findViewById(R.id.mainUI_linkService_btn);
//        rl_authBtn = activity.findViewById(R.id.rl_authBtn);
//        tv_authBtn = activity.findViewById(R.id.tv_authBtn);
//        cartBtn = (Button)activity.findViewById(R.id.mainUI_cartBtn);
//        rechargeBtn = (Button)activity.findViewById(R.id.mainUI_rechargeBtn);
//        refreshLayout = (LinearLayout) activity.findViewById(R.id.mainUI_refreshLayout);
//        slideLayout = (LinearLayout)activity.findViewById(R.id.mainUI_slideLayout);
//        closeBtn = (ImageView)dialogView.findViewById(R.id.ui_fristView_closeBtn);

//        ImageView iv_myLocation =  activity.findViewById(R.id.mainUI_myLocation7);
//        Glide.with(context).load(R.drawable.loading_large).crossFade().into(iv_myLocation);

        ArrayList<BitmapDescriptor> iconList = new ArrayList<>();
        iconList.add(BitmapDescriptorFactory.fromView(View.inflate(context, R.layout.marker_info_layout1, null)));
        iconList.add(BitmapDescriptorFactory.fromView(View.inflate(context, R.layout.marker_info_layout2, null)));
        iconList.add(BitmapDescriptorFactory.fromView(View.inflate(context, R.layout.marker_info_layout3, null)));

        //title一定要设，不然可能出现marker不显示
        centerMarkerOptionLoading = new MarkerOptions();
        centerMarkerOptionLoading.icons(iconList).period(2);
//        centerMarkerOption.position(myLocation).icons(iconList).period(2);

//        lockLoading = new LoadingDialog2(context);
//        lockLoading.setCancelable(false);
//        lockLoading.setCanceledOnTouchOutside(false);



        Log.e("BF===initView", "==="+aMap);

        if(aMap==null){
            aMap = mapView.getMap();
//            setUpMap();
        }
//        else{
//            aMap.clear();
//            aMap.setLocationSource(this);
//            aMap.setMyLocationEnabled(true);
//            aMap = mapView.getMap();
//            setUpMap();
//        }

//        aMap.setMapType(AMap.MAP_TYPE_NAVI);
//        aMap.getUiSettings().setZoomControlsEnabled(false);
//        aMap.getUiSettings().setMyLocationButtonEnabled(false);
//        aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);// 设置地图logo显示在右下方
//        aMap.getUiSettings().setLogoBottomMargin(-100);
//
//        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(18f);// 设置缩放监听
//        aMap.moveCamera(cameraUpdate);
//        successDescripter = BitmapDescriptorFactory.fromResource(R.drawable.icon_usecarnow_position_succeed);
        successDescripter = BitmapDescriptorFactory.fromResource(R.drawable.icon_usecarnow_position_succeed);
        freeDescripter = BitmapDescriptorFactory.fromResource(R.drawable.free_icon);
        bikeDescripter = BitmapDescriptorFactory.fromResource(R.drawable.bike_icon);

        marker_park_Option = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.park_icon));
        marker_tip_Option = new MarkerOptions().icon(BitmapDescriptorFactory.fromView(View.inflate(context, R.layout.marker_tip_layout, null)));
        marker_tip_Option2 = new MarkerOptions().icon(BitmapDescriptorFactory.fromView(View.inflate(context, R.layout.marker_tip_layout2, null)));

        aMap.setOnMapTouchListener(this);
//        aMap.setOnMapClickListener(this);
        setUpLocationStyle();

//        leftBtn.setOnClickListener(this);
//        rightBtn.setOnClickListener(this);

        myCommissionLayout.setOnClickListener(this);
        marqueeLayout.setOnClickListener(this);
//        myLocationLayout.setOnClickListener(this);
//        linkLayout.setOnClickListener(this);
//        scanLock.setOnClickListener(this);
//        linkBtn.setOnClickListener(this);
//        rl_authBtn.setOnClickListener(this);
//        rechargeBtn.setOnClickListener(this);
//        refreshLayout.setOnClickListener(this);
//        closeBtn.setOnClickListener(myOnClickLister);

//        cartBtn.setOnClickListener(this);
//        slideLayout.setOnClickListener(this);

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

    }


    @Override
    public void onResume() {
        isForeground = true;
        super.onResume();


        Log.e("b===onResume", carmodel_id+"==="+type+"==="+SharedPreferencesUrls.getInstance().getString("iscert", ""));



        if(carmodel_id==2){
            MainFragment.getInstance().changeTab(1);
        }


        tz = 0;


//        if(mapView!=null){
//            mapView.onResume();
//        }

//        if (aMap != null) {
//            setUpMap();
//        }

        context = getContext();


//        if (1 == 1) {
//            return;
//        }


        if (flag == 1) {
            flag = 0;
            return;
        }

        uid = SharedPreferencesUrls.getInstance().getString("uid", "");
        access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");

//        m_nowMac = SharedPreferencesUrls.getInstance().getString("m_nowMac", "");
//        oid = SharedPreferencesUrls.getInstance().getString("oid", "");
//        osn = SharedPreferencesUrls.getInstance().getString("osn", "");
//        type = SharedPreferencesUrls.getInstance().getString("type", "");

        ToastUtil.showMessage(context, ">>>" + osn + ">>>" + type + ">>>main===onResume===" + SharedPreferencesUrls.getInstance().getString("iscert", "") + ">>>" + m_nowMac);


//        closeBroadcast();
//        try {
//            registerReceiver(Config.initFilter());
//            GlobalParameterUtils.getInstance().setLockType(LockType.MTS);
//        } catch (Exception e) {
//            ToastUtil.showMessage(this, "eee====" + e);
//        }

//        if(!isHidden && !"4".equals(type) && !"7".equals(type)){
//            getFeedbackStatus();
//        }

    }



    protected Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 33:
                    ImageView iv_myLocation =  activity.findViewById(R.id.mainUI_myLocation7);
                    AnimationDrawable animDrawable = (AnimationDrawable) iv_myLocation.getBackground();
                    iv_myLocation.setBackground(animDrawable);

                    if (animDrawable != null && !animDrawable.isRunning()) {
                        animDrawable.start();
                    }
                    break;

                case 0:
                    if (!BaseApplication.getInstance().getIBLE().isEnable()){

                        break;
                    }
                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, BikeFragment.this);
                    break;
                case 1:
                    break;
                case 2:
                    if (lockLoading != null && lockLoading.isShowing()){
                        lockLoading.dismiss();
                    }

                    stopXB();

                    Log.e("main===", "type==="+type);

                    if("3".equals(type)){
                        endBtn3();
                    }else{
                        endBtn();
                    }

                    break;
                case 3:
                    if (lockLoading != null && lockLoading.isShowing()){
                        lockLoading.dismiss();
                    }
                    stopXB();

                    if(macList.size()>0  || isContainsList.contains(true)){
                        if (lockLoading != null && !lockLoading.isShowing()){
                            lockLoading.setTitle("正在连接");
                            lockLoading.show();
                        }

                        isConnect = false;
                        m_myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (lockLoading != null && lockLoading.isShowing()){
                                    lockLoading.dismiss();
                                }

                                if(!isConnect){
                                    if("3".equals(type)){
                                        if(first3){
                                            first3 = false;
                                            customDialog4.show();
                                        }else{
                                            carClose();
                                        }
                                    }else{
                                        if (!BaseApplication.getInstance().getIBLE().getConnectStatus()){
                                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                            customBuilder.setTitle("连接失败").setMessage("关锁后，请离车1米内重试或在右上角提交")
                                                    .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                        }
                                                    });
                                            customBuilder.create().show();
                                        }
                                    }
                                }

                            }
                        }, 10 * 1000);

                        connect();
                    }else{
                        if("3".equals(type)){
                            customDialog4.show();
                        }else{
                            customDialog3.show();
                        }
                    }

                    break;
                case 4:
                    animMarker();
                    break;

                case 0x99://搜索超时
                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, BikeFragment.this);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
//        aMap.setLoadOfflineData(true);

//        String styleName = "style.data";
//        FileOutputStream outputStream = null;
//        InputStream inputStream = null;
//        String filePath = null;
//        try {
//            inputStream = context.getAssets().open(styleName);
//            byte[] b = new byte[inputStream.available()];
//            inputStream.read(b);
//
//            filePath = context.getFilesDir().getAbsolutePath();
//            File file = new File(filePath + "/" + styleName);
//
//            Log.e("setUpMap===0", b.length+"==="+context.getAssets()+"==="+file+"==="+file.exists());
//
//            if (file.exists()) {
//                file.delete();
//            }
//            file.createNewFile();
//            outputStream = new FileOutputStream(file);
//            outputStream.write(b);
//
//
//            Log.e("setUpMap===1", context.getAssets()+"==="+file+"==="+file.exists());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (inputStream != null)
//                    inputStream.close();
//
//                if (outputStream != null)
//                    outputStream.close();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
////        aMap.setCustomMapStylePath(filePath + "/" + styleName);
////        aMap.setMapCustomEnable(true);
//
//        aMap.setCustomMapStyle(
//                new com.amap.api.maps.model.CustomMapStyleOptions()
//                        .setEnable(true)
////                        .setStyleDataPath(filePath)
////                      .setStyleExtraPath(filePath)
//                        .setStyleId("1b319281566f48d9ce57449b30be3b6e")//官网控制台-自定义样式 获取
//
////                      .setStyleExtraPath("/mnt/sdcard/amap/style_extra.data")
////                      .setStyleTexturePath("/mnt/sdcard/amap/textures.zip")
//        );
//
//        Log.e("setUpMap===2", context.getAssets()+"===");
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
//	    super.activate(listener);

        mListener = listener;

        Log.e("main===b", isContainsList.contains(true) + "===listener===" + mlocationClient);


        if (mlocationClient != null) {

            mlocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(2 * 1000);
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();

//			mListener.onLocationChanged(amapLocation);
        }

//		if (mListener != null) {
//			mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
//		}

        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(context);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);

            // 关闭缓存机制
            mLocationOption.setLocationCacheEnable(false);
            //设置是否只定位一次,默认为false
            mLocationOption.setOnceLocation(false);
            //设置是否强制刷新WIFI，默认为强制刷新
            mLocationOption.setWifiActiveScan(true);
            //设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(false);

            mLocationOption.setInterval(2 * 1000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        change = true;

        if(isHidden) return;

        if (mListener != null && amapLocation != null) {

            if ((referLatitude == amapLocation.getLatitude()) && (referLongitude == amapLocation.getLongitude())) return;

            Log.e("main===Changed", isContainsList.contains(true) + "》》》" + near + "===" + macList.size() + "===" + amapLocation.getLatitude() + "===" + amapLocation.getLongitude());
            ToastUtil.showMessage(context, isContainsList.contains(true) + "》》》" + near + "===" + amapLocation.getLatitude() + "===" + amapLocation.getLongitude());

            if (amapLocation != null && amapLocation.getErrorCode() == 0) {

                if (0.0 != amapLocation.getLatitude() && 0.0 != amapLocation.getLongitude()) {
                    String latitude = SharedPreferencesUrls.getInstance().getString("biking_latitude", "");
                    String longitude = SharedPreferencesUrls.getInstance().getString("biking_longitude", "");
//                    if (latitude != null && !"".equals(latitude) && longitude != null && !"".equals(longitude)) {
//                        if (AMapUtils.calculateLineDistance(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)
//                        ), new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())) > 10) {
//                            SharedPreferencesUrls.getInstance().putString("biking_latitude", "" + amapLocation.getLatitude());
//                            SharedPreferencesUrls.getInstance().putString("biking_longitude", "" + amapLocation.getLongitude());
//                            addMaplocation(amapLocation.getLatitude(), amapLocation.getLongitude());
//                        }
//                    }
                    if (mListener != null) {
                        mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                    }

                    referLatitude = amapLocation.getLatitude();
                    referLongitude = amapLocation.getLongitude();
                    myLocation = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());

                    Log.e("main===Changed>>>0", referLatitude+"==="+referLongitude+"》》》"+mFirstFix);

                    if (mFirstFix) {

                        Log.e("main===Changed>>>1", "》》》");

                        mFirstFix = false;
//                        schoolRange();
                        initNearby(amapLocation.getLatitude(), amapLocation.getLongitude());
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));

                        accuracy = amapLocation.getAccuracy();

                        addChooseMarker();
                        addCircle(myLocation, amapLocation.getAccuracy());
                    } else {
//                        centerMarker.remove();
//                        mCircle.remove();
//                        centerMarker = null;
//                        mCircle = null;

                        if (!isContainsList.isEmpty() || 0 != isContainsList.size()) {
                            isContainsList.clear();
                        }
                        for (int i = 0; i < pOptions.size(); i++) {
                            isContainsList.add(pOptions.get(i).contains(myLocation));
                        }
                    }

                    ToastUtil.showMessage(context, isContainsList.contains(true) + "======" + near);



//                    if(!SharedPreferencesUrls.getInstance().getBoolean("switcher",false)){
//                        if (start) {
//                            start = false;
//
//                            if (mlocationClient != null) {
//                                mlocationClient.setLocationListener(BikeFragment.this);
//                                mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
//                                mLocationOption.setInterval(2 * 1000);
//                                mlocationClient.setLocationOption(mLocationOption);
//                                mlocationClient.startLocation();
//                            }
//
//                            macList2 = new ArrayList<> (macList);
//                            BaseApplication.getInstance().getIBLE().getLockStatus();
//                        } else {
//
//                            if (isContainsList.contains(true) && near==1){
//                                macList2 = new ArrayList<> (macList);
//
//                                ToastUtil.showMessage(context,"biking---》》》里");
//                                BaseApplication.getInstance().getIBLE().getLockStatus();
//                            }else if (!isContainsList.contains(true) && near==0){
//                                macList2 = new ArrayList<> (macList);
//
//                                ToastUtil.showMessage(context,"biking---》》》外");
//                                BaseApplication.getInstance().getIBLE().getLockStatus();
//                            }
//
//                        }
//                    }


                    if ((isContainsList.contains(true) || macList.size() > 0) && !"1".equals(type)) {
                        near = 0;
                    } else {
                        near = 1;
                    }

                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开位置权限！")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    activity.finish();
                                }
                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
                        }
                    });
                    customBuilder.create().show();
                }

                //保存经纬度到本地
                SharedPreferencesUrls.getInstance().putString("latitude", "" + amapLocation.getLatitude());
                SharedPreferencesUrls.getInstance().putString("longitude", "" + amapLocation.getLongitude());
            }


        }
    }

    @Override
    public void onStart() {
        super.onStart();
        screen = true;
        start = true;

        Log.e("main===onStart_b", "===="+mlocationClient);

        if (mlocationClient != null) {
            mlocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(5 * 1000);
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }

//        if (!"".equals(m_nowMac)) {
//            mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
//                @Override
//                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//                    k++;
//                    Log.e("main===LeScan", device + "====" + rssi + "====" + k);
//
//                    if (!macList.contains(""+device)){
//                        macList.add(""+device);
//                    }
//
//                }
//            };
//        }
    }

    @Override
    public void onPause() {
        isForeground = false;
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        if (lockLoading != null && lockLoading.isShowing()) {
            lockLoading.dismiss();
        }
        super.onPause();

//		if(mlocationClient!=null) {
//			mlocationClient.stopLocation();//停止定位
//		}


//		if(mapView!=null){
//            mapView.onPause();
//        }

//		deactivate();
//		mFirstFix = false;
        tz = 0;

        ToastUtil.showMessage(context, "main====onPause");
        Log.e("main===", "main====onPause");

//		closeBroadcast();

    }

    @Override
    public void onStop() {
        super.onStop();
        screen = false;
        change = false;

        Log.e("bikeFrag===", "===onStop");

//		closeBroadcast();
//
//		if(mlocationClient!=null) {
//			mlocationClient.stopLocation(); // 停止定位
//		}

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if(mapView!=null){
            mapView.onDestroy();
        }

        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }

        ToastUtil.showMessage(context, "main===onDestroy");



        deactivate();

        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }


//		if (broadcastReceiver2 != null) {
//			unregisterReceiver(broadcastReceiver2);
//			broadcastReceiver2 = null;
//		}

        closeBroadcast();

    }

    private void startXB() {
        if (mBluetoothAdapter == null) {
            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (mBluetoothAdapter == null) {
            ToastUtil.showMessageApp(context, "获取蓝牙失败");
            activity.finish();
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            flag = 1;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 188);
        }else{
            if (macList.size() != 0) {
                macList.clear();
            }
            UUID[] uuids = {Config.xinbiaoUUID};

            Log.e("main===startXB",mBluetoothAdapter+"==="+mLeScanCallback);
            mBluetoothAdapter.startLeScan(uuids, mLeScanCallback);
        }
    }

//    protected void handleReceiver(final Context context, final Intent intent) {
//
//        m_myHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                // 广播处理
//                if (intent == null) {
//                    return;
//                }
//
//                String action = intent.getAction();
//                String data = intent.getStringExtra("data");
//
//                Log.e("main===", "handleReceiver===" + action + "===" + data);
//
//                switch (action) {
//                    case BluetoothAdapter.ACTION_STATE_CHANGED:
//
//                        ToastUtil.showMessage(context, "main===蓝牙CHANGED");
//                        int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
//                        switch (blueState) {
//                            case BluetoothAdapter.STATE_TURNING_ON:
//                                break;
//
//                            case BluetoothAdapter.STATE_ON:
//                                break;
//
//                            case BluetoothAdapter.STATE_TURNING_OFF:
//                                ToastUtil.showMessage(context, "main===TURNING_OFF");
//                                break;
//
//                            case BluetoothAdapter.STATE_OFF:
//                                ToastUtil.showMessage(context, "main===OFF");
//                                break;
//                        }
//
//                        break;
//                    case Config.TOKEN_ACTION:
//                        isConnect = true;
//
//                        if (customDialog3 != null && customDialog3.isShowing()) {
//                            customDialog3.dismiss();
//                        }
//                        if (customDialog4 != null && customDialog4.isShowing()) {
//                            customDialog4.dismiss();
//                        }
//
//
//                        if (mlocationClient != null) {
//                            mlocationClient.startLocation();//停止定位
//                        }
//
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                BaseApplication.getInstance().getIBLE().getBattery();
//                            }
//                        }, 500);
//                        if (null != lockLoading && lockLoading.isShowing()) {
//                            lockLoading.dismiss();
//                        }
////					isStop = true;
//                        ToastUtil.showMessageApp(context, "设备连接成功");
//
//
//                        break;
//                    case Config.BATTERY_ACTION:
//                        if (isConnect) {
//                        }
//
//                        macList2 = new ArrayList<> (macList);
//
//                        Log.e("main===", "main===BATTERY_ACTION==="+macList2+"==="+type);
//                        BaseApplication.getInstance().getIBLE().getLockStatus();
//
//                        break;
//                    case Config.OPEN_ACTION:
//                        ToastUtil.showMessage(context, "####===3");
//                        break;
//                    case Config.CLOSE_ACTION:
//                        ToastUtil.showMessage(context, "####===4");
//                        break;
//                    case Config.LOCK_STATUS_ACTION:
//
//                        if (loadingDialog != null && loadingDialog.isShowing()) {
//                            loadingDialog.dismiss();
//                        }
//                        if (lockLoading != null && lockLoading.isShowing()) {
//                            lockLoading.dismiss();
//                        }
//
//                        if (TextUtils.isEmpty(data)) {
//                            ToastUtil.showMessageApp(context, "锁已关闭");
//                            Log.e("main===", "main===锁已关闭==="+macList2+"==="+type+"==="+first3);
//                            //锁已关闭
//
//                            if (mBluetoothAdapter == null) {
//                                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
//                                mBluetoothAdapter = bluetoothManager.getAdapter();
//                            }
//
//                            if (mBluetoothAdapter == null) {
//                                ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                                activity.finish();
//                                return;
//                            }
//
//                            if (!mBluetoothAdapter.isEnabled()) {
//                                flag = 1;
//                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                                startActivityForResult(enableBtIntent, 188);
//                            }else{
//
//                                if("3".equals(type)){
//                                    if (!isContainsList.contains(true) && macList2.size() <= 0) {
//                                        customDialog4.show();
//
//                                    } else {
//                                        submit(uid, access_token);
//                                    }
//                                }else{
//                                    if (!isContainsList.contains(true) && macList2.size() <= 0) {
//                                        customDialog3.show();
//
//                                    } else {
//                                        submit(uid, access_token);
//                                    }
//                                }
//
//                            }
//                        } else {
//                            //锁已开启
//                            ToastUtil.showMessageApp(context, "您还未上锁，请给车上锁后还车");
//                        }
//                        break;
//                    case Config.LOCK_RESULT:
//
//                        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
//                        boolean screenOn = pm.isScreenOn();
//                        if (!screenOn) {
//                            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
//                            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
//                            wl.acquire();
////					wl.acquire(10000); // 点亮屏幕
//                            wl.release(); // 释放
//                        }
//
//                        // 屏幕解锁
//                        KeyguardManager keyguardManager = (KeyguardManager) activity.getSystemService(KEYGUARD_SERVICE);
//                        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("");
////				KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("unLock");
//                        // 屏幕锁定
////				keyguardLock.reenableKeyguard();
//                        keyguardLock.disableKeyguard(); // 解锁
//
//                        if (mlocationClient != null) {
//                            mlocationClient.startLocation();
//                        }
//
//
//                        if (loadingDialog != null && loadingDialog.isShowing()) {
//                            loadingDialog.dismiss();
//                        }
//                        if (lockLoading != null && lockLoading.isShowing()) {
//                            lockLoading.dismiss();
//                        }
//
//                        ToastUtil.showMessageApp(context, "恭喜您，您已成功上锁");
//                        Log.e("main===", "main===恭喜您，您已成功上锁");
//
////                //自动还车
////                if(SharedPreferencesUrls.getInstance().getBoolean("switcher", false)) break;
////
////                startXB();
////
////                if (lockLoading != null && !lockLoading.isShowing()){
////                    lockLoading.setTitle("还车点确认中");
////                    lockLoading.show();
////                }
////
////                new Thread(new Runnable() {
////                    @Override
////                    public void run() {
////                        try {
////                            int n=0;
////                            while(macList.size() == 0){
////
////                                Thread.sleep(1000);
////                                n++;
////
////                                if(n>=6) break;
////
////                            }
////                        } catch (InterruptedException e) {
////                            e.printStackTrace();
////                        }
////
////                        m_myHandler.sendEmptyMessage(2);
////
////                    }
////                }).start();
//
//                        break;
//                }
//            }
//        });
//
//    }

    @Override
    public void onClick(View view) {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (view.getId()){
            case R.id.mainUI_marqueeLayout:

                break;
            case R.id.personUI_bottom_billing_myCommissionLayout:
//                Intent intent = new Intent(context,InviteCodeActivity.class);
                Intent intent = new Intent(context, CouponActivity.class);
                intent.putExtra("isBack",true);
                context.startActivity(intent);
                break;

//            case R.id.mainUI_myLocationLayout:
//                if (myLocation != null) {
//                    CameraUpdate update = CameraUpdateFactory.changeLatLng(myLocation);
//                    aMap.animateCamera(update);
//                }
//
////                m_myHandler.sendEmptyMessage(33);
//
//                break;
            default:
                break;
        }
    }

    private String parseAdvData(int rssi, byte[] scanRecord) {
        byte[] bytes = ParseLeAdvData.adv_report_parse(ParseLeAdvData.BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA, scanRecord);
        if (bytes[0] == 0x01 && bytes[1] == 0x02) {
            return bytes2hex03(bytes);
        }
        return "";
    }

    private void stopXB() {
        if (!"1".equals(type)) {
            if (mLeScanCallback != null && mBluetoothAdapter != null) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
//				mLeScanCallback = null;
            }
        }
    }

    protected void submit(String uid, String access_token){

//        Log.e("main===submit",macList2.size()+"==="+SharedPreferencesUrls.getInstance().getBoolean("isStop",true)+"==="+uid+"==="+access_token+"==="+oid+"==="+referLatitude+"==="+referLongitude);
//
//        RequestParams params = new RequestParams();
//        params.put("uid", uid);
//        params.put("access_token", access_token);
//        params.put("oid", oid);
//        params.put("latitude", referLatitude);
//        params.put("longitude", referLongitude);
//        if (macList2.size() > 0){
//            params.put("xinbiao",macList2.get(0));
//        }
//        HttpHelper.post(context, Urls.backBikescan, params, new TextHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                onStartCommon("正在提交");
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
//                        Log.e("base===","结束用车:"+responseString);
//                        try {
//                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                            if (result.getFlag().equals("Success")) {
//
//                                SharedPreferencesUrls.getInstance().putString("type","");
//                                SharedPreferencesUrls.getInstance().putString("m_nowMac","");
//                                SharedPreferencesUrls.getInstance().putString("oid","");
//                                SharedPreferencesUrls.getInstance().putString("osn","");
//                                SharedPreferencesUrls.getInstance().putString("type","");
//                                SharedPreferencesUrls.getInstance().putBoolean("isStop",true);
//                                SharedPreferencesUrls.getInstance().putBoolean("switcher", false);
//                                SharedPreferencesUrls.getInstance().putString("biking_latitude","");
//                                SharedPreferencesUrls.getInstance().putString("biking_longitude","");
//
//                                if (loadingDialog != null && loadingDialog.isShowing()){
//                                    loadingDialog.dismiss();
//                                }
//                                if (customDialog3 != null && customDialog3.isShowing()){
//                                    customDialog3.dismiss();
//                                }
//
//                                if ("1".equals(result.getData())){
//                                    ToastUtil.showMessageApp(context, result.getMsg());
//                                    if ("已为您免单,欢迎反馈问题".equals(result.getMsg())){
//
//                                        ToastUtil.showMessage(context,"context==="+context);
//
////								if(context instanceof CurRoadStartActivity){
////									CurRoadStartActivity.isEnd = true;
////									CurRoadStartActivity.instance.finish();
////								}
//
//                                        tz = 1;
//                                        UIHelper.goToAct(context, FeedbackActivity.class);
////								UIHelper.goToAct(context, Main2Activity.class);
////                              scrollToFinishActivity();
//
//                                        Log.e("base===","base===Feedback");
//                                    }else {
//                                        tz = 2;
//                                        Intent intent = new Intent(context, HistoryRoadDetailActivity.class);
//                                        intent.putExtra("oid",oid);
//                                        startActivity(intent);
//
//                                        Log.e("base===","base===HistoryRoadDetail==="+oid);
//                                    }
//                                }else {
//                                    ToastUtil.showMessageApp(context,"恭喜您,还车成功,请支付!");
//
//                                    tz = 3;
//                                    UIHelper.goToAct(context, CurRoadBikedActivity.class);
//
////							Intent intent = new Intent(getApplicationContext(),  CurRoadBikedActivity.class);
////							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
////							startActivity(intent);
//
//                                    Log.e("base===","base===CurRoadBiked");
//                                }
////                        scrollToFinishActivity();
//
//                            }else {
//                                ToastUtil.showMessageApp(context, result.getMsg());
//                            }
//                        }catch (Exception e){
//
//                        }
//                        if (loadingDialog != null && loadingDialog.isShowing()){
//                            loadingDialog.dismiss();
//                        }
//                        if (customDialog3 != null && customDialog3.isShowing()){
//                            customDialog3.dismiss();
//                        }
//                    }
//                });
//
//            }
//        });
    }

    public void carClose(){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);

        HttpHelper.post(context, Urls.carClose, params, new TextHttpResponseHandler() {
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
                                ToastUtil.showMessage(context,"数据更新成功");

                                Log.e("main===", "carClose===="+result.getData());

                                if ("0".equals(result.getData())){
                                    submit(uid, access_token);
                                } else {
                                    ToastUtil.showMessageApp(context,"您还未上锁，请给车上锁后还车");
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

    public void endBtn(){
        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");

        Log.e("main===", "endBtn1===="+uid+"===="+access_token);

        if (access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context,LoginActivity.class);
        }else {
            ToastUtil.showMessage(context,uid+"==="+access_token);
            ToastUtil.showMessage(context,macList+"==="+isContainsList);
            ToastUtil.showMessage(context,macList.size()+"==="+isContainsList.contains(true));

            Log.e("main===", "endBtn2===="+uid+"===="+access_token);

            if (macList.size() > 0 && !"1".equals(type)){
                if (!TextUtils.isEmpty(m_nowMac)) {
                    //蓝牙锁
                    if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                        ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                        activity.finish();
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

                        isConnect = false;
                        m_myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();

                                    if(!isConnect){
                                        ToastUtil.showMessage(context, "连接失败，请重启手机蓝牙后再结束用车");
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

                        isConnect = false;
                        m_myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (lockLoading != null && lockLoading.isShowing()){
                                    lockLoading.dismiss();
                                }

                                if(!isConnect){
                                    if (!BaseApplication.getInstance().getIBLE().getConnectStatus()){
                                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                        customBuilder.setTitle("连接失败").setMessage("关锁后，请离车1米内重试或在右上角提交")
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

                        connect();
                    }
                }

                return;
            }

            if(screen){
                if (isContainsList.contains(true)){
                    if ("1".equals(type)){
                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                        customBuilder.setTitle("温馨提示").setMessage("还车必须到校内关锁并拨乱数字密码，距车一米内在APP点击结束!")
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                submit(uid,access_token);
                            }
                        });
                        customBuilder.create().show();
                    }else {
                        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                            ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                            activity.finish();
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

                            isConnect = false;
                            m_myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (loadingDialog != null && loadingDialog.isShowing()){
                                        loadingDialog.dismiss();

                                        if(!isConnect){
                                            ToastUtil.showMessage(context, "连接失败，请重启手机蓝牙后再结束用车");
                                        }

                                    }
                                }
                            }, 10 * 1000);

                            Log.e("main===", "endBtn4===="+uid+"===="+access_token);

                            macList2 = new ArrayList<> (macList);
                            BaseApplication.getInstance().getIBLE().getLockStatus();
                        }else {
                            if (lockLoading != null && !lockLoading.isShowing()){
                                lockLoading.setTitle("正在连接");
                                lockLoading.show();
                            }

                            ToastUtil.showMessage(context, "连接失败，请重启手机蓝牙后再结束用车");

                            isConnect = false;
                            m_myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (lockLoading != null && lockLoading.isShowing()){
                                        lockLoading.dismiss();
                                    }

                                    if(!isConnect){
                                        if (!BaseApplication.getInstance().getIBLE().getConnectStatus()){
                                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                            customBuilder.setTitle("连接失败").setMessage("关锁后，请离车1米内重试或在右上角提交")
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

                            connect();

                        }
                    }
                }else {
                    customDialog3.show();
                }
            }
        }
    }

    public void endBtn3(){
        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            Log.e("biking===endBtn3",macList.size()+"==="+type);

            if (macList.size() > 0){
                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                    activity.finish();
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

                    isConnect = false;
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();

                                if(!isConnect){
                                    if(first3){
                                        first3 = false;
                                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                        customBuilder.setTitle("连接失败").setMessage("请关闭手机蓝牙后再试")
                                                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        customBuilder.create().show();
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

                    isConnect = false;
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (lockLoading != null && lockLoading.isShowing()){
                                lockLoading.dismiss();
                            }

                            if(!isConnect){
                                if(first3){
                                    first3 = false;
                                    customDialog4.show();
                                }else{
                                    carClose();
                                }
                            }
                        }
                    }, 10 * 1000);

                    connect();
                }

                return;
            }

            if(screen){
                if (isContainsList.contains(true)){
                    if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                        ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                        activity.finish();
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

                        isConnect = false;
                        m_myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();

                                    if(!isConnect){
                                        if(first3){
                                            first3 = false;
                                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                            customBuilder.setTitle("连接失败").setMessage("请关闭手机蓝牙后再试")
                                                    .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                        }
                                                    });
                                            customBuilder.create().show();
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

                        isConnect = false;
                        m_myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (lockLoading != null && lockLoading.isShowing()){
                                    lockLoading.dismiss();
                                }

                                if(!isConnect){
                                    if(first3){
                                        first3 = false;
                                        customDialog4.show();
                                    }else{
                                        carClose();
                                    }
                                }
                            }
                        }, 10 * 1000);

                        connect();
                    }
                }else {
                    customDialog4.show();
                }
            }
        }
    }

    private void closeBroadcast() {
        try {
            stopXB();

            first = true;
            macList.clear();
            macList2.clear();

//            if (internalReceiver != null) {
//                activity.unregisterReceiver(internalReceiver);
//                internalReceiver = null;
//            }

            ToastUtil.showMessage(context, "main====closeBroadcast===");
            Log.e("main====", "closeBroadcast===");

        } catch (Exception e) {
            ToastUtil.showMessage(context, "eee====" + e);
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, Intent data) {

        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showMessage(context, resultCode + "====" + requestCode);

                if (resultCode == RESULT_OK) {
                    switch (requestCode) {
                        case 188:

//                    mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
//                        @Override
//                        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//                            k++;
//                            Log.e("main===LeScan", device + "====" + rssi + "====" + k);
//
//                            if (!macList.contains(""+device)){
//                                macList.add(""+device);
//                            }
//
//                        }
//                    };

                            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
                            mBluetoothAdapter = bluetoothManager.getAdapter();

                            if (mBluetoothAdapter == null) {
                                ToastUtil.showMessageApp(context, "获取蓝牙失败");
                                activity.finish();
                                return;
                            }
                            if (!mBluetoothAdapter.isEnabled()) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, 188);
                            }else{
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
//
//                                        Thread.sleep(1000);
//                                        n++;
//
//                                        Log.e("main===", "n====" + n);
//
//                                        if(n>=6) break;
//
//                                    }
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//
//                                m_myHandler.sendEmptyMessage(3);
//
//                            }
//                        }).start();
                            }


                            break;

                        default:
                            break;

                    }
                } else {
                    switch (requestCode) {
//                        case PRIVATE_CODE:
//                            openGPSSettings();
//                            break;

                        case 188:
                            ToastUtil.showMessageApp(context, "需要打开蓝牙");
                            AppManager.getAppManager().AppExit(context);
                            break;
                        default:
                            break;
                    }
                }
            }
        });

    }

    private void addChooseMarker() {
        // 加入自定义标签

        Log.e("addChooseMarker===0", "==="+centerMarker);

        if(centerMarker == null){

            View view = View.inflate(context, R.layout.marker_info_layout, null);
            tv_car_count = view.findViewById(R.id.tv_car_count);
            tv_car_count.setText((car_count>99?99:car_count)+"辆");
//            ImageView iv_marker = view.findViewById(R.id.iv_marker);
//            Glide.with(context).load(R.drawable.loading_large).crossFade().into(iv_marker);
            centerMarkerOption = new MarkerOptions().position(myLocation).icon(BitmapDescriptorFactory.fromView(view));

//            ImageView iv_myLocation =  activity.findViewById(R.id.mainUI_myLocation7);
//            Glide.with(context).load(R.drawable.loading_large).crossFade().into(iv_myLocation);

//            ArrayList<BitmapDescriptor> iconList = new ArrayList<>();
//            iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.compass1));
//            iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.compass2));
//            iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.compass3));
//
//            //title一定要设，不然可能出现marker不显示
//            MarkerOptions centerMarkerOption = new MarkerOptions();
//            centerMarkerOption.position(myLocation).icons(iconList).period(2);

            Log.e("addChooseMarker===", "==="+myLocation);

            centerMarker = aMap.addMarker(centerMarkerOption);

//            if(unauthorized_code==6){
//                centerMarker.setAlpha(0);
//            }else{
//                centerMarker.setAlpha(255);
//            }

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    CameraUpdate update = CameraUpdateFactory.changeLatLng(myLocation);
//                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(myLocation));
                    CameraUpdate update = CameraUpdateFactory.zoomTo(18f);
                    aMap.animateCamera(update, 1000, new AMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            aMap.setOnCameraChangeListener(BikeFragment.this);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
            }, 1000);
        }

    }

    private void addCircle(LatLng latlng, double radius) {
        if(mCircle == null){
            CircleOptions options = new CircleOptions();
            options.strokeWidth(1f);
            options.fillColor(FILL_COLOR);
            options.strokeColor(STROKE_COLOR);
            options.center(latlng);
            options.radius(radius);
            mCircle = aMap.addCircle(options);
        }
    }

    protected  void connect() {
//		BaseApplication.getInstance().getIBLE().resetBluetoothAdapter();

        BaseApplication.getInstance().getIBLE().stopScan();

        Log.e("main===","connect1==="+m_nowMac);

        m_myHandler.sendEmptyMessage(0x99);

        Log.e("main===","connect2==="+m_nowMac);

        BaseApplication.getInstance().getIBLE().startScan(new OnDeviceSearchListener() {
            @Override
            public void onScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {

                Log.e("main===","connect3==="+m_nowMac+"==="+device);

                if (device==null || TextUtils.isEmpty(device.getAddress()))return;


                if (m_nowMac.equalsIgnoreCase(device.getAddress())){
                    Log.e("main===","connect4===");
                    m_myHandler.removeMessages(0x99);

                    Log.e("main===","connect5===");
                    BaseApplication.getInstance().getIBLE().stopScan();

                    Log.e("main===","connect6===");
                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, BikeFragment.this);

                    Log.e("main===","connect7===");
                }
            }
        });
    }

//    @Override
//    public void onMapClick(LatLng point) {
//        Log.e("onMapClick===", ll_top.isShown()+"===" + routeOverLay+"===" + ll_top_navi);
//
//        if(!ll_top.isShown()){
//            routeOverLay.removeFromMap();
//
//            ll_top.setVisibility(View.VISIBLE);
//            ll_top_navi.setVisibility(View.GONE);
//        }
//    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        Log.e("main===onTouch", "===");

        if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                motionEvent.getAction() == MotionEvent.ACTION_CANCEL || motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE
                || motionEvent.getActionMasked() == MotionEvent.ACTION_POINTER_UP){
            isUp = true;
        }else {
            isUp = false;
        }
    }

//    @Override
//    public void onInitNaviFailure() { }
//
//    @Override
//    public void onInitNaviSuccess() {
//        Log.e("onInitNaviSuccess===", "===");
//    }
//
//    @Override
//    public void onCalculateRouteSuccess(int[] ints) {
//        if(isHidden) return;
//
//        Log.e("onCalculateRouteSuc=", "==="+routeOverLay);
//
////        routeOverlays.clear();
////        ways.clear();
//
//        if(routeOverLay != null){
//            routeOverLay.removeFromMap();
//        }
//
////        mAMapNavi.startNavi(NaviType.EMULATOR);
//
//        AMapNaviPath path = mAMapNavi.getNaviPath();
//
//        drawRoutes(-1, path);   // 单路径不需要进行路径选择，直接传入－1即可
////        showMarkInfo(path);
//    }
//
//    private void drawRoutes(int routeId, AMapNaviPath path) {
////        calculateSuccess = true;
//        aMap.moveCamera(CameraUpdateFactory.changeTilt(0));
//        //路径绘制图层
//        routeOverLay = new RouteOverLay(aMap, path, context);
//        routeOverLay.setTrafficLine(false);
//        routeOverLay.setStartPointBitmap(null);
//        routeOverLay.setEndPointBitmap(null);
//        routeOverLay.addToMap();
//
//
//        Log.e("drawRoutes===", "==="+path.getAllLength());
//
//        tv_navi_distance.setText(path.getAllLength()+"米");
//
////        routeOverlays.put(routeId, routeOverLay);
//    }
//
//    @Override
//    public void onStartNavi(int i) {}
//
//    @Override
//    public void onTrafficStatusUpdate() {}
//
//    @Override
//    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {}
//
//    @Override
//    public void onGetNavigationText(int i, String s) {}
//
//    @Override
//    public void onGetNavigationText(String s) {}
//
//    @Override
//    public void onEndEmulatorNavi() {}
//
//    @Override
//    public void onArriveDestination() {}
//
//    @Override
//    public void onCalculateRouteFailure(int i) {}
//
//    @Override
//    public void onReCalculateRouteForYaw() {}
//
//    @Override
//    public void onReCalculateRouteForTrafficJam() {}
//
//    @Override
//    public void onArrivedWayPoint(int i) {}
//
//    @Override
//    public void onGpsOpenStatus(boolean b) {}
//
//    @Override
//    public void onNaviInfoUpdate(NaviInfo naviInfo) {}
//
//    @Override
//    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {}
//
//    @Override
//    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {}
//
//    @Override
//    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {}
//
//    @Override
//    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {}
//
//    @Override
//    public void showCross(AMapNaviCross aMapNaviCross) {}
//
//    @Override
//    public void hideCross() {}
//
//    @Override
//    public void showModeCross(AMapModelCross aMapModelCross) {}
//
//    @Override
//    public void hideModeCross() {}
//
//    @Override
//    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {}
//
//    @Override
//    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {}
//
//    @Override
//    public void hideLaneInfo() {}
//
//    @Override
//    public void notifyParallelRoad(int i) {}
//
//    @Override
//    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {}
//
//    @Override
//    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {}
//
//    @Override
//    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {}
//
//    @Override
//    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {}
//
//    @Override
//    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {}
//
//    @Override
//    public void onPlayRing(int i) {}
//
//    @Override
//    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {}
//
//    @Override
//    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {}
//
//    @Override
//    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {}

    protected void getCurrentorder2(String uid, String access_token){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
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
                                if ("[]".equals(result.getData()) || 0 == result.getData().length()){
                                    SharedPreferencesUrls.getInstance().putBoolean("isStop",true);
//                                    cardCheck();  //TODO
                                }else {
                                    if (loadingDialog != null && loadingDialog.isShowing()){
                                        loadingDialog.dismiss();
                                    }
                                    CurRoadBikingBean bean = JSON.parseObject(result.getData(),CurRoadBikingBean.class);

                                    if ("1".equals(bean.getStatus())){
                                        SharedPreferencesUrls.getInstance().putBoolean("isStop",false);
                                        if (loadingDialog != null && loadingDialog.isShowing()){
                                            loadingDialog.dismiss();
                                        }
                                        closeBroadcast();
                                        deactivate();

                                        UIHelper.goToAct(context, CurRoadBikingActivity.class);
                                    }else {
                                        SharedPreferencesUrls.getInstance().putBoolean("isStop",true);
                                        if (loadingDialog != null && loadingDialog.isShowing()){
                                            loadingDialog.dismiss();
                                        }
                                        UIHelper.goToAct(context,CurRoadBikedActivity.class);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void setUpLocationStyle() {
        // 自定义系统定位蓝点
//		MyLocationStyle myLocationStyle = new MyLocationStyle();
//		myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));
//		myLocationStyle.strokeWidth(0);
//		myLocationStyle.strokeColor(R.color.main_theme_color);
//		myLocationStyle.radiusFillColor(Color.TRANSPARENT);
//		aMap.setMyLocationStyle(myLocationStyle);

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));
        myLocationStyle.radiusFillColor(android.R.color.transparent);
        myLocationStyle.strokeColor(android.R.color.transparent);
        aMap.setMyLocationStyle(myLocationStyle);
    }

    private void addMaplocation(double latitude,double longitude){
//        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
//        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
//        if (access_token != null && !"".equals(access_token)){
//            RequestParams params = new RequestParams();
//            params.put("uid",uid);
//            params.put("access_token",access_token);
//            params.put("oid",oid);
//            params.put("osn",osn);
//            params.put("latitude",latitude);
//            params.put("longitude",longitude);
//            HttpHelper.post(context, Urls.addMaplocation, params, new TextHttpResponseHandler() {
//                @Override
//                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//
//                }
//
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                    try {
//                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                        if (result.getFlag().equals("Success")) {
//                            if (myLocation != null){
//                                SharedPreferencesUrls.getInstance().putString("biking_latitude",""+myLocation.latitude);
//                                SharedPreferencesUrls.getInstance().putString("biking_longitude",""+myLocation.longitude);
//                            }
//                        }
//                    }catch (Exception e){
//
//                    }
//                }
//            });
//        }
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
                                    localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                                    startActivity(localIntent);
                                    activity.finish();
                                }
                            });
                            customBuilder.create().show();
                        }
                        break;
                    case 100:
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                        if (customDialog2 != null && customDialog2.isShowing()){
                            customDialog2.dismiss();
                        }

                        if (grantResults[0] == PERMISSION_GRANTED) {
                            // Permission Granted
                            if (permissions[0].equals(Manifest.permission.CAMERA)){
                                try {
//                                    closeBroadcast();
//                                    deactivate();
//
//                                    Intent intent = new Intent();
//                                    intent.setClass(context, ActivityScanerCode.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    startActivityForResult(intent, SCANNIN_GREQUEST_CODE);

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
                                    localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                                    startActivity(localIntent);
                                    activity.finish();
                                }
                            });
                            customBuilder.create().show();
                        }
                        break;
                    case REQUEST_CODE_ASK_PERMISSIONS:
                        if (grantResults[0] == PERMISSION_GRANTED) {
                            // Permission Granted
//					if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
//						if (aMap == null) {
//							aMap = mapView.getMap();
//							setUpMap();
//						}
//						aMap.getUiSettings().setZoomControlsEnabled(false);
//						aMap.getUiSettings().setMyLocationButtonEnabled(false);
//						aMap.getUiSettings()
//								.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);// 设置地图logo显示在右下方
//						CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(18);// 设置缩放监听
//						aMap.moveCamera(cameraUpdate);
//						setUpLocationStyle();
//					}

                            initView();
                        } else {
                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                            customBuilder.setTitle("温馨提示").setMessage("您需要在设置里允许获取定位权限！")
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
