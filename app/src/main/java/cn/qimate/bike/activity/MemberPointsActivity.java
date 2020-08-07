package cn.qimate.bike.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jock.pickerview.view.view.OptionsPickerView;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.nostra13.universalimageloader.core.ImageLoader;
import cn.qimate.bike.BuildConfig;
import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.base.BaseViewAdapter;
import cn.qimate.bike.base.BaseViewHolder;
import cn.qimate.bike.core.common.GetImagePath;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.model.BillBean;
import cn.qimate.bike.model.GlobalConfig;
import cn.qimate.bike.model.PointsExchangeBean;
import cn.qimate.bike.model.PointsIndexBean;
import cn.qimate.bike.model.ProcessDetailBean;
import cn.qimate.bike.model.RankingUserBean;
import cn.qimate.bike.model.RechargeBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.SigninBean;
import cn.qimate.bike.model.TaskBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.LogUtil;
import cn.qimate.bike.util.ToastUtil;
import cn.qimate.bike.view.RoundImageView;

/**
 * Created by Administrator1 on 2017/2/14.
 */

public class MemberPointsActivity extends SwipeBackActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener, PLA_AdapterView.OnItemClickListener {

    private Context context = this;

    // List
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView myList;

    private View footerView;
    private View footerViewType01;
    private View footerViewType02;
    private View footerViewType03;
    private View footerViewType04;
    private View footerViewType05;
    private ImageView iv_type05;
    private TextView tv_type05;
    private TextView tv_detail;
    private LinearLayout ll_signin, ll_oneoff_mission, ll_daily_mission, ll_points_exchange_detail;


    TextView tv_user_points;
    TextView tv_rule;

    private View footerLayout;

    private MyAdapter myAdapter;
    private List<TaskBean> datas;

    private PointsExchangeAdapter pointsExchangeAdapter;
    private List<PointsExchangeBean> datas_pointsExchange;

    private boolean isRefresh = true;// 是否刷新中
    private boolean isLast = false;
    private int showPage = 1;

    private OptionsPickerView pvOptions;

    private RelativeLayout ll_bill;
    private TextView tv_bill;
    private ArrayList<String> item = new ArrayList<>();

    private int order_type = 1;

    LinearLayout ll_back;
    private MultiColumnListView lv_pointsExchange;
    ImageView iv_pointsExchange;

    private Dialog advDialog;
    private TextView tv_signin1;
    private TextView tv_signin2;
    private TextView tv_points;
    private TextView signinConfirmBtn;

    TextView tv_sign;

    RelativeLayout rl_task_cert;
    RelativeLayout rl_task_info;
    RelativeLayout rl_task_signin;
    RelativeLayout rl_task_cycling_bike;
    RelativeLayout rl_task_cycling_ebike;
    RelativeLayout rl_task_cycling_bike_7;
    RelativeLayout rl_task_cycling_ebike_7;
    RelativeLayout rl_task_buy_card_1;
    RelativeLayout rl_task_buy_card_2;

    TextView tv_current_signin;
    TextView tv_points_signin1;
    ImageView iv_is_signin1;
    TextView tv_time_signin1;
    TextView tv_points_signin2;
    ImageView iv_is_signin2;
    TextView tv_time_signin2;
    TextView tv_points_signin3;
    ImageView iv_is_signin3;
    TextView tv_time_signin3;
    TextView tv_points_signin4;
    ImageView iv_is_signin4;
    TextView tv_time_signin4;
    TextView tv_points_signin5;
    ImageView iv_is_signin5;
    TextView tv_time_signin5;

    ImageView iv_task_cert;
    TextView tv_task_title_cert;
    TextView tv_task_desc_cert;
    TextView tv_task_process_cert;
    ImageView iv_task_info;
    TextView tv_task_title_info;
    TextView tv_task_desc_info;
    TextView tv_task_process_info;
    ImageView iv_task_signin;
    TextView tv_task_title_signin;
    TextView tv_task_desc_signin;
    TextView tv_task_process_signin;
    ImageView iv_task_cycling_bike;
    TextView tv_task_title_cycling_bike;
    TextView tv_task_desc_cycling_bike;
    TextView tv_task_process_cycling_bike;
    ImageView iv_task_cycling_ebike;
    TextView tv_task_title_cycling_ebike;
    TextView tv_task_desc_cycling_ebike;
    TextView tv_task_process_cycling_ebike;
    ImageView iv_task_cycling_bike_7;
    TextView tv_task_title_cycling_bike_7;
    TextView tv_task_desc_cycling_bike_7;
    TextView tv_task_process_cycling_bike_7;
    ImageView iv_task_cycling_ebike_7;
    TextView tv_task_title_cycling_ebike_7;
    TextView tv_task_desc_cycling_ebike_7;
    TextView tv_task_process_cycling_ebike_7;
    ImageView iv_task_buy_card_1;
    TextView tv_task_title_buy_card_1;
    TextView tv_task_desc_buy_card_1;
    TextView tv_task_process_buy_card_1;
    ImageView iv_task_buy_card_2;
    TextView tv_task_title_buy_card_2;
    TextView tv_task_desc_buy_card_2;
    TextView tv_task_process_buy_card_2;

    int user_points;
    int user_cert1_status;
    int user_cert2_status;

