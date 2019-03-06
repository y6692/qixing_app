package cn.qimate.bike.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseFragmentActivity;
import cn.qimate.bike.fragment.BikeFragment;
import cn.qimate.bike.fragment.MineFragment;
import cn.qimate.bike.fragment.NearFragment;
import cn.qimate.bike.fragment.PurseFragment;
import cn.qimate.bike.model.TabEntity;

@SuppressLint("NewApi")
public class MainActivity extends BaseFragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String INTENT_MSG_COUNT = "INTENT_MSG_COUNT";
    public final static String MESSAGE_RECEIVED_ACTION = "io.yunba.example.msg_received_action";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

//    @BindView(R.id.fl_change) FrameLayout flChange;
//    @BindView(R.id.tab)
    CommonTabLayout tab;
//    @BindView(R.id.ll_tab) LinearLayout llTab;

    private Context mContext;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private String[] mTitles = { "用车", "周边", "钱包", "我的" };
    private int[] mIconUnselectIds = {
            R.drawable.bike, R.drawable.near, R.drawable.purse, R.drawable.mine
    };
    private int[] mIconSelectIds = {
            R.drawable.bike2, R.drawable.near2, R.drawable.purse2, R.drawable.mine2
    };
    private BikeFragment bikeFragment;
    private NearFragment nearFragment;
    private PurseFragment purseFragment;
    private MineFragment mineFragment;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ui_main);
        ButterKnife.bind(this);

        IntentFilter filter = new IntentFilter("data.broadcast.action");
        registerReceiver(mReceiver, filter);

        initData();
        initView();
        initListener();
//        initLocation();
//        AppApplication.getApp().scan();
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();

//            Log.e("main===mReceiver", "==="+action);

            if ("data.broadcast.action".equals(action)) {
                int count = intent.getIntExtra("count", 0);
                if (count > 0) {
                    tab.showMsg(1, count);
                    tab.setMsgMargin(1, -8, 5);
                } else {
                    tab.hideMsg(0);
                }
            }
        }
    };

    private void initData() {
        mContext = this;
        bikeFragment = new BikeFragment();
        nearFragment = new NearFragment();
        purseFragment = new PurseFragment();
        mineFragment = new MineFragment();
        mFragments.add(bikeFragment);
        mFragments.add(nearFragment);
        mFragments.add(purseFragment);
        mFragments.add(mineFragment);

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
    }

    private void initView() {

        tab = findViewById(R.id.tab);

        tab.setTabData(mTabEntities, MainActivity.this, R.id.fl_change, mFragments);
        tab.setCurrentTab(0);
    }

    public void changeTab(int index) {
        tab.setCurrentTab(index);
    }

    private void initListener() {
    }

    @Override protected void onResume() {
        super.onResume();
    }

}
