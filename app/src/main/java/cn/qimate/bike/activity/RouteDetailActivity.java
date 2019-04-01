package cn.qimate.bike.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
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
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.core.common.DisplayUtil;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.core.widget.MyPagerGalleryView;
import cn.qimate.bike.model.BannerBean;
import cn.qimate.bike.model.HistoryRoadDetailBean;
import cn.qimate.bike.model.MapTraceBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;

/**
 * Created by Administrator on 2017/2/18 0018.
 */

public class RouteDetailActivity extends SwipeBackActivity implements View.OnClickListener {

    private Context context;
    private LoadingDialog loadingDialog;
    private LinearLayout ll_back;
    private TextView title;
    private TextView rightBtn;

    private TextView payState;
    private TextView codeText;
    private TextView bikeNum;
    private TextView st_time;
    private TextView ed_time;
    private TextView total_mintues;
    private TextView prices;
    private Button submitBtn;
    private RelativeLayout imagesLayout;
    private MyPagerGalleryView gallery;
    private LinearLayout pointLayout;

    private TextView freeDaysText;

    private MapView mMapView;
    private AMap mAMap;

    private UMImage image;

    private String oid;
    public static boolean isForeground = false;

    /** 图片id的数组,本地测试用 */
    private int[] imageId = new int[] { R.drawable.empty_photo };
    private String[] imageStrs;
    private List<BannerBean> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);
        mMapView = (MapView) findViewById(R.id.route_map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        context = this;
        datas = new ArrayList<>();
        initView();
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();

        Log.e("history===","history===onResume");
    }

    @Override
    protected void onDestroy() {
        isForeground = false;
        super.onDestroy();
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        oid = getIntent().getExtras().getString("oid");

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("行程详情");
        rightBtn = (TextView)findViewById(R.id.mainUI_title_rightBtn);
        rightBtn.setText("分享");

//        payState = (TextView)findViewById(R.id.history_roadDetailUI_state);
//        codeText = (TextView)findViewById(R.id.history_roadDetailUI_bikeCode);
        bikeNum = (TextView)findViewById(R.id.history_roadDetailUI_bikeNum);
        st_time = (TextView)findViewById(R.id.history_roadDetailUI_startTime);
//        ed_time = (TextView)findViewById(R.id.history_roadDetailUI_endTime);
        total_mintues = (TextView)findViewById(R.id.history_roadDetailUI_totalMintues);
        prices  = (TextView)findViewById(R.id.history_roadDetailUI_totalMoney);
//        submitBtn = (Button)findViewById(R.id.history_roadDetailUI_submitBtn);
        imagesLayout = (RelativeLayout)findViewById(R.id.history_roadDetailUI_imagesLayout);
        gallery = (MyPagerGalleryView)findViewById(R.id.history_roadDetailUI_gallery);
        pointLayout = (LinearLayout)findViewById(R.id.history_roadDetailUI_pointLayout);
//
//        freeDaysText = (TextView) findViewById(R.id.history_roadDetailUI_freeDaysText);
//        if ("0".equals(SharedPreferencesUrls.getInstance().getString("specialdays","0.00"))
//                || "0.00".equals(SharedPreferencesUrls.getInstance().getString("specialdays","0.00"))){
//            freeDaysText.setVisibility(View.GONE);
//        }else {
//            freeDaysText.setVisibility(View.VISIBLE);
//            freeDaysText.setText("剩余免费"+
//                    SharedPreferencesUrls.getInstance().getString("specialdays","0.00")
//                    +"天，每次前一个小时免费");
//        }
//
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)imagesLayout.getLayoutParams();
//        params.height = (DisplayUtil.getWindowWidth(this) - DisplayUtil.dip2px(context,20))/2;
//        imagesLayout.setLayoutParams(params);


        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }

        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(25);// 设置缩放监听
        mAMap.moveCamera(cameraUpdate);

        myOrdermap();

        ll_back.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
//        submitBtn.setOnClickListener(this);

