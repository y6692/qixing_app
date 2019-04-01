package com.sunshine.blelibrary.dispose;

/**
 * 处理指令基类
 * Created by sunshine on 2017/2/20.
 */

public abstract class BaseHandler {
    /**
     * 下一级处理器
     */
    public BaseHandler nextHandler;

    public final void handlerRequest(String hexString){
        if (hexString.startsWith(action())){
            handler(hexString);
        }else {
            nextHandler.handlerRequest(hexString);
        }
    }
    /**
     * 处理指令
     * @param hexString 接收到的指令
     */
    protected abstract void handler(String hexString);
    /**
     * 自身的指令头
     * @return 指令头
     */
    protected abstract String action();

}
