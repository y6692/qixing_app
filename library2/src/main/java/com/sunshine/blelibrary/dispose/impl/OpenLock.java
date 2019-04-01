package com.sunshine.blelibrary.dispose.impl;

import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.dispose.BaseHandler;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;

/**
 * 开锁指令
 * Created by sunshine on 2017/2/20.
 */

public class OpenLock extends BaseHandler {
    @Override
    protected void handler(String hexString) {
        if (hexString.startsWith("05020101")){
            GlobalParameterUtils.getInstance().sendBroadcast(Config.OPEN_ACTION,"");
        }else {
            GlobalParameterUtils.getInstance().sendBroadcast(Config.OPEN_ACTION,hexString);
        }
    }

    @Override
    protected String action() {
        return "0502";
    }
}
