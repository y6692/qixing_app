package cn.qimate.bike.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
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
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator on 2017/2/18 0018.
 */

public class HistoryRoadDetailActivity extends SwipeBackActivity implements View.OnClickListener {

    private Context context;
    private LoadingDialog loadingDialog;
    private ImageView backImg;
    private TextView title;

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

    private TextView freeText;
    private TextView freeDaysText;
    private TextView tv;

    private String oid;
    public static boolean isForeground = false;

    /** 图片id的数组,本地测试用 */
    private int[] imageId = new int[] { R.drawable.empty_photo };
    private String[] imageStrs;
    private List<BannerBean> datas;

    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_history_road_detail);
        context = this;

        type = SharedPreferencesUrls.getInstance().getString("type", "");

        datas = new ArrayList<>();
        initView();
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();

        Log.e("history===","history===onResume==="+type);
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

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("行程详情");

        payState = (TextView)findViewById(R.id.history_roadDetailUI_state);
        codeText = (TextView)findViewById(R.id.history_roadDetailUI_bikeCode);
        bikeNum = (TextView)findViewById(R.id.history_roadDetailUI_bikeNum);
        st_time = (TextView)findViewById(R.id.history_roadDetailUI_startTime);
        ed_time = (TextView)findViewById(R.id.history_roadDetailUI_endTime);
        total_mintues = (TextView)findViewById(R.id.history_roadDetailUI_totalMintues);
        prices  = (TextView)findViewById(R.id.history_roadDetailUI_totalMoney);
        submitBtn = (Button)findViewById(R.id.history_roadDetailUI_submitBtn);
        imagesLayout = (RelativeLayout)findViewById(R.id.history_roadDetailUI_imagesLayout);
        gallery = (MyPagerGalleryView)findViewById(R.id.history_roadDetailUI_gallery);
        pointLayout = (LinearLayout)findViewById(R.id.history_roadDetailUI_pointLayout);
        tv = (TextView)findViewById(R.id.tv);



//        freeText = (TextView) findViewById(R.id.history_roadDetailUI_freeText);
//        if ("0".equals(SharedPreferencesUrls.getInstance().getString("specialdays","0.00"))
//                || "0.00".equals(SharedPreferencesUrls.getInstance().getString("specialdays","0.00"))){
//            freeText.setVisibility(View.GONE);
//        }else {
//            freeText.setVisibility(View.VISIBLE);
//            freeText.setText("剩余免费"+
//                    SharedPreferencesUrls.getInstance().getString("specialdays","0.00")
//                    +"天，每次前一个小时免费");
//        }

        freeDaysText = (TextView) findViewById(R.id.history_roadDetailUI_freeDaysText);
        if ("0".equals(SharedPreferencesUrls.getInstance().getString("specialdays","0.00"))
                || "0.00".equals(SharedPreferencesUrls.getInstance().getString("specialdays","0.00"))){
            freeDaysText.setVisibility(View.GONE);
        }else {
            freeDaysText.setVisibility(View.VISIBLE);
            if("4".equals(type)){
                freeDaysText.setText("电单车剩余免费"+ SharedPreferencesUrls.getInstance().getString("specialdays","0.00") +"天，每次前一个小时免费");
            }else{
                freeDaysText.setText("单车剩余免费"+ SharedPreferencesUrls.getInstance().getString("specialdays","0.00") +"天，每次前一个小时免费");
            }
        }

        if("4".equals(type)){
            tv.setText("还车须至校内地图绿色区域，封顶20元/天");
        }else{
            tv.setText("还车须至校内地图红色区域，封顶5元/天");
        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)imagesLayout.getLayoutParams();
        params.height = (DisplayUtil.getWindowWidth(this) - DisplayUtil.dip2px(context,20))/2;
        imagesLayout.setLayoutParams(params);

        backImg.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        initHttp();
        initBannerHttp();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        m_myHandler.sendEmptyMessage(1);
//                    }
//                }).start();


                break;
            case R.id.history_roadDetailUI_submitBtn:
                Intent intent = new Intent(context,MapTaceActivity.class);
                intent.putExtra("oid",oid);
                startActivity(intent);
                break;
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
                        if ("2".equals(bean.getIspay())){
                            payState.setText("支付成功");
                        }else {
                            payState.setText("未支付");
                        }
                        codeText.setText("行程编号:"+bean.getOsn());
                        bikeNum.setText(bean.getCodenum());
                        st_time.setText(bean.getSt_time());
                        ed_time.setText(bean.getEd_time());
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


//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    m_myHandler.sendEmptyMessage(1);
//                }
//            }).start();

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
//                        BaseApplication.getInstance().getIBLE().disableBluetooth();
                    }

                    break;
                default:
                    break;
            }
            return false;
        }
    });
}
