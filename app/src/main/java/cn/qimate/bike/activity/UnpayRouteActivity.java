package cn.qimate.bike.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.config.LockType;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.alipay.PayResult;
import cn.qimate.bike.ble.BLEService;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.OrderBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.ToastUtil;

/**
 * Created by yuanyi on 2019/12/9.
 */
public class UnpayRouteActivity extends SwipeBackActivity implements View.OnClickListener{
    private static final int SDK_PAY_FLAG = 1;
    private IWXAPI api;

    private Context context;
    private LoadingDialog loadingDialog;
    private LinearLayout backImg;
    private LinearLayout submitBtn;

    private TextView tv_unpay_route_car_number;
    private TextView tv_unpay_route_car_start_time;
    private TextView tv_unpay_route_car_end_time;
    private TextView tv_unpay_route_order_amount;

    private int order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unpay_route);
        context = this;

        initView();
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        backImg = (LinearLayout) findViewById(R.id.ll_backBtn);
        tv_unpay_route_car_number = (TextView)findViewById(R.id.tv_unpay_route_car_number);
        tv_unpay_route_car_start_time = (TextView)findViewById(R.id.tv_unpay_route_car_start_time);
        tv_unpay_route_car_end_time = (TextView)findViewById(R.id.tv_unpay_route_car_end_time);
        tv_unpay_route_order_amount = (TextView)findViewById(R.id.tv_unpay_route_order_amount);
        submitBtn = (LinearLayout)findViewById(R.id.unpay_route_submitBtn);

        backImg.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

//        cycling();
        pay();
    }

    private void cycling() {
        Log.e("ura===cycling", "===");

        HttpHelper.get(context, Urls.cycling, new TextHttpResponseHandler() {
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

//                                oid = bean.getOrder_sn();
//                                codenum = bean.getCar_number();
//                                type = ""+bean.getLock_id();
//                                m_nowMac = bean.getCar_lock_mac();
//                                force_backcar = bean.getForce_backcar();  //TODO
//
//                                SharedPreferencesUrls.getInstance().getString("type", type);

//                                tv_biking_codenum.setText(codenum);     //TODO
//                                tv_estimated_cost.setText(bean.getEstimated_cost());
//                                tv_estimated_cost2.setText(bean.getEstimated_cost());
//                                tv_car_start_time.setText(bean.getCar_start_time());
//                                tv_car_start_time2.setText(bean.getCar_start_time());
//                                tv_car_mileage.setText(mileage);
//                                tv_car_electricity.setText(electricity);

                                tv_unpay_route_car_number.setText(bean.getCar_number());
                                tv_unpay_route_car_start_time.setText(bean.getCar_start_time());
                                tv_unpay_route_car_end_time.setText(bean.getCar_end_time());
                                tv_unpay_route_order_amount.setText(bean.getOrder_amount());


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
                scrollToFinishActivity();
                break;

            case R.id.unpay_route_submitBtn:
                if (access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.e("rid===", "===");

                pay();

                break;
        }
    }



    private void pay(){
        RequestParams params = new RequestParams();
        params.put("payment_id",3);
        params.put("order_id", 3);
        params.put("order_type",1);

//        payment_id	Int
//        支付方式ID 支付方式 1：余额支付 2：微信app支付 3：支付宝app支付 4：微信小程序支付 5：支付宝小程序支付 6：微信h5支付 7：支付宝h5支付
//
//        order_id	Int
//        订单ID
//
//        order_type	Int
//        订单类型 1骑行订单 2购买骑行卡订单 3调度费订单 4赔偿费订单 5充值订单(普通充值、认证充值)

        Log.e("pay===", order_id+"===");

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
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Log.e("pay===1", responseString+"===");

                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                    OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);
                    final  JSONObject jsonObject = new JSONObject(result.getData());

//                    api = WXAPIFactory.createWXAPI(context, "wx86d98ec252f67d07", false);
//                    api.registerApp("wx86d98ec252f67d07");
//                    JSONObject jsonObject2 = new JSONObject(result.getData());
//
//                    JSONObject jsonObject = new JSONObject(jsonObject2.getString("payinfo"));
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
//
//                    Log.e("pay===2", req.appId +"===" +req.sign);

                    Runnable payRunnable = new Runnable() {
                        @Override
                        public void run() {
                            // 构造PayTask 对象
                            PayTask alipay = new PayTask(UnpayRouteActivity.this);
                            // 调用支付接口，获取支付结果
                            String result = null;

                            try {
                                Log.e("pay===2", jsonObject.getString("payinfo")+"===");

                                result = alipay.pay(jsonObject.getString("payinfo"), true);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Message msg = new Message();
                            msg.what = SDK_PAY_FLAG;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    };
                    // 必须异步调用
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();

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

//                        if("1".equals(gamestatus)){
//                            UIHelper.goToAct(context,MainActivity.class);
//                        }else{
//                            Intent intent = new Intent(context, WebviewActivity.class);
//                            intent.putExtra("link", "http://www.7mate.cn/Home/Games/index.html");
//                            intent.putExtra("title", "活动详情");
//                            startActivity(intent);
//                        }
//
//                        scrollToFinishActivity();
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
