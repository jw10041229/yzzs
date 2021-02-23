package com.huimv.yzzs.model;

import java.io.Serializable;

public class SensorBaseData implements Serializable {

	private static final long serialVersionUID = 1L;

	private String sblx; // 设备类型：1：温度；2：湿度；3：温湿度；4：氨气
	private String sbbh; // 设备编号
	private String cyjg; // 采样间隔
	private String cgqdz; // 传感器地址
	private String kgzt; // 开关状态：1：打开；0：关闭
	private String azwz; // 安装位置

	public String getSblx() {
		return sblx;
	}

	public void setSblx(String sblx) {
		this.sblx = sblx;
	}

	public String getSbbh() {
		return sbbh;
	}

	public void setSbbh(String sbbh) {
		this.sbbh = sbbh;
	}

	public String getCyjg() {
		return cyjg;
	}

	public void setCyjg(String cyjg) {
		this.cyjg = cyjg;
	}

	public String getCgqdz() {
		return cgqdz;
	}

	public void setCgqdz(String cgqdz) {
		this.cgqdz = cgqdz;
	}

	public String getKgzt() {
		return kgzt;
	}

	public void setKgzt(String kgzt) {
		this.kgzt = kgzt;
	}

	public String getAzwz() {
		return azwz;
	}

	public void setAzwz(String azwz) {
		this.azwz = azwz;
	}

	@Override
	public String toString() {
		return "Hk_CgqbdDataTest3 [sblx=" + sblx + ", sbbh=" + sbbh + ", cyjg=" + cyjg + ", cgqdz=" + cgqdz + ", kgzt="
				+ kgzt + ", azwz=" + azwz + "]";
	}

}
