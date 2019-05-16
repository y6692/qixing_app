package cn.qimate.bike.core.common;

import android.content.Context;
import android.content.Intent;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.InsureanceActivity;
import cn.qimate.bike.activity.InviteCodeActivity;
import cn.qimate.bike.activity.MainActivity;
import cn.qimate.bike.activity.MonthlyCardActivity;
import cn.qimate.bike.activity.WebviewActivity;
import cn.qimate.bike.base.BaseActivity;
import cn.qimate.bike.core.widget.ConfirmDialog;
import cn.qimate.bike.fragment.BikeFragment;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

public class UIHelper {

	private static ConfirmDialog confirmDialog;

	public static void ToastMessageClose(SwipeBackActivity baseActivity, Context context, String msg, int ResID) {

		openDialogToastMsg(baseActivity, context, ResID, msg);
	}

	public static void ToastMessageClose(BaseActivity baseActivity, Context context, String msg, int ResID) {

		openDialogToastMsg(baseActivity, context, ResID, msg);
	}

	// 提醒
	public static void showToastMsg(Context context, String msg, int drawID) {
		openDialogOneMsg(context, drawID, msg);
	}

	/**
	 * 关闭当前页,跳转页面
	 */
	public static void ToastGoActClose(SwipeBackActivity baseActivity, Context context, Class<?> clz, String msgStr,
			int drawID) {

		openDialogGoActColse(baseActivity, context, clz, drawID, msgStr);
	};

	/**
	 * 关闭所有前页,跳转登录页面
	 */
	public static void ToastGoLoginClose(SwipeBackActivity baseActivity, Context context, String msgStr, int drawID) {

		openDialogGoLoginColse(baseActivity, context, drawID, msgStr);
	};

	/**
	 * 关闭所有前页,跳转登录页面
	 */
	public static void ToastGoLoginClose(BaseActivity baseActivity, Context context, String msgStr, int drawID) {

		openDialogGoLoginColse(baseActivity, context, drawID, msgStr);
	};

	/**
	 * 关闭当前页,跳转页面
	 */
	public static void ToastGoActClose(BaseActivity baseActivity, Context context, Class<?> clz, String msgStr,
			int drawID) {

		openDialogGoActColse(baseActivity, context, clz, drawID, msgStr);
	};

	/**
	 * 不关闭当前页,跳转页面
	 */
	public static void ToastGoAc(Context context, Class<?> clz, String msgStr, int drawID) {

		openDialogGoAct(context, clz, drawID, msgStr);
	};

	/**
	 * 弹出Toast错误消息
	 * 
	 * @param msg
	 */
	public static void ToastError(Context context, String msg) {
		if (context == null || msg == null || "".equals(msg))
			return;
		if (!NetworkUtils.isNetWorkAvalible(context)) {
			openDialogOneMsg(context, R.drawable.ic_error, "无网络连接，请先打开网络连接");
		} else if (msg.contains("SocketTimeoutException") || msg.contains("ConnectTimeoutException")) {
//			openDialogOneMsg(context, R.drawable.ic_error, "网速不给力哦！");
			openDialogOneMsg(context, R.drawable.ic_error, "请求超时，请稍后再试");
		} else {
			openDialogOneMsg(context, R.drawable.ic_error, "请求失败");
		}
	}

	/**
	 * 不关闭当前页
	 */
	public static void openDialogOneMsg(Context context, int drawID, String msgStr) {
		if (confirmDialog != null) {
			if (confirmDialog.isShowing()) {
				confirmDialog.dismiss();
			}
			confirmDialog = null;
		}
		confirmDialog = new ConfirmDialog(context, drawID, msgStr);
		confirmDialog.show();
		confirmDialog.setCanceledOnTouchOutside(false);
		confirmDialog.setCancelable(false);
		confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
			@Override
			public void doConfirm() {
				confirmDialog.dismiss();
			}

			@Override
			public void doCancel() {
				confirmDialog.dismiss();
			}
		});
	}

	/**
	 * 关闭当前页
	 */
	public static void openDialogToastMsg(final SwipeBackActivity baseActivity, final Context context, int drawID,
			String msgStr) {
		if (confirmDialog != null) {
			if (confirmDialog.isShowing()) {
				confirmDialog.dismiss();
			}
			confirmDialog = null;
		}
		confirmDialog = new ConfirmDialog(context, drawID, msgStr);
		confirmDialog.show();
		confirmDialog.setCanceledOnTouchOutside(false);
		confirmDialog.setCancelable(false);
		confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
			@Override
			public void doConfirm() {
				confirmDialog.dismiss();
				baseActivity.scrollToFinishActivity();
			}

			@Override
			public void doCancel() {
				confirmDialog.dismiss();
			}
		});
	}

	/**
	 * 关闭当前页
	 */
	public static void openDialogToastMsg(final BaseActivity baseActivity, final Context context, int drawID,
			String msgStr) {
		if (confirmDialog != null) {
			if (confirmDialog.isShowing()) {
				confirmDialog.dismiss();
			}
			confirmDialog = null;
		}
		confirmDialog = new ConfirmDialog(context, drawID, msgStr);
		confirmDialog.show();
		confirmDialog.setCanceledOnTouchOutside(false);
		confirmDialog.setCancelable(false);
		confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
			@Override
			public void doConfirm() {
				confirmDialog.dismiss();
				baseActivity.finishMine();
			}

			@Override
			public void doCancel() {
				confirmDialog.dismiss();
			}
		});
	}

	/**
	 * 关闭当前页,跳转页面
	 */

	public static void openDialogGoActColse(final SwipeBackActivity baseActivity, final Context context,
			final Class<?> clz, int drawID, String msgStr) {
		if (confirmDialog != null) {
			if (confirmDialog.isShowing()) {
				confirmDialog.dismiss();
			}
			confirmDialog = null;
		}
		confirmDialog = new ConfirmDialog(context, drawID, msgStr);
		confirmDialog.show();
		confirmDialog.setCanceledOnTouchOutside(false);
		confirmDialog.setCancelable(false);
		confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
			@Override
			public void doConfirm() {
				confirmDialog.dismiss();
				goToAct(context, clz);
				baseActivity.scrollToFinishActivity();
			}

			@Override
			public void doCancel() {
				confirmDialog.dismiss();
			}
		});
	}

	/**
	 * 关闭所有页面,跳转登录页面
	 */

	public static void openDialogGoLoginColse(final SwipeBackActivity baseActivity, final Context context, int drawID,
			String msgStr) {
		if (confirmDialog != null) {
			if (confirmDialog.isShowing()) {
				confirmDialog.dismiss();
			}
			confirmDialog = null;
		}
		confirmDialog = new ConfirmDialog(context, drawID, msgStr);
		confirmDialog.show();
		confirmDialog.setCanceledOnTouchOutside(false);
		confirmDialog.setCancelable(false);
		confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
			@Override
			public void doConfirm() {
//				confirmDialog.dismiss();
//				Intent intent = new Intent(context, LoginActivity.class);
//				intent.putExtra("Tag", 1);
//				context.startActivity(intent);
//				((Activity) context).overridePendingTransition(R.anim.push_rigth_in, R.anim.push_left_out);
			}

			@Override
			public void doCancel() {
				confirmDialog.dismiss();
			}
		});
	}

	/**
	 * 关闭所有页面,跳转登录页面
	 */

	public static void openDialogGoLoginColse(final BaseActivity baseActivity, final Context context, int drawID,
			String msgStr) {
		if (confirmDialog != null) {
			if (confirmDialog.isShowing()) {
				confirmDialog.dismiss();
			}
			confirmDialog = null;
		}
		confirmDialog = new ConfirmDialog(context, drawID, msgStr);
		confirmDialog.show();
		confirmDialog.setCanceledOnTouchOutside(false);
		confirmDialog.setCancelable(false);
		confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
			@Override
			public void doConfirm() {
				confirmDialog.dismiss();
//				Intent intent = new Intent(context, LoginActivity.class);
//				intent.putExtra("Tag", 1);
//				context.startActivity(intent);
//				((Activity) context).overridePendingTransition(R.anim.push_rigth_in, R.anim.push_left_out);
			}

			@Override
			public void doCancel() {
				confirmDialog.dismiss();
			}
		});
	}

	/**
	 * 关闭当前页,跳转页面
	 */

	public static void openDialogGoActColse(final BaseActivity baseActivity, final Context context, final Class<?> clz,
			int drawID, String msgStr) {
		if (confirmDialog != null) {
			if (confirmDialog.isShowing()) {
				confirmDialog.dismiss();
			}
			confirmDialog = null;
		}
		confirmDialog = new ConfirmDialog(context, drawID, msgStr);
		confirmDialog.show();
		confirmDialog.setCanceledOnTouchOutside(false);
		confirmDialog.setCancelable(false);
		confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
			@Override
			public void doConfirm() {
				confirmDialog.dismiss();
				goToAct(context, clz);
				baseActivity.finishMine();
				;
			}

			@Override
			public void doCancel() {
				confirmDialog.dismiss();
			}
		});
	}

	/**
	 * 不关闭关闭当前页,跳转页面
	 */

	public static void openDialogGoAct(final Context context, final Class<?> clz, int drawID, String msgStr) {
		if (confirmDialog != null) {
			if (confirmDialog.isShowing()) {
				confirmDialog.dismiss();
			}
			confirmDialog = null;
		}
		confirmDialog = new ConfirmDialog(context, drawID, msgStr);
		confirmDialog.show();
		confirmDialog.setCanceledOnTouchOutside(false);
		confirmDialog.setCancelable(false);
		confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
			@Override
			public void doConfirm() {
				confirmDialog.dismiss();
				goToAct(context, clz);
			}

			@Override
			public void doCancel() {
				confirmDialog.dismiss();
			}
		});
	}

	// 页面跳转
	public static void goToAct(Context context, Class<?> clz) {

		Intent intent = new Intent(context, clz);
		context.startActivity(intent);
	}
	
	// 广告跳转
	public static void bannerGoAct(Context context, String app_type, String app_id, String link) {

		if ((app_type == null || "".equals(app_type))) {
			return;
		}
		switch (Integer.parseInt(app_type)) {
		case 1:
			if (!"#".equals(link) && link != null && !"".equals(link)) {
				Intent intent = new Intent(context, WebviewActivity.class);
				intent.putExtra("link", link);
				intent.putExtra("title", "广告详情");
				context.startActivity(intent);
			}
			break;
		case 2:
			Intent intent = new Intent(context, InviteCodeActivity.class);
			intent.putExtra("isBack",false);
			context.startActivity(intent);
			break;
		case 3:
			Intent intent1 = new Intent(context, InsureanceActivity.class);
			intent1.putExtra("isBack",false);
			context.startActivity(intent1);
			break;
		case 4:
			if (!"#".equals(link) && link != null && !"".equals(link)) {
				Intent intent2 = new Intent(context, MonthlyCardActivity.class);
				intent2.putExtra("link", link);
				intent2.putExtra("title", "活动详情");
//				intent2.putExtra("title", "拼手气 拿手机");
//				intent2.putExtra("isBack",false);
				context.startActivity(intent2);
			}

			break;
		default:
			if (!BikeFragment.isForeground){
				UIHelper.goToAct(context, MainActivity.class);
			}
			break;
		}
	}

	/**
	 *
	 * 跳转H5界面
	 *
	 * */
	public static void goWebViewAct(Context context,String title, String link){

		Intent intent = new Intent(context, WebviewActivity.class);
		intent.putExtra("title",title);
		intent.putExtra("link",link);
		context.startActivity(intent);
	}
}
