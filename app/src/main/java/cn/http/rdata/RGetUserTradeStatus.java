package cn.http.rdata;

/**
 * Created by lan on 2016/7/28.
 */
public class RGetUserTradeStatus extends RData {

    /**
     * usingTime : 10845
     * bikeNo : 001-02013
     * lastTradeTime : 2016-11-03 11:02:20
     * userTradeStatus : 1
     * tradeId : 581AA8BA001-02013
     */

    private ResultBean info;

    public ResultBean getInfo() {
        return info;
    }

    public void setInfo(ResultBean info) {
        this.info = info;
    }

    public static class ResultBean {
        private int usingTime;
        private String bikeNo;
        private String lastTradeTime;
        private int userTradeStatus;
        private String tradeId;

        public int getUsingTime() {
            return usingTime;
        }

        public void setUsingTime(int usingTime) {
            this.usingTime = usingTime;
        }

        public String getBikeNo() {
            return bikeNo;
        }

        public void setBikeNo(String bikeNo) {
            this.bikeNo = bikeNo;
        }

        public String getLastTradeTime() {
            return lastTradeTime;
        }

        public void setLastTradeTime(String lastTradeTime) {
            this.lastTradeTime = lastTradeTime;
        }

        public int getUserTradeStatus() {
            return userTradeStatus;
        }

        public void setUserTradeStatus(int userTradeStatus) {
            this.userTradeStatus = userTradeStatus;
        }

        public String getTradeId() {
            return tradeId;
        }

        public void setTradeId(String tradeId) {
            this.tradeId = tradeId;
        }
    }
}
