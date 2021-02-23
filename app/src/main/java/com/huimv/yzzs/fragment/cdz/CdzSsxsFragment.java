package com.huimv.yzzs.fragment.cdz;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.IndexActivity;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.CdzCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.orhanobut.logger.Logger;

import java.util.Arrays;

/**
 * 测定站实时显示
 * @author jiangwei
 *
 */
public class CdzSsxsFragment extends YzzsBaseFragment implements OnClickListener, EventHandler,ConnectTimeoutListener{
	private final static String TAG = CdzSsxsFragment.class.getSimpleName();
	private final StringBuilder falutSb = new StringBuilder();//故障
	private boolean isTimeOut = true;
	/**
	 * 暂停.恢复tag
	 */
	private boolean PAUSE_TAG ;
	/**
	 * 暂停按钮
	 */
	private TextView tv_pause;
	/**
	 * 设置按钮
	 */
	private TextView tv_set;
	/**
	 *故障刷新
	 */
	private ImageView iv_refresh_fault;
	/**
	 * 体重值
	 */
	private TextView tv_weight_value;
	private TextView tv_weight_value2;
	/**
	 * 体重单位
	 */
	private TextView tv_weight_unit;
	/**
	 * 体重小数点
	 */
	private TextView tv_weight_point;
	/**
	 * RFID值
	 */
	private TextView tv_rfid_value;
	/**
	 * 运行状态
	 */
	private TextView tv_dev_state_value;
	/**
	 * 工作阶段
	 */
	private TextView tv_work_model;
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
	/**
	 * 体重分栏值
	 */
	private TextView tv_fl_weight_state_value;
	/**
	 * 体温
	 */
	private TextView tv_temperature_value;
	
	/**
	 * 出栏/分栏数据
	 */
	private LinearLayout ll_fl_cl_data;
	/**
	 * 目标出栏个数
	 */
	private TextView tv_target_cl_count_value;
	/**
	 * 当前出栏个数
	 */
	private TextView tv_cl_count_now_value;
	/**
	 * 出栏是否结束
	 */
	private TextView tv_is_over_value;

	/**
	 * 工作模式location
	 */
	private int work_model_location = -1;

