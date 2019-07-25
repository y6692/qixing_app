package com.sunshine.blelibrary.inter;

/**
 * 作者：LiZhao
 * 时间：2017.2.8 11:46
 * 邮箱：44493547@qq.com
 * 备注：链接状态回调
 */
public interface OnConnectionListener {
    void onDisconnect(int state);
    void onServicesDiscovered(String name,String address);
    void onTimeOut();
}
