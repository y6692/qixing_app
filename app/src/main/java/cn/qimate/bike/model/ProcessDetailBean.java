package cn.qimate.bike.model;

public class ProcessDetailBean {

	int points;	//奖励积分
	int is_signin;	//该天是否已签到
	String time;	//时间
	int current;	//已连续签到天数

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getIs_signin() {
		return is_signin;
	}

	public void setIs_signin(int is_signin) {
		this.is_signin = is_signin;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}
}
