package cn.qimate.bike.model;

/**
 * Created by Administrator1 on 2017/2/13.
 */

public class HistoryRoadBean {

    private String oid;
    private String osn;
    private String prices;
    private String codenum;
    private String st_time;
    private boolean isSelected = false;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getOsn() {
        return osn;
    }

    public void setOsn(String osn) {
        this.osn = osn;
    }

    public String getPrices() {
        return prices;
    }

    public void setPrices(String prices) {
        this.prices = prices;
    }

    public String getCodenum() {
        return codenum;
    }

    public void setCodenum(String codenum) {
        this.codenum = codenum;
    }

    public String getSt_time() {
        return st_time;
    }

    public void setSt_time(String st_time) {
        this.st_time = st_time;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
