package cn.http.pdata;

/**
 * Created by lan on 2017/3/31.
 */

public class PGetCaptcha extends PData {

    public PGetCaptcha(String phone) {
        bodyAdd("phone", phone);
    }

    @Override
    protected void method() {
        method = "GetCaptcha";
    }
}
