package com.huimv.yzzs.fragment.scj;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huimv.yzzs.R;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.constant.ScjCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.YzzsCommonUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressLint("DefaultLocale")
public class ScjRfidTempFragment extends YzzsBaseFragment implements OnClickListener, EventHandler {
	private TextView tvScjState, tvRfidValue, tvTempCodeValue, tvCode1Value, tvTemp1Value, tvCode2Value, tvTemp2Value;
	private Button btnSet;
	private ImageView ibtnScjStateOpen, ibtnScjStateClose, ibtnRead;
	private EditText etCode1Value, etTemp1Value, etCode2Value, etTemp2Value;
	private RelativeLayout rlScjStateOpen, rlScjStateClose;

	private Runnable mRunnable; // 超时定时器
	private Handler handler = new Handler();
	private boolean isReveivedData = false;

	public static int GET_CALIBRAT_VALUE = 1; // 得到 4个标定值

	public static int SWITCH_SCJ_WORK_START = 1; // 打开开关
	public static int SWITCH_SCJ_WORK_STOP = 0; // 关闭开关

	public static String REGEX_SPACE = " "; // 空格

	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_scj_rfid_temp_fragment, null);
		initView(view);
		initParams();
		initOnListeners();
		return view;
	}

	private void initView(View view) {
		etCode1Value = (EditText) view.findViewById(R.id.et_code1_value);
		etTemp1Value = (EditText) view.findViewById(R.id.et_temp1_value);
		etCode2Value = (EditText) view.findViewById(R.id.et_code2_value);
		etTemp2Value = (EditText) view.findViewById(R.id.et_temp2_value);

		tvScjState = (TextView) view.findViewById(R.id.tv_scj_state);
		tvRfidValue = (TextView) view.findViewById(R.id.tv_rfid_value);
		tvTempCodeValue = (TextView) view.findViewById(R.id.tv_temp_code_value);
		tvCode1Value = (TextView) view.findViewById(R.id.tv_code1_value);
		tvTemp1Value = (TextView) view.findViewById(R.id.tv_temp1_value);
		tvCode2Value = (TextView) view.findViewById(R.id.tv_code2_value);
		tvTemp2Value = (TextView) view.findViewById(R.id.tv_temp2_value);

		btnSet = (Button) view.findViewById(R.id.btn_set);

		ibtnScjStateOpen = (ImageView) view.findViewById(R.id.ibtn_scj_state_open);
		ibtnScjStateClose = (ImageView) view.findViewById(R.id.ibtn_scj_state_close);
		ibtnRead = (ImageView) view.findViewById(R.id.ibtn_read);

		rlScjStateOpen = (RelativeLayout) view.findViewById(R.id.rl_scj_state_open);
		rlScjStateClose = (RelativeLayout) view.findViewById(R.id.rl_scj_state_close);
	}

	private void initOnListeners() {
		ibtnScjStateOpen.setOnClickListener(this);
		ibtnScjStateClose.setOnClickListener(this);
		ibtnRead.setOnClickListener(this);
		btnSet.setOnClickListener(this);
	}

	private void initParams() {
		// 默认处于“不可读”状态
		tvScjState.setText("不可读");
		rlScjStateOpen.setVisibility(View.VISIBLE);
		rlScjStateClose.setVisibility(View.GONE);
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
	public void onConnected(boolean isConnected) {
		YzzsApplication.isConnected = isConnected;
		if (!isConnected) {// 如果断开
			ToastMsg(getActivity(), getString(R.string.disconnected));
		}
	}

	@Override
	public void onReady(boolean isReady) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(byte[] message) {
		receivePack(message);
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
		if (cmd == ScjCMDConstant.GET_TEMP_CODE) { // 得到 TEMP CODE 数据（10进制显示）
			int tempCode = YzzsCommonUtil.ChangeByteToPositiveNumber(message[6]) * 256
					+ YzzsCommonUtil.ChangeByteToPositiveNumber(message[7]);
			tvTempCodeValue.setText(String.valueOf(tempCode));
		}
		if (cmd == ScjCMDConstant.SWITCH_SCJ_WORK_STATE) { // 设置手持机状态
			isReveivedData = true;
			handler.removeCallbacks(mRunnable); // 取消定时
			dismissLoading();
			if (message[5] == 0 && message[6] == 1) {
				ToastMsg(getActivity(), "手持机切换“可读”状态成功");
				tvScjState.setText("可读");
				rlScjStateOpen.setVisibility(View.GONE);
				rlScjStateClose.setVisibility(View.VISIBLE);
			}
			if (message[5] == 0 && message[6] == 0) {
				ToastMsg(getActivity(), "手持机切换“不可读”状态成功");
				tvScjState.setText("不可读");
				rlScjStateOpen.setVisibility(View.VISIBLE);
				rlScjStateClose.setVisibility(View.GONE);
			}
		}

		if (cmd == ScjCMDConstant.SET_CELIBRAT_VALUE) { // 设置 4 个标志值
			dismissLoading();
			isReveivedData = true;
			handler.removeCallbacks(mRunnable); // 取消定时
			if (message[5] == 0) {
				int code1 = Integer.valueOf(YzzsCommonUtil.ChangeByteToPositiveNumber(message[6]) * 256
						+ YzzsCommonUtil.ChangeByteToPositiveNumber(message[7]));
				int temp1 = Integer.valueOf(YzzsCommonUtil.ChangeByteToPositiveNumber(message[8]) * 256
						+ YzzsCommonUtil.ChangeByteToPositiveNumber(message[9]));
				int code2 = Integer.valueOf(YzzsCommonUtil.ChangeByteToPositiveNumber(message[10]) * 256
						+ YzzsCommonUtil.ChangeByteToPositiveNumber(message[11]));
				int temp2 = Integer.valueOf(YzzsCommonUtil.ChangeByteToPositiveNumber(message[12]) * 256
						+ YzzsCommonUtil.ChangeByteToPositiveNumber(message[13]));
				tvCode1Value.setText(String.valueOf(code1));
				tvTemp1Value.setText(String.valueOf(temp1));
				tvCode2Value.setText(String.valueOf(code2));
				tvTemp2Value.setText(String.valueOf(temp2));
			} else {
				ToastMsg(getActivity(), "设置标定值失败...");
			}
		}

		if (cmd == ScjCMDConstant.GET_CALIBRAT_VALUE) { // 得到 4 个标志值
			isReveivedData = true;
			handler.removeCallbacks(mRunnable); // 取消定时
			dismissLoading();
			if (message[5] == 0) {
				int code1 = YzzsCommonUtil.ChangeByteToPositiveNumber(message[6]) * 256
						+ YzzsCommonUtil.ChangeByteToPositiveNumber(message[7]);
				int temp1 = YzzsCommonUtil.ChangeByteToPositiveNumber(message[8]) * 256
						+ YzzsCommonUtil.ChangeByteToPositiveNumber(message[9]);
				int code2 = YzzsCommonUtil.ChangeByteToPositiveNumber(message[10]) * 256
						+ YzzsCommonUtil.ChangeByteToPositiveNumber(message[11]);
				int temp2 = YzzsCommonUtil.ChangeByteToPositiveNumber(message[12]) * 256
						+ YzzsCommonUtil.ChangeByteToPositiveNumber(message[13]);
				tvCode1Value.setText(String.valueOf(code1));
				tvTemp1Value.setText(String.valueOf(temp1));
				tvCode2Value.setText(String.valueOf(code2));
				tvTemp2Value.setText(String.valueOf(temp2));
			} else {
				ToastMsg(getActivity(), "标定值读取失败...");
			}
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

	@Override
	public void onNetChange(boolean isNetConnected) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		if (YzzsApplication.isConnected) {
			super.onClick(v);
			switch (v.getId()) {
			case R.id.ibtn_scj_state_open:
				sendCMDToBtDataLength12(ScjCMDConstant.SWITCH_SCJ_WORK_STATE, SWITCH_SCJ_WORK_START);
				startTimerCount(2000); // 开启定时器
				break;

			case R.id.ibtn_scj_state_close:
				sendCMDToBtDataLength12(ScjCMDConstant.SWITCH_SCJ_WORK_STATE, SWITCH_SCJ_WORK_STOP);
				startTimerCount(2000); // 开启定时器
				break;

			case R.id.btn_set:
				String code1 = etCode1Value.getText().toString().trim();
				String temp1 = etTemp1Value.getText().toString().trim();
				String code2 = etCode2Value.getText().toString().trim();
				String temp2 = etTemp2Value.getText().toString().trim();
				if(validateEditTextValue(code1, temp1, code2, temp2)){
					// 向蓝牙发送 4 个校准值
					sendCalibratValueToBt(ScjCMDConstant.SET_CELIBRAT_VALUE, Integer.valueOf(code1), Integer.valueOf(temp1),
							Integer.valueOf(code2), Integer.valueOf(temp2));
					startTimerCount(2000); // 开启定时器
				} else {
					ToastMsg(getActivity(), "输入的 4 个校准值不能为空...");
				}
				break;

			case R.id.ibtn_read:
				showLoading("正在读取标定值...");
				sendCMDToBtDataLength12(ScjCMDConstant.GET_CALIBRAT_VALUE, GET_CALIBRAT_VALUE);
				startTimerCount(2000); // 开启定时器
				break;
			}

		} else {
			ToastMsg(getActivity(), "蓝牙已断开连接，请到首页重新连接");
		}
	}
	
	/**
	 * 校验 4 个校准值不能为空
	 * @return true:不为空；false:有值为空
	 */
	private boolean validateEditTextValue(String code1, String temp1, String code2, String temp2){
		boolean isDataHasNull = true;
		List<String> list = new ArrayList<>();
		list.add(code1);
		list.add(temp1);
		list.add(code2);
		list.add(temp2);
		for (String str : list) {
			if("".equals(str)){
				isDataHasNull = false;
				break;
			}
		}
		return isDataHasNull;
	}

	/**
	 * 发送命令到 bt，数据总长度为12的命令
	 * 
	 * @param CmdType
	 * @param dataType
	 *            命令类型：0：设置手持机为“不可读状态”；或者：切换为“显示普通状态”
	 *            1：设置手持机为“可读状态”；或者切换为“显示更多状态”
	 */
	private void sendCMDToBtDataLength12(int CmdType, int dataType) {
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
	 * 发送设置的标定值到蓝牙
	 * 
	 * @param CmdType
	 * @param temp1
	 * @param code1
	 * @param temp2
	 * @param code2
	 */
	private void sendCalibratValueToBt(int CmdType, int code1, int temp1, int code2, int temp2) {
		int dataLength = 19; // 数据总长度
		byte[] byteSendData = new byte[dataLength]; // 构造 byte 数组
		byteSendData[0] = YzzsCommonUtil.intTobyte(104); // 协议头 “h”
		byteSendData[1] = YzzsCommonUtil.intTobyte(109); // 协议头 “m”
		byteSendData[2] = YzzsCommonUtil.intTobyte(ScjCMDConstant.SCJ_SBBZ); // 数据域：类型
		byteSendData[3] = YzzsCommonUtil.intTobyte(CmdType / 256); // 数据域：命令高位
		byteSendData[4] = YzzsCommonUtil.intTobyte(CmdType % 256); // 数据域：命令低位
		byteSendData[5] = YzzsCommonUtil.intTobyte(dataLength / 256); // 数据域：数据总长度高位
		byteSendData[6] = YzzsCommonUtil.intTobyte(dataLength % 256); // 数据域：数据总长度低位

		byteSendData[7] = YzzsCommonUtil.intTobyte(code1 / 256); // code1
		byteSendData[8] = YzzsCommonUtil.intTobyte(code1 % 256);
		byteSendData[9] = YzzsCommonUtil.intTobyte(temp1 / 256); // temp1
		byteSendData[10] = YzzsCommonUtil.intTobyte(temp1 % 256);
		byteSendData[11] = YzzsCommonUtil.intTobyte(code2 / 256); // code2
		byteSendData[12] = YzzsCommonUtil.intTobyte(code2 % 256);
		byteSendData[13] = YzzsCommonUtil.intTobyte(temp2 / 256); // temp2
		byteSendData[14] = YzzsCommonUtil.intTobyte(temp2 % 256);

		byteSendData[15] = YzzsCommonUtil.intTobyte(1); // 校验位
		byteSendData[16] = YzzsCommonUtil.intTobyte(101); // 协议尾：“e”
		byteSendData[17] = YzzsCommonUtil.intTobyte(110); // 协议尾：“n”
		byteSendData[18] = YzzsCommonUtil.intTobyte(100); // 协议尾：“d”
		sendUnpackData(byteSendData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
		showLoading("正在设置标定值...");
		startTimerCount(2000); // 开启定时器
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
