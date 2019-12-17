package cn.qimate.bike.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.apache.http.Header;
import org.json.JSONArray;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.BillBean;
import cn.qimate.bike.model.GlobalConfig;
import cn.qimate.bike.model.OrderBean;
import cn.qimate.bike.model.PaymentBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UserBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by yuanyi on 2019/12/9.
 */
public class SettlementPlatformActivity extends SwipeBackActivity implements View.OnClickListener{


    private Context context;
    private LoadingDialog loadingDialog;
    private LinearLayout backImg;
    private LinearLayout submitBtn;

    private TextView tv_unpay_route_car_number;
    private TextView tv_unpay_route_car_start_time;
    private TextView tv_unpay_route_car_end_time;
    private TextView tv_unpay_route_order_amount;

    private TextView tv_balance;
    private TextView tv_recharge;
    private TextView tv_order_amount;
    private LinearLayout ll_pay1;
    private LinearLayout ll_pay2;
    private LinearLayout ll_pay3;

    private int order_id;
    private int pay_scene;
    private double balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement_platform);
        context = this;

        initView();
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        backImg = (LinearLayout) findViewById(R.id.ll_backBtn);
        tv_order_amount = (TextView)findViewById(R.id.tv_order_amount);
        tv_balance = (TextView)findViewById(R.id.tv_balance);
        tv_recharge = (TextView)findViewById(R.id.tv_recharge);
        ll_pay1 = (LinearLayout) findViewById(R.id.ll_pay1);
        ll_pay2 = (LinearLayout) findViewById(R.id.ll_pay2);
        ll_pay3 = (LinearLayout) findViewById(R.id.ll_pay3);
