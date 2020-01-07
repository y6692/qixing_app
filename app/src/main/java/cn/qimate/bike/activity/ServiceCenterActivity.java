package cn.qimate.bike.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.UpdateManager;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.fragment.BikeFaultFragment;
import cn.qimate.bike.fragment.BikeServiceFragment;
import cn.qimate.bike.fragment.EbikeFaultFragment;
import cn.qimate.bike.fragment.EbikeServiceFragment;
import cn.qimate.bike.model.GlobalConfig;
import cn.qimate.bike.model.PhoneBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.ServiceBean;
import cn.qimate.bike.model.TabTopEntity;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

@SuppressLint("NewApi")
public class ServiceCenterActivity extends SwipeBackActivity implements View.OnClickListener {

    CommonTabLayout tab;

    private LinearLayout ll_back;
    private TextView rightBtn;

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private String[] mTitles = { "单车", "助力车"};

    String title1;
    String title2;
    String phone1;
    String phone2;

    private String bikeCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_center);
        context = this;

        bikeCode = getIntent().getStringExtra("bikeCode");

        init();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void init(){

        ll_back = (LinearLayout) findViewById(R.id.ll_backBtn);
        rightBtn = (TextView) findViewById(R.id.mainUI_title_rightBtn);
        rightBtn.setText("人工客服");

        tab = (CommonTabLayout) findViewById(R.id.tab);

        BikeServiceFragment bikeServiceFragment = new BikeServiceFragment();
        EbikeServiceFragment ebikeServiceFragment = new EbikeServiceFragment();
        mFragments.add(bikeServiceFragment);
        mFragments.add(ebikeServiceFragment);

        Log.e("cfa===initData", "===");

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabTopEntity(mTitles[i]));
        }

        tab = (CommonTabLayout) findViewById(R.id.tab);
        tab.setTabData(mTabEntities, this, R.id.fl_carFault, mFragments);


        ll_back.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        initHttp();
    }

    private void initHttp(){
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            return;
        }

        HttpHelper.get(context, Urls.phones, null, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("sca===phonesFail","==="+responseString);
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Log.e("sca===phones1","==="+responseString);

                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                    JSONArray array = new JSONArray(result.getData());


                    for (int i = 0; i < array.length();i++){
                        PhoneBean bean = JSON.parseObject(array.getJSONObject(i).toString(), PhoneBean.class);

                        if(i==0){
                            title1 = bean.getTitle();
                            phone1 = bean.getPhone();
                        } else if(i==1){
                            title2 = bean.getTitle();
                            phone2 = bean.getPhone();
                        }


//                        datas.add(bean);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.mainUI_title_rightBtn:
//                UIHelper.goToAct(context, MyCartActivity.class);

                initmPopupWindowView();

                break;
        }
    }

    public void initmPopupWindowView(){

        // 获取自定义布局文件的视图
        View customView = getLayoutInflater().inflate(R.layout.pop_service_menu, null, false);
        // 创建PopupWindow宽度和高度
        RelativeLayout pop_win_bg = (RelativeLayout) customView.findViewById(R.id.pop_win_bg);
        ImageView iv_popup_window_back = (ImageView) customView.findViewById(R.id.popupWindow_back);
        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(this);
        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
            UtilBitmap.blurImageView(context, iv_popup_window_back, 10,0xAA000000);
        } else {
            // 获取的Bitmap为null时，用半透明代替
            iv_popup_window_back.setBackgroundColor(0x77000000);
        }
        // 打开弹窗
        UtilAnim.showToUp(pop_win_bg, iv_popup_window_back);
        // 创建PopupWindow宽度和高度
        final PopupWindow popupwindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

        //设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
        popupwindow.setAnimationStyle(R.style.PopupAnimation);
        popupwindow.setOutsideTouchable(false);

        TextView tv_feedback = (TextView)customView.findViewById(R.id.pop_tv_feedback);
        TextView tv_title1 = (TextView)customView.findViewById(R.id.pop_title1);
        TextView tv_title2 = (TextView)customView.findViewById(R.id.pop_title2);
        TextView tv_phone1 = (TextView)customView.findViewById(R.id.pop_phone1);
        TextView tv_phone2 = (TextView)customView.findViewById(R.id.pop_phone2);
        LinearLayout cancelLayout = (LinearLayout)customView.findViewById(R.id.ll_pop_cancelLayout);
//        final LinearLayout callLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_callLayout);

        tv_title1.setText(title1);
        tv_title2.setText(title2);
        tv_phone1.setText(phone1);
        tv_phone2.setText(phone2);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.pop_phone1:
                        if (Build.VERSION.SDK_INT >= 23) {
                            int checkPermission = checkSelfPermission(Manifest.permission.CALL_PHONE);
                            if (checkPermission != PERMISSION_GRANTED) {
                                if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                                    requestPermissions(new String[] { Manifest.permission.CALL_PHONE }, 0);
                                } else {
                                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                    customBuilder.setType(3).setTitle("温馨提示").setMessage("您需要在设置里打开拨打电话权限！")
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            requestPermissions(
                                                    new String[] { Manifest.permission.CALL_PHONE }, 0);
                                        }
                                    });
                                    customBuilder.create().show();
                                }
                                return;
                            }
                        }
                        Intent intent=new Intent();
                        intent.setAction(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + phone1));
                        context.startActivity(intent);
                        break;

                    case R.id.pop_phone2:
                        if (Build.VERSION.SDK_INT >= 23) {
                            int checkPermission = checkSelfPermission(Manifest.permission.CALL_PHONE);
                            if (checkPermission != PERMISSION_GRANTED) {
                                if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                                    requestPermissions(new String[] { Manifest.permission.CALL_PHONE }, 0);
                                } else {
                                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                    customBuilder.setType(3).setTitle("温馨提示").setMessage("您需要在设置里打开拨打电话权限！")
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            requestPermissions(
                                                    new String[] { Manifest.permission.CALL_PHONE }, 0);
                                        }
                                    });
                                    customBuilder.create().show();
                                }
                                return;
                            }
                        }
                        intent=new Intent();
                        intent.setAction(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + phone2));
                        context.startActivity(intent);
                        break;

                    case R.id.pop_tv_feedback:
                        UpdateManager.getUpdateManager().setType(2).setBikeCode(bikeCode).checkAppUpdate(ServiceCenterActivity.this, context, 1, null);
//                        UIHelper.goToAct(context, EndBikeFeedBackActivity.class);

                        break;
                    case R.id.ll_pop_cancelLayout:
//                        UIHelper.goToAct(context, ServiceCenterActivity.class);

                        break;
                }
                popupwindow.dismiss();
            }
        };

        tv_feedback.setOnClickListener(listener);
        tv_phone1.setOnClickListener(listener);
        tv_phone2.setOnClickListener(listener);
        cancelLayout.setOnClickListener(listener);


        popupwindow.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

}