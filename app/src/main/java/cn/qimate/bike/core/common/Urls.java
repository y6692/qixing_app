package cn.qimate.bike.core.common;

/**
 * 请求地址帮助类
 *
 * @author LiDongYao
 *
 * @version v1.0 2016-4-7
 */
public class Urls {
//	https://newmapi.7mate.cn/
	public static String HTTP = "https://";
	public static String host = HTTP + "app.7mate.cn";
	public static String host2 = HTTP + "newmapi.7mate.cn/api";	    //https://newmapi.7mate.cn/api
//    public static String host2 = HTTP + "test-mapi.7mate.cn/api";
//	public static String host2 = HTTP + "dev-mapi.7mate.cn/api";

//	public static String host = HTTP + "web1.7mate.cn";
//	public static String host = HTTP + "test.7mate.cn";
//	public static String host = HTTP + "uat.7mate.cn";
//  public static String host = HTTP + "192.168.206.10:8345";
//  public static String host2 = HTTP + "dev-mapi.7mate.cn/api";

	//注册登录接口、退出登录接口delete
	public static String authorizations = host2 + "/authorizations";
	//获取验证码接口
	public static String verificationcode = host2 + "/verificationcode";
	//获取上传凭证接口
	public static String uploadtoken = host2 + "/qiniu/uploadtoken";
	//提交认证接口_post、认证信息接口_get
	public static String cert = host2 + "/cert";
	//申诉接口
	public static String appeal = host2 + "/appeal";
	//学校列表接口
	public static String schools = host2 + "/schools";
	//入学时间选项接口
	public static String admission_time = host2 + "/admission_time";

    //log
    public static String log = host2 + "/log";

	//换绑手机接口
	public static String change_phone = host2 + "/user/change_phone";



    //广告接口
    public static String agreement = host2 + "/agreement/";
	//广告接口
	public static String banner = host2 + "/banner/";

	//运营区域接口
	public static String operating_areas = host2 + "/operating_areas";
	//电子围栏接口
	public static String parking_ranges = host2 + "/parking_ranges";
	//充值价格列表
	public static String recharge_prices = host2 + "/recharge_prices";
	//套餐卡列表接口
	public static String cycling_cards = host2 + "/cycling_cards";
	//我的套餐卡接口
	public static String my_cycling_cards = host2 + "/user/cycling_cards";
	//我的套餐卡接口
	public static String car_authority = host2 + "/user/car_authority";
	//车辆信息接口
	public static String car = host2 + "/car/";
	//车辆位置接口
	public static String location = host2 + "/car/";
	//下单权限接口
	public static String order_authority = host2 + "/order/authority";
	//下单接口
	public static String order = host2 + "/order";
	//上报车锁状态接口
	public static String car_notification = host2 + "/order/car_notification";
	//当前骑行订单接口
	public static String cycling = host2 + "/order/cycling";
	//当前其他订单（调度单、赔偿单）接口
	public static String other = host2 + "/order/other";
	//上锁接口
	public static String lock = host2 + "/car/lock";
	//开锁接口
	public static String unlock = host2 + "/car/unlock";
	//泺平锁获取开锁秘钥接口
	public static String rent = host2 + "/car/lp/rent";
	//提交问题反馈接口
	public static String feedback = host2 + "/feedback";
	//停车点接口
	public static String parkings = host2 + "/parkings";
	//人工客服接口
	public static String service_phones = host2 + "/service/phones";
	//GET:用户信息接口	PUT:更新用户信息
	public static String user = host2 + "/user";
	//订单/账单列表接口
	public static String orders = host2 + "/orders";
	//订单/账单列表接口
	public static String pay = host2 + "/payment/pay";
	//我的消息列表接口
	public static String notices = host2 + "/notices";
	//客服中心接口
	public static String services = host2 + "/services";
	//人工客服接口
	public static String phones = host2 + "/service/phones";
	//查询支付状态接口
	public static String query_order = host2 + "/payment/query_order";
	//获取支付方式接口
	public static String payments = host2 + "/payments";
	//骑行订单详情接口
	public static String order_detail = host2 + "/order/cycling/";
	//兑换套餐卡接口
	public static String exchange = host2 + "/cycling_card/exchange";
	//检测新版本接口
	public static String version = host2 + "/version";
	//已读消息上报接口
	public static String notification = host2 + "/notice/notification";
	//是否可再次开锁接口
	public static String car_can_unlock = host2 + "/car_can_unlock";
	//附近的车辆接口
	public static String car_nearby = host2 + "/car/";
	//关于我们接口
	public static String aboutus = host2 + "/aboutus";
	//押金价格接口
	public static String depositprice = host2 + "/depositprice";

	//网络锁(自动还车场景)临时上锁接口
	public static String temp_lock = host2 + "/order/temp_lock";

	//用户相关 - 学院列表
	public static String colleges = host2 + "/user/school/colleges";

	//会员积分体系
	// 积分任务列表接口
	public static String points_tasks = host2 + "/points/tasks";
	// 积分兑换列表接口
	public static String points_exchange_lists = host2 + "/points/exchange_lists";
	// 积分兑换接口
	public static String points_exchange = host2 + "/points/exchange";
	// 积分兑换记录接口
	public static String points_exchange_records = host2 + "/user/points_exchange_records";
	// 积分明细接口
	public static String points_records = host2 + "/user/points_records";
	// 积分首页接口
	public static String points_index = host2 + "/points/index";
	// 签到接口
	public static String signin = host2 + "/signin";
	// 骑行次数排行榜
	public static String user_rank_cycling = host2 + "/user_ranks/cycling";

//	http://testnewmapi.7mate.cn/api/order/temp_lock
//	http://testnewmapi.7mate.cn/api/depositprice
//	http://testnewmapi.7mate.cn/api/aboutus
//	http://testnewmapi.7mate.cn/api/car/{carmodel_id}/nearby
//	http://testnewmapi.7mate.cn/api/car_can_unlock
//	http://testnewmapi.7mate.cn/api/notice/notification
//	http://testnewmapi.7mate.cn/api/version
//	http://testnewmapi.7mate.cn/api/user/change_phone
//	http://testnewmapi.7mate.cn/api/schools
//  http://testnewmapi.7mate.cn/api/agreement/{name}
//	http://testnewmapi.7mate.cn/api/order/other
//	http://testnewmapi.7mate.cn/api/payment/query_order
//	http://testnewmapi.7mate.cn/api/cycling_card/exchange
//	http://testnewmapi.7mate.cn/api/appeal
//	http://testnewmapi.7mate.cn/api/authorizations
//	http://testnewmapi.7mate.cn/api/order/cycling/{id}
//	http://testnewmapi.7mate.cn/api/user
//	http://testnewmapi.7mate.cn/api/payments
//	http://testnewmapi.7mate.cn/api/service/phones
//	http://testnewmapi.7mate.cn/api/services
//	http://testnewmapi.7mate.cn/api/notices
//	http://testnewmapi.7mate.cn/api/payment/pay
//	http://testnewmapi.7mate.cn/api/orders
//	http://testnewmapi.7mate.cn/api/banner/{position}
//	http://testnewmapi.7mate.cn/api/parking_ranges
//	http://testnewmapi.7mate.cn/api/authorizations
//	http://testnewmapi.7mate.cn/api/user
//	http://testnewmapi.7mate.cn/api/service/phones
//	http://testnewmapi.7mate.cn/api/parkings
//	http://testnewmapi.7mate.cn/api/feedback
//	http://testnewmapi.7mate.cn/api/agreement/{name}
//	http://testnewmapi.7mate.cn/api/banner/{position}
//	http://testnewmapi.7mate.cn/api/car/lp/rent
//	http://testnewmapi.7mate.cn/api/car/unlock
//	http://testnewmapi.7mate.cn/api/car/lock
//	http://testnewmapi.7mate.cn/api/order/cycling
//	http://testnewmapi.7mate.cn/api/order/car_notification
//	http://testnewmapi.7mate.cn/api/order
//	http://testnewmapi.7mate.cn/api/order/authority
//	http://testnewmapi.7mate.cn/api/car/{number}
//	http://testnewmapi.7mate.cn/api/user/car_authority
//	http://testnewmapi.7mate.cn/api/user/cycling_cards
//	http://testnewmapi.7mate.cn/api/cycling_cards
//	http://testnewmapi.7mate.cn/api/recharge_prices
//	http://testnewmapi.7mate.cn/api/operating_areas
//	http://testnewmapi.7mate.cn/api/cert
//	http://testnewmapi.7mate.cn/api/qiniu/uploadtoken
//	http://testnewmapi.7mate.cn/api/authorizations


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
	/**认证接口*/
	public static String certification = host + "/index.php?g=App&m=User&a=certification";
	/**学校列表*/
	public static String schoolList = host + "/index.php?g=App&m=Index&a=schoolList";
	/**上传图片*/
	public static String uploadsImg = host + "/index.php?g=App&m=Index&a=uploadsImg";
	/**意见反馈*/
//	public static String feedback = host + "/index.php?g=App&m=Index&a=feedback";
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
	/**获取认证信息状态*/
	public static String getCertification = host + "/index.php?g=App&m=User&a=getCertification";
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
	public static String schoolRange = host + "/index.php?g=App&m=SchoolRange&a=index";
	/** 版本检测更新 */
//	public static String updateApp = "http://web1.7mate.cn/index.php?g=App&m=Index&a=android";
	public static String updateApp = host + "/index.php?g=App&m=Index&a=android";
	/**获取启动页图广告*/
	public static String getIndexAd = host + "/index.php?g=App&m=Index&a=getIndexAd";
	/**
	 * 蓝牙锁使用帮助
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
	public static String userMonthIndex = host + "/index.php?g=App&m=userMonth&a=index";

	/**3合1锁再次开锁*/
	public static String openAgain = host + "/index.php?g=App&m=User&a=open_again";
	/**判断当前锁是否为关闭*/
	public static String carClose = host + "/index.php?g=App&m=User&a=car_close";
	/**电单车关锁*/
	public static String closeEbike = host + "/index.php?g=App&m=User&a=closeEbike";
	/**电单车开锁*/
	public static String openEbike = host + "/index.php?g=App&m=User&a=openEbike";

	/**坏车列表*/
	public static String badcarList = host + "/index.php?g=App&m=UserManage&a=badcar_list";
	/**坏车详情*/
	public static String badcarShow = host + "/index.php?g=App&m=UserManage&a=badcar_show";

    /**电单车信息*/
	public static String ebikeInfo = host + "/index.php?g=App&m=Ebike&a=info";
//    public static String ebikeInfo = host + "/index.php?g=App&m=Ebike&a=info";


	/**支付宝支付单车费用接口*/
	public static String alipayBike = host + "/index.php?g=App&m=AlipayBike&a=alipay";
	/**微信支付单车费用接口*/
	public static String wxpayBike = host + "/index.php?g=App&m=WxpayBike&a=wxpay";
	/**泺平锁加密接口*/
//	public static String rent = host + "/index.php?g=App&m=Index&a=rent";

	/**常见热门问题接口*/
	public static String question = host + "/index.php?g=App&m=Question&a=index";
	/**电单车位置信息接口*/
//	public static String location = host + "/index.php?g=App&m=Ebike&a=location";

    /**电单车位置信息接口*/
//    http://app.7mate.cn/index.php?g=App&m=Login&a=verifyDevice_info&act=1&platform=Android&version=1.7.7

	/**用户事件接口*/
	public static String memberEvent = host + "/index.php?g=App&m=SensorsData&a=memberEvent";
}
