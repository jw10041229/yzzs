package com.huimv.yzzs.fragment.hk;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.IndexActivity;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.GeneralCMDConstant;
import com.huimv.yzzs.constant.HkCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;

import java.util.Arrays;

/**
 * 环控密码验证
 * 
 * @author jiangwei
 *通道绑定,端口数据整合成 一个SP 
 */
public class HkMmyzFragment extends YzzsBaseFragment implements EventHandler ,ConnectTimeoutListener,OnClickListener{
	private Button loginBtn;
	private EditText et_password;
	private SharePreferenceUtil mSpUtil;
	private StringBuffer sb = new StringBuffer();
	private StringBuffer sbDw = new StringBuffer();
	private String identity = "";// 区分身份
	private String MAX = HkCMDConstant.MAX;
	private String MIN = HkCMDConstant.MIN;
	private String DW = HkCMDConstant.DW;
	public int sbNumS = 0;
	private boolean isTimeOut = true;
	@SuppressLint("InflateParams")
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_hk_mmyz_fragment, null);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		initView(view);
		initData();
		return view;
	}

	private void initData() {
		setConnectTimeoutListener(this);
/*		String password = mSpUtil.getHkPassword();
		if (CommonUtil.isNotEmpty(password)) {
			et_password.setText(password);
			if (!checkPasswordLenth()) {
				return;
			}
			if(YzzsApplication.isConnected) {
				isTimeOut = true;
				startTime(HkCMDConstant.LOGIN_TIME, "登录");
				sendLoginCMDEx();
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
		}*/
	}

	private void initView(View view) {
		et_password = (EditText) view.findViewById(R.id.mmsrEt);
		et_password.setInputType(InputType.TYPE_CLASS_NUMBER); // 输入类型
		et_password.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) }); // 最大输入长度
		et_password.setTransformationMethod(PasswordTransformationMethod.getInstance()); // 设置为密码输入框
		loginBtn = (Button) view.findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginBtn:
			if (!checkPasswordLenth()) {
				return;
			}
			if(YzzsApplication.isConnected) {
				isTimeOut = true;
				startTime(HkCMDConstant.LOGIN_TIME, "登录");
				sendLoginCMDEx();
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		}
		super.onClick(v);
	}
	/**
	 * 校验密码长度
	 * @return
	 */
	private boolean checkPasswordLenth () {
		if (et_password.getText().toString().length() != 6) {
			ToastMsg(getActivity(), "密码长度不足6位");
			return false;
		}
		return true;
	}
	/**
	 * 发送密码验证命令
	 */
	private void sendLoginCMDEx() {
		showLoading("正在验证身份...");
		int lenthMmsrEt = et_password.getText().toString().length();
		int lenth = lenthMmsrEt + 12;
		int cmd = HkCMDConstant.SET_MMYZ;
		byte [] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);//h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);//m
		byteData[2] = 1;//设备区分标志
		byteData[3] = YzzsCommonUtil.intTobyte(cmd / 256);//命令高位
		byteData[4] = YzzsCommonUtil.intTobyte(cmd % 256);//命令低位
		byteData[5] = YzzsCommonUtil.intTobyte(lenth / 256);//长度低位
		byteData[6] = YzzsCommonUtil.intTobyte(lenth % 256);//长度高位
		byteData[7] = YzzsCommonUtil.intTobyte(lenthMmsrEt);//后面值得长度
		for (int i = 8; i < 14; i++) {
			byteData[i] = YzzsCommonUtil
					.intTobyte(Integer.parseInt(et_password.getText().toString().substring(i - 8, i - 7)));
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
	private void sendGetSbpzCMDEx (final int cmd) {
		showLoading("正在获取配置");
		int lenth = 12;
		byte [] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);//h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);//m
		byteData[2] = 1;//设备区分标志
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
	private void getSbpzEx (byte[] message) {
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		if (cmd == HkCMDConstant.GET_SBPZ) {// 得到设备配置
			if (message [5] != 0) {//数据接收成功
				return;
			}
			//104 109
			// 1 0 7(命令) 0 x(长度) 0 3  00 01  00 02 00 02 00 02 00 02（五档的档位温度） 12(单个循环体长度)  00 01 00 02 sn lx(两个字节) sx(两个字节) bppge (变频百分比，3个字节，99% 为 099)
            int lenth = message[27];//每个循坏体的长度 9
			YzzsApplication.hasBpfj(lenth,mSpUtil);
			int sbNum = message[6];//几个设备
			sbNumS = sbNum;//有几个设备
			HkSshjxsFragment.sbNumS = sbNumS;
			int dwNum = 5;//一共几个档位
			//03 01 05 01  00 01 00 02  00 03 00 04 sn lx sx 去除包之后的长度
			byte [] data = Arrays.copyOfRange(message, dwNum * 4 + 8, message.length);
			byte[] dwWdData = Arrays.copyOfRange(message, 7 , dwNum * 4 + 7);//档位温度
			for (int i = 1; i <= dwNum; i ++) {//循环档位
				for (int j = 0; j < sbNum; j++) {//设备循环
					//第几档的第几个设备
					byte[] sbData = Arrays.copyOfRange(data, (i - 1 ) * sbNum*lenth + j*lenth , (i - 1) * sbNum*lenth + (j +1) *lenth);
					
					int dqdkK1 = YzzsCommonUtil.ChangeByteToPositiveNumber(sbData[0]);// 当前端口开1时间
					int dqdkK2 = YzzsCommonUtil.ChangeByteToPositiveNumber(sbData[1]);// 当前端口开2时间
					int dqdkG1 = YzzsCommonUtil.ChangeByteToPositiveNumber(sbData[2]);// 当前端口关1时间
					int dqdkG2 = YzzsCommonUtil.ChangeByteToPositiveNumber(sbData[3]);// 当前端口关2时间
					int dwSn = sbData[4];
					
					String dqdkK = String.valueOf(dqdkK1 * 256 + dqdkK2);// 当前端口开时间
					String dqdkG = String.valueOf(dqdkG1 * 256 + dqdkG2);// 当前端口开时间
					String  sblx = sbData[5] + "" + sbData[6] ;//设备类型
					String sbxh = sbData[7] + "" + sbData[8] ;//设备序号
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
				
				int wdlow1 = YzzsCommonUtil.ChangeByteToPositiveNumber(dwWdData[(i -1) * 4 + 0]) ;// 档位温度下限1
				int wdlow2 = YzzsCommonUtil.ChangeByteToPositiveNumber(dwWdData[(i -1) * 4 + 1]);// 档位温度下限2
				int wdtop1 = YzzsCommonUtil.ChangeByteToPositiveNumber(dwWdData[(i -1) * 4 + 2]);// 档位温度上限1
				int wdtop2 = YzzsCommonUtil.ChangeByteToPositiveNumber(dwWdData[(i -1) * 4 + 3]);// 档位温度上限2
				
				String wdlow = String.valueOf(wdlow1 * 256 + wdlow2);// 档位温度下限
				String wdtop = String.valueOf(wdtop1 * 256 + wdtop2);// 档位温度上限
				mSpUtil.setDwWd(DW + i + MIN, wdlow);//档位温度下限
				mSpUtil.setDwWd(DW + i + MAX, wdtop);//档位温度上限
				sbDw.append(sb).append(XtAppConstant.SEPARSTOR);
				sb.delete(0, sb.length());
			}
			//循环结束
			mSpUtil.setDwSave("5");
			mSpUtil.setDwdk(sbDw.toString());
			sbDw.delete(0, sbDw.length());
			isTimeOut = false;
			//跳转到yxcs页面
			dismissLoading();
			toYxcsContainerFragment();
		}
	}


	/**
	 * 跳转到yxcs页面
	 */
	private void toYxcsContainerFragment() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		HkYxcsContainerFragment mHk_yxcsFragment = new HkYxcsContainerFragment();
		Bundle bundle = new Bundle();
		bundle.putString("identity", identity);
		mHk_yxcsFragment.setArguments(bundle);
		String toFragmentName = IndexActivity.HK_YXCS_CONTAINER_FRAGMENT;
		ft.replace(R.id.fragment_container, mHk_yxcsFragment, toFragmentName);
		ft.addToBackStack(null);
		ft.commit();
	}
	
	
	@Override
	public void onPause() {
		MessageReceiver.ehList.remove(this);
		cancelTime();
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
			YzzsApplication.isConnected = false;
			dismissLoading();
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
		if(message[0] == HkCMDConstant.HK_SBBZ) {//环控
			try {
				getMmyzPack(message);
				getSbpzEx(message);
			} catch (Exception e) {
				dismissLoading();
				ToastMsg(getActivity(), "数据异常");
			}
		}
	}
	/**
	 * 密码验证
	 * @param message
	 */
	private void getMmyzPack(byte[] message) {
		//104 109 1 0 5 0x  0(是否成功) 0(密码验证登陆成功) 1(身份) 12345678 1(校验位) 101 110 100
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		if (cmd == HkCMDConstant.SET_MMYZ) {
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
					mSpUtil.setHkPassword(et_password.getText().toString().trim());
					sendGetSbpzCMDEx(HkCMDConstant.GET_SBPZ);
				} else {
					dismissLoading();
					ToastMsg(getActivity(), "密码错误");
					isTimeOut = false;
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