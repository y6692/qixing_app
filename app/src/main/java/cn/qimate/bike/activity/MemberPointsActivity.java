package cn.qimate.bike.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cn.jock.pickerview.view.view.OptionsPickerView;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseViewAdapter;
import cn.qimate.bike.base.BaseViewHolder;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.model.BillBean;
import cn.qimate.bike.model.GlobalConfig;
import cn.qimate.bike.model.RechargeBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator1 on 2017/2/14.
 */

public class MemberPointsActivity extends SwipeBackActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener, PLA_AdapterView.OnItemClickListener {

    private Context context = this;
    private LinearLayout ll_back;
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

    private View footerLayout;

    private MyAdapter myAdapter;
    private List<BillBean> datas;

    private PointsExchangeAdapter pointsExchangeAdapter;
    private List<RechargeBean> datas_pointsExchange;

    private boolean isRefresh = true;// 是否刷新中
    private boolean isLast = false;
    private int showPage = 1;

    private OptionsPickerView pvOptions;

    private RelativeLayout ll_bill;
    private TextView tv_bill;
    private ArrayList<String> item = new ArrayList<>();

    private int order_type = 1;

    private MultiColumnListView lv_pointsExchange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_points);
        datas = new ArrayList<>();
        datas_pointsExchange = new ArrayList<>();
        initView();
    }

    private void initView(){
//        item.add("骑行订单");       //TODO  3
//        item.add("充值订单");
//        item.add("购买套餐卡订单");
//        item.add("调度费订单");
//        item.add("赔偿费订单");
//
//        ll_back = (LinearLayout) findViewById(R.id.ll_backBtn);
//
//        pvOptions = new OptionsPickerView(context,false);
//        pvOptions.setTitle("交易类型");

        // list投资列表
        footerView = LayoutInflater.from(context).inflate(R.layout.footer_item, null);
        footerViewType01 = footerView.findViewById(R.id.footer_Layout_type01);// 点击加载更多
        footerViewType02 = footerView.findViewById(R.id.footer_Layout_type02);// 正在加载，请您稍等
        footerViewType03 = footerView.findViewById(R.id.footer_Layout_type03);// 已无更多
        footerViewType04 = footerView.findViewById(R.id.footer_Layout_type04);// 刷新失败，请重试
        footerViewType05 = footerView.findViewById(R.id.footer_Layout_type05);// 暂无数据
        iv_type05 = footerView.findViewById(R.id.footer_Layout_iv_type05);
        tv_type05 = footerView.findViewById(R.id.footer_Layout_tv_type05);
        iv_type05.setImageResource(R.drawable.no_bill_icon);
        tv_type05.setText("您还未有账单…");

        footerLayout = footerView.findViewById(R.id.footer_Layout);



        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.Layout_swipeParentLayout);
        myList = (ListView)findViewById(R.id.Layout_swipeListView);
        myList.addFooterView(footerView);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark), getResources().getColor(android.R.color.holo_green_light), getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light));

        myList.setOnItemClickListener(this);
        if(datas.isEmpty()){
            initHttp();
        }

        myAdapter = new MyAdapter(context);
        myAdapter.setDatas(datas);
        myList.setAdapter(myAdapter);



        lv_pointsExchange = (MultiColumnListView)findViewById(R.id.lv_pointsExchange);

        if (datas_pointsExchange.isEmpty() || 0 == datas_pointsExchange.size()){
            pointsExchange();
        }
        pointsExchangeAdapter = new PointsExchangeAdapter(context);
        lv_pointsExchange.setAdapter(pointsExchangeAdapter);
        lv_pointsExchange.setOnItemClickListener(this);


        tv_detail = (TextView) findViewById(R.id.tv_detail);

        tv_detail.setOnClickListener(this);
        footerLayout.setOnClickListener(this);

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

            case R.id.tv_detail:
                UIHelper.goToAct(context, MemberPointsDetailActivity.class);
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

    private void initHttp(){

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("order_type", 1);    //订单类型 1骑行订单 2套餐卡订单 3充值订单 4认证充值订单 5调度费订单 6赔偿费订单 7充值+认证充值订单 8调度费+赔偿费订单
        params.put("origin", 1);        //来源 1我的订单 2账单 默认1
        params.put("page",showPage);    //当前页码
        params.put("per_page", GlobalConfig.PAGE_SIZE);
        HttpHelper.get(context, Urls.orders, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                setFooterType(1);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                UIHelper.ToastError(context, throwable.toString());
                swipeRefreshLayout.setRefreshing(false);
                isRefresh = false;
                setFooterType(3);
                setFooterVisibility();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {

                    Log.e("orders===", "==="+responseString);

                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                    JSONArray array = new JSONArray(result.getData());

                    if (array.length() == 0 && showPage == 1) {
                        footerLayout.setVisibility(View.VISIBLE);
                        setFooterType(4);
                        return;
                    } else if (array.length() < GlobalConfig.PAGE_SIZE && showPage == 1) {
                        footerLayout.setVisibility(View.GONE);
                        setFooterType(5);
                    } else if (array.length() < GlobalConfig.PAGE_SIZE) {
                        footerLayout.setVisibility(View.VISIBLE);
                        setFooterType(2);
                    } else if (array.length() >= 10) {
                        footerLayout.setVisibility(View.VISIBLE);
                        setFooterType(0);
                    }

                    Log.e("orders===1", "==="+array.length());

                    for (int i = 0; i < array.length(); i++) {

                        Log.e("orders===2", "==="+array.getJSONObject(i).toString());

                        BillBean bean = JSON.parseObject(array.getJSONObject(i).toString(), BillBean.class);

                        Log.e("orders===3", "==="+bean.getOrder_type());

                        datas.add(bean);
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

    private void pointsExchange(){
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录您的账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context,LoginActivity.class);
            return;
        }

        Log.e("recharge_prices===","==="+access_token);

        RequestParams params = new RequestParams();
        params.put("tab", 1);
        HttpHelper.get(context, Urls.recharge_prices, params, new TextHttpResponseHandler() {
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

                Log.e("recharge_prices===Fail","==="+responseString);

                UIHelper.ToastError(context, throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {

                    Log.e("recharge_prices===1","==="+responseString);


                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                    Log.e("recharge_prices===","==="+result.getData());
//                    UserMsgBean bean = JSON.parseObject(result.getData(), UserMsgBean.class);

                    JSONArray array = new JSONArray(result.getData());
                    if (datas_pointsExchange.size() != 0 || !datas_pointsExchange.isEmpty()){
                        datas_pointsExchange.clear();
                    }
                    for (int i = 0; i < array.length(); i++){
                        RechargeBean bean = JSON.parseObject(array.getJSONObject(i).toString(), RechargeBean.class);
                        datas_pointsExchange.add(bean);
//                        if ( 0 == i){
//                            rid = bean.getId();
//                            price = bean.getPrice();
//                            bean.setSelected(true);
//                        }else {
//                            bean.setSelected(false);
//                        }
                    }
                    pointsExchangeAdapter.setDatas(datas_pointsExchange);
                    pointsExchangeAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
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
    private class MyAdapter extends BaseViewAdapter<BillBean> {

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
//            RankingListBean bean = getDatas().get(position);
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
    private class PointsExchangeAdapter extends BaseViewAdapter<RechargeBean> {

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
//            LinearLayout layout = BaseViewHolder.get(convertView,R.id.item_recharge_layout);
//            TextView moneyText = BaseViewHolder.get(convertView,R.id.item_recharge_money);
//
//            RechargeBean bean = getDatas().get(position);
//
//            Log.e("peAdapter===", bean.getPrice_s()+"==="+bean.isSelected());
//
//            layout.setSelected(bean.isSelected());
//            moneyText.setSelected(bean.isSelected());
//            moneyText.setText(bean.getPrice_s());

//            ll_payBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//
//                    Log.e("bcf===onClick", "==="+position+"==="+card_code);
//
//
//                    order(card_code);
//
//                }
//            });

            return convertView;
        }

    }
}
