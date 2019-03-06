package cn.qimate.bike.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import cn.qimate.bike.R;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.fragment.CarCouponFragment;
import cn.qimate.bike.fragment.MerchantCouponFragment;
import cn.qimate.bike.fragment.MyIntegralRuleFragment;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * 我的车位
 * Created by Wikison on 2017/9/16.
 */
public class CouponActivity extends SwipeBackActivity {
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

  private TextView title;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_coupon);
//    ButterKnife.bind(this);
    context = this;
    init();
  }

  private void init(){

    title = (TextView) findViewById(R.id.mainUI_title_titleText);
    title.setText("优惠券");

    tab = (TabLayout) findViewById(R.id.tab);
    vp = (ViewPager)findViewById(R.id.vp);

    myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
    vp.setAdapter(myPagerAdapter);
//    vp.setOffscreenPageLimit(2);
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
  }


  class MyPagerAdapter extends FragmentPagerAdapter {
    private String[] titles = new String[]{"用车券", "商家券"};
    private List<Fragment> fragmentList;

    public MyPagerAdapter(FragmentManager fm) {
      super(fm);

      CarCouponFragment carCouponFragment = new CarCouponFragment();
      MerchantCouponFragment merchantCouponFragment = new MerchantCouponFragment();
//      MyIntegralRuleFragment merchantCouponFragment = new MyIntegralRuleFragment();

      fragmentList = new ArrayList<>();
      fragmentList.add(carCouponFragment);
      fragmentList.add(merchantCouponFragment);
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

  public void btn(View view) {
    int viewId = view.getId();
    if (viewId == R.id.ll_bike) {
      UIHelper.goToAct(this, MainActivity.class);
      scrollToFinishActivity();
    } else if (viewId == R.id.ll_purse) {
      UIHelper.goToAct(this, MyPurseActivity.class);
      scrollToFinishActivity();
    } else if (viewId == R.id.ll_mine) {
      UIHelper.goToAct(this, PersonAlterActivity.class);
      scrollToFinishActivity();
    }
  }


//
//  @OnClick({ R.id.ll_back }) public void Onclick(View v) {
//    switch (v.getId()) {
//      case R.id.ll_back:
//        this.finish();
//        break;
//    }
//  }
}
