package cn.qimate.bike.util;

/**
 * Created by lan on 2016/6/29.
 */
public class Constants {

    public static final String DEFAULT_CACHE_DIR = "smartlock";
    private static final String IP = "alabike.luopingelec.com";
    private static final String HTTP = "https://" + IP;
    public static final String URL = HTTP + "/alabike/ab_mapp";

    public static final int CONNECT_TIME_OUT = 3;
    public static final int SOCKET_TIME_OUT = 30;
    public static final int cachSize = 10*1024*1024;
    public static final int FILE_SOCKET_TIME_OUT = 10;
    public static final int PAGE_SIZE = 20;         //列表数据加载条数

}
