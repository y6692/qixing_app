package cn.qimate.bike.model;

/**
 * Created by Administrator1 on 2017/7/20.
 */

public class NearbyBean {

    private String latitude;
    private String longitude;
    private String quantity_level;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getQuantity_level() {
        return quantity_level;
    }

    public void setQuantity_level(String quantity_level) {
        this.quantity_level = quantity_level;
    }
}
