package cn.http.pdata;

/**
 * Created by lan on 2017/3/31.
 */

public class PUserRegister extends PData {

    public PUserRegister(String phone, String devicenum, String captcha) {
        bodyAdd("phone", phone);
        bodyAdd("devicenum", devicenum);
        bodyAdd("captcha", captcha);
    }

    @Override
    protected void method() {
        method = "UserRegister";
    }
}
