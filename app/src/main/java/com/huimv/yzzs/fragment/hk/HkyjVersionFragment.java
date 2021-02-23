package com.huimv.yzzs.fragment.hk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.huimv.yzzs.R;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.HkCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.huimv.yzzs.util.wheel.ItemSelectWheelUtil;

/**
 * 环控硬件版本
 * 
 * @author jw
 *
 */
public class HkyjVersionFragment extends YzzsBaseFragment implements OnClickListener,
					EventHandler,ConnectTimeoutListener,ItemSelectWheelUtil.OnConfirmClickListener {
	/**
	 * 保存按钮
	 */
	private Button btn_save;
	/**
	 * 读取按钮
	 */
	private Button btn_read;
	private TextView tv_version_confim_value;
	private int whichDev = -1;
	// 固定位 --- 数据头长度 = 协议头 + 数据域头固定长度
	private static final int GDW_LEN_HEAD = 8;
	// 固定位 --- 数据尾长度 = 校验位 + 协议尾
	private static final int GDW_LEN_TAIL = 4;
	private int whichDevSaveCMD = -1;
	private int whichDevReadCMD = -1;
	private boolean isTimeOut = true;
	private ItemSelectWheelUtil itemSelectWheelUtil;
	private int positon = 0;
	private String [] items ;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_hk_yj_version_fragment, null);
		initView(view);
		initData();
		return view;
	}

	private void initData() {
		setConnectTimeoutListener(this);
		whichDev = getArguments().getInt("whichDev");
		whichDevSaveCMD = HkCMDConstant.SAVE_YJ_VERSION;
		whichDevReadCMD = HkCMDConstant.READ_YJ_VERSION;
		btn_save.setOnClickListener(this);
		btn_read.setOnClickListener(this);
		tv_version_confim_value.setOnClickListener(this);
		items = getResources().getStringArray(R.array.hk_yj_version_item_array);
		itemSelectWheelUtil = new ItemSelectWheelUtil(getActivity(),"请选择硬件版本",items,this);
	}

	private void initView(View view) {
		btn_save = (Button) view.findViewById(R.id.btn_save);
		btn_read = (Button) view.findViewById(R.id.btn_read);
		tv_version_confim_value = (TextView) view.findViewById(R.id.tv_version_confim_value);
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
				sendXgHkYjVersionCmd();
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
		case R.id.tv_version_confim_value:
			itemSelectWheelUtil.showDialog(0,"请选择硬件版本");
			break;
		}
		super.onClick(v);
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
				tv_version_confim_value.setText(items[message[8]]);
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
	 * 硬件版本版本
	 */
	private void sendXgHkYjVersionCmd() {
		int singleDataLength = 1; // 单条有效数据的长度
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
		byteSendData[8] = YzzsCommonUtil.intTobyte(positon);
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

	@Override
	public void onConfirm(int position, int i) {
		this.positon = position;
		if (items != null && items.length > 0) {
			tv_version_confim_value.setText(items [position]);
		}
	}
}