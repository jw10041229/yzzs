package com.huimv.yzzs.webservice;

public class ResultSet {
    private ResultCode resultCode;
    private String message;
    private String strJson;

    public ResultSet() {
    }

    public ResultSet(ResultCode code, String msg, String strJson) {
        this.setResultCode(code);
        this.setMessage(msg);
        this.setStrJson(strJson);
    }

    public ResultSet(ResultCode code, String msg) {
        this(code, msg, "");
    }

    public ResultSet(ResultCode code) {
        this(code, "", "");
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStrJson() {
        return strJson;
    }

    public void setStrJson(String strJson) {
        this.strJson = strJson;
    }

    public boolean IsSuccess() {
        return resultCode.getValue() >= ResultCode.SUCCESS.getValue();

    }
}
