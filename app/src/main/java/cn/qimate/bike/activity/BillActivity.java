package cn.qimate.bike.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cn.jock.pickerview.view.view.OptionsPickerView;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.adapter.BillAdapter;
import cn.qimate.bike.adapter.MyMessageAdapter;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.model.BillBean;
import cn.qimate.bike.model.GlobalConfig;
import cn.qimate.bike.model.MyMessageBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator1 on 2017/2/14.
 */

public class BillActivity extends SwipeBackActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener {

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

    private View footerLayout;

    private BillAdapter myAdapter;
    private List<BillBean> datas;

    private boolean isRefresh = true;// 是否刷新中
    private boolean isLast = false;
    private int showPage = 1;

    private OptionsPickerView pvOptions;

    private RelativeLayout ll_bill;
    private TextView tv_bill;
    private ArrayList<String> item = new ArrayList<>();

    private int order_type = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        datas = new ArrayList<>();
        initView();
    }

    private void initView(){

        ll_back = (LinearLayout) findViewById(R.id.ll_backBtn);

        pvOptions = new OptionsPickerView(context,false);
        pvOptions.setTitle("交易类型");

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
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark), getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light));

        ll_bill = (RelativeLayout)findViewById(R.id.ll_bill);
        tv_bill = (TextView)findViewById(R.id.tv_bill);

        myList.setOnItemClickListener(this);
        if(datas.isEmpty()){
            initHttp();
        }

        myAdapter = new BillAdapter(context);
        myAdapter.setDatas(datas);
        myList.setAdapter(myAdapter);

        ll_back.setOnClickListener(this);
        ll_bill.setOnClickListener(this);
        footerLayout.setOnClickListener(this);

//        1骑行订单 2购买骑行卡订单 3调度费订单 4赔偿费订单 5充值订单
        item.add("骑行订单");
        item.add("购买骑行卡订单");
        item.add("调度费订单");
        item.add("赔偿费订单");
        item.add("充值订单");       //TODO  不支持的订单类型

        pvOptions.setPicker(item);
        pvOptions.setCyclic(false, false, false);
        pvOptions.setSelectOptions(0, 0, 0);

        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {

                order_type = options1+1;
                tv_bill.setText(item.get(options1));

                datas.clear();
                myAdapter.notifyDataSetChanged();

                initHttp();

//                school = item.get(options1)[0];
//                schoolText.setText(school);

//                cert_method = item1.get(options1)[1];
//
//                Log.e("pvOptions===", "==="+cert_method);
//
//                if("0".equals(cert_method)){
//                    isVisible = true;
//                    ll_1.setVisibility(View.VISIBLE);
//                    ll_2.setVisibility(View.GONE);
//                    ll_3.setVisibility(View.GONE);
//                }else if("1".equals(cert_method) || "2".equals(cert_method)){
//                    isVisible = false;
//                    ll_1.setVisibility(View.GONE);
//                }else{
//                    isVisible = true;
//                    ll_1.setVisibility(View.VISIBLE);
//                    ll_2.setVisibility(View.VISIBLE);
//                    ll_3.setVisibility(View.VISIBLE);
//                }
            }
        });
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

            case R.id.ll_bill:
                pvOptions.show();
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
        params.put("order_type", order_type);
        params.put("page",showPage);
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

                    Log.e("orders===1", datas.size()+"==="+order_type+"==="+array.length());

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



                    for (int i = 0; i < array.length(); i++) {

                        Log.e("orders===2", "==="+array.getJSONObject(i).toString());

                        BillBean bean = JSON.parseObject(array.getJSONObject(i).toString(), BillBean.class);
                        bean.setOrder_type(order_type);
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
}
