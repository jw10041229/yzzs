package com.huimv.yzzs.fragment.cdz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.BluetoothScanActivity;
import com.huimv.yzzs.activity.IndexActivity;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.CdzCMDConstant;
import com.huimv.yzzs.constant.GeneralCMDConstant;
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
import com.huimv.yzzs.util.wheel.CdzDevDebugItemSelectWheelUtil;
import com.huimv.yzzs.util.wheel.FlzSetItemSelectIsShowWheelUtil;

/**
 * 测定设备调试
 * @author jiangwei
 *
 */
public class CdzSbtsFragment extends YzzsBaseFragment implements OnClickListener,
EventHandler,CdzDevDebugItemSelectWheelUtil.onDevDebugConfirmClickListener,FlzSetItemSelectIsShowWheelUtil.OnItemConfirmClickSetListener,ConnectTimeoutListener{
	private final StringBuffer dataStr = new StringBuffer();
	private CdzDevDebugItemSelectWheelUtil mCdz_dev_debug_ItemSelectWheelUtil;
	private FlzSetItemSelectIsShowWheelUtil mCdz_setItemSelectIsShowWheelUtil;
	private TextView tv_manual_debug;
	private TextView tv_exit_debug;
	private TextView tv_sensor_state;
	private ImageView iv_set;
	private ImageView iv_sensor_location;
	private ScrollView sv_yxzt;
	private SharePreferenceUtil mSpUtil;
	private String identity = "1";
	/**
	 * 是否超时
	 */
	private boolean isTimeOut = true;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_flz_sbts_fragment, rootView,false);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		initView(view);
		initData();
		return view;
	}
	
	private void initData() {
		setConnectTimeoutListener(this);
		identity = getArguments().getString("identity");// 根据这个Key来判断是什么身份
		mCdz_setItemSelectIsShowWheelUtil = new FlzSetItemSelectIsShowWheelUtil(getActivity(), "请选择子项目",Integer.valueOf(identity),CdzSbtsFragment.this);
		mCdz_dev_debug_ItemSelectWheelUtil = new CdzDevDebugItemSelectWheelUtil(getActivity(), "请选择调试项目",CdzSbtsFragment.this);
		tvStateIsDebug();
	}

	private void initView(View view) {
		iv_set = (ImageView) view.findViewById(R.id.iv_set);
		iv_set.setOnClickListener(this);
		tv_manual_debug = (TextView) view.findViewById(R.id.tv_manual_debug);
		tv_manual_debug.setOnClickListener(this);
		tv_exit_debug = (TextView) view.findViewById(R.id.tv_exit_debug);
		tv_exit_debug.setOnClickListener(this);
		tv_sensor_state = (TextView) view.findViewById(R.id.tv_sensor_state);
		sv_yxzt = (ScrollView) view.findViewById(R.id.sv_yxzt);
		iv_sensor_location =  (ImageView) view.findViewById(R.id.iv_sensor_location);
		int screenWidth = YzzsApplication.getDisplayMetrics().widthPixels;
		ViewGroup.LayoutParams lp = iv_sensor_location.getLayoutParams();
		lp.width = screenWidth;
		lp.height = screenWidth;
		iv_sensor_location.setLayoutParams(lp);
	}

	@Override
	public void onPause() {
		MessageReceiver.ehList.remove(this);
		super.onPause();
	}

	@Override
	public void onResume() {
		MessageReceiver.ehList.add(this);
		iv_sensor_location.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.cdz_sensor_location));
		super.onResume();
	}
	
	/**
	 * tv显示
	 */
	private void tvStateIsDebug() {
		if (IndexActivity.cdz_work_isDebuging) {
			tv_sensor_state.setText("当前处于调试模式");
		} else {
			tv_sensor_state.setText("当前处于运行模式");
		}
	}
	@Override
	public void onConnected(boolean isConnected) {
	}
	
	@Override
	public void onReady(boolean isReady) {
	}
	
	@Override
	public void onNetChange(boolean isNetConnected) {
	}

	@Override
	public void onDevDebugConfirm(int position) {
		String pistionName = getResources().getStringArray(R.array.cdz_debug_wheel_string_item_array)[position];
		int cmd = -1;
		if (pistionName .equals("打开入口门")) {
			cmd = CdzCMDConstant.CDZ_DKRKM;
		}
		if (pistionName .equals("关闭入口门")) {
			cmd = CdzCMDConstant.CDZ_GBRKM;
		}
		if (pistionName .equals("喷墨一")) {
			cmd = CdzCMDConstant.CDZ_PM1;
		}
		if (pistionName .equals("喷墨二")) {
			cmd = CdzCMDConstant.CDZ_PM2;
		}
		if (pistionName .equals("快速调试")) {
			cmd = CdzCMDConstant.CDZ_KSTS;
		}
		if (pistionName .equals("称重调试")) {
			toFragment(new CdzCztsFragment());
		}
		if (pistionName .equals("RFID调试")) {
			toFragment(new CdzRFIDtsFragment());
		}
		if (pistionName .equals("料槽时间")) {
			toFragment(new CdzLcsjFragment());
		}
		if (pistionName .equals("电机调试")) {
			toFragment(new CdzDjtsFragment());
		}
		if (cmd != -1) {
			if (cmd == CdzCMDConstant.CDZ_KSTS) {
				showLoading("正在快速调试...");
				startTime(20000, pistionName);
			} else {
				showLoading("正在调试...");
				startTime(6000, pistionName);
			}
			sendGetBaseCMD(CdzCMDConstant.CDZ_SBBZ, cmd);
			isTimeOut = true;
		}
	}
	private void toFragment(Fragment mFragment){
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fragment_container, mFragment);
		ft.addToBackStack(null);
		ft.commit();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_manual_debug:
			if(YzzsApplication.isConnected) {
				if (IndexActivity.cdz_work_isDebuging) {//如果是调试模式,怎直接运行调试项目
					mCdz_dev_debug_ItemSelectWheelUtil.showDialog();
				} else {//如果不是调试模式，则发通知告诉本地进入调试模式
					prepareDebugDialog();
				}
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		case R.id.tv_exit_debug:
			if(YzzsApplication.isConnected) {
				if(IndexActivity.cdz_work_isDebuging) {
					doExitDebug();
					isTimeOut = true;
					startTime(5000, "退出调试模式");
				} else {
					ToastMsg(getActivity(), "当前处于运行模式,不可进行此操作");
				}
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		case R.id.iv_set:
			mCdz_setItemSelectIsShowWheelUtil.showDialog();
			break;
		default:
			break;
		}
		super.onClick(v);
	}
	
	/**
	 * 进入调试模式
	 */
	private void prepareDebug() {
		showLoading("正在准备调试...");
		sendGetBaseCMD(CdzCMDConstant.CDZ_SBBZ, CdzCMDConstant.CDZ_ZBTS);
	}
	/**
	 * 是否进入调试模式
	 */
	private void prepareDebugDialog () {
		new AlertDialog.Builder(getActivity()).setMessage("是否进入调试模式?").setTitle("是否进入调试模式?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						prepareDebug();
						isTimeOut = true;
						startTime(XtAppConstant.SEND_CMD_TIMEOUT, "进入调试模式");
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}
	/**
	 * 退出调试
	 */
	private void doExitDebug() {
		showLoading("正在退出调试...");
		sendGetBaseCMD(CdzCMDConstant.CDZ_SBBZ, CdzCMDConstant.CDZ_TCTS);
	}

	@Override
	public void onMessage(byte[] message) {
		receivePack(message);
	}
	/**
	 * 接收包
	 * @param message
	 */
	private void receivePack(byte[] message) {
		if (message.length <= 6) {
			return;
		}
		if(message[0] == CdzCMDConstant.CDZ_SBBZ) {//分栏站
			try {
				int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
				switch (cmd) {
				case CdzCMDConstant.CDZ_KSTS://快速调试
					kstsDataParsing(message);
					break;
				case CdzCMDConstant.CDZ_DKRKM://打开入口门
				case CdzCMDConstant.CDZ_GBRKM://关闭入口门
					sensorStatePausing(message);
					break;
				case CdzCMDConstant.CDZ_PM1://喷墨1
				case CdzCMDConstant.CDZ_PM2://喷墨2
					pmStatePausing(message);
					break;
				case CdzCMDConstant.CDZ_TCTS://退出调试
					exitDebugPausing(message);
					break;
				case GeneralCMDConstant.RESTART_XT_DEVICE://设备重启
					restartDev(message);
					break;
				case CdzCMDConstant.CDZ_ZBTS://进入调试模式
					doPreparePausing(message);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				ToastMsg(getActivity(), "数据出错");
				dismissLoading();
			}
		}
	}
	/**
	 * 喷墨
	 * @param message
	 */
	private void pmStatePausing(byte[] message) {
		if (message[5] == 0 && message[6] == 0) {//喷墨
			dismissLoading();
			isTimeOut = false;
			tvStateIsDebug();
			ToastMsg(getActivity(), "喷墨成功!");
		} else {
			ToastMsg(getActivity(), "喷墨失败");
		}
	}

	/**
	 * 重启设备
	 * @param message
	 */
	private void restartDev(byte[] message) {
		if (message[5] == 0 && message[6] == 0) {//重启设备
			dismissLoading();
			isTimeOut = false;
			toBluetoothScan();
			ToastMsg(getActivity(), "设备重启成功!");
		} else {
			ToastMsg(getActivity(), "设备重启失败");
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
	 * 调试模式
	 * @param message
	 */
	private void doPreparePausing(byte[] message) {
		if (message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if (message[6] == 0) {// 动作
				IndexActivity.cdz_work_isDebuging = true;
				tvStateIsDebug();
				mCdz_dev_debug_ItemSelectWheelUtil.showDialog();
			} else { // 动作
				ToastMsg(getActivity(), "进入调试模式失败");
			}
		}
	}
	/**
	 * 退出调试模式成功
	 * @param message
	 */
	private void exitDebugPausing(byte[] message) {
		if (message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if (message[6] == 0) {// 动作
				IndexActivity.cdz_work_isDebuging = false;
				tvStateIsDebug();
				ToastMsg(getActivity(), "动作完成!");
			} else { // 动作
				ToastMsg(getActivity(), "动作失败");
			}
		}
	}
	/**
	 * 传感器数据解析
	 * @param message
	 */
	private void sensorStatePausing(byte[] message) {
		dismissLoading();
		if (message.length < 10) {
			ToastMsg(getActivity(), "数据出错");
			return;
		}
		isTimeOut = false;
		if (message [5] == 0) {//接受收据成功
			for (int i = 0; i < message[7]; i++) {
				String textBottom = "\n";
				if (i == 8) {
					textBottom = "";
				}
				if (message[i + 8] == 1) {
					dataStr.append("S").append(i + 1).append("传感器灯亮").append(textBottom);
				} else {
					dataStr.append("S").append(i + 1).append("传感器灯不亮").append(textBottom);
				}
			}
			sv_yxzt.post(new Runnable() {
				public void run() {
					sv_yxzt.fullScroll(ScrollView.FOCUS_FORWARD);
				}
			});
			tv_sensor_state.setText(dataStr.toString());
			dataStr.delete(0, dataStr.length());
			if (message [6] == 0) {
				ToastMsg(getActivity(), "动作成功");
			} else {
				ToastMsg(getActivity(), "动作失败");
			}
			
		}
	}

	/**
	 * 快速调试
	 */
	private void kstsDataParsing(byte [] message) {
		if (message[5] == 0) {// 恢复出厂设置成功
			dismissLoading();
			isTimeOut = false;
			tvStateIsDebug();
			if (message[6] == 0) {
				ToastMsg(getActivity(), "快速调试完成!");
			} else {
				ToastMsg(getActivity(), "快速调试失败");
			}
		} else {
			ToastMsg(getActivity(), "快速调试失败");
		}
	}
	
	/**
	 * 子项目选项
	 */
	@Override
	public void onItemConfirm(int position) {
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
		case 3:// 机器ID
			if (hasLogined()) {
				toSetFragment(new CdzJqidFragment());
			} else {
				ToastMsg(getActivity(), "在进行操作机器ID前,请登录账户！");
			}
			break;
		case 4://重启设备
			restartDevDialog();
			break;
		case 5:// ip/网关
			toSetFragment(new IpWgYmFragment());
			break;
		case 6:// 报警器号码
			toSetFragment(new BjqPhoneNumberFragment());
			break;
		case 7:// MAC
			toSetFragment(new MacAddressFragment());
			break;
		case 8://蓝牙指示灯
			toSetFragment(new LyzsdVersionFragment());
			break;
		}
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
	 * 设备重启
	 */
	private void sendRestartDeviceCMD() {
		sendGetBaseCMD(CdzCMDConstant.CDZ_SBBZ, GeneralCMDConstant.RESTART_XT_DEVICE);
	}

	/**
	 * 判断是否已经登陆
	 */
	private boolean hasLogined() {
		return (!"" .equals(mSpUtil.getYhxm()) || !"" .equals(mSpUtil.getYhmm()) );
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
			tvStateIsDebug();
			ToastMsg(getActivity(), content + getString(R.string.connect_time_out));
		}
	}
}
