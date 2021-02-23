package com.huimv.yzzs.fragment.lc;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.HkCMDConstant;
import com.huimv.yzzs.constant.LcCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.wheel.DateTimePickDialogUtil;
import com.huimv.yzzs.util.wheel.DateTimePickDialogUtil.OnTimeClickListener;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.huimv.yzzs.util.wheel.LcTitleWheelUtil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;
/**
 *时间校准
 * @author jiangwei
 *
 */
public class LcSjjzFragment extends YzzsBaseFragment implements EventHandler, OnClickListener,OnTimeClickListener,LcTitleWheelUtil.OnTitleConfirmClickListener,ConnectTimeoutListener{
	private boolean isTimeOut = true;
	private SharePreferenceUtil mSpUtil;
	private String[] items;
	private LcTitleWheelUtil mLc_titleWheelUtil;
	private TextView tv_title;
	private TextView tv_set_sjjz_value;
	private CheckedTextView ctv_sjjz_pack;
	private Button btn_save_time;
	private Button btn_read_time;
	/**
	 * 设备序号：默认为1号料槽
	 */
	private int devNumber = 1;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_lc_sjjz_fragment, null);
		initView(view);
		initListener();
		initData();
		cleanData();
		return view;
	}
	
	private void initData() {
		setConnectTimeoutListener(this);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		int ziLcNum = Integer.valueOf(mSpUtil.getZiLcNum());
		if (ziLcNum == -1 || ziLcNum <= 0) {
			ToastMsg(getActivity(), "数据获取出错");
			return;
		}
		items = new String[ziLcNum];
		for (int i = 0; i < ziLcNum; i++) {
			items[i] = (i+1) + "号料槽";
		}
		mLc_titleWheelUtil = new LcTitleWheelUtil(getActivity(), "请选择料槽", items, LcSjjzFragment.this);
		tv_title.setText("1号料槽");
	}

	private void initListener() {
		tv_title.setOnClickListener(this);
		tv_set_sjjz_value.setOnClickListener(this);
		btn_read_time.setOnClickListener(this);
		btn_save_time.setOnClickListener(this);
		ctv_sjjz_pack.setOnClickListener(this);
	}

	private void initView(View view) {
		tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_set_sjjz_value = (TextView) view.findViewById(R.id.tv_set_sjjz_value);
		ctv_sjjz_pack = (CheckedTextView) view.findViewById(R.id.ctv_sjjz_pack);
		btn_save_time = (Button) view.findViewById(R.id.btn_save_time);
		btn_read_time = (Button) view.findViewById(R.id.btn_read_time);
	}
	private void cleanData () {
		tv_set_sjjz_value.setText("N/A");
		ctv_sjjz_pack.setChecked(false);
	}
	@Override
	public void onPause() {
		MessageReceiver.ehList.remove(this);
		super.onPause();
	}

	@Override
	public void onResume() {
		MessageReceiver.ehList.add(this);
		devNumber = 1;
		doReadData();
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			mLc_titleWheelUtil.showDialog();
			break;
		case R.id.tv_set_sjjz_value:
			new DateTimePickDialogUtil(getActivity(), "",0,LcSjjzFragment.this).dateTimePicKDialog(0);
			break;
		case R.id.ctv_sjjz_pack:
			ctv_sjjz_pack.toggle();
			break;
		case R.id.btn_read_time:
			doReadData();
			break;
		case R.id.btn_save_time:
			if (checkSjTimeData()) {
				doSaveData();
			}
			break;
		}
		super.onClick(v);
	}

	private void doSaveData() {
		if (YzzsApplication.isConnected) {
			isTimeOut = true;
			startTime(HkCMDConstant.LOGIN_TIME, "保存");
			showLoading("正在保存数据...");
			sendSaveTimeCMD(LcCMDConstant.LC_SAVE_SJJZ);
		}
	}

	private void sendSaveTimeCMD(int cmd) {
		int isPack = ctv_sjjz_pack.isChecked() ? 0 : 1;
		String time1 = tv_set_sjjz_value.getText().toString().trim();
		String time2 = time1.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
		int dataLenth = time2.length();
		byte [] message = new byte[13 + dataLenth + 1 + 1];
		int lenth = message.length;
		message[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);//h
		message[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);//m
		message[2] = LcCMDConstant.LC_SBBZ;//设备区分标志
		message[3] = YzzsCommonUtil.intTobyte(cmd / 256);//命令高位
		message[4] = YzzsCommonUtil.intTobyte(cmd % 256);//命令低位
		message[5] = YzzsCommonUtil.intTobyte(lenth / 256);//长度低位
		message[6] = YzzsCommonUtil.intTobyte(lenth % 256);//长度高位
		message[7] = YzzsCommonUtil.intTobyte(3);//长度
		message[8] = YzzsCommonUtil.intTobyte(devNumber);//几号料槽
		message[9] = YzzsCommonUtil.intTobyte(isPack);//是否批处理
		message[10] = YzzsCommonUtil.intTobyte(dataLenth);//是否批处理
		for (int i = 11; i < dataLenth + 11; i++) {
			message[i] = YzzsCommonUtil
					.intTobyte(Integer.parseInt((time2).substring(i - 11, i - 10)));
		}
		message[lenth - 4] = 1;//校验位
		message[lenth - 3] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);//e
		message[lenth - 2] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//n
		message[lenth - 1] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
		sendUnpackData(message,XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}

	private boolean checkSjTimeData() {
		String time = tv_set_sjjz_value.getText().toString().trim();
		if (CommonUtil.isEmpty(time) || time.equals(getResources().getString(R.string.lc_click_select_time))) {
			ToastMsg(getActivity(), "设置时间不能为空");
			return false;
		}
		return true;
	}

	private void doReadData() {
		if (YzzsApplication.isConnected) {
			isTimeOut = true;
			startTime(HkCMDConstant.LOGIN_TIME, "读取");
			showLoading("正在读取数据...");
			cleanData();
			sendGetDataCMD(LcCMDConstant.LC_SBBZ, LcCMDConstant.LC_READ_SJJZ);
		}
	}

	/**
	 * 获取数据
	 * @param dev
	 * @param cmd
	 */
	private void sendGetDataCMD(int dev, int cmd) {
		int lenth = 13;
		final byte[] message = new byte[lenth];
		message[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);// h
		message[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);// m
		message[2] = YzzsCommonUtil.intTobyte(dev);
		message[3] = YzzsCommonUtil.intTobyte(cmd / 256);// 命令高位
		message[4] = YzzsCommonUtil.intTobyte(cmd % 256);// 命令低位
		message[5] = YzzsCommonUtil.intTobyte(lenth / 256);// 长度低位
		message[6] = YzzsCommonUtil.intTobyte(lenth % 256);// 长度高位
		message[7] = 1;// 后面值得长度
		message[8] = YzzsCommonUtil.intTobyte(devNumber);//几号设备
		message[9] = 1;// 校验位
		message[10] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);// e
		message[11] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);// n
		message[12] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);// d
		sendUnpackData(message, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}
	
	@Override
	public void onTime(String time,int itemPosition) {
		tv_set_sjjz_value.setText(time);
	}

	@Override
	public void onMessage(byte[] message) {
		getJoiningPack(message);
	}

	private void getJoiningPack(byte[] message) {
		if (message.length <= 6) {
			return;
		}
		if(message[0] == LcCMDConstant.LC_SBBZ) {//料槽
			try {
				int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
				if (cmd == LcCMDConstant.LC_SAVE_SJJZ) {
					getSaveSjjzResult(message);
				}
				if (cmd == LcCMDConstant.LC_READ_SJJZ) {
					getReadSjjzResult(message);
				}
			} catch (Exception e) {
				dismissLoading();
				ToastMsg(getActivity(), "数据异常");
			}
		}
	
	}

	private void getSaveSjjzResult(byte[] message) {
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
	/**
	 * 读取系统时间
	 * @param message
	 */
	private void getReadSjjzResult(byte[] message) {
		if(message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if(message[6] == 0) {
				ToastMsg(getActivity(),   "读取成功");
				devNumber = message[7];
				tv_title.setText(devNumber + "号料槽");
				ctv_sjjz_pack.setChecked(message[8] == 0);
				int lenth = message [9];
				StringBuilder sb = new StringBuilder();
				for (int i = 10; i < lenth + 10; i++) {
					sb.append(message [i]);
				}
				sb.insert(4, "-");
				sb.insert(7, "-");
				sb.insert(10, " ");
				sb.insert(13, ":");
				tv_set_sjjz_value.setText(sb.toString());
			} else {
				ToastMsg(getActivity(), "读取失败");
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
	public void onTitleConfirm(int position) {
		devNumber = position + 1;
		tv_title.setText(items[position]);
		doReadData();
	}
}
