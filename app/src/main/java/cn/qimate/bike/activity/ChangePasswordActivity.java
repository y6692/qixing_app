package cn.qimate.bike.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.Md5Helper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator on 2017/2/14 0014.
 */

public class ChangePasswordActivity extends SwipeBackActivity implements View.OnClickListener {

    private Context context;
    private LoadingDialog loadingDialog;
    private ImageView backImg;
    private TextView title;

    private EditText oldPsdEdit,newPsdEdit,againNewPsdEdit;
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_change_password);
        context = this;
        initView();
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("修改密码");

        oldPsdEdit = (EditText)findViewById(R.id.changePsdUI_oldPsd);
        newPsdEdit = (EditText)findViewById(R.id.changePsdUI_newPsd);
        againNewPsdEdit = (EditText)findViewById(R.id.changePsdUI_againNewPsd);
        submitBtn = (Button)findViewById(R.id.changePsdUI_submitBtn);

        backImg.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.changePsdUI_submitBtn:
                submit();
                break;
        }
    }

    private void submit(){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            return;
        }
        String password_old = oldPsdEdit.getText().toString();
        String password_new = newPsdEdit.getText().toString();
        String password_new2 = againNewPsdEdit.getText().toString();
        if (password_old == null || "".equals(password_old)){
            Toast.makeText(context,"请输入原始登录密码",Toast.LENGTH_SHORT).show();
            return;
        }
        if (password_new == null || "".equals(password_new)){
            Toast.makeText(context,"请输入新登录密码",Toast.LENGTH_SHORT).show();
            return;
        }
        if(6 > password_new.length()){
            Toast.makeText(context,"新登录密码至少6位数",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password_new2.equals(password_new)){
            Toast.makeText(context,"两次新密码输入不一致",Toast.LENGTH_SHORT).show();
            return;
        }

        Md5Helper Md5Helper = new Md5Helper();
        String oldpassword = cn.qimate.bike.core.common.Md5Helper.encode(password_old);
        String password = cn.qimate.bike.core.common.Md5Helper.encode(password_new);
        RequestParams params = new RequestParams();
        params.put("oldpassword", oldpassword);
        params.put("password", password);
        params.put("uid", uid);
        params.put("access_token", access_token);
        HttpHelper.post(context, Urls.alterPassword, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在提交");
                    loadingDialog.show();
                }
            }
            @Override
            public void onSuccess(int statusCode,  org.apache.http.Header[] headers, String responseString) {
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        String access_token = result.getData();
                        SharedPreferencesUrls.getInstance().putString("access_token", access_token);
                        Toast.makeText(context, "恭喜您,密码修改成功！", Toast.LENGTH_SHORT).show();
                        scrollToFinishActivity();
                    } else {
                        UIHelper.showToastMsg(context, result.getMsg(), R.drawable.ic_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode,  org.apache.http.Header[] headers, String responseString, Throwable throwable) {
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
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
