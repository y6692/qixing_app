package cn.qimate.bike.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
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
 * Created by Administrator on 2017/2/12 0012.
 */

public class CreditScoreActivity extends SwipeBackActivity implements View.OnClickListener {

    private Context context;
    private LoadingDialog loadingDialog;
    private ImageView backImg;
    private TextView title;
    private TextView rightBtn;

    private TextView nameEdit;
    private TextView phoneNum;
    private RelativeLayout rl_name;
    private RelativeLayout rl_phoneNum;
    private RelativeLayout rl_realNameAuth;
    private RelativeLayout rl_studentAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_score);
        context = this;
        initView();
    }

    private void initView(){
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("信用分");
        rightBtn = (TextView) findViewById(R.id.mainUI_title_rightBtn);
        rightBtn.setText("信用记录");

//        nameEdit = (TextView)findViewById(R.id.settingUI_name);
//        phoneNum = (TextView)findViewById(R.id.settingUI_phoneNum);
//        rl_name = (RelativeLayout)findViewById(R.id.rl_name);
//        rl_phoneNum = (RelativeLayout)findViewById(R.id.rl_phoneNum);
//        rl_realNameAuth = (RelativeLayout)findViewById(R.id.rl_realNameAuth);
//        rl_studentAuth = (RelativeLayout)findViewById(R.id.rl_studentAuth);
//
        backImg.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
//        rl_name.setOnClickListener(this);
//        rl_phoneNum.setOnClickListener(this);
//        rl_realNameAuth.setOnClickListener(this);
//        rl_studentAuth.setOnClickListener(this);
//        initHttp();

    }

    @Override
    public void onClick(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;

            case R.id.mainUI_title_rightBtn:
                UIHelper.goToAct(context, CreditRecordActivity.class);
                break;

//            case R.id.rl_name:
//                UIHelper.goToAct(context, ChangePhoneNumActivity.class);
//                break;
//
//            case R.id.rl_phoneNum:
//                UIHelper.goToAct(context, ChangePhoneNumActivity.class);
//                break;
//
//            case R.id.rl_realNameAuth:
//                UIHelper.goToAct(context, RealNameAuthActivity.class);
//                break;
//
//            case R.id.rl_studentAuth:
//                UIHelper.goToAct(context, ChangePhoneNumActivity.class);
//                break;

        }
    }




    private void initHttp(){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid != null && !"".equals(uid) && access_token != null && !"".equals(access_token)){
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            HttpHelper.get(context, Urls.userIndex, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("正在加载");
                        loadingDialog.show();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                    UIHelper.ToastError(context, throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        if (result.getFlag().equals("Success")) {
                            UserIndexBean bean = JSON.parseObject(result.getData(), UserIndexBean.class);
                            nameEdit.setText(bean.getRealname());
                            phoneNum.setText(bean.getTelphone());
                        } else {
                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                }
            });
        }else {
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context,LoginActivity.class);
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
