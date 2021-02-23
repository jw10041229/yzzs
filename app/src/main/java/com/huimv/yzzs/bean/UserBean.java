package com.huimv.yzzs.bean;

/**
 * 与 webservice 交互时，转 json 的用户实体
 * 
 * @author zwl
 *
 */
public class UserBean {

	private String yhxm;
	private String yhmm;

	public String getYhxm() {
		return yhxm;
	}

	public void setYhxm(String yhxm) {
		this.yhxm = yhxm;
	}

	public String getYhmm() {
		return yhmm;
	}

	public void setYhmm(String yhmm) {
		this.yhmm = yhmm;
	}

	@Override
	public String toString() {
		return "UserBean [yhxm=" + yhxm + ", yhmm=" + yhmm + "]";
	}

}
