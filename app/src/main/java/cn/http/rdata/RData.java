package cn.http.rdata;

/**
 * Created by lan on 2016/7/15.
 */
public abstract class RData {

    private int reqCode;
    private int result;

    public RData() {
        result = -901;
    }

    public int getReqCode() {
        return reqCode;
    }

    public void setReqCode(int reqCode) {
        this.reqCode = reqCode;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
