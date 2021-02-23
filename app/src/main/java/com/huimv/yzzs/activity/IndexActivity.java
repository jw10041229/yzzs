package com.huimv.yzzs.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huimv.yzzs.R;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseActivity;
import com.huimv.yzzs.base.YzzsBaseActivity.ConnectTimeoutListener;
import com.huimv.yzzs.constant.CdzCMDConstant;
import com.huimv.yzzs.constant.FlzCMDConstant;
import com.huimv.yzzs.constant.GeneralCMDConstant;
import com.huimv.yzzs.constant.HkCMDConstant;
import com.huimv.yzzs.constant.LcCMDConstant;
import com.huimv.yzzs.constant.QkCMDConstant;
import com.huimv.yzzs.constant.ScjCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.fragment.cdz.CdzContainerFragment;
import com.huimv.yzzs.fragment.flz.FlzContainerFragment;
import com.huimv.yzzs.fragment.hk.HkDevSetFragment;
import com.huimv.yzzs.fragment.hk.HkSshjxsFragment;
import com.huimv.yzzs.fragment.lc.LcSsxsFragment;
import com.huimv.yzzs.fragment.qk.QkSbbdFragment;
import com.huimv.yzzs.fragment.qk.QkSshjxsFragment;
import com.huimv.yzzs.fragment.scj.ScjSsxsFragment;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.service.BluetoothService;
import com.huimv.yzzs.support.general.IndexActivitySupport;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.orhanobut.logger.Logger;

import java.util.Arrays;

