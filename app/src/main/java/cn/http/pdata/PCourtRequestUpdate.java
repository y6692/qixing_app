package cn.http.pdata;

/**
 * Created by lan on 2017-08-23.
 */

public class PCourtRequestUpdate extends PData {

    public PCourtRequestUpdate(String lockno) {
        bodyAdd("lockno", lockno);
    }

    @Override
    protected void method() {
        method = "CourtRequestUpdate";
    }
}
