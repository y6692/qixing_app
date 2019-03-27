package cn.qimate.bike.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ListView;
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
import cn.qimate.bike.activity.PayMontCartActivity;
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
import cn.qimate.bike.model.ResultConsel;

import static android.app.Activity.RESULT_OK;

@SuppressLint("NewApi")
public class MontCartFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener{
    private View v;
    Unbinder unbinder;

    private Context context;



    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_mont_cart, null);
        unbinder = ButterKnife.bind(this, v);

        return v;
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();

//        initView();
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
    public void onClick(View v) {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (v.getId()){

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
