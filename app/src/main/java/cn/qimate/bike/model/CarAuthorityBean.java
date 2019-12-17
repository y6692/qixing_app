package cn.qimate.bike.model;

import org.json.JSONObject;

/**
 * Created by LDY on 2017/2/10.
 */

public class CarAuthorityBean {

    private int unauthorized_code;
    private String order;

    public int getUnauthorized_code() {
        return unauthorized_code;
    }

    public void setUnauthorized_code(int unauthorized_code) {
        this.unauthorized_code = unauthorized_code;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
