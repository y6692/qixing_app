package cn.http.rdata;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lan on 2016/7/28.
 */
public class RRent extends RData {

    /**
     * “tradeId” : ”xxxxxxxxx”
     * “encryptionKey” : Y
     * “keys” : ”XXXXXXX”
     * “serverTime” : xxxxx
     * “url” : http://airbike.luopingelec.com/airbike/jsp/bikeRent.jsp? bikeNo=
     */
    private ResultBean info;
    /**
     * “tradeId” : ”xxxxxxxxx”
     * “encryptionKey” : Y
     * “keys” : ”XXXXXXX”
     * “serverTime” : xxxxx
     * “url” : http://airbike.luopingelec.com/airbike/jsp/bikeRent.jsp? bikeNo=
     */
    public ResultBean getInfo() {
        return info;
    }

    public void setInfo(ResultBean info) {
        this.info = info;
    }

    public static class ResultBean {
        @SerializedName("tradeId")
        private String tradeId;
        @SerializedName("encryptionKey")
        private Integer encryptionKey;
        @SerializedName("keys")
        private String keys;
        @SerializedName("serverTime")
        private Integer serverTime;
        @SerializedName("url")
        private String url;

        public String getTradeId() {
            return tradeId;
        }

        public void setTradeId(String tradeId) {
            this.tradeId = tradeId;
        }

        public Integer getEncryptionKey() {
            return encryptionKey;
        }

        public void setEncryptionKey(Integer encryptionKey) {
            this.encryptionKey = encryptionKey;
        }

        public String getKeys() {
            return keys;
        }

        public void setKeys(String keys) {
            this.keys = keys;
        }

        public Integer getServerTime() {
            return serverTime;
        }

        public void setServerTime(Integer serverTime) {
            this.serverTime = serverTime;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
