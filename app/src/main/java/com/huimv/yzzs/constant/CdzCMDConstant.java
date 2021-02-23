package com.huimv.yzzs.constant;

public interface CdzCMDConstant {
	/**--------------------------------------CdzCMD-------------------------------------------------**/
	/**体温**/
	int CDZ_TW = -1;
	/**密码验证**/
	int CDZ_MMYZ = 5;
	/**测定站标志**/
	int CDZ_SBBZ = 6;

	/**打开入口门**/
	int CDZ_DKRKM = 50;
	/**关闭入口门**/
	int CDZ_GBRKM = 51;
	/**打开左通道**/
	int CDZ_DKZTD = 52;
	/**打开右通道**/
	int CDZ_DKYTD = 53;
	/**关闭旋转门**/
	int CDZ_GBXZM = 54;
	/**喷气驱赶**/
	int CDZ_PQQG = 55;
	/**打开分离门**/
	int CDZ_DKFLM = 56;
	/**关闭分离门**/
	int CDZ_GBFLM = 57;
	/**喷墨1**/
	int CDZ_PM1 = 58;
	/**喷墨2**/
	int CDZ_PM2 = 59;
	/**读出重量**/
	int CDZ_DCZL = 60;
	/**设置为空称**/
	int CDZ_SZWGC = 61;
	/**校准**/
	int CDZ_JZ = 62;
	/**设置校准参数**/
	int CDZ_SZJZCS = 63;
	/**保存当前设置**/
	int CDZ_BCDQSZ = 64;
	/**RFID调试**/
	int CDZ_RFIDTS = 65;
	/**直角通道方向设置**/
	int CDZ_ZJTDFXSZ = 66;
	/**默认通道方向设置**/
	int CDZ_MRTDFXSZ = 67;

	/**体重**/
	int CDZ_TZ = 68;
	/**RIFID**/
	int CDZ_RFID = 69;
	/**暂停**/
	int CDZ_ZT = 70;
	/**恢复**/
	int CDZ_HF = 71;

	/**退出调试**/
	int CDZ_TCTS = 72;
	/**快速调试**/
	int CDZ_KSTS = 73;
	
	/**
	 * 工作模式
	 */
	int CDZ_GZMS = 74;
	/**
	 * 调试模式
	 */
	int CDZ_ZBTS = 75;
	/**设备故障状态**/
	int CDZ_GZZT = 76;
	/**运行状态**/
	int CDZ_YXZT = 77;
	/*** RFID分栏*/
	int CDZ_RIFD_FL = 78;
	/**传感器故障状态**/
	int CDZ_SENSOR_GZ = 79;
	/**
	 * 分栏/出栏体重
	 */
	int CDZ_FL_CL_DATA = 80;
	/**
	 * 读取正转数据
	 */
	int CDZ_READ_ROLL_TIME = 81;
	/**
	 * 读取反转数据
	 */
	int CDZ_READ_ROLLBACK_TIME = 82;
	/**
	 * 保存正转数据
	 */
	int CDZ_SAVE_ROLL_TIME = 83;
	/**
	 * 保存反转数据
	 */
	int CDZ_SAVE_ROLLBACK_TIME = 84;
	/**
	 * 调试反转数据
	 */
	int CDZ_DEBUG_ROLL = 85;
	/**
	 * 采食量
	 */
	int CDZ_FEED_NUB = 86;
	/**--------------------------------------LC CMD-------------------------------------------------**/
	/**RIFID**/
	int GTLC_RFID = 101;
	/**下料量**/
	int GTLC_RATE = 102;
	/**时间**/
	int GTLC_TIME = 103;
	/**剩余量**/
	int GTLC_REMAININT_AMOUNT = 104;
	/**采食量**/
	int GTLC_FEED_AMOUNT= 105;
	/**设备故障**/
	int GTLC_DEV_FZULT= 106;
	/**状态刷新**/
	int GTLC_STATE_REFRESH= 107;
	
	/**时间**/
	int QTLC_TIME = 108;
	/**下料量**/
	int QTLC_RATE = 109;
	/**水流量**/
	int QTLC_WATER_FLOW = 110;
	/**触碰次数**/
	int QTLC_TOUCH_NUMBER = 111;
	/**状态刷新**/
	int QTLC_STATE_REFRESH= 112;
	/**设备故障**/
	int QTLC_DEV_FZULT= 113;
}
