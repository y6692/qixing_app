package com.sunshine.blelibrary.dispose.impl;

import android.content.Intent;

import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.dispose.BaseHandler;
import com.sunshine.blelibrary.utils.ConvertUtils;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;

/**
 * token
 * Created by sunshine on 2017/2/20.
 */

public class Token extends BaseHandler {
    @Override
    protected void handler(String hexString) {
        byte[] mingwen = ConvertUtils.hexString2Bytes(hexString);
        if(mingwen != null && mingwen.length == 16) {
            if(mingwen[0] == 0x06 && mingwen[1] == 0x02){
                byte[] token = new byte[4];
                token[0] = mingwen[3];
                token[1] = mingwen[4];
                token[2] = mingwen[5];
                token[3] = mingwen[6];
                GlobalParameterUtils.getInstance().setCHIPTYPE(mingwen[7]);
                GlobalParameterUtils.getInstance().setDEVTYPE(mingwen[10]);
                GlobalParameterUtils.getInstance().setVersion(Integer.parseInt(hexString.substring(16,18),16)+"."+Integer.parseInt(hexString.substring(18,20),16));
                GlobalParameterUtils.getInstance().setToken(token);
                GlobalParameterUtils.getInstance().getContext().sendBroadcast(new Intent(Config.TOKEN_ACTION));
            }
        }
    }

    @Override
    protected String action() {
        return "0602";
    }
}
