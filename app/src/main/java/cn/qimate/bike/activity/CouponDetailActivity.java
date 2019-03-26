package cn.qimate.bike.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.adapter.ActionCenterAdapter;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.ActionCenterBean;
import cn.qimate.bike.model.GlobalConfig;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * 优惠券详情
 * Created by yuanyi 2019/2/26.
 */

public class CouponDetailActivity extends SwipeBackActivity implements View.OnClickListener {

    private Context context = this;
    private LinearLayout ll_back;
    private TextView title;
    // List
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView myList;


    private ActionCenterAdapter myAdapter;
    private List<ActionCenterBean> datas;
    private boolean isRefresh = true;// 是否刷新中
    private boolean isLast = false;
    private int showPage = 1;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_detail);
        context = this;
        initView();
    }

    private void initView() {

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("优惠券详情");

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        ll_back.setOnClickListener(this);
    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_back:
                scrollToFinishActivity();
                break;

            default:
                break;
        }
    }

    private void initHttp() {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录您的账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context,LoginActivity.class);
            return;
        }
        RequestParams params = new RequestParams();
//        params.put("uid",uid);
//        params.put("access_token",access_token);
//        params.put("codenum", codenum);

        HttpHelper.get(context, Urls.badcarShow, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在加载");
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
                        JSONObject obj = new JSONObject(result.getData());

                        Log.e("initHttp===", "==="+obj.getString("longitude"));

//                        longitude = Double.parseDouble(obj.getString("longitude"));
//                        latitude = Double.parseDouble(obj.getString("latitude"));
//                        tvNum.setText(obj.getString("codenum"));
//
//                        final String telphone = obj.getString("telphone");
//
//                        tvTel.setText(telphone);
//                        tvTime.setText(obj.getString("lastusetime").substring(0,16));
//
//                        myLocation = new LatLng(latitude,longitude);
//                        addChooseMarker();
//                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12));
//
//                        tvTel.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Log.e("Test","111111111");
////                                linkTel = bean.getTelphone();
//                                if (Build.VERSION.SDK_INT >= 23) {
//                                    int checkPermission = context.checkSelfPermission(Manifest.permission.CALL_PHONE);
//                                    if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//                                        if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
//                                            requestPermissions(new String[] { Manifest.permission.CALL_PHONE }, 1);
//                                        } else {
//                                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//                                            customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开拨打电话权限！")
//                                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                                        public void onClick(DialogInterface dialog, int which) {
//                                                            dialog.cancel();
//                                                        }
//                                                    }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.cancel();
//                                                    MissionDetailActivity.this.requestPermissions(new String[] { Manifest.permission.CALL_PHONE }, 1);
//                                                }
//                                            });
//                                            customBuilder.create().show();
//                                        }
//                                        return;
//                                    }
//                                }
//                                CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//                                customBuilder.setTitle("温馨提示").setMessage("确认拨打" + telphone + "吗?")
//                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                dialog.cancel();
//                                            }
//                                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.cancel();
//                                        Intent intent=new Intent();
//                                        intent.setAction(Intent.ACTION_CALL);
//                                        intent.setData(Uri.parse("tel:" + telphone));
//                                        startActivity(intent);
//                                    }
//                                });
//                                customBuilder.create().show();
//                            }
//                        });


                    } else {
                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
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
