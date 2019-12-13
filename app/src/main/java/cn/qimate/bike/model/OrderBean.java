package cn.qimate.bike.model;

public class OrderBean {

	private int order_id;	//订单ID
	private String order_sn;	//订单编号
	private String car_number;	//车辆编号
	private int lock_id;	//锁ID(原锁类型)
	private String car_lock_mac;
	private String estimated_cost;	//预估费用
	private String car_start_time;	//借车时间
	private String car_end_time;	//结束时间
	private String order_amount;	//订单金额
	private int order_state;	//订单状态 订单状态 0已取消 10已下单 20进行中 30待支付 40已完成
	private String electricity;	//订单金额
	private String mileage;	//订单金额


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

	public String getCar_number() {
		return car_number;
	}

	public void setCar_number(String car_number) {
		this.car_number = car_number;
	}

	public int getLock_id() {
		return lock_id;
	}

	public void setLock_id(int lock_id) {
		this.lock_id = lock_id;
	}

	public String getCar_lock_mac() {
		return car_lock_mac;
	}

	public void setCar_lock_mac(String car_lock_mac) {
		this.car_lock_mac = car_lock_mac;
	}

	public String getEstimated_cost() {
		return estimated_cost;
	}

	public void setEstimated_cost(String estimated_cost) {
		this.estimated_cost = estimated_cost;
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

	public String getElectricity() {
		return electricity;
	}

	public void setElectricity(String electricity) {
		this.electricity = electricity;
	}

	public String getMileage() {
		return mileage;
	}

	public void setMileage(String mileage) {
		this.mileage = mileage;
	}
}
