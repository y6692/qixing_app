package cn.qimate.bike.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.sofi.blelocker.library.search.SearchRequest;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.config.LockType;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xiaoantech.sdk.XiaoanBleApiClient;
import com.zxing.lib.scaner.activity.MainFragmentPermissionsDispatcher;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.nostra13.universalimageloader.core.ImageLoader;
import cn.qimate.bike.R;
import cn.qimate.bike.alipay.PayResult;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.ble.BLEService;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.fragment.MainFragment;
import cn.qimate.bike.model.BillBean;
import cn.qimate.bike.model.GlobalConfig;
import cn.qimate.bike.model.OrderBean;
import cn.qimate.bike.model.PaymentBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UserBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.ToastUtil;
import cn.qimate.bike.wxapi.WXEntryActivity;

/**
 * Created by yuanyi on 2019/12/9.
 */
public class SettlementPlatformActivity extends SwipeBackActivity implements View.OnClickListener {


    private Context context;
    private LoadingDialog loadingDialog;
    private LinearLayout backImg;
    private LinearLayout submitBtn;

    private static final int SDK_PAY_FLAG = 1;
    private IWXAPI api;

    private TextView tv_unpay_route_car_number;
    private TextView tv_unpay_route_car_start_time;
    private TextView tv_unpay_route_car_end_time;
    private TextView tv_unpay_route_order_amount;

    private TextView tv_balance;
    private TextView tv_recharge;
    private TextView tv_order_amount;
    private RelativeLayout ll_pay;
    private LinearLayout ll_pay1;
    private LinearLayout ll_pay2;
    private LinearLayout ll_pay3;
    private ImageView iv_balance_icon;
    private ImageView iv_alipay_icon;
    private ImageView iv_wechat_icon;
    private ImageView iv_balance;
    private ImageView iv_alipay;
    private ImageView iv_wechat;

    private String order_amount = "";
    private int payment_id = 1;
    private int order_id;
    private int order_type = 1;
    private static int order_id2;
    private static int order_type2 = 1;
    private double balance;
    private boolean isRemain;

    private boolean isToPay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement_platform);
        context = this;

        isToPay = false;

        IntentFilter filter = new IntentFilter("data.broadcast.rechargeAction");
        registerReceiver(broadcastReceiver, filter);
        initView();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
//                    if("1".equals(gamestatus)){
//                        UIHelper.goToAct(context,MainActivity.class);
//                    }else{
//                        Intent intent2 = new Intent(context, WebviewActivity.class);
//                        intent2.putExtra("link", "http://www.7mate.cn/Home/Games/index.html");
//                        intent2.putExtra("title", "活动详情");
//                        startActivity(intent2);
//                    }
//                    scrollToFinishActivity();

                    Log.e("broadcastReceiver===", "===");

                    query_order();
                }
            });

        }
    };

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        backImg = (LinearLayout) findViewById(R.id.ll_backBtn);
        tv_order_amount = (TextView)findViewById(R.id.tv_order_amount);
        tv_balance = (TextView)findViewById(R.id.tv_balance);
        tv_recharge = (TextView)findViewById(R.id.tv_recharge);
        ll_pay = (RelativeLayout) findViewById(R.id.ll_pay);
        ll_pay1 = (LinearLayout) findViewById(R.id.ll_pay1);
        ll_pay2 = (LinearLayout) findViewById(R.id.ll_pay2);
        ll_pay3 = (LinearLayout) findViewById(R.id.ll_pay3);
        iv_balance_icon = (ImageView) findViewById(R.id.iv_balance_icon);
        iv_alipay_icon = (ImageView) findViewById(R.id.iv_alipay_icon);
        iv_wechat_icon = (ImageView) findViewById(R.id.iv_wechat_icon);
        iv_balance = (ImageView) findViewById(R.id.iv_balance);
        iv_alipay = (ImageView) findViewById(R.id.iv_alipay);
        iv_wechat = (ImageView) findViewById(R.id.iv_wechat);


        submitBtn = (LinearLayout)findViewById(R.id.settlement_platform_submitBtn);

        ll_pay1.setOnClickListener(this);
        ll_pay2.setOnClickListener(this);
        ll_pay3.setOnClickListener(this);
        backImg.setOnClickListener(this);
        tv_recharge.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        api = WXAPIFactory.createWXAPI(context, "wx86d98ec252f67d07", false);
        api.registerApp("wx86d98ec252f67d07");
