package com.huimv.yzzs.model;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * 运行参数 
 * @author jiangwei
 *
 */
public class HkYxcsDatas implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7939599044911665639L;
	private ArrayList <HkYxcsData> mHk_yxcsList;
	public ArrayList<HkYxcsData> getmHk_yxcsList() {
		return mHk_yxcsList;
	}
	public void setmHk_yxcsList(ArrayList<HkYxcsData> mHk_yxcsList) {
		this.mHk_yxcsList = mHk_yxcsList;
	}
}
