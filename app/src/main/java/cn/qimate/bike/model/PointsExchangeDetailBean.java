package cn.qimate.bike.model;

/**
 * Created by Administrator1 on 2017/2/15.
 */

public class PointsExchangeDetailBean {

    private String time;  //时间
    private String content;   //描述
    private String points;  //积分

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
