package com.sunshine.blelibrary.dispose.impl;

import android.util.Log;

import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.dispose.BaseHandler;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;

/**
 * 复位指令
 * Created by yuanyi on 2019/12/19.
 */

public class Xinbiao extends BaseHandler {
    @Override
    protected void handler(String hexString) {

        Log.e("handler===", hexString + "===" + action());

        if (hexString.startsWith("05850200")){
            GlobalParameterUtils.getInstance().sendBroadcast(Config.XINBIAO_ACTION, hexString);
        }

    }

    @Override
    protected String action() {
        return "0585";
    }
}
