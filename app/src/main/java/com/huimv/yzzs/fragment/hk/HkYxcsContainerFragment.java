package com.huimv.yzzs.fragment.hk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.BluetoothScanActivity;
import com.huimv.yzzs.activity.IndexActivity;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.GeneralCMDConstant;
import com.huimv.yzzs.constant.HkCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.fragment.general.BbxxFragment;
import com.huimv.yzzs.fragment.general.BjqPhoneNumberFragment;
import com.huimv.yzzs.fragment.general.BjsxjgFragment;
import com.huimv.yzzs.fragment.general.IpWgYmFragment;
import com.huimv.yzzs.fragment.general.LybmFragment;
import com.huimv.yzzs.fragment.general.LyzsdVersionFragment;
import com.huimv.yzzs.fragment.general.MacAddressFragment;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.huimv.yzzs.util.wheel.SetItemSelectIsShowWheelUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.huimv.yzzs.activity.IndexActivity.HK_HKBD_FRAGMENT;

/**
 * 环控密码验证
 * 
 * @author jiangwei
 */
public class HkYxcsContainerFragment extends YzzsBaseFragment implements
		EventHandler ,ConnectTimeoutListener,SetItemSelectIsShowWheelUtil.OnConfirmClickSetListener {
	private SharePreferenceUtil mSpUtil;
	public static String identity = "";
	private boolean isTimeOut = true;
	/**
	 * 按钮
	 */
	private Button btn_cgqbd, btn_hkbd;
	/**
	 * viewPager
	 */
	private ViewPager vp_index;
	/**
	 * Fragment
	 */
	private Fragment mHk_semi_automatic_yxcsFragment;
	private Fragment mHk_automatic_yxcsFragment;
	/**
	 * fragment List
	 */
	private List<Fragment> viewList;// 把需要滑动的页卡添加到这个list中
	/**
	 * 
	 */
	private SetItemSelectIsShowWheelUtil mSetItemSelectWheelUtil;
	/**
	 * 
	 */
	private ImageView ivSet,iv_change_model;
	/**
	 * 
	 */
	private String wheelItemArr [];
	/**
	 * 
	 */
	private LinearLayout ll_bottom_bar;
	/**
	 * 
	 */
	private TextView tv_title;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_hk_yxcs_container_fragment, null);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		initView(view);
		initData();
		initOnListeners();
		initDentity();
		return view;
	}
	
	private void initData() {
		identity = getArguments().getString("identity");// 根据这个Key来判断是什么身份
		viewList = new ArrayList<>();
		mHk_semi_automatic_yxcsFragment = new HkSemiAutomaticYxcsFragment();
		//mHk_automatic_yxcsFragment = new Hk_automatic_yxcsFragment();
		if(IndexActivity.hk_work_model == 0) {//半自动
			viewList.add(mHk_semi_automatic_yxcsFragment);
			tv_title.setText("半自动模式");
		}
		if(IndexActivity.hk_work_model == 1) {//全自动
			viewList.add(mHk_automatic_yxcsFragment);
			tv_title.setText("全自动模式");
		}
		vp_index.setCurrentItem(0);
		vp_index.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), viewList));
	}

	private void initView(View view) {
		vp_index = (ViewPager) view.findViewById(R.id.vp_index);
		btn_cgqbd = (Button) view.findViewById(R.id.btn_cgqbd);
		btn_hkbd = (Button) view.findViewById(R.id.btn_hkbd);
		ivSet = (ImageView) view.findViewById(R.id.iv_set);
		iv_change_model = (ImageView) view.findViewById(R.id.iv_change_model);
		ll_bottom_bar = (LinearLayout) view.findViewById(R.id.ll_bottom_bar);
		tv_title = (TextView) view.findViewById(R.id.tv_title);
	}
	
	private void initOnListeners() {
		setConnectTimeoutListener(this);
		btn_cgqbd.setOnClickListener(this);
		btn_hkbd.setOnClickListener(this);
		ivSet.setOnClickListener(this);
		iv_change_model.setOnClickListener(this);
	}
	
	/**
	 * 身份判断
	 */
	private void initDentity () {
		if(identity.equals(GeneralCMDConstant.IDENTITY_ORDINARY)){ // 普通用户
			wheelItemArr = getActivity().getResources().getStringArray(R.array.hk_set_item_array_customer);
			ll_bottom_bar.setVisibility(View.GONE);
		} 
		if(identity.equals(GeneralCMDConstant.IDENTITY_DEBUG)){ // 调试用户
			wheelItemArr = getActivity().getResources().getStringArray(R.array.hk_set_item_array_debug);
			ll_bottom_bar.setVisibility(View.VISIBLE);
		}
		mSetItemSelectWheelUtil = new SetItemSelectIsShowWheelUtil(getActivity(), "请选择设置项目", wheelItemArr,
				HkYxcsContainerFragment.this);
	}
	
	@Override
	public void onResume() {
		MessageReceiver.ehList.add(this);
		
		super.onResume();
	}
	@Override
	public void onPause() {
		MessageReceiver.ehList.remove(this);
		super.onPause();
	}
	
	@Override
	public void onClick(View v) {
		if (!isSave5()) {
			return;
		}
		switch (v.getId()) {
		case R.id.iv_set:
			mSetItemSelectWheelUtil.showDialog();
			break;
		case R.id.iv_change_model:
			//changeDevWorkModelDialog();
			LoginOutDialog();
			break;
		case R.id.btn_cgqbd:
			if(YzzsApplication.isConnected) {
				showLoading("正在获取传感器绑定数据");
				sendGetDataCmdToBt(HkCMDConstant.GET_CGQBD);
				startTime(XtAppConstant.SEND_CMD_TIMEOUT, "获取传感器绑定数据");
				isTimeOut = true;
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		case R.id.btn_hkbd:
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
		super.onClick(v);
	}
	
	private boolean isSave5() {
		boolean isSave5;
		if (IndexActivity.hk_work_model == 0) {
			if(!mSpUtil.getDwSave().equals(String.valueOf(5))) {
				ToastMsg(getActivity(), "请完整保存5档数据!");
				isSave5 = false;
			} else {
				isSave5 = true;
			}
		} else {
			isSave5 = true;
		}
		return isSave5;
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
	public void onMessage(byte[] message) {
		getJoiningPack(message);
	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		
	}
	
	/**
	 * 拼包
	 * 
	 * @param message
	 */
	private void getJoiningPack(byte[] message) {
		if (message[0] == HkCMDConstant.HK_SBBZ) {// 环控
			try {
				int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);// 命令类型
				// 重启设备
				if (cmd == GeneralCMDConstant.RESTART_XT_DEVICE) {
					restartDeviceEx(message);
				}
				// 传感器绑定
				if (cmd == HkCMDConstant.GET_CGQBD) {
					saveCgqbdToSpFromBt(message);
				}
				// 环控绑定
				if (cmd == HkCMDConstant.GET_HKBDPZ) {
					saveHkbdToSpFromBt(message);
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
		if (message[0] == HkCMDConstant.HK_SBBZ) {
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
	 * 保存环控绑定数据到 sp
	 * 
	 * @param message
	 *            去头去尾的传感器数据
	 */
	private void saveHkbdToSpFromBt(byte[] message) {
		isTimeOut = false;
		int dataLength = message[6]; // 每段数据的长度
		// 截包：截取循环体数据
		byte[] byteCircle = Arrays.copyOfRange(message, 7, message.length);
		// 循环体数据保存到 sb 中
		StringBuilder sb = new StringBuilder();
		for (byte byteValue:byteCircle ) {
			sb.append(byteToString(byteValue));
		}
		StringBuilder sbResult = new StringBuilder();
		while (sb.length() >= dataLength) {
			sbResult.append(sb.substring(0, dataLength) + "#");
			sb = sb.delete(0, dataLength);
		}
		String hkbdStr = sbResult.substring(0, sbResult.length());// 去掉最后一个#
		mSpUtil.setSbbdTd(hkbdStr);
		// 跳转到环控绑定界面
		toSetFragment(new HkDevSetFragment(),HK_HKBD_FRAGMENT);
		dismissLoading();
	}
	
	/**
	 * 保存传感器绑定数据到 sp
	 * 
	 * @param backResult
	 *            去头去尾的传感器数据
	 */
	private void saveCgqbdToSpFromBt(byte[] backResult) {
		isTimeOut = false;
		int dataLength = backResult[6]; // 每段数据的长度
		// 截包：截取循环体数据
		byte[] byteCircle = Arrays.copyOfRange(backResult, 7, backResult.length);
		// 循环体数据保存到 sb 中
		StringBuilder sb = new StringBuilder();
		for (byte byteValue:byteCircle) {
			sb.append(byteToString(byteValue));
		}
		StringBuilder sbResult = new StringBuilder();
		while (sb.length() >= dataLength) {
			sbResult.append(sb.substring(0, dataLength) + "#");
			sb = sb.delete(0, dataLength);
		}
		String cgqbdStr = sbResult.substring(0, sbResult.length()); // 去掉最后一个#
		mSpUtil.setCgqbdData(cgqbdStr);
		// 跳转到传感器绑定界面
		toSetFragment(new HkSensorSetFragment(),"");
		dismissLoading();
	}
	
	/**
	 * 跳转目标Fragment
	 * 
	 * @param mFragment
	 */
	private void toSetFragment(Fragment mFragment,String tag) {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fragment_container, mFragment,tag);
		Bundle bundle = new Bundle();
		bundle.putInt("whichDev", HkCMDConstant.HK_SBBZ);
		mFragment.setArguments(bundle);
		ft.addToBackStack(null);
		ft.commit();
	}

	/**
	 * byte 转 String
	 * 
	 * @param b
	 * @return
	 */
	private String byteToString(byte b) {
		StringBuilder sb = new StringBuilder();
		sb.delete(0, sb.length());
		sb.append(b);
		return sb.toString();
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

	class MyFragmentPagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> listFragments;

		public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> listFragments) {
			super(fm);
			this.listFragments = listFragments;
		}

		@Override
		public Fragment getItem(int position) {
			return listFragments.get(position);
		}

		@Override
		public int getCount() {
			return listFragments.size();
		}

		@Override
		public int getItemPosition(Object object) {
		return super.getItemPosition(object);
		}
	}
	/**
	 * 判断是否已经登陆
	 */
	private boolean hasLogined() {
		return (!"".equals(mSpUtil.getYhxm()) || !"".equals(mSpUtil.getYhmm()));
	}
	
	/**
	 * 重启设备
	 */
	private void restartDevDialog () {
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
	 * 重启系统
	 */
	private void sendRestartDeviceCMD() {
		int initCMD = GeneralCMDConstant.RESTART_XT_DEVICE;
		int lenth = 12;
		byte[] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);// h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);// m
		byteData[2] = 1;// 设备区分标志
		byteData[3] = YzzsCommonUtil.intTobyte(initCMD / 256);// 命令高位
		byteData[4] = YzzsCommonUtil.intTobyte(initCMD % 256);// 命令低位
		byteData[5] = YzzsCommonUtil.intTobyte(lenth / 256);// 命令低位
		byteData[6] = YzzsCommonUtil.intTobyte(lenth % 256);// 命令高位
		byteData[7] = 0;// 后面值得长度
		byteData[8] = 1;// 校验位
		byteData[9] = 101;// e
		byteData[10] = 110;// n
		byteData[11] = 100;// d
		sendUnpackData(byteData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}
	
	private void toHkSshjxsFragment() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fragment_container, new HkSshjxsFragment(),"hk_sshjxsFragment");
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.addToBackStack(null);
		ft.commit();
	}
	/**
	 * 退出当前权限账户
	 */
	private void LoginOutDialog () {
		String message = "是否确定退出当前权限账户";
		new AlertDialog.Builder(getActivity()).setMessage(message).setTitle("退出账户提示")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if(YzzsApplication.isConnected) {
							mSpUtil.setHkPassword("");
							toHkSshjxsFragment();
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
	 * 切换自动模式/半自动
	 */
	public void changeDevWorkModelDialog () {
		String message;
		if(IndexActivity.hk_work_model == 1) {
			message = "当前工作在全自动模式,是否切换到半自动模式?";
		} else {
			message = "当前工作在半自动模式,是否切换到全自动模式?";
		}
		new AlertDialog.Builder(getActivity()).setMessage(message).setTitle("切换模式")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if(YzzsApplication.isConnected) {
			/*				showLoading("正在重启设备...");
							sendRestartDeviceCMD();
							startTime(XtAppConstant.SEND_CMD_TIMEOUT, "重启设备");
							isTimeOut = true;*/
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
	 * 返回首页登陆
	 */
	private void goIndexToLoginDialog () {
		new AlertDialog.Builder(getActivity()).setMessage("当前没有登陆账户,是否返回首页登陆?").setTitle("账户登录")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						startActivity(new Intent(getActivity(),BluetoothScanActivity.class));
						getActivity().finish();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}
	
	@Override
	public void onConfirm(int position) {
		switch (position) {
		case 0:
			toSetFragment(new HkDefalutGearSetyxcsFragment(),"");
			break;
		case 1:
			toSetFragment(new BbxxFragment(),"");
			break;
		case 2:
			toSetFragment(new BjsxjgFragment(),"");
			break;
		case 3:// 地暖
			toSetFragment(new HkDnTempSetFragment(),"");
			break;
		case 4:// 蓝牙别名
			if (hasLogined()) {
				toSetFragment(new LybmFragment(),"");
			} else {
				goIndexToLoginDialog();
			}
			break;
		case 5:// 机器ID
			if (hasLogined()) {
				toSetFragment(new HkJqidFragment(),"");
			} else {
				goIndexToLoginDialog();
			}
			break;
		case 6:// 重启设备
			restartDevDialog();
			break;
		case 7:// ip/网关
			toSetFragment(new IpWgYmFragment(),"");
			break;
		case 8:// ip/网关
			toSetFragment(new BjqPhoneNumberFragment(),"");
			break;
		case 9://mack地址
			toSetFragment(new MacAddressFragment(),"");
			break;
		case 10://蓝牙指示灯
			toSetFragment(new LyzsdVersionFragment(),"");
			break;
		case 11://环控硬件版本
			toSetFragment(new HkyjVersionFragment(),"");
			break;
		case 12://加热器温度
			toSetFragment(new HkJrqTempFragment(),"");
			break;
		case 13://目标温度
			toSetFragment(new HkTargetTempFragment(),"");
			break;
		case 14://风机轮询时间
			toSetFragment(new HkFjLxsjFragment(),"");
			break;
		case 15://加热器温度新
			toSetFragment(new HkJrqTempFragmentNew(),"");
			break;
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
			ToastMsg(getActivity(), content + getActivity().getString(R.string.connect_time_out));
		}
	}
}