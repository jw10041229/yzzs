package com.huimv.yzzs.fragment.cdz;

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
import com.huimv.yzzs.constant.CdzCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.orhanobut.logger.Logger;

import java.util.Arrays;

/**
 * 料槽时间设置
 * @author jiangwei
 */
public class CdzLcsjFragment extends YzzsBaseFragment implements OnClickListener,EventHandler,ConnectTimeoutListener{
	private final static String TAG = CdzLcsjFragment.class.getSimpleName();
	private SharePreferenceUtil mSpUtil;
	private boolean isTimeOut = true;
	/**
	 * 读取正转时间
	 */
	private Button btn_read_roll_runtime;
	/**
	 * 保存正转时间
	 */
	private Button btn_save_roll_runtime;
	/**
	 * 读取反转时间
	 */
	private Button btn_read_rollback_runtime;
	/**
	 * 保存反转时间
	 */
	private Button btn_save_rollback_runtime;
	/**
	 * 输入正转时间
	 */
	private EditText et_roll_runtime;
	/**
	 * 输入反转时间
	 */
	private EditText et_rollback_runtime;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_cdz_lc_runtime_fragment, rootView,false);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		initView(view);
		initData();
		return view;
	}
	
	private void initData() {
		setConnectTimeoutListener(this);
		btn_read_roll_runtime.setOnClickListener(this);
		btn_save_roll_runtime.setOnClickListener(this);
		btn_read_rollback_runtime.setOnClickListener(this);
		btn_save_rollback_runtime.setOnClickListener(this);
	}

	private void initView(View view) {
		btn_read_roll_runtime = (Button) view.findViewById(R.id.btn_read_roll_runtime);
		btn_save_roll_runtime = (Button) view.findViewById(R.id.btn_save_roll_runtime);

		btn_read_rollback_runtime = (Button) view.findViewById(R.id.btn_read_rollback_runtime);
		btn_save_rollback_runtime = (Button) view.findViewById(R.id.btn_save_rollback_runtime);

		et_roll_runtime = (EditText) view.findViewById(R.id.et_roll_runtime);
		et_rollback_runtime = (EditText) view.findViewById(R.id.et_rollback_runtime);
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
	public void onClick(View v) {
		if(!YzzsApplication.isConnected) {
			ToastMsg(getActivity(), getString(R.string.disconnected));
			return;
		}
		switch (v.getId()) {
		case R.id.btn_read_roll_runtime:
			showLoading("正在读取...");
			isTimeOut = true;
			startTime(3000, "通信");
			sendGetBaseCMD(CdzCMDConstant.CDZ_SBBZ,CdzCMDConstant.CDZ_READ_ROLL_TIME);
			break;
		case R.id.btn_save_roll_runtime:
			String dataRoll  = et_roll_runtime.getText().toString();
			if (checkDataIsNull(dataRoll)) {
				ToastMsg(getActivity(),"输入时间不能为空");
				return;
			}
			isTimeOut = true;
			startTime(3000, "通信");
			showLoading("正在保存...");
			sendSaveRunTimeCMD(CdzCMDConstant.CDZ_SAVE_ROLL_TIME,YzzsCommonUtil.formatStringAdd0(dataRoll,3,1));
			break;
		case R.id.btn_read_rollback_runtime:
			isTimeOut = true;
			startTime(3000, "通信");
			showLoading("正在读取...");
			sendGetBaseCMD(CdzCMDConstant.CDZ_SBBZ,CdzCMDConstant.CDZ_READ_ROLLBACK_TIME);
			break;
		case R.id.btn_save_rollback_runtime:
			String dataRollback  = et_rollback_runtime.getText().toString();
			if (checkDataIsNull(dataRollback)) {
				ToastMsg(getActivity(),"输入时间不能为空");
				return;
			}
			isTimeOut = true;
			startTime(3000, "通信");
			showLoading("正在保存...");
			sendSaveRunTimeCMD(CdzCMDConstant.CDZ_SAVE_ROLLBACK_TIME,YzzsCommonUtil.formatStringAdd0(dataRollback,3,1));
			break;
		default:
			break;
		}
		super.onClick(v);
	}

	private boolean checkDataIsNull(String data) {
		boolean isNull = false;
		if (CommonUtil.isEmpty(data)) {
			isNull = true;
		}
		return isNull;
	}

	private void sendSaveRunTimeCMD(int cmd,String data) {
		int lenth = data.length() + 12;
		byte [] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);//h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);//m
		byteData[2] = YzzsCommonUtil.intTobyte(CdzCMDConstant.CDZ_SBBZ);//设备区分标志
		byteData[3] = YzzsCommonUtil.intTobyte(cmd / 256);//命令高位
		byteData[4] = YzzsCommonUtil.intTobyte(cmd % 256);//命令低位
		byteData[5] = YzzsCommonUtil.intTobyte(lenth / 256);//长度低位
		byteData[6] = YzzsCommonUtil.intTobyte(lenth % 256);//长度高位
		byteData[7] = YzzsCommonUtil.intTobyte(data.length());//后面值得长度
		for (int i = 8; i < 8 + byteData[7]; i++) {
			byteData[i] = YzzsCommonUtil
					.intTobyte(Integer.parseInt(data.substring(i - 8, i - 7)));
		}
		byteData[11] = 1;//校验位
		byteData[12] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);//e
		byteData[13] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//n
		byteData[14] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
		sendUnpackData(byteData,XtAppConstant.SEND_DELAY_PACK_INTERVAL);
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
	
	/**
	 * 接收包
	 * @param message
	 */
	private void receivePack(byte[] message) {
		if (message.length <= 6) {
			return;
		}
		if(message[0] == CdzCMDConstant.CDZ_SBBZ) {//测定站
			try {
				int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
				switch (cmd) {
				case CdzCMDConstant.CDZ_READ_ROLL_TIME://读取正转时间
					readDataParsing(message);
					break;
				case CdzCMDConstant.CDZ_READ_ROLLBACK_TIME://读取正转时间
					readDataParsing(message);
					break;
				case CdzCMDConstant.CDZ_SAVE_ROLL_TIME:
					saveDataParsing(message);
					break;
				case CdzCMDConstant.CDZ_SAVE_ROLLBACK_TIME:
					saveDataParsing(message);
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
	 * 保存
	 * @param message
	 */
	private void saveDataParsing(byte[] message) {
		Logger.d("保存",Arrays.toString(message));
		if (message[5] == 0 && message[6] == 0) {// 保存
			isTimeOut = false;
			dismissLoading();
			ToastMsg(getActivity(), "保存成功!");
		} else { // 保存
			dismissLoading();
			ToastMsg(getActivity(), "保存失败,请重试!");
		}
	}
	/**
	 * 读取数据
	 * @param message
	 */
	private void readDataParsing(byte[] message) {
		if (message [5] !=0) {//数据接收成功
			dismissLoading();
			ToastMsg(getActivity(), "数据接收异常");
			isTimeOut = false;
			return;
		}
		if (message [6] == 0 ) {
			dismissLoading();
			Logger.d("读取", Arrays.toString(message));
			isTimeOut = false;
			int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < message[7]; i++) {
				sb.append(message[8 + i]);
			}
			if (cmd == CdzCMDConstant.CDZ_READ_ROLL_TIME) {
				et_roll_runtime.setText(Integer.valueOf(sb.toString()) + "");
			}
			if (cmd == CdzCMDConstant.CDZ_READ_ROLLBACK_TIME) {
				et_rollback_runtime.setText(Integer.valueOf(sb.toString()) + "");
			}
		}
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
