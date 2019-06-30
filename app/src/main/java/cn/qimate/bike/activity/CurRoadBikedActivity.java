package cn.qimate.bike.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import org.w3c.dom.Text;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.alipay.PayResult;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.CurRoadBikingBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;

/**
 * Created by Administrator on 2017/2/12 0012.
 */

public class CurRoadBikedActivity extends SwipeBackActivity implements View.OnClickListener{

    private static final int SDK_PAY_FLAG = 1;
    private IWXAPI api;

    private Context context;
    private LoadingDialog loadingDialog;
    private LoadingDialog payLoadingDialog;
    private ImageView backImg;
    private TextView title;

    private TextView bikeCode;
    private TextView bikeNum;
    private TextView startTime;
    private TextView endTime;
    private TextView timeText;
    private TextView tv_time;
    private RelativeLayout rl_time;
    private TextView moneyText;
    private TextView balanceText;
    private LinearLayout payBalanceLayout;
    private LinearLayout payOtherLayout;
    private String oid = "";
    private String osn = "";
    private String user_money = "";
    private String prices = "";
    public static boolean isForeground = false;

    private ImageView alipayTypeImage,WeChatTypeImage;
    private String paytype = "1";//1支付宝2微信

    private String uid = "";
    private String access_token = "";

    private int flag = 0;

