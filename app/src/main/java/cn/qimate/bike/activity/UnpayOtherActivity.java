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
import org.json.JSONObject;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.OrderBean;
import cn.qimate.bike.model.OtherBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by yuanyi on 2019/12/9.
 */
public class UnpayOtherActivity extends SwipeBackActivity implements View.OnClickListener{


    private Context context;
    private LoadingDialog loadingDialog;
    private LinearLayout backImg;
    private LinearLayout submitBtn;

    private TextView tv_title;
    private TextView tv_title2;
    private TextView tv_unpay_other_order_amount;
    private TextView tv_unpay_other_car_number;
    private TextView tv_unpay_other_car_start_time;
    private TextView tv_unpay_other_car_end_time;
    private TextView tv_unpay_other_parking_location;
    private TextView tv_unpay_other_remark;

    private int order_id;
    private String order_amount;
    private int order_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unpay_other);
        context = this;

        initView();
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        backImg = (LinearLayout) findViewById(R.id.ll_backBtn);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title2 = (TextView)findViewById(R.id.tv_title2);
        tv_unpay_other_order_amount = (TextView)findViewById(R.id.tv_unpay_other_order_amount);
        tv_unpay_other_car_number = (TextView)findViewById(R.id.tv_unpay_other_car_number);
        tv_unpay_other_car_start_time = (TextView)findViewById(R.id.tv_unpay_other_car_start_time);
        tv_unpay_other_car_end_time = (TextView)findViewById(R.id.tv_unpay_other_car_end_time);
        tv_unpay_other_parking_location = (TextView)findViewById(R.id.tv_unpay_other_parking_location);
        tv_unpay_other_remark = (TextView)findViewById(R.id.tv_unpay_other_remark);
        submitBtn = (LinearLayout)findViewById(R.id.unpay_other_submitBtn);



        backImg.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        other();
    }

    private void other() {
        Log.e("uoa===other", "===");

        HttpHelper.get(context, Urls.other, new TextHttpResponseHandler() {
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

                            Log.e("uoa===other1", responseString + "===" + result.data);

                            OtherBean bean = JSON.parseObject(result.getData(), OtherBean.class);

                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }


                            if(null != bean.getOrder_sn()){
                                Log.e("uoa===cycling2", bean.getOrder_sn()+"===" + bean.getCar_number()+"===");

                                order_id = bean.getOrder_id();
                                order_amount = bean.getOrder_amount();
                                order_type = bean.getType() + 4;

                                tv_unpay_other_order_amount.setText(bean.getOrder_amount());
                                tv_unpay_other_car_number.setText(bean.getCar_number());
                                tv_unpay_other_car_start_time.setText(bean.getCar_start_time());
                                tv_unpay_other_car_end_time.setText(bean.getCar_end_time());
                                tv_unpay_other_parking_location.setText(bean.getParking_location());
                                tv_unpay_other_remark.setText(bean.getRemark());


                                if(bean.getType()==1){
                                    tv_title.setText("待支付调度费");
                                    tv_title2.setText("您需要支付车辆调度费");
                                }else{
                                    tv_title.setText("待支付赔偿费");
                                    tv_title2.setText("您需要支付车辆赔偿费");
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


    @Override
    public void onClick(View v) {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (v.getId()){
            case R.id.ll_backBtn:
                scrollToFinishActivity();
                break;

            case R.id.unpay_other_submitBtn:
                if (access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.e("rid===", order_id+"==="+order_type+"==="+order_amount);

                Intent intent = new Intent(context, SettlementPlatformActivity.class);
                intent.putExtra("order_id", order_id);
                intent.putExtra("order_type", order_type);
                intent.putExtra("order_amount", order_amount);

                context.startActivity(intent);      //TODO

//                order();
                break;
        }
    }



//    private void order() {
//        Log.e("order===", "==="+price);
//
//        RequestParams params = new RequestParams();
//        params.put("order_type", 3);        //订单类型 1骑行订单 2套餐卡订单 3充值订单 4认证充值订单
////        params.put("car_number", URLEncoder.encode(codenum));
////        params.put("card_code", card_code);        //套餐卡券码（order_type为2时必传）
//        params.put("price", price);        //传价格数值 例如：20.00(order_type为3、4时必传)
//
//        HttpHelper.post(context, Urls.order, params, new TextHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                onStartCommon("正在加载");
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                onFailureCommon(throwable.toString());
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
//
//                m_myHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//
//                            Log.e("order===1", responseString + "===" + result.data);
//
//                            JSONObject jsonObject = new JSONObject(result.getData());
//
//                            int order_id = jsonObject.getInt("order_id");
//                            String order_amount = jsonObject.getString("order_amount");
//
//                            Log.e("order===1", order_id + "===" + order_amount );
//
//                            Intent intent = new Intent(context, SettlementPlatformActivity.class);
//                            intent.putExtra("order_type", 3);
//                            intent.putExtra("order_amount", order_amount);
//                            intent.putExtra("order_id", order_id);
//                            context.startActivity(intent);
//
//                        } catch (Exception e) {
////                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
//                        }
//
//                        if (loadingDialog != null && loadingDialog.isShowing()) {
//                            loadingDialog.dismiss();
//                        }
//
//                    }
//                });
//
//
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
