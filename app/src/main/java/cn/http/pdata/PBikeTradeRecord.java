package cn.http.pdata;

/**
 * Created by heyong on 16/6/30.
 */
public class PBikeTradeRecord extends PData {

    public PBikeTradeRecord(String phone, String bikeTradeNo, String timestamp,
                            String transType, String mackey, String index,
                            String Cap, String Vol, String latitude, String lontitude) {
        bodyAdd("phone", phone);
        bodyAdd("bikeTradeNo", bikeTradeNo);
        bodyAdd("timestamp", timestamp);
        bodyAdd("transType", transType);
        bodyAdd("mackey", mackey);
        bodyAdd("index", index);
        bodyAdd("Cap", Cap);
        bodyAdd("Vol", Vol);
        bodyAdd("lati", latitude);
        bodyAdd("longi", lontitude);
    }

    @Override
    protected void method() {
        method = "BikeTradeRecord";
    }
}
