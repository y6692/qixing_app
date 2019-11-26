package cn.qimate.bike.model;

/**
 * Created by Administrator on 2017/2/16 0016.
 */

public class UserBean {
    private int id; //用户ID
    private String name;    //用户姓名
    private String phone;   //用户手机
    private int school_id;  //学校ID
    private String school_name; //学校名称
    private String school_area; //所属校区
    private int credit_score;   //信用分
    private String balance; //余额
    private int cert1_status;   //免押金认证状态 0待认证 1认证中 2已驳回 3认证成功
    private int cert2_status;   //充值认证状态 0待认证 1认证中 2已驳回 3认证成功
    private int status; //用户状态 0锁定 1正常 2已注销
    private String created_at;  //注册时间
    private String is_new;  //是否新用户 1是 0不是

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getSchool_id() {
        return school_id;
    }

    public void setSchool_id(int school_id) {
        this.school_id = school_id;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getSchool_area() {
        return school_area;
    }

    public void setSchool_area(String school_area) {
        this.school_area = school_area;
    }

    public int getCredit_score() {
        return credit_score;
    }

    public void setCredit_score(int credit_score) {
        this.credit_score = credit_score;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public int getCert1_status() {
        return cert1_status;
    }

    public void setCert1_status(int cert1_status) {
        this.cert1_status = cert1_status;
    }

    public int getCert2_status() {
        return cert2_status;
    }

    public void setCert2_status(int cert2_status) {
        this.cert2_status = cert2_status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getIs_new() {
        return is_new;
    }

    public void setIs_new(String is_new) {
        this.is_new = is_new;
    }
}
