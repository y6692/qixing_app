package cn.qimate.bike.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.alipay.PayResult;
import cn.qimate.bike.base.BaseViewAdapter;
import cn.qimate.bike.base.BaseViewHolder;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.CertBean;
import cn.qimate.bike.model.RechargeBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UpTokenBean;
import cn.qimate.bike.model.UserBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.ToastUtil;

/**
 * Created by Administrator1 on 2017/2/15.
 */
public class AuthCenterActivity extends SwipeBackActivity implements View.OnClickListener{

    private static final int SDK_PAY_FLAG = 1;
    private IWXAPI api;

    private Context context;
    private LoadingDialog loadingDialog;

    private LinearLayout ll_backBtn;
    private TextView rightBtn;

    private RelativeLayout rl_selectLayout;
    private RelativeLayout rl_selectLayout2;
    private ImageView iv_select, iv_select2;
    private TextView tv_auth, tv_auth2, tv_depositprice;
    private LinearLayout submitBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_center);
        context = this;

        initView();
    }


    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        ll_backBtn = (LinearLayout) findViewById(R.id.ll_backBtn);
        rightBtn = (TextView)findViewById(R.id.mainUI_title_rightBtn);
        rightBtn.setText("联系客服");

        rl_selectLayout = (RelativeLayout)findViewById(R.id.rl_selectLayout);
        rl_selectLayout2 = (RelativeLayout)findViewById(R.id.rl_selectLayout2);
        iv_select = (ImageView)findViewById(R.id.iv_select);
        iv_select2 = (ImageView)findViewById(R.id.iv_select2);
        tv_auth = (TextView)findViewById(R.id.tv_auth);
        tv_auth2 = (TextView)findViewById(R.id.tv_auth2);
        tv_depositprice = (TextView)findViewById(R.id.tv_depositprice);
        submitBtn = (LinearLayout)findViewById(R.id.auth_center_submitBtn);

        ll_backBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        rl_selectLayout.setOnClickListener(this);
        rl_selectLayout2.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        initHttp();
        depositprice();
    }

    @Override
    public void onClick(View v) {
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (v.getId()){
            case R.id.ll_backBtn:
                scrollToFinishActivity();
                break;

            case R.id.rl_selectLayout:
//                iv_select.setVisibility(View.VISIBLE);
//                iv_select2.setVisibility(View.GONE);

//                UIHelper.goToAct(context, DepositFreeAuthActivity.class);
//                setResult(RESULT_OK);

                Intent intent = new Intent(context, DepositFreeAuthActivity.class);
                startActivityForResult(intent, 10);
//                setResult(RESULT_OK, intent);

                break;

            case R.id.rl_selectLayout2:
//                iv_select.setVisibility(View.GONE);
//                iv_select2.setVisibility(View.VISIBLE);

//                UIHelper.goToAct(context, RealNameAuthActivity.class);
//                setResult(RESULT_OK);

                intent = new Intent(context, RealNameAuthActivity.class);
                startActivityForResult(intent, 10);

                break;

            case R.id.mainUI_title_rightBtn:
                if (access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                    return;
                }
                UIHelper.goToAct(context, ServiceCenterActivity.class);
                break;

            case R.id.auth_center_submitBtn:
                if (access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                    return;
                }

                UIHelper.goToAct(context, RealNameAuthActivity.class);

                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {

                default:
                    break;
            }
        };
    };




    private void initHttp(){
        HttpHelper.get(context, Urls.user, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e("user===", "==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

//                            cert1_status	Int
//                            免押金认证状态 0待认证 1认证中 2已驳回 3认证成功      点击认证 认证中 已驳回 认证成功
//
//                            cert2_status	Int
//                            充值认证状态 0待认证 1认证中 2已驳回 3认证成功

                            if(result.getStatus_code()==0){
                                UserBean bean = JSON.parseObject(result.getData(), UserBean.class);

                                int  status1 = bean.getCert1_status();
                                int  status2 = bean.getCert2_status();
                                tv_auth.setText(status1==0?"点击认证":status1==1?"认证中":status1==2?"已驳回":"认证成功");
                                tv_auth2.setText(status2==0?"点击认证":status2==1?"认证中":status2==2?"已驳回":"认证成功");

                                Log.e("cert===2", bean+"==="+bean.getStatus());
                            }else{
                                ToastUtil.showMessageApp(context, result.getMessage());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                });

            }
        });
    }

    private void depositprice(){
        HttpHelper.get(context, Urls.depositprice, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e("depositprice===", "==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            JSONObject json = new JSONObject(result.getData());

                            tv_depositprice.setText("充值"+json.getInt("price_s")+"元立即用车");

//                            充值200元立即用车
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                });

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("aca=onActivityResult", requestCode+"==="+resultCode);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {
//                    codenum = data.getStringExtra("codenum");
//                    m_nowMac = data.getStringExtra("m_nowMac");

//                    Intent rIntent = new Intent();
//                    setResult(RESULT_OK, rIntent);
                    setResult(RESULT_OK);
                    scrollToFinishActivity();


                } else {
//                    Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
