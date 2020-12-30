package cn.qimate.bike.model;

public class BillBean {
	private int order_id;	//订单id
	private String order_sn = "";	//订单编号
	private String created_at = "";	//订单创建时间
	private int car_type;	//订单车辆类型 1单车 2助力车
	private String order_amount = "";	//订单金额
	private int order_state;	//订单状态 0已取消 10已下单 20进行中 30待支付 40已完成
	private int payment_id;	//支付方式ID
	private String payment_name = "";	//支付方式名称
	private String payment_time = "";	//支付时间
	private String car_number = "";	//车辆编号
	private String car_start_time = "";	//开始时间
	private String car_end_time = "";	//结束时间
	private String price = "";	//骑行起步价
	private String continued_price = "";	//骑行后续价
	private String credit_score_desc = "";	//
	private String cycling_time = "";	//骑行时间
	private String each_free_time = "";	//每次免费时长
	private int order_type;	//订单类型 1骑行订单 2套餐卡订单 3充值订单 4认证充值订单 5调度费订单 6赔偿费订单
	private int is_over_area;	//是否超区 1是 0否

	public int getOrder_id() {
		return order_id;
	}

	public void setOrder_id(int order_id) {
		this.order_id = order_id;
	}

	public String getOrder_sn() {
		return order_sn;
	}

	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public int getCar_type() {
		return car_type;
	}

	public void setCar_type(int car_type) {
		this.car_type = car_type;
	}

	public String getOrder_amount() {
		return order_amount;
	}

	public void setOrder_amount(String order_amount) {
		this.order_amount = order_amount;
	}

	public int getOrder_state() {
		return order_state;
	}

	public void setOrder_state(int order_state) {
		this.order_state = order_state;
	}

	public int getPayment_id() {
		return payment_id;
	}

	public void setPayment_id(int payment_id) {
		this.payment_id = payment_id;
	}

	public String getPayment_name() {
		return payment_name;
	}

	public void setPayment_name(String payment_name) {
		this.payment_name = payment_name;
	}

	public String getPayment_time() {
		return payment_time;
	}

	public void setPayment_time(String payment_time) {
		this.payment_time = payment_time;
	}

	public String getCar_number() {
		return car_number;
	}

	public void setCar_number(String car_number) {
		this.car_number = car_number;
	}

	public String getCar_start_time() {
		return car_start_time;
	}

	public void setCar_start_time(String car_start_time) {
		this.car_start_time = car_start_time;
	}

	public String getCar_end_time() {
		return car_end_time;
	}

	public void setCar_end_time(String car_end_time) {
		this.car_end_time = car_end_time;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getContinued_price() {
		return continued_price;
	}

	public void setContinued_price(String continued_price) {
		this.continued_price = continued_price;
	}

	public String getCredit_score_desc() {
		return credit_score_desc;
	}

	public void setCredit_score_desc(String credit_score_desc) {
		this.credit_score_desc = credit_score_desc;
	}

	public String getCycling_time() {
		return cycling_time;
	}

	public void setCycling_time(String cycling_time) {
		this.cycling_time = cycling_time;
	}

	public String getEach_free_time() {
		return each_free_time;
	}

	public void setEach_free_time(String each_free_time) {
		this.each_free_time = each_free_time;
	}

	public int getOrder_type() {
		return order_type;
	}

	public void setOrder_type(int order_type) {
		this.order_type = order_type;
	}

	public int getIs_over_area() {
		return is_over_area;
	}

	public void setIs_over_area(int is_over_area) {
		this.is_over_area = is_over_area;
	}
}
