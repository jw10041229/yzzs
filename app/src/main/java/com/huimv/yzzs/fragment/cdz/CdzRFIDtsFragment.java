package com.huimv.yzzs.fragment.cdz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.huimv.yzzs.R;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.CdzCMDConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.YzzsCommonUtil;

/**
 * RFID调试
 * @author jiangwei
 *
 */
public class CdzRFIDtsFragment extends YzzsBaseFragment implements EventHandler,ConnectTimeoutListener{
	private final static String TAG = CdzRFIDtsFragment.class.getSimpleName();
	/**
	 * read_rfid按钮
	 */
	private Button btn_rfid_read;
	/**
	 * rfid值
	 */
	private TextView tv_rfid_value;
	/**
	 * 体温
	 */
	private TextView tv_temperature_value;
	/**
	 * 超时
	 */
	private boolean isTimeOut = true;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_flz_rfidts_fragment, rootView,false);
		initView(view);
		return view;
	}
	
	private void initView(View view) {
		btn_rfid_read = (Button) view.findViewById(R.id.btn_rfid_read);
		tv_rfid_value = (TextView) view.findViewById(R.id.tv_rfid_value);
		tv_temperature_value = (TextView) view.findViewById(R.id.tv_temperature_value);
		setConnectTimeoutListener(this);
		btn_rfid_read.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(YzzsApplication.isConnected) {
					isTimeOut = true;
					cleanData();
					showLoading("正在读取RFID...");
					sendGetBaseCMD(CdzCMDConstant.CDZ_SBBZ, CdzCMDConstant.CDZ_RFIDTS);
					startTime(5000, "读取RFID");
				} else {
					ToastMsg(getActivity(), getString(R.string.bluetooth_disconnected));
				}
			}
		});
	}

	private void cleanData() {
		tv_rfid_value.setText("");
		tv_temperature_value.setText("");
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
		if (message.length < 6) {
			return;
		}
		if(message[0] == CdzCMDConstant.CDZ_SBBZ) {//分栏站
			try {
				int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
				switch (cmd) {
				case CdzCMDConstant.CDZ_RFIDTS://耳标
					RFIDTsDataParsing(message);
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
	 * RFID数据解析
	 * @param message
	 */
	private void RFIDTsDataParsing(byte[] message) {
		if(message.length < 11) {
			tv_rfid_value.setText("未读取到RFID");
			tv_temperature_value.setText("未读取到体温");
			dismissLoading();
			isTimeOut = false;
		} else {
			if (message[5] == 0) {
				isTimeOut = false;
				dismissLoading();
				if (message[6] == 0) {
					StringBuilder dataSb = new StringBuilder();
					int RFIDLenth = message[7];//RFID长度
					int tempLenthLocation = message[7] + 8;//体温长度位置
					for (int i = 0; i < RFIDLenth; i++) {//20位RFID
						dataSb.append(message[i + 8]);
					}
					tv_rfid_value.setText(dataSb.toString());
					dataSb.delete(0, dataSb.length());
					int tempLenth = message [tempLenthLocation];
					for (int i = 0; i < tempLenth; i++) {
						dataSb.append(message[tempLenthLocation + i + 1]);
					}
					if (dataSb.length() == 0 || dataSb.length() !=6) {
						tv_temperature_value.setText("");
					} else {
						String symbol = dataSb.substring(0,1);
						String noSymbol = dataSb.delete(0, 1).toString();
						int IntPart = Integer.valueOf(noSymbol.substring(0,3));//整数部分
						String DeciPart = noSymbol.substring(3,5);//小数部分
						StringBuilder sb = new StringBuilder();
						if (symbol.equals("1")) {
							//正数
							sb.append(IntPart).append(".").append(DeciPart);
						} else {
							sb.append("-").append(IntPart).append(".").append(DeciPart);
						}
						tv_temperature_value.setText(sb.toString());
					}
				} else {
					tv_rfid_value.setText("RFID数据读取失败");
					tv_temperature_value.setText("体温数据读取失败");
				}
			} else {
				tv_rfid_value.setText("RFID数据接收异常");
				tv_temperature_value.setText("体温数据读取异常");
			}
		}
	}

	@Override
	public void Timeout(String content) {
		if(!isAdded()) {
			return;
		}
		if (!isTimeOut) {
			isTimeOut = false; // 标志位重置false
		} else {
			dismissLoading();
			ToastMsg(getActivity(), content + getString(R.string.connect_time_out));
		}
	}
}
