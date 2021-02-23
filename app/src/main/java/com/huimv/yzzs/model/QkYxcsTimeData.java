package com.huimv.yzzs.model;

import java.io.Serializable;

public class QkYxcsTimeData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String startTime;//开始时间
	private String continueTime;//持续时间
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getContinueTime() {
		return continueTime;
	}
	public void setContinueTime(String continueTime) {
		this.continueTime = continueTime;
	}
}
