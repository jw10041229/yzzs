package com.huimv.yzzs.model;

import java.io.Serializable;

public class HkYxcsData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4274413786817912627L;
	private String tdkg;
	private String ksj;
	private String gsj;
	private String lx;
	private String sx;
	private String bbpge;//变频百分比

	public String getBbpge() {
		return bbpge;
	}

	public void setBbpge(String bbpge) {
		this.bbpge = bbpge;
	}

	public String getLx() {
		return lx;
	}
	public void setLx(String lx) {
		this.lx = lx;
	}
	public String getSx() {
		return sx;
	}
	public void setSx(String sx) {
		this.sx = sx;
	}
	public String getTdkg() {
		return tdkg;
	}
	public void setTdkg(String tdkg) {
		this.tdkg = tdkg;
	}
	public String getKsj() {
		return ksj;
	}
	public void setKsj(String ksj) {
		this.ksj = ksj;
	}
	public String getGsj() {
		return gsj;
	}
	public void setGsj(String gsj) {
		this.gsj = gsj;
	}
}
