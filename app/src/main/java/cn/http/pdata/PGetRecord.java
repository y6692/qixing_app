package cn.http.pdata;

/**
 * Created by heyong on 16/6/30.
 */
public class PGetRecord extends PData {

    public PGetRecord(Integer recordNum, Integer pageNum, Integer ID) {
        bodyAdd("recordNum", recordNum);
        bodyAdd("pageNum", pageNum);
        bodyAdd("ID", ID);
    }

    @Override
    protected void method() {
        method = "GetTradeRecord";
    }
}
