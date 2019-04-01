package com.sunshine.blelibrary.dispose.impl;

import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.dispose.BaseHandler;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;

/**
 * 锁状态
 * Created by sunshine on 2017/2/20.
 */

public class LockStatus extends BaseHandler {
    @Override
    protected void handler(String hexString) {
        if (hexString.startsWith("050F0101")){
            GlobalParameterUtils.getInstance().sendBroadcast(Config.LOCK_STATUS_ACTION,"");
        }else {
            GlobalParameterUtils.getInstance().sendBroadcast(Config.LOCK_STATUS_ACTION,hexString);
        }
    }

    @Override
    protected String action() {
        return "050F";
    }
}
