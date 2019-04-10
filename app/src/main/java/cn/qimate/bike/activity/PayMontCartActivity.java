package cn.qimate.bike.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.Header;
import org.json.JSONObject;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.alipay.PayResult;
import cn.qimate.bike.core.common.AppManager;
import cn.qimate.bike.core.common.DensityUtils;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.PayMonthCartBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * 购买月卡
 * Created by Administrator on 2017/9/9 0009.
 */

public class PayMontCartActivity extends SwipeBackActivity implements View.OnClickListener {
    private static final int SDK_PAY_FLAG = 1;
    private IWXAPI api;
    private Context context;
    private LoadingDialog loadingDialog;
    private ImageView backImg;
    private TextView title;
    private RelativeLayout headLayout;
    private TextView moneyText;
    private TextView daysText;

    private RelativeLayout alipayTypeLayout,WeChatTypeLayout,balanceTypeLayout;
    private RelativeLayout moreLayout;
    private LinearLayout moreLayout2;
    private ImageView alipayTypeImage,WeChatTypeImage,balanceTypeImage;
    private LinearLayout type1Layout,type2Layout,type3Layout;
    private TextView type1Text,type2Text,type3Text;
    private TextView type1Text2,type1Text3,type2Text2,type2Text3;
    private TextView days1Text,days2Text,days3Text;
    private Button submitBtn;
    private String paytype = "1";
    private String osn = "";
    private int type = 2;
    private String gamestatus = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_pay_month_cart);
        context = this;
        IntentFilter filter = new IntentFilter("data.broadcast.rechargeAction");
        registerReceiver(broadcastReceiver, filter);
        initView();
    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if("1".equals(gamestatus)){
                UIHelper.goToAct(context,MainActivity.class);
            }else{
                Intent intent2 = new Intent(context, WebviewActivity.class);
                intent2.putExtra("link", "http://www.7mate.cn/Home/Games/index.html");
                intent2.putExtra("title", "活动详情");
                startActivity(intent2);
            }
            scrollToFinishActivity();
        }
    };
    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

