package com.huimv.yzzs.fragment.scj;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huimv.android.basic.util.AndroidUtil;
import com.huimv.android.basic.util.CommonUtil;
import com.huimv.android.basic.util.DateUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.BluetoothScanActivity;
import com.huimv.yzzs.activity.IndexActivity;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.ScjCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.db.entity.Da_mc;
import com.huimv.yzzs.db.entity.Da_scj;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.support.general.BluetoothScanActivitySupport;
import com.huimv.yzzs.support.general.CreateZsmcActivitySupport;
import com.huimv.yzzs.support.scj.ScjSsxsFragmentSupport;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.huimv.yzzs.util.wheel.ScjMcDataSelectDialogUtil;
import com.huimv.yzzs.util.wheel.SetItemSelectIsShowWheelUtil;
import com.huimv.yzzs.webservice.WsCmd;
import com.orhanobut.logger.Logger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

@SuppressLint("DefaultLocale")
public class ScjSsxsFragment extends YzzsBaseFragment
		implements OnClickListener, MessageReceiver.EventHandler,
		SetItemSelectIsShowWheelUtil.OnConfirmClickSetListener,
		ScjMcDataSelectDialogUtil.OnMcDataSelectConfirmListener,
		Callback,ConnectTimeoutListener{
    Handler handler=new Handler();
	private TextView tvRfidValue, tvTempValue, tvScjState, tvTempUnit;
	private RelativeLayout rrBtnOpenLayout, rrBtnCloseLayout, rlParentLayout, rlBtStateLayout;
	private ImageView ivBtnOpen, ivBtnClose, ivDebugSet;
	boolean isTempRfid = true;
	private SetItemSelectIsShowWheelUtil mSetItemSelectWheelUtil;
	//public static final String TIME_FORMATS = "yyyy-MM-dd HH:mm:ss:SSS";
	private static final int INTALVALTIME = 10000;//10s
	public static final int SWITCH_SCJ_WORK_START = 1; // 切换可读状态
	public static final int SWITCH_SCJ_WORK_STOP = 0; // 切换不可读状态
	public static String REGEX_SPACE = " "; // 空格
	public static final int SWITCH_SCJ_MORE_MSG = 1; // 切换显示更多状态
	private ImageView iv_upload_set;//上传数据
	private TextView tv_title;
	private ScjMcDataSelectDialogUtil mScjMcDataSelectDialogUtil;
	private ScjSsxsFragmentSupport mScj_ssxsFragmentSupport = new ScjSsxsFragmentSupport();
	private String jqid = "";
	private SharePreferenceUtil mSpUtil;
	private Gson mGson = new Gson();
	private Da_scj mDa_scj;
	List<Da_scj> scjAllListLimit;
	private boolean isTimeOut = true;
	DecimalFormat df = new DecimalFormat("######0.00");//两位小数
	private int allScjSize = 0;
	private String APsw;//手持机计算公式
	private ArrayList<Da_scj> rfidTwList = new ArrayList<>();
	private boolean isCanRead = false;
	private TextView tvTempRfid;
    private SpannableStringBuilder spannableStringBuilder;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_scj_ssxs_fragment, null);
		initView(view);
		initParams();
		initOnListeners();
		return view;
	}

    private void initRunnable() {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable,Integer.valueOf(mSpUtil.getScjTime()));
    }

    private void initView(View view) {
		tvRfidValue = (TextView) view.findViewById(R.id.tv_rfid_value);
		tvScjState = (TextView) view.findViewById(R.id.tv_scj_state);
		tvTempValue = (TextView) view.findViewById(R.id.tv_temp_value);
		tvTempUnit = (TextView) view.findViewById(R.id.tv_temp_unit);

		ivBtnOpen = (ImageView) view.findViewById(R.id.iv_btn_open);
		ivBtnClose = (ImageView) view.findViewById(R.id.iv_btn_close);
		ivDebugSet = (ImageView) view.findViewById(R.id.iv_debug_set);

		rrBtnOpenLayout = (RelativeLayout) view.findViewById(R.id.rr_btn_open_layout);
		rrBtnCloseLayout = (RelativeLayout) view.findViewById(R.id.rr_btn_close_layout);
		rlParentLayout = (RelativeLayout) view.findViewById(R.id.rl_parent_layout);
		rlBtStateLayout = (RelativeLayout) view.findViewById(R.id.rl_bt_state_layout);

		iv_upload_set = (ImageView) view.findViewById(R.id.iv_upload_set);

		tv_title = (TextView) view.findViewById(R.id.tv_title);
		tvTempRfid = (TextView) view.findViewById(R.id.tv_temp_rfid);
	}

	private void initOnListeners() {
		setConnectTimeoutListener(this);
		ivBtnOpen.setOnClickListener(this);
		ivBtnClose.setOnClickListener(this);
		ivDebugSet.setOnClickListener(this);
		iv_upload_set.setOnClickListener(this);
		tv_title.setOnClickListener(this);
	}

	private void initParams() {
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		mScjMcDataSelectDialogUtil = new ScjMcDataSelectDialogUtil(getActivity(), "请选择猪舍", 1, this);
		String[] wheelItemArr = getActivity().getResources().getStringArray(R.array.scj_set_item_array2);
		mSetItemSelectWheelUtil = new SetItemSelectIsShowWheelUtil(getActivity(), "请选择设置项目", wheelItemArr,
				ScjSsxsFragment.this);
		if (IndexActivity.SCJ_WORK_STATE == 1) {
			// 手持机处于“可读”状态
			tvScjState.setText("可读");
			//rrBtnOpenLayout.setVisibility(View.GONE);
			//rrBtnCloseLayout.setVisibility(View.VISIBLE);
		} else {
			// 手持机处于“不可读”状态
			tvScjState.setText("不可读");
			//rrBtnOpenLayout.setVisibility(View.VISIBLE);
			//rrBtnCloseLayout.setVisibility(View.GONE);
		}
		if (!hasLogined()) {
			//noLoginedDialog();
		}
		List<Da_mc> daMcList = BluetoothScanActivitySupport.getAllMc(getActivity());
		if (daMcList.size() == 0) {//没有牧场
			tv_title.setText("温度");
		} else {
			List<String> mcmcList = new ArrayList<>();
			for (Da_mc daMc : daMcList) {
				mcmcList.add(daMc.getMcmc());
			}
			// 取出 mcidList中不重复牧场名称数据保存在 set中
			HashSet<String> set = new HashSet<>(mcmcList);
			mcmcList.clear(); // 将去重的牧场名称重新放进 mcmcList 中
			for (String mcmc : set) {
				mcmcList.add(mcmc);
			}
			// 初始化 牧场名称 数组吗，以显示 wheel
			String [] mcmcArr = new String[mcmcList.size()];
			for (int i = 0; i < set.size(); i++) {
				mcmcArr[i] = mcmcList.get(i);
			}
			String mcmcFirst = mcmcArr[0];//第一个牧场名称
			List<String> zslbList = CreateZsmcActivitySupport.getZslbByMcmc(getActivity(), mcmcFirst);
			if (zslbList.size() == 0) {//此牧场没有猪舍
				tv_title.setText("该牧场没有猪舍");
			} else {
				String zsmcFirst = zslbList.get(0);//第一个猪舍名称
				jqid = CreateZsmcActivitySupport.getJqidByZsmc(getActivity(), zsmcFirst);
				if (CommonUtil.isEmpty(jqid)) {
					tv_title.setText("该猪舍没有机器ID,请换其他舍");
				} else {
					tv_title.setText(zsmcFirst);
				}
			}
		}
	}

	@Override
	public void onResume() {
		MessageReceiver.ehList.add(this);
		initUI();
		initRunnable();
		super.onResume();
	}

	private void initUI() {
		tvTempValue.setText("N/A");
		tvRfidValue.setText("");
		rfidTwList.clear();
	}

	@Override
	public void onPause() {
		MessageReceiver.ehList.remove(this);
		super.onPause();
	}

	@Override
	public void onConnected(boolean isConnected) {
		YzzsApplication.isConnected = isConnected;
		if (!isConnected) {// 如果断开
			ToastMsg(getActivity(), getString(R.string.disconnected));
			rlBtStateLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onReady(boolean isReady) {

	}

	@Override
	public void onMessage(byte[] message) {
		receivePack(message);
	}

	/**
	 * 接收包
	 *
	 * @param message
	 */
	private void receivePack(byte[] message) {
		if (message[0] == ScjCMDConstant.SCJ_SBBZ) { // 手持机
			try {
			refrehRealDataEx(message);
			} catch (Exception e) {
				dismissLoading();
				ToastMsg(getActivity(), "收到数据格式错误");
			}
		}
	}

/*	*//**
	 * 精确到毫秒
	 * @param longTime
	 * @return
	 *//*
	public static String parserTimeTosss(long longTime) {
		DateFormat format = new SimpleDateFormat(TIME_FORMATS, Locale.SIMPLIFIED_CHINESE);
		try {
			return format.format(new Date(longTime));
		} catch (Exception e) {
			return null;
		}
	}*/
	/**
	 * 更新 RFID 和 体温 的实时数据
	 *
	 * @param message
	 */
	private void refrehRealDataEx(byte[] message) {
		// 截取实时数据：从第 6 个位置开始截取
		byte[] realData = Arrays.copyOfRange(message, 6, message.length);
		int cmd = YzzsCommonUtil.getCMD(message[1], message[2]);
		if (cmd == ScjCMDConstant.GET_RFID) { // 得到 RFID 数据
			APsw = bytesToHexString(realData, REGEX_SPACE).replace(" ", "");
			//boolean isTempRfid = APsw.substring(15,16).equals("5");//5是温度标签
			isTempRfid = true;
			String StrHex = APsw.substring(0,15);
			mDa_scj = new Da_scj();
			mDa_scj.setJqid(jqid);
			mDa_scj.setRfid(StrHex);
			mDa_scj.setCjsj(DateUtil.parser(System.currentTimeMillis()));
			Logger.d("GET_RFID",Arrays.toString(message));
			if(!isTempRfid) {//普通标签
				showSaveData(rfidTwList,mDa_scj,"N/A",true);
			}
		}

		if (cmd == ScjCMDConstant.GET_TEMP_CODE) {
			tvTempValue.setText(message[6] + "" +  message[7] +".0");
		}
		if (cmd == ScjCMDConstant.GET_TEMP) { // 得到 TEMP 数据


			Logger.d("GET_TEMP",Arrays.toString(message));
			String ABCValue = APsw.substring(16);
			if ("".equals(APsw) || ABCValue.length() != 8) {
				ToastMsg(getActivity(), "数据出错");
			} else {
				String temp = bytesToString(realData, REGEX_SPACE);
				String[] strArr = temp.split(REGEX_SPACE);
				int code = Integer.parseInt(Integer.toHexString(Integer.valueOf(strArr[0])) + "" + Integer.toHexString(Integer.valueOf(strArr[1])),16);
				String A = ABCValue.substring(0,2);
				String B = ABCValue.substring(2,4);
				String C = ABCValue.substring(4,6);
				if (A.equals("00") && B.equals("00") && C.equals("00")) {
					A ="97";
					B ="70";
					C ="55";
				}
				String tempValue = df.format(Double.valueOf("12"+ A)/10000 * code - Double.valueOf("2" + B + C)/100) + "";
				switchBgColorBtTemp(Float.valueOf(tempValue));
				if (Float.valueOf(tempValue) < 0 || Float.valueOf(tempValue) > 50) {
					tvTempValue.setText("温度异常");
					tvTempRfid.setText(mDa_scj.getRfid());
				} else {
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, Integer.valueOf(mSpUtil.getScjTime()));
					mDa_scj.setTw(tempValue);
					showSaveData(rfidTwList,mDa_scj,tempValue,true);
				}
			}
		}

		if (cmd == ScjCMDConstant.SWITCH_SCJ_WORK_STATE) { // 设置手持机状态
			isTimeOut = false;
			dismissLoading();
			if (message[5] == 0 && message[6] == 1) {
				ToastMsg(getActivity(), "手持机切换“可读”状态成功");
				tvScjState.setText("可读");
				rrBtnOpenLayout.setVisibility(View.GONE);
				rrBtnCloseLayout.setVisibility(View.VISIBLE);
				isCanRead = true;
			}
			if (message[5] == 0 && message[6] == 0) {
				ToastMsg(getActivity(), "手持机切换“不可读”状态成功");
				tvScjState.setText("不可读");
				rrBtnOpenLayout.setVisibility(View.VISIBLE);
				rrBtnCloseLayout.setVisibility(View.GONE);
				isCanRead = false;
			}
		}
		if (cmd == ScjCMDConstant.SWITCH_SCJ_MORE_MSG) {
			isTimeOut = false;
			dismissLoading();
			// 切换显示信息状态
			if (message[5] == 0 && message[6] == 1) {
				// 跳转到更多信息界面
				ToastMsg(getActivity(), "显示更多信息状态...");
				toSetFragment(new ScjSsxsMoreFragment());
			}
			if (message[5] == 0 && message[6] == 0) {
				ToastMsg(getActivity(), "显示普通信息状态...");
			}
		}
	}

	private void showSaveData(ArrayList<Da_scj> rfidTwList,Da_scj mDa_scj,String tempValue,boolean isSaveDB) {
		if (rfidTwList.size() == 15) {
			rfidTwList.remove(0);
		}
		rfidTwList.add(mDa_scj);
		StringBuilder sb = new StringBuilder();
		int startLenthPos = 0;
		for (int i = 0; i < rfidTwList.size() ; i++) {
			sb.append(rfidTwList.get(i).getRfid());
			if (isTempRfid) {
				sb.append("/").append(rfidTwList.get(i).getTw()).append("℃");
			} else {
				sb.append("/").append(DateUtil.parser2(System.currentTimeMillis()));
			}
			if(i !=rfidTwList.size() - 1 ) {
				sb.append("\n");
			}
			if (isTempRfid) {
				startLenthPos = rfidTwList.get(rfidTwList.size() - 1).getRfid().length() + rfidTwList.get(rfidTwList.size() - 1).getTw().length() + 2;
			} else
				startLenthPos = rfidTwList.get(rfidTwList.size() - 1).getRfid().length()  + rfidTwList.get(rfidTwList.size() - 1).getTw().length() + DateUtil.TIME_FORMATSS.length();
		}
		spannableStringBuilder = new SpannableStringBuilder(sb);
		//TODO 图片 位置不正确
/*
		Drawable d = getResources().getDrawable(R.drawable.figer_right);
		d.setBounds(50, 50, 50, 50);
		ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
		spannableStringBuilder.setSpan(span, sb.length() - startLenthPos, sb.length() - startLenthPos + 50, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/
		StyleSpan span = new StyleSpan(Typeface.BOLD);
		//用颜色标记
		spannableStringBuilder.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.b_main)), sb.length() - startLenthPos, sb.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		//action_bar_disable
		//粗体
		spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), sb.length() - startLenthPos, sb.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		if (Float.valueOf(tempValue) < 38 || Float.valueOf(tempValue) > 40) {
			tvTempValue.setText(tempValue + "异常");
			tvTempValue.setTextColor(getResources().getColor(R.color.orange));
		} else  {
			tvTempValue.setText(tempValue);
			tvTempValue.setTextColor(getResources().getColor(R.color.scj_bg_main_green));
		}
		tvTempRfid.setText(mDa_scj.getRfid());
        mDa_scj.setTw(tempValue);
		tvRfidValue.setText(spannableStringBuilder);
		if (isSaveDB) {
            doDBAction(mDa_scj);
        }
	}
	/**
	 * 1:如果数据库里没有数据,直接存入数据库
	 * 2:如果有数据则判断
	 *    2.1:如果机器ID相等并且RFID相等
	 *        2.1.1:如果时间相差10s之内,体温取两者的平均值,
	 *                时间取平均值拼成一条新数据替代原来的一条数据存入
	 *        or 直接存入
	 *    or 直接存入
	 *
	 */
	private void doDBAction(Da_scj mDa_scj2) {
		List<Da_scj> dataFromDb = mScj_ssxsFragmentSupport.getAllScj(getActivity());
		int dataSize = dataFromDb.size();
		if (dataSize == 8000) {
			ToastMsg(getActivity(), "本地存储的数据上限为10000条,当前已有8000条,请及时上传,");
		}
		if (dataSize == 10000) {
			ToastMsg(getActivity(), "本地存储的数据上限为10000条,当前已有10000条,请上传,");
			return;
		}
		if (dataSize == 0) {
			mScj_ssxsFragmentSupport.insertScj(getActivity(), mDa_scj2);
		} else {
			Da_scj firstData = dataFromDb.get(dataSize - 1);
			if (firstData.getRfid().equals(mDa_scj2.getRfid()) && firstData.getJqid().equals(mDa_scj2.getJqid()) ) {
				long reviceCjsj = DateUtil.parsers(mDa_scj2.getCjsj()).getTime();
				long firstCjsj = DateUtil.parsers(firstData.getCjsj()).getTime();
				if (reviceCjsj - firstCjsj < INTALVALTIME) {
					long cjsj = (reviceCjsj + firstCjsj) / 2;
					String tw = String.valueOf(df.format((Double.valueOf(mDa_scj2.getTw()) + Double.valueOf(firstData.getTw())) /2));
					mDa_scj2.setCjsj(DateUtil.parser(cjsj));
					mDa_scj2.setTw(tw);
					mScj_ssxsFragmentSupport.deleteScjData(getActivity(), firstData);
					mScj_ssxsFragmentSupport.insertScj(getActivity(), mDa_scj2);
				} else {
					mScj_ssxsFragmentSupport.insertScj(getActivity(), mDa_scj2);
				}
			} else {
				mScj_ssxsFragmentSupport.insertScj(getActivity(), mDa_scj2);
			}
		}
	}

	/**
	 * 根据当前温度切换界面温度
	 *
	 * @param currentTemp
	 */
	private void switchBgColorBtTemp(float currentTemp) {
		if (currentTemp < 25) {
			tvTempValue.setTextColor(getResources().getColor(R.color.scj_bg_main_blue));
			tvTempUnit.setTextColor(getResources().getColor(R.color.scj_bg_main_blue));
			rlParentLayout.setBackgroundResource(R.color.scj_bg_main_blue);
			tvTempRfid.setTextColor(getResources().getColor(R.color.scj_bg_main_blue));
		}
		if (currentTemp < 30 && currentTemp >= 25) {
			tvTempValue.setTextColor(getResources().getColor(R.color.scj_bg_main_green));
			tvTempUnit.setTextColor(getResources().getColor(R.color.scj_bg_main_green));
			rlParentLayout.setBackgroundResource(R.color.scj_bg_main_green);
			tvTempRfid.setTextColor(getResources().getColor(R.color.scj_bg_main_green));
		}
		if (currentTemp < 40 && currentTemp >= 30) {
			tvTempValue.setTextColor(getResources().getColor(R.color.scj_bg_main_yellow));
			tvTempUnit.setTextColor(getResources().getColor(R.color.scj_bg_main_yellow));
			rlParentLayout.setBackgroundResource(R.color.scj_bg_main_yellow);
			tvTempRfid.setTextColor(getResources().getColor(R.color.scj_bg_main_yellow));
		}
		if (currentTemp >= 40) {
			tvTempValue.setTextColor(getResources().getColor(R.color.scj_bg_main_red));
			tvTempUnit.setTextColor(getResources().getColor(R.color.scj_bg_main_red));
			rlParentLayout.setBackgroundResource(R.color.scj_bg_main_red);
			tvTempRfid.setTextColor(getResources().getColor(R.color.scj_bg_main_red));
		}
	}

	@Override
	public void onNetChange(boolean isNetConnected) {
	}

	@Override
	public void onClick(View v) {
		if (YzzsApplication.isConnected) {
			super.onClick(v);
			switch (v.getId()) {
				case R.id.iv_btn_open:
					sendSwitchScjStateToBt(ScjCMDConstant.SWITCH_SCJ_WORK_STATE, SWITCH_SCJ_WORK_START);
					startTime(XtAppConstant.SEND_CMD_TIMEOUT, "开关操作");
					isTimeOut = true;
					break;
				case R.id.iv_btn_close:
					sendSwitchScjStateToBt(ScjCMDConstant.SWITCH_SCJ_WORK_STATE, SWITCH_SCJ_WORK_STOP);
					startTime(XtAppConstant.SEND_CMD_TIMEOUT, "开关操作");
					isTimeOut = true;
					break;
				case R.id.iv_debug_set:
					mSetItemSelectWheelUtil.showDialog();
					break;
			}
		} else {
			ToastMsg(getActivity(), getString(R.string.disconnected));
		}

		if (v.getId() == R.id.iv_upload_set) {
			if (!AndroidUtil.isConn(getActivity())) {
				AndroidUtil.setNetworkMethod(getActivity());
			} else {
				allScjSize = mScj_ssxsFragmentSupport.getAllScj(getActivity()).size();
				doUpLoad();
			}
		}
		if (v.getId()  == R.id.tv_title) {
			if (hasLogined()) {
				mScjMcDataSelectDialogUtil.showDialog();
			}
		}
	}

	/**
	 * 执行上传功能
	 */
	private void doUpLoad() {
		scjAllListLimit = new ArrayList<Da_scj>();
		scjAllListLimit = mScj_ssxsFragmentSupport.getAllScjLimit(getActivity(),1000);
		int size = scjAllListLimit.size();
		if (size != 0) {
			upload();
		} else {
			dismissLoading();
			ToastMsg(getActivity(), "本地无可上传数据");
		}
	}

	private void upload() {
		int remainingSize= mScj_ssxsFragmentSupport.getAllScj(getActivity()).size();
		showLoading("正在上传" + (allScjSize - remainingSize + 1000) + "/" + allScjSize);
		//startTime(HkCMDConstant.SEND_HK_SAVE_CMD_TIMEOUT, "数据上传");
		startTime(10000, "数据上传");
		isTimeOut = true;
		String params = mGson.toJson(scjAllListLimit);
		mScj_ssxsFragmentSupport.scjUploadThread(params, getActivity(), new Handler(this));
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
		bundle.putInt("whichDev", ScjCMDConstant.SCJ_SBBZ);
		mFragment.setArguments(bundle);
		ft.addToBackStack(null);
		ft.commit();
	}

	/**
	 * 发送切换手持机状态的命令到 bt
	 *
	 * @param CmdType
	 * @param dataType
	 *            命令类型：0：设置手持机为“不可读状态”；或者：切换为“显示普通状态”
	 *            1：设置手持机为“可读状态”；或者切换为“显示更多状态”
	 */
	private void sendSwitchScjStateToBt(int CmdType, int dataType) {
		int dataLength = 12; // 数据总长度
		byte[] byteSendData = new byte[dataLength]; // 构造 byte 数组
		byteSendData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH); // 协议头 “h”
		byteSendData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM); // 协议头 “m”
		byteSendData[2] = YzzsCommonUtil.intTobyte(ScjCMDConstant.SCJ_SBBZ); // 数据域：类型
		byteSendData[3] = YzzsCommonUtil.intTobyte(CmdType / 256); // 数据域：命令高位
		byteSendData[4] = YzzsCommonUtil.intTobyte(CmdType % 256); // 数据域：命令低位
		byteSendData[5] = YzzsCommonUtil.intTobyte(dataLength / 256); // 数据域：数据总长度高位
		byteSendData[6] = YzzsCommonUtil.intTobyte(dataLength % 256); // 数据域：数据总长度低位
		byteSendData[7] = YzzsCommonUtil.intTobyte(dataType); // 数据域：单条有效数据长度：0
		byteSendData[8] = YzzsCommonUtil.intTobyte(1); // 校验位
		byteSendData[9] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE); // 协议尾：“e”
		byteSendData[10] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN); // 协议尾：“n”
		byteSendData[11] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD); // 协议尾：“d”
		sendUnpackData(byteSendData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
		showLoading("正在设置手持机状态...");
	}


	/**
	 * byte[]转换成十六进制字符串
	 *
	 * @param bArray
	 * @param regex
	 * @return
	 */
	public String bytesToHexString(byte[] bArray, String regex) {
		StringBuilder sb = new StringBuilder(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase() + regex);
		}
		// 去掉最后一个 regex
		sb.substring(0, sb.length() - 1);
		return sb.toString();
	}

	/**
	 * byte[]转换成字符串，byte值中间用 regex 隔开（负数转为正数显示）
	 *
	 * @param bArray
	 * @param regex
	 * @return
	 */
	private String bytesToString(byte[] bArray, String regex) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bArray.length; i++) {
			// 负数转为正数
			if (bArray[i] < 0) {
				sb.append(YzzsCommonUtil.ChangeByteToPositiveNumber(bArray[i]) + regex);
			} else {
				sb.append(byteToString(bArray[i]) + regex);
			}
		}
		// 去掉最后一位 regex
		sb.substring(0, sb.length() - 1);
		return sb.toString();
	}

	/**
	 * byte 转 String
	 *
	 * @param b
	 * @return
	 */
	private String byteToString(byte b) {
		StringBuilder sb = new StringBuilder();
		sb.append(b);
		return sb.toString();
	}

	@Override
	public void onConfirm(int position) {
		switch (position) {
			case 0:
				//数据列表
				toSetFragment(new ScjDataListFragment());
				break;
			case 1:
				// 显示信息
				sendSwitchScjStateToBt(ScjCMDConstant.SWITCH_SCJ_MORE_MSG, SWITCH_SCJ_MORE_MSG);
				break;
			case 2:
				// 跳转到参数设置界面
				showDebugLoginDialog();
				break;
			case 3:
				showDebugRfidTempCalibrat();
				break;

		}
	}

	/**
	 * 进入设置界面前密码验证
	 */
	private void showDebugLoginDialog() {
		final LinearLayout ll = new LinearLayout(getActivity());
		final EditText etPassword = new EditText(getActivity());
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.addView(etPassword);
		etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		etPassword.setGravity(Gravity.CENTER);
		etPassword.setHint("调试密码");
		etPassword.setText("654321");
		etPassword.setHeight(200);
		new AlertDialog.Builder(getActivity()).setTitle("请输入调试密码").setView(ll)
				.setPositiveButton("登陆", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String debugPassword = etPassword.getText().toString().trim();
						if (CommonUtil.isEmpty(debugPassword)) {
							ToastMsg(getActivity(), "调试密码不能为空");
							return;
						}
						if ("654321".equals(debugPassword)) {
							showLoading("正在获取手持机当前的频率值和功率值");
							sendSwitchScjStateToBt(ScjCMDConstant.GET_POWER_FREQUENCY_VALUE, 0);
							toSetFragment(new ScjSetParamFragment());
							dismissLoading();
						} else {
							ToastMsg(getActivity(), "调试密码输入不正确，请重新输入验证");
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
	 * 进入耳标温度标定
	 */
	private void showDebugRfidTempCalibrat() {
		final LinearLayout ll = new LinearLayout(getActivity());
		final EditText etPassword = new EditText(getActivity());
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.addView(etPassword);
		etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		etPassword.setGravity(Gravity.CENTER);
		etPassword.setHint("调试密码");
		etPassword.setText("654321");
		etPassword.setHeight(200);
		new AlertDialog.Builder(getActivity()).setTitle("请输入调试密码").setView(ll)
				.setPositiveButton("登陆", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String debugPassword = etPassword.getText().toString().trim();
						if (CommonUtil.isEmpty(debugPassword)) {
							ToastMsg(getActivity(), "调试密码不能为空");
							return;
						}
						if ("654321".equals(debugPassword)) {
							// 进入到 耳标温度标定 界面
							toSetFragment(new ScjRfidTempFragment());
						} else {
							ToastMsg(getActivity(), "调试密码输入不正确，请重新输入验证");
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
	public void OnMcDataSelectConfirm(HashMap<String, String> paramsMap) {
		String zsmc = paramsMap.get("zsmc");
		jqid = paramsMap.get("jqid");
		if (CommonUtil.isEmpty(zsmc)) {
			tv_title.setText("无猪舍,数据不可上传");
		} else {
			tv_title.setText(zsmc);
		}
	}

	/**
	 * 是否登陆
	 * @return
	 */
	private boolean hasLogined () {
		return CommonUtil.isNotEmpty(mSpUtil.getYhxm())
				&& CommonUtil.isNotEmpty(mSpUtil.getYhmm());
	}

	/**
	 * 没登录提醒
	 */
	public void noLoginedDialog () {
		new AlertDialog.Builder(getActivity()).setMessage("请登录账户,否则数据将不能同步到云端").setTitle("连接登录?")
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
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
			case XtAppConstant.SCJ_UPLOAD_VISION:
				isTimeOut = false;
				if (msg.arg1 == WsCmd.SUCCESS) {
					//ToastMsg(getActivity(), "上传成功");
					mScj_ssxsFragmentSupport.deleteAllScj(getActivity(), scjAllListLimit);
					doUpLoad();
				}
				if (msg.arg1 == WsCmd.PARAM_ERROR) {
					dismissLoading();
					ToastMsg(getActivity(), "上传失败");
				}
				if (msg.arg1 == WsCmd.SYSTEM_ERROR) {
					dismissLoading();
					ToastMsg(getActivity(), "服务器异常");
				}// 参数错误
				break;
			default:
				break;
		}
		return false;
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

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
			if (isCanRead) {
				List<Da_scj> mDa_scjList = mScj_ssxsFragmentSupport.getAllScj(getActivity());
				if (mDa_scjList.size() > 0) {
					Random random = new Random();
					Da_scj mDa_scj = mDa_scjList.get(random.nextInt(mDa_scjList.size()));
					showSaveData(rfidTwList,mDa_scj,mDa_scj.getTw(),false);
				}
			}
            //要做的事情
            handler.postDelayed(this, Integer.valueOf(mSpUtil.getScjTime()));
        }
    };
}
