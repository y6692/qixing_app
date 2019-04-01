package com.sunshine.blelibrary.inter;

import android.bluetooth.BluetoothDevice;

/**
 * 搜索设备接口
 * Created by Sunshine on 2016/8/4.
 */
public interface OnDeviceSearchListener {
    void onScanDevice(BluetoothDevice device,int rssi, byte[] scanRecord);
}
