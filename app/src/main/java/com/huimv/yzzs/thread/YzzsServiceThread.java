package com.huimv.yzzs.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.huimv.yzzs.webservice.ResultSet;
import com.huimv.yzzs.webservice.YzzsServiceClient;

import java.util.HashMap;
import java.util.Map;

public class YzzsServiceThread extends Thread {
	private int what;
	private Handler handler;
	private Context context;
	private Map<String,String> parmasMap = new HashMap<String, String>();
	public YzzsServiceThread(String cmd, String params, String imgStreams, int what,
			Handler handler, Context context) {
		this.what = what;
		this.handler = handler;
		this.context = context;
		parmasMap.put("cmd", cmd);
		parmasMap.put("params", params);
	}

	@Override
	public void run() {
		YzzsServiceClient client = new YzzsServiceClient();
		try {
			ResultSet rs = client.BaseService(parmasMap, context);
			Message msg = handler.obtainMessage();
			msg.obj = rs.getStrJson();
			msg.arg1 = rs.getResultCode().getValue();
			msg.what = what;
			handler.sendMessage(msg);
		} catch (Exception e) {
		}
	}
}