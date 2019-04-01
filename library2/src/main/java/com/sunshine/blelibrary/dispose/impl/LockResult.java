package com.sunshine.blelibrary.dispose.impl;

import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.dispose.BaseHandler;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;

/**
 * 关锁
 * Created by sunshine on 2017/2/20.
 */

public class LockResult extends BaseHandler {
    @Override
    protected void handler(String hexString) {
        if (hexString.startsWith("05080101")){
            GlobalParameterUtils.getInstance().sendBroadcast(Config.LOCK_RESULT,"");
        }else {
            GlobalParameterUtils.getInstance().sendBroadcast(Config.LOCK_RESULT,hexString);
        }
    }

    @Override
    protected String action() {
        return "0508";
    }
}
