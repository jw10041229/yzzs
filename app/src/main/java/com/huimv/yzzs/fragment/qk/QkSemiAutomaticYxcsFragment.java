package com.huimv.yzzs.fragment.qk;

import java.util.ArrayList;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.BluetoothScanActivity;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.GeneralCMDConstant;
import com.huimv.yzzs.constant.HkCMDConstant;
import com.huimv.yzzs.constant.QkCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.fragment.flz.FlzJqidFragment;
import com.huimv.yzzs.fragment.general.BbxxFragment;
import com.huimv.yzzs.fragment.general.BjqPhoneNumberFragment;
import com.huimv.yzzs.fragment.general.BjsxjgFragment;
import com.huimv.yzzs.fragment.general.IpWgYmFragment;
import com.huimv.yzzs.fragment.general.LybmFragment;
import com.huimv.yzzs.fragment.general.LymcFragment;
import com.huimv.yzzs.fragment.hk.HkYxcsContainerFragment;
import com.huimv.yzzs.model.QkYxcsData;
import com.huimv.yzzs.model.QkYxcsTimeData;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.slideexpandablelistview.SlideExpandableListAdapter;
import com.huimv.yzzs.util.wheel.DateTimePickDialogUtil;
import com.huimv.yzzs.util.wheel.DateTimePickDialogUtil.OnTimeClickListener;
import com.huimv.yzzs.util.wheel.ThreeNumPickDialogUtil;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.huimv.yzzs.util.wheel.SetItemSelectIsShowWheelUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 *运行参数
 * 
 * @author jiangwei
 */
