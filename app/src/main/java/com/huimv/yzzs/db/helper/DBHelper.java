package com.huimv.yzzs.db.helper;

import android.content.Context;

import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.db.entity.Da_anqi;
import com.huimv.yzzs.db.entity.Da_anqiDao;
import com.huimv.yzzs.db.entity.Da_bt;
import com.huimv.yzzs.db.entity.Da_btDao;
import com.huimv.yzzs.db.entity.Da_hksj;
import com.huimv.yzzs.db.entity.Da_hksjDao;
import com.huimv.yzzs.db.entity.Da_mc;
import com.huimv.yzzs.db.entity.Da_mcDao;
import com.huimv.yzzs.db.entity.Da_ph;
import com.huimv.yzzs.db.entity.Da_phDao;
import com.huimv.yzzs.db.entity.Da_qk_shuiliu;
import com.huimv.yzzs.db.entity.Da_qk_shuiliuDao;
import com.huimv.yzzs.db.entity.Da_qk_shuiya;
import com.huimv.yzzs.db.entity.Da_qk_shuiyaDao;
import com.huimv.yzzs.db.entity.Da_scj;
import com.huimv.yzzs.db.entity.Da_scjDao;
import com.huimv.yzzs.db.entity.Da_shidu;
import com.huimv.yzzs.db.entity.Da_shiduDao;
import com.huimv.yzzs.db.entity.Da_shiduDao.Properties;
import com.huimv.yzzs.db.entity.Da_wendu;
import com.huimv.yzzs.db.entity.Da_wenduDao;
import com.huimv.yzzs.db.entity.DaoSession;

import java.util.List;

import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.QueryBuilder;

public class DBHelper {
	//private static final String TAG = DBHelper.class.getSimpleName();
	private static DBHelper instance;
	private static Context appContext;
	private DaoSession mDaoSession;
	private Da_shiduDao da_shiduEntityDao;
	private Da_wenduDao da_wenduEntityDao;
	private Da_anqiDao da_anqiEntityDao;
	private Da_mcDao da_mcEntityDao;
	private Da_btDao da_btEntityDao;
	private Da_phDao da_phEntityDao;
	private Da_hksjDao da_hksjEntityDao;
	private Da_qk_shuiyaDao da_qk_shuiyaEntityDao;
	private Da_qk_shuiliuDao da_qk_shuiliuEntityDao;
	private Da_scjDao da_scjEntityDao;
	private DBHelper() {
	}

	// 单例模式，DBHelper只初始化一次
	public static DBHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DBHelper();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.mDaoSession = YzzsApplication.getDaoSession(context);
			instance.da_shiduEntityDao = instance.mDaoSession.getDa_shiduDao();
			instance.da_wenduEntityDao = instance.mDaoSession.getDa_wenduDao();
			instance.da_anqiEntityDao = instance.mDaoSession.getDa_anqiDao();
			instance.da_mcEntityDao = instance.mDaoSession.getDa_mcDao();
			instance.da_btEntityDao = instance.mDaoSession.getDa_btDao();
			instance.da_hksjEntityDao = instance.mDaoSession.getDa_hksjDao();
			instance.da_phEntityDao = instance.mDaoSession.getDa_phDao();
			instance.da_qk_shuiyaEntityDao = instance.mDaoSession.getDa_qk_shuiyaDao();
			instance.da_qk_shuiliuEntityDao = instance.mDaoSession.getDa_qk_shuiliuDao();
			instance.da_scjEntityDao = instance.mDaoSession.getDa_scjDao();
		}
		return instance;
	}

	// 删除温度表
	public void dropChatTable() {
		Da_wenduDao.dropTable(mDaoSession.getDatabase(), true);
	}

	// 删除所有表
	public void dropAllTable() {
		Da_wenduDao.dropTable(mDaoSession.getDatabase(), true);
		Da_shiduDao.dropTable(mDaoSession.getDatabase(), true);
		Da_anqiDao.dropTable(mDaoSession.getDatabase(), true);
		Da_mcDao.dropTable(mDaoSession.getDatabase(), true);
		Da_hksjDao.dropTable(mDaoSession.getDatabase(), true);
	}

	// 创建所有表
	public void createAllTable() {
		Da_wenduDao.createTable(mDaoSession.getDatabase(), true);
		Da_shiduDao.createTable(mDaoSession.getDatabase(), true);
		Da_anqiDao.createTable(mDaoSession.getDatabase(), true);
		Da_mcDao.createTable(mDaoSession.getDatabase(), true);
		Da_hksjDao.createTable(mDaoSession.getDatabase(), true);
	}
		// 插入单个数据，根据数据类型
		public long insertHkSj(Da_hksj da_hksjEntity) {
			return da_hksjEntityDao.insert(da_hksjEntity);
		}

		// 删除全部环控数据
		public void deleteAllDa_hksj() {
			da_hksjEntityDao.deleteAll();
		}

		// 获取所有环控数据 list
		public List<Da_hksj> loadAllHksjBySjlxAndCgqsx(String sjlx, String cgqsx) {
			QueryBuilder<Da_hksj> qBuilder = da_hksjEntityDao.queryBuilder();
			List<Da_hksj> list = qBuilder.where(Da_hksjDao.Properties.Cgqsx.eq(cgqsx), Da_hksjDao.Properties.Sjlx.eq(sjlx))
					.build().list();
			return list;
		}

		//  得到数据环控数据的前几条数据list
		public List<Da_hksj> loadAllHksjBySjlxAndCgqsxLimit(int i, String cgqsx, String sjlx) {
			QueryBuilder<Da_hksj> qBuilder = da_hksjEntityDao.queryBuilder();
			List<Da_hksj> list = qBuilder.where(Da_hksjDao.Properties.Cgqsx.eq(cgqsx), Da_hksjDao.Properties.Sjlx.eq(sjlx))
					.limit(i).build().list();
			return list;
		}
		
		//  删除数据表中一条数据
		public void deleteHksj(Da_hksj mDa_hksj){
			da_hksjEntityDao.delete(mDa_hksj);
		}
	
	
	// 插入单个湿度对象
	public long insertShidu(Da_shidu da_shiduEntity) {
		return da_shiduEntityDao.insert(da_shiduEntity);
	}
	// 插入单个湿度对象
	public long insertPh(Da_ph da_phEntity) {
		return da_phEntityDao.insert(da_phEntity);
	}
	// 插入单个温度对象
	public long insertWendu(Da_wendu da_wenduEntity) {
		return da_wenduEntityDao.insert(da_wenduEntity);
	}

	// 插入单个氨气对象
	public long insertAnqi(Da_anqi da_anqiEntity) {
		return da_anqiEntityDao.insert(da_anqiEntity);
	}

	// 获得所有的Da_wendu列表
	public List<Da_wendu> loadAllWendu() {
		return da_wenduEntityDao.loadAll();
	}

	// 获得所有的Da_shidu列表
	public List<Da_shidu> loadAllShidu() {
		return da_shiduEntityDao.loadAll();
	}

	// 获得所有的Da_anqi列表
	public List<Da_anqi> loadAllAnqi() {
		return da_anqiEntityDao.loadAll();
	}

	// 获得所有的Da_shidu列表降序
	public List<Da_shidu> loadAllShiduDesc() {
		QueryBuilder<Da_shidu> mqBuilder = da_shiduEntityDao.queryBuilder();
		mqBuilder.orderDesc(Properties.Id);
		return mqBuilder.list();
	}

	// 获得所有的同一顺序Da_wendu列表
	public List<Da_wendu> loadWenduBySx(String sx) {
		QueryBuilder<Da_wendu> qBuilder = da_wenduEntityDao.queryBuilder();
		List<Da_wendu> list = qBuilder.where(Da_wenduDao.Properties.Hkwdsx.eq(sx)).build().list();
		return list;
	}

	// 获得所有的同一顺序Da_shidu列表
	public List<Da_shidu> loadShiduBySx(String sx) {
		QueryBuilder<Da_shidu> qBuilder = da_shiduEntityDao.queryBuilder();
		List<Da_shidu> list = qBuilder.where(Properties.Hksdsx.eq(sx)).build().list();
		return list;
	}

	// 获得所有的同一顺序Da_anqi列表
	public List<Da_anqi> loadAnqiBySx(String sx) {
		QueryBuilder<Da_anqi> qBuilder = da_anqiEntityDao.queryBuilder();
		List<Da_anqi> list = qBuilder.where(Da_anqiDao.Properties.Hkaqsx.eq(sx)).build().list();
		return list;
	}
	
	// 获得所有的同一顺序Da_ph列表
	public List<Da_ph> loadPhBySx(String sx) {
		QueryBuilder<Da_ph> qBuilder = da_phEntityDao.queryBuilder();
		List<Da_ph> list = qBuilder.where(Da_phDao.Properties.Hkphsx.eq(sx)).build().list();
		return list;
	}
	// 获得所有的Da_wendu列表降序
	public List<Da_wendu> loadAllWenduDesc() {
		QueryBuilder<Da_wendu> mqBuilder = da_wenduEntityDao.queryBuilder();
		mqBuilder.orderDesc(Da_wenduDao.Properties.Id);
		return mqBuilder.list();
	}

	// 获得所有的Da_anqi列表降序
	public List<Da_anqi> loadAllAnqiDesc() {
		QueryBuilder<Da_anqi> mqBuilder = da_anqiEntityDao.queryBuilder();
		mqBuilder.orderDesc(Da_anqiDao.Properties.Id);
		return mqBuilder.list();
	}

	// 获得所有的Da_wendu前几条
	public List<Da_wendu> loadAllWenduLimit(int i) {
		QueryBuilder<Da_wendu> mqBuilder = da_wenduEntityDao.queryBuilder();
		mqBuilder.limit(i);
		return mqBuilder.list();
	}

	// 获得温度同一顺序所有的Da_wendu前几条
	public List<Da_wendu> loadAllWenduBySxLimit(int i, String sx) {
		QueryBuilder<Da_wendu> qBuilder = da_wenduEntityDao.queryBuilder();
		List<Da_wendu> list = qBuilder.where(Da_wenduDao.Properties.Hkwdsx.eq(sx)).limit(i).build().list();
		return list;
	}

	// 获得湿度同一顺序所有的Da_shidu前几条
	public List<Da_shidu> loadAllShiduBySxLimit(int i, String sx) {
		QueryBuilder<Da_shidu> qBuilder = da_shiduEntityDao.queryBuilder();
		List<Da_shidu> list = qBuilder.where(Properties.Hksdsx.eq(sx)).limit(i).build().list();
		return list;
	}

	// 获氨气同一顺序所有的Da_shidu前几条
	public List<Da_anqi> loadAllAnqiBySxLimit(int i, String sx) {
		QueryBuilder<Da_anqi> qBuilder = da_anqiEntityDao.queryBuilder();
		List<Da_anqi> list = qBuilder.where(Da_anqiDao.Properties.Hkaqsx.eq(sx)).limit(i).build().list();
		return list;
	}
	// 获PH同一顺序所有的Da_shidu前几条
	public List<Da_ph> loadAllPHBySxLimit(int i, String sx) {
		QueryBuilder<Da_ph> qBuilder = da_phEntityDao.queryBuilder();
		List<Da_ph> list = qBuilder.where(Da_phDao.Properties.Hkphsx.eq(sx)).limit(i).build().list();
		return list;
	}
	// 获得所有的Da_shidu前几条
	public List<Da_shidu> loadAllShiduLimit(int i) {
		QueryBuilder<Da_shidu> mqBuilder = da_shiduEntityDao.queryBuilder();
		mqBuilder.limit(i);
		return mqBuilder.list();
	}

	// 获得所有的Da_anqi前几条
	public List<Da_anqi> loadAllAnqiLimit(int i) {
		QueryBuilder<Da_anqi> mqBuilder = da_anqiEntityDao.queryBuilder();
		mqBuilder.limit(i);
		return mqBuilder.list();
	}

	// 删除全部湿度数据
	public void deleteAllDa_shidu() {
		da_shiduEntityDao.deleteAll();
	}

	// 删除全部温度数据
	public void deleteAllDa_wendu() {
		da_wenduEntityDao.deleteAll();
	}

	// 删除全部氨气数据
	public void deleteAllDa_anqi() {
		da_anqiEntityDao.deleteAll();
	}
	// 删除全部Ph数据
	public void deleteAllDa_ph() {
		da_phEntityDao.deleteAll();
	}
	// 根据id删除某一条温度
	public void deleteWenduById(long id) {
		da_wenduEntityDao.deleteByKey(id);
	}

	// 根据id删除某一条湿度
	public void deleteShiduById(long id) {
		da_shiduEntityDao.deleteByKey(id);
	}

	// 根据id删除某一条氨气
	public void deleteAnqiById(long id) {
		da_anqiEntityDao.deleteByKey(id);
	}

	public void deleteShidu(Da_shidu shidu) {
		da_shiduEntityDao.delete(shidu);
	}

	public void deleteWendu(Da_wendu wendu) {
		da_wenduEntityDao.delete(wendu);
	}

	public void deleteAnqi(Da_anqi anqi) {
		da_anqiEntityDao.delete(anqi);
	}
	public void deletePh(Da_ph ph) {
		da_phEntityDao.delete(ph);
	}
	// 获取全部 牧场 数据
	public List<Da_mc> loadAllMc() {
		return da_mcEntityDao.loadAll();
	}

	// 删除 牧场 全部数据
	public void deleteAllMc() {
		da_mcEntityDao.deleteAll();
	}

	// 插入一条牧场数据
	public long insertMc(Da_mc da_McEntity) {
		return da_mcEntityDao.insert(da_McEntity);
	}

	// 根据 猪舍id 获取该猪舍的 机器id
	public List<Da_mc> getJqidByZsid(String zsid) {
		Property mcProperty = Da_mcDao.Properties.Zsid;
		QueryBuilder<Da_mc> qBuilder = da_mcEntityDao.queryBuilder();
		List<Da_mc> list = qBuilder.where(mcProperty.eq(zsid)).build().list();
		return list;
	}

	// 根据 猪舍名称 获取该猪舍的 机器id
	public List<Da_mc> getJqidByZsmc(String zsmc) {
		Property mcProperty = Da_mcDao.Properties.Zsmc;
		QueryBuilder<Da_mc> qBuilder = da_mcEntityDao.queryBuilder();
		List<Da_mc> list = qBuilder.where(mcProperty.eq(zsmc)).build().list();
		return list;
	}

	// 根据 机器id 获取 mc信息
	public List<Da_mc> getMcByJqid(String jqid) {
		Property mcProperty = Da_mcDao.Properties.Jqid;
		QueryBuilder<Da_mc> qBuilder = da_mcEntityDao.queryBuilder();
		List<Da_mc> list = qBuilder.where(mcProperty.eq(jqid)).build().list();
		return list;
	}

	// 根据 牧场名称 获取 mc信息
	public List<Da_mc> getMcByMcmc(String mcmc) {
		Property mcProperty = Da_mcDao.Properties.Mcmc;
		QueryBuilder<Da_mc> qBuilder = da_mcEntityDao.queryBuilder();
		List<Da_mc> list = qBuilder.where(mcProperty.eq(mcmc)).build().list();
		return list;
	}

	// 插入蓝牙数据
	public long insertBt(Da_bt da_bt) {
		return da_btEntityDao.insert(da_bt);
	}

	// 查询所有蓝牙数据
	public List<Da_bt> loadAllBt() {
		return da_btEntityDao.loadAll();
	}

	// 删除所有蓝牙数据
	public void deleteAllBt() {
		da_btEntityDao.deleteAll();
	}

	// 根据 蓝牙地址 获取该 蓝牙别名
	public List<Da_bt> getLybmByLydz(String lydz) {
		Property mcProperty = Da_btDao.Properties.Lydz;
		QueryBuilder<Da_bt> qBuilder = da_btEntityDao.queryBuilder();
		List<Da_bt> list = qBuilder.where(mcProperty.eq(lydz)).build().list();
		return list;
	}

	// 根据 蓝牙地址 获取该 该条数据的id
	public List<Da_bt> getIdByLydz(String lydz) {
		Property mcProperty = Da_btDao.Properties.Lydz;
		QueryBuilder<Da_bt> qBuilder = da_btEntityDao.queryBuilder();
		List<Da_bt> list = qBuilder.where(mcProperty.eq(lydz)).build().list();
		return list;
	}

	// 根据id 更新蓝牙数据
	public long updateBtById(Da_bt newDaBt, long id) {
		newDaBt.setId(id);
		return da_btEntityDao.insertOrReplace(newDaBt);
	}

	// 根据机器id获取 Mc 表中的 mcid
	public List<Da_mc> getMcidByJqid(String jqid) {
		Property mcProperty = Da_mcDao.Properties.Jqid;
		QueryBuilder<Da_mc> qBuilder = da_mcEntityDao.queryBuilder();
		List<Da_mc> list = qBuilder.where(mcProperty.eq(jqid)).build().list();
		return list;
	}
	
	/** * --------------------------全控---------------------------------------- */
	// 获得所有的同一顺序Da_qk_shuiya列表
	public List<Da_qk_shuiya> qk_loadshuiyaBySx(String sx) {
		QueryBuilder<Da_qk_shuiya> qBuilder = da_qk_shuiyaEntityDao.queryBuilder();
		List<Da_qk_shuiya> list = qBuilder.where(Da_qk_shuiyaDao.Properties.Qksysx.eq(sx)).build().list();
		return list;
	}
	// 获得所有的同一顺序Da_qk_shuiliu列表
	public List<Da_qk_shuiliu> qk_loadshuiliuBySx(String sx) {
		QueryBuilder<Da_qk_shuiliu> qBuilder = da_qk_shuiliuEntityDao.queryBuilder();
		List<Da_qk_shuiliu> list = qBuilder.where(Da_qk_shuiliuDao.Properties.Qkslsx.eq(sx)).build().list();
		return list;
	}
	
	// 删除全部全控水压数据
		public void qk_deleteAllshuiya() {
			da_qk_shuiyaEntityDao.deleteAll();
		}
		// 删除全部全控水流数据
		public void qk_deleteAllshuiliu() {
			da_qk_shuiliuEntityDao.deleteAll();
		}
		
		// 插入单个水压对象
		public long qk_insertShuiya(Da_qk_shuiya da_qk_shuiyaEntity) {
			return da_qk_shuiyaEntityDao.insert(da_qk_shuiyaEntity);
		}
		
		// 插入单个水流对象
		public long qk_insertShuiliu(Da_qk_shuiliu da_qk_shuiliuEntity) {
			return da_qk_shuiliuEntityDao.insert(da_qk_shuiliuEntity);
		}
		
		// 获得所有的同一顺序Da_qk_shuiliu列表
		public List<Da_qk_shuiliu> qk_loadShuiliuBySx(String sx) {
			QueryBuilder<Da_qk_shuiliu> qBuilder = da_qk_shuiliuEntityDao.queryBuilder();
			List<Da_qk_shuiliu> list = qBuilder.where(Da_qk_shuiliuDao.Properties.Qkslsx.eq(sx)).build().list();
			return list;
		}
		
		// 获得所有的同一顺序Da_qk_shuiya列表
		public List<Da_qk_shuiya> qk_loadShuiyaBySx(String sx) {
			QueryBuilder<Da_qk_shuiya> qBuilder = da_qk_shuiyaEntityDao.queryBuilder();
			List<Da_qk_shuiya> list = qBuilder.where(Da_qk_shuiyaDao.Properties.Qksysx.eq(sx)).build().list();
			return list;
		}
		
		// 获得水压同一顺序所有的Da_shidu前几条
		public List<Da_qk_shuiya> qk_loadAllShuiyaBySxLimit(int i, String sx) {
			QueryBuilder<Da_qk_shuiya> qBuilder = da_qk_shuiyaEntityDao.queryBuilder();
			List<Da_qk_shuiya> list = qBuilder.where(Da_qk_shuiyaDao.Properties.Qksysx.eq(sx)).limit(i).build().list();
			return list;
		}
		// 获得水流同一顺序所有的Da_shidu前几条
		public List<Da_qk_shuiliu> qk_loadAllShuiliuBySxLimit(int i, String sx) {
			QueryBuilder<Da_qk_shuiliu> qBuilder = da_qk_shuiliuEntityDao.queryBuilder();
			List<Da_qk_shuiliu> list = qBuilder.where(Da_qk_shuiliuDao.Properties.Qkslsx.eq(sx)).limit(i).build().list();
			return list;
		}
		//删除水压
		public void qk_deleteShuiya(Da_qk_shuiya mDa_qk_shuiya) {
			da_qk_shuiyaEntityDao.delete(mDa_qk_shuiya);
		}
		//删除水流
		public void qk_deleteShuiliu(Da_qk_shuiliu mDa_qk_shuiliu) {
			da_qk_shuiliuEntityDao.delete(mDa_qk_shuiliu);
		}
		
		/**********************手持机**********************/
		// 删除全部手持机数据
		public void deleteAllDa_scj() {
			da_scjEntityDao.deleteAll();
		}
		// 查询所有手持机数据
		public List<Da_scj> loadAllDa_Scj() {
			QueryBuilder<Da_scj> qBuilder = da_scjEntityDao.queryBuilder();
			List<Da_scj> list = qBuilder.orderDesc(Da_scjDao.Properties.Cjsj).build().list();
			return list;
		}
		// 查询所有手持机数据
		public List<Da_scj> loadAllDa_ScjLimit(int limit) {
			QueryBuilder<Da_scj> qBuilder = da_scjEntityDao.queryBuilder();
			List<Da_scj> list = qBuilder.orderDesc(Da_scjDao.Properties.Cjsj).limit(limit).build().list();
			return list;
		}

	// 查询所有手持机数据
	public List<Da_scj> loadAllDa_ScjByRfid(String rfid) {
		QueryBuilder<Da_scj> qBuilder = da_scjEntityDao.queryBuilder();
		List<Da_scj> list = qBuilder.where(Da_scjDao.Properties.Rfid.like("%" + rfid + "%")).orderDesc(Da_scjDao.Properties.Cjsj).build().list();
		return list;
	}
		// 插入单个手持机
		public long scj_insertScj(Da_scj da_scjEntity) {
			return da_scjEntityDao.insert(da_scjEntity);
		}
		
		// 删除全部温度数据
		public void deleteDa_scj(Da_scj da_scjEntity) {
			da_scjEntityDao.delete(da_scjEntity);
		}
		
		// 删除全部温度数据
		public void deleteDa_scjs(List<Da_scj> da_scjEntitys) {
			da_scjEntityDao.deleteInTx(da_scjEntitys);
		}
}

