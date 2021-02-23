package com.huimv.yzzs.webservice;

public enum ResultCode {
    SUCCESS(0),                        // 成功
    SYSTEM_ERROR(-100),                // 系统错误
    SYSTEM_OTHER_ERROR(-101),        // 其它系统错误
    UNCONNECTION_SERVICE_ERROR(-111),//连接服务器出错
    /**
     * Android Start
     **/
    AD_SUCCESS_DEL_ALL(1001),
    AD_SUCCESS_DEL_MAXDAY(1002),

    AD_BBXX_GX(1004),                // 版本信息有更新
    AD_BBXX_WGX(1005),                // 版本信息有更新
    /**
     * Android End
     **/

	/* 预定义业务错误 */
    BUSINESS_1(-1),
    BUSINESS_2(-2),
    BUSINESS_3(-3),
    BUSINESS_4(-4),
    BUSINESS_5(-5),
    BUSINESS_6(-6),
    BUSINESS_7(-7),
    BUSINESS_8(-8),
    BUSINESS_9(-9),
    BUSINESS_10(-10);

    private final int value;

    ResultCode(int val) {
        this.value = val;
    }

    public int getValue() {
        return value;
    }
}