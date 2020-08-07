package cn.qimate.bike.core.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
	private OnScrollListener onScrollListener;

	public MyScrollView(Context context) {
		this(context, null);
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (onScrollListener != null) {
			onScrollListener.onScroll(t);
		}
	}

	public interface OnScrollListener {
		public void onScroll(int scrollY);
	}

	// 是否要其弹性滑动
	@SuppressLint("NewApi")
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

		// 弹性滑动关键则是maxOverScrollX， 以及maxOverScrollY，
		// 一般默认值都是0，需要弹性时，更改其值即可
		// 即就是，为零则不会发生弹性，不为零（>0,负数未测试）则会滑动到其值的位置
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, 0, 0, isTouchEvent);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//关键点在这
		getParent().requestDisallowInterceptTouchEvent(true);
		return super.onInterceptTouchEvent(ev);
	}
}