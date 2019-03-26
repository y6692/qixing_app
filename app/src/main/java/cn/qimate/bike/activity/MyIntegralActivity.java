package cn.qimate.bike.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.qimate.bike.R;
import cn.qimate.bike.fragment.MyIntegralRecordFragment;
import cn.qimate.bike.fragment.MyIntegralRuleFragment;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator1 on 2017/2/14.
 */

public class MyIntegralActivity extends SwipeBackActivity implements View.OnClickListener {

    private Context context;
    private LinearLayout ll_back;
    private Button leftBtn,rightBtn;

    private MyIntegralRecordFragment recordFragment;
    private MyIntegralRuleFragment ruleFragment;

    private Fragment hide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_my_integral);
        context = this;
        initView();
    }

    private void initView(){

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        leftBtn = (Button)findViewById(R.id.myIntegralUI_recordBtn);
        rightBtn = (Button)findViewById(R.id.myIntegralUI_ruleBtn);

        recordFragment = new MyIntegralRecordFragment();
        ruleFragment = new MyIntegralRuleFragment();

        ll_back.setOnClickListener(this);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        initTab();
    }

    private void initTab() {
        setTabBackground(0);
    }

    private void setTabBackground(int type) {
        switch (type) {
            case 0:
                replaceFragment(recordFragment, R.id.myIntegralUI_middleLayout);
                leftBtn.setSelected(true);
                rightBtn.setSelected(false);
                hide = recordFragment;
                break;
            case 1:
                replaceFragment(ruleFragment, R.id.myIntegralUI_middleLayout);
                leftBtn.setSelected(false);
                rightBtn.setSelected(true);
                hide = ruleFragment;
                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.ll_back:
                scrollToFinishActivity();
                break;
            case R.id.myIntegralUI_recordBtn:
                setTabBackground(0);
                break;
            case R.id.myIntegralUI_ruleBtn:
                setTabBackground(1);
                break;
        }
    }

    private void replaceFragment(Fragment replace, int containerViewId) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (replace.isAdded()) {
            ft.show(replace);
        } else {
            ft.add(containerViewId, replace);
        }
        if (hide != null && hide.isAdded() && replace != hide) {
            ft.hide(hide);
        }
        if (!isFinishing()) {
            ft.commit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
