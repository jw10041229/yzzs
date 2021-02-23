package com.huimv.yzzs.fragment.cdz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.CdzCMDConstant;
import com.huimv.yzzs.constant.GeneralCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.db.entity.Da_mc;
import com.huimv.yzzs.fragment.general.SmsSetSheNameFragment;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.support.general.BluetoothScanActivitySupport;
import com.huimv.yzzs.support.general.CreateZsmcActivitySupport;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.huimv.yzzs.util.wheel.McZsItemSelectWheelUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 修改測定站jqid
 * 
 * @author jiangwei
 *
 */
public class CdzJqidFragment extends YzzsBaseFragment implements EventHandler ,ConnectTimeoutListener{
	private Button btnWriteJqid, btnReadJqid;
	private Button btnMcmc, btnZsmc;
	private Button btnSelectMcmc, btnSelectZsmc;
	private TextView tvJqid, tvInfo;
	private ImageView iv_back;
	private TextView tv_sms_she_name;
	private McZsItemSelectWheelUtil mcmcItemSelectWheelUtil;
	private McZsItemSelectWheelUtil zsmcItemSelectWheelUtil;
	private String[] mcmcArr;
	private String[] zsmcArr;
	private String selectMcmc; // 用于保存 wheel 中选择的牧场名称
	private String selectZsmc; // 用于保存 wheel 中选择的猪舍名称
	private String jqidFromDB; // 保存从数据库获取到的 机器ID（写入时使用）
	private String jqidFromBt; // 保存从蓝牙读取到的 机器ID（读取时使用）
	private SharePreferenceUtil mSpUtil;
	private boolean isMcmcSelected = false; // 牧场名称被选择的标记位

