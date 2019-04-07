package cn.qimate.bike.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import cn.qimate.bike.activity.CouponDetailActivity;
import cn.qimate.bike.activity.FaultReportActivity;
import cn.qimate.bike.activity.PayMontCartActivity;
import cn.qimate.bike.activity.ReportViolationActivity;
import cn.qimate.bike.activity.ServiceCenterActivity;
import cn.qimate.bike.base.BaseFragment;
import cn.qimate.bike.base.BaseViewAdapter;
import cn.qimate.bike.base.BaseViewHolder;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.BadCarBean;
import cn.qimate.bike.model.GlobalConfig;
import cn.qimate.bike.model.HistoryRoadBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;

import static android.app.Activity.RESULT_OK;

@SuppressLint("NewApi")
public class MontCartFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener{
    private View v;
    Unbinder unbinder;

    private Context context;

    private RelativeLayout ll_coupon;
    private LinearLayout submitBtn;

    ListView listview;
    SwipeRefreshLayout swipeRefreshLayout;
    private MyAdapter myAdapter;

    private RelativeLayout rl_total_select;
    private ImageView iv_total_select;

    private View footerView;
    private View footerViewType01;
    private View footerViewType02;
    private View footerViewType03;
    private View footerViewType04;
    private View footerViewType05;

    private View footerLayout;
    private List<HistoryRoadBean> datas;
    private boolean isRefresh = true;// 是否刷新中
    private boolean isLast = false;
    private int showPage = 1;



    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_mont_cart, null);
        unbinder = ButterKnife.bind(this, v);

        return v;
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        datas = new ArrayList<>();

        initView();
//
//        initHttp();
//
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
//        showPage = 1;
//        badtime="2115-02-08 20:20";
//        if (!isRefresh) {
//            if(datas.size()!=0){
//                myAdapter.getDatas().clear();
//                myAdapter.notifyDataSetChanged();
//            }
//            isRefresh = true;
//            initHttp();
//        } else {
//            swipeRefreshLayout.setRefreshing(false);
//        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            //pause
        }else{
            //resume
        }
    }


    private void initView(){
//        loadingDialog = new LoadingDialog(context);
//        loadingDialog.setCancelable(false);
//        loadingDialog.setCanceledOnTouchOutside(false);

        ll_coupon = (RelativeLayout)getActivity().findViewById(R.id.ll_coupon);
        submitBtn = (LinearLayout)getActivity().findViewById(R.id.ui_payMonth_cart_submitBtn);

        ll_coupon.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.ll_coupon:
                initmPopupWindowView();
                break;

            case R.id.ui_payMonth_cart_submitBtn:
                initmPopupWindowView2();
                break;

            case R.id.rl_total_select:
                iv_total_select.setImageResource(R.drawable.pay_type_selected);
                for (int i = 0; i < datas.size(); i++) {
                    datas.get(i).setSelected(false);
                }
                myAdapter.notifyDataSetChanged();
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


    public void initmPopupWindowView(){

        // 获取自定义布局文件的视图
        View customView = getLayoutInflater().inflate(R.layout.pop_coupon_menu, null, false);
        // 创建PopupWindow宽度和高度
        RelativeLayout pop_win_bg = (RelativeLayout) customView.findViewById(R.id.pop_win_bg);
        ImageView iv_popup_window_back = (ImageView) customView.findViewById(R.id.popupWindow_back);

        rl_total_select = (RelativeLayout)customView.findViewById(R.id.rl_total_select);
        iv_total_select = (ImageView)customView.findViewById(R.id.iv_total_select);

        footerView = LayoutInflater.from(context).inflate(R.layout.footer_item, null);
        footerViewType01 = footerView.findViewById(R.id.footer_Layout_type01);// 点击加载更多
        footerViewType02 = footerView.findViewById(R.id.footer_Layout_type02);// 正在加载，请您稍等
        footerViewType03 = footerView.findViewById(R.id.footer_Layout_type03);// 已无更多
        footerViewType04 = footerView.findViewById(R.id.footer_Layout_type04);// 刷新失败，请重试
        footerViewType05 = footerView.findViewById(R.id.footer_Layout_type05);// 暂无数据
        footerLayout = footerView.findViewById(R.id.footer_Layout);


        swipeRefreshLayout = (SwipeRefreshLayout)customView.findViewById(R.id.Layout_couponParentLayout);
        listview = (ListView)customView.findViewById(R.id.Layout_couponListView);
        listview.addFooterView(footerView);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark), getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light));

        if(datas.isEmpty()){
            initHttp();
        }

        myAdapter = new MyAdapter(context);
        myAdapter.setDatas(datas);
        listview.setAdapter(myAdapter);

        footerLayout.setOnClickListener(this);
        rl_total_select.setOnClickListener(this);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                Toast.makeText(context, position+"==="+myAdapter.getDatas().get(position), Toast.LENGTH_SHORT).show();

                iv_total_select.setImageResource(R.drawable.pay_type_normal);

                if (!datas.get(position).isSelected()) {
                    datas.get(position).setSelected(true);
                    for (int i = 0; i < datas.size(); i++) {
                        if (i != position) {
                            datas.get(i).setSelected(false);
                        }
                    }
                }

                myAdapter.notifyDataSetChanged();


            }
        });






        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(getActivity());
        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
            UtilBitmap.blurImageView(context, iv_popup_window_back, 10,0xAA000000);
        } else {
            // 获取的Bitmap为null时，用半透明代替
            iv_popup_window_back.setBackgroundColor(0x77000000);
        }
        // 打开弹窗
        UtilAnim.showToUp(pop_win_bg, iv_popup_window_back);
        // 创建PopupWindow宽度和高度
        final PopupWindow popupwindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, true);
        /**
         * 设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
         */
        popupwindow.setAnimationStyle(R.style.PopupAnimation);
        popupwindow.setOutsideTouchable(false);

        popupwindow.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

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
//        if (starttime != null && !"".equals(starttime)){
//            params.put("starttime", starttime);
//        }
//        if (endtime != null && !"".equals(endtime)){
//            params.put("endtime", endtime);
//        }

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
                            datas.add(bean);
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
    private class MyAdapter extends BaseViewAdapter<HistoryRoadBean> {

        private LayoutInflater inflater;

        public MyAdapter(Context context){
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

//        isSelect()

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_coupon_record, null);
            }

            ImageView select = BaseViewHolder.get(convertView, R.id.item_select);
//            select.setImageResource(R.drawable.pay_type_normal);


//            TextView money = BaseViewHolder.get(convertView, R.id.item_historyRoad_money);
//            TextView bikeCode = BaseViewHolder.get(convertView, R.id.item_historyRoad_bikeCode);
            TextView time = BaseViewHolder.get(convertView, R.id.item_historyRoad_time);
            HistoryRoadBean bean = getDatas().get(position);

            if(bean.isSelected()){
                select.setImageResource(R.drawable.pay_type_selected);
            }else{
                select.setImageResource(R.drawable.pay_type_normal);
            }

//            if (position == getDatas().size() -1){
//                line.setVisibility(View.GONE);
//            }else {
//                line.setVisibility(View.VISIBLE);
//            }
//            money.setText(bean.getPrices());
//            bikeCode.setText(bean.getCodenum());
            time.setText(bean.getSt_time());

            return convertView;
        }
    }

    public void initmPopupWindowView2(){

        // 获取自定义布局文件的视图
        View customView = getLayoutInflater().inflate(R.layout.pop_pay_menu, null, false);
        // 创建PopupWindow宽度和高度
        RelativeLayout pop_win_bg = (RelativeLayout) customView.findViewById(R.id.pop_win_bg);
        ImageView iv_popup_window_back = (ImageView) customView.findViewById(R.id.popupWindow_back);
        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(getActivity());
        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
            UtilBitmap.blurImageView(context, iv_popup_window_back, 10,0xAA000000);
        } else {
            // 获取的Bitmap为null时，用半透明代替
            iv_popup_window_back.setBackgroundColor(0x77000000);
        }
        // 打开弹窗
        UtilAnim.showToUp(pop_win_bg, iv_popup_window_back);
        // 创建PopupWindow宽度和高度
        final PopupWindow popupwindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, true);
        /**
         * 设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
         */
        popupwindow.setAnimationStyle(R.style.PopupAnimation);
        popupwindow.setOutsideTouchable(false);

        popupwindow.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onRefresh() {
//        showPage = 1;
//        if (!isRefresh) {
//            if(datas.size()!=0){
//                myAdapter.getDatas().clear();
//                myAdapter.notifyDataSetChanged();
//            }
//            isRefresh = true;
//            initHttp();
//        } else {
//            swipeRefreshLayout.setRefreshing(false);
//        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }



//    private void initHttp(){
//
//        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
//        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
//        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
//            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
//            return;
//        }
//        RequestParams params = new RequestParams();
//        params.put("uid",uid);
//        params.put("access_token",access_token);
//        params.put("page", showPage);
//        params.put("pagesize", GlobalConfig.PAGE_SIZE);
//
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
//    }

    @Override
    public void onResume() {
        super.onResume();
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