    private boolean isAuth;
    private String avatar = "";
    private String nickname = "";
    private String nickname0 = "";
    private String phone = "";
    private int sex;
    private String school_name = "";
    private String school_area = "";
    private int college_id;
    private String college_name = "";
    private String admission_time = "";
    private int is_full;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_points);
        datas = new ArrayList<>();
        datas_pointsExchange = new ArrayList<>();



        isAuth = getIntent().getBooleanExtra("isAuth", false);
        avatar = getIntent().getStringExtra("avatar");
        nickname = getIntent().getStringExtra("nickname");
        phone = getIntent().getStringExtra("phone");
        sex = getIntent().getIntExtra("sex", 0);
        school_name = getIntent().getStringExtra("school_name");
        school_area = getIntent().getStringExtra("school_area");
        college_id = getIntent().getIntExtra("college_id", 0);
        college_name = getIntent().getStringExtra("college_name");
        admission_time = getIntent().getStringExtra("admission_time");
        is_full = getIntent().getIntExtra("is_full", 0);

        initView();
    }

    private void initView(){
        ll_back = (LinearLayout) findViewById(R.id.ll_backBtn);
//
//        pvOptions = new OptionsPickerView(context,false);
//        pvOptions.setTitle("交易类型");

        advDialog = new Dialog(context, R.style.Theme_AppCompat_Dialog);
        View advDialogView = LayoutInflater.from(context).inflate(R.layout.ui_signin_view, null);
        advDialog.setContentView(advDialogView);
        advDialog.setCanceledOnTouchOutside(false);

        tv_signin1 = (TextView)advDialogView.findViewById(R.id.tv_signin1);
        tv_signin2 = (TextView)advDialogView.findViewById(R.id.tv_signin2);
        tv_points = (TextView)advDialogView.findViewById(R.id.tv_points);
        signinConfirmBtn = (TextView)advDialogView.findViewById(R.id.ui_signin_confirmBtn);

        ll_back.setOnClickListener(this);
        signinConfirmBtn.setOnClickListener(this);

        // list投资列表
        footerView = LayoutInflater.from(context).inflate(R.layout.footer_item, null);
        footerViewType01 = footerView.findViewById(R.id.footer_Layout_type01);// 点击加载更多
        footerViewType02 = footerView.findViewById(R.id.footer_Layout_type02);// 正在加载，请您稍等
        footerViewType03 = footerView.findViewById(R.id.footer_Layout_type03);// 已无更多
        footerViewType04 = footerView.findViewById(R.id.footer_Layout_type04);// 刷新失败，请重试
        footerViewType05 = footerView.findViewById(R.id.footer_Layout_type05);// 暂无数据
        iv_type05 = footerView.findViewById(R.id.footer_Layout_iv_type05);
        tv_type05 = footerView.findViewById(R.id.footer_Layout_tv_type05);
        iv_type05.setImageResource(R.drawable.no_points_icon);
//        tv_type05.setText("您还未有账单…");

        footerLayout = footerView.findViewById(R.id.footer_Layout);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.Layout_swipeParentLayout);
        myList = (ListView)findViewById(R.id.Layout_swipeListView);
        myList.addFooterView(footerView);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark), getResources().getColor(android.R.color.holo_green_light), getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light));

        myList.setOnItemClickListener(this);


        myAdapter = new MyAdapter(context);
        myAdapter.setDatas(datas);
        myList.setAdapter(myAdapter);

        if(datas.isEmpty()){
            initHttp();
        }

        lv_pointsExchange = (MultiColumnListView)findViewById(R.id.lv_pointsExchange);
        iv_pointsExchange = (ImageView)findViewById(R.id.iv_pointsExchange);

        if (datas_pointsExchange.isEmpty() || 0 == datas_pointsExchange.size()){
            pointsExchange();
        }
        pointsExchangeAdapter = new PointsExchangeAdapter(context);
        lv_pointsExchange.setAdapter(pointsExchangeAdapter);
        lv_pointsExchange.setOnItemClickListener(this);


        tv_detail = (TextView) findViewById(R.id.tv_detail);
        ll_points_exchange_detail = (LinearLayout) findViewById(R.id.ll_points_exchange_detail);

        tv_detail.setOnClickListener(this);
        ll_points_exchange_detail.setOnClickListener(this);
        footerLayout.setOnClickListener(this);

        tv_user_points = (TextView) findViewById(R.id.tv_user_points);
        tv_rule = (TextView) findViewById(R.id.tv_rule);

        tv_sign = (TextView) findViewById(R.id.tv_sign);
        tv_sign.setOnClickListener(this);

        ll_signin = (LinearLayout) findViewById(R.id.ll_signin);
        ll_oneoff_mission = (LinearLayout) findViewById(R.id.ll_oneoff_mission);
        ll_daily_mission = (LinearLayout) findViewById(R.id.ll_daily_mission);

        rl_task_cert = (RelativeLayout) findViewById(R.id.rl_task_cert);
        rl_task_info = (RelativeLayout) findViewById(R.id.rl_task_info);
        rl_task_signin = (RelativeLayout) findViewById(R.id.rl_task_signin);
        rl_task_cycling_bike = (RelativeLayout) findViewById(R.id.rl_task_cycling_bike);
        rl_task_cycling_ebike = (RelativeLayout) findViewById(R.id.rl_task_cycling_ebike);
        rl_task_cycling_bike_7 = (RelativeLayout) findViewById(R.id.rl_task_cycling_bike_7);
        rl_task_cycling_ebike_7 = (RelativeLayout) findViewById(R.id.rl_task_cycling_ebike_7);
        rl_task_buy_card_1 = (RelativeLayout) findViewById(R.id.rl_task_buy_card_1);
        rl_task_buy_card_2 = (RelativeLayout) findViewById(R.id.rl_task_buy_card_2);

        tv_current_signin= (TextView) findViewById(R.id.tv_current_signin);
        tv_points_signin1 = (TextView) findViewById(R.id.tv_points_signin1);
        iv_is_signin1 = (ImageView) findViewById(R.id.iv_is_signin1);
        tv_time_signin1 = (TextView) findViewById(R.id.tv_time_signin1);
        tv_points_signin2 = (TextView) findViewById(R.id.tv_points_signin2);
        iv_is_signin2 = (ImageView) findViewById(R.id.iv_is_signin2);
        tv_time_signin2 = (TextView) findViewById(R.id.tv_time_signin2);
        tv_points_signin3 = (TextView) findViewById(R.id.tv_points_signin3);
        iv_is_signin3 = (ImageView) findViewById(R.id.iv_is_signin3);
        tv_time_signin3 = (TextView) findViewById(R.id.tv_time_signin3);
        tv_points_signin4 = (TextView) findViewById(R.id.tv_points_signin4);
        iv_is_signin4 = (ImageView) findViewById(R.id.iv_is_signin4);
        tv_time_signin4 = (TextView) findViewById(R.id.tv_time_signin4);
        tv_points_signin5 = (TextView) findViewById(R.id.tv_points_signin5);
        iv_is_signin5 = (ImageView) findViewById(R.id.iv_is_signin5);
        tv_time_signin5 = (TextView) findViewById(R.id.tv_time_signin5);

        iv_task_cert = (ImageView) findViewById(R.id.iv_task_cert);
        tv_task_title_cert = (TextView) findViewById(R.id.tv_task_title_cert);
        tv_task_desc_cert = (TextView) findViewById(R.id.tv_task_desc_cert);
        tv_task_process_cert = (TextView) findViewById(R.id.tv_task_process_cert);
        iv_task_info = (ImageView) findViewById(R.id.iv_task_info);
        tv_task_title_info = (TextView) findViewById(R.id.tv_task_title_info);
        tv_task_desc_info = (TextView) findViewById(R.id.tv_task_desc_info);
        tv_task_process_info = (TextView) findViewById(R.id.tv_task_process_info);
        iv_task_signin = (ImageView) findViewById(R.id.iv_task_signin);
        tv_task_title_signin = (TextView) findViewById(R.id.tv_task_title_signin);
        tv_task_desc_signin = (TextView) findViewById(R.id.tv_task_desc_signin);
        tv_task_process_signin = (TextView) findViewById(R.id.tv_task_process_signin);
        iv_task_cycling_bike = (ImageView) findViewById(R.id.iv_task_cycling_bike);
        tv_task_title_cycling_bike = (TextView) findViewById(R.id.tv_task_title_cycling_bike);
        tv_task_desc_cycling_bike = (TextView) findViewById(R.id.tv_task_desc_cycling_bike);
        tv_task_process_cycling_bike = (TextView) findViewById(R.id.tv_task_process_cycling_bike);
        iv_task_cycling_ebike = (ImageView) findViewById(R.id.iv_task_cycling_ebike);
        tv_task_title_cycling_ebike = (TextView) findViewById(R.id.tv_task_title_cycling_ebike);
        tv_task_desc_cycling_ebike = (TextView) findViewById(R.id.tv_task_desc_cycling_ebike);
        tv_task_process_cycling_ebike = (TextView) findViewById(R.id.tv_task_process_cycling_ebike);
        iv_task_cycling_bike_7 = (ImageView) findViewById(R.id.iv_task_cycling_bike_7);
        tv_task_title_cycling_bike_7 = (TextView) findViewById(R.id.tv_task_title_cycling_bike_7);
        tv_task_desc_cycling_bike_7 = (TextView) findViewById(R.id.tv_task_desc_cycling_bike_7);
        tv_task_process_cycling_bike_7 = (TextView) findViewById(R.id.tv_task_process_cycling_bike_7);
        iv_task_cycling_ebike_7 = (ImageView) findViewById(R.id.iv_task_cycling_ebike_7);
        tv_task_title_cycling_ebike_7 = (TextView) findViewById(R.id.tv_task_title_cycling_ebike_7);
        tv_task_desc_cycling_ebike_7 = (TextView) findViewById(R.id.tv_task_desc_cycling_ebike_7);
        tv_task_process_cycling_ebike_7 = (TextView) findViewById(R.id.tv_task_process_cycling_ebike_7);
        iv_task_buy_card_1 = (ImageView) findViewById(R.id.iv_task_buy_card_1);
        tv_task_title_buy_card_1 = (TextView) findViewById(R.id.tv_task_title_buy_card_1);
        tv_task_desc_buy_card_1= (TextView) findViewById(R.id.tv_task_desc_buy_card_1);
        tv_task_process_buy_card_1 = (TextView) findViewById(R.id.tv_task_process_buy_card_1);
        iv_task_buy_card_2 = (ImageView) findViewById(R.id.iv_task_buy_card_2);
        tv_task_title_buy_card_2 = (TextView) findViewById(R.id.tv_task_title_buy_card_2);
        tv_task_desc_buy_card_2= (TextView) findViewById(R.id.tv_task_desc_buy_card_2);
        tv_task_process_buy_card_2 = (TextView) findViewById(R.id.tv_task_process_buy_card_2);

        tv_task_process_cert.setOnClickListener(this);
        tv_task_process_info.setOnClickListener(this);
        tv_task_process_signin.setOnClickListener(this);
        tv_task_process_cycling_bike.setOnClickListener(this);
        tv_task_process_cycling_ebike.setOnClickListener(this);
        tv_task_process_cycling_bike_7.setOnClickListener(this);
        tv_task_process_cycling_ebike_7.setOnClickListener(this);
        tv_task_process_buy_card_1.setOnClickListener(this);
        tv_task_process_buy_card_2.setOnClickListener(this);

        points_index();
    }

    @Override
    public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
