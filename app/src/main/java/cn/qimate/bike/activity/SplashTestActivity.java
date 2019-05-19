package cn.qimate.bike.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.sofi.blelocker.library.utils.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.http.OkHttpClientManager;
import cn.http.ResultCallback;
import cn.http.rdata.RUserLogin;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.util.Globals;
import cn.qimate.bike.util.Installation;
import cn.qimate.bike.util.UIHelper;
import okhttp3.Request;

/**
 * Created by heyong on 2017/5/23.
 */

public class SplashTestActivity extends Activity {
//    @BindView(R.id.layProgress)
//    LinearLayout layProgress;

    private String username = "", deviceId = "";
    private long delay = 1000;
    private Timer timer;
    private long beginTime = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_ui_splash);
        ButterKnife.bind(this);

        bindData();

        bindView();
    }

    protected void bindData() {
        timer = new Timer();
        username = "18151207269";
    }

    protected void bindView() {
        if (StringUtils.isNotEmpty(username)) {
//            layProgress.setVisibility(View.VISIBLE);
            deviceId = Installation.id(this);
            beginTime = System.currentTimeMillis();

//            deviceId = "5649abda-4bbd-42a7-9ecb-f56ecbd8a944";

//            toListActivity();

            OkHttpClientManager.getInstance().UserLogin(username, deviceId, new ResultCallback<RUserLogin>() {
                @Override
                public void onError(Request request, Exception e) {
                    UIHelper.showToast(SplashTestActivity.this, e.getMessage());
                    failLogin();
                }

                @Override
                public void onResponse(RUserLogin rUserLogin) {
                    if (rUserLogin.getResult() < 0) {
                        failLogin();

                        Log.e("login===1", deviceId+"==="+username);
                    }
                    else {
                        Globals.USERNAME = username;
                        successLogin();

                        Log.e("login===2", "==="+username);
                    }

                    //5649abda-4bbd-42a7-9ecb-f56ecbd8a944===18112348925
                    //b80a6288-5550-4cf7-88d8-c7d51bbee558===18112348925

//                    Globals.USERNAME = username;
//                    successLogin();
                }
            });
        }
        else {
            delayToLogin(delay);
        }
    }

    private void successLogin() {
        long waitTime = System.currentTimeMillis() - beginTime;
        boolean wait = waitTime < delay;

        if(wait) {
            delayToList(waitTime);
        }else {
            toListActivity();
        }
    }

    private void failLogin() {
        long waitTime = System.currentTimeMillis() - beginTime;
        boolean wait = waitTime < delay;

        if(wait) {
            delayToLogin(waitTime);
        }else {
            toLoginActivity();
        }
    }

    private void delayToLogin(long delay) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toLoginActivity();
            }
        }, delay);
    }

    private void delayToList(long delay) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toListActivity();
            }
        }, delay);
    }

    /**
     * 进入Main界面
     */
    private void toListActivity() {
//        showActivity(DeviceListActivity.class);
//        showActivity(DeviceDetailActivity.class);
        Intent intent = new Intent(this, DeviceDetailActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 进入Login界面
     */
    private void toLoginActivity() {
//        showActivity(LoginActivity.class);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}
