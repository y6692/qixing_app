package cn.qimate.bike.core.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.qimate.bike.R;

public class LoadingDialog2 extends Dialog {

	private static final int CHANGE_TITLE_WHAT = 1;
	private static final int CHNAGE_TITLE_DELAYMILLIS = 300;
	private static final int MAX_SUFFIX_NUMBER = 3;
	private static final char SUFFIX = '.';

	private ImageView iv_route;
	private RelativeLayout iv_progress_bar;
	private TextView detail_tv;
	private TextView tv_point;
//	private RotateAnimation mAnim;
	private Context context;

	private Handler handler = new Handler() {
		private int num = 0;

		public void handleMessage(android.os.Message msg) {
			if (msg.what == CHANGE_TITLE_WHAT) {
				StringBuilder builder = new StringBuilder();
				if (num >= MAX_SUFFIX_NUMBER) {
					num = 0;
				}
				num++;
				for (int i = 0; i < num; i++) {
					builder.append(SUFFIX);
				}
				tv_point.setText(builder.toString());
				if (isShowing()) {
					handler.sendEmptyMessageDelayed(CHANGE_TITLE_WHAT, CHNAGE_TITLE_DELAYMILLIS);
				} else {
					num = 0;
				}
			}
		};
	};

	public LoadingDialog2(Context context) {
		super(context, R.style.MyDialogStyle);
		this.context = context;
		init();
	}

	private void init() {
		setContentView(R.layout.common_dialog_loading_layout2);
		iv_route = (ImageView) findViewById(R.id.iv_route);
//		iv_progress_bar = (RelativeLayout) findViewById(R.id.iv_progress_bar);
		detail_tv = (TextView) findViewById(R.id.detail_tv);
		tv_point = (TextView) findViewById(R.id.tv_point);
//		initAnim();
	}

//	private void initAnim() {
//		mAnim = new RotateAnimation(0, 360, Animation.RESTART, 0.5f, Animation.RESTART, 0.5f);
////		mAnim.setDuration(2000);
//		mAnim.setDuration(1500);
//		mAnim.setRepeatCount(Animation.INFINITE);
//		mAnim.setRepeatMode(Animation.RESTART);
//		mAnim.setStartTime(Animation.START_ON_FIRST_FRAME);
//	}

	@Override
	public void show() {// 在要用到的地方调用这个方法
//		iv_route.startAnimation(mAnim);
//		Glide.with(context).load(R.drawable.loading_large).crossFade().into(iv_route);

//		AnimationDrawable animDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.lys_anim_ws_unit_sound_play);
		AnimationDrawable animDrawable = (AnimationDrawable) iv_route.getBackground();
		iv_route.setBackground(animDrawable);

//		AnimationDrawable animDrawable2 = (AnimationDrawable) iv_progress_bar.getBackground();
//		iv_progress_bar.setBackground(animDrawable2);

//		iv_route.setImageDrawable(animDrawable);
//		animDrawable.start();

		if (animDrawable != null && !animDrawable.isRunning()) {
			animDrawable.start();
		}

//		if (animDrawable2  != null && !animDrawable2 .isRunning()) {
//			animDrawable2 .start();
//		}

//		handler.sendEmptyMessage(CHANGE_TITLE_WHAT);
		super.show();
	}

	@Override
	public void dismiss() {
//		mAnim.cancel();
		super.dismiss();
	}

	@Override
	public void setTitle(CharSequence title) {
		if (TextUtils.isEmpty(title)) {
			detail_tv.setText("正在加载");
		} else {
			detail_tv.setText(title);
		}
	}

	@Override
	public void setTitle(int titleId) {
		setTitle(getContext().getString(titleId));
	}

	public static void dismissDialog(LoadingDialog2 loadingDialog) {
		if (null == loadingDialog) {
			return;
		}
		loadingDialog.dismiss();
	}
}