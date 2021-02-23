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
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.huimv.yzzs.util.wheel.LcDjSelectWheelUtil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * 默认挡位设置
 * @author jiangwei
 */
public class HkDefalutGearSetyxcsFragment extends YzzsBaseFragment implements EventHandler ,ConnectTimeoutListener,LcDjSelectWheelUtil.OnDjConfirmClickListener {
	/**
	 * 按钮
	 */
	private Button btn_save,btn_read;
	private TextView tv_default_gear_value;
	private boolean isTimeOut = true;
	private LcDjSelectWheelUtil mLc_djSelectWheelUtil;
	private SharePreferenceUtil mSpUtil;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_hk_default_gear_set_fragment, null);
		initView(view);
		initData();
		initListener();
		return view;
	}

	private void initListener() {
		setConnectTimeoutListener(this);
		btn_save.setOnClickListener(this);
		btn_read.setOnClickListener(this);
		tv_default_gear_value.setOnClickListener(this);
	}

	private void initData() {
		//doReadTemp();
	}

	private void initView(View view) {
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		btn_save = (Button) view.findViewById(R.id.btn_save);
		btn_read = (Button) view.findViewById(R.id.btn_read);
		tv_default_gear_value = (TextView) view.findViewById(R.id.tv_default_gear_value);
		String items[] = new String[5];
		for (int i = 0; i < items.length; i++) {
			items[i] = (i+1) + "档";
		}
		mLc_djSelectWheelUtil = new LcDjSelectWheelUtil(getActivity(), "默认档位", items, HkDefalutGearSetyxcsFragment.this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_read:
			if(YzzsApplication.isConnected) {
				doReadGear();
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		case R.id.btn_save:
			if(YzzsApplication.isConnected) {
				if (checkGearData()) {
					doSaveGear();
				}
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		case R.id.tv_default_gear_value:
			mLc_djSelectWheelUtil.showDialog(0, "默认档位");
			break;
		default:
			break;
		}
		super.onClick(v);
	}
	
	private boolean checkGearData() {
		boolean isOk;
		String gearValue = tv_default_gear_value.getText().toString().trim();
		if (CommonUtil.isEmpty(gearValue)) {
			isOk = false;
			ToastMsg(getActivity(), "档位不能为空");
		} else {
			isOk = true;
		}
		return isOk;
	}

	/**
	 * 读取温度
	 */
	private void doReadGear() {
		if (YzzsApplication.isConnected) {
			isTimeOut = true;
			startTime(HkCMDConstant.LOGIN_TIME, "读取");
			showLoading("正在读取默认档位..");
			sendGetBaseCMD(HkCMDConstant.HK_SBBZ, HkCMDConstant.READ_DEFALUT_GEAR);
		}
	}
	/**
	 * 保存温度
	 */
	private void doSaveGear() {
		if (YzzsApplication.isConnected) {
			isTimeOut = true;
			startTime(HkCMDConstant.LOGIN_TIME, "保存");
			sendSaveGearCMD();
		}
	}

	/**
	 * 发送密码验证命令
	 */
	private void sendSaveGearCMD() {
		showLoading("正在保存数据...");
		String gearValue = tv_default_gear_value.getText().toString().trim().replace("档", "");
		int grarValueInt = Integer.valueOf(gearValue);
		int lenth = 12 + gearValue.length() ;
		int cmd =HkCMDConstant.SAVE_DEFALUT_GEAR;
		byte [] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);//h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);//m
		byteData[2] = HkCMDConstant.HK_SBBZ;//设备区分标志
		byteData[3] = YzzsCommonUtil.intTobyte(cmd / 256);//命令高位
		byteData[4] = YzzsCommonUtil.intTobyte(cmd % 256);//命令低位
		byteData[5] = YzzsCommonUtil.intTobyte(lenth / 256);//长度低位
		byteData[6] = YzzsCommonUtil.intTobyte(lenth % 256);//长度高位
		byteData[7] = YzzsCommonUtil.intTobyte(1);//后面值得长度
		byteData[8] = YzzsCommonUtil.intTobyte(grarValueInt);//后面值得长度
		byteData[9] = 1;//校验位
		byteData[10] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);//e
		byteData[11] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//n
		byteData[12] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
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
				getSaveResult(message);
				getReadGearResult(message);
			} catch (Exception e) {
				dismissLoading();
				ToastMsg(getActivity(), "数据异常");
			}
		}
	}
	/**
	 * 读取默认配置档位
	 * @param message
	 */
	private void getReadGearResult(byte[] message) {
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		if (cmd == HkCMDConstant.READ_DEFALUT_GEAR) {
			if(message[5] == 0) {
				dismissLoading();
				isTimeOut = false;
				if(message[6] == 0) {
					if (message[8] == 0) {
						ToastMsg(getActivity(), "当前设备没有配置默认档位,请手动配置");
					} else {
						tv_default_gear_value.setText(message[8] + "档");
						mSpUtil.setHkMrdw(message[8] + "");
					}
				} else {
					ToastMsg(getActivity(), "默认档位读取失败");
				}
			}
		}
	}

	/**
	 * 保存返回结果
	 * @param message
	 */
	private void getSaveResult(byte[] message) {
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		if (cmd == HkCMDConstant.SAVE_DEFALUT_GEAR) {
			if(message[5] == 0) {
				dismissLoading();
				isTimeOut = false;
				if(message[6] == 0) {
					String gearValue = tv_default_gear_value.getText().toString().trim().replace("档", "");
					mSpUtil.setHkMrdw(gearValue);
					ToastMsg(getActivity(), "保存成功");
				} else {
					ToastMsg(getActivity(), "保存失败");
				}
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

	@Override
	public void onDjConfirm(int position, int i) {
		tv_default_gear_value.setText((position + 1) + "档");
	}
}