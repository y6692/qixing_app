package cn.qimate.bike.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.fragment.BikeCartFragment;
import cn.qimate.bike.fragment.EbikeCartFragment;
import cn.qimate.bike.fragment.MyBikeCartFragment;
import cn.qimate.bike.fragment.MyEbikeCartFragment;
import cn.qimate.bike.model.CarmodelBean;
import cn.qimate.bike.model.GlobalConfig;
import cn.qimate.bike.model.MyCartBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.TabBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * 我的车位
 * Created by Wikison on 2017/9/16.
 */
public class MyCartActivity extends SwipeBackActivity implements View.OnClickListener{
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

  String carmodel1;
  String carmodel2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my_cart);
    context = this;
    init();
  }

  private void init(){
    loadingDialog = new LoadingDialog(context);
    loadingDialog.setCancelable(false);
    loadingDialog.setCanceledOnTouchOutside(false);

    ll_back = (LinearLayout) findViewById(R.id.ll_backBtn);
    title = (TextView) findViewById(R.id.mainUI_title_titleText);
    title.setText("我的套餐卡");
//    title.setTextColor(0xEA5359);
//    rightBtn = (TextView)findViewById(R.id.mainUI_title_rightBtn);
//    rightBtn.setText("我的套餐卡");

    tab = (TabLayout) findViewById(R.id.tab);
    vp = (ViewPager)findViewById(R.id.vp);



    ll_back.setOnClickListener(this);
//    rightBtn.setOnClickListener(this);

    initHttp();
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
    private String[] titles = new String[]{"单车", "助力车"};
    private List<Fragment> fragmentList;

    public MyPagerAdapter(FragmentManager fm) {
      super(fm);

      MyBikeCartFragment myBikeCartFragment = new MyBikeCartFragment();
      MyEbikeCartFragment myEbikeCartFragment = new MyEbikeCartFragment();

      myBikeCartFragment.data = carmodel1;
      myEbikeCartFragment.data = carmodel2;

      fragmentList = new ArrayList<>();
      fragmentList.add(myBikeCartFragment);
      fragmentList.add(myEbikeCartFragment);
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

  private void initHttp(){
    String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
    if (access_token == null || "".equals(access_token)){
      Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
      return;
    }
    RequestParams params = new RequestParams();
//    params.put("type", 1);
//    params.put("page", showPage);
//    params.put("per_page", GlobalConfig.PAGE_SIZE);

    HttpHelper.get(context, Urls.my_cycling_cards, params, new TextHttpResponseHandler() {
      @Override
      public void onStart() {
//        m_myHandler.post(new Runnable() {
//          @Override
//          public void run() {
//            setFooterType(1);
//          }
//        });
        onStartCommon("正在加载");
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, String responseString, final Throwable throwable) {
        onFailureCommon(throwable.toString());

//        m_myHandler.post(new Runnable() {
//          @Override
//          public void run() {
//            UIHelper.ToastError(context, throwable.toString());
//            swipeRefreshLayout.setRefreshing(false);
//            isRefresh = false;
//            setFooterType(3);
//            setFooterVisibility();
//          }
//        });

      }

      @Override
      public void onSuccess(int statusCode, Header[] headers, final String responseString) {
        m_myHandler.post(new Runnable() {
          @Override
          public void run() {
            try {
              Log.e("my_cycling_cards===1","==="+responseString);

              ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//              TabBean obj = JSON.parseObject(result.getData(), TabBean.class);
//              Log.e("my_cycling_cards===2",obj.getTabs()+"==="+obj.getDatas());
//
//              carmodel_array = new JSONArray(obj.getTabs());
//              carmodel_num = carmodel_array.length();
//              Log.e("my_cycling_cards===3","==="+carmodel_array.length());
//
              CarmodelBean obj2 = JSON.parseObject(result.getData(), CarmodelBean.class);
              Log.e("my_cycling_cards===4",obj2.getCarmodel1()+"==="+obj2.getCarmodel2());

              carmodel1 = obj2.getCarmodel1();
              carmodel2 = obj2.getCarmodel2();

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


            } catch (Exception e) {
              e.printStackTrace();
            } finally {
            }
            if (loadingDialog != null && loadingDialog.isShowing()){
              loadingDialog.dismiss();
            }
          }
        });

      }
    });
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