    PopupWindow popupwindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_cur_road_biked);
        context = this;
        SharedPreferencesUrls.getInstance().putBoolean("isStop",true);

        IntentFilter filter = new IntentFilter("data.broadcast.rechargeAction");
        registerReceiver(broadcastReceiver, filter);

        initView();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            intent = new Intent(context, HistoryRoadDetailActivity.class);
            intent.putExtra("oid", oid);
            startActivity(intent);
            scrollToFinishActivity();
        }
    };

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();

        if(flag==1) return;

        uid = SharedPreferencesUrls.getInstance().getString("uid","");
        access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT);
            UIHelper.goToAct(context,LoginActivity.class);
        }else {
            getCurrentorder(uid,access_token);
        }

        Log.e("history===","biked===onResume");
    }

    @Override
    protected void onDestroy() {
        isForeground = false;
        super.onDestroy();

        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        payLoadingDialog = new LoadingDialog(context);
        payLoadingDialog.setCancelable(false);
        payLoadingDialog.setCanceledOnTouchOutside(false);

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("当前行程");

        bikeCode = (TextView)findViewById(R.id.curRoadUI_biked_code);
        bikeNum = (TextView)findViewById(R.id.curRoadUI_biked_num);
        startTime = (TextView)findViewById(R.id.curRoadUI_biked_startTime);
        endTime = (TextView)findViewById(R.id.curRoadUI_biked_endTime);
        timeText = (TextView)findViewById(R.id.curRoadUI_biked_time);
        rl_time = (RelativeLayout)findViewById(R.id.rl_time);
        tv_time = (TextView)findViewById(R.id.tv_time);
        moneyText = (TextView)findViewById(R.id.curRoadUI_biked_money);
        balanceText = (TextView)findViewById(R.id.curRoadUI_biked_balance);
        payBalanceLayout = (LinearLayout) findViewById(R.id.curRoadUI_biked_payBalanceLayout);
        payOtherLayout = (LinearLayout) findViewById(R.id.curRoadUI_biked_payOtherLayout);

        backImg.setOnClickListener(this);
        payBalanceLayout.setOnClickListener(this);
        payOtherLayout.setOnClickListener(this);
    }



    private void getCurrentorder(String uid, String access_token){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        HttpHelper.post(context, Urls.getCurrentorder, params, new TextHttpResponseHandler() {
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
                        CurRoadBikingBean bean = JSON.parseObject(result.getData(),CurRoadBikingBean.class);
                        bikeCode.setText("行程编号:"+bean.getOsn());
                        bikeNum.setText(bean.getCodenum());
                        startTime.setText(bean.getSt_time());
                        endTime.setText(bean.getEd_time());
                        timeText.setText(bean.getTotal_mintues());

                        if(bean.getIs_super_member()==1){
                            rl_time.setVisibility(View.VISIBLE);
                            payOtherLayout.setVisibility(View.VISIBLE);

                            tv_time.setText(""+(Integer.parseInt(bean.getTotal_mintues())-60));
                        }else{
                            rl_time.setVisibility(View.GONE);
                            payOtherLayout.setVisibility(View.GONE);
                        }


                        if (bean.getPrices() != null && !"".equals(bean.getPrices())){
                            prices = bean.getPrices();
                        }else {
                            prices = "0.00";
                        }
                        moneyText.setText(prices);
                        if (bean.getUser_money() != null && !"".equals(bean.getUser_money())){
                            user_money = bean.getUser_money();
                        }else {
                            user_money = "0.00";
                        }
                        balanceText.setText(user_money);
                        oid = bean.getOid();
                        osn = bean.getOsn();
//                        if (Double.parseDouble(prices) <= Double.parseDouble(user_money)
//                                && Double.parseDouble(prices) <= 5){
                        if (Double.parseDouble(prices) <= Double.parseDouble(user_money)){
                            m_myHandler.sendEmptyMessage(0);
//                            paySubmit();
                        }
                    } else {
                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;

            case R.id.curRoadUI_biked_payBalanceLayout:
                if (Double.parseDouble(user_money) < Double.parseDouble(prices)){
                    flag = 0;
                    Toast.makeText(context,"当前余额不足,请先充值!",Toast.LENGTH_SHORT).show();
                    UIHelper.goToAct(context,RechargeActivity.class);
                }else {
                    paySubmit();
                }
                break;

            case R.id.curRoadUI_biked_payOtherLayout:
                // 获取自定义布局文件的视图
                View customView = getLayoutInflater().inflate(R.layout.pop_pay_menu, null, false);
                // 创建PopupWindow宽度和高度
                RelativeLayout pop_win_bg = (RelativeLayout) customView.findViewById(R.id.pop_win_bg);
                ImageView iv_popup_window_back = (ImageView) customView.findViewById(R.id.popupWindow_back);
                TextView tv_pop_money = (TextView) customView.findViewById(R.id.tv_pop_money);
                LinearLayout ll_pop_payLayout = (LinearLayout) customView.findViewById(R.id.ll_pop_payLayout);
                RelativeLayout alipayTypeLayout = (RelativeLayout)customView.findViewById(R.id.ui_curRoadBiked_alipayTypeLayout);
                RelativeLayout WeChatTypeLayout = (RelativeLayout)customView.findViewById(R.id.ui_curRoadBiked_WeChatTypeLayout);
                alipayTypeImage = (ImageView)customView.findViewById(R.id.ui_curRoadBiked_alipayTypeImage);
                WeChatTypeImage = (ImageView)customView.findViewById(R.id.ui_curRoadBiked_WeChatTypeImage);

                tv_pop_money.setText(prices);

                ll_pop_payLayout.setOnClickListener(this);
                alipayTypeLayout.setOnClickListener(this);
                WeChatTypeLayout.setOnClickListener(this);
                iv_popup_window_back.setOnClickListener(this);

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
                popupwindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, true);
                /**
                 * 设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
                 */
                popupwindow.setAnimationStyle(R.style.PopupAnimation);

                popupwindow.setBackgroundDrawable(new BitmapDrawable());
//                popupwindow.setFocusable(true);
//                popupwindow.setTouchable(true);
                popupwindow.setOutsideTouchable(true);

                popupwindow.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;

            case R.id.popupWindow_back:
                popupwindow.dismiss();
                break;

            case R.id.ui_curRoadBiked_alipayTypeLayout:
                alipayTypeImage.setImageResource(R.drawable.pay_type_selected);
                WeChatTypeImage.setImageResource(R.drawable.pay_type_normal);
                paytype = "1";
                break;
            case R.id.ui_curRoadBiked_WeChatTypeLayout:
                alipayTypeImage.setImageResource(R.drawable.pay_type_normal);
                WeChatTypeImage.setImageResource(R.drawable.pay_type_selected);
                paytype = "2";
                break;

            case R.id.ll_pop_payLayout:
                Toast.makeText(context,"当前余额不足,请先充值!",Toast.LENGTH_SHORT).show();

                if ("1".equals(paytype)){
                    alipayBikeAction(osn, uid, access_token);
                }else {
                    wxpayBikeAction(osn, uid, access_token);
                }
                break;


        }
    }

    public void alipayBikeAction(final String osn,String uid,String access_token) {
        Toast.makeText(context, "正在调起支付宝支付...", Toast.LENGTH_LONG).show();

        Log.e("alipayBike===000", uid+"==="+access_token+"==="+osn);

        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("osn", osn);
        HttpHelper.get(context, Urls.alipayBike, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                    Log.e("alipayBikeAction===", "==="+responseString);

                    if (result.getFlag().equals("Success")) {
                        final String payInfo = result.getData();
                        Runnable payRunnable = new Runnable() {
                            @Override
                            public void run() {
                                // 构造PayTask 对象
                                PayTask alipay = new PayTask(CurRoadBikedActivity.this);
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

    Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 0:
                    paySubmit();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

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

                    flag=1;

                    Log.e("mHandler===", "==="+resultStatus);

                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(context, "恭喜您,支付成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context,HistoryRoadDetailActivity.class);
                        intent.putExtra("oid",oid);
                        startActivity(intent);
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

    public void wxpayBikeAction(final String osn,String uid,String access_token) {

        Log.e("wxpayBike===000", uid+"==="+access_token+"==="+osn);

        SharedPreferencesUrls.getInstance().putBoolean("isTreasure",false);
        Toast.makeText(context, "正在调起微信支付...", Toast.LENGTH_LONG).show();
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("osn", osn);
        HttpHelper.get(context, Urls.wxpayBike, params, new TextHttpResponseHandler() {
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

                        flag=1;
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


    private void paySubmit(){
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT);
            UIHelper.goToAct(context,LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("oid",oid);
            HttpHelper.post(context, Urls.orderPaybalance, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (payLoadingDialog != null && !payLoadingDialog.isShowing()) {
                        payLoadingDialog.setTitle("正在加载");
                        payLoadingDialog.show();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    if (payLoadingDialog != null && payLoadingDialog.isShowing()){
                        payLoadingDialog.dismiss();
                    }
                    UIHelper.ToastError(context, throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        if (result.getFlag().equals("Success")) {
                            Toast.makeText(context,"恭喜您,支付成功!",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context,HistoryRoadDetailActivity.class);
                            intent.putExtra("oid",oid);
                            startActivity(intent);
                            scrollToFinishActivity();
                        } else {
                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                    }
                    if (payLoadingDialog != null && payLoadingDialog.isShowing()){
                        payLoadingDialog.dismiss();
                    }
                }
            });
        }
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