//        api.handleIntent(getIntent(), SettlementPlatformActivity.this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//      must store the new intent unless getIntent() will return the old one
        setIntent(intent);

        Log.e("spa===onNewIntent", isRemain+"==="+isToPay+"==="+order_id+"==="+order_type);

//        if(api!=null){
//            api.handleIntent(getIntent(), SettlementPlatformActivity.this);
//        }

    }

    @Override
    public void onResume() {
        super.onResume();

        order_id = getIntent().getIntExtra("order_id", 0);
        order_type = getIntent().getIntExtra("order_type", 1);
        order_amount = getIntent().getStringExtra("order_amount");
        isRemain = getIntent().getBooleanExtra("isRemain", false);

        if(order_amount==null){
            tv_order_amount.setText("");
        }else{
            tv_order_amount.setText("¥"+order_amount);
        }


        Log.e("spa===onResume", isRemain+"==="+isToPay+"==="+order_id+"==="+order_type);

//        if(api!=null){
//            api.handleIntent(getIntent(), SettlementPlatformActivity.this);
//        }


        if(!isToPay){
            user();
        }

    }

    private void user() {
        Log.e("spfa===user", order_id+"==="+order_type);

        HttpHelper.get(context, Urls.user, new TextHttpResponseHandler() {
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

                            Log.e("spfa===user1", responseString + "===" + result.data);

                            UserBean bean = JSON.parseObject(result.getData(), UserBean.class);

                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                            balance = Double.parseDouble(bean.getBalance());
                            tv_balance.setText("当前余额¥"+balance);

                            if(order_type==1){
                                cycling();
                            }

                            payments();

                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });
            }
        });

    }


    private void payments() {
        Log.e("ura===payments", order_type+"===");

        RequestParams params = new RequestParams();
        params.put("order_type", order_type); //订单类型 1骑行订单 2套餐卡订单 3充值订单 4认证充值订单 5调度费订单 6赔偿费订单
        HttpHelper.get(context, Urls.payments, params, new TextHttpResponseHandler() {
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

                            JSONArray array = new JSONArray(result.getData());

                            ll_pay1.setVisibility(View.GONE);
                            ll_pay2.setVisibility(View.GONE);
                            ll_pay3.setVisibility(View.GONE);

                            Log.e("ura===payments1", responseString + "===" + result.data);

                            for (int i = 0; i < array.length(); i++) {

                                Log.e("ura===payments2", "==="+array.getJSONObject(i).toString());

                                PaymentBean bean = JSON.parseObject(array.getJSONObject(i).toString(), PaymentBean.class);



//                                int top = 180/ Math.round(BaseApplication.density) *(i+1);

                                int top = 43*Math.round(BaseApplication.density) *(i+1);

                                Log.e("ura===payments3", top+"==="+bean.getId());

                                if(bean.getId()==1){
                                    ll_pay1.setVisibility(View.VISIBLE);
                                    ImageLoader.getInstance().displayImage(bean.getIcon(), iv_balance_icon);

//                                    ll_pay.addView(ll_pay1);

                                    if(i==0){
                                        payment_id = 1;
                                        iv_balance.setImageResource(R.drawable.pay_type_selected);
                                    }


                                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll_pay1.getLayoutParams();
                                    params.setMargins(0, top, 0, 0);
                                    ll_pay1.setLayoutParams(params);


//                                    m_myHandler.sendEmptyMessage(0);

                                }else if(bean.getId()==2){      //wechat

                                    ll_pay2.setVisibility(View.VISIBLE);
                                    ImageLoader.getInstance().displayImage(bean.getIcon(), iv_wechat_icon);

//                                    ll_pay.addView(ll_pay2);

//                                    TextView tv = new TextView(context);
//                                    tv.setText("我和猫猫是新添加的");
//                                    tv.setBackgroundColor(Color.GRAY);
//                                    LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                                    FrameLayout.LayoutParams  LP_WW = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

//                                    LinearLayout.LayoutParams LP_WW = (LinearLayout.LayoutParams)ll_pay2.getLayoutParams();
//                                    LP_WW.topMargin = 100;
//                                    LP_WW.setMargins(0, 100, 0, 0);//设置边距
//                                    ll_pay2.setLayoutParams(LP_WW);

//                                    ll_pay.addView(ll_pay2);

                                    if(i==0){
                                        payment_id = 2;
                                        iv_wechat.setImageResource(R.drawable.pay_type_selected);
                                    }

//                                    ll_pay2.setPadding(0, top, 0, 0);
                                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll_pay2.getLayoutParams();
                                    params.setMargins(0, top, 0, 0);
                                    ll_pay2.setLayoutParams(params);
                                }else if(bean.getId()==3){      //alipay
                                    ll_pay3.setVisibility(View.VISIBLE);
                                    ImageLoader.getInstance().displayImage(bean.getIcon(), iv_alipay_icon);

//                                    ll_pay.addView(ll_pay3);

                                    if(i==0){
                                        payment_id = 3;
                                        iv_alipay.setImageResource(R.drawable.pay_type_selected);
                                    }

//                                    ll_pay3.setPadding(0, top, 0, 0);
                                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll_pay3.getLayoutParams();
                                    params.setMargins(0, top, 0, 0);
                                    ll_pay3.setLayoutParams(params);
                                }

//                                datas.add(bean);
                            }



                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }


                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });
            }
        });

    }

    private Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 0:
//                    ll_pay1.setPadding(0, top, 0, 0);
//                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll_pay1.getLayoutParams();
//                    FrameLayout.LayoutParams params= (FrameLayout.LayoutParams)ll_pay1.getLayoutParams();
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll_pay1.getLayoutParams();
                    params.setMargins(0, 100, 0, 0);
                    ll_pay1.setLayoutParams(params);

//                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                    lp.setMargins(0, 100, 0, 0);
//                    ll_pay1.setLayoutParams(lp);
                    break;

                default:
                    break;
            }
            return false;
        }
    });

    private void cycling() {
        Log.e("ura===order_detail", "==="+order_id);

        HttpHelper.get(context, Urls.order_detail+order_id, new TextHttpResponseHandler() {
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

                            Log.e("ura===order_detail1", responseString + "===" + result.data);

                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);

                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                            if(null != bean.getOrder_sn()){
                                Log.e("ura===order_detail2", bean.getOrder_sn()+"===" + bean.getCar_number()+"===" + bean.getLock_id());

                                order_id = bean.getOrder_id();

                                tv_order_amount.setText("¥"+bean.getOrder_amount());

                                if("0.00".equals(bean.getOrder_amount()) || "0".equals(bean.getOrder_amount())){
                                    Toast.makeText(context,"已为您取消本次订单，谢谢使用",Toast.LENGTH_SHORT).show();
                                    end();
                                }else if(Double.parseDouble(bean.getOrder_amount())<=balance){
                                    pay();
                                }else{

                                }

                            }

                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });
            }
        });

    }



    private void end(){
        if(order_type>=2){
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("flag", true);
            startActivity(intent);
        }else{
            Intent intent = new Intent(context, MyOrderDetailActivity.class);
            intent.putExtra("order_id", order_id);
            startActivity(intent);
        }

        scrollToFinishActivity();
    }


    private void pay(){
        RequestParams params = new RequestParams();
        params.put("payment_id", payment_id);     //支付方式ID 支付方式 1：余额支付 2：微信app支付 3：支付宝app支付 4：微信小程序支付 5：支付宝小程序支付 6：微信h5支付 7：支付宝h5支付
        params.put("order_id", order_id);      //订单ID
        params.put("order_type", order_type);     //订单类型 1骑行订单 2套餐卡订单 3充值订单 4认证充值订单 5调度费订单 6赔偿费订单

        Log.e("spa===pay", order_amount+"==="+payment_id+"==="+order_id+"==="+order_type);

        HttpHelper.post(context, Urls.pay, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在提交");
                    loadingDialog.show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("spa===pay===fail", responseString+"==="+throwable.toString());

                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {


                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                    Log.e("spa===pay1", payment_id+"==="+responseString+"==="+result.getStatus_code());

                    if(result.getStatus_code()==0){
                        if(payment_id==1){  //余额
                            Toast.makeText(context,"支付成功",Toast.LENGTH_SHORT).show();
                            end();
                        }else if(payment_id==2){    //微信
                            isToPay = true;

//                          OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);

                            JSONObject jsonObject2 = new JSONObject(result.getData());

                            String payinfo = jsonObject2.getString("payinfo");

                            Log.e("pay===2", payinfo +"===");

//                          String payinfo = "{\"appid\":\"wx86d98ec252f67d07\",\"noncestr\":\"p8tSBfNaKTrDxuA37JKUBpDm1qe4maqb\",\"package\":\"Sign=WXPay\",\"partnerid\":\"1489420872\",\"prepayid\":\"wx11200539512208dad2422cb81357385600\",\"sign\":\"7D866235FE557709FC14D3FE60543257\",\"timestamp\":1576065939}";

                            JSONObject jsonObject = new JSONObject(payinfo);


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
                                Log.e("pay===3", payinfo +"===");
                                api.sendReq(req);
                            } else {
                                Log.e("pay===4", payinfo +"===");
                                Toast.makeText(context, "请下载最新版微信App", Toast.LENGTH_LONG).show();
                            }

//                            startActivity(new Intent(WXEntryActivity.this, PayActivity.class));
//
                        }else if(payment_id==3){    //支付宝
                            isToPay = true;

                            final  JSONObject jsonObject = new JSONObject(result.getData());
                            Runnable payRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    // 构造PayTask 对象
                                    PayTask alipay = new PayTask(SettlementPlatformActivity.this);
                                    // 调用支付接口，获取支付结果
//                                  String result = null;
                                    Map<String, String> result = null;

                                    try {
                                        Log.e("pay===2", jsonObject.getString("payinfo")+"===");

                                        String payinfo = jsonObject.getString("payinfo");

//                                      payinfo = "partner=\"2088621211667181\"&seller_id=\"publicbicycles@163.com\"&out_trade_no=\"M201912111949196915\"&subject=\"\u652f\u4ed8M201912111949196915\"&body=\"\u652f\u4ed8\u67d2\u739b\u6708\u5361\u8ba2\u5355\"&total_fee=\"45.90\"&notify_url=\"http://app.7mate.cn/App/AlipayMonth/callback.html\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&return_url=\"m.alipay.com\"&sign=\"dbnW7cObywGWjTz09urH8TEHedJ73vNCnDinmnV24lSap302ePopAD3DG28LZMCSwjjRJq5ANTfsE8CwbLmsFcYQoj9MXFjLL3buM16eppmCQr1SP3xEY9r2eLbTnN%2FQypapYP890qW9l3weqoaJWyaVbI%2BvEJSvvbjyJt8ZLsI%3D\"&sign_type=\"RSA\"";

//                                      payinfo = "app_id=\"2016082000295641\"&format=\"JSON\"&charset=\"utf-8\"&sign_type=\"RSA2\"&version=\"1.0\"&notify_url=\"http%3A%2F%2Fwww.7mate.cn%2Fapi%2Fpayment%2Fali_notify\"&timestamp=\"2019-12-11+19%3A25%3A57\"&biz_content=\"%7B%22out_trade_no%22%3A%2220191211192557735862468%22%2C%22\n" +
//                                        "total_fee%22%3A100%2C%22body%22%3A%227MA%5Cu51fa%5Cu884c%5Cu9a91%5Cu884c%5Cu8ba2%5Cu535519121118210723625416%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%7D\"&method=\"alipay.trade.app.pay\"&sign=\"YG9K2BgCwBEK%2BoJ549B4AQLp%2FQa4BsbUWjBcKm7fY16%2FpLv1h1Kq1s%2B1dhBJwh%2B2ZRRYNYjkDWEeUGkkJ7U%2F%2By06e674lM8wzQPeCOBWH%2BjygJ2UJd9U2e%2FdSbQrwlIFBCmtJlagD5%2FIcgczWbYjGVRiT8ZxZpgx7OcsbIBh9UlQNzh6KKd9w0fXbkDKf5BfmAjZwT6ENaJ5Ir%2FBiC%2FAw%2Fxb%2Fe4nNITti5TqncrF8uN2FUzNbdPYuYkoFPiSUWtxZHgvyK6y83Mo%2Bo76gNTRmGAhOuvWckLF%2B2senT9Q7YTka2MNxMeBZqqRFlL8qK42%2FQ307s7UCrd0OSuJAXYv8w%3D%3D\"";

//                                      payinfo = "partner=\"2088621211667181\"&seller_id=\"publicbicycles@163.com\"&out_trade_no=\"M201912111949196915\"&subject=\"\u652f\u4ed8M201912111949196915\"&body=\"\u652f\u4ed8\u67d2\u739b\u6708\u5361\u8ba2\u5355\"&total_fee=\"45.90\"&notify_url=\"http://app.7mate.cn/App/AlipayMonth/callback.html\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&return_url=\"m.alipay.com\"&sign=\"dbnW7cObywGWjTz09urH8TEHedJ73vNCnDinmnV24lSap302ePopAD3DG28LZMCSwjjRJq5ANTfsE8CwbLmsFcYQoj9MXFjLL3buM16eppmCQr1SP3xEY9r2eLbTnN%2FQypapYP890qW9l3weqoaJWyaVbI%2BvEJSvvbjyJt8ZLsI%3D\"&sign_type=\"RSA\"";


//                                      result = alipay.pay(payinfo, true);
                                        result = alipay.payV2(payinfo, true);
                                        Log.e("msp", result.toString());

//
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Message msg = new Message();
                                    msg.what = SDK_PAY_FLAG;
                                    msg.obj = result;
                                    mHandler.sendMessage(msg);
                                }
                            };
                            Thread payThread = new Thread(payRunnable);
                            payThread.start();
                        }
                    }else{
                        ToastUtil.showMessageApp(context, result.getMessage());

//                        mCropLayout2.setVisibility(View.VISIBLE);
//                        ll_input.setVisibility(View.GONE);
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

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
//                    PayResult payResult = new PayResult((String) msg.obj);

                    Log.e("mHandler===1", msg.obj+"===");

                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);

                    Log.e("mHandler===2", payResult+"===");

                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档

                    query_order();


//                    if (TextUtils.equals(resultStatus, "9000")) {
//                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
////                        showAlert(PayDemoActivity.this, "pay_success" + payResult);
//                        Toast.makeText(context, "pay_success" + payResult, Toast.LENGTH_SHORT).show();
//                    } else {
//                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
////                        showAlert(PayDemoActivity.this, "pay_failed" + payResult);
//                        Toast.makeText(context, "pay_failed" + payResult, Toast.LENGTH_SHORT).show();
//                    }

//                    if (TextUtils.equals(resultStatus, "9000")) {
//                        Toast.makeText(context, "恭喜您,支付成功", Toast.LENGTH_SHORT).show();
//
////                        if("1".equals(gamestatus)){
////                            UIHelper.goToAct(context,MainActivity.class);
////                        }else{
////                            Intent intent = new Intent(context, WebviewActivity.class);
////                            intent.putExtra("link", "http://www.7mate.cn/Home/Games/index.html");
////                            intent.putExtra("title", "活动详情");
////                            startActivity(intent);
////                        }
////
////                        scrollToFinishActivity();
//                    } else {
//                        if (TextUtils.equals(resultStatus, "8000")) {
//                            Toast.makeText(context, "支付结果确认中", Toast.LENGTH_SHORT).show();
//                        } else {
//                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
//                            Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT).show();
//                        }
//                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    private void query_order() {
        Log.e("spa===query_order", order_id+"==="+order_type);

        RequestParams params = new RequestParams();
        params.put("order_id", order_id);       //订单ID
        params.put("order_type", order_type); //订单类型 1骑行订单 2套餐卡订单 3充值订单 4认证充值订单 5调度费订单 6赔偿费订单
        HttpHelper.get(context, Urls.query_order, params, new TextHttpResponseHandler() {
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

                            Log.e("spa===query_order1", order_type+ "===" +isRemain+ "===" +responseString);

                            if(result.getStatus_code()==200){
                                if(order_type==1){
                                    end();
                                }else if(order_type==4){
//                                    Intent rIntent = new Intent();
//                                    rIntent.putExtra("codenum", codenum);
//                                    rIntent.putExtra("m_nowMac", m_nowMac);
//                                    rIntent.putExtra("type", type);
//                                    rIntent.putExtra("lock_no", bean.getLock_no());
//                                    rIntent.putExtra("bleid", bleid);
//                                    rIntent.putExtra("deviceuuid", deviceuuid);
//                                    rIntent.putExtra("price", price);
//                                    rIntent.putExtra("electricity", electricity);
//                                    rIntent.putExtra("mileage", mileage);
//                                    setResult(RESULT_OK, rIntent);

//                                    setResult(RESULT_OK);
//                                    scrollToFinishActivity();

                                    setResult(RESULT_OK);
//                                    UIHelper.goToAct(context, MainActivity.class);
                                    scrollToFinishActivity();
                                }else{
                                    if(isRemain){
                                        order_type = order_type2;
                                        order_id = order_id2;

                                        Log.e("spa===query_order2", order_type+ "===" +order_id);

                                        iv_balance.setImageResource(R.drawable.pay_type_normal);
                                        iv_wechat.setImageResource(R.drawable.pay_type_normal);
                                        iv_alipay.setImageResource(R.drawable.pay_type_normal);

                                        user();
                                    }else {

                                        Intent intent = new Intent(context, MainActivity.class);
                                        intent.putExtra("flag", true);
                                        startActivity(intent);
                                        scrollToFinishActivity();
                                    }
                                }
                            }

//                            for (int i = 0; i < array.length(); i++) {
//
//                                Log.e("ura===payments2", "==="+array.getJSONObject(i).toString());
//
//                                PaymentBean bean = JSON.parseObject(array.getJSONObject(i).toString(), PaymentBean.class);
//
//                                Log.e("ura===payments3", "==="+bean.getId());
//
//                                if(bean.getId()==1){
//                                    ll_pay1.setVisibility(View.VISIBLE);
//                                }else if(bean.getId()==2){      //wechat
//                                    ll_pay2.setVisibility(View.VISIBLE);
//                                }else if(bean.getId()==3){      //alipay
//                                    ll_pay3.setVisibility(View.VISIBLE);
//                                }
//
////                                datas.add(bean);
//                            }



                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }


                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });
            }
        });

    }


    @Override
    public void onClick(View v) {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (v.getId()){
            case R.id.ll_backBtn:
                setResult(RESULT_OK);
                scrollToFinishActivity();
                break;



            case R.id.ll_pay1:  //balance
                Log.e("ll_pay1===", "===");

                iv_balance.setImageResource(R.drawable.pay_type_selected);
                iv_alipay.setImageResource(R.drawable.pay_type_normal);
                iv_wechat.setImageResource(R.drawable.pay_type_normal);
                payment_id = 1;
                break;

            case R.id.ll_pay2:  //wechat
                Log.e("ll_pay2===", "===");

                iv_balance.setImageResource(R.drawable.pay_type_normal);
                iv_wechat.setImageResource(R.drawable.pay_type_selected);
                iv_alipay.setImageResource(R.drawable.pay_type_normal);

                payment_id = 2;
                break;

            case R.id.ll_pay3:  //alipay
                Log.e("ll_pay3===", "===");

                iv_balance.setImageResource(R.drawable.pay_type_normal);
                iv_wechat.setImageResource(R.drawable.pay_type_normal);
                iv_alipay.setImageResource(R.drawable.pay_type_selected);

                payment_id = 3;
                break;





            case R.id.settlement_platform_submitBtn:
                if (access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.e("spf===submitBtn", "===");
                pay();

                break;

            case R.id.tv_recharge:
//                UIHelper.goToAct(context, RechargeActivity.class);

//                intent.putExtra("order_type", order_type);
//                intent.putExtra("order_id", order_id);

                order_type2 = order_type;
                order_id2 = order_id;

                isToPay = false;

                iv_balance.setImageResource(R.drawable.pay_type_normal);
                iv_wechat.setImageResource(R.drawable.pay_type_normal);
                iv_alipay.setImageResource(R.drawable.pay_type_normal);

                Intent intent = new Intent();
                intent.setClass(context, RechargeActivity.class);
                intent.putExtra("isRemain", true);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case 1:
                if (resultCode == RESULT_OK) {
//                    codenum = data.getStringExtra("codenum");
//                    m_nowMac = data.getStringExtra("m_nowMac");

                    Log.e("spf===onActivityResult", requestCode+"==="+resultCode);

                } else {
//                    Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                }


                break;


            default:
                break;

        }
    }


//    private void userRecharge(final String uid, final String access_token){
//        RequestParams params = new RequestParams();
//        params.put("uid",uid);
//        params.put("access_token",access_token);
//        params.put("rid",rid);
//        params.put("paytype",paytype);
//
//        Log.e("userRecharge===", rid+"==="+paytype);
//
//        HttpHelper.post(context, Urls.userRecharge, params, new TextHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                    loadingDialog.setTitle("正在提交");
//                    loadingDialog.show();
//                }
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
//                UIHelper.ToastError(context, throwable.toString());
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                try {
//                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                    if (result.getFlag().equals("Success")) {
//                        osn = result.getData();
//                        if ("1".equals(paytype)){
//                            show_alipay(osn,uid,access_token);
//                        }else {
//                            show_wxpay(osn,uid,access_token);
//                        }
//                    } else {
//                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
//            }
//        });
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }


}