//        tv_unpay_route_car_number = (TextView)findViewById(R.id.tv_unpay_route_car_number);
//        tv_unpay_route_car_start_time = (TextView)findViewById(R.id.tv_unpay_route_car_start_time);
//        tv_unpay_route_car_end_time = (TextView)findViewById(R.id.tv_unpay_route_car_end_time);
//        tv_unpay_route_order_amount = (TextView)findViewById(R.id.tv_unpay_route_order_amount);
        submitBtn = (LinearLayout)findViewById(R.id.settlement_platform_submitBtn);


        backImg.setOnClickListener(this);
        tv_recharge.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        pay_scene = getIntent().getIntExtra("pay_scene", 1);
        order_id = getIntent().getIntExtra("order_id", 0);


        user();
    }

    private void payments() {
        Log.e("ura===payments", pay_scene+"===");

        RequestParams params = new RequestParams();
        params.put("scene", pay_scene); //支付场景 1骑行订单支付 2充值订单(包括认证充值)支付 3调度、赔偿单支付
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

                            Log.e("ura===payments1", responseString + "===" + result.data);

                            JSONArray array = new JSONArray(result.getData());

                            for (int i = 0; i < array.length(); i++) {

                                Log.e("ura===payments2", "==="+array.getJSONObject(i).toString());

                                PaymentBean bean = JSON.parseObject(array.getJSONObject(i).toString(), PaymentBean.class);

                                Log.e("ura===payments3", "==="+bean.getId());

                                if(bean.getId()==1){
                                    ll_pay1.setVisibility(View.VISIBLE);
                                }else if(bean.getId()==2){
                                    ll_pay2.setVisibility(View.VISIBLE);
                                }else if(bean.getId()==3){
                                    ll_pay3.setVisibility(View.VISIBLE);
                                }

//                                datas.add(bean);
                            }



                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

//                            if(null != bean.getOrder_sn()){
//                                Log.e("ura===payments2", bean.getOrder_sn()+"===" + bean.getCar_number()+"===" + bean.getLock_id());
//
//                                tv_unpay_route_car_number.setText(bean.getCar_number());
//                                tv_unpay_route_car_start_time.setText(bean.getCar_start_time());
//                                tv_unpay_route_car_end_time.setText(bean.getCar_end_time());
//                                tv_unpay_route_order_amount.setText(bean.getOrder_amount());
//                            }

                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });
            }
        });

    }

    private void user() {
        Log.e("spfa===user", "===");

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
                            tv_balance.setText("当前余额￥"+balance);


                            cycling();

                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });
            }
        });

    }

    private void cycling() {
        Log.e("ura===cycling", "==="+order_id);

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

                            Log.e("ura===cycling1", responseString + "===" + result.data);

                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);

                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                            if(null != bean.getOrder_sn()){
                                Log.e("ura===cycling2", bean.getOrder_sn()+"===" + bean.getCar_number()+"===" + bean.getLock_id());

                                order_id = bean.getOrder_id();

                                tv_order_amount.setText(bean.getOrder_amount());

                                if("0.00".equals(bean.getOrder_amount()) || "0".equals(bean.getOrder_amount())){
                                    Toast.makeText(context,"已为您取消本次订单，谢谢使用",Toast.LENGTH_SHORT).show();
                                    end();
                                }else if(Double.parseDouble(bean.getOrder_amount())<balance){
                                    pay();
                                }else{
                                    payments();
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
        Intent intent = new Intent(context, MyOrderDetailActivity.class);
        intent.putExtra("order_id", order_id);
        startActivity(intent);
        scrollToFinishActivity();
    }

    private void pay(){
        RequestParams params = new RequestParams();
        params.put("payment_id",1);     //支付方式ID 支付方式 1：余额支付 2：微信app支付 3：支付宝app支付 4：微信小程序支付 5：支付宝小程序支付 6：微信h5支付 7：支付宝h5支付
        params.put("order_id", order_id);      //订单ID
        params.put("order_type",1);     //订单类型 1骑行订单 2购买骑行卡订单 3调度费订单 4赔偿费订单 5充值订单(普通充值、认证充值)

        Log.e("spa===pay", order_id+"===");

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
                    Log.e("spa===pay1", responseString+"===");

                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                    Toast.makeText(context,"支付成功",Toast.LENGTH_SHORT).show();

                    end();

//                    OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);

//                    api = WXAPIFactory.createWXAPI(context, "wx86d98ec252f67d07", false);
//                    api.registerApp("wx86d98ec252f67d07");
//                    JSONObject jsonObject2 = new JSONObject(result.getData());
//
//                    String payinfo = jsonObject2.getString("payinfo");
//
//                    Log.e("pay===2", payinfo +"===");
//
////                    String payinfo = "{\"appid\":\"wx86d98ec252f67d07\",\"noncestr\":\"p8tSBfNaKTrDxuA37JKUBpDm1qe4maqb\",\"package\":\"Sign=WXPay\",\"partnerid\":\"1489420872\",\"prepayid\":\"wx11200539512208dad2422cb81357385600\",\"sign\":\"7D866235FE557709FC14D3FE60543257\",\"timestamp\":1576065939}";
//
//                    JSONObject jsonObject = new JSONObject(payinfo);
//
//
//                    PayReq req = new PayReq();
//                    req.appId = jsonObject.getString("appid");// wpay.getAppid();//
//                    // 微信appId
//                    req.packageValue = jsonObject.getString("package");// wpay.getPackageValue();//
//                    // 包
//                    req.extData = "app data"; // optional
//                    req.timeStamp = jsonObject.getString("timestamp");// wpay.getTimeStamp();//
//                    // 时间戳
//                    req.partnerId = jsonObject.getString("partnerid");// wpay.getPartnerId();//
//                    // 商户号"
//                    req.prepayId = jsonObject.getString("prepayid");// wpay.getPrepayId();//
//                    // 预支付订单号
//                    req.nonceStr = jsonObject.getString("noncestr");// wpay.getNonceStr();//
//                    // 随机字符串
//                    req.sign = jsonObject.getString("sign");// wpay.getSign();//
//                    // 后台返回的签名
//                    // 调微信支付
//                    if (api.isWXAppInstalled() && api.isWXAppSupportAPI()) {
//                        api.sendReq(req);
//                    } else {
//                        Toast.makeText(context, "请下载最新版微信App", Toast.LENGTH_LONG).show();
//                    }

//                    final  JSONObject jsonObject = new JSONObject(result.getData());
//                    Runnable payRunnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            // 构造PayTask 对象
//                            PayTask alipay = new PayTask(UnpayRouteActivity.this);
//                            // 调用支付接口，获取支付结果
////                            String result = null;
//                            Map<String, String> result = null;
//
//                            try {
//                                Log.e("pay===2", jsonObject.getString("payinfo")+"===");
//
//                                String payinfo = jsonObject.getString("payinfo");
//
////                                payinfo = "partner=\"2088621211667181\"&seller_id=\"publicbicycles@163.com\"&out_trade_no=\"M201912111949196915\"&subject=\"\u652f\u4ed8M201912111949196915\"&body=\"\u652f\u4ed8\u67d2\u739b\u6708\u5361\u8ba2\u5355\"&total_fee=\"45.90\"&notify_url=\"http://app.7mate.cn/App/AlipayMonth/callback.html\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&return_url=\"m.alipay.com\"&sign=\"dbnW7cObywGWjTz09urH8TEHedJ73vNCnDinmnV24lSap302ePopAD3DG28LZMCSwjjRJq5ANTfsE8CwbLmsFcYQoj9MXFjLL3buM16eppmCQr1SP3xEY9r2eLbTnN%2FQypapYP890qW9l3weqoaJWyaVbI%2BvEJSvvbjyJt8ZLsI%3D\"&sign_type=\"RSA\"";
//
////                                payinfo = "app_id=\"2016082000295641\"&format=\"JSON\"&charset=\"utf-8\"&sign_type=\"RSA2\"&version=\"1.0\"&notify_url=\"http%3A%2F%2Fwww.7mate.cn%2Fapi%2Fpayment%2Fali_notify\"&timestamp=\"2019-12-11+19%3A25%3A57\"&biz_content=\"%7B%22out_trade_no%22%3A%2220191211192557735862468%22%2C%22\n" +
////                                        "total_fee%22%3A100%2C%22body%22%3A%227MA%5Cu51fa%5Cu884c%5Cu9a91%5Cu884c%5Cu8ba2%5Cu535519121118210723625416%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%7D\"&method=\"alipay.trade.app.pay\"&sign=\"YG9K2BgCwBEK%2BoJ549B4AQLp%2FQa4BsbUWjBcKm7fY16%2FpLv1h1Kq1s%2B1dhBJwh%2B2ZRRYNYjkDWEeUGkkJ7U%2F%2By06e674lM8wzQPeCOBWH%2BjygJ2UJd9U2e%2FdSbQrwlIFBCmtJlagD5%2FIcgczWbYjGVRiT8ZxZpgx7OcsbIBh9UlQNzh6KKd9w0fXbkDKf5BfmAjZwT6ENaJ5Ir%2FBiC%2FAw%2Fxb%2Fe4nNITti5TqncrF8uN2FUzNbdPYuYkoFPiSUWtxZHgvyK6y83Mo%2Bo76gNTRmGAhOuvWckLF%2B2senT9Q7YTka2MNxMeBZqqRFlL8qK42%2FQ307s7UCrd0OSuJAXYv8w%3D%3D\"";
//
////                                payinfo = "partner=\"2088621211667181\"&seller_id=\"publicbicycles@163.com\"&out_trade_no=\"M201912111949196915\"&subject=\"\u652f\u4ed8M201912111949196915\"&body=\"\u652f\u4ed8\u67d2\u739b\u6708\u5361\u8ba2\u5355\"&total_fee=\"45.90\"&notify_url=\"http://app.7mate.cn/App/AlipayMonth/callback.html\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&return_url=\"m.alipay.com\"&sign=\"dbnW7cObywGWjTz09urH8TEHedJ73vNCnDinmnV24lSap302ePopAD3DG28LZMCSwjjRJq5ANTfsE8CwbLmsFcYQoj9MXFjLL3buM16eppmCQr1SP3xEY9r2eLbTnN%2FQypapYP890qW9l3weqoaJWyaVbI%2BvEJSvvbjyJt8ZLsI%3D\"&sign_type=\"RSA\"";
//
//
////                                result = alipay.pay(payinfo, true);
//                                result = alipay.payV2(payinfo, true);
//                                Log.e("msp", result.toString());
//
////
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            Message msg = new Message();
//                            msg.what = SDK_PAY_FLAG;
//                            msg.obj = result;
//                            mHandler.sendMessage(msg);
//                        }
//                    };
//                    Thread payThread = new Thread(payRunnable);
//                    payThread.start();

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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (v.getId()){
            case R.id.ll_backBtn:
                scrollToFinishActivity();
                break;

            case R.id.tv_recharge:
                UIHelper.goToAct(context, RechargeActivity.class);
                break;

            case R.id.settlement_platform_submitBtn:
                if (access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.e("spf===submitBtn", "===");

                pay();

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
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
