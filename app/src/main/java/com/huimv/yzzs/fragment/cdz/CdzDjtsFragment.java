package com.huimv.yzzs.fragment.cdz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

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
 * 电机调试
 * @author jiangwei
 */
public class CdzDjtsFragment extends YzzsBaseFragment implements OnClickListener,EventHandler,ConnectTimeoutListener{
	private final static String TAG = CdzDjtsFragment.class.getSimpleName();
	private SharePreferenceUtil mSpUtil;
	private boolean isTimeOut = true;
	/**
	 * 正转调试
	 */
	private Button btn_debug_motor_roll;
	/**
	 * 反转调试
	 */
	private Button btn_debug_motor_rollback;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_cdz_djts_fragment, rootView,false);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		initView(view);
		initData();
		return view;
	}
	
	private void initData() {
		setConnectTimeoutListener(this);
		btn_debug_motor_roll.setOnClickListener(this);
		btn_debug_motor_rollback.setOnClickListener(this);
	}

	private void initView(View view) {
		btn_debug_motor_roll = (Button) view.findViewById(R.id.btn_debug_motor_roll);
		btn_debug_motor_rollback = (Button) view.findViewById(R.id.btn_debug_motor_rollback);
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
		case R.id.btn_debug_motor_roll:
			showLoading("正在调试...");
			isTimeOut = true;
			startTime(3000, "通信");
			sendDebugMotorCMD(CdzCMDConstant.CDZ_DEBUG_ROLL,0);
			break;
		case R.id.btn_debug_motor_rollback:
			isTimeOut = true;
			startTime(3000, "通信");
			showLoading("正在调试...");
			sendDebugMotorCMD(CdzCMDConstant.CDZ_DEBUG_ROLL,1);
			break;
		default:
			break;
		}
		super.onClick(v);
	}

	private void sendDebugMotorCMD(int cmd,int rollTag) {
		int lenth = 1 + 12;
		byte [] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);//h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);//m
		byteData[2] = YzzsCommonUtil.intTobyte(CdzCMDConstant.CDZ_SBBZ);//设备区分标志
		byteData[3] = YzzsCommonUtil.intTobyte(cmd / 256);//命令高位
		byteData[4] = YzzsCommonUtil.intTobyte(cmd % 256);//命令低位
		byteData[5] = YzzsCommonUtil.intTobyte(lenth / 256);//长度低位
		byteData[6] = YzzsCommonUtil.intTobyte(lenth % 256);//长度高位
		byteData[7] = YzzsCommonUtil.intTobyte(1);//后面值得长度
		byteData[8] = YzzsCommonUtil.intTobyte(rollTag);//正转反转 0:正转 1:反转
		byteData[9] = 1;//校验位
		byteData[10] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);//e
		byteData[11] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//n
		byteData[12] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
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
				case CdzCMDConstant.CDZ_DEBUG_ROLL:
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
			ToastMsg(getActivity(), "调试成功!");
		} else { // 保存
			dismissLoading();
			ToastMsg(getActivity(), "调试失败,请重试!");
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
