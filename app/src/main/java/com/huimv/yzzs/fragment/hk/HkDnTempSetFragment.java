package com.huimv.yzzs.fragment.hk;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.HkCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.YzzsCommonUtil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * 地暖温度
 * @author jiangwei
 */
public class HkDnTempSetFragment extends YzzsBaseFragment implements EventHandler ,ConnectTimeoutListener{
	/**
	 * 按钮
	 */
	private Button btn_save,btn_read;
	private Button btn_save_force_open_swtich,btn_save_force_closed_swtich;
	private Button btn_save_temp_protect,btn_read_temp_protect;
	private Button btn_save_start_temp,btn_read_start_temp;
	/**
	 * 整数,小数
	 */
	private EditText et_auto_target_temp_integer;
	private EditText et_heigh_temp_protect_value,et_low_temp_protect_value;
	/**
	 * 启动温差
	 */
	private EditText et_start_temp_value;
	private boolean isTimeOut = true;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_hk_dn_temp_fragment, null);
		initView(view);
		initData();
		initListener();
		return view;
	}

	private void initListener() {
		setConnectTimeoutListener(this);
		btn_save.setOnClickListener(this);
		btn_read.setOnClickListener(this);
		btn_save_force_open_swtich.setOnClickListener(this);
		btn_save_force_closed_swtich.setOnClickListener(this);
		btn_save_temp_protect.setOnClickListener(this);
		btn_read_temp_protect.setOnClickListener(this);
		btn_save_start_temp.setOnClickListener(this);
		btn_read_start_temp.setOnClickListener(this);
	}

	private void initData() {
		//doReadTemp();
	}

	private void initView(View view) {
		btn_save = (Button) view.findViewById(R.id.btn_save);
		btn_read = (Button) view.findViewById(R.id.btn_read);
		btn_save_force_open_swtich = (Button) view.findViewById(R.id.btn_save_force_open_swtich);
		btn_save_force_closed_swtich = (Button) view.findViewById(R.id.btn_save_force_closed_swtich);
		et_auto_target_temp_integer = (EditText) view.findViewById(R.id.et_auto_target_temp_integer);
		et_start_temp_value = (EditText) view.findViewById(R.id.et_start_temp_value);
		btn_save_temp_protect = (Button) view.findViewById(R.id.btn_save_temp_protect);
		btn_read_temp_protect = (Button) view.findViewById(R.id.btn_read_temp_protect);
		et_heigh_temp_protect_value = (EditText) view.findViewById(R.id.et_heigh_temp_protect_value);
		et_low_temp_protect_value = (EditText) view.findViewById(R.id.et_low_temp_protect_value);
		btn_save_start_temp = (Button) view.findViewById(R.id.btn_save_start_temp);
		btn_read_start_temp = (Button) view.findViewById(R.id.btn_read_start_temp);
	}
	
	@Override
	public void onClick(View v) {
		if(!YzzsApplication.isConnected) {
			ToastMsg(getActivity(), getString(R.string.disconnected));
			return;
		}
		switch (v.getId()) {
		case R.id.btn_read_start_temp:
			doReadStartTemp();
			break;
		case R.id.btn_save_start_temp:
			if(checkStartTempData()) {
				doSaveStartTemp();
			}
			break;
		case R.id.btn_read_temp_protect:
			doReadProtectTemp();
			break;
		case R.id.btn_save_temp_protect:
			if(checkProtectTempData()) {
				doSaveProtectTemp();
			}
			break;
		case R.id.btn_read:
			doReadTemp();
			break;
		case R.id.btn_save:
			if(checkTempData()) {
				doSaveTemp();
			}
			break;
		case R.id.btn_save_force_open_swtich:
			doSetForceOpenOrClose(true);
			break;
		case R.id.btn_save_force_closed_swtich:
			doSetForceOpenOrClose(false);
			break;
		default:
			break;
		}
		super.onClick(v);
	}
	
	private void doReadStartTemp() {
		if (YzzsApplication.isConnected) {
			isTimeOut = true;
			startTime(HkCMDConstant.LOGIN_TIME, "读取");
			showLoading("正在读取启动温差..");
			sendGetBaseCMD(HkCMDConstant.HK_SBBZ, HkCMDConstant.READ_DN_START_TEMP);
		}
	}

	private void doSaveProtectTemp() {
		if (YzzsApplication.isConnected) {
			isTimeOut = true;
			startTime(HkCMDConstant.LOGIN_TIME, "保存高低温保护值");
			sendSaveProtectTempCMD();
		}
	}
	
	private void doSaveStartTemp() {
		if (YzzsApplication.isConnected) {
			isTimeOut = true;
			startTime(HkCMDConstant.LOGIN_TIME, "保存启动温差");
			sendSaveStartTempCMD();
		}
	}
	
	private void doSetForceOpenOrClose(boolean isOpen) {
		if (YzzsApplication.isConnected) {
			isTimeOut = true;
			startTime(HkCMDConstant.LOGIN_TIME, "强制开关");
			showLoading("正在设置强制开关");
			doSendForceOpenOrCloseCMD (isOpen);
		}
	}

	private void doSendForceOpenOrCloseCMD(boolean isOpen) {
		int isOpenInt1 = isOpen ? 0 : 1;
		int isOpenInt2 = isOpen ? 1 : 0;
		int lenth = 14;
		final byte[] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);// h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);// m
		byteData[2] = YzzsCommonUtil.intTobyte(HkCMDConstant.HK_SBBZ);
		byteData[3] = YzzsCommonUtil.intTobyte(HkCMDConstant.DN_FORCE_SWTICH / 256);// 命令高位
		byteData[4] = YzzsCommonUtil.intTobyte(HkCMDConstant.DN_FORCE_SWTICH % 256);// 命令低位
		byteData[5] = YzzsCommonUtil.intTobyte(lenth / 256);// 长度低位
		byteData[6] = YzzsCommonUtil.intTobyte(lenth % 256);// 长度高位
		byteData[7] = 2;// 后面值得长度
		byteData[8] = YzzsCommonUtil.intTobyte(isOpenInt1);
		byteData[9] = YzzsCommonUtil.intTobyte(isOpenInt2);
		byteData[10] = 1;// 校验位
		byteData[11] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);// e
		byteData[12] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);// n
		byteData[13] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);// d
		sendUnpackData(byteData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}
	
	/**
	 * 读取高低温保护值
	 */
	private void doReadProtectTemp() {
		if (YzzsApplication.isConnected) {
			isTimeOut = true;
			startTime(HkCMDConstant.LOGIN_TIME, "读取");
			showLoading("正在读取高低温保护值温度..");
			sendGetBaseCMD(HkCMDConstant.HK_SBBZ, HkCMDConstant.READ_DN_PROTECT_TEMP);
		}
	}
	
	/**
	 * 读取温度
	 */
	private void doReadTemp() {
		if (YzzsApplication.isConnected) {
			isTimeOut = true;
			startTime(HkCMDConstant.LOGIN_TIME, "读取");
			showLoading("正在读取设定温度..");
			sendGetBaseCMD(HkCMDConstant.HK_SBBZ, HkCMDConstant.READ_DN_TEMP);
		}
	}
	/**
	 * 保存温度
	 */
	private void doSaveTemp() {
		if (YzzsApplication.isConnected) {
			isTimeOut = true;
			startTime(HkCMDConstant.LOGIN_TIME, "保存");
			sendSaveTempCMD();
		}
	}
	/**
	 * 检测启动温度温度数据是否符合要求
	 */
	private boolean checkStartTempData() {
		//整数部分
		String startTemp = et_start_temp_value.getText().toString().trim();
		if (CommonUtil.isEmpty(startTemp)) {
			ToastMsg(getActivity(), "设置目标温度不能为空");
			return false;
		}
		return true;
	}
	/**
	 * 检测高低温温度数据是否符合要求
	 */
	private boolean checkProtectTempData() {
		String heighProtectTemp = et_heigh_temp_protect_value.getText().toString().trim();
		String lowProtectTemp = et_low_temp_protect_value.getText().toString().trim();
		if (CommonUtil.isEmpty(heighProtectTemp)) {
			ToastMsg(getActivity(), "高温保护值不能为空不能为空");
			return false;
		}
		if (CommonUtil.isEmpty(lowProtectTemp)) {
			ToastMsg(getActivity(), "低温保护值不能为空");
			return false;
		}
		if (!(Integer.valueOf(heighProtectTemp) >= 35 && Integer.valueOf(heighProtectTemp) <= 40)) {
			ToastMsg(getActivity(), "高温保护值处于35-40℃之间");
			return false;
		}
		if (!(Integer.valueOf(lowProtectTemp) >= 5 && Integer.valueOf(lowProtectTemp) <= 10)) {
			ToastMsg(getActivity(), "低温保护值处于5-10℃之间");
			return false;
		}
		return true;
	}
	/**
	 * 检测温度数据是否符合要求
	 */
	private boolean checkTempData() {
		//整数部分
		String tempInt = et_auto_target_temp_integer.getText().toString().trim();
		if (CommonUtil.isEmpty(tempInt)) {
			ToastMsg(getActivity(), "设置目标温度不能为空");
			return false;
		}
		if (Integer.valueOf(tempInt) > 35) {
			ToastMsg(getActivity(), "温度设定不能大于35℃");
			return false;
		}
		if (Integer.valueOf(tempInt) < 5) {
			ToastMsg(getActivity(), "温度设定不能小于5℃");
			return false;
		}
		return true;
	}
	
	/**
	 * 发送密码验证命令
	 */
	private void sendSaveProtectTempCMD() {
		showLoading("正在保存数据...");
		String highTempValue = YzzsCommonUtil.formatStringAdd0
				(et_heigh_temp_protect_value.getText().toString().trim(), 2, 1);
		String lowTempValue = YzzsCommonUtil.formatStringAdd0
				(et_low_temp_protect_value.getText().toString().trim(), 2, 1);
		
		int lenth = highTempValue.length() + lowTempValue.length() + 1 + 12;
		int cmd = HkCMDConstant.SAVE_DN_PROTECT_TEMP;
		byte [] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);//h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);//m
		byteData[2] = HkCMDConstant.HK_SBBZ;//设备区分标志
		byteData[3] = YzzsCommonUtil.intTobyte(cmd / 256);//命令高位
		byteData[4] = YzzsCommonUtil.intTobyte(cmd % 256);//命令低位
		byteData[5] = YzzsCommonUtil.intTobyte(lenth / 256);//长度低位
		byteData[6] = YzzsCommonUtil.intTobyte(lenth % 256);//长度高位
		byteData[7] = YzzsCommonUtil.intTobyte(highTempValue.length());//后面值得长度
		for (int i = 8; i <  highTempValue.length() + 8; i++) {
			byteData[i] = YzzsCommonUtil
					.intTobyte(Integer.parseInt((highTempValue).substring(i - 8, i - 7)));
		}
		byteData[10] = YzzsCommonUtil.intTobyte(lowTempValue.length());//长度
		for (int i = 11; i <  lowTempValue.length() + 11; i++) {
			byteData[i] = YzzsCommonUtil
					.intTobyte(Integer.parseInt((lowTempValue).substring(i - 11, i - 10)));
		}
		byteData[13] = 1;//校验位
		byteData[14] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);//e
		byteData[15] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//n
		byteData[16] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
		sendUnpackData(byteData,XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}
	
	/**
	 * 发送保存温度命令
	 */
	private void sendSaveStartTempCMD() {
		showLoading("正在保存数据...");
		//整数部分
		String tempInt = YzzsCommonUtil.formatStringAdd0
				(et_start_temp_value.getText().toString().trim(), 1, 1);
		int etDecimalLenth = tempInt.length();
		int lenth = etDecimalLenth + 12;
		int cmd = HkCMDConstant.SAVE_DN_START_TEMP;
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
					.intTobyte(Integer.parseInt((tempInt).substring(i - 8, i - 7)));
		}
		byteData[9] = 1;//校验位
		byteData[10] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);//e
		byteData[11] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//n
		byteData[12] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
		sendUnpackData(byteData,XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}
	/**
	 * 发送保存温度命令
	 */
	private void sendSaveTempCMD() {
		showLoading("正在保存数据...");
		//整数部分
		String tempInt = YzzsCommonUtil.formatStringAdd0
				(et_auto_target_temp_integer.getText().toString().trim(), 2, 1);
		int etDecimalLenth = tempInt.length();
		int lenth = etDecimalLenth + 12;
		int cmd = HkCMDConstant.SAVE_DN_TEMP;
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
					.intTobyte(Integer.parseInt((tempInt).substring(i - 8, i - 7)));
		}
		byteData[10] = 1 ;//校验位
		byteData[11] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);//e
		byteData[12] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//n
		byteData[13] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
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
		if (message.length <= 6) {
			return;
		}
		if(message[0] == HkCMDConstant.HK_SBBZ) {//环控
			try {
				int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
				
				if (cmd == HkCMDConstant.SAVE_DN_TEMP) {
					getSaveTempResult(message);
				}
				if (cmd == HkCMDConstant.READ_DN_TEMP) {
					getReadTempResult(message);
				}
				if (cmd == HkCMDConstant.READ_DN_START_TEMP) {
					getReadStartTempResult(message);
				}
				if (cmd == HkCMDConstant.SAVE_DN_START_TEMP) {
					getSaveStartTempResult(message);
				}
				if (cmd == HkCMDConstant.DN_FORCE_SWTICH) {
					getSaveForceOpenOrCloseResult(message);
				}
				if (cmd == HkCMDConstant.SAVE_DN_PROTECT_TEMP) {
					getSaveProtectTemp(message);
				}
				if (cmd == HkCMDConstant.READ_DN_PROTECT_TEMP) {
					getReadProtectTempResult(message);
				}
			} catch (Exception e) {
				dismissLoading();
				ToastMsg(getActivity(), "地暖数据异常");
			}
		}
	}
	
	private void getSaveStartTempResult(byte[] message) {
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

	private void getReadStartTempResult(byte[] message) {
		if(message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if(message[6] == 0) {
				et_start_temp_value.setText(Integer.valueOf(message[8]) + "");
			} else {
				ToastMsg(getActivity(), "温度读取失败");
				et_start_temp_value.setText("N/A");
			}
		}
	}

	private void getReadProtectTempResult(byte[] message) {
		if(message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if(message[6] == 0) {
				et_heigh_temp_protect_value.setText(Integer.valueOf(message[8] + "" + message[9]) + "");
				if (message.length > 8 + message[7] ) {
					et_low_temp_protect_value.setText(Integer.valueOf(message[11] + "" + message[12]) + "");
				}
			} else {
				ToastMsg(getActivity(), "高低温保护值读取失败");
			}
		}
	}
	
	/**
	 * 高低温值保存
	 * @param message
	 */
	private void getSaveProtectTemp(byte[] message) {
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

	private void getSaveForceOpenOrCloseResult(byte[] message) {
		if(message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if(message[6] == 0) {
				ToastMsg(getActivity(), "操作成功");
			} else {
				ToastMsg(getActivity(), "操作失败");
			}
		}
	}

	/**
	 * 读取设定温度值
	 * @param message
	 */
	private void getReadTempResult(byte[] message) {
		if(message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if(message[6] == 0) {
				et_auto_target_temp_integer.setText(Integer.valueOf(message[8] + "" +message[9]) + "");
			} else {
				ToastMsg(getActivity(), "温度读取失败");
			}
		}
	}

	/**
	 * 保存返回结果
	 * @param message
	 */
	private void getSaveTempResult(byte[] message) {
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