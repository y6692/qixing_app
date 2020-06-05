package cn.qimate.bike.util;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import com.sunshine.blelibrary.inter.OnDeviceSearchListener;

import java.util.Locale;

import cn.qimate.bike.activity.ClientManager;
import cn.qimate.bike.base.BaseApplication;
import cn.qimate.bike.fragment.MainFragment;

public class LogUtil {

    public static void e(final String s1, final String s2) {

        m_myHandler.post(new Runnable() {
            @Override
            public void run() {


                Log.e(s1, s2);

                if(MainFragment.testLog.length()>60000){
                    MainFragment.testLog = "";
                }

//              MainFragment.tv_test0.setText(MainFragment.testLog);
                MainFragment.testLog += (s1 + "：" + s2 + "\n");

                if(MainFragment.tv_test != null){
//                  MainFragment.tv_test0.setText(s1 + "：");
//                  MainFragment.tv_test.setText(s2 + "\n");
                    MainFragment.tv_test.setText(MainFragment.testLog);

//                  scrollToBottom(MainFragment.sv_test, MainFragment.tv_test);

                }
            }
        });


//        MainFragment.sv_test.fullScroll(ScrollView.FOCUS_DOWN); //滚动到底部

    }

    public static void scrollToBottom(final View scroll, final View inner) {

        Handler mHandler = new Handler();

        mHandler.post(new Runnable() {
            public void run() {
                if (scroll == null || inner == null) {
                    return;
                }
                int offset = inner.getMeasuredHeight() - scroll.getHeight();
                if (offset < 0) {
                    offset = 0;
                }

                scroll.scrollTo(0, offset);
            }
        });
    }


    private static Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {


                default:
                    break;
            }
            return false;
        }
    });
}