public class IndexActivity extends YzzsBaseActivity implements EventHandler,ConnectTimeoutListener{
	private final static String TAG = IndexActivity.class.getSimpleName();
	public final static String HK_SSXS_FRAGMENT = "HK_SSXS_FRAGMENT";
	public final static String QK_SSXS_FRAGMENT = "QK_SSXS_FRAGMENT";
	public final static String FLZ_SBTS_FRAGMENT = "FLZ_SBTS_FRAGMENT";
	public final static String CDZ_SBTS_FRAGMENT = "CDZ_SBTS_FRAGMENT";
	public final static String SCJ_SSXS_FRAGMENT = "SCJ_SSXS_FRAGMENT";
	public final static String HK_HKBD_FRAGMENT = "HK_HKBD_FRAGMENT";
	public final static String FLZ_CONTAINER_FRANGMENT = "FLZ_CONTAINER_FRANGMENT";
	public final static String LZ_SSXS_FRAGMENT = "LZ_SSXS_FRAGMENT";
	public final static String QK_SBBD_FRAGMENT = "QK_SBBD_FRAGMENT";
	public final static String HK_YXCS_CONTAINER_FRAGMENT = "HK_YXCS_CONTAINER_FRAGMENT";
	public final static String CDZ_CONTAINER_FRANGMENT = "CDZ_CONTAINER_FRANGMENT";
	public static BluetoothService mBluetoothLeService;
	private String mDeviceAddress,mDeviceName;////master2
	private TextView stateTv,tv_version;
	private final Context context = IndexActivity.this;
	private RelativeLayout titlebarLayout;
	private SharePreferenceUtil mSpUtil;
	public static int numDw = 5;//分几档
	private static long EXITTIME = 0;
	private boolean isBtConnected = false;
	public static int CGQ_LX [] = new int[] {0,0,1,2,2,4};//传感器类型
	public static String CGQ_WZ [] = new String[] {"00","01","02","03","03","03"};//传感器安装位置
	public static boolean flz_isRunning = false;//分栏站暂停/运行状态
	public static boolean flz_work_isDebuging = true;//是否处于调试模式
	public static boolean cdz_isRunning = false;//测定站暂停/运行状态
	public static boolean cdz_work_isDebuging = true;//是否处于调试模式
	private final IndexActivitySupport mIndexActivitySupport = new IndexActivitySupport();
	public static byte [] sbLxMax = {0,5,1,5,3,2,4,5,5,5,6,4,7,10,8,1,10,2};
	public static int SCJ_WORK_STATE = 0; // 手持机工作状态：0：“不可读取状态”
	public static int hk_work_model = 0;//0为半自动模式,1为自动
	private boolean hasFragmentFront = false;
	//private ImageView imageView;
	//private AnimationDrawable animationDrawable;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
		MessageReceiver.ehList.add(this);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		mDeviceAddress = getIntent().getStringExtra(BluetoothScanActivity.EXTRAS_DEVICE_ADDRESS);
		mDeviceName = getIntent().getStringExtra(BluetoothScanActivity.EXTRAS_DEVICE_NAME);
		Intent gattServiceIntent = new Intent(this, BluetoothService.class);
		startService(gattServiceIntent);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		initView();
		initData();
		//TODO yzzs 演示版本
/*	
		for (int i = 0; i < 10; i++) {
			byte [] message1 = new byte [] {1, 0, 24, 0, 20, 0, 48, 49, 52, 57, 50, 50, 53, 49};
			byte [] message2 = new byte [] {1, 0, 24, 0, 20, 0, 49, 49, 52, 57, 50, 50, 53, 51};
			byte [] message3 = new byte [] {1, 0, 24, 0, 20, 0, 51, 49, 52, 57, 50, 50, 53, 52};
			byte [] message4 = new byte [] {1, 0, 24, 0, 20, 0, 54, 49, 52, 57, 48, 55, 53, 54};
			mIndexActivitySupport.insertWenduDataEx(context, message1);
			mIndexActivitySupport.insertShiduDataEx(context, message2);
			mIndexActivitySupport.insertAnqiDataEx(context, message3);
			mIndexActivitySupport.insertPhDataEx(context, message4);
		}
		toFragment(new HkSshjxsFragment(),HK_SSXS_FRAGMENT);*/
	}
	private void initData() {
		hasFragmentFront = false;
		setConnectTimeoutListener(this);
		startTime(XtAppConstant.CONNECT_BLUETOOTH_TIMEOUT,"蓝牙连接");
		//tv_version.setText(YzzsApplication.getVersionName(this));
/*		int drawable[] = new int[] {R.drawable.delete,R.drawable.add};
		animationDrawable = new AnimationDrawable();
		for (int i = 0; i < drawable.length; i++) {
			animationDrawable.addFrame(getResources().getDrawable(drawable[i]), 250);
		}
		//设置手否重复播放，false为重复
		animationDrawable.setOneShot(false);
		imageView.setImageDrawable(animationDrawable);
		animationDrawable.start();*/
	}

	private void initView() {
		titlebarLayout = (RelativeLayout) findViewById(R.id.titlebarlayout);
		stateTv = (TextView) findViewById(R.id.stateTv);
		tv_version = (TextView) findViewById(R.id.tv_version);
		//imageView = (ImageView) findViewById(R.id.imageView);
	}

	private void toFragment(Fragment mFragment,String fragmentName) {
		hasFragmentFront = true;
		cancelTime();
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fragment_container, mFragment,fragmentName);
		ft.commitAllowingStateLoss();
		titlebarLayout.setVisibility(View.GONE);
		mSpUtil.setLymc(mDeviceName);
	}
	
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((BluetoothService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			Log.e(TAG, "mBluetoothLeService is okay");
			mBluetoothLeService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cancelTime();
		MessageReceiver.ehList.remove(this);
		unbindService(mServiceConnection);
		if(mBluetoothLeService != null)
		{
			mBluetoothLeService.close();
			mBluetoothLeService = null;
		}
		YzzsApplication.tempData = new byte[0];
		YzzsApplication.messCount = 0;
	}

	@Override
	public void onConnected(boolean isConnected) {
		isBtConnected = isConnected;
	}

	@Override
	public void onReady(boolean isReady) {
		YzzsApplication.isConnected = true;
		stateTv.setText("正在读取设备信息...");
		sendInitCMDEx(XtAppConstant.WHAT_DEV, 12);
	}

	/**
	 * 获取初始配置命令
	 * initCMD 命令
	 * lenth 长度
	 */
	private void sendInitCMDEx(int initCMD,int lenth) {
		final byte [] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);//h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);//m
		byteData[2] = 0;//设备区分标志
		byteData[3] = YzzsCommonUtil.intTobyte(initCMD / 256);//命令高位
		byteData[4] = YzzsCommonUtil.intTobyte(initCMD % 256);//命令低位
		byteData[5] = YzzsCommonUtil.intTobyte(lenth / 256);//命令低位
		byteData[6] = YzzsCommonUtil.intTobyte(lenth % 256);//命令高位
		byteData[7] = 0;//后面值得长度
		byteData[8] = 1;//校验位
		byteData[9] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);// e
		byteData[10] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//n
		byteData[11] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
		sendUnpackData(byteData, XtAppConstant.SEND_DELAY_GET_INIT_DATA);
	}
	
	@Override
	public void onMessage(byte[] message) {
		if (message.length < 6) {
			return;
		}
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		if (cmd == XtAppConstant.WHAT_DEV) {//判断什么设备的命令
			switch (message [0]) {
			case HkCMDConstant.HK_SBBZ:
				getHkInitData(message);//环控
				break;
			case QkCMDConstant.QK_SBBZ:
				getQkInitData(message);//全控
				break;
			case FlzCMDConstant.FLZ_SBBZ:
				getFlzInitData(message);//分栏站
				break;
			case ScjCMDConstant.SCJ_SBBZ:
				getScjInitData(message);// 手持机
				break;
			case LcCMDConstant.LC_SBBZ:
				getLcInitData(message);//料槽
				break;
			case CdzCMDConstant.CDZ_SBBZ:
				getCdzInitData(message);//測定站
				break;
			default:
				break;
			}
		}
		
		if(message[0] == HkCMDConstant.HK_SBBZ) {//环控实时数据
			getHkRealData(message);
		}
		
		if(message[0] == QkCMDConstant.QK_SBBZ) {//全控实时数据
			getQkRealData(message);
		}
		
		if (cmd == GeneralCMDConstant.READ_BBXX) {//读取版本信息
			if (!hasFragmentFront) {
				String version = getReadResult(message);
				switch (message [0]) {
				case HkCMDConstant.HK_SBBZ:
/*					if (YzzsApplication.versionCompare(XtAppConstant.CVersion,version)) {
						mSpUtil.setHkIsHasBpfjVersion("1");
					} else {
						mSpUtil.setHkIsHasBpfjVersion("0");
					}*/
					toFragment(new HkSshjxsFragment(),HK_SSXS_FRAGMENT);
					break;
				case QkCMDConstant.QK_SBBZ:
					toFragment(new QkSshjxsFragment(),QK_SSXS_FRAGMENT);
					break;
				case FlzCMDConstant.FLZ_SBBZ:
					toFragment(new FlzContainerFragment(),FLZ_CONTAINER_FRANGMENT);
					break;
				case ScjCMDConstant.SCJ_SBBZ:
					break;
				case LcCMDConstant.LC_SBBZ:
					toFragment(new LcSsxsFragment(),LZ_SSXS_FRAGMENT);
					break;
				case CdzCMDConstant.CDZ_SBBZ:
					toFragment(new CdzContainerFragment(),CDZ_CONTAINER_FRANGMENT);
					break;
				default:
					break;
				}
			}
		}
	}
	
	private String getReadResult(byte[] message) {
		String version = "";
		if (message [5] == 0) {
			if(message[6] == 0) {
				String msg = new String (message);
				int dataLenth = message[7];
				version = msg.substring(8,8 + dataLenth);
				mSpUtil.setVersion(version);
			}
		}
		return  version;
	}
	/**
	 * 料槽
	 * @param message
	 */
	private void getLcInitData(byte[] message) {
		if (message.length < 6) {
			return;
		}
		if (message [5] == 0) {//接收成功
			byte [] removeData = Arrays.copyOfRange(message, 6, message.length);//去头
			int dataLenth = removeData[0] ;//几种设备
			int lcCurrentCum;//装了几个料槽
			if (dataLenth == 0) {
				lcCurrentCum = 0;
			} else {
				lcCurrentCum = removeData [1];
			}
			sbLxMax = Arrays.copyOfRange(removeData, dataLenth + 2, dataLenth + 2 + removeData [dataLenth + 1]);
			mSpUtil.setZiLcNum(lcCurrentCum + "");//当前料槽个数
			sendGetBaseCMD(LcCMDConstant.LC_SBBZ, GeneralCMDConstant.READ_BBXX);
		}
	}

	/**
	 * 手持机初始化
	 * @param message
	 */
	private void getScjInitData(byte[] message) {
		if (message.length < 7) {
			return;
		}
		if(message[5] == 1){ // 单片机数据接收失败
			ToastMsg(this, "手持机接收数据失败");
			finish();
			return;
		}
		if (message[5] == 0) {//单片机数据接收成功
			// 手持机工作状态：0：不可读状态；1：可读状态
			byte [] removeData = Arrays.copyOfRange(message, 6, message.length);//去头
			SCJ_WORK_STATE = removeData[0];
			
			if (removeData.length > 1) {//说明后面还有数据
				int len = removeData[1];//后面数据的长度
				if (len == 8) {//版本号
					StringBuilder scjVersionSb = new StringBuilder();
					for (int i = 0; i < len; i++) {
						scjVersionSb.append(removeData[i + 3]);
					}
					YzzsApplication.getInstance().setScjVersion(scjVersionSb.toString());
				} 
			}
			toFragment(new ScjSsxsFragment(),SCJ_SSXS_FRAGMENT);
		}
	}
	/**
	 * 全控的数据
	 * @param message
	 */
	private void getQkRealData(byte[] message) {
		String DataName = "";
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		try {
			if(cmd == QkCMDConstant.GET_SHUIYA) {//如果收到水压
				DataName = "水压";
				mIndexActivitySupport.qk_insertShuiyaData(context, message);
			}
			if(cmd == QkCMDConstant.GET_SHUILIU) {//如果收到水流
				DataName = "水流";
				mIndexActivitySupport.qk_insertShuiliuData(context, message);
			}
			if (cmd ==QkCMDConstant.GET_PH) {
				DataName = "PH";
				mIndexActivitySupport.insertPhDataEx(context, message);
			}
		} catch (Exception e) {
			if(isHk_sshjxsFragment()) {
				ToastMsg(this, DataName + "数据异常");
			}
		}
	}

	/**
	 * 全控初始化
	 * @param message
	 */
	private void getQkInitData(byte[] message) {
		try {
			if (message [5] == 0) {//成功
				byte [] removeData = Arrays.copyOfRange(message, 6, message.length);//去头
				//IndexActivity.numDw = removeData[0];//总档位数
				int dataLenth = 1;
				//绑定了传感器
				if (removeData [1] >= 0) {
					CGQ_LX = new int[removeData [1]];
					CGQ_WZ = new String[removeData [1]];
					for (int i = 0; i < removeData[1]; i ++) {
						CGQ_LX[i] = removeData[i * 3 + 2];//绑定的传感器类型
						CGQ_WZ[i] = removeData[i * 3 + 2 + 1] + "" + removeData[i * 3 + 2 + 2];//传感器位置
					}
					dataLenth += removeData [1] * 3 ;
				}
				
				if (removeData.length > dataLenth + 1) {//如果大于表示还有其他信息
					if (removeData [dataLenth + 1] > 0) {//这里是设备的容量
						sbLxMax = Arrays.copyOfRange(removeData, dataLenth + 2, dataLenth + 2 + removeData [dataLenth + 1]);
					}
					dataLenth += removeData [dataLenth + 1]  + 1;
				}
				
				if (removeData.length > dataLenth + 1) {
					if (removeData [dataLenth + 1] > 0) {//这里是运行模式：半自动还是自动
						hk_work_model = removeData[dataLenth + 2];
					}
					//dataLenth += removeData [dataLenth + 1]  + 1;
				}
				
				sendGetBaseCMD(QkCMDConstant.QK_SBBZ, GeneralCMDConstant.READ_BBXX);
			}
		} catch (NumberFormatException e) {
			ToastMsg(context, "获取初始化配置错误");
		}
	}
	/**
	 * 分栏站初始化
	 * @param message
	 */
	private void getFlzInitData(byte[] message) {
		if (message.length < 9) {
			return;
		}
		if (message [5] == 0) {//接收成功
			flz_isRunning = message[6] != 1;//1 暂停，0运行
			mSpUtil.setZjtdfx(message[7] + "");
			mSpUtil.setMrtdfx(message[8] + "");
			flz_work_isDebuging = message[9] != 0;
			sendGetBaseCMD(FlzCMDConstant.FLZ_SBBZ, GeneralCMDConstant.READ_BBXX);
		}
	}
	/**
	 * 测定站初始化
	 * @param message
	 */
	private void getCdzInitData(byte[] message) {
		if (message.length < 9) {
			return;
		}
		if (message [5] == 0) {//接收成功
			cdz_isRunning = message[6] != 1;//1 暂停，0运行
			cdz_work_isDebuging = message[9] != 0;
			sendGetBaseCMD(CdzCMDConstant.CDZ_SBBZ, GeneralCMDConstant.READ_BBXX);
		}
	}
	/**
	 * 得到温湿度实时数据
	 * @param message
	 */
	private void getHkRealData(byte[] message) {
		String DataName = "";
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		try {
			switch (cmd) {
			case HkCMDConstant.GET_WENDU:
				DataName = "温度";
				mIndexActivitySupport.insertWenduDataEx(context, message);
				break;
			case HkCMDConstant.GET_SHIDU:
				DataName = "湿度";
				mIndexActivitySupport.insertShiduDataEx(context, message);
				break;
			case HkCMDConstant.GET_ANQI:
				DataName = "氨气";
				mIndexActivitySupport.insertAnqiDataEx(context, message);
				break;
			case HkCMDConstant.GET_PH:
				DataName = "PH";
				mIndexActivitySupport.insertPhDataEx(context, message);
				break;
			default:
				break;
			}
		} catch (NumberFormatException e) {
			if(isHk_sshjxsFragment()) {
				ToastMsg(this, DataName + "数据异常");
			}
		}
	}

	/**
	 * 获取初始化参数,并且根据参数跳转到相应的页面
	 * @param message
	 */
	private void getHkInitData(byte[] message) {
		try {
			if (message [5] == 0) {//成功
				byte [] removeData = Arrays.copyOfRange(message, 6, message.length);//去头
				IndexActivity.numDw = removeData[0];//总档位数
				int dataLenth = 1;
				//绑定了传感器
				if (removeData [1] >= 0) {
					CGQ_LX = new int[removeData [1]];
					CGQ_WZ = new String[removeData [1]];
					for (int i = 0; i < removeData[1]; i ++) {
						CGQ_LX[i] = removeData[i * 3 + 2];//绑定的传感器类型
						CGQ_WZ[i] = removeData[i * 3 + 2 + 1] + "" + removeData[i * 3 + 2 + 2];//传感器位置
					}
					dataLenth += removeData [1] * 3 ;
				}
				
				if (removeData.length > dataLenth + 1) {//如果大于表示还有其他信息
					if (removeData [dataLenth + 1] > 0) {//这里是设备的容量
						sbLxMax = Arrays.copyOfRange(removeData, dataLenth + 2, dataLenth + 2 + removeData [dataLenth + 1]);
						Logger.d("sbLxMax",Arrays.toString(sbLxMax));
					}
					dataLenth += removeData [dataLenth + 1]  + 1;
				}
				
				if (removeData.length > dataLenth + 1) {
					if (removeData [dataLenth + 1] > 0) {//这里是运行模式：半自动还是自动
						hk_work_model = removeData[dataLenth + 2];
					}
					//dataLenth += removeData [dataLenth + 1]  + 1;
				}
				sendGetBaseCMD(HkCMDConstant.HK_SBBZ, GeneralCMDConstant.READ_BBXX);
			}
		} catch (NumberFormatException e) {
			ToastMsg(context, "获取初始化配置错误");
		}
	}


	@Override
	public void onNetChange(boolean isNetConnected) {
	}
	
	@Override
	protected void onPause() {
		//animationDrawable.stop();
		super.onPause();
	}
	
	/**
	 * 重启设备
	 */
	private void restartDevDialog () {
		new AlertDialog.Builder(this).setMessage("五档还未保存完成,如强制退出将会导致数据错误").setTitle("退出警告")
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}
	
	@Override
	public void onBackPressed() {
		if(isBtConnected) {
			if(getCurrentFragment(HK_YXCS_CONTAINER_FRAGMENT)) {
				if (IndexActivity.hk_work_model == 0) {
					if(!mSpUtil.getDwSave().equals(String.valueOf(numDw))) {
						restartDevDialog();
						return;
					}
				}
			}
			if(getCurrentFragment(FLZ_SBTS_FRAGMENT)) {
				if (IndexActivity.flz_work_isDebuging) {//如果是调试模式
					ToastMsg(this, "当前处于调试模式,请退出调试模式");
					return;
				}
			}

			if(getCurrentFragment(CDZ_SBTS_FRAGMENT)) {
				if (IndexActivity.cdz_work_isDebuging) {//如果是调试模式
					ToastMsg(this, "当前处于调试模式,请退出调试模式");
					return;
				}
			}
			if(getCurrentFragment(FLZ_CONTAINER_FRANGMENT) || getCurrentFragment(CDZ_CONTAINER_FRANGMENT)) {
				if ((System.currentTimeMillis() - EXITTIME) > 1500) { // 当连按两次back键超过2秒钟，则提示用户，否则关闭应用
					ToastMsg(this, getString(R.string.press_again_exit));
					EXITTIME = System.currentTimeMillis();
				} else {
					finish();
				}
				return;
			}
			if(getCurrentFragment(HK_HKBD_FRAGMENT)) {
				if (!HkDevSetFragment.isDataSaveOk) {
					ToastMsg(this, "当前还未保存成功,请重新保存");
					return;
				}
			}
			if(getCurrentFragment(QK_SBBD_FRAGMENT)) {
				if (!QkSbbdFragment.isDataSaveOk) {
					ToastMsg(this, "当前还未保存成功,请重新保存");
					return;
				}
			}
			if (getCurrentFragment(HK_SSXS_FRAGMENT)) {
				finish();
			}
		}
		super.onBackPressed();
	}
	
	/**
	 * 判断当前的framgent
	 */
	private boolean getCurrentFragment (String framgnetName) {
		Fragment currentFragment = getSupportFragmentManager().
				findFragmentByTag(framgnetName);
		return currentFragment != null && currentFragment.isVisible();
	}
	/**
	 * 判断当前显示的是不是sshjxsFragment
	 * @return
	 */
	private boolean isHk_sshjxsFragment() {
		Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(HK_SSXS_FRAGMENT);
		return currentFragment != null && currentFragment.isVisible();
	}

	@Override
	public void Timeout(String content) {
		Toast.makeText(this, content + getString(R.string.connect_time_out), Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {
				
				@Override 
				public void run() {
					if(YzzsApplication.isTopActivity(IndexActivity.this,TAG)) {
						startActivity(new Intent(IndexActivity.this,BluetoothScanActivity.class));
						finish();
					}
				}
			}, 2000);
	}
}