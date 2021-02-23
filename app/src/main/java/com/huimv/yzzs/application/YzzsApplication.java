package com.huimv.yzzs.application;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.huimv.yzzs.BuildConfig;
import com.huimv.yzzs.db.entity.DaoMaster;
import com.huimv.yzzs.db.entity.DaoMaster.OpenHelper;
import com.huimv.yzzs.db.entity.DaoSession;
import com.huimv.yzzs.db.helper.DBHelper;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import java.util.LinkedList;

public class YzzsApplication extends Application {
	SharePreferenceUtil mSpUtil;
	private static YzzsApplication instance;
	private static final String SP_FILE_NAME = "yzzs_message";
	private static LinkedList<Activity> activityList = new LinkedList<Activity>(); // 使用集合类统一管理Activity实例
	private static DaoMaster daoMaster;
	private static DaoSession daoSession;
	public static SQLiteDatabase db;
	public static final String DB_NAME = "Yzzs.db";
	public static int messCount;// 接受包的总数,超过30个作超时处理
	public static byte[] tempData;// 前一个包的遗留数据
	public static boolean isConnected = false;// 蓝牙连接
	private String scjVersion = "";

	public String getScjVersion() {
		return scjVersion;
	}

	public void setScjVersion(String scjVersion) {
		this.scjVersion = scjVersion;
	}

	public static DaoMaster getDaoMaster(Context context) {
		if (daoMaster == null) {
			OpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
			daoMaster = new DaoMaster(helper.getWritableDatabase());
		}
		return daoMaster;
	}

	public static DaoSession getDaoSession(Context context) {
		if (daoSession == null) {
			if (daoMaster == null) {
				daoMaster = getDaoMaster(context);
			}
			daoSession = daoMaster.newSession();
		}
		return daoSession;
	}

	public static SQLiteDatabase getSQLDatebase(Context context) {
		if (daoSession == null) {
			if (daoMaster == null) {
				daoMaster = getDaoMaster(context);
			}
			db = daoMaster.getDatabase();
		}
		return db;
	}

	@Override
	public void onCreate() {
		//是否开启打印
		if (BuildConfig.LOG_DEBUG) {
			Logger.init("AppPlusLog").setLogLevel(LogLevel.FULL);
		} else {
			Logger.init("AppPlusLog").setLogLevel(LogLevel.NONE);
		}
		/*CrashHandler crashHandler = CrashHandler.getInstance();
		//注册crashHandler
		crashHandler.init(getApplicationContext());
		//发送以前没发送的报告(可选)
		crashHandler.sendPreviousReportsToServer();*/
		mSpUtil = new SharePreferenceUtil(this, SP_FILE_NAME);
		instance = this;
		DBHelper.getInstance(this);// 创建数据库
		messCount = 0;
		tempData = new byte[0];
		super.onCreate();
	}

	public synchronized static YzzsApplication getInstance() {
		return instance;
	}

	public synchronized SharePreferenceUtil getSpUtil() {
		if (mSpUtil == null) {
			mSpUtil = new SharePreferenceUtil(this, SP_FILE_NAME);
		}
		return mSpUtil;
	}

	/**
	 * 添加Activity到容器，在每个Activity的onCreate调用
	 */
	public static void addActivity(Activity activity) {
		activityList.add(activity);
	}

	/**
	 * 遍历Activity并finish，在mainActivity的主界面当点击back键调用
	 */
	public static void exit() {
		// 遍历所有Activity实例，挨个finish
		for (Activity activity : activityList) {
			if (activity != null)
				activity.finish();
		}
		android.os.Process.killProcess(android.os.Process.myPid());// 获取当前进程PID，并杀掉
	}

	/**
	 * 得到APP版本号
	 */
	public static String getVersionName(Context context) {
		String loadVersionName = "";
		PackageManager nPackageManager = context.getPackageManager();// 得到包管理器
		try {
			PackageInfo nPackageInfo = nPackageManager.getPackageInfo(context.getPackageName(),
					PackageManager.GET_CONFIGURATIONS);
			loadVersionName = nPackageInfo.versionName;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		return loadVersionName;
	}

	/**
	 * 获取系统显示材质
	 ***/
	public static DisplayMetrics getDisplayMetrics() {
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		WindowManager windowMgr = (WindowManager) instance.getSystemService(Context.WINDOW_SERVICE);
		windowMgr.getDefaultDisplay().getMetrics(mDisplayMetrics);
		return mDisplayMetrics;
	}

	/**
	 * 判断当前Activity是否在栈顶
	 *
	 * @param context
	 * @param Tag
	 * @return
	 */
	public static boolean isTopActivity(Context context, String Tag) {
		boolean isTop = false;
		ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		if (cn.getClassName().contains(Tag)) {
			isTop = true;
		}
		return isTop;
	}

	//是否有变频风机
	public static void hasBpfj(int lenth, SharePreferenceUtil mSpUtil) {
		if (lenth == 9) {
			mSpUtil.setHkIsHasBpfjVersion("0");
		} else {
			mSpUtil.setHkIsHasBpfjVersion("1");
		}
	}

	/**
	 * 版本比较
	 */
	public static boolean versionCompare(String oldVersion, String currentVersion) {
		return Integer.valueOf(currentVersion.replace("V", "").replace(".", "")) >
				Integer.valueOf(oldVersion.replace("V", "").replace(".", ""));
	}
}
