package cn.qimate.bike.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.apache.http.Header;

import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.OrderBean;
import cn.qimate.bike.model.ResultConsel;
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
//        tv_unpay_route_car_number = (TextView)findViewById(R.id.tv_unpay_route_car_number);
//        tv_unpay_route_car_start_time = (TextView)findViewById(R.id.tv_unpay_route_car_start_time);
//        tv_unpay_route_car_end_time = (TextView)findViewById(R.id.tv_unpay_route_car_end_time);
//        tv_unpay_route_order_amount = (TextView)findViewById(R.id.tv_unpay_route_order_amount);
        submitBtn = (LinearLayout)findViewById(R.id.unpay_route_submitBtn);

        backImg.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        cycling();
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

//                userRecharge(uid, access_token);
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
