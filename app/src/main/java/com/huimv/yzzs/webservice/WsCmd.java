package com.huimv.yzzs.webservice;

public interface WsCmd {

    String HM_YZZS_VISION_UPGRADE = "HM_YZZS_VISION_UPGRADE";// 版本检测

    String HM_MCXX_SELECT = "HM_MCXX_SELECT"; // 根据用户姓名与用户密码获取牧场信息
    String HM_JQID_GET = "HM_JQID_GET";// 获取 机器id
    String HM_LYBM_UPLOAD = "HM_LYBM_UPLOAD"; // 蓝牙别名上传
    String HM_SCJ_DATA_UPLOAD = "HM_SCJ_DATA_UPLOAD";//手持机数据上传
    // webservice 返回状态定义，跟服务器端保持一致
    int SUCCESS = 0;
    int SYSTEM_ERROR = -100;
    int SYSTEM_OTHER_ERROR = -101;
    int SYSTEM_UPGRADE = 1;
    int PARAM_ERROR = -1;
    int REC_NUM_ERROR = -2;
    int BUSINESS_3 = -3;
    int BUSINESS_4 = -4;
    int BUSINESS_5 = -5;
    int BUSINESS_6 = -6;
    int BUSINESS_7 = -7;
    int BUSINESS_8 = -8;
    int BUSINESS_9 = -9;
    int BUSINESS_10 = -10;
    int AD_BBXX_GX = 1004;
    int AD_BBXX_WGX = 1005;
}