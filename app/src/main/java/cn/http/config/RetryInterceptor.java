package cn.http.config;



import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by lan on 2016/11/30.
 */

public class RetryInterceptor implements Interceptor {
    private static final int RETRY = 3;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        int tryCount = 0;
        Response response = null;
        do {
            try {
                // try the request
                response = chain.proceed(request);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("intercept", "Request is not successful - " + tryCount);
            }
            tryCount++;
        }while ((response == null || !response.isSuccessful()) && tryCount < RETRY);

        if ((response == null || !response.isSuccessful()) && tryCount == RETRY) {
            throw new IOException("连接主机失败");
        }

        // otherwise just pass the original response on
        return response;
    }

}
