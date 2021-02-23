package com.huimv.yzzs.constant;

public interface ScjCMDConstant {
	
	/**手持机设备标志**/
	int SCJ_SBBZ = 5;
	
	/** 获取 RFID 数据**/
	int GET_RFID = 3;
	
	/** 获取 温度 数据**/
	int GET_TEMP = 4;
	
	/** 获取 senssorCode 数据**/
	int GET_SENSSOR_CODE = 5;
	
	/** 获取 rssi 数据**/
	int GET_RSSI = 6;
	
	/** 工作状态 **/
	int SWITCH_SCJ_WORK_STATE = 7;
	
	/** 显示更多信息 **/
	int SWITCH_SCJ_MORE_MSG = 8;
	
	/** 发送频率 **/
	int SEND_FREQUENCY = 9;
	
	/** 发送功率 **/
	int SEND_POWER = 10;
	
	/** 获取当前手持机的功率值和频率值 **/
	int GET_POWER_FREQUENCY_VALUE = 11;
	
	/** 获取当前手持机的电量 **/
	int GET_BATTERY = 12;
	
	/** 获取 TEMP CODE **/
	int GET_TEMP_CODE = 13;
	
	/** 设置 4 个耳标温度标定值 **/
	int SET_CELIBRAT_VALUE = 14;
	
	/** 读取 4 个耳标温度标定值 **/
	int GET_CALIBRAT_VALUE = 15;

}
