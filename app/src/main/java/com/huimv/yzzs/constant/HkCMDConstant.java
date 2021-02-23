package com.huimv.yzzs.constant;

/**
 * 环控蓝牙命令
 * @author jiangwei
 *
 */
public interface HkCMDConstant {
/**--------------------------------------HK_CONSTANT-------------------------------------------------**/
	String MAX = "max";
	String MIN = "min";
	String DW = "dw";
	/**绑定**/
	String DEV_IS_BANGDING = "1";
	/**不使能**/
	String DEV_ISNOT_SHINENG = "0";
	/**使能**/
	String DEV_IS_SHINENG = "1";
	/**霍尔使能**/
	String DEV_HUER_SHINENG = "1";
	
	/** 登陆取消时间**/
	int LOGIN_TIME = 3000;
	/**温湿度氨气**/
	int WENDU_TAG = 1;
	int SHIDU_TAG = 2;
	int ANQI_TAG = 3;
	int PH_TAG = 4;
	
	/**
	 * 环控绑定超时
	 */
	int SEND_HK_SAVE_CMD_TIMEOUT = 5000;//超时时间
	/**几种上传的数据类型**/
	int CGQ_DATA_LX = XtAppConstant.Hk_tabBg_normal.length;//温度，湿度，氨气，CO,CO2
	/**--------------------------------------CMD-------------------------------------------------**/
	/**环控标志**/
	int HK_SBBZ = 1;
	/**温度Ex**/
	int GET_WENDU = 24;
	/**湿度Ex**/
	int GET_SHIDU = 25;
	/**氨气Ex**/
	int GET_ANQI = 26;
	/**PH**/
	int GET_PH = 31;
	/**运行状态Ex协议拆分**/
	int GET_YXZT_DWGZ = 28;
	/**运行状态Ex协议拆分**/
	int GET_YXZT_CGQGZ = 29;
	/**运行状态Ex协议拆分**/
	int GET_YXZT_SBGZ = 30;
	/**运行状态**/
	int GET_YXZT = 9;
	/**密码验证**/
	int SET_MMYZ = 5;
	/**获取设备配置**/
	int GET_SBPZ = 7;
	/**保存设备配置**/
	int SAVE_SBPZ = 8;
	/**获取环控绑定配置**/
	int GET_HKBDPZ = 11;
	/**保存环控绑定配置**/
	int SAVE_HKBDPZ = 12;
	/**保存传感器绑定配置**/
	int SAVE_CGQBDPZ = 14;
	/**获取传感器绑定配置**/
	int GET_CGQBD = 13;
	/**地暖温度**/
	int READ_DN_TEMP = 50;
	int SAVE_DN_TEMP = 51;
	/**地暖强制开关**/
	int DN_FORCE_SWTICH= 52;
	/**高低温保护**/
	int READ_DN_PROTECT_TEMP = 53;
	int SAVE_DN_PROTECT_TEMP = 54;
	/**启动温差**/
	int READ_DN_START_TEMP = 55;
	int SAVE_DN_START_TEMP = 56;
	
	/**设置默认档位**/
	int READ_DEFALUT_GEAR = 57;
	int SAVE_DEFALUT_GEAR = 58;

	/**设置硬件版本**/
	int READ_YJ_VERSION = 59;
	int SAVE_YJ_VERSION = 60;

	/**加热器温度设置**/
	int READ_JRQ_TEMP = 61;
	int SAVE_JRQ_TEMP = 62;

	/**读取轮训时间**/
	int READ_FJLXSJ_TEMP = 63;
	int SAVE_FJLXSJ_TEMP = 64;

	/**读取目标温度**/
	int READ_TARGET_TEMP = 65;
	int SAVE_TARGET_TEMP = 66;

	/**读取加热器温度设置EX**/
	int READ_JRQ_TEMP_EX = 67;
	int SAVE_JRQ_TEMP_EX = 68;
}