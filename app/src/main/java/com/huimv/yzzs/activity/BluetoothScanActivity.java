/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huimv.yzzs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huimv.android.basic.util.AndroidUtil;
import com.huimv.android.basic.util.CommonUtil;
import com.huimv.android.basic.util.Md5Util;
import com.huimv.yzzs.R;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseActivity;
import com.huimv.yzzs.base.YzzsBaseActivity.ConnectTimeoutListener;
import com.huimv.yzzs.bean.UserBean;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.db.entity.Da_bt;
import com.huimv.yzzs.db.entity.Da_mc;
import com.huimv.yzzs.support.general.BluetoothScanActivitySupport;
import com.huimv.yzzs.util.BluetoothUtils;
import com.huimv.yzzs.util.SharePreferenceUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.webservice.ResultCode;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class BluetoothScanActivity extends YzzsBaseActivity
		implements OnLongClickListener, Callback, MessageReceiver.EventHandler, ConnectTimeoutListener {
	private LeDeviceListAdapter mLeDeviceListAdapter;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;
	private ListView btListView;
	private TextView scanTv, tv_noDevData;
	private ProgressBar mProgressBar;
	private static final int REQUEST_ENABLE_BT = 1;
	// Stops scanning after 10 seconds.
	private static final long SCAN_PERIOD = 10000;
	private ImageView ivIconLogo;
	private LinearLayout ll_bottom_bar;
	private SharePreferenceUtil mSpUtil;
	private Button btnUpload, btnLogin, btnLogout;
	private BluetoothUtils ble;
	private Handler handler = new Handler(this);
	private Gson gson = new GsonBuilder().create();
	private List<Da_bt> daBtListFromDB = new ArrayList<>(); // 用于保存数据库中蓝牙数据列表
	private List<Da_bt> daBtList = new ArrayList<>(); // 用于保平台下发的蓝牙数据列表
	private boolean isLoginFlag = false; // 用于标记是否为登录操作的标记位
	private String username, password; // 用于记录登录的用户名与密码（MD5加密后）

	private boolean isTimeOut = true; // 是否超时
	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		YzzsApplication.addActivity(this);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		btListView = (ListView) findViewById(R.id.btListView);
		scanTv = (TextView) findViewById(R.id.scanTv);
		tv_noDevData = (TextView) findViewById(R.id.tv_noDevData);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mProgressBar.setVisibility(View.INVISIBLE);
		ll_bottom_bar = (LinearLayout) findViewById(R.id.ll_bottom_bar);
		ivIconLogo = (ImageView) findViewById(R.id.iv_icon_logo);
		ivIconLogo.setOnLongClickListener(this);
		btnUpload = (Button) findViewById(R.id.btn_upload);
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnLogout = (Button) findViewById(R.id.btn_logout);

		btnUpload.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
		btnLogout.setOnClickListener(this);
		/**
		 * 超时监听
		 */
		setConnectTimeoutListener(this);

		// 初始化界面时，如果 sp 中用户名和密码是否为空，显示登录
		if (CommonUtil.isEmpty(mSpUtil.getYhmm())) {
			btnLogin.setVisibility(View.VISIBLE);
			btnLogout.setVisibility(View.GONE);
		} else {
			// sp 中不为空，说明有账户登录，显示注销
			btnLogin.setVisibility(View.GONE);
			btnLogout.setVisibility(View.VISIBLE);
			btnLogout.setText(mSpUtil.getYhxm());
		}

		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
			finish();
		}

		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		btListView.setEmptyView(tv_noDevData);
		mLeDeviceListAdapter = new LeDeviceListAdapter();
		btListView.setAdapter(mLeDeviceListAdapter);
		scanTv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mBluetoothAdapter.isEnabled()) {
					ToastMsg(BluetoothScanActivity.this, "手机蓝牙未打开");
					return;
				}
				if (scanTv.getText().toString().equals(getString(R.string.menu_scan))) {// 搜索
					scanLeDevice(true);
				} else {// 停止
					scanLeDevice(false);
				}
			}
		});
		btListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!mBluetoothAdapter.isEnabled()) {
					ToastMsg(BluetoothScanActivity.this, "手机蓝牙未打开");
					return;
				}
				final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
				if (device == null) {
					return;
				}
				// 蓝牙名称，蓝牙地址保存到 sp 中
				mSpUtil.setLymc(device.getName());
				mSpUtil.setLydz(device.getAddress());
				
				if (!hasLogined()) {
					connectDevDialog(device);
				} else {
					toIndexActivity(device);
				}
			}
		});
		
		if (scanTv.getText().toString().equals(getString(R.string.menu_scan))) {// 搜索
			mLeDeviceListAdapter.clear();
			scanLeDevice(true);
		} else {// 停止
			scanLeDevice(false);
		}
		//new AnimationUtil().setShowAnimation(ll_bottom_bar, 3000);
        //设置动画，从自身位置的最下端向上滑动了自身的高度，持续时间为500ms
		ll_bottom_bar.setVisibility(View.INVISIBLE);
        final TranslateAnimation ctrlAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
        ctrlAnimation.setDuration(2000L);     //设置动画的过渡时间
        ll_bottom_bar.postDelayed(new Runnable() {
            @Override
            public void run() {
            	ll_bottom_bar.setVisibility(View.VISIBLE);
            	ll_bottom_bar.startAnimation(ctrlAnimation);
            }
        }, 500);
	}
	
	private void toIndexActivity(BluetoothDevice device) {
		final Intent intent = new Intent(BluetoothScanActivity.this, IndexActivity.class);
		intent.putExtra(EXTRAS_DEVICE_NAME, device.getName());
		intent.putExtra(EXTRAS_DEVICE_ADDRESS, device.getAddress());
		if (mScanning) {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			mScanning = false;
		}
		startActivity(intent);
	}
	
	private void pBIsVisible() {
		if (!mScanning) {
			mProgressBar.setVisibility(View.GONE);
			scanTv.setText(R.string.menu_scan);
		} else {
			mProgressBar.setVisibility(View.VISIBLE);
			scanTv.setText(R.string.menu_stop);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		YzzsApplication.isConnected = false;
		MessageReceiver.ehList.add(this);
		isLogined2SetBtn();
		
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
		mLeDeviceListAdapter.clear();
		mLeDeviceListAdapter.notifyDataSetChanged();
	}

	/**
	 * 是否已经登陆设置同步按是否可点击
	 */
	private void isLogined2SetBtn() {
		if (!mSpUtil.getYhxm().equals("") && !mSpUtil.getYhmm().equals("")) {
			btnUpload.setVisibility(View.VISIBLE);
			// btnUpload.setBackground(getResources().getDrawable(R.drawable.mybutton));
		} else {
			btnUpload.setVisibility(View.GONE);
			// btnUpload.setEnabled(false);
			// btnUpload.setBackgroundColor(getResources().getColor(R.color.gray_light));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MessageReceiver.ehList.remove(this);
		scanLeDevice(false);
		// mLeDeviceListAdapter.clear();
	}

	private void scanLeDevice(final boolean enable) {
		if (enable) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (mScanning) {
						mScanning = false;
						//mBluetoothAdapter.stopLeScan(mLeScanCallback);
						ble.stopScanning();
						pBIsVisible();
					}
				}
			}, SCAN_PERIOD);
			mLeDeviceListAdapter.clear();
			mScanning = true;
			mHandler.sendEmptyMessage(1);
			//mBluetoothAdapter.startLeScan(mLeScanCallback);
			initBle();
		} else {
			mScanning = false;
			//mBluetoothAdapter.stopLeScan(mLeScanCallback);
			//initBle();
			ble.stopScanning();
		}
		pBIsVisible();
	}

	/**
	 * 开始蓝牙扫描
	 */
	private boolean initBle() {
		ble = BluetoothUtils.sharedInstance(this);
		boolean isBLEable = ble.initBleManager();
		boolean isStartScanning = ble.startScanning(10, new BluetoothUtils.OnBleScanListener() {
			@Override
			public void onScanDevice(BluetoothDevice device) {
				mLeDeviceListAdapter.addDevice(device);
				mHandler.sendEmptyMessage(XtAppConstant.WHAT_BLUETOOTHSCAN_THREAD);
			}

			@Override
			public void onScanBeaconDevice(BluetoothUtils.BeaconDevice device) {

			}

			@Override
			public void onScanStop() {
			}
		});
		return isBLEable && isStartScanning;
	}

	private static char findHex(byte b) {
		int t = Byte.valueOf(b).intValue();
		t = t < 0 ? t + 16 : t;

		if ((0 <= t) && (t <= 9)) {
			return (char) (t + '0');
		}

		return (char) (t - 10 + 'A');
	}

	public static String ByteToString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		// for (int i = 0; i < bytes.length && bytes[i] != (byte) 0; i++) {
		for (byte byteValue:bytes) {
			sb.append(findHex((byte) ((byteValue & 0xf0) >> 4)));
			sb.append(findHex((byte) (byteValue & 0x0f)));
		}
		return sb.toString();
	}

	// Adapter for holding devices found through scanning.
	private class LeDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflator;

		private LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<>();
			mInflator = BluetoothScanActivity.this.getLayoutInflater();
		}

		private void addDevice(BluetoothDevice device) {
			if (!mLeDevices.contains(device)) {
				mLeDevices.add(device);
			}
		}

		private BluetoothDevice getDevice(int position) {
			return mLeDevices.get(position);
		}

		public void clear() {
			mLeDevices.clear();
		}

		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int i) {
			return mLeDevices.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder;
			// General ListView optimization code.
			if (view == null) {
				view = mInflator.inflate(R.layout.listitem_device, viewGroup,false);
				viewHolder = new ViewHolder();
				viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
				viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
				viewHolder.deviceLymc = (TextView) view.findViewById(R.id.device_lymc);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			BluetoothDevice device = mLeDevices.get(i);
			final String deviceName = device.getName(); // 蓝牙名称
			final String deviceAddress = device.getAddress(); // 蓝牙地址

			// 根据蓝牙地址，从数据库中获取蓝牙别名
			String lybm = BluetoothScanActivitySupport.getLybmByLydz(BluetoothScanActivity.this, deviceAddress);
			// 别名存在
			if (lybm != null && lybm.length() > 0) {
				viewHolder.deviceName.setText(lybm);
				String TextValue = "蓝牙名称:" + device.getName();
				viewHolder.deviceLymc.setText(TextValue);
			} else { // 别名不存在
				if (deviceName != null && deviceName.length() > 0) {
					viewHolder.deviceName.setText(deviceName);
					String TextValue = "蓝牙名称:" + device.getName();
					viewHolder.deviceLymc.setText(TextValue);
				} else {
					viewHolder.deviceName.setText(R.string.unknown_device);
				}
			}
			viewHolder.deviceAddress.setText(deviceAddress);

			return view;
		}
	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mLeDeviceListAdapter.addDevice(device);
					mHandler.sendEmptyMessage(XtAppConstant.WHAT_BLUETOOTHSCAN_THREAD);
				}
			});
		}
	};

	static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
		TextView deviceLymc;
	}

	// Hander
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case XtAppConstant.WHAT_BLUETOOTHSCAN_THREAD: // Notify change
				mLeDeviceListAdapter.notifyDataSetChanged();
				break;
			}
		}
	};

	@Override
	public boolean onLongClick(View v) {
		//validatePassword();
		return true;
	}

	/**
	 * 密码验证后，跳转到 机器ID 获取界面
	 */
	private void validatePassword() {
		if (!"".equals(mSpUtil.getYhxm()) && !"".equals(mSpUtil.getYhmm())) {
			final EditText etPass = new EditText(BluetoothScanActivity.this);
			etPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			etPass.setGravity(Gravity.CENTER);
			new AlertDialog.Builder(this).setMessage("在获取机器ID前，请先输入密码").setTitle("提示信息").setView(etPass)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String content = etPass.getText().toString().trim();
							if (CommonUtil.isEmpty(content)) {
								ToastMsg(BluetoothScanActivity.this, "密码不能为空");
								return;
							}
							// String s = Md5Util.crypt(content); // md5加密
							if (!(content.equals("654321"))) {
								ToastMsg(BluetoothScanActivity.this, "输入的密码不正确");
							} else {
								dialog.dismiss();
								//startActivity(new Intent(BluetoothScanActivity.this, CreateZsmcActivity.class));
							}
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).create().show();
		} else {
			ToastMsg(BluetoothScanActivity.this, "温馨提示：使用新建猪舍功能前，请先登录账户");
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_login:
			// 首先检测手机是否处于有网络状态
			if (!AndroidUtil.isConn(BluetoothScanActivity.this)) {
				AndroidUtil.setNetworkMethod(BluetoothScanActivity.this);
			} else {
				if (CommonUtil.isEmpty(mSpUtil.getYhmm())) {
					// 账户登录
					showLoginDialog();
				}
			}
			break;

		case R.id.btn_logout:
			// 账户注销
			showLogoutDialog();
			break;

		case R.id.btn_upload:
			// 首先检测手机是否处于有网络状态
			if (!AndroidUtil.isConn(BluetoothScanActivity.this)) {
				AndroidUtil.setNetworkMethod(BluetoothScanActivity.this);
			} else {
				try {
					// 在进行同步时，检测是否已经登陆了账户
					if (!mSpUtil.getYhxm().equals("")  && !mSpUtil.getYhmm().equals("") ) {
						uploadBtData();
					} else {
						ToastMsg(BluetoothScanActivity.this, "温馨提示：在数据同步前，首先登录账户！");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}

	/**
	 * 显示登录的dialog对话框
	 * 
	 *            根布局
	 *            组件 用户名
	 *            组件 密码
	 */
	public void showLoginDialog() {
		final LinearLayout ll = new LinearLayout(BluetoothScanActivity.this);
		final EditText etUsername = new EditText(BluetoothScanActivity.this);
		final EditText etPassword = new EditText(BluetoothScanActivity.this);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.addView(etUsername);
		ll.addView(etPassword);
		etUsername.setInputType(InputType.TYPE_CLASS_TEXT);
		etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		etUsername.setGravity(Gravity.CENTER);
		etPassword.setGravity(Gravity.CENTER);
		etUsername.setHint("用户名");
		etPassword.setHint("密码");
		etUsername.setHeight(200);
		etPassword.setHeight(200);
		etUsername.setText(mSpUtil.getYhxm());
		// etPassword.setText("huimu");
		new AlertDialog.Builder(this).setTitle("登录验证").setView(ll)
				.setPositiveButton("登录", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 首先检测手机是否处于有网络状态
						if (!AndroidUtil.isConn(BluetoothScanActivity.this)) {
							AndroidUtil.setNetworkMethod(BluetoothScanActivity.this);
						} else {
							username = etUsername.getText().toString().trim();
							String pass = etPassword.getText().toString().trim();
							if (CommonUtil.isEmpty(username) || CommonUtil.isEmpty(pass)) {
								ToastMsg(BluetoothScanActivity.this, "用户名或密码不能为空");
								return;
							}
							password = Md5Util.crypt(pass); // md5加密
							UserBean userBean = new UserBean();
							userBean.setYhxm(username);
							userBean.setYhmm(password); // 发送加密后的密码
							String param = gson.toJson(userBean);
							showLoading("正在验证用户");
							isLoginFlag = true; // WHAT_MCXX_SELECT 得到的是 登录
												// 操作的返回数据
							BluetoothScanActivitySupport.getMcxxDataThread(param, BluetoothScanActivity.this, handler);
							startTime(5000, "登陆超时");
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
	 * 显示用户注销的dialog对话框
	 */
	private void showLogoutDialog() {
		final LinearLayout ll = new LinearLayout(BluetoothScanActivity.this);
		final TextView tvUsername = new TextView(BluetoothScanActivity.this);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.addView(tvUsername);
		tvUsername.setInputType(InputType.TYPE_CLASS_TEXT);
		tvUsername.setGravity(Gravity.CENTER);
		tvUsername.setTextSize(20);
		tvUsername.setHeight(200);
		String textValue = "当前账户：" + mSpUtil.getYhxm();
		tvUsername.setText(textValue); // 从 sp 中获取账户名，回显界面上
		new AlertDialog.Builder(this).setTitle("注销当前用户？").setView(ll)
				.setPositiveButton("注销", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//mSpUtil.setYhxm(""); // 用户名置空
						mSpUtil.setYhmm(""); // 密码置空
						dialog.dismiss();
						ToastMsg(BluetoothScanActivity.this, "账户注销成功");
						// 注销成功后，显示登录，隐藏注销
						btnLogin.setVisibility(View.VISIBLE);
						btnLogout.setVisibility(View.GONE);
						btnUpload.setVisibility(View.GONE);
						// btnUpload.setEnabled(false);
						// btnUpload.setBackgroundColor(getResources().getColor(R.color.gray_light));
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}

	/**
	 * 上传蓝牙数据到平台
	 * 
	 * @throws JSONException
	 */
	private void uploadBtData() throws JSONException {
		daBtListFromDB = BluetoothScanActivitySupport.getAllBt(BluetoothScanActivity.this);
		List<Da_bt> daBtList = new ArrayList<>();// 用于保存数据标志位 为 2 的数据
		for (Da_bt daBt : daBtListFromDB) {
			if ("2".equals(daBt.getSjbz())) {
				daBtList.add(daBt); // 数据标志位 为 2 ，上传到平台的蓝牙数据列表
			}
		}
		// 得到牧场表的所有不同的牧场id
		List<Da_mc> daMcList = BluetoothScanActivitySupport.getAllMc(BluetoothScanActivity.this);
		List<Integer> mcidList = new ArrayList<>();
		for (Da_mc daMc : daMcList) {
			mcidList.add(Integer.valueOf(daMc.getMcid()));
		}
		// 取出 mcidList中不重复数据保存在 set中
		HashSet<Integer> set = new HashSet<>(mcidList);
		TreeSet<Integer> treeSet = new TreeSet<>(set);
		StringBuilder sb = new StringBuilder();
		for (Integer mcid : treeSet) {
			sb.append(mcid).append("#"); // mcid 通过 # 连接
		}
		
		// ① 有蓝牙别名被修改，上传别名被修改的那些数据
		if (daBtList.size() > 0) {
			String mcidStr = sb.substring(0, sb.length() - 1);
			// 拼装 json
			String json = "";
			json += "{\"mcid\":\"" + mcidStr + "\",\"lyxx\":";
			Gson gson = new GsonBuilder().create();
			String param = gson.toJson(daBtList);
			json = json + param + "}";
			showLoading("正在上传蓝牙数据");
			BluetoothScanActivitySupport.uploadLybmDataThread(json, BluetoothScanActivity.this, handler);
			startTime(5000, "上传蓝牙数据");
			isTimeOut = true;
		} else {
			// ② 没有别名被修改，通过上传 账户名与密码 获取新的账户信息
			String username = mSpUtil.getYhxm(); // sp 中保存的用户名
			String password = mSpUtil.getYhmm(); // sp 中保存的密码（已经md5加密）
			UserBean userBean = new UserBean();
			userBean.setYhxm(username);
			userBean.setYhmm(password); // 发送加密后的密码
			String param = gson.toJson(userBean);
			showLoading("正在获取最新数据");
			isLoginFlag = false; // WHAT_MCXX_SELECT 得到的返回数据是 同步操作，而不是登录操作
			BluetoothScanActivitySupport.getMcxxDataThread(param, BluetoothScanActivity.this, handler);
			startTime(5000, "获取最新数据");
			isTimeOut = true;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case XtAppConstant.WHAT_MCXX_SELECT:
			if (ResultCode.UNCONNECTION_SERVICE_ERROR.getValue() == msg.arg1) {
				// 连接服务器失败
				break;
			}
			// 解析平台下发的牧场信息，保存到数据库
			try {
				isTimeOut = false;
				if (ResultCode.SYSTEM_ERROR.getValue() == msg.arg1) {
					// 登录失败
					dismissLoading();
					ToastMsg(BluetoothScanActivity.this, "温馨提示：账户或密码错误，请重新登录！");
				}

				if (ResultCode.SUCCESS.getValue() == msg.arg1) {
					String data = msg.obj.toString();
					boolean loginResult = BluetoothScanActivitySupport.doMcxxDataParsing(data,
							BluetoothScanActivity.this);
					// 登录成功
					if (!loginResult) {
						ToastMsg(BluetoothScanActivity.this, "温馨提示：该账户下没有牧场信息！");
					} else {
						if (isLoginFlag) { // 登录操作的返回数据
							ToastMsg(BluetoothScanActivity.this, "账户登录成功");
							// 登录成功后，将用户名与密码保存到 sp 中
							mSpUtil.setYhxm(username);
							mSpUtil.setYhmm(password); // MD5 加密后
							// 登陆成功后，隐藏登录，显示注销
							btnLogin.setVisibility(View.GONE);
							btnLogout.setVisibility(View.VISIBLE);
							// 注销按钮回显当前用户名
							btnLogout.setText(mSpUtil.getYhxm());
							btnUpload.setVisibility(View.VISIBLE);
							// btnUpload.setEnabled(true);
							// btnUpload.setBackground(getResources().getDrawable(R.drawable.mybutton));
						} else {
							ToastMsg(BluetoothScanActivity.this, "数据同步完成");
						}
					}
					dismissLoading();
				}

			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			break;

		case XtAppConstant.WHAT_LYBM_UPLOAD:
			if (ResultCode.UNCONNECTION_SERVICE_ERROR.getValue() == msg.arg1) {
				// 连接服务器失败
				break;
			}
			try {
				isTimeOut = false;
				// 解析平台下发的蓝牙列表数据
				String data = msg.obj.toString();
				daBtList = BluetoothScanActivitySupport.doLybmDataParsing(data, BluetoothScanActivity.this);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// 首先清空数据库中蓝牙数据列表
			BluetoothScanActivitySupport.deleteAllBt(BluetoothScanActivity.this);
			// 再保存 列表daBtList 到数据库中
			for (Da_bt bt : daBtList) {
				BluetoothScanActivitySupport.saveBt2DB(BluetoothScanActivity.this, bt);
			}
			dismissLoading();
			ToastMsg(BluetoothScanActivity.this, "蓝牙别名信息同步完成");
			break;
		}
		return false;
	}

	/**
	 * 连接确定
	 */
	public void connectDevDialog (final BluetoothDevice device) {
		toIndexActivity(device);

	/*	new AlertDialog.Builder(this).setMessage("你还未登陆账户,是否继续连接蓝牙?").setTitle("连接确认?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						toIndexActivity(device);
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();*/
	}
	/**
	 * 是否登陆
	 * @return 返回true or false
	 */
	private boolean hasLogined () {
		return CommonUtil.isNotEmpty(mSpUtil.getYhxm()) 
				&& CommonUtil.isNotEmpty(mSpUtil.getYhmm());
	}
	
	@Override
	public void onBackPressed() {
		YzzsApplication.exit();
		super.onBackPressed();
	}

	@Override
	public void onConnected(boolean isConnected) {
		if (!isConnected) {
			YzzsApplication.isConnected = false;
			dismissLoading();
			ToastMsg(this, getString(R.string.disconnected));
		} else {
			ToastMsg(this, getString(R.string.connected));
			YzzsApplication.isConnected = true;
		}
	}

	@Override
	public void onReady(boolean isReady) {

	}

	@Override
	public void onMessage(byte[] message) {

	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		if (!isNetConnected) {
			dismissLoading();
			ToastMsg(this, getString(R.string.net_disconnected));
		}
	}

	@Override
	public void Timeout(String context) {
		if (!isTimeOut) {
			// 5 秒钟有收到数据，则停止计时
			isTimeOut = false; // 标志位重新置false
		} else {
			dismissLoading();
			ToastMsg(BluetoothScanActivity.this, context + "超时,请重试");
		}
	}
}