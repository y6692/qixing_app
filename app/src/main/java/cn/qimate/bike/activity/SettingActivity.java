package cn.qimate.bike.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.apache.http.Header;
import org.json.JSONArray;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import cn.jock.pickerview.view.view.OptionsPickerView;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.loopj.android.http.TextHttpResponseHandler2;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.DataCleanManager;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.UpdateManager;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.H5Bean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.SchoolListBean;
import cn.qimate.bike.model.UserIndexBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.ToastUtil;

import static java.lang.System.getProperties;

/**
 * Created by Administrator on 2017/2/12 0012.
 */

public class SettingActivity extends SwipeBackActivity implements View.OnClickListener {

    private Context context;
    private ImageView backImg;

    CustomDialog.Builder customBuilder;
    private CustomDialog customDialog;
    private CustomDialog customDialog2;

    private RelativeLayout cleanLayout;
    private RelativeLayout checkUpdateLayout;
    private RelativeLayout aboutUsLayout;
    private RelativeLayout logoutLayout;
    private TextView tv_version;
    private ImageView iv_isUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        context = this;

        initView();
    }

    private void initView(){
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setType(3).setTitle("温馨提示").setMessage("您将退出登录")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                        dialog.cancel();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
        });
        customDialog = customBuilder.create();

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setType(4).setTitle("温馨提示").setMessage("操作成功")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                        dialog.cancel();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        customDialog2 = customBuilder.create();

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        cleanLayout = (RelativeLayout)findViewById(R.id.settingUI_cleanLayout);
        checkUpdateLayout = (RelativeLayout)findViewById(R.id.settingUI_checkUpdateLayout);
        aboutUsLayout = (RelativeLayout)findViewById(R.id.settingUI_aboutUsLayout);
        logoutLayout = (RelativeLayout)findViewById(R.id.settingUI_logoutLayout);
        tv_version = (TextView)findViewById(R.id.tv_version);
        iv_isUpdate = (ImageView)findViewById(R.id.iv_isUpdate);

        backImg.setOnClickListener(this);
        cleanLayout.setOnClickListener(this);
        checkUpdateLayout.setOnClickListener(this);
        aboutUsLayout.setOnClickListener(this);
        logoutLayout.setOnClickListener(this);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            tv_version.setText("版本"+info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }

        UpdateManager.getUpdateManager().setType(0).checkAppUpdate(this, context, 3, iv_isUpdate);

//        initHttp();

        Log.e("initView===", "===");
    }

    @Override
    public void onClick(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;

            case R.id.settingUI_cleanLayout:
                DataCleanManager.clearAllCache(this);

                Log.e("onClick===cleanLayout", "===");

                customDialog2.show();

//                // 清除编辑器保存的临时内容
//                Properties props = getProperties();
//                for (Object key : props.keySet()) {
//                    String _key = key.toString();
//                    if (_key.startsWith("temp"))
//                        removeProperty(_key);
//                }
//                Core.getKJBitmap().cleanCache();
//
//                PackageManager pm = getPackageManager();
//                //反射
//                try {
//                    Method method = PackageManager.class.getMethod("getPackageSizeInfo", new Class[]{String.class, IPackageStatsObserver.class});
//                    method.invoke(pm, new Object[]{"com.wang.clearcache",new IPackageStatsObserver.Stub() {
//
//                        @Override
//                        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
//                            long cachesize = pStats.cacheSize;
//                            long codesize = pStats.codeSize;
//                            long datasize = pStats.dataSize;
//                            System.out.println("cachesize:"+ cachesize);
//                            System.out.println("codesize:"+ codesize);
//                            System.out.println("datasize"+ datasize);
//                        }
//                    }});
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                break;

            case R.id.settingUI_checkUpdateLayout:
                UpdateManager.getUpdateManager().setType(0).checkAppUpdate(this, context, 1, iv_isUpdate);
                break;

            case R.id.settingUI_aboutUsLayout:
//                UIHelper.goWebViewAct(context, "关于我们", Urls.aboutUs);
                aboutus();
                break;

            case R.id.settingUI_logoutLayout:
                customDialog.show();
                break;

        }
    }

    private void aboutus(){

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token != null && !"".equals(access_token)){
            HttpHelper.get(context, Urls.aboutus+"?token="+access_token, new TextHttpResponseHandler() {
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
                                ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                                Log.e("aboutus===", "==="+responseString);

                                H5Bean bean = JSON.parseObject(result.getData(), H5Bean.class);

                                UIHelper.goWebViewAct(context, bean.getH5_title(), bean.getH5_url());

//                                if (result.getFlag().equals("Success")) {
//                                    UserIndexBean bean = JSON.parseObject(result.getData(), UserIndexBean.class);
//                                    nameEdit.setText(bean.getRealname());
//
//                                    sex = bean.getSex();
//                                    school = bean.getSchool();
//                                } else {
//                                    Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                                }
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
        }else {
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context,LoginActivity.class);
        }
    }

    private void logout() {

        HttpHelper.delete(context, Urls.authorizations, new TextHttpResponseHandler2() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在提交");
                    loadingDialog.show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {

                    Log.e("logout===", "==="+responseString);

                    if(responseString==null){
                        Intent intent0 = new Intent();
                        setResult(RESULT_OK, intent0);

                        SharedPreferencesUrls.getInstance().putString("access_token", "");
                        SharedPreferencesUrls.getInstance().putString("iscert", "");
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        scrollToFinishActivity();
                    }else{
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        ToastUtil.showMessageApp(context, result.getMessage());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }
        });
    }


    private void initHttp(){

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token != null && !"".equals(access_token)){
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            HttpHelper.get(context, Urls.userIndex, params, new TextHttpResponseHandler() {
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
                                ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                                Log.e("initHttp===", "==="+responseString);

//                                if (result.getFlag().equals("Success")) {
//                                    UserIndexBean bean = JSON.parseObject(result.getData(), UserIndexBean.class);
//                                    nameEdit.setText(bean.getRealname());
//
//                                    sex = bean.getSex();
//                                    school = bean.getSchool();
//                                } else {
//                                    Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                                }
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
