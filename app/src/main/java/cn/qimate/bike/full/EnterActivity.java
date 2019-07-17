package cn.qimate.bike.full;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.jpush.android.api.JPushInterface;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.MainActivity;
import cn.qimate.bike.base.BaseActivity;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.widget.MyScrollLayout;
import cn.qimate.bike.listener.OnViewChangeListener;

public class EnterActivity extends BaseActivity implements OnViewChangeListener, OnClickListener {
	/** Called when the activity is first created. */

	private MyScrollLayout mScrollLayout;
	private ImageView[] mImageViews;
	private int mViewCount;
	private int mCurSel;
	private Button enter_btn;
	private Button enter_btn_1;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		setContentView(R.layout.main_guide);
		init();
		enter_btn = (Button) this.findViewById(R.id.enter_btn);
		enter_btn_1 = (Button) this.findViewById(R.id.enter_btn_1);
		enter_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tz();
			}
		});
		enter_btn_1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tz();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}

	@Override	
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}

	private void init() {
		mScrollLayout = (MyScrollLayout) findViewById(R.id.ScrollLayout);
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llayout);
		mViewCount = mScrollLayout.getChildCount();
		mImageViews = new ImageView[mViewCount];
		for (int i = 0; i < mViewCount; i++) {
			mImageViews[i] = (ImageView) linearLayout.getChildAt(i);
			mImageViews[i].setEnabled(true);
			mImageViews[i].setOnClickListener(this);
			mImageViews[i].setTag(i);
		}
		mCurSel = 0;
		mImageViews[mCurSel].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);

	}

	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
			return;
		}
		mImageViews[mCurSel].setEnabled(true);
		mImageViews[index].setEnabled(false);
		mCurSel = index;
	}

	public void OnViewChange(int view) {
		// TODO Auto-generated method stub
		if (view < 0 || mCurSel == view) {
			return;
		} else if (view > mViewCount - 1){
			tz();
		}
		setCurPoint(view);
	}

	private void tz(){
		UIHelper.goToAct(context, MainActivity.class);
		finishMine();
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		int pos = (Integer) (v.getTag());
		setCurPoint(pos);
		mScrollLayout.snapToScreen(pos);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// AppManager.getAppManager().finishActivity(this);
			finishMine();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}