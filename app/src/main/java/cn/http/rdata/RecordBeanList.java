package cn.http.rdata;



import java.util.ArrayList;

import cn.qimate.bike.model.RecordBean;

/**
 * Created by lan on 2016/11/3.
 */

public class RecordBeanList extends RData {
    /**
     * total : 43
     * recordList : [{"endPoint":"滨江区南环路3730","tradeSum":0,"bikeNo":"001-02013","bikeId":42,"startPoint":"滨江区南环路3730","rentAddress":"滨江区南环路3730","ID":15127,"rentTime":"2016-11-02 09:02:21","timeCost":13,"returnTime":"2016-11-02 09:02:34","tradeId":15127,"returnAddress":"滨江区南环路3730"}]
     */

    private ResultBean info;

    public ResultBean getInfo() {
        return info;
    }

    public void setInfo(ResultBean info) {
        this.info = info;
    }

    public static class ResultBean {
        private int total;
        /**
         * endPoint : 滨江区南环路3730
         * tradeSum : 0.0
         * bikeNo : 001-02013
         * bikeId : 42
         * startPoint : 滨江区南环路3730
         * rentAddress : 滨江区南环路3730
         * ID : 15127
         * rentTime : 2016-11-02 09:02:21
         * timeCost : 13
         * returnTime : 2016-11-02 09:02:34
         * tradeId : 15127
         * returnAddress : 滨江区南环路3730
         */

        private ArrayList<RecordBean> recordList;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public ArrayList<RecordBean> getRecordList() {
            return recordList;
        }

        public void setRecordList(ArrayList<RecordBean> recordList) {
            this.recordList = recordList;
        }
    }
}
