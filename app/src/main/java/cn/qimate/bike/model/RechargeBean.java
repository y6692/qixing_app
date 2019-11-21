package cn.qimate.bike.model;

/**
 * Created by Administrator1 on 2017/2/15.
 */

public class RechargeBean {

    private String id;
    private String title;
    private String price;
    private String price_s;
    private boolean isSelected;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice_s() {
        return price_s;
    }

    public void setPrice_s(String price_s) {
        this.price_s = price_s;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
