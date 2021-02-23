package com.huimv.yzzs.fragment.scj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.huimv.yzzs.R;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.constant.ScjCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.YzzsCommonUtil;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ScjSsxsMoreFragment extends YzzsBaseFragment implements EventHandler {

	private TextView tvTempValue, tvTempCodeValue, tvRssiValue, tvSenssorCodeValue, tvBatteryInfo, tvRfidValue,
			tvScjState;
	private ImageView ivBatteryValue;
	private RelativeLayout rrBtnOpenLayout, rrBtnCloseLayout;
	private ImageView ivBtnOpen, ivBtnClose;

	public static final int SWITCH_SCJ_WORK_START = 1; // 切换可读状态
	public static final int SWITCH_SCJ_WORK_STOP = 0; // 切换不可读状态
	public static final int SWITCH_SCJ_NOMORE_MSG = 0; // 切换正常状态

	public static String REGEX_SPACE = " "; // 空格

	private Runnable mRunnable; // 超时定时器
	private Handler handler = new Handler();
	private boolean isReveivedData = false;

	private List<Integer> batteryValueList = new ArrayList<>();

	private boolean firstRefreshFlag = true; // 第一次显示电量值得标志位

	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_scj_ssxs_more_fragmrnt, null);
		initView(view);
		initParams();
		initOnListeners();
		return view;
	}

	private void initView(View view) {
		tvTempValue = (TextView) view.findViewById(R.id.tv_temp_value);
		tvTempCodeValue = (TextView) view.findViewById(R.id.tv_temp_code_value);
		tvRssiValue = (TextView) view.findViewById(R.id.tv_rssi_value);
		tvSenssorCodeValue = (TextView) view.findViewById(R.id.tv_senssor_code_value);
		tvBatteryInfo = (TextView) view.findViewById(R.id.tv_battery_info);
		tvRfidValue = (TextView) view.findViewById(R.id.tv_rfid_value);
		tvScjState = (TextView) view.findViewById(R.id.tv_scj_state);

		rrBtnOpenLayout = (RelativeLayout) view.findViewById(R.id.rr_btn_open_layout);
		rrBtnCloseLayout = (RelativeLayout) view.findViewById(R.id.rr_btn_close_layout);

		ivBtnOpen = (ImageView) view.findViewById(R.id.iv_btn_open);
		ivBtnClose = (ImageView) view.findViewById(R.id.iv_btn_close);
		ivBatteryValue = (ImageView) view.findViewById(R.id.iv_battery_value);

	}

	private void initOnListeners() {
		ivBtnOpen.setOnClickListener(this);
		ivBtnClose.setOnClickListener(this);
	}

	private void initParams() {
		// 默认是 不可读 状态
		tvScjState.setText("不可读");
		rrBtnOpenLayout.setVisibility(View.VISIBLE);
		rrBtnCloseLayout.setVisibility(View.GONE);
	}

	@Override
	public void onResume() {
		MessageReceiver.ehList.add(this);
		super.onResume();
	}

	@Override
	public void onPause() {
		MessageReceiver.ehList.remove(this);
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 发送显示普通信息命令
		sendSwitchScjStateToBt(ScjCMDConstant.SWITCH_SCJ_MORE_MSG, SWITCH_SCJ_NOMORE_MSG);
	}

	@Override
	public void onClick(View v) {

		if (YzzsApplication.isConnected) {
			super.onClick(v);
			switch (v.getId()) {
			case R.id.iv_btn_open:
				sendSwitchScjStateToBt(ScjCMDConstant.SWITCH_SCJ_WORK_STATE, SWITCH_SCJ_WORK_START);
				startTimerCount(2000); // 开启定时器
				break;
			case R.id.iv_btn_close:
				sendSwitchScjStateToBt(ScjCMDConstant.SWITCH_SCJ_WORK_STATE, SWITCH_SCJ_WORK_STOP);
				startTimerCount(2000); // 开启定时器
				break;
			}

		} else {
			ToastMsg(getActivity(), "蓝牙已断开连接，请到首页重新连接");
		}
	}

	@Override
	public void onConnected(boolean isConnected) {
		YzzsApplication.isConnected = isConnected;
		if (!isConnected) {// 如果断开
			ToastMsg(getActivity(), getString(R.string.disconnected));
		}
	}

	@Override
	public void onReady(boolean isReady) {

	}

	@Override
	public void onMessage(byte[] message) {
		receivePack(message);
	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		// TODO Auto-generated method stub
	}

	/**
	 * 接收包
	 * 
	 * @param message
	 */
	private void receivePack(byte[] message) {
		if (message[0] == ScjCMDConstant.SCJ_SBBZ) { // 手持机
			try {
				refrehRealDataEx(message);
			} catch (Exception e) {
				dismissLoading();
				ToastMsg(getActivity(), "收到数据格式错误");
			}
		}
	}

	/**
	 * 更新 RFID 和 体温 的实时数据
	 * 
	 * @param message
	 */
	private void refrehRealDataEx(byte[] message) {
		// 截取实时数据：从第 6 个位置开始截取
		byte[] realData = Arrays.copyOfRange(message, 6, message.length);

		int cmd = YzzsCommonUtil.getCMD(message[1], message[2]);
		if (cmd == ScjCMDConstant.GET_RFID) { // 得到 RFID 数据
			String StrRfid = bytesToHexString(realData, REGEX_SPACE);
			tvRfidValue.setGravity(Gravity.START);
			tvRfidValue.setText(StrRfid);
		}
		if (cmd == ScjCMDConstant.GET_TEMP) { // 得到 温度 数据
			// 显示 温度值
			String temp = bytesToString(realData, REGEX_SPACE);
			String[] strArr = temp.split(REGEX_SPACE);
			String tempValue = strArr[0] + "." + strArr[1];
			tvTempValue.setText(tempValue);

			// 显示温度 TEMP 的 hex 值
			String StrTemp = bytesToHexString(realData, REGEX_SPACE);
			tvTempCodeValue.setText(StrTemp);
		}
		if (cmd == ScjCMDConstant.GET_SENSSOR_CODE) { // 得到 senssorCode 数据
			String StrSenssorCode = bytesToHexString(realData, REGEX_SPACE);
			tvSenssorCodeValue.setText(StrSenssorCode);
		}
		if (cmd == ScjCMDConstant.GET_RSSI) { // 得到 rssi 数据
			String StrRssi = bytesToHexString(realData, REGEX_SPACE);
			tvRssiValue.setText(StrRssi);
		}
		if (cmd == ScjCMDConstant.GET_TEMP_CODE) { // 得到 TEMP CODE 数据
			// 显示温度 TEMP 的 hex 值
			String StrTempCode = bytesToHexString(realData, REGEX_SPACE);
			tvTempCodeValue.setText(StrTempCode);
		}
		if (cmd == ScjCMDConstant.GET_BATTERY) { // 得到电量
			String StrBattery = bytesToString(realData, REGEX_SPACE);
			String[] batttery = StrBattery.split(REGEX_SPACE);
			// 当前电量值
			int batteryValue = Integer.valueOf(batttery[1]);
			showAndFreshBatteryValue(batteryValue);
			// 手持机是否在充电
			int isChargingFlag = Integer.valueOf(batttery[0]);
			if (isChargingFlag == 1) {
				if (!firstRefreshFlag) {
					tvBatteryInfo.setVisibility(View.VISIBLE);
					tvBatteryInfo.setText("正在充电中...");
				}
			} else {
				if (!firstRefreshFlag) {
					tvBatteryInfo.setVisibility(View.GONE);
				}
			}
		}
		if (cmd == ScjCMDConstant.SWITCH_SCJ_WORK_STATE) { // 设置手持机状态
			isReveivedData = true;
			handler.removeCallbacks(mRunnable); // 取消定时
			dismissLoading();
			if (message[5] == 0 && message[6] == 1) {
				ToastMsg(getActivity(), "手持机切换“可读”状态成功");
				tvScjState.setText("可读");
				rrBtnOpenLayout.setVisibility(View.GONE);
				rrBtnCloseLayout.setVisibility(View.VISIBLE);
			}
			if (message[5] == 0 && message[6] == 0) {
				ToastMsg(getActivity(), "手持机切换“不可读”状态成功");
				tvScjState.setText("不可读");
				rrBtnOpenLayout.setVisibility(View.VISIBLE);
				rrBtnCloseLayout.setVisibility(View.GONE);
			}
		}

	}

	private void showAndFreshBatteryValue(int batteryVal) {
		batteryValueList.add(batteryVal);
		// 第一次显示电量值，只采集 5 个点
		if (firstRefreshFlag) {
			if (batteryValueList.size() == 5) {
				// 将 list 排序
				Collections.sort(batteryValueList);
				// 设置电量值
				Integer currentValue = batteryValueList.get(2);
				// 隐藏提示消息“读取中...”
				tvBatteryInfo.setVisibility(View.GONE);
				switchBatteryIcon(currentValue);
				// 清空 list
				batteryValueList.clear();
				firstRefreshFlag = false;
			}
		} else {
			// 接下来每采集30个点刷新一次电量值
			if (batteryValueList.size() == 31) {
				// 将 list 排序
				Collections.sort(batteryValueList);
				// 设置电量值
				Integer currentValue = batteryValueList.get(15);
				switchBatteryIcon(currentValue);
				// 清空 list
				batteryValueList.clear();
			}
		}
	}

	/**
	 * 根据当前电量值，切换电量图片Icon
	 * 
	 * @param currentValue
	 *            当前电量值
	 * @param currentValue
	 *            是否在充电的flag
	 */
	private void switchBatteryIcon(int currentValue) {
		if (currentValue > 0 && currentValue <= 30) {
			tvBatteryInfo.setVisibility(View.VISIBLE);
			tvBatteryInfo.setText("当前电量过低，请尽快充电...");
			ivBatteryValue.setImageResource(R.drawable.icon_battery_one);
		}
		if (currentValue > 30 && currentValue <= 50) {
			ivBatteryValue.setImageResource(R.drawable.icon_battery_two);
		}
		if (currentValue > 50 && currentValue <= 70) {
			ivBatteryValue.setImageResource(R.drawable.icon_battery_three);
		}
		if (currentValue > 70) {
			ivBatteryValue.setImageResource(R.drawable.icon_battery_four);
		}
	}

	/**
	 * byte[]转换成十六进制字符串
	 * 
	 * @param bArray
	 * @param regex
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public String bytesToHexString(byte[] bArray, String regex) {
		StringBuilder sb = new StringBuilder(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase() + regex);
		}
		// 去掉最后一个 regex
		sb.substring(0, sb.length() - 1);
		return sb.toString();
	}

	/**
	 * byte[]转换成字符串，byte值中间用 regex 隔开（负数转为正数显示）
	 * 
	 * @param bArray
	 * @param regex
	 * @return
	 */
	private String bytesToString(byte[] bArray, String regex) {
		StringBuilder sb = new StringBuilder();
		sb.delete(0, sb.length());
		for (int i = 0; i < bArray.length; i++) {
			// 负数转为正数
			if (bArray[i] < 0) {
				sb.append(YzzsCommonUtil.ChangeByteToPositiveNumber(bArray[i]) + regex);
			} else {
				sb.append(byteToString(bArray[i]) + regex);
			}
		}
		// 去掉最后一位 regex
		sb.substring(0, sb.length() - 1);
		return sb.toString();
	}

	/**
	 * byte 转 String
	 * 
	 * @param b
	 * @return
	 */
	private String byteToString(byte b) {
		StringBuilder sb = new StringBuilder();
		sb.delete(0, sb.length());
		sb.append(b);
		return sb.toString();
	}

	/**
	 * 发送切换手持机状态的命令到 bt
	 * 
	 * @param CmdType
	 * @param dataType
	 *            命令类型：0：设置手持机为“不可读状态”；或者：切换为“显示普通状态”
	 *            1：设置手持机为“可读状态”；或者切换为“显示更多状态”
	 */
	private void sendSwitchScjStateToBt(int CmdType, int dataType) {
		int dataLength = 12; // 数据总长度
		byte[] byteSendData = new byte[dataLength]; // 构造 byte 数组
		byteSendData[0] = YzzsCommonUtil.intTobyte(104); // 协议头 “h”
		byteSendData[1] = YzzsCommonUtil.intTobyte(109); // 协议头 “m”
		byteSendData[2] = YzzsCommonUtil.intTobyte(ScjCMDConstant.SCJ_SBBZ); // 数据域：类型
		byteSendData[3] = YzzsCommonUtil.intTobyte(CmdType / 256); // 数据域：命令高位
		byteSendData[4] = YzzsCommonUtil.intTobyte(CmdType % 256); // 数据域：命令低位
		byteSendData[5] = YzzsCommonUtil.intTobyte(dataLength / 256); // 数据域：数据总长度高位
		byteSendData[6] = YzzsCommonUtil.intTobyte(dataLength % 256); // 数据域：数据总长度低位
		byteSendData[7] = YzzsCommonUtil.intTobyte(dataType); // 数据域：单条有效数据长度：0
		byteSendData[8] = YzzsCommonUtil.intTobyte(1); // 校验位
		byteSendData[9] = YzzsCommonUtil.intTobyte(101); // 协议尾：“e”
		byteSendData[10] = YzzsCommonUtil.intTobyte(110); // 协议尾：“n”
		byteSendData[11] = YzzsCommonUtil.intTobyte(100); // 协议尾：“d”
		sendUnpackData(byteSendData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}

	/**
	 * 定时器，超过定时时间自动取消 dialog 框
	 * 
	 * @param timeValue
	 *            定时时间
	 */
	private void startTimerCount(int timeValue) {
		mRunnable = new Runnable() {
			@Override
			public void run() {
				if (isReveivedData) {
					// 2 秒钟有收到数据，则停止计时
					handler.removeCallbacks(this);
					isReveivedData = false; // 标志位重新置 false
				} else {
					dismissLoading();
					ToastMsg(getActivity(), "连接超时，请重新操作");
				}
			}
		};
		handler.postDelayed(mRunnable, timeValue); // 开始 2 秒钟定时，2 秒后，执行 run 方法
	}

}
