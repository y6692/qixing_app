package cn.qimate.bike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.qimate.bike.R;

/**
 * Created by heyong on 2017/5/23.
 */
public abstract class Base2Activity extends AppCompatActivity {
    private Unbinder mUnBinder;
    private Toolbar mToolbar;
    private TextView tvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
//        setContentView(getLayoutRes());
//        bindData();
//        bindView();
    }

    public void setTitle(int strId) {
        if(tvTitle != null) {
            tvTitle.setText(strId);
        }
    }

    public void setTitle(String text) {
        if(tvTitle != null) {
            tvTitle.setText(text);
        }
    }

    protected void setDisplayNone() {
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    protected void setDisplayBack() {
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void showActivity(Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    public void showActivityForResult(Class<? extends AppCompatActivity> activity, int reqCode) {
        Intent intent = new Intent(this, activity);
        startActivityForResult(intent, reqCode);
    }

    protected boolean autoBindViews() {
        return true;
    }

//    @CallSuper
//    protected void bindView() {
//        if (autoBindViews()) {
//            mUnBinder = ButterKnife.bind(this);
//        }
//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        tvTitle = (TextView) findViewById(R.id.toolbar_title);
//
//        if (mToolbar != null) {
//            setSupportActionBar(mToolbar);
//        }
//        if(getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//        }
//    }

    protected void bindData() {

    }

    protected void unbindView() {
        if (autoBindViews() && mUnBinder != null) {
            mUnBinder.unbind();
        }
    }

//    protected abstract int getLayoutRes();

    @Override
    protected void onDestroy() {
        unbindView();
        super.onDestroy();
    }

}
