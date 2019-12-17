package cn.qimate.bike.model;

public class PaymentBean {
	private int id;	//支付方式id
	private String name;	//支付方式名称
	private String icon;	//支付方式icon
	private String name_s;	//支付方式短名称


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName_s() {
		return name_s;
	}

	public void setName_s(String name_s) {
		this.name_s = name_s;
	}
}
