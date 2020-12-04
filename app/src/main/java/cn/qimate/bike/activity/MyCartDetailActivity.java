package cn.qimate.bike.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import cn.qimate.bike.model.BillBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by yuanyi on 2019/12/9.
 */
public class MyCartDetailActivity extends SwipeBackActivity implements View.OnClickListener{

    private Context context;
    private LoadingDialog loadingDialog;
    private LinearLayout backImg;
    private LinearLayout submitBtn;

    private ImageView iv_bike;
    private TextView tv_car_type;
    private TextView tv_car_number;
    private TextView tv_order_amount;
    private TextView tv_order_sn;
    private TextView tv_payment_name;
    private TextView tv_car_start_time;
    private TextView tv_car_end_time;
    private TextView tv_payment_time;
    private TextView tv_price;
    private TextView tv_continued_price;
    private TextView tv_credit_score_desc;
    private TextView tv_credit_score_desc2;
    private TextView tv_each_free_time;
    private TextView tv_cycling_time;

    private RelativeLayout rl_payment_name;
    private RelativeLayout rl_each_free_time;
    private RelativeLayout rl_payment_time;

    private int order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart_detail);
        context = this;

        initView();
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        backImg = (LinearLayout) findViewById(R.id.ll_backBtn);
//        iv_bike = (ImageView)findViewById(R.id.my_order_detail_iv_bike);
//        tv_car_type = (TextView)findViewById(R.id.my_order_detail_tv_car_type);
//        tv_car_number = (TextView)findViewById(R.id.my_order_detail_tv_car_number);
//        tv_order_amount = (TextView)findViewById(R.id.my_order_detail_tv_order_amount);
//        tv_order_sn = (TextView)findViewById(R.id.my_order_detail_tv_order_sn);
//        tv_payment_name = (TextView)findViewById(R.id.my_order_detail_tv_payment_name);
//        tv_payment_time = (TextView)findViewById(R.id.my_order_detail_tv_payment_time);
//        tv_car_start_time = (TextView)findViewById(R.id.my_order_detail_tv_car_start_time);
//        tv_car_end_time = (TextView)findViewById(R.id.my_order_detail_tv_car_end_time);
//        tv_price = (TextView)findViewById(R.id.my_order_detail_tv_price);
//        tv_continued_price= (TextView)findViewById(R.id.my_order_detail_tv_continued_price);
//        tv_credit_score_desc= (TextView)findViewById(R.id.my_order_detail_tv_credit_score_desc);
//        tv_credit_score_desc2= (TextView)findViewById(R.id.my_order_detail_tv_credit_score_desc2);
//        tv_each_free_time= (TextView)findViewById(R.id.my_order_detail_tv_each_free_time);
//        tv_cycling_time = (TextView)findViewById(R.id.my_order_detail_tv_cycling_time);
//
//        rl_each_free_time= (RelativeLayout) findViewById(R.id.my_order_detail_rl_each_free_time);
//
//        rl_payment_name = (RelativeLayout)findViewById(R.id.my_order_detail_rl_payment_name);
//        rl_payment_time = (RelativeLayout)findViewById(R.id.my_order_detail_rl_payment_time);
//
//        backImg.setOnClickListener(this);
////        submitBtn.setOnClickListener(this);
//
//        order_id = getIntent().getIntExtra("order_id", 1);
//
//        order_detail();
    }

    private void order_detail() {
        Log.e("oda===order_detail", order_id+"===");

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

                            Log.e("oda===order_detail1", responseString + "===" + result.data);

                            BillBean bean = JSON.parseObject(result.getData(), BillBean.class);

                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                            if(null != bean.getOrder_sn()){
                                Log.e("oda===order_detail2", bean.getOrder_sn()+"===" + bean.getCar_number()+"===" + bean.getCar_type());

                                tv_car_type.setText(bean.getCar_type()==1?"单车":"助力车");
                                tv_car_number.setText(""+bean.getCar_number());
                                tv_order_amount.setText(""+bean.getOrder_amount());
                                tv_order_sn.setText(""+bean.getOrder_sn());
                                tv_payment_name.setText(""+bean.getPayment_name());
                                tv_car_start_time.setText(""+bean.getCar_start_time());
                                tv_car_end_time.setText(""+bean.getCar_end_time());
                                tv_payment_time.setText(""+bean.getPayment_time());
                                tv_price.setText(""+bean.getPrice());
                                tv_continued_price.setText(""+bean.getContinued_price());
                                tv_credit_score_desc.setText("("+bean.getCredit_score_desc()+")");
                                tv_credit_score_desc2.setText("("+bean.getCredit_score_desc()+")");

                                if(!"0".equals(bean.getEach_free_time())){
                                    rl_each_free_time.setVisibility(View.VISIBLE);
                                    tv_each_free_time.setText(bean.getEach_free_time()+"分钟");
                                }else{
                                    rl_each_free_time.setVisibility(View.GONE);
                                }

                                if("".equals(bean.getCredit_score_desc())){
                                    tv_credit_score_desc.setVisibility(View.GONE);
                                    tv_credit_score_desc2.setVisibility(View.GONE);
                                    tv_price.setTextColor(0xFF333333);
                                    tv_continued_price.setTextColor(0xFF333333);
                                }else{
                                    tv_credit_score_desc.setVisibility(View.VISIBLE);
                                    tv_credit_score_desc2.setVisibility(View.VISIBLE);
                                    tv_price.setTextColor(0xFFFD555B);
                                    tv_continued_price.setTextColor(0xFFFD555B);
                                }

                                tv_cycling_time.setText(""+bean.getCycling_time());

                                if(bean.getCar_type()==1){
                                    iv_bike.setImageResource(R.drawable.bike_icon2);
                                }else{
                                    iv_bike.setImageResource(R.drawable.ebike_icon2);
                                }

                                if("0.00".equals(bean.getOrder_amount()) || "0".equals(bean.getOrder_amount())){
                                    rl_payment_name.setVisibility(View.GONE);
                                    rl_payment_time.setVisibility(View.GONE);
                                }else{
                                    rl_payment_name.setVisibility(View.VISIBLE);
                                    rl_payment_time.setVisibility(View.VISIBLE);
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
//                Intent intent = new Intent(context, MainActivity.class);
////              intent.putExtra("flag", true);
//                startActivity(intent);

                scrollToFinishActivity();
                break;

            case R.id.unpay_other_submitBtn:
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

//            Intent intent = new Intent(context, MainActivity.class);
////            intent.putExtra("flag", true);
//            startActivity(intent);

            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
