package cn.qimate.bike.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.qimate.bike.R;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by yuanyi on 2019/12/9.
 */
public class MyMessageDatailActivity extends SwipeBackActivity implements View.OnClickListener{


    private Context context;
    private LoadingDialog loadingDialog;
    private LinearLayout backImg;

    private TextView tv_title;
    private TextView tv_created_at;
    private TextView tv_action_content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message_datail);
        context = this;

        initView();
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        backImg = (LinearLayout) findViewById(R.id.ll_backBtn);
        tv_title = (TextView)findViewById(R.id.myMessageDetail_title);
        tv_created_at = (TextView)findViewById(R.id.myMessageDetail_created_at);
        tv_action_content = (TextView)findViewById(R.id.myMessageDetail_action_content);


        tv_title.setText(getIntent().getStringExtra("title"));
        tv_created_at.setText(getIntent().getStringExtra("created_at"));
        tv_action_content.setText(getIntent().getStringExtra("action_content"));

        backImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (v.getId()){
            case R.id.ll_backBtn:
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
