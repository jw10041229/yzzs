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

/**
 * 加热器开启温度设置
 */
public class HkJrqTempFragmentNew extends YzzsBaseFragment implements EventHandler ,ConnectTimeoutListener,OnClickListener{
	private SharePreferenceUtil mSpUtil;
	private boolean isTimeOut = true;
	private Button btn_save;
	private Button btn_read;
	private EditText et_temp_1;
	private EditText et_temp_2;
	private EditText et_start_temp_1;
	private EditText et_start_temp_2;
	private EditText et_close_temp_1;
	private EditText et_close_temp_2;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_hk_jrq_temp_fragment_new, null);
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
		et_temp_2 = (EditText) view.findViewById(R.id.et_temp_2);
        btn_save = (Button) view.findViewById(R.id.btn_save);
		btn_read = (Button) view.findViewById(R.id.btn_read);
		et_start_temp_1 = (EditText) view.findViewById(R.id.et_start_temp_1);
		et_start_temp_2 = (EditText) view.findViewById(R.id.et_start_temp_2);
		et_close_temp_1 = (EditText) view.findViewById(R.id.et_close_temp_1);
		et_close_temp_2 = (EditText) view.findViewById(R.id.et_close_temp_2);
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
		String temp2 = et_temp_2.getText().toString();

		String start_temp1 = et_start_temp_1.getText().toString();
		String start_temp2 = et_start_temp_2.getText().toString();
		String close_temp1 = et_close_temp_1.getText().toString();
		String close_temp2 = et_close_temp_2.getText().toString();

		if(CommonUtil.isNotEmpty(temp1)) {
			if (Integer.valueOf(temp1) >= 50) {
				ToastMsg(getActivity(),"温度不能高于50度");
				return  false;
			}
		}
		if(CommonUtil.isNotEmpty(temp2)) {
			if (Integer.valueOf(temp2) >= 50) {
				ToastMsg(getActivity(),"温度不能高于50度");
				return  false;
			}
		}
		if(CommonUtil.isNotEmpty(start_temp1)) {
			if (Integer.valueOf(start_temp1) >= 10) {
				ToastMsg(getActivity(),"温差不能高于10度");
				return  false;
			}
		}
		if(CommonUtil.isNotEmpty(start_temp2)) {
			if (Integer.valueOf(start_temp2) >= 10) {
				ToastMsg(getActivity(),"温差不能高于10度");
				return  false;
			}
		}
		if(CommonUtil.isNotEmpty(close_temp1)) {
			if (Integer.valueOf(close_temp1) >= 10) {
				ToastMsg(getActivity(),"温差不能高于10度");
				return  false;
			}
		}
		if(CommonUtil.isNotEmpty(close_temp2)) {
			if (Integer.valueOf(close_temp2) >= 10) {
				ToastMsg(getActivity(),"温差不能高于10度");
				return  false;
			}
		}
		return  true;
	}

	private void readTemp() {
		startTime(XtAppConstant.SEND_CMD_TIMEOUT,"读取温度");
		showLoading("正在读取...");
		sendGetBaseCMD(HkCMDConstant.HK_SBBZ,HkCMDConstant.READ_JRQ_TEMP_EX);
	}

	private void saveTemp() {
		showLoading("正在保存...");
		startTime(XtAppConstant.SEND_CMD_TIMEOUT,"保存温度");
		sendSaveTemp();
	}

	private void sendSaveTemp() {
		showLoading("正在保存数据...");
		String start_temp1 = et_start_temp_1.getText().toString();
		String start_temp2 = et_start_temp_2.getText().toString();
		String close_temp1 = et_close_temp_1.getText().toString();
		String close_temp2 = et_close_temp_2.getText().toString();


		String startTemp1 = YzzsCommonUtil.formatStringAdd0
				(start_temp1.trim().equals("") ? "0" : start_temp1 , 2, 1);

		String closeTemp1 = YzzsCommonUtil.formatStringAdd0
				(close_temp1.trim().equals("") ? "0" : close_temp1 , 2, 1);

		String startTemp2 = YzzsCommonUtil.formatStringAdd0
				(start_temp2.trim().equals("") ? "0" : start_temp2 , 2, 1);

		String closeTemp2 = YzzsCommonUtil.formatStringAdd0
				(close_temp2.trim().equals("") ? "0" : close_temp2 , 2, 1);
		StringBuilder paramSb = new StringBuilder();
		paramSb.append(startTemp1).append(closeTemp1)
				.append(startTemp2).append(closeTemp2);

		int etDecimalLenth = paramSb.length();
		int lenth = etDecimalLenth + 12;
		int cmd = HkCMDConstant.SAVE_JRQ_TEMP_EX;
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
					case HkCMDConstant.READ_JRQ_TEMP_EX:
						getReadResult(message);
						break;
					case HkCMDConstant.SAVE_JRQ_TEMP_EX:
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
				if (message [7] == 8) {
					StringBuilder resultParam = new StringBuilder();
					et_start_temp_1.setText(String.valueOf(Integer.valueOf(resultParam.append(message [8]).append(message [9]).toString())));
					resultParam.setLength(0);
					et_close_temp_1.setText(String.valueOf(Integer.valueOf(resultParam.append(message [10]).append(message [11]).toString())));
					resultParam.setLength(0);
					et_start_temp_2.setText(String.valueOf(Integer.valueOf(resultParam.append(message [12]).append(message [13]).toString())));
					resultParam.setLength(0);
					et_close_temp_2.setText(String.valueOf(Integer.valueOf(resultParam.append(message [14]).append(message [15]).toString())));
				} else {
					ToastMsg(getActivity(), "温度读取出错");
				}
			} else {
				ToastMsg(getActivity(), "温度读取失败");
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