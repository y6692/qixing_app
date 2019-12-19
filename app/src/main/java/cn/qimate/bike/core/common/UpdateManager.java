package cn.qimate.bike.core.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.vondear.rxtools.RxAppTool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.BuildConfig;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.ClientServiceActivity;
import cn.qimate.bike.activity.EndBikeFeedBackActivity;
import cn.qimate.bike.activity.FeedbackActivity;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.util.ToastUtil;

/**
 * 应用程序更新工具包
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.1
 * @created 2012-6-29
 */
public class UpdateManager {

	private static final int DOWN_NOSDCARD = 0;
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;

	// private static final int DIALOG_TYPE_LATEST = 0;
	// private static final int DIALOG_TYPE_FAIL = 1;

	private static UpdateManager updateManager;

	private Activity mActivity;
	private Context mContext;
	// 通知对话框
	private Dialog noticeDialog;
	// 下载对话框
	private Dialog downloadDialog;
	// '已经是最新' 或者 '无法获取最新版本' 的对话框
	private Dialog latestOrFailDialog;
	// 进度条
	private ProgressBar mProgress;
	// 显示下载数值
	private TextView mProgressText;
	// 查询动画
	private LoadingDialog loadingDialog;
	// 进度值
	private int progress;
	// 下载线程
	private Thread downLoadThread;
	// 终止标记
	private boolean interceptFlag;
	// 下载包保存路径
	private String savePath = "";
	// apk保存完整路径
	private String apkFilePath = "";
	// 临时下载文件路径
	private String tmpFilePath = "";
	// 下载文件大小
	private String apkFileSize;
	// 已下载文件大小
	private String tmpFileSize;

	private String curVersionName = "";
	// private String curVersionCode;
	private Update mUpdate;
	// 更新提示对话框
	private Dialog updateDialog;

	private RelativeLayout confirmLayout;
	private RelativeLayout closeLayout;

	private int type = 0;
	private  String bikeCode = "";

	CustomDialog.Builder customBuilder;
	private CustomDialog customDialog;

	public int getType() {
		return type;
	}

	public UpdateManager setType(int type) {
		this.type = type;
		return updateManager;
	}

	public String getBikeCode() {
		return bikeCode;
	}

