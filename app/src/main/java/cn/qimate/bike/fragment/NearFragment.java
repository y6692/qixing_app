package cn.qimate.bike.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.CouponActivity;
import cn.qimate.bike.activity.PayMontCartActivity;
import cn.qimate.bike.base.BaseFragment;
import cn.qimate.bike.base.BaseViewAdapter;
import cn.qimate.bike.base.BaseViewHolder;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.BadCarBean;
import cn.qimate.bike.model.GlobalConfig;
import cn.qimate.bike.model.ResultConsel;

import static android.app.Activity.RESULT_OK;

@SuppressLint("NewApi")
public class NearFragment extends BaseFragment{

    private View v;
    Unbinder unbinder;

    public static final String INTENT_INDEX = "INTENT_INDEX";

    TabLayout tab;
    ViewPager vp;

    private MyPagerAdapter myPagerAdapter;

    private TextView title;

    private Context context;



    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_coupon, null);
        unbinder = ButterKnife.bind(this, v);

        return v;
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();

        initView();
    }



    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            //pause
        }else{
            //resume
        }
    }


    private void initView(){
        title = (TextView) getActivity().findViewById(R.id.mainUI_title_titleText);
        title.setText("优惠券");

        tab = (TabLayout) getActivity().findViewById(R.id.tab);
        vp = (ViewPager)getActivity().findViewById(R.id.vp);

        myPagerAdapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
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





    @Override
    public void onResume() {
        super.onResume();
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
