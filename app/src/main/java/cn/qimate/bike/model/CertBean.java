package cn.qimate.bike.model;

public class CertBean {
	private String user_name;	//姓名
	private String student_id;	//学号
	private int school_id;	//学校ID
	private String admission_time;	//入学时间
	private String identity_number;	//身份证号
	private String cert_photo;	//证件照
	private String holding_cert_photo;	//手持证件照
	private int status;	//认证状态 0待认证 1认证中 2已驳回 3认证成功

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getStudent_id() {
		return student_id;
	}

	public void setStudent_id(String student_id) {
		this.student_id = student_id;
	}

	public int getSchool_id() {
		return school_id;
	}

	public void setSchool_id(int school_id) {
		this.school_id = school_id;
	}

	public String getAdmission_time() {
		return admission_time;
	}

	public void setAdmission_time(String admission_time) {
		this.admission_time = admission_time;
	}

	public String getIdentity_number() {
		return identity_number;
	}

	public void setIdentity_number(String identity_number) {
		this.identity_number = identity_number;
	}

	public String getCert_photo() {
		return cert_photo;
	}

	public void setCert_photo(String cert_photo) {
		this.cert_photo = cert_photo;
	}

	public String getHolding_cert_photo() {
		return holding_cert_photo;
	}

	public void setHolding_cert_photo(String holding_cert_photo) {
		this.holding_cert_photo = holding_cert_photo;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
