package cn.qimate.bike.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.sofi.blelocker.library.connect.listener.BluetoothStateListener;
import com.sofi.blelocker.library.search.SearchRequest;
import com.xiaoantech.sdk.XiaoanBleApiClient;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseFragmentActivity;
import cn.qimate.bike.core.common.AppManager;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.UpdateManager;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.fragment.BikeCartFragment;
import cn.qimate.bike.fragment.BikeFaultFragment;
import cn.qimate.bike.fragment.EbikeCartFragment;
import cn.qimate.bike.fragment.EbikeFaultFragment;
import cn.qimate.bike.fragment.MainFragment;
import cn.qimate.bike.fragment.MineFragment;
import cn.qimate.bike.fragment.PurseFragment;
import cn.qimate.bike.model.BannerBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.TabEntity;
import cn.qimate.bike.model.TabTopEntity;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.ToastUtil;
import cn.qimate.bike.view.RoundImageView;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

@SuppressLint("NewApi")
public class CarFaultActivity extends SwipeBackActivity implements View.OnClickListener {

    CommonTabLayout tab;


    private LinearLayout ll_back;

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private String[] mTitles = { "单车", "助力车"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_fault);
        context = this;
        init();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void init(){

        ll_back = (LinearLayout) findViewById(R.id.ll_backBtn);

        tab = (CommonTabLayout) findViewById(R.id.tab);

        BikeFaultFragment bikeFaultFragment = new BikeFaultFragment();
        EbikeFaultFragment ebikeFaultFragment = new EbikeFaultFragment();
        mFragments.add(bikeFaultFragment);
        mFragments.add(ebikeFaultFragment);

        Log.e("cfa===initData", "===");

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabTopEntity(mTitles[i]));
        }

        tab = (CommonTabLayout) findViewById(R.id.tab);

        tab.setTabData(mTabEntities, this, R.id.fl_carFault, mFragments);


        ll_back.setOnClickListener(this);
//        rightBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_backBtn:
                scrollToFinishActivity();
                break;
//            case R.id.mainUI_title_rightBtn:
//                UIHelper.goToAct(context, MyCartActivity.class);
//                break;
        }
    }



}