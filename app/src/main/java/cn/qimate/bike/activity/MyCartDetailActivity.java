package cn.qimate.bike.activity;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.apache.http.Header;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.BillBean;
import cn.qimate.bike.model.MyCartBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by yuanyi on 2019/12/9.
 */
public class MyCartDetailActivity extends SwipeBackActivity implements View.OnClickListener{

    private Context context;
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
    private TextView tv_continued_price;
    private TextView tv_credit_score_desc;
    private TextView tv_credit_score_desc2;
    private TextView tv_cycling_time;

    private RelativeLayout rl_payment_name;
    private RelativeLayout rl_each_free_time;
    private RelativeLayout rl_payment_time;

    private int order_id;

    String name;
    int id;
    String card_code;

    ImageView iv_discount;
    RelativeLayout rl_img;
    TextView tv_name,tv_remaining_free,tv_remaining_free2,tv_remaining_free_name,tv_daily_free_times,tv_each_free_time,tv_name2,tv_price2,tv_original_price2,tv_carmodel_name,tv_carmodel_name2,tv_desc;
    View view_split;
    LinearLayout ll_daily_free_times;

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

        rl_img= (RelativeLayout) findViewById(R.id.rl_img);
        tv_carmodel_name= (TextView)findViewById(R.id.tv_carmodel_name);
        tv_name= (TextView)findViewById(R.id.tv_name);
        tv_remaining_free= (TextView)findViewById(R.id.tv_remaining_free);
        tv_remaining_free2= (TextView)findViewById(R.id.tv_remaining_free2);
        tv_remaining_free_name= (TextView)findViewById(R.id.tv_remaining_free_name);
        view_split= (View)findViewById(R.id.view_split);
        ll_daily_free_times= (LinearLayout) findViewById(R.id.ll_daily_free_times);
        tv_daily_free_times= (TextView)findViewById(R.id.tv_daily_free_times);
        tv_each_free_time= (TextView)findViewById(R.id.tv_each_free_time);

        tv_carmodel_name2= (TextView)findViewById(R.id.tv_carmodel_name2);
        tv_desc= (TextView)findViewById(R.id.tv_desc);

        backImg.setOnClickListener(this);
//        submitBtn.setOnClickListener(this);

//        order_id = getIntent().getIntExtra("order_id", 1);
//
//        order_detail();

        id = getIntent().getIntExtra("id", 0);

        Log.e("mcda===initView","==="+id);

        initHttp();
    }

    private void initHttp(){
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
//    params.put("tab", 1);

        Log.e("my_cycling_cards===","==="+id);

        HttpHelper.get(context, Urls.my_cycling_cards_detail+id+"/detail", params, new TextHttpResponseHandler() {
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
                            Log.e("my_cards_detail=1","==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                            MyCartBean bean = JSON.parseObject(result.getData(), MyCartBean.class);

                            name = bean.getName();

                            Log.e("my_cards_detail=2",name+"===");

                            tv_name.setText(name);

                            int type = bean.getType();
                            int state = bean.getState();

                            if(state==0 || state==1){
                                rl_img.setBackgroundResource(R.drawable.my_cart_use_icon);
                            }else{
                                rl_img.setBackgroundResource(R.drawable.my_cart_unuse_icon);
                            }

                            if(type==2){
                                if(state==0){
                                    tv_remaining_free.setText("免费骑行"+bean.getTimes()+"次");
                                }else{
                                    tv_remaining_free.setText("剩余"+bean.getRemaining_free_times()+"次");
                                }

                                tv_remaining_free2.setText(bean.getTimes()+"次");
                                tv_remaining_free_name.setText("免费骑行次数");

                                view_split.setVisibility(View.GONE);
                                ll_daily_free_times.setVisibility(View.GONE);
                            }else{
                                if(state==0){
                                    tv_remaining_free.setText("免费骑行"+bean.getDays()+"天");
                                }else{
                                    tv_remaining_free.setText("剩余"+bean.getRemaining_free_days()+"天");
                                }

                                tv_remaining_free2.setText(bean.getDays()+"天");
                                tv_remaining_free_name.setText("免费骑行天数");

                                view_split.setVisibility(View.VISIBLE);
                                ll_daily_free_times.setVisibility(View.VISIBLE);
                            }
                            if(bean.getDaily_free_times()==0){
                                tv_daily_free_times.setText("不限次");

                            }else{
                                tv_daily_free_times.setText(bean.getDaily_free_times()+"次");

                            }

                            tv_each_free_time.setText(bean.getEach_free_time()+"分钟");


                            tv_carmodel_name.setText(bean.getCarmodel_name());
                            tv_carmodel_name2.setText(bean.getCarmodel_name());
                            tv_desc.setText(bean.getDesc());

//                            price = "";

//                            if(price.equals(original_price)){
//                                iv_discount.setVisibility(View.GONE);
//                            }else{
//                                iv_discount.setVisibility(View.VISIBLE);
//                            }

//                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                            TabBean obj = JSON.parseObject(result.getData(), TabBean.class);
//                            Log.e("cycling_cards===2",obj.getTabs()+"==="+obj.getDatas());
//
//                            carmodel_array = new JSONArray(obj.getTabs());
//                            carmodel_num = carmodel_array.length();
//                            Log.e("cycling_cards===3","==="+carmodel_array.length());
//
//                            CarmodelBean obj2 = JSON.parseObject(obj.getDatas(), CarmodelBean.class);
//                            Log.e("cycling_cards===4",obj2.getCarmodel1()+"==="+obj2.getCarmodel2());

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }

                    }
                });



            }
        });
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
//
//                            BillBean bean = JSON.parseObject(result.getData(), BillBean.class);
//
//                            if (loadingDialog != null && loadingDialog.isShowing()) {
//                                loadingDialog.dismiss();
//                            }
//
//                            if(null != bean.getOrder_sn()){
//                                Log.e("oda===order_detail2", bean.getOrder_sn()+"===" + bean.getCar_number()+"===" + bean.getCar_type());
//
//                                tv_car_type.setText(bean.getCar_type()==1?"单车":"助力车");
//                                tv_car_number.setText(""+bean.getCar_number());
//                                tv_order_amount.setText(""+bean.getOrder_amount());
//                                tv_order_sn.setText(""+bean.getOrder_sn());
//                                tv_payment_name.setText(""+bean.getPayment_name());
//                                tv_car_start_time.setText(""+bean.getCar_start_time());
//                                tv_car_end_time.setText(""+bean.getCar_end_time());
//                                tv_payment_time.setText(""+bean.getPayment_time());
//                                tv_price.setText(""+bean.getPrice());
//                                tv_continued_price.setText(""+bean.getContinued_price());
//                                tv_credit_score_desc.setText("("+bean.getCredit_score_desc()+")");
//                                tv_credit_score_desc2.setText("("+bean.getCredit_score_desc()+")");
//
//                                if(!"0".equals(bean.getEach_free_time())){
//                                    rl_each_free_time.setVisibility(View.VISIBLE);
//                                    tv_each_free_time.setText(bean.getEach_free_time()+"分钟");
//                                }else{
//                                    rl_each_free_time.setVisibility(View.GONE);
//                                }
//
//                                if("".equals(bean.getCredit_score_desc())){
//                                    tv_credit_score_desc.setVisibility(View.GONE);
//                                    tv_credit_score_desc2.setVisibility(View.GONE);
//                                    tv_price.setTextColor(0xFF333333);
//                                    tv_continued_price.setTextColor(0xFF333333);
//                                }else{
//                                    tv_credit_score_desc.setVisibility(View.VISIBLE);
//                                    tv_credit_score_desc2.setVisibility(View.VISIBLE);
//                                    tv_price.setTextColor(0xFFFD555B);
//                                    tv_continued_price.setTextColor(0xFFFD555B);
//                                }
//
//                                tv_cycling_time.setText(""+bean.getCycling_time());
//
//                                if(bean.getCar_type()==1){
//                                    iv_bike.setImageResource(R.drawable.bike_icon2);
//                                }else{
//                                    iv_bike.setImageResource(R.drawable.ebike_icon2);
//                                }
//
//                                if("0.00".equals(bean.getOrder_amount()) || "0".equals(bean.getOrder_amount())){
//                                    rl_payment_name.setVisibility(View.GONE);
//                                    rl_payment_time.setVisibility(View.GONE);
//                                }else{
//                                    rl_payment_name.setVisibility(View.VISIBLE);
//                                    rl_payment_time.setVisibility(View.VISIBLE);
//                                }
//
//                            }

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
