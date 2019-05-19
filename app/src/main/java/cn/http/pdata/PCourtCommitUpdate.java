package cn.http.pdata;

/**
 * Created by lan on 2017-08-23.
 */

public class PCourtCommitUpdate extends PData {

    public PCourtCommitUpdate(String lockno, boolean success) {
        bodyAdd("lockno", lockno);
        bodyAdd("success", String.valueOf(success));
    }

    @Override
    protected void method() {
        method = "CourtCommitUpdate";
    }
}
