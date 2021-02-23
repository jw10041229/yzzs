package com.huimv.yzzs.fragment.qk;

import java.util.ArrayList;
import java.util.Arrays;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.IndexActivity;
import com.huimv.yzzs.adapter.SbbdListAdapter;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.QkCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.model.SbbdData;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.huimv.yzzs.util.wheel.ItemSelectWheelUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 设备绑定
 * 
 * @author zwl
 */
public class QkSbbdFragment extends YzzsBaseFragment
		implements EventHandler, OnClickListener, ItemSelectWheelUtil.OnConfirmClickListener,ConnectTimeoutListener{
	private final static String TAG = QkSbbdFragment.class.getSimpleName();
	private SharePreferenceUtil mSpUtil;
	private TextView tv_back, tv_save;
	private ImageView btnAddSbbd, btnRemoveSbbd;
	private ListView listViewSbbd;
	private ItemSelectWheelUtil mFjsltemSelectWheelUtil;

	private String[] sbbdData = null; // 保存sp中设备绑定数据
	private ArrayList<SbbdData> mQkBdDataList = new ArrayList<>();
	private SbbdListAdapter mQkbdListAdapter;

	private static final int SBBD_FLAG_ADD = 1; // wheel 添加绑定的标记位
	private static final int SBBD_FLAG_REMOVE = 2; // wheel 删除绑定的标记位

	private static final int WHEEL_ITEM_LX_POS = 0; // wheel 中 item 的上料位置号
	private static final int WHEEL_ITEM_SL_POS = 1; // wheel 中 item 的湿帘位置号
	private static final int WHEEL_ITEM_PW_POS = 2; // wheel 中 item 的喷雾位置号
	
	private static final String SBBD_KGZT_OPEN = "1"; // 开关状态 - 打开
	private static final String SBBD_KGZT_CLOSE = "0"; // 开关状态 - 关闭

	// 固定位 --- 数据头长度 = 协议头 + 数据域头固定长度
	private static final int GDW_LEN_HEAD = 8;
	// 固定位 --- 数据尾长度 = 校验位 + 协议尾
	private static final int GDW_LEN_TAIL = 4;

	private boolean isTimeOut = true;
	private boolean isConfimDelete = false;
	public static boolean isDataSaveOk = true;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_qk_sbbd_fragment, null);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		mFjsltemSelectWheelUtil = new ItemSelectWheelUtil(getActivity(), "请选择绑定类型", 
				getResources().getStringArray(R.array.qk_device_item_array),QkSbbdFragment.this);
		initView(view);
		initData();
		initParams();

		return view;
	}

	private void initData() {
		setConnectTimeoutListener(this);
		getInitSbbdData();
	}

	private void initView(View view) {
		tv_back = (TextView) view.findViewById(R.id.tv_back);
		tv_save = (TextView) view.findViewById(R.id.tv_save);
		listViewSbbd = (ListView) view.findViewById(R.id.lv_sbbd);
		btnAddSbbd = (ImageView) view.findViewById(R.id.btn_add_sbbd);
		btnRemoveSbbd = (ImageView) view.findViewById(R.id.btn_remove_sbbd);

		tv_back.setOnClickListener(this);
		tv_save.setOnClickListener(this);
		btnAddSbbd.setOnClickListener(this);
		btnRemoveSbbd.setOnClickListener(this);

	}

	/**
	 * 解析设备绑定数据
	 */
	private void getInitSbbdData() {
		sbbdData = mSpUtil.getSbbdTd().split(XtAppConstant.SEPARSTOR);
		int size = CommonUtil.isEmpty(sbbdData[0]) ? 0:sbbdData.length;
		mQkBdDataList.clear();
		for (int i = 0; i < size; i++) {
			SbbdData mSbbdData = new SbbdData();
			mSbbdData.setSbLx(sbbdData[i].substring(0, 2)); // 设备类型
			mSbbdData.setSbXh(sbbdData[i].substring(2, 4)); // 设备编号
			mSbbdData.setGlz(sbbdData[i].substring(4, 9)); // 功率值
			mSbbdData.setTdKg(sbbdData[i].substring(9, 10)); // 通道开关
			mSbbdData.setHeKg(sbbdData[i].substring(10, 11)); // 霍尔开关
			mQkBdDataList.add(mSbbdData);
		}
	}

	private void initParams() {
		mQkbdListAdapter = new SbbdListAdapter(getActivity(), mQkBdDataList);
		listViewSbbd.setAdapter(mQkbdListAdapter);
	}

	/**
	 * 得到 list 中各个设备的使用个数
	 * 
	 * @param deviceType
	 * @return
	 */
	private int getDeviceUsedNumValue(String deviceType) {
		int deviceUsedNum = 0;
		for (SbbdData mSbbdData : mQkBdDataList) {
			String sblx = mSbbdData.getSbLx();
			if (deviceType.equals(sblx)) {
				deviceUsedNum++;
			}
		}
		return deviceUsedNum;
	}

	/**
	 * 验证各个设备是否可以被添加
	 * 
	 * @param deviceType
	 *            当前设备的使用个数
	 * @param deviceType
	 *            该类型设备的最大支持个数
	 * @return true : 可以继续添加； false： 不可以继续添加
	 */
	private boolean validateDeviceCanAdd(String deviceType, int deviceMaxNum) {
		return getDeviceUsedNumValue(deviceType) < deviceMaxNum;
	}

	/**
	 * 添加设备绑定
	 * 
	 * @param wheelItemPos
	 *            设备绑定类型的标记位；0：料线；1：喷雾
	 */
	private void addSbbd(int wheelItemPos) {
		SbbdData mNewSbBdData = new SbbdData();
		mNewSbBdData.setGlz("00000"); // 功率值
		mNewSbBdData.setIsBd("1"); // 是否绑定：该字段无用，设置默认值
		mNewSbBdData.setTdKg(SBBD_KGZT_OPEN); // 使能开关：默认打开
		mNewSbBdData.setHeKg(SBBD_KGZT_CLOSE); // 霍尔开关：默认关闭
		int lxMaxNoValue = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_LX); // 上料最大编号
		int pwMaxNoValue = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_PW);// 喷雾最大编号
		int slMaxNoValue = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_QK_SL);// 湿帘最大编号
		byte[] sbLxMax = IndexActivity.sbLxMax;
		int insertItemPosition = 0; // 插入item的位置
		// 料线
		if (wheelItemPos == WHEEL_ITEM_LX_POS) {
			// 得到料线的最大支持个数
			int fjMaxNum = YzzsCommonUtil.getSbMax(sbLxMax, Integer.valueOf(XtAppConstant.SBBD_SBLX_LX));

			if (validateDeviceCanAdd(XtAppConstant.SBBD_SBLX_LX, fjMaxNum)) {
				ToastMsg(getActivity(), "添加料线");
				mNewSbBdData.setSbLx(XtAppConstant.SBBD_SBLX_LX);
				String fjSbxh = String.valueOf(lxMaxNoValue + 1);
				String sbxh = YzzsCommonUtil.formatStringAdd0(fjSbxh, 2, 1);
				mNewSbBdData.setSbXh(sbxh);
				insertItemPosition = lxMaxNoValue;
			} else {
				ToastMsg(getActivity(), "不能再添加更多的料线");
				return;
			}
		}
		// 喷雾
		if (wheelItemPos == WHEEL_ITEM_PW_POS) {
			int pwMaxNum = YzzsCommonUtil.getSbMax(sbLxMax, Integer.valueOf(XtAppConstant.SBBD_SBLX_PW));
			if (validateDeviceCanAdd(XtAppConstant.SBBD_SBLX_PW, pwMaxNum)) {
				ToastMsg(getActivity(), "添加喷雾");
				mNewSbBdData.setSbLx(XtAppConstant.SBBD_SBLX_PW);
				String pwSbxh = String.valueOf(pwMaxNoValue + 1);
				String sbxh = YzzsCommonUtil.formatStringAdd0(pwSbxh, 2, 1);
				mNewSbBdData.setSbXh(sbxh);
				// 新添加喷雾的位置 = 三种种设备类型最大编号的和
				insertItemPosition = lxMaxNoValue + slMaxNoValue + pwMaxNoValue;
			} else {
				ToastMsg(getActivity(), "不能再添加更多的喷雾");
				return;
			}
		}
		// 湿帘
		if (wheelItemPos == WHEEL_ITEM_SL_POS) {
			int slMaxNum = YzzsCommonUtil.getSbMax(sbLxMax, Integer.valueOf(XtAppConstant.SBBD_SBLX_QK_SL));
			if (validateDeviceCanAdd(XtAppConstant.SBBD_SBLX_QK_SL, slMaxNum)) {
				ToastMsg(getActivity(), "添加湿帘");
				mNewSbBdData.setSbLx(XtAppConstant.SBBD_SBLX_QK_SL);
				String slSbxh = String.valueOf(slMaxNoValue + 1);
				String sbxh = YzzsCommonUtil.formatStringAdd0(slSbxh, 2, 1);
				mNewSbBdData.setSbXh(sbxh);
				// 新添加湿帘的位置 = 两种设备类型最大编号的和
				insertItemPosition = lxMaxNoValue + slMaxNoValue;
			} else {
				ToastMsg(getActivity(), "不能再添加更多的湿帘");
				return;
			}
		}
		mQkBdDataList.add(insertItemPosition, mNewSbBdData);
		mQkbdListAdapter.notifyDataSetChanged();
		listViewSbbd.setSelection(insertItemPosition);
	}

	/**
	 * 验证料线 喷雾设备可以被删除
	 * 
	 * @param deviceType 设备类型
	 * @return true:可以被删除； false： 不可以被删除
	 */
	private boolean  validateDeviceCanRemove(String deviceType) {
		return getDeviceUsedNumValue(deviceType) != 0;
	}

	/**
	 * 删除设备绑定
	 * 
	 * @param wheelItemPos
	 *            设备绑定类型的标记位；0：料线；1：喷雾
	 */
	private void removeHkbd(int wheelItemPos) {
		int lxUsedNum = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_LX); // 料线最大编号
		int pwUsedNum = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_PW);// 喷雾最大编号
		int slUsedNum = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_QK_SL);// 湿帘最大编号
		int removeItemPosition = 0;
		if (wheelItemPos == WHEEL_ITEM_LX_POS) {
			if(validateDeviceCanRemove(XtAppConstant.SBBD_SBLX_LX)){
				// 删除料线位置 = 料线 - 1 
				removeItemPosition = lxUsedNum - 1;
				ToastMsg(getActivity(), "删除料线");
			} else {
				ToastMsg(getActivity(), "当前没有绑定料线");
				return;
			}
		}
		if (wheelItemPos == WHEEL_ITEM_SL_POS) {
			if(validateDeviceCanRemove(XtAppConstant.SBBD_SBLX_QK_SL)){
				// 删除湿帘的位置 = 料线 + 湿帘 - 1
				removeItemPosition = lxUsedNum + slUsedNum - 1;
				ToastMsg(getActivity(), "删除湿帘");
			} else {
				ToastMsg(getActivity(), "当前没有绑定湿帘");
				return;
			}
		}
		if (wheelItemPos == WHEEL_ITEM_PW_POS) {
			if(validateDeviceCanRemove(XtAppConstant.SBBD_SBLX_PW)){
				// 删除喷雾的位置 = 料线 + 喷雾 + 湿帘- 1
				removeItemPosition = lxUsedNum + pwUsedNum + slUsedNum - 1;
				ToastMsg(getActivity(), "删除喷雾");
			} else {
				ToastMsg(getActivity(), "当前没有绑定喷雾");
				return;
			}
		}
		mQkBdDataList.remove(removeItemPosition);
		mQkbdListAdapter.notifyDataSetChanged();
		listViewSbbd.setSelection(removeItemPosition);
	}

	private void sendSbbdDataToBt() {
		/**
		 * 1 01200 1 1 01
		 * 
		 * 1:设备类型 01200:功率 1:开关 1:霍尔开关 01:设备顺序
		 * 
		 * @param hkbdTd1
		 */
		StringBuilder sb = new StringBuilder();
		int singleDataLength = 0; // 单条有效数据的长度
		for (int i = 0; i < mQkBdDataList.size(); i++) {
			String sblx = mQkBdDataList.get(i).getSbLx(); // 设备类型
			String sbxh = mQkBdDataList.get(i).getSbXh(); // 设备编号
			String glz = mQkBdDataList.get(i).getGlz(); // 功率值
			String tdkg = mQkBdDataList.get(i).getTdKg(); // 通道开关
			String hekg = mQkBdDataList.get(i).getHeKg(); // 霍尔开关
			sb.append(sblx).append(sbxh).append(glz).append(tdkg).append(hekg);
			if (i == 0) {
				singleDataLength = sb.length(); // 得到单个有效数据的长度
			}
		}
		String circleDataStr = sb.toString(); // 循环体数据
		// byte 数据的总长度
		int dataLength = circleDataStr.length() + GDW_LEN_HEAD + GDW_LEN_TAIL;
		// 循环体 + 前固定位 的长度（用于填充数据使用）
		int len = circleDataStr.length() + GDW_LEN_HEAD;
		byte[] byteSendData = new byte[dataLength]; // 构造 byte 数组
		byteSendData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH); // 协议头 “h”
		byteSendData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM); // 协议头 “m”
		byteSendData[2] = YzzsCommonUtil.intTobyte(QkCMDConstant.QK_SBBZ); // 数据域：类型
		byteSendData[3] = YzzsCommonUtil.intTobyte(QkCMDConstant.SAVE_SBBDPZ / 256); // 数据域：命令高位
		byteSendData[4] = YzzsCommonUtil.intTobyte(QkCMDConstant.SAVE_SBBDPZ % 256); // 数据域：命令低位
																						// 12
		byteSendData[5] = YzzsCommonUtil.intTobyte(dataLength / 256); // 数据域：数据总长度高位
		byteSendData[6] = YzzsCommonUtil.intTobyte(dataLength % 256); // 数据域：数据总长度低位
		byteSendData[7] = YzzsCommonUtil.intTobyte(singleDataLength); // 数据域：单条有效数据长度
		for (int i = GDW_LEN_HEAD; i < len; i++) {
			int data = Integer.valueOf(circleDataStr.substring(i - GDW_LEN_HEAD, i - GDW_LEN_HEAD + 1));
			byteSendData[i] = YzzsCommonUtil.intTobyte(data);
		}
		byteSendData[dataLength - 4] = YzzsCommonUtil.intTobyte(111); // 校验位：暂时不用
		byteSendData[dataLength - 3] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE); // 协议尾：“e”
		byteSendData[dataLength - 2] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN); // 协议尾：“n”
		byteSendData[dataLength - 1] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD); // 协议尾：“d”
		showLoading("正在保存设备绑定配置(1/2)");
		sendUnpackData(byteSendData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}


	@Override
	public void onConfirm(int position, int i) {
		if (i == SBBD_FLAG_ADD) {
			addSbbd(position);
		}
		if (i == SBBD_FLAG_REMOVE) {
			if (isConfimDelete) {
				removeHkbd(position);
			} else {
				confimDelete(position);
			}
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_add_sbbd:
			// 显示添加设备绑定的 wheel 框
			mFjsltemSelectWheelUtil.showDialog(SBBD_FLAG_ADD,"请选择添加的设备类型");
			break;

		case R.id.btn_remove_sbbd:
			// 显示删除设备绑定的 wheel 框
			mFjsltemSelectWheelUtil.showDialog(SBBD_FLAG_REMOVE,"请选择删除的设备类型");
			break;

		case R.id.tv_save:
			saveData();
			break;

		case R.id.tv_back:
			getFragmentManager().popBackStack();
			break;
		}
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
	public void onResume() {
		isConfimDelete = false;
		MessageReceiver.ehList.add(this);
		super.onResume();
	}

	@Override
	public void onPause() {
		MessageReceiver.ehList.remove(this);
		super.onPause();
	}
	/**
	 * 保存
	 */
	private void saveData () {
		if (mQkBdDataList.size() > 0) {
			if(YzzsApplication.isConnected) {
				isDataSaveOk = false;
				sendSbbdDataToBt();
				startTime(QkCMDConstant.SEND_QK_SBBD_SAVE_TIMEOUT, "设备数据保存");
				isTimeOut = true;
			} else {
				isDataSaveOk = true;
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
		} else {
			ToastMsg(getActivity(), "绑定设备个数不能为零");
		}
	}
	/**
	 * 确定删除
	 */
	public void confimDelete (final int position) {
		String message = "如被删除设备正在运行,则该设备会停止运行!是否确认删除?";
		new AlertDialog.Builder(getActivity()).setMessage(message).setTitle("删除提示")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						removeHkbd(position);
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}
	/**
	 * 保存设备绑定数据
	 * 
	 * @param message
	 */
	private void getSaveSbbdResult(byte[] message) {
		if (message[6] == 0) {
			showLoading("正在保存设备绑定配置(2/2)");
			sendGetDataCmdToBt(QkCMDConstant.GET_SBCSPZ);
		} else { // 重启设备失败
			dismissLoading();
			ToastMsg(getActivity(), "设备配置保存失败,请重试!");
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
		byteSendData[2] = YzzsCommonUtil.intTobyte(QkCMDConstant.QK_SBBZ); // 数据域：类型
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
	
	/**
	 * 获取设备配置
	 * 
	 * @param message
	 * 
	 */
	private void getSaveSbpzResult(byte[] message) {
		if (message [5] != 0) {//数据接收成功
			return;
		}
		isDataSaveOk = true;
		StringBuilder sb = new StringBuilder();
		int startTimeCnt = message[7];//几个运行时间段
		QkMmyzFragment.sbNumS = message[6];//几个设备
		QkMmyzFragment.startTimeCnt = startTimeCnt;
		int singleCycleLength = message[8];//单个循环长度
		byte [] data = Arrays.copyOfRange(message, 9, message.length);
		for (int i = 0; i < message[6]; i++) {
			for (int j = i * singleCycleLength ; j < i * singleCycleLength + startTimeCnt * 4; j++) {
				sb.append(YzzsCommonUtil.formatStringAdd0(YzzsCommonUtil.ChangeByteToPositiveNumber(data[j]) + "", 3, 1));
			}//运行时间
			
			for (int j = i * singleCycleLength + startTimeCnt * 4; j < i * singleCycleLength + startTimeCnt * 4 + 4; j++) {
				sb.append(data[j]);
			}//设备类型，设备顺序
			sb.append(XtAppConstant.SEPARSTOR);
		}
		mSpUtil.setQkyxcs(sb.toString());
		dismissLoading();
		isTimeOut = false;
		ToastMsg(getActivity(), "保存成功");
	}

	@Override
	public void onMessage(byte[] message) {
		if (message[0] == QkCMDConstant.QK_SBBZ) {// 设备
			try {
				int cmd = YzzsCommonUtil.getCMD(message[1], message[2]); // 命令类型
				// 设备保存
				if (cmd == QkCMDConstant.SAVE_SBBDPZ) {
					getSaveSbbdResult(message);
				}
				// 得到设备配置：运行参数保存
				if (cmd == QkCMDConstant.GET_SBCSPZ) {
					getSaveSbpzResult(message);
				}

			} catch (Exception e) {
				dismissLoading();
				ToastMsg(getActivity(), TAG + "数据异常");
			}
		}
	}

	@Override
	public void onNetChange(boolean isNetConnected) {
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