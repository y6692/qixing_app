package cn.qimate.bike.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import cn.qimate.bike.adapter.MyIntegralRecordAdapter;
import cn.qimate.bike.adapter.SchoolListAdapter;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.model.GlobalConfig;
import cn.qimate.bike.model.MyIntegralRecordBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.SchoolListBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator on 2017/2/18 0018.
 */

public class SchoolSelectActivity extends SwipeBackActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener {

    private Context context;

    private LinearLayout ll_back;
    private TextView title;

    private EditText et_school;
    private Button btnQuery;

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

    private SchoolListAdapter myAdapter;
    private List<SchoolListBean> datas;
    private boolean isRefresh = true;// 是否刷新中
    private boolean isLast = false;
    private int showPage = 1;

    private List<SchoolListBean> schoolList;
    static ArrayList<String> item = new ArrayList<>();
    static ArrayList<String[]> item1 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_select);
        context = this;

        datas = new ArrayList<>();
        schoolList = new ArrayList<>();

        initView();
    }

    private void initView(){

        ll_back = (LinearLayout) findViewById(R.id.ll_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("信用记录");

//        btnQuery = (Button)findViewById(R.id.btn_click_one);
        et_school = (EditText)findViewById(R.id.et_school);

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

//        if(datas.isEmpty()){
//            initHttp();
//        }

        if (schoolList.isEmpty() || item1.isEmpty()){
            getSchoolList();
        }

        myAdapter = new SchoolListAdapter(context);
        myAdapter.setDatas(datas);
        myList.setAdapter(myAdapter);

        ll_back.setOnClickListener(this);
        footerLayout.setOnClickListener(this);
//        btnQuery.setOnClickListener(this);

        et_school.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}//文本改变之前执行

            @Override
            //文本改变的时候执行
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //如果长度为0
                querySchoolList();
            }

            public void afterTextChanged(Editable s) { }//文本改变之后执行
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SchoolListBean bean = myAdapter.getDatas().get(position);

        Intent rIntent = new Intent();
        rIntent.putExtra("school_id", bean.getId());
        rIntent.putExtra("school_name", bean.getSchool());
        setResult(RESULT_OK, rIntent);
        scrollToFinishActivity();
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
            getSchoolList();
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

//            case R.id.btn_click_one:
//                querySchoolList();
//
//                break;

            case R.id.footer_Layout:
                if (!isLast) {
                    showPage += 1;
                    getSchoolList();
                    myAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    private void querySchoolList(){
        Log.e("onClick===et", "==="+et_school.getText());

        myAdapter.getDatas().clear();

        for (int i = 0; i < schoolList.size(); i++){
//                    SchoolListBean bean = JSON.parseObject(JSONArray.getJSONObject(i).toString(),SchoolListBean.class);
//                    schoolList.add(bean);
            if(schoolList.get(i).getSchool().contains(et_school.getText().toString())){
                datas.add(schoolList.get(i));
            }


            myAdapter.notifyDataSetChanged();

//                    item.add(bean.getSchool());
//                    item1.add(new String[]{bean.getSchool(), bean.getCert_method()});

        }
    }

    private void getSchoolList(){
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);

        HttpHelper.get(context, Urls.schoolList, params, new TextHttpResponseHandler() {
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

                            Log.e("getSchoolList===", "==="+responseString);

                            if (result.getFlag().equals("Success")) {
                                JSONArray JSONArray = new JSONArray(result.getData());
                                if (schoolList.size() != 0 || !schoolList.isEmpty()){
                                    schoolList.clear();
                                }
                                if (item.size() != 0 || !item.isEmpty()){
                                    item.clear();
                                }
                                if (item1.size() != 0 || !item1.isEmpty()){
                                    item1.clear();
                                }
                                for (int i = 0; i < JSONArray.length();i++){
                                    SchoolListBean bean = JSON.parseObject(JSONArray.getJSONObject(i).toString(),SchoolListBean.class);
                                    schoolList.add(bean);
                                    datas.add(bean);
//                                    item.add(bean.getSchool()+"_"+bean.getCert_method());
                                    item.add(bean.getSchool());
                                    item1.add(new String[]{bean.getSchool(), bean.getCert_method()});

                                }

                                setFooterType(2);

                                Log.e("getSchoolList===2", datas.size()+"==="+schoolList.size());

                                myAdapter.notifyDataSetChanged();

//                                handler.sendEmptyMessage(0x123);
                            }else {
                                Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            isRefresh = false;
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                });

            }
        });
    }


    private void getSchoolList2(){

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
        HttpHelper.get(context, Urls.schoolList, params, new TextHttpResponseHandler() {
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
                    if ("Success".equals(result.getFlag())){
                        JSONArray array = new JSONArray(result.getData());
                        for (int i = 0; i < array.length(); i++) {
//                            MyIntegralRecordBean bean = JSON.parseObject(array.getJSONObject(i).toString(), MyIntegralRecordBean.class);
//                            datas.add(bean);
                            SchoolListBean bean = JSON.parseObject(array.getJSONObject(i).toString(), SchoolListBean.class);
                            schoolList.add(bean);
                            datas.add(bean);
                        }

                        if (array.length() == 0 && showPage == 1) {
                            setFooterType(4);// 暂无数据
                            return;
                        } else if (array.length() < GlobalConfig.PAGE_SIZE && showPage == 1 && array.length() >= 1) {
                            setFooterType(5);
                        } else if (array.length() < GlobalConfig.PAGE_SIZE) {
                            setFooterType(2);// 数据已全部加载
                        } else if (array.length() == GlobalConfig.PAGE_SIZE) {
                            setFooterType(0);// 点击加载更多
                        }
                    } else {
                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                } finally {
                    isRefresh = false;
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
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
