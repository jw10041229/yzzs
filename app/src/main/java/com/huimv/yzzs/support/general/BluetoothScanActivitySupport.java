package com.huimv.yzzs.support.general;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.db.entity.Da_bt;
import com.huimv.yzzs.db.entity.Da_mc;
import com.huimv.yzzs.db.helper.DBHelper;

import android.content.Context;
import android.os.Handler;

import com.huimv.yzzs.thread.YzzsServiceThread;
import com.huimv.yzzs.webservice.WsCmd;

/**
 * 蓝牙数据上传的帮助类
 * 
 * @author zwl
 *
 */
public class BluetoothScanActivitySupport {

	/**
	 * 通过用户名，密码获取牧场信息的线程
	 * 
	 * @param params
	 * @param context
	 * @param handler
	 */
	public static void getMcxxDataThread(String params, Context context, Handler handler) {
		new YzzsServiceThread(WsCmd.HM_MCXX_SELECT, params, "", XtAppConstant.WHAT_MCXX_SELECT, handler, context)
				.start();
	}

	/**
	 * 解析平台下发的牧场信息，并将数据保存到数据库
	 * 
	 * @param data
	 * @param context
	 * @throws JSONException
	 * @return 登录验证成功与否的返回消息
	 */
	public static boolean doMcxxDataParsing(String data, Context context) throws JSONException {
		List<Da_mc> daMcList = new ArrayList<>(); // 保存 牧场 的list
		List<Da_bt> daBtList = new ArrayList<>(); // 保存 蓝牙 的list
		JSONArray jsonArray1 = new JSONArray(data); // 第一层 json 数据
		// 平台返回的数据为空 --- 该账户下没有牧场信息
		if (jsonArray1.length() == 0) {
			return false;
		} else {
			for (int i = 0; i < jsonArray1.length(); i++) {
				JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
				// 第 2 层 json 数据
				JSONArray jsonArray2 = new JSONArray(jsonObject1.getString("mcxx"));
				for (int j = 0; j < jsonArray2.length(); j++) {
					Da_mc daMc = new Da_mc();
					daMc.setMcid(jsonObject1.getString("mcid"));
					daMc.setMcmc(jsonObject1.getString("mcmc"));
					JSONObject jsonObject2 = jsonArray2.getJSONObject(j);
					daMc.setZsid(jsonObject2.getString("zsid"));
					daMc.setZsmc(jsonObject2.getString("zsmc"));
					if (jsonObject2.has("jqid")) { // json 中存在关键字 jqxx
						daMc.setJqid(jsonObject2.getString("jqid"));
					} else {
						daMc.setJqid(null);
					}
					daMcList.add(daMc); // 将 牧场 添加到 daMcList 中
					if (jsonObject2.has("jqxx")) { // json 中存在关键字 jqxx
						// 第三层 json 数据
						JSONArray jsonArray3 = new JSONArray(jsonObject2.getString("jqxx"));
						for (int k = 0; k < jsonArray3.length(); k++) {
							Da_bt daBt = new Da_bt();
							daBt.setJqid(jsonObject2.getString("jqid"));
							JSONObject jsonObject3 = jsonArray3.getJSONObject(k);
							if (jsonObject3.has("lcid")) { // 如果 lcid 存在
								daBt.setLcid(jsonObject3.getString("lcid"));
							} else {
								daBt.setLcid(null);
							}
							daBt.setSblx(jsonObject3.getString("sblx"));
							daBt.setLydz(jsonObject3.getString("lydz"));
							daBt.setLybm(jsonObject3.getString("lybm"));
							daBt.setSjbz("1"); // 从平台获取到的蓝牙数据，数据标志位全部置 1
							daBtList.add(daBt); // 将 蓝牙 添加到 daBtList 中
						}
					}
				}
			}
			// ① 将 牧场数据 保存到手机数据库 --- 保存前先清空 Mc 表
			deleteAllMc(context);
			for (Da_mc mc : daMcList) {
				saveMc2DB(context, mc);
			}

			// ② 将 蓝牙数据 保存到手机数据库 --- 保存前先清空 Bt 表
			deleteAllBt(context);
			for (Da_bt daBt : daBtList) {
				saveBt2DB(context, daBt);
			}
			return true;
		}
		
	}

	/**
	 * 向平台发送上传蓝牙别名数据的线程
	 * 
	 * @param context
	 * @param handler
	 */
	public static void uploadLybmDataThread(String param, Context context, Handler handler) {
		new YzzsServiceThread(WsCmd.HM_LYBM_UPLOAD, param, "", XtAppConstant.WHAT_LYBM_UPLOAD, handler, context)
				.start();
	}

	/**
	 * 解析从平台下发的蓝牙别名列表
	 * 
	 * @param data
	 * @param context
	 * @return
	 * @throws JSONException
	 */
	public static List<Da_bt> doLybmDataParsing(String data, Context context) throws JSONException {
		List<Da_bt> daBtList = new ArrayList<>();
		JSONArray jsonArray = new JSONArray(data);
		for (int i = 0; i < jsonArray.length(); i++) {
			Da_bt daBt = new Da_bt();
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			daBt.setJqid(jsonObject.getString("jqid"));
			if (jsonObject.has("lcid")) { // 如果有 lcid
				daBt.setLcid(jsonObject.getString("lcid"));
			} else {
				daBt.setLcid(null);
			}
			daBt.setSblx(jsonObject.getString("sblx"));
			daBt.setLydz(jsonObject.getString("lydz"));
			daBt.setLybm(jsonObject.getString("lybm"));
			daBt.setSjbz("1"); // 平台返回的数据，数据标志位置 1
			daBtList.add(daBt);
		}
		return daBtList;
	}

	/**
	 * 保存蓝牙数据到数据库
	 * 
	 * @param context
	 *            上下文
	 * @param daBt
	 *            蓝牙数据DAO实体
	 */
	public static long saveBt2DB(Context context, Da_bt daBt) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		return dbHelper.insertBt(daBt);
	}

	/**
	 * 从数据库中获取所有蓝牙数据
	 * 
	 * @param context
	 *            上下文
	 * @return
	 */
	public static List<Da_bt> getAllBt(Context context) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		return dbHelper.loadAllBt();
	}

	/**
	 * 删除蓝牙数据表中所有数据
	 * 
	 * @param context
	 *            上下文
	 */
	public static void deleteAllBt(Context context) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		dbHelper.deleteAllBt();
	}

	/**
	 * 根据蓝牙地址，获取蓝牙别名
	 * 
	 * @param context
	 *            上下文
	 * @param lydz
	 *            蓝牙地址
	 * @return
	 */
	public static String getLybmByLydz(Context context, String lydz) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		List<Da_bt> list = dbHelper.getLybmByLydz(lydz);
		if (!list.isEmpty()) {
			Da_bt daBt = list.get(0);
			return daBt.getLybm();
		} else {
			return null;
		}
	}

	/**
	 * 根据蓝牙地址，获取该条数据的 id
	 * 
	 * @param context
	 * @param lydz
	 * @return
	 */
	public static long getIdByLymc(Context context, String lydz) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		List<Da_bt> list = dbHelper.getIdByLydz(lydz);
		if (!list.isEmpty()) {
			Da_bt daBt = list.get(0);
			return daBt.getId();
		} else {
			return 0;
		}
	}

	/**
	 * 根据 id 更新蓝牙数据
	 * 
	 * @param context
	 *            上下文
	 * @param newDaBt
	 *            新的蓝牙数据
	 * @param id
	 *            id
	 * @return
	 */
	public static long updateBtById(Context context, Da_bt newDaBt, long id) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		return dbHelper.updateBtById(newDaBt, id);
	}

	/**
	 * -------------------------------------------------------------------------
	 * --------
	 */

	/**
	 * 保存 牧场 到数据库
	 * 
	 * @param context
	 */
	private static void saveMc2DB(Context context, Da_mc daMc) {
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
	 * 通过机器ID获取 Mc 表中的牧场id
	 * 
	 * @param context
	 *            上下文
	 * @param jqid
	 *            机器id
	 * @return
	 */
	public static String getMcidByJqid(Context context, String jqid) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		List<Da_mc> list = dbHelper.getMcByJqid(jqid);
		if (!list.isEmpty()) {
			Da_mc daMc = list.get(0);
			return daMc.getMcid();
		} else {
			return "";
		}
	}

	/**
	 * 删除全部 Mc 数据
	 * 
	 * @param context
	 *            上下文
	 */
	private static void deleteAllMc(Context context) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		dbHelper.deleteAllMc();
	}

}
