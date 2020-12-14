package cn.qimate.bike.model;

/**
 * Created by 123 on 2017/12/3.
 */

public class MyCartBean {

    private int id;        //用户套餐卡ID

    private String code;        //套餐卡编号

    private String name;        //套餐卡名称

    private int type;        //套餐卡类型 1天数卡 2次数卡

    private String type_name;        //套餐卡类型名称

    private String original_price;        //套餐卡原价

    private String price;        //套餐卡现价

    private String[] linear_gradient;        //背景渐变色

    private int days;        //针对天数卡，套餐卡免费天数

    private int times;        //针对次数卡，套餐卡免费次数

    private int each_free_time;        //每次免费时长

    private int daily_free_times;        //每天免费次数 0不限次

    private String desc;        //套餐卡描述

    private String valid_start_time;        //针对天数卡，生效开始时间

    private String valid_end_time;        //针对天数卡，生效结束时间

    private int remaining_free_times;        //针对次数卡，剩余免费次数

    private int carmodel_id;        //套餐卡适用车型ID

    private String carmodel_name;        //套餐卡适用车型名称

    private int upper_daily_limit;        //是否已达每日免费上限 1是 0否

    private int today_free_times;        //今日免费次数 -1无免费次数上限 0今日没有免费次数

    private int state;        //状态 0待使用 1使用中 2已过期 3已用完

    private int remaining_free_days;        //针对天数卡， 剩余免费天数


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(String original_price) {
        this.original_price = original_price;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String[] getLinear_gradient() {
        return linear_gradient;
    }

    public void setLinear_gradient(String[] linear_gradient) {
        this.linear_gradient = linear_gradient;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getEach_free_time() {
        return each_free_time;
    }

    public void setEach_free_time(int each_free_time) {
        this.each_free_time = each_free_time;
    }

    public int getDaily_free_times() {
        return daily_free_times;
    }

    public void setDaily_free_times(int daily_free_times) {
        this.daily_free_times = daily_free_times;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getValid_start_time() {
        return valid_start_time;
    }

    public void setValid_start_time(String valid_start_time) {
        this.valid_start_time = valid_start_time;
    }

    public String getValid_end_time() {
        return valid_end_time;
    }

    public void setValid_end_time(String valid_end_time) {
        this.valid_end_time = valid_end_time;
    }

    public int getRemaining_free_times() {
        return remaining_free_times;
    }

    public void setRemaining_free_times(int remaining_free_times) {
        this.remaining_free_times = remaining_free_times;
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

    public int getUpper_daily_limit() {
        return upper_daily_limit;
    }

    public void setUpper_daily_limit(int upper_daily_limit) {
        this.upper_daily_limit = upper_daily_limit;
    }

    public int getToday_free_times() {
        return today_free_times;
    }

    public void setToday_free_times(int today_free_times) {
        this.today_free_times = today_free_times;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getRemaining_free_days() {
        return remaining_free_days;
    }

    public void setRemaining_free_days(int remaining_free_days) {
        this.remaining_free_days = remaining_free_days;
    }
}
