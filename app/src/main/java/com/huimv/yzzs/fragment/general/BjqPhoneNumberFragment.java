package com.huimv.yzzs.fragment.general;

import java.util.HashMap;
import java.util.Map;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.android.basic.util.PhoneUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.GeneralCMDConstant;
import com.huimv.yzzs.constant.HkCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.YzzsCommonUtil;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * 报警时间间隔
 * 
 * @author jw
 *
 */
public class BjqPhoneNumberFragment extends YzzsBaseFragment implements OnClickListener,
					EventHandler,ConnectTimeoutListener{
	/**
	 * 保存按钮
	 */
	private Button btn_save;
	/**
	 * 读取按钮
	 */
	private Button btn_read;
	/**
	 * 输入框
	 */
	private EditText et_phone_number_confim_value1;
	private EditText et_phone_number_confim_value2;
	private EditText et_phone_number_confim_value3;
	private EditText et_phone_number_confim_value4;
	private int whichDev = -1;
	// 固定位 --- 数据头长度 = 协议头 + 数据域头固定长度
	private static final int GDW_LEN_HEAD = 8;
	// 固定位 --- 数据尾长度 = 校验位 + 协议尾
	private static final int GDW_LEN_TAIL = 4;
	private int whichDevSaveCMD = -1;
	private int whichDevReadCMD = -1;
	private boolean isTimeOut = true;
	@SuppressLint("UseSparseArrays")
	private final Map<Integer, EditText> etMap = new HashMap<>();
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_bj_phonenumber_fragment, rootView,false);
		initView(view);
		initData();
		return view;
	}

	private void initData() {
		setConnectTimeoutListener(this);
		whichDev = getArguments().getInt("whichDev");
		whichDevSaveCMD = GeneralCMDConstant.SAVE_BJQ_PHONE_NUMBER;
		whichDevReadCMD = GeneralCMDConstant.READ_BJQ_PHONE_NUMBER;
		btn_save.setOnClickListener(this);
		btn_read.setOnClickListener(this);
		etMap.put(0, et_phone_number_confim_value1);
		etMap.put(1, et_phone_number_confim_value2);
		etMap.put(2, et_phone_number_confim_value3);
		etMap.put(3, et_phone_number_confim_value4);
	}

	private void initView(View view) {
		btn_save = (Button) view.findViewById(R.id.btn_save);
		btn_read = (Button) view.findViewById(R.id.btn_read);
		et_phone_number_confim_value1 = (EditText) view.findViewById(R.id.et_phone_number_confim_value1);
		et_phone_number_confim_value2 = (EditText) view.findViewById(R.id.et_phone_number_confim_value2);
		et_phone_number_confim_value3 = (EditText) view.findViewById(R.id.et_phone_number_confim_value3);
		et_phone_number_confim_value4 = (EditText) view.findViewById(R.id.et_phone_number_confim_value4);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_save:
			if(YzzsApplication.isConnected) {
				if (checkData(etMap.get(0)) && checkData(etMap.get(1)) && 
						checkData(etMap.get(2)) && checkData(etMap.get(3))) {
					sendXgPhoneNumberCmd();
					startTime(HkCMDConstant.SEND_HK_SAVE_CMD_TIMEOUT, "号码保存");
					isTimeOut = true;
				}
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		case R.id.btn_read:
			if(YzzsApplication.isConnected) {
				showLoading("正在读取...");
				sendGetBaseCMD(whichDev, whichDevReadCMD);
				startTime(HkCMDConstant.SEND_HK_SAVE_CMD_TIMEOUT, "读取号码");
				isTimeOut = true;
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		}
		super.onClick(v);
	}
	
	/**
	 * 检查数据是否合理
	 * @return isOK
	 */
	private boolean checkData(EditText et_phone_number_confim_value) {
		boolean isOK;
		String inputData = et_phone_number_confim_value.getText().toString().trim();
		if (CommonUtil.isEmpty(inputData)) {
			isOK = true;
		} else {
			if (PhoneUtil.isMobileNO(inputData)) {
				isOK = true;
			} else {
				et_phone_number_confim_value.setTextColor(ContextCompat.getColor(getActivity(),R.color.red));
				isOK = false;
				ToastMsg(getActivity(), "请输入正确的手机号码");
			}
		}
		return isOK;
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
	public void onMessage(byte[] message) {
		try {
			receiveMessage(message);
		} catch (Exception e) {
			ToastMsg(getActivity(), "数据出错");
		}
	}

	private void receiveMessage(byte[] message) {
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		if (cmd == whichDevSaveCMD) {//保存结果
			getSaveResult(message);
		}
		if (cmd == whichDevReadCMD) {//读取结果
			getReadResult(message);
		}
	}
	/**
	 * 读取结果
	 * @param message
	 */
	private void getReadResult(byte[] message) {
		if (message [5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if(message[6] == 0) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < message[7]; i++) {
					sb.append(message [8 + i] );
				}
				for (int i = 0; i < etMap.size(); i++) {
					etMap.get(i).setText("");
				}
				for (int i = 0; i < sb.toString().length() / 11; i++) {
					etMap.get(i).setText(sb.toString().substring(11 *i,11 *(i +1)));
				}
				ToastMsg(getActivity(), "手机号码读取成功");
			} else {
				ToastMsg(getActivity(), "手机号码读取失败");
			}
		}
	}
	
	/**
	 * 保存结果
	 * @param message
	 */
	private void getSaveResult(byte[] message) {
		if(message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if(message[6] == 0) {
				ToastMsg(getActivity(), "保存成功");
			} else {
				ToastMsg(getActivity(), "保存失败");
			}
		}
	}

	/**
	 * 修改确认时间蓝牙名称
	 */
	private void sendXgPhoneNumberCmd() {
		String inputData1 = et_phone_number_confim_value1.getText().toString().trim();
		String inputData2 = et_phone_number_confim_value2.getText().toString().trim();
		String inputData3 = et_phone_number_confim_value3.getText().toString().trim();
		String inputData4 = et_phone_number_confim_value4.getText().toString().trim();
		StringBuilder phoneNumber = new StringBuilder();
		phoneNumber.append(inputData1).append(inputData2).append(inputData3).append(inputData4);
		int singleDataLength = phoneNumber.length(); // 单条有效数据的长度
		// byte 数据的总长度
		int dataLength = singleDataLength + GDW_LEN_HEAD + GDW_LEN_TAIL;
		// 循环体 + 前固定位 的长度（用于填充数据使用）
		byte[] byteSendData = new byte[dataLength]; // 构造 byte 数组
		int len = singleDataLength + GDW_LEN_HEAD;
		showLoading("正在保存手机号码...");
		byteSendData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH); // 协议头 “h”
		byteSendData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM); // 协议头 “m”
		byteSendData[2] = YzzsCommonUtil.intTobyte(whichDev); // 数据域：类型
		byteSendData[3] = YzzsCommonUtil.intTobyte(whichDevSaveCMD / 256); // 数据域：命令高位
		byteSendData[4] = YzzsCommonUtil.intTobyte(whichDevSaveCMD % 256); // 数据域：命令低位
		byteSendData[5] = YzzsCommonUtil.intTobyte(dataLength / 256); // 数据域：数据总长度高位
		byteSendData[6] = YzzsCommonUtil.intTobyte(dataLength % 256); // 数据域：数据总长度低位
		byteSendData[7] = YzzsCommonUtil.intTobyte(singleDataLength); // 数据域：单条有效数据长度
		for (int i = GDW_LEN_HEAD; i < len; i++) {
			int data = Integer.valueOf((phoneNumber.substring(i - GDW_LEN_HEAD, i - GDW_LEN_HEAD + 1)));
			byteSendData[i] = YzzsCommonUtil.intTobyte(data);
		}
		byteSendData[dataLength - 4] = YzzsCommonUtil.intTobyte(0); // 校验位：暂时不用
		byteSendData[dataLength - 3] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE); // 协议尾：“e”
		byteSendData[dataLength - 2] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN); // 协议尾：“n”
		byteSendData[dataLength - 1] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD); // 协议尾：“d”
		sendUnpackData(byteSendData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}
	
	@Override
	public void onNetChange(boolean isNetConnected) {
		
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