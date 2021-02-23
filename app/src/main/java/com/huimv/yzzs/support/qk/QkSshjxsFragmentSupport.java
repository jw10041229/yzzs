package com.huimv.yzzs.support.qk;


import android.content.Context;

import com.huimv.yzzs.db.helper.DBHelper;


/**
 * @author jw
 * @version 1.0
 */
public class QkSshjxsFragmentSupport {
	/**
	 * 删除水压全部
	 * @param context
	 */
	public void qk_deleteAllshuiya(Context context) {
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.qk_deleteAllshuiya();
	}
	/**
	 * 删除水流全部
	 * @param context
	 */
	public void qk_deleteAllshuiliu(Context context) {
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.qk_deleteAllshuiliu();
	}
	/**
	 * 删除Ph
	 * @param context
	 */
	public void qk_deleteAllPh(Context context) {
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.deleteAllDa_ph();
	}
}
