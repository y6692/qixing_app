package cn.qimate.bike.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

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

import cn.jock.pickerview.view.view.OptionsPickerView;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.nostra13.universalimageloader.core.ImageLoader;
import cn.qimate.bike.BuildConfig;
import cn.qimate.bike.R;
import cn.qimate.bike.alipay.PayResult;
import cn.qimate.bike.core.common.BitmapUtils1;
import cn.qimate.bike.core.common.DensityUtils;
import cn.qimate.bike.core.common.DisplayUtil;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.img.NetUtil;
import cn.qimate.bike.model.AuthStateBean;
import cn.qimate.bike.model.GradeListBean;
import cn.qimate.bike.model.PayMonthCartBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.SchoolListBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * 实名认证
 * Created by yuanyi on 2019/4/11.
 */

public class RealNameAuthActivity extends SwipeBackActivity implements View.OnClickListener {
    private static final int SDK_PAY_FLAG = 1;
    private IWXAPI api;
    private Context context;
    private LoadingDialog loadingDialog;
    private LinearLayout ll_back;
    private TextView title;
    private ImageView rightBtn;
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
    private RelativeLayout rl_selectLayout, rl_selectLayout2, rl_select, rl_select2;
    private RelativeLayout cartLayout;
    private ImageView cartImage ;
    private LinearLayout payLayout;
    private RelativeLayout payLayout2;
    private LinearLayout submitBtn;
    private TextView totalMoney;
    private String paytype = "1";
    private String osn = "";
    private int goType = 1;
    private int type = 1;
    private String gamestatus = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_name_auth);
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

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("实名认证");
        rightBtn = (ImageView)findViewById(R.id.mainUI_title_rightBtn);
//        rightBtn.setText("联系客服");

        rl_selectLayout = (RelativeLayout)findViewById(R.id.rl_selectLayout);
        rl_selectLayout2 = (RelativeLayout)findViewById(R.id.rl_selectLayout2);
        rl_select = (RelativeLayout)findViewById(R.id.rl_select);
        rl_select2 = (RelativeLayout)findViewById(R.id.rl_select2);
        WeChatTypeLayout = (RelativeLayout)findViewById(R.id.realNameAuthUI_WeChatTypeLayout);
        alipayTypeLayout = (RelativeLayout)findViewById(R.id.realNameAuthUI_alipayTypeLayout);
        WeChatTypeImage = (ImageView)findViewById(R.id.realNameAuthUI_WeChatTypeImage);
        alipayTypeImage = (ImageView)findViewById(R.id.realNameAuthUI_alipayTypeImage);
        cartLayout = (RelativeLayout)findViewById(R.id.realNameAuthUI_cartLayout);
        cartImage = (ImageView)findViewById(R.id.realNameAuthUI_cartImage);
        payLayout = (LinearLayout)findViewById(R.id.realNameAuthUI_payLayout);
        payLayout2 = (RelativeLayout)findViewById(R.id.realNameAuthUI_payLayout2);
        submitBtn = (LinearLayout)findViewById(R.id.ui_realNameAuth_submitBtn);
        totalMoney = (TextView)findViewById(R.id.tv_totalMoney);

        totalMoney.setText("219.90");

        ll_back.setOnClickListener(this);
        rightBtn.setOnClickListener(this);


        WeChatTypeLayout.setOnClickListener(this);
        alipayTypeLayout.setOnClickListener(this);
        rl_selectLayout.setOnClickListener(this);
        rl_selectLayout2.setOnClickListener(this);
        cartLayout.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
//
//        UserMonth();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                scrollToFinishActivity();
                break;
            case R.id.mainUI_title_rightBtn:
                if (Build.VERSION.SDK_INT >= 23) {
                    int checkPermission = checkSelfPermission(Manifest.permission.CALL_PHONE);
                    if (checkPermission != PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                            requestPermissions(new String[] { Manifest.permission.CALL_PHONE }, 0);
                        } else {
                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                            customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开拨打电话权限！")
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    requestPermissions(new String[] { Manifest.permission.CALL_PHONE }, 0);
                                }
                            });
                            customBuilder.create().show();
                        }
                        return;
                    }
                }
                CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                customBuilder.setTitle("温馨提示").setMessage("确认拨打" + "0519-86999222" + "吗?")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent=new Intent();
                        intent.setAction(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + "0519-86999222"));
                        startActivity(intent);
                    }
                });
                customBuilder.create().show();
                break;
            case R.id.rl_selectLayout:
                goType = 1;
                rl_select.setVisibility(View.VISIBLE);
                rl_select2.setVisibility(View.GONE);

                payLayout.setVisibility(View.VISIBLE);
                payLayout2.setVisibility(View.GONE);

                if(type == 0){
                    totalMoney.setText("200.00");
                }else{
                    totalMoney.setText("219.00");
                }

                break;
            case R.id.rl_selectLayout2:
                goType = 2;
                rl_select.setVisibility(View.GONE);
                rl_select2.setVisibility(View.VISIBLE);

                if(type == 0){
                    payLayout.setVisibility(View.GONE);
                    payLayout2.setVisibility(View.VISIBLE);
                }else{
                    totalMoney.setText("19.00");
                }


                break;

            case R.id.realNameAuthUI_WeChatTypeLayout:
                WeChatTypeImage.setImageResource(R.drawable.pay_type_selected);
                alipayTypeImage.setImageResource(R.drawable.pay_type_normal);
                paytype = "1";
                break;
            case R.id.realNameAuthUI_alipayTypeLayout:
                WeChatTypeImage.setImageResource(R.drawable.pay_type_normal);
                alipayTypeImage.setImageResource(R.drawable.pay_type_selected);
                paytype = "2";
                break;

            case R.id.realNameAuthUI_cartLayout:

                if(type==1){
                    type = 0;
                    cartImage.setImageResource(R.drawable.pay_type_normal);

                    if(goType == 2){
                        payLayout.setVisibility(View.GONE);
                        payLayout2.setVisibility(View.VISIBLE);
                    }else{
                        totalMoney.setText("200.00");
                    }
                }else{
                    type = 1;
                    cartImage.setImageResource(R.drawable.pay_type_selected);
                    payLayout.setVisibility(View.VISIBLE);
                    payLayout2.setVisibility(View.GONE);

                    if(goType == 2){
                        totalMoney.setText("19.00");
                    }else{
                        totalMoney.setText("219.00");
                    }
                }

                break;

            case R.id.ui_realNameAuth_submitBtn:
//                customBuilder = new CustomDialog.Builder(this);
//                customBuilder.setTitle("温馨提示").setMessage("是否确定支付?")
//                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                        userPay();
//                    }
//                });
//                customBuilder.create().show();

                if(goType == 1){
                    Intent intent1 = new Intent(context,InsureanceActivity.class);
                    intent1.putExtra("isBack",true);
                    context.startActivity(intent1);
                }else{
                    UIHelper.goToAct(context, DepositFreeAuthActivity.class);
                }

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
                                PayTask alipay = new PayTask(RealNameAuthActivity.this);
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
