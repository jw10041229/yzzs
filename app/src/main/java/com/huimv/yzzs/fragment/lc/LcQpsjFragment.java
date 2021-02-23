package com.huimv.yzzs.fragment.lc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

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
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.huimv.yzzs.util.wheel.LcTitleWheelUtil;
/**
 * 清盘时间
 * @author jiangwei
 */
public class LcQpsjFragment extends YzzsBaseFragment implements EventHandler,
			OnClickListener,DateTimePickDialogUtil.OnTimeClickListener,LcTitleWheelUtil.OnTitleConfirmClickListener,ConnectTimeoutListener{
	private boolean isTimeOut = true;
	private SharePreferenceUtil mSpUtil;
	private String[] items;
	private LcTitleWheelUtil mLc_titleWheelUtil;
	private TextView tv_title;
	/**
	 * 设备序号：默认为1号料槽
	 */
	private int devNumber = 1;
	private TextView tv_set_start_time_value1;
	private TextView tv_set_start_time_value2;
	private TextView tv_set_start_time_value3;
	private TextView tv_set_start_time_value4;
	
	private EditText et_set_continue_time_value1;
	private EditText et_set_continue_time_value2;
	private EditText et_set_continue_time_value3;
	private EditText et_set_continue_time_value4;
	
	private CheckedTextView ctv_qpsj_pack;
	
	private Button btn_save_time;
	private Button btn_read_time;
	
	private TextView mTextView [];
	private EditText mEditText [];
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_lc_qpsj_fragment, null);
		initView(view);
		initListener();
		initData();
		return view;
	}
	
	private void initData() {
		mTextView  = new TextView [] {tv_set_start_time_value1,tv_set_start_time_value2,
				tv_set_start_time_value3,tv_set_start_time_value4};
		mEditText = new EditText [] {et_set_continue_time_value1,et_set_continue_time_value2,
				et_set_continue_time_value3,et_set_continue_time_value4};
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
		mLc_titleWheelUtil = new LcTitleWheelUtil(getActivity(), "请选择料槽", items, LcQpsjFragment.this);
		tv_title.setText("1号料槽");
		
	}

	private void initListener() {
		tv_title.setOnClickListener(this);
		tv_set_start_time_value1.setOnClickListener(this);
		tv_set_start_time_value2.setOnClickListener(this);
		tv_set_start_time_value3.setOnClickListener(this);
		tv_set_start_time_value4.setOnClickListener(this);
		
		ctv_qpsj_pack.setOnClickListener(this);
		
		btn_read_time.setOnClickListener(this);
		btn_save_time.setOnClickListener(this);
	}

	private void initView(View view) {
		tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_set_start_time_value1 = (TextView) view.findViewById(R.id.tv_set_start_time_value1);
		tv_set_start_time_value2 = (TextView) view.findViewById(R.id.tv_set_start_time_value2);
		tv_set_start_time_value3 = (TextView) view.findViewById(R.id.tv_set_start_time_value3);
		tv_set_start_time_value4 = (TextView) view.findViewById(R.id.tv_set_start_time_value4);
		
		ctv_qpsj_pack = (CheckedTextView) view.findViewById(R.id.ctv_qpsj_pack);
		
		btn_read_time = (Button) view.findViewById(R.id.btn_read_time);
		btn_save_time = (Button) view.findViewById(R.id.btn_save_time);
		
		et_set_continue_time_value1 = (EditText) view.findViewById(R.id.et_set_continue_time_value1);
		et_set_continue_time_value2 = (EditText) view.findViewById(R.id.et_set_continue_time_value2);
		et_set_continue_time_value3 = (EditText) view.findViewById(R.id.et_set_continue_time_value3);
		et_set_continue_time_value4 = (EditText) view.findViewById(R.id.et_set_continue_time_value4);
	}
	/**
	 * 清除数据
	 */
	private void cleanData() {
		tv_set_start_time_value1.setText("");
		tv_set_start_time_value2.setText("");
		tv_set_start_time_value3.setText("");
		tv_set_start_time_value4.setText("");
		
		et_set_continue_time_value1.setText("");
		et_set_continue_time_value2.setText("");
		et_set_continue_time_value3.setText("");
		et_set_continue_time_value4.setText("");
		
		ctv_qpsj_pack.setChecked(false);
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
		cleanData();
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
		case R.id.btn_read_time:
			doReadData();
			break;
		case R.id.btn_save_time:
			if (checkContinueTimeData() && checkStartTime()) {
				doSaveData();
			}
			break;
		case R.id.ctv_qpsj_pack:
			ctv_qpsj_pack.toggle();
			break;
		case R.id.tv_set_start_time_value1:
			new DateTimePickDialogUtil(getActivity(), "",1,LcQpsjFragment.this).dateTimePicKDialog(1);
			break;
		case R.id.tv_set_start_time_value2:
			new DateTimePickDialogUtil(getActivity(), "",1,LcQpsjFragment.this).dateTimePicKDialog(2);
			break;
		case R.id.tv_set_start_time_value3:
			new DateTimePickDialogUtil(getActivity(), "",1,LcQpsjFragment.this).dateTimePicKDialog(3);
			break;
		case R.id.tv_set_start_time_value4:
			new DateTimePickDialogUtil(getActivity(), "",1,LcQpsjFragment.this).dateTimePicKDialog(4);
			break;
		case R.id.tv_title:
			mLc_titleWheelUtil.showDialog();
			break;
		}
		super.onClick(v);
	}
	/**
	 * 校验开始时间
	 * @return
	 */
	private boolean checkStartTime() {
		String startTime1 = tv_set_start_time_value1.getText().toString().trim();
		String startTime2 = tv_set_start_time_value2.getText().toString().trim();
		String startTime3 = tv_set_start_time_value3.getText().toString().trim();
		String startTime4 = tv_set_start_time_value4.getText().toString().trim();
		if (CommonUtil.isEmpty(startTime1) || CommonUtil.isEmpty(startTime2) ||
				CommonUtil.isEmpty(startTime3) ||CommonUtil.isEmpty(startTime4)) {
			ToastMsg(getActivity(), "开始时间不能为空");
			return false;
		}
		return true;
	}

	private void doReadData() {
		if (YzzsApplication.isConnected) {
			isTimeOut = true;
			startTime(HkCMDConstant.LOGIN_TIME, "读取");
			showLoading("正在读取数据...");
			sendGetDataCMD(LcCMDConstant.LC_SBBZ, LcCMDConstant.LC_READ_QPSJ);
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
	private void doSaveData() {
		if (YzzsApplication.isConnected) {
			isTimeOut = true;
			startTime(HkCMDConstant.LOGIN_TIME, "保存");
			showLoading("正在保存数据...");
			sendSaveContinueTimeCMD(LcCMDConstant.LC_SAVE_QPSJ);
		}
	}

	private void sendSaveContinueTimeCMD(int cmd) {
		String slb_value1 = tv_set_start_time_value1.getText().toString().trim().replace(":", "");
		String slb_value2 = tv_set_start_time_value2.getText().toString().trim().replace(":", "");
		String slb_value3 = tv_set_start_time_value3.getText().toString().trim().replace(":", "");
		String slb_value4 = tv_set_start_time_value4.getText().toString().trim().replace(":", "");
		String continue_slb_value1 = et_set_continue_time_value1.getText().toString().trim();
		String continue_slb_value2 = et_set_continue_time_value2.getText().toString().trim();
		String continue_slb_value3 = et_set_continue_time_value3.getText().toString().trim();
		String continue_slb_value4 = et_set_continue_time_value4.getText().toString().trim();
		int dataLenth = (slb_value1.length() + YzzsCommonUtil.formatStringAdd0(continue_slb_value1, 3, 1).length()) * 4;
		StringBuilder sb = new StringBuilder();
		sb.append(slb_value1).append(YzzsCommonUtil.formatStringAdd0(continue_slb_value1, 3, 1))
			.append(slb_value2).append(YzzsCommonUtil.formatStringAdd0(continue_slb_value2, 3, 1))
			.append(slb_value3).append(YzzsCommonUtil.formatStringAdd0(continue_slb_value3, 3, 1))
			.append(slb_value4).append(YzzsCommonUtil.formatStringAdd0(continue_slb_value4, 3, 1));
		int lenth = 13 + 1 + dataLenth + 1;
		
		byte [] message = new byte[lenth];
		int isPack = ctv_qpsj_pack.isChecked() ? 0 : 1;
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
					.intTobyte(Integer.parseInt((sb.toString()).substring(i - 11, i - 10)));
		}
		message[lenth - 4] = 1;//校验位
		message[lenth - 3] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);//e
		message[lenth - 2] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//n
		message[lenth - 1] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
		sendUnpackData(message,XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}

	/**
	 * 检测持续时间
	 */
	private boolean checkContinueTimeData() {
		String continueTime1 = et_set_continue_time_value1.getText().toString().trim();
		String continueTime2 = et_set_continue_time_value2.getText().toString().trim();
		String continueTime3 = et_set_continue_time_value3.getText().toString().trim();
		String continueTime4 = et_set_continue_time_value4.getText().toString().trim();
		if (CommonUtil.isEmpty(continueTime1) || CommonUtil.isEmpty(continueTime2) ||
				CommonUtil.isEmpty(continueTime3) ||CommonUtil.isEmpty(continueTime4)) {
			ToastMsg(getActivity(), "设置持续时间不能为空");
			return false;
		}
		if (Integer.valueOf(continueTime1) > 200 || Integer.valueOf(continueTime2) > 200 ||
				Integer.valueOf(continueTime3) > 200 ||Integer.valueOf(continueTime4) > 200) {
			ToastMsg(getActivity(), "设置持续时间不大于200");
			return false;
		}
		return true;
	}
	
	@Override
	public void onTime(String time,int itemPosition) {
		switch (itemPosition) {
		case 1://清盘时间1
			tv_set_start_time_value1.setText(time);
			break;
		case 2://清盘时间2
			tv_set_start_time_value2.setText(time);
			break;
		case 3://清盘时间3
			tv_set_start_time_value3.setText(time);
			break;
		case 4://清盘时间4
			tv_set_start_time_value4.setText(time);
			break;
		default:
			break;
		}
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
				if (cmd == LcCMDConstant.LC_SAVE_QPSJ) {
					getSaveQpsjResult(message);
				}
				if (cmd == LcCMDConstant.LC_READ_QPSJ) {
					getReadQpsjResult(message);
				}
			} catch (Exception e) {
				dismissLoading();
				ToastMsg(getActivity(), "数据异常");
			}
		}
	}
	
	private void getReadQpsjResult(byte[] message) {
		// rev:1 (几号料槽) 0（是否批处理）24(10)  03 58 33  03 : 58 33 03 58 33 03 58 33
		if(message[5] == 0) {
			ToastMsg(getActivity(), "读取成功");
			dismissLoading();
			isTimeOut = false;
			if(message[6] == 0) {
				devNumber = message[7];
				tv_title.setText(devNumber + "号料槽");
				ctv_qpsj_pack.setChecked(message[8] == 0);
				int lenth = message [9];
				StringBuilder sb = new StringBuilder();
				for (int i = 10; i < lenth + 10; i++) {
					sb.append(message [i]);
				}
				for (int i = 0; i < mEditText.length; i++) {
					String data = sb.substring(i * 7,i * 7 + 7);
					mEditText[i].setText(Integer.valueOf(data.substring(4,7)) + "");
					mTextView[i].setText(data.substring(0,2) + ":" + data.substring(2,4));
				}
			} else {
				ToastMsg(getActivity(), "读取失败");
			}
		}
	}

	private void getSaveQpsjResult(byte[] message) {
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
		cleanData();
		doReadData();
	}
}