//        RechargeBean bean = myAdapter.getDatas().get(position);
//        rid = bean.getId();
//        price = bean.getPrice();
//
//
//
//        if (position != selectPosition){
//            myAdapter.getDatas().get(position).setSelected(true);
//            myAdapter.getDatas().get(selectPosition).setSelected(false);
//            selectPosition = position;
//        }
//        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e("mpa===onResume", "===");

//        initHttp();



        isRefresh = true;
        if(datas.size()!=0){
            myAdapter.notifyDataSetChanged();
        }

        if(datas_pointsExchange.size()!=0){
            pointsExchangeAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onRefresh() {
        showPage = 1;
        if (!isRefresh) {
            if(datas.size()!=0){
                myAdapter.getDatas().clear();
                myAdapter.notifyDataSetChanged();
            }
            isRefresh = true;
            initHttp();
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_backBtn:
                scrollToFinishActivity();
                break;

            case R.id.tv_sign:
                if (access_token == null || "".equals(access_token)) {
                    Toast.makeText(context, "请先登录账号", Toast.LENGTH_SHORT).show();
                    return;
                }

                signin();

                break;

            case R.id.ui_signin_confirmBtn:
                if (advDialog != null && advDialog.isShowing()) {
                    advDialog.dismiss();
                }

                initHttp();

                break;

            case R.id.tv_task_process_cert: //
                if(!"已完成".equals(tv_task_process_cert.getText().toString())){
                    UIHelper.goToAct(context, AuthCenterActivity.class);
                }

                break;

            case R.id.tv_task_process_info:
                if(!"已完成".equals(tv_task_process_info.getText().toString())){
                    Intent intent = new Intent(context, PersonInfoActivity.class);
//                    intent.putExtra("isAuth", isAuth);
//                    intent.putExtra("avatar", avatar);
//                    intent.putExtra("nickname", nickname);
//                    intent.putExtra("phone", phone);
//                    intent.putExtra("sex", sex);
//                    intent.putExtra("school_name", school_name);
//                    intent.putExtra("school_area", school_area);
//                    intent.putExtra("college_id", college_id);
//                    intent.putExtra("college_name", college_name);
//                    intent.putExtra("admission_time", admission_time);
//                    intent.putExtra("is_full", is_full);

                    startActivityForResult(intent, 10);

                }

                break;

            case R.id.tv_task_process_signin:
                if(!"已完成".equals(tv_task_process_signin.getText().toString())){
                    signin();
                }

                break;

            case R.id.tv_task_process_cycling_bike:
            case R.id.tv_task_process_cycling_ebike:
            case R.id.tv_task_process_cycling_bike_7:
            case R.id.tv_task_process_cycling_ebike_7:
                if(!"已完成".equals(tv_task_process_cycling_bike.getText().toString())){
//                  UIHelper.goToAct(context, MainActivity.class);

//                  MainActivity.changeTab(0);
//                  UIHelper.goToAct(context, MainActivity.class);

                    Intent intent0 = new Intent();
                    intent0.putExtra("is_cycling", true);
                    setResult(RESULT_OK, intent0);
                    scrollToFinishActivity();
                }

                break;

            case R.id.tv_task_process_buy_card_1:
            case R.id.tv_task_process_buy_card_2:
                if(!"已完成".equals(tv_task_process_cycling_bike.getText().toString())){
//                    UIHelper.goToAct(context, MainActivity.class);

//                  MainActivity.changeTab(0);
//                  UIHelper.goToAct(context, MainActivity.class);

                    Intent intent0 = new Intent();
                    intent0.putExtra("is_buy_card", true);
                    setResult(RESULT_OK, intent0);
                    scrollToFinishActivity();
                }

                break;

            case R.id.tv_detail:
                Intent intent = new Intent(context, MemberPointsDetailActivity.class);
                intent.putExtra("user_points", user_points);
                context.startActivity(intent);
//                UIHelper.goToAct(context, MemberPointsDetailActivity.class);
                break;

            case R.id.ll_points_exchange_detail:
                UIHelper.goToAct(context, PointsExchangeDetailActivity.class);
                break;

            case R.id.footer_Layout:
                if (!isLast) {
                    showPage += 1;
                    initHttp();
                    myAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    private void signin(){
        RequestParams params = new RequestParams();
        HttpHelper.post(context, Urls.signin, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                LogUtil.e("minef===signin_fail", "==="+throwable.toString());
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtil.e("minef===signin", "==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            if(result.getStatus_code()==200){
                                SigninBean bean = JSON.parseObject(result.getData(), SigninBean.class);

                                tv_signin1.setText(bean.getTitle());
                                tv_signin2.setText(bean.getDesc());
                                tv_points.setText("+"+bean.getPoints()+"积分");

                                WindowManager windowManager = getWindowManager();
                                Display display = windowManager.getDefaultDisplay();
                                WindowManager.LayoutParams lp = advDialog.getWindow().getAttributes();
                                lp.width = (int) (display.getWidth() * 1);
                                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                advDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                                advDialog.getWindow().setAttributes(lp);
                                advDialog.show();


                                tv_sign.setText("已签到");
                                tv_sign.setBackgroundResource(R.drawable.points_exchange_finish_bcg);

                                ToastUtil.showMessageApp(context, result.getMessage());

                                points_index();
                            }



                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }
                });

            }
        });
    }

    private void points_index(){
//        RequestParams params = new RequestParams();
        HttpHelper.get(context, Urls.points_index, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, final Throwable throwable) {
                onFailureCommon(throwable.toString());

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e("mpa===points_index", "==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            PointsIndexBean bean = JSON.parseObject(result.getData(), PointsIndexBean.class);

                            user_points = bean.getUser_points();
                            user_cert1_status = bean.getUser_cert1_status();
                            user_cert2_status = bean.getUser_cert2_status();

                            tv_user_points.setText(""+user_points);
                            tv_rule.setText(bean.getPoints_rule());

                            if(user_cert1_status!=3){
                                lv_pointsExchange.setVisibility(View.GONE);
                                iv_pointsExchange.setVisibility(View.VISIBLE);
                                iv_pointsExchange.setImageResource(R.drawable.no_cert_icon);
                            }else{
                                lv_pointsExchange.setVisibility(View.VISIBLE);
                                iv_pointsExchange.setVisibility(View.GONE);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
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
//        params.put("order_type", 1);    //订单类型 1骑行订单 2套餐卡订单 3充值订单 4认证充值订单 5调度费订单 6赔偿费订单 7充值+认证充值订单 8调度费+赔偿费订单
//        params.put("origin", 1);        //来源 1我的订单 2账单 默认1
//        params.put("page",showPage);    //当前页码
//        params.put("per_page", GlobalConfig.PAGE_SIZE);
        HttpHelper.get(context, Urls.points_tasks, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                setFooterType(1);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, final Throwable throwable) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        UIHelper.ToastError(context, throwable.toString());
                        swipeRefreshLayout.setRefreshing(false);
                        isRefresh = false;
                        setFooterType(3);
                        setFooterVisibility();
                    }
                });

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Log.e("points_tasks===", "==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

//                    JSONArray array = new JSONArray(result.getData());
                            JSONObject jsonObject = new JSONObject(result.getData());


                            JSONArray array = new JSONArray(jsonObject.getString("one_time_tasks"));        //daily_tasks

                            if(array.length()>0){
                                ll_oneoff_mission.setVisibility(View.VISIBLE);
                            }else{
                                ll_oneoff_mission.setVisibility(View.GONE);
                            }

                            Log.e("points_tasks===1", "==="+array.length());

                            for (int i = 0; i < array.length(); i++) {

                                TaskBean bean = JSON.parseObject(array.getJSONObject(i).toString(), TaskBean.class);

                                if("cert".equals(bean.getName())){
                                    rl_task_cert.setVisibility(View.VISIBLE);
                                    String icon = bean.getIcon();
                                    Glide.with(context).load(icon).into(iv_task_cert);
//                            if(avatar==null || "".equals(avatar)){
//                                iv_avatar.setImageResource(R.drawable.head_icon);
//                            }else{
//                                Glide.with(context).load(icon).into(iv_task_cert);
//                            }
                                    tv_task_title_cert.setText(bean.getTitle());
                                    tv_task_desc_cert.setText(""+bean.getDesc());
                                    String task_process_cert = bean.getProcess(); //任务进度 当该字段为字符0时，进度按钮文本应为做任务或者去购买；当该字段为字符1时，进度按钮文本应为已完成；其他情况原样输出进度文本；
                                    if("".equals(task_process_cert)){
                                        tv_task_process_cert.setText("去认证");
                                        tv_task_process_cert.setBackgroundResource(R.drawable.points_exchange_bcg);
                                        tv_task_process_cert.setTextColor(0xFFFD555B);
                                    }else if("1".equals(task_process_cert)){
                                        tv_task_process_cert.setText("已完成");
                                        tv_task_process_cert.setBackgroundResource(R.drawable.points_exchange_finish_bcg);
                                        tv_task_process_cert.setTextColor(0xFFFFFFFF);
                                    }else{
                                        tv_task_process_cert.setText(task_process_cert);
                                        tv_task_process_cert.setBackgroundResource(R.drawable.points_exchange_bcg);
                                        tv_task_process_cert.setTextColor(0xFFFD555B);
                                    }

                                }else if("info".equals(bean.getName())){
                                    rl_task_info.setVisibility(View.VISIBLE);
                                    String icon = bean.getIcon();
                                    Glide.with(context).load(icon).into(iv_task_info);
                                    tv_task_title_info.setText(bean.getTitle());
                                    tv_task_desc_info.setText(""+bean.getDesc());
                                    String process = bean.getProcess(); //任务进度 当该字段为字符0时，进度按钮文本应为做任务或者去购买；当该字段为字符1时，进度按钮文本应为已完成；其他情况原样输出进度文本；
                                    if("".equals(process)){
                                        tv_task_process_info.setText("去完成");
                                        tv_task_process_info.setBackgroundResource(R.drawable.points_exchange_bcg);
                                        tv_task_process_info.setTextColor(0xFFFD555B);
                                    }else if("1".equals(process)){
                                        tv_task_process_info.setText("已完成");
                                        tv_task_process_info.setBackgroundResource(R.drawable.points_exchange_finish_bcg);
                                        tv_task_process_info.setTextColor(0xFFFFFFFF);
                                    }else{
                                        tv_task_process_info.setText(process);
                                        tv_task_process_info.setBackgroundResource(R.drawable.points_exchange_bcg);
                                        tv_task_process_info.setTextColor(0xFFFD555B);
                                    }
                                }

                            }


                            array = new JSONArray(jsonObject.getString("daily_tasks"));        //daily_tasks

                            if(array.length()>0){
                                ll_daily_mission.setVisibility(View.VISIBLE);
                            }else{
                                ll_daily_mission.setVisibility(View.GONE);
                            }

                            Log.e("points_tasks===2", "==="+array.length());

                            for (int i = 0; i < array.length(); i++) {

                                TaskBean bean = JSON.parseObject(array.getJSONObject(i).toString(), TaskBean.class);

                                Log.e("points_tasks===3", bean.getName()+"==="+bean.getProcess_details());

//                                if(bean.getSignin_task()==0){
//                                    signinLayout.setVisibility(View.GONE);
//                                }else{
//                                    signinLayout.setVisibility(View.VISIBLE);
//                                }

                                if("signin".equals(bean.getName())){
                                    ll_signin.setVisibility(View.VISIBLE);
                                    rl_task_signin.setVisibility(View.VISIBLE);
                                    String icon = bean.getIcon();
                                    Glide.with(context).load(icon).into(iv_task_signin);
                                    tv_task_title_signin.setText(bean.getTitle());
                                    tv_task_desc_signin.setText(""+bean.getDesc());
                                    String process = bean.getProcess(); //任务进度 当该字段为字符0时，进度按钮文本应为做任务或者去购买；当该字段为字符1时，进度按钮文本应为已完成；其他情况原样输出进度文本；
                                    if("".equals(process)){
                                        tv_task_process_signin.setText("签到");
                                        tv_task_process_signin.setBackgroundResource(R.drawable.points_exchange_bcg);
                                        tv_task_process_signin.setTextColor(0xFFFD555B);

                                        tv_sign.setText("签到");
                                        tv_sign.setBackgroundResource(R.drawable.signin_bcg);
                                    }else if("1".equals(process)){
                                        tv_task_process_signin.setText("已完成");
                                        tv_task_process_signin.setBackgroundResource(R.drawable.points_exchange_finish_bcg);
                                        tv_task_process_signin.setTextColor(0xFFFFFFFF);

                                        tv_sign.setText("已签到");
                                        tv_sign.setBackgroundResource(R.drawable.points_exchange_finish_bcg);
                                    }else{
                                        tv_task_process_signin.setText(process);
                                        tv_task_process_signin.setBackgroundResource(R.drawable.points_exchange_bcg);
                                        tv_task_process_signin.setTextColor(0xFFFD555B);

                                        tv_sign.setText("签到");
                                        tv_sign.setBackgroundResource(R.drawable.signin_bcg);
                                    }

                                    Log.e("points_tasks===4", "==="+bean.getProcess_details());

                                    JSONArray array2 = new JSONArray(bean.getProcess_details());
//
                                    int count = 0;
                                    for (int j = 0; j < array2.length(); j++) {
                                        ProcessDetailBean bean2 = JSON.parseObject(array2.getJSONObject(j).toString(), ProcessDetailBean.class);

//                                        if(bean2.getCurrent()==1){
//                                            tv_current_signin.setText(""+(j+1));
//                                        }


                                        int is_signin = bean2.getIs_signin();

                                        ImageView iv_is_signin = j==0?iv_is_signin1:j==1?iv_is_signin2:j==2?iv_is_signin3:j==3?iv_is_signin4:iv_is_signin5;
                                        TextView tv_points_signin = j==0?tv_points_signin1:j==1?tv_points_signin2:j==2?tv_points_signin3:j==3?tv_points_signin4:tv_points_signin5;
                                        TextView tv_time_signin = j==0?tv_time_signin1:j==1?tv_time_signin2:j==2?tv_time_signin3:j==3?tv_time_signin4:tv_time_signin5;

                                        if(is_signin==1){
                                            count++;
                                            iv_is_signin.setImageResource(R.drawable.signin_yes_icon);
                                        }else{
                                            iv_is_signin.setImageResource(R.drawable.signin_no_icon);
                                        }
                                        tv_points_signin.setText("+"+bean2.getPoints());
                                        tv_time_signin.setText(bean2.getTime());
                                    }
                                    tv_current_signin.setText(""+count);

                                }else if("cycling_bike".equals(bean.getName())){
                                    rl_task_cycling_bike.setVisibility(View.VISIBLE);
                                    String icon = bean.getIcon();
                                    Glide.with(context).load(icon).into(iv_task_cycling_bike);
                                    tv_task_title_cycling_bike.setText(bean.getTitle());
                                    tv_task_desc_cycling_bike.setText(""+bean.getDesc());
                                    String process = bean.getProcess(); //任务进度 当该字段为字符0时，进度按钮文本应为做任务或者去购买；当该字段为字符1时，进度按钮文本应为已完成；其他情况原样输出进度文本；
                                    if("".equals(process)){
                                        tv_task_process_cycling_bike.setText("去骑行");
                                        tv_task_process_cycling_bike.setBackgroundResource(R.drawable.points_exchange_bcg);
                                        tv_task_process_cycling_bike.setTextColor(0xFFFD555B);
                                    }else if("1".equals(process)){
                                        tv_task_process_cycling_bike.setText("已完成");
                                        tv_task_process_cycling_bike.setBackgroundResource(R.drawable.points_exchange_finish_bcg);
                                        tv_task_process_cycling_bike.setTextColor(0xFFFFFFFF);
                                    }else{
                                        tv_task_process_cycling_bike.setText(process);
                                        tv_task_process_cycling_bike.setBackgroundResource(R.drawable.points_exchange_bcg);
                                        tv_task_process_cycling_bike.setTextColor(0xFFFD555B);
                                    }
                                }else if("cycling_ebike".equals(bean.getName())){
                                    rl_task_cycling_ebike.setVisibility(View.VISIBLE);
                                    String icon = bean.getIcon();
                                    Glide.with(context).load(icon).into(iv_task_cycling_ebike);
                                    tv_task_title_cycling_ebike.setText(bean.getTitle());
                                    tv_task_desc_cycling_ebike.setText(""+bean.getDesc());
                                    String process = bean.getProcess(); //任务进度 当该字段为字符0时，进度按钮文本应为做任务或者去购买；当该字段为字符1时，进度按钮文本应为已完成；其他情况原样输出进度文本；
                                    if("".equals(process)){
                                        tv_task_process_cycling_ebike.setText("去骑行");
                                        tv_task_process_cycling_ebike.setBackgroundResource(R.drawable.points_exchange_bcg);
                                        tv_task_process_cycling_ebike.setTextColor(0xFFFD555B);
                                    }else if("1".equals(process)){
                                        tv_task_process_cycling_ebike.setText("已完成");
                                        tv_task_process_cycling_ebike.setBackgroundResource(R.drawable.points_exchange_finish_bcg);
                                        tv_task_process_cycling_ebike.setTextColor(0xFFFFFFFF);
                                    }else{
                                        tv_task_process_cycling_ebike.setText(process);
                                        tv_task_process_cycling_ebike.setBackgroundResource(R.drawable.points_exchange_bcg);
                                        tv_task_process_cycling_ebike.setTextColor(0xFFFD555B);
                                    }
                                }else if("cycling_bike_7".equals(bean.getName())){
                                    rl_task_cycling_bike_7.setVisibility(View.VISIBLE);
                                    String icon = bean.getIcon();
                                    Glide.with(context).load(icon).into(iv_task_cycling_bike_7);
                                    tv_task_title_cycling_bike_7.setText(bean.getTitle());
                                    tv_task_desc_cycling_bike_7.setText(""+bean.getDesc());
                                    String process = bean.getProcess(); //任务进度 当该字段为字符0时，进度按钮文本应为做任务或者去购买；当该字段为字符1时，进度按钮文本应为已完成；其他情况原样输出进度文本；
                                    if("".equals(process)){
                                        tv_task_process_cycling_bike_7.setText("去骑行");
                                        tv_task_process_cycling_bike_7.setBackgroundResource(R.drawable.points_exchange_bcg);
                                        tv_task_process_cycling_bike_7.setTextColor(0xFFFD555B);
                                    }else if("1".equals(process)){
                                        tv_task_process_cycling_bike_7.setText("已完成");
                                        tv_task_process_cycling_bike_7.setBackgroundResource(R.drawable.points_exchange_finish_bcg);
                                        tv_task_process_cycling_bike_7.setTextColor(0xFFFFFFFF);
                                    }else{
                                        tv_task_process_cycling_bike_7.setText(process);
                                        tv_task_process_cycling_bike_7.setBackgroundResource(R.drawable.points_exchange_bcg);
                                        tv_task_process_cycling_bike_7.setTextColor(0xFFFD555B);
                                    }
                                }else if("cycling_ebike_7".equals(bean.getName())){
                                    rl_task_cycling_ebike_7.setVisibility(View.VISIBLE);
                                    String icon = bean.getIcon();
                                    Glide.with(context).load(icon).into(iv_task_cycling_ebike_7);
                                    tv_task_title_cycling_ebike_7.setText(bean.getTitle());
                                    tv_task_desc_cycling_ebike_7.setText(""+bean.getDesc());
                                    String process = bean.getProcess(); //任务进度 当该字段为字符0时，进度按钮文本应为做任务或者去购买；当该字段为字符1时，进度按钮文本应为已完成；其他情况原样输出进度文本；
                                    if("".equals(process)){
                                        tv_task_process_cycling_ebike_7.setText("去骑行");
                                        tv_task_process_cycling_ebike_7.setBackgroundResource(R.drawable.points_exchange_bcg);
                                        tv_task_process_cycling_ebike_7.setTextColor(0xFFFD555B);
                                    }else if("1".equals(process)){
                                        tv_task_process_cycling_ebike_7.setText("已完成");
                                        tv_task_process_cycling_ebike_7.setBackgroundResource(R.drawable.points_exchange_finish_bcg);
                                        tv_task_process_cycling_ebike_7.setTextColor(0xFFFFFFFF);
                                    }else{
                                        tv_task_process_cycling_ebike_7.setText(process);
                                        tv_task_process_cycling_ebike_7.setBackgroundResource(R.drawable.points_exchange_bcg);
                                        tv_task_process_cycling_ebike_7.setTextColor(0xFFFD555B);
                                    }
                                }else if("buy_card_1".equals(bean.getName())){
                                    rl_task_buy_card_1.setVisibility(View.VISIBLE);
                                    String icon = bean.getIcon();
                                    Glide.with(context).load(icon).into(iv_task_buy_card_1);
                                    tv_task_title_buy_card_1.setText(bean.getTitle());
                                    tv_task_desc_buy_card_1.setText(""+bean.getDesc());
                                    String process = bean.getProcess(); //任务进度 当该字段为字符0时，进度按钮文本应为做任务或者去购买；当该字段为字符1时，进度按钮文本应为已完成；其他情况原样输出进度文本；
                                    if("".equals(process)){
                                        tv_task_process_buy_card_1.setText("去购买");
                                        tv_task_process_buy_card_1.setBackgroundResource(R.drawable.points_exchange_bcg);
                                        tv_task_process_buy_card_1.setTextColor(0xFFFD555B);
                                    }else if("1".equals(process)){
                                        tv_task_process_buy_card_1.setText("已完成");
                                        tv_task_process_buy_card_1.setBackgroundResource(R.drawable.points_exchange_finish_bcg);
                                        tv_task_process_buy_card_1.setTextColor(0xFFFFFFFF);
                                    }else{
                                        tv_task_process_buy_card_1.setText(process);
                                        tv_task_process_buy_card_1.setBackgroundResource(R.drawable.points_exchange_bcg);
                                        tv_task_process_buy_card_1.setTextColor(0xFFFD555B);
                                    }
                                }else if("buy_card_2".equals(bean.getName())){
                                    rl_task_buy_card_2.setVisibility(View.VISIBLE);
                                    String icon = bean.getIcon();
                                    Glide.with(context).load(icon).into(iv_task_buy_card_2);
                                    tv_task_title_buy_card_2.setText(bean.getTitle());
                                    tv_task_desc_buy_card_2.setText(""+bean.getDesc());
                                    String process = bean.getProcess(); //任务进度 当该字段为字符0时，进度按钮文本应为做任务或者去购买；当该字段为字符1时，进度按钮文本应为已完成；其他情况原样输出进度文本；
                                    if("".equals(process)){
                                        tv_task_process_buy_card_2.setText("去购买");
                                        tv_task_process_buy_card_2.setBackgroundResource(R.drawable.points_exchange_bcg);
                                        tv_task_process_buy_card_2.setTextColor(0xFFFD555B);
                                    }else if("1".equals(process)){
                                        tv_task_process_buy_card_2.setText("已完成");
                                        tv_task_process_buy_card_2.setBackgroundResource(R.drawable.points_exchange_finish_bcg);
                                        tv_task_process_buy_card_2.setTextColor(0xFFFFFFFF);
                                    }else{
                                        tv_task_process_buy_card_2.setText(process);
                                        tv_task_process_buy_card_2.setBackgroundResource(R.drawable.points_exchange_bcg);
                                        tv_task_process_buy_card_2.setTextColor(0xFFFD555B);
                                    }
                                }

                            }

                        } catch (Exception e) {

                        } finally {
                            swipeRefreshLayout.setRefreshing(false);
                            isRefresh = false;
                            setFooterVisibility();
                        }
                    }
                });

            }
        });
    }

    private void pointsExchange(){
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录您的账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context,LoginActivity.class);
            return;
        }

        Log.e("recharge_prices===","==="+access_token);

        RequestParams params = new RequestParams();
//        params.put("tab", 1);
        HttpHelper.get(context, Urls.points_exchange_lists, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingDialog != null && !loadingDialog.isShowing()) {
                            loadingDialog.setTitle("正在加载");
                            loadingDialog.show();
                        }
                    }
                });

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, final String responseString, final Throwable throwable) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }

                        Log.e("recharge_prices===Fail","==="+responseString);

                        UIHelper.ToastError(context, throwable.toString());
                    }
                });

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Log.e("points_exchange===1","==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            JSONArray array = new JSONArray(result.getData());
                            if (datas_pointsExchange.size() != 0 || !datas_pointsExchange.isEmpty()){
                                datas_pointsExchange.clear();
                            }

                            Log.e("points_exchange===2",array.length()+"==="+result.getData());


                            if(array.length()==0){
                                lv_pointsExchange.setVisibility(View.GONE);
                                iv_pointsExchange.setVisibility(View.VISIBLE);
                                iv_pointsExchange.setImageResource(R.drawable.no_goods_icon);

                                return;
                            }else{
                                lv_pointsExchange.setVisibility(View.VISIBLE);
                                iv_pointsExchange.setVisibility(View.GONE);
                            }

                            for (int i = 0; i < array.length(); i++){
                                PointsExchangeBean bean = JSON.parseObject(array.getJSONObject(i).toString(), PointsExchangeBean.class);

//                              Log.e("points_exchange===3","==="+bean);
                                datas_pointsExchange.add(bean);
                            }
                            pointsExchangeAdapter.setDatas(datas_pointsExchange);
                            pointsExchangeAdapter.notifyDataSetChanged();

                            Log.e("points_exchange===3",lv_pointsExchange.getDividerHeight()+"==="+lv_pointsExchange.getCount());

                            int height = 0;

                            View temp = pointsExchangeAdapter.getView(0, null, lv_pointsExchange);
                            temp.measure(0,0);
                            height = temp.getMeasuredHeight();

                            Log.e("points_exchange===4",height+"==="+((lv_pointsExchange.getCount()+1)/2));

                            ViewGroup.LayoutParams params = lv_pointsExchange.getLayoutParams();
                            params.height = height * ((lv_pointsExchange.getCount()+1)/2);

                            Log.e("points_exchange===5",height+"==="+params.height);

                            lv_pointsExchange.setLayoutParams(params);

//                            for(int i=0;i<count;i++){
//                                View temp = pointsExchangeAdapter.getView(i, null, lv_pointsExchange);
//                                temp.measure(0,0);
//                                height += temp.getMeasuredHeight();
//
//                                Log.e("points_exchange===4",height+"===");
//                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                });

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

    private void setFooterType(int type) {
        switch (type) {
            case 0:
                isLast = false;
                footerViewType01.setVisibility(View.VISIBLE);
                footerViewType02.setVisibility(View.GONE);
                footerViewType03.setVisibility(View.GONE);
                footerViewType04.setVisibility(View.GONE);
                footerViewType05.setVisibility(View.GONE);
                break;
            case 1:
                isLast = false;
                footerViewType01.setVisibility(View.GONE);
                footerViewType02.setVisibility(View.VISIBLE);
                footerViewType03.setVisibility(View.GONE);
                footerViewType04.setVisibility(View.GONE);
                footerViewType05.setVisibility(View.GONE);
                break;
            case 2:
                isLast = true;
                footerViewType01.setVisibility(View.GONE);
                footerViewType02.setVisibility(View.GONE);
                footerViewType03.setVisibility(View.VISIBLE);
                footerViewType04.setVisibility(View.GONE);
                footerViewType05.setVisibility(View.GONE);
                break;
            case 3:
                isLast = false;
                // showPage -= 1;
                footerViewType01.setVisibility(View.GONE);
                footerViewType02.setVisibility(View.GONE);
                footerViewType03.setVisibility(View.GONE);
                footerViewType04.setVisibility(View.VISIBLE);
                footerViewType05.setVisibility(View.GONE);
                break;
            case 4:
                isLast = true;
                footerViewType01.setVisibility(View.GONE);
                footerViewType02.setVisibility(View.GONE);
                footerViewType03.setVisibility(View.GONE);
                footerViewType04.setVisibility(View.GONE);
                footerViewType05.setVisibility(View.VISIBLE);
                break;
            case 5:
                isLast = true;
                footerViewType01.setVisibility(View.GONE);
                footerViewType02.setVisibility(View.GONE);
                footerViewType03.setVisibility(View.GONE);
                footerViewType04.setVisibility(View.GONE);
                footerViewType05.setVisibility(View.GONE);
                break;
        }
    }

    private void setFooterVisibility() {
        if (footerView.getVisibility() == View.GONE) {
            footerView.setVisibility(View.VISIBLE);
        }
    }


    @SuppressLint("NewApi")
    private class MyAdapter extends BaseViewAdapter<TaskBean> {

        private LayoutInflater inflater;

        public MyAdapter(Context context){
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_member_points_detail, null);
            }

            View v_divider = BaseViewHolder.get(convertView,R.id.v_divider);
//            TextView car_type = BaseViewHolder.get(convertView,R.id.item_car_type);
//            TextView car_status = BaseViewHolder.get(convertView,R.id.item_car_status);
//
//            PointsExchangeBean bean = getDatas().get(position);
//
//            car_number.setText(bean.getCar_number());
//            car_type.setText(bean.getCar_type());
//
//            int status = bean.getCar_status();
//            car_status.setText(status==0?"待投放":status==1?"正常":status==2?"锁定":status==3?"确认为坏车":status==4?"坏车已回收":status==5?"调运中":"报废");

            Log.e("mpda===", "==="+position);

            if(position==0){
                v_divider.setVisibility(View.GONE);
            }else{
                v_divider.setVisibility(View.VISIBLE);
            }

            return convertView;
        }
    }

    @SuppressLint("NewApi")
    private class PointsExchangeAdapter extends BaseViewAdapter<PointsExchangeBean> {

        private LayoutInflater inflater;

        public PointsExchangeAdapter(Context context){
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_points_exchange, null);
            }

            RoundImageView iv_image = BaseViewHolder.get(convertView,R.id.item_iv_image);
            TextView tv_points = BaseViewHolder.get(convertView,R.id.item_tv_points);
            TextView tv_exchange = BaseViewHolder.get(convertView,R.id.item_tv_exchange);



            PointsExchangeBean bean = getDatas().get(position);
            final int id = bean.getId();
            final String points = bean.getPoints();

