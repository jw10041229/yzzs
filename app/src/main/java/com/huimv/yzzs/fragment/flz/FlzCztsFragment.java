package com.huimv.yzzs.fragment.flz;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.FlzCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.YzzsCommonUtil;
/**
 * 称重调试
 * @author jiangwei
 *
 */
public class FlzCztsFragment extends YzzsBaseFragment implements OnClickListener, EventHandler,ConnectTimeoutListener{
	//private final static String TAG = FlzCztsFragment.class.getSimpleName();
	/**
	 * 超时
	 */
	private boolean isTimeOut = true;
	/**
	 * 读出重量
	 */
	private TextView tv_read_weight;
	/**
	 * 读出重量值
	 */
	private TextView tv_read_weight_value;
	/**
	 * 校准
	 */
	private TextView tv_calibrate;
	/**
	 * 校准值
	 */
	private EditText tv_calibrate_value;
	/**
	 * 设置校准参数
	 */
	private TextView tv_set_calibrate;
	/**
	 * 设置校准参数值
	 */
	private EditText tv_set_calibrate_value;
	/**
	 * 设置为空称
	 */
	private TextView tv_set_empty_scale;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_flz_czts_fragment, null);
		initView(view);
		return view;
	}
	
	private void initView(View view) {
		setConnectTimeoutListener(this);
		tv_read_weight = (TextView) view.findViewById(R.id.tv_read_weight);
		tv_read_weight_value = (TextView) view.findViewById(R.id.tv_read_weight_value);
		tv_calibrate = (TextView) view.findViewById(R.id.tv_calibrate);
		tv_calibrate_value = (EditText) view.findViewById(R.id.tv_calibrate_value);
		tv_read_weight = (TextView) view.findViewById(R.id.tv_read_weight);
		tv_set_calibrate = (TextView) view.findViewById(R.id.tv_set_calibrate);
		tv_set_calibrate_value = (EditText) view.findViewById(R.id.tv_set_calibrate_value);
		tv_set_empty_scale = (TextView) view.findViewById(R.id.tv_set_empty_scale);
		
		tv_read_weight.setOnClickListener(this);
		tv_calibrate.setOnClickListener(this);
		tv_set_calibrate.setOnClickListener(this);
		tv_set_empty_scale.setOnClickListener(this);
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
	}
	
	@Override
	public void onReady(boolean isReady) {
	}
	
	@Override
	public void onNetChange(boolean isNetConnected) {
	}

	
	@Override
	public void onClick(View v) {
		if(!YzzsApplication.isConnected) {
			ToastMsg(getActivity(), getString(R.string.disconnected));
			return;
		}
		isTimeOut = true;
		startTime(6000, "通信");
		switch (v.getId()) {
		case R.id.tv_read_weight:
			doReadWeight();
			break;
		case R.id.tv_calibrate:
			doCalibrate(tv_calibrate_value,3,"正在校准..." ,FlzCMDConstant.FLZ_JZ);
			break;
		case R.id.tv_set_calibrate:
			doCalibrate(tv_set_calibrate_value,4,"正在设置校准参数..." ,FlzCMDConstant.FLZ_SZJZCS);
			break;
		case R.id.tv_set_empty_scale:
			doSetEmptyScale();
			break; 
		default:
			break;
		}
		super.onClick(v);
	}

	/**
	 * 设置为空称
	 */
	private void doSetEmptyScale() {
		showLoading("正在设置空称");
		sendGetBaseCMD(FlzCMDConstant.FLZ_SBBZ, FlzCMDConstant.FLZ_SZWGC);
	}
	
	/**
	 * 校准tv_calibrate_value
	 */
	private void doCalibrate(EditText tv_calibrate_value,int lenth,String showName,int cmd) {
		String calibrate_value = tv_calibrate_value.getText().toString();
		if (CommonUtil.isEmpty(calibrate_value)) {
			tv_calibrate_value.setHint("校准砝码不能为空");
			tv_calibrate_value.setHintTextColor(ContextCompat.getColor(getActivity(),R.color.red));
			isTimeOut = false;
			return;
		}
		sendCalibrateCMD(showName, 
				YzzsCommonUtil.formatStringAdd0(tv_calibrate_value.getText().toString(), lenth, 1), 
				cmd);
	}
	
	/**
	 * 发送命令
	 * @param showName
	 * @param data
	 * @param cmd
	 */
	private void sendCalibrateCMD(String showName,String data,int cmd) {
		showLoading(showName);
		int et_value_lenth = data.length();
		int lenth = et_value_lenth + 12;
		byte [] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);//h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);//m
		byteData[2] = YzzsCommonUtil.intTobyte(FlzCMDConstant.FLZ_SBBZ);//设备区分标志
		byteData[3] = YzzsCommonUtil.intTobyte(cmd / 256);//命令高位
		byteData[4] = YzzsCommonUtil.intTobyte(cmd % 256);//命令低位
		byteData[5] = YzzsCommonUtil.intTobyte(lenth / 256);//长度低位
		byteData[6] = YzzsCommonUtil.intTobyte(lenth % 256);//长度高位
		byteData[7] = YzzsCommonUtil.intTobyte(et_value_lenth);//后面值得长度
		for (int i = 8; i < et_value_lenth + 8; i++) {
			byteData[i] = YzzsCommonUtil
					.intTobyte(Integer.parseInt(data.substring(i - 8, i - 7)));
		}
		byteData[8 + et_value_lenth] = 1;//校验位
		byteData[9 + et_value_lenth] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);//e
		byteData[10 + et_value_lenth] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//n
		byteData[11 + et_value_lenth] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
		sendUnpackData(byteData,XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}

	/**
	 * 读出重量
	 */
	private void doReadWeight() {
		showLoading("正在读取重量");
		tv_read_weight_value.setText("");
		sendGetBaseCMD(FlzCMDConstant.FLZ_SBBZ, FlzCMDConstant.FLZ_DCZL);
		
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
				case FlzCMDConstant.FLZ_DCZL://读出重量
					doReadWeightDataPasing(message);
					break;
				case FlzCMDConstant.FLZ_JZ://校准
					doActionJzPasing(message, "校准");
					break;
				case FlzCMDConstant.FLZ_SZJZCS://设置校准参数
					doActionDataPasing(message, "设置校准参数");
					break;
				case FlzCMDConstant.FLZ_SZWGC://设置为空称
					doActionDataPasing(message, "设置空称");
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
	 * 读出重量
	 * @param message
	 */
	private void doReadWeightDataPasing(byte[] message) {
		if(message.length > 11) {
			isTimeOut = false;
			dismissLoading();
			//0 0  6 0 （正为0.负为1） 11111
			if (message [5] == 0 && message [6] == 0) {//读出重量成功
				StringBuilder sb = new StringBuilder();
				int symbol = message[8];
				for (int i = 9; i < message [7] + 8 ; i++) {
					sb.append(message[i]);
				}
				String data = String.valueOf(Double.parseDouble(sb.toString())/100);
				if (symbol == 1) {//如果是负的，怎加个“-”
					data = "-" + data;
				}
				tv_read_weight_value.setText(data);
			} else {
				tv_read_weight_value.setText("重量读取失败");
			}
		}
	}

	/**
	 * 动作返回数据
	 * @param message
	 * @param itemName
	 */
	private void doActionDataPasing(byte[] message,String itemName) {
		if (message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if( message[6] == 0) {
				ToastMsg(getActivity(), itemName + "成功");
			} else {
				ToastMsg(getActivity(), itemName + "失败");
			}
		}
	}
	/**
	 * 动作返回数据
	 * @param message
	 * @param itemName
	 */
	private void doActionJzPasing(byte[] message,String itemName) {
		if (message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if( message[6] == 0) {
				ToastMsg(getActivity(), "校准值已下发，请读取体重进行确认");
			} else {
				ToastMsg(getActivity(), itemName + "失败");
			}
		}
	}
	@Override
	public void Timeout(String content) {
		if(!isAdded()) {
			return;
		}
		if (!isTimeOut) {
			// N 秒钟有收到数据，则停止计时
			isTimeOut = false; // 标志位重新置false
		} else {
			dismissLoading();
			ToastMsg(getActivity(), content + getString(R.string.connect_time_out));
		}
	}
}
