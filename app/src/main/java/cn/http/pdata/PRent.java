package cn.http.pdata;

/**
 * Created by heyong on 16/6/30.
 */
public class PRent extends PData {

    public PRent(String macKey, String keySource, int timestamp) {
        bodyAdd("MAC", macKey);
        bodyAdd("keySource", keySource);
        bodyAdd("timestamp", timestamp);
    }

    @Override
    protected void method() {
        method = "Rent";
    }
}
