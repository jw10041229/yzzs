package com.huimv.yzzs.fragment.general;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.BluetoothScanActivity;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.constant.GeneralCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.AsciiUtil;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;

/**
 * 蓝牙名称修改
 * 
 * @author jiangwei
 *
 */
public class LymcFragment extends YzzsBaseFragment implements EventHandler {
	private Button btn_changeLymc;
	private Button btn_readLymc;
	private EditText et_lymc;
	private SharePreferenceUtil mSpUtil;
	private ImageView iv_back;

	// 固定位 --- 数据头长度 = 协议头 + 数据域头固定长度
	private static final int GDW_LEN_HEAD = 8;
	// 固定位 --- 数据尾长度 = 校验位 + 协议尾
	private static final int GDW_LEN_TAIL = 4;

	private int whichDev = -1;
	private int whichDevChangeCMD = -1;
	
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_lymc_fragment, null);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		initView(view);
		initData();
		return view;
	}
	
	private void initData() {
		whichDev = getArguments().getInt("whichDev");
		whichDevChangeCMD = GeneralCMDConstant.CHANGE_LYMC;
	}
	
	private void initView(View view) {
		iv_back = (ImageView) view.findViewById(R.id.iv_back);
		btn_readLymc = (Button) view.findViewById(R.id.readLymcBtn);
		et_lymc = (EditText) view.findViewById(R.id.lymcEt);
		//et_lymc.setInputType(InputType.TYPE_CLASS_NUMBER); // 输入类型
		//et_lymc.setInputType(InputType.TYPE_CLASS_TEXT); // 输入类型
		et_lymc.setFilters(new InputFilter[] { new InputFilter.LengthFilter(11) }); // 最大输入长度
		et_lymc.setText(mSpUtil.getLymc());
		btn_changeLymc = (Button) view.findViewById(R.id.changeLymcBtn);
		iv_back.setOnClickListener(this);
		btn_readLymc.setOnClickListener(this);
		btn_changeLymc.setOnClickListener(this);
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
		case R.id.iv_back:
			getFragmentManager().popBackStack();
			break;
		case R.id.readLymcBtn:
			break;
		case R.id.changeLymcBtn:
			sendXgLymcCmd();
			break;
		}
		super.onClick(v);
	}

	@Override
	public void onMessage(byte[] message) {
		getXgLymcResult(message);
	}

	/**
	 * 修改蓝牙名称
	 */
	private void sendXgLymcCmd() {
		if (CommonUtil.isEmpty(et_lymc.getText().toString())) {
			ToastMsg(getActivity(), "输入蓝牙名称不能空");
			return;
		}

		String lymcStr = et_lymc.getText().toString().trim(); // 蓝牙名称
		int singleDataLength = lymcStr.length(); // 单条有效数据的长度
		// byte 数据的总长度
		int dataLength = singleDataLength + GDW_LEN_HEAD + GDW_LEN_TAIL;
		// 循环体 + 前固定位 的长度（用于填充数据使用）
		byte[] byteSendData = new byte[dataLength]; // 构造 byte 数组
		int len = singleDataLength + GDW_LEN_HEAD;
		showLoading("正在修改蓝牙名称...");
		byteSendData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH); // 协议头 “h”
		byteSendData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM); // 协议头 “m”
		byteSendData[2] = YzzsCommonUtil.intTobyte(whichDev); // 数据域：类型
		byteSendData[3] = YzzsCommonUtil.intTobyte(whichDevChangeCMD / 256); // 数据域：命令高位
		byteSendData[4] = YzzsCommonUtil.intTobyte(whichDevChangeCMD % 256); // 数据域：命令低位
		byteSendData[5] = YzzsCommonUtil.intTobyte(dataLength / 256); // 数据域：数据总长度高位
		byteSendData[6] = YzzsCommonUtil.intTobyte(dataLength % 256); // 数据域：数据总长度低位
		byteSendData[7] = YzzsCommonUtil.intTobyte(singleDataLength); // 数据域：单条有效数据长度
		for (int i = GDW_LEN_HEAD; i < len; i++) {
			int data = Integer.valueOf(AsciiUtil.stringToAscii(lymcStr.substring(i - GDW_LEN_HEAD, i - GDW_LEN_HEAD + 1)));
			byteSendData[i] = YzzsCommonUtil.intTobyte(data);
		}
		byteSendData[dataLength - 4] = YzzsCommonUtil.intTobyte(0); // 校验位：暂时不用
		byteSendData[dataLength - 3] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE); // 协议尾：“e”
		byteSendData[dataLength - 2] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN); // 协议尾：“n”
		byteSendData[dataLength - 1] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD); // 协议尾：“d”
		sendUnpackData(byteSendData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}

	/**
	 * 保存传感器绑定数据
	 * 
	 * @param backData
	 */
	private void getXgLymcResult(byte[] backData) {
		if (backData[0] == whichDev) {// 环控
			try {
				int cmd = backData[1] * 256 + backData[2]; // 命令类型
				// 新的蓝牙名称保存
				if (cmd == whichDevChangeCMD) {
					dismissLoading();
					if (backData[6] == 0) {
						ToastMsg(getActivity(), "新的蓝牙名称保存成功!");
						final Intent intent = new Intent(getActivity(), BluetoothScanActivity.class);
						startActivity(intent);
						getActivity().finish();
					} else {
						ToastMsg(getActivity(), "新的蓝牙名称保存失败,请重试!");
					}
				}
			} catch (Exception e) {
				dismissLoading();
				ToastMsg(getActivity(), "lymcFragment 修改蓝牙名称数据异常");
			}
		}
	}
}