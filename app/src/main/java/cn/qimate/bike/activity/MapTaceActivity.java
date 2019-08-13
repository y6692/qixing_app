package cn.qimate.bike.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.nostra13.universalimageloader.core.ImageLoader;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.DensityUtil;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.MapTraceBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator on 2017/2/18 0018.
 */

public class MapTaceActivity extends SwipeBackActivity implements View.OnClickListener{

    private Context context;
    private LoadingDialog loadingDialog;
    private ImageView backImg;
    private TextView title;
    private TextView rightBtn;

    private RelativeLayout descLayout;
    private TextView telText;
    private TextView distanceText;
    private TextView timeText;
    private ImageView heading;

    private MapView mMapView;
    private AMap mAMap;
    private String oid = "";
    private List<MapTraceBean>data;
    private List<LatLng>latLngs;
    private BitmapDescriptor originDescripter;
    private BitmapDescriptor terminusDescripter;
    //分享
//    private String shareTitle;
    private UMImage image;
//    private String shareDesc;
//    private String share_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_map_trace);
        mMapView = (MapView) findViewById(R.id.traceUI_map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        context = this;
        data = new ArrayList<>();
        latLngs = new ArrayList<>();
        initView();
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
//        oid = getIntent().getExtras().getString("oid");

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("行程轨迹");
        rightBtn = (TextView)findViewById(R.id.mainUI_title_rightBtn);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText("分享");

        descLayout = (RelativeLayout)findViewById(R.id.ui_mapTrace_descLayout);
        telText = (TextView)findViewById(R.id.ui_mapTrace_telText);
        distanceText = (TextView)findViewById(R.id.ui_mapTrace_distanceText);
        timeText = (TextView)findViewById(R.id.ui_mapTrace_timeText);
        heading = (ImageView)findViewById(R.id.ui_mapTrace_heading);

        originDescripter = BitmapDescriptorFactory.fromResource(R.drawable.origin_icon);
        terminusDescripter = BitmapDescriptorFactory.fromResource(R.drawable.terminus_icon);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)descLayout.getLayoutParams();
        params.height = DensityUtil.getWindowHeight(this)/3;
        descLayout.setLayoutParams(params);

        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }

        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(25);// 设置缩放监听
        mAMap.moveCamera(cameraUpdate);
        myOrdermap();

        backImg.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.mainUI_title_rightBtn:
                /**
                 * shareboard need the platform all you want and
                 * callbacklistener,then open it
                 **/
                new ShareAction(this).setDisplayList(SHARE_MEDIA.WEIXIN,
                        SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE).setShareboardclickCallback(shareBoardlistener)
                        .open();
                break;
            default:
                break;
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    /**
     *
     * 骑行记录详情地图
     * */
    private void myOrdermap(){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请登录您的账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context,LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("oid",oid);
            HttpHelper.get(context, Urls.myOrdermap, params, new TextHttpResponseHandler() {
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
                            if (result.getData() != null && !"".equals(result.getData())){
                                MapTraceBean bean = JSON.parseObject(result.getData(),MapTraceBean.class);
                                if ("2".equals(bean.getShow_status())){
                                    Toast.makeText(context,"无行车轨迹",Toast.LENGTH_SHORT).show();
                                    scrollToFinishActivity();
                                    return;
                                }
                                if (!latLngs.isEmpty() || 0 != latLngs.size()){
                                    latLngs.clear();
                                }
                                // 加入自定义标签
                                MarkerOptions originMarkerOption = new MarkerOptions().position(new LatLng(
                                        Double.parseDouble(bean.getLat_start()),Double.parseDouble(bean.getLng_start()))).icon(originDescripter);
                                mAMap.addMarker(originMarkerOption);
                                MarkerOptions terminusMarkerOption = new MarkerOptions().position(new LatLng(
                                                Double.parseDouble(bean.getLat_end()),Double.parseDouble(bean.getLng_end()))).icon(terminusDescripter);
                                mAMap.addMarker(terminusMarkerOption);
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(Double.parseDouble(bean.getLat_start()),
                                                Double.parseDouble(bean.getLng_start())), 18);// 设置缩放监听
                                mAMap.moveCamera(cameraUpdate);
                                ImageLoader.getInstance().displayImage(Urls.host + bean.getHeadimgurl(),heading);
                                telText.setText(bean.getTelphone());
                                distanceText.setText(bean.getDistance());
                                timeText.setText(bean.getLongtimes());
                                if (bean.getShare_url().indexOf(Urls.HTTP) == -1){
                                    image = new UMImage(context, Urls.host+bean.getShare_url());
                                }else {
                                    image = new UMImage(context, bean.getShare_url());
                                }
                            }else {
                                Toast.makeText(context,"无历史行驶轨迹",Toast.LENGTH_SHORT).show();
                                scrollToFinishActivity();
                            }
                        } else {
                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
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
    }
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(context, " 分享失败啦", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(context, "分享取消啦", Toast.LENGTH_SHORT).show();
        }
    };
    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            new ShareAction(MapTaceActivity.this).setPlatform(share_media).setCallback(umShareListener)
                    .withMedia(image).share();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this **/
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
