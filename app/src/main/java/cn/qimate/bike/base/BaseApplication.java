package cn.qimate.bike.base;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.hubcloud.adhubsdk.AdHub;
import com.sofi.blelocker.library.BluetoothContext;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.config.LockType;
import com.sunshine.blelibrary.impl.AndroidBle;
import com.sunshine.blelibrary.inter.IBLE;
import com.sunshine.blelibrary.inter.OnDeviceSearchListener;
import com.sunshine.blelibrary.service.BLEService;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;
import com.umeng.socialize.PlatformConfig;
import com.vondear.rxtools.RxTool;

import java.io.File;

import cn.jpush.android.api.JPushInterface;
import cn.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import cn.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import cn.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import cn.nostra13.universalimageloader.core.DisplayImageOptions;
import cn.nostra13.universalimageloader.core.ImageLoader;
import cn.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import cn.nostra13.universalimageloader.core.assist.QueueProcessingType;
import cn.nostra13.universalimageloader.core.download.BaseImageDownloader;
import cn.nostra13.universalimageloader.utils.StorageUtils;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.lock.utils.ToastUtils;

/**
 * 自定义Application
 * 
 * @author wutao
 *
 */
public class BaseApplication extends Application {
	public  static Context context;
	private static BaseApplication app;
	private PackageInfo packageInfo;
	private IBLE bleManager;
	private boolean debug = false;
	private boolean test = false;
	private boolean testLog = false;

	public static  BaseApplication getInstance() {
		return app;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isTest() {
		return test;
	}

	public void setTest(boolean test) {
		this.test = test;
	}

	public boolean isTestLog() {
		return testLog;
	}

	public void setTestLog(boolean testLog) {
		this.testLog = testLog;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		app = this;

		BluetoothContext.set(this);

		setDebug(false);
//		setTestLog(true);
		setTestLog(false);
//		setTest(true);
		setTest(false);

//		registerReceiver(broadcastReceiver2, Config.initFilter());
//		GlobalParameterUtils.getInstance().setLockType(LockType.MTS);

		AdHub.initialize(this, "2597");

		bleManager = new AndroidBle(this);
		RxTool.init(getApplicationContext());
		ToastUtils.init(getApplicationContext());
		// registerUncaughtExceptionHandler();
		initImageLoader(getApplicationContext());
		try {
			packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		JPushInterface.setDebugMode(false); // 设置开启日志,发布时请关闭日志
		JPushInterface.init(this); // 初始化 JPush

		if (SharedPreferencesUrls.getInstance().getInt("versionCode", 0) != packageInfo.versionCode) {
			SharedPreferencesUrls.getInstance().putBoolean("isFirst", true);
			SharedPreferencesUrls.getInstance().putInt("versionCode", packageInfo.versionCode);
		} else {
			SharedPreferencesUrls.getInstance().putBoolean("isFirst", false);
		}
		ToastUtils.init(getApplicationContext());



	}


	// 各个平台的配置，建议放在全局Application或者程序入口
	{
		// 微信 wx12342956d1cab4f9,a5ae111de7d9ea137e88a5e02c07c94d
		PlatformConfig.setWeixin("wx86d98ec252f67d07", "4e4aafa841609025036b0367d30ef052");
		// QQ
		PlatformConfig.setQQZone("1105975305", "wkfWoPW8aYGwdvcL");
	}

	private static void initImageLoader(Context context) {

		File cacheDir = StorageUtils.getCacheDirectory(context);

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.memoryCacheExtraOptions(480, 800) // maxwidth, max
				// height，即保存的每个缓存文件的最大长宽
				.threadPoolSize(3)// 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // 你可以通过自己的内存缓存实现
				.memoryCacheSize(2 * 1024 * 1024).diskCacheSize(50 * 1024 * 1024)
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())// 将保存的时候的URI名称用MD5加密
				.tasksProcessingOrder(QueueProcessingType.LIFO).diskCacheFileCount(100) // 缓存的文件数量
				.diskCache(new UnlimitedDiskCache(cacheDir))// 自定义缓存路径
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout(5s),readTimeout(30s)超时时间
				.writeDebugLogs() // Remove for releaseapp
				.build();// 开始构建

		ImageLoader.getInstance().init(config);
	}

	// 注册App异常崩溃处理器
	private void registerUncaughtExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}

	public IBLE getIBLE() {
		if (bleManager == null) {

			Log.e("main===", "IBLE====");

			bleManager = new AndroidBle(this);
		}
		return bleManager;
	}

}
