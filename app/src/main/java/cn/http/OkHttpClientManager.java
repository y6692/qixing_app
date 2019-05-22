package cn.http;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.http.config.CookieManager;
import cn.http.config.RetryInterceptor;
import cn.http.config.XTrustManager;
import cn.http.pdata.PBikeTradeRecord;
import cn.http.pdata.PCourtCommitUpdate;
import cn.http.pdata.PCourtRequestUpdate;
import cn.http.pdata.PDataEmpty;
import cn.http.pdata.PGetCaptcha;
import cn.http.pdata.PRent;
import cn.http.pdata.PUserLogin;
import cn.http.pdata.PUserRegister;
import cn.qimate.bike.util.Constants;
import cn.qimate.bike.util.Globals;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by heyong on 2017/5/23.
 */

public class OkHttpClientManager {
    private static final String TAG = OkHttpClientManager.class.getSimpleName();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClientManager mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private Gson mGson;

    private OkHttpClientManager() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); //包含header、body数据

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(Constants.CONNECT_TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(Constants.SOCKET_TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(Constants.SOCKET_TIME_OUT, TimeUnit.SECONDS);
        builder.cookieJar(new CookieManager());
        builder.addInterceptor(new RetryInterceptor());
        builder.addInterceptor(loggingInterceptor);

        X509TrustManager trustManager = new XTrustManager();
        SSLSocketFactory sslSocketFactory = provideSSLFactory(trustManager);
        if(sslSocketFactory != null) {
            builder.sslSocketFactory(sslSocketFactory, trustManager);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
        }
        mOkHttpClient = builder.build();
        mDelivery = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }

    public static OkHttpClientManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }

    public void UserRegister(String phone, String devicenum, String captcha, final ResultCallback callback) {
        PUserRegister p = new PUserRegister(phone, devicenum, captcha);
        PUserLogin plogin = new PUserLogin(phone, devicenum);
        Globals.tempStr = plogin.getPStr();      //临时保存以做会话过期用

        RequestBody requestBody = RequestBody.create(JSON, p.getPStr());

        deliveryRequest(buildRequest(requestBody), callback);
    }

    public void GetCaptcha(String phone, final ResultCallback callback) {
        PGetCaptcha p = new PGetCaptcha(phone);

        RequestBody requestBody = RequestBody.create(JSON, p.getPStr());

        deliveryRequest(buildRequest(requestBody), callback);
    }

    public void UserLogin(String phone, String devicenum, final ResultCallback callback) {
        PUserLogin p = new PUserLogin(phone, devicenum);
        Globals.tempStr = p.getPStr();      //临时保存以做会话过期用

        RequestBody requestBody = RequestBody.create(JSON, p.getPStr());

        deliveryRequest(buildRequest(requestBody), callback);
    }

    //获取当前状态
    public void GetUserTradeStatus(final ResultCallback callback) {
        PDataEmpty p = new PDataEmpty("GetUserTradeStatus");

        RequestBody requestBody = RequestBody.create(JSON, p.getPStr());

        deliveryRequest(buildRequest(requestBody), callback);
    }

    //租车
    public void Rent(String macKey, String keySource, int timestamp, final ResultCallback callback) {
        PRent p = new PRent(macKey, keySource, timestamp);
        RequestBody requestBody = RequestBody.create(JSON, p.getPStr());

        Log.e("p.getPStr===", "==="+ p.getPStr());

        deliveryRequest(buildRequest(requestBody), callback);
    }

    //上传交易记录
    public void BikeTradeRecord(String phone, String bikeTradeNo, String timestamp, String transType,
                                String mackey, String index, String Cap, String Vol, String latitude,
                                String lontitude, final ResultCallback callback) {
        PBikeTradeRecord p = new PBikeTradeRecord(phone, bikeTradeNo, timestamp, transType, mackey, index, Cap, Vol, latitude, lontitude);
        RequestBody requestBody = RequestBody.create(JSON, p.getPStr());

        deliveryRequest(buildRequest(requestBody), callback);
    }

    //请求秘钥
    public void CourtRequestUpdate(String lockno, final ResultCallback callback) {
        PCourtRequestUpdate p = new PCourtRequestUpdate(lockno);
        RequestBody requestBody = RequestBody.create(JSON, p.getPStr());

        deliveryRequest(buildRequest(requestBody), callback);
    }

    //确认更改秘钥成功
    public void CourtCommitUpdate(String lockno, boolean success, final ResultCallback callback) {
        PCourtCommitUpdate p = new PCourtCommitUpdate(lockno, success);
        RequestBody requestBody = RequestBody.create(JSON, p.getPStr());

        deliveryRequest(buildRequest(requestBody), callback);
    }

    private Request buildRequest(RequestBody requestBody) {
        Request.Builder builder = new Request.Builder()
                .url(Constants.URL)
                .header("User-Agent", "okhttp/demo");
        builder.post(requestBody);

        return builder.build();
    }

    private void deliveryRequest(final Request request, final ResultCallback callback) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedStringCallback(request, e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try
                {
                    final String string = response.body().string();

                    if (callback.mType == String.class) {
                        sendSuccessResultCallback(string, callback);
                    } else {
                        Object o = mGson.fromJson(string, callback.mType);
                        sendSuccessResultCallback(o, callback);
                    }
                } catch (IOException e) {
                    sendFailedStringCallback(response.request(), e, callback);
                } catch (com.google.gson.JsonParseException e){ //Json解析的错误
                    sendFailedStringCallback(response.request(), e, callback);
                }
            }
        });
    }

    private void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onError(request, e);
            }
        });
    }

    private void sendSuccessResultCallback(final Object object, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResponse(object);
                }
            }
        });
    }

    private SSLSocketFactory provideSSLFactory(TrustManager trustManager) {
        // Create a trust manager that does not validate certificate chains
        try {
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{trustManager}, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            return  sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
