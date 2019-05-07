package cn.qimate.bike.base;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.sunshine.blelibrary.inter.OnConnectionListener;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.qimate.bike.core.common.SharedPreferencesUrls;

public class BaseFragment extends Fragment implements OnConnectionListener {

	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;

	private BaseApplication baseApplication;
	public String uid;
	public String access_token;
	public Boolean userlogin = false;

	public static String m_nowMac = "";  //"A8:1B:6A:B4:E7:C9"
	public static String oid = "";
	public static String osn = "";
	public static String type = "";

	protected AMap aMap;
	protected BitmapDescriptor successDescripter;

	public static double referLatitude = 0.0;
	public static double referLongitude = 0.0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		uid = SharedPreferencesUrls.getInstance().getString("uid","");	
		access_token = SharedPreferencesUrls.getInstance().getString("access_token","");

		m_nowMac = SharedPreferencesUrls.getInstance().getString("m_nowMac", "");
		oid = SharedPreferencesUrls.getInstance().getString("oid", "");
		osn = SharedPreferencesUrls.getInstance().getString("osn", "");
		type = SharedPreferencesUrls.getInstance().getString("type", "");

		RefreshLogin();
	}
	private void init() {
		baseApplication = (BaseApplication) getActivity().getApplication();
	}

	public BaseApplication getBaseApplication() {
		return baseApplication;
	}

	public void setBaseApplication(BaseApplication baseApplication) {
		this.baseApplication = baseApplication;
	}
	
	public void gotoAct(Class<?> clazz){
		Intent intent = new Intent(getActivity(), clazz);
		startActivity(intent);
	}

	@Override
	public void onDisconnect(int state) {
		mHandler.sendEmptyMessageDelayed(0, 1000);
	}
	@Override
	public void onServicesDiscovered(String name, String address) {
		getToken();
	}
	@Override
	public void onTimeOut() {}

	private void getToken() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				BaseApplication.getInstance().getIBLE().getToken();
			}
		}, 500);
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case MSG_SET_ALIAS:
					JPushInterface.setAliasAndTags(getContext(), (String) msg.obj, null, null);
					break;

				case MSG_SET_TAGS:
					JPushInterface.setAliasAndTags(getContext(), null, (Set<String>) msg.obj, null);
					break;

				default:
			}
		}
	};
	
	//用户已经登录过没有退出刷新登录
	public void RefreshLogin(){
//		if(access_token == null || "".equals(access_token)){
//			userlogin = false;
//		}else{
//			RequestParams params = new RequestParams();
//			params.add("uid", uid);
//			params.add("access_token", access_token);
//			HttpHelper.post(AppManager.getAppManager().currentActivity(), Urls.userFastLogin, params, new TextHttpResponseHandler() {				
//				@Override
//				public void onSuccess(int statusCode, Header[] headers, String responseString) {					
//					ResultConsel result = new ResultConsel();
//					try {
//						result = JSON.parseObject(responseString, ResultConsel.class);  
//						if(result.getFlag().equals("Success")){
//							LoginBean bean = JSON.parseObject(result.getData(), LoginBean.class);
//							// 极光标记别名
//							SharedPreferencesUrls.getInstance().putString("uid", bean.getUid());
//							SharedPreferencesUrls.getInstance().putString("access_token",
//									bean.getAccess_token());
//							SharedPreferencesUrls.getInstance().putString("nickname", bean.getNickname());
//							SharedPreferencesUrls.getInstance().putString("realname", bean.getRealname());
//							SharedPreferencesUrls.getInstance().putString("user_type", bean.getUser_type());
//							SharedPreferencesUrls.getInstance().putString("merchant_name",
//									bean.getMerchant_name());
//							SharedPreferencesUrls.getInstance().putString("headimgurl", bean.getHeadimgurl());
//							SharedPreferencesUrls.getInstance().putString("points", bean.getPoints());
//							SharedPreferencesUrls.getInstance().putString("sign_days", bean.getSign_days());
//							SharedPreferencesUrls.getInstance().putString("today_pass", bean.getToday_pass());
//							
//							SharedPreferencesUrls.getInstance().putString("surplus_day", bean.getSurplus_day());
//							SharedPreferencesUrls.getInstance().putString("activation_status", bean.getActivation_status());
//							SharedPreferencesUrls.getInstance().putString("mname", bean.getMname());
//							
//							userlogin = true;
//						}else{
//							UIHelper.showToastMsg(AppManager.getAppManager().currentActivity(), result.getMsg(),R.drawable.ic_error);
//							//清空数据
//							SharedPreferencesUrls.getInstance().putString("uid", "");
//							SharedPreferencesUrls.getInstance().putString("access_token", "");
//							userlogin = false;
//						}
//						//Log.e("Login", result.getMsg());
//					} catch (Exception e) {
//						e.printStackTrace();
//					}	
//				}			
//				@Override
//				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//					UIHelper.ToastError(AppManager.getAppManager().currentActivity(), throwable.toString());
//				}
//			});
//			
//		}		
	}
}