	// 固定位 --- 数据头长度 = 协议头 + 数据域头固定长度
	private static final int GDW_LEN_HEAD = 8;
	// 固定位 --- 数据尾长度 = 校验位 + 协议尾
	private static final int GDW_LEN_TAIL = 4;
	private boolean isTimeOut = true;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_hk_jqid_fragment, rootView,false);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		initView(view);
		getMcmcFromDBInitMcmcArr();
		initOnListeners();
		return view;
	}

	private void initView(View view) {
		btnMcmc = (Button) view.findViewById(R.id.btn_mcmc);
		btnZsmc = (Button) view.findViewById(R.id.btn_zsmc);
		btnSelectMcmc = (Button) view.findViewById(R.id.btn_select_mcmc);
		btnSelectZsmc = (Button) view.findViewById(R.id.btn_select_zsmc);
		btnReadJqid = (Button) view.findViewById(R.id.btn_read_jqid);
		btnWriteJqid = (Button) view.findViewById(R.id.btn_write_jqid);
		tvJqid = (TextView) view.findViewById(R.id.tv_jqid);
		tvInfo = (TextView) view.findViewById(R.id.tv_info);
		iv_back = (ImageView) view.findViewById(R.id.iv_back);
		tv_sms_she_name = (TextView) view.findViewById(R.id.tv_sms_she_name);
	}

	private void initOnListeners() {
		setConnectTimeoutListener(this);
		iv_back.setOnClickListener(this);
		btnSelectMcmc.setOnClickListener(this);
		btnSelectZsmc.setOnClickListener(this);
		btnWriteJqid.setOnClickListener(this);
		btnReadJqid.setOnClickListener(this);
		tv_sms_she_name.setOnClickListener(this);

		mcmcItemSelectWheelUtil = new McZsItemSelectWheelUtil(getActivity(), "请选择牧场", mcmcArr,
				new McZsItemSelectWheelUtil.OnConfirmClickListener() {

					@Override
					public void onConfirm(int position) {
						isMcmcSelected = true; // 牧场名称被选择
						selectMcmc = mcmcArr[position];
						btnMcmc.setText(selectMcmc); // 显示牧场名称
						// 选择完牧场后，让选择猪舍按钮可按
						btnSelectZsmc.setEnabled(true);
						// 初始化猪舍名称数组 zsmcArr 以显示 wheel
						initZsmcArrByMcmc(selectMcmc);
					}
				});

	}

	/**
	 * 从数据库中读取牧场名称，并显示在界面上
	 */
	private void getMcmcFromDBInitMcmcArr() {
		// 得到牧场表的所有不同的牧场名称
		List<Da_mc> daMcList = BluetoothScanActivitySupport.getAllMc(getActivity());
		if (daMcList.size() == 0) {
			ToastMsg(getActivity(), "该账户下没有牧场数据，请检查账户信息");
		} else {
			List<String> mcmcList = new ArrayList<>();
			for (Da_mc daMc : daMcList) {
				mcmcList.add(daMc.getMcmc());
			}
			// 取出 mcidList中不重复牧场名称数据保存在 set中
			HashSet<String> set = new HashSet<>(mcmcList);
			mcmcList.clear(); // 将去重的牧场名称重新放进 mcmcList 中
			for (String mcmc : set) {
				mcmcList.add(mcmc);
			}
			// 初始化 牧场名称 数组吗，以显示 wheel
			mcmcArr = new String[mcmcList.size()];
			for (int i = 0; i < set.size(); i++) {
				mcmcArr[i] = mcmcList.get(i);
			}
		}

	}

	/**
	 * 通过选择的牧场名称，初始化 猪舍名称数组
	 * 
	 * @param selectMcmc
	 */
	private void initZsmcArrByMcmc(String selectMcmc) {
		// 根据选择的牧场名称，查询该牧场名称下面所有的猪舍列表，初始化 zsmcArr 以显示 wheel
		List<String> zslbList = CreateZsmcActivitySupport.getZslbByMcmc(getActivity(), selectMcmc);
		if (zslbList.size() == 0) {
			ToastMsg(getActivity(), "该牧场下没有猪舍");
		} else {
			zsmcArr = new String[zslbList.size()];
			for (int i = 0; i < zslbList.size(); i++) {
				zsmcArr[i] = zslbList.get(i);
			}
		}
		// 显示猪舍名称的 wheel
		zsmcItemSelectWheelUtil = new McZsItemSelectWheelUtil(getActivity(), "请选择猪舍", zsmcArr,
				new McZsItemSelectWheelUtil.OnConfirmClickListener() {

					@Override
					public void onConfirm(int position) {
						selectZsmc = zsmcArr[position];
						btnZsmc.setText(selectZsmc); // 显示猪舍名称
						// 根据猪舍名称获取机器id
						jqidFromDB = CreateZsmcActivitySupport.getJqidByZsmc(getActivity(), selectZsmc);
						if ("".equals(jqidFromDB)) {
							String textValue = "该猪舍没有对应的机器ID";
							tvJqid.setText(textValue);
						} else {
							tvJqid.setText(jqidFromDB);
						}
					}
				});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_sms_she_name:
			toSetFragment(new SmsSetSheNameFragment(), "");
			break;
		case R.id.btn_write_jqid:
			if(YzzsApplication.isConnected) {
				// 修改机器ID的命令
				sendXgJqidCmd();
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;

		case R.id.btn_read_jqid:
			if(YzzsApplication.isConnected) {
				// 读取机器ID的命令
				showLoading("正在读取机器ID");
				sendReadJqid(GeneralCMDConstant.READ_JQID);
				startTime(XtAppConstant.SEND_CMD_TIMEOUT, "读取机器ID");
				isTimeOut = true;
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;

		case R.id.btn_select_mcmc:
			if (!CommonUtil.isEmpty(mcmcArr) && mcmcArr.length > 0) {
				mcmcItemSelectWheelUtil.showDialog();
			} else {
				ToastMsg(getActivity(), "没有牧场数据，请重新登录用户");
			}
			break;

		case R.id.btn_select_zsmc:
			if (isMcmcSelected) {
				if (!CommonUtil.isEmpty(zsmcArr) && zsmcArr.length > 0) {
					zsmcItemSelectWheelUtil.showDialog();
				} else {
					ToastMsg(getActivity(), "该牧场下没有猪舍");
				}
			} else {
				ToastMsg(getActivity(), "在选择猪舍前，请先选择对应牧场");
			}
			break;
		}

		super.onClick(v);
	}

	/**
	 * 发送获取数据CMD
	 * 数据格式： h,m,1,0,17,0,12,  0  ,111,e,n,d
	 * @param CmdType
	 */
	private void sendReadJqid(int CmdType){
		int dataLength = 12; // 数据总长度
		byte[] byteSendData = new byte[dataLength]; // 构造 byte 数组
		byteSendData[0] = YzzsCommonUtil.intTobyte(104); // 协议头 “h”
		byteSendData[1] = YzzsCommonUtil.intTobyte(109); // 协议头 “m”
		byteSendData[2] = YzzsCommonUtil.intTobyte(CdzCMDConstant.CDZ_SBBZ); // 数据域：类型
		byteSendData[3] = YzzsCommonUtil.intTobyte(CmdType / 256); // 数据域：命令高位
		byteSendData[4] = YzzsCommonUtil.intTobyte(CmdType % 256); // 数据域：命令低位
		byteSendData[5] = YzzsCommonUtil.intTobyte(dataLength / 256); // 数据域：数据总长度高位
		byteSendData[6] = YzzsCommonUtil.intTobyte(dataLength % 256); // 数据域：数据总长度低位
		byteSendData[7] = YzzsCommonUtil.intTobyte(0); // 数据域：单条有效数据长度：0
		byteSendData[8] = YzzsCommonUtil.intTobyte(1); // 校验位
		byteSendData[9] = YzzsCommonUtil.intTobyte(101); // 协议尾：“e”
		byteSendData[10] = YzzsCommonUtil.intTobyte(110); // 协议尾：“n”
		byteSendData[11] = YzzsCommonUtil.intTobyte(100); // 协议尾：“d”
		sendUnpackData(byteSendData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
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
			ToastMsg(getActivity(), "蓝牙连接已断开");
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
		if (message[0] == CdzCMDConstant.CDZ_SBBZ) {// 分栏站
			try {
				int cmd = YzzsCommonUtil.getCMD(message[1] , message[2]); // 命令类型
				switch (cmd) {
				case GeneralCMDConstant.READ_JQID:
					getDqjqidResult(message);
					break;
				case GeneralCMDConstant.CHANGE_JQID:
					getXgjqidResult(message);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				dismissLoading();
				ToastMsg(getActivity(), "数据异常");
			}
		}
	}

	/**
	 * 修改机器ID
	 */
	private void sendXgJqidCmd() {
		if (tvJqid.getText().toString().length() != 8) {
			ToastMsg(getActivity(), "该猪舍的机器ID不存在，请选择合法的机器ID写入");
			return;
		}
		startTime(XtAppConstant.SEND_CMD_TIMEOUT, "写入机器ID");
		isTimeOut = true;
		String jqidStr = tvJqid.getText().toString().trim(); // 机器ID
		int singleDataLength = jqidStr.length(); // 单条有效数据的长度
		// byte 数据的总长度
		int dataLength = singleDataLength + GDW_LEN_HEAD + GDW_LEN_TAIL;
		// 循环体 + 前固定位 的长度（用于填充数据使用）
		byte[] byteSendData = new byte[dataLength]; // 构造 byte 数组
		int len = singleDataLength + GDW_LEN_HEAD;
		showLoading("正在修改机器ID...");
		byteSendData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH); // 协议头 “h”
		byteSendData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM); // 协议头 “m”
		byteSendData[2] = YzzsCommonUtil.intTobyte(CdzCMDConstant.CDZ_SBBZ); // 数据域：类型
		byteSendData[3] = YzzsCommonUtil.intTobyte(GeneralCMDConstant.CHANGE_JQID / 256); // 数据域：命令高位
		byteSendData[4] = YzzsCommonUtil.intTobyte(GeneralCMDConstant.CHANGE_JQID % 256); // 数据域：命令低位
		byteSendData[5] = YzzsCommonUtil.intTobyte(dataLength / 256); // 数据域：数据总长度高位
		byteSendData[6] = YzzsCommonUtil.intTobyte(dataLength % 256); // 数据域：数据总长度低位
		byteSendData[7] = YzzsCommonUtil.intTobyte(singleDataLength); // 数据域：单条有效数据长度
		for (int i = GDW_LEN_HEAD; i < len; i++) {
			int data = Integer.valueOf(jqidStr.substring(i - GDW_LEN_HEAD, i - GDW_LEN_HEAD + 1));
			byteSendData[i] = YzzsCommonUtil.intTobyte(data);
		}
		byteSendData[dataLength - 4] = YzzsCommonUtil.intTobyte(0); // 校验位：暂时不用
		byteSendData[dataLength - 3] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE); // 协议尾：“e”
		byteSendData[dataLength - 2] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN); // 协议尾：“n”
		byteSendData[dataLength - 1] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD); // 协议尾：“d”
		sendUnpackData(byteSendData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}

	/**
	 * 得到修改机器ID的返回值 模拟发送过来的 机器id数据包 h,m,1,0,17,0,13,1,0,111,e,n,d 1:长度 0：修改成功
	 */
	private void getXgjqidResult(byte[] backData) {
		if (backData[6] == 0) {
			isTimeOut = false;
			dismissLoading();
			ToastMsg(getActivity(), "修改机器ID成功!");
		} else {
			ToastMsg(getActivity(), "修改机器ID失败,请重试!");
		}
	}

	/**
	 * 读取机器ID 机器id数据包 h,m,1,0,16,0,20,8,1,2,3,4,5,6,7,8,111,e,n,d
	 */
	private void getDqjqidResult(byte[] backData) {
		if (backData.length < backData[7] + 8) {
			ToastMsg(getActivity(), "数据格式错误");
			dismissLoading();
		}
		if (backData[5] == 0 && backData[6] == 0) {
			isTimeOut = false;
			StringBuilder sb = new StringBuilder();
			for (int i = 8; i < backData[7] + 8; i++) {
				sb.append(backData[i]);
			}
			jqidFromBt = sb.toString(); // 保存从蓝牙获取到的机器id
			mSpUtil.setJqid(jqidFromBt); // 将 机器ID 保存到 sp 中
			// 回显牧场名称与猪舍名称
			showMcmcAndZsmcByJqid(jqidFromBt);
			tvJqid.setText(sb.toString());
			sb.delete(0, sb.length());
			dismissLoading();
		} else {
			dismissLoading();
			ToastMsg(getActivity(), "读取机器ID操作失败,请重试!");
		}
	}

	/**
	 * 通过机器ID显示牧场和猪舍
	 * 
	 * @param jqid
	 */
	private void showMcmcAndZsmcByJqid(String jqid) {
		Da_mc daMc = CreateZsmcActivitySupport.getMcByJqid(getActivity(), jqid);
		String mcmc = daMc.getMcmc();
		String zsmc = daMc.getZsmc();
		if (mcmc != null && zsmc != null) {
			// 回显牧场名称
			btnMcmc.setText(mcmc);
			btnZsmc.setText(zsmc);
		} else {
			// 该机器id，在数据库中没有保存对应的牧场和猪舍
			ToastMsg(getActivity(), "该机器ID在该账户的牧场中没有对应的猪舍，请重新登录正确的用户");
			tvInfo.setVisibility(View.VISIBLE);
			String textValue = "该机器ID在该账户的牧场中没有对应的猪舍，请重新登录正确的用户";
			tvInfo.setText(textValue);
		}
	}
	/**
	 * 跳转目标Fragment
	 * 
	 * @param mFragment，tag
	 */
	private void toSetFragment(Fragment mFragment,String tag) {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fragment_container, mFragment,tag);
		Bundle bundle = new Bundle();
		bundle.putInt("whichDev", CdzCMDConstant.CDZ_SBBZ);
		mFragment.setArguments(bundle);
		ft.addToBackStack(null);
		ft.commit();
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
