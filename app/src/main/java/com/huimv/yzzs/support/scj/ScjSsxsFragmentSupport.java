package com.huimv.yzzs.support.scj;

import android.content.Context;
import android.os.Handler;

import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.db.entity.Da_scj;
import com.huimv.yzzs.db.helper.DBHelper;
import com.huimv.yzzs.thread.YzzsServiceThread;
import com.huimv.yzzs.webservice.WsCmd;

import java.util.List;

/**
 * @author jw
 * @version 1.0
 */
public class ScjSsxsFragmentSupport {
	/**
	 * 删除SCJ数据全部
	 * 
	 * @param context
	 */
	public void deleteAllScj(Context context) {
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.deleteAllDa_scj();
	}
	/**
	 * 根据实体类集合删除数据
	 * @param context
	 * @param da_scjEntitys
	 */
	public void deleteAllScj(Context context,List<Da_scj> da_scjEntitys) {
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.deleteDa_scjs(da_scjEntitys);
	}
	/**
	 * 获取SCJ数据全部
	 * 
	 * @param context
	 */
	public List<Da_scj> getAllScj(Context context) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.loadAllDa_Scj();
	}

	/**
	 * 根据耳标获取SCJ数据全部
	 *
	 * @param context
	 */
	public List<Da_scj> getScjByRfid(Context context,String rfid) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.loadAllDa_ScjByRfid(rfid);
	}
	/**
	 * 获取SCJ数据全部
	 * 
	 * @param context
	 */
	public List<Da_scj> getAllScjLimit(Context context,int limit) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.loadAllDa_ScjLimit(limit);
	}
	/**
	 * 插入单条SCJ数据
	 * 
	 * @param context
	 */
	public void insertScj(Context context, Da_scj da_scjEntity) {
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.scj_insertScj(da_scjEntity);
	}
	/**
	 * 删除数据
	 * @param context
	 */
	public void deleteScjData(Context context,Da_scj da_scjEntity) {
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.deleteDa_scj(da_scjEntity);
	}
	/** 上传 **/
	public void scjUploadThread(String param, Context context, Handler handler) {
		new YzzsServiceThread(WsCmd.HM_SCJ_DATA_UPLOAD, param, "", 
				XtAppConstant.SCJ_UPLOAD_VISION, handler, context).start();
	}
}
