package cn.qimate.bike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.qimate.bike.R;
import cn.qimate.bike.fragment.MontCartFragment;
import cn.qimate.bike.fragment.MontCartHistoryFragment;
import cn.qimate.bike.fragment.TimesCartFragment;
import cn.qimate.bike.fragment.TimesCartHistoryFragment;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * 历史明细
 * Created by yuanyi on 2019/3/29.
 */
public class HistoryDetailActivity extends SwipeBackActivity implements View.OnClickListener{
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


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_history_detail);
    context = this;
    init();
  }

  private void init(){

    ll_back = (LinearLayout) findViewById(R.id.ll_back);
    title = (TextView) findViewById(R.id.mainUI_title_titleText);
    title.setText("历史明细");

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

  }

  @Override
  public void onClick(View v) {

    switch (v.getId()) {
      case R.id.ll_back:
        scrollToFinishActivity();
        break;
    }
  }

  class MyPagerAdapter extends FragmentPagerAdapter {
    private String[] titles = new String[]{"月卡", "次卡"};
    private List<Fragment> fragmentList;

    public MyPagerAdapter(FragmentManager fm) {
      super(fm);

      MontCartHistoryFragment montCartHistoryFragment = new MontCartHistoryFragment();
      TimesCartHistoryFragment timesCartHistoryFragment = new TimesCartHistoryFragment();
//      TimesCartFragment timesCartHistoryFragment = new TimesCartFragment();

      fragmentList = new ArrayList<>();
      fragmentList.add(montCartHistoryFragment);
      fragmentList.add(timesCartHistoryFragment);
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
//      UIHelper.goToAct(this, MainActivity.class);
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
