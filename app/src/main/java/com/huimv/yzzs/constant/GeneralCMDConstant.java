package com.huimv.yzzs.constant;
/**
 * 通用代码命令
 * @author jiangwei
 *
 */
public interface GeneralCMDConstant {
	/**普通用户**/
	String IDENTITY_ORDINARY = "1";
	/**调试人员**/
	String IDENTITY_DEBUG = "2";
	
	
	/**恢复出厂设置**/
	int RESTORE_FACTORY_SET = 15;
	/**机器ID**/
	int READ_JQID = 16;
	int CHANGE_JQID = 17;
	/**蓝牙名称**/
	int READ_LYMC = 18;
	int CHANGE_LYMC = 19;
	/**IP**/
	int READ_IP = 20;
	int CHANGE_IP = 21;
	/**网关**/
	int READ_WG = 22;
	int CHANGE_WG = 23;
	/**重启系统设备**/
	int RESTART_XT_DEVICE = 27;
	
	
	/**读取断电确认时间**/
	int READ_DD_QRSJ = 100;
	int SAVE_DD_QRSJ = 101;
	/** 版本信息*/
	int READ_BBXX = 102;
	/**SMS舍**/
	int READ_SMS_SHE = 103;
	int SAVE_SMS_SHE = 104;
	
	/**掩码**/
	int READ_YM = 105;
	int CHANGE_YM = 106;
	
	/**报警器手机**/
	int READ_BJQ_PHONE_NUMBER = 107;
	int SAVE_BJQ_PHONE_NUMBER = 108;

	/**mac 地址**/
	int READ_MAC_ADDRESS = 109;
	int CHANGE_MAC_ADDRESS = 110;

	/**ip冲突检测**/
	int CHECK_IP_CONFLICT = 111;
	/**读取蓝牙指示灯版本**/
	int READ_LYZSD_VERISON = 112;
	int SAVE_LYZSD_VERISON = 113;
}