	public UpdateManager setBikeCode(String bikeCode) {
		this.bikeCode = bikeCode;
		return updateManager;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				mProgressText.setText(tmpFileSize + "/" + apkFileSize);
				break;
			case DOWN_OVER:
				downloadDialog.dismiss();
//				installApk();
				RxAppTool.installApp(mContext,apkFilePath);
				break;
			case DOWN_NOSDCARD:
				downloadDialog.dismiss();
				UIHelper.showToastMsg(mContext, "无法下载安装文件，请检查SD卡是否挂载", R.drawable.ic_error);
				break;
			}
		};
	};

	public static UpdateManager getUpdateManager() {
		if (updateManager == null) {
			updateManager = new UpdateManager();
		}
		updateManager.interceptFlag = false;
		return updateManager;
	}

	/**
	 * 检查App更新
	 *
	 * @param context
	 * @param isShowMsg
	 *            是否显示提示消息
	 */
	public void checkAppUpdate(Activity activity, final Context context, final int isShowMsg, final ImageView iv) {
		mContext = context;
		mActivity = activity;
		loadingDialog = new LoadingDialog(context);
		loadingDialog.setCancelable(false);
		loadingDialog.setCanceledOnTouchOutside(false);

		getCurrentVersion(context);

		Log.e("checkAppUpdate==0", "===");



		Log.e("checkAppUpdate==1", "===");

		HttpHelper.get(context, Urls.updateApp, new TextHttpResponseHandler() {		//TODO

			@Override
			public void onStart() {
				if (loadingDialog != null && !loadingDialog.isShowing()) {
					loadingDialog.setTitle("正在获取最新版本信息...");
					loadingDialog.show();
				}
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				mUpdate = new Update();
				try {
					ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
					if (0 == result.getErrcode()) {
						mUpdate = JSON.parseObject(result.getData(), Update.class);
						if (mUpdate != null) {

							ToastUtil.showMessage(context, curVersionName+"==="+mUpdate.getAppVersion());

							Log.e("checkAppUpdate==", curVersionName+"==="+mUpdate.getAppVersion());

							if (!curVersionName.equals(mUpdate.getAppVersion())) {
							    if(iv!=null) iv.setVisibility(View.VISIBLE);

								if ("2".equals(mUpdate.isForce())) {
									showDownloadDialog();
								} else {
									showNoticeDialog();
								}
							} else {
                                if(iv!=null) iv.setVisibility(View.GONE);

								if(type==0){

									if(isShowMsg==1){
										customDialog.show();
									}else if(isShowMsg==2){
										Toast.makeText(context,"当前已是最新版本",Toast.LENGTH_SHORT).show();
									}

								}else if(type==1){
									Intent intent = new Intent(context, ClientServiceActivity.class);
									intent.putExtra("bikeCode", bikeCode);
									context.startActivity(intent);
								}else if(type==2){
									Intent intent = new Intent(context, EndBikeFeedBackActivity.class);
									intent.putExtra("bikeCode",bikeCode);
									context.startActivity(intent);
								}
//								else if(type==3){
//									UIHelper.goToAct(context, EndBikeFeedbackActivity.class);
//								}

							}
						} else {
							UIHelper.ToastError(context, result.getMsg());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				UIHelper.ToastError(mContext, throwable.toString());
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
			}
		});
	}

	/**
	 * 获取当前客户端版本信息
	 */
	private void getCurrentVersion(Context context) {
		try {
			PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			curVersionName = info.versionName;

			customBuilder = new CustomDialog.Builder(context);
			customBuilder.setTitle("温馨提示").setMessage("当前已为最新版本\n"+curVersionName)
					.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			customDialog = customBuilder.create();
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * 显示版本更新通知对话框
	 */
	private void showNoticeDialog() {

//		Builder builder = new Builder(mContext);
//		builder.setTitle("软件版本更新");
//		builder.setMessage(mUpdate.getUpdateDesc());
//		builder.setOnKeyListener(keylistener).setCancelable(false);

		noticeDialog = new Dialog(mContext, R.style.Theme_AppCompat_Dialog);
		View noticeDialogView = LayoutInflater.from(mContext).inflate(R.layout.ui_update_view, null);
		noticeDialog.setContentView(noticeDialogView);
		noticeDialog.setCanceledOnTouchOutside(false);

		confirmLayout = (RelativeLayout)noticeDialogView.findViewById(R.id.ui_update_confirmLayout);
		closeLayout = (RelativeLayout)noticeDialogView.findViewById(R.id.ui_update_closeLayout);

		TextView title = (TextView)noticeDialogView.findViewById(R.id.ui_title);
		TextView version = (TextView)noticeDialogView.findViewById(R.id.ui_update_version);
		TextView text = (TextView)noticeDialogView.findViewById(R.id.ui_update_text);

		if(type==0){
			version.setText("V"+mUpdate.getAppVersion());
			text.setText(mUpdate.getUpdateDesc());
		}else{
			title.setText("温馨提示");
			version.setText("");
			text.setText("您当前软件非最新版本，会影响功能体验，请您更新软件后再试");
		}


		Log.e("showNoticeDialog===", mUpdate.getLink()+"==="+mUpdate.getUpdateDesc());

		if ("2".equals(mUpdate.isForce())) {
			showDownloadDialog();
		} else {
			confirmLayout.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					noticeDialog.dismiss();
					showDialog();
				}
			});

			closeLayout.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					noticeDialog.dismiss();
				}
			});

		}

		WindowManager windowManager =mActivity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = noticeDialog.getWindow().getAttributes();
		noticeDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
		noticeDialog.show();

		Log.e("showNoticeDialog===2", "===");
	}

	private void showDialog() {
		if (NetworkUtils.isNetWorkAvalible(mContext)) {
			if (NetworkUtils.getNetWorkType(mContext) != NetworkUtils.WIFI) {
				CustomDialog.Builder customBuilder = new CustomDialog.Builder(mContext);
				customBuilder.setTitle("温馨提示").setMessage("你当前是在非WIFI下，是否确定更新?")
						.setNegativeButton("取消", new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						}).setPositiveButton("确认", new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								showDownloadDialog();
								dialog.cancel();
							}
						});
				customBuilder.create().show();
			} else {
				showDownloadDialog();
			}
		} else {
			UIHelper.showToastMsg(mContext, "网络未连接,请连接网络!", R.drawable.ic_error);
		}
	}

	/**
	 * 显示下载对话框
	 */
	private void showDownloadDialog() {
		Builder builder = new Builder(mContext);
		builder.setTitle("正在下载新版本");
		builder.setOnKeyListener(keylistener).setCancelable(false);
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.update_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		mProgressText = (TextView) v.findViewById(R.id.update_progress_text);
		builder.setView(v);

		if ("1".equals(mUpdate.isForce())) {
			builder.setNegativeButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					interceptFlag = true;
				}
			});
			builder.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					dialog.dismiss();
					interceptFlag = true;
				}
			});
		}
		downloadDialog = builder.create();
		downloadDialog.setCanceledOnTouchOutside(false);
		downloadDialog.show();

		downloadApk();
	}

	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				String apkName = mUpdate.getAppName() + ".apk";
				String tmpApk = mUpdate.getAppVersion() + ".tmp";
				// 判断是否挂载了SD卡
				String storageState = Environment.getExternalStorageState();
				if (storageState.equals(Environment.MEDIA_MOUNTED)) {
					savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/7MA/Update/";
					File file = new File(savePath);
					if (!file.exists()) {
						file.mkdirs();
					}
					apkFilePath = savePath + apkName;
					tmpFilePath = savePath + tmpApk;
				}

				// 没有挂载SD卡，无法下载文件
				if (apkFilePath == null || apkFilePath == "") {
					mHandler.sendEmptyMessage(DOWN_NOSDCARD);
					return;
				}

				File ApkFile = new File(apkFilePath);

				// 是否已下载更新文件
				// if (ApkFile.exists()) {
				// downloadDialog.dismiss();
				// installApk();
				// return;
				// }

				// 输出临时下载文件
				File tmpFile = new File(tmpFilePath);
				FileOutputStream fos = new FileOutputStream(tmpFile);

				URL url = new URL(mUpdate.getLink());
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				// 显示文件大小格式：2个小数点显示
				DecimalFormat df = new DecimalFormat("0.00");
				// 进度条下面显示的总文件大小
				apkFileSize = df.format((float) length / 1024 / 1024) + "MB";

				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);
					count += numread;
					// 进度条下面显示的当前下载文件大小
					tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
					// 当前进度值
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成 - 将临时下载文件转成APK文件
						if (tmpFile.renameTo(ApkFile)) {
							// 通知安装
							mHandler.sendEmptyMessage(DOWN_OVER);
						}
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载

				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

	/**
	 * 下载apk
	 */
	private void downloadApk() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/**
	 * 安装apk
	 *
	 * @param url
	 */
	/**
	 * 安装apk
	 */
	private void installApk() {
		File apkfile = new File(apkFilePath);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
			i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			Uri contentUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileprovider", apkfile);
			i.setDataAndType(contentUri, "application/vnd.android.package-archive");
		}else {
			i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		}
		mContext.startActivity(i);
	}
	OnKeyListener keylistener = new OnKeyListener() {
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				return true;
			} else {
				return false;
			}
		}
	};
}
