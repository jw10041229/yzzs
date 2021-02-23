package com.huimv.yzzs.fragment.qk;

import java.util.Arrays;

import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.BluetoothScanActivity;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.GeneralCMDConstant;
import com.huimv.yzzs.constant.HkCMDConstant;
import com.huimv.yzzs.constant.QkCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;

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

/**
 * 全控密码验证
 * 
 * @author jiangwei
 *通道绑定,端口数据整合成 一个SP 
 */
public class QkMmyzFragment extends YzzsBaseFragment implements EventHandler ,ConnectTimeoutListener ,OnClickListener{
	private Button loginBtn;
	private EditText mmsrEt;
	private SharePreferenceUtil mSpUtil;
	private ImageView iv_retore;
	private String identity = "2";// 区分身份
	public static int sbNumS = 0;
	public static int startTimeCnt = 0;//几个运行循环
	private boolean isTimeOut = true;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_qk_mmyz_fragment, null);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		initView(view);
		initData();
		return view;
	}

	private void initView(View view) {
		iv_retore = (ImageView) view.findViewById(R.id.iv_retore);
		iv_retore.setVisibility(View.VISIBLE);
		mmsrEt = (EditText) view.findViewById(R.id.mmsrEt);
		mmsrEt.setInputType(InputType.TYPE_CLASS_NUMBER); // 输入类型
		mmsrEt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) }); // 最大输入长度
		mmsrEt.setTransformationMethod(PasswordTransformationMethod.getInstance()); // 设置为密码输入框
		loginBtn = (Button) view.findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(this);
		iv_retore.setOnClickListener(this);
	}
	private void initData() {
		setConnectTimeoutListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_retore:
			restoreDialog();
			break;

		case R.id.loginBtn:
			if (!checkPasswordLenth()) {
				return;
			}
			if(YzzsApplication.isConnected) {
				isTimeOut = true;
				startTime(QkCMDConstant.LOGIN_TIME, "登录");
				sendLoginCMD();
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
/*		getSbpz(new byte[] {2,0,41,0,1,0,14,5,24,
					13,37,1,2,13,27,1,3,14,27,1,4,15,27,0,5,16,27,0,7,0,7,0,1,
					14,27,0,2,13,27,0,3,14,27,0,4,15,27,1,5,16,27,1,7,0,7,0,2,
					15,27,0,2,13,27,0,3,14,27,0,4,15,27,1,5,16,27,1,7,0,7,0,3,
					16,27,0,2,13,27,0,3,14,27,0,4,15,27,1,5,16,27,1,7,0,7,0,4,
					17,27,0,2,13,27,0,3,14,27,0,4,15,27,1,5,16,27,1,7,0,7,0,5,
					18,27,0,2,13,27,0,3,14,27,0,4,15,27,1,5,16,27,1,7,0,7,0,6,
					19,27,0,2,13,27,0,3,14,27,0,4,15,27,1,5,16,27,1,7,0,7,0,7,
					20,27,0,2,13,27,0,3,14,27,0,4,15,27,1,5,16,27,1,7,0,7,0,8,
					21,27,0,2,13,27,0,3,14,27,0,4,15,27,1,5,16,27,1,7,0,7,0,9,
					22,27,0,2,13,27,0,3,14,27,0,4,15,27,1,5,16,27,1,7,0,7,1,0,
					23,27,0,2,13,27,0,3,14,27,0,4,15,27,1,5,16,27,1,7,0,8,0,1,
					23,27,0,2,13,27,0,3,14,27,0,4,15,27,1,5,16,27,1,7,0,8,0,2,
					23,27,0,2,13,27,0,3,14,27,0,4,15,27,1,5,16,27,1,7,0,6,0,1,
					23,27,0,2,13,27,0,3,14,27,0,4,15,27,1,5,16,27,1,7,0,6,0,2
					});*/
			//sendGetSbpzCMD(QkCMDConstant.GET_SBCSPZ);
		
			break;
		}
		super.onClick(v);
	}
	/**
	 * 校验密码长度
	 * @return
	 */
	private boolean checkPasswordLenth () {
		if (mmsrEt.getText().toString().length() != 6) {
			ToastMsg(getActivity(), "密码长度不足6位");
			return false;
		}
		return true;
	}
	/**
	 * 发送密码验证命令
	 */
	private void sendLoginCMD() {
		if (mmsrEt.getText().toString().length() != 6) {
			ToastMsg(getActivity(), "密码长度不足6位");
			return;
		}
		showLoading("正在验证身份...");
		int lenthMmsrEt = mmsrEt.getText().toString().length();
		int lenth = lenthMmsrEt + 12;
		int cmd = QkCMDConstant.SET_MMYZ;
		byte [] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);//h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);//m
		byteData[2] = 2;//设备区分标志
		byteData[3] = YzzsCommonUtil.intTobyte(cmd / 256);//命令高位
		byteData[4] = YzzsCommonUtil.intTobyte(cmd % 256);//命令低位
		byteData[5] = YzzsCommonUtil.intTobyte(lenth / 256);//长度低位
		byteData[6] = YzzsCommonUtil.intTobyte(lenth % 256);//长度高位
		byteData[7] = YzzsCommonUtil.intTobyte(lenthMmsrEt);//后面值得长度
		for (int i = 8; i < 14; i++) {
			byteData[i] = YzzsCommonUtil
					.intTobyte(Integer.parseInt(mmsrEt.getText().toString().substring(i - 8, i - 7)));
		}
		byteData[14] = 1;//校验位
		byteData[15] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);//e
		byteData[16] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//n
		byteData[17] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
		sendUnpackData(byteData,XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}
	
	/**
	 * 发送获取设备配置命令
	 * @param cmd
	 */
	private void sendGetSbpzCMD (final int cmd) {
		showLoading("正在获取配置");
		int lenth = 12;
		byte [] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);//h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);//m
		byteData[2] = 2;//设备区分标志
		byteData[3] = YzzsCommonUtil.intTobyte(cmd / 256);//命令高位
		byteData[4] = YzzsCommonUtil.intTobyte(cmd % 256);//命令低位
		byteData[5] = YzzsCommonUtil.intTobyte(lenth / 256);//长度低位
		byteData[6] = YzzsCommonUtil.intTobyte(lenth % 256);//长度高位
		byteData[7] = 0;//后面值得长度
		byteData[8] = 1;//校验位
		byteData[9] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);//e
		byteData[10] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//n
		byteData[11] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
		sendUnpackData(byteData,XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}
	/**
	 * 获取设备配置Ex
	 * @param message
	 * 
	 */
	private void getSbpz (byte[] message) {
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		if (cmd == QkCMDConstant.GET_SBCSPZ) {// 得到设备配置
			if (message [5] != 0) {//数据接收成功
				return;
			}
			StringBuilder sb = new StringBuilder();
			sbNumS =  message[6];//几个设备
			startTimeCnt = message[7];//几个运行时间段
			int singleCycleLength = message[8];//单个循环长度
			byte [] data = Arrays.copyOfRange(message, 9, message.length);
			for (int i = 0; i < sbNumS; i++) {
				for (int j = i * singleCycleLength ; j < i * singleCycleLength + startTimeCnt * 4; j++) {
					sb.append(YzzsCommonUtil.formatStringAdd0(YzzsCommonUtil.ChangeByteToPositiveNumber(data[j]) + "", 3, 1));
				}//运行时间
				
				for (int j = i * singleCycleLength + startTimeCnt * 4; j < i * singleCycleLength + startTimeCnt * 4 + 4; j++) {
					sb.append(data[j]);
				}//设备类型，设备顺序
				sb.append(XtAppConstant.SEPARSTOR);
			}
			mSpUtil.setQkyxcs(sb.toString());
			isTimeOut = false;
			dismissLoading();
			toQkYxcsContainerFragment();
		}
	}
	
	/**
	 * 跳转到yxcs页面
	 */
	private void toQkYxcsContainerFragment() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		QkYxcsContainerFragment mHk_yxcsFragment = new QkYxcsContainerFragment();
		Bundle bundle = new Bundle();
		bundle.putString("identity", identity);
		mHk_yxcsFragment.setArguments(bundle);
		ft.replace(R.id.fragment_container, mHk_yxcsFragment, "qk_yxcsFragment");
		ft.addToBackStack(null);
		ft.commit();
	}
	
	/**
	 * 发送恢复出厂设置命令
	 * @param initCMD
	 */
	private void sendRestoreCMD (int initCMD) {
		showLoading("正在恢复出厂设置");
		int lenth = 12;
		final byte [] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);//h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);//m
		byteData[2] = YzzsCommonUtil.intTobyte(QkCMDConstant.QK_SBBZ);//设备区分标志全控
		byteData[3] = YzzsCommonUtil.intTobyte(initCMD / 256);//命令高位
		byteData[4] = YzzsCommonUtil.intTobyte(initCMD % 256);//命令低位
		byteData[5] = YzzsCommonUtil.intTobyte(lenth / 256);//命令低位
		byteData[6] = YzzsCommonUtil.intTobyte(lenth % 256);//命令高位
		byteData[7] = 0;//后面值得长度
		byteData[8] = 1;//校验位
		byteData[9] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);// e
		byteData[10] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//n
		byteData[11] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
		sendBaseCMD(byteData);
	}
	
	/**
	 * 恢复出厂设置对话框
	 */
	public void restoreDialog () {
		new AlertDialog.Builder(getActivity()).setMessage("恢复出厂设置之后需要手动重启设备才能生效,是否恢复出厂设置?").setTitle("恢复出厂设置提示")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if(YzzsApplication.isConnected) {
							isTimeOut = true;
							startTime(HkCMDConstant.LOGIN_TIME, "恢复出厂设置");
							sendRestoreCMD(GeneralCMDConstant.RESTORE_FACTORY_SET);
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
			ToastMsg(getActivity(), getString(R.string.disconnected));
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
		getJoiningPack(message);
	}
	/**
	 * 拼包
	 * @param message
	 */
	private void getJoiningPack (byte[] message) {
		if(message[0] == QkCMDConstant.QK_SBBZ) {//环控
			try {
				getMmyzResult(message);
				getSbpz(message);
				restoreFactorySet(message);
			} catch (Exception e) {
				dismissLoading();
				ToastMsg(getActivity(), "QkMmyzFragment getJoiningPack数据异常");
			}
		}
	}
	/**
	 * 密码验证
	 * @param message
	 */
	private void getMmyzResult(byte[] message) {
		//104 109 1 0 5 0x  0(是否成功) 0(密码验证登陆成功) 1(身份) 12345678 1(校验位) 101 110 100
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		if (cmd == QkCMDConstant.SET_MMYZ) {
			if (message [5] !=0) {//数据接收成功
				return;
			}
			if (message.length > 6) {
				message = Arrays.copyOfRange(message, 6, message.length);//截取有效数据
				//开始解析
				if (message [0] == 0 && message.length >= 10) {//登陆成功,有机器id
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
					sendGetSbpzCMD(QkCMDConstant.GET_SBCSPZ);
				} else {
					isTimeOut = false;
					dismissLoading();
					ToastMsg(getActivity(), "密码错误");
					// 密码错误时，直接关闭 3 秒登录超时的定时器
				}
			}
		}
	}
	/**
	 * 恢复出厂设置
	 * 
	 * @param message
	 */
	private void restoreFactorySet(byte[] message) {
		if (message[0] == 1) {
			int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
			if (cmd == GeneralCMDConstant.RESTORE_FACTORY_SET) {
				if (message[6] == 0) {// 恢复出厂设置成功
					isTimeOut = false;
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
			ToastMsg(getActivity(), content + getString(R.string.connect_time_out));
		}
	}
}