package cn.qimate.bike.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
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
import org.json.JSONArray;
import org.json.JSONObject;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseViewHolder;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.BillBean;
import cn.qimate.bike.model.CarmodelBean;
import cn.qimate.bike.model.H5Bean;
import cn.qimate.bike.model.MyCartBean;
import cn.qimate.bike.model.PayCartBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.TabBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by yuanyi on 2019/12/9.
 */
public class CartDetailActivity extends SwipeBackActivity implements View.OnClickListener{

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

    CheckBox cb;
    TextView tv1, tv2, tv_serviceProtocol, tv_submitBtn;
    LinearLayout ll_submitBtn;

    CustomDialog.Builder customBuilder;
    CustomDialog.Builder customBuilder2;
    private CustomDialog customDialog;
    private CustomDialog customDialog2;

    String name;
    String card_code;

    ImageView iv_discount;
    TextView tv_name,tv_price,tv_original_price,tv_days,tv_days_name,tv_daily_free_times,tv_each_free_time,tv_name2,tv_price2,tv_original_price2,tv_carmodel_name,tv_desc;
    View view_split;
    LinearLayout ll_daily_free_times;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_detail);
        context = this;

        initView();
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

//        2、如果没有购买点击立即购买要有弹窗提示：“请阅读并同意套餐卡购买协议，再购买！”；
//
//        3、勾选并同意购买套餐卡协议后点击立即购买要
//
//        有弹窗提示：“请确认是否购买某某套餐卡！”
//
//        点击“是”到付款页，点击“否”留在确认订单页；

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setType(3).setTitle("温馨提示").setMessage("请阅读并同意套餐卡购买协议，再购买！")
                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        logout();
                        dialog.cancel();
                    }
                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        })
        ;
        customDialog = customBuilder.create();

        customBuilder2 = new CustomDialog.Builder(context);
        customBuilder2.setType(3).setTitle("温馨提示").setMessage("请确认是否购买某某套餐卡！")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        order(card_code);

                        dialog.cancel();
                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
//        customDialog = customBuilder.create();

        backImg = (LinearLayout) findViewById(R.id.ll_backBtn);
        cb = (CheckBox) findViewById(R.id.cb);
        tv1 = (TextView)findViewById(R.id.tv1);
        tv2 = (TextView)findViewById(R.id.tv2);

        iv_discount = (ImageView)findViewById(R.id.iv_discount);
        tv_name= (TextView)findViewById(R.id.tv_name);
        tv_price= (TextView)findViewById(R.id.tv_price);
        tv_original_price= (TextView)findViewById(R.id.tv_original_price);
        tv_days= (TextView)findViewById(R.id.tv_days);
        tv_days_name= (TextView)findViewById(R.id.tv_days_name);
        view_split= (View)findViewById(R.id.view_split);
        ll_daily_free_times= (LinearLayout) findViewById(R.id.ll_daily_free_times);
        tv_daily_free_times= (TextView)findViewById(R.id.tv_daily_free_times);
        tv_each_free_time= (TextView)findViewById(R.id.tv_each_free_time);


        tv_name2= (TextView)findViewById(R.id.tv_name2);
        tv_price2= (TextView)findViewById(R.id.tv_price2);
        tv_original_price2= (TextView)findViewById(R.id.tv_original_price2);
        tv_carmodel_name= (TextView)findViewById(R.id.tv_carmodel_name);
        tv_desc= (TextView)findViewById(R.id.tv_desc);

        tv_serviceProtocol = (TextView)findViewById(R.id.tv_serviceProtocol);
        tv_submitBtn = (TextView)findViewById(R.id.tv_submitBtn);
        ll_submitBtn = (LinearLayout) findViewById(R.id.ll_submitBtn);


        backImg.setOnClickListener(this);
        cb.setOnClickListener(this);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv_serviceProtocol.setOnClickListener(this);
        ll_submitBtn.setOnClickListener(this);

        card_code = getIntent().getStringExtra("card_code");

        initHttp();
    }

    private void order(String card_code) {
        Log.e("order===", "==="+card_code);

        RequestParams params = new RequestParams();
        params.put("order_type", 2);        //订单类型 1骑行订单 2套餐卡订单 3充值订单 4认证充值订单
//        params.put("car_number", URLEncoder.encode(codenum));
        params.put("card_code", card_code);        //套餐卡券码（order_type为2时必传）
//        params.put("price", "");        //传价格数值 例如：20.00(order_type为3、4时必传)

        HttpHelper.post(context, Urls.order, params, new TextHttpResponseHandler() {
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

                            Log.e("order===1", responseString + "===" + result.data);

                            JSONObject jsonObject = new JSONObject(result.getData());

                            order_id = jsonObject.getInt("order_id");
                            String order_amount = jsonObject.getString("order_amount");

                            Log.e("order===1", order_id + "===" + order_amount );

                            Intent intent = new Intent(context, SettlementPlatformActivity.class);
                            intent.putExtra("order_type", 2);
                            intent.putExtra("order_amount", order_amount);
                            intent.putExtra("order_id", order_id);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            context.startActivity(intent);

                            finish();

                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }

                    }
                });


            }
        });
    }

    private void initHttp(){
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
//    params.put("tab", 1);

        Log.e("cycling_cards===","===");

        HttpHelper.get(context, Urls.cycling_cards_detail+card_code+"/detail", params, new TextHttpResponseHandler() {
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
                            Log.e("cycling_cards_detail=1","==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                            MyCartBean bean = JSON.parseObject(result.getData(), MyCartBean.class);

                            name = bean.getName();
                            String price = bean.getPrice();
                            String original_price = bean.getOriginal_price();

                            tv_name.setText(name);
                            tv_price.setText(price);
                            tv_original_price.setText("¥"+original_price);

                            if(bean.getType()==2){
                                tv_days.setText(bean.getTimes()+"次");
                                tv_days_name.setText("免费骑行次数");

                                view_split.setVisibility(View.GONE);
                                ll_daily_free_times.setVisibility(View.GONE);
                            }else{
                                tv_days.setText(bean.getDays()+"天");
                                tv_days_name.setText("免费骑行天数");

                                view_split.setVisibility(View.VISIBLE);
                                ll_daily_free_times.setVisibility(View.VISIBLE);
                            }
                            if(bean.getDaily_free_times()==0){
                                tv_daily_free_times.setText("不限次");

                            }else{
                                tv_daily_free_times.setText(bean.getDaily_free_times()+"次");

                            }

                            tv_each_free_time.setText(bean.getEach_free_time()+"分钟");

//                            view_split= (View)findViewById(R.id.view_split);
//                            ll_daily_free_times= (LinearLayout) findViewById(R.id.ll_daily_free_times);



                            tv_name2.setText(bean.getName());
                            tv_price2.setText(price);
                            tv_original_price2.setText("¥"+original_price);
                            tv_carmodel_name.setText(bean.getCarmodel_name());
                            tv_desc.setText(bean.getDesc());
                            tv_original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                            tv_original_price2.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

//                            tv_name2.setText("助力车700次套餐卡");
//                            tv_price2.setText("121.00");
//                            tv_original_price2.setText("¥122.00");

//                            price = "";

                            if(price.equals(original_price)){
                                iv_discount.setVisibility(View.GONE);
                            }else{
                                iv_discount.setVisibility(View.VISIBLE);
                            }

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
                Log.e("ll_backBtn===", "===");

                scrollToFinishActivity();
                break;

            case R.id.cb:

//                Intent intent = new Intent(context, MainActivity.class);
////              intent.putExtra("flag", true);
//                startActivity(intent);

                Log.e("cb===", "==="+cb.isChecked());

                if(cb.isChecked()){
                    ll_submitBtn.setBackgroundResource(R.drawable.btn_bcg2);
                    tv_submitBtn.setTextColor(0xffffffff);

                    tv1.setTextColor(0xFFEA5359);
                    tv2.setTextColor(0xFFEA5359);
                }else{
                    ll_submitBtn.setBackgroundResource(R.drawable.btn_bcg3);
                    tv_submitBtn.setTextColor(0xffEA5359);

                    tv1.setTextColor(0xFF999999);
                    tv2.setTextColor(0xFF999999);
                }

                break;

            case R.id.tv1:
            case R.id.tv2:

                Log.e("tv1===", "==="+cb.isChecked());

                if(cb.isChecked()){
                    cb.setChecked(false);

                    ll_submitBtn.setBackgroundResource(R.drawable.btn_bcg3);
                    tv_submitBtn.setTextColor(0xffEA5359);

                    tv1.setTextColor(0xFF999999);
                    tv2.setTextColor(0xFF999999);
                }else{
                    cb.setChecked(true);

                    ll_submitBtn.setBackgroundResource(R.drawable.btn_bcg2);
                    tv_submitBtn.setTextColor(0xffffffff);

                    tv1.setTextColor(0xFFEA5359);
                    tv2.setTextColor(0xFFEA5359);
                }

                break;

            case R.id.tv_serviceProtocol:
                agreement();
                break;

            case R.id.ll_submitBtn:
                if (access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(cb.isChecked()){
                    customBuilder2.setTitle("温馨提示").setMessage("请确认是否购买"+name+"！");
                    customDialog2 = customBuilder2.create();
                    customDialog2.show();


                }else{
//                    customBuilder.setTitle("温馨提示").setMessage("请勾选");
//                    customDialog = customBuilder.create();
                    customDialog.show();


                }


                Log.e("rid===", "===");

//                userRecharge(uid, access_token);
                break;
        }
    }

    private void agreement() {

        Log.e("agreement===0", "===");

        try{
//          协议名 register注册协议 recharge充值协议 cycling_card骑行卡协议 insurance保险协议
            HttpHelper.get(context, Urls.agreement+"cycling_card", new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("请稍等");
                        loadingDialog.show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                  Toast.makeText(context, "fail=="+responseString, Toast.LENGTH_LONG).show();

                    Log.e("agreement===fail", throwable.toString()+"==="+responseString);

                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                    UIHelper.ToastError(context, throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {

//                        Toast.makeText(context, "=="+responseString, Toast.LENGTH_LONG).show();

                        Log.e("agreement===", "==="+responseString);

                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                        H5Bean bean = JSON.parseObject(result.getData(), H5Bean.class);

                        UIHelper.goWebViewAct(context, bean.getH5_title(), bean.getH5_url());
//                        UIHelper.goWebViewAct(context, bean.getH5_title(), Urls.agreement+"register");



                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }
                }


            });
        }catch (Exception e){
            Toast.makeText(context, "==="+e, Toast.LENGTH_SHORT).show();
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
