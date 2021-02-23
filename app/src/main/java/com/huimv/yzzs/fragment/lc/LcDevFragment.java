package com.huimv.yzzs.fragment.lc;

import java.util.ArrayList;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.IndexActivity;
import com.huimv.yzzs.adapter.SbbdListAdapter;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.HkCMDConstant;
import com.huimv.yzzs.constant.LcCMDConstant;
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
 * 料槽设备
 * 
 * @author jiangwei
 */
public class LcDevFragment extends YzzsBaseFragment
		implements EventHandler, OnClickListener, ItemSelectWheelUtil.OnConfirmClickListener,ConnectTimeoutListener{
	//private final static String TAG = LcDevFragment.class.getSimpleName();
	private SharePreferenceUtil mSpUtil;
	private TextView tv_back, tv_save;
	private ImageView btnAddDev, btnRemoveDev;
	private ListView listViewDev;
	private ItemSelectWheelUtil mLcItemSelectWheelUtil;

	private String[] devBdData; // 保存sp中环控绑定数据
	private final ArrayList<SbbdData> mDevBdDataList = new ArrayList<>();
	private SbbdListAdapter mDevbdListAdapter;

	private static final int DEV_BD_FLAG_ADD = 1; // wheel 添加绑定的标记位
	private static final int DEV_BD_FLAG_REMOVE = 2; // wheel 删除绑定的标记位

	private static final int WHEEL_ITEM_LC_POS = 0; // wheel 中 item 的料槽位置号

	private static final String DEV_BD_KGZT_OPEN = "1"; // 开关状态 - 打开
	private static final String DEV_BD_KGZT_CLOSE = "0"; // 开关状态 - 关闭

	// 固定位 --- 数据头长度 = 协议头 + 数据域头固定长度
	private static final int GDW_LEN_HEAD = 8;
	// 固定位 --- 数据尾长度 = 校验位 + 协议尾
	private static final int GDW_LEN_TAIL = 4;

	private boolean isConfimDelete = false;
	private boolean isTimeOut = true;
	private static boolean isDataSaveOk = true;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_lc_dev_framgent, null);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		mLcItemSelectWheelUtil = new ItemSelectWheelUtil(getActivity(), "请选择设备类型", 
				getResources().getStringArray(R.array.lc_device_item_array),LcDevFragment.this);
		initView(view);
		initData();
		initParams();

		return view;
	}

	private void initData() {
		setConnectTimeoutListener(this);
		getInitDevBdData();
	}

	private void initView(View view) {
		tv_back = (TextView) view.findViewById(R.id.tv_back);
		tv_save = (TextView) view.findViewById(R.id.tv_save);
		listViewDev = (ListView) view.findViewById(R.id.lv_dev);
		btnAddDev = (ImageView) view.findViewById(R.id.btn_add_dev);
		btnRemoveDev = (ImageView) view.findViewById(R.id.btn_remove_dev);

		tv_back.setOnClickListener(this);
		tv_save.setOnClickListener(this);
		btnAddDev.setOnClickListener(this);
		btnRemoveDev.setOnClickListener(this);

	}

	/**
	 * 解析设备绑定数据
	 */
	private void getInitDevBdData() {
		devBdData = mSpUtil.getLcSbbd().split(XtAppConstant.SEPARSTOR);
		int size = CommonUtil.isEmpty(devBdData[0]) ? 0:devBdData.length;
		mDevBdDataList.clear();
		for (int i = 0; i < size; i++) {
			SbbdData mDevBdData = new SbbdData();
			mDevBdData.setSbLx(devBdData[i].substring(0, 2)); // 设备类型
			mDevBdData.setSbXh(devBdData[i].substring(2, 4)); // 设备编号
			mDevBdData.setGlz(devBdData[i].substring(4, 9)); // 功率值
			mDevBdData.setTdKg(devBdData[i].substring(9, 10)); // 通道开关
			mDevBdData.setHeKg(devBdData[i].substring(10, 11)); // 霍尔开关
			mDevBdDataList.add(mDevBdData);
		}
	}

	private void initParams() {
		mDevbdListAdapter = new SbbdListAdapter(getActivity(), mDevBdDataList);
		listViewDev.setAdapter(mDevbdListAdapter);
	}

	/**
	 * 得到 list 中各个设备的使用个数
	 * 
	 * @param deviceType
	 * @return
	 */
	private int getDeviceUsedNumValue(String deviceType) {
		int deviceUsedNum = 0;
		for (SbbdData mHk_BdData : mDevBdDataList) {
			String sblx = mHk_BdData.getSbLx();
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
	 * 添加环控绑定
	 * 
	 * @param wheelItemPos
	 *            环控绑定类型的标记位；0：料槽；1：水帘
	 */
	private void addDev(int wheelItemPos) {
		SbbdData mNewDevBdData = new SbbdData();
		mNewDevBdData.setGlz("00000"); // 功率值
		mNewDevBdData.setIsBd("1"); // 是否绑定：该字段无用，设置默认值
		mNewDevBdData.setTdKg(DEV_BD_KGZT_OPEN); // 使能开关：默认打开
		mNewDevBdData.setHeKg(DEV_BD_KGZT_CLOSE); // 霍尔开关：默认关闭
		int lcMaxNoValue = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_LC); // 料槽最大编号
		byte[] sbLxMax = IndexActivity.sbLxMax;
		int insertItemPosition = 0; // 插入item的位置
		
		if (wheelItemPos == WHEEL_ITEM_LC_POS) {
			// 得到料槽的最大支持个数
			int lcMaxNum = YzzsCommonUtil.getSbMax(sbLxMax, Integer.valueOf(XtAppConstant.SBBD_SBLX_LC));
			if (validateDeviceCanAdd(XtAppConstant.SBBD_SBLX_LC, lcMaxNum)) {
				ToastMsg(getActivity(), "添加料槽设备");
				mNewDevBdData.setSbLx(XtAppConstant.SBBD_SBLX_LC);
				String fjSbxh = String.valueOf(lcMaxNoValue + 1);
				String sbxh = YzzsCommonUtil.formatStringAdd0(fjSbxh, 2, 1);
				mNewDevBdData.setSbXh(sbxh);
				insertItemPosition = lcMaxNoValue;
			} else {
				ToastMsg(getActivity(), "不能再添加更多的料槽");
				return;
			}
		}
		
		mDevBdDataList.add(insertItemPosition, mNewDevBdData);
		mDevbdListAdapter.notifyDataSetChanged();
		listViewDev.setSelection(insertItemPosition);
	}

	/**
	 * 验证料槽 湿帘设备可以被删除
	 * 
	 * @param deviceType 设备类型
	 * @return true:可以被删除； false： 不可以被删除
	 */
	private boolean  validateDeviceCanRemove(String deviceType) {
		return getDeviceUsedNumValue(deviceType) != 0;
	}

	/**
	 * 删除环控绑定
	 * 
	 * @param wheelItemPos
	 *            环控绑定类型的标记位；0：料槽；1：水帘
	 */
	private void removeDev(int wheelItemPos) {
		int lcUsedNum = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_LC); // 料槽最大编号
		int removeItemPosition = 0;
		if (wheelItemPos == WHEEL_ITEM_LC_POS) {
			if(validateDeviceCanRemove(XtAppConstant.SBBD_SBLX_LC)){
				// 删除料槽位置 = 料槽 - 1 
				removeItemPosition = lcUsedNum - 1;
				ToastMsg(getActivity(), "删除料槽设备");
			} else {
				ToastMsg(getActivity(), "当前没有绑定料槽");
				return;
			}
		}
		mDevBdDataList.remove(removeItemPosition);
		mDevbdListAdapter.notifyDataSetChanged();
		listViewDev.setSelection(removeItemPosition);
	}

	private void sendDevbdDataToBt() {
		/**
		 * 1 01200 1 1 01
		 * 
		 * 1:设备类型 01200:功率 1:开关 1:霍尔开关 01:设备顺序
		 * 
		 * @param hkbdTd1
		 */
		StringBuilder sb = new StringBuilder();
		int singleDataLength = 0; // 单条有效数据的长度
		for (int i = 0; i < mDevBdDataList.size(); i++) {
			String sblx = mDevBdDataList.get(i).getSbLx(); // 设备类型
			String sbxh = mDevBdDataList.get(i).getSbXh(); // 设备编号
			String glz = mDevBdDataList.get(i).getGlz(); // 功率值
			String tdkg = mDevBdDataList.get(i).getTdKg(); // 通道开关
			String hekg = mDevBdDataList.get(i).getHeKg(); // 霍尔开关
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
		byte[] message = new byte[dataLength]; // 构造 byte 数组
		message[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH); // 协议头 “h”
		message[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM); // 协议头 “m”
		message[2] = YzzsCommonUtil.intTobyte(LcCMDConstant.LC_SBBZ); // 数据域：类型
		message[3] = YzzsCommonUtil.intTobyte(LcCMDConstant.LC_SAVE_DEV / 256); // 数据域：命令高位
		message[4] = YzzsCommonUtil.intTobyte(LcCMDConstant.LC_SAVE_DEV % 256); // 数据域：命令低位
																						// 12
		message[5] = YzzsCommonUtil.intTobyte(dataLength / 256); // 数据域：数据总长度高位
		message[6] = YzzsCommonUtil.intTobyte(dataLength % 256); // 数据域：数据总长度低位
		message[7] = YzzsCommonUtil.intTobyte(singleDataLength); // 数据域：单条有效数据长度
		for (int i = GDW_LEN_HEAD; i < len; i++) {
			int data = Integer.valueOf(circleDataStr.substring(i - GDW_LEN_HEAD, i - GDW_LEN_HEAD + 1));
			message[i] = YzzsCommonUtil.intTobyte(data);
		}
		message[dataLength - 4] = YzzsCommonUtil.intTobyte(111); // 校验位：暂时不用
		message[dataLength - 3] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE); // 协议尾：“e”
		message[dataLength - 2] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN); // 协议尾：“n”
		message[dataLength - 1] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD); // 协议尾：“d”
		showLoading("正在保存设备");
		sendUnpackData(message, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}


	@Override
	public void onConfirm(int position, int i) {
		if (i == DEV_BD_FLAG_ADD) {
			addDev(position);
		}
		if (i == DEV_BD_FLAG_REMOVE) {
			if (isConfimDelete) {
				removeDev(position);
			} else {
				confimDelete(position);
			}
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_add_dev:
			// 显示添加环控绑定的 wheel 框
			mLcItemSelectWheelUtil.showDialog(DEV_BD_FLAG_ADD,"请选择添加的设备类型");
			break;

		case R.id.btn_remove_dev:
			// 显示删除环控绑定的 wheel 框
			mLcItemSelectWheelUtil.showDialog(DEV_BD_FLAG_REMOVE,"请选择删除的设备类型");
			break;

		case R.id.tv_save:
			saveData();
			break;
		}
	}
	
	/**
	 * 保存
	 */
	private void saveData () {
		if (mDevBdDataList.size() > 0) {
			if(YzzsApplication.isConnected) {
				isDataSaveOk = false;
				sendDevbdDataToBt();
				startTime(HkCMDConstant.SEND_HK_SAVE_CMD_TIMEOUT, "设备数据保存");
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
	 * 确定保存
	 */
	private void confimDelete (final int position) {
		String message = "如被删除设备正在运行,则该设备会停止运行!是否确认删除?";
		new AlertDialog.Builder(getActivity()).setMessage(message).setTitle("删除提示")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						isConfimDelete = true;
						removeDev(position);
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
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
	 * 保存设备绑定数据
	 * 
	 * @param message
	 */
	private void getSaveDevbdResult(byte[] message) {
		if (message[6] == 0) {
			dismissLoading();
			ToastMsg(getActivity(), "设备保存成功");
		} else { // 重启设备失败
			dismissLoading();
			ToastMsg(getActivity(), "设备保存失败,请重试!");
		}
	}
	
	/**
	 * 发送获取数据CMD
	 * 数据格式： h,m,1,0,17,0,12,  0  ,111,e,n,d
	 * @param CmdType
	 */
	public void sendGetDataCmdToBt(int CmdType){
		int dataLength = 12; // 数据总长度
		byte[] message = new byte[dataLength]; // 构造 byte 数组
		message[0] = YzzsCommonUtil.intTobyte(104); // 协议头 “h”
		message[1] = YzzsCommonUtil.intTobyte(109); // 协议头 “m”
		message[2] = YzzsCommonUtil.intTobyte(LcCMDConstant.LC_SBBZ); // 数据域：类型
		message[3] = YzzsCommonUtil.intTobyte(CmdType / 256); // 数据域：命令高位
		message[4] = YzzsCommonUtil.intTobyte(CmdType % 256); // 数据域：命令低位
		message[5] = YzzsCommonUtil.intTobyte(dataLength / 256); // 数据域：数据总长度高位
		message[6] = YzzsCommonUtil.intTobyte(dataLength % 256); // 数据域：数据总长度低位
		message[7] = YzzsCommonUtil.intTobyte(0); // 数据域：单条有效数据长度：0
		message[8] = YzzsCommonUtil.intTobyte(0); // 校验位
		message[9] = YzzsCommonUtil.intTobyte(101); // 协议尾：“e”
		message[10] = YzzsCommonUtil.intTobyte(110); // 协议尾：“n”
		message[11] = YzzsCommonUtil.intTobyte(100); // 协议尾：“d”
		sendUnpackData(message, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}

	@Override
	public void onMessage(byte[] message) {
		if (message[0] == LcCMDConstant.LC_SBBZ) {// 料槽
			try {
				int cmd = YzzsCommonUtil.getCMD(message[1], message[2]); // 命令类型

				// 料槽保存
				if (cmd == LcCMDConstant.LC_SAVE_DEV) {
					getSaveDevbdResult(message);
				}

			} catch (Exception e) {
				dismissLoading();
				ToastMsg(getActivity(), "数据异常");
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