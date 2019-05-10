package cn.qimate.bike.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zxing.lib.scaner.activity.ActivityScanerCode;

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
import cn.qimate.bike.core.widget.MLImageView;
import cn.qimate.bike.model.EbikeInfoBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UserMonthIndexBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator1 on 2017/2/13.
 */

public class MyPurseActivity extends SwipeBackActivity implements View.OnClickListener{

    private Context context;
    private LoadingDialog loadingDialog;
    private ImageView backImg;
    private TextView title;

    private RelativeLayout headLayout;
    private MLImageView header;
    private TextView money;
    private TextView rechargeBtn;
    private Button activationBtn;

    private Dialog dialog;
    private EditText codeEdit;
    private Button positiveButton,negativeButton;
    private TextView dialogTitle;
    private TextView dialogTitle2;

    private Button monthCardBike;
    private Button monthCardEbike;

    private String bike_img_url = "";
    private String ebike_img_url = "";
    private String bike_desc = "";
    private String ebike_desc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_my_purse);
        context = this;
        initView();
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("我的钱包");

        headLayout = (RelativeLayout)findViewById(R.id.myPurseUI_headLayout);
        header = (MLImageView)findViewById(R.id.myPurseUI_headImage);
        money = (TextView)findViewById(R.id.myPurseUI_money);
        money.setText(SharedPreferencesUrls.getInstance().getString("money",""));
        rechargeBtn = (TextView)findViewById(R.id.myPurseUI_rechargeBtn);
        activationBtn = (Button)findViewById(R.id.myPurseUI_activationNum_btn);
        monthCardBike = (Button)findViewById(R.id.myPurseUI_monthCard_bike);
        monthCardEbike = (Button)findViewById(R.id.myPurseUI_monthCard_ebike);

        rechargeBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG );

        // 设置广告高度为屏幕高度0.6倍
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) headLayout.getLayoutParams();
        params.height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.6);
        headLayout.setLayoutParams(params);

        dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.pop_circles_menu, null);
        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);

        dialogTitle = (TextView) dialogView.findViewById(R.id.title);
        dialogTitle.setText("输入兑换码");

        dialogTitle2 = (TextView) dialogView.findViewById(R.id.title2);
        dialogTitle2.setVisibility(View.GONE);

        codeEdit = (EditText)dialogView.findViewById(R.id.pop_circlesMenu_bikeNumEdit);
        positiveButton = (Button)dialogView.findViewById(R.id.pop_circlesMenu_positiveButton);
        negativeButton = (Button)dialogView.findViewById(R.id.pop_circlesMenu_negativeButton);
        codeEdit.setHint("请输入兑换码");

        backImg.setOnClickListener(this);
        rechargeBtn.setOnClickListener(this);
        activationBtn.setOnClickListener(this);
        positiveButton.setOnClickListener(this);
        negativeButton.setOnClickListener(this);
        monthCardBike.setOnClickListener(this);
        monthCardEbike.setOnClickListener(this);

        userMonthIndex();

    }

    @Override
    public void onClick(View v) {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.myPurseUI_rechargeBtn:
                if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                    UIHelper.goToAct(context, LoginActivity.class);
                }else {
                    UIHelper.goToAct(context,RechargeActivity.class);
                }
                break;
            case R.id.myPurseUI_activationNum_btn:

                if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                    UIHelper.goToAct(context, LoginActivity.class);
                }else {
                    WindowManager windowManager = getWindowManager();
                    Display display = windowManager.getDefaultDisplay();
                    WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                    lp.width = (int) (display.getWidth() * 0.8); // 设置宽度0.6
                    lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    dialog.getWindow().setAttributes(lp);
                    dialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
                    dialog.show();

                    InputMethodManager manager = (InputMethodManager) getSystemService(
                            INPUT_METHOD_SERVICE);
                    manager.showSoftInput(v, InputMethodManager.RESULT_SHOWN);
                    manager.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                            InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
                break;
            case R.id.pop_circlesMenu_negativeButton:
                InputMethodManager manager1= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager1.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
                break;
            case R.id.pop_circlesMenu_positiveButton:
                String bikeNum = codeEdit.getText().toString().trim();
                if (bikeNum == null || "".equals(bikeNum)){
                    Toast.makeText(this,"请输入兑换码",Toast.LENGTH_SHORT).show();
                    return;
                }
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                activation(bikeNum);
                break;
            case R.id.myPurseUI_monthCard_bike:

                CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                customBuilder.setMessage(bike_desc);

                customBuilder.setType(5).setImg_url(bike_img_url).setTitle("温馨提示").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        Intent intent = new Intent(context, PayMontCartActivity.class);
                        intent.putExtra("carType",1);
                        context.startActivity(intent);

//                        UIHelper.goToAct(context,PayMontCartActivity.class);
//                        scrollToFinishActivity();
                    }
                }).setHint(false);
                customBuilder.create().show();


                break;
            case R.id.myPurseUI_monthCard_ebike:
                customBuilder = new CustomDialog.Builder(this);
                customBuilder.setMessage(ebike_desc);

                customBuilder.setType(5).setImg_url(ebike_img_url).setTitle("温馨提示").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        Intent intent = new Intent(context, PayMontCartActivity.class);
                        intent.putExtra("carType",2);
                        context.startActivity(intent);

//                        UIHelper.goToAct(context,PayMontCartActivity.class);
//                        scrollToFinishActivity();
                    }
                }).setHint(false);
                customBuilder.create().show();
                break;
            default:
                break;
        }
    }

    private void userMonthIndex(){
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            HttpHelper.post(context, Urls.userMonthIndex, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("正在提交");
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
//                            Toast.makeText(context,"获取成功",Toast.LENGTH_SHORT).show();

                            UserMonthIndexBean bean = JSON.parseObject(result.getData(), UserMonthIndexBean.class);

                            if("0".equals(bean.getBike_open_state())){
//                                monthCardBike.setVisibility(View.GONE);
//                                monthCardEbike.setVisibility(View.GONE);

                                monthCardBike.setVisibility(View.VISIBLE);
                                monthCardEbike.setVisibility(View.VISIBLE);
                            }else if("1".equals(bean.getBike_open_state())){
                                monthCardBike.setVisibility(View.VISIBLE);
                                monthCardEbike.setVisibility(View.GONE);
                            }else if("2".equals(bean.getBike_open_state())){
                                monthCardBike.setVisibility(View.GONE);
                                monthCardEbike.setVisibility(View.VISIBLE);
                            }else if("3".equals(bean.getBike_open_state())){
                                monthCardBike.setVisibility(View.VISIBLE);
                                monthCardEbike.setVisibility(View.VISIBLE);
                            }

                            bike_img_url = bean.getBike_img_url();
                            ebike_img_url = bean.getEbike_img_url();

                            bike_desc = bean.getBike_desc();
                            ebike_desc = bean.getEbike_desc();

                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }else {
                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                }
            });
        }
    }

    private void activation(final String code){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("codenums",code);
            HttpHelper.post(context, Urls.activation, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("正在提交");
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
                            Toast.makeText(context,"恭喜您，兑换成功",Toast.LENGTH_SHORT).show();
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }else {
                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                }
            });
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
