package cn.qimate.bike.core.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class MyGridView extends GridView {

	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyGridView(Context context) {
		super(context);
	}

	public boolean isOnMeasure = false;

	public boolean isOnMeasure() {
		return isOnMeasure;
	}

	public void setOnMeasure(boolean onMeasure) {
		isOnMeasure = onMeasure;
	}

	public MyGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		isOnMeasure = false;
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		isOnMeasure = true;
		super.onLayout(changed, l, t, r, b);
	}

}
