package com.huimv.yzzs.fragment.hk;

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
import com.huimv.yzzs.constant.HkCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.orhanobut.logger.Logger;

import java.util.Arrays;

import static com.huimv.yzzs.R.id.et_close_temp_1;
import static com.huimv.yzzs.R.id.et_close_temp_2;
import static com.huimv.yzzs.R.id.et_start_temp_1;
import static com.huimv.yzzs.R.id.et_start_temp_2;
import static com.huimv.yzzs.R.id.et_temp_2;

/**
 * 风机轮训时间
 */
public class HkFjLxsjFragment extends YzzsBaseFragment implements EventHandler ,ConnectTimeoutListener,OnClickListener{
	private SharePreferenceUtil mSpUtil;
	private boolean isTimeOut = true;
	private Button btn_save;
	private Button btn_read;
	private EditText et_temp_1;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_hk_fj_lxsj_fragment, null);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		initView(view);
		initData();
		return view;
	}

	private void initData() {
		setConnectTimeoutListener(this);
	}

	private void initView(View view) {
		et_temp_1 = (EditText) view.findViewById(R.id.et_temp_1);
        btn_save = (Button) view.findViewById(R.id.btn_save);
		btn_read = (Button) view.findViewById(R.id.btn_read);
		btn_save.setOnClickListener(this);
		btn_read.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_save:
			if (checkTemp()) {
				saveTemp();
			}
			break;
			case R.id.btn_read:
				readTemp();
			break;
		}
		super.onClick(v);
	}

	private boolean checkTemp() {
		String temp1 = et_temp_1.getText().toString();
		if(CommonUtil.isNotEmpty(temp1)) {
			if (Integer.valueOf(temp1) >= 50) {
				ToastMsg(getActivity(),"温度不能高于50度");
				return  false;
			}
		}
		return  true;
	}

	private void readTemp() {
		startTime(XtAppConstant.SEND_CMD_TIMEOUT,"读取温度");
		showLoading("正在读取...");
		sendGetBaseCMD(HkCMDConstant.HK_SBBZ,HkCMDConstant.READ_FJLXSJ_TEMP);
	}

	private void saveTemp() {
		showLoading("正在保存...");
		startTime(XtAppConstant.SEND_CMD_TIMEOUT,"保存时间");
		sendSaveTemp();
	}

	private void sendSaveTemp() {
		showLoading("正在保存数据...");
		String etTemp_1 = YzzsCommonUtil.formatStringAdd0
				(et_temp_1.getText().toString().trim().equals("") ? "0" : et_temp_1.getText().toString() , 2, 1);
		StringBuilder paramSb = new StringBuilder();
		paramSb.append(etTemp_1);
		int etDecimalLenth = paramSb.length();
		int lenth = etDecimalLenth + 12;
		int cmd = HkCMDConstant.SAVE_FJLXSJ_TEMP;
		byte [] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);//h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);//m
		byteData[2] = HkCMDConstant.HK_SBBZ;//设备区分标志
		byteData[3] = YzzsCommonUtil.intTobyte(cmd / 256);//命令高位
		byteData[4] = YzzsCommonUtil.intTobyte(cmd % 256);//命令低位
		byteData[5] = YzzsCommonUtil.intTobyte(lenth / 256);//长度低位
		byteData[6] = YzzsCommonUtil.intTobyte(lenth % 256);//长度高位
		byteData[7] = YzzsCommonUtil.intTobyte(etDecimalLenth);//后面值得长度
		for (int i = 8; i < etDecimalLenth + 8; i++) {
			byteData[i] = YzzsCommonUtil
					.intTobyte(Integer.parseInt((paramSb).substring(i - 8, i - 7)));
		}
		byteData[lenth - 4] = 1 ;//校验位
		byteData[lenth - 3] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);//e
		byteData[lenth - 2 ] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//n
		byteData[lenth - 1] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
		sendUnpackData(byteData,XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}
	@Override
	public void onPause() {
		MessageReceiver.ehList.remove(this);
		cancelTime();
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
			YzzsApplication.isConnected = false;
			dismissLoading();
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
	public void onMessage(byte[] message) {
		getJoiningPack(message);
	}
	/**
	 * 拼包
	 * @param message
	 */
	private void getJoiningPack (byte[] message) {
		if(message[0] == HkCMDConstant.HK_SBBZ) {//环控
			try {
				if (message.length <= 6) {
					return;
				}
				int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
				switch (cmd) {
					case HkCMDConstant.READ_FJLXSJ_TEMP:
						getReadResult(message);
						break;
					case HkCMDConstant.SAVE_FJLXSJ_TEMP:
						getSaveResult(message);
						break;
				}
			} catch (Exception e) {
				dismissLoading();
				ToastMsg(getActivity(), "数据异常");
			}
		}
	}

	private void getReadResult(byte[] message) {
		if(message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if(message[6] == 0) {
				if (message [7] == 2) {
					StringBuilder resultParam = new StringBuilder();
					et_temp_1.setText(String.valueOf(Integer.valueOf(resultParam.append(message [8]).append(message [9]).toString())));
				} else {
					ToastMsg(getActivity(), "读取出错");
				}
			} else {
				ToastMsg(getActivity(), "读取失败");
			}
		}
	}

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