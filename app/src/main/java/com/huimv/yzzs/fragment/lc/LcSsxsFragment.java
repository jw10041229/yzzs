package com.huimv.yzzs.fragment.lc;

import com.huimv.yzzs.R;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.LcCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.huimv.yzzs.util.wheel.LcTitleWheelUtil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
/**
 * 料槽实时显示
 * @author jiangwei
 *
 */
public class LcSsxsFragment extends YzzsBaseFragment implements OnClickListener, EventHandler,LcTitleWheelUtil.OnTitleConfirmClickListener,ConnectTimeoutListener{
	private final static String TAG = LcSsxsFragment.class.getSimpleName();
	private LcTitleWheelUtil mFlz_lc_titleWheelUtil;
	private boolean isTimeOut = true;
	private SharePreferenceUtil mSpUtil;
	/**
	 * title 
	 */
	private TextView tv_title;
	/**
	 *故障刷新
	 */
	private ImageView iv_refresh_fault;
	/**
	 *参数刷新
	 */
	private ImageView iv_refresh;
	/**
	 *当前量整数
	 */
	private TextView tv_aql_value_integer;
	/**
	 *当前量小数
	 */
	private TextView tv_aql_value_decimal;
	private TextView tv_aql_value_point;
	private TextView tv_aql_value_unit;
	
	/**
	 * 下料量
	 */
	private TextView tv_rate_one_day_value;
	/**
	 * 下水量
	 */
	private TextView tv_water_one_day_value;
	/**
	 * 故障
	 */
	private TextView tv_fault;
	/**
	 * 故障ScrollView
	 */
	private ScrollView sv_fault;
	/**
	 * 图片旋转
	 */
	private Animation operatingAnim;
	String[] items;
	/**
	 * 设备序号：默认为1号料槽
	 */
	private int devNumber = 1;
	/**
	 * 设置
	 */
	private TextView tv_set;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_lc_ssxs_fragment, null);
		initView(view);
		initData();
		return view;
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		setConnectTimeoutListener(this);
		operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.img_rotate);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
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
		mFlz_lc_titleWheelUtil = new LcTitleWheelUtil(getActivity(), "请选择料槽", items, LcSsxsFragment.this);
		tv_title.setText("1号料槽");
	}
	
	/**
	 * 初始化view
	 * @param view
	 */
	private void initView(View view) {
		tv_aql_value_point = (TextView) view.findViewById(R.id.tv_aql_value_point);
		tv_aql_value_unit = (TextView) view.findViewById(R.id.tv_aql_value_unit);
		tv_aql_value_integer = (TextView) view.findViewById(R.id.tv_aql_value_integer);
		tv_aql_value_decimal = (TextView) view.findViewById(R.id.tv_aql_value_decimal);
		iv_refresh_fault = (ImageView) view.findViewById(R.id.iv_refresh_fault);
		iv_refresh_fault.setOnClickListener(this);
		tv_rate_one_day_value = (TextView) view.findViewById(R.id.tv_rate_one_day_value);
		tv_water_one_day_value = (TextView) view.findViewById(R.id.tv_water_one_day_value);
		tv_fault = (TextView) view.findViewById(R.id.tv_fault);
		sv_fault = (ScrollView) view.findViewById(R.id.sv_fault);
		tv_set = (TextView) view.findViewById(R.id.tv_set);
		tv_set.setOnClickListener(this);
		iv_refresh = (ImageView) view.findViewById(R.id.iv_refresh);
		iv_refresh.setOnClickListener(this);
		tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_title.setOnClickListener(this);
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "onResume()");
		MessageReceiver.ehList.add(this);
		devNumber = 1;
		tv_title.setText("1号料槽");
		if(YzzsApplication.isConnected) {
			sendRefreshCMD(iv_refresh,LcCMDConstant.LC_GET_SHUI_LIAO_DATA);
			cleanTv();
		} else {
			ToastMsg(getActivity(), getString(R.string.bluetooth_disconnected));
		}
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
		case R.id.iv_refresh_fault:
			if(YzzsApplication.isConnected) {
				sendRefreshCMD(iv_refresh_fault,LcCMDConstant.GET_FALUT_YXZT);
			} else {
				ToastMsg(getActivity(), getString(R.string.bluetooth_disconnected));
			}
			break;
		case R.id.iv_refresh:
			if(YzzsApplication.isConnected) {
				sendRefreshCMD(iv_refresh,LcCMDConstant.LC_GET_SHUI_LIAO_DATA);
				cleanTv();
			} else {
				ToastMsg(getActivity(), getString(R.string.bluetooth_disconnected));
			}
			break;
		case R.id.tv_title:
			mFlz_lc_titleWheelUtil.showDialog();
			break;
		case R.id.tv_set:
			toSetFragment(new LcMmyzFragment(), "");
			break;
		default:
			break;
		}
		super.onClick(v);
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
	private void cleanTv() {
		tv_aql_value_integer.setText("");
		tv_aql_value_unit.setVisibility(View.INVISIBLE);
		tv_rate_one_day_value.setText("");
		tv_water_one_day_value.setText("");
	}

	/**
	 * 接收包
	 * @param message
	 */
	private void receivePack (byte[] message) {
		if (message.length < 6) {
			return;
		}
		if(message[0] == LcCMDConstant.LC_SBBZ) {//料槽
				int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
				switch (cmd) {
				case LcCMDConstant.LC_XLL://下料量
					feedDataParsing(message,tv_rate_one_day_value,"下料");
					break;
				case LcCMDConstant.LC_XSL://下水水量
					feedDataParsing(message,tv_water_one_day_value,"下水");
					break;
				case LcCMDConstant.LC_DQL://当前料斗重量
					dqlDataParsing(message);
					break;
				case LcCMDConstant.GTLC_DEV_FZULT://设备故障故障状态
					gzztDataParsing(message);
					break;
				default:
					break;
				}
		}
	}
	
	private void dqlDataParsing(byte[] message) {
		if(message.length < 11) {
			tv_aql_value_integer.setText("当前料数据异常");
		} else {
			isTimeOut = false;
			iv_refresh.clearAnimation();
			if (message[5] == 0 ) {
				StringBuilder dataSb = new StringBuilder();
				//0 0  6 0 （正为0.负为1） 11111
				for (int i = 7; i < 7 + message[6]; i++) {//5位下料量
					dataSb.append(message[i]);
				}
				String data  = String.valueOf(Integer.parseInt(dataSb.toString()));
				tv_aql_value_integer.setText(data);
				tv_aql_value_integer.setTextSize(46);
				tv_aql_value_integer.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
				//tv_aql_value_decimal.setText(dataSb.toString().substring(3,5));
				tv_aql_value_decimal.setVisibility(View.GONE);
				tv_aql_value_point.setVisibility(View.GONE);
				tv_aql_value_unit.setVisibility(View.VISIBLE);
			} else {
				tv_aql_value_integer.setText("当前料数据接收异常");
				tv_aql_value_integer.setTextSize(24);
				tv_aql_value_integer.setGravity(Gravity.CENTER);
				tv_aql_value_decimal.setVisibility(View.GONE);
				tv_aql_value_point.setVisibility(View.GONE);
				tv_aql_value_unit.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 故障信息
	 * @param message
	 */
	private void gzztDataParsing(byte[] message) {
		iv_refresh_fault.clearAnimation();
		if (message.length < 13) {
			tv_fault.setText("故障数据异常");
			return;
		}
		boolean hasFault = false;
		String[] faultData = getActivity().getResources().getStringArray(R.array.lc_guzhang_zhuangtai_item_array);
		StringBuilder faultDataSb = new StringBuilder();
		String dataBottom = "\n";
		int min = message[6] < faultData.length ? message[6] : faultData.length;//取两者中小的
		for (int i = 0; i < min; i++) {
			if (message[7 + i] == 1) {
				if (i == faultData.length -1 ) {
					dataBottom = "";
				}
				faultDataSb.append(faultData [i]).append(dataBottom);
				hasFault = true;
			} else {
				hasFault = false;
			}
		}
		sv_fault.post(new Runnable() {
			public void run() {
				sv_fault.fullScroll(ScrollView.FOCUS_FORWARD);
			} 
		});
		if (!hasFault){
			tv_fault.setText("设备无故障");
		} else {
			tv_fault.setText(faultDataSb.toString());
		}
	}
	
	/**
	 * 下料量/采食量/剩余量
	 * @param message
	 */
	private void feedDataParsing(byte[] message,TextView tv,String msg) {
		if (message.length < 10) {
			tv.setText( msg + "数据异常");
			return;
		}
		if (message[5] == 0 && message[6] >= 4) {
			StringBuilder dataSb = new StringBuilder();
			for (int i = 7; i < 7 + message[6]; i++) {//数值
				dataSb.append(message[i]);
			}
			tv.setText(Double.valueOf(dataSb.toString())/100 + "");
		} else {
			tv.setText(msg + "数据接收异常");
		}
	}

	
	/**
	 * 刷新获取数据
	 * @param dev
	 * @param cmd
	 */
	public void sendGetRefreshCMD(int dev, int cmd) {
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
	 * 发送刷新命令
	 */
	private void sendRefreshCMD(ImageView iv,int cmd) {
		if(iv.getAnimation() !=null && iv.getAnimation().hasStarted()) {
			iv.clearAnimation();
			isTimeOut = false;
			cancelTime();
		} else {
			iv.startAnimation(operatingAnim);
			sendGetRefreshCMD(LcCMDConstant.LC_SBBZ, cmd);
			isTimeOut = true;
			startTime(XtAppConstant.SEND_CMD_TIMEOUT, "刷新");
		}
	}

	@Override
	public void onTitleConfirm(int position) {
		devNumber = position + 1;
		tv_title.setText(items[position]);
		cleanTv();
		sendRefreshCMD(iv_refresh,LcCMDConstant.LC_GET_SHUI_LIAO_DATA);
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
			if(iv_refresh_fault.getAnimation() !=null && iv_refresh_fault.getAnimation().hasStarted()) {
				iv_refresh_fault.clearAnimation();
			}
			if(iv_refresh.getAnimation() !=null && iv_refresh.getAnimation().hasStarted()) {
				iv_refresh.clearAnimation();
			}
			ToastMsg(getActivity(), content + getString(R.string.connect_time_out));
		}
	}
}
