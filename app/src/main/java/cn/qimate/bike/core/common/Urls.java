package cn.qimate.bike.core.common;

/**
 * 请求地址帮助类
 *
 * @author LiDongYao
 *
 * @version v1.0 2016-4-7
 */
public class Urls {

	public static String HTTP = "http://";
	public static String host = HTTP + "app.7mate.cn";
//	public static String host = HTTP + "test.7mate.cn";
//	public static String host = HTTP + "uat.7mate.cn";
//  public static String host = HTTP + "192.168.206.10:8345";
	/***上传坐标*/
	public static String locationHost = HTTP + "106.14.188.246";
	/**存入设备信息*/
	public static String DevicePostUrl = host + "/index.php?g=App&m=Login&a=verifyDevice_info";
	/**获取首页焦点图广告*/
	public static String bannerUrl = host + "/index.php?g=App&m=Index&a=getIndexAd";
	/**access_token登陆*/
	public static String accesslogin = host + "/index.php?g=App&m=Login&a=accesslogin";
	/**注册*/
	public static String register = host + "/index.php?g=App&m=Login&a=register";
	/**发送验证码*/
	public static String sendcode = host + "/index.php?g=App&m=Login&a=sendcode";
	/**账号密码登录*/
	public static String loginNormal = host + "/index.php?g=App&m=Login&a=loginNormal";
	/**短信验证码登录*/
	public static String loginCode = host + "/index.php?g=App&m=Login&a=loginCode";
	/**忘记密码*/
	public static String forgetpwd = host + "/index.php?g=App&m=Login&a=forgetpwd";
	/**用户信息*/
	public static String userIndex = host + "/index.php?g=App&m=User&a=userIndex";
	/**修改密码*/
	public static String alterPassword = host + "/index.php?g=App&m=User&a=alterPassword";
	/**变更手机号码*/
	public static String changetel = host + "/index.php?g=App&m=User&a=changetel";
	/**活动列表*/
	public static String activityList = host + "/index.php?g=App&m=Index&a=activityList";
	/**自动认证*/
	public static String autoauthentication = "http://jsut.qian-xue.com/student/checkxhxma";
	/**手动认证*/
	public static String authentication = host + "/index.php?g=App&m=User&a=authentication";
	/**学校列表*/
	public static String schoolList = host + "/index.php?g=App&m=Index&a=schoolList";
	/**上传图片*/
	public static String uploadsImg = host + "/index.php?g=App&m=Index&a=uploadsImg";
	/**意见反馈*/
	public static String feedback = host + "/index.php?g=App&m=Index&a=feedback";
	/**我的骑行记录列表*/
	public static String myOrderlist = host + "/index.php?g=App&m=User&a=myOrderlist";
	/**myOrderdetail*/
	public static String myOrderdetail = host + "/index.php?g=App&m=User&a=myOrderdetail";
	/**骑行记录详情地图*/
	public static String myOrdermap = host + "/index.php?g=App&m=UserJourney&a=index";
	/**修改用户信息*/
	public static String editUserinfo = host + "/index.php?g=App&m=User&a=editUserinfo";
	/**上传头像*/
	public static String uploadsheadImg = host + "/index.php?g=App&m=User&a=uploadsheadImg";
	/**充值选项列表*/
	public static String rechargeList = host + "/index.php?g=App&m=Index&a=rechargeList";
	/**我的积分记录*/
	public static String myPointslog = host + "/index.php?g=App&m=User&a=myPointslog";
	/**消息列表*/
	public static String messageList = host + "/index.php?g=App&m=User&a=messageList";
	/**获取当前行程订单(未付款)*/
	public static String getCurrentorder = host + "/index.php?g=App&m=User&a=getCurrentorder";
	/**获取反馈意见状态*/
	public static String getFeedbackStatus = host + "/index.php?a=feedback_status&m=Index&g=App";
	/**充值记录*/
	public static String rechargeLog = host + "/index.php?g=App&m=User&a=rechargeLog";
	/**获取认证信息状态*/
	public static String getAuthentication = host + "/index.php?g=App&m=User&a=getAuthentication";
	/**用户充值,提交充值订单*/
	public static String userRecharge = host + "/index.php?g=App&m=User&a=userRecharge";
	/**支付宝付款*/
	public static String alipayType = host + "/index.php?g=App&m=Alipay&a=alipay";
	/**扫码用车*/
	public static String useCar = host + "/index.php?g=App&m=User&a=useCar";
	/**结束用车*/
	public static String backBikescan = host + "/index.php?g=App&m=User&a=backBikescan";
	/**余额支付行程订单*/
	public static String orderPaybalance = host + "/index.php?g=App&m=User&a=orderPaybalance";
	/**退出登录*/
	public static String logout = host + "/index.php?g=App&m=Login&a=logout";
	/**上传骑行坐标*/
	public static String addMaplocation = locationHost + "/index.php?g=App&m=User&a=addMaplocation";
	/**,蓝牙锁开锁成功,添加骑行订单*/
	public static String addOrderbluelock = host+"/index.php?g=App&m=User&a=addOrderbluelock";
	/**使用帮助(H5)*/
	public static String useHelp = host + "/index.php?g=App&m=Index&a=isee";
	/**活动详情(H5)*/
	public static String activityDetail = host + "/index.php?g=App&m=Index&a=activitydetail";
	/**关于我们(H5)*/
	public static String aboutUs = host + "/index.php?g=App&m=Index&a=about";
	/**积分规则(H5)*/
	public static String pointRule = host + "/index.php?g=App&m=Index&a=pointsrole";
	/**学校范围电子栅栏*/
	public static String schoolrangeList = host + "/index.php?g=App&m=Index&a=schoolrangeList";
	/**学校范围电子栅栏*/
	public static String schoolRange = host + "?g=App&m=SchoolRange&a=index";
	/** 版本检测更新 */
	public static String updateApp = host + "/index.php?g=App&m=Index&a=android";
	/**获取启动页图广告*/
	public static String getIndexAd = host + "/index.php?g=App&m=Index&a=getIndexAd";
	/**
	 *
	 * 蓝牙锁使用帮助
	 *
	 * */
	public static String bluecarisee = host + "/index.php?g=App&m=Index&a=bluecarisee";
	/**用户协议*/
	public static String useragreement = host + "/index.php?g=App&m=Index&a=useragreement";
	/**充值协议 h5地址*/
	public static String rechargeDeal = host + "/index.php?g=App&m=Index&a=recharge";
	/**附近车接口*/
	public static String nearby = host + "/index.php?g=App&m=Index&a=nearby";
	/**兑换码激活*/
	public static String activation = host + "/index.php?g=App&m=User&a=activation";
	/**规则接口*/
	public static String account_rules = host + "/index.php?g=App&m=UserMonth&a=account_rules";
	/**月卡支付*/
	public static String monthcard = host + "/index.php?g=App&m=UserMonth&a=monthcard_school";

	public static String monthAlipay = host + "/index.php?g=App&m=AlipayMonth&a=alipay";
	/**微信支付新接口*/
	public static String wxpay = host + "/index.php?g=App&m=WxpayMonth&a=wxpay";//月卡
	public static String wxpay1 = host + "/index.php?g=App&m=Wxpay&a=wxpay"; //充值
	/**停车点*/
	public static String stopSite = host + "/index.php?a=pmaps&m=Index&g=App";
	/**t停车点H5*/
	public static String phtml5 = host + "/index.php?g=App&m=Helper&a=phtml5&uid=";
	/**t电单车停车点H5*/
	public static String ebike_phtml5 = host + "/index.php?g=App&m=Helper&a=ebike_phtml5&uid=";
	/**获取身份证信息*/
	public static String useinfo = host + "/index.php?g=App&m=UserCard&a=cardinfo";
	/**提交身份证信息*/
	public static String postUseinfo = host + "/index.php?g=App&m=UserCard&a=postCardinfo";
	public static String inviteCode = host + "/index.php?g=App&m=UserInviter&a=index";
	public static String commissionRecord = host + "/index.php?g=App&m=UserInviter&a=commission";
	public static String commissionTXRecord = host + "/index.php?g=App&m=UserInviter&a=cashlog";
	public static String myMsg = host + "/index.php?g=App&m=UserInviter&a=referrer";
	public static String applyCash = host + "/index.php?g=App&m=UserInviter&a=cash";
	/**余额充值余额**/
	public static String payMonth = host + "/index.php?g=App&m=UserMonth&a=payMonth";
	/**获取年级接口**/
	public static String gradeList = host + "/index.php?a=get_grade_list&m=Index&g=App";
	/**获取月卡配置接口*/
	public static String userMonth = host + "/index.php?a=month_card_set&m=UserMonth&g=App";
	/**月卡首页接口*/
	public static String userMonthIndex = host + "?g=App&m=userMonth&a=index";

	/**3合1锁再次开锁*/
	public static String openAgain = host + "/index.php?g=App&m=User&a=open_again";
	/**判断当前锁是否为关闭*/
	public static String carClose = host + "/index.php?g=App&m=User&a=car_close";
	/**电单车关锁*/
	public static String closeEbike = host + "/index.php?g=App&m=User&a=closeEbike";
	/**电单车开锁*/
	public static String openEbike = host + "?g=App&m=User&a=openEbike";

	/**坏车列表*/
	public static String badcarList = host + "/index.php?g=App&m=UserManage&a=badcar_list";
	/**坏车详情*/
	public static String badcarShow = host + "/index.php?g=App&m=UserManage&a=badcar_show";

    /**电单车信息*/
	public static String ebikeInfo = host + "?g=App&m=Ebike&a=info";
//    public static String ebikeInfo = host + "/index.php?g=App&m=Ebike&a=info";


	/**支付宝支付单车费用接口*/
	public static String alipayBike = host + "?g=App&m=AlipayBike&a=alipay";
	/**微信支付单车费用接口*/
	public static String wxpayBike = host + "?g=App&m=WxpayBike&a=wxpay";
}
