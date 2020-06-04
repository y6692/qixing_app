package cn.qimate.bike.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import cn.qimate.bike.core.common.AppManager;

public class Base2Activity extends BaseFragmentActivity {


	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		context = this;
		BaseApplication.context = context;

		AppManager.getAppManager().addActivity(this);
		// 修改状态栏颜色，4.4+生效
//		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
//			// 透明状态栏
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			// 透明导航栏
////			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//		}



	}

	//字体适配解决方案
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (newConfig.fontScale != 1)//非默认值
			getResources();

		Log.e("ba===1", "===");

		super.onConfigurationChanged(newConfig);
	}

	@Override
	public Resources getResources() {
		Resources res = super.getResources();
		if (res.getConfiguration().fontScale != 1) {//非默认值
			Configuration newConfig = new Configuration();
			newConfig.setToDefaults();//设置默认
			res.updateConfiguration(newConfig, res.getDisplayMetrics());
		}

		Log.e("ba===2", res+"==="+res.getConfiguration().fontScale);

		return res;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			try {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				return imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			} catch (Exception e) {
			}
		}
		return super.onTouchEvent(event);
	}

	public void gotoAct(Class<?> clazz) {
		Intent intent = new Intent(this, clazz);
		startActivity(intent);
	}

	public void finishMine() {
		AppManager.getAppManager().finishActivity(this);
		// overridePendingTransition(R.anim.slide_in_from_left,R.anim.slide_out_from_right);
	}
}
