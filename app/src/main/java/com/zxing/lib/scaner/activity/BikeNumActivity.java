package com.zxing.lib.scaner.activity;

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
import cn.qimate.bike.activity.MainActivity;
import cn.qimate.bike.activity.MyPurseActivity;
import cn.qimate.bike.activity.PersonAlterActivity;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.fragment.CarCouponFragment;
import cn.qimate.bike.fragment.MerchantCouponFragment;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * 输入编号开锁
 * Created by yuanyi on 2019/3/13.
 */
public class BikeNumActivity extends SwipeBackActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bike_num);
    context = this;
    init();
  }

  private void init(){

//    title = (TextView) findViewById(R.id.mainUI_title_titleText);
//    title.setText("优惠券");


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
