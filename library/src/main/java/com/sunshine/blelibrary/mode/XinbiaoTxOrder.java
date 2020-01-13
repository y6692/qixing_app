package com.sunshine.blelibrary.mode;

/**
 * 复位
 * Created by sunshine on 2017/2/24.
 */

public class XinbiaoTxOrder extends TxOrder {

    public XinbiaoTxOrder() {
        super(TYPE.XINBIAO);
        add(new byte[]{ 0x02, 0x00});
    }
}
