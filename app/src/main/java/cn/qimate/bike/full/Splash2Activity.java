package cn.qimate.bike.full;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cn.jpush.android.api.JPushInterface;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.activity.CrashHandler;
import cn.qimate.bike.activity.MainActivity;
import cn.qimate.bike.base.BaseActivity;
import cn.qimate.bike.core.common.AppManager;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.Md5Helper;
import cn.qimate.bike.core.common.NetworkUtils;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.model.BannerBean;
import cn.qimate.bike.model.H5Bean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.util.ToastUtil;

@SuppressLint("NewApi")
public class Splash2Activity extends BaseActivity implements View.OnClickListener{

	public static boolean isForeground = false;

	private AMapLocationClient locationClient = null;
	private AMapLocationClientOption locationOption = new AMapLocationClientOption();
	private ImageView loadingImage;
	private LinearLayout skipLayout;
	private TextView skipTime;
	private String imageUrl;
	private String h5_title;
	private String ad_link;
	private String app_type;
	private String app_id;

	private boolean isStop = false;
	private int num = 5;
	private static long ExitTime = 0;
	private boolean isEnd = false;
	private WebView myWebView;
	private WebView webView;
	private Context context;
	String ss = "";

	private Runnable runnable;

//	private Myhandler myhandler;

	private Handler handler = new MainHandler(this);

	private boolean flag = true;
	private boolean isTz = false;
	private String action_content;
	private boolean isAdv = false;

	private Dialog dialog;

	private TextView privacyText;
	private TextView closeBtn;
	private TextView confirmBtn;

	private String title;
	private String url;
	private String title2;
	private String url2;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.main_enter2);
		context = this;

		isForeground = true;

//		android.os.Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND);

		CrashHandler.getInstance().init(this);

		if (SharedPreferencesUrls.getInstance().getBoolean("isStop", true)) {
			SharedPreferencesUrls.getInstance().putString("m_nowMac", "");
		}
		if ("".equals(SharedPreferencesUrls.getInstance().getString("m_nowMac", ""))) {
			SharedPreferencesUrls.getInstance().putBoolean("isStop", true);
			SharedPreferencesUrls.getInstance().putBoolean("switcher", false);
		}

		ToastUtil.showMessage(this, SharedPreferencesUrls.getInstance().getBoolean("isStop", true) + "===" + SharedPreferencesUrls.getInstance().getString("m_nowMac", ""));

		loadingImage = findViewById(R.id.plash_loading_main);
		skipLayout = findViewById(R.id.plash_loading_skipLayout);
		skipTime = findViewById(R.id.plash_loading_skipTime);

		loadingImage.setOnClickListener(this);

//		dialog = new Dialog(context, R.style.Theme_AppCompat_Dialog);
//		dialog = new Dialog(context, R.style.UpdateDialogStyle);
//		View dialogView = LayoutInflater.from(context).inflate(R.layout.ui_privacy_view, null);
//
//		dialog.setOnKeyListener(keylistener);
//		dialog.setContentView(dialogView);
//		dialog.setCanceledOnTouchOutside(false);
//
//
//		privacyText = (TextView)dialogView.findViewById(R.id.ui_privacy_text);
//		closeBtn = (TextView)dialogView.findViewById(R.id.ui_privacy_closeBtn);
//		confirmBtn = (TextView)dialogView.findViewById(R.id.ui_privacy_confirmBtn);
//
//		closeBtn.setOnClickListener(this);
//		confirmBtn.setOnClickListener(this);

//		if (SharedPreferencesUrls.getInstance().getBoolean("ISFRIST",true)){
//			agreement();
//		}else{
//			init();
//		}

        init();

	}

	DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener(){
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			Log.e("sa===keylistener", "==="+keyCode);

			if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
//				dialog.dismiss();
//				finishMine();
				return true;
			}else{
				return false;
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e("sa===onKeyDown", "==="+keyCode);

		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			AppManager.getAppManager().AppExit(context);
			finishMine();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	public void onClick(View view) {
//		String uid = SharedPreferencesUrls.getInstance().getString("uid","");
//		String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");

		Log.e("sa===onClick0", "ui_adv==="+app_type+"==="+h5_title+"==="+action_content);

		switch (view.getId()){
			case R.id.ui_privacy_closeBtn:
				dialog.dismiss();
				finishMine();
				break;

			case R.id.ui_privacy_confirmBtn:
				dialog.dismiss();
				SharedPreferencesUrls.getInstance().putBoolean("ISFRIST",false);
				init();
				break;

			case R.id.plash_loading_main:

				Log.e("sa===onClick", "ui_adv==="+app_type+"==="+h5_title+"==="+action_content);

				isAdv = true;

				if(!isTz){
					isTz = true;

					try{
						tz();

					}catch (Exception e){

					}
				}


//				isStop = true;


//
//				UIHelper.goWebViewAct(context, h5_title, action_content);

				break;



			default:
				break;
		}
	}

	private void initHttp() {
		Log.e("sa===banner", "===");
//		handler.sendEmptyMessageDelayed(0, 900);

		HttpHelper.get2(context, Urls.banner + 1, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				onStartCommon("正在加载");
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				Log.e("sa===banner=fail", "===" + throwable.toString());
				onFailureCommon(throwable.toString());
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, final String responseString) {
				m_myHandler.post(new Runnable() {
					@Override
					public void run() {
						try {
							Log.e("sa===banner0", responseString + "===");

							ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

							JSONArray ja_banners = new JSONArray(new JSONObject(result.getData()).getString("banners"));

							Log.e("sa===banner1", ja_banners.length() + "===" + result.data);

							if(ja_banners.length()==0){
								tz();
							}else{
								for (int i = 0; i < ja_banners.length(); i++) {
									BannerBean bean = JSON.parseObject(ja_banners.get(i).toString(), BannerBean.class);

									imageUrl = bean.getImage_url();
									h5_title = bean.getH5_title();

									action_content = bean.getAction_content();
									if(action_content.contains("?")){
										if(access_token.contains(" ")){
											action_content += "&client=android&token="+access_token.split(" ")[1];
										}else{
											action_content += "&client=android&token="+access_token;
										}
									}else{
										if(access_token.contains(" ")){
											action_content += "?client=android&token="+access_token.split(" ")[1];
										}else{
											action_content += "?client=android&token="+access_token;
										}
									}

									Log.e("sa===banner2", imageUrl+"==="+h5_title+"==="+action_content);

//                                		imagePath.add(imageUrl);

									if (imageUrl == null || "".equals(imageUrl)) {
//										loadingImage.setBackgroundResource(R.drawable.enter_bg);
									} else {
										// 加载图片
										Glide.with(context).load(imageUrl).into(loadingImage);
									}

									skipLayout.setVisibility(View.VISIBLE);
									handler.sendEmptyMessageDelayed(0, 900);

								}
							}

//                            mBanner.setBannerTitles(imageTitle);
//                            mBanner.setImages(imagePath).setOnBannerListener(MainActivity.this).start();

						} catch (Exception e) {
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());

							if (loadingDialog != null && loadingDialog.isShowing()) {
								loadingDialog.dismiss();
							}
						}

					}
				});
			}
		});
	}

	private void agreement() {

		Log.e("agreement===0", "===");

		try{
//          协议名 register注册协议 recharge充值协议 cycling_card骑行卡协议 insurance保险协议 use_car用车服务协议
			HttpHelper.get(context, Urls.agreement+"use_car", new TextHttpResponseHandler() {
				@Override
				public void onStart() {
					if (loadingDialog != null && !loadingDialog.isShowing()) {
						loadingDialog.setTitle("请稍等");
						loadingDialog.show();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					Toast.makeText(context, "fail=="+responseString, Toast.LENGTH_LONG).show();

					Log.e("agreement===fail", throwable.toString()+"==="+responseString);

					if (loadingDialog != null && loadingDialog.isShowing()){
						loadingDialog.dismiss();
					}
					UIHelper.ToastError(context, throwable.toString());
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String responseString) {
					try {
//                        Toast.makeText(context, "=="+responseString, Toast.LENGTH_LONG).show();

						Log.e("agreement===", "==="+responseString);

						ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

						H5Bean bean = JSON.parseObject(result.getData(), H5Bean.class);

						title = bean.getH5_title();
						url = bean.getH5_url();

						agreement2();

//						UIHelper.goWebViewAct(context, bean.getH5_title(), bean.getH5_url());
//                        UIHelper.goWebViewAct(context, bean.getH5_title(), Urls.agreement+"register");

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (loadingDialog != null && loadingDialog.isShowing()) {
							loadingDialog.dismiss();
						}
					}
				}


			});
		}catch (Exception e){
			Toast.makeText(context, "==="+e, Toast.LENGTH_SHORT).show();
		}

	}

	private void agreement2() {

		Log.e("agreement===0", "===");

		try{
//          协议名 register注册协议 recharge充值协议 cycling_card骑行卡协议 insurance保险协议 use_car用车服务协议 privacy隐私协议
			HttpHelper.get(context, Urls.agreement+"privacy", new TextHttpResponseHandler() {
				@Override
				public void onStart() {
					if (loadingDialog != null && !loadingDialog.isShowing()) {
						loadingDialog.setTitle("请稍等");
						loadingDialog.show();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					Toast.makeText(context, "fail=="+responseString, Toast.LENGTH_LONG).show();

					Log.e("agreement===fail", throwable.toString()+"==="+responseString);

					if (loadingDialog != null && loadingDialog.isShowing()){
						loadingDialog.dismiss();
					}
					UIHelper.ToastError(context, throwable.toString());
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String responseString) {
					try {

//                        Toast.makeText(context, "=="+responseString, Toast.LENGTH_LONG).show();

						Log.e("agreement===", "==="+responseString);

						ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

						H5Bean bean = JSON.parseObject(result.getData(), H5Bean.class);

						title2 = bean.getH5_title();
						url2 = bean.getH5_url();

//						WindowManager windowManager = getWindowManager();
//						Display display = windowManager.getDefaultDisplay();
//						WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//						lp.width = (int) (display.getWidth() * 0.8); // 设置宽度0.6
//						lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//						dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
//						dialog.getWindow().setAttributes(lp);
						dialog.show();

//						handler.sendEmptyMessageDelayed(1, 2000);

						privacyText.setMovementMethod(LinkMovementMethod.getInstance());
						String text = "亲爰的用户：感谢您信任并使用7MA出行！<br>" +
								"我们依据相关法律制定了<a href=\""+url+"\">《服务协议》</a>和<a href=\""+url2+"\">《7MA隐私政策》</a>，请您在点击同意之前仔细阅读并充分理解相关条款，其中的重点条款已为您标注，方便您了解自己的权利。<br>" +
								"我们将通过隐私政策向您说明：<br>" +
								"1、为了您享受骑行服务，我们会根据您的授权内容，收集和使用对应的必要信息（例如位置信息、相机权限等）<br>" +
								"2、我们的产品可能涉及使用第三方提供的自动化工具（如代码、接口、开发工具包（SDK）等）嵌入或接入。<br>" +
								"3、您可以对上述信息进行访问、更正、删除以及注销账户，我们也将提供专门的个人信息保护联系方式。<br>" +
								"4、未经您的授权同意，我们不会将上述信息共享给第三方或用于您未授权的其他用途。<br>" +
								"详细内容请仔细阅读《7MA隐私保护政策》";
//						Spanned text = Html.fromHtml("123<a href=\""+bean.getH5_url()+"\">隐私政策</a>456");
//						privacyText.setText(text);
						privacyText.setText(setTextLink(context, text));


//						UIHelper.goWebViewAct(context, bean.getH5_title(), bean.getH5_url());
//                        UIHelper.goWebViewAct(context, bean.getH5_title(), Urls.agreement+"register");

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (loadingDialog != null && loadingDialog.isShowing()) {
							loadingDialog.dismiss();
						}
					}
				}


			});
		}catch (Exception e){
			Toast.makeText(context, "==="+e, Toast.LENGTH_SHORT).show();
		}

	}

	public SpannableStringBuilder setTextLink(final Context context, String answerstring) {

		Log.e("sa===setTextLink", "==="+answerstring);

		if(!TextUtils.isEmpty(answerstring)) {

			//fromHtml(String source)在Android N中已经弃用，推荐使用fromHtml(String source, int
			// flags)，flags 参数说明，
			// Html.FROM_HTML_MODE_COMPACT：html块元素之间使用一个换行符分隔
			// Html.FROM_HTML_MODE_LEGACY：html块元素之间使用两个换行符分隔
			Spanned htmlString = Html.fromHtml(answerstring, Html.FROM_HTML_MODE_COMPACT);
			if(htmlString instanceof SpannableStringBuilder) {
				final SpannableStringBuilder spannablestringbuilder = (SpannableStringBuilder) htmlString;
				//取得与a标签相关的span
				Object[] objs = spannablestringbuilder.getSpans(0, spannablestringbuilder.length(), URLSpan.class);
				if(null != objs && objs.length != 0) {
					for(Object obj : objs) {


						final int start = spannablestringbuilder.getSpanStart(obj);
						final int end = spannablestringbuilder.getSpanEnd(obj);

						Log.e("sa===setTextLink1", obj.getClass() + "===" +start + "===" + end+ "===" + htmlString+ "===" + spannablestringbuilder);



						if(obj instanceof URLSpan) {
							//先移除这个span，再新添加一个自己实现的span。
							URLSpan span = (URLSpan) obj;

							Log.e("sa===setTextLink2", span + "===" + span.toString());

							final String url = span.getURL();
							spannablestringbuilder.removeSpan(obj);
							spannablestringbuilder.setSpan(new ClickableSpan() {
								@Override
								public void onClick(View widget) {
									//这里可以实现自己的跳转逻辑
									Log.e("agreement===onclick", url+"==="+start+"==="+end+"==="+((TextView)widget).getText());

									if(spannablestringbuilder.toString().substring(start, end).contains("隐私")){
										UIHelper.goWebViewAct(context, title2, url);
									}else{
										UIHelper.goWebViewAct(context, title, url);
									}


//									Toast.makeText(MainActivity.this, url, Toast.LENGTH_LONG).show();
								}
							}, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
						}
					}
				}
				return spannablestringbuilder;
			}
		}
		return new SpannableStringBuilder(answerstring);
	}

	@Override
	protected void onResume() {
		isForeground = true;
		super.onResume();
		JPushInterface.onResume(this);

		Log.e("splash===onResume", "===");

//		m_myHandler = new Handler();
//		myhandler = new Myhandler();

		if(isStop == true && isEnd == true){
			isStop = false;
			isEnd = false;

			handler.sendEmptyMessageDelayed(0, 900);
		}


	}

	private void init() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			int checkPermission = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
			if (checkPermission != PackageManager.PERMISSION_GRANTED) {
				if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
					requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
				} else {
					requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
				}
				return;
			}
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			int checkPermission = this.checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
			if (checkPermission != PackageManager.PERMISSION_GRANTED) {
				if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
					requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
				} else {
					requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
				}
				return;
			}
		}

		// <!-- 写入扩展存储，向扩展卡写入数据，用于写入离线定位数据 -->
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			int checkPermission = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			if (checkPermission != PackageManager.PERMISSION_GRANTED) {
				if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
				} else {
					requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
				}
				return;
			}
		}

		// <!-- 写入扩展存储，向扩展卡写入数据，用于写入离线定位数据 -->
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			int checkPermission = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
			if (checkPermission != PackageManager.PERMISSION_GRANTED) {
				if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
					requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
				} else {
					requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
				}
				return;
			}
		}

		if (null == locationOption) {
			locationOption = new AMapLocationClientOption();
		}
		initjpush();
//		registerMessageReceiver();
//		initLocation();

		skipLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.e("skipLayout===", "==="+isTz);

				if(!isTz){
					isTz = true;

					try{
						tz();

					}catch (Exception e){

					}
				}


			}
		});



		initHttp();

//		Countdown();
	}

	private void tz(){


		if(!isStop && !isEnd){

			isStop = true;
			isEnd = true;

			Log.e("splash===init", isAdv+"==="+getVersion()+"==="+SharedPreferencesUrls.getInstance().getBoolean("isFirst", true)+"==="+SharedPreferencesUrls.getInstance().getInt("version", 0));

			synchronized(ss){

				skipLayout.setEnabled(false);
//				UIHelper.goToAct(context, MainActivity.class);

				Intent intent = new Intent(context, MainActivity.class);
				if(isAdv){
					intent.putExtra("h5_title", h5_title);
					intent.putExtra("action_content", action_content);
				}

				startActivity(intent);

				ToastUtil.showMessage(this,  "===111" );

				finishMine();

//				if ((!SharedPreferencesUrls.getInstance().getBoolean("isFirst", true) && getVersion() == SharedPreferencesUrls.getInstance().getInt("version", 0))) {
//					UIHelper.goToAct(context, MainActivity.class);
//
//					ToastUtil.showMessage(this,  "===111" );
//				} else {
//					SharedPreferencesUrls.getInstance().putBoolean("isFirst", false);
//					SharedPreferencesUrls.getInstance().putInt("version", getVersion());
//					UIHelper.goToAct(context, EnterActivity.class);
//
//					ToastUtil.showMessage(this,  "===222" );
//				}
			}

//			UIHelper.goToAct(context, InterstitialActivity.class);

		}


	}


//	private synchronized void tz(){
//
//		if(!isStop && !isEnd){
//
//			isStop = true;
//			isEnd = true;
//
////			handler.removeMessages(0);
//
//			if ((!SharedPreferencesUrls.getInstance().getBoolean("isFirst", true) && getVersion() == SharedPreferencesUrls.getInstance().getInt("version", 0))) {
//				UIHelper.goToAct(context, MainFragment.class);
//			} else {
//				SharedPreferencesUrls.getInstance().putBoolean("isFirst", false);
//				SharedPreferencesUrls.getInstance().putInt("version", getVersion());
//				UIHelper.goToAct(context, EnterActivity.class);
//			}
//
//			finishMine();
//
//		}
//	}



	@Override
	protected void onPause() {
		isForeground = false;
		super.onPause();
		JPushInterface.onPause(this);

//		try {
//			if (internalReceiver != null) {
//				unregisterReceiver(internalReceiver);
//				internalReceiver = null;
//			}
//		} catch (Exception e) {
//			ToastUtil.showMessage(this, "eee===="+e);
//		}
	}

	@Override
	protected void onDestroy() {
		isForeground = false;

		if(dialog!=null && dialog.isShowing()){
			dialog.dismiss();
		}

		super.onDestroy();

//		handler.removeCallbacksAndMessages(null);

//		if(mMessageReceiver!=null){
//			unregisterReceiver(mMessageReceiver);
//		}


		stopLocation();

		isStop = true;
		isEnd = true;

		handler.removeMessages(0);

//		if (runnable != null) {
//			handler.removeCallbacks(runnable);
//		}


//		m_myHandler.removeCallbacks(myhandler);

	}

//	@Override
//	public void deactivate() {
//		mListener = null;
//		if (mlocationClient != null) {
//			mlocationClient.stopLocation();
//			mlocationClient.onDestroy();
//		}
//		mlocationClient = null;
//	}


	private static class MainHandler extends Handler {
//		class MainHandler extends Handler {
		WeakReference<Splash2Activity> softReference;

		public MainHandler(Splash2Activity activity) {
			softReference = new WeakReference<Splash2Activity>(activity);
		}

		@Override
		public void handleMessage(Message mes) {
			Splash2Activity splashActivity = softReference.get();

			switch (mes.what) {
				case 0:
//					time();
					splashActivity.time();
					break;

				case 1:
					splashActivity.show();
					break;
				default:
					break;
			}
		}
	}

//	protected Handler handler = new Handler(new Handler.Callback() {
//		@Override
//		public boolean handleMessage(Message mes) {
//			switch (mes.what) {
//				case 0:
//					time();
//					break;
//
//				default:
//					break;
//			}
//			return false;
//		}
//	});

	private void show(){
		dialog.show();
	}

	private void time(){

		if (num != 0) {
			skipLayout.setVisibility(View.VISIBLE);
			skipTime.setText("" + (--num) + "s");

			if(num==1){
//				skipLayout.setVisibility(View.GONE);
				tz();
			}

		} else {
//			skipLayout.setVisibility(View.GONE);
//
//			tz();

//			if (!isStop) {
//
//				isStop = true;
//				isEnd = true;

//				synchronized (this) {
//
//					if ((!SharedPreferencesUrls.getInstance().getBoolean("isFirst", true) && getVersion() == SharedPreferencesUrls.getInstance().getInt("version", 0))) {
//						UIHelper.goToAct(context, Main2Activity.class);
//					} else {
//						SharedPreferencesUrls.getInstance().putBoolean("isFirst", false);
//						SharedPreferencesUrls.getInstance().putInt("version", getVersion());
//						UIHelper.goToAct(context, EnterActivity.class);
//					}
//
//					finishMine();
//				}
//			}

		}
		if (!isEnd && !isStop) {
//			m_myHandler.sendEmptyMessage(0);
			handler.sendEmptyMessageDelayed(0, 900);
//			m_myHandler.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					time();
//				}
//			}, 900);
		}
	}








//	@Override
//	public void onBackPressed() {
//	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		Log.e("main===WebView", myWebView+"===");
//
//		if(myWebView!=null){
//			myWebView.goBack();
////			return true;
//		}else{
//			if(System.currentTimeMillis()-ExitTime > 2000){
//				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
//				ExitTime=System.currentTimeMillis();
////				return false;
//			}else{
////				return super.onKeyDown(keyCode, event);
//			}
//		}
//
//		return super.onKeyDown(keyCode, event);
//
//	}





	/**
	 * 初始化定位
	 *
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void initLocation() {
		if (NetworkUtils.getNetWorkType(context) != NetworkUtils.NONETWORK) {
			//初始化client
			locationClient = new AMapLocationClient(this.getApplicationContext());
			//设置定位参数
			locationClient.setLocationOption(getDefaultOption());
			// 设置定位监听
			locationClient.setLocationListener(locationListener);
			startLocation();

//			PostDeviceInfo(0, 0);
		} else {
			Toast.makeText(context, "暂无网络连接，请连接网络", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	/**
	 * 默认的定位参数
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private AMapLocationClientOption getDefaultOption() {
		AMapLocationClientOption mOption = new AMapLocationClientOption();
		mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
		mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
		mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
		mOption.setInterval(20 * 1000);//可选，设置定位间隔。默认为2秒
		mOption.setNeedAddress(false);//可选，设置是否返回逆地理地址信息。默认是true
		mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
		mOption.setOnceLocationLatest(true);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
		AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
		mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
		mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
		mOption.setLocationCacheEnable(false); //可选，设置是否使用缓存定位，默认为true
		return mOption;
	}

	/**
	 * 定位监听
	 */
	AMapLocationListener locationListener = new AMapLocationListener() {
		@Override
		public void onLocationChanged(AMapLocation loc) {
			if (null != loc) {
//				Toast.makeText(context,"===="+loc.getLongitude(),Toast.LENGTH_SHORT).show();

				Log.e("onLocationChanged===", loc.getLongitude()+"==="+loc.getLongitude());

				if (0.0 != loc.getLongitude() && 0.0 != loc.getLongitude() ) {

					if(flag){
						flag = false;
//						PostDeviceInfo(loc.getLatitude(), loc.getLongitude());
					}

				} else {
					CustomDialog.Builder customBuilder = new CustomDialog.Builder(Splash2Activity.this);
					customBuilder.setType(3).setTitle("温馨提示").setMessage("您需要在设置里打开定位权限！")
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
									finishMine();
								}
							}).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							Intent localIntent = new Intent();
							localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
							localIntent.setData(Uri.fromParts("package", getPackageName(), null));
							startActivity(localIntent);
							finishMine();
						}
					});
					customBuilder.create().show();
					return;
				}
			} else {
				Toast.makeText(context, "定位失败", Toast.LENGTH_SHORT).show();
				finishMine();
			}
		}
	};

	/**
	 * 开始定位
	 *
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void startLocation() {
		if (null != locationClient) {
			// 设置定位参数
			locationClient.setLocationOption(locationOption);
			// 启动定位
			locationClient.startLocation();
		}

	}

	/**
	 * 停止定位
	 *
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void stopLocation() {
		// 停止定位
		if (null != locationClient) {
			locationClient.stopLocation();
		}

	}

	/**
	 * 销毁定位
	 *
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
//	private void destroyLocation() {
//		if (null != locationClient) {
//			/**
//			 * 如果AMapLocationClient是在当前Activity实例化的，
//			 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
//			 */
//			locationClient.onDestroy();
//			locationClient = null;
//			locationOption = null;
//		}
//	}




	// 初始化极光
	private void initjpush() {
		JPushInterface.init(getApplicationContext()); // 初始化 JPush
	}




	// for receive customer msg from jpush server
//	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";

//	public void registerMessageReceiver() {
//		mMessageReceiver = new MessageReceiver();
//		IntentFilter filter = new IntentFilter();
//		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
//		filter.addAction(MESSAGE_RECEIVED_ACTION);
//		registerReceiver(mMessageReceiver, filter);
//	}
//
//	public class MessageReceiver extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
//				String messge = intent.getStringExtra(KEY_MESSAGE);
//				String extras = intent.getStringExtra(KEY_EXTRAS);
//				StringBuilder showMsg = new StringBuilder();
//				showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
//				if (extras != null && !"".equals(extras)) {
//					showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
//				}
//			}
//		}
//	}



	/**
	 * 获取版本号
	 *
	 * @return 当前应用的版本号
	 */
	public int getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			int version = info.versionCode;

			Log.e("getVersion===", "==="+version);

			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	// 提交设备信息到appinfo
	private void PostDeviceInfo(double latitude, double longitude) {
		if (NetworkUtils.getNetWorkType(context) != NetworkUtils.NONETWORK) {
			try {
				TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
				if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
					return;
				}
				String UUID = tm.getDeviceId();
//				String UUID = java.util.UUID.randomUUID().toString();

				String system_version = Build.VERSION.RELEASE;
				String device_model = new Build().MODEL;
				RequestParams params = new RequestParams();
				Md5Helper Md5Helper = new Md5Helper();
				String verify = Md5Helper.encode("7mateapp" + UUID);

				Log.e("PostDeviceInfo===0", latitude+"==="+longitude+"==="+system_version+"==="+device_model+"==="+new Build().MANUFACTURER+"==="+UUID);

				params.put("verify", verify);
				params.put("system_name", "Android");
				params.put("system_version", system_version);
				params.put("device_model", device_model);
				params.put("device_user", new Build().MANUFACTURER + device_model);
				params.put("longitude", ""+longitude);
				params.put("latitude", ""+latitude);
				params.put("UUID", UUID);
				HttpHelper.post(context, Urls.DevicePostUrl, params, new TextHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, String responseString) {
						try {
							Log.e("PostDeviceInfo===", "==="+responseString);

							ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
							if (result.getFlag().toString().equals("Success")) {

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

					}
				});
//				mThread.start();
			} catch (Exception e) {
//				showDialog();
				return;
			}
		}else{
			Toast.makeText(context,"暂无网络连接，请连接网络",Toast.LENGTH_SHORT).show();
			return;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case 0:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//						int checkPermission = this.checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
//						if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//							if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
//								requestPermissions(new String[] { Manifest.permission.READ_PHONE_STATE }, 100);
//							} else {
//								SplashActivity.this.requestPermissions(
//										new String[] { Manifest.permission.READ_PHONE_STATE }, 100);
//								return;
//							}
//						}
//					}
					init();
				} else {
					CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
					customBuilder.setType(3).setTitle("温馨提示").setMessage("您需要在设置里打开存储空间权限！")
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
									finishMine();
								}
							}).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							Intent localIntent = new Intent();
							localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
							localIntent.setData(Uri.fromParts("package", getPackageName(), null));
							startActivity(localIntent);
							finishMine();
						}
					});
					customBuilder.create().show();
				}
				return;
			case 1:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					init();
				} else {
					CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
					customBuilder.setType(3).setTitle("温馨提示").setMessage("您需要在设置里打开存储空间权限！")
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
									finishMine();
								}
							}).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							Intent localIntent = new Intent();
							localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
							localIntent.setData(Uri.fromParts("package", getPackageName(), null));
							startActivity(localIntent);
							finishMine();
						}
					});
					customBuilder.create().show();
				}
				return;
			case 100:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// <!-- 写入扩展存储，向扩展卡写入数据，用于写入离线定位数据 -->
//					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//						int checkPermission = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
//						if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//							if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//								requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE,
//										Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
//							} else {
//								SplashActivity.this.requestPermissions(
//										new String[] { Manifest.permission.READ_EXTERNAL_STORAGE,
//												Manifest.permission.WRITE_EXTERNAL_STORAGE },
//										0);
//								return;
//							}
//						}
//					}
					init();
				} else {
					CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
					customBuilder.setType(3).setTitle("温馨提示").setMessage("您需要在设置里允许设备信息权限！")
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
									finishMine();
								}
							}).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							Intent localIntent = new Intent();
							localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
							localIntent.setData(Uri.fromParts("package", getPackageName(), null));
							startActivity(localIntent);
							finishMine();
						}
					});
					customBuilder.create().show();
				}
				return;
			case 101:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					init();
				}else {
					CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
					customBuilder.setType(3).setTitle("温馨提示").setMessage("您需要在设置里定位权限！")
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
									finishMine();
								}
							}).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							Intent localIntent = new Intent();
							localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
							localIntent.setData(Uri.fromParts("package", getPackageName(), null));
							startActivity(localIntent);
							finishMine();
						}
					});
					customBuilder.create().show();
				}
				return;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}


}
