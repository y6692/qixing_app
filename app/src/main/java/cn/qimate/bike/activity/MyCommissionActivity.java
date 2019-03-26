package cn.qimate.bike.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.longsh.longshlibrary.PagerSlidingTabStrip;

import butterknife.ButterKnife;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.DensityUtil;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.fragment.MyCommissionFragment;
import cn.qimate.bike.fragment.MyCommissionWithdrawFragment;
import cn.qimate.bike.lock.utils.ToastUtils;
import cn.qimate.bike.model.InviteCodeBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by 123 on 2018/3/14.
 */

public class MyCommissionActivity extends SwipeBackActivity implements View.OnClickListener {

    private LinearLayout ll_back;
    private TextView titleText;
    private TextView moneyText;
    private Button withdrawBtn;
    private LinearLayout headLayout;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;


    private MyCommissionFragment oneFragment;
    private MyCommissionWithdrawFragment twoFragment;

    private Context context;
    private Activity mActivity;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_my_commission);
        ButterKnife.bind(this);
        context = this;
        mActivity = this;
        init();
    }

    private void init() {

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        titleText = (TextView) findViewById(R.id.mainUI_title_titleText);
        moneyText = (TextView) findViewById(R.id.ui_myCommision_moneyText);
        withdrawBtn = (Button) findViewById(R.id.ui_myCommision_withdrawBtn);
        headLayout = (LinearLayout) findViewById(R.id.ui_myCommision_headLayout);
        tabs = (PagerSlidingTabStrip)findViewById(R.id.ui_mainTab_tabs);
        pager = (ViewPager)findViewById(R.id.ui_mainTab_pager) ;

        titleText.setText("我的佣金");

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)headLayout.getLayoutParams();
        params.height = DensityUtil.getWindowWidth(mActivity)*2/5;
        headLayout.setLayoutParams(params);

        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabs.setViewPager(pager);

        setTabsValue();
        initListener();
    }
    private void initListener() {

        ll_back.setOnClickListener(this);
        withdrawBtn.setOnClickListener(this);
    }

    private void setTabsValue() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        // 设置Tab底部选中的指示器Indicator的高度
        tabs.setIndicatorHeight(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, dm));
        // 设置Tab底部选中的指示器 Indicator的颜色
        tabs.setIndicatorColorResource(R.color.ui_main);
        //设置指示器Indicatorin是否跟文本一样宽，默认false
        tabs.setIndicatorinFollowerTv(false);
        //设置小红点提示，item从0开始计算，true为显示，false为隐藏
//        tabs.setMsgToast(2, true);
        //设置红点滑动到当前页面自动消失,默认为true
        tabs.setMsgToastPager(true);
        //设置Tab标题文字的颜色
//        tabs.setTextColor(R.color.tx_black);
        tabs.setTextColor(Color.parseColor("#f57752"));
        // 设置Tab标题文字的大小
        tabs.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, dm));
        // 设置选中的Tab文字的颜色
        tabs.setSelectedTextColorResource(R.color.ui_main);
        //设置Tab底部分割线的高度
        tabs.setUnderlineHeight(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, dm));
        //设置Tab底部分割线的颜色
        tabs.setUnderlineColorResource(R.color.line_color);
        // 设置点击某个Tab时的背景色,设置为0时取消背景色tabs.setTabBackground(0);
//        tabs.setTabBackground(R.drawable.bg_tab);
        tabs.setTabBackground(0);
        // 设置Tab是自动填充满屏幕的
        tabs.setShouldExpand(true);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                scrollToFinishActivity();
                break;
            case R.id.ui_myCommision_withdrawBtn:
                String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
                if (uid != null && !"".equals(uid) && access_token != null && !"".equals(access_token)) {
                    UIHelper.goToAct(context,WithdrawActivity.class);
                }else {
                    ToastUtils.show("请先登录");
                    UIHelper.goToAct(context,LoginActivity.class);
                }
                break;

            default:
                break;
        }
    }
    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"佣金记录", "提现记录"};

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }


        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (oneFragment == null) {
                        oneFragment = new MyCommissionFragment();
                    }
                    return oneFragment;
                case 1:
                    if (twoFragment == null) {
                        twoFragment = new MyCommissionWithdrawFragment();
                    }
                    return twoFragment;

                default:
                    return null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initHttp();
    }
private void initHttp(){

    String uid = SharedPreferencesUrls.getInstance().getString("uid","");
    String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
    if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
        Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
        UIHelper.goToAct(context,LoginActivity.class);
        return;
    }
    RequestParams params = new RequestParams();
    params.put("uid", uid);
    params.put("access_token", access_token);
    HttpHelper.get(context, Urls.inviteCode, params, new TextHttpResponseHandler() {
        @Override
        public void onStart() {
            if (loadingDialog != null && !loadingDialog.isShowing()) {
                loadingDialog.setTitle("正在加载");
                loadingDialog.show();
            }
        }
        @Override
        public void onSuccess(int statusCode,  org.apache.http.Header[] headers, String responseString) {
            com.umeng.socialize.utils.Log.e("Test","RRRR:"+responseString);
            try {
                ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                if (result.getFlag().equals("Success")) {
                    InviteCodeBean bean = JSON.parseObject(result.getData(),InviteCodeBean.class);
                    moneyText.setText(bean.getCommission());
                } else {
                    UIHelper.showToastMsg(context, result.getMsg(), R.drawable.ic_error);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (loadingDialog != null && loadingDialog.isShowing()){
                loadingDialog.dismiss();
            }
        }

        @Override
        public void onFailure(int statusCode,  org.apache.http.Header[] headers, String responseString, Throwable throwable) {
            if (loadingDialog != null && loadingDialog.isShowing()){
                loadingDialog.dismiss();
            }
            UIHelper.ToastError(context, throwable.toString());
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
}