//        initHttp();
        initBannerHttp();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                scrollToFinishActivity();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        m_myHandler.sendEmptyMessage(1);
                    }
                }).start();
                break;

            case R.id.mainUI_title_rightBtn:
//                new ShareAction(this).setDisplayList(SHARE_MEDIA.WEIXIN,
//                        SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE).setShareboardclickCallback(shareBoardlistener)
//                        .open();

                initmPopupWindowView();


//                ShareBoardConfig config =new ShareBoardConfig();
//                config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_CENTER);//分享面板在中间出现
//                config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);// 圆角背景
//                config.setShareboardBackgroundColor(Color.BLUE);//分享面板背景颜色
//                config.setCancelButtonText("我也能改，点我取消");//取消按钮
//                config.setTitleVisibility(false);// 隐藏title
//                mShareAction.open(config);

//                UMWeb web = new UMWeb("http:www.baidu.com");
//                web.setTitle("This is music title");//标题
//                web.setThumb(new UMImage(this, R.mipmap.ic_launcher));  //缩略图
//                web.setDescription("my description");//描述



                break;
        }
    }

    public void initmPopupWindowView(){

        // 获取自定义布局文件的视图
        View customView = getLayoutInflater().inflate(R.layout.pop_share_menu, null, false);
        // 创建PopupWindow宽度和高度
        RelativeLayout pop_win_bg = (RelativeLayout) customView.findViewById(R.id.pop_win_bg);
        ImageView iv_popup_window_back = (ImageView) customView.findViewById(R.id.popupWindow_back);
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
        final PopupWindow popupwindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, true);
        /**
         * 设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
         */
        popupwindow.setAnimationStyle(R.style.PopupAnimation);
        popupwindow.setOutsideTouchable(false);

        LinearLayout wechatLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_wechatLayout);
        LinearLayout wxcircleLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_wxcircleLayout);
        LinearLayout qqLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_qqLayout);
        LinearLayout qzoneBtn = (LinearLayout)customView.findViewById(R.id.pop_menu_qzoneLayout);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.pop_menu_wechatLayout:
                        new ShareAction(RouteDetailActivity.this).setPlatform(SHARE_MEDIA.WEIXIN).setCallback(umShareListener).withMedia(image).share();
                        break;

                    case R.id.pop_menu_wxcircleLayout:
                        new ShareAction(RouteDetailActivity.this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).setCallback(umShareListener).withMedia(image).share();
                        break;

                    case R.id.pop_menu_qqLayout:
                        new ShareAction(RouteDetailActivity.this).setPlatform(SHARE_MEDIA.QQ).setCallback(umShareListener).withMedia(image).share();
                        break;

                    case R.id.pop_menu_qzoneLayout:
                        new ShareAction(RouteDetailActivity.this).setPlatform(SHARE_MEDIA.QZONE).setCallback(umShareListener).withMedia(image).share();
                        break;
                }
                popupwindow.dismiss();
            }
        };

        wechatLayout.setOnClickListener(listener);
        wxcircleLayout.setOnClickListener(listener);
        qqLayout.setOnClickListener(listener);
        qzoneBtn.setOnClickListener(listener);

        popupwindow.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            new ShareAction(RouteDetailActivity.this).setPlatform(share_media).setCallback(umShareListener)
                    .withMedia(image).share();
        }
    };

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
//                                if ("2".equals(bean.getShow_status())){
//                                    Toast.makeText(context,"无行车轨迹",Toast.LENGTH_SHORT).show();
//                                    scrollToFinishActivity();
//                                    return;
//                                }
//                                if (!latLngs.isEmpty() || 0 != latLngs.size()){
//                                    latLngs.clear();
//                                }
//                                // 加入自定义标签
//                                MarkerOptions originMarkerOption = new MarkerOptions().position(new LatLng(
//                                        Double.parseDouble(bean.getLat_start()),Double.parseDouble(bean.getLng_start()))).icon(originDescripter);
//                                mAMap.addMarker(originMarkerOption);
//                                MarkerOptions terminusMarkerOption = new MarkerOptions().position(new LatLng(
//                                        Double.parseDouble(bean.getLat_end()),Double.parseDouble(bean.getLng_end()))).icon(terminusDescripter);
//                                mAMap.addMarker(terminusMarkerOption);
//                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
//                                        new LatLng(Double.parseDouble(bean.getLat_start()),
//                                                Double.parseDouble(bean.getLng_start())), 18);// 设置缩放监听
//                                mAMap.moveCamera(cameraUpdate);
//                                ImageLoader.getInstance().displayImage(Urls.host + bean.getHeadimgurl(),heading);
//                                telText.setText(bean.getTelphone());
//                                distanceText.setText(bean.getDistance());
//                                timeText.setText(bean.getLongtimes());
//
//                                Log.e("url===", "==="+bean.getShare_url());
//
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

    private void initHttp(){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("oid",oid);
        HttpHelper.get(context, Urls.myOrderdetail, params, new TextHttpResponseHandler() {
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
                        HistoryRoadDetailBean bean = JSON.parseObject(result.getData(), HistoryRoadDetailBean.class);
//                        if ("2".equals(bean.getIspay())){
//                            payState.setText("支付成功");
//                        }else {
//                            payState.setText("未支付");
//                        }
//                        codeText.setText("行程编号:"+bean.getOsn());
                        bikeNum.setText(bean.getCodenum());
                        st_time.setText(bean.getSt_time());
//                        ed_time.setText(bean.getEd_time());
                        total_mintues.setText(bean.getTotal_mintues());
                        prices.setText(bean.getPrices());
                    } else {
                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });
    }
    /**
     * 广告图片
     */
    private void initBannerHttp() {

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");

        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("adsid",12);
        HttpHelper.get(context, Urls.bannerUrl, params, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在载入");
                    loadingDialog.show();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.e("Test","广告:"+responseString);
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (0 == result.getErrcode()) {
                        if (datas.size() != 0 || !datas.isEmpty()) {
                            datas.clear();
                        }
                        JSONArray array = new JSONArray(result.getData());
                        for (int i = 0; i < array.length(); i++) {
                            BannerBean bean = JSON.parseObject(array.getJSONObject(i).toString(), BannerBean.class);
                            datas.add(bean);
                        }
                    } else {
                        UIHelper.showToastMsg(context, result.getMsg(), R.drawable.ic_error);
                    }
                    initBanner();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
                initBanner();
            }
        });
    }
    private void initBanner() {
        if (datas == null || datas.isEmpty()) {
            gallery.start(context, null, imageId, 0, pointLayout, R.drawable.point_sel, R.drawable.point_nos);
        } else {
            imageStrs = new String[datas.size()];
            for (int i = 0; i < datas.size(); i++) {
                imageStrs[i] = datas.get(i).getAd_file();
            }
            gallery.start(context, imageStrs, imageId, 3000, pointLayout, R.drawable.point_sel, R.drawable.point_nos);

            gallery.setMyOnItemClickListener(new MyPagerGalleryView.MyOnItemClickListener() {

                @Override
                public void onItemClick(int curIndex) {
                    UIHelper.bannerGoAct(context, datas.get(curIndex).getApp_type(), datas.get(curIndex).getApp_id(),
                            datas.get(curIndex).getAd_link());
                }
            });
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {


            new Thread(new Runnable() {
                @Override
                public void run() {
                    m_myHandler.sendEmptyMessage(1);
                }
            }).start();

            scrollToFinishActivity();


            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 0:
                    break;
                case 1:

                    if(BaseApplication.getInstance().getIBLE().isEnable()){
                        BaseApplication.getInstance().getIBLE().refreshCache();
                        BaseApplication.getInstance().getIBLE().close();
                        BaseApplication.getInstance().getIBLE().disconnect();
                        BaseApplication.getInstance().getIBLE().disableBluetooth();
                    }

                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this **/
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

}
