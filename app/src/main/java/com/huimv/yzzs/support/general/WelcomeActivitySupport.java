package com.huimv.yzzs.support.general;

import org.json.JSONException;
import org.json.JSONObject;

import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.util.YzzsCommonUtil;

import android.content.Context;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;

import com.huimv.yzzs.thread.YzzsServiceThread;
import com.huimv.yzzs.webservice.WsCmd;


public class WelcomeActivitySupport {
    /** 版本升级线程 **/
    public static void VisionCheckThread(String param, Context context, Handler handler) {
        new YzzsServiceThread(WsCmd.HM_YZZS_VISION_UPGRADE, param, "",
                XtAppConstant.WHAT_CHECK_VISION, handler, context).start();
    }
	/**版本信息数据处理* */
	public static void doVisionDataParsing(String data,Context context) {
		try {
			JSONObject jsonObject = new JSONObject(data);
			Spanned gengXinMsg;
			String url = (String) jsonObject.get("xbburl");
			gengXinMsg = Html.fromHtml((String) jsonObject.get("gxsm"));
			new YzzsCommonUtil(context, gengXinMsg, url).exitDialog();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
