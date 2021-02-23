package com.huimv.yzzs.fragment.flz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huimv.yzzs.R;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.FlzCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;
/**
 * 硬件设置
 * @author jiangwei
 *
 */
public class FlzYjszFragment extends YzzsBaseFragment implements EventHandler,OnClickListener,ConnectTimeoutListener{
	//private final static String TAG = FlzYjszFragment.class.getSimpleName();
	private SharePreferenceUtil mSpUtil;
	/**
	 * 超时
	 */
	private boolean isTimeOut = false;
	
	/**
	 * 直角方向设置
	 */
	private TextView tv_orthogonal_direction_set;
	/**
	 * 直角方向设置值
	 */
	private TextView tv_orthogonal_direction_set_value;
	/**
	 * 默认通道设置
	 */
	private TextView tv_default_channel_set;
	/**
	 * 默认通道设置值
	 */
	private TextView tv_default_channel_set_value;
	/**
	 * 左右通道
	 */
	private static final String LEFT_CHANNEL = "左通道";
	private static final String RIGHT_CHANNEL = "右通道";
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_flz_yjts_fragment, null);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		initView(view);
		intiData();
		return view;
	}
	
	/**
	 * 初始化数据
	 */
	private void intiData() {
		setConnectTimeoutListener(this);
		if(mSpUtil.getZjtdfx().equals(FlzCMDConstant.LEFT_CHANNEL_SP)) {
			tv_orthogonal_direction_set_value.setText(LEFT_CHANNEL);
		} else {
			tv_orthogonal_direction_set_value.setText(RIGHT_CHANNEL);
		}
		if(mSpUtil.getMrtdfx().equals(FlzCMDConstant.LEFT_CHANNEL_SP)) {
			tv_default_channel_set_value.setText(LEFT_CHANNEL);
		} else {
			tv_default_channel_set_value.setText(RIGHT_CHANNEL);
		}
	}

	private void initView(View view) {
		tv_orthogonal_direction_set = (TextView) view.findViewById(R.id.tv_orthogonal_direction_set);
		tv_orthogonal_direction_set_value = (TextView) view.findViewById(R.id.tv_orthogonal_direction_set_value);
		tv_default_channel_set = (TextView) view.findViewById(R.id.tv_default_channel_set);
		tv_default_channel_set_value = (TextView) view.findViewById(R.id.tv_default_channel_set_value);
		
		tv_default_channel_set_value.setOnClickListener(this);
		tv_orthogonal_direction_set_value.setOnClickListener(this);
		tv_orthogonal_direction_set.setOnClickListener(this);
		tv_default_channel_set.setOnClickListener(this);
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
		if (message.length <= 6) {
			return;
		}
		if(message[0] == FlzCMDConstant.FLZ_SBBZ) {//分栏站
			try {
				int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
				switch (cmd) {
				case FlzCMDConstant.FLZ_ZJTDFXSZ://直角
					zjtdfxszDataParsing(message);
					break;
				case FlzCMDConstant.FLZ_MRTDFXSZ://默认
					mrtdfxszDataParsing(message);
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
	 * 默认通道
	 * @param message 
	 */
	private void mrtdfxszDataParsing(byte[] message) {
		if (message[5] == 0 ) {
			dismissLoading();
			isTimeOut = false;
			if(message[6] == 0) {
				if (tv_default_channel_set_value.getText().equals(LEFT_CHANNEL)) {
					mSpUtil.setMrtdfx(FlzCMDConstant.LEFT_CHANNEL_SP);
				} else {
					mSpUtil.setMrtdfx(FlzCMDConstant.RIGHT_CHANNEL_SP);
				}
				ToastMsg(getActivity(), "默认通道设置成功!");
			} else {
				ToastMsg(getActivity(), "默认通道设置失败!");
			}
		}
	}
	/**
	 * 直角通道
	 * @param message 
	 */
	private void zjtdfxszDataParsing(byte[] message) {
		if (message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if(message[6] == 0) {
			if (tv_orthogonal_direction_set_value.getText().equals(LEFT_CHANNEL)) {
				mSpUtil.setZjtdfx(FlzCMDConstant.LEFT_CHANNEL_SP);
			} else {
				mSpUtil.setZjtdfx(FlzCMDConstant.RIGHT_CHANNEL_SP);
			}
			ToastMsg(getActivity(), "直角通道设置成功!");
			} else {
			ToastMsg(getActivity(), "直角通道设置失败!");
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_orthogonal_direction_set:
			if(YzzsApplication.isConnected) {
				isTimeOut = true;
				doOrthogonalDirectionSet();
				startTime(XtAppConstant.SEND_CMD_TIMEOUT, "直角方向设置");
			} else {
				ToastMsg(getActivity(), getString(R.string.bluetooth_disconnected));
			}
			break;
		case R.id.tv_default_channel_set:
			if(YzzsApplication.isConnected) {
				isTimeOut = true;
				doDefaultChannelSet();
				startTime(XtAppConstant.SEND_CMD_TIMEOUT, "默认通道设置");
			} else {
				ToastMsg(getActivity(), getString(R.string.bluetooth_disconnected));
			}
			break;
		case R.id.tv_orthogonal_direction_set_value:
			changeDirection(tv_orthogonal_direction_set_value);
			break;
		case R.id.tv_default_channel_set_value:
			changeDirection(tv_default_channel_set_value);
			break;
		default:
			break;
		}
		super.onClick(v);
	}
	
	/**
	 *默认通道设置
	 */
	private void doDefaultChannelSet() {
		showLoading("正在设置默认通道...");
		if (tv_default_channel_set_value.getText().toString().equals(RIGHT_CHANNEL)) {
			sendCMD(FlzCMDConstant.FLZ_SBBZ, FlzCMDConstant.FLZ_MRTDFXSZ, 1);
		} else {
			sendCMD(FlzCMDConstant.FLZ_SBBZ, FlzCMDConstant.FLZ_MRTDFXSZ, 0);
		}
	}
	/**
	 * 直角方向设置
	 */
	private void doOrthogonalDirectionSet() {
		showLoading("正在设置直角通道...");
		if (tv_orthogonal_direction_set_value.getText().toString().equals(RIGHT_CHANNEL)) {
			sendCMD(FlzCMDConstant.FLZ_SBBZ, FlzCMDConstant.FLZ_ZJTDFXSZ, 1);
		} else {
			sendCMD(FlzCMDConstant.FLZ_SBBZ, FlzCMDConstant.FLZ_ZJTDFXSZ, 0);
		}
	}
	/**
	 * 发送基础13个长度的命令
	 * @param dev
	 * @param cmd
	 */
	private void sendCMD (int dev,int cmd,int value) {
		int lenth = 13;
		final byte [] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);//h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);//m
		byteData[2] = YzzsCommonUtil.intTobyte(dev);
		byteData[3] = YzzsCommonUtil.intTobyte(cmd / 256);//命令高位
		byteData[4] = YzzsCommonUtil.intTobyte(cmd % 256);//命令低位
		byteData[5] = YzzsCommonUtil.intTobyte(lenth / 256);//长度低位
		byteData[6] = YzzsCommonUtil.intTobyte(lenth % 256);//长度高位
		byteData[7] = 1;//后面值得长度
		byteData[8] = YzzsCommonUtil.intTobyte(value);
		byteData[9] = 1;//校验位
		byteData[10] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);//e
		byteData[11] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//n
		byteData[12] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
		sendUnpackData(byteData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}
	
	/**
	 * 转换通道
	 * @param mTextView
	 */
	private void changeDirection(TextView mTextView) {
		if (mTextView.getText().toString().equals(RIGHT_CHANNEL)) {
			mTextView.setText(LEFT_CHANNEL);
		} else if (mTextView.getText().toString().equals(LEFT_CHANNEL)) {
			mTextView.setText(RIGHT_CHANNEL);
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
