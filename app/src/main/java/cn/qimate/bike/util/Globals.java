package cn.qimate.bike.util;


public class Globals {
	public static String USERNAME = "";                 // 手机号
	public static String tempStr = "";                //临时登录信息

	public static boolean isBleFeature = false;      //当前是否支持蓝牙4.0
	public static boolean isBleConnected = false;   //当前蓝牙是否已连接
	public static String BLE_NAME = "";   //蓝牙名
	public static String BLE_ADDRESS = "";           //蓝牙地址

	public static int bType = 0;// 租车状态，0未租车，1正在租车，2订单未评价，(网络)
	public static String bikeNo = "";        //租车号

	public static boolean BLE_INIT = true;        //是否提醒蓝牙

}
