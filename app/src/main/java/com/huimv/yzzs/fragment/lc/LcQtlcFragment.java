package com.huimv.yzzs.fragment.lc;

import com.huimv.yzzs.R;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.FlzCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.YzzsCommonUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
/**
 * 群体料槽
 * @author jiangwei
 *
 */
public class LcQtlcFragment extends YzzsBaseFragment implements OnClickListener, EventHandler,ConnectTimeoutListener{
	private final static String TAG = LcSsxsFragment.class.getSimpleName();
	private boolean isTimeOut = true;
	/**
	 *故障刷新
	 */
	private ImageView iv_refresh_fault;
	/**
	 *参数刷新
	 */
	private ImageView iv_refresh;
	
	/**
	 * 时间值
	 */
	private TextView tv_time_value;
	/**
	 * 下料量
	 */
	private TextView tv_rate_value;
	/**
	 *水流量
	 */
	private TextView tv_water_flow_value;
	/**
	 * 触碰次数
	 */
	private TextView tv_touch_number_value;
	/**
	 * 故障
	 */
	private TextView tv_fault;
	/**
	 * 故障ScrollView
	 */
	private ScrollView sv_fault;
	/**
	 * 图片旋转
	 */
	private Animation operatingAnim;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_flz_qtlc_fragment, null);
		initView(view);
		initData();
		return view;
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		setConnectTimeoutListener(this);
		operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.img_rotate);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
	}
	/**
	 * 初始化view
	 * @param view
	 */
	private void initView(View view) {
		iv_refresh_fault = (ImageView) view.findViewById(R.id.iv_refresh_fault);
		iv_refresh_fault.setOnClickListener(this);
		tv_time_value = (TextView) view.findViewById(R.id.tv_time_value);
		tv_rate_value = (TextView) view.findViewById(R.id.tv_rate_value);
		tv_water_flow_value = (TextView) view.findViewById(R.id.tv_water_flow_value);
		tv_touch_number_value = (TextView) view.findViewById(R.id.tv_touch_number_value);
		tv_fault = (TextView) view.findViewById(R.id.tv_fault);
		sv_fault = (ScrollView) view.findViewById(R.id.sv_fault);
		iv_refresh = (ImageView) view.findViewById(R.id.iv_refresh);
		iv_refresh.setOnClickListener(this);
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "onResume()");
		MessageReceiver.ehList.add(this);
		timeDataParsing(new byte[] {3,0,29,0,16,0,8,0,3,3,4,2,1,1,2});
		PamDataParsing(new byte[] {3,0,29,0,16,0,4,4,4,4,4},tv_rate_value,"下料量");
		PamDataParsing(new byte[] {3,0,29,0,16,0,4,1,1,1,1},tv_water_flow_value,"水流量");
		PamDataParsing(new byte[] {3,0,29,0,16,0,4,3,3,3,3},tv_touch_number_value,"触碰次数");
		gzztDataParsing(new byte[] {3,0,28,0,16,0,23,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1});
		super.onResume();
	}
	
	@Override
	public void onPause() {
		Log.d(TAG, "onPause()");
		MessageReceiver.ehList.remove(this);
		super.onPause();
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
	public void onMessage(byte[] message) {
		receivePack(message);
	}
	
	@Override
	public void onNetChange(boolean isNetConnected) {
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_refresh_fault:
			if(YzzsApplication.isConnected) {
				sendRefreshCMD(iv_refresh_fault,FlzCMDConstant.GTLC_DEV_FZULT);
			} else {
				ToastMsg(getActivity(), getString(R.string.bluetooth_disconnected));
			}
			break;
		case R.id.iv_refresh:
			if(YzzsApplication.isConnected) {
				sendRefreshCMD(iv_refresh,FlzCMDConstant.GTLC_STATE_REFRESH);
				cleanTv();
			} else {
				ToastMsg(getActivity(), getString(R.string.bluetooth_disconnected));
			}
			break;
		default:
			break;
		}
		super.onClick(v);
	}
	
	/**
	 * 清除数值
	 */
	private void cleanTv() {
		tv_time_value.setText("N/A");
		tv_rate_value.setText("N/A");
		tv_water_flow_value.setText("N/A");
		tv_touch_number_value.setText("N/A");
	}

	/**
	 * 接收包
	 * @param message
	 */
	private void receivePack (byte[] message) {
		if (message.length < 6) {
			return;
		}
		if(message[0] == FlzCMDConstant.FLZ_SBBZ) {//分栏站
				int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
				switch (cmd) {
				case FlzCMDConstant.GTLC_TIME://时间
					timeDataParsing(message);
					break;
				case FlzCMDConstant.GTLC_RATE://下料量
					PamDataParsing(message,tv_rate_value,"下料");
					break;
				case FlzCMDConstant.GTLC_REMAININT_AMOUNT://剩余量
					PamDataParsing(message,tv_water_flow_value,"剩余量");
					break;
				case FlzCMDConstant.GTLC_FEED_AMOUNT://采食量
					PamDataParsing(message,tv_touch_number_value,"采食量");
					break;
				case FlzCMDConstant.GTLC_DEV_FZULT://故障状态
					gzztDataParsing(message);
					//gzztDataParsing(new byte[] {3,0,28,0,16,0,23,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1});
					break;
				default:
					break;
				}
		}
	}
	/**
	 * 时间
	 * @param message
	 */
	private void timeDataParsing(byte[] message) {
		if(message.length < 15) {
			tv_time_value.setText("时间数据异常");
			return;
		}
		if (message[5] == 0 && message[6] >= 8) {
			StringBuilder dataSb = new StringBuilder();
			for (int i = 7; i < 7 + message[6]; i++) {//8位时间
				dataSb.append(message[i]);
			}
			String startTime = dataSb.toString().substring(0,2) + ":" + dataSb.toString().trim().substring(2,4);
			String endTime = dataSb.toString().substring(4,6) + ":" + dataSb.toString().trim().substring(6,8);
			tv_time_value.setText(startTime + "-" + endTime);
		} else {
			tv_time_value.setText("时间数据接收异常");
		}
	}

	
	/**
	 * 故障信息
	 * @param message
	 */
	private void gzztDataParsing(byte[] message) {
		iv_refresh_fault.clearAnimation();
		if (message.length < 29) {
			tv_fault.setText("故障数据异常");
			return;
		}
		boolean hasFault = false;
		String[] faultData = getActivity().getResources().getStringArray(R.array.lc_guzhang_zhuangtai_item_array);
		StringBuilder faultDataSb = new StringBuilder();
		String dataBottom = "\n";
		int min = message[6] < faultData.length ? message[6] : faultData.length;//取两者中小的
		for (int i = 0; i < min; i++) {
			if (message[7 + i] == 1) {
				if (i == faultData.length -1 ) {
					dataBottom = "";
				}
				faultDataSb.append(faultData [i]).append(dataBottom);
				hasFault = true;
			} else {
				hasFault = false;
			}
		}
		sv_fault.post(new Runnable() {
			public void run() {
				sv_fault.fullScroll(ScrollView.FOCUS_FORWARD);
			}
		});
		if (!hasFault){
			tv_fault.setText("设备无故障");
		} else {
			tv_fault.setText(faultDataSb.toString());
		}
	}
	/**
	 * 下料量/水流量/触碰次数
	 * @param message
	 */
	private void PamDataParsing(byte[] message,TextView tv,String msg) {
		if (message.length < 10) {
			tv.setText( msg + "数据异常");
			return;
		}
		if (message[5] == 0 && message[6] >= 4) {
			StringBuilder dataSb = new StringBuilder();
			for (int i = 7; i < 7 + message[6]; i++) {//数值
				dataSb.append(message[i]);
			}
			tv.setText(Double.valueOf(dataSb.toString())/10 + "");
		} else {
			tv.setText(msg + "数据接收异常");
		}
	}

	/**
	 * 发送刷新命令
	 */
	private void sendRefreshCMD(ImageView iv,int cmd) {
		if(iv.getAnimation() !=null && iv.getAnimation().hasStarted()) {
			iv.clearAnimation();
			isTimeOut = false;
			cancelTime();
		} else {
			iv.startAnimation(operatingAnim);
			sendGetBaseCMD(FlzCMDConstant.FLZ_SBBZ, cmd);
			isTimeOut = true;
			startTime(XtAppConstant.SEND_CMD_TIMEOUT, "刷新");
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
			if(iv_refresh_fault.getAnimation() !=null && iv_refresh_fault.getAnimation().hasStarted()) {
				iv_refresh_fault.clearAnimation();
			}
			if(iv_refresh.getAnimation() !=null && iv_refresh.getAnimation().hasStarted()) {
				iv_refresh.clearAnimation();
			}
			ToastMsg(getActivity(), content + getString(R.string.connect_time_out));
		}
	}
}
