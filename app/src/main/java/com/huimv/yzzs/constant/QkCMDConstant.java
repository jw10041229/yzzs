package com.huimv.yzzs.constant;

public interface QkCMDConstant {
	/**水流水压**/
	int SHUIYA_TAG = 1;
	int SHUILIU_TAG = 2;
	int PH_TAG = 3;
	/** 登陆取消时间**/
	int LOGIN_TIME = 3000;
	/**全控标志**/
	int QK_SBBZ = 2;
	
	/**
	 * 设备绑定保存超时
	 */
	int SEND_QK_SBBD_SAVE_TIMEOUT = 3000;//超时时间
	/**几种上传的数据类型**/
	int CGQ_DATA_LX = XtAppConstant.Qk_tabBg_normal.length;//水压，水流
	/**密码验证**/
	int SET_MMYZ = 5;
	/**运行状态**/
	int GET_YXZT = 9;
	/**获取传感器绑定配置**/
	int GET_CGQBD = 13;
	/**保存传感器绑定配置**/
	int SAVE_CGQBDPZ = 14;
	/**运行状态Ex协议拆分**/
	int GET_YXZT_XTGZ = 28;
	/**运行状态Ex协议拆分**/
	int GET_YXZT_CGQGZ = 29;
	/**运行状态Ex协议拆分**/
	int GET_YXZT_SBGZ = 30;
	/**PH**/
	int GET_PH = 31;
	/**获取设备参数配置**/
	int GET_SBCSPZ = 41;
	/**保存设备参数配置**/
	int SAVE_SBCSPZ = 42;
	/**获取设备绑定配置**/
	int GET_SBBDPZ = 43;
	/**保存设备绑定配置**/
	int SAVE_SBBDPZ = 44;
	/**水压**/
	int GET_SHUIYA = 45;
	/**水流**/
	int GET_SHUILIU = 25;
	
	/**系统时间**/
	int GET_SYS_TIME = 46;
}
