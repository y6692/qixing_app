package cn.qimate.bike.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
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
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.MyCartDetailActivity;
import cn.qimate.bike.activity.PayMontCartActivity;
import cn.qimate.bike.base.BaseFragment;
import cn.qimate.bike.base.BaseViewAdapter;
import cn.qimate.bike.base.BaseViewHolder;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.GlobalConfig;
import cn.qimate.bike.model.MyCartBean;
import cn.qimate.bike.model.ResultConsel;

import static android.app.Activity.RESULT_OK;

@SuppressLint("NewApi")
public class MyEbikeCartFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener{

    Unbinder unbinder;

    private Context context;

//    @BindView(R.id.Layout_swipeListView)
    ListView listview;
//    @BindView(R.id.Layout_swipeParentLayout)
    SwipeRefreshLayout swipeRefreshLayout;
//    @BindView(R.id.msgText)
//    TextView msgText;

    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstFix = true;
    private LatLng myLocation = null;
    private Circle mCircle;
    private BitmapDescriptor successDescripter;
    private BitmapDescriptor bikeDescripter;
    private Handler handler = new Handler();
    private Marker centerMarker;
    private boolean isMovingMarker = false;

    private List<Marker> bikeMarkerList;
    private boolean isUp = false;

    private double latitude = 0.0;
    private double longitude = 0.0;
    private int isLock = 0;
    private View v;

    private LoadingDialog loadingDialog;
    private Dialog dialog;
    private List<MyCartBean> datas;
    private MyAdapter myAdapter;
    private int curPosition = 0;
    private int showPage = 1;
    private boolean isRefresh = true;// 是否刷新中
    private boolean isLast = false;

    private View footerView;
    private View footerViewType01;
    private View footerViewType02;
    private View footerViewType03;
    private View footerViewType04;
    private View footerViewType05;

    private ImageView iv_type05;
    private TextView tv_type05;

    private View footerLayout;

    String badtime="2115-02-08 20:20";
    String codenum="";
    String totalnum="";

    public String data;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_my_ebike_cart, null);
        unbinder = ButterKnife.bind(this, v);

        return v;
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        datas = new ArrayList<>();

        initView();

