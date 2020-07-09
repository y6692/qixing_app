package cn.qimate.bike.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.qimate.bike.R;
import cn.qimate.bike.fragment.BikeCartFragment;
import cn.qimate.bike.fragment.EbikeCartFragment;
import cn.qimate.bike.fragment.WeekFragment;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;


/**
 * 调运详情
 * Created by Wikison on 2017/9/16.
 */
public class RankingListActivity extends SwipeBackActivity implements View.OnClickListener{
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

  private String sn;
  private String status;
  private String start_time;
  private String end_time;

  private String[] titles = new String[]{"本周", "本月", "本季"};
  int[] tabIcons = {R.drawable.tab_week_bcg, R.drawable.tab_month_bcg, R.drawable.tab_quarter_bcg};

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ranking_list);
    context = this;

    sn = getIntent().getStringExtra("sn");
    status = getIntent().getStringExtra("status");
    start_time = getIntent().getStringExtra("start_time");
    end_time = getIntent().getStringExtra("end_time");

    init();
  }

  private void init(){

    ll_back = (LinearLayout) findViewById(R.id.ll_backBtn);
//    title = (TextView) findViewById(R.id.mainUI_title_titleText);
//    title.setText("调运详情");
//    rightBtn = (TextView)findViewById(R.id.mainUI_title_rightBtn);
//    rightBtn.setText("我的套餐卡");

    tab = (TabLayout) findViewById(R.id.tab);
    vp = (ViewPager)findViewById(R.id.vp);

    myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
    vp.setAdapter(myPagerAdapter);
    tab.setupWithViewPager(vp);
    setupTabIcons();
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
//    rightBtn.setOnClickListener(this);

//    TextView tv_sn = (TextView)findViewById(R.id.dispatch_drtail_sn);
//    TextView tv_status = (TextView)findViewById(R.id.dispatch_drtail_status);
//    TextView tv_created_at = (TextView)findViewById(R.id.dispatch_drtail_created_at);
//    TextView tv_end_at = (TextView)findViewById(R.id.dispatch_drtail_end_at);
//    TextView tv_car_numbers = (TextView)findViewById(R.id.item_car_numbers);
//    Button btn_dispatch_detail = BaseViewHolder.get(convertView,R.id.btn_dispatch_detail);
//    Button btn_confirm_finish = BaseViewHolder.get(convertView,R.id.btn_confirm_finish);
//
//    tv_sn.setText("调运单号："+sn);
//    if("0".equals(status)){
//      tv_status.setText("调运中");
//    }else{
//      tv_status.setText("已完成");
//    }
//
//    tv_created_at.setText("开始时间："+start_time);
//    if(end_time==null || "".equals(end_time)){
//      tv_end_at.setVisibility(View.GONE);
//    }else{
//      tv_end_at.setVisibility(View.VISIBLE);
//      tv_end_at.setText("结束时间："+end_time);
//    }


//    int[] tabIcons = {R.drawable.tab_week_bcg, R.drawable.month_icon, R.drawable.quarter_icon};
//
//    for (int i = 0; i < 3; i++) {
//      tab.getTabAt(i).setIcon(tabIcons[i]);
//      tab.getTabAt(i).setText("m");
////      tab.getTabAt(i).setContentDescription("a");
//    }
  }

  private void setupTabIcons() {
    tab.getTabAt(0).setCustomView(getTabView(0));
    tab.getTabAt(1).setCustomView(getTabView(1));
    tab.getTabAt(2).setCustomView(getTabView(2));
  }


  public View getTabView(int position) {
    View view = LayoutInflater.from(this).inflate(R.layout.item_ranking_tab, null);
    TextView txt_title = (TextView) view.findViewById(R.id.txt_title);
    txt_title.setText(titles[position]);
    ImageView img_title = (ImageView) view.findViewById(R.id.img_title);
    img_title.setImageResource(tabIcons[position]);
    return view;
  }

  @Override
  public void onClick(View v) {

    switch (v.getId()) {
      case R.id.ll_backBtn:
        scrollToFinishActivity();
        break;
      case R.id.mainUI_title_rightBtn:
//        UIHelper.goToAct(context, HistoryDetailActivity.class);
        break;
    }
  }

  class MyPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList;

    public MyPagerAdapter(FragmentManager fm) {
      super(fm);

      WeekFragment weekFragment = new WeekFragment();
      BikeCartFragment monthFragment = new BikeCartFragment();
      EbikeCartFragment quarterFragment = new EbikeCartFragment();

      fragmentList = new ArrayList<>();
      fragmentList.add(weekFragment);
      fragmentList.add(monthFragment);
      fragmentList.add(quarterFragment);


    }

    @Override
    public Fragment getItem(int position) {
      Log.e("getItem===", "==="+position);
      return fragmentList.get(position);
    }

    @Override
    public int getCount() {
      Log.e("getCount===", "==="+fragmentList.size());
      return fragmentList.size();
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//
//      Log.e("getPageTitle===", "==="+position);
//
//      Drawable drawable = null;
//      String title=null;
//      switch (position) {
//        case 0:
//          drawable = ContextCompat.getDrawable(context, R.drawable.tab_week_bcg);
//          title = "a";
//          break;
//        case 1:
//          drawable = ContextCompat.getDrawable(context, R.drawable.month_icon);
//          title = "b";
//          break;
//        case 2:
//          drawable = ContextCompat.getDrawable(context, R.drawable.quarter_icon);
//          title = "c";
//          break;
//        default:
//          break;
//      }
//      drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//
//      ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
//      SpannableString spannableString = new SpannableString("   " + title);
//      spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//      return spannableString;
//
//
//
//
////      return titles[position];
//    }
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