//        dialog2 = new Dialog(context, R.style.main_publishdialog_style);
//        View tagView = LayoutInflater.from(context).inflate(R.layout.dialog_maintenance, null);
//        dialog2.setContentView(tagView);
//        dialog2.setCanceledOnTouchOutside(false);
//
//        tagMainLayout = tagView.findViewById(R.id.dialog_maintenance_mainLayout);
//        tagFlowLayout = tagView.findViewById(R.id.dialog_maintenance_flowlayout);
//        closeLayout = tagView.findViewById(R.id.dialog_maintenance_closeLayout);
//        affirmLayout = tagView.findViewById(R.id.dialog_maintenance_affirmLayout);
//
//        LinearLayout.LayoutParams params4 = (LinearLayout.LayoutParams)tagMainLayout.getLayoutParams();
//        params4.width = DisplayUtil.getWindowWidth(activity) * 3 / 5;
//        tagMainLayout.setLayoutParams(params4);
//
//        tagAdapter = new TagAdapter<TagBean>(tagDatas) {
//            @Override
//            public View getView(FlowLayout parent, int position, TagBean bean) {
//                TextView tag = (TextView) LayoutInflater.from(context).inflate(R.layout.ui_tag, tagFlowLayout, false);
//                tag.setText(bean.getName());
//                return tag;
//            }
//        };
//        tagFlowLayout.setAdapter(tagAdapter);


        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("购买月卡");
        headLayout = (RelativeLayout) findViewById(R.id.ui_payMonth_cart_headLayout);
        // 设置广告高度为屏幕高度0.6倍
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) headLayout.getLayoutParams();
        params.width = getWindowManager().getDefaultDisplay().getWidth() - DensityUtils.dip2px(context,20);
        params.height = (getWindowManager().getDefaultDisplay().getWidth() - DensityUtils.dip2px(context,20)) * 638 / 1010;
        headLayout.setLayoutParams(params);

        moneyText = (TextView) findViewById(R.id.ui_payMonth_card_moneyText);
        daysText = (TextView)findViewById(R.id.ui_payMonth_card_daysText);

        alipayTypeLayout = (RelativeLayout)findViewById(R.id.ui_payMonth_cart_alipayTypeLayout);
        WeChatTypeLayout = (RelativeLayout)findViewById(R.id.ui_payMonth_cart_WeChatTypeLayout);
        balanceTypeLayout = (RelativeLayout)findViewById(R.id.ui_payMonth_cart_balanceTypeLayout);
        alipayTypeImage = (ImageView)findViewById(R.id.ui_payMonth_cart_alipayTypeImage);
        WeChatTypeImage = (ImageView)findViewById(R.id.ui_payMonth_cart_WeChatTypeImage);
        balanceTypeImage = (ImageView)findViewById(R.id.ui_payMonth_cart_balanceTypeImage);

        moreLayout = (RelativeLayout)findViewById(R.id.ui_payMonth_cart_moreLayout);
        moreLayout2 = (LinearLayout)findViewById(R.id.ui_payMonth_cart_moreLayout2);

        type1Layout = (LinearLayout)findViewById(R.id.ui_payMonth_cart_type1Layout);
        type2Layout = (LinearLayout)findViewById(R.id.ui_payMonth_cart_type2Layout);
        type3Layout = (LinearLayout)findViewById(R.id.ui_payMonth_cart_type3Layout);

        type1Text = (TextView)findViewById(R.id.ui_payMonth_cart_type1Text);
        type2Text = (TextView)findViewById(R.id.ui_payMonth_cart_type2Text);
        type3Text = (TextView)findViewById(R.id.ui_payMonth_cart_type3Text);

        type1Text2 = (TextView)findViewById(R.id.ui_payMonth_cart_type1Text2);
        type1Text3 = (TextView)findViewById(R.id.ui_payMonth_cart_type1Text3);
        type2Text2 = (TextView)findViewById(R.id.ui_payMonth_cart_type2Text2);
        type2Text3 = (TextView)findViewById(R.id.ui_payMonth_cart_type2Text3);

        type1Text2.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        type2Text2.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        days1Text = (TextView)findViewById(R.id.ui_payMonth_cart_days1Text);
        days2Text = (TextView)findViewById(R.id.ui_payMonth_cart_days2Text);
        days3Text = (TextView)findViewById(R.id.ui_payMonth_cart_days3Text);

        submitBtn = (Button)findViewById(R.id.ui_payMonth_cart_submitBtn);
        backImg.setOnClickListener(this);
        moreLayout.setOnClickListener(this);
        alipayTypeLayout.setOnClickListener(this);
        WeChatTypeLayout.setOnClickListener(this);
        balanceTypeLayout.setOnClickListener(this);
        type1Layout.setOnClickListener(this);
        type2Layout.setOnClickListener(this);
        type3Layout.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        UserMonth();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.ui_payMonth_cart_moreLayout:
                moreLayout.setVisibility(View.GONE);
                moreLayout2.setVisibility(View.VISIBLE);
                break;
            case R.id.ui_payMonth_cart_alipayTypeLayout:
                alipayTypeImage.setImageResource(R.drawable.pay_type_selected);
                WeChatTypeImage.setImageResource(R.drawable.pay_type_normal);
                balanceTypeImage.setImageResource(R.drawable.pay_type_normal);
                paytype = "1";
                break;
            case R.id.ui_payMonth_cart_WeChatTypeLayout:
                alipayTypeImage.setImageResource(R.drawable.pay_type_normal);
                WeChatTypeImage.setImageResource(R.drawable.pay_type_selected);
                balanceTypeImage.setImageResource(R.drawable.pay_type_normal);
                paytype = "2";
                break;
            case R.id.ui_payMonth_cart_balanceTypeLayout:
                paytype = "3";
                alipayTypeImage.setImageResource(R.drawable.pay_type_normal);
                WeChatTypeImage.setImageResource(R.drawable.pay_type_normal);
                balanceTypeImage.setImageResource(R.drawable.pay_type_selected);
                break;
            case R.id.ui_payMonth_cart_type1Layout:
                type = 1;
                moneyText.setText(type1Text.getText().toString().trim());
                daysText.setText(days1Text.getText().toString().trim());
                type1Layout.setBackgroundResource(R.drawable.shape_cart_secleced);
                type2Layout.setBackgroundResource(R.drawable.shape_feedback_edit);
                type3Layout.setBackgroundResource(R.drawable.shape_feedback_edit);
                type1Text.setTextColor(getResources().getColor(R.color.white));
                type1Text2.setTextColor(getResources().getColor(R.color.white));
                type1Text3.setTextColor(getResources().getColor(R.color.white));
                type2Text.setTextColor(getResources().getColor(R.color.black));
                type2Text2.setTextColor(getResources().getColor(R.color.black));
                type2Text3.setTextColor(getResources().getColor(R.color.black));
                type3Text.setTextColor(getResources().getColor(R.color.black));
                days1Text.setTextColor(getResources().getColor(R.color.white));
                days2Text.setTextColor(getResources().getColor(R.color.black));
                days3Text.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.ui_payMonth_cart_type2Layout:
                type = 2;
                moneyText.setText(type2Text.getText().toString().trim());
                daysText.setText(days2Text.getText().toString().trim());
                type1Layout.setBackgroundResource(R.drawable.shape_feedback_edit);
                type2Layout.setBackgroundResource(R.drawable.shape_cart_secleced);
                type3Layout.setBackgroundResource(R.drawable.shape_feedback_edit);
                type1Text.setTextColor(getResources().getColor(R.color.black));
                type1Text2.setTextColor(getResources().getColor(R.color.black));
                type1Text3.setTextColor(getResources().getColor(R.color.black));
                type2Text.setTextColor(getResources().getColor(R.color.white));
                type2Text2.setTextColor(getResources().getColor(R.color.white));
                type2Text3.setTextColor(getResources().getColor(R.color.white));
                type3Text.setTextColor(getResources().getColor(R.color.black));
                days1Text.setTextColor(getResources().getColor(R.color.black));
                days2Text.setTextColor(getResources().getColor(R.color.white));
                days3Text.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.ui_payMonth_cart_type3Layout:
                type = 4;
                moneyText.setText(type3Text.getText().toString().trim());
                daysText.setText(days3Text.getText().toString().trim());
                type1Layout.setBackgroundResource(R.drawable.shape_feedback_edit);
                type2Layout.setBackgroundResource(R.drawable.shape_feedback_edit);
                type3Layout.setBackgroundResource(R.drawable.shape_cart_secleced);
                type1Text.setTextColor(getResources().getColor(R.color.black));
                type1Text2.setTextColor(getResources().getColor(R.color.black));
                type1Text3.setTextColor(getResources().getColor(R.color.black));
                type2Text.setTextColor(getResources().getColor(R.color.black));
                type2Text2.setTextColor(getResources().getColor(R.color.black));
                type2Text3.setTextColor(getResources().getColor(R.color.black));
                type3Text.setTextColor(getResources().getColor(R.color.white));
                days1Text.setTextColor(getResources().getColor(R.color.black));
                days2Text.setTextColor(getResources().getColor(R.color.black));
                days3Text.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.ui_payMonth_cart_submitBtn:
                CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
