package com.sunshine.blelibrary.mode;

/**
 * 复位
 * Created by sunshine on 2017/2/24.
 */

public class resetLockTxOrder extends TxOrder {

    public resetLockTxOrder() {
        super(TYPE.RESET_LOCK);
        add(new byte[]{ 0x01, 0x01});
    }
}
