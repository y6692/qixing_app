package com.sunshine.blelibrary.inter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 * 作者：LiZhao
 * 时间：2017.2.8 11:29
 * 邮箱：44493547@qq.com
 * 备注：BLE操作接口
 */
public interface IBLE {

    /**
     * 开启蓝牙
     *
     * @return 成功或失败
     */
    boolean enableBluetooth();
    /**
     * 关闭蓝牙
     *
     * @return 成功或失败
     */
    boolean disableBluetooth();

    /**
     * 重启蓝牙
     */
    void resetBluetoothAdapter();

    /**
     * 蓝牙是否开启
     *
     * @return 成功或失败
     */
    boolean isEnable();

    /**
     * @title 扫描设备
     * @param onDeviceSearchListener 搜索设备回调
     */
    void startScan(OnDeviceSearchListener onDeviceSearchListener);

    /**
     * 停止扫描
     */
    void stopScan();

    /**
     * 链接设备
     * @param address 设备地址
     * @param onConnectionListener 链接状态回调
     */
    boolean connect(String address,OnConnectionListener onConnectionListener);

    /**
     * 链接设备
     * @param device 设备
     * @param onConnectionListener 链接状态回调
     */
    boolean connectDevice(BluetoothDevice device,OnConnectionListener onConnectionListener);

    /**
     * 获取Token
     * @return 是否成功
     */
    boolean getToken();

    /**
     * @title 获取电量
     * @return 是否成功
     */
    boolean getBattery();

    /**
     * 开锁
     * @return 是否成功
     */
    boolean openLock();

    /**
     * 复位
     * @return 是否成功
     */
    boolean resetLock();
    /**
     * 获取锁状态
     * @return 是否成功
     */
    boolean getLockStatus();


    /**
     * 断开链接
     */
    void disconnect();

    /**
     * 获取蓝牙适配器
     * @return
     */
    BluetoothAdapter getBluetoothAdapter();

    /**
     * gatt关闭
     */
    void close();

    /**
     * 获取链接状态
     * @return
     */
    boolean getConnectStatus();

    /**
     * 清除缓存
     * @return
     */
    boolean refreshCache();
}
