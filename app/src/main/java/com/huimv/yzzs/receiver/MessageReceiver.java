package com.huimv.yzzs.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.huimv.android.basic.util.AndroidUtil;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.service.BluetoothService;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;

import static android.R.id.message;

public class MessageReceiver extends BroadcastReceiver {
	//每次收到一条数据都会执行一次
	private final static String TAG = MessageReceiver.class.getSimpleName();
	private byte [] messageData = new byte[0];
	int count = YzzsApplication.messCount;
	private byte[] tempData = YzzsApplication.tempData;
	
	
	public static ArrayList<EventHandler> ehList = new ArrayList<>();

	public interface EventHandler {
		void onConnected(boolean isConnected);//蓝牙是否连接
		void onReady(boolean isReady);//蓝牙是否准备好
		void onMessage(byte[] message);//接收到数据
		void onNetChange(boolean isNetConnected);//网络连接
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {//自启动
			//Intent mainActivityIntent = new Intent(context, BluetoothScanActivity.class);// 要启动的Activity
			//mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//context.startActivity(mainActivityIntent);
			//return;
		}
		if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {// 网络变化
			boolean isNetConnected = AndroidUtil.isConn(context);
			for (int i = 0; i < ehList.size(); i++) {
				ehList.get(i).onNetChange(isNetConnected);
			}
		}
		if (BluetoothService.ACTION_GATT_CONNECTED.equals(action)) {// 连接成功
			Log.e(TAG, "连接成功");
			for (int i = 0; i < ehList.size(); i++) {
				ehList.get(i).onConnected(true);
			}
		} else if (BluetoothService.ACTION_GATT_DISCONNECTED.equals(action)) {// 断开连接
			Log.e(TAG, "连接断开");
			for (int i = 0; i < ehList.size(); i++) {
				ehList.get(i).onConnected(false);
			}
		} else if (BluetoothService.ACTION_GATT_SERVICES_DISCOVERED
				.equals(action)) // 可以开始干活了
		{
			Log.e(TAG, "可以开始干活了");
			for (int i = 0; i < ehList.size(); i++) {
				ehList.get(i).onReady(true);
			}
		} else if (BluetoothService.ACTION_DATA_AVAILABLE.equals(action)) { // 收到数据
			byte[] message = intent.getByteArrayExtra(BluetoothService.EXTRA_DATA);
			Log.e(TAG, "收到数据" + Arrays.toString(message));
			//这里处理接受到的原始数据，整理出一条协议，然后下发
			try {
				getRemovePackData(message);
			} catch (Exception e) {
				Log.d(TAG, "getRemovePackData异常" + Arrays.toString(message));
			}
		}
	}
	
	//下发数据到观察者
	private void dispatchData(byte[] mDispatchData) {
		if (mDispatchData != null) {
			for (int i = 0; i < ehList.size(); i++) {
				ehList.get(i).onMessage(mDispatchData);
			}
		}
	}
	
	/**
	 * 接受原始包进行组装
	 * @param message
	 */
	public void getRemovePackData(byte[] message) {
		//添加判断 防止只能接收包头一直无法接收包尾的问题
		count ++;
		if (count > 300) {
			YzzsApplication.messCount = 0;
			messageData = new byte[0];
			YzzsApplication.tempData = new byte[0];
		}
		int tempDataLength = tempData.length;
		int messageLength = message.length;
		//上次剩下的变量 把新来的数组拼接在后面
		tempData = Arrays.copyOf(tempData, tempDataLength + messageLength);
		System.arraycopy(message, 0, tempData, tempDataLength, messageLength);
		//message:要copy的源数组，0:从源数组的第几位开始copy, tempData: 目的数组 tempData.length:目的数组放置的起始位置，message.length：copy的长度
		//校验临时包必须有包头
		if (tempData.length > 7) {
			for (int i = 0; i < tempData.length - 2; i++) {
				//包头校验
				if (i == 0 && !checkBt()) {
					YzzsApplication.tempData = new byte[0];
					break;
				}
				//包尾存在
				if (tempData[i] == XtAppConstant.packBottomE && 
						tempData[i + 1] == XtAppConstant.packBottomN && 
						tempData[i + 2] == XtAppConstant.packBottomD) {
					//获取第一次正确的协议
					messageData = Arrays.copyOfRange(tempData, 0, i + 3);
					//继续查询协议
					tempData = Arrays.copyOfRange(tempData, i + 3, tempData.length);
					YzzsApplication.tempData = tempData;
					//下发协议
					xfLoad();
					//重新循环
					i = -1;
				}
			}
			//对剩余包进行处理
			for (int i = tempData.length - 1; i > 0; i--) {
				if (tempData[i] == XtAppConstant.packHeadM && 
						tempData[i - 1] == XtAppConstant.packHeadH) {
					YzzsApplication.tempData = Arrays.copyOfRange(tempData, i - 1 , tempData.length);
					break;
				}
			}
		} else {
			YzzsApplication.tempData = new byte[0];
		}
	}
		/**
		 * 包头校验
		 * @return 有包头返回true
		 */
		public boolean checkBt() {
			boolean flag = false;
			//包头校验
			if (!(tempData [0] == XtAppConstant.packHeadH && 
					tempData [1] == XtAppConstant.packHeadM)) {
				//如果出现丢掉包头，那么从下一个包头开始协议
				for (int i = 0; i < tempData.length; i++) {
					//包头满足第一位
					if (tempData[i] == XtAppConstant.packHeadH) {
						//当满足第一位且为最后一位 向下 否则截取进行判断
						if (i == tempData.length - 1) {
							tempData = Arrays.copyOfRange(tempData, i, tempData.length);
							break;
						}
						if (i != tempData.length - 1 && tempData[i + 1] == XtAppConstant.packHeadM) {
							tempData = Arrays.copyOfRange(tempData, i, tempData.length);
							flag = true;
							break;
						}
					}
				}
				
			} else {
				flag = true;
			}
			return flag;
		}
		
		/**
		 * 下发
		 */
		public void xfLoad() {
			count = 0;
			if (messageData.length > 7) {
				int length1 = YzzsCommonUtil.ChangeByteToPositiveNumber(messageData[5]);
				int length2 = YzzsCommonUtil.ChangeByteToPositiveNumber(messageData[6]);
				int length = YzzsCommonUtil.getCMD(length1, length2);
				if (length == messageData.length) {//拼包结束
					messageData = Arrays.copyOfRange(messageData, 2, length - 4);//去协议头尾跟校验位
					dispatchData(messageData);
					com.orhanobut.logger.Logger.d(TAG,Arrays.toString(messageData));
				} else {
					com.orhanobut.logger.Logger.d(TAG,messageData.length + "");
				} 
			}
		}
}
