package com.huimv.yzzs.model;

import java.io.Serializable;

public class SbbdData implements Serializable {

	/**
	 * 设备绑定
	 */
	private static final long serialVersionUID = -6577709019870741985L;
	private String isBd;// 是否绑定
	private String sbLx;// 设备类型
	private String sbXh;// 设备序号
	private String tdKg;// 通道开关
	private String heKg;// 霍尔开关
	private String glz;//功率值
	private String fjIsBd;//风机是否绑定 ：包括普通风机与变频风机，用来判断是否需要轮训
	private String yxsj;//天窗,卷帘运行时间

	public String getYxsj() {
		return yxsj;
	}

	public void setYxsj(String yxsj) {
		this.yxsj = yxsj;
	}

	public String getFjIsBd() {
		return fjIsBd;
	}

	public void setFjIsBd(String fjIsBd) {
		this.fjIsBd = fjIsBd;
	}

	public String getGlz() {
		return glz;
	}

	public void setGlz(String glz) {
		this.glz = glz;
	}

	public String getIsBd() {
		return isBd;
	}

	public void setIsBd(String isBd) {
		this.isBd = isBd;
	}

	public String getSbLx() {
		return sbLx;
	}

	public void setSbLx(String sbLx) {
		this.sbLx = sbLx;
	}

	public String getSbXh() {
		return sbXh;
	}

	public void setSbXh(String sbXh) {
		this.sbXh = sbXh;
	}

	public String getTdKg() {
		return tdKg;
	}

	public void setTdKg(String tdKg) {
		this.tdKg = tdKg;
	}

	public String getHeKg() {
		return heKg;
	}

	public void setHeKg(String heKg) {
		this.heKg = heKg;
	}
}
