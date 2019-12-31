package cn.qimate.bike.model;

/**
 * Created by Administrator on 2017/2/14.
 */

public class MyMessageBean {

    private int id;     //消息ID
    private String title;       //标题
    private String content;     //内容
    private int is_read;     //1已读 0未读
    private String action_type;     //跳转类型 h5 跳转h5 app 跳转原生app页面
    private String action_content;     //跳转内容
    private String created_at;     //发送时间


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
