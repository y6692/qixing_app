package com.sunshine.blelibrary.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.impl.AndroidBle;
import com.sunshine.blelibrary.inter.IBLE;

public class BLEService extends Service {
    private LocalBinder mLocalBinder = new LocalBinder();
    private AndroidBle mIBLE;
    @Override
    public void onCreate() {
        super.onCreate();
        //TODO 初始化操作类
        mIBLE = new AndroidBle(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    public void updateBroadcast(String s) {
        Intent intent = new Intent(Config.UPDATE_NEXT);
        intent.putExtra("data",s);
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public BLEService getService() {
            return BLEService.this;
        }
    }

    private void sendBroadcast(String action){
        sendBroadcast(new Intent(action));
    }

    public AndroidBle getIBLE(){
        return mIBLE;
    }
}
