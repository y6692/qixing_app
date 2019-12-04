package cn.qimate.bike.listener;

import org.xml.sax.XMLReader;

import android.content.Context;
import android.text.Editable;
import android.text.Html.TagHandler;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;

public class HtmlTagHandler implements TagHandler {

	private int sIndex = 0;
	private int eIndex = 0;
	private final Context context;

	public HtmlTagHandler(Context context) {
		this.context = context;
	}

	@Override
	public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {

		if (tag.toLowerCase().equals("span")) {
			if (opening) {
				sIndex = output.length();
			} else {
				eIndex = output.length();
				output.setSpan(new HtmlSpan(), sIndex, eIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}

	private class HtmlSpan extends ClickableSpan implements OnClickListener {

		@Override
		public void onClick(View view) {
			// 具体代码，可以是跳转页面，可以是弹出对话框，下面是跳转页面
			// context.startActivity(new Intent(mContext,MainFragment.class));
		}
	}
}
