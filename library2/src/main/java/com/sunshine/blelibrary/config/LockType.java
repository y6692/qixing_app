package com.sunshine.blelibrary.config;

/**
 * 锁类型
 * Created by sunshine on 2017/2/23.
 */

public enum  LockType {

    MTS(1),
    YXS(2);

    private final int value;

     LockType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
