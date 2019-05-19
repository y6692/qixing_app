package cn.http.pdata;

/**
 * Created by lan on 2017/3/31.
 */

public class PUserLogin extends PData {

    public PUserLogin(String phone, String devicenum) {
        bodyAdd("phone", phone);
        bodyAdd("devicenum", devicenum);
    }

    @Override
    protected void method() {
        method = "UserLogin";
    }
}
