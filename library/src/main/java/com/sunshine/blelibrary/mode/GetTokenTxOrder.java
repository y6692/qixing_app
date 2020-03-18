package com.sunshine.blelibrary.mode;

import android.util.Log;

import java.util.Random;

/**
 * 发送token 指令
 * Created by sunshine on 2017/2/24.
 */

public class GetTokenTxOrder extends TxOrder {

    private Random random = new Random();
    /**
     *  指令类型
     */
    public GetTokenTxOrder() {
        super(TYPE.GET_TOKEN);
        add(new byte[]{0x01,0x01});
    }

    @Override
    public String generateString() {
        StringBuilder builder = new StringBuilder();
        // 命令类型
        int typeValue = getType().getValue();
        int type = ((typeValue >> 8) & 0x00ff);// 命令类型高8位
        int code = ((typeValue) & 0x00ff);// 命令类型低8位
        // 拼凑命令类型
        builder.append(formatByte2HexStr((byte) type));
        builder.append(formatByte2HexStr((byte) code));

        Log.e("2generateString===1", "==="+builder);

        // 拼凑数据
        for (int i = 0; i < size(); i++) {
            byte value = get(i);//获取数据
            builder.append(formatByte2HexStr(value));//拼凑数据
        }

        Log.e("2generateString===2", "==="+builder);

        // 如果数据总位数不够，在数据后面补0
        for (int i = builder.length()/2; i < 16; i++) {
            builder.append(formatByte2HexStr((byte) random.nextInt(127)));
        }
        Log.e("2generateString===3", "==="+builder);
        // 生成字符串形式的指令
        String orderStr = builder.toString();

        orderStr = "0601010172142946094B6C3122301976";

        Log.e("2generateString===end", getType()+"==="+typeValue+"==="+getDatas().size()+"==="+getDatas());

        return orderStr;
    }
}
