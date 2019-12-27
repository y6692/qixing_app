package cn.qimate.bike.model;

/**
 * Created by Administrator1 on 2017/2/21.
 */

public class AuthStateBean {

    private String school;
    private String realname;
    private String stunum;
    private String stunumfile;
    private String stunumfile2;
    private String iscert;
    private String cert_method;

    private String user_name;   //姓名
    private String student_id;  //学号
    private int school_id;   //学校ID
    private String school_name;
    private String admission_time;  //入学时间
    private String identity_number; //身份证号
    private String cert_photo;  //证件照
    private String cert_photo_url;  //证件照地址
    private String holding_cert_photo;  //手持证件照
    private String holding_cert_photo_url;  //手持证件照地址
    private int status; //认证状态 0待认证 1认证中 2已驳回 3认证成功


    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getStunum() {
        return stunum;
    }

    public void setStunum(String stunum) {
        this.stunum = stunum;
    }

    public String getStunumfile() {
        return stunumfile;
    }

    public void setStunumfile(String stunumfile) {
        this.stunumfile = stunumfile;
    }

    public String getStunumfile2() {
        return stunumfile2;
    }

    public void setStunumfile2(String stunumfile2) {
        this.stunumfile2 = stunumfile2;
    }

    public String getIscert() {
        return iscert;
    }

    public void setIscert(String iscert) {
        this.iscert = iscert;
    }

    public String getCert_method() {
        return cert_method;
    }

    public void setCert_method(String cert_method) {
        this.cert_method = cert_method;
    }


    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
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

    public String getAdmission_time() {
        return admission_time;
    }

    public void setAdmission_time(String admission_time) {
        this.admission_time = admission_time;
    }

    public String getIdentity_number() {
        return identity_number;
    }

    public void setIdentity_number(String identity_number) {
        this.identity_number = identity_number;
    }

    public String getCert_photo() {
        return cert_photo;
    }

    public void setCert_photo(String cert_photo) {
        this.cert_photo = cert_photo;
    }

    public String getHolding_cert_photo() {
        return holding_cert_photo;
    }

    public void setHolding_cert_photo(String holding_cert_photo) {
        this.holding_cert_photo = holding_cert_photo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCert_photo_url() {
        return cert_photo_url;
    }

    public void setCert_photo_url(String cert_photo_url) {
        this.cert_photo_url = cert_photo_url;
    }

    public String getHolding_cert_photo_url() {
        return holding_cert_photo_url;
    }

    public void setHolding_cert_photo_url(String holding_cert_photo_url) {
        this.holding_cert_photo_url = holding_cert_photo_url;
    }
}
