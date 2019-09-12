package cn.qimate.bike.model;

/**
 * Created by yuanyi on 2019/5/9.
 */

public class EbikeInfoBean {

    private String electricity;
    private String mileage;
    private String fee;
    private String  is_locked;

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

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getIs_locked() {
        return is_locked;
    }

    public void setIs_locked(String is_locked) {
        this.is_locked = is_locked;
    }
}
