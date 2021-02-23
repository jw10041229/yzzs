package com.huimv.yzzs.fragment.cdz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.BluetoothScanActivity;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.CdzCMDConstant;
import com.huimv.yzzs.constant.GeneralCMDConstant;
import com.huimv.yzzs.constant.HkCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;

import java.util.Arrays;

import static com.huimv.yzzs.activity.IndexActivity.CDZ_SBTS_FRAGMENT;

/**
 * 测定站密码验证
 * @author jiangwei
 */
public class CdzMmyzFragment extends YzzsBaseFragment implements OnClickListener,EventHandler,ConnectTimeoutListener{
	private final static String TAG = CdzMmyzFragment.class.getSimpleName();
	private SharePreferenceUtil mSpUtil;
	/**
	 * 登陆按钮
	 */
	private Button btn_login;
	/**
	 * 密码输入
	 */
	private EditText et_password_input;
	/**
	 * 恢复出厂设置
	 */
	private ImageView iv_retore;
	private boolean isTimeOut = true;
	/**
	 * 区分身份
	 */
	private String identity = "2";
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_flz_mmyz_fragment, rootView,false);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		initView(view);
		initData();
		return view;
	}
	
	private void initData() {
		setConnectTimeoutListener(this);
		String password = mSpUtil.getCdzPassword();
		if (CommonUtil.isNotEmpty(password)) {
			et_password_input.setText(password);
		}
	}

	private void initView(View view) {
		btn_login = (Button) view.findViewById(R.id.btn_login);
		et_password_input = (EditText) view.findViewById(R.id.et_password_input);
		et_password_input.setInputType(InputType.TYPE_CLASS_NUMBER); //输入类型  
		et_password_input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)}); //最大输入长度  
		et_password_input.setTransformationMethod(PasswordTransformationMethod.getInstance()); //设置为密码输入框
		iv_retore = (ImageView) view.findViewById(R.id.iv_retore);
		btn_login.setOnClickListener(this);
		iv_retore.setOnClickListener(this);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			doLogin();
			break;
		case R.id.iv_retore:
			restoreDialog();
			break;
		default:
			break;
		}
		super.onClick(v);
	}
	/**
	 * 登陆
	 */
	private void doLogin() {
		if (!checkPasswordLenth()) {
			return;
		}
		if(YzzsApplication.isConnected) {
			isTimeOut = true;
			sendLoginCMD();
			startTime(HkCMDConstant.LOGIN_TIME, "登录");
		} else {
			ToastMsg(getActivity(), getString(R.string.bluetooth_disconnected));
		}
	}

	/**
	 * 跳转到设备调试
	 */
	private void toSbtsFragment() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		CdzSbtsFragment mCdzFragment= new CdzSbtsFragment();
		Bundle bundle = new Bundle();
		bundle.putString("identity", identity);
		mCdzFragment.setArguments(bundle);
		ft.replace(R.id.fragment_container, mCdzFragment,CDZ_SBTS_FRAGMENT);
		ft.addToBackStack(null);
		ft.commit();
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
	public void onNetChange(boolean isNetConnected) {
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
		if(message[0] == CdzCMDConstant.CDZ_SBBZ) {//测定站
			try {
				int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
				switch (cmd) {
				case CdzCMDConstant.CDZ_MMYZ://密码验证
					mmyzDataParsing(message);
					break;
				case GeneralCMDConstant.RESTORE_FACTORY_SET://恢复出厂设置
					hfccszDataParsing(message);
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
	 * 恢复出厂设置
	 * @param message
	 */
	private void hfccszDataParsing(byte[] message) {
		if (message[5] == 0 && message[6] == 0) {// 恢复出厂设置成功
			dismissLoading();
			ToastMsg(getActivity(), "恢复出厂设置成功!");
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					final Intent intent = new Intent(getActivity(), BluetoothScanActivity.class);
					startActivity(intent);
					getActivity().finish();
				}
			}, 2000);
		} else { // 恢复出厂设置失败
			ToastMsg(getActivity(), "恢复出厂设置失败,请重试!");
		}
	}
	/**
	 * 密码验证
	 * @param message
	 */
	private void mmyzDataParsing(byte[] message) {
		if (message [5] !=0) {//数据接收成功
			dismissLoading();
			ToastMsg(getActivity(), "数据接收异常");
			return;
		}
		if (message.length >= 7) {
			isTimeOut = false;
			if (message [6] == 0 && message.length >= 16) {//密码验证成功
				message = Arrays.copyOfRange(message, 6, message.length);//截取有效数据
				if (message[1] == 1) {
					identity = GeneralCMDConstant.IDENTITY_ORDINARY;// 普通用户
				}
				if (message[1] == 2) {
					identity = GeneralCMDConstant.IDENTITY_DEBUG;// 调试
				}
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < 8; i++) {
					sb.append(message[2 + i]);
				}
				String jqid = sb.toString();
				mSpUtil.setJqid(jqid);
				mSpUtil.setCdzPassword(et_password_input.getText().toString().trim());
				dismissLoading();
				toSbtsFragment();
			} else {
				dismissLoading();
				ToastMsg(getActivity(), "密码错误");
				isTimeOut = false;
			}
		} 
	}
	/**
	 * 验证密码是否合法
	 * @return
	 */
	private boolean checkPasswordLenth() {
		if (et_password_input.getText().toString().length() != 6) {
			ToastMsg(getActivity(), "密码长度不足6位");
			return false;
		}
		return true;
	}
	
	/**
	 * 发送登陆命令
	 */
	private void sendLoginCMD() {
		showLoading("正在验证身份...");
		int password_lenth = et_password_input.getText().toString().length();
		int lenth = password_lenth + 12;
		int cmd = CdzCMDConstant.CDZ_MMYZ;
		byte [] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);//h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);//m
		byteData[2] = YzzsCommonUtil.intTobyte(CdzCMDConstant.CDZ_SBBZ);//设备区分标志
		byteData[3] = YzzsCommonUtil.intTobyte(cmd / 256);//命令高位
		byteData[4] = YzzsCommonUtil.intTobyte(cmd % 256);//命令低位
		byteData[5] = YzzsCommonUtil.intTobyte(lenth / 256);//长度低位
		byteData[6] = YzzsCommonUtil.intTobyte(lenth % 256);//长度高位
		byteData[7] = YzzsCommonUtil.intTobyte(password_lenth);//后面值得长度
		for (int i = 8; i < 14; i++) {
			byteData[i] = YzzsCommonUtil
					.intTobyte(Integer.parseInt(et_password_input.getText().toString().substring(i - 8, i - 7)));
		}
		byteData[14] = 1;//校验位
		byteData[15] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);//e
		byteData[16] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//n
		byteData[17] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
		sendUnpackData(byteData,XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}
	
	/**
	 * 恢复出厂设置对话框
	 */
	private void restoreDialog () {
		new AlertDialog.Builder(getActivity()).setMessage("恢复出厂设置之后需要手动重启设备才能生效,是否恢复出厂设置?").setTitle("恢复出厂设置提示")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if(YzzsApplication.isConnected) {
							isTimeOut = true;
							startTime(HkCMDConstant.LOGIN_TIME, "恢复出厂设置");
							showLoading("正在恢复出厂设置");
							sendGetBaseCMD(CdzCMDConstant.CDZ_SBBZ, GeneralCMDConstant.RESTORE_FACTORY_SET);
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
