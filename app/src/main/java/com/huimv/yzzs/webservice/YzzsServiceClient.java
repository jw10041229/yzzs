package com.huimv.yzzs.webservice;

import java.util.Map;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.yzzs.constant.XtAppConstant;

import android.content.Context;

public class YzzsServiceClient {
    private String nullStr = "[]";

    public ResultSet BaseService(Map<String, String> parmasMap, Context context)
            throws Exception {
        ResultSet rs = new ResultSet();
        String namespace = ""; // 使用公司的命名空间，可为空
        String url = "http://" + XtAppConstant.SERVICE_IP
                + "/ifm/services/HmYzzsService";
        String methodName = "YzzsService"; // 指定调用的服务名
        String soupaction = namespace + "/" + methodName;
        SoapObject soapObject = new SoapObject(namespace, methodName);
        soapObject.addProperty("cmd", parmasMap.get("cmd"));
        soapObject.addProperty("strJsonParam", parmasMap.get("params"));
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.bodyOut = soapObject;
        envelope.setOutputSoapObject(soapObject);
        HttpTransportSE transport = new HttpTransportSE(url);
        try {
            transport.call(soupaction, envelope);
            if (envelope.getResponse() != null) {
                // 获取服务器响应返回的SOAP消息,如果三个属性为空的话 采用抛异常的方式解决
                SoapObject result = (SoapObject) envelope.bodyIn;
                SoapObject out = (SoapObject) result.getProperty("out");
                // 封装结果到ResultSet
                SoapPrimitive sp;
                sp = (SoapPrimitive) out.getProperty("message");
                rs.setMessage(sp.toString());
                sp = (SoapPrimitive) out.getProperty("resultCode");
                rs.setResultCode(Enum.valueOf(ResultCode.class, sp.toString()));
                sp = (SoapPrimitive) out.getProperty("strJson");
                rs.setStrJson(sp.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof java.net.SocketTimeoutException ||
                    e instanceof java.net.UnknownHostException ||
                    e instanceof java.net.ConnectException) {
                rs.setMessage("连接服务器失败,请检查网络");
                rs.setResultCode(Enum.valueOf(ResultCode.class, "UNCONNECTION_SERVICE_ERROR"));
            }
            rs.setStrJson(nullStr);
        }
        if (CommonUtil.isEmpty(rs)) {
            rs.setMessage("请求数据出错");
            rs.setResultCode(Enum.valueOf(ResultCode.class, "SYSTEM_DATA_ERROR"));
            rs.setStrJson(nullStr);
        }
        return rs;
    }
}
