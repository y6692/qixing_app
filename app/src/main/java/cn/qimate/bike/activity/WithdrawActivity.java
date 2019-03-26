package cn.qimate.bike.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import org.apache.http.Header;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.lock.utils.ToastUtils;
import cn.qimate.bike.model.InviteCodeBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UserIndexBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;

/**
 * Created by 123 on 2018/3/15.
 */

public class WithdrawActivity extends SwipeBackActivity implements View.OnClickListener{

    private Context context;
    private Activity mActivity;
    private LoadingDialog loadingDialog;

    private LinearLayout ll_back;
    private TextView titleText;
    private LinearLayout headLayout;
    private TextView nameText;
    private TextView accontText;
    private EditText moneyEdit;
    private TextView totalMoney;
    private TextView totalWithdrawBtn;
    private Button submitBtn;

    private ImageView iv_popup_window_back;
    private RelativeLayout popupWindow;
    private EditText dialogName;
    private EditText dialogAccount;
    private LinearLayout dialogCancle;
    private LinearLayout dialogConfirm;

    private String commission = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_withdraw);
        context = this;
        mActivity = this;
        init();
    }
    private void init(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        titleText = (TextView) findViewById(R.id.mainUI_title_titleText);
        titleText.setText("佣金提现");

        headLayout = (LinearLayout) findViewById(R.id.ui_withdraw_headLayout);
        nameText = (TextView)findViewById(R.id.ui_withdraw_nameText);
        accontText = (TextView)findViewById(R.id.ui_withdraw_accontText);
        moneyEdit = (EditText)findViewById(R.id.ui_withdraw_moneyEdit);
        totalMoney = (TextView)findViewById(R.id.ui_withdraw_totalMoney);
        totalWithdrawBtn = (TextView)findViewById(R.id.ui_withdraw_totalWithdrawBtn);
        submitBtn = (Button)findViewById(R.id.ui_withdraw_submitBtn);

        iv_popup_window_back = (ImageView)findViewById(R.id.dialog_withdraw_popupWindow_back);
        popupWindow = (RelativeLayout)findViewById(R.id.ui_withdraw_popupWindow);
        dialogName = (EditText)findViewById(R.id.dialog_withdraw_dialogName);
        dialogAccount = (EditText)findViewById(R.id.dialog_withdraw_dialogAccount);
        dialogConfirm = (LinearLayout)findViewById(R.id.dialog_withdraw_dialogConfirm);
        dialogCancle = (LinearLayout)findViewById(R.id.dialog_withdraw_dialogCancle);

        initListener();
    }
    private void initListener(){

        ll_back.setOnClickListener(this);
        headLayout.setOnClickListener(this);
        totalWithdrawBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        dialogConfirm.setOnClickListener(this);
        dialogCancle.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                scrollToFinishActivity();
                break;
            case R.id.ui_withdraw_headLayout:
                clickPopupWindow();
                break;
            case R.id.ui_withdraw_totalWithdrawBtn:
                moneyEdit.setText(commission);
                // 切换后将EditText光标置于末尾
                CharSequence charSequence = moneyEdit.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
                break;
            case R.id.ui_withdraw_submitBtn:
                if (nameText.getText().toString().trim() == null || "".equals(nameText.getText().toString().trim())
                        || "暂未设置支付宝".equals(nameText.getText().toString().trim())) {
                    ToastUtils.show("请设置您的支付宝真实姓名");
                    return;
                }
                if (accontText.getText().toString().trim() == null || "".equals(accontText.getText().toString().trim())
                        || "暂未设置支付宝提现账号".equals(accontText.getText().toString().trim())) {
                    ToastUtils.show( "请设置您的支付宝账号");
                    return;
                }
                if (moneyEdit.getText().toString().trim() == null || "".equals(moneyEdit.getText().toString().trim())) {
                    ToastUtils.show("请输入您的提现金额");
                    return;
                }
                CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                customBuilder.setTitle("温馨提示").setMessage("请确认账号是否正确、有效")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        userCash(moneyEdit.getText().toString().trim());
                    }
                });
                customBuilder.create().show();
                break;
            case R.id.dialog_withdraw_dialogConfirm:
                String realName = dialogName.getText().toString().trim();
                String account = dialogAccount.getText().toString().trim();
                if (realName == null || "".equals(realName)){
                    ToastUtils.show("请输入您的支付宝姓名");
                }
                if (account == null || "".equals(account)){
                    ToastUtils.show("请输入您的支付宝账号");
                }
                nameText.setText(realName);
                accontText.setText(account);
                clickClosePopupWindow();
                break;
            case R.id.dialog_withdraw_dialogCancle:
                clickClosePopupWindow();
                break;
            default:
                break;
        }
    }
    private void userCash(String cashmoney) {

        String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)) {
           ToastUtils.show("请先登录您的账号");
            UIHelper.goToAct(context, LoginActivity.class);
            return;
        }
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("access_token", access_token);
        params.put("cashmoney", cashmoney);
        params.put("bankaccount", accontText.getText().toString().trim());
        params.put("realname", nameText.getText().toString().trim());
        HttpHelper.post(context, Urls.applyCash, params, new TextHttpResponseHandler() {
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
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if ("Success".equals(result.getFlag())) {
                        ToastUtils.show("恭喜您,提现申请提交成功");
                        scrollToFinishActivity();
                    } else {
                        if ("10086".equals(result.getErrcode())) {
                            SharedPreferencesUrls.getInstance().putString("uid", "");
                            SharedPreferencesUrls.getInstance().putString("access_token", "");
                            UIHelper.goToAct(context, LoginActivity.class);
                        }
                        ToastUtils.show(result.getMsg());
                    }
                } catch (Exception e) {
                }
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        initHttp();
        initCommision();
    }

    private void initHttp() {

        String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (uid != null && !"".equals(uid) && access_token != null && !"".equals(access_token)) {
            RequestParams params = new RequestParams();
            params.put("uid", uid);
            params.put("access_token", access_token);
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
                    if (loadingDialog != null && loadingDialog.isShowing()) {
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
                            nameText.setText(bean.getRealname());
                            accontText.setText(bean.getTelphone());
                            dialogName.setText(bean.getRealname());
                            dialogAccount.setText(bean.getTelphone());
                        } else {
                            Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                }
            });
        } else {
            Toast.makeText(context, "请先登录账号", Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }
    }
    /**
     * 显示弹窗
     */
    private void clickPopupWindow() {
        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(this);
        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
            UtilBitmap.blurImageView(this, iv_popup_window_back, 6, 0xAA000000);
        } else {
            // 获取的Bitmap为null时，用半透明代替
            iv_popup_window_back.setBackgroundColor(0x77000000);
        }
        // 打开弹窗
        UtilAnim.showToUp(popupWindow, iv_popup_window_back);
    }
    /**
     * 关闭弹窗
     */
    private void clickClosePopupWindow() {
        UtilAnim.hideToDown(popupWindow, iv_popup_window_back);
    }

    private void initCommision(){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context,LoginActivity.class);
            return;
        }
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("access_token", access_token);
        HttpHelper.get(context, Urls.inviteCode, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在加载");
                    loadingDialog.show();
                }
            }
            @Override
            public void onSuccess(int statusCode,  org.apache.http.Header[] headers, String responseString) {
                com.umeng.socialize.utils.Log.e("Test","RRRR:"+responseString);
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        InviteCodeBean bean = JSON.parseObject(result.getData(),InviteCodeBean.class);
                        commission = bean.getCommission();
                        totalMoney.setText("可提现余额：" + bean.getCommission());
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
