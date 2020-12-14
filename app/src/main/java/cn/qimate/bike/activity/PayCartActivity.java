package cn.qimate.bike.activity;

import android.content.Intent;
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
import com.sunshine.blelibrary.config.Config;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import cn.qimate.bike.model.CarBean;
import cn.qimate.bike.model.CarmodelBean;
import cn.qimate.bike.model.H5Bean;
import cn.qimate.bike.model.PayCartBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.TabBean;
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

  LinearLayout ll_tab;


  int carmodel_num;
  String carmodel1;
  String carmodel2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pay_cart);
    context = this;
    init();
  }

  private void init(){
    loadingDialog = new LoadingDialog(context);
    loadingDialog.setCancelable(false);
    loadingDialog.setCanceledOnTouchOutside(false);

    ll_back = (LinearLayout) findViewById(R.id.ll_backBtn);
    title = (TextView) findViewById(R.id.mainUI_title_titleText);

    rightBtn = (TextView)findViewById(R.id.mainUI_title_rightBtn);
    rightBtn.setText("我的");

    iv_question = (ImageView)findViewById(R.id.iv_question);

    ll_tab = (LinearLayout) findViewById(R.id.ll_tab);
    tab = (TabLayout) findViewById(R.id.tab);
    vp = (ViewPager)findViewById(R.id.vp);

    ll_tab.setVisibility(View.VISIBLE);   //TODO
    title.setVisibility(View.GONE);



    ll_back.setOnClickListener(this);
    rightBtn.setOnClickListener(this);
    iv_question.setOnClickListener(this);

    initHttp();

//    onStartCommon("正在加载");

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

  private List<Fragment> fragmentList;
  class MyPagerAdapter extends FragmentPagerAdapter {
    private String[] titles = new String[]{"单车", "助力车"};


    public MyPagerAdapter(FragmentManager fm) {
      super(fm);

      Log.e("MyPagerAdapter===1","===");

      BikeCartFragment bikeCartFragment = new BikeCartFragment();
      EbikeCartFragment ebikeCartFragment = new EbikeCartFragment();

      Log.e("MyPagerAdapter===2","===");

      bikeCartFragment.data = carmodel1;
      ebikeCartFragment.data = carmodel2;

      Log.e("MyPagerAdapter===3","==="+carmodel_array);

      try {
        fragmentList = new ArrayList<>();
        if(carmodel_array.length()==2){
          fragmentList.add(bikeCartFragment);
          fragmentList.add(ebikeCartFragment);
        }else{
          if(carmodel1_array.length()>0){
            fragmentList.add(bikeCartFragment);
          }else if(carmodel2_array.length()>0){
            fragmentList.add(ebikeCartFragment);
          }else{
            fragmentList.add(bikeCartFragment);
          }
        }


//        else if(carmodel_array.length()==1){
//
//            if("单车".equals(carmodel_array.getString(0))){
//              fragmentList.add(bikeCartFragment);
//            }else{
//              fragmentList.add(ebikeCartFragment);
//            }
//
//        }

      } catch (Exception e) {
        e.printStackTrace();
      }

      Log.e("MyPagerAdapter===4","==="+carmodel_num);

//      carmodel_num = 1;

      if(carmodel_num<2){
        ll_tab.setVisibility(View.GONE);   //TODO
        title.setVisibility(View.VISIBLE);
        title.setText("套餐卡");
      }else{
        ll_tab.setVisibility(View.VISIBLE);   //TODO
        title.setVisibility(View.GONE);
      }


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

  JSONArray carmodel_array;
  JSONArray carmodel1_array;
  JSONArray carmodel2_array;
  private void initHttp(){
    String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
    if (access_token == null || "".equals(access_token)){
      Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
      return;
    }
    RequestParams params = new RequestParams();
//    params.put("tab", 1);

    Log.e("cycling_cards===","===");

    HttpHelper.get(context, Urls.cycling_cards, params, new TextHttpResponseHandler() {
      @Override
      public void onStart() {
        onStartCommon("正在加载");
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        onFailureCommon(throwable.toString());
      }

      @Override
      public void onSuccess(int statusCode, Header[] headers, final String responseString) {

        m_myHandler.post(new Runnable() {
          @Override
          public void run() {
            try {
              Log.e("cycling_cards===1","==="+responseString);

              ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
              TabBean obj = JSON.parseObject(result.getData(), TabBean.class);
              Log.e("cycling_cards===2",obj.getTabs()+"==="+obj.getDatas());

              carmodel_array = new JSONArray(obj.getTabs());
              carmodel_num = carmodel_array.length();
              Log.e("cycling_cards===3","==="+carmodel_array.length());

              CarmodelBean obj2 = JSON.parseObject(obj.getDatas(), CarmodelBean.class);
              Log.e("cycling_cards===4",obj2.getCarmodel1()+"==="+obj2.getCarmodel2());

              carmodel1 = obj2.getCarmodel1();
              carmodel2 = obj2.getCarmodel2();

              carmodel1_array = new JSONArray(carmodel1);
              carmodel2_array = new JSONArray(carmodel2);

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







//          ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//
//          JSONArray array = new JSONArray(result.getData());
//
//          if (array.length() == 0) {
//            footerViewType05.setVisibility(View.VISIBLE);
//          }else{
//            footerViewType05.setVisibility(View.GONE);
//          }
//
//          for (int i = 0; i < array.length();i++){
//            PayCartBean bean = JSON.parseObject(array.getJSONObject(i).toString(), PayCartBean.class);
//
//            datas.add(bean);
//          }
//
//          myAdapter.notifyDataSetChanged();

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
