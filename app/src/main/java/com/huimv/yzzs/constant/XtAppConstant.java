package com.huimv.yzzs.constant;

import com.huimv.yzzs.R;

public interface XtAppConstant {
	/**数据库版本
	 * v2.2.0.16:5
	 * v2.2.0.17:6
	 * v2.2.0.18:7
	 * v2.2.0.19:8
	 * v2.2.0.20:10
	 * v2.2.0.21:11
	  * v2.2.0.22:12
	 * **/
	/** IP**/
	//String SERVICE_IP = "101.231.195.96";//服务器IP
	String SERVICE_IP = "122.112.219.87:8089";//服务器IP华为云
	
	//String SERVICE_IP = "192.168.1.46:8080";//本地服务器IP
	//String SERVICE_IP = "120.26.91.137:8084";//本地服务器IP阿里云
	String CVersion = "V1.3.1.2";
	/** what for Thread **/
	int WHAT_CHECK_VISION = 0;
	int WHAT_BLUETOOTHSCAN_THREAD = 0x1;//BLE搜索
	
	int SCJ_UPLOAD_VISION = 13;
	/**连接服务器失败**/
	String UNCONNETCTION_SERVICE = "连接服务器失败，请检查网络";//连接服务器失败，请检查网络
	/**故障状态**/
	int GUZHANG_ZHONGLEI = 14;//故障种类
	String GUZHANG = "1";//1故障
	String ZHENGCHANG = "0";//0正常
	String XT_WUGUZHANG = "设备无故障";//设备无故障
	/**发送延时**/
	//int FASONGYANSHI_50 = 50;//发送延时
	//int FASONGYANSHI_10 = 10;//发送延时
	int SEND_DELAY_PACK_INTERVAL = 10;//包之间间隔延时
	/**环控获取初始配置发送延时**/
	int SEND_DELAY_GET_INIT_DATA = 2000;//发送延时
	int CONNECT_BLUETOOTH_TIMEOUT = 10000;//超时时间
	int SEND_CMD_TIMEOUT = 3000;//超时时间
	int WHAT_MCXX_SELECT = 10; // 得到牧场信息列表
	int WHAT_JQID_GET = 11; // 得到机器ID
	int WHAT_LYBM_UPLOAD = 12; // 蓝牙别名上传
	int SCJ_READ_TIMEOUT = 10000;//手持机展示间隔时间
	/**判断什么设备的命令**/
	int WHAT_DEV = 2;
	int packHeadH = 'h';//h
	int packHeadM = 'm';//m
	int packBottomE = 'e';//e
	int packBottomN = 'n';//n
	int packBottomD = 'd';//d
	int[] packHead = {'h', 'm'};
	int[] packBottom = {'e', 'n', 'd'};
	
	/** Sp 分隔符  */
	String SEPARSTOR = "#";
	/** 冒号  */
	String COLON = ":";
	/** Hk实时显示TAB图片切换  */
	int[] Hk_tabBg_normal = new int []{R.drawable.tab_icon_wd_normal,R.drawable.tab_icon_sd_normal,R.drawable.tab_icon_aq_normal,R.drawable.tab_icon_ph_normal};
	int[] Hk_tabBg_pressed = new int []{R.drawable.tab_icon_wd_pressed,R.drawable.tab_icon_sd_pressed,R.drawable.tab_icon_aq_pressed,R.drawable.tab_icon_ph_pressed};
	/** Qk实时显示TAB图片切换  */
	int[] Qk_tabBg_pressed = new int []{R.drawable.qk_tab_icon_sy_pressed,R.drawable.qk_tab_icon_sl_pressed,R.drawable.tab_icon_ph_pressed};
	int[] Qk_tabBg_normal = new int []{R.drawable.qk_tab_icon_sy_normal,R.drawable.qk_tab_icon_sl_normal,R.drawable.tab_icon_ph_normal};

	/*** ---------------------设备--------------------------*/
	/** 各个设备类型 **/
	String CGQBD_SBLX_WD = "00"; // 设备类型 - 温度传感器
	String CGQBD_SBLX_SD = "01"; // 设备类型 - 湿度传感器
	String CGQBD_SBLX_AQ = "02"; // 设备类型 - 氨气传感器
	String CGQBD_SBLX_SY = "03"; // 设备类型 - 水压传感器
	String CGQBD_SBLX_PH = "04"; // 设备类型 - PH值传感器
	String CGQBD_SBLX_SL= "31"; // 设备类型 - 水流传感器
	String CGQBD_SBLX_DLJCB= "33"; // 设备类型 - 电流检测板
	String SBBD_SBLX_FJ = "05"; // 设备类型 - 风机
	String SBBD_SBLX_SL = "06"; // 设备类型 - 湿帘
	String SBBD_SBLX_LX = "07"; // 设备类型 - 上料
	String SBBD_SBLX_PW = "08"; // 设备类型 - 喷雾
	String SBBD_SBLX_DR = "09"; //  设备类型 - 地暖
	String SBBD_SBLX_QK_SL = "10"; // 设备类型 - 全控湿帘
	String SBBD_SBLX_BPFJ = "38"; // 设备类型 - 变频风机
	String SBBD_SBLX_LC = "33"; // 设备类型 - 料槽
	String SBBD_SBLX_JRQ = "39"; // 设备类型 - 加热器
	String SBBD_SBLX_TC = "42"; // 设备类型 - 天窗
	String SBBD_SBLX_JL = "43"; // 设备类型 - 卷帘


	String CGQBD_AZWZ_SNKZ = "00"; // 安装位置 - 室内空中
	String CGQBD_AZWZ_SNDM = "01"; // 安装位置 - 室内地面
	String CGQBD_AZWZ_SWKZ = "02"; // 安装位置 - 室外空中
	String CGQBD_AZWZ_SWDM = "03"; // 安装位置 - 室外地面
	String CGQBD_AZWZ_SM = "04"; // 安装位置 - 水面
	String CGQBD_AZWZ_SD = "05"; // 安装位置 - 水底
	
}