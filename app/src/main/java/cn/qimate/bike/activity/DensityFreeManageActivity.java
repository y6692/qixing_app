package cn.qimate.bike.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.apache.http.Header;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.UpdateManager;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UserIndexBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator on 2017/2/12 0012.
 */

public class DensityFreeManageActivity extends SwipeBackActivity implements View.OnClickListener {

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;

    private Context context;
    private LoadingDialog loadingDialog;
    private LinearLayout ll_back;
    private TextView title;

    private RelativeLayout cleanLayout, checkLayout, aboutUsLayout, questionLayout;
    private LinearLayout logoutLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_density_free_manage);
        context = this;
        initView();
    }


    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("免密管理");


//        cleanLayout = (RelativeLayout) findViewById(R.id.personUI_bottom_cleanLayout);
//        checkLayout = (RelativeLayout) findViewById(R.id.personUI_bottom_checkLayout);
//        aboutUsLayout = (RelativeLayout) findViewById(R.id.personUI_bottom_aboutUsLayout);
//        questionLayout = (RelativeLayout) findViewById(R.id.personUI_bottom_questionLayout);
//        logoutLayout = (LinearLayout) findViewById(R.id.personUI_logoutLayout);
//
        ll_back.setOnClickListener(this);
//        cleanLayout.setOnClickListener(this);
//        checkLayout.setOnClickListener(this);
//        aboutUsLayout.setOnClickListener(this);
//        questionLayout.setOnClickListener(this);
//        logoutLayout.setOnClickListener(this);
//
//        initHttp();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                scrollToFinishActivity();
                break;
        }
    }

//    private void logout(String uid, String access_token) {
//
//        RequestParams params = new RequestParams();
//        params.put("uid", uid);
//        params.put("access_token", access_token);
//        HttpHelper.post(context, Urls.logout, params, new TextHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                    loadingDialog.setTitle("正在提交");
//                    loadingDialog.show();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (loadingDialog != null && loadingDialog.isShowing()) {
//                    loadingDialog.dismiss();
//                }
//                UIHelper.ToastError(context, throwable.toString());
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                try {
//                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                    if (result.getFlag().equals("Success")) {
//                        SharedPreferencesUrls.getInstance().putString("uid", "");
//                        SharedPreferencesUrls.getInstance().putString("access_token", "");
//                        SharedPreferencesUrls.getInstance().putString("nickname", "");
//                        SharedPreferencesUrls.getInstance().putString("realname", "");
//                        SharedPreferencesUrls.getInstance().putString("sex", "");
//                        SharedPreferencesUrls.getInstance().putString("headimg", "");
//                        SharedPreferencesUrls.getInstance().putString("points", "");
//                        SharedPreferencesUrls.getInstance().putString("money", "");
//                        SharedPreferencesUrls.getInstance().putString("bikenum", "");
//                        SharedPreferencesUrls.getInstance().putString("iscert", "");
//                        setAlias("");
//                        Toast.makeText(context, "恭喜您,您已安全退出!", Toast.LENGTH_SHORT).show();
//                        scrollToFinishActivity();
//                    } else {
//                        Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (loadingDialog != null && loadingDialog.isShowing()) {
//                    loadingDialog.dismiss();
//                }
//            }
//        });
//    }
//
//    // 极光推送===================================================================
//    private void setAlias(String uid) {
//        // 调用JPush API设置Alias
//        mHandler1.sendMessage(mHandler1.obtainMessage(MSG_SET_ALIAS, uid));
//    }
//
//    private final Handler mHandler1 = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case MSG_SET_ALIAS:
//                    JPushInterface.setAliasAndTags(context, (String) msg.obj, null, null);
//                    break;
//
//                case MSG_SET_TAGS:
//                    JPushInterface.setAliasAndTags(context, null, (Set<String>) msg.obj, null);
//                    break;
//
//                default:
//            }
//        }
//    };
//
//
//    private void initHttp(){
//
//        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
//        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
//        if (uid != null && !"".equals(uid) && access_token != null && !"".equals(access_token)){
//            RequestParams params = new RequestParams();
//            params.put("uid",uid);
//            params.put("access_token",access_token);
//            HttpHelper.get(context, Urls.userIndex, params, new TextHttpResponseHandler() {
//                @Override
//                public void onStart() {
//                    if (loadingDialog != null && !loadingDialog.isShowing()) {
//                        loadingDialog.setTitle("正在加载");
//                        loadingDialog.show();
//                    }
//                }
//                @Override
//                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }
//                    UIHelper.ToastError(context, throwable.toString());
//                }
//
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                    try {
//                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                        if (result.getFlag().equals("Success")) {
//                            UserIndexBean bean = JSON.parseObject(result.getData(), UserIndexBean.class);
////                            nameEdit.setText(bean.getRealname());
//
//                        } else {
//                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }
//                }
//            });
//        }else {
//            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
//            UIHelper.goToAct(context,LoginActivity.class);
//        }
//    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
