package com.huimv.yzzs.fragment.scj;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huimv.yzzs.R;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.constant.ScjCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.YzzsCommonUtil;

@SuppressLint("DefaultLocale")
public class ScjSetParamFragment extends YzzsBaseFragment implements OnClickListener, EventHandler {
	private ImageView ibtnScjSetFreq, ibtnScjSetPow;
	private EditText etFrequencyValue, etPowerValue;
	private RelativeLayout rlBtStateLayout;
	private TextView tvPowerInfo, tvFrequencyInfo, tvRefresh;
	private ProgressBar progressBar;

	private String frequencyValue, powerValue; // 设置后的频率和功率值

	private Runnable mRunnable; // 超时定时器
	private Handler handler = new Handler();
	private boolean isReveivedData = false;

	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_scj_ssxs_set_fragment, null);
		dismissLoading();
		initView(view);
		initParams();
		initOnListeners();
		return view;
	}

	private void initView(View view) {
		etFrequencyValue = (EditText) view.findViewById(R.id.et_frequency_value);
		etPowerValue = (EditText) view.findViewById(R.id.et_power_value);

		ibtnScjSetFreq = (ImageView) view.findViewById(R.id.ibtn_scj_set_freq);
		ibtnScjSetPow = (ImageView) view.findViewById(R.id.ibtn_scj_set_pow);

		tvFrequencyInfo = (TextView) view.findViewById(R.id.tv_frequency_info);
		tvPowerInfo = (TextView) view.findViewById(R.id.tv_power_info);
		tvRefresh = (TextView) view.findViewById(R.id.tv_refresh);
		
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

		rlBtStateLayout = (RelativeLayout) view.findViewById(R.id.rl_bt_state_layout);
	}

	private void initOnListeners() {
		ibtnScjSetFreq.setOnClickListener(this);
		ibtnScjSetPow.setOnClickListener(this);
		tvRefresh.setOnClickListener(this);
	}

	private void initParams() {
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
	public void onConnected(boolean isConnected) {
		YzzsApplication.isConnected = isConnected;
		if (!isConnected) {// 如果断开
			ToastMsg(getActivity(), getString(R.string.disconnected));
			rlBtStateLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onReady(boolean isReady) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMessage(byte[] message) {
		receivePack(message);
	}

	/**
	 * 接收包
	 * 
	 * @param message
	 */
	private void receivePack(byte[] message) {
		if (message[0] == ScjCMDConstant.SCJ_SBBZ) { // 手持机
			try {
				refrehRealDataEx(message);
			} catch (Exception e) {
				dismissLoading();
				ToastMsg(getActivity(), "收到数据格式错误");
			}
		}
	}

	/**
	 * 更新 频率值 和 功率值
	 * 
	 * @param message
	 */
	private void refrehRealDataEx(byte[] message) {
		progressBar.setVisibility(View.GONE);
		isReveivedData = true;
		handler.removeCallbacks(mRunnable); // 取消定时
		int cmd = YzzsCommonUtil.getCMD(message[1], message[2]);
		if (cmd == ScjCMDConstant.SEND_FREQUENCY) {
			isReveivedData = true;
			handler.removeCallbacks(mRunnable); // 取消定时
			dismissLoading();
			if (message[5] == 0) {
				int freq = YzzsCommonUtil.ChangeByteToPositiveNumber(message[8]) * 256
						+ YzzsCommonUtil.ChangeByteToPositiveNumber(message[9]);
				frequencyValue = String.valueOf(freq);
				etFrequencyValue.setText("");
				tvFrequencyInfo.setText(frequencyValue);
			} else {
				ToastMsg(getActivity(), "手持机接收数据失败，请重试...");
			}

		}
		if (cmd == ScjCMDConstant.SEND_POWER) {
			isReveivedData = true;
			handler.removeCallbacks(mRunnable); // 取消定时
			dismissLoading();
			if (message[5] == 0) {
				int power = YzzsCommonUtil.ChangeByteToPositiveNumber(message[6]) * 256
						+ YzzsCommonUtil.ChangeByteToPositiveNumber(message[7]);
				float floatPower = (float) (power / 10.0);
				powerValue = String.valueOf(floatPower);
				etPowerValue.setText("");
				tvPowerInfo.setText(powerValue);
			} else {
				ToastMsg(getActivity(), "手持机接收数据失败，请重试...");
			}
		}

	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		// TODO Auto-generated method stub

	}

	/**
	 * 发送设置的频率值
	 */
	private void sendFrequencyData() {
		String frequencyStr = etFrequencyValue.getText().toString().trim();
		if ("".equals(frequencyStr)) {
			ToastMsg(getActivity(), "设置的频率值不能为空...");
		} else {
			int frequencyData = Integer.valueOf(frequencyStr);
			if (frequencyData >= 820 && frequencyData <= 950) {
				// 发送频率值
				sendFrequencyDataToBt(ScjCMDConstant.SEND_FREQUENCY, frequencyData);
			} else {
				ToastMsg(getActivity(), "输入的频率值只能在 820 ~ 950 之间");
			}
		}
	}

	/**
	 * 发送设置的功率值
	 */
	private void sendPowerData() {
		String powerStr = etPowerValue.getText().toString().trim();
		if ("".equals(powerStr)) {
			ToastMsg(getActivity(), "设置的功率值不能为空...");
		} else {
			float powerData = Float.valueOf(powerStr);
			if (powerData > 0 && powerData <= 30) {
				// 功率值 *10 处理
				int power = (int) (powerData * 10);
				// 发送功率值到蓝牙
				sendPowerDataToBt(ScjCMDConstant.SEND_POWER, power);
			} else {
				ToastMsg(getActivity(), "输入的频率值只能在 0 ~ 30 之间");
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (YzzsApplication.isConnected) {
			super.onClick(v);
			switch (v.getId()) {
			case R.id.ibtn_scj_set_freq:
				String freq = etFrequencyValue.getText().toString().trim();
				if("".equals(freq)){
					ToastMsg(getActivity(), "输入的频率值不能为空...");
				} else {
					showLoading("正在设置频率值...");
					sendFrequencyData();
					startTimerCount(2000); // 开启定时器
				}
				break;

			case R.id.ibtn_scj_set_pow:
				String power = etPowerValue.getText().toString().trim();
				if("".equals(power)){
					ToastMsg(getActivity(), "输入的功率值不能为空...");
				} else {
					showLoading("正在设置功率值...");
					sendPowerData();
					startTimerCount(2000); // 开启定时器
				}
				break;
				
			case R.id.tv_refresh:
				// 刷新频率和功率值
				progressBar.setVisibility(View.VISIBLE);
				sendRefreshToBt(ScjCMDConstant.GET_POWER_FREQUENCY_VALUE, 0);
				startTimerCount(2000); // 开启定时器
				break;
			}
		} else {
			ToastMsg(getActivity(), "蓝牙已断开连接，请到首页重新连接");
		}
	}

	/**
	 * 发送频率值到 bt
	 * 
	 * @param CmdType
	 * @param frequencyData
	 */
	private void sendFrequencyDataToBt(int CmdType, int frequencyData) {
		int dataLength = 15; // 数据总长度
		byte[] byteSendData = new byte[dataLength]; // 构造 byte 数组
		byteSendData[0] = YzzsCommonUtil.intTobyte(104); // 协议头 “h”
		byteSendData[1] = YzzsCommonUtil.intTobyte(109); // 协议头 “m”
		byteSendData[2] = YzzsCommonUtil.intTobyte(ScjCMDConstant.SCJ_SBBZ); // 数据域：类型
		byteSendData[3] = YzzsCommonUtil.intTobyte(CmdType / 256); // 数据域：命令高位
		byteSendData[4] = YzzsCommonUtil.intTobyte(CmdType % 256); // 数据域：命令低位
		byteSendData[5] = YzzsCommonUtil.intTobyte(dataLength / 256); // 数据域：数据总长度高位
		byteSendData[6] = YzzsCommonUtil.intTobyte(dataLength % 256); // 数据域：数据总长度低位

		byteSendData[7] = YzzsCommonUtil.intTobyte(0); // 数据域：通道：这里默认为 0 通道
		byteSendData[8] = YzzsCommonUtil.intTobyte(1); // 数据域：通道开关：默认为 1：打开；0：关闭
		byteSendData[9] = YzzsCommonUtil.intTobyte(frequencyData / 256); // 数据域：频率值高位
		byteSendData[10] = YzzsCommonUtil.intTobyte(frequencyData % 256); // 数据域：频率值低位

		byteSendData[11] = YzzsCommonUtil.intTobyte(1); // 校验位
		byteSendData[12] = YzzsCommonUtil.intTobyte(101); // 协议尾：“e”
		byteSendData[13] = YzzsCommonUtil.intTobyte(110); // 协议尾：“n”
		byteSendData[14] = YzzsCommonUtil.intTobyte(100); // 协议尾：“d”
		sendUnpackData(byteSendData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
		showLoading("正在设置频率值...");
		startTimerCount(2000); // 开启定时器
	}

	/**
	 * 发送功率值到 bt
	 * 
	 * @param CmdType
	 * @param powerData
	 */
	private void sendPowerDataToBt(int CmdType, int powerData) {
		int dataLength = 13; // 数据总长度
		byte[] byteSendData = new byte[dataLength]; // 构造 byte 数组
		byteSendData[0] = YzzsCommonUtil.intTobyte(104); // 协议头 “h”
		byteSendData[1] = YzzsCommonUtil.intTobyte(109); // 协议头 “m”
		byteSendData[2] = YzzsCommonUtil.intTobyte(ScjCMDConstant.SCJ_SBBZ); // 数据域：类型
		byteSendData[3] = YzzsCommonUtil.intTobyte(CmdType / 256); // 数据域：命令高位
		byteSendData[4] = YzzsCommonUtil.intTobyte(CmdType % 256); // 数据域：命令低位
		byteSendData[5] = YzzsCommonUtil.intTobyte(dataLength / 256); // 数据域：数据总长度高位
		byteSendData[6] = YzzsCommonUtil.intTobyte(dataLength % 256); // 数据域：数据总长度低位

		byteSendData[7] = YzzsCommonUtil.intTobyte(powerData / 256); // 数据域：功率值高位
		byteSendData[8] = YzzsCommonUtil.intTobyte(powerData % 256); // 数据域：功率值低位

		byteSendData[9] = YzzsCommonUtil.intTobyte(1); // 校验位
		byteSendData[10] = YzzsCommonUtil.intTobyte(101); // 协议尾：“e”
		byteSendData[11] = YzzsCommonUtil.intTobyte(110); // 协议尾：“n”
		byteSendData[12] = YzzsCommonUtil.intTobyte(100); // 协议尾：“d”
		sendUnpackData(byteSendData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
		showLoading("正在设置功率值...");
		startTimerCount(2000); // 开启定时器
	}
	
	private void sendRefreshToBt(int CmdType, int dataType) {
		int dataLength = 12; // 数据总长度
		byte[] byteSendData = new byte[dataLength]; // 构造 byte 数组
		byteSendData[0] = YzzsCommonUtil.intTobyte(104); // 协议头 “h”
		byteSendData[1] = YzzsCommonUtil.intTobyte(109); // 协议头 “m”
		byteSendData[2] = YzzsCommonUtil.intTobyte(ScjCMDConstant.SCJ_SBBZ); // 数据域：类型
		byteSendData[3] = YzzsCommonUtil.intTobyte(CmdType / 256); // 数据域：命令高位
		byteSendData[4] = YzzsCommonUtil.intTobyte(CmdType % 256); // 数据域：命令低位
		byteSendData[5] = YzzsCommonUtil.intTobyte(dataLength / 256); // 数据域：数据总长度高位
		byteSendData[6] = YzzsCommonUtil.intTobyte(dataLength % 256); // 数据域：数据总长度低位
		byteSendData[7] = YzzsCommonUtil.intTobyte(dataType); // 数据域：单条有效数据长度：0
		byteSendData[8] = YzzsCommonUtil.intTobyte(1); // 校验位
		byteSendData[9] = YzzsCommonUtil.intTobyte(101); // 协议尾：“e”
		byteSendData[10] = YzzsCommonUtil.intTobyte(110); // 协议尾：“n”
		byteSendData[11] = YzzsCommonUtil.intTobyte(100); // 协议尾：“d”
		sendUnpackData(byteSendData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
		showLoading("正在获取手持机当前的频率值和功率值");
	}

	/**
	 * 定时器，超过定时时间自动取消 dialog 框
	 * 
	 * @param timeValue
	 *            定时时间
	 */
	private void startTimerCount(int timeValue) {
		mRunnable = new Runnable() {
			@Override
			public void run() {
				if (isReveivedData) {
					// 2 秒钟有收到数据，则停止计时
					handler.removeCallbacks(this);
					isReveivedData = false; // 标志位重新置 false
				} else {
					dismissLoading();
					ToastMsg(getActivity(), "连接超时，请重新操作");
				}
			}
		};
		handler.postDelayed(mRunnable, timeValue); // 开始 2 秒钟定时，2 秒后，执行 run 方法
	}
}
