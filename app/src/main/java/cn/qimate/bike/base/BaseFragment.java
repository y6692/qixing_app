package cn.qimate.bike.base;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polygon;
import com.sunshine.blelibrary.inter.OnConnectionListener;
import com.zxing.lib.scaner.activity.ActivityScanerCode;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.activity.LoginActivity;
import cn.qimate.bike.ble.BLEService;
import cn.qimate.bike.core.common.AppManager;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.full.SplashActivity;
import cn.qimate.bike.model.CarAuthorityBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UserMsgBean;
import cn.qimate.bike.swipebacklayout.SwipeBackLayout;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivityBase;
import cn.qimate.bike.util.ToastUtil;

public class BaseFragment extends Fragment implements OnConnectionListener, SwipeBackActivityBase {

	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;

	private BaseApplication baseApplication;
	public String uid;
	public String access_token;
	public Boolean userlogin = false;

	protected LoadingDialog loadingDialog;
	protected Context context;

	public BLEService bleService = new BLEService();

//	protected AMap aMap;
//	protected BitmapDescriptor successDescripter;
//	protected MapView mapView;


	public static String m_nowMac = "";  //"A8:1B:6A:B4:E7:C9"
	public static int order_id;
	public static String oid = "";
	public static String osn = "";
	public static String type = "";
	public static int unauthorized_code;

	public static List<Polygon> pOptions;
	public static List<Boolean> isContainsList;
	public static List<LatLng> listPoint = new ArrayList<>();
	public static List<LatLng> centerList = new ArrayList<LatLng>();

	public static double referLatitude = 0.0;
	public static double referLongitude = 0.0;

	public static JSONArray jsonArray;
	public static JSONArray jsonArray2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		uid = SharedPreferencesUrls.getInstance().getString("uid","");	
		access_token = SharedPreferencesUrls.getInstance().getString("access_token","");

//		m_nowMac = SharedPreferencesUrls.getInstance().getString("m_nowMac", "");
//		oid = SharedPreferencesUrls.getInstance().getString("oid", "");
//		osn = SharedPreferencesUrls.getInstance().getString("osn", "");
		type = SharedPreferencesUrls.getInstance().getString("type", "");


		context = getActivity();

		BaseApplication.context = context;

//		RefreshLogin();
	}

	@Override
	public void onResume() {
		super.onResume();

		Log.e("BF===onResume", "==="+LoginActivity.isForeground);

//		if(!LoginActivity.isForeground && !SplashActivity.isForeground){
//			RefreshLogin();
//		}

//		RefreshLogin();
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

	public void onStartCommon(final String title) {
		m_myHandler.post(new Runnable() {
			@Override
			public void run() {
				if (loadingDialog != null && !loadingDialog.isShowing()) {
					loadingDialog.setTitle(title);
					loadingDialog.show();
				}
			}
		});

	}

	public void onFailureCommon(final String s) {
		m_myHandler.post(new Runnable() {
			@Override
			public void run() {
				if (loadingDialog != null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}

				Log.e("onFailureCommon===", "==="+s);

				UIHelper.ToastError(context, s);
			}
		});
	}

	public void onFailureCommon(final String title, final String s) {
		m_myHandler.post(new Runnable() {
			@Override
			public void run() {
				if (loadingDialog != null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}

				Log.e("onFailureCommon===", title+"==="+s);

				UIHelper.ToastError(context, s);
			}
		});
	}

	Handler m_myHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message mes) {
			switch (mes.what) {
				case 0:
					break;
				default:
					break;
			}
			return false;
		}
	});

	@Override
	public SwipeBackLayout getSwipeBackLayout() {
		return null;
	}

	@Override
	public void setSwipeBackEnable(boolean enable) {

	}

	@Override
	public void scrollToFinishActivity() {
		finishMine();
	}

	public void finishMine() {
		AppManager.getAppManager().finishActivity(getActivity());
	}
	
	//用户已经登录过没有退出刷新登录
	public void RefreshLogin() {
		String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
		String uid = SharedPreferencesUrls.getInstance().getString("uid", "");

		Log.e("bf===RefreshLogin", uid+"==="+access_token);

		if (access_token == null || "".equals(access_token)) {
			setAlias("");
			ToastUtil.showMessageApp(context, "请先登录账号");
			UIHelper.goToAct(context, LoginActivity.class);
		} else {
//			RequestParams params = new RequestParams();
//			params.add("uid", uid);
//			params.add("access_token", access_token);
			HttpHelper.get(AppManager.getAppManager().currentActivity(), Urls.car_authority, new TextHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, String responseString) {
					try {
						Log.e("bf===car_authority", "==="+responseString);

						ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

						CarAuthorityBean bean = JSON.parseObject(result.getData(), CarAuthorityBean.class);

						SharedPreferencesUrls.getInstance().putString("iscert", ""+bean.getUnauthorized_code());

//						if (result.getFlag().equals("Success")) {
//							UserMsgBean bean = JSON.parseObject(result.getData(), UserMsgBean.class);
//							// 极光标记别名
//
//
//
////									setAlias(bean.getUid());
////									SharedPreferencesUrls.getInstance().putString("uid", bean.getUid());
////									SharedPreferencesUrls.getInstance().putString("access_token", bean.getAccess_token());
////									SharedPreferencesUrls.getInstance().putString("nickname", bean.getNickname());
////									SharedPreferencesUrls.getInstance().putString("realname", bean.getRealname());
////									SharedPreferencesUrls.getInstance().putString("sex", bean.getSex());
////									SharedPreferencesUrls.getInstance().putString("headimg", bean.getHeadimg());
////									SharedPreferencesUrls.getInstance().putString("points", bean.getPoints());
////									SharedPreferencesUrls.getInstance().putString("money", bean.getMoney());
////									SharedPreferencesUrls.getInstance().putString("bikenum", bean.getBikenum());
////									SharedPreferencesUrls.getInstance().putString("specialdays", bean.getSpecialdays());
////									SharedPreferencesUrls.getInstance().putString("ebike_specialdays", bean.getEbike_specialdays());
////									SharedPreferencesUrls.getInstance().putString("iscert", bean.getIscert());
//
//							SharedPreferencesUrls.getInstance().putString("access_token", bean.getToken());
//						} else {
//							setAlias("");
//							if (BaseApplication.getInstance().getIBLE() != null){
//								if (BaseApplication.getInstance().getIBLE().getConnectStatus()){
//									BaseApplication.getInstance().getIBLE().refreshCache();
//									BaseApplication.getInstance().getIBLE().close();
//									BaseApplication.getInstance().getIBLE().stopScan();
//								}
//							}
//							SharedPreferencesUrls.getInstance().putString("uid", "");
//							SharedPreferencesUrls.getInstance().putString("access_token","");
//						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString,
									  Throwable throwable) {
				}
			});


//			RequestParams params = new RequestParams();
//			params.add("uid", uid);
//			params.add("access_token", access_token);
//			HttpHelper.post(AppManager.getAppManager().currentActivity(), Urls.accesslogin, params,
//					new TextHttpResponseHandler() {
//						@Override
//						public void onSuccess(int statusCode, Header[] headers, String responseString) {
//							try {
//								ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//								if (result.getFlag().equals("Success")) {
//									UserMsgBean bean = JSON.parseObject(result.getData(), UserMsgBean.class);
//									// 极光标记别名
//
//									Log.e("RefreshLogin===BF", bean.getSpecialdays()+"==="+bean.getEbike_specialdays());
//
////									setAlias(bean.getUid());
////									SharedPreferencesUrls.getInstance().putString("uid", bean.getUid());
////									SharedPreferencesUrls.getInstance().putString("access_token", bean.getAccess_token());
////									SharedPreferencesUrls.getInstance().putString("nickname", bean.getNickname());
////									SharedPreferencesUrls.getInstance().putString("realname", bean.getRealname());
////									SharedPreferencesUrls.getInstance().putString("sex", bean.getSex());
////									SharedPreferencesUrls.getInstance().putString("headimg", bean.getHeadimg());
////									SharedPreferencesUrls.getInstance().putString("points", bean.getPoints());
////									SharedPreferencesUrls.getInstance().putString("money", bean.getMoney());
////									SharedPreferencesUrls.getInstance().putString("bikenum", bean.getBikenum());
////									SharedPreferencesUrls.getInstance().putString("specialdays", bean.getSpecialdays());
////									SharedPreferencesUrls.getInstance().putString("ebike_specialdays", bean.getEbike_specialdays());
////									SharedPreferencesUrls.getInstance().putString("iscert", bean.getIscert());
//
//									SharedPreferencesUrls.getInstance().putString("access_token", bean.getToken());
//								} else {
//									setAlias("");
//									if (BaseApplication.getInstance().getIBLE() != null){
//										if (BaseApplication.getInstance().getIBLE().getConnectStatus()){
//											BaseApplication.getInstance().getIBLE().refreshCache();
//											BaseApplication.getInstance().getIBLE().close();
//											BaseApplication.getInstance().getIBLE().stopScan();
//										}
//									}
//									SharedPreferencesUrls.getInstance().putString("uid", "");
//									SharedPreferencesUrls.getInstance().putString("access_token","");
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//
//						@Override
//						public void onFailure(int statusCode, Header[] headers, String responseString,
//											  Throwable throwable) {
//						}
//					});
		}
	}

	// 极光推送===================================================================
	private void setAlias(String uid) {
		// 调用JPush API设置Alias
		mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, uid));
	}

}
