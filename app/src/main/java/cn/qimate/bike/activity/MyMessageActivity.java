package cn.qimate.bike.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import cn.qimate.bike.adapter.MyMessageAdapter;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.model.GlobalConfig;
import cn.qimate.bike.model.MyMessageBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UserBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.ToastUtil;

/**
 * Created by Administrator1 on 2017/2/14.
 */

public class MyMessageActivity extends SwipeBackActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener {

    private Context context = this;
    private ImageView backImg;
    private TextView title;
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

    private MyMessageAdapter myAdapter;
    private List<MyMessageBean> datas;

    private boolean isRefresh = true;// 是否刷新中
    private boolean isLast = false;
    private int showPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_my_message);
        datas = new ArrayList<>();
        initView();
    }

    private void initView(){

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);

        // list投资列表
        footerView = LayoutInflater.from(context).inflate(R.layout.footer_item, null);
        footerViewType01 = footerView.findViewById(R.id.footer_Layout_type01);// 点击加载更多
        footerViewType02 = footerView.findViewById(R.id.footer_Layout_type02);// 正在加载，请您稍等
        footerViewType03 = footerView.findViewById(R.id.footer_Layout_type03);// 已无更多
        footerViewType04 = footerView.findViewById(R.id.footer_Layout_type04);// 刷新失败，请重试
        footerViewType05 = footerView.findViewById(R.id.footer_Layout_type05);// 暂无数据
        iv_type05 = footerView.findViewById(R.id.footer_Layout_iv_type05);
        tv_type05 = footerView.findViewById(R.id.footer_Layout_tv_type05);
        iv_type05.setImageResource(R.drawable.no_msg_icon);
        tv_type05.setText("您还未有消息！");

        footerLayout = footerView.findViewById(R.id.footer_Layout);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.Layout_swipeParentLayout);
        myList = (ListView)findViewById(R.id.Layout_swipeListView);
        myList.addFooterView(footerView);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark), getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light));

        myList.setOnItemClickListener(this);


        myAdapter = new MyMessageAdapter(context);
        myAdapter.setDatas(datas);
        myList.setAdapter(myAdapter);

        backImg.setOnClickListener(this);
        footerLayout.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long rid) {
        MyMessageBean bean = myAdapter.getDatas().get(position);

        int id = bean.getId();
        int is_read= bean.getIs_read();
        String title = bean.getTitle();
        String created_at = bean.getCreated_at();
        String action_type = bean.getAction_type();
        String action_content = bean.getAction_content();

        Log.e("mma===onItemClick", id+"==="+ is_read+"==="+ title +"==="+ action_type +"==="+ action_content);

//        Intent intent = new Intent(context, MyMessageDatailActivity.class);
//        intent.putExtra("id", id);
//        intent.putExtra("title", title);
//        intent.putExtra("created_at", created_at);
//        intent.putExtra("action_content", action_content);
//        startActivity(intent);

        if(is_read==0){
            notification(id);
        }

        bannerTz(title, action_type, action_content);

    }

    private void bannerTz(String title, String type, String url) {
        if("app".equals(type)){
            if("home".equals(url)){
//                ((MainActivity)activity).changeTab(0);

                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }else if("wallet".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
//                    ((MainActivity)activity).changeTab(1);

                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                }
            }else if("member".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
//                    ((MainActivity)activity).changeTab(2);

                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                }
            }else if("recharge".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context, RechargeActivity.class);
                }
            }else if("cycling_card".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    user(url);
                }
            }else if("my_cycling_card".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    user(url);
                }
            }else if("cycling_card_exchange".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    user(url);
                }
            }else if("bill".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context, BillActivity.class);
                }
            }else if("order".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context, MyOrderActivity.class);
                }
            }else if("notice".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context, MyMessageActivity.class);
                }
            }else if("service".equals(url)){
                UIHelper.goToAct(context, ServiceCenterActivity.class);
            }else if("phone_change".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context, ChangePhoneActivity.class);
                }
            }else if("setting".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context, SettingActivity.class);
                }
            }else if("cert".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context, AuthCenterActivity.class);
                }
            }else if("cert1".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context, DepositFreeAuthActivity.class);
                }
            }else if("cert2".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context,RealNameAuthActivity.class);
                }
            }else if("car_bad".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    UIHelper.goToAct(context, CarFaultProActivity.class);
                }
            }else{
                Log.e("bannerTz===else", "===");

                initHttp();
            }

        }else if("h5".equals(type)){
            UIHelper.goWebViewAct(context, title, url);
        }
    }

    private void user(final String url) {
        Log.e("pf===user", "===");

        HttpHelper.get2(context, Urls.user, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon("pf===user", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            Log.e("pf===user1", responseString + "===" + result.data);

                            UserBean bean = JSON.parseObject(result.getData(), UserBean.class);

                            int cert1_status = bean.getCert1_status();

                            if(cert1_status!=3){
                                ToastUtil.showMessageApp(context, "请先进行免押金认证");
                            }else {

                                if ("cycling_card".equals(url)) {
                                    UIHelper.goToAct(context, PayCartActivity.class);
                                } else if ("my_cycling_card".equals(url)) {
                                    UIHelper.goToAct(context, MyCartActivity.class);
                                } else if ("cycling_card_exchange".equals(url)) {
                                    UIHelper.goToAct(context, ExchangeActivity.class);
                                }
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

    private void notification(int id){
        RequestParams params = new RequestParams();
        params.put("id", id);

        Log.e("notification===", id+"===");

        HttpHelper.post(context, Urls.notification, params, new TextHttpResponseHandler() {
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
                    Log.e("notification===", "==="+responseString);

                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

//                    if (result.getFlag().equals("Success")) {
//                        osn = result.getData();
//                    } else {
//                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                    }
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
    public void onResume() {
        super.onResume();
        isRefresh = true;

        Log.e("mma==onResume", "==="+datas);
        initHttp();
        if(datas.size()!=0){


//            myAdapter.notifyDataSetChanged();
        }



//        if(datas.isEmpty()){
//
//        }

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
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
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
        params.put("page",showPage);
        params.put("per_page", GlobalConfig.PAGE_SIZE);
        HttpHelper.get(context, Urls.notices, params, new TextHttpResponseHandler() {
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
                    Log.e("notices===", "==="+responseString);

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

                    if(datas.size()>0){
                        datas.clear();
                    }

                    for (int i = 0; i < array.length(); i++) {
                        MyMessageBean bean = JSON.parseObject(array.getJSONObject(i).toString(), MyMessageBean.class);
                        datas.add(bean);
                    }

                    myAdapter.notifyDataSetChanged();
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
