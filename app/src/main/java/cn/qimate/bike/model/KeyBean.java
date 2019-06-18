package cn.qimate.bike.model;

/**
 * Created by Administrator1 on 2017/11/9.
 */

public class KeyBean {

    private int encryptionKey;
    private String keys;
    private int serverTime;

    public int getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(int encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public int getServerTime() {
        return serverTime;
    }

    public void setServerTime(int serverTime) {
        this.serverTime = serverTime;
    }
}
