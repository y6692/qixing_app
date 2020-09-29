package cn.qimate.bike.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.fragment.BikeCartFragment;
import cn.qimate.bike.fragment.EbikeCartFragment;
import cn.qimate.bike.model.H5Bean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * 我的车位
 * Created by Wikison on 2017/9/16.
 */
public class PayCartActivity extends SwipeBackActivity implements View.OnClickListener{
  public static final String INTENT_INDEX = "INTENT_INDEX";
//  @BindView(R.id.ll_back) LinearLayout llBack;
//  @BindView(R.id.lh_tv_title) TextView lhTvTitle;
//  @BindView(R.id.tab) TabLayout tab;
//  @BindView(R.id.vp) ViewPager vp;

    TabLayout tab;
    ViewPager vp;

//  private PrivateLockFragment privateLockFragment;
//  private RentLockFragment rentLockFragment;
  private MyPagerAdapter myPagerAdapter;

  private LinearLayout ll_back;
  private TextView title;
  private TextView rightBtn;
  private ImageView iv_question;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pay_cart);
    context = this;
    init();
  }

  private void init(){

    ll_back = (LinearLayout) findViewById(R.id.ll_backBtn);
//    title = (TextView) findViewById(R.id.mainUI_title_titleText);
//    title.setText("购买骑行套餐");
    rightBtn = (TextView)findViewById(R.id.mainUI_title_rightBtn);
    rightBtn.setText("我的套餐卡");

    iv_question = (ImageView)findViewById(R.id.iv_question);

    tab = (TabLayout) findViewById(R.id.tab);
    vp = (ViewPager)findViewById(R.id.vp);

    myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
    vp.setAdapter(myPagerAdapter);
    tab.setupWithViewPager(vp);

    vp.setCurrentItem(0);

    vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override public void onPageSelected(int position) {
            vp.setCurrentItem(position);
        }

        @Override public void onPageScrollStateChanged(int state) {

        }
    });

    ll_back.setOnClickListener(this);
    rightBtn.setOnClickListener(this);
    iv_question.setOnClickListener(this);

  }

  @Override
  public void onClick(View v) {

    switch (v.getId()) {
      case R.id.ll_backBtn:
        scrollToFinishActivity();
        break;

      case R.id.iv_question:
        agreement();
        break;

      case R.id.mainUI_title_rightBtn:
        UIHelper.goToAct(context, MyCartActivity.class);
        break;
    }
  }

  private void agreement() {

    Log.e("agreement===0", "===");

    try{
//    协议名 register注册协议 recharge充值协议 cycling_card骑行卡协议 insurance保险协议
      HttpHelper.get(context, Urls.agreement+"cycling_card", new TextHttpResponseHandler() {
        @Override
        public void onStart() {
          if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.setTitle("请稍等");
            loadingDialog.show();
          }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//          Toast.makeText(context, "fail=="+responseString, Toast.LENGTH_LONG).show();

          Log.e("agreement===fail", throwable.toString()+"==="+responseString);

          if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
          }
          UIHelper.ToastError(context, throwable.toString());
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
          try {

//                        Toast.makeText(context, "=="+responseString, Toast.LENGTH_LONG).show();

            Log.e("agreement===", "==="+responseString);

            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

            H5Bean bean = JSON.parseObject(result.getData(), H5Bean.class);

            UIHelper.goWebViewAct(context, bean.getH5_title(), bean.getH5_url());
//                        UIHelper.goWebViewAct(context, bean.getH5_title(), Urls.agreement+"register");



          } catch (Exception e) {
            e.printStackTrace();
          } finally {
            if (loadingDialog != null && loadingDialog.isShowing()) {
              loadingDialog.dismiss();
            }
          }
        }


      });
    }catch (Exception e){
      Toast.makeText(context, "==="+e, Toast.LENGTH_SHORT).show();
    }

  }

  class MyPagerAdapter extends FragmentPagerAdapter {
    private String[] titles = new String[]{"单车套餐卡", "助力车套餐卡"};
    private List<Fragment> fragmentList;

    public MyPagerAdapter(FragmentManager fm) {
      super(fm);

      BikeCartFragment bikeCartFragment = new BikeCartFragment();
      EbikeCartFragment ebikeCartFragment = new EbikeCartFragment();

      fragmentList = new ArrayList<>();
      fragmentList.add(bikeCartFragment);
      fragmentList.add(ebikeCartFragment);
    }

    @Override
    public Fragment getItem(int position) {
      return fragmentList.get(position);
    }

    @Override
    public int getCount() {
      return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

      return titles[position];
    }
  }

//  public void btn(View view) {
//    int viewId = view.getId();
//    if (viewId == R.id.ll_bike) {
//      UIHelper.goToAct(this, MainFragment.class);
//      scrollToFinishActivity();
//    } else if (viewId == R.id.ll_purse) {
//      UIHelper.goToAct(this, MyPurseActivity.class);
//      scrollToFinishActivity();
//    } else if (viewId == R.id.ll_mine) {
//      UIHelper.goToAct(this, PersonAlterActivity.class);
//      scrollToFinishActivity();
//    }
//  }

}
