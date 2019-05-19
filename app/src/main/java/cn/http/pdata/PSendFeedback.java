package cn.http.pdata;

/**
 * Created by heyong on 16/6/30.
 */
public class PSendFeedback extends PData {

    public PSendFeedback(String type, String bikeno, String content) {
        bodyAdd("type", type);
        bodyAdd("bikeno", bikeno);
        bodyAdd("content", content);
    }

    @Override
    protected void method() {
        method = "SendFeedback";
    }
}
