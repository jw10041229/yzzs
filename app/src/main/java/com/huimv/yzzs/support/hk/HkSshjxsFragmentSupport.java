package com.huimv.yzzs.support.hk;


import android.content.Context;

import com.huimv.yzzs.db.helper.DBHelper;


/**
 * @author jw
 * @version 1.0
 */
public class HkSshjxsFragmentSupport {
	/**
	 * 删除温度全部
	 * @param context
	 */
	public void deleteAllWendu(Context context) {
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.deleteAllDa_wendu();
	}
	/**
	 * 删除湿度全部
	 * @param context
	 */
	public void deleteAllShidu(Context context) {
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.deleteAllDa_shidu();
	}
	
	/**
	 * 删除氨气全部
	 * @param context
	 */
	public void deleteAllAnqi(Context context) {
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.deleteAllDa_anqi();
	}
	/**
	 * 删除Ph全部
	 * @param context
	 */
	public void deleteAllPh(Context context) {
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.deleteAllDa_ph();
	}
}
