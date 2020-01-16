package cn.qimate.bike.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.AuthCenterActivity;
import cn.qimate.bike.activity.BillActivity;
import cn.qimate.bike.activity.CarFaultProActivity;
import cn.qimate.bike.activity.ChangePhoneActivity;
import cn.qimate.bike.activity.ClientManager;
import cn.qimate.bike.activity.CurRoadBikedActivity;
import cn.qimate.bike.activity.DepositFreeAuthActivity;
import cn.qimate.bike.activity.ExchangeActivity;
import cn.qimate.bike.activity.LoginActivity;
import cn.qimate.bike.activity.MainActivity;
import cn.qimate.bike.activity.MyCartActivity;
import cn.qimate.bike.activity.MyMessageActivity;
import cn.qimate.bike.activity.MyOrderActivity;
import cn.qimate.bike.activity.PayCartActivity;
import cn.qimate.bike.activity.RealNameAuthActivity;
import cn.qimate.bike.activity.RechargeActivity;
import cn.qimate.bike.activity.ServiceCenterActivity;
import cn.qimate.bike.activity.SettingActivity;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.base.BaseFragment;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.model.BannerBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UserBean;
import cn.qimate.bike.util.ToastUtil;
import cn.qimate.bike.view.RoundImageView;

@SuppressLint("NewApi")
public class PurseFragment extends BaseFragment implements View.OnClickListener, OnBannerListener {

    private View v;
    Unbinder unbinder;

    private Context context;
    private Activity activity;

    private TextView tv_balance, tv_recharge;

    private RelativeLayout rl_payCart;
    private RelativeLayout rl_exchange;
    private RelativeLayout rl_bill;

    private LinearLayout rl_ad;

    private Banner mBanner;
    private MyImageLoader mMyImageLoader;
    private ArrayList<String> imagePath;
    private ArrayList<String> imageTitle;
    private ArrayList<String> urlPath;
    private ArrayList<String> typeList;

    private int cert1_status;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_purse, null);
        unbinder = ButterKnife.bind(this, v);

        return v;
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        activity = getActivity();

        initView();
