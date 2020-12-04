package cn.qimate.bike.core.widget;

import android.content.Context;
import android.util.AttributeSet;

public class MarqueTextView extends android.support.v7.widget.AppCompatTextView{
    public MarqueTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public MarqueTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MarqueTextView(Context context) {
        super(context);
    }
    @Override
    public boolean isFocused() {
        //就是把这里返回true即可
        return true;
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasWindowFocus) {
//
//        LogUtil.e("onWindowFocusChanged===", "==="+hasWindowFocus);
//
//        if(hasWindowFocus)
//            super.onWindowFocusChanged(hasWindowFocus);
//    }
}
