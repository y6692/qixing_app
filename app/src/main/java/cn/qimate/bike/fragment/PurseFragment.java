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
import cn.qimate.bike.activity.BalanceActivity;
import cn.qimate.bike.activity.CurRoadBikedActivity;
import cn.qimate.bike.activity.DensityFreeManageActivity;
import cn.qimate.bike.activity.MainActivity;
import cn.qimate.bike.activity.PayCartActivity;
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
public class PurseFragment extends BaseFragment implements View.OnClickListener{

    private View v;
    Unbinder unbinder;

    private Context context;

    private TextView title;

    private RelativeLayout rl_payCart;
    private RelativeLayout rl_payCart2;
    private RelativeLayout rl_balance;
    private RelativeLayout rl_coupon;
    private RelativeLayout rl_densityFreeManage;


    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_purse, null);
        unbinder = ButterKnife.bind(this, v);

        return v;
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();

        initView();
//        initHttp();
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
//        title = (TextView) getActivity().findViewById(R.id.mainUI_title_titleText);
//        title.setText("我的钱包");

        rl_payCart = (RelativeLayout) getActivity().findViewById(R.id.rl_payCart);
        rl_payCart2 = (RelativeLayout) getActivity().findViewById(R.id.rl_payCart2);
        rl_balance = (RelativeLayout) getActivity().findViewById(R.id.rl_balance);
        rl_coupon = (RelativeLayout) getActivity().findViewById(R.id.rl_coupon);
        rl_densityFreeManage = (RelativeLayout) getActivity().findViewById(R.id.rl_densityFreeManage);

        rl_payCart.setOnClickListener(this);
        rl_payCart2.setOnClickListener(this);
        rl_balance.setOnClickListener(this);
        rl_coupon.setOnClickListener(this);
        rl_densityFreeManage.setOnClickListener(this);

//        loadingDialog = new LoadingDialog(context);
//        loadingDialog.setCancelable(false);
//        loadingDialog.setCanceledOnTouchOutside(false);
//
//        footerView = LayoutInflater.from(context).inflate(R.layout.footer_item, null);
//        footerViewType01 = footerView.findViewById(R.id.footer_Layout_type01);// 点击加载更多
//        footerViewType02 = footerView.findViewById(R.id.footer_Layout_type02);// 正在加载，请您稍等
//        footerViewType03 = footerView.findViewById(R.id.footer_Layout_type03);// 已无更多
//        footerViewType04 = footerView.findViewById(R.id.footer_Layout_type04);// 刷新失败，请重试
//        footerViewType05 = footerView.findViewById(R.id.footer_Layout_type05);// 暂无数据
//        footerLayout = footerView.findViewById(R.id.footer_Layout);

    }

    @Override
    public void onClick(View v) {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (v.getId()){

            case R.id.rl_payCart:
            case R.id.rl_payCart2:
                UIHelper.goToAct(context, PayCartActivity.class);
//                scrollToFinishActivity();
                break;

            case R.id.rl_balance:
                UIHelper.goToAct(context, BalanceActivity.class);
                break;

            case R.id.rl_coupon:
//                ((MainActivity)getActivity()).changeTab(1);
                UIHelper.goToAct(context,CurRoadBikedActivity.class);

                break;

            case R.id.rl_densityFreeManage:
                UIHelper.goToAct(context, DensityFreeManageActivity.class);
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
//        params.put("page", showPage);
//        params.put("pagesize", GlobalConfig.PAGE_SIZE);

        HttpHelper.get(context, Urls.badcarList, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                UIHelper.ToastError(context, throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        JSONArray array = new JSONArray(result.getData());


                    } else {
                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }





    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}
