package cn.qimate.bike.core.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class ListenerInputView extends android.support.v7.widget.AppCompatEditText {
	private static final boolean DEBUG = true;
	private static final String TAG = "softinput";
	private Context mContext;
	private int softKeyboardHeight = 0;
	public ListenerInputView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public ListenerInputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ListenerInputView(Context context) {

		super(context);
		init(context);

	}

	private void init(Context context) {
		this.mContext = context;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		Log.e("onSizeChanged===", "===");

		super.onSizeChanged(w, h, oldw, oldh);

		if (DEBUG) {

			Log.i(TAG, "screenHeight=" + 0 + ";w=" + w + ";h=" + h + ";oldw=" + oldw + ";oldh=" + oldh);

		}

		if (Math.abs(h - oldh) > softKeyboardHeight) {

			if (h >= oldh) {

				if (DEBUG) {

					if (listener != null) {
						listener.OnKeyBoardState(0);
					}
					Log.i(TAG, "变化为正常状态(输入法关闭).");

				}

			} else {

				if (DEBUG) {
					if (listener != null) {
						listener.OnKeyBoardState(1);
					}
					Log.i(TAG, "输入法显示了.");
				}

			}

		} else {

			if (oldh != 0) {

				if (DEBUG) {
					Log.i(TAG, "变化为正常状态.(全屏关闭)");
					if (listener != null) {
						listener.OnKeyBoardState(0);
					}
				}

			} else {

				if (DEBUG) {
					Log.i(TAG, "初始化，当前为正常状态.");

				}

			}

		}

	}

	/**
	 * 监听键盘变化 state 1 开启软键盘 0关闭软键盘
	 * 
	 * @author haoweilai
	 * 
	 */
	public interface OnKeyBoardStateChangeListener {

		void OnKeyBoardState(int state);
	}

	private OnKeyBoardStateChangeListener listener;

	public void setOnKeyBoardStateChangeListener(
			OnKeyBoardStateChangeListener listener) {
		this.listener = listener;
	}
}
