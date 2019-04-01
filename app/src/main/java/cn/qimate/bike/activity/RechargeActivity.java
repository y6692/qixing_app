package cn.qimate.bike.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.vondear.rxtools.RxFileTool;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.nostra13.universalimageloader.core.ImageLoader;
import cn.qimate.bike.BuildConfig;
import cn.qimate.bike.R;
import cn.qimate.bike.alipay.PayResult;
import cn.qimate.bike.base.BaseViewAdapter;
import cn.qimate.bike.base.BaseViewHolder;
import cn.qimate.bike.core.common.BitmapUtils1;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.NetworkUtils;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.core.widget.MyGridView;
import cn.qimate.bike.core.widget.MyListView;
import cn.qimate.bike.core.widget.MyPagerGalleryView;
import cn.qimate.bike.img.NetUtil;
import cn.qimate.bike.model.BannerBean;
import cn.qimate.bike.model.RechargeBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.ToastUtil;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by Administrator1 on 2017/7/20.
 */
@SuppressLint("NewApi")
public class RechargeActivity extends SwipeBackActivity implements View.OnClickListener, PLA_AdapterView.OnItemClickListener {

    private static final int SDK_PAY_FLAG = 1;
    private IWXAPI api;

    private TextView Tag1,Tag2,Tag3,Tag4;

    private boolean isSelected1 = false;
    private boolean isSelected2 = false;
    private boolean isSelected3 = false;
    private boolean isSelected4 = false;

    private Context context;
    private LoadingDialog loadingDialog;
    private LinearLayout ll_back;
    private TextView title;

    private MultiColumnListView moneyListView;
    private RelativeLayout alipayTypeLayout,WeChatTypeLayout;
    private ImageView alipayTypeImage,WeChatTypeImage;
    private LinearLayout submitBtn;
    private RelativeLayout imagesLayout;
    private MyPagerGalleryView gallery;
    private LinearLayout pointLayout;

    private List<String> TagsList;

    private List<RechargeBean> recharge_datas;
    private MyAdapter myAdapter;
    private int selectPosition = 0;

    private String rid = ""; //充值类型
    private String paytype = "1";//1支付宝2微信
    private String osn = "";

    private int[] imageId = new int[] { R.drawable.empty_photo };
    private String[] imageStrs;
    private List<BannerBean> banner_datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        context = this;
        recharge_datas = new ArrayList<>();
        banner_datas = new ArrayList<>();
        TagsList = new ArrayList<>();

        IntentFilter filter = new IntentFilter("data.broadcast.rechargeAction");
        registerReceiver(broadcastReceiver, filter);

        initView();
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

//        Tag1 = (TextView)findViewById(R.id.feedbackUI_type_Tag1);
//        Tag2 = (TextView)findViewById(R.id.feedbackUI_type_Tag2);
//        Tag3 = (TextView)findViewById(R.id.feedbackUI_type_Tag3);
//        Tag4 = (TextView)findViewById(R.id.feedbackUI_type_Tag4);

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("充值");

        moneyListView = (MultiColumnListView)findViewById(R.id.rechargeUI_moneyList);
        alipayTypeLayout = (RelativeLayout)findViewById(R.id.rechargeUI_alipayTypeLayout);
        WeChatTypeLayout = (RelativeLayout)findViewById(R.id.rechargeUI_WeChatTypeLayout);
        alipayTypeImage = (ImageView)findViewById(R.id.rechargeUI_alipayTypeImage);
        WeChatTypeImage = (ImageView)findViewById(R.id.rechargeUI_WeChatTypeImage);
        submitBtn = (LinearLayout) findViewById(R.id.rechargeUI_submitBtn);
        imagesLayout = (RelativeLayout)findViewById(R.id.rechargeUI_imagesLayout);
        gallery = (MyPagerGalleryView)findViewById(R.id.rechargeUI_gallery);
        pointLayout = (LinearLayout)findViewById(R.id.rechargeUI_pointLayout);

        if (recharge_datas.isEmpty() || 0 == recharge_datas.size()){
            initHttp();
        }
        myAdapter = new MyAdapter(context);
        moneyListView.setAdapter(myAdapter);
        moneyListView.setOnItemClickListener(this);


        ll_back.setOnClickListener(this);
        alipayTypeLayout.setOnClickListener(this);
        WeChatTypeLayout.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

//        Tag1.setOnClickListener(this);
//        Tag2.setOnClickListener(this);
//        Tag3.setOnClickListener(this);
//        Tag4.setOnClickListener(this);

        initBannerHttp();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                UIHelper.goToAct(context, CurRoadBikingActivity.class);
                scrollToFinishActivity();
                break;

            case R.id.feedbackUI_type_Tag1:

                Log.e("TagsList===", TagsList.size()+"==="+TagsList);

                if (isSelected1){
                    isSelected1 = false;
                    if (TagsList.contains(Tag1.getText().toString())){
                        TagsList.remove(Tag1.getText().toString());
                    }
                    Tag1.setTextColor(Color.parseColor("#666666"));
                    Tag1.setBackgroundResource(R.drawable.shape_feedback);
                }else {
                    isSelected1 = true;
                    if (!TagsList.contains(Tag1.getText().toString())){
                        TagsList.add(Tag1.getText().toString());
                    }
                    Tag1.setTextColor(Color.parseColor("#f57752"));
                    Tag1.setBackgroundResource(R.drawable.shape_feedback_selectd);
                }

                Log.e("TagsList===111", TagsList.size()+"==="+TagsList);
                break;
            case R.id.feedbackUI_type_Tag2:
                if (isSelected2){
                    isSelected2 = false;
                    if (TagsList.contains(Tag2.getText().toString())){
                        TagsList.remove(Tag2.getText().toString());
                    }
                    Tag2.setTextColor(Color.parseColor("#666666"));
                    Tag2.setBackgroundResource(R.drawable.shape_feedback);
                }else {
                    isSelected2 = true;
                    if (!TagsList.contains(Tag2.getText().toString())){
                        TagsList.add(Tag2.getText().toString());
                    }
                    Tag2.setTextColor(Color.parseColor("#f57752"));
                    Tag2.setBackgroundResource(R.drawable.shape_feedback_selectd);
                }
                break;
            case R.id.feedbackUI_type_Tag3:
                if (isSelected3){
                    isSelected3 = false;
                    if (TagsList.contains(Tag3.getText().toString())){
                        TagsList.remove(Tag3.getText().toString());
                    }
                    Tag3.setTextColor(Color.parseColor("#666666"));
                    Tag3.setBackgroundResource(R.drawable.shape_feedback);
                }else {
                    isSelected3 = true;
                    if (!TagsList.contains(Tag3.getText().toString())){
                        TagsList.add(Tag3.getText().toString());
                    }
                    Tag3.setTextColor(Color.parseColor("#f57752"));
                    Tag3.setBackgroundResource(R.drawable.shape_feedback_selectd);
                }
                break;
            case R.id.feedbackUI_type_Tag4:
                if (isSelected4){
                    isSelected4 = false;
                    if (TagsList.contains(Tag4.getText().toString())){
                        TagsList.remove(Tag4.getText().toString());
                    }
                    Tag4.setTextColor(Color.parseColor("#666666"));
                    Tag4.setBackgroundResource(R.drawable.shape_feedback);
                }else {
                    isSelected4 = true;
                    if (!TagsList.contains(Tag4.getText().toString())){
                        TagsList.add(Tag4.getText().toString());
                    }
                    Tag4.setTextColor(Color.parseColor("#f57752"));
                    Tag4.setBackgroundResource(R.drawable.shape_feedback_selectd);
                }
                break;

