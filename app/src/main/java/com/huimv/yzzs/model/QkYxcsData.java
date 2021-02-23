package com.huimv.yzzs.model;

import java.io.Serializable;
import java.util.ArrayList;

public class QkYxcsData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String lx;//设备类型
	private String sx;//设备顺序
	private ArrayList<QkYxcsTimeData> timeList;
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
	public ArrayList<QkYxcsTimeData> getTimeList() {
		return timeList;
	}
	public void setTimeList(ArrayList<QkYxcsTimeData> timeList) {
		this.timeList = timeList;
	}
}
