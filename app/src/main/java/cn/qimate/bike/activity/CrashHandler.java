package cn.qimate.bike.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;


import com.alibaba.fastjson.JSON;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.util.ToastUtil;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 *
 * @author way
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private Context mContext;
    private UncaughtExceptionHandler mDefaultHandler;            // 系统默认的UncaughtException处理类
    private static CrashHandler INSTANCE = new CrashHandler();            // CrashHandler实例
    private Map<String, String> info = new HashMap<String, String>();    // 用来存储设备信息和异常信息

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {

    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
//        mContext = context.getApplicationContext();
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器
        Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器
    }

    /**
     * 当UncaughtException发生时会转入该重写的方法来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (mDefaultHandler != null) {
            handleException(ex);// 如果自定义的没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);// 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());

//            StackTraceElement[] stackTrace = ex.getStackTrace();
//            stackTrace[1].getLineNumber();
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex 异常信息 <a
     *           href="\"http://www.eoeandroid.com/home.php?mod=space&uid=7300\""
     *           target="\"_blank\"">@return</a> true 如果处理了该异常信息;否则返回false.
     */
    public boolean handleException(final Throwable ex) {
        if (ex == null) return false;

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();

                ToastUtil.showMessageApp(mContext, ">>>"+ex.getMessage());

//                memberEvent(mContext.getClass().getName()+"_"+ex.getStackTrace()[0].getLineNumber()+"_"+ex.getMessage());

                Looper.loop();
            }
        }.start();



        // 收集设备参数信息
        collectDeviceInfo(mContext);
        // 保存日志文件
        final String exString = getCrashInfoString(ex);

        Log.e("crash===", exString+"==="+ex);

        saveCrashInfo2File(exString, mContext);
        return true;
    }

    void memberEvent(String ex) {
        RequestParams params = new RequestParams();
        try {
            Log.e("CrashH===memberEvent0", new Build().MANUFACTURER.toUpperCase()+"==="+new Build().MODEL+"==="+Build.VERSION.RELEASE+"==="+mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName);

            String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
            String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");

            params.put("uid", uid);
            params.put("access_token", access_token);
            params.put("phone_brand", new Build().MANUFACTURER.toUpperCase());
            params.put("phone_model", new Build().MODEL);
            params.put("phone_system", "Android");
            params.put("phone_system_version", Build.VERSION.RELEASE);     //手机系统版本 必传 如：13.1.2
            params.put("app_version", mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName);      //应用版本 必传 如：1.8.2
            params.put("event", "1");
            params.put("event_content", ex);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        HttpHelper.post(mContext, Urls.memberEvent, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Log.e("CrashH===memberEvent1", "==="+responseString);

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
    }

    /**
     * 收集设备参数信息
     *
     * @param context
     */
    public void collectDeviceInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();// 获得包管理器
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);// 得到该应用的信息，即主Activity
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                info.put("versionName", versionName);
                info.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e("crash===","Error: " + e);
        }

        Field[] fields = Build.class.getDeclaredFields();// 反射机制
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                info.put(field.getName(), field.get("").toString());
            } catch (IllegalArgumentException e) {
                Log.e("crash===","Error: " + e);
            } catch (IllegalAccessException e) {
                Log.e("crash===","Error: " + e);
            }
        }
    }

    private String getCrashInfoString(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\r\n");
        }
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        // 循环着把所有的异常信息写入writer中
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();// 记得关闭
        String result = writer.toString();
        sb.append(result);
        return sb.toString();
    }

    @SuppressLint("SimpleDateFormat")
    public static String saveCrashInfo2File(String exString, Context context) {
        long timetamp = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        String fileName = "-crash-" + time + "-" + timetamp + ".log";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                //File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "SynChinese");
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ".crash" + File.separator + context.getPackageName());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File outfile = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(outfile);
                fos.write(exString.getBytes());
                fos.close();
                return outfile.getAbsolutePath();
            } catch (FileNotFoundException e) {
                Log.e("crash===","Error: " + e);
            } catch (IOException e) {
                Log.e("crash===","Error: " + e);
            }
        }
        return null;
    }
}
