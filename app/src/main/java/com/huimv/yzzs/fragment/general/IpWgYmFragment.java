package com.huimv.yzzs.fragment.general;

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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
/**
 * 修改IP
 * @author jiangwei
 *
 */
public class IpWgYmFragment extends YzzsBaseFragment implements EventHandler,ConnectTimeoutListener,OnClickListener{
	private Button changeIpBtn,readIpBtn;
	private Button changeWgBtn,readWgBtn;
	private Button changeYmBtn,readYmBtn;
	private Button checkIPBtn;
	private EditText IpEt1,IpEt2,IpEt3,IpEt4;
	private EditText WgEt1,WgEt2,WgEt3,WgEt4;
	private EditText YmEt1;
	private int whichDev = -1;
	private int whichDevReadIPCMD = -1;
	private int whichDevChangeIpCMD = -1;
	private int whichDevReadWgCMD = -1;
	private int whichDevChangeWgCMD = -1;
	private int whichDevReadYmCMD = -1;
	private int whichDevChangeYmCMD = -1;
	private int whichDevCheckIPCMD = -1;
	private boolean isTimeOut = true;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_ip_wg_ym_fragment, null);
		initView(view);
		initData();
		return view;
	}
	private void initData() {
		whichDev = getArguments().getInt("whichDev");
		whichDevReadIPCMD = GeneralCMDConstant.READ_IP;
		whichDevChangeIpCMD = GeneralCMDConstant.CHANGE_IP;
		whichDevReadWgCMD = GeneralCMDConstant.READ_WG;
		whichDevChangeWgCMD = GeneralCMDConstant.CHANGE_WG;
		whichDevReadYmCMD = GeneralCMDConstant.READ_YM;
		whichDevChangeYmCMD = GeneralCMDConstant.CHANGE_YM;
		whichDevCheckIPCMD = GeneralCMDConstant.CHECK_IP_CONFLICT;
		setConnectTimeoutListener(this);
	}
	
	private void initView(View view) {
		IpEt1 = (EditText) view.findViewById(R.id.IpEt1);
		IpEt2 = (EditText) view.findViewById(R.id.IpEt2);
		IpEt3 = (EditText) view.findViewById(R.id.IpEt3);
		IpEt4 = (EditText) view.findViewById(R.id.IpEt4);
		WgEt1 = (EditText) view.findViewById(R.id.WgEt1);
		WgEt2 = (EditText) view.findViewById(R.id.WgEt2);
		WgEt3 = (EditText) view.findViewById(R.id.WgEt3);
		WgEt4 = (EditText) view.findViewById(R.id.WgEt4);
		YmEt1 = (EditText) view.findViewById(R.id.YmEt1);
		changeIpBtn = (Button) view.findViewById(R.id.changeIpBtn);
		readIpBtn = (Button) view.findViewById(R.id.readIpBtn);
		changeIpBtn.setOnClickListener(this);
		readIpBtn.setOnClickListener(this);
		changeWgBtn = (Button) view.findViewById(R.id.changeWgBtn);
		readWgBtn = (Button) view.findViewById(R.id.readWgBtn);
		changeWgBtn.setOnClickListener(this);
		readWgBtn.setOnClickListener(this);
		
		changeYmBtn = (Button) view.findViewById(R.id.changeYmBtn);
		readYmBtn = (Button) view.findViewById(R.id.readYmBtn);
		
		changeYmBtn.setOnClickListener(this);
		readYmBtn.setOnClickListener(this);

		checkIPBtn = (Button) view.findViewById(R.id.checkIpBtn);
		checkIPBtn.setOnClickListener(this);
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
		if (!isConnected) {
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.changeIpBtn:
			if(YzzsApplication.isConnected) {
				if (!checkIPData()) {
					return;
				}
				showLoading("正在保存...");
				sendXgCmd(whichDevChangeIpCMD,true);
				startTime(HkCMDConstant.SEND_HK_SAVE_CMD_TIMEOUT, "保存数据");
				isTimeOut = true;
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		case R.id.readIpBtn:
			if(YzzsApplication.isConnected) {
				showLoading("正在读取...");
				sendDqCmd(whichDevReadIPCMD);
				startTime(HkCMDConstant.SEND_HK_SAVE_CMD_TIMEOUT, "读取数据");
				isTimeOut = true;
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		case R.id.readWgBtn:
			if(YzzsApplication.isConnected) {
				showLoading("正在读取...");
				sendDqCmd(whichDevReadWgCMD);
				startTime(HkCMDConstant.SEND_HK_SAVE_CMD_TIMEOUT, "读取数据");
				isTimeOut = true;
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		case R.id.changeWgBtn:
			if(YzzsApplication.isConnected) {
				if (!checkWgData()) {
					return;
				}
				showLoading("正在保存...");
				sendXgCmd(whichDevChangeWgCMD,false);
				startTime(HkCMDConstant.SEND_HK_SAVE_CMD_TIMEOUT, "保存数据");
				isTimeOut = true;
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		case R.id.changeYmBtn:
			if(YzzsApplication.isConnected) {
				if (!checkYmData()) {
					return;
				}
				showLoading("正在保存...");
				sendXgYm(whichDev,whichDevChangeYmCMD);
				startTime(HkCMDConstant.SEND_HK_SAVE_CMD_TIMEOUT, "保存数据");
				isTimeOut = true;
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		case R.id.readYmBtn:
			if(YzzsApplication.isConnected) {
				showLoading("正在读取...");
				sendDqCmd(whichDevReadYmCMD);
				startTime(HkCMDConstant.SEND_HK_SAVE_CMD_TIMEOUT, "读取数据");
				isTimeOut = true;
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		case R.id.checkIpBtn:
			if(YzzsApplication.isConnected) {
				showLoading("检测ip是否冲突...");
				sendGetBaseCMD(whichDev,whichDevCheckIPCMD);
				startTime(HkCMDConstant.SEND_HK_SAVE_CMD_TIMEOUT, "读取数据");
				isTimeOut = true;
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		}
		super.onClick(v);
	}
	
	private void sendXgYm(int dev,int cmd) {
		int ymTag = -1;
		if (YmEt1.getText().toString().trim().equals("255.255.255.0")) {
			ymTag = 24;
		}
		if (YmEt1.getText().toString().trim().equals("255.255.0")) {
			ymTag = 16;
		}
		int lenth = 13;
		final byte[] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);// h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);// m
		byteData[2] = YzzsCommonUtil.intTobyte(dev);
		byteData[3] = YzzsCommonUtil.intTobyte(cmd / 256);// 命令高位
		byteData[4] = YzzsCommonUtil.intTobyte(cmd % 256);// 命令低位
		byteData[5] = YzzsCommonUtil.intTobyte(lenth / 256);// 长度低位
		byteData[6] = YzzsCommonUtil.intTobyte(lenth % 256);// 长度高位
		byteData[7] = 1;// 后面值得长度
		byteData[8] = YzzsCommonUtil.intTobyte(ymTag);// 后面值得长度
		byteData[9] = 1;// 校验位
		byteData[10] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);// e
		byteData[11] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);// n
		byteData[12] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);// d
		sendUnpackData(byteData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}
	
	@Override
	public void onMessage(byte[] message) {
		if (message[0] == whichDev) {//是不是环控的命令
			int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
			if (cmd == whichDevReadIPCMD) {
				getDqIp(message);
			}
			if (cmd == whichDevChangeIpCMD) {
				getXgIp(message);
			}
			if (cmd == whichDevReadWgCMD) {
				getDqWg(message);
			}
			if (cmd == whichDevChangeWgCMD) {
				getXgWg(message);
			}
			if (cmd == whichDevReadYmCMD) {
				getDqYm(message);
			}
			if (cmd == whichDevChangeYmCMD) {
				getXgYm(message);
			}
			if (cmd == whichDevCheckIPCMD) {
				getCheckIP(message);
			}
		}
	}
	
	/**
	 * 读取ip/网关名称
	 */
	private void sendDqCmd(int cmd) {
		sendGetBaseCMD(whichDev, cmd);
	}

	/**
	 * 得到修改IP的返回值
	 */
	private void getXgIp(byte[] message) {
		if (message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if (message[6] == 0) {
				ToastMsg(getActivity(), "IP修改成功");
			} else {
				ToastMsg(getActivity(), "IP修改失败");
			}
		} else {
			ToastMsg(getActivity(), "IP网关修改失败");
		}
	}
	/**
	 * 得到修改IP的返回值
	 */
	private void getXgWg(byte[] message) {
		if (message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if (message[6] == 0) {
				ToastMsg(getActivity(), "网关修改成功");
			} else {
				ToastMsg(getActivity(), "网关修改失败");
			}
		} else {
			ToastMsg(getActivity(), "网关修改失败");
		}
		dismissLoading();
	}
	/**
	 * 得到修改掩码的返回值
	 */
	private void getXgYm(byte[] message) {
		if (message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if (message[6] == 0) {
				ToastMsg(getActivity(), "掩码修改成功");
			} else {
				ToastMsg(getActivity(), "掩码修改失败");
			}
		} else {
			ToastMsg(getActivity(), "掩码修改失败");
		}
		dismissLoading();
	}
	/**
	 * 读取的淹码
	 * @param message
	 */
	private void getDqYm(byte[] message) {
		if (message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if (message[6] == 0) {
				int tag = message[7];
				if (tag == 24) {
					YmEt1.setText("255.255.255.0");
				}
				if (tag == 16) {
					YmEt1.setText("255.255.0");
				}
				ToastMsg(getActivity(), "掩码读取成功");
			} else {
				ToastMsg(getActivity(), "掩码读取失败");
			}
		} else {
			ToastMsg(getActivity(), "数据读取失败");
		}
	}
	/**
	 * 读取的IP
	 * @param message
	 */
	private void getDqWg(byte[] message) {
		if (message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if (message[6] == 0) {
				WgEt1.setText(YzzsCommonUtil.ChangeByteToPositiveNumber(message[8]) + "");
				WgEt2.setText(YzzsCommonUtil.ChangeByteToPositiveNumber(message[9]) + "");
				WgEt3.setText(YzzsCommonUtil.ChangeByteToPositiveNumber(message[10]) + "");
				WgEt4.setText(YzzsCommonUtil.ChangeByteToPositiveNumber(message[11]) + "");
				ToastMsg(getActivity(), "网关读取成功");
			} else {
				ToastMsg(getActivity(), "网关读取失败");
			}
		} else {
			ToastMsg(getActivity(), "数据读取失败");
		}
	}
	/**
	 * 读取的IP
	 * @param message
	 */
	private void getDqIp(byte[] message) {
		if (message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if (message[6] == 0) {
				IpEt1.setText(YzzsCommonUtil.ChangeByteToPositiveNumber(message[8]) + "");
				IpEt2.setText(YzzsCommonUtil.ChangeByteToPositiveNumber(message[9]) + "");
				IpEt3.setText(YzzsCommonUtil.ChangeByteToPositiveNumber(message[10]) + "");
				IpEt4.setText(YzzsCommonUtil.ChangeByteToPositiveNumber(message[11]) + "");
				ToastMsg(getActivity(), "IP读取成功");
			} else {
				ToastMsg(getActivity(), "IP读取失败");
			}
		} else {
			ToastMsg(getActivity(), "数据读取失败");
		}
	}
	
	private boolean checkIPData() {
		if (CommonUtil.isEmpty(IpEt1.getText().toString()) || CommonUtil.isEmpty(IpEt2.getText().toString()) ||
				CommonUtil.isEmpty(IpEt3.getText().toString()) || CommonUtil.isEmpty(IpEt4.getText().toString())) {
			ToastMsg(getActivity(), "输入IP不能空");
			return false;
		}
		if (Integer.parseInt(IpEt1.getText().toString()) > 255 || Integer.parseInt(IpEt2.getText().toString()) > 255 ||
				Integer.parseInt(IpEt3.getText().toString()) > 255 || Integer.parseInt(IpEt4.getText().toString()) > 255) {
			ToastMsg(getActivity(), "输入IP不合法");
			return false;
		}
		return true;
	}
	private boolean checkWgData() {
		if (CommonUtil.isEmpty(WgEt1.getText().toString()) || CommonUtil.isEmpty(WgEt2.getText().toString()) ||
				CommonUtil.isEmpty(WgEt3.getText().toString()) || CommonUtil.isEmpty(WgEt4.getText().toString())) {
			ToastMsg(getActivity(), "输入网关不能空");
			return false;
		}
		if (Integer.parseInt(WgEt1.getText().toString()) > 255 || Integer.parseInt(WgEt2.getText().toString()) > 255 ||
				Integer.parseInt(WgEt3.getText().toString()) > 255 || Integer.parseInt(WgEt4.getText().toString()) > 255) {
			ToastMsg(getActivity(), "输入网关不合法");
			return false;
		}
		return true;
	}
	/**
	 * 检验掩码
	 * @return
	 */
	private boolean checkYmData() {
		if (CommonUtil.isEmpty(WgEt1.getText().toString()) || CommonUtil.isEmpty(WgEt2.getText().toString()) ||
				CommonUtil.isEmpty(WgEt3.getText().toString()) || CommonUtil.isEmpty(WgEt4.getText().toString())) {
			ToastMsg(getActivity(), "输入掩码不能空");
			return false;
		}
		if (!YmEt1.getText().toString().trim().equals("255.255.255.0") || !YmEt1.getText().toString().trim().equals("255.255.0")) {
			ToastMsg(getActivity(), "掩码必须为255.255.255.0,或255.255.0");
			return false;
		}
		return true;
	}
	/**
	 * 修改IP
	 */
	private void sendXgCmd(int cmd,boolean isIp) {
		String Et1;
		String Et2;
		String Et3;
		String Et4;
		if (isIp) {
			Et1 = IpEt1.getText().toString().trim();
			Et2 = IpEt2.getText().toString().trim();
			Et3 = IpEt3.getText().toString().trim();
			Et4 = IpEt4.getText().toString().trim();
		} else {
			Et1 = WgEt1.getText().toString().trim();
			Et2 = WgEt2.getText().toString().trim();
			Et3 = WgEt3.getText().toString().trim();
			Et4 = WgEt4.getText().toString().trim();
		}
		int dataLength = 12 + 4;
		byte [] byteSendData = new byte[dataLength];
		byteSendData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH); // 协议头 “h”
		byteSendData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM); // 协议头 “m”
		byteSendData[2] = YzzsCommonUtil.intTobyte(whichDev);//什么设备
		byteSendData[3] = YzzsCommonUtil.intTobyte(cmd / 256); // 数据域：命令高位
		byteSendData[4] = YzzsCommonUtil.intTobyte(cmd % 256); // 数据域：命令低位
		byteSendData[5] = YzzsCommonUtil.intTobyte(dataLength / 256); // 数据域：数据总长度高位
		byteSendData[6] = YzzsCommonUtil.intTobyte(dataLength % 256); // 数据域：数据总长度低位
		byteSendData[7] = 4;
		byteSendData[8] = YzzsCommonUtil.intTobyte(Integer.parseInt(Et1));
		byteSendData[9] = YzzsCommonUtil.intTobyte(Integer.parseInt(Et2));
		byteSendData[10] = YzzsCommonUtil.intTobyte(Integer.parseInt(Et3));
		byteSendData[11] = YzzsCommonUtil.intTobyte(Integer.parseInt(Et4));
		byteSendData[dataLength - 4] = YzzsCommonUtil.intTobyte(0); // 校验位：暂时不用
		byteSendData[dataLength - 3] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE); // 协议尾：“e”
		byteSendData[dataLength - 2] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN); // 协议尾：“n”
		byteSendData[dataLength - 1] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD); // 协议尾：“d”
		sendBaseCMD(byteSendData);
	}

	/**
	 * 检测IP是否冲突
	 */
	private void getCheckIP(byte[] message) {
		if (message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if (message[6] == 0) {
				ToastMsg(getActivity(), "此IP未冲突,可使用");
			} else {
				ToastMsg(getActivity(), "此IP已被使用,请重新设置");
			}
		} else {
			ToastMsg(getActivity(), "掩码修改失败");
		}
		dismissLoading();
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