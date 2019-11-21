package cn.qimate.bike.model;

/**
 * Created by Administrator1 on 2017/2/16.
 */

public class UserMsgBean {

    private String uid;
    private String access_token;
    private String nickname;
    private String realname;
    private String sex;
    private String headimg;
    private String money;
    private String points;
    private String bikenum;
    private String iscert;
    private String specialdays;
    private String ebike_specialdays;

    private String token;

    public String getEbike_specialdays() {
        return ebike_specialdays;
    }

    public void setEbike_specialdays(String ebike_specialdays) {
        this.ebike_specialdays = ebike_specialdays;
    }

    public String getSpecialdays() {
        return specialdays;
    }

    public void setSpecialdays(String specialdays) {
        this.specialdays = specialdays;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getHeadimg() {
        return headimg;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getBikenum() {
        return bikenum;
    }

    public void setBikenum(String bikenum) {
        this.bikenum = bikenum;
    }

    public String getIscert() {
        return iscert;
    }

    public void setIscert(String iscert) {
        this.iscert = iscert;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
