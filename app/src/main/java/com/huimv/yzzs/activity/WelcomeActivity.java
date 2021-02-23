package com.huimv.yzzs.activity;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.annotation.NonNull;
import android.widget.TextView;


import com.google.gson.Gson;
import com.huimv.android.basic.base.BaseActivity;
import com.huimv.yzzs.R;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.constant.XtAppConstant;

import com.huimv.yzzs.webservice.WsCmd;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class WelcomeActivity extends BaseActivity implements Callback, EasyPermissions.PermissionCallbacks  {
	public static final String TAG = WelcomeActivity.class.getSimpleName();
	private Gson gson = new Gson();
	private TextView tv_version_info;
	private static final int RC_BLUETOOTH = 10000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		YzzsApplication.addActivity(this);
		initView();
		initData();
		handler = new Handler();
		requestPermissions();
	}
	/**
	 * 去申请权限
	 */
	private void requestPermissions() {
		String[] perms = {Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION};

		//判断有没有权限
		if (EasyPermissions.hasPermissions(this, perms)) {
			// 如果有权限了, 就做你该做的事情
			handler.postDelayed(startAct, 10);
		} else {
			// 如果没有权限, 就去申请权限
			// this: 上下文
			// Dialog显示的正文
			// RC_CAMERA_AND_RECORD_AUDIO 请求码, 用于回调的时候判断是哪次申请
			// perms 就是你要申请的权限
			EasyPermissions.requestPermissions(this, "请授予蓝牙权限，否则无法使用", RC_BLUETOOTH, perms);
		}
	}
	private void initData() {
		tv_version_info.setText(YzzsApplication.getVersionName(this));
/*		KenBurnsView mKenBurns = (KenBurnsView) findViewById(R.id.ken_burns_images);
		Glide.with(this)
				.load(R.drawable.splash)
				.into(mKenBurns);*/
	}

	private void initView() {
		tv_version_info = (TextView) findViewById(R.id.tv_version_info);
	}

	private Handler handler;

	Runnable startAct = new Runnable() {

		@Override
		public void run() {
			startActivity(new Intent(WelcomeActivity.this, BluetoothScanActivity.class));
			finish();
		}
	};
	@Override
	protected void onResume() {


/*		if (!AndroidUtil.isConn(this)) {
			//AndroidUtil.setNetworkMethod(this);
			handler = new Handler();
			handler.postDelayed(startAct, 2000);
		} else {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			VersionBean version = new VersionBean();
			version.setBbh(YzzsApplication.getVersionName(this));
			String param = gson.toJson(version);
			WelcomeActivitySupport.VisionCheckThread(param, this, new Handler(
					this));
		}*/
		super.onResume();
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case XtAppConstant.WHAT_CHECK_VISION:
			if (msg.arg1 == WsCmd.AD_BBXX_GX) {// 有更新
/*				WelcomeActivitySupport.doVisionDataParsing(msg.obj.toString(),
						WelcomeActivity.this);*/
			}
			if (msg.arg1 == WsCmd.AD_BBXX_WGX) {// 无有更新之后验证登陆
				//startActivity(new Intent(WelcomeActivity.this, BluetoothScanActivity.class));
				finish();
			}
			if (msg.arg1 == WsCmd.SYSTEM_ERROR) {
				ToastMsg(WelcomeActivity.this, "服务器异常");
			}// 参数错误
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
		handler.postDelayed(startAct, 10);
	}

	@Override
	public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
		finish();
	}
}
