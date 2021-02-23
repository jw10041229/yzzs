package com.huimv.yzzs.bean;

public class ZsxxBean {

	private String zsid; // 猪舍id
	private String zsmc; // 猪舍名称
	private String jqid; // 机器ID

	public String getZsid() {
		return zsid;
	}

	public void setZsid(String zsid) {
		this.zsid = zsid;
	}

	public String getZsmc() {
		return zsmc;
	}

	public void setZsmc(String zsmc) {
		this.zsmc = zsmc;
	}

	public String getJqid() {
		return jqid;
	}

	public void setJqid(String jqid) {
		this.jqid = jqid;
	}

	@Override
	public String toString() {
		return "ZsxxBean [zsid=" + zsid + ", zsmc=" + zsmc + ", jqid=" + jqid + "]";
	}

}
