package cn.qimate.bike.model;

/**
 * Created by Administrator1 on 2017/2/15.
 */

public class PointsExchangeBean {

    private int id;  //兑换商品ID
    private String image;   //兑换商品图
    private String points;  //兑换所需积分

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