	/**
	 * 运行状态
	 */
	private LinearLayout ll_yxzt;
	private View view_separstor_8;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_flz_ssxs_fragment, null);
		initView(view);
		initData();
		return view;
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.img_rotate);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		setConnectTimeoutListener(this);
		if(IndexActivity.cdz_isRunning) {
			tv_pause.setText(getString(R.string.flz_state_pause));
		} else {
			tv_pause.setText(getString(R.string.flz_state_run));
		}
	}
	/**
	 * 初始化view
	 * @param view
	 */
	private void initView(View view) {
		tv_pause = (TextView) view.findViewById(R.id.tv_pause);
		tv_set = (TextView) view.findViewById(R.id.tv_set);
		iv_refresh_fault = (ImageView) view.findViewById(R.id.iv_refresh_fault);
		tv_pause.setOnClickListener(this);
		tv_set.setOnClickListener(this);
		iv_refresh_fault.setOnClickListener(this);
		tv_weight_value = (TextView) view.findViewById(R.id.tv_weight_value);
		tv_weight_value2 = (TextView) view.findViewById(R.id.tv_weight_value2);
		tv_weight_unit = (TextView) view.findViewById(R.id.tv_weight_unit);
		tv_weight_point = (TextView) view.findViewById(R.id.tv_weight_point);
		tv_rfid_value = (TextView) view.findViewById(R.id.tv_rfid_value);
		tv_dev_state_value = (TextView) view.findViewById(R.id.tv_dev_state_value);
		tv_work_model = (TextView) view.findViewById(R.id.tv_work_model);
		tv_fault = (TextView) view.findViewById(R.id.tv_fault);
		sv_fault = (ScrollView) view.findViewById(R.id.sv_fault);
		tv_fl_weight_state_value = (TextView) view.findViewById(R.id.tv_fl_weight_state_value);
		tv_temperature_value = (TextView) view.findViewById(R.id.tv_temperature_value);
		ll_fl_cl_data = (LinearLayout) view.findViewById(R.id.ll_fl_cl_data);
		tv_target_cl_count_value = (TextView) view.findViewById(R.id.tv_target_cl_count_value);
		tv_cl_count_now_value = (TextView) view.findViewById(R.id.tv_cl_count_now_value);
		tv_is_over_value = (TextView) view.findViewById(R.id.tv_is_over_value);
		ll_yxzt = (LinearLayout) view.findViewById(R.id.ll_yxzt);
		ll_yxzt.setVisibility(View.GONE);
		view_separstor_8 = view.findViewById(R.id.view_separstor_8);
		view_separstor_8.setVisibility(View.GONE);
	}
	
	@Override
	public void onResume() {
		MessageReceiver.ehList.add(this);
		PAUSE_TAG = IndexActivity.cdz_isRunning;
		falutSb.delete(0, falutSb.length());
		cleanData();
		if (YzzsApplication.isConnected) {
			sendRefreshFalutCMD();
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
		case R.id.tv_set:
			if (PAUSE_TAG) {
				toMmyzFragment();
			} else {
				ToastMsg(getActivity(), "当前设备暂停,请恢复再进行调试");
			}
			break;
		case R.id.tv_pause:
			if(YzzsApplication.isConnected) {
				if (IndexActivity.cdz_work_isDebuging) {
					ToastMsg(getActivity(), "当前处于调试模式,请退出调试模式");
					return;
				}
				sendRunOrPauseCMD(PAUSE_TAG);
				isTimeOut = true;
				startTime(XtAppConstant.SEND_CMD_TIMEOUT, "设置");
			} else {
				ToastMsg(getActivity(), getString(R.string.bluetooth_disconnected));
			}
			break;
		case R.id.iv_refresh_fault:
			if(YzzsApplication.isConnected) {
				sendRefreshFalutCMD();
			} else {
				ToastMsg(getActivity(), getString(R.string.bluetooth_disconnected));
			}
			break;
		default:
			break;
		}
		super.onClick(v);
	}
	
	/**
	 * 接收包
	 * @param message
	 */
	private void receivePack (byte[] message) {
		if (message.length < 6) {
			return;
		}
		if(message[0] == CdzCMDConstant.CDZ_SBBZ) {//测定站
			int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
			try {
				switch (cmd) {
				case CdzCMDConstant.CDZ_TZ://体重
					tzDataParsing(message);
					break;
				case CdzCMDConstant.CDZ_RFID://耳标
					RFIDDataParsing(message);
					break;
				case CdzCMDConstant.CDZ_TW://体温
					twDataParsing(message);
					break;
				case CdzCMDConstant.CDZ_ZT://暂停状态
					ztDataParsing(message);
					break;
				case CdzCMDConstant.CDZ_HF://恢复状态
					hfDataParsing(message);
					break;
				case CdzCMDConstant.CDZ_GZZT://设备故障状态
					gzztDataParsing(message);
					break;
				case CdzCMDConstant.CDZ_FEED_NUB://采食量
					feedDataParsing(message);
					break;
				case CdzCMDConstant.CDZ_SENSOR_GZ://传感器故障
					getSensorsGz(message);//传感器故障
					showFalutText();
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
	 * 采食量
	 * @param message
	 */
	private void feedDataParsing(byte[] message) {
		if(message.length > 11) {
			isTimeOut = false;
			dismissLoading();
			Logger.d("埰食量",Arrays.toString(message));
			//0 0  6 0 （正为0.负为1） 11111
			if (message [5] == 0 && message [6] == 0) {//读取采食量成功
				StringBuilder sb = new StringBuilder();
				int symbol = message[8];
				for (int i = 9; i < message [7] + 8 ; i++) {
					sb.append(message[i]);
				}
				String data = String.valueOf(Double.parseDouble(sb.toString())/1000);
				if (symbol == 1) {//如果是负的，怎加个“-”
					data = "-" + data;
				}
				tv_work_model.setText("采食量:" + "\n" + data + "Kg");
			} else {
				tv_work_model.setText("采食量读取失败");
			}
		}
	}

	/**
	 * 显示故障
	 */
	private void showFalutText() {
		sv_fault.post(new Runnable() {
			public void run() {
				sv_fault.fullScroll(ScrollView.FOCUS_FORWARD);
			}
		});
		dismissLoading();
		isTimeOut = false;
		if (CommonUtil.isEmpty(falutSb.toString())) {
			tv_fault.setText("当前无故障");
		} else {
			tv_fault.setText(falutSb.toString()); 
		}
		iv_refresh_fault.clearAnimation();
	}

	/**
	 * 传感器故障
	 * @param message
	 */
	private void getSensorsGz(byte[] message) {
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		if(cmd == CdzCMDConstant.CDZ_SENSOR_GZ) {//傳感器故障
			if (message.length > 6) {
				if (message [5] !=0) {//接受失败
					return;
				}
				StringBuilder mStringBuilder = new StringBuilder();
				message = Arrays.copyOfRange(message, 6, message.length);//去除6位包头
				//开始解析
				String [] cgqGz = getActivity().getResources().getStringArray(R.array.sensor_guzhang_array);
				for (int i = 0;i < message[0];i ++) {//绑定了几个传感器
					if (message[i + 1] != 1) {
						if(message[i + 1] < cgqGz.length) {
							if (i == 0) {
								mStringBuilder.append((i + 1)).append("号传感器故障");
							} else {
								if (mStringBuilder.length() ==0) {
									mStringBuilder.append((i + 1)).append("号传感器故障");
								} else {
									mStringBuilder.append("\n").append((i + 1)).append("号传感器故障");
								}
							}
						}
					}
				}
				if (mStringBuilder.length() != 0) {
					falutSb.append(mStringBuilder);
				}
			}
		}
	}

	/**
	 * 工作模式
	 * @param message
	 */
	private void gzmsDataParsing(byte[] message) {
		if (message.length < 8) {
			ToastMsg(getActivity(), "格式错误");
			return;
		}
		if (message [6] ==0) {
			String [] work_model = this.getResources().getStringArray(R.array.shebei_work_model_item_array);
			work_model_location = message[7];
			if (work_model_location> 10) {
				tv_work_model.setText("数据出错");
			} else {
				String work_model_infor = work_model [work_model_location - 1];
				tv_work_model.setText(work_model_infor);
				if (!(work_model_location == 4 || work_model_location == 5
						|| work_model_location == 7 ||  work_model_location == 8 || work_model_location == 9)) {
					// 4:体重分栏 ，5:体重百分百分栏，7:最重体重分栏 8:最轻体重分栏 9:RFID出栏
					ll_fl_cl_data.setVisibility(View.GONE);
				} else {
					ll_fl_cl_data.setVisibility(View.VISIBLE);
				}
			}
		} else {
			tv_work_model.setText("工作模式数据接收失败");
		}
	}

	/**
	 * 恢复
	 * @param message
	 */
	private void hfDataParsing(byte[] message) {
		if (message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if(message[6] == 0) {
				tv_pause.setText(getString(R.string.flz_state_pause));
				PAUSE_TAG = true;
				IndexActivity.cdz_isRunning = true;
				ToastMsg(getActivity(), "已恢复运行状态");
			} else {
				ToastMsg(getActivity(), "运行失败");
			}
		}
	}
	
	/**
	 * 暂停
	 * @param message
	 */
	private void ztDataParsing(byte[] message) {
		if (message[5] == 0) {
			dismissLoading();
			isTimeOut = false;
			if(message[6] == 0) {
				tv_pause.setText(getString(R.string.flz_state_run));
				PAUSE_TAG = false;
				IndexActivity.cdz_isRunning = false;
				ToastMsg(getActivity(), "暂停成功");
			} else {
				ToastMsg(getActivity(), "暂停失败,当前设备有故障或者动作正在进行");
			}
		}
	}
	
	/**
	 * 故障信息
	 * @param message
	 */
	private void gzztDataParsing(byte[] message) {
		falutSb.delete(0, falutSb.length());
		if (message.length < 10) {
			tv_fault.setText("故障数据异常");
			return;
		}
		if (message [5] != 0) {
			tv_fault.setText("故障数据接受异常");
			return;
		}
		if (message [6] != 0) {
			tv_fault.setText("获取故障数据");
			return;
		}
		String[] faultData = getActivity().getResources().getStringArray(R.array.flz_guzhang_zhuangtai_item_array);
		StringBuilder faultDataSb = new StringBuilder();
		String dataBottom;
		int min = message[7] < faultData.length ? message[7] : faultData.length;//取两者中小的
		for (int i = 0; i < min; i++) {
			if (message[8 + i] == 1) {
				if (i == faultData.length -1 ) {
					dataBottom = "";
				} else {
					dataBottom = "\n";
				}
				faultDataSb.append(faultData [i]).append(dataBottom);
			}
		}
		falutSb.append(faultDataSb);
	}
	
	/**
	 * 体温
	 * @param message
	 */
	private void twDataParsing(byte[] message) {

	}
	
	/**
	 * 体重
	 * @param message
	 */
	private void tzDataParsing(byte[] message) {
		if(message.length < 11) {
			tv_weight_value.setText("体重数据异常");
		} else {
			if (message[5] == 0 && message[6] == 0) {
				StringBuilder dataSb = new StringBuilder();
				//0 0  6 0 （正为0.负为1） 11111
				int symbol = message[8];
				for (int i = 9; i < 8 + message[7]; i++) {//5位体重
					dataSb.append(message[i]);
				}
				String data  = String.valueOf(Integer.parseInt(dataSb.toString().substring(0,3)));
				if (symbol == 1) {//如果是负的，怎加个“-”
					data = "-" + data;
				}
				tv_weight_value.setText(data);
				tv_weight_value.setTextSize(46);
				tv_weight_value.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
				tv_weight_value2.setText(dataSb.toString().substring(3,5));
				tv_weight_value2.setVisibility(View.VISIBLE);
				tv_weight_point.setVisibility(View.VISIBLE);
				tv_weight_unit.setVisibility(View.VISIBLE);
			} else {
				tv_weight_value.setText("体重数据接收异常");
				tv_weight_value.setTextSize(24);
				tv_weight_value.setGravity(Gravity.CENTER);
				tv_weight_value2.setVisibility(View.GONE);
				tv_weight_point.setVisibility(View.GONE);
				tv_weight_unit.setVisibility(View.GONE);
			}
			cleanData();
		}
	}
	
	/**
	 * 初始化数据
	 */
	private void cleanData () {
		tv_rfid_value.setText("");
		tv_temperature_value.setText("");
		tv_fl_weight_state_value.setText("");
		tv_target_cl_count_value.setText("");
		tv_cl_count_now_value.setText("");
		tv_is_over_value.setText("");
		tv_dev_state_value.setText("");
	}
	
	/**
	 * RFID
	 * @param message
	 */
	private void RFIDDataParsing(byte[] message) {
		if(message.length < 11) {
			tv_rfid_value.setText("RIFD数据异常");
		} else {
			if (message[5] == 0) {
				isTimeOut = false;
				dismissLoading();
				if (message[6] == 0) { 
					StringBuilder dataSb = new StringBuilder();
					int RFIDLenth = message[7];//RFID长度
					int tempLenthLocation = message[7] + 8;//体温长度位置
					for (int i = 0; i < RFIDLenth; i++) {//20位RFID
						dataSb.append(message[i + 8]);
					}
					tv_rfid_value.setText(dataSb.toString());
					dataSb.delete(0, dataSb.length());
					int tempLenth = message [tempLenthLocation];
					for (int i = 0; i < tempLenth; i++) {
						dataSb.append(message[tempLenthLocation + i + 1]);
					}
					if (dataSb.length() == 0 || dataSb.length() !=6) {
						tv_temperature_value.setText("");
					} else {
						String symbol = dataSb.substring(0,1);
						String noSymbol = dataSb.delete(0, 1).toString();
 						int IntPart = Integer.valueOf(noSymbol.substring(0,3));//整数部分
						String DeciPart = noSymbol.substring(3,5);//小数部分
						StringBuilder sb = new StringBuilder();
						if (symbol.equals("1")) {
							//正数
							sb.append(IntPart).append(".").append(DeciPart);
						} else {
							sb.append("-").append(IntPart).append(".").append(DeciPart);
						}
						tv_temperature_value.setText(sb.toString());
					}
				} else {
					tv_rfid_value.setText("RIFD-体温数据读取失败");
				}
			} else {
				tv_rfid_value.setText("RIFD-体温数据接收异常");
			}
		}
	}
	/**
	 * 运行状态
	 * @param message
	 */
	private void yxztDataParsing(byte[] message) {
		if (message[5] == 0) {
			String [] data = getActivity().getResources().getStringArray(R.array.shebei_zhuangtai_item_array);
			if(message.length < 9) {
				if(message[6] == 0 && message[7] < data.length + 1) {
					tv_dev_state_value.setText(data[message[7] - 1]);
				} else {
					tv_dev_state_value.setText("运行状态数据不合法");
				}
			} else {
				tv_dev_state_value.setText("运行状态数据异常");
			}
		} else {
			tv_dev_state_value.setText("运行状态数据接收失败");
		}
	}

	/**
	 * 跳转到密码验证界面
	 */
	private void toMmyzFragment() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fragment_container, new CdzMmyzFragment());
		ft.addToBackStack(null);
		ft.commit();
	}

	/**
	 * 发送暂停或者运行命令
	 * @param isRunnig
	 */
	private void sendRunOrPauseCMD(boolean isRunnig) {
		showLoading();
		int cmd = isRunnig ? CdzCMDConstant.CDZ_ZT : CdzCMDConstant.CDZ_HF;
		sendGetBaseCMD(CdzCMDConstant.CDZ_SBBZ, cmd);
	}

	/**
	 * 发送刷新故障命令
	 */
	private void sendRefreshFalutCMD() {
		if(iv_refresh_fault.getAnimation() !=null && iv_refresh_fault.getAnimation().hasStarted()) {
			iv_refresh_fault.clearAnimation();
			isTimeOut = false;
			cancelTime();
		} else {
			iv_refresh_fault.startAnimation(operatingAnim);
			sendGetBaseCMD(CdzCMDConstant.CDZ_SBBZ, CdzCMDConstant.CDZ_GZZT);
			sendGetBaseCMD(CdzCMDConstant.CDZ_SBBZ, CdzCMDConstant.CDZ_SENSOR_GZ);
			isTimeOut = true;
			startTime(XtAppConstant.SEND_CMD_TIMEOUT, "故障刷新");
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
			if(iv_refresh_fault.getAnimation() !=null && iv_refresh_fault.getAnimation().hasStarted()) {
				iv_refresh_fault.clearAnimation();
			}
			ToastMsg(getActivity(), content + getString(R.string.connect_time_out));
		}
	}
}
