package com.huimv.yzzs.fragment.general;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.GeneralCMDConstant;
import com.huimv.yzzs.constant.HkCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.AsciiUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.orhanobut.logger.Logger;

import java.util.Arrays;

/**
 * 修改mac地址
 * @author jiangwei
 *
 */
public class MacAddressFragment extends YzzsBaseFragment implements EventHandler,ConnectTimeoutListener,OnClickListener{
	private final static String TAG = MacAddressFragment.class.getSimpleName();
	private Button changeMacBtn,readMacBtn;
	private EditText macEt1,macEt2,macEt3,macEt4,macEt5,macEt6;
	private int whichDev = -1;
	private int whichDevReadCMD = -1;
	private int whichDevChangeCMD = -1;
	private boolean isTimeOut = true;

	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_mac_address_fragment, null);
		initView(view);
		initData();
		return view;
	}
	private void initData() {
		whichDev = getArguments().getInt("whichDev");
		whichDevReadCMD = GeneralCMDConstant.READ_MAC_ADDRESS;
		whichDevChangeCMD = GeneralCMDConstant.CHANGE_MAC_ADDRESS;
		setConnectTimeoutListener(this);
	}

	private void initView(View view) {
		macEt1 = (EditText) view.findViewById(R.id.et_mac_1);
		macEt2 = (EditText) view.findViewById(R.id.et_mac_2);
		macEt3 = (EditText) view.findViewById(R.id.et_mac_3);
		macEt4 = (EditText) view.findViewById(R.id.et_mac_4);
		macEt5 = (EditText) view.findViewById(R.id.et_mac_5);
		macEt6 = (EditText) view.findViewById(R.id.et_mac_6);
		changeMacBtn = (Button) view.findViewById(R.id.btn_change_mac_address);
		readMacBtn = (Button) view.findViewById(R.id.btn_read_mac_address);
		changeMacBtn.setOnClickListener(this);
		readMacBtn.setOnClickListener(this);
	}

	@Override
	public void onPause() {
		MessageReceiver.ehList.remove(this);
		super.onPause();
	}

	@Override
	public void onResume() {
		MessageReceiver.ehList.add(this);
		super.onResume();
	}

	@Override
	public void onConnected(boolean isConnected) {
		if (!isConnected) {
			ToastMsg(getActivity(), getString(R.string.disconnected));
		}
	}

	@Override
	public void onReady(boolean isReady) {
	}

	@Override
	public void onNetChange(boolean isNetConnected) {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_change_mac_address:
			if(YzzsApplication.isConnected) {
				if (!checkMacData()) {
					return;
				}
				showLoading("正在保存...");
				sendXgCmd(whichDevChangeCMD);
				startTime(HkCMDConstant.SEND_HK_SAVE_CMD_TIMEOUT, "保存数据");
				isTimeOut = true;
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		case R.id.btn_read_mac_address:
			if(YzzsApplication.isConnected) {
				showLoading("正在读取...");
				sendDqCmd(whichDevReadCMD);
				startTime(HkCMDConstant.SEND_HK_SAVE_CMD_TIMEOUT, "读取数据");
				isTimeOut = true;
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		}
		super.onClick(v);
	}

	@Override
	public void onMessage(byte[] message) {
		if (message[0] == whichDev) {//是不是环控的命令
			int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
			if (cmd == whichDevReadCMD) {
				getMacDddress(message);
			}
			if (cmd == whichDevChangeCMD) {
				getChangeMacAddress(message);
			}
		}
	}

	/**
	 * 读取ip/网关名称
	 */
	private void sendDqCmd(int cmd) {
		sendGetBaseCMD(whichDev, cmd);
	}

	/**
	 * 得到修改IP的返回值
	 */
	private void getChangeMacAddress(byte[] message) {
		if (message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if (message[6] == 0) {
				ToastMsg(getActivity(), "MAC地址修改成功");
			} else {
				ToastMsg(getActivity(), "MAC地址修改失败");
			}
		} else {
			ToastMsg(getActivity(), "数据出错");
		}
	}

	/**
	 * 读取的K地址
	 * @param message 内容
	 */
	private void getMacDddress(byte[] message) {
		if (message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if (message[6] == 0) {
				if (message[7] == 12) {
					macEt1.setText(AsciiUtil.asciiToString(YzzsCommonUtil.ChangeByteToPositiveNumber(message[8]) + "") +
							AsciiUtil.asciiToString(YzzsCommonUtil.ChangeByteToPositiveNumber(message[9]) + ""));
					macEt2.setText(AsciiUtil.asciiToString(YzzsCommonUtil.ChangeByteToPositiveNumber(message[10]) + "") +
							AsciiUtil.asciiToString(YzzsCommonUtil.ChangeByteToPositiveNumber(message[11]) + ""));
					macEt3.setText(AsciiUtil.asciiToString(YzzsCommonUtil.ChangeByteToPositiveNumber(message[12]) + "") +
							AsciiUtil.asciiToString(YzzsCommonUtil.ChangeByteToPositiveNumber(message[13]) + ""));
					macEt4.setText(AsciiUtil.asciiToString(YzzsCommonUtil.ChangeByteToPositiveNumber(message[14]) + "") +
							AsciiUtil.asciiToString(YzzsCommonUtil.ChangeByteToPositiveNumber(message[15]) + ""));
					macEt5.setText(AsciiUtil.asciiToString(YzzsCommonUtil.ChangeByteToPositiveNumber(message[16]) + "") +
							AsciiUtil.asciiToString(YzzsCommonUtil.ChangeByteToPositiveNumber(message[17]) + ""));
					macEt6.setText(AsciiUtil.asciiToString(YzzsCommonUtil.ChangeByteToPositiveNumber(message[18]) + "") +
							AsciiUtil.asciiToString(YzzsCommonUtil.ChangeByteToPositiveNumber(message[19]) + ""));
					ToastMsg(getActivity(), "MAC地址读取成功");
				} else {
					ToastMsg(getContext(),"数据错误");
				}
			} else {
				ToastMsg(getActivity(), "MAC地址读取失败");
			}
		} else {
			ToastMsg(getActivity(), "数据读取失败");
		}
	}

	private boolean checkMacData() {
		if (CommonUtil.isEmpty(macEt1.getText().toString()) || CommonUtil.isEmpty(macEt2.getText().toString()) ||
				CommonUtil.isEmpty(macEt3.getText().toString()) || CommonUtil.isEmpty(macEt4.getText().toString()) ||
				CommonUtil.isEmpty(macEt5.getText().toString()) || CommonUtil.isEmpty(macEt6.getText().toString())) {
			ToastMsg(getActivity(), "输入MAC地址段不能为空");
			return false;
		}
		return true;
	}
	/**
	 * 修改Mac
	 */
	private void sendXgCmd(int cmd) {
		String Et1;
		String Et2;
		String Et3;
		String Et4;
		String Et5;
		String Et6;
		Et1 = macEt1.getText().toString().trim();
		Et2 = macEt2.getText().toString().trim();
		Et3 = macEt3.getText().toString().trim();
		Et4 = macEt4.getText().toString().trim();
		Et5 = macEt5.getText().toString().trim();
		Et6 = macEt6.getText().toString().trim();
		StringBuilder sb = new StringBuilder();
		sb.append(Et1).append(Et2).append(Et3).append(Et4).append(Et5).append(Et6);
		int sbLength = sb.toString().length();
		int dataLength = 12 + sbLength;
		byte [] byteSendData = new byte[dataLength];
		byteSendData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH); // 协议头 “h”
		byteSendData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM); // 协议头 “m”
		byteSendData[2] = YzzsCommonUtil.intTobyte(whichDev);//什么设备
		byteSendData[3] = YzzsCommonUtil.intTobyte(cmd / 256); // 数据域：命令高位
		byteSendData[4] = YzzsCommonUtil.intTobyte(cmd % 256); // 数据域：命令低位
		byteSendData[5] = YzzsCommonUtil.intTobyte(dataLength / 256); // 数据域：数据总长度高位
		byteSendData[6] = YzzsCommonUtil.intTobyte(dataLength % 256); // 数据域：数据总长度低位
		byteSendData[7] = YzzsCommonUtil.intTobyte(sbLength);
		for (int i = 0; i < sbLength; i++) {
			byteSendData [8 + i] = YzzsCommonUtil.intTobyte(Integer.valueOf(AsciiUtil.stringToAscii(sb.substring(i,i +1))));
		}
		byteSendData[dataLength - 4] = YzzsCommonUtil.intTobyte(0); // 校验位：暂时不用
		byteSendData[dataLength - 3] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE); // 协议尾：“e”
		byteSendData[dataLength - 2] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN); // 协议尾：“n”
		byteSendData[dataLength - 1] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD); // 协议尾：“d”
		sendUnpackData(byteSendData,XtAppConstant.SEND_DELAY_PACK_INTERVAL);
		Logger.e(TAG, Arrays.toString(byteSendData));
	}
	
	
	@Override
	public void Timeout(String content) {
		if(!isAdded()) {
			return;
		}
		if (!isTimeOut) {
			// 5 秒钟有收到数据，则停止计时
			isTimeOut = false; // 标志位重新置false
		} else {
			dismissLoading();
			ToastMsg(getActivity(), content + getString(R.string.connect_time_out));
		}
	}
}