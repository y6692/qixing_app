package cn.qimate.bike.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
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
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.nostra13.universalimageloader.core.ImageLoader;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.PayMontCartActivity;
import cn.qimate.bike.activity.SettlementPlatformActivity;
import cn.qimate.bike.base.BaseFragment;
import cn.qimate.bike.base.BaseViewAdapter;
import cn.qimate.bike.base.BaseViewHolder;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.BadCarBean;
import cn.qimate.bike.model.GlobalConfig;
import cn.qimate.bike.model.PayCartBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.view.RoundImageView;

import static android.app.Activity.RESULT_OK;

@SuppressLint("NewApi")
public class BikeCartFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
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
    private List<PayCartBean> datas;
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

    private String badtime="2115-02-08 20:20";
    private String codenum="";
    private String totalnum="";

//    private String card_code;
    private String price;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_bike_cart, null);
        unbinder = ButterKnife.bind(this, v);

        return v;
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        datas = new ArrayList<>();

        initView();

        initHttp();

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
        tv_type05.setText("暂无套餐卡");

        swipeRefreshLayout = (SwipeRefreshLayout)getActivity().findViewById(R.id.Layout_swipeParentLayout);
        listview = (ListView)getActivity().findViewById(R.id.Layout_swipeListView);
        listview.addFooterView(footerView);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark), getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light));

//        myList.setOnItemClickListener(this);

        myAdapter = new MyAdapter(context);
        myAdapter.setDatas(datas);
        listview.setAdapter(myAdapter);

        footerLayout.setOnClickListener(this);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                curPosition = position;
//                WindowManager.LayoutParams params1 = dialog.getWindow().getAttributes();
//                params1.width = LinearLayout.LayoutParams.MATCH_PARENT;
//                params1.height = LinearLayout.LayoutParams.MATCH_PARENT;
//                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                dialog.getWindow().setAttributes(params1);
//                dialog.show();

//                Intent intent = new Intent(context, PayMontCartActivity.class);
//                startActivity(intent);
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
    private class MyAdapter extends BaseViewAdapter<PayCartBean> {

        private LayoutInflater inflater;

        public MyAdapter(Context context){
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_bike_pay_cart, null);
            }
            TextView name = BaseViewHolder.get(convertView,R.id.item_name);
            TextView tv_price = BaseViewHolder.get(convertView,R.id.tv_price);
            TextView tv_original_price = BaseViewHolder.get(convertView,R.id.tv_original_price);
            final TextView tv_desc = BaseViewHolder.get(convertView,R.id.tv_desc);
            final ImageView iv_down = BaseViewHolder.get(convertView,R.id.item_down);
            LinearLayout ll_payBtn = BaseViewHolder.get(convertView,R.id.ll_payBtn);
            RelativeLayout rl_desc = BaseViewHolder.get(convertView,R.id.item_rl_desc);
            final PayCartBean bean = getDatas().get(position);

            name.setText(bean.getName());
            final String card_code = bean.getCode();
            String price = bean.getPrice();
            tv_price.setText(price);
            tv_original_price.setText("¥"+bean.getOriginal_price());
            tv_desc.setText(bean.getDesc());

            RoundImageView iv_img = BaseViewHolder.get(convertView, R.id.item_iv_img);
            ImageLoader.getInstance().displayImage(bean.getImage(), iv_img);


            tv_original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

            ll_payBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    PayCartBean bean = myAdapter.getDatas().get(position);

                    Log.e("bcf===onClick", "==="+position+"==="+card_code);

//                    int order_id = bean.getOrder_id();

//                    order(bean.getCode());

                    order(card_code);

                }
            });

            rl_desc.setOnClickListener(new View.OnClickListener() {
                boolean flag = false;
                @Override
                public void onClick(View view) {

                    Log.e("mcf===onClick", "===");

                    if(flag){
                        flag = false;
                        iv_down.setImageResource(R.drawable.down_icon2);
                        tv_desc.setMaxLines(1);
                    }else{
                        flag = true;
                        iv_down.setImageResource(R.drawable.up_icon2);
                        tv_desc.setMaxLines(20);
                    }


                }
            });


//            if("即将超时".equals(bean.getStatus_name())){
//                num.setTextColor(getResources().getColor(R.color.red));
//                status.setTextColor(getResources().getColor(R.color.red));
//            }else{
//                num.setTextColor(getResources().getColor(R.color.tx_black));
//                status.setTextColor(getResources().getColor(R.color.tx_black));
//            }

            return convertView;
        }
    }

    private void order(String card_code) {
        Log.e("order===", "==="+codenum);

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
                            context.startActivity(intent);



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
        params.put("tab", 1);

        HttpHelper.get(context, Urls.cycling_cards, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在加载");
                    loadingDialog.show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Log.e("cycling_cards===1","==="+responseString);

                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                    JSONArray array = new JSONArray(result.getData());
//                    if (array.length() == 0 && showPage == 1) {
//                        footerLayout.setVisibility(View.VISIBLE);
//                        setFooterType(4);
//                        return;
//                    } else if (array.length() < GlobalConfig.PAGE_SIZE && showPage == 1) {
//                        footerLayout.setVisibility(View.GONE);
//                        setFooterType(5);
//                    } else if (array.length() < GlobalConfig.PAGE_SIZE) {
//                        footerLayout.setVisibility(View.VISIBLE);
//                        setFooterType(2);
//                    } else if (array.length() >= 10) {
//                        footerLayout.setVisibility(View.VISIBLE);
//                        setFooterType(0);
//                    }

                    if (array.length() == 0) {
                        footerViewType05.setVisibility(View.VISIBLE);
                    }else{
                        footerViewType05.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < array.length();i++){
                        PayCartBean bean = JSON.parseObject(array.getJSONObject(i).toString(), PayCartBean.class);

//                        if(i==0 && bean.getBadtime().compareTo(badtime)<0){
//                            badtime = bean.getBadtime();
//                            codenum = bean.getCodenum();
//                            totalnum = bean.getTotalnum();
//                        }

                        datas.add(bean);
                    }

                    myAdapter.notifyDataSetChanged();

//                    Intent intent = new Intent("data.broadcast.action");
//                    intent.putExtra("codenum", codenum);
//                    intent.putExtra("count", Integer.parseInt(totalnum));
//                    context.sendBroadcast(intent);

//                    if (result.getFlag().equals("Success")) {
//
//                    } else {
//                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
//                    swipeRefreshLayout.setRefreshing(false);
//                    isRefresh = false;
//                    setFooterVisibility();
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });
    }

    private void initHttp2(){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
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
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
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
