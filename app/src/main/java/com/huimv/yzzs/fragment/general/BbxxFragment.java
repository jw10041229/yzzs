package com.huimv.yzzs.fragment.general;

import com.huimv.yzzs.R;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.GeneralCMDConstant;
import com.huimv.yzzs.constant.HkCMDConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * 版本信息
 * @author jiangwei
 */
public class BbxxFragment extends YzzsBaseFragment implements EventHandler,ConnectTimeoutListener,OnClickListener {
	private Button btn_readBbxx;
	private TextView tv_bbxx;
	// 固定位 --- 数据头长度 = 协议头 + 数据域头固定长度
	//private static final int GDW_LEN_HEAD = 8;
	// 固定位 --- 数据尾长度 = 校验位 + 协议尾
	//private static final int GDW_LEN_TAIL = 4;
	private SharePreferenceUtil mSpUtil;
	private int whichDev = -1;
	private boolean isTimeOut = true;
	private int whichDevReadCMD = -1;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_bbxx_fragment, rootView,false);
		initView(view);
		initData();
		return view;
	}
	
	private void initData() {
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		setConnectTimeoutListener(this);
		whichDev = getArguments().getInt("whichDev");
		tv_bbxx.setText(mSpUtil.getVersion());
		whichDevReadCMD = GeneralCMDConstant.READ_BBXX;
	}
	
	private void initView(View view) {
		tv_bbxx = (TextView) view.findViewById(R.id.tv_bbxx);
		btn_readBbxx = (Button) view.findViewById(R.id.btn_read);
		btn_readBbxx.setOnClickListener(this);
		
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
		case R.id.btn_read:
			if(YzzsApplication.isConnected) {
				showLoading("正在读取...");
				sendGetBaseCMD(whichDev, whichDevReadCMD);
				startTime(HkCMDConstant.SEND_HK_SAVE_CMD_TIMEOUT, "读取版本信息");
				isTimeOut = true;
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		}
		super.onClick(v);
	}

	@Override
	public void onMessage(byte[] message) {
		try {
			getReadBbxxResult(message);
		} catch (Exception e) {
			ToastMsg(getActivity(), "数据出错");
		}
	}


	/**
	 * 版本信息
	 * @param message
	 */
	private void getReadBbxxResult(byte[] message) {
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		if (cmd == GeneralCMDConstant.READ_BBXX) {//保存结果
			getReadResult(message);
		}
	}
	
	/**
	 * 获取读取到的版本结果
	 * @param message
	 */
	private void getReadResult(byte[] message) {
		if (message [5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if(message[6] == 0) {
				String msg = new String (message);
				int dataLenth = message[7];
				tv_bbxx.setText(msg.substring(8,8 + dataLenth));
				ToastMsg(getActivity(), "版本读取成功");
			} else {
				ToastMsg(getActivity(), "版本读取失败");
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