/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package cn.loopj.android.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import cn.qimate.bike.activity.LoginActivity;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.util.ToastUtil;

/**
 * Used to intercept and handle the responses from requests made using {@link AsyncHttpClient}. The
 * {@link #onSuccess(int, Header[], String)} method is designed to be anonymously
 * overridden with your own response handling code. <p>&nbsp;</p> Additionally, you can override the
 * {@link #onFailure(int, Header[], String, Throwable)}, {@link #onStart()}, and
 * {@link #onFinish()} methods as required. <p>&nbsp;</p> For example: <p>&nbsp;</p>
 * <pre>
 * AsyncHttpClient client = new AsyncHttpClient();
 * client.get("http://www.google.com", new TextHttpResponseHandler() {
 *     &#064;Override
 *     public void onStart() {
 *         // Initiated the request
 *     }
 *
 *     &#064;Override
 *     public void onSuccess(String responseBody) {
 *         // Successfully got a response
 *     }
 *
 *     &#064;Override
 *     public void onFailure(String responseBody, Throwable e) {
 *         // Response failed :(
 *     }
 *
 *     &#064;Override
 *     public void onFinish() {
 *         // Completed the request (either success or failure)
 *     }
 * });
 * </pre>
 */
public abstract class TextHttpResponseHandler extends AsyncHttpResponseHandler {
    private static final String LOG_TAG = "TextHttpResponseHandler";

    /**
     * Creates new instance with default UTF-8 encoding
     */
    public TextHttpResponseHandler() {
        this(DEFAULT_CHARSET);
    }

    /**
     * Creates new instance with given string encoding
     *
     * @param encoding String encoding, see {@link #setCharset(String)}
     */
    public TextHttpResponseHandler(String encoding) {
        super();
        setCharset(encoding);
    }

    /**
     * Called when request fails
     *
     * @param statusCode     http response status line
     * @param headers        response headers if any
     * @param responseString string response of given charset
     * @param throwable      throwable returned when processing request
     */
    public abstract void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable);

    /**
     * Called when request succeeds
     *
     * @param statusCode     http response status line
     * @param headers        response headers if any
     * @param responseString string response of given charset
     */
    public abstract void onSuccess(int statusCode, Header[] headers, String responseString);


    private Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {

                default:
                    break;
            }
            return false;
        }
    });

    @Override
    public void onSuccess(final int statusCode, final Header[] headers, final byte[] responseBytes) {

        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.e("onSuccess===00", responseBytes+"===");

                String responseString = getResponseString(responseBytes, getCharset());


                Log.e("onSuccess===0", responseString+"===");

                if(responseString!=null && !"".equals(responseString)){
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                    Log.e("onSuccess===1", responseString+"==="+result.getStatus_code());

                    if(result.getStatus_code()==401){
                        Log.e("onSuccess===2", responseString+"==="+result.getStatus_code());

                        SharedPreferencesUrls.getInstance().putString("access_token", "");
                        SharedPreferencesUrls.getInstance().putString("iscert", "");

                        ToastUtil.showMessageApp(BaseApplication.context, result.getMessage());

                        Intent intent = new Intent(BaseApplication.context, LoginActivity.class);
                        BaseApplication.context.startActivity(intent);
                    }else{
                        onSuccess(statusCode, headers, responseString);
                    }
                }

            }
        });



//        try {
//        	String returnStr = new String(responseBytes, "utf-8");
//        	Log.e("MyTest", "onSuccess:" + returnStr);
//			onSuccess(statusCode, headers, getResponseString(RSAUtils.decryptByPrivateKey(Base64Utils.decode(returnStr), MyKey.cliPrivateKey), "utf-8"));
//		} catch (Exception e) {
//			Log.e("MyTest", "successError:" + e.toString());
//			e.printStackTrace();
//		}
    }


    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable throwable) {
        onFailure(statusCode, headers, getResponseString(responseBytes, getCharset()), throwable);
    }

    /**
     * Attempts to encode response bytes as string of set encoding
     *
     * @param charset     charset to create string with
     * @param stringBytes response bytes
     * @return String of set encoding or null
     */
    public static String getResponseString(byte[] stringBytes, String charset) {
        try {
            return stringBytes == null ? null : new String(stringBytes, charset);
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, "Encoding response into string failed", e);
            return null;
        }
    }

}
