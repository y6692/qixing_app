package com.zbar.lib;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.sunshine.blelibrary.config.LockType;
import com.sunshine.blelibrary.inter.OnConnectionListener;
import com.sunshine.blelibrary.inter.OnDeviceSearchListener;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.camera.CameraPreview;
import com.zbar.lib.decode.InactivityTimer;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.CurRoadStartActivity;
import cn.qimate.bike.activity.LoginActivity;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.SystemUtil;

/**
 * 二维码扫描
 */
@SuppressLint("NewApi")
public class ScanCaptureAct extends SwipeBackActivity implements View.OnClickListener,OnConnectionListener {

	BluetoothAdapter mBluetoothAdapter;
	/**
	 * 选中的蓝牙设备
	 */
	BluetoothDevice mDevice;
	/**
	 * BLE蓝牙通信状态广播
	 */
	BroadcastReceiver mBLEStateChangeBroadcast;
	/**
	 * 蓝牙数据接收广播
	 */
	BroadcastReceiver mDataValueBroadcast;

	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;
	private CameraManager mCameraManager;

	private InactivityTimer inactivityTimer;

	private FrameLayout scanPreview;
	private RelativeLayout scanContainer;
	private RelativeLayout scanCropView;
	private ImageView scanLine;

	private Rect mCropRect = null;
	private boolean previewing = true;
	private ImageScanner mImageScanner = null;

	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.50f;
	private boolean vibrate;

	private TextView cancle;
	private TextView bikeNunBtn;
	private LoadingDialog loadingDialog;

	volatile String m_nowMac = "";
	private String codenum = "";
	// 输入法
	private Dialog dialog;
	private EditText bikeNumEdit;
	private Button positiveButton,negativeButton;
	private Button lightBtn;
	private boolean flag = true;
	private int Tag = 0;

	private String quantity = "";
	private int num = 15;//扫描时间
	private boolean isStop = false;

	static {
		System.loadLibrary("iconv");
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanner);
		registerReceiver(broadcastReceiver, com.sunshine.blelibrary.config.Config.initFilter());
		GlobalParameterUtils.getInstance().setLockType(LockType.MTS);
		BaseApplication.getInstance().getIBLE().close();
		BaseApplication.getInstance().getIBLE().disconnect();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		findViewById();
	}

	@Override
	protected void onResume() {
		super.onResume();

		Toast.makeText(this, "capture====", Toast.LENGTH_SHORT).show();

//		try {
//			if (internalReceiver != null) {
//				unregisterReceiver(internalReceiver);
//			}
//		} catch (Exception e) {
//			Toast.makeText(this, "eee===="+e, Toast.LENGTH_SHORT).show();
//		}

		initViews();
		playBeep = true;
		isStop = false;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	private void findViewById() {

		loadingDialog = new LoadingDialog(ScanCaptureAct.this);
		loadingDialog.setCancelable(false);
		loadingDialog.setCanceledOnTouchOutside(false);

		scanPreview =  (FrameLayout) findViewById(R.id.capture_preview);
		scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
		scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
		scanLine = (ImageView) findViewById(R.id.capture_scan_line);
		cancle = (TextView)findViewById(R.id.loca_show_btncancle);
		bikeNunBtn = (TextView)findViewById(R.id.loca_show_btnBikeNum);
		lightBtn = (Button)findViewById(R.id.activity_qr_scan_lightBtn);

		dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog);
		View dialogView = LayoutInflater.from(this).inflate(R.layout.pop_circles_menu, null);
		dialog.setContentView(dialogView);
		dialog.setCanceledOnTouchOutside(false);

		bikeNumEdit = (EditText)dialogView.findViewById(R.id.pop_circlesMenu_bikeNumEdit);
		positiveButton = (Button)dialogView.findViewById(R.id.pop_circlesMenu_positiveButton);
		negativeButton = (Button)dialogView.findViewById(R.id.pop_circlesMenu_negativeButton);

		cancle.setOnClickListener(this);
		bikeNunBtn.setOnClickListener(this);
		positiveButton.setOnClickListener(this);
		negativeButton.setOnClickListener(this);
		lightBtn.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		String uid = SharedPreferencesUrls.getInstance().getString("uid","");
		String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
		switch (v.getId()){
			case R.id.loca_show_btncancle:
				scrollToFinishActivity();
				break;
			case R.id.loca_show_btnBikeNum:
				isStop = true;
				if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
					Toast.makeText(ScanCaptureAct.this,"请先登录账号",Toast.LENGTH_SHORT).show();
					UIHelper.goToAct(ScanCaptureAct.this, LoginActivity.class);
				}else {
					//关闭相机
					releaseCamera();
					mCameraManager.closeDriver();

					WindowManager windowManager = getWindowManager();
					Display display = windowManager.getDefaultDisplay();
					WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
					lp.width = (int) (display.getWidth() * 0.8); // 设置宽度0.6
					lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
					dialog.getWindow().setAttributes(lp);
					dialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
					dialog.show();
					InputMethodManager manager = (InputMethodManager) getSystemService(
							INPUT_METHOD_SERVICE);
					manager.showSoftInput(v, InputMethodManager.RESULT_SHOWN);
					manager.toggleSoftInput(InputMethodManager.SHOW_FORCED,
							InputMethodManager.HIDE_IMPLICIT_ONLY);
				}
				break;
			case R.id.pop_circlesMenu_positiveButton:
				String bikeNum = bikeNumEdit.getText().toString().trim();
				if (bikeNum == null || "".equals(bikeNum)){
					Toast.makeText(this,"请输入单车编号",Toast.LENGTH_SHORT).show();
					return;
				}
				InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				manager.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				Tag = 1;
				useBike(bikeNum);
				break;
			case R.id.pop_circlesMenu_negativeButton:
				BaseApplication.getInstance().getIBLE().close();
				BaseApplication.getInstance().getIBLE().disconnect();
				InputMethodManager manager1= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				manager1.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				break;
			case R.id.activity_qr_scan_lightBtn:
				if (flag) {
					flag = false;
					lightBtn.setText("关闭手电筒");
					mCameraManager.openLight();
				} else {
					flag = true;
					lightBtn.setText("开启手电筒");
					mCameraManager.offLight();
				}
				break;
			default:
				break;
		}
	}



	private void initViews() {

		inactivityTimer = new InactivityTimer(this);

		mImageScanner = new ImageScanner();
		mImageScanner.setConfig(0, Config.X_DENSITY, 3);
		mImageScanner.setConfig(0, Config.Y_DENSITY, 3);

		autoFocusHandler = new Handler();
		mCameraManager = new CameraManager(this);
		try {
			mCameraManager.openDriver();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//调整扫描框大小,自适应屏幕
		Display display = this.getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
	    int height = display.getHeight();
			    
		RelativeLayout.LayoutParams linearParams =  (RelativeLayout.LayoutParams)scanCropView.getLayoutParams();
		linearParams.height = (int) (width*0.65);
		linearParams.width = (int) (width*0.65);
		scanCropView.setLayoutParams(linearParams);
		//**
		
		mCamera = mCameraManager.getCamera();
		mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
		scanPreview.addView(mPreview);

		TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f,
				TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
		mAnimation.setDuration(1500);
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.REVERSE);
		mAnimation.setInterpolator(new LinearInterpolator());
		scanLine.setAnimation(mAnimation);
	}

	public void onPause() {
		super.onPause();
		releaseCamera();
		mCameraManager.closeDriver();
	}

	private void releaseCamera() {
		if (mCamera != null) {
			previewing = false;
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		isStop = true;
		super.onDestroy();
//		PublicWay.BLE_CONNECT_STATUS = false;
//		BLEService.clearCmdQueue(this);
//		stopService(new Intent(ScanCaptureAct.this, BLEService.class));

		if (broadcastReceiver != null) {
			unregisterReceiver(broadcastReceiver);
			broadcastReceiver = null;
		}
		m_myHandler.removeCallbacksAndMessages(null);
	}

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (previewing)
				mCamera.autoFocus(autoFocusCB);
		}
	};

	PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Size size = camera.getParameters().getPreviewSize();

			// 这里需要将获取的data翻转一下，因为相机默认拿的的横屏的数据
			byte[] rotatedData = new byte[data.length];
			for (int y = 0; y < size.height; y++) {
				for (int x = 0; x < size.width; x++)
					rotatedData[x * size.height + size.height - y - 1] = data[x
							+ y * size.width];
			}

			// 宽高也要调整
			int tmp = size.width;
			size.width = size.height;
			size.height = tmp;

			initCrop();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(rotatedData);
			barcode.setCrop(mCropRect.left, mCropRect.top, mCropRect.width(),
					mCropRect.height());

			int result = mImageScanner.scanImage(barcode);
			String resultStr = null;

			if (result != 0) {
				SymbolSet syms = mImageScanner.getResults();
				for (Symbol sym : syms) {
					resultStr = sym.getData();
				}
			}

			if (!TextUtils.isEmpty(resultStr)) {

				inactivityTimer.onActivity();
				playBeepSoundAndVibrate();

				previewing = false;
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();

				releaseCamera();
				Tag = 0;
				useBike(resultStr);
			}
		}
	};

	private void useBike(String result){

		String uid = SharedPreferencesUrls.getInstance().getString("uid","");
		String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
		if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
			Toast.makeText(ScanCaptureAct.this,"请先登录账号",Toast.LENGTH_SHORT).show();
			UIHelper.goToAct(ScanCaptureAct.this, LoginActivity.class);
		}else {
			RequestParams params = new RequestParams();
			params.put("uid",uid);
			params.put("access_token",access_token);
			params.put("tokencode",result);
			params.put("latitude",SharedPreferencesUrls.getInstance().getString("latitude",""));
			params.put("longitude",SharedPreferencesUrls.getInstance().getString("longitude",""));
			params.put("telprama","手机型号：" + SystemUtil.getSystemModel()
					+", Android系统版本号："+SystemUtil.getSystemVersion());
			HttpHelper.post(ScanCaptureAct.this, Urls.useCar, params, new TextHttpResponseHandler() {
				@Override
				public void onStart() {
					if (loadingDialog != null && !loadingDialog.isShowing()) {
						loadingDialog.setTitle("正在连接");
						loadingDialog.show();
					}
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							if (!isStop){
								if (loadingDialog != null && loadingDialog.isShowing()) {
									loadingDialog.dismiss();
								}
								Toast.makeText(ScanCaptureAct.this,"请重启软件，开启定位服务,输编号用车",5 * 1000).show();
								scrollToFinishActivity();
							}
						}
					}, 30 * 1000);
				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					if (loadingDialog != null && loadingDialog.isShowing()){
						loadingDialog.dismiss();
					}
					UIHelper.ToastError(ScanCaptureAct.this, throwable.toString());
				}
				@Override
				public void onSuccess(int statusCode, Header[] headers, String responseString) {
					try {
						ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
						if (result.getFlag().equals("Success")) {
							JSONObject jsonObject = new JSONObject(result.getData());
							if ("1".equals(jsonObject.getString("type"))){
								if (loadingDialog != null && loadingDialog.isShowing()){
									loadingDialog.dismiss();
								}
								//机械锁
								UIHelper.goToAct(ScanCaptureAct.this, CurRoadStartActivity.class);
								scrollToFinishActivity();
							}else {
								codenum = jsonObject.getString("codenum");
								m_nowMac = jsonObject.getString("macinfo");
								if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
									Toast.makeText(ScanCaptureAct.this, "您的设备不支持蓝牙4.0", Toast.LENGTH_SHORT).show();
									scrollToFinishActivity();
								}
								//蓝牙锁
								BluetoothManager bluetoothManager =
										(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

								mBluetoothAdapter = bluetoothManager.getAdapter();

								if (mBluetoothAdapter == null) {
									Toast.makeText(ScanCaptureAct.this, "获取蓝牙失败", Toast.LENGTH_SHORT).show();
									scrollToFinishActivity();
									return;
								}
								if (!mBluetoothAdapter.isEnabled()) {
									Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
									startActivityForResult(enableBtIntent, 188);
								}else{
									connect();
								}
							}
						} else {
							Toast.makeText(ScanCaptureAct.this,result.getMsg(),Toast.LENGTH_SHORT).show();
							if (loadingDialog != null && loadingDialog.isShowing()){
								loadingDialog.dismiss();
							}
							scrollToFinishActivity();
						}
					} catch (Exception e) {
						e.printStackTrace();
						if (loadingDialog != null && loadingDialog.isShowing()){
							loadingDialog.dismiss();
						}
					}
				}
			});
		}
	}
	private void addOrderbluelock(){
		String uid = SharedPreferencesUrls.getInstance().getString("uid","");
		String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
		if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
			Toast.makeText(ScanCaptureAct.this,"请先登录账号",Toast.LENGTH_SHORT).show();
			UIHelper.goToAct(ScanCaptureAct.this, LoginActivity.class);
		}else {
			RequestParams params = new RequestParams();
			params.put("uid",uid);
			params.put("access_token",access_token);
			params.put("codenum",codenum);
			if (quantity != null && !"".equals(quantity)){
				params.put("quantity",quantity);
			}
			HttpHelper.post(ScanCaptureAct.this, Urls.addOrderbluelock, params, new TextHttpResponseHandler() {
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					if (loadingDialog != null && loadingDialog.isShowing()){
						loadingDialog.dismiss();
					}
					UIHelper.ToastError(ScanCaptureAct.this, throwable.toString());
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String responseString) {
					try {
						ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
						if (result.getFlag().equals("Success")) {

						} else {
							Toast.makeText(ScanCaptureAct.this,result.getMsg(),Toast.LENGTH_SHORT).show();
							if (loadingDialog != null && loadingDialog.isShowing()){
								loadingDialog.dismiss();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						if (loadingDialog != null && loadingDialog.isShowing()){
							loadingDialog.dismiss();
						}
					}
				}
			});
		}
	}

	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};

	/**
	 * 初始化截取的矩形区域
	 */
	private void initCrop() {
		int cameraWidth = mCameraManager.getCameraResolution().y;
		int cameraHeight = mCameraManager.getCameraResolution().x;

		/** 获取布局中扫描框的位置信息 */
		int[] location = new int[2];
		scanCropView.getLocationInWindow(location);

		int cropLeft = location[0];
		int cropTop = location[1] - getStatusBarHeight();

		int cropWidth = scanCropView.getWidth();
		int cropHeight = scanCropView.getHeight();

		/** 获取布局容器的宽高 */
		int containerWidth = scanContainer.getWidth();
		int containerHeight = scanContainer.getHeight();

		/** 计算最终截取的矩形的左上角顶点x坐标 */
		int x = cropLeft * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的左上角顶点y坐标 */
		int y = cropTop * cameraHeight / containerHeight;

		/** 计算最终截取的矩形的宽度 */
		int width = cropWidth * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的高度 */
		int height = cropHeight * cameraHeight / containerHeight;

		/** 生成最终的截取的矩形 */
		mCropRect = new Rect(x, y, width + x, height + y);
	}

	private int getStatusBarHeight() {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			return getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}


	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK)
		{
			switch (requestCode) {
				case 288:{
					break;
				}
				case 188:{
//					handler.sendEmptyMessageDelayed(1, 1000);
					if (!TextUtils.isEmpty(m_nowMac)) {
						connect();
					}
					break;
				}
				default:{
					break;
				}
			}
		}else if( requestCode == 188){
			Toast.makeText(this, "需要打开蓝牙", Toast.LENGTH_SHORT).show();
			scrollToFinishActivity();
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finishMine();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

//	Handler handler = new Handler() {
//		public void handleMessage(Message msg) {
//			if (msg.what == 1) {
//				if (num != 0) {
//					--num;
//				} else {
//					isStop = true;
//					if (loadingDialog != null && loadingDialog.isShowing()){
//						loadingDialog.dismiss();
//					}
//					PublicWay.BLE_CONNECT_STATUS = false;
//					BLEService.clearCmdQueue(ScanCaptureAct.this);
//					stopService(new Intent(ScanCaptureAct.this, BLEService.class));
//					Toast.makeText(ScanCaptureAct.this,"请开启手机定位并重试",2 * 1000).show();
//					scrollToFinishActivity();
//				}
//				if (!isStop) {
//					handler.sendEmptyMessageDelayed(1, 1000);
//				}
//			}
//		};
//	};

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mBLEStateChangeBroadcast != null) {
			unregisterReceiver(mBLEStateChangeBroadcast);
		}
		if (mDataValueBroadcast != null) {
			unregisterReceiver(mDataValueBroadcast);
		}
	}

	/**
	 * 链接设备
	 * 1. 先进行搜索设备，匹配mac地址是否能搜索到
	 * 2. 搜索到就链接设备，取消倒计时；
	 * 3. 搜索不到就执行直接连接设备
	 */
	protected void connect() {
		BaseApplication.getInstance().getIBLE().stopScan();
		m_myHandler.sendEmptyMessage(0x99);
		BaseApplication.getInstance().getIBLE().startScan(new OnDeviceSearchListener() {
			@Override
			public void onScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
				if (device==null||TextUtils.isEmpty(device.getAddress()))return;
				if (m_nowMac.equalsIgnoreCase(device.getAddress())){
					isStop = true;
					m_myHandler.removeMessages(0x99);
					BaseApplication.getInstance().getIBLE().stopScan();
					BaseApplication.getInstance().getIBLE().connect(m_nowMac, ScanCaptureAct.this);
				}
			}
		});
	}

	@Override
	public void onTimeOut() {

	}

	@Override
	public void onDisconnect(int state) {
		m_myHandler.sendEmptyMessageDelayed(0, 1000);
	}
	@Override
	public void onServicesDiscovered(String name, String address) {
		isStop = true;
		getToken();
	}
	/**
	 * 获取token
	 */
	private void getToken() {
		m_myHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				BaseApplication.getInstance().getIBLE().getToken();
			}
		}, 500);
	}
	/**
	 * 广播
	 * */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String data = intent.getStringExtra("data");
			switch (action) {
				case com.sunshine.blelibrary.config.Config.TOKEN_ACTION:
					isStop = true;
					m_myHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							BaseApplication.getInstance().getIBLE().getBattery();
						}
					}, 1000);
					if (null != loadingDialog && loadingDialog.isShowing()) {
						loadingDialog.dismiss();
						loadingDialog = null;
					}
					CustomDialog.Builder customBuilder = new CustomDialog.Builder(ScanCaptureAct.this);
					if (0 == Tag){
						customBuilder.setMessage("扫码成功,是否开锁?");
					}else {
						customBuilder.setMessage("输号成功,是否开锁?");
					}
					customBuilder.setTitle("温馨提示").setNegativeButton("取消", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							scrollToFinishActivity();
						}
					}).setPositiveButton("确认", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							if (loadingDialog != null && !loadingDialog.isShowing()) {
								loadingDialog.setTitle("正在开锁");
								loadingDialog.show();
							}
							addOrderbluelock();
						}
					}).setHint(false);
					customBuilder.create().show();
					break;
				case com.sunshine.blelibrary.config.Config.BATTERY_ACTION:
					if (!TextUtils.isEmpty(data)) {
						quantity = String.valueOf(Integer.parseInt(data, 16));
					}else {
						quantity = "";
					}
					break;
				case com.sunshine.blelibrary.config.Config.OPEN_ACTION:
					if (loadingDialog != null && loadingDialog.isShowing()){
						loadingDialog.dismiss();
					}
					if (TextUtils.isEmpty(data)) {
						Toast.makeText(context,"开锁失败,请重试",Toast.LENGTH_SHORT).show();
						scrollToFinishActivity();
					} else {
						Toast.makeText(context,"恭喜您,开锁成功!",Toast.LENGTH_SHORT).show();
						UIHelper.goToAct(ScanCaptureAct.this,CurRoadStartActivity.class);
						scrollToFinishActivity();
					}
					break;
				case com.sunshine.blelibrary.config.Config.CLOSE_ACTION:
					if (TextUtils.isEmpty(data)) {
					} else {
					}
					break;
				case com.sunshine.blelibrary.config.Config.LOCK_STATUS_ACTION:
					if (TextUtils.isEmpty(data)) {

					} else {

					}
					break;
				case com.sunshine.blelibrary.config.Config.LOCK_RESULT:
					if (TextUtils.isEmpty(data)) {
					} else {

					}
					break;
			}
		}
	};

	Handler m_myHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message mes) {
			switch (mes.what) {
				case 0:
					BaseApplication.getInstance().getIBLE().connect(m_nowMac, ScanCaptureAct.this);
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 9:
					break;
				case 0x99://搜索超时
					BaseApplication.getInstance().getIBLE().connect(m_nowMac,
							ScanCaptureAct.this);
					break;
				default:
					break;
			}
			return false;
		}
	});
}
