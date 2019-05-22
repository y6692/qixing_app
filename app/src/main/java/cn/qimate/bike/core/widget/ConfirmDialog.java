package cn.qimate.bike.core.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.qimate.bike.R;

public class ConfirmDialog extends Dialog {
	private Context context;
	private String sureText = "确定";
	private String cancelText = "取消";
	private String descStr;
	private int resImgID;
	private int type;
	private ClickListenerInterface clickListenerInterface;

	public interface ClickListenerInterface {

		void doConfirm();

		void doCancel();
	}

	public ConfirmDialog(Context context, int resImgID, String descStr) {
		super(context, R.style.Theme_AppCompat_Light_Dialog);
		this.context = context;
		this.descStr = descStr;
		this.resImgID = resImgID;
	}
	
	public ConfirmDialog(Context context, int resImgID, String descStr, int type) {
		super(context, R.style.Theme_AppCompat_Light_Dialog);
		this.context = context;
		this.descStr = descStr;
		this.type = type;
		this.resImgID = resImgID;
	}
	
	public ConfirmDialog(Context context, int resImgID, String descStr ,String sureText, String cancelText, int type) {
		super(context, R.style.Theme_AppCompat_Light_Dialog);
		this.context = context;
		this.descStr = descStr;
		this.sureText = sureText;
		this.cancelText = cancelText;
		this.type = type;
		this.resImgID = resImgID;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	public void init() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_confirm, null);
		setContentView(view);

		ImageView resImage = view.findViewById(R.id.dialogConfirm_icon);
		TextView tvDesc = view.findViewById(R.id.dialogConfirm_descText);
		Button sureButton = view.findViewById(R.id.dialogConfirm_sureButton);
		Button cancelButton = view.findViewById(R.id.dialogConfirm_cancelButton);

		resImage.setImageResource(resImgID);
		tvDesc.setText(descStr);
		sureButton.setText(sureText);
		cancelButton.setText(cancelText);
		if(type == 0){
			cancelButton.setVisibility(View.GONE);
		}
		
		sureButton.setOnClickListener(new clickListener());
		cancelButton.setOnClickListener(new clickListener());

		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
		lp.width = (int) (d.widthPixels * 0.8); // 宽度设置为屏幕的0.6
		dialogWindow.setWindowAnimations(R.style.dialogWindowAnim);
		dialogWindow.setAttributes(lp);
	}

	public void setClicklistener(ClickListenerInterface clickListenerInterface) {
		this.clickListenerInterface = clickListenerInterface;
	}

	private class clickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.dialogConfirm_sureButton:
				clickListenerInterface.doConfirm();
				break;
			case R.id.dialogConfirm_cancelButton:
				clickListenerInterface.doCancel();
				break;
			}
		}

	}
}
