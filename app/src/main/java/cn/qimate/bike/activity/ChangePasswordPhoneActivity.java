package cn.qimate.bike.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator on 2017/2/12 0012.
 */

public class ChangePasswordPhoneActivity extends SwipeBackActivity implements View.OnClickListener {

    private Context context;
    private TextView title;
    private LinearLayout ll_back;

    private RelativeLayout passwordLayout;
    private RelativeLayout phoneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_change_password_phone);
        context = this;
        initView();
    }

    private void initView(){
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("修改密码、手机号");

        passwordLayout = (RelativeLayout)findViewById(R.id.settingUI_passwordLayout);
        phoneLayout = (RelativeLayout)findViewById(R.id.settingUI_phoneLayout);

        ll_back.setOnClickListener(this);
        passwordLayout.setOnClickListener(this);
        phoneLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        switch (v.getId()){
            case R.id.ll_back:
                scrollToFinishActivity();
                break;
            case R.id.settingUI_passwordLayout:
                UIHelper.goToAct(context, ChangePasswordActivity.class);
                break;
            case R.id.settingUI_phoneLayout:
                UIHelper.goToAct(context,ChangePhoneNumActivity.class);
                break;


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
