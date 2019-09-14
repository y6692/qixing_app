package cn.qimate.bike.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.jock.pickerview.view.view.OptionsPickerView;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.adapter.HotQuestionAdapter;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.CommonQuestionBean;
import cn.qimate.bike.model.GlobalConfig;
import cn.qimate.bike.model.HotQuestionBean;
import cn.qimate.bike.model.QuestionBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * 客服中心
 * Created by yuanyi on 2019/3/19 0012.
 */

public class ServiceCenter3Activity extends SwipeBackActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener,AdapterView.OnItemClickListener {

    private Context context;
    private LoadingDialog loadingDialog;
    private LinearLayout ll_back;
    private TextView title;
    private TextView rightBtn;
    private TextView tv_title;

    private TextView nameEdit;
    private TextView phoneNum;
    private RelativeLayout rl_name;
    private RelativeLayout rl_phoneNum;
    private RelativeLayout rl_realNameAuth;
    private RelativeLayout rl_studentAuth;

    private OptionsPickerView pvOptions;
    private OptionsPickerView pvOptions1;
    private OptionsPickerView pvOptions2;
    private String sex = "";
    private String school = "";

    private ImageView backImg;
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

    private HotQuestionAdapter myAdapter;
    //    private List<QuestionBean> data;
    private List<HotQuestionBean> hotData;
    private List<CommonQuestionBean> commonData;
    private boolean isRefresh = true;// 是否刷新中
    private boolean isLast = false;
    private int showPage = 1;
    private String starttime = "";
    private String endtime = "";

    private String title2 = "";
    private String data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_center2);
        context = this;
//        data = new ArrayList<>();
        hotData = new ArrayList<>();

        data = getIntent().getStringExtra("data");
        title2 = getIntent().getStringExtra("title");

        initView();
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

//        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("客服中心");
        rightBtn = (TextView) findViewById(R.id.mainUI_title_rightBtn);
        rightBtn.setText("问题反馈");

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(title2);

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
//        myList.addFooterView(footerView);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark), getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light));

        myList.setOnItemClickListener(this);
//        if(hotData.isEmpty()){
//            initHttp();
//        }



//        myAdapter = new QuestionAdapter(context);
        myAdapter = new HotQuestionAdapter(context);
        myAdapter.setDatas(hotData);
        myList.setAdapter(myAdapter);

//        if("".equals(data)){
//            initHttp();
//        }else{
//        }

        JSONArray array = null;
        try {
//                array = new JSONArray("[{\"childrens\":[],\"content\":\"<p>电单车还不了车~</p>\",\"h5_url\":\"http://www.7mate.cn/Home/Question/show.html?id=6\",\"title\":\"电单车还不了车\"},{\"childrens\":[],\"content\":\"<p>单车还不了车！！！</p>\",\"h5_url\":\"http://www.7mate.cn/Home/Question/show.html?id=7\",\"title\":\"单车还不了车\"}]");
            array = new JSONArray(data);

            for (int i = 0; i < array.length(); i++) {
                HotQuestionBean bean = JSON.parseObject(array.getJSONObject(i).toString(), HotQuestionBean.class);

                hotData.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setListViewHeight(myList);

        backImg.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        footerLayout.setOnClickListener(this);

    }

    private void setListViewHeight(ListView listView){
        ListAdapter listAdapter = listView.getAdapter(); //得到ListView 添加的适配器
        if(listAdapter == null){
            return;
        }

        View itemView = listAdapter.getView(0, null, listView); //获取其中的一项
        //进行这一项的测量，为什么加这一步，具体分析可以参考 https://www.jianshu.com/p/dbd6afb2c890这篇文章
        itemView.measure(0,0);
        int itemHeight = itemView.getMeasuredHeight(); //一项的高度
        int itemCount = listAdapter.getCount();//得到总的项数
        LinearLayout.LayoutParams layoutParams = null; //进行布局参数的设置

        Log.e("setListViewHeight===", itemHeight+"==="+itemCount);

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT ,itemHeight*(itemCount-1));
        listView.setLayoutParams(layoutParams);

        swipeRefreshLayout.setLayoutParams(layoutParams);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


//        UIHelper.goWebViewAct(context,"停车须知",Urls.phtml5 + uid);

        Log.e("onItemClick===", "==="+ myAdapter.getDatas().get(position).getChildrens());

        String s = myAdapter.getDatas().get(position).getChildrens();

        if ("[]".equals(s)){
            UIHelper.goWebViewAct(context, myAdapter.getDatas().get(position).getTitle(), myAdapter.getDatas().get(position).getH5_url());
        }else{
            Intent intent = new Intent(context, ServiceCenter2Activity.class);
            intent.putExtra("data", s);
            startActivity(intent);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        isRefresh = true;
        if(hotData.size()!=0){
            myAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onRefresh() {
        showPage = 1;
        if (!isRefresh) {
            if(hotData.size()!=0){
                myAdapter.getDatas().clear();
                myAdapter.notifyDataSetChanged();
            }
            isRefresh = true;
//            initHttp();
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void initHttp(){
        Log.e("initHttp===", "==="+Urls.question);

//        [{"childrens":[],"content":"<p>电单车还不了车~</p>","h5_url":"http://www.7mate.cn/Home/Question/show.html?id=6","title":"电单车还不了车"},{"childrens":[],"content":"<p>单车还不了车！！！</p>","h5_url":"http://www.7mate.cn/Home/Question/show.html?id=7","title":"单车还不了车"}]

        HttpHelper.get(context, Urls.question, null, new TextHttpResponseHandler() {

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
//                        JSONArray array = new JSONArray(result.getData());
                        JSONObject jsonObject = new JSONObject(result.getData());
                        QuestionBean questionBean = JSON.parseObject(result.getData(), QuestionBean.class);

                        Log.e("initHttp===Success", "==="+questionBean.getHot_problem());

                        JSONArray array = new JSONArray(questionBean.getHot_problem());

//                        HotQuestionBean hotQuestionBean = JSON.parseObject(jsonObject.getString("hot_problem"), HotQuestionBean.class);
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

                        Log.e("initHttp===Success2", array.length()+"==="+array.length());

                        for (int i = 0; i < array.length(); i++) {
                            HotQuestionBean bean = JSON.parseObject(array.getJSONObject(i).toString(), HotQuestionBean.class);
                            Log.e("initHttp===Success", "333==="+bean);

                            hotData.add(bean);
                        }

//                        Log.e("initHttp===Success", "==="+hotQuestionBean.getTitle());

                    } else {
                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                    Log.e("initHttp===e", "==="+e.toString());

                } finally {
                    swipeRefreshLayout.setRefreshing(false);
                    isRefresh = false;
                    setFooterVisibility();
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



    @Override
    public void onClick(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;

            case R.id.mainUI_title_rightBtn:
//                SharedPreferencesUrls.getInstance().putString("type", "1");
                UIHelper.goToAct(context,FeedbackActivity.class);
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
}
