package cn.qimate.bike.model;

import org.json.JSONObject;

/**
 * Created by LDY on 2017/2/10.
 */

public class CarAuthorityBean {

    private int unauthorized_code;      //未授权码（判断可否用车时用） 0（有权限时为0）1需要登录 2未认证 3认证中 4认证被驳回 5需要充值余额或购买骑行卡 6有进行中行程 7有待支付行程 8有待支付调度费 9有待支付赔偿费
    private int notice_code;        //消息码 同未授权码（展示公告时用）可以用车但认证中或被驳回此值不为0，为3或4
    private String order;
    private int refresh_interval;   //用车权限刷新频率 单位：毫秒

    public int getUnauthorized_code() {
        return unauthorized_code;
    }

    public void setUnauthorized_code(int unauthorized_code) {
        this.unauthorized_code = unauthorized_code;
    }

    public int getNotice_code() {
        return notice_code;
    }

    public void setNotice_code(int notice_code) {
        this.notice_code = notice_code;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getRefresh_interval() {
        return refresh_interval;
    }

    public void setRefresh_interval(int refresh_interval) {
        this.refresh_interval = refresh_interval;
    }
}
