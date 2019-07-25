package com.sunshine.blelibrary.dispose.impl;

import android.content.Intent;

import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.dispose.BaseHandler;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;

/**
 * 统一交互指令
 * Created by sunshine on 2017/2/20.
 */

public class TY extends BaseHandler {
    @Override
    protected void handler(String hexString) {
        if (hexString.startsWith("CB070101")){
            GlobalParameterUtils.getInstance().getContext().sendBroadcast(new Intent(Config.SEND_AQ_ACTION));
        }
    }

    @Override
    protected String action() {
        return "CB";
    }
}
