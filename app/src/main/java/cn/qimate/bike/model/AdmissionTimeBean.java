package cn.qimate.bike.model;

public class AdmissionTimeBean {
	private int unix;	//当前时间戳
	private int count;	//向前推移多少年

	public int getUnix() {
		return unix;
	}

	public void setUnix(int unix) {
		this.unix = unix;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
