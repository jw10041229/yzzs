package com.huimv.yzzs.constant;

public interface FlzCMDConstant {
	String LEFT_CHANNEL_SP = "0";
	String RIGHT_CHANNEL_SP = "1";

	/**--------------------------------------FLZ CMD-------------------------------------------------**/
	/**体温**/
	int FLZ_TW = -1;
	/**密码验证**/
	int FLZ_MMYZ = 5;
	/**分栏站标志**/
	int FLZ_SBBZ = 3;

	/**打开入口门**/
	int FLZ_DKRKM = 50;
	/**关闭入口门**/
	int FLZ_GBRKM = 51;
	/**打开左通道**/
	int FLZ_DKZTD = 52;
	/**打开右通道**/
	int FLZ_DKYTD = 53;
	/**关闭旋转门**/
	int FLZ_GBXZM = 54;
	/**喷气驱赶**/
	int FLZ_PQQG = 55;
	/**打开分离门**/
	int FLZ_DKFLM = 56;
	/**关闭分离门**/
	int FLZ_GBFLM = 57;
	/**喷墨1**/
	int FLZ_PM1 = 58;
	/**喷墨2**/
	int FLZ_PM2 = 59;
	/**读出重量**/
	int FLZ_DCZL = 60;
	/**设置为空称**/
	int FLZ_SZWGC = 61;
	/**校准**/
	int FLZ_JZ = 62;
	/**设置校准参数**/
	int FLZ_SZJZCS = 63;
	/**保存当前设置**/
	int FLZ_BCDQSZ = 64;
	/**RFID调试**/
	int FLZ_RFIDTS = 65;
	/**直角通道方向设置**/
	int FLZ_ZJTDFXSZ = 66;
	/**默认通道方向设置**/
	int FLZ_MRTDFXSZ = 67;

	/**体重**/
	int FLZ_TZ = 68;
	/**RIFID**/
	int FLZ_RFID = 69;
	/**暂停**/
	int FLZ_ZT = 70;
	/**恢复**/
	int FLZ_HF = 71;

	/**退出调试**/
	int FLZ_TCTS = 72;
	/**快速调试**/
	int FLZ_KSTS = 73;
	
	/**
	 * 工作模式
	 */
	int FLZ_GZMS = 74;
	/**
	 * 调试模式
	 */
	int FLZ_ZBTS = 75;
	/**设备故障状态**/
	int FLZ_GZZT = 76;
	/**运行状态**/
	int FLZ_YXZT = 77;
	/*** RFID分栏*/
	int FLZ_RIFD_FL = 78;
	/**传感器故障状态**/
	int FLZ_SENSOR_GZ = 79;
	/**
	 * 分栏/出栏体重
	 */
	int FLZ_FL_CL_DATA = 80;
	
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