            case R.id.rechargeUI_alipayTypeLayout:
                alipayTypeImage.setImageResource(R.drawable.pay_type_selected);
                WeChatTypeImage.setImageResource(R.drawable.pay_type_normal);
                paytype = "1";
                break;
            case R.id.rechargeUI_WeChatTypeLayout:
                alipayTypeImage.setImageResource(R.drawable.pay_type_normal);
                WeChatTypeImage.setImageResource(R.drawable.pay_type_selected);
                paytype = "2";
                break;
            case R.id.rechargeUI_submitBtn:
                if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                    return;
                }
                userRecharge(uid,access_token);
                break;

            default:
                break;
        }
    }

    private void userRecharge(final String uid, final String access_token){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("rid",rid);
        params.put("paytype",paytype);
        HttpHelper.post(context, Urls.userRecharge, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在提交");
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
                        osn = result.getData();
                        if ("1".equals(paytype)){
                            show_alipay(osn,uid,access_token);
                        }else {
                            show_wxpay(osn,uid,access_token);
                        }
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

    public void show_alipay(final String osn,String uid,String access_token) {
        Toast.makeText(context, "正在调起支付宝支付...", Toast.LENGTH_LONG).show();
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("osn", osn);
        HttpHelper.get(context, Urls.alipayType, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        final String payInfo = result.getData();
                        Runnable payRunnable = new Runnable() {
                            @Override
                            public void run() {
                                // 构造PayTask 对象
                                PayTask alipay = new PayTask(RechargeActivity.this);
                                // 调用支付接口，获取支付结果
                                String result = alipay.pay(payInfo, true);
                                Message msg = new Message();
                                msg.what = SDK_PAY_FLAG;
                                msg.obj = result;
                                mHandler.sendMessage(msg);
                            }
                        };
                        // 必须异步调用
                        Thread payThread = new Thread(payRunnable);
                        payThread.start();
                    } else {
                        UIHelper.showToastMsg(context, result.getMsg(), R.drawable.ic_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                UIHelper.ToastError(context, throwable.toString());
            }
        });

    }

    public void show_wxpay(final String osn,String uid,String access_token) {
        SharedPreferencesUrls.getInstance().putBoolean("isTreasure",false);
        Toast.makeText(context, "正在调起微信支付...", Toast.LENGTH_LONG).show();
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("osn", osn);
        HttpHelper.get(context, Urls.wxpay1, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        api = WXAPIFactory.createWXAPI(context, "wx86d98ec252f67d07", false);
                        api.registerApp("wx86d98ec252f67d07");
                        JSONObject jsonObject = new JSONObject(result.getData());
                        PayReq req = new PayReq();
                        req.appId = jsonObject.getString("appid");// wpay.getAppid();//
                        // 微信appId
                        req.packageValue = jsonObject.getString("package");// wpay.getPackageValue();//
                        // 包
                        req.extData = "app data"; // optional
                        req.timeStamp = jsonObject.getString("timestamp");// wpay.getTimeStamp();//
                        // 时间戳
                        req.partnerId = jsonObject.getString("partnerid");// wpay.getPartnerId();//
                        // 商户号"
                        req.prepayId = jsonObject.getString("prepayid");// wpay.getPrepayId();//
                        // 预支付订单号
                        req.nonceStr = jsonObject.getString("noncestr");// wpay.getNonceStr();//
                        // 随机字符串
                        req.sign = jsonObject.getString("sign");// wpay.getSign();//
                        // 后台返回的签名
                        // 调微信支付
                        if (api.isWXAppInstalled() && api.isWXAppSupportAPI()) {
                            api.sendReq(req);
                        } else {
                            Toast.makeText(context, "请下载最新版微信App", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        UIHelper.showToastMsg(context, result.getMsg(), R.drawable.ic_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                UIHelper.ToastError(context, throwable.toString());
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(context, "恭喜您,支付成功", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(context, PersonMallOrderShowActivity.class);
//                        intent.putExtra("osn", getIntent().getExtras().getString("orderNumber"));
//                        startActivity(intent);
                        finishMine();
                    } else {
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(context, "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            scrollToFinishActivity();
        }
    };


//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//
//    }

    @Override
    public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
        rid = myAdapter.getDatas().get(position).getId();
        if (position != selectPosition){
            myAdapter.getDatas().get(position).setSelected(true);
            myAdapter.getDatas().get(selectPosition).setSelected(false);
            selectPosition = position;
        }
        myAdapter.notifyDataSetChanged();
    }

    private class MyAdapter extends BaseViewAdapter<RechargeBean> {

        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_recharge_money, null);
            }
            LinearLayout layout = BaseViewHolder.get(convertView,R.id.item_recharge_layout);
            TextView moneyText = BaseViewHolder.get(convertView,R.id.item_recharge_money);

            RechargeBean bean = getDatas().get(position);
            layout.setSelected(bean.isSelected());
            moneyText.setSelected(bean.isSelected());
            moneyText.setText(bean.getTitle());
            return convertView;
        }
    }

    private void initHttp(){
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null ||"".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录您的账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context,LoginActivity.class);
            return;
        }
        Log.e("Test","uid:"+uid);
        Log.e("Test","access_token:"+access_token);
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        HttpHelper.get(context, Urls.rechargeList,params, new TextHttpResponseHandler() {
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
                        JSONArray array = new JSONArray(result.getData());
                        if (recharge_datas.size() != 0 || !recharge_datas.isEmpty()){
                            recharge_datas.clear();
                        }
                        for (int i = 0; i < array.length(); i++){
                            RechargeBean bean = JSON.parseObject(array.getJSONObject(i).toString(), RechargeBean.class);
                            recharge_datas.add(bean);
                            if ( 0 == i){
                                rid = bean.getId();
                                bean.setSelected(true);
                            }else {
                                bean.setSelected(false);
                            }
                        }
                        myAdapter.setDatas(recharge_datas);
                        myAdapter.notifyDataSetChanged();
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
                        if (banner_datas.size() != 0 || !banner_datas.isEmpty()) {
                            banner_datas.clear();
                        }
                        JSONArray array = new JSONArray(result.getData());
                        for (int i = 0; i < array.length(); i++) {
                            BannerBean bean = JSON.parseObject(array.getJSONObject(i).toString(), BannerBean.class);
                            banner_datas.add(bean);
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
        if (banner_datas == null || banner_datas.isEmpty()) {
            gallery.start(context, null, imageId, 0, pointLayout, R.drawable.point_sel, R.drawable.point_nos);
        } else {
            imageStrs = new String[banner_datas.size()];
            for (int i = 0; i < banner_datas.size(); i++) {
                imageStrs[i] = banner_datas.get(i).getAd_file();
            }
            gallery.start(context, imageStrs, imageId, 3000, pointLayout, R.drawable.point_sel, R.drawable.point_nos);

            gallery.setMyOnItemClickListener(new MyPagerGalleryView.MyOnItemClickListener() {

                @Override
                public void onItemClick(int curIndex) {
                    UIHelper.bannerGoAct(context, banner_datas.get(curIndex).getApp_type(), banner_datas.get(curIndex).getApp_id(),
                            banner_datas.get(curIndex).getAd_link());
                }
            });
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            UIHelper.goToAct(context, CurRoadBikingActivity.class);
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
