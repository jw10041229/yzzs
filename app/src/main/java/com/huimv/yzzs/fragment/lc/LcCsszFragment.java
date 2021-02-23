package com.huimv.yzzs.fragment.lc;

import java.util.Arrays;

import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.BluetoothScanActivity;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.GeneralCMDConstant;
import com.huimv.yzzs.constant.LcCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.fragment.general.BbxxFragment;
import com.huimv.yzzs.fragment.general.BjqPhoneNumberFragment;
import com.huimv.yzzs.fragment.general.BjsxjgFragment;
import com.huimv.yzzs.fragment.general.IpWgYmFragment;
import com.huimv.yzzs.fragment.general.LybmFragment;
import com.huimv.yzzs.fragment.general.LymcFragment;
import com.huimv.yzzs.fragment.general.MacAddressFragment;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.huimv.yzzs.util.wheel.FlzSetItemSelectIsShowWheelUtil;
import com.huimv.yzzs.util.wheel.ItemSelectWheelUtil;
import com.huimv.yzzs.util.wheel.LcDjSelectWheelUtil;
import com.huimv.yzzs.util.wheel.LcTitleWheelUtil;

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
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 料槽参数设置
 * @author jiangwei
 *
 */
public class LcCsszFragment extends YzzsBaseFragment implements OnClickListener,
		FlzSetItemSelectIsShowWheelUtil.OnItemConfirmClickSetListener,
		LcDjSelectWheelUtil.OnDjConfirmClickListener,
		LcTitleWheelUtil.OnTitleConfirmClickListener,EventHandler,
		ItemSelectWheelUtil.OnConfirmClickListener,ConnectTimeoutListener{
	//private final static String TAG = LcCsszFragment.class.getSimpleName();
	private ItemSelectWheelUtil mItemSelectWheelUtil;
	private boolean isTimeOut = true;
	private LcTitleWheelUtil mFlz_lc_titleWheelUtil;
	private SharePreferenceUtil mSpUtil;
	private LcDjSelectWheelUtil mLc_djSelectWheelUtil;
	private FlzSetItemSelectIsShowWheelUtil mFlz_setItemSelectIsShowWheelUtil;
	/**
	 * 其他
	 */
	private TextView tv_item;
	private TextView tv_title;
	private CheckedTextView ctv_slb_pack;
	private CheckedTextView ctv_xll_pack;
	private CheckedTextView ctv_cpdj_pack;
	
	private TextView tv_set_slb_value;
	private TextView tv_set_xll_value;
	private TextView tv_set_cpdj_value;
	
	private Button btn_save_slb;
	private Button btn_save_xll;
	private Button btn_save_cpdj;
	
	private Button btn_read_slb;
	private Button btn_read_xll;
	private Button btn_read_cpdj;
	private Button btn_read_sb;
	String[] items;
	/**
	 * 设备序号：默认为1号料槽
	 */
	private int devNumber = 1;
	
	private ImageView iv_set;
	private String identity = "1";
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_lc_cssz_fragment, null);
		initView(view);
		initListener();
		initData();
		return view;
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		identity = getArguments().getString("identity");// 根据这个Key来判断是什么身份
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
		mFlz_lc_titleWheelUtil = new LcTitleWheelUtil(getActivity(), "请选择料槽", items, LcCsszFragment.this);
		tv_title.setText("1号料槽");
		
		
		String itemFucation[] = getActivity().getResources().getStringArray(R.array.lc_function_set_array);
		mItemSelectWheelUtil = new ItemSelectWheelUtil(getActivity(), "请选择项目", itemFucation, LcCsszFragment.this);
		String djItem [] = new String[9];
		for (int i = 0; i < 9; i++) {
			djItem[i] = (i+1) + "级";
		}
		mLc_djSelectWheelUtil = new LcDjSelectWheelUtil(getActivity(), "请选择等级", djItem, LcCsszFragment.this);
		
		mFlz_setItemSelectIsShowWheelUtil = new FlzSetItemSelectIsShowWheelUtil(getActivity(), "请选择项目", Integer.valueOf(identity), LcCsszFragment.this);
	}
	/**
	 * 初始化view
	 * @param view
	 */
	private void initView(View view) {
		tv_title = (TextView) view.findViewById(R.id.tv_title);
		ctv_xll_pack = (CheckedTextView) view.findViewById(R.id.ctv_xll_pack);
		ctv_cpdj_pack = (CheckedTextView) view.findViewById(R.id.ctv_cpdj_pack);
		ctv_slb_pack = (CheckedTextView) view.findViewById(R.id.ctv_slb_pack);
		tv_item = (TextView) view.findViewById(R.id.tv_item);
		tv_set_slb_value = (TextView) view.findViewById(R.id.tv_set_slb_value);
		tv_set_xll_value = (TextView) view.findViewById(R.id.tv_set_xll_value);
		tv_set_cpdj_value = (TextView) view.findViewById(R.id.tv_set_cpdj_value);

		btn_save_slb = (Button) view.findViewById(R.id.btn_save_slb);
		btn_save_xll = (Button) view.findViewById(R.id.btn_save_xll);
		btn_save_cpdj = (Button) view.findViewById(R.id.btn_save_cpdj);
		
		btn_read_slb = (Button) view.findViewById(R.id.btn_read_slb);
		btn_read_xll = (Button) view.findViewById(R.id.btn_read_xll);
		btn_read_cpdj = (Button) view.findViewById(R.id.btn_read_cpdj);
		iv_set = (ImageView) view.findViewById(R.id.iv_set);
		
		btn_read_sb = (Button) view.findViewById(R.id.btn_read_sb);
	}
	
	private void initListener () {
		
		ctv_slb_pack.setOnClickListener(this);
		ctv_cpdj_pack.setOnClickListener(this);
		ctv_xll_pack.setOnClickListener(this);
		
		tv_item.setOnClickListener(this);
		tv_title.setOnClickListener(this);
		
		tv_set_slb_value.setOnClickListener(this);
		tv_set_xll_value.setOnClickListener(this);
		tv_set_cpdj_value.setOnClickListener(this);
		
		btn_save_slb.setOnClickListener(this);
		btn_save_xll.setOnClickListener(this);
		btn_save_cpdj.setOnClickListener(this);
		btn_read_slb.setOnClickListener(this);
		btn_read_xll.setOnClickListener(this);
		btn_read_cpdj.setOnClickListener(this);
		
		iv_set.setOnClickListener(this);
		
		btn_read_sb.setOnClickListener(this);
	}
	@Override
	public void onResume() {
		MessageReceiver.ehList.add(this);
		devNumber = 1;
		tv_title.setText("1号料槽");
		//readData(LcCMDConstant.LC_READ_SLB);
		//readData(LcCMDConstant.LC_READ_XLL);
		//readData(LcCMDConstant.LC_READ_CPDJ);
		cleanData();
		super.onResume();
	}
	
	@Override
	public void onPause() {
		MessageReceiver.ehList.remove(this);
		super.onPause();
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
	public void onMessage(byte[] message) {
		receivePack(message);
	}
	
	@Override
	public void onNetChange(boolean isNetConnected) {
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_read_sb:
			readDevData();
			break;
		case R.id.iv_set:
			mFlz_setItemSelectIsShowWheelUtil.showDialog();
			break;
		case R.id.tv_title:
			mFlz_lc_titleWheelUtil.showDialog();
			break;
		case R.id.tv_item:
			mItemSelectWheelUtil.showDialog(0, "请选择项目");
			break;
		case R.id.ctv_slb_pack:
			ctv_slb_pack.toggle();
			break;
		case R.id.ctv_xll_pack:
			ctv_xll_pack.toggle();
			break;
		case R.id.ctv_cpdj_pack:
			ctv_cpdj_pack.toggle();
			break;
		case R.id.btn_save_slb:
			saveData(LcCMDConstant.LC_SAVE_SLB,ctv_slb_pack,tv_set_slb_value);
			break;
		case R.id.btn_save_xll:
			saveData(LcCMDConstant.LC_SAVE_XLL,ctv_xll_pack,tv_set_xll_value);
			break;
		case R.id.btn_save_cpdj:
			saveData(LcCMDConstant.LC_SAVE_CPDJ,ctv_cpdj_pack,tv_set_cpdj_value);
			break;
		case R.id.btn_read_slb:
			readData(LcCMDConstant.LC_READ_SLB);
			break;
		case R.id.btn_read_xll:
			readData(LcCMDConstant.LC_READ_XLL);
			break;
		case R.id.btn_read_cpdj:
			readData(LcCMDConstant.LC_READ_CPDJ);
			break;
		case R.id.tv_set_slb_value:
			mLc_djSelectWheelUtil.showDialog(0, "等级");
			break;
		case R.id.tv_set_xll_value:
			mLc_djSelectWheelUtil.showDialog(1, "等级");
			break;
		case R.id.tv_set_cpdj_value:
			mLc_djSelectWheelUtil.showDialog(2, "等级");
			break;
		}
		super.onClick(v);
	}
	
	private void readDevData() {
		if(YzzsApplication.isConnected) {
			startTime(XtAppConstant.SEND_CMD_TIMEOUT, "");
			isTimeOut = true;
			showLoading("正在读取设备数据...");
			sendGetBaseCMD(LcCMDConstant.LC_SBBZ,LcCMDConstant.LC_READ_DEV);
		} else {
			ToastMsg(getActivity(), getString(R.string.bluetooth_disconnected));
		}
	}

	/**
	 * 保存数据
	 * @param cmd
	 * @param ctv
	 */
	private void saveData(int cmd, CheckedTextView ctv,TextView tvDJ) {
		if(YzzsApplication.isConnected) {
			startTime(XtAppConstant.SEND_CMD_TIMEOUT, "");
			isTimeOut = true;
			sendCsszCMD(LcCMDConstant.LC_SBBZ, cmd, tvDJ.getText().toString(), ctv.isChecked());
			//cleanData();
		} else {
			ToastMsg(getActivity(), getString(R.string.bluetooth_disconnected));
		}
	}
	
	/**
	 * 读取
	 * @param cmd
	 */
	private void readData(int cmd) {
		if(YzzsApplication.isConnected) {
			startTime(XtAppConstant.SEND_CMD_TIMEOUT, "");
			isTimeOut = true;
			showLoading("正在读取数据...");
			sendGetDataCMD(LcCMDConstant.LC_SBBZ, cmd);
			//cleanData();
		} else {
			ToastMsg(getActivity(), getString(R.string.bluetooth_disconnected));
		}
	}

	
	/**
	 * 获取数据
	 * @param dev
	 * @param cmd
	 */
	public void sendGetDataCMD(int dev, int cmd) {
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
		bundle.putInt("whichDev", LcCMDConstant.LC_SBBZ);
		mFragment.setArguments(bundle);
		ft.addToBackStack(null);
		ft.commit();
	}
	
	/**
	 * 清除数值
	 */
	private void cleanData() {
		tv_set_xll_value.setText("");
		tv_set_slb_value.setText("");
		tv_set_cpdj_value.setText("");
		
		ctv_slb_pack.setChecked(false);
		ctv_xll_pack.setChecked(false);
		ctv_cpdj_pack.setChecked(false);
	}

	/**
	 * 接收包
	 * @param message
	 */
	private void receivePack (byte[] message) {
		if (message.length < 6) {
			return;
		}
		if(message[0] == LcCMDConstant.LC_SBBZ) {
			int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
			switch (cmd) {
			case LcCMDConstant.LC_SAVE_SLB:
				getSaveResult(message);
				break;
			case LcCMDConstant.LC_SAVE_XLL:
				getSaveResult(message);
				break;
			case LcCMDConstant.LC_SAVE_CPDJ:
				getSaveResult(message);
				break;
			case LcCMDConstant.LC_READ_SLB:
				getReadResult(message,LcCMDConstant.LC_SAVE_SLB);
				break;
			case LcCMDConstant.LC_READ_XLL:
				getReadResult(message,LcCMDConstant.LC_SAVE_XLL);
				break;
			case LcCMDConstant.LC_READ_CPDJ:
				getReadResult(message,LcCMDConstant.LC_SAVE_CPDJ);
				break;
			case GeneralCMDConstant.RESTART_XT_DEVICE://设备重启
				restartDev(message);
				break;
			case LcCMDConstant.LC_READ_DEV:
				saveLcDevToSpFromBt(message);
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 保存料槽绑定数据到 sp
	 * 
	 * @param message
	 *            去头去尾的传感器数据
	 */
	private void saveLcDevToSpFromBt(byte[] message) {
		isTimeOut = false;
		int dataLength = message[6]; // 每段数据的长度
		// 截包：截取循环体数据
		byte[] byteCircle = Arrays.copyOfRange(message, 7, message.length);
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
		String devbdStr = sbResult.substring(0, sbResult.length()); // 去掉最后一个#
		mSpUtil.setLcSbbd(devbdStr);
		// 跳转到环控绑定界面
		toSetFragment(new LcDevFragment(),"lc_devFragment");
		dismissLoading();
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
	
	private void getReadResult(byte[] message,int cmd) {
		if (message[5] == 0) {
			isTimeOut = false;
			dismissLoading();
			if (message[6] == 0) {
				devNumber = message[8];
				int dj = message[9];
				int doPack = message[10];
				boolean isDoPack = doPack == 0;
				if (cmd == LcCMDConstant.LC_SAVE_SLB) {
					ToastMsg(getActivity(), "水料比读取成功");
					tv_set_slb_value.setText(dj + "级");
					ctv_slb_pack.setChecked(isDoPack);
				}
				if (cmd == LcCMDConstant.LC_SAVE_XLL) {
					ToastMsg(getActivity(), "下料量读取成功");
					tv_set_xll_value.setText(dj + "级");
					ctv_xll_pack.setChecked(isDoPack);
				}
				if (cmd == LcCMDConstant.LC_SAVE_CPDJ) {
					ToastMsg(getActivity(), "触碰等级读取成功");
					tv_set_cpdj_value.setText(dj + "级");
					ctv_cpdj_pack.setChecked(isDoPack);
				}
			} else {
				ToastMsg(getActivity(), "读取失败");
			}
		}
	}

	private void getSaveResult(byte[] message) {
		if (message[5] == 0) {
			isTimeOut = false;
			dismissLoading();
			if (message[6] == 0) {
				ToastMsg(getActivity(), "保存成功");
			} else {
				ToastMsg(getActivity(), "保存失败");
			}
		}
	}

	/**
	 * 发送数据
	 * @param dev 设备号
	 * @param cmd 命令
	 */
	public void sendCsszCMD(int dev, int cmd,String dj,boolean isDoPack ) {
		String dj2 = dj.replace("级", "");
		if(dj.equals("") || dj.equals("0级")) {
			ToastMsg(getActivity(), "等级不能为空或者为0级");
			return;
		}
		int doPack = isDoPack ? 0 : 1;
		int lenth = 15;
		
		final byte[] message = new byte[lenth];
		message[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);// h
		message[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);// m
		message[2] = YzzsCommonUtil.intTobyte(dev);
		message[3] = YzzsCommonUtil.intTobyte(cmd / 256);// 命令高位
		message[4] = YzzsCommonUtil.intTobyte(cmd % 256);// 命令低位
		message[5] = YzzsCommonUtil.intTobyte(lenth / 256);// 长度低位
		message[6] = YzzsCommonUtil.intTobyte(lenth % 256);// 长度高位
		message[7] = 3;// 后面值得长度
		message[8] = YzzsCommonUtil.intTobyte(devNumber);//几号设备
		message[9] = YzzsCommonUtil.intTobyte(Integer.valueOf(dj2));//等级
		message[10] = YzzsCommonUtil.intTobyte(doPack);//是否批处理
		message[11] = 1;// 校验位
		message[12] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);// e
		message[13] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);// n
		message[14] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);// d
		showLoading("正在保存数据...");
		sendUnpackData(message, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}


	@Override
	public void onConfirm(int position, int i) {
		switch (position) {
		case 0://清盘时间
			toSetFragment(new LcQpsjFragment(), "");
			break;
		case 1://时间校准
			toSetFragment(new LcSjjzFragment(), "");
			break;
		default:
			break;
		}
	}

	@Override
	public void onTitleConfirm(int position) {
		devNumber = position + 1;
		tv_title.setText(items[position]);
		readData(LcCMDConstant.LC_READ_SLB);
		readData(LcCMDConstant.LC_READ_XLL);
		readData(LcCMDConstant.LC_READ_CPDJ);
		cleanData();
	}

	@Override
	public void onDjConfirm(int position, int i) {
		switch (i) {
		case 0:
			tv_set_slb_value.setText((position + 1) + "级");
			break;
		case 1:
			tv_set_xll_value.setText((position + 1) + "级");
			break;
		case 2:
			tv_set_cpdj_value.setText((position + 1) + "级");
			break;
		}
	}

	@Override
	public void onItemConfirm(int position) {
		switch (position) {
		case 0:
			toSetFragment(new BbxxFragment(),"");
			break;
		case 1://报警生效时间
			toSetFragment(new BjsxjgFragment(),"");
			break;
		case 2:// 蓝牙别名
			if (hasLogined()) {
				toSetFragment(new LybmFragment(),"");
			} else {
				ToastMsg(getActivity(), "使用蓝牙别名功能前,请先登录账户");
			}
			break;
		case 3:// 蓝牙名称
			toSetFragment(new LymcFragment(),"");
			break;
		case 4:// 机器ID
			if (hasLogined()) {
				toSetFragment(new LcJqidFragment(),"");
			} else {
				ToastMsg(getActivity(), "在进行操作机器ID前,请登录账户！");
			}
			break;
		case 5:// 重启设备
			restartDevDialog();
			break;
		case 6:// ip/网关
			toSetFragment(new IpWgYmFragment(),"");
			break;
		case 7://报警器
			toSetFragment(new BjqPhoneNumberFragment(),"");
			break;
		case 8://MACK
			toSetFragment(new MacAddressFragment(),"");
			break;
		}
		
	}
	/**
	 * 判断是否已经登陆
	 */
	private boolean hasLogined() {
		return ("".equals(mSpUtil.getYhxm()) && "".equals(mSpUtil.getYhmm()));
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
		sendGetBaseCMD(LcCMDConstant.LC_SBBZ, GeneralCMDConstant.RESTART_XT_DEVICE);
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