//        initHttp();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        Log.e("pf===onHiddenChanged", "==="+hidden);

        if(hidden){
            //pause
        }else{
            //resume

            String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
            if("".equals(access_token)){
                ToastUtil.showMessageApp(context, "请先登录");

                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }else{
                banner();
                user();
            }

        }
    }


    private void initView(){
//        title = (TextView) getActivity().findViewById(R.id.mainUI_title_titleText);
//        title.setText("我的钱包");

        tv_balance = getActivity().findViewById(R.id.tv_balance);
        tv_recharge = getActivity().findViewById(R.id.tv_recharge);
        rl_payCart = getActivity().findViewById(R.id.rl_payCart);
        rl_exchange= getActivity().findViewById(R.id.rl_exchange);
        rl_bill = getActivity().findViewById(R.id.rl_bill);

        tv_recharge.setOnClickListener(this);
        rl_payCart.setOnClickListener(this);
        rl_exchange.setOnClickListener(this);
        rl_bill.setOnClickListener(this);

//        loadingDialog = new LoadingDialog(context);
//        loadingDialog.setCancelable(false);
//        loadingDialog.setCanceledOnTouchOutside(false);
//        footerView = LayoutInflater.from(context).inflate(R.layout.footer_item, null);
//        footerViewType01 = footerView.findViewById(R.id.footer_Layout_type01);// 点击加载更多
//        footerViewType02 = footerView.findViewById(R.id.footer_Layout_type02);// 正在加载，请您稍等
//        footerViewType03 = footerView.findViewById(R.id.footer_Layout_type03);// 已无更多
//        footerViewType04 = footerView.findViewById(R.id.footer_Layout_type04);// 刷新失败，请重试
//        footerViewType05 = footerView.findViewById(R.id.footer_Layout_type05);// 暂无数据
//        footerLayout = footerView.findViewById(R.id.footer_Layout);

        imagePath = new ArrayList<>();
        imageTitle = new ArrayList<>();
        urlPath = new ArrayList<>();
        typeList = new ArrayList<>();

        mMyImageLoader = new MyImageLoader();
        mBanner = activity.findViewById(R.id.purse_banner);
        //设置样式，里面有很多种样式可以自己都看看效果
        mBanner.setBannerStyle(0);
        //设置图片加载器
        mBanner.setImageLoader(mMyImageLoader);
        //设置轮播的动画效果,里面有很多种特效,可以都看看效果。
        mBanner.setBannerAnimation(Transformer.ZoomOutSlide);
        //轮播图片的文字
//      mBanner.setBannerTitles(imageTitle);
        //设置轮播间隔时间
        mBanner.setDelayTime(3000);
        //设置是否为自动轮播，默认是true
        mBanner.isAutoPlay(true);
        //设置指示器的位置，小点点，居中显示
        mBanner.setIndicatorGravity(BannerConfig.CENTER);

        rl_ad = activity.findViewById(R.id.rl_purse_ad);
        rl_ad.setOnClickListener(this);


    }


    @Override
    public void onResume() {
        super.onResume();

        activity = getActivity();
        boolean flag = activity.getIntent().getBooleanExtra("flag", false);
//        SharedPreferencesUrls.getInstance().getString("access_token", "");
        Log.e("pf===onResume", flag+"==="+SharedPreferencesUrls.getInstance().getString("access_token", "")+"==="+type);

        if(flag){
            user();
        }


    }

    public void user() {
        Log.e("pf===user", "==="+isHidden());

        if(isHidden()) return;

        HttpHelper.get(context, Urls.user, new TextHttpResponseHandler() {
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

                            cert1_status = bean.getCert1_status();

                            tv_balance.setText(""+bean.getBalance());

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

    private void banner() {
        Log.e("pf===banner", "===");

        HttpHelper.get2(context, Urls.banner + 4, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("pf===banner=fail", "===" + throwable.toString());
                onFailureCommon("pf===banner", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e("pf===banner0", responseString + "===");

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            JSONArray ja_banners = new JSONArray(new JSONObject(result.getData()).getString("banners"));

                            Log.e("pf===banner1", ja_banners.length() + "===" + result.data);

                            if(ja_banners.length()==0){
                                rl_ad.setVisibility(View.GONE);
                            }else{
                                rl_ad.setVisibility(View.VISIBLE);

                                if(imagePath.size()>0){
                                    imagePath.clear();
                                    imageTitle.clear();
                                    urlPath.clear();
                                    typeList.clear();
                                }

                                for (int i = 0; i < ja_banners.length(); i++) {
                                    BannerBean bean = JSON.parseObject(ja_banners.get(i).toString(), BannerBean.class);

                                    Log.e("pf===banner2", bean.getImage_url()+"===");

                                    imagePath.add(bean.getImage_url());
                                    imageTitle.add(bean.getH5_title());

                                    String action_content = bean.getAction_content();
                                    String action_type = bean.getAction_type();
                                    if("h5".equals(action_type)){
                                        if(action_content.contains("?")){
                                            action_content+="&token="+access_token.split(" ")[1];
                                        }else{
                                            action_content+="?token="+access_token.split(" ")[1];
                                        }
                                    }
                                    urlPath.add(action_content);
                                    typeList.add(action_type);
                                }

                                mBanner.setBannerTitles(imageTitle);
                                mBanner.setImages(imagePath).setOnBannerListener(PurseFragment.this).start();
                            }


                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());

                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                        }

                    }
                });
            }
        });
    }

    protected Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {

                default:
                    break;
            }
            return false;
        }
    });

    @Override
    public void OnBannerClick(int position) {
//        Toast.makeText(context, "你点了第" + (position + 1) + "张轮播图", Toast.LENGTH_SHORT).show();

        Log.e("pf===OnBannerClick", imageTitle.get(position)+"==="+urlPath.get(position)+"==="+typeList.get(position)+"==="+("car_bad".equals(urlPath.get(position))));


        bannerTz(imageTitle.get(position), typeList.get(position), urlPath.get(position));
//        initmPopupWindowView();
    }

    private void bannerTz(String title, String type, String url) {
        if("app".equals(type)){
            if("home".equals(url)){
                ((MainActivity)activity).changeTab(0);
            }else if("wallet".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    ((MainActivity)activity).changeTab(1);
                }
            }else if("member".equals(url)){
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if("".equals(access_token)){
                    ToastUtil.showMessageApp(context, "请先登录");

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else{
                    ((MainActivity)activity).changeTab(2);
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
                    UIHelper.goToAct(context, RealNameAuthActivity.class);
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
            }
        }else if("h5".equals(type)){
            UIHelper.goWebViewAct(context, title, url);
        }
    }

    private void user(final String url) {
        Log.e("pf===user", "==="+isHidden());

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


    private class MyImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context.getApplicationContext()).load(path).into(imageView);
        }

        @Override
        public ImageView createImageView(Context context) {
            RoundImageView img = new RoundImageView(context);
            return img;
        }
    }

    @Override
    public void onClick(View v) {
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (v.getId()){

            case R.id.tv_recharge:
//                UIHelper.goToAct(context, RechargeActivity.class);

                Intent  intent = new Intent(context, RechargeActivity.class);
                intent.putExtra("isRemain", false);
                startActivity(intent);

                break;

            case R.id.rl_payCart:
                if(cert1_status!=3){
                    ToastUtil.showMessageApp(context, "请先进行免押金认证");
                }else{
                    UIHelper.goToAct(context, PayCartActivity.class);
                }

                break;

            case R.id.rl_exchange:
                if(cert1_status!=3){
                    ToastUtil.showMessageApp(context, "请先进行免押金认证");
                }else{
                    UIHelper.goToAct(context, ExchangeActivity.class);
                }

                break;

            case R.id.rl_bill:
                UIHelper.goToAct(context, BillActivity.class);
                break;

            default:
                break;
        }
    }


    private void initHttp(){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
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
