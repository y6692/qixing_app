package cn.qimate.bike.model;

/**
 * Created by Administrator on 2017/2/16 0016.
 */

public class UserBean {
    private int id; //用户ID
    private String name;    //用户姓名
    private String nickname;    //用户昵称
    private String avatar;  //头像
    private int sex;  //性别
    private int college_id;  //学院id
    private String college_name;  //学院名字
    private String admission_time; //入学时间
    private String phone;   //用户手机
    private int school_id;  //学校ID
    private String school_name; //学校名称
    private String school_area; //所属校区
    private int credit_score;   //信用分
    private String balance; //余额
    private int cert1_status;   //免押金认证状态 0待认证 1认证中 2已驳回 3认证成功
    private int cert2_status;   //充值认证状态 0待认证 1认证中 2已驳回 3认证成功
    private int can_cert1;   //1可进入 0不可进入
    private int can_cert2;	//1可进入 0不可进入
    private int status; //用户状态 0锁定 1正常 2已注销
    private String created_at;  //注册时间
    private String is_new;  //是否新用户 1是 0不是
    private String invite_h5_title;  //邀请有礼H5标题
    private String invite_h5_url;  //邀请有礼H5地址
    private String credit_scores_h5_title;  //信用分H5标题
    private String credit_scores_h5_url;  //信用分H5地址 需要拼接上 ?token=xxx
    private String history_order_h5_title;  //历史订单H5标题
    private String history_order_h5_url;    //历史订单H5地址 需要拼接上 ?token=xxx
    private String ranks_h5_title;  //历史订单H5标题
    private String ranks_h5_url;    //历史订单H5地址 需要拼接上 ?token=xxx
    private int unread_count; //未读消息数 为0时不显示红点 否则显示红点
    private int is_full;    //资料是否已补全 1已补全 0未补全
    private int points;     //积分
    private int signin_task;     //是否开启签到任务 1开启 0关闭

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getCollege_id() {
        return college_id;
    }

    public void setCollege_id(int college_id) {
        this.college_id = college_id;
    }

    public String getCollege_name() {
        return college_name;
    }

    public void setCollege_name(String college_name) {
        this.college_name = college_name;
    }

    public String getAdmission_time() {
        return admission_time;
    }

    public void setAdmission_time(String admission_time) {
        this.admission_time = admission_time;
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

    public int getCan_cert1() {
        return can_cert1;
    }

    public void setCan_cert1(int can_cert1) {
        this.can_cert1 = can_cert1;
    }

    public int getCan_cert2() {
        return can_cert2;
    }

    public void setCan_cert2(int can_cert2) {
        this.can_cert2 = can_cert2;
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

    public String getInvite_h5_title() {
        return invite_h5_title;
    }

    public void setInvite_h5_title(String invite_h5_title) {
        this.invite_h5_title = invite_h5_title;
    }

    public String getInvite_h5_url() {
        return invite_h5_url;
    }

    public void setInvite_h5_url(String invite_h5_url) {
        this.invite_h5_url = invite_h5_url;
    }

    public String getCredit_scores_h5_title() {
        return credit_scores_h5_title;
    }

    public void setCredit_scores_h5_title(String credit_scores_h5_title) {
        this.credit_scores_h5_title = credit_scores_h5_title;
    }

    public String getCredit_scores_h5_url() {
        return credit_scores_h5_url;
    }

    public void setCredit_scores_h5_url(String credit_scores_h5_url) {
        this.credit_scores_h5_url = credit_scores_h5_url;
    }

    public String getHistory_order_h5_title() {
        return history_order_h5_title;
    }

    public void setHistory_order_h5_title(String history_order_h5_title) {
        this.history_order_h5_title = history_order_h5_title;
    }

    public String getHistory_order_h5_url() {
        return history_order_h5_url;
    }

    public void setHistory_order_h5_url(String history_order_h5_url) {
        this.history_order_h5_url = history_order_h5_url;
    }

    public String getRanks_h5_title() {
        return ranks_h5_title;
    }

    public void setRanks_h5_title(String ranks_h5_title) {
        this.ranks_h5_title = ranks_h5_title;
    }

    public String getRanks_h5_url() {
        return ranks_h5_url;
    }

    public void setRanks_h5_url(String ranks_h5_url) {
        this.ranks_h5_url = ranks_h5_url;
    }

    public int getUnread_count() {
        return unread_count;
    }

    public void setUnread_count(int unread_count) {
        this.unread_count = unread_count;
    }

    public int getIs_full() {
        return is_full;
    }

    public void setIs_full(int is_full) {
        this.is_full = is_full;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getSignin_task() {
        return signin_task;
    }

    public void setSignin_task(int signin_task) {
        this.signin_task = signin_task;
    }
}
