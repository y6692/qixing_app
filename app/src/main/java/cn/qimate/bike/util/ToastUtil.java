/*
 *  Copyright (c) 2015 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */package cn.qimate.bike.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cn.qimate.bike.base.BaseApplication;


public class ToastUtil {

    private static Handler handler = new Handler(Looper.getMainLooper());

    private static Toast toast = null;

    private static Object synObj = new Object();

    public static void showMessage(final Context context, final String msg) {
        if(BaseApplication.getInstance().isDebug()){
            showMessage(context, msg, Toast.LENGTH_SHORT);
        }
    }

    public static void showMessageApp(final Context context, final String msg) {
        showMessage(context, msg, Toast.LENGTH_SHORT);
//        showMessage(context, msg, Toast.LENGTH_LONG);
    }

    public static void showNetErrorMessage() {
//        showMessage(Constants.NET_ERROR, Toast.LENGTH_SHORT);
    }

    /**
     * 根据设置的文本显示
     * @param msg
     */
    public static void showMessage(final Context context, final int msg) {
        showMessage(context, msg, Toast.LENGTH_SHORT);
    }

    private static View view;
    /**
     * 显示一个文本并且设置时长
     * @param msg
     * @param len
     */
    public static void showMessage(final Context context, final CharSequence msg, final int len) {
        if (msg == null || msg.equals("")) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (synObj) { //加上同步是为了每个toast只要有机会显示出来
//                    if (toast != null) {
//
//                        Log.e("toast===1", "==="+context);
//
//                        toast.cancel();
//                        toast = null;
//                    }
//
//                    if (toast == null){
//                        Log.e("toast===2", "==="+context);
//
//                        toast = Toast.makeText(context, msg, len);
//                        toast.show();
//                    }

                    if (toast != null) {

//                        Log.e("toast===1", "==="+context);

                        toast.cancel();
                        toast = null;


                    }else{
                        Log.e("toast===2", "==="+context);

                    }

                    toast = Toast.makeText(context, msg, len);
                    toast.show();
                }
            }
        });
    }

    /**
     * 资源文件方式显示文本
     * @param msg
     * @param len
     */
    public static void showMessage(final Context context, final int msg, final int len) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (synObj) {
                    if (toast != null) {
                        //toast.cancel();
                        toast.setText(msg);
                        toast.setDuration(len);
                    } else {
                        toast = Toast.makeText(context, msg, len);
                    }
                    toast.show();
                }
            }
        });
    }
}
