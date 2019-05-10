package cn.qimate.bike.model;

/**
 * Created by yuanyi on 2019/5/10.
 */

public class UserMonthIndexBean {

    private String bike_open_state;
    private String bike_img_url;
    private String ebike_img_url;
    private String bike_desc;
    private String ebike_desc;

    public String getBike_open_state() {
        return bike_open_state;
    }

    public void setBike_open_state(String bike_open_state) {
        this.bike_open_state = bike_open_state;
    }

    public String getBike_img_url() {
        return bike_img_url;
    }

    public void setBike_img_url(String bike_img_url) {
        this.bike_img_url = bike_img_url;
    }

    public String getEbike_img_url() {
        return ebike_img_url;
    }

    public void setEbike_img_url(String ebike_img_url) {
        this.ebike_img_url = ebike_img_url;
    }

    public String getBike_desc() {
        return bike_desc;
    }

    public void setBike_desc(String bike_desc) {
        this.bike_desc = bike_desc;
    }

    public String getEbike_desc() {
        return ebike_desc;
    }

    public void setEbike_desc(String ebike_desc) {
        this.ebike_desc = ebike_desc;
    }
}
