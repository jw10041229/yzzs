package com.huimv.yzzs.fragment.lc;/*package com.huimv.yzzs.lc.fragment;

import com.huimv.android.base.BaseFragment;
import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.IndexActivity;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.constant.LcCMDConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
*//**
 * 水料比
 * @author jiangwei
 *
 *//*
public class Lc_slb_start_Fragment extends BaseFragment implements EventHandler{
	private final static String TAG = Lc_slb_start_Fragment.class.getSimpleName();
	private Button saveBtn;
	private SharePreferenceUtil mSpUtil;
	private StringBuffer sb = new StringBuffer();
	@Override
	public View onInitView(LayoutInflater inflater, ViewGroup rootView,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_lc_start_fragment, null);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		initView(view);
		return view;
	}
	
	private void initView(View view) {
		saveBtn = (Button) view.findViewById(R.id.saveBtn);
		saveBtn.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.saveBtn:
			sendGetSlbCMD();
			break;
		}
		super.onClick(v);
	}
	*//**
	 * 发送请求水料比命令
	 *//*
	private void sendGetSlbCMD() {
		showLoading("正在获取水料比");
		byte [] byteData = new byte[20];
		byteData[0] = 2;
		byteData[1] = LcCMDConstant.GET_SLB;
		byteData[2] = 3;
		byteData[3] = 0;
		byteData[4] = 0;
		byteData[4] = 1;
		IndexActivity.mBluetoothLeService.WriteValue(byteData);
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
	}
	
	@Override
	public void onReady(boolean isReady) {
	}
	
	@Override
	public void onNetChange(boolean isNetConnected) {
	}

	@Override
	public void onMessage(byte[] message) {
		if (message[0] == 2) {//是不是料槽的命令
			if (message[1] == LcCMDConstant.GET_SLB) {//获取水料比
				int num = message[3];// 一共几个包
				int cnt = message[4];// 当前几个包/第几个包就是第几行数据
				int dqdkK1 = message[6] >= 0 ? message[6] : message[6] + 256;// 当前端口开1时间
				int dqdkK2 = message[7] >=0 ? message[7] : message[7] + 256;// 当前端口开2时间
				int dqdkG1 = message[8] >= 0 ? message[8] : message[8] + 256;// 当前端口关1时间
				int dqdkG2 = message[9] >= 0 ? message[9] : message[9] + 256;// 当前端口关2时间
				String dqdkK = String.valueOf(dqdkK1 * 256 + dqdkK2);// 当前端口开时间
				String dqdkG = String.valueOf(dqdkG1 * 256 + dqdkG2);// 当前端口开时间
				switch (message[5]) {//第几档数据(高峰还是低峰)
				case 1://高峰
					sb.append(YzzsCommonUtil.formatStringAdd0(dqdkK, 3, 1));//开三位
					sb.append(YzzsCommonUtil.formatStringAdd0(dqdkG, 3, 1));//关三位
					if (num == cnt) {// 最后一个包
						mSpUtil.setGfData(sb.toString());
						sb.delete(0, sb.length());
					}
					break;
				case 2://低峰
					sb.append(YzzsCommonUtil.formatStringAdd0(dqdkK, 3, 1));//开三位
					sb.append(YzzsCommonUtil.formatStringAdd0(dqdkG, 3, 1));//关三位
					if (num == cnt) {// 最后一个包
						mSpUtil.setDfData(sb.toString());
						sb.delete(0, sb.length());
						dismissLoading();
						FragmentManager fm = getActivity().getSupportFragmentManager();
						FragmentTransaction ft = fm.beginTransaction();
						Lc_slbFragment mHk_yxcsFragment = new Lc_slbFragment();
						ft.replace(R.id.fragment_container, mHk_yxcsFragment);
						ft.addToBackStack(null);
						ft.commit();
					}
					break;
				}
			}
		}
	}
}
*/