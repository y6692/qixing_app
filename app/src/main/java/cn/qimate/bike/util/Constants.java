package cn.qimate.bike.util;

import java.util.HashMap;
import java.util.Map;

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

    public static Map<String, String> keyMap= new HashMap<String, String>();

    static{
        keyMap.put("3C:A3:08:AE:BE:24", "eralHInialHInitwokPs5138m9pleBLEPeri4herzat4on8L0P1functP1functi7onSim9pInit2ali");
        keyMap.put("A4:34:F1:7B:BF:9A", "NDQzMDMyMzYzOTM5MzkzMzQxNDQzMzQxMzMzOTMyMzE0NTM3NDEzMzQzMzU0NTM4MzYzMDM1Mzk0MzAw");
        keyMap.put("50:F1:4A:B2:B0:83", "heralHInpleBLEPeri4heraleBLEPeri4heralHIonSim9pleBLEPerileBLEPeri4heralHfuncti7o");
        keyMap.put("38:D2:69:BF:3F:80", "ri4heralL0P1functi7onSimlHInitwokPs5138sat4on8L0P1functileBLEPeri4heralHri4heral");
        keyMap.put("38:D2:69:C8:51:45", "im9pleBLleBLEPeri4heralHzat4on8L0P1functat4on8L0P1functit2alizat4on8L0P1im9pleBL");
        keyMap.put("E8:EB:11:0D:64:84", "eralHIni2alizat4on8L0P1fn8L0P1functi7onSP1functi7onSim9plHInitwokPs5138sSim9pleB");
        keyMap.put("38:D2:69:C8:46:86", "alHInitwcti7onSim9pleBLEzat4on8L0P1functat4on8L0P1functiri4heralHInitwokalHInitw");
        keyMap.put("50:F1:4A:B5:CD:A1", "7onSim9pn8L0P1functi7onSEPeri4heralHInitonSim9pleBLEPeri8L0P1functi7onSinit2aliz");
        keyMap.put("3C:A3:08:BE:ED:5F", "kPs5138sralHInitwokPs513alHInitwokPs5138zat4on8L0P1funct0P1functi7onSim94on8L0P1");
        keyMap.put("3C:A3:08:AE:BE:24", "eralHInialHInitwokPs5138m9pleBLEPeri4herzat4on8L0P1functP1functi7onSim9pInit2ali");
        keyMap.put("3C:A3:08:CB:9D:50", "ri4heraln8L0P1functi7onS4on8L0P1functi7ozat4on8L0P1functpleBLEPeri4heraleralHIni");
        keyMap.put("3C:A3:08:CD:9F:47", "9pleBLEPL0P1functi7onSimn8L0P1functi7onSzat4on8L0P1functeBLEPeri4heralHIBLEPeri4");
    }
}
