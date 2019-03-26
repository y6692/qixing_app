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
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.apache.http.Header;

import cn.jock.pickerview.view.view.OptionsPickerView;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UserIndexBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * 客服中心
 * Created by yuanyi on 2019/3/19 0012.
 */

public class ServiceCenterActivity extends SwipeBackActivity implements View.OnClickListener {

    private Context context;
    private LoadingDialog loadingDialog;
    private LinearLayout ll_back;
    private TextView title;
    private TextView rightBtn;

    private TextView nameEdit;
    private TextView phoneNum;
    private RelativeLayout rl_name;
    private RelativeLayout rl_phoneNum;
    private RelativeLayout rl_realNameAuth;
    private RelativeLayout rl_studentAuth;

    private OptionsPickerView pvOptions;
    private OptionsPickerView pvOptions1;
    private OptionsPickerView pvOptions2;
    private String sex = "";
    private String school = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_center);
        context = this;
        initView();
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);


        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("客服中心");
        rightBtn = (TextView) findViewById(R.id.mainUI_title_rightBtn);
        rightBtn.setText("拨打客服电话");

//        nameEdit = (TextView)findViewById(R.id.settingUI_name);
//        phoneNum = (TextView)findViewById(R.id.settingUI_phoneNum);
//        rl_name = (RelativeLayout)findViewById(R.id.rl_name);
//        rl_phoneNum = (RelativeLayout)findViewById(R.id.rl_phoneNum);
//        rl_realNameAuth = (RelativeLayout)findViewById(R.id.rl_realNameAuth);
//        rl_studentAuth = (RelativeLayout)findViewById(R.id.rl_studentAuth);
//
//
        ll_back.setOnClickListener(this);
//        rl_name.setOnClickListener(this);
//        rl_phoneNum.setOnClickListener(this);
//        rl_realNameAuth.setOnClickListener(this);
//        rl_studentAuth.setOnClickListener(this);
//
//        initHttp();

    }

    @Override
    public void onClick(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        switch (v.getId()){
            case R.id.ll_back:
                scrollToFinishActivity();
                break;

            case R.id.rl_name:
                UIHelper.goToAct(context, ChangePhoneNumActivity.class);
                break;

            case R.id.rl_phoneNum:
                UIHelper.goToAct(context, ChangePhoneNumActivity.class);
                break;

            case R.id.rl_realNameAuth:
                UIHelper.goToAct(context, RealNameAuthActivity.class);
                break;

            case R.id.rl_studentAuth:
                UIHelper.goToAct(context, ChangePhoneNumActivity.class);
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