public class QkSemiAutomaticYxcsFragment extends YzzsBaseFragment implements EventHandler,
		SetItemSelectIsShowWheelUtil.OnConfirmClickSetListener,ConnectTimeoutListener{
	private ImageView ivSet;
	private TextView tvSave, tvReset, tvBack;
	private LinearLayout llBottomBar;
	private SharePreferenceUtil mSpUtil;
	private String identity = "";
	private SetItemSelectIsShowWheelUtil mSetItemSelectWheelUtil;
	private int SbNum = 0;
	private int startTimeCnt = QkMmyzFragment.startTimeCnt;
	private String[] yxcsItemData; // 每个设备的数据转换为数组
	private ArrayList<QkYxcsData> mQk_YxcsList = new ArrayList<>();
	private ArrayList<QkYxcsTimeData> mQk_yxcsTimeData;
	private ListView yxcsListView;
	private YxcsListAdapter mYxcsListAdapter;
	private boolean isTimeOut = true;
	private String wheelItemArr [];
	StringBuilder sbTime = new StringBuilder();
	private TextView tv_sys_time;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		View view = inflater.inflate(R.layout.activity_qk_semi_automatic_yxcs_fragment, null);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		identity = HkYxcsContainerFragment.identity;// 根据这个Key来判断是什么身份
		initViews(view);
		initOnListeners();
		initData();
		return view;
	}

	private void initData() {
		if(identity.equals(GeneralCMDConstant.IDENTITY_ORDINARY)){ // 普通用户
			wheelItemArr = getActivity().getResources().getStringArray(R.array.qk_set_item_array_customer);
			llBottomBar.setVisibility(View.GONE);
		} 
		if(identity.equals(GeneralCMDConstant.IDENTITY_DEBUG)){ // 调试用户
			wheelItemArr = getActivity().getResources().getStringArray(R.array.qk_set_item_array_debug);
			llBottomBar.setVisibility(View.VISIBLE);
		}
		mSetItemSelectWheelUtil = new SetItemSelectIsShowWheelUtil(getActivity(), "请选择设置项目", wheelItemArr,
				QkSemiAutomaticYxcsFragment.this);
		mYxcsListAdapter = new YxcsListAdapter();
		yxcsListView.setAdapter(new SlideExpandableListAdapter(
				mYxcsListAdapter,
				R.id.dkLL1,
				R.id.ll_expandable_item_more));
	}

	/**
	 * 刷新dwData与list中的数据
	 */
	private void getQkyxcsFromSP () {
		getYxcsItemData();
		getYxcsListData();
	}
	
	/**
	 * 得到个设备的数据
	 */
	private void getYxcsItemData () {
		yxcsItemData = mSpUtil.getQkyxcs().split(XtAppConstant.SEPARSTOR);
	}

	/**
	 * 解析数据到List
	 */
	private void getYxcsListData() {
		mQk_YxcsList.clear();
		QkYxcsData mQk_yxcsData;
		for (int i = 0; i < yxcsItemData.length; i++) {// 几个设备
			String yxcsItem = yxcsItemData[i];
			if (CommonUtil.isEmpty(yxcsItem)) {
				return;
			}
			mQk_yxcsData = new QkYxcsData();
			int cnt = startTimeCnt * 6 * 2;
			mQk_yxcsData.setLx(yxcsItem.substring(cnt, cnt + 2));
			mQk_yxcsData.setSx(yxcsItem.substring(cnt + 2, cnt + 4));
			mQk_yxcsTimeData = new ArrayList<>();
			for (int k = 0; k < startTimeCnt; k++) {//
				int positon = k * 12;
				QkYxcsTimeData mHk_YxcsData = new QkYxcsTimeData();
				mHk_YxcsData.setStartTime(yxcsItem.substring(positon,positon + 6));
				mHk_YxcsData.setContinueTime(yxcsItem.substring(positon + 6,positon + 12));
				mQk_yxcsTimeData.add(mHk_YxcsData);
			}
			mQk_yxcsData.setTimeList(mQk_yxcsTimeData);
			mQk_YxcsList.add(mQk_yxcsData);
		}
	}

	private void initViews(View view) {
		yxcsListView = (ListView) view.findViewById(R.id.yxcsListView);
		tvSave = (TextView) view.findViewById(R.id.tv_save);
		tvReset = (TextView) view.findViewById(R.id.tv_reset);
		ivSet = (ImageView) view.findViewById(R.id.iv_set);
		llBottomBar = (LinearLayout) view.findViewById(R.id.ll_bottom_bar);
		tvBack = (TextView) view.findViewById(R.id.tv_back);
		tv_sys_time = (TextView) view.findViewById(R.id.tv_sys_time);
	}

	private void initOnListeners() {
		setConnectTimeoutListener(this);
		tvBack.setOnClickListener(this);
		ivSet.setOnClickListener(this);
		tvSave.setOnClickListener(this);
		tvReset.setOnClickListener(this);
	}


	/**
	 * 将数据保存到Sp里
	 */
	private void saveData2Sp() {
		StringBuilder spData = new StringBuilder();
		for (int i = 0; i < SbNum; i++) {
			for (int j = 0; j < startTimeCnt; j++) {
				spData.append(mQk_YxcsList.get(i).getTimeList().get(j).getStartTime()).
				append(mQk_YxcsList.get(i).getTimeList().get(j).getContinueTime());
			}
			spData.append(mQk_YxcsList.get(i).getLx()).append(mQk_YxcsList.get(i).getSx());
			spData.append(XtAppConstant.SEPARSTOR);
		}
		mSpUtil.setQkyxcs(spData.toString());
		getQkyxcsFromSP();
		mYxcsListAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPause() {
		MessageReceiver.ehList.remove(this);
		super.onPause();
	}

	@Override
	public void onResume() {
		try {
			MessageReceiver.ehList.add(this);
			SbNum = QkMmyzFragment.sbNumS;
			getQkyxcsFromSP();
			mYxcsListAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			ToastMsg(getActivity(), "数据解析错误,请重试");
			getFragmentManager().popBackStack();
		}
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_set:
			mSetItemSelectWheelUtil.showDialog();
			break;
		case R.id.tv_save:
			if (SbNum == 0) {
				ToastMsg(getActivity(), "设备为空,请添加设备");
				return;
			}
			if(YzzsApplication.isConnected) {
				sendData();
				startTime(XtAppConstant.SEND_CMD_TIMEOUT, "数据保存");
				isTimeOut = true;
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		case R.id.cgqbdBtn:
			if(YzzsApplication.isConnected) {
				showLoading("正在获取传感器绑定数据");
				sendGetDataCmdToBt(HkCMDConstant.GET_CGQBD);
				startTime(XtAppConstant.SEND_CMD_TIMEOUT, "获取传感器绑定数据");
				isTimeOut = true;
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		case R.id.hkbdBtn:
			if(YzzsApplication.isConnected) {
				showLoading("正在获取环控设备数据");
				// 发送获取环控绑定命令
				sendGetDataCmdToBt(HkCMDConstant.GET_HKBDPZ);
				startTime(XtAppConstant.SEND_CMD_TIMEOUT, "获取环控设备数据");
				isTimeOut = true;
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		}
	}

	/**
	 * 发送获取数据CMD
	 * 数据格式： h,m,1,0,17,0,12,  0  ,111,e,n,d
	 * @param CmdType
	 */
	public void sendGetDataCmdToBt(int CmdType){
		int dataLength = 12; // 数据总长度
		byte[] byteSendData = new byte[dataLength]; // 构造 byte 数组
		byteSendData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH); // 协议头 “h”
		byteSendData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM); // 协议头 “m”
		byteSendData[2] = YzzsCommonUtil.intTobyte(HkCMDConstant.HK_SBBZ); // 数据域：类型
		byteSendData[3] = YzzsCommonUtil.intTobyte(CmdType / 256); // 数据域：命令高位
		byteSendData[4] = YzzsCommonUtil.intTobyte(CmdType % 256); // 数据域：命令低位
		byteSendData[5] = YzzsCommonUtil.intTobyte(dataLength / 256); // 数据域：数据总长度高位
		byteSendData[6] = YzzsCommonUtil.intTobyte(dataLength % 256); // 数据域：数据总长度低位
		byteSendData[7] = YzzsCommonUtil.intTobyte(0); // 数据域：单条有效数据长度：0
		byteSendData[8] = YzzsCommonUtil.intTobyte(0); // 校验位
		byteSendData[9] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE); // 协议尾：“e”
		byteSendData[10] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN); // 协议尾：“n”
		byteSendData[11] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD); // 协议尾：“d”
		sendUnpackData(byteSendData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
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

	/**
	 * 发送配置
	 * 
	 */
	private void sendData() {
		byte[] allData = packData(QkCMDConstant.SAVE_SBCSPZ);
		// 大数组拼接完成,分20包发送
		showLoading("正在保存");
		sendUnpackData(allData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}

	/**
	 * 拼成一个大数据包
	 * @throws InterruptedException 
	 */
	private byte [] packData(int cmd) {
		int sbNum = QkMmyzFragment.sbNumS;//有几个设备
		int startTimeCnt = QkMmyzFragment.startTimeCnt;
		int lenth = (startTimeCnt * 4 + 4) * sbNum;
		byte [] Data = new byte [lenth];//循环体
		int lenthAll = lenth + 11 + 4;//总数据长度 = 循环体长度 + 前10位 + 后4位协议尾 
		
		byte [] allData = new byte [lenthAll];//大数据包
		allData [0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);//h
		allData [1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);//m
		allData [2] = 2;//环控
		allData [3] = YzzsCommonUtil.intTobyte(cmd / 256);//命令1
		allData [4] = YzzsCommonUtil.intTobyte(cmd % 256);//命令2
		allData [5] = YzzsCommonUtil.intTobyte(lenthAll / 256);//总长度
		allData [6] = YzzsCommonUtil.intTobyte(lenthAll % 256);//总长度
		allData [7] = 0;
		allData [8] = YzzsCommonUtil.intTobyte(sbNum);//几个使能设备
		
		allData [9] = YzzsCommonUtil.intTobyte(startTimeCnt);//运行使能循环
		allData [10] = YzzsCommonUtil.intTobyte(startTimeCnt * 4 + 4);//单个循环体长度
		
		for (int i = 0; i < sbNum; i++) {
			byte [] DataSige = new byte [startTimeCnt * 4 + 4];//单个运行周期循环体
			int size = mQk_YxcsList.get(i).getTimeList().size();//运行周期循环长度，及跟startTimeCnt一样
			for (int j = 0; j < size; j++) {//单个循环
				String startTime = mQk_YxcsList.get(i).getTimeList().get(j).getStartTime();//012027
				String continueTime = mQk_YxcsList.get(i).getTimeList().get(j).getContinueTime();//000003
				StringBuilder startTimeSb = new StringBuilder().append(startTime).replace(0, 1, "").replace(2, 3, "");//1227
				
				String continueTimeHigh = continueTime.substring(0,3);//000
				String continueTimeLow = continueTime.substring(3,6);//003
				DataSige[j * 4] = YzzsCommonUtil.intTobyte(Integer.valueOf(startTimeSb.substring(0,2)));
				DataSige[j * 4 + 1 ] = YzzsCommonUtil.intTobyte(Integer.valueOf(startTimeSb.substring(2,4)));
				
				DataSige[j * 4 + 2] = YzzsCommonUtil.intTobyte(Integer.valueOf(continueTimeHigh));
				DataSige[j * 4 + 3] =  YzzsCommonUtil.intTobyte(Integer.valueOf(continueTimeLow));
			}
			DataSige[DataSige.length - 4] = YzzsCommonUtil.intTobyte(Integer.valueOf(mQk_YxcsList.get(i).getLx().substring(0,1)));//类型
			DataSige[DataSige.length - 3] = YzzsCommonUtil.intTobyte(Integer.valueOf(mQk_YxcsList.get(i).getLx().substring(1,2)));//类型
			DataSige[DataSige.length - 2] = YzzsCommonUtil.intTobyte(Integer.valueOf(mQk_YxcsList.get(i).getSx().substring(0,1)));//类型
			DataSige[DataSige.length - 1] = YzzsCommonUtil.intTobyte(Integer.valueOf(mQk_YxcsList.get(i).getSx().substring(1,2)));//类型
			for (int j = 0; j < DataSige.length; j++) {//单个循环体拼入循环体数组
				Data[i * DataSige.length + j] = DataSige[j];
			}
		}
		System.arraycopy(Data, 0, allData, 11, Data.length);
		allData [allData.length - 4] = 111;//校验位
		allData [allData.length - 3] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);//协议尾e
		allData [allData.length - 2] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//h
		allData [allData.length - 1] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
		return allData;
	}

	@Override
	public void onMessage(byte[] message) {
		getJoiningPack(message);
	}

	/**
	 * 拼包
	 * 
	 * @param message
	 */
	private void getJoiningPack(byte[] message) {
		if (message[0] == QkCMDConstant.QK_SBBZ) {// 环控
			try {
				int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);// 命令类型
				// 档位保存
				if (cmd == QkCMDConstant.SAVE_SBCSPZ) {
					saveData(message);
				}
				if (cmd == QkCMDConstant.GET_SYS_TIME) {
					getTitleTime(message);
				}
				// 重启设备
				if (cmd == GeneralCMDConstant.RESTART_XT_DEVICE) {
					restartDeviceEx(message);
				}
			} catch (Exception e) {
				dismissLoading();
				ToastMsg(getActivity(), "数据异常");
			}
		}
	}
	/**
	 * 重启设备
	 * @param message
	 */
	private void restartDeviceEx(byte[] message) {
		if (message[0] == QkCMDConstant.QK_SBBZ) {
			int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
			if (cmd == GeneralCMDConstant.RESTART_XT_DEVICE) {
				isTimeOut = false;
				dismissLoading();
				if (message[5] == 0) {// 重启设备成功
					ToastMsg(getActivity(), "重启系统设备成功!");
					toBluetoothScan();
				} else { // 重启设备失败
					ToastMsg(getActivity(), "重启系统设备失败,请重试!");
				}
			}
		}
	}
	/**
	 * 返回首页
	 */
	private void toBluetoothScan() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				final Intent intent = new Intent(getActivity(), BluetoothScanActivity.class);
				startActivity(intent);
				getActivity().finish();
			}
		}, 2000);
	}
	/**
	 * 读取系统时间
	 * @param message
	 */
	private void getTitleTime(byte[] message) {
		if(message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if(message[6] == 0) {
				int lenth = message [7];
				StringBuilder sb = new StringBuilder();
				for (int i = 8; i < lenth + 8; i++) {
					sb.append(message [i]);
				}
				if (lenth == 4) {
					tv_sys_time.setText(sb.insert(2, ":"));
				}
			}
		}
	}
	/**
	 * 保存数据
	 * @param message
	 */
	private void saveData(byte[] message) {
		int cmd = YzzsCommonUtil.getCMD(message[1], message[2]);
			if (cmd == QkCMDConstant.SAVE_SBCSPZ) {// 档位保存
				if (message[5] == 0) {// 成功与否
					isTimeOut = false;
					if (message[6] == 0) {
						saveData2Sp();
						dismissLoading();
						ToastMsg(getActivity(), "保存成功");
					} else {
						dismissLoading();
						ToastMsg(getActivity(), "保存失败");
					}
				} else {
					dismissLoading();
					ToastMsg(getActivity(), "保存失败,请重试");
				}
			}
	}

	@Override
	public void onConfirm(int position) {
		switch (position) {
		case 0:
			toSetFragment(new BbxxFragment());
			break;
		case 1://报警生效时间
			toSetFragment(new BjsxjgFragment());
			break;
		case 2:// 蓝牙别名
			if (hasLogined()) {
				toSetFragment(new LybmFragment());
			} else {
				ToastMsg(getActivity(), "使用蓝牙别名功能前,请先登录账户");
			}
			break;
		case 3:// 蓝牙名称
			toSetFragment(new LymcFragment());
			break;
		case 4:// 机器ID
			if (hasLogined()) {
				toSetFragment(new FlzJqidFragment());
			} else {
				ToastMsg(getActivity(), "在进行操作机器ID前,请登录账户！");
			}
			break;
		case 5://重启设备
			restartDevDialog();
			break;
		case 6:// ip/网关
			toSetFragment(new IpWgYmFragment());
			break;
		case 7:// ip/网关
			toSetFragment(new BjqPhoneNumberFragment());
			break;
		}
	}
	/**
	 * 重启设备
	 */
	public void restartDevDialog () {
		new AlertDialog.Builder(getActivity()).setMessage("是否确认重启设备?").setTitle("重启设备")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if(YzzsApplication.isConnected) {
							showLoading("正在重启设备...");
							sendRestartDeviceCMD();
							startTime(XtAppConstant.SEND_CMD_TIMEOUT, "重启设备");
							isTimeOut = true;
						} else {
							ToastMsg(getActivity(), getString(R.string.disconnected));
						}
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}
	/**
	 * 设备重启
	 */
	private void sendRestartDeviceCMD() {
		sendGetBaseCMD(QkCMDConstant.QK_SBBZ, GeneralCMDConstant.RESTART_XT_DEVICE);
	}
	/**
	 * 判断是否已经登陆
	 */
	private boolean hasLogined() {
		return ("".equals(mSpUtil.getYhxm()) && "".equals(mSpUtil.getYhmm()));
	}

	/**
	 * 跳转目标Fragment
	 * 
	 * @param mFragment
	 */
	private void toSetFragment(Fragment mFragment) {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fragment_container, mFragment);
		Bundle bundle = new Bundle();
		bundle.putInt("whichDev", QkCMDConstant.QK_SBBZ);
		mFragment.setArguments(bundle);
		ft.addToBackStack(null);
		ft.commit();
	}

	/**
	 * 设备适配器
	 * 
	 * @author jiangwei
	 *
	 */
	private class YxcsListAdapter extends BaseAdapter {
		private LayoutInflater mInflator;

		public YxcsListAdapter() {
			super();
			mInflator = getActivity().getLayoutInflater();
		}

		@Override
		public int getCount() {
			return mQk_YxcsList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = mInflator.inflate(R.layout.qk_semi_automatic_listview_item, null);
				viewHolder.iv_sb_icon1 = (ImageView) convertView.findViewById(R.id.iv_sb_icon1);
				viewHolder.tv_sb_sx1 = (TextView) convertView.findViewById(R.id.tv_sb_sx1);
				viewHolder.tv_start_time1 = (TextView) convertView.findViewById(R.id.tv_start_time1);
				viewHolder.tv_start_time2 = (TextView) convertView.findViewById(R.id.tv_start_time2);
				viewHolder.tv_start_time3 = (TextView) convertView.findViewById(R.id.tv_start_time3);
				viewHolder.tv_start_time4 = (TextView) convertView.findViewById(R.id.tv_start_time4);
				viewHolder.tv_start_time5 = (TextView) convertView.findViewById(R.id.tv_start_time5);
				viewHolder.tv_continued_time1 = (TextView) convertView.findViewById(R.id.tv_continued_time1);
				viewHolder.tv_continued_time2 = (TextView) convertView.findViewById(R.id.tv_continued_time2);
				viewHolder.tv_continued_time3 = (TextView) convertView.findViewById(R.id.tv_continued_time3);
				viewHolder.tv_continued_time4 = (TextView) convertView.findViewById(R.id.tv_continued_time4);
				viewHolder.tv_continued_time5 = (TextView) convertView.findViewById(R.id.tv_continued_time5);
				doOnClickListener(viewHolder.tv_start_time1, 1, 0);
				doOnClickListener(viewHolder.tv_start_time2, 1, 1);
				doOnClickListener(viewHolder.tv_start_time3, 1, 2);
				doOnClickListener(viewHolder.tv_start_time4, 1, 3);
				doOnClickListener(viewHolder.tv_start_time5, 1, 4);
				doOnClickListener(viewHolder.tv_continued_time1, 2, 0);
				doOnClickListener(viewHolder.tv_continued_time2, 2, 1);
				doOnClickListener(viewHolder.tv_continued_time3, 2, 2);
				doOnClickListener(viewHolder.tv_continued_time4, 2, 3);
				doOnClickListener(viewHolder.tv_continued_time5, 2, 4);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			String sblx = mQk_YxcsList.get(position).getLx();
			int sbid = -1;
			if (sblx.equals(XtAppConstant.SBBD_SBLX_LX)) {
				sbid = R.drawable.lx_on;
			}
			if (sblx.equals(XtAppConstant.SBBD_SBLX_PW)) {
				sbid = R.drawable.pw_on;
			}
			if (sblx.equals(XtAppConstant.SBBD_SBLX_QK_SL)) {
				sbid = R.drawable.sl_on;
			}
			if (sbid != -1) {
				viewHolder.iv_sb_icon1.setImageResource(sbid);
			} else {
				viewHolder.iv_sb_icon1.setImageResource(R.drawable.icon_close);
			}
			
			viewHolder.tv_sb_sx1.setText(Integer.valueOf(mQk_YxcsList.get(position).getSx()).toString());
			
			doStartTimeText(viewHolder.tv_start_time1, position, 0);
			doStartTimeText(viewHolder.tv_start_time2, position, 1);
			doStartTimeText(viewHolder.tv_start_time3, position, 2);
			doStartTimeText(viewHolder.tv_start_time4, position, 3);
			doStartTimeText(viewHolder.tv_start_time5, position, 4);
			
			doContinueTimeText(viewHolder.tv_continued_time1, position, 0);
			doContinueTimeText(viewHolder.tv_continued_time2, position, 1);
			doContinueTimeText(viewHolder.tv_continued_time3, position, 2);
			doContinueTimeText(viewHolder.tv_continued_time4, position, 3);
			doContinueTimeText(viewHolder.tv_continued_time5, position, 4);
			setTag(viewHolder.tv_start_time1, position);
			setTag(viewHolder.tv_start_time2, position);
			setTag(viewHolder.tv_start_time3, position);
			setTag(viewHolder.tv_start_time4, position);
			setTag(viewHolder.tv_start_time5, position);
			setTag(viewHolder.tv_continued_time1, position);
			setTag(viewHolder.tv_continued_time2, position);
			setTag(viewHolder.tv_continued_time3, position);
			setTag(viewHolder.tv_continued_time4, position);
			setTag(viewHolder.tv_continued_time5, position);
			return convertView;
		}
		private class MyClickListener implements OnClickListener {
			private TextView textView;
			private int flag = -1;
			private int itemPosition = -1;//第几个item
			private int subItemPosition = -1;//一个item中第几个循环
			public MyClickListener(int flag,View view,int subItemPosition) {
				this.textView = (TextView) view;
				this.flag = flag;
				this.subItemPosition = subItemPosition;
			}

			@Override
			public void onClick(View v) {
				if(flag == 1) {
					//开始时间
					new DateTimePickDialogUtil(getActivity(), "", 1, new OnTimeClickListener() {
						
						@Override
						public void onTime(String time,int itemPosition) {
							textView.setText(time);
							itemPosition = (Integer) textView.getTag();
							if (itemPosition != -1) {
								StringBuilder textStr = new StringBuilder().append(textView.getText().toString().trim());
								textStr.replace(2, 3, "");//去掉冒号
								textStr.insert(0, "0").insert(3, "0");//1227转换为012 027
								mQk_YxcsList.get(itemPosition).getTimeList().get(subItemPosition).setStartTime(textStr.toString());
							}
						}
					}).dateTimePicKDialog(0);
				}
				
				if(flag == 2) {
					new ThreeNumPickDialogUtil(getActivity(), "请选择持续时间", new ThreeNumPickDialogUtil.OnContinueTimeClickListener() {
					
						@Override
						public void OnContinueTime(String time) {
							textView.setText(String.valueOf(Integer.valueOf(time)));
							itemPosition = (Integer) textView.getTag();
							if (itemPosition != -1) {
								String textStr = textView.getText().toString().trim();
								StringBuilder sbContinueTime = new StringBuilder();
								String high = String.valueOf(Integer.valueOf(textStr) / 256);
								String low = String.valueOf(Integer.valueOf(textStr) % 256);
								sbContinueTime.append(YzzsCommonUtil.formatStringAdd0(high, 3, 1))
								.append(YzzsCommonUtil.formatStringAdd0(low, 3, 1));
								mQk_YxcsList.get(itemPosition).getTimeList().get(subItemPosition).setContinueTime(sbContinueTime.toString());
							}
						}
					}).showDialog();
				}
			}
	}
		private void setTag(TextView textVew,int position) {
			textVew.setTag(position);
		}
		
		private void doOnClickListener(TextView textVew,int flag,int i) {
			textVew.setOnClickListener(new MyClickListener(flag,textVew,i));
		}
		
		private void doStartTimeText (TextView textVew,int position,int i) {
			textVew.setText(sbTime.append(mQk_YxcsList.get(position).getTimeList().get(i).getStartTime()).replace(0, 1, "").replace(2, 3, "").insert(2, XtAppConstant.COLON));
			sbTime.delete(0, sbTime.length());
		}
		
		private void doContinueTimeText(TextView textView ,int position,int i) {
			int subHighStr = Integer.valueOf(mQk_YxcsList.get(position).getTimeList().get(i).getContinueTime().substring(0,3));
			int subLowStr = Integer.valueOf(mQk_YxcsList.get(position).getTimeList().get(i).getContinueTime().substring(3,6));
			int continueTime = YzzsCommonUtil.getCMD(YzzsCommonUtil.ChangeByteToPositiveNumber(YzzsCommonUtil.intTobyte(subHighStr)), 
					YzzsCommonUtil.ChangeByteToPositiveNumber(YzzsCommonUtil.intTobyte(subLowStr)));
			
			textView.setText(String.valueOf(continueTime));
		}
		

	class ViewHolder {
		TextView tv_start_time1;
		TextView tv_start_time2;
		TextView tv_start_time3;
		TextView tv_start_time4;
		TextView tv_start_time5;
		
		TextView tv_continued_time1;
		TextView tv_continued_time2;
		TextView tv_continued_time3;
		TextView tv_continued_time4;
		TextView tv_continued_time5;
		ImageView iv_sb_icon1;
		TextView tv_sb_sx1;
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