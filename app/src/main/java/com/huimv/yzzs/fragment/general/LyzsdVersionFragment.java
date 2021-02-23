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
import com.huimv.yzzs.util.YzzsCommonUtil;

/**
 * 蓝牙指示灯版本
 * 
 * @author jw
 *
 */
public class LyzsdVersionFragment extends YzzsBaseFragment implements OnClickListener,
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
	private EditText et_version_confim_value;
	private int whichDev = -1;
	// 固定位 --- 数据头长度 = 协议头 + 数据域头固定长度
	private static final int GDW_LEN_HEAD = 8;
	// 固定位 --- 数据尾长度 = 校验位 + 协议尾
	private static final int GDW_LEN_TAIL = 4;
	private int whichDevSaveCMD = -1;
	private int whichDevReadCMD = -1;
	private boolean isTimeOut = true;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_lyzsd_version_fragment, null);
		initView(view);
		initData();
		return view;
	}

	private void initData() {
		setConnectTimeoutListener(this);
		whichDev = getArguments().getInt("whichDev");
		whichDevSaveCMD = GeneralCMDConstant.SAVE_LYZSD_VERISON;
		whichDevReadCMD = GeneralCMDConstant.READ_LYZSD_VERISON;
		btn_save.setOnClickListener(this);
		btn_read.setOnClickListener(this);
	}

	private void initView(View view) {
		btn_save = (Button) view.findViewById(R.id.btn_save);
		btn_read = (Button) view.findViewById(R.id.btn_read);
		et_version_confim_value = (EditText) view.findViewById(R.id.et_version_confim_value);
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
				if (!checkData()) {
					return;
				} 
				sendXgLyzsdVersionCmd();
				startTime(HkCMDConstant.SEND_HK_SAVE_CMD_TIMEOUT, "版本保存");
				isTimeOut = true;
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		case R.id.btn_read:
			if(YzzsApplication.isConnected) {
				showLoading("正在读取...");
				sendGetBaseCMD(whichDev, whichDevReadCMD);
				startTime(HkCMDConstant.SEND_HK_SAVE_CMD_TIMEOUT, "读取版本");
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
	private boolean checkData() {
		boolean isOK;
		String inputData = et_version_confim_value.getText().toString().trim();
		if (CommonUtil.isEmpty(inputData) ) {
			ToastMsg(getActivity(), "蓝牙指示灯版本不能为空");
			isOK = false;
		} else {
			isOK = true;
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
				et_version_confim_value.setText(Integer.valueOf(sb.toString()) + "");
			} else {
				ToastMsg(getActivity(), "版本读取失败");
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
	 * 修改蓝牙指示灯版本
	 */
	private void sendXgLyzsdVersionCmd() {
		String versionStr = et_version_confim_value.getText().toString().trim(); // 输入确认时间
		versionStr = YzzsCommonUtil.formatStringAdd0(versionStr, 1, 1);
		int singleDataLength = versionStr.length(); // 单条有效数据的长度
		// byte 数据的总长度
		int dataLength = singleDataLength + GDW_LEN_HEAD + GDW_LEN_TAIL;
		// 循环体 + 前固定位 的长度（用于填充数据使用）
		byte[] byteSendData = new byte[dataLength]; // 构造 byte 数组
		int len = singleDataLength + GDW_LEN_HEAD;
		showLoading("正在保存版本...");
		byteSendData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH); // 协议头 “h”
		byteSendData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM); // 协议头 “m”
		byteSendData[2] = YzzsCommonUtil.intTobyte(whichDev); // 数据域：类型
		byteSendData[3] = YzzsCommonUtil.intTobyte(whichDevSaveCMD / 256); // 数据域：命令高位
		byteSendData[4] = YzzsCommonUtil.intTobyte(whichDevSaveCMD % 256); // 数据域：命令低位
		byteSendData[5] = YzzsCommonUtil.intTobyte(dataLength / 256); // 数据域：数据总长度高位
		byteSendData[6] = YzzsCommonUtil.intTobyte(dataLength % 256); // 数据域：数据总长度低位
		byteSendData[7] = YzzsCommonUtil.intTobyte(singleDataLength); // 数据域：单条有效数据长度
		for (int i = GDW_LEN_HEAD; i < len; i++) {
			int data = Integer.valueOf((versionStr.substring(i - GDW_LEN_HEAD, i - GDW_LEN_HEAD + 1)));
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