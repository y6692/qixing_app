package cn.qimate.bike.model;

public class PointsIndexBean {

	private int user_points;	//用户积分
	private int user_cert1_status;	//免押金认证状态 免押金认证状态 0待认证 1认证中 2已驳回 3认证成功
	private int user_cert2_status;	//充值认证状态 0待认证 1认证中 2已驳回 3认证成功
	private String points_rule = "";	//积分规则

	public int getUser_points() {
		return user_points;
	}

	public void setUser_points(int user_points) {
		this.user_points = user_points;
	}

	public int getUser_cert1_status() {
		return user_cert1_status;
	}

	public void setUser_cert1_status(int user_cert1_status) {
		this.user_cert1_status = user_cert1_status;
	}

	public int getUser_cert2_status() {
		return user_cert2_status;
	}

	public void setUser_cert2_status(int user_cert2_status) {
		this.user_cert2_status = user_cert2_status;
	}

	public String getPoints_rule() {
		return points_rule;
	}

	public void setPoints_rule(String points_rule) {
		this.points_rule = points_rule;
	}
}