//            Glide.with(context).load(bean.getImage()).into(iv_image);
            ImageLoader.getInstance().displayImage(bean.getImage(), iv_image);
            tv_points.setText(points+"积分");

            tv_exchange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.e("bcf===onClick", "==="+id);

                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                    customBuilder.setType(3).setTitle("温馨提示").setMessage("确认消耗"+points+"积分兑换该骑行卡吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    points_exchange(id);
                                    dialog.cancel();
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    customBuilder.create().show();

                }
            });

//            ViewGroup.LayoutParams params = lv_pointsExchange.getLayoutParams();
//            params.height = totalHeight + (lv_pointsExchange.getDividerHeight() * (lv_pointsExchange.getCount() - 1));
//            lv_pointsExchange.setLayoutParams(params);

            return convertView;
        }

    }



    private void points_exchange(int id) {
        Log.e("points_exchange===", "==="+id);

        RequestParams params = new RequestParams();
        params.put("id", id);

        HttpHelper.post(context, Urls.points_exchange, params, new TextHttpResponseHandler() {
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

                            Log.e("points_exchange===1", responseString + "===" + result.data);

//                            JSONObject jsonObject = new JSONObject(result.getData());

                            if(result.getStatus_code()==200 || result.getStatus_code()==501){
                                ToastUtil.showMessageApp(context, result.getMessage());

                                points_index();
                            }

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("mpa=onActivityResult", requestCode+"==="+resultCode);

        switch (requestCode) {
            case 10:
                initHttp();
                points_index();

                if (resultCode == RESULT_OK) {


                } else {
//                    Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                }
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
