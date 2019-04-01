package com.sunshine.blelibrary.config;

import android.bluetooth.BluetoothAdapter;
import android.content.IntentFilter;

import java.util.UUID;

/**
 * 作者：LiZhao
 * 时间：2017.2.8 11:26
 * 邮箱：44493547@qq.com
 * 备注：
 */
public class Config {

    public static final UUID xinbiaoUUID = UUID.fromString("0000fbca-0000-1000-8000-00805f9b34fb");

    public static final String NOT_SUPPORTED = "com.sunshine.blelibrary.config.not_supported";
    public static final int OBJECT_EMPTY = -1;
    public static final int DISCONNECT = 0;

    public static final UUID bltServerUUID = UUID.fromString("0000fee7-0000-1000-8000-00805f9b34fb");
    public static final UUID readDataUUID = UUID.fromString("000036f6-0000-1000-8000-00805f9b34fb");
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID writeDataUUID = UUID.fromString("000036f5-0000-1000-8000-00805f9b34fb");
    public static final UUID OAD_SERVICE_UUID = UUID.fromString("f000ffc0-0451-4000-b000-000000000000");
    public static final UUID OAD_READ_UUID = UUID.fromString("f000ffc1-0451-4000-b000-000000000000");
    public static final UUID OAD_WRITE_UUID = UUID.fromString("f000ffc2-0451-4000-b000-000000000000");


    /**
     * 马蹄锁
     */
    public static byte[] key = {30,85,45,80,52,73,60,70,45,75,60,86,10,90,40,42};

//    public static byte[] key = {36,87,48,82,54,75,26,71,48,80,65,88,12,99,45,23};
    /**
     * 圆形锁
     */
    public static byte[] yx_key = {58,96,67,42,92,01,33,31,41,30,15,78,12,19,40,37};

    public static byte[] password = {0x32, 0x30, 0x31, 0x37, 0x31, 0x35};

    public static final String TOKEN_ACTION = "com.sunshine.blelibrary.config.token_action";
    public static final String BATTERY_ACTION = "com.sunshine.blelibrary.config.battery_action";
    public static final String OPEN_ACTION = "com.sunshine.blelibrary.config.open_action";
    public static final String CLOSE_ACTION = "com.sunshine.blelibrary.config.close_action";
    public static final String LOCK_STATUS_ACTION = "com.sunshine.blelibrary.config.lock_status_action";
    public static final String PASSWORD_ACTION = "com.sunshine.blelibrary.config.password_action";
    public static final String AQ_ACTION = "com.sunshine.blelibrary.config.aq_action";
    public static final String SCAN_QR_ACTION = "com.sunshine.blelibrary.config.scan_qr_action";
    public static final String RESET_ACTION = "com.sunshine.blelibrary.config.reset_action";
    public static final String LOCK_RESULT = "com.sunshine.blelibrary.config.lock_result_action";
    public static final String SEND_AQ_ACTION = "com.sunshine.blelibrary.config.SEND_AQ_ACTION";
    public static final String UPDATE_VERSION_ACTION ="com.sunshine.blelibrary.config.UPDATE_VERSION_ACTION";
    public static final String GET_MODE = "com.sunshine.blelibrary.config.GET_MODE";
    public static final String SET_MODE = "com.sunshine.blelibrary.config.SET_MODE";
    public static final String GEt_LOCK_WORK_STATUS = "com.sunshine.blelibrary.config.GEt_LOCK_WORK_STATUS";
    public static final String GSM_ID_ACTION = "com.sunshine.blelibrary.config.GSM_ID_ACTION";
    public static final String GSM_VERSION_ACTION = "com.sunshine.blelibrary.config.GSM_VERSION_ACTION";
    public static final String UPDATE_NEXT = "com.sunshine.blelibrary.config.UPDATE_NEXT";

    public static final String BLE_DATA = "com.sunshine.blelibrary.config.BLE_DATA";
    public static final String UPDATE_VIEW = "com.sunshine.blelibrary.config.UPDATE_VIEW";
    public static final String REFRESH_MAC = "com.sunshine.blelibrary.config.REFRESH_MAC";

    public static final String GET_LOCKIP = "com.sunshine.blelibrary.config.GET_LOCKIP";
    public static final String GET_DEMAINNAME_FIRST = "com.sunshine.blelibrary.config.GET_DEMAINNAME_FIRST";
    public static final String GET_DEMAINNAME_SECOND = "com.sunshine.blelibrary.config.GET_DEMAINNAME_SECOND";

    public static final String GET_ICC_ID = "com.sunshine.blelibrary.config.GET_ICC_ID";


    public static IntentFilter initFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TOKEN_ACTION);
        intentFilter.addAction(BATTERY_ACTION);
        intentFilter.addAction(OPEN_ACTION);
        intentFilter.addAction(CLOSE_ACTION);
        intentFilter.addAction(LOCK_STATUS_ACTION);
        intentFilter.addAction(PASSWORD_ACTION);
        intentFilter.addAction(AQ_ACTION);
        intentFilter.addAction(SCAN_QR_ACTION);
        intentFilter.addAction(RESET_ACTION);
        intentFilter.addAction(LOCK_RESULT);
        intentFilter.addAction(SEND_AQ_ACTION);
        intentFilter.addAction(UPDATE_VERSION_ACTION);
        intentFilter.addAction(UPDATE_NEXT);
        intentFilter.addAction(GET_MODE);
        intentFilter.addAction(SET_MODE);
        intentFilter.addAction(GEt_LOCK_WORK_STATUS);
        intentFilter.addAction(GSM_ID_ACTION);
        intentFilter.addAction(GSM_VERSION_ACTION);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        intentFilter.addAction(GET_LOCKIP);
        intentFilter.addAction(GET_DEMAINNAME_FIRST);
        intentFilter.addAction(GET_DEMAINNAME_SECOND);
        intentFilter.addAction(GET_ICC_ID);

        intentFilter.addAction(BLE_DATA);
        intentFilter.addAction(UPDATE_VIEW);
        intentFilter.addAction(REFRESH_MAC);
        return intentFilter;
    }
}

