package cn.qimate.bike.model;

/**
 * Created by LDY on 2017/2/10.
 */

public class BannerBean {

    private String image_url;  //图片url
    private String h5_title;  //h5标题（针对跳转类型为h5）
    private String action_type;  //跳转类型 h5、app等
    private String action_content;  //跳转内容

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getH5_title() {
        return h5_title;
    }

    public void setH5_title(String h5_title) {
        this.h5_title = h5_title;
    }

    public String getAction_type() {
        return action_type;
    }

    public void setAction_type(String action_type) {
        this.action_type = action_type;
    }

    public String getAction_content() {
        return action_content;
    }

    public void setAction_content(String action_content) {
        this.action_content = action_content;
    }
}