//        initHttp();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            while (true){
//
//                try {
//                    Thread.sleep(30*1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                m_myHandler.sendEmptyMessage(1);
//            }
//
//            }
//        }).start();

    }

    protected Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 0:
                    break;
                case 1:
                    resetList();

                    break;
                default:
                    break;
            }
            return false;
        }
    });

    public void resetList(){
        showPage = 1;
        badtime="2115-02-08 20:20";
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            //pause
        }else{
            //resume
//            resetList();
        }
    }


    private void initView(){
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

//        dialog = new Dialog(context, R.style.main_publishdialog_style);
//        View tagView = LayoutInflater.from(context).inflate(R.layout.dialog_deduct_mark, null);
//        dialog.setContentView(tagView);
//        dialog.setCanceledOnTouchOutside(false);

        footerView = LayoutInflater.from(context).inflate(R.layout.footer_item, null);
        footerViewType01 = footerView.findViewById(R.id.footer_Layout_type01);// 点击加载更多
        footerViewType02 = footerView.findViewById(R.id.footer_Layout_type02);// 正在加载，请您稍等
        footerViewType03 = footerView.findViewById(R.id.footer_Layout_type03);// 已无更多
        footerViewType04 = footerView.findViewById(R.id.footer_Layout_type04);// 刷新失败，请重试
        footerViewType05 = footerView.findViewById(R.id.footer_Layout_type05);// 暂无数据
        footerLayout = footerView.findViewById(R.id.footer_Layout);
        footerViewType01.setVisibility(View.GONE);

        iv_type05 = footerView.findViewById(R.id.footer_Layout_iv_type05);
        tv_type05 = footerView.findViewById(R.id.footer_Layout_tv_type05);
        iv_type05.setImageResource(R.drawable.no_card_icon);
        tv_type05.setText("赶紧购买套餐卡吧…");


        swipeRefreshLayout = (SwipeRefreshLayout)getActivity().findViewById(R.id.Layout_swipeParentLayout2);
        listview = (ListView)getActivity().findViewById(R.id.Layout_swipeListView2);
        listview.addFooterView(footerView);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark), getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light));


        JSONArray array = null;
        try {
            array = new JSONArray(data);

            Log.e("mecf===initView", "==="+array);    //#BDBDBD

            if (array.length() == 0) {
//                footerViewType05.setVisibility(View.VISIBLE);

                footerLayout.setVisibility(View.VISIBLE);
                setFooterType(4);
            }else{
                footerViewType05.setVisibility(View.GONE);
            }

            for (int i = 0; i < array.length();i++){
                MyCartBean bean = JSON.parseObject(array.getJSONObject(i).toString(), MyCartBean.class);

                datas.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        myAdapter = new MyAdapter(context);
        myAdapter.setDatas(datas);
        listview.setAdapter(myAdapter);

        footerLayout.setOnClickListener(this);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long vid) {
//                curPosition = position;
//                WindowManager.LayoutParams params1 = dialog.getWindow().getAttributes();
//                params1.width = LinearLayout.LayoutParams.MATCH_PARENT;
//                params1.height = LinearLayout.LayoutParams.MATCH_PARENT;
//                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                dialog.getWindow().setAttributes(params1);
//                dialog.show();

//                Intent intent = new Intent(context, PayMontCartActivity.class);
//                startActivity(intent);

                int id = myAdapter.getDatas().get(position).getId();

                Log.e("mbcf===MyAdapter", "==="+id);    //#BDBDBD


                Intent intent = new Intent(context, MyCartDetailActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @SuppressLint("NewApi")
    private class MyAdapter extends BaseViewAdapter<MyCartBean> {

        private LayoutInflater inflater;

        public MyAdapter(Context context){
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_my_ebike_cart, null);
            }

            RelativeLayout ll_bg = BaseViewHolder.get(convertView,R.id.item_bg);
            ImageView iv_state = BaseViewHolder.get(convertView,R.id.item_state);
            TextView carmodel_name = BaseViewHolder.get(convertView,R.id.item_carmodel_name);
            TextView name = BaseViewHolder.get(convertView,R.id.item_name);
            TextView remaining_free = BaseViewHolder.get(convertView,R.id.item_remaining_free);
            TextView upper_daily_limit = BaseViewHolder.get(convertView,R.id.item_upper_daily_limit);

            final MyCartBean bean = getDatas().get(position);

            Log.e("mecf===MyAdapter", bean.getName()+"==="+bean.getState()+"==="+bean.getRemaining_free_days()+"==="+bean.getRemaining_free_times()+"==="+bean.getLinear_gradient()[0]);    //#BDBDBD

            GradientDrawable drawable = (GradientDrawable)ll_bg.getBackground();
            if(bean.getLinear_gradient()!=null){

                drawable.mutate();

                if(bean.getLinear_gradient().length==1){
                    drawable.setColors(new int[]{Color.parseColor(bean.getLinear_gradient()[0]), Color.parseColor(bean.getLinear_gradient()[0])});
                }else{
                    drawable.setColors(new int[]{Color.parseColor(bean.getLinear_gradient()[0]), Color.parseColor(bean.getLinear_gradient()[1])});
                }
            }

//            drawable.setColors(new int[]{Color.parseColor("#BDBDBD"), Color.parseColor("#ffffff")});

            carmodel_name.setText(bean.getCarmodel_name());
            name.setText(bean.getName());
            int type = bean.getType();
            if(type==1){
                remaining_free.setText("剩余"+bean.getRemaining_free_days()+"天");
            }else{
                remaining_free.setText("剩余"+bean.getRemaining_free_times()+"次");
            }

            if(bean.getUpper_daily_limit()==1){
                upper_daily_limit.setVisibility(View.VISIBLE);
            }else{
                upper_daily_limit.setVisibility(View.GONE);
            }

            int state = bean.getState();    //状态 0待使用 1使用中 2已过期 3已用完
            if(state==0 || state==1){
                iv_state.setVisibility(View.GONE);

                if(type==1){
                    if(state==0){
                        remaining_free.setVisibility(View.GONE);
                    }else{
                        remaining_free.setVisibility(View.VISIBLE);
                    }
                }else{
                    remaining_free.setVisibility(View.VISIBLE);
                }


            }else if(state==2){
                remaining_free.setVisibility(View.GONE);
                iv_state.setVisibility(View.VISIBLE);
                iv_state.setImageResource(R.drawable.cart_expire_icon);
            }else if(state==3){
                remaining_free.setVisibility(View.GONE);
                iv_state.setVisibility(View.VISIBLE);
                iv_state.setImageResource(R.drawable.cart_used_icon);
            }


            return convertView;
        }
    }

    private void initHttp(){
//        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
//        if (access_token == null || "".equals(access_token)){
//            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
//            return;
//        }
//        RequestParams params = new RequestParams();
//        params.put("type", 2);
//        params.put("page", showPage);
//        params.put("per_page", GlobalConfig.PAGE_SIZE);
//
//        HttpHelper.get(context, Urls.my_cycling_cards, params, new TextHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                m_myHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        setFooterType(1);
//                    }
//                });
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, final Throwable throwable) {
//                m_myHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        UIHelper.ToastError(context, throwable.toString());
//                        swipeRefreshLayout.setRefreshing(false);
//                        isRefresh = false;
//                        setFooterType(3);
//                        setFooterVisibility();
//                    }
//                });
//
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
//                m_myHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Log.e("my_cycling_cards===eb_1","==="+responseString);
//
//                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//
//                            JSONArray array = new JSONArray(result.getData());
//                            if (array.length() == 0 && showPage == 1) {
//                                footerLayout.setVisibility(View.VISIBLE);
//                                setFooterType(4);
//                                return;
//                            } else if (array.length() < GlobalConfig.PAGE_SIZE && showPage == 1) {
//                                footerLayout.setVisibility(View.GONE);
//                                setFooterType(5);
//                            } else if (array.length() < GlobalConfig.PAGE_SIZE) {
//                                footerLayout.setVisibility(View.VISIBLE);
//                                setFooterType(2);
//                            } else if (array.length() >= 10) {
//                                footerLayout.setVisibility(View.VISIBLE);
//                                setFooterType(0);
//                            }
//
//                            for (int i = 0; i < array.length();i++){
//                                MyCartBean bean = JSON.parseObject(array.getJSONObject(i).toString(), MyCartBean.class);
//
//
//                                datas.add(bean);
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        } finally {
//                            swipeRefreshLayout.setRefreshing(false);
//                            isRefresh = false;
//                            setFooterVisibility();
//                        }
//                    }
//                });
//
//            }
//        });
    }

    private void initHttp2(){

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("page", showPage);
        params.put("pagesize", GlobalConfig.PAGE_SIZE);

//        HttpHelper.get(context, Urls.badcarList, params, new TextHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                setFooterType(1);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                UIHelper.ToastError(context, throwable.toString());
//                swipeRefreshLayout.setRefreshing(false);
//                isRefresh = false;
//                setFooterType(3);
//                setFooterVisibility();
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                try {
//                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                    if (result.getFlag().equals("Success")) {
//                        JSONArray array = new JSONArray(result.getData());
//                        if (array.length() == 0 && showPage == 1) {
//                            footerLayout.setVisibility(View.VISIBLE);
//                            setFooterType(4);
//                            return;
//                        } else if (array.length() < GlobalConfig.PAGE_SIZE && showPage == 1) {
//                            footerLayout.setVisibility(View.GONE);
//                            setFooterType(5);
//                        } else if (array.length() < GlobalConfig.PAGE_SIZE) {
//                            footerLayout.setVisibility(View.VISIBLE);
//                            setFooterType(2);
//                        } else if (array.length() >= 10) {
//                            footerLayout.setVisibility(View.VISIBLE);
//                            setFooterType(0);
//                        }
//
//                        for (int i = 0; i < array.length();i++){
//                            BadCarBean bean = JSON.parseObject(array.getJSONObject(i).toString(), BadCarBean.class);
//
//                            if(i==0 && bean.getBadtime().compareTo(badtime)<0){
//                                badtime = bean.getBadtime();
//                                codenum = bean.getCodenum();
//                                totalnum = bean.getTotalnum();
//                            }
//
//                            datas.add(bean);
//                        }
//
//                        Intent intent = new Intent("data.broadcast.action");
//                        intent.putExtra("codenum", codenum);
//                        intent.putExtra("count", Integer.parseInt(totalnum));
//                        context.sendBroadcast(intent);
//
//                    } else {
//                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    swipeRefreshLayout.setRefreshing(false);
//                    isRefresh = false;
//                    setFooterVisibility();
//                }
////                if (loadingDialog != null && loadingDialog.isShowing()){
////                    loadingDialog.dismiss();
////                }
//            }
//        });
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
    public void onResume() {
        super.onResume();
        isRefresh = true;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){

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

    @Override
    public void onPause() {
        super.onPause();
    }
    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("requestCode===", "==="+requestCode);

        switch (requestCode) {

            case 1:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("QR_CODE");
                } else {
					Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                }

                Log.e("requestCode===1", "==="+resultCode);
                break;

            default:
                break;

        }
    }


}
