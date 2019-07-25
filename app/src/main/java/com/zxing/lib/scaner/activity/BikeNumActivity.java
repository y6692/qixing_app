package com.zxing.lib.scaner.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.qimate.bike.R;
import cn.qimate.bike.activity.MainActivity;
import cn.qimate.bike.activity.MyPurseActivity;
import cn.qimate.bike.activity.PersonAlterActivity;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.widget.ClearEditText;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.fragment.CarCouponFragment;
import cn.qimate.bike.fragment.MerchantCouponFragment;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.ToastUtil;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * 输入编号开锁
 * Created by yuanyi on 2019/3/13.
 */
public class BikeNumActivity extends SwipeBackActivity implements View.OnClickListener{
  private LinearLayout ll_back;
  private TextView title;

  private ClearEditText bikeNumEdit;
  private LinearLayout scanCodeLock;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bike_num);
    context = this;
    init();
  }

  private void init(){

    ll_back = (LinearLayout) findViewById(R.id.ll_back);
    title = (TextView) findViewById(R.id.mainUI_title_titleText);
    title.setText("输入编号开锁");

    bikeNumEdit = (ClearEditText) findViewById(R.id.et_bike_num);
    scanCodeLock = (LinearLayout) findViewById(R.id.mainUI_scanCode_lock);

    ll_back.setOnClickListener(this);
    scanCodeLock.setOnClickListener(this);
  }

  @Override
  public void onClick(View v){
    InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    switch (v.getId()){
      case R.id.ll_back:
        scrollToFinishActivity();
        break;
      case R.id.mainUI_scanCode_lock:
        String bikeNum = bikeNumEdit.getText().toString().trim();

        Log.e("bikeNum===", "==="+bikeNum);

        if (bikeNum == null || "".equals(bikeNum)) {

          ToastUtil.showMessageApp(context, "请输入车牌号");
          return;
        }
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(v.getWindowToken(), 0);

        Intent intent = new Intent();
        intent.putExtra("bikeNum", bikeNum);
        setResult(RESULT_OK, intent);
        scrollToFinishActivity();

        break;
      case R.id.mainUI_title_rightBtn:
        break;

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
