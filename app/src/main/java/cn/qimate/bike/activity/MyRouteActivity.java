package cn.qimate.bike.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.adapter.HistoryRoadAdapter;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.model.GlobalConfig;
import cn.qimate.bike.model.HistoryRoadBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator1 on 2017/2/13.
 */

public class MyRouteActivity extends SwipeBackActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener {

    private Context context;
    private LinearLayout ll_back;
    private TextView title;
    private TextView rightBtn;
    // List
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView myList;

    private View footerView;
    private View footerViewType01;
    private View footerViewType02;
    private View footerViewType03;
    private View footerViewType04;
    private View footerViewType05;

    private View footerLayout;

    private HistoryRoadAdapter myAdapter;
    private List<HistoryRoadBean>data;
    private boolean isRefresh = true;// 是否刷新中
    private boolean isLast = false;
    private int showPage = 1;
    private String starttime = "";
    private String endtime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_route);
        context = this;
        data = new ArrayList<>();
        initView();
    }

    private void initView(){

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("我的行程");
        rightBtn = (TextView)findViewById(R.id.mainUI_title_rightBtn);
        rightBtn.setText("查询");

        // list投资列表
        footerView = LayoutInflater.from(context).inflate(R.layout.footer_item, null);
        footerViewType01 = footerView.findViewById(R.id.footer_Layout_type01);// 点击加载更多
        footerViewType02 = footerView.findViewById(R.id.footer_Layout_type02);// 正在加载，请您稍等
        footerViewType03 = footerView.findViewById(R.id.footer_Layout_type03);// 已无更多
        footerViewType04 = footerView.findViewById(R.id.footer_Layout_type04);// 刷新失败，请重试
        footerViewType05 = footerView.findViewById(R.id.footer_Layout_type05);// 暂无数据

        footerLayout = footerView.findViewById(R.id.footer_Layout);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.Layout_swipeParentLayout);
        myList = (ListView)findViewById(R.id.Layout_swipeListView);
        myList.addFooterView(footerView);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark), getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light));

        myList.setOnItemClickListener(this);
        if(data.isEmpty()){
            initHttp();
        }

        myAdapter = new HistoryRoadAdapter(context);
        myAdapter.setDatas(data);
        myList.setAdapter(myAdapter);

        ll_back.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        footerLayout.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(context, RouteDetailActivity.class);
//        Intent intent = new Intent(context, HistoryRoadDetailActivity.class);
        intent.putExtra("oid", myAdapter.getDatas().get(position).getOid());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        isRefresh = true;
        if(data.size()!=0){
            myAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onRefresh() {
        showPage = 1;
        if (!isRefresh) {
            if(data.size()!=0){
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
            case R.id.ll_back:
                scrollToFinishActivity();
                break;
            case R.id.mainUI_title_rightBtn:
                Intent intent = new Intent(context, HistoryRoadFiltateActivity.class);
                startActivityForResult(intent,0);
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

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("page",showPage);
        params.put("pagesize", GlobalConfig.PAGE_SIZE);
        if (starttime != null && !"".equals(starttime)){
            params.put("starttime", starttime);
        }
        if (endtime != null && !"".equals(endtime)){
            params.put("endtime", endtime);
        }

        HttpHelper.get(context, Urls.myOrderlist, params, new TextHttpResponseHandler() {

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
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if ("Success".equals(result.getFlag())) {
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
                        for (int i = 0; i < array.length(); i++) {
                            HistoryRoadBean bean = JSON.parseObject(array.getJSONObject(i).toString(), HistoryRoadBean.class);
                            data.add(bean);
                        }

                    } else {
                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (data != null) {
                    starttime = data.getExtras().getString("starttime");
                    endtime = data.getExtras().getString("endtime");
                    onRefresh();
                }
                break;

            default:
                break;
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
