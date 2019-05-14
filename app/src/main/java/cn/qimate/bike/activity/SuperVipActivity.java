package cn.qimate.bike.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.qimate.bike.R;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator1 on 2017/8/18.
 */

public class SuperVipActivity extends SwipeBackActivity implements View.OnClickListener {

    private ImageView backImg;
    private TextView title;

    private LinearLayout headLayout;
    private TextView bikeNumText;
    private TextView daysBikeText;
    private TextView daysEbikeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_super_vip);
        initView();
    }

    private void initView(){

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("超级会员");

        headLayout = (LinearLayout)findViewById(R.id.ui_superVip_headLayout);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) headLayout.getLayoutParams();
        params.height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.4);
        headLayout.setLayoutParams(params);

        bikeNumText = (TextView)findViewById(R.id.ui_superVip_bikeNumText);
        daysBikeText = (TextView)findViewById(R.id.ui_superVip_daysBikeText);
        daysEbikeText = (TextView)findViewById(R.id.ui_superVip_daysEbikeText);

        backImg.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SharedPreferencesUrls.getInstance().getString("bikenum","") == null ||
                "".equals(SharedPreferencesUrls.getInstance().getString("bikenum",""))
                || "0".equals(SharedPreferencesUrls.getInstance().getString("bikenum",""))){
            bikeNumText.setText("/");
        }else {
            bikeNumText.setText(SharedPreferencesUrls.getInstance().getString("bikenum",""));
        }
        if (SharedPreferencesUrls.getInstance().getString("specialdays","") == null ||
                "".equals(SharedPreferencesUrls.getInstance().getString("specialdays",""))
                || "0".equals(SharedPreferencesUrls.getInstance().getString("specialdays",""))){
            daysBikeText.setText("/");
        }else {
            daysBikeText.setText(SharedPreferencesUrls.getInstance().getString("specialdays",""));
        }

        if (SharedPreferencesUrls.getInstance().getString("ebike_specialdays","") == null ||
                "".equals(SharedPreferencesUrls.getInstance().getString("ebike_specialdays",""))
                || "0".equals(SharedPreferencesUrls.getInstance().getString("ebike_specialdays",""))){
            daysEbikeText.setText("/");
        }else {
            daysEbikeText.setText(SharedPreferencesUrls.getInstance().getString("ebike_specialdays",""));
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            default:
                break;
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
