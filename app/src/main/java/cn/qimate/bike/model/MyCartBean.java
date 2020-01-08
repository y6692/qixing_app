package cn.qimate.bike.model;

/**
 * Created by 123 on 2017/12/3.
 */

public class MyCartBean {

    private String name;
    private String remaining;
    private String desc;
    private String status;
    private String[] linear_gradient;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemaining() {
        return remaining;
    }

    public void setRemaining(String remaining) {
        this.remaining = remaining;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getLinear_gradient() {
        return linear_gradient;
    }

    public void setLinear_gradient(String[] linear_gradient) {
        this.linear_gradient = linear_gradient;
    }
}