//                customBuilder.setTitle("温馨提示").setMessage("使用优惠券，省X元           实付款：X元")
                customBuilder.setTitle("温馨提示").setMessage("是否确定支付?")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        userPay();
                    }
                });
                customBuilder.create().show();

                break;
            default:
                break;
        }
    }
    private void userPay(){
        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("paytype",paytype);
            params.put("type",type);
            HttpHelper.post(context, Urls.monthcard, params, new TextHttpResponseHandler() {
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
                            }else if("2".equals(paytype)){
                                show_wxpay(osn,uid,access_token);
                            }else {
                                banlancePay(osn,uid,access_token);
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
    }

    public void show_alipay(final String osn,String uid,String access_token) {
        Toast.makeText(context, "正在调起支付宝支付...", Toast.LENGTH_LONG).show();
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("osn", osn);
        HttpHelper.get(context, Urls.monthAlipay, params, new TextHttpResponseHandler() {
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
                                PayTask alipay = new PayTask(PayMontCartActivity.this);
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

                        if("1".equals(gamestatus)){
                            UIHelper.goToAct(context,MainActivity.class);
                        }else{
                            Intent intent = new Intent(context, WebviewActivity.class);
                            intent.putExtra("link", "http://www.7mate.cn/Home/Games/index.html");
                            intent.putExtra("title", "活动详情");
                            startActivity(intent);
                        }

                        scrollToFinishActivity();
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

    public void show_wxpay(final String osn,String uid,String access_token) {
        SharedPreferencesUrls.getInstance().putBoolean("isTreasure",false);
        Toast.makeText(context, "正在调起微信支付...", Toast.LENGTH_LONG).show();
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.add("osn", osn);
        HttpHelper.get(context, Urls.wxpay, params, new TextHttpResponseHandler() {
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

    //余额充值
    public void banlancePay(final String osn,String uid,String access_token) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("access_token", access_token);
        params.put("osn", osn);
        HttpHelper.post(context, Urls.payMonth, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()){
                    loadingDialog.show();
                    loadingDialog.setTitle("正在提交");
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        Toast.makeText(context, "恭喜您,支付成功", Toast.LENGTH_SHORT).show();

                        if("1".equals(gamestatus)){
                            UIHelper.goToAct(context,MainActivity.class);
                        }else{
                            Intent intent = new Intent(context, WebviewActivity.class);
                            intent.putExtra("link", "http://www.7mate.cn/Home/Games/index.html");
                            intent.putExtra("title", "活动详情");
                            startActivity(intent);
                        }

//                        http://www.7mate.cn/Home/Games/index.html?from=singlemessage

                        scrollToFinishActivity();
                    } else {
                        UIHelper.showToastMsg(context, result.getMsg(), R.drawable.ic_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
            }
        });
    }
    //获取月卡配置接口
    public void UserMonth() {
        RequestParams params = new RequestParams();
        params.put("uid",SharedPreferencesUrls.getInstance().getString("uid",""));
        params.put("access_token",SharedPreferencesUrls.getInstance().getString("access_token",""));
        HttpHelper.get(context, Urls.userMonth,params,new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()){
                    loadingDialog.show();
                    loadingDialog.setTitle("正在提交");
                }
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        PayMonthCartBean bean = JSON.parseObject(result.getData(),PayMonthCartBean.class);
                        type1Text.setText(bean.getMonth_money()+"元");
                        days1Text.setText("("+bean.getMonth_day()+"天不限次)");
                        type2Text.setText(bean.getQuarter_money()+"元");
                        days2Text.setText("("+bean.getQuarter_day()+"天不限次)");
                        type3Text.setText(bean.getWeek_money()+"元");
                        days3Text.setText("("+bean.getWeek_day()+"天不限次)");

                        moneyText.setText(type2Text.getText().toString().trim());
                        daysText.setText(days2Text.getText().toString().trim());

                        gamestatus = bean.getGamestatus();

                        Log.e("userMonth===", "==="+bean.getGamestatus());

                    } else {
                        UIHelper.showToastMsg(context, result.getMsg(), R.drawable.ic_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
            }
        });
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
