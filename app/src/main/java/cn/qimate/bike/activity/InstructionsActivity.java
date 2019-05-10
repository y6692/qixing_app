package cn.qimate.bike.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseActivity;
import cn.qimate.bike.core.common.DisplayUtil;
import cn.qimate.bike.core.widget.LoadingDialog;

/**
 * Created by Administrator on 2017/3/26 0026.
 */

public class InstructionsActivity extends BaseActivity implements View.OnClickListener{

    private Context context;
    private LinearLayout mainLayout;
    private ImageView titleImage;
    private ImageView exImage_1;
    private ImageView exImage_2;
    private ImageView exImage_3;

    private ImageView closeBtn;
    private int imageWith = 0;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ui_frist_view);
        context = this;
        imageWith = (int)(getWindowManager().getDefaultDisplay().getWidth() * 0.8);
        initView();
    }

    private void initView(){

        mainLayout = (LinearLayout)findViewById(R.id.ui_fristView_mainLayout);
        titleImage = (ImageView)findViewById(R.id.ui_fristView_title);
        exImage_1 = (ImageView)findViewById(R.id.ui_fristView_exImage_1);
//        exImage_2 = (ImageView)findViewById(R.id.ui_fristView_exImage_2);
//        exImage_3 = (ImageView)findViewById(R.id.ui_fristView_exImage_3);
        closeBtn = (ImageView)findViewById(R.id.ui_fristView_closeBtn);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) titleImage.getLayoutParams();
        params.height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.16);
        titleImage.setLayoutParams(params);

//        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) exImage_1.getLayoutParams();
//        params1.height = (imageWith - DisplayUtil.dip2px(context,20)) * 2 / 5;
//        exImage_1.setLayoutParams(params1);
//
//        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) exImage_2.getLayoutParams();
//        params2.height = (imageWith - DisplayUtil.dip2px(context,20)) * 2 / 5;
//        exImage_2.setLayoutParams(params2);
//
//        LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) exImage_3.getLayoutParams();
//        params3.height = (imageWith - DisplayUtil.dip2px(context,20)) * 2 / 5;
//        exImage_3.setLayoutParams(params3);

        LinearLayout.LayoutParams params4 = (LinearLayout.LayoutParams) mainLayout.getLayoutParams();
        params4.height = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.8);
        mainLayout.setLayoutParams(params4);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           finishMine();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
