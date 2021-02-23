package com.huimv.yzzs.fragment.hk;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.IndexActivity;
import com.huimv.yzzs.adapter.SbbdListAdapter;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.HkCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.model.SbbdData;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.huimv.yzzs.util.wheel.ItemSelectWheelUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 环控绑定
 * 
 * @author jiangwei
 */
public class HkDevSetFragment extends YzzsBaseFragment
		implements EventHandler, OnClickListener,
		ItemSelectWheelUtil.OnConfirmClickListener,ConnectTimeoutListener{
	private final static String TAG = HkDevSetFragment.class.getSimpleName();
	private boolean isCanAdd;
	private SharePreferenceUtil mSpUtil;
	private TextView tv_save;
	private ImageView btnAddHkbd, btnRemoveHkbd;
	private ListView listViewHkbd;
	private ItemSelectWheelUtil mFjsltemSelectWheelUtil;
	private SbbdData mNewHkBdData;
	private String[] hkBdData = null; // 保存sp中环控绑定数据
	private final ArrayList<SbbdData> mHkBdDataList = new ArrayList<>();
	private SbbdListAdapter mHkbdListAdapter;
	public  boolean hasYxsjVersion = false;
	private static final int HKBD_FLAG_ADD = 1; // wheel 添加绑定的标记位
	private static final int HKBD_FLAG_REMOVE = 2; // wheel 删除绑定的标记位

	private static final int WHEEL_ITEM_FJ_POS = 0; // wheel 中 item 的风机位置号
	private static final int WHEEL_ITEM_SL_POS = 1; // wheel 中 item 的水帘位置号
	private static final int WHEEL_ITEM_DR_POS = 2; // wheel 中 item 的地热位置号
	private static final int WHEEL_ITEM_BPFJ_POS = 3; // wheel 中 item 的变频风机位置号
	private static final int WHEEL_ITEM_JRQ_POS = 4; // wheel 中 item 的加热器位置号
	private static final int WHEEL_ITEM_TC_POS = 5; // wheel 中 item 的天窗位置号
	private static final int WHEEL_ITEM_JL_POS = 6; // wheel 中 item 的卷帘位置号

	private static final String HKBD_KGZT_OPEN = "1"; // 开关状态 - 打开
	private static final String HKBD_KGZT_CLOSE = "0"; // 开关状态 - 关闭

	// 固定位 --- 数据头长度 = 协议头 + 数据域头固定长度
	private static final int GDW_LEN_HEAD = 8;
	// 固定位 --- 数据尾长度 = 校验位 + 协议尾
	private static final int GDW_LEN_TAIL = 4;

	private final String MAX = HkCMDConstant.MAX;
	private final String MIN = HkCMDConstant.MIN;
	private final String DW = HkCMDConstant.DW;
	private boolean isConfimDelete = false;
	private boolean isTimeOut = true;
	public static boolean isDataSaveOk = true;
	int insertItemPosition = 0; // 插入item的位置
	@SuppressLint("InflateParams")
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_hk_sbbd_fragment, null);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		mFjsltemSelectWheelUtil = new ItemSelectWheelUtil(getActivity(), "请选择绑定类型", 
				getResources().getStringArray(R.array.hk_device_item_array),HkDevSetFragment.this);
		initView(view);
		initData();
		initParams();
		return view;
	}

	private void initData() {
		setConnectTimeoutListener(this);
		getInitHkBdData();
	}

	private void initView(View view) {
		tv_save = (TextView) view.findViewById(R.id.tv_save);
		listViewHkbd = (ListView) view.findViewById(R.id.lv_hkbd);
		btnAddHkbd = (ImageView) view.findViewById(R.id.btn_add_hkbd);
		btnRemoveHkbd = (ImageView) view.findViewById(R.id.btn_remove_hkbd);

		tv_save.setOnClickListener(this);
		btnAddHkbd.setOnClickListener(this);
		btnRemoveHkbd.setOnClickListener(this);
	}

	/**
	 * 解析设备绑定数据
	 */
	private void getInitHkBdData() {
		hkBdData = mSpUtil.getSbbdTd().split(XtAppConstant.SEPARSTOR);
		int size = CommonUtil.isEmpty(hkBdData[0]) ? 0:hkBdData.length;
		mHkBdDataList.clear();
		for (int i = 0; i < size; i++) {
			SbbdData mHkBdData = new SbbdData();
			mHkBdData.setSbLx(hkBdData[i].substring(0, 2)); // 设备类型
			mHkBdData.setSbXh(hkBdData[i].substring(2, 4)); // 设备编号
			mHkBdData.setGlz(hkBdData[i].substring(4, 9)); // 功率值
			mHkBdData.setTdKg(hkBdData[i].substring(9, 10)); // 通道开关
			mHkBdData.setHeKg(hkBdData[i].substring(10, 11)); // 霍尔开关
			if (hkBdData[0].length() == 15) {//有风机绑定字段,有运行时间
				hasYxsjVersion = true;
				mHkBdData.setFjIsBd(hkBdData[i].substring(11,12));
				mHkBdData.setYxsj(hkBdData[i].substring(12,15));
			}
			mHkBdDataList.add(mHkBdData);
		}
	}

	private void initParams() {
		mHkbdListAdapter = new SbbdListAdapter(getActivity(), mHkBdDataList);
		listViewHkbd.setAdapter(mHkbdListAdapter);
	}

	/**
	 * 得到 list 中各个设备的使用个数
	 * 
	 * @param deviceType
	 * @return
	 */
	private int getDeviceUsedNumValue(String deviceType) {
		int deviceUsedNum = 0;
		for (SbbdData mHk_BdData : mHkBdDataList) {
			String sblx = mHk_BdData.getSbLx();
			if (deviceType.equals(sblx)) {
				deviceUsedNum++;
			}
		}
		return deviceUsedNum;
	}

	/**
	 * 验证各个设备是否可以被添加
	 * 风机水流的总个数不能超过风机的容量
	 * @param deviceType
	 *            当前设备的使用个数
	 * @param deviceType
	 *            该类型设备的最大支持个数
	 * @return true : 可以继续添加； false： 不可以继续添加
	 */
	private boolean validateDeviceCanAdd(String deviceType, int deviceMaxNum) {
		boolean isCanAdd;
		if (deviceType.equals(XtAppConstant.SBBD_SBLX_SL)) {
			int fjdebMaxNum = YzzsCommonUtil.getSbMax(IndexActivity.sbLxMax, Integer.valueOf(XtAppConstant.SBBD_SBLX_FJ));//风机最大容量
			isCanAdd = ((getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_FJ) + getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_SL)) < fjdebMaxNum) &&
					(getDeviceUsedNumValue(deviceType) < deviceMaxNum);//水帘的个数小于水帘的容量并且水帘的个数+风机的个数不能大于风机的容量
		} else {
			if (deviceType.equals(XtAppConstant.SBBD_SBLX_FJ)) {
				isCanAdd = ((getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_FJ) + getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_SL)) < deviceMaxNum);
			} else {
				isCanAdd = getDeviceUsedNumValue(deviceType) < deviceMaxNum;
			}
		}
		return isCanAdd;
	}

	/**
	 * 添加环控绑定
	 * 
	 * @param wheelItemPos
	 *            环控绑定类型的标记位；0：风机；1：水帘
	 */
	private void addHkbd(int wheelItemPos) {
		mNewHkBdData = new SbbdData();
		mNewHkBdData.setGlz("00000"); // 功率值
		mNewHkBdData.setIsBd("1"); // 是否绑定：该字段无用，设置默认值
		mNewHkBdData.setTdKg(HKBD_KGZT_OPEN); // 使能开关：默认打开
		mNewHkBdData.setHeKg(HKBD_KGZT_CLOSE); // 霍尔开关：默认关闭
		mNewHkBdData.setYxsj("000");
		mNewHkBdData.setFjIsBd(HKBD_KGZT_CLOSE);
		insertItemPosition = 0; // 插入item的位置

		// 风机
		if (wheelItemPos == WHEEL_ITEM_FJ_POS) {
			isCanAdd = addWhichDev(WHEEL_ITEM_FJ_POS,XtAppConstant.SBBD_SBLX_FJ,"风机");
		}
		
		// 水帘
		if (wheelItemPos == WHEEL_ITEM_SL_POS) {
			isCanAdd = addWhichDev(WHEEL_ITEM_SL_POS,XtAppConstant.SBBD_SBLX_SL,"湿帘");
		}
		
		// 地暖
		if (wheelItemPos == WHEEL_ITEM_DR_POS) {
			isCanAdd = addWhichDev(WHEEL_ITEM_DR_POS,XtAppConstant.SBBD_SBLX_DR,"地暖");
		}

		// 变频风机
		if (wheelItemPos == WHEEL_ITEM_BPFJ_POS) {
			isCanAdd = addWhichDev(WHEEL_ITEM_BPFJ_POS,XtAppConstant.SBBD_SBLX_BPFJ,"变频风机");
		}
		// 天窗
		if (wheelItemPos == WHEEL_ITEM_TC_POS) {
			isCanAdd = addWhichDev(WHEEL_ITEM_TC_POS,XtAppConstant.SBBD_SBLX_TC,"天窗");
		}
		// 加热器
		if (wheelItemPos == WHEEL_ITEM_JRQ_POS) {
			isCanAdd = addWhichDev(WHEEL_ITEM_JRQ_POS,XtAppConstant.SBBD_SBLX_JRQ,"加热器");
		}
		// 卷帘
		if (wheelItemPos == WHEEL_ITEM_JL_POS) {
			isCanAdd = addWhichDev(WHEEL_ITEM_JL_POS,XtAppConstant.SBBD_SBLX_JL,"卷帘");
		}

		if (isCanAdd) {
			mHkBdDataList.add(insertItemPosition, mNewHkBdData);
			mHkbdListAdapter.notifyDataSetChanged();
			listViewHkbd.setSelection(insertItemPosition);
		}
	}
	private boolean addWhichDev (int wheelItemPos,String devTag,String devTagName) {
		boolean isCanAdd;
		int fjMaxNoValue = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_FJ); // 风机最大编号
		int slMaxNoValue = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_SL);// 湿帘最大
		int drMaxNoValue = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_DR);// 地热最大编号
		int bpfjMaxNoValue = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_BPFJ);// 变频风机最大编号
		int tcMaxNoValue = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_TC);// 天窗最大编号
		int jlMaxNoValue = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_JL);// 卷帘最大编号
		int jrqMaxNoValue = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_JRQ);// 加热器最大编号
		int debMaxNum = YzzsCommonUtil.getSbMax(IndexActivity.sbLxMax, Integer.valueOf(devTag));
		int devMaxNoValue = 0;
		if (validateDeviceCanAdd(devTag, debMaxNum)) {
			isCanAdd = true;
			ToastMsg(getActivity(), "添加" + devTagName);
			mNewHkBdData.setSbLx(devTag);

			// 新添加湿帘的位置 = 两种设备类型最大编号的和
			switch (wheelItemPos ) {
				case WHEEL_ITEM_FJ_POS:
					insertItemPosition = fjMaxNoValue;
					devMaxNoValue = fjMaxNoValue;
					break;
				case WHEEL_ITEM_SL_POS:
					insertItemPosition = fjMaxNoValue + slMaxNoValue;
					devMaxNoValue = slMaxNoValue;
					break;
				case WHEEL_ITEM_DR_POS:
					insertItemPosition = fjMaxNoValue + slMaxNoValue + drMaxNoValue;
					devMaxNoValue = drMaxNoValue;
					break;
				case WHEEL_ITEM_BPFJ_POS:
					insertItemPosition = fjMaxNoValue + slMaxNoValue + drMaxNoValue + bpfjMaxNoValue;
					devMaxNoValue = bpfjMaxNoValue;
					break;
				case WHEEL_ITEM_JRQ_POS:
					insertItemPosition = fjMaxNoValue + slMaxNoValue + drMaxNoValue + bpfjMaxNoValue + jrqMaxNoValue;
					devMaxNoValue = jrqMaxNoValue;
					break;
				case WHEEL_ITEM_TC_POS:
					insertItemPosition = fjMaxNoValue + slMaxNoValue + drMaxNoValue + bpfjMaxNoValue+ tcMaxNoValue + jrqMaxNoValue;
					devMaxNoValue = tcMaxNoValue;
					break;
				case WHEEL_ITEM_JL_POS:
					insertItemPosition = fjMaxNoValue + slMaxNoValue + drMaxNoValue + bpfjMaxNoValue+ tcMaxNoValue + jrqMaxNoValue + jlMaxNoValue;
					devMaxNoValue = jlMaxNoValue;
					break;

			}
			String devSbxh = String.valueOf(devMaxNoValue + 1);
			String sbxh = YzzsCommonUtil.formatStringAdd0(devSbxh, 2, 1);
			mNewHkBdData.setSbXh(sbxh);
		} else {
			isCanAdd = false;
			ToastMsg(getActivity(), "不能再添加更多的" + devTagName);
		}
		return  isCanAdd;
	}
	/**
	 * 验证风机 湿帘设备可以被删除
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
	 *            环控绑定类型的标记位；0：风机；1：水帘
	 */
	private void removeHkbd(int wheelItemPos) {
		int fjUsedNum = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_FJ); // 风机最大编号
		int slUsedNum = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_SL);// 湿帘最大编号
		int drUsedNum = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_DR);// 地热最大编号
		int bpfjUsedNum = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_BPFJ);// 变频风机最大编号
		int tcUsedNum = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_TC);// 天窗最大编号
		int jrqUsedNum = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_JRQ);// 加热器最大编号
		int jlUsedNum = getDeviceUsedNumValue(XtAppConstant.SBBD_SBLX_JL);// 卷帘最大编号
		int removeItemPosition = 0;
		if (wheelItemPos == WHEEL_ITEM_FJ_POS) {
			if(validateDeviceCanRemove(XtAppConstant.SBBD_SBLX_FJ)){
				// 删除风机位置 = 风机 - 1 
				removeItemPosition = fjUsedNum - 1;
				ToastMsg(getActivity(), "删除风机设备");
			} else {
				ToastMsg(getActivity(), "当前没有绑定风机");
				return;
			}
		}
		if (wheelItemPos == WHEEL_ITEM_SL_POS) {
			if(validateDeviceCanRemove(XtAppConstant.SBBD_SBLX_SL)){
				// 删除湿帘的位置 = 风机 + 水帘 - 1
				removeItemPosition = fjUsedNum + slUsedNum - 1;
				ToastMsg(getActivity(), "删除水帘设备");
			} else {
				ToastMsg(getActivity(), "当前没有绑定水帘");
				return;
			}
		}
		if (wheelItemPos == WHEEL_ITEM_DR_POS) {
			if(validateDeviceCanRemove(XtAppConstant.SBBD_SBLX_DR)){
				// 删除湿帘的位置 = 风机 + 水帘 - 1
				removeItemPosition = fjUsedNum + slUsedNum + drUsedNum - 1;
				ToastMsg(getActivity(), "删除地热设备");
			} else {
				ToastMsg(getActivity(), "当前没有绑定地暖");
				return;
			}
		}

		if (wheelItemPos == WHEEL_ITEM_BPFJ_POS) {
			if(validateDeviceCanRemove(XtAppConstant.SBBD_SBLX_BPFJ)){
				// 删除湿帘的位置 = 风机 + 水帘 - 1
				removeItemPosition = fjUsedNum + slUsedNum + drUsedNum + bpfjUsedNum - 1;
				ToastMsg(getActivity(), "删除变频风机设备");
			} else {
				ToastMsg(getActivity(), "当前没有绑定变频风机");
				return;
			}
		}
		if (wheelItemPos == WHEEL_ITEM_JRQ_POS) {
			if(validateDeviceCanRemove(XtAppConstant.SBBD_SBLX_JRQ)){
				// 删除天窗的位置 = 风机 + 水帘 - 1
				removeItemPosition = fjUsedNum + slUsedNum + drUsedNum + bpfjUsedNum + jrqUsedNum - 1;
				ToastMsg(getActivity(), "删除加热器");
			} else {
				ToastMsg(getActivity(), "当前没有绑定加热器");
				return;
			}
		}
		if (wheelItemPos == WHEEL_ITEM_TC_POS) {
			if(validateDeviceCanRemove(XtAppConstant.SBBD_SBLX_TC)){
				// 删除卷帘的位置 = 风机 + 水帘 - 1
				removeItemPosition = fjUsedNum + slUsedNum + drUsedNum + bpfjUsedNum + tcUsedNum + jrqUsedNum - 1;
				ToastMsg(getActivity(), "删除天窗");
			} else {
				ToastMsg(getActivity(), "当前没有绑定天窗");
				return;
			}
		}
		if (wheelItemPos == WHEEL_ITEM_JL_POS) {
			if(validateDeviceCanRemove(XtAppConstant.SBBD_SBLX_JL)){
				// 删除卷帘的位置 = 风机 + 水帘 - 1
				removeItemPosition = fjUsedNum + slUsedNum + drUsedNum + bpfjUsedNum + tcUsedNum + jrqUsedNum + jlUsedNum - 1;
				ToastMsg(getActivity(), "删除卷帘");
			} else {
				ToastMsg(getActivity(), "当前没有绑定卷帘");
				return;
			}
		}
		mHkBdDataList.remove(removeItemPosition);
		mHkbdListAdapter.notifyDataSetChanged();
		listViewHkbd.setSelection(removeItemPosition);
	}

	private void sendHkbdDataToBt() {
		StringBuilder sb = new StringBuilder();
		int singleDataLength = 0; // 单条有效数据的长度
		for (int i = 0; i < mHkBdDataList.size(); i++) {
			String sblx = mHkBdDataList.get(i).getSbLx(); // 设备类型
			String sbxh = mHkBdDataList.get(i).getSbXh(); // 设备编号
			String glz = mHkBdDataList.get(i).getGlz(); // 功率值
			String tdkg = mHkBdDataList.get(i).getTdKg(); // 通道开关
			String hekg = mHkBdDataList.get(i).getHeKg(); // 霍尔开关
			sb.append(sblx).append(sbxh).append(glz).append(tdkg).append(hekg);
			if (hasYxsjVersion) {
				String fjBing = mHkBdDataList.get(i).getFjIsBd(); // 风机绑定
				String yxsj = mHkBdDataList.get(i).getYxsj(); // 运行时间
				if (CommonUtil.isEmpty(fjBing)) {
					fjBing = "";
				}
				if (CommonUtil.isEmpty(yxsj)) {
					yxsj = "";
				}
				sb.append(fjBing).append(yxsj);
			}
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
		byteSendData[3] = YzzsCommonUtil.intTobyte(HkCMDConstant.SAVE_HKBDPZ / 256); // 数据域：命令高位
		byteSendData[4] = YzzsCommonUtil.intTobyte(HkCMDConstant.SAVE_HKBDPZ % 256); // 数据域：命令低位
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
		showLoading("正在保存环控绑定配置(1/2)");
		sendUnpackData(byteSendData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}


	@Override
	public void onConfirm(int position, int i) {
		if (i == HKBD_FLAG_ADD) {
			addHkbd(position);
		}
		if (i == HKBD_FLAG_REMOVE) {
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
		case R.id.btn_add_hkbd:
			// 显示添加环控绑定的 wheel 框
			mFjsltemSelectWheelUtil.showDialog(HKBD_FLAG_ADD,"请选择添加的设备类型");
			break;

		case R.id.btn_remove_hkbd:
			// 显示删除环控绑定的 wheel 框
			mFjsltemSelectWheelUtil.showDialog(HKBD_FLAG_REMOVE,"请选择删除的设备类型");
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
		if (mHkBdDataList.size() > 0) {
			if(YzzsApplication.isConnected) {
				isDataSaveOk = false;
				sendHkbdDataToBt();
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
						//saveData();
						isConfimDelete = true;
						removeHkbd(position);
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
	 * 保存环控绑定数据
	 * 
	 * @param backData
	 */
	private void getSaveHkbdResult(byte[] backData) {
		if (backData[6] == 0) {
			showLoading("正在保存环控绑定配置(2/2)");
			sendGetDataCmdToBt(HkCMDConstant.GET_SBPZ);
		} else {
			dismissLoading();
			ToastMsg(getActivity(), "环控配置保存失败,请重试!");
		}
	}
	/**
	 * 发送获取数据CMD
	 * 数据格式： h,m,1,0,17,0,12,  0  ,111,e,n,d
	 * @param CmdType
	 */
	private void sendGetDataCmdToBt(int CmdType){
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
	/**
	 * 获取设备配置Ex
	 * 
	 * @param backData
	 * 
	 */
	private void getSaveSbpzResult(byte[] backData) {
		isTimeOut = false;
		isDataSaveOk = true;
		// 104 109
		// 1 0 7(命令) 0 x(长度) 0 3 00 01 00 02 00 02 00 02 00 02（五档的档位温度）
		// 11(单个循环体长度) 00 01 00 02 sn lx(两个字节) sx(两个字节)
		int lenth = backData[27];// 每个循坏体的长度 9
		int sbNum = backData[6];// 几个设备
		HkSshjxsFragment.sbNumS = sbNum;// 有几个设备
		int dwNum = 5;// 一共几个档位
		// 03 01 05 01 00 01 00 02 00 03 00 04 sn lx sx 去除包之后的长度
		byte[] data = Arrays.copyOfRange(backData, dwNum * 4 + 8, backData.length);
		byte[] dwWdData = Arrays.copyOfRange(backData, 7, dwNum * 4 + 7);// 档位温度
		StringBuilder sb = new StringBuilder();
		StringBuilder sbDw = new StringBuilder();
		for (int i = 1; i <= dwNum; i++) {// 循环档位
			for (int j = 0; j < sbNum; j++) {// 设备循环
				// 第几档的第几个设备
				byte[] sbData = Arrays.copyOfRange(data, (i - 1) * sbNum * lenth + j * lenth,
						(i - 1) * sbNum * lenth + (j + 1) * lenth);

				int dqdkK1 = YzzsCommonUtil.ChangeByteToPositiveNumber(sbData[0]);// 当前端口开1时间
				int dqdkK2 = YzzsCommonUtil.ChangeByteToPositiveNumber(sbData[1]);// 当前端口开2时间
				int dqdkG1 = YzzsCommonUtil.ChangeByteToPositiveNumber(sbData[2]);// 当前端口关1时间
				int dqdkG2 = YzzsCommonUtil.ChangeByteToPositiveNumber(sbData[3]);// 当前端口关2时间
				int dwSn = sbData[4];

				String dqdkK = String.valueOf(dqdkK1 * 256 + dqdkK2);// 当前端口开时间
				String dqdkG = String.valueOf(dqdkG1 * 256 + dqdkG2);// 当前端口开时间
				String sblx = sbData[5] + "" + sbData[6];// 设备类型
				String sbxh = sbData[7] + "" + sbData[8];// 设备序号
				sb.append(dwSn);
				sb.append(YzzsCommonUtil.formatStringAdd0(dqdkK, 4, 1));// 开四位
				sb.append(YzzsCommonUtil.formatStringAdd0(dqdkG, 4, 1));// 关四位
				sb.append(sblx);
				sb.append(sbxh);
				if (mSpUtil.getHkIsHasBpfjVersion().equals("1")) {//如果有变频风机
					String bppge = sbData[9] + "" + sbData[10] + sbData[11];//变频百分比
					sb.append(bppge);
				}
			}

			int wdlow1 = dwWdData[(i - 1) * 4] >= 0 ? dwWdData[(i - 1) * 4] : dwWdData[(i - 1) * 4] + 256;// 档位温度下限1
			int wdlow2 = dwWdData[(i - 1) * 4 + 1] >= 0 ? dwWdData[(i - 1) * 4 + 1] : dwWdData[(i - 1) * 4 + 1] + 256;// 档位温度下限2
			int wdtop1 = dwWdData[(i - 1) * 4 + 2] >= 0 ? dwWdData[(i - 1) * 4 + 2] : dwWdData[(i - 1) * 4 + 2] + 256;// 档位温度上限1
			int wdtop2 = dwWdData[(i - 1) * 4 + 3] >= 0 ? dwWdData[(i - 1) * 4 + 3] : dwWdData[(i - 1) * 4 + 3] + 256;// 档位温度上限2

			String wdlow = String.valueOf(wdlow1 * 256 + wdlow2);// 档位温度下限
			String wdtop = String.valueOf(wdtop1 * 256 + wdtop2);// 档位温度上限
			mSpUtil.setDwWd(DW + i + MIN, wdlow);// 档位温度下限
			mSpUtil.setDwWd(DW + i + MAX, wdtop);// 档位温度上限
			sbDw.append(sb).append(XtAppConstant.SEPARSTOR);
			sb.delete(0, sb.length());
		}
		// 循环结束
		mSpUtil.setDwSave("5");
		mSpUtil.setDwdk(sbDw.toString());
		sbDw.delete(0, sbDw.length());
		dismissLoading();
		ToastMsg(getActivity(), "保存成功,请手动重启设备使配置生效");
	}

	@Override
	public void onMessage(byte[] backData) {
		if (backData[0] == HkCMDConstant.HK_SBBZ) {// 环控
			try {
				int cmd = backData[1] * 256 + backData[2]; // 命令类型

				// 环控保存
				if (cmd == HkCMDConstant.SAVE_HKBDPZ) {
					getSaveHkbdResult(backData);
				}

				// 得到设备配置：运行参数保存
				if (cmd == HkCMDConstant.GET_SBPZ) {
					getSaveSbpzResult(backData);
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