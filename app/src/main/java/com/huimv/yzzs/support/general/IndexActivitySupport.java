package com.huimv.yzzs.support.general;

import android.content.Context;

import com.huimv.yzzs.db.entity.Da_anqi;
import com.huimv.yzzs.db.entity.Da_hksj;
import com.huimv.yzzs.db.entity.Da_ph;
import com.huimv.yzzs.db.entity.Da_qk_shuiliu;
import com.huimv.yzzs.db.entity.Da_qk_shuiya;
import com.huimv.yzzs.db.entity.Da_shidu;
import com.huimv.yzzs.db.entity.Da_wendu;
import com.huimv.yzzs.db.helper.DBHelper;

import java.util.Arrays;
import java.util.List;



public class IndexActivitySupport {
	
	/** * --------------------------环控---------------------------------------- */
	/** TODO　数据解析
	 * @param context
	 * @param message
	 * @param sjlx 数据类型
	 */
	public void hkDataParsingAndInsert(Context context, byte[] message, String sjlx) {
		if (message[5] != 0) {//接受失败
			return;
		}
		byte [] removeData = Arrays.copyOfRange(message, 5, message.length);//去头
		String messStr = new String(removeData);
		Da_hksj mHksj = new Da_hksj();
		String value = messStr.substring(5, 8); // 数据
		String sj = messStr.substring(1, 5);// 时间
		String cgqsx = messStr.substring(8, 9);// 传感器顺序
		mHksj.setValue(String.valueOf(Double.valueOf(value) / 10));
		mHksj.setSj(sj.substring(0, 2) + ":" + sj.substring(2, 4));
		mHksj.setCgqsx(cgqsx);
		mHksj.setSjlx(sjlx);
		insertHkSj(context, mHksj); // 插入数据
		int sdCount = loadAllHksjBySjlxAndCgqsx(context, sjlx, cgqsx).size();
		// 保证数据表中只有 10 条最新数据
		if (sdCount > 10) {
			List<Da_hksj> hksjList = loadAllHksjByCgqsxAndSjlxLimit(context, sdCount - 10, cgqsx, sjlx);
			for (int i = 0; i < hksjList.size(); i++) {
				deleteHksj(context, hksjList.get(i));
			}
		}
	}
	
	/**
	 * 获取 同类型 同序号 全部环控数据
	 * @param context
	 * @param sjlx 数据类型
	 * @param cgqsx 传感器顺序
	 * @return
	 */
	public static List<Da_hksj> loadAllHksjBySjlxAndCgqsx(Context context,  String sjlx , String cgqsx){
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.loadAllHksjBySjlxAndCgqsx(sjlx, cgqsx);
	}
	
	/**
	 * 获取 同类型 同序号 全部环控数据 的前几条
	 * @param context
	 * @param i
	 * @param cgqsx 传感器序号
	 * @param sjlx 数据类型
	 * @return
	 */
	public List<Da_hksj> loadAllHksjByCgqsxAndSjlxLimit(Context context, int i, String cgqsx, String sjlx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.loadAllHksjBySjlxAndCgqsxLimit(i, cgqsx, sjlx);
	}
	
	/**
	 *  插入单个环控数据
	 * @param context
	 * @param da_hksjEntity
	 */
	private void insertHkSj(Context context, Da_hksj da_hksjEntity) {
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.insertHkSj(da_hksjEntity);
	}
	
	/**
	 *  从数据表中删除单个数据
	 * @param context
	 * @param mDa_hksj
	 */
	public void deleteHksj(Context context, Da_hksj mDa_hksj){
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.deleteHksj(mDa_hksj);
	}
	

	/**
	 * 氨气数据解析Ex
	 * @param context
	 * @param message
	 */
	public void insertAnqiDataEx (Context context,byte[] message) {
		if (message[5] != 0) {//接受失败
			return;
		}
		byte [] removeData = Arrays.copyOfRange(message, 6, message.length);//去头
		String messStr = new String(removeData);
		Da_anqi mAnqi = new Da_anqi();
		String anqi = messStr.substring(4,7);//值
		String aqsx = messStr.substring(7,8);//传感器顺序
		String shijian = messStr.substring(0,4);//时间
		mAnqi.setHkaq(String.valueOf(Double.valueOf(anqi)/10));
		mAnqi.setHksj(shijian.substring(0,2) + ":" + shijian.substring(2,4));
		mAnqi.setHkaqsx(aqsx);//传感器顺序
		insertAnqi(context, mAnqi);
		int aqCount = selectAnqiCountBySx(context,aqsx);
		if (aqCount > 10){
			List<Da_anqi> anqiList = loadAllAnqiBySxLimit(context, aqCount -10,aqsx);
			for (int i = 0;i < anqiList.size();i++) {
				deleteAnqi(context, anqiList.get(i));
			}
		}
	}
	
	/**
	 * 温度数据解析Ex
	 * @param context
	 * @param message
	 */
	public void insertWenduDataEx (Context context,byte[] message) {
		if (message[5] != 0) {//接受失败
			return;
		}
		byte [] removeData = Arrays.copyOfRange(message, 6, message.length);//去头
		String messStr = new String(removeData);
		Da_wendu mWendu = new Da_wendu();
		String wendu = messStr.substring(4,7);
		String wdsx = messStr.substring(7,8);//传感器顺序
		String shijian = messStr.substring(0,4);//时间
		mWendu.setHkwd(String.valueOf(Double.valueOf(wendu)/10));
		mWendu.setHksj(shijian.substring(0,2) + ":" + shijian.substring(2,4));
		mWendu.setHkwdsx(wdsx);//传感器顺序
		insertWendu(context, mWendu);
		int wdCount = selectWenduCountBySx(context, wdsx);
		if (wdCount > 10){
			List<Da_wendu> wenduList = loadAllWenduBySxLimit(context, wdCount -10,wdsx);
			for (int i = 0;i < wenduList.size();i++) {
				deleteWendu(context,wenduList.get(i));
			}
		}
	}
	/**
	 * 湿度数据解析Ex
	 * @param context
	 * @param message
	 */
	public void insertShiduDataEx (Context context,byte[] message) {
		if (message[5] != 0) {//接受失败
			return;
		}
		byte [] removeData = Arrays.copyOfRange(message, 6, message.length);//去头
		String messStr = new String(removeData);
		Da_shidu mShidu = new Da_shidu();
		String sd = messStr.substring(4,7);
		String sdsx = messStr.substring(7,8);//传感器顺序
		String shijian = messStr.substring(0,4);//时间
		mShidu.setHksd(String.valueOf(Double.valueOf(sd)/10));
		mShidu.setHksj(shijian.substring(0,2) + ":" + shijian.substring(2,4));
		mShidu.setHksdsx(sdsx);//传感器顺序
		insertShidu(context, mShidu);
		int sdCount = selectShiduCountBySx(context,sdsx);
		if (sdCount > 10){
			List<Da_shidu> shiduList = loadAllShiduBySxLimit(context, sdCount - 10,sdsx);
			for (int i = 0;i < shiduList.size();i++) {
				deleteShidu(context, shiduList.get(i));
			}
		}
	}
	
	/**
	 * Ph数据解析Ex
	 * @param context
	 * @param message
	 */
	public void insertPhDataEx (Context context,byte[] message) {
		if (message[5] != 0) {//接受失败
			return;
		}
		byte [] removeData = Arrays.copyOfRange(message, 6, message.length);//去头
		String messStr = new String(removeData);
		Da_ph mPh = new Da_ph();
		String ph = messStr.substring(4,7);//值
		String phsx = messStr.substring(7,8);//传感器顺序
		String shijian = messStr.substring(0,4);//时间
		mPh.setHkph(String.valueOf(Double.valueOf(ph)/10));
		mPh.setHksj(shijian.substring(0,2) + ":" + shijian.substring(2,4));
		mPh.setHkphsx(phsx);//传感器顺序
		insertPh(context, mPh);
		int phCount = selectPhCountBySx(context,phsx);
		if (phCount > 10){
			List<Da_ph> phList = loadAllPHBySxLimit(context, phCount - 10,phsx);
			for (int i = 0;i < phList.size();i++) {
				deletePh(context, phList.get(i));
			}
		}
	}
	/**
	 * 插入单个湿度
	 * @param context
	 * @param da_shiduEntity
	 */
	private void insertShidu(Context context, Da_shidu da_shiduEntity){
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.insertShidu(da_shiduEntity);
	}
	/**
	 * 插入单个PH
	 * @param context
	 * @param
	 */
	private void insertPh(Context context, Da_ph da_phEntity){
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.insertPh(da_phEntity);
	}
	/**
	 * 插入单个温度
	 * @param context
	 * @param da_wenduEntity
	 */
	private void insertWendu(Context context, Da_wendu da_wenduEntity){
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.insertWendu(da_wenduEntity);
	}
	
	/**
	 * 插入单个氨气
	 * @param context
	 * @param da_anqiEntity
	 */
	private void insertAnqi(Context context, Da_anqi da_anqiEntity){
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.insertAnqi(da_anqiEntity);
	}
	/**
	 * 得到温度数据
	 * @param context
	 * @return
	 */
	public static List<Da_wendu> loadAllWenduListBySx(Context context,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.loadWenduBySx(sx);
	}
	
	public static List<Da_wendu> loadAllWenduList(Context context,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.loadAllWendu();
	}
	/**
	 * 得到湿度数据
	 * @param context
	 * @return
	 */
	public static List<Da_shidu> loadAllShiduListBySx(Context context,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.loadShiduBySx(sx);
	}
	
	public static List<Da_shidu> loadAllShiduList(Context context,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.loadAllShidu();
	}
	/**
	 * 得到氨气数据
	 * @param context
	 * @return
	 */
	public static List<Da_anqi> loadAllAnqiListBySx(Context context,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.loadAnqiBySx(sx);
	}
	/**
	 * 得到数据
	 * @param context
	 * @return
	 */
	public static List<Da_ph> loadAllPhListBySx(Context context,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.loadPhBySx(sx);
	}
	/**
	 * 根据温度传感器的顺序查次顺序的温度传感器有几条
	 * @param context
	 * @param sx 温度传感器顺序
	 * @return
	 */
	private int selectWenduCountBySx(Context context,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.loadWenduBySx(sx).size();
	}
	/**
	 * 根据湿度传感器的顺序查次顺序的湿度传感器有几条
	 * @param context
	 * @param sx 湿度传感器顺序
	 * @return
	 */
	private int selectShiduCountBySx(Context context,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.loadShiduBySx(sx).size();
	}
	
	/**
	 * 根据氨气传感器的顺序查次顺序的湿度传感器有几条
	 * @param context
	 * @param sx 氨气传感器顺序
	 * @return
	 */
	private int selectAnqiCountBySx(Context context,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.loadAnqiBySx(sx).size();
	}
	/**
	 * 根据Ph传感器的顺序查次顺序的湿度传感器有几条
	 * @param context
	 * @param sx Ph传感器顺序
	 * @return
	 */
	private int selectPhCountBySx(Context context,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.loadPhBySx(sx).size();
	}
	/**
	 *删除某一条温度
	 * @param context
	 */
	private void deleteWendu(Context context,Da_wendu wendu) {
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.deleteWendu(wendu);
	}
	
	/**
	 *删除某一条氨气
	 * @param context
	 * @param
	 */
	private void deleteAnqi(Context context,Da_anqi anqi) {
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.deleteAnqi(anqi);
	}
	
	/**
	 *删除某一条PH
	 * @param context
	 */
	private void deletePh(Context context,Da_ph ph) {
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.deletePh(ph);
	}
	/**
	 * 删除某一条湿度
	 * @param context
	 */
	private void deleteShidu(Context context,Da_shidu shidu) {
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.deleteShidu(shidu);
	}

	/**
	 * 得到温度同一顺序传感器前几条
	 * @param context
	 * @param i
	 * @return
	 */
	private List<Da_wendu> loadAllWenduBySxLimit(Context context,int i,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.loadAllWenduBySxLimit(i,sx);
	}
	/**
	 * 得到湿度同一顺序传感器前几条
	 * @param context
	 * @param i
	 * @return
	 */
	private List<Da_shidu> loadAllShiduBySxLimit(Context context,int i,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.loadAllShiduBySxLimit(i,sx);
	}
	/**
	 * 得到氨气同一顺序传感器前几条
	 * @param context
	 * @param i
	 * @return
	 */
	private List<Da_anqi> loadAllAnqiBySxLimit(Context context,int i,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.loadAllAnqiBySxLimit(i,sx);
	}
	/**
	 * 得到Ph同一顺序传感器前几条
	 * @param context
	 * @param i
	 * @return
	 */
	private List<Da_ph> loadAllPHBySxLimit(Context context,int i,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.loadAllPHBySxLimit(i,sx);
	}
	
	/** * --------------------------全控---------------------------------------- */
	
	/**
	 * 水压
	 * @param context
	 * @param message
	 */
	public void qk_insertShuiyaData (Context context,byte[] message) {
		if (message[5] != 0) {//接受失败
			return;
		}
		byte [] removeData = Arrays.copyOfRange(message, 6, message.length);//去头
		String messStr = new String(removeData);
		Da_qk_shuiya mShuiya = new Da_qk_shuiya();
		String sy = messStr.substring(4,7);
		String sysx = messStr.substring(7,8);//传感器顺序
		String shijian = messStr.substring(0,4);//时间
		mShuiya.setQksy(String.valueOf(Double.valueOf(sy)/10));
		mShuiya.setQksj(shijian.substring(0,2) + ":" + shijian.substring(2,4));
		mShuiya.setQksysx(sysx);//传感器顺序
		qk_insertShuiya(context, mShuiya);
		int syCount = qk_selectShuiyaCountBySx(context,sysx);
		if (syCount > 10){
			List<Da_qk_shuiya> shuiyaList = qk_loadAllShuiyaBySxLimit(context, syCount -10,sysx);
			for (int i = 0;i < shuiyaList.size();i++) {
				qk_deleteShuiya(context, shuiyaList.get(i));
			}
		}
	}
	
	/**
	 * 水流
	 * @param context
	 * @param message
	 */
	public void qk_insertShuiliuData (Context context,byte[] message) {
		if (message[5] != 0) {//接受失败
			return;
		}
		byte [] removeData = Arrays.copyOfRange(message, 6, message.length);//去头
		String messStr = new String(removeData);
		Da_qk_shuiliu mShuiliu = new Da_qk_shuiliu();
		String sl = messStr.substring(4,7);
		String slsx = messStr.substring(7,8);//传感器顺序
		String shijian = messStr.substring(0,4);//时间
		mShuiliu.setQksl(String.valueOf(Double.valueOf(sl)/10));
		mShuiliu.setQksj(shijian.substring(0,2) + ":" + shijian.substring(2,4));
		mShuiliu.setQkslsx(slsx);//传感器顺序
		qk_insertShuiliu(context, mShuiliu);
		int slCount = qk_selectShuiliuCountBySx(context,slsx);
		if (slCount > 10){
			List<Da_qk_shuiliu> shuiliuList = qk_loadAllShuiliuBySxLimit(context, slCount -10,slsx);
			for (int i = 0;i < shuiliuList.size();i++) {
				qk_deleteShuiliu(context, shuiliuList.get(i));
			}
		}
	}
	/**
	 * 得到水压数据
	 * @param context
	 * @return
	 */
	public static List<Da_qk_shuiya> qk_LoadAllShuiyaListBySx(Context context,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.qk_loadshuiyaBySx(sx);
	}
	/**
	 * 得到水流数据
	 * @param context
	 * @return
	 */
	public static List<Da_qk_shuiliu> qk_LoadAllShuiliuListBySx(Context context,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.qk_loadshuiliuBySx(sx);
	}
	/**
	 * 插入单个水压
	 * @param context
	 */
	private void qk_insertShuiya(Context context, Da_qk_shuiya da_qk_shuiyaEntity){
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.qk_insertShuiya(da_qk_shuiyaEntity);
	}
	
	/**
	 * 插入单个水流
	 * @param context
	 */
	private void qk_insertShuiliu(Context context, Da_qk_shuiliu da_qk_shuiliuEntity){
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.qk_insertShuiliu(da_qk_shuiliuEntity);
	}
	
	/**
	 * 根据水压传感器的顺序查次顺序的湿度传感器有几条
	 * @param context
	 * @param sx 水压传感器顺序
	 * @return
	 */
	private int qk_selectShuiyaCountBySx(Context context,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.qk_loadShuiyaBySx(sx).size();
	}
	
	/**
	 * 根据水流传感器的顺序查次顺序的湿度传感器有几条
	 * @param context
	 * @param sx 水流传感器顺序
	 * @return
	 */
	private int qk_selectShuiliuCountBySx(Context context,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.qk_loadShuiliuBySx(sx).size();
	}
	
	/**
	 * 得到水压同一顺序传感器前几条
	 * @param context
	 * @param i
	 * @return
	 */
	private List<Da_qk_shuiya> qk_loadAllShuiyaBySxLimit(Context context,int i,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.qk_loadAllShuiyaBySxLimit(i,sx);
	}
	
	/**
	 * 得到水流同一顺序传感器前几条
	 * @param context
	 * @param i
	 * @return
	 */
	private List<Da_qk_shuiliu> qk_loadAllShuiliuBySxLimit(Context context,int i,String sx) {
		DBHelper dBManager = DBHelper.getInstance(context);
		return dBManager.qk_loadAllShuiliuBySxLimit(i,sx);
	}
	
	/**
	 * 删除某一条水压
	 * @param context
	 * @param
	 */
	private void qk_deleteShuiya(Context context,Da_qk_shuiya mDa_qk_shuiya) {
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.qk_deleteShuiya(mDa_qk_shuiya);
	}
	
	/**
	 * 删除某一条水流
	 * @param context
	 */
	private void qk_deleteShuiliu(Context context,Da_qk_shuiliu mDa_qk_shuiliu) {
		DBHelper dBManager = DBHelper.getInstance(context);
		dBManager.qk_deleteShuiliu(mDa_qk_shuiliu);
	}
}
