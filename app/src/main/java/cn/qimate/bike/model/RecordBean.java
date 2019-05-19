package cn.qimate.bike.model;

import java.io.Serializable;

/**
 * CarRecord
 * 租车记录实体类
 */
public class RecordBean implements Serializable {

    /**
     * 内部ID
     */
    private int ID;

    /**
     * 车辆内部id
     */
    private String bikeId;

    /**
     * 车辆内部编号
     */
    private String bikeNo;

    /**
     * 租车时间，转成时间戳，以秒为单位
     */
    private String rentTime;

    /**
     * 经度，数据库值为null的话，该字段可能不存在
     */
    private String rentLongitude;

    /**
     * 纬度
     */
    private String rentLatitude;

    /**
     * 还车时间，转成时间戳，以秒为单位
     */
    private String returnTime;

    /**
     * 经度
     */
    private String returnLongitude;

    /**
     * 经度
     */
    private String returnLatitude;

    /**
     * 费用
     */
    private String tradeSum;

    /**
     * 交易内部id
     */
    private String tradeId;

    /**
     * 起点地名
     */
    private String startPoint;

    /**
     * 终点地名
     */
    private String endPoint;

    public String getBikeId() {
        return bikeId;
    }

    public void setBikeId(String bikeId) {
        this.bikeId = bikeId;
    }

    public String getBikeNo() {
        return bikeNo;
    }

    public void setBikeNo(String bikeNo) {
        this.bikeNo = bikeNo;
    }

    public String getRentLongitude() {
        return rentLongitude;
    }

    public void setRentLongitude(String rentLongitude) {
        this.rentLongitude = rentLongitude;
    }

    public String getRentLatitude() {
        return rentLatitude;
    }

    public void setRentLatitude(String rentLatitude) {
        this.rentLatitude = rentLatitude;
    }

    public String getRentTime() {
        return rentTime;
    }

    public void setRentTime(String rentTime) {
        this.rentTime = rentTime;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public String getReturnLongitude() {
        return returnLongitude;
    }

    public void setReturnLongitude(String returnLongitude) {
        this.returnLongitude = returnLongitude;
    }

    public String getReturnLatitude() {
        return returnLatitude;
    }

    public void setReturnLatitude(String returnLatitude) {
        this.returnLatitude = returnLatitude;
    }

    public String getTradeSum() {
        return tradeSum;
    }

    public void setTradeSum(String tradeSum) {
        this.tradeSum = tradeSum;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
