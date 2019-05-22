package cn.qimate.bike.core.widget;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MoneyTextWatcher implements TextWatcher {
	
	private EditText editText;
	private int moneyLength;
	
	public MoneyTextWatcher(EditText editText, int moneyLength) {
		this.editText = editText;
		this.moneyLength = moneyLength;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		
	}
	
	@Override
	public void onTextChanged(CharSequence s, int start, int count, int after) {
		if (moneyLength != 0 && s.toString().length() >= (moneyLength + 1)) {
			if (s.toString().trim().indexOf(".") < (moneyLength - 2) || s.toString().trim().indexOf(".") > moneyLength) {
				s = s.toString().subSequence(0, moneyLength);
				editText.setText(s);
				editText.setSelection(s.length());
			}
		}
		if (s.toString().contains(".")) {
			if (s.length() - 1 - s.toString().indexOf(".") > 2) {
				s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
				editText.setText(s);
				editText.setSelection(s.length());
			}
		}
		if (s.toString().trim().equals(".")) {
			s = "0" + s;
			editText.setText(s);
			editText.setSelection(2);
		}

		if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
			if (!s.toString().substring(1, 2).equals(".")) {
				editText.setText(s.subSequence(0, 1));
				editText.setSelection(1);
				return;
			}
		}
	}

}
