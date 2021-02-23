package com.huimv.yzzs.fragment.qk;

import java.util.ArrayList;
import java.util.List;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.IndexActivity;
import com.huimv.yzzs.adapter.QkSensorSetListAdapter;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.constant.QkCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.model.QkSensorData;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.huimv.yzzs.util.wheel.SensorItemSelectAzwzWheelUtil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 传感器绑定
 * 
 * @author jw
 *
 */
public class QkSensorSetFragment extends YzzsBaseFragment implements EventHandler,
		SensorItemSelectAzwzWheelUtil.OnConfirmClickListener {
	private final static String TAG = QkSensorSetFragment.class.getSimpleName();
	private SharePreferenceUtil mSpUtil = YzzsApplication.getInstance().getSpUtil();
	private TextView tvSave, tvBack;
	private ListView sensorSetlistView;
	private ImageView btnAddSensor, btnRemoveSensor;
	private QkSensorSetListAdapter mSensorSetListAdapter;

	private int CGQ_LX[];
	private String CGQ_WZ[];

	// 开关状态
	private static final String KGZT_OPEN = "1"; // 打开

	private static final int SENSOR_FLAG_ADD = 1; // wheel 添加绑定的标记位
	private static final int SENSOR_FLAG_REMOVE = 2; // wheel 删除绑定的标记位

	private static final int WHEEL_ITEM_SY_POS = 0; // wheel 中 水压传感器 位置号
	private static final int WHEEL_ITEM_SL_POS = 1; // wheel 中 水流传感器 位置号
	private static final int WHEEL_ITEM_PH_POS = 2; // wheel 中 Ph传感器 位置号
	private List<QkSensorData> mQk_sensorSetDataList = null; // 保存传感器绑定数据的list

	private SensorItemSelectAzwzWheelUtil mSensorItemSelectWheelUtil;
	private QkSensorData Qk_sensorSetData; // 新添加的传感器

	// 固定位 --- 数据头长度 = 协议头 + 数据域头固定长度
	private static final int GDW_LEN_HEAD = 8;
	// 固定位 --- 数据尾长度 = 校验位 + 协议尾
	private static final int GDW_LEN_TAIL = 4;

	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		View view = inflater.inflate(R.layout.activity_qk_cgqbd_framgent, null);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();

		initViews(view);
		initOnListeners();
		initParams();
		getInitCgqbdDataFromSp();
		initBindDataToViews();

		return view;
	}

	private void initViews(View view) {
		tvSave = (TextView) view.findViewById(R.id.tv_cgqbd_save);
		tvBack = (TextView) view.findViewById(R.id.tv_cgqbd_back);
		btnAddSensor = (ImageView) view.findViewById(R.id.btn_add_sensor);
		btnRemoveSensor = (ImageView) view.findViewById(R.id.btn_remove_sensor);

		sensorSetlistView = (ListView) view.findViewById(R.id.listview_cgqbd);
	}

	private void initOnListeners() {
		tvSave.setOnClickListener(this);
		tvBack.setOnClickListener(this);
		btnAddSensor.setOnClickListener(this);
		btnRemoveSensor.setOnClickListener(this);
	}

	private void initParams() {
		// 弹出选择传感器 wheel 框
		mSensorItemSelectWheelUtil = new SensorItemSelectAzwzWheelUtil(getActivity(), "请选择传感器类型",
				R.array.qk_sensor_item_array_icon, QkSensorSetFragment.this);
		mQk_sensorSetDataList = new ArrayList<QkSensorData>();
		mSensorSetListAdapter = new QkSensorSetListAdapter(getActivity(), mQk_sensorSetDataList);
	}

	/**
	 * 从 sp 中读取传感器绑定数据，初始化 mQk_sensorSetDataList
	 */
	private void getInitCgqbdDataFromSp() {
		String[] sensorData = mSpUtil.getCgqbdData().split(XtAppConstant.SEPARSTOR);
		int size = CommonUtil.isEmpty(sensorData[0]) ? 0:sensorData.length;
		mQk_sensorSetDataList.clear();
		for (int i = 0; i < size; i++) {
			QkSensorData mQk_sensorData = new QkSensorData();
			mQk_sensorData.setSblx(sensorData[i].substring(0, 2)); // 设备类型
			mQk_sensorData.setSbbh(sensorData[i].substring(2, 4)); // 设备编号
			mQk_sensorData.setCyjg(sensorData[i].substring(4, 8)); // 采样间隔
			mQk_sensorData.setCgqdz(sensorData[i].substring(8, 11)); // 传感器地址
			mQk_sensorData.setKgzt(sensorData[i].substring(11, 12)); // 开关状态
			mQk_sensorData.setAzwz(sensorData[i].substring(12, 14)); // 安装位置
			mQk_sensorSetDataList.add(mQk_sensorData);
		}
	}

	/**
	 * 根据 mQk_sensorSetDataList 数据，回显到ListView界面
	 */
	private void initBindDataToViews() {
		sensorSetlistView.setAdapter(mSensorSetListAdapter);
		mSensorSetListAdapter.notifyDataSetChanged();
	}

	/**
	 * 拼包 发送数据到蓝牙 格式：01 01 1234 123 0 01:设备类型 01:设备编号 1234:采样间隔 123：传感器地址 0:开关
	 * （1：打开；0：关闭）01:安装位置
	 */
	private void sendBindDataToBluetooth() {
		StringBuilder sb = new StringBuilder();
		int singleDataLength = 0; // 单条有效数据的长度
		for (int i = 0; i < mQk_sensorSetDataList.size(); i++) {
			String sblx = mQk_sensorSetDataList.get(i).getSblx(); // 设备类型
			String sbbh = mQk_sensorSetDataList.get(i).getSbbh(); // 设备编号
			String cyjg = mQk_sensorSetDataList.get(i).getCyjg(); // 采样间隔
			String cgqdz = mQk_sensorSetDataList.get(i).getCgqdz(); // 传感器地址 123
			String kgzt = mQk_sensorSetDataList.get(i).getKgzt(); // 开关状态 1
			String azwz = mQk_sensorSetDataList.get(i).getAzwz(); // 安装位置
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
		byteSendData[2] = YzzsCommonUtil.intTobyte(QkCMDConstant.QK_SBBZ); // 数据域：类型
		byteSendData[3] = YzzsCommonUtil.intTobyte(QkCMDConstant.SAVE_CGQBDPZ / 256); // 数据域：命令高位
		byteSendData[4] = YzzsCommonUtil.intTobyte(QkCMDConstant.SAVE_CGQBDPZ % 256); // 数据域：命令低位
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
		for (QkSensorData mHk_CgqbdData : mQk_sensorSetDataList) {
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
		Qk_sensorSetData = new QkSensorData();
		Qk_sensorSetData.setCyjg("0020");
		Qk_sensorSetData.setCgqdz("000");
		Qk_sensorSetData.setKgzt(KGZT_OPEN); // 默认是打开状态
		Qk_sensorSetData.setAzwz(XtAppConstant.CGQBD_AZWZ_SNKZ); // 默认安装到 室内空中

		// 得到各传感器的使用个数s
		int syUsedNoValue = getSensorUsedNumValue(XtAppConstant.CGQBD_SBLX_SY);
		int slUsedNoValue = getSensorUsedNumValue(XtAppConstant.CGQBD_SBLX_SL);
		int phUsedNoValue = getSensorUsedNumValue(XtAppConstant.CGQBD_SBLX_PH);
		// 得到各个传感器最大支持数 byte[]
		byte[] byteSbLxMax = IndexActivity.sbLxMax;
		//byte[] byteSbLxMax = {0,5,1,5,4,5, 10,5,11,5};
		int insertItemPosition = 0; // 插入item的位置
		switch (wheelItemPosition) {
		case WHEEL_ITEM_SY_POS:
			// 得到水压传感器最大支持个数
			int wdMaxNum = YzzsCommonUtil.getSbMax(byteSbLxMax, Integer.valueOf(XtAppConstant.CGQBD_SBLX_SY));
			if (validateSensorCanAdd(XtAppConstant.CGQBD_SBLX_SY, wdMaxNum)) {
				ToastMsg(getActivity(), "添加水压传感器");
				Qk_sensorSetData.setSblx(XtAppConstant.CGQBD_SBLX_SY);
				String wdSbbh = String.valueOf(syUsedNoValue + 1);
				if (Integer.valueOf(wdSbbh) <= 9) {
					Qk_sensorSetData.setSbbh("0" + wdSbbh); // 补零
				} else {
					Qk_sensorSetData.setSbbh(wdSbbh);
				}
				insertItemPosition = syUsedNoValue;
			} else {
				ToastMsg(getActivity(), "不能再添加更多的水压传感器");
				return;

			}
			break;

		case WHEEL_ITEM_SL_POS:
			// 得到水流传感器最大支持个数
			int sdMaxNum = YzzsCommonUtil.getSbMax(byteSbLxMax, Integer.valueOf(XtAppConstant.CGQBD_SBLX_SL));
			if (validateSensorCanAdd(XtAppConstant.CGQBD_SBLX_SL, sdMaxNum)) {
				ToastMsg(getActivity(), "添加水流传感器");
				Qk_sensorSetData.setSblx(XtAppConstant.CGQBD_SBLX_SL);
				String sdSbbh = String.valueOf(slUsedNoValue + 1);
				if (Integer.valueOf(sdSbbh) <= 9) {
					Qk_sensorSetData.setSbbh("0" + sdSbbh); // 补零
				} else {
					Qk_sensorSetData.setSbbh(sdSbbh);
				}
				// 新添加湿度传感器位置：水压 + 水流
				insertItemPosition = syUsedNoValue + slUsedNoValue;
			} else {
				ToastMsg(getActivity(), "不能再添加更多的水流传感器");
				return;
			}
			break;
		case WHEEL_ITEM_PH_POS:
			// 得到PH传感器最大支持个数
			int phMaxNum = YzzsCommonUtil.getSbMax(byteSbLxMax, Integer.valueOf(XtAppConstant.CGQBD_SBLX_PH));
			if (validateSensorCanAdd(XtAppConstant.CGQBD_SBLX_PH, phMaxNum)) {
				ToastMsg(getActivity(), "添加PH传感器");
				Qk_sensorSetData.setSblx(XtAppConstant.CGQBD_SBLX_PH);
				String phSbbh = String.valueOf(phUsedNoValue + 1);
				if (Integer.valueOf(phSbbh) <= 9) {
					Qk_sensorSetData.setSbbh("0" + phSbbh); // 补零
				} else {
					Qk_sensorSetData.setSbbh(phSbbh);
				}
				// 新添加湿度传感器位置：水压 + 水流
				insertItemPosition = syUsedNoValue + slUsedNoValue + phUsedNoValue;
			} else {
				ToastMsg(getActivity(), "不能再添加更多的PH传感器");
				return;
			}
			break;
		}

		mQk_sensorSetDataList.add(insertItemPosition, Qk_sensorSetData);
		mSensorSetListAdapter.notifyDataSetChanged();
		sensorSetlistView.setSelection(insertItemPosition);
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
		Qk_sensorSetData = new QkSensorData();
		Qk_sensorSetData.setCyjg("0020");
		Qk_sensorSetData.setCgqdz("000");
		Qk_sensorSetData.setKgzt(KGZT_OPEN); // 默认是打开状态

		// 得到各传感器使用个数
		int syUsedNum = getSensorUsedNumValue(XtAppConstant.CGQBD_SBLX_SY);
		int slUsedNum = getSensorUsedNumValue(XtAppConstant.CGQBD_SBLX_SL);
		int phUsedNum = getSensorUsedNumValue(XtAppConstant.CGQBD_SBLX_PH);
		int removeItemPosition = 0; // 删除item的位置
		switch (wheelItemPosition) {
		case WHEEL_ITEM_SY_POS:
			if (validateSensorCanRemove(XtAppConstant.CGQBD_SBLX_SY)) {
				// 删除温度传感器位置：水压- 1
				removeItemPosition = syUsedNum - 1;
				ToastMsg(getActivity(), "删除水压传感器绑定");
			} else {
				ToastMsg(getActivity(), "当前没有绑定水压传感器");
				return;
			}
			break;

		case WHEEL_ITEM_SL_POS:
			if (validateSensorCanRemove(XtAppConstant.CGQBD_SBLX_SL)) {
				// 删除湿度传感器位置：水压 + 水流 - 1
				removeItemPosition = syUsedNum + slUsedNum - 1;
				ToastMsg(getActivity(), "删除水流传感器传感器");
			} else {
				ToastMsg(getActivity(), "当前没有绑定水流传感器");
				return;
			}
			break;
		case WHEEL_ITEM_PH_POS:
			if (validateSensorCanRemove(XtAppConstant.CGQBD_SBLX_PH)) {
				// 删除湿度传感器位置：水压 + 水流 + PH - 1
				removeItemPosition = syUsedNum + slUsedNum + phUsedNum- 1;
				ToastMsg(getActivity(), "删除PH传感器传感器");
			} else {
				ToastMsg(getActivity(), "当前没有绑定PH传感器");
				return;
			}
			break;
		}
		mQk_sensorSetDataList.remove(removeItemPosition);
		mSensorSetListAdapter.notifyDataSetChanged();
		sensorSetlistView.setSelection(removeItemPosition);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_cgqbd_save:
			if (mQk_sensorSetDataList.size() > 0) {
				showLoading("正在保存传感器绑定数据");
				sendBindDataToBluetooth();
			} else {
				ToastMsg(getActivity(), "绑定的传感器个数不能为零");
			}
			break;

		case R.id.tv_cgqbd_back:
			getFragmentManager().popBackStack();
			break;

		case R.id.btn_add_sensor:
			mSensorItemSelectWheelUtil.showDialog(SENSOR_FLAG_ADD, 0,"请选择添加的传感器类型");
			break;
		case R.id.btn_remove_sensor:
			mSensorItemSelectWheelUtil.showDialog(SENSOR_FLAG_REMOVE, 0,"请选择删除的传感器类型");
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

	/**
	 * 保存传感器绑定数据
	 * 
	 * @param message
	 */
	private void getSaveCgqbdResult(byte[] message) {
		if (message[0] == QkCMDConstant.QK_SBBZ) {// 环控
			try {
				int cmd = YzzsCommonUtil.getCMD(message[1], message[2]);// 命令类型
				// 传感器保存
				if (cmd == QkCMDConstant.SAVE_CGQBDPZ) {
					dismissLoading();
					if (message[6] == 0) {
						CGQ_LX = new int[message[7]];
						CGQ_WZ = new String[message[7]];
						for (int i = 0; i < message[7]; i ++) {
							CGQ_LX[i] = message[i * 3 + 8];//绑定的传感器类型
							CGQ_WZ[i] = message[i * 3 + 8 + 1] + "" + message[i * 3 + 8 + 2];//传感器位置
						}
						IndexActivity.CGQ_LX = CGQ_LX;
						IndexActivity.CGQ_WZ = CGQ_WZ;
						ToastMsg(getActivity(), "传感器配置保存成功!");
					} else {
						ToastMsg(getActivity(), "传感器配置保存失败,请重试!");
					}
				}

			} catch (Exception e) {
				dismissLoading();
				ToastMsg(getActivity(), TAG + "数据异常");
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
		if (showDialogPos == SENSOR_FLAG_ADD) {
			addNewSensor(wheelItemPosition);
		}
		if (showDialogPos == SENSOR_FLAG_REMOVE) {
			removeSensor(wheelItemPosition);
		}
	}
}
