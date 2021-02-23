package com.huimv.yzzs.constant;

/**
 * 料槽蓝牙命令
 * @author jiangwei
 *
 */
public interface LcCMDConstant {
	/**设备标志**/
	int LC_SBBZ = 4;
	/**密码验证**/
	int LC_MMYZ = 5;
	/**运行状态**/
	int GET_FALUT_YXZT = 6;
	/**设备故障**/
	int GTLC_DEV_FZULT = 8;
	/**故障2**/
	int LC_FZ2 = 9;
	/**故障3**/
	int LC_FZ3 = 10;
	
	/**获取水料数据**/
	int LC_GET_SHUI_LIAO_DATA = 7;
	/**下水量**/
	int LC_XSL = 39;
	/**当前料**/
	int LC_DQL = 37;
	/**下料量**/
	int LC_XLL = 38;
	
	/**读水料比等级**/
	int LC_READ_SLB = 12;
	/**写水料比等级**/
	int LC_SAVE_SLB = 13;
	
	/**读下料量**/
	int LC_READ_XLL = 28;
	/**写下料量**/
	int LC_SAVE_XLL = 29;
	
	/**读触碰等级**/
	int LC_READ_CPDJ = 30;
	/**写触碰等级**/
	int LC_SAVE_CPDJ = 36;
	
	/**读时间校准**/
	int LC_READ_SJJZ = 32;
	/**写时间校准**/
	int LC_SAVE_SJJZ = 33;
	
	/**读清盘时间**/
	int LC_READ_QPSJ = 34;
	/**写清盘时间**/
	int LC_SAVE_QPSJ = 35;
	
	/**读取设备**/
	int LC_READ_DEV = 40;
	/**保存设备**/
	int LC_SAVE_DEV = 41;
}