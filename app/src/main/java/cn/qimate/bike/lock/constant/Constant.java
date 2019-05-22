package cn.qimate.bike.lock.constant;

import android.os.Environment;

import java.io.File;

import cn.qimate.bike.base.BaseApplication;

/**
 * 作者：王凯强 on 2017/1/06 11:07
 * <p>
 * 邮箱：317097478@qq.com
 */
public interface Constant {

    boolean isShowLog = true;

    //sp
    String TOKEN = "token";
    //当前登录用户
    String LOGIN_USER = "login_user";
    //当前骑行的单车
    String USING_CAR = "using_car";
    //是否有新评论
    String NEW_SUGGEST_FLAG = "new_suggest_flag";
    //个推ID
    String GETUI_ID = "getui_id";
    //微信APP_ID
    String APP_ID = "wxf3126c077e216335";//wxc0796004a1074abd

    String WELCOME_IMG_URL = "welcome_img_url";//网络路径
    String WELCOME_IMG_PATH = "welcome_img_path";//本地路径
    String WELCOME_IMG_LINK = "welcome_img_link";//活动地址
    String WELCOME_IMG_TITLE = "welcome_img_title";//活动标题

    //0 钱包支付 1 微信支付 2 支付宝支付 3 信用卡支付 4 银行卡支付 5 周卡支付 6 月卡支付 7 年卡支付 8 优惠券支付 9 免支付
    int PAY_WALLET = 0;
    int PAY_WX = 1;
    int PAY_ALI = 2;
    int PAY_CREDIT_CARD = 3;
    int PAY_BANK_CARD = 4;
    int PAY_WEEK_CARD = 5;
    int PAY_MONTH_CARD = 6;
    int PAY_YEAR_CARD = 7;
    int PAY_COUPON = 8;
    int PAY_FREE = 9;

    //1 押金 2 余额
    int YJ = 1;
    int YE = 2;
    //0 GPS+蓝牙 1 纯蓝牙 2 纯GPS
    int LOCK_TYPE_DEFAULT = 0;
    int LOCK_TYPE_BLE = 1;
    int LOCK_TYPE_GPS = 2;

    //正则表达式 二维码验证
    String REGEX_QR_CODE = "http://www.happybike.club/\\?b=\\d{9}";

    String BLE_SEARCH = "-2";

    String BLE_NOT_SEARCH = "-1";

    String BLE_START_CONNECT = "0";

    String BLE_CONNECT = "1";

    String BLE_DISCONNECT = "-3";

    String BLE_CONNECTED = "2";

    String BLE_TONKEN = "3";

    String BLE_OPEN_OK = "4";

    String BLE_OPEN_FAIL = "5";

    String BLE_CLOSE_NOT = "6";

    String BLE_CLOSE_OK = "7";

    String BLE_SEARCH_XB = "8";

    String BLE_NOT_XB = "9";

    String BLE_HAVE_XB = "10";

    String BLE_ZJ_START = "11";


    // 程序目录定义
    class Directorys {
        public static String SDCARD = Environment.getExternalStorageDirectory()
                .toString();
        public static final String ROOT = SDCARD + File.separator
                + BaseApplication.getInstance().getPackageName()+".fileprovider"
                + File.separator;
        public static final String TEMP = ROOT + "temp" + File.separator;

    }

}
