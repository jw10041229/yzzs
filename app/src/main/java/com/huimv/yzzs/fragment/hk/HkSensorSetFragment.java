package com.huimv.yzzs.fragment.hk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.IndexActivity;
import com.huimv.yzzs.adapter.HkSensorSetListAdapter;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.HkCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.model.HkSensorData;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.huimv.yzzs.util.wheel.SensorItemSelectAzwzWheelUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 传感器绑定
 * 
 * @author zwl
 *
 */
public class HkSensorSetFragment extends YzzsBaseFragment implements EventHandler,
		SensorItemSelectAzwzWheelUtil.OnConfirmClickListener,ConnectTimeoutListener{

	private SharePreferenceUtil mSpUtil = YzzsApplication.getInstance().getSpUtil();
	private TextView tvSave;
	private ListView listViewCgqbd;
	private ImageView btnAddSensor, btnRemoveSensor;
	private HkSensorSetListAdapter mCgqbdListAdapter;

	private int CGQ_LX[];
	private String CGQ_WZ[];

	// 开关状态
	private static final String KGZT_OPEN = "1"; // 打开

	private static final int CGQBD_FLAG_ADD = 1; // wheel 添加绑定的标记位
	private static final int CGQBD_FLAG_REMOVE = 2; // wheel 删除绑定的标记位

	private static final int WHEEL_ITEM_WD_POS = 0; // wheel 中 温度传感器 位置号
	private static final int WHEEL_ITEM_SD_POS = 1; // wheel 中 湿度传感器 位置号
	private static final int WHEEL_ITEM_AQ_POS = 2; // wheel 中 氨气传感器 位置号
	private static final int WHEEL_ITEM_PH_POS = 3; // wheel 中 PH值传感器 位置号
	private static final int WHEEL_ITEM_DLJCB_POS = 4; // wheel 中 电流检测板位置号
	private List<HkSensorData> mHk_CgqbdDataList = null; // 保存传感器绑定数据的list

	private SensorItemSelectAzwzWheelUtil mSensorItemSelectWheelUtil;
	private HkSensorData newHk_CgqbdData; // 新添加的传感器

	// 固定位 --- 数据头长度 = 协议头 + 数据域头固定长度
	private static final int GDW_LEN_HEAD = 8;
	// 固定位 --- 数据尾长度 = 校验位 + 协议尾
	private static final int GDW_LEN_TAIL = 4;
	private boolean isTimeOut = true;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		View view = inflater.inflate(R.layout.activity_hk_cgqbd_framgent, null);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		initViews(view);
		initData();
		return view;
	}

	private void initData() {
		initOnListeners();
		initParams();
		getInitCgqbdDataFromSp();
		initBindDataToViews();
		setConnectTimeoutListener(this);
	}

	private void initViews(View view) {
		tvSave = (TextView) view.findViewById(R.id.tv_cgqbd_save);
		btnAddSensor = (ImageView) view.findViewById(R.id.btn_add_sensor);
		btnRemoveSensor = (ImageView) view.findViewById(R.id.btn_remove_sensor);

		listViewCgqbd = (ListView) view.findViewById(R.id.listview_cgqbd);
	}

	private void initOnListeners() {
		tvSave.setOnClickListener(this);
		btnAddSensor.setOnClickListener(this);
		btnRemoveSensor.setOnClickListener(this);
	}

	private void initParams() {
		// 弹出选择传感器 wheel 框
		mSensorItemSelectWheelUtil = new SensorItemSelectAzwzWheelUtil(getActivity(), "请选择传感器类型",
				R.array.hk_sensor_item_array_icon, HkSensorSetFragment.this);
		mHk_CgqbdDataList = new ArrayList<>();
		mCgqbdListAdapter = new HkSensorSetListAdapter(getActivity(), mHk_CgqbdDataList);

	}

	/**
	 * 从 sp 中读取传感器绑定数据，初始化 mHk_CgqbdDataList
	 */
	private void getInitCgqbdDataFromSp() {
		String[] cgqbdDataTdNo = mSpUtil.getCgqbdData().split(XtAppConstant.SEPARSTOR);
		int size = CommonUtil.isEmpty(cgqbdDataTdNo[0]) ? 0:cgqbdDataTdNo.length;
		mHk_CgqbdDataList.clear();
		for (int i = 0; i < size; i++) {
			HkSensorData mHk_CgqbdData = new HkSensorData();
			mHk_CgqbdData.setSblx(cgqbdDataTdNo[i].substring(0, 2)); // 设备类型
			mHk_CgqbdData.setSbbh(cgqbdDataTdNo[i].substring(2, 4)); // 设备编号
			mHk_CgqbdData.setCyjg(cgqbdDataTdNo[i].substring(4, 8)); // 采样间隔
			mHk_CgqbdData.setCgqdz(cgqbdDataTdNo[i].substring(8, 11)); // 传感器地址
			mHk_CgqbdData.setKgzt(cgqbdDataTdNo[i].substring(11, 12)); // 开关状态
			mHk_CgqbdData.setAzwz(cgqbdDataTdNo[i].substring(12, 14)); // 安装位置
			mHk_CgqbdDataList.add(mHk_CgqbdData);
		}
	}

	/**
	 * 根据 mHk_CgqbdDataList 数据，回显到ListView界面
	 */
	private void initBindDataToViews() {
		listViewCgqbd.setAdapter(mCgqbdListAdapter);
		mCgqbdListAdapter.notifyDataSetChanged();
	}

	/**
	 * 拼包 发送数据到蓝牙 格式：01 01 1234 123 0 01:设备类型 01:设备编号 1234:采样间隔 123：传感器地址 0:开关
	 * （1：打开；0：关闭）01:安装位置
	 */
	private void sendBindDataToBluetooth() {
		StringBuilder sb = new StringBuilder();
		int singleDataLength = 0; // 单条有效数据的长度
		for (int i = 0; i < mHk_CgqbdDataList.size(); i++) {
			String sblx = mHk_CgqbdDataList.get(i).getSblx(); // 设备类型
			String sbbh = mHk_CgqbdDataList.get(i).getSbbh(); // 设备编号
			String cyjg = mHk_CgqbdDataList.get(i).getCyjg(); // 采样间隔
			String cgqdz = mHk_CgqbdDataList.get(i).getCgqdz(); // 传感器地址 123
			String kgzt = mHk_CgqbdDataList.get(i).getKgzt(); // 开关状态 1
			String azwz = mHk_CgqbdDataList.get(i).getAzwz(); // 安装位置
			sb.append(sblx).append(sbbh).append(cyjg).append(cgqdz).append(kgzt).append(azwz);
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
		byteSendData[2] = YzzsCommonUtil.intTobyte(HkCMDConstant.HK_SBBZ); // 数据域：类型
		byteSendData[3] = YzzsCommonUtil.intTobyte(HkCMDConstant.SAVE_CGQBDPZ / 256); // 数据域：命令高位
		byteSendData[4] = YzzsCommonUtil.intTobyte(HkCMDConstant.SAVE_CGQBDPZ % 256); // 数据域：命令低位
																						// 14
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
		sendUnpackData(byteSendData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}

	/**
	 * 得到 list 中传感器的使用个数
	 * 
	 * @param sensorType
	 *            传感器类型
	 * @return
	 */
	private int getSensorUsedNumValue(String sensorType) {
		int sensorUsedNum = 0;
		for (HkSensorData mHk_CgqbdData : mHk_CgqbdDataList) {
			String sblx = mHk_CgqbdData.getSblx();
			if (sensorType.equals(sblx)) {
				sensorUsedNum++;
			}
		}
		return sensorUsedNum;
	}

	/**
	 * 验证该类型传感器是否可以继续添加
	 * 
	 * @param sensorType
	 *            传感器类型
	 * @param sensorMaxNum
	 *            该类型传感器最大支持个数
	 * @return
	 */
	private boolean validateSensorCanAdd(String sensorType, int sensorMaxNum) {
		return getSensorUsedNumValue(sensorType) < sensorMaxNum;
	}

	/**
	 * 添加传感器绑定
	 * 
	 * @param wheelItemPosition
	 *            wheel 中各传感器的位置
	 */
	private void addNewSensor(int wheelItemPosition) {
		newHk_CgqbdData = new HkSensorData();
		newHk_CgqbdData.setCyjg("0020");
		newHk_CgqbdData.setCgqdz("000");
		newHk_CgqbdData.setKgzt(KGZT_OPEN); // 默认是打开状态
		newHk_CgqbdData.setAzwz(XtAppConstant.CGQBD_AZWZ_SNKZ); // 默认安装到 室内空中

		// 得到各传感器的使用个数s
		int wdUsedNoValue = getSensorUsedNumValue(XtAppConstant.CGQBD_SBLX_WD);
		int sdUsedNoValue = getSensorUsedNumValue(XtAppConstant.CGQBD_SBLX_SD);
		int aqUsedNoValue = getSensorUsedNumValue(XtAppConstant.CGQBD_SBLX_AQ);
		int phUsedNoValue = getSensorUsedNumValue(XtAppConstant.CGQBD_SBLX_PH);
		int dljcbUsedNoValue = getSensorUsedNumValue(XtAppConstant.CGQBD_SBLX_DLJCB);
		// 得到各个传感器最大支持数 byte[]
		byte[] byteSbLxMax = IndexActivity.sbLxMax;
		//byte[] byteSbLxMax = {0,5,1,5,4,5, 10,5,11,5};
		int insertItemPosition = 0; // 插入item的位置
		switch (wheelItemPosition) {
		case WHEEL_ITEM_WD_POS:
			// 得到温度传感器最大支持个数
			int wdMaxNum = YzzsCommonUtil.getSbMax(byteSbLxMax, Integer.valueOf(XtAppConstant.CGQBD_SBLX_WD));
			if (validateSensorCanAdd(XtAppConstant.CGQBD_SBLX_WD, wdMaxNum)) {
				ToastMsg(getActivity(), "添加温度传感器");
				newHk_CgqbdData.setSblx(XtAppConstant.CGQBD_SBLX_WD);
				String wdSbbh = String.valueOf(wdUsedNoValue + 1);
				if (Integer.valueOf(wdSbbh) <= 9) {
					newHk_CgqbdData.setSbbh("0" + wdSbbh); // 补零
				} else {
					newHk_CgqbdData.setSbbh(wdSbbh);
				}
				insertItemPosition = wdUsedNoValue;
			} else {
				ToastMsg(getActivity(), "不能再添加更多的温度传感器");
				return;

			}
			break;

		case WHEEL_ITEM_SD_POS:
			// 得到湿度传感器最大支持个数
			int sdMaxNum = YzzsCommonUtil.getSbMax(byteSbLxMax, Integer.valueOf(XtAppConstant.CGQBD_SBLX_SD));
			if (validateSensorCanAdd(XtAppConstant.CGQBD_SBLX_SD, sdMaxNum)) {
				ToastMsg(getActivity(), "添加湿度传感器");
				newHk_CgqbdData.setSblx(XtAppConstant.CGQBD_SBLX_SD);
				String sdSbbh = String.valueOf(sdUsedNoValue + 1);
				if (Integer.valueOf(sdSbbh) <= 9) {
					newHk_CgqbdData.setSbbh("0" + sdSbbh); // 补零
				} else {
					newHk_CgqbdData.setSbbh(sdSbbh);
				}
				// 新添加湿度传感器位置：温度 + 湿度
				insertItemPosition = wdUsedNoValue + sdUsedNoValue;
			} else {
				ToastMsg(getActivity(), "不能再添加更多的湿度传感器");
				return;
			}
			break;

		case WHEEL_ITEM_AQ_POS:
			// 得到氨气传感器最大支持个数
			int aqMaxNum = YzzsCommonUtil.getSbMax(byteSbLxMax, Integer.valueOf(XtAppConstant.CGQBD_SBLX_AQ));
			if (validateSensorCanAdd(XtAppConstant.CGQBD_SBLX_AQ, aqMaxNum)) {
				ToastMsg(getActivity(), "添加氨气传感器");
				newHk_CgqbdData.setSblx(XtAppConstant.CGQBD_SBLX_AQ);
				String aqSbbh = String.valueOf(aqUsedNoValue + 1);
				if (Integer.valueOf(aqSbbh) <= 9) {
					newHk_CgqbdData.setSbbh("0" + aqSbbh); // 补零
				} else {
					newHk_CgqbdData.setSbbh(aqSbbh);
				}
				// 新添加湿度传感器位置：温度 + 湿度 + 温湿度 + 氨气
				insertItemPosition = wdUsedNoValue + sdUsedNoValue + aqUsedNoValue;
			} else {
				ToastMsg(getActivity(), "不能再添加更多的氨气传感器");
				return;
			}
			break;

		case WHEEL_ITEM_PH_POS:
			// 得到PH值传感器最大支持个数
			int phMaxNum = YzzsCommonUtil.getSbMax(byteSbLxMax, Integer.valueOf(XtAppConstant.CGQBD_SBLX_PH));
			if (validateSensorCanAdd(XtAppConstant.CGQBD_SBLX_PH, phMaxNum)) {
				ToastMsg(getActivity(), "添加PH值传感器");
				newHk_CgqbdData.setSblx(XtAppConstant.CGQBD_SBLX_PH);
				String phSbbh = String.valueOf(phUsedNoValue + 1);
				if (Integer.valueOf(phSbbh) <= 9) {
					newHk_CgqbdData.setSbbh("0" + phSbbh); // 补零
				} else {
					newHk_CgqbdData.setSbbh(phSbbh);
				}
				// 新添加湿度传感器位置：温度 + 湿度 + 氨气 + ph值
				insertItemPosition = wdUsedNoValue + sdUsedNoValue + aqUsedNoValue + phUsedNoValue;
			} else {
				ToastMsg(getActivity(), "不能再添加更多的PH值传感器");
				return;
			}
			break;
			case WHEEL_ITEM_DLJCB_POS:
				// 得到电流检测最大支持个数
				int dljcbMaxNum = YzzsCommonUtil.getSbMax(byteSbLxMax, Integer.valueOf(XtAppConstant.CGQBD_SBLX_DLJCB));
				if (validateSensorCanAdd(XtAppConstant.CGQBD_SBLX_DLJCB, dljcbMaxNum)) {
					ToastMsg(getActivity(), "添加电流检测板");
					newHk_CgqbdData.setSblx(XtAppConstant.CGQBD_SBLX_DLJCB);
					String dljcbSbbh = String.valueOf(dljcbUsedNoValue + 1);
					if (Integer.valueOf(dljcbSbbh) <= 9) {
						newHk_CgqbdData.setSbbh("0" + dljcbSbbh); // 补零
					} else {
						newHk_CgqbdData.setSbbh(dljcbSbbh);
					}
					// 新添加湿度传感器位置：温度 + 湿度 + 氨气 + ph值 + 电流检测板
					insertItemPosition = wdUsedNoValue + sdUsedNoValue + aqUsedNoValue + phUsedNoValue + dljcbUsedNoValue;
				} else {
					ToastMsg(getActivity(), "不能再添加更多的电流检测板");
					return;
				}
				break;
		}

		mHk_CgqbdDataList.add(insertItemPosition, newHk_CgqbdData);
		Logger.e(newHk_CgqbdData.getSbbh());
		mCgqbdListAdapter.notifyDataSetChanged();
		listViewCgqbd.setSelection(insertItemPosition);
	}

	/**
	 * 验证各个传感器是否可以被删除
	 * 
	 * @param sensorType
	 *            传感器类型
	 * @return true 可以被删除 ； false 不可以被删除
	 */
	private boolean validateSensorCanRemove(String sensorType) {
		return getSensorUsedNumValue(sensorType) != 0;
	}

	/**
	 * 删除传感器绑定
	 * 
	 * @param wheelItemPosition
	 *            wheel 中各传感器的位置
	 */
	private void removeSensor(int wheelItemPosition) {
		newHk_CgqbdData = new HkSensorData();
		newHk_CgqbdData.setCyjg("0020");
		newHk_CgqbdData.setCgqdz("000");
		newHk_CgqbdData.setKgzt(KGZT_OPEN); // 默认是打开状态

		// 得到各传感器使用个数
		int wdUsedNum = getSensorUsedNumValue(XtAppConstant.CGQBD_SBLX_WD);
		int sdUsedNum = getSensorUsedNumValue(XtAppConstant.CGQBD_SBLX_SD);
		int aqUsedNum = getSensorUsedNumValue(XtAppConstant.CGQBD_SBLX_AQ);
		int phUsedNum = getSensorUsedNumValue(XtAppConstant.CGQBD_SBLX_PH);
		int dljcbUsedNum = getSensorUsedNumValue(XtAppConstant.CGQBD_SBLX_DLJCB);
		int removeItemPosition = 0; // 删除item的位置
		switch (wheelItemPosition) {
		case WHEEL_ITEM_WD_POS:
			if (validateSensorCanRemove(XtAppConstant.CGQBD_SBLX_WD)) {
				// 删除温度传感器位置：温度- 1
				removeItemPosition = wdUsedNum - 1;
				ToastMsg(getActivity(), "删除温度传感器绑定");
			} else {
				ToastMsg(getActivity(), "当前没有绑定温度传感器");
				return;
			}
			break;

		case WHEEL_ITEM_SD_POS:
			if (validateSensorCanRemove(XtAppConstant.CGQBD_SBLX_SD)) {
				// 删除湿度传感器位置：温度 + 湿度 - 1
				removeItemPosition = wdUsedNum + sdUsedNum - 1;
				ToastMsg(getActivity(), "删除湿度传感器传感器");
			} else {
				ToastMsg(getActivity(), "当前没有绑定湿度传感器");
				return;
			}
			break;

		case WHEEL_ITEM_AQ_POS:
			if (validateSensorCanRemove(XtAppConstant.CGQBD_SBLX_AQ)) {
				// 删除氨气传感器位置：温度 + 湿度 + 氨气 - 1
				removeItemPosition = wdUsedNum + sdUsedNum + aqUsedNum - 1;
				ToastMsg(getActivity(), "删除氨气传感器");
			} else {
				ToastMsg(getActivity(), "当前没有绑定氨气传感器");
				return;
			}
			break;

		case WHEEL_ITEM_PH_POS:
			if (validateSensorCanRemove(XtAppConstant.CGQBD_SBLX_PH)) {
				// 删除氨气传感器位置：温度 + 湿度 + 氨气 + 地面温度 + PH值 - 1
				removeItemPosition = wdUsedNum + sdUsedNum + aqUsedNum + phUsedNum - 1;
				ToastMsg(getActivity(), "删除PH值传感器");
			} else {
				ToastMsg(getActivity(), "当前没有绑定PH值传感器");
				return;
			}
			break;
			case WHEEL_ITEM_DLJCB_POS:
				if (validateSensorCanRemove(XtAppConstant.CGQBD_SBLX_DLJCB)) {
					// 删除氨气传感器位置：温度 + 湿度 + 氨气 + 地面温度 + PH值 - 1
					removeItemPosition = wdUsedNum + sdUsedNum + aqUsedNum + phUsedNum + dljcbUsedNum - 1;
					ToastMsg(getActivity(), "删除电流检测板");
				} else {
					ToastMsg(getActivity(), "当前没有绑定电流检测板");
					return;
				}
				break;
		}
		mHk_CgqbdDataList.remove(removeItemPosition);
		mCgqbdListAdapter.notifyDataSetChanged();
		listViewCgqbd.setSelection(removeItemPosition);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_cgqbd_save:
			if (mHk_CgqbdDataList.size() > 0) {
				if(YzzsApplication.isConnected) {
					showLoading("正在保存传感器绑定数据");
					sendBindDataToBluetooth();
					startTime(XtAppConstant.SEND_CMD_TIMEOUT, "传感器数据保存");
					isTimeOut = true;
				} else {
					ToastMsg(getActivity(), getString(R.string.disconnected));
				}
			} else {
				ToastMsg(getActivity(), "绑定的传感器个数不能为零");
			}
			break;

		case R.id.btn_add_sensor:
			mSensorItemSelectWheelUtil.showDialog(CGQBD_FLAG_ADD, 0,"请选择添加的传感器类型");
			break;
		case R.id.btn_remove_sensor:
			mSensorItemSelectWheelUtil.showDialog(CGQBD_FLAG_REMOVE, 0,"请选择删除的传感器类型");
			break;
		}

	}

	@Override
	public void onConnected(boolean isConnected) {
		if (!isConnected) {
			ToastMsg(getActivity(), "蓝牙连接已断开");
		}
	}

	@Override
	public void onReady(boolean isReady) {
		// TODO Auto-generated method stub

	}

	/**
	 * 保存传感器绑定数据
	 * 
	 * @param backData
	 */
	private void getSaveCgqbdResult(byte[] backData) {
		if (backData[0] == HkCMDConstant.HK_SBBZ) {// 环控
			try {
				int cmd = YzzsCommonUtil.getCMD(backData[1], backData[2]);// 命令类型

				// 传感器保存
				if (cmd == HkCMDConstant.SAVE_CGQBDPZ) {
					isTimeOut = false;
					dismissLoading();
					if (backData[6] == 0) {
						CGQ_LX = new int[backData[7]];
						CGQ_WZ = new String[backData[7]];
						for (int i = 0; i < backData[7]; i ++) {
							CGQ_LX[i] = backData[i * 3 + 8];//绑定的传感器类型
							CGQ_WZ[i] = backData[i * 3 + 8 + 1] + "" + backData[i * 3 + 8 + 2];//传感器位置
						}
						IndexActivity.CGQ_LX = CGQ_LX;
						IndexActivity.CGQ_WZ = CGQ_WZ;
						ToastMsg(getActivity(), "保存成功!请手动重启设备使配置生效");
					} else {
						ToastMsg(getActivity(), "传感器配置保存失败,请重试!");
					}
				}

			} catch (Exception e) {
				dismissLoading();
				ToastMsg(getActivity(), "数据异常");
			}
		}
	}

	@Override
	public void onMessage(byte[] message) {
		getSaveCgqbdResult(message);
	}

	@Override
	public void onNetChange(boolean isNetConnected) {

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
	public void onConfirm(int wheelItemPosition, int showDialogPos, int whichItemPos) {
		if (showDialogPos == CGQBD_FLAG_ADD) {
			addNewSensor(wheelItemPosition);
		}
		if (showDialogPos == CGQBD_FLAG_REMOVE) {
			removeSensor(wheelItemPosition);
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
