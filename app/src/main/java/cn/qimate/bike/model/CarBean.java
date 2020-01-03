package cn.qimate.bike.model;

/**
 * Created by LDY on 2017/2/10.
 */

public class CarBean {

    private int id; //车辆ID
    private String number;  //车辆编号
    private String qrcode;  //车辆二维码
    private int carmodel_id;    //车型ID
    private String carmodel_name;   //车型名称
    private int school_id;  //学校ID
    private String school_name; //学校名称
    private String school_area; //学校区域
    private int lock_id;    //锁ID
    private String lock_name;   //锁名称(英文)
    private String lock_title;   //锁名称别名(中文)
    private String vendor_lock_id;  //厂商锁ID、deviceuuid
    private String lock_no; //锁lock_no
    private String lock_mac;    //锁mac
    private String lock_secretkey;  //锁秘钥、行运兔bleid
    private int lock_status;    //车锁状态 0未知 1已上锁 2已开锁 3离线
    private String lock_code;   //车锁操作状态码 701离线 702超时 703骑行中 704第三方服务器内部错误 705设备内存错误
    private String lock_status_updated_at;  //锁状态更新时间
    private String electricity;    //电量
    private String mileage;   //续航里程

    private String each_free_time;    //每次免费时长 单位：分钟 为0不免费
    private String first_time;   //起步时间 单位：分钟
    private String first_price;    //起步价格
    private String continued_time; //后续时间 单位：分钟
    private String continued_price; //后续价格


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public int getCarmodel_id() {
        return carmodel_id;
    }

    public void setCarmodel_id(int carmodel_id) {
        this.carmodel_id = carmodel_id;
    }

    public String getCarmodel_name() {
        return carmodel_name;
    }

    public void setCarmodel_name(String carmodel_name) {
        this.carmodel_name = carmodel_name;
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

    public int getLock_id() {
        return lock_id;
    }

    public void setLock_id(int lock_id) {
        this.lock_id = lock_id;
    }

    public String getLock_name() {
        return lock_name;
    }

    public void setLock_name(String lock_name) {
        this.lock_name = lock_name;
    }

    public String getLock_title() {
        return lock_title;
    }

    public void setLock_title(String lock_title) {
        this.lock_title = lock_title;
    }

    public String getVendor_lock_id() {
        return vendor_lock_id;
    }

    public void setVendor_lock_id(String vendor_lock_id) {
        this.vendor_lock_id = vendor_lock_id;
    }

    public String getLock_no() {
        return lock_no;
    }

    public void setLock_no(String lock_no) {
        this.lock_no = lock_no;
    }

    public String getLock_mac() {
        return lock_mac;
    }

    public void setLock_mac(String lock_mac) {
        this.lock_mac = lock_mac;
    }

    public String getLock_secretkey() {
        return lock_secretkey;
    }

    public void setLock_secretkey(String lock_secretkey) {
        this.lock_secretkey = lock_secretkey;
    }

    public int getLock_status() {
        return lock_status;
    }

    public void setLock_status(int lock_status) {
        this.lock_status = lock_status;
    }

    public String getLock_code() {
        return lock_code;
    }

    public void setLock_code(String lock_code) {
        this.lock_code = lock_code;
    }

    public String getLock_status_updated_at() {
        return lock_status_updated_at;
    }

    public void setLock_status_updated_at(String lock_status_updated_at) {
        this.lock_status_updated_at = lock_status_updated_at;
    }

    public String getElectricity() {
        return electricity;
    }

    public void setElectricity(String electricity) {
        this.electricity = electricity;
    }


    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getEach_free_time() {
        return each_free_time;
    }

    public void setEach_free_time(String each_free_time) {
        this.each_free_time = each_free_time;
    }

    public String getFirst_time() {
        return first_time;
    }

    public void setFirst_time(String first_time) {
        this.first_time = first_time;
    }

    public String getFirst_price() {
        return first_price;
    }

    public void setFirst_price(String first_price) {
        this.first_price = first_price;
    }

    public String getContinued_time() {
        return continued_time;
    }

    public void setContinued_time(String continued_time) {
        this.continued_time = continued_time;
    }

    public String getContinued_price() {
        return continued_price;
    }

    public void setContinued_price(String continued_price) {
        this.continued_price = continued_price;
    }
}
