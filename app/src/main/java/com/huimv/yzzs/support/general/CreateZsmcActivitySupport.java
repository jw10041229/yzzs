package com.huimv.yzzs.support.general;

import android.content.Context;
import android.os.Handler;

import com.huimv.yzzs.bean.ZsxxBean;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.db.entity.Da_mc;
import com.huimv.yzzs.db.helper.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.huimv.yzzs.thread.YzzsServiceThread;
import com.huimv.yzzs.webservice.WsCmd;

/**
 * 机器id数据上传的帮助类
 * 
 * @author zwl
 *
 */
public class CreateZsmcActivitySupport {

	/**
	 * 通过新建猪舍名称，向平台发送获取机器id的webservice请求
	 * 
	 * @param param
	 *            请求参数
	 * @param context
	 *            上下文
	 * @param handler
	 *            handler
	 */
	public static void getJqidDataByNewZsmcThread(String param, Context context, Handler handler) {
		new YzzsServiceThread(WsCmd.HM_JQID_GET, param, "", XtAppConstant.WHAT_JQID_GET, handler, context).start();
	}

	/**
	 * 解析从平台获取的 机器id
	 * 
	 * @param data
	 *            json数据
	 * @param context
	 *            上下文
	 * @return 猪舍信息：zsid，jqid
	 * @throws JSONException
	 */
	public static ZsxxBean doJqidDataParsing(String data, Context context) throws JSONException {
		// jsonArray中只有一条数据
		JSONArray jsonArray = new JSONArray(data);
		ZsxxBean zsxxBean = new ZsxxBean();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			zsxxBean.setZsid(jsonObject.getString("zsid"));
			zsxxBean.setJqid(jsonObject.getString("jqid"));
		}
		return zsxxBean;
	}

	/**
	 * 删除全部Mc 数据
	 * 
	 * @param context
	 *            上下文
	 */
	public static void deleteAllMc(Context context) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		dbHelper.deleteAllMc();
	}

	/**
	 * 保存 牧场+猪舍信息 到数据库
	 * 
	 */
	public static void saveMc2DB(Context context, Da_mc daMc) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		dbHelper.insertMc(daMc);
	}

	/**
	 * 得到数据库中的全部 Mc 列表
	 *
	 * @param context
	 * @return
	 */
	public static List<Da_mc> getAllMc(Context context) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		return dbHelper.loadAllMc();
	}

	/**
	 * 根据猪舍id查询该猪舍的机器id，回显到界面上
	 * 
	 * @param context
	 *            上下文
	 * @param zsid
	 *            猪舍id
	 * @return
	 */
	public static String getJqidByZsid(Context context, String zsid) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		List<Da_mc> list = dbHelper.getJqidByZsid(zsid);
		if (list.size() > 0) {
			Da_mc daMc = list.get(0);
			String jqid = daMc.getJqid();
			if (jqid == null || "".equals(jqid)) {
				return "";
			} else {
				return jqid;
			}
		} else {
			return "";
		}
	}

	/**
	 * 根据猪舍名称查询该猪舍的机器id，回显到界面上
	 * 
	 * @param context
	 *            上下文
	 * @param zsmc
	 *            猪舍名称
	 * @return
	 */
	public static String getJqidByZsmc(Context context, String zsmc) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		List<Da_mc> list = dbHelper.getJqidByZsmc(zsmc);
		if (list.size() > 0) {
			Da_mc daMc = list.get(0);
			String jqidStr = daMc.getJqid();
			if (jqidStr == null || "".equals(jqidStr)) {
				return "";
			} else {
				return jqidStr;
			}
		} else {
			return "";
		}
	}

	/**
	 * 根据机器id获取 mc 信息，回显界面
	 * 
	 * @param jqid
	 * @param context
	 * @return
	 */
	public static Da_mc getMcByJqid(Context context, String jqid) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		List<Da_mc> list = dbHelper.getMcByJqid(jqid);
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return new Da_mc();
		}
	}

	/**
	 * 
	 * @param context
	 *            上下文
	 * @return false：该牧场中存在猪舍；true：该牧场中没有猪舍
	 */
	public static boolean isHasNoZs(Context context) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		List<Da_mc> daMcList = dbHelper.loadAllMc();
		for (Da_mc daMc : daMcList) {
			// Mc 表中如果有猪舍，则 zsid 一定不为空；如果 zsid 为空，说明该牧场下没有猪舍
			if (daMc.getZsid() == null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据 牧场名称 获取该牧场名称下的 所有猪舍列表
	 * 
	 * @param context
	 *            上下文
	 * @param mcmc
	 *            牧场名称
	 * @return
	 */
	public static List<String> getZslbByMcmc(Context context, String mcmc) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		List<Da_mc> daMclist = dbHelper.getMcByMcmc(mcmc);
		// 保存该牧场名称下所有猪舍列表
		List<String> zslbList = new ArrayList<>();
		for (Da_mc mc : daMclist) {
			zslbList.add(mc.getZsmc());
		}
		return zslbList;
	}
}
