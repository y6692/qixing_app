package cn.qimate.bike.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.qimate.bike.R;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.StringUtil;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by yuanyi on 2019/12/9.
 */
public class ChangePhoneActivity extends SwipeBackActivity implements View.OnClickListener{


    private Context context;
    private LoadingDialog loadingDialog;
    private LinearLayout backImg;


    private EditText phoneEdit;
    private LinearLayout submitBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);
        context = this;

        initView();
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        backImg = (LinearLayout) findViewById(R.id.ll_backBtn);

        phoneEdit = (EditText) findViewById(R.id.changePhoneUI_phone);
        submitBtn = (LinearLayout)findViewById(R.id.changePhoneUI_submitBtn);



        backImg.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (v.getId()){
            case R.id.ll_backBtn:
                scrollToFinishActivity();
                break;

            case R.id.changePhoneUI_submitBtn:
                String telphone = phoneEdit.getText().toString();
                if (telphone == null || "".equals(telphone)) {
                    Toast.makeText(context, "请输入您的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!StringUtil.isPhoner(telphone)) {
                    Toast.makeText(context, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }

                UIHelper.goToAct(context, NoteLoginActivity.class);
                scrollToFinishActivity();

                break;
        }
    }

//    private void userRecharge(final String uid, final String access_token){
//        RequestParams params = new RequestParams();
//        params.put("uid",uid);
//        params.put("access_token",access_token);
//        params.put("rid",rid);
//        params.put("paytype",paytype);
//
//        Log.e("userRecharge===", rid+"==="+paytype);
//
//        HttpHelper.post(context, Urls.userRecharge, params, new TextHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                    loadingDialog.setTitle("正在提交");
//                    loadingDialog.show();
//                }
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (loadingDialog != null && loadingDialog.isShowing()){
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
//                        osn = result.getData();
//                        if ("1".equals(paytype)){
//                            show_alipay(osn,uid,access_token);
//                        }else {
//                            show_wxpay(osn,uid,access_token);
//                        }
//                    } else {
//                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
//            }
//        });
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
