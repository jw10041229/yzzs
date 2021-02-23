package com.huimv.yzzs.fragment.cdz;

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
import com.huimv.yzzs.constant.CdzCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.huimv.yzzs.util.wheel.LcDjSelectWheelUtil;
import com.orhanobut.logger.Logger;

import java.util.Arrays;

/**
 * 称重调试
 * @author jiangwei
 *
 */
public class CdzCztsFragment extends YzzsBaseFragment implements OnClickListener, EventHandler,ConnectTimeoutListener,LcDjSelectWheelUtil.OnDjConfirmClickListener {
	private final static String TAG = CdzCztsFragment.class.getSimpleName();
	/**
	 * 超时
	 */
	private boolean isTimeOut = true;
	/**
	 * 读出重量
	 */
	TextView tv_read_weight;
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
	private EditText et_calibrate_value;
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

	/**
	 *秤类型
     */
	private LcDjSelectWheelUtil mLcDjSelectWheelUtil;

	/**
	 * 秤标题
     */
	private TextView tv_title;

	/**
	 * 秤类型postion
     */
	private int scaleTypePos;
	private String scaleType [];
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_cdz_czts_fragment, rootView,false);
		initView(view);
		initData();
		return view;
	}
	private void initData() {
		scaleTypePos = 0;
		scaleType = getResources().getStringArray(R.array.cdz_scale_type_array);
		mLcDjSelectWheelUtil = new LcDjSelectWheelUtil(getActivity(),"请选择秤类型",scaleType,this);
	}

	private void initView(View view) {
		setConnectTimeoutListener(this);
		tv_read_weight = (TextView) view.findViewById(R.id.tv_read_weight);
		tv_read_weight_value = (TextView) view.findViewById(R.id.tv_read_weight_value);
		tv_calibrate = (TextView) view.findViewById(R.id.tv_calibrate);
		et_calibrate_value = (EditText) view.findViewById(R.id.et_calibrate_value);
		tv_read_weight = (TextView) view.findViewById(R.id.tv_read_weight);
		tv_set_calibrate = (TextView) view.findViewById(R.id.tv_set_calibrate);
		tv_set_calibrate_value = (EditText) view.findViewById(R.id.tv_set_calibrate_value);
		tv_set_empty_scale = (TextView) view.findViewById(R.id.tv_set_empty_scale);
		tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_read_weight.setOnClickListener(this);
		tv_calibrate.setOnClickListener(this);
		tv_set_calibrate.setOnClickListener(this);
		tv_set_empty_scale.setOnClickListener(this);
		tv_title.setOnClickListener(this);
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
		if (v.getId() == R.id.tv_title) {
			mLcDjSelectWheelUtil.showDialog(0,"请选择秤类型");
			return;
		}
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
			doCalibrate(et_calibrate_value,2,"正在校准..." , CdzCMDConstant.CDZ_JZ);
			break;
		case R.id.tv_set_calibrate:
			//doCalibrate(tv_set_calibrate_value,4,"正在设置校准参数..." ,CdzCMDConstant.CDZ_SZJZCS);
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
		sendBaseSacleCMD(CdzCMDConstant.CDZ_SBBZ, CdzCMDConstant.CDZ_SZWGC);
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
		int et_value_lenth = data.length() ;
		int lenth = et_value_lenth + 12 + 1;
		byte [] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);//h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);//m
		byteData[2] = YzzsCommonUtil.intTobyte(CdzCMDConstant.CDZ_SBBZ);//设备区分标志
		byteData[3] = YzzsCommonUtil.intTobyte(cmd / 256);//命令高位
		byteData[4] = YzzsCommonUtil.intTobyte(cmd % 256);//命令低位
		byteData[5] = YzzsCommonUtil.intTobyte(lenth / 256);//长度低位
		byteData[6] = YzzsCommonUtil.intTobyte(lenth % 256);//长度高位
		byteData[7] = YzzsCommonUtil.intTobyte(et_value_lenth + 1);//后面值得长度
		byteData[8] = YzzsCommonUtil.intTobyte(scaleTypePos);//秤类型
		for (int i = 9; i < et_value_lenth + 9; i++) {
			byteData[i] = YzzsCommonUtil
					.intTobyte(Integer.parseInt(data.substring(i - 9, i - 8)));
		}
		byteData[9 + et_value_lenth] = 1;//校验位
		byteData[10 + et_value_lenth] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);//e
		byteData[11 + et_value_lenth] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//n
		byteData[12 + et_value_lenth] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
		sendUnpackData(byteData,XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}

	/**
	 * 读出重量
	 */
	private void doReadWeight() {
		showLoading("正在读取重量");
		tv_read_weight_value.setText("");
		sendBaseSacleCMD(CdzCMDConstant.CDZ_SBBZ, CdzCMDConstant.CDZ_DCZL);
	}

	/**
	 * 获取读取重量
	 * @param dev
	 * @param cmd
     */
	private void sendBaseSacleCMD(int dev, int cmd) {
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
		byteData[8] = YzzsCommonUtil.intTobyte(scaleTypePos);// 秤类型
		byteData[9] = 1;// 校验位
		byteData[10] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);// e
		byteData[11] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);// n
		byteData[12] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);// d
		sendUnpackData(byteData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
		Logger.d(TAG + "send",Arrays.toString(byteData));
	}

	@Override
	public void onMessage(byte[] message) {
		receivePack(message);
	}

	private void receivePack(byte[] message) {
		if (message.length <= 6) {
			return;
		}
		if(message[0] == CdzCMDConstant.CDZ_SBBZ) {//测定站
			try {
				int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
				switch (cmd) {
				case CdzCMDConstant.CDZ_DCZL://读出重量
					doReadWeightDataPasing(message);
					break;
				case CdzCMDConstant.CDZ_JZ://校准
					doActionJzPasing(message, "校准");
					break;
				case CdzCMDConstant.CDZ_SZWGC://设置为空称
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
			Logger.d("读出重量",Arrays.toString(message));
			//0 0  6 0 （正为0.负为1） 11111
			if (message [5] == 0 && message [6] == 0) {//读出重量成功
				int scaleType = message[7];//秤类型
				StringBuilder sb = new StringBuilder();
				int symbol = message[9];
				for (int i = 10; i < message [8] + 9 ; i++) {
					sb.append(message[i]);
				}
				String data = String.valueOf(Double.parseDouble(sb.toString())/100);
				if (symbol == 1) {//如果是负的，怎加个“-”
					data = "-" + data;
				}
				tv_read_weight_value.setText(data);
				ToastMsg(getActivity(),"读取成功");
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

	/**
	 * 秤类型
	 * @param position
	 * @param i
     */
	@Override
	public void onDjConfirm(int position, int i) {
		scaleTypePos = position == 0 ? 0 : 2;//去除料斗秤，0 ，1，2 去除类型为1的料斗秤
		tv_title.setText(getResources().getStringArray(R.array.cdz_scale_type_array)[position]);
		tv_read_weight_value.setText("");
	}
}
