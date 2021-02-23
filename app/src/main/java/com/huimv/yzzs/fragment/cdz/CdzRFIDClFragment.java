package com.huimv.yzzs.fragment.cdz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.adapter.FlzRFIDFlAdapter;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.FlzCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.model.FlzRFIDFlData;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.YzzsCommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * RFID出栏 
 * @author jiangwei
 *
 */
public class CdzRFIDClFragment extends YzzsBaseFragment implements EventHandler,ConnectTimeoutListener,
					OnClickListener{
	//private final static String TAG = FlzRFIDClFragment.class.getSimpleName();
	/**
	 * 保存
	 */
	private TextView tv_RFID_save;
	/**
	 * 超时
	 */
	private boolean isTimeOut = true;
	/**
	 * listview
	 */
	private ListView listview_RFID_fl;
	private List<FlzRFIDFlData> mFlz_RFID_FlDataList;
	/**
	 * 添加按钮
	 */
	private ImageView btn_add_RFID;
	private FlzRFIDFlAdapter mFlz_RFID_FlAdapter;
	private boolean isOK = true;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_flz_rfid_cl_fragment, rootView,false);
		initView(view);
		initData();
		initListener();
		return view;
	}
	
	private void initListener() {
		setConnectTimeoutListener(this);
		tv_RFID_save.setOnClickListener(this);
		btn_add_RFID.setOnClickListener(this);
	}

	private void initData() {
		mFlz_RFID_FlDataList = new ArrayList<>();
		mFlz_RFID_FlAdapter = new FlzRFIDFlAdapter(getActivity(), mFlz_RFID_FlDataList);
		listview_RFID_fl.setAdapter(mFlz_RFID_FlAdapter);
	}

	private void initView(View view) {
		tv_RFID_save = (TextView) view.findViewById(R.id.tv_RFID_save);
		listview_RFID_fl = (ListView) view.findViewById(R.id.listview_RFID_fl);
		btn_add_RFID = (ImageView) view.findViewById(R.id.btn_add_RFID);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_RFID_save:
			isOK = true;
			int dataSize = removeNullRFIDFromList();
			if (dataSize == 0) {
				ToastMsg(getActivity(), "RFID必须为15位");
				return;
			}
			if (!isOK) {
				return;
			}
			if(YzzsApplication.isConnected) {
				isTimeOut = true;
				sendRIFDChannelCMD(dataSize);
				startTime(5000, "保存");
			} else {
				ToastMsg(getActivity(), getString(R.string.bluetooth_disconnected));
			}
			break;
		case R.id.btn_add_RFID:
			if(mFlz_RFID_FlDataList.size() == 20) {
				ToastMsg(getActivity(), "数据不能超过20组");
				return;
			}
			mFlz_RFID_FlAdapter.addRFID();
			break;
		default:
			break;
		}
		super.onClick(v);
	}
	/**
	 * 去除RFID为空的数据
	 */
	private int removeNullRFIDFromList () {
		for (int i = 0; i < mFlz_RFID_FlDataList.size(); i++) {
			if (CommonUtil.isEmpty(mFlz_RFID_FlDataList.get(i).getRFID())) {
				mFlz_RFID_FlDataList.remove(i);
				i = i == 0 ? 0 : -- i; 
			}
		}
		for (int i = 0; i < mFlz_RFID_FlDataList.size(); i++) { 
			if (mFlz_RFID_FlDataList.get(i).getRFID().length() != 15) {
				ToastMsg(getActivity(), "RFID必须为15位");
				dismissLoading();
				isOK = false;
				break;
			}
		}
		mFlz_RFID_FlAdapter.notifyDataSetChanged();
		return mFlz_RFID_FlDataList.size();
	}
	/**
	 * 修改蓝牙名称
	 */
	private void sendRIFDChannelCMD(int DataSize) {
		int singleDataLength = 16; // 单条有效数据的长度
		// byte 数据的总长度
		int dataLength = singleDataLength * DataSize + 9 + 4;
		// 循环体 + 前固定位 的长度（用于填充数据使用）
		byte[] byteSendData = new byte[dataLength]; // 构造 byte 数组
		showLoading("正在保存数据...");
		byteSendData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH); // 协议头 “h”
		byteSendData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM); // 协议头 “m”
		byteSendData[2] = YzzsCommonUtil.intTobyte(FlzCMDConstant.FLZ_SBBZ); // 数据域：类型
		byteSendData[3] = YzzsCommonUtil.intTobyte(FlzCMDConstant.FLZ_RIFD_FL / 256); // 数据域：命令高位
		byteSendData[4] = YzzsCommonUtil.intTobyte(FlzCMDConstant.FLZ_RIFD_FL % 256); // 数据域：命令低位
		byteSendData[5] = YzzsCommonUtil.intTobyte(dataLength / 256); // 数据域：数据总长度高位
		byteSendData[6] = YzzsCommonUtil.intTobyte(dataLength % 256); // 数据域：数据总长度低位
		byteSendData[7] = YzzsCommonUtil.intTobyte(singleDataLength); // 数据域：单条有效数据长度
		byteSendData[8] = YzzsCommonUtil.intTobyte(DataSize); // 有几条数据
		StringBuilder singleDataSB = new StringBuilder();
		for (int i = 0; i < DataSize; i++) {//把所有的通道加RFID拼装
			singleDataSB.append(mFlz_RFID_FlDataList.get(i).getChannelTag()).
			append(mFlz_RFID_FlDataList.get(i).getRFID());
		}
		for (int i = 0; i < singleDataSB.length(); i++) {
			byteSendData [i + 9] = YzzsCommonUtil
					.intTobyte(Integer.parseInt(singleDataSB.toString().substring( i, i + 1)));
		}
		byteSendData[dataLength - 4] = YzzsCommonUtil.intTobyte(0); // 校验位：暂时不用
		byteSendData[dataLength - 3] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE); // 协议尾：“e”
		byteSendData[dataLength - 2] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN); // 协议尾：“n”
		byteSendData[dataLength - 1] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD); // 协议尾：“d”
		sendUnpackData(byteSendData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
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
		YzzsApplication.isConnected = isConnected;
		if (!isConnected) {
			dismissLoading();
			ToastMsg(getActivity(), getString(R.string.bluetooth_disconnected));
		}
	}
	
	@Override
	public void onReady(boolean isReady) {
	}
	
	@Override
	public void onNetChange(boolean isNetConnected) {
	}

	@Override
	public void onMessage(byte[] message) {
		receivePack(message);
	}

	private void receivePack(byte[] message) {
		if (message.length < 6) {
			return;
		}
		if(message[0] == FlzCMDConstant.FLZ_SBBZ) {//分栏站
			try {
				int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
				switch (cmd) {
				case FlzCMDConstant.FLZ_RIFD_FL://耳标
					RFIDFlDataParsing(message);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				ToastMsg(getActivity(), "数据出错");
				dismissLoading();
			}
		}
	}
	
	/**
	 * RFID数据解析
	 * @param message
	 */
	private void RFIDFlDataParsing(byte[] message) {
		if(message.length < 6) {
			ToastMsg(getActivity(), "数据异常");
		} else {
			if (message[5] == 0) {
				isTimeOut = false;
				dismissLoading();
				if (message[6] == 0) {
					ToastMsg(getActivity(), "保存成功");
				} else {
					ToastMsg(getActivity(), "保存失败");
				}
			} else {
				ToastMsg(getActivity(), "数据传输失败");
			}
		}
	}

	@Override
	public void Timeout(String content) {
		if(!isAdded()) {
			return;
		}
		if (!isTimeOut) {
			isTimeOut = false; // 标志位重置false
		} else {
			dismissLoading();
			ToastMsg(getActivity(), content + getString(R.string.connect_time_out));
		}
	}
}
