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
import android.widget.EditText;
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
import cn.qimate.bike.model.RechargeBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.ToastUtil;

/**
 * Created by yuanyi on 2019/12/9.
 */
public class ExchangeActivity extends SwipeBackActivity implements View.OnClickListener{


    private Context context;
    private LoadingDialog loadingDialog;
    private LinearLayout backImg;
    private EditText codeEdit;
    private LinearLayout submitBtn;

    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        context = this;

        initView();
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        backImg = (LinearLayout) findViewById(R.id.ll_backBtn);
        submitBtn = (LinearLayout)findViewById(R.id.exchangeUI_submitBtn);
        codeEdit = (EditText)findViewById(R.id.exchangeUI_code);


        backImg.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (v.getId()){
            case R.id.ll_backBtn:
                scrollToFinishActivity();
                break;

            case R.id.exchangeUI_submitBtn:
                code = codeEdit.getText().toString();

                if (access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (code == null || "".equals(code)){
                    Toast.makeText(context,"请输入兑换码",Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.e("submitBtn===", "===");

                exchange();
                break;
        }
    }

    private void exchange(){

        Log.e("ea===exchange", "==="+code);

        RequestParams params = new RequestParams();
        params.put("exchange_code", code);

        HttpHelper.post(context, Urls.exchange, params, new TextHttpResponseHandler() {     //TODO  {"message":"Non-static method Illuminate\\Database\\Eloquent\\Builder::update() should not be called statically","status_code":500,"debug":{"line":105,"file":"\/www\/wwwroot\/newmapi.7mate\/app\/Http\/Controllers\/Api\/Web\/V1\/CyclingCardController.php","class":"ErrorException","trace":["#0 \/www\/wwwroot\/newmapi.7mate\/app\/Http\/Controllers\/Api\/Web\/V1\/CyclingCardController.php(105): Laravel\\Lumen\\Application->Laravel\\Lumen\\Concerns\\{closure}(8192, 'Non-static meth...', '\/www\/wwwroot\/ne...', 105, Array)","#1 [internal function]: App\\Http\\Controllers\\Api\\Web\\V1\\CyclingCardController->exchange(Object(Dingo\\Api\\Http\\Request))","#2 \/www\/wwwroot\/newmapi.7mate\/vendor\/illuminate\/container\/BoundMethod.php(29): call_user_func_array(Array, Array)","#3 \/www\/wwwroot\/newmapi.7mate\/vendor\/illuminate\/container\/BoundMethod.php(87): Illuminate\\Container\\BoundMethod::Illuminate\\Container\\{closure}()","#4 \/www\/wwwroot\/newmapi.7mate\/vendor\/illuminate\/container\/BoundMethod.php(31): Illuminate\\Container\\BoundMethod::callBoundMethod(Object(Laravel\\Lumen\\Application), Array, Object(Closure))","#5 \/www\/wwwroot\/newmapi.7mate\/vendor\/illuminate\/container\/Container.php(572): Illuminate\\Container\\BoundMethod::call(Object(Laravel\\Lumen\\Application), Array, Array, NULL)","#6 \/www\/wwwroot\/newmapi.7mate\/vendor\/laravel\/lumen-framework\/src\/Concerns\/RoutesRequests.php(378): Illuminate\\Container\\Container->call(Array, Array)","#7 \/www\/wwwroot\/newmapi.7mate\/vendor\/laravel\/lumen-framework\/src\/Concerns\/RoutesRequests.php(344): Laravel\\Lumen\\Application->callControllerCallable(Array, Array)","#8 \/www\/wwwroot\/newmapi.7mate\/vendor\/laravel\/lumen-framework\/src\/Concerns\/RoutesRequests.php(318): Laravel\\Lumen\\Application->callLumenController(Object(App\\Http\\Controllers\\Api\\Web\\V1\\CyclingCardController), 'exchange', Array)","#9 \/www\/wwwroot\/newmapi.7mate\/vendor\/laravel\/lumen-framework\/src\/Concerns\/RoutesRequests.php(280): Laravel\\Lumen\\Application->callControllerAction(Array)","#10 \/www\/wwwroot\/newmapi.7mate\/vendor\/laravel\/lumen-framework\/src\/Concerns\/RoutesRequests.php(260): Laravel\\Lumen\\Application->callActionOnArrayBasedRoute(Array)","#11 [internal function]: Laravel\\Lumen\\Application->Laravel\\Lumen\\Concerns\\{closure}(Object(Dingo\\Api\\Http\\Request))","#12 \/www\/wwwroot\/newmapi.7mate\/vendor\/laravel\/lumen-framework\/src\/Routing\/Pipeline.php(52): call_user_func(Object(Closure), Object(Dingo\\Api\\Http\\Request))","#13 \/www\/wwwroot\/newmapi.7mate\/vendor\/dingo\/api\/src\/Http\/Middleware\/Auth.php(55): Laravel\\Lumen\\Routing\\Pipeline->Laravel\\Lumen\\Routing\\{closure}(Object(Dingo\\Api\\Http\\Request))","#14 \/www\/wwwroot\/newmapi.7mate\/vendor\/illuminate\/pipeline\/Pipeline.php(163): Dingo\\Api\\Http\\Middleware\\Auth->handle(Object(Dingo\\Api\\Http\\Request), Object(Closure))","#15 [internal function]: Illuminate\\Pipeline\\Pipeline->Illuminate\\Pipeline\\{closure}(Object(Dingo\\Api\\Http\\Request))","#16 \/www\/wwwroot\/newmapi.7mate\/vendor\/laravel\/lumen-framework\/src\/Routing\/Pipeline.php(32): call_user_func(Object(Closure), Object(Dingo\\Api\\Http\\Request))","#17 \/www\/wwwroot\/newmapi.7mate\/vendor\/dingo\/api\/src\/Http\/Middleware\/RateLimit.php(70): Laravel\\Lumen\\Routing\\Pipeline->Laravel\\Lumen\\Routing\\{closure}(Object(Dingo\\Api\\Http\\Request))","#18 \/www\/wwwroot\/newmapi.7mate\/vendor\/illuminate\/pipeline\/Pipeline.php(163): Dingo\\Api\\Http\\Middleware\\RateLimit->handle(Object(Dingo\\Api\\Http\\Request), Object(Closure))","#19 [internal function]: Illuminate\\Pipeline\\Pipeline->Illuminate\\Pipeline\\{closure}(Object(Dingo\\Api\\Http\\Request))","#20 \/www\/wwwroot\/newmapi.7mate\/vendor\/laravel\/lumen-framework\/src\/Routing\/Pipeline.php(32): call_user_func(Object(Closure), Object(Dingo\\Api\\Http\\Request))","#21 \/www\/wwwroot\/newmapi.7mate\/vendor\/liyu\/dingo-serializer
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在提交");
                    loadingDialog.show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                Log.e("ea===exchange_fail", throwable.toString()+"==="+responseString);

                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Log.e("ea===exchange1", "==="+responseString);

                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);


                    ToastUtil.showMessageApp(context, result.getMessage());

                    if(result.getStatus_code()==200){
                        scrollToFinishActivity();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
