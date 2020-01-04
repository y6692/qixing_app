package cn.qimate.bike.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import cn.qimate.bike.activity.BillActivity;
import cn.qimate.bike.activity.ClientManager;
import cn.qimate.bike.activity.CurRoadBikedActivity;
import cn.qimate.bike.activity.ExchangeActivity;
import cn.qimate.bike.activity.PayCartActivity;
import cn.qimate.bike.activity.RechargeActivity;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.base.BaseFragment;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.model.BannerBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UserBean;
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

            user();
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

        banner();
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e("pf===onResume", "===");

//        user();
    }

    private void user() {
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

                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                            tv_balance.setText(""+bean.getBalance());

                        } catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });
            }
        });

    }

    private void banner() {
        Log.e("pf===banner", "===");

        HttpHelper.get(context, Urls.banner + 4, new TextHttpResponseHandler() {
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

                            for (int i = 0; i < ja_banners.length(); i++) {
                                BannerBean bean = JSON.parseObject(ja_banners.get(i).toString(), BannerBean.class);

                                Log.e("pf===banner2", bean.getImage_url()+"===");

                                imagePath.add(bean.getImage_url());

                                imageTitle.add(bean.getH5_title());
                            }

                            mBanner.setBannerTitles(imageTitle);
                            mBanner.setImages(imagePath).setOnBannerListener(PurseFragment.this).start();

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
        Toast.makeText(context, "你点了第" + (position + 1) + "张轮播图", Toast.LENGTH_SHORT).show();

//        initmPopupWindowView();
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
                UIHelper.goToAct(context, RechargeActivity.class);
                break;

            case R.id.rl_payCart:
                UIHelper.goToAct(context, PayCartActivity.class);
                break;

            case R.id.rl_exchange:
                UIHelper.goToAct(context, ExchangeActivity.class);
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
