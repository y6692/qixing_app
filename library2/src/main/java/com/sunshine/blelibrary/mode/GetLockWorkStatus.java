package com.sunshine.blelibrary.mode;

/**
 * 获取锁的工作状态
 * Created by sunshine on 2017/3/8.
 */

public class GetLockWorkStatus extends TxOrder {

    public GetLockWorkStatus() {
        super(TYPE.GET_LOCK_STATUS);
        add(new byte[]{0x01,0x00});
    }
}
