package cn.qimate.bike.model;

public class TaskBean {

	String name;	//任务名称 唯一标识 根据此字段获取相关任务信息 cert：免押金任务 info：个人信息任务 signin：签到任务 cycling_bike：骑行单车任务 cycling_ebike：骑行助力车任务 cycling_bike_7：连续7天骑行单车任务 cycling_ebike_7：连续7天骑行助力车任务 buy_card_1：购买周卡、月卡、季卡任务 buy_card_2：购买购买半年卡、年卡任务
	String title;	//任务标题
	String desc;	//任务描述
	String icon;	//任务图标
	String process;	//任务进度 当该字段为字符0时，进度按钮文本应为做任务或者去购买；当该字段为字符1时，进度按钮文本应为已完成；其他情况原样输出进度文本；
	String process_details;	//任务进度详情 JSON字符串。对于签到任务，time：代表时间；is_signin：代表该天是否已签到；points：代表奖励积分

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getProcess_details() {
		return process_details;
	}

	public void setProcess_details(String process_details) {
		this.process_details = process_details;
	}
}
