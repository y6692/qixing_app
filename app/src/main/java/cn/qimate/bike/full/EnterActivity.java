package cn.qimate.bike.full;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import cn.jpush.android.api.JPushInterface;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.Main2Activity;
import cn.qimate.bike.activity.Main4Activity;
import cn.qimate.bike.activity.MainActivity;
import cn.qimate.bike.base.BaseActivity;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.MyScrollLayout;
import cn.qimate.bike.fragment.BikeFragment;
import cn.qimate.bike.listener.OnViewChangeListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class EnterActivity extends BaseActivity implements OnViewChangeListener, OnClickListener {
	/** Called when the activity is first created. */

	private MyScrollLayout mScrollLayout;
	private ImageView[] mImageViews;
	private int mViewCount;
	private int mCurSel;
	private Button enter_btn;
	private Button enter_btn_1;
	private Context context;

	GifImageView scrollLayout1;
	GifDrawable gifDrawable;

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

//		m_myHandler.postDelayed().sendEmptyMessage(1);

//		m_myHandler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				Log.e("gifDrawable===", "==="+gifDrawable);
//
////				gifDrawable.start();
//
//			}
//		}, 2 * 1000);
	}

	@Override	
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}

	private void init() {
		mScrollLayout = (MyScrollLayout) findViewById(R.id.ScrollLayout);
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llayout);
//		ImageView scrollLayout1 = (ImageView) findViewById(R.id.ScrollLayout1);
		scrollLayout1 = (GifImageView) findViewById(R.id.ScrollLayout1);
//		gifDrawable = (GifDrawable) scrollLayout1.getDrawable();

//		Glide.with(context).load("").into(scrollLayout1);

//		Glide.with(context).load("").asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(scrollLayout1);

//		mViewCount = mScrollLayout.getChildCount();
		mViewCount = 1;
		mImageViews = new ImageView[mViewCount];
		for (int i = 0; i < mViewCount; i++) {
			mImageViews[i] = (ImageView) linearLayout.getChildAt(i);
			mImageViews[i].setEnabled(true);
			mImageViews[i].setOnClickListener(this);
			mImageViews[i].setTag(i);
		}
		mCurSel = 0;
		mImageViews[mCurSel].setEnabled(false);
		scrollLayout1.setOnClickListener(this);
//		mScrollLayout.SetOnViewChangeListener(this);



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
//		if (view < 0 || mCurSel == view) {
//			return;
//		} else if (view > mViewCount - 1){
//			tz();
//		}
		tz();
//		setCurPoint(view);
	}

	private void tz(){
		UIHelper.goToAct(context, MainActivity.class);
		finishMine();
	}

	protected Handler m_myHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message mes) {
			switch (mes.what) {
				case 1:

					gifDrawable.start();
//					scrollLayout1.setBackground(gifDrawable);
					break;

				default:
					break;
			}
			return false;
		}
	});

	public void onClick(View v) {
//		int pos = (Integer) (v.getTag());
//		setCurPoint(pos);
//		mScrollLayout.snapToScreen(pos);

		tz();
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