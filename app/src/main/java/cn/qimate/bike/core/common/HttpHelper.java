package cn.qimate.bike.core.common;

import org.apache.http.impl.cookie.BasicClientCookie;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import java.util.Date;

import cn.loopj.android.http.AsyncHttpClient;
import cn.loopj.android.http.AsyncHttpResponseHandler;
import cn.loopj.android.http.PersistentCookieStore;
import cn.loopj.android.http.RequestParams;
import cn.qimate.bike.util.SHA1;

/**
 * 网络请求帮助类
 * 
 * @author Bo.Zhang
 *
 */
public class HttpHelper {

	private static AsyncHttpClient client = new AsyncHttpClient();

	static {

		client.setTimeout(15000); // 设置链接超时，如果不设置，默认为10s
		client.setUserAgent("Mozilla/5.0 (Linux; U; Android " + android.os.Build.VERSION.RELEASE + "; zh-cn; "
				+ android.os.Build.MODEL + ") AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");


	}

	/**
	 * 添加cookie
	 * 
	 * @param context
	 * @param hostUrl
	 */
	public static void addCookie(Context context, String hostUrl) {
		PersistentCookieStore cookieStore = new PersistentCookieStore(context);
		client.setCookieStore(cookieStore);
		BasicClientCookie newCookie = new BasicClientCookie("cookiesare", "awesome");
		newCookie.setVersion(1);
		newCookie.setDomain(hostUrl);
		newCookie.setPath("/");
		cookieStore.addCookie(newCookie);
	}

	/**
	 * get请求
	 * 
	 * @param context
	 * @param url
	 * @param responseHandler
	 */
	public static void get(Context context, String url, AsyncHttpResponseHandler responseHandler) {

		Log.e("header===0", "===");

//		try {
//			url = url + "&act=1&platform=Android&version="
//					+ context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}


		String token = SharedPreferencesUrls.getInstance().getString("access_token","");


		client.addHeader("Authorization", token);
		client.addHeader("Accept", "application/vnd.ws.v1+json");
		client.get(context, url, responseHandler);
	}

	/**
	 * get请求
	 * 
	 * @param context
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public static void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//		try {
//			url = url + "&act=1&platform=Android&version="
//					+ context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}


		String token = SharedPreferencesUrls.getInstance().getString("access_token","");

		Log.e("header===", "==="+token);

		client.addHeader("Authorization", token);
		client.addHeader("Accept", "application/vnd.ws.v1+json");
		client.get(context, url, params, responseHandler);
	}

	public static void post(Context context, String url, AsyncHttpResponseHandler responseHandler) {

		client.addHeader("Authorization", SharedPreferencesUrls.getInstance().getString("access_token",""));
		client.addHeader("Accept", "application/vnd.ws.v1+json");
		client.post(context, url, responseHandler);
	}

	/**
	 * post请求
	 * 
	 * @param context
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//		try {
//
//
//			url = url + "&act=1&platform=Android&version=" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}

		client.addHeader("Authorization", SharedPreferencesUrls.getInstance().getString("access_token",""));
		client.addHeader("Accept", "application/vnd.ws.v1+json");
		client.post(context, url, params, responseHandler);
	}

	/**
	 * post请求带head
	 *
	 * @param context
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public static void postWithHead(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//		try {
//			url = url + "&act=1&platform=Android&version="
//					+ context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}

		String time = ""+Math.round(new Date().getTime()/1000);
		String sign = SHA1.encode(time+"ga4H9dwf"+"StfmzsxJ6NBQGRFd2lI5gWhZnPVboLjU4eCcwauHYrqKOE0739AM18iDyTkXvp");

//		client.addHeader("A-APPKEY", "ga4H9dwf");
//		client.addHeader("A-TIMESTAMP", time);
//		client.addHeader("A-SIGN", sign);

		client.addHeader("Accept", "application/vnd.ws.v1+json");

		client.post(context, url, params, responseHandler);
	}

	public static void delete(Context context, String url, AsyncHttpResponseHandler responseHandler) {

		client.addHeader("Authorization", SharedPreferencesUrls.getInstance().getString("access_token",""));
		client.addHeader("Accept", "application/vnd.ws.v1+json");
		client.delete(context, url, responseHandler);
	}
}
