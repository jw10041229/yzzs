package com.huimv.yzzs.fragment.hk;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.BluetoothScanActivity;
import com.huimv.yzzs.activity.IndexActivity;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.chart.HkAqChart;
import com.huimv.yzzs.chart.HkPhChart;
import com.huimv.yzzs.chart.HkSdChart;
import com.huimv.yzzs.chart.HkWdChart;
import com.huimv.yzzs.constant.GeneralCMDConstant;
import com.huimv.yzzs.constant.HkCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.support.hk.HkSshjxsFragmentSupport;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.ToolUnit;
import com.huimv.yzzs.util.YzzsCommonUtil;

import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.model.SeriesSelection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/**
 * 实时环境显示
 * @author jiangwei
 *
 */
@SuppressLint({ "UseSparseArrays", "InflateParams" })
public class HkSshjxsFragment extends YzzsBaseFragment implements EventHandler,OnClickListener,ConnectTimeoutListener{
	private final StringBuilder yxztStr = new StringBuilder();
	private RelativeLayout blueToothStateRL;
	private Button setBtn;
	private ImageView bluetoothSearchIv;
	private ScrollView yxzt_sv;
	private SharePreferenceUtil mSpUtil;
	private RelativeLayout chartRL;
	private PopupWindow mUpLeftPopupTip,mUpRightPopupTip;//提示信息pop(Left/Right)
	private View mUpLeftTipView,mUpRightTipView;//提示信息PopView
	private int mPopTipsWidth;//pop提示窗口宽度
	private int mPopTipsHeight;//pop提示窗口高度
	private TextView tv_tips_ul,tv_tips_ur;//提示信息Textview
	private TextView hkgzTv;//故障
	private LineChart xychart;
	private ImageView iv_refresh_fault;
	private GraphicalView mChartView;
	private HorizontalScrollView hs_btn;
	private LinearLayout cgqBtnLL,ll_bottom_tab;
	private boolean isTimeOut = true;
	public static int currentDw = 1;
	private StringBuilder sb = new StringBuilder();
	private StringBuilder sbDw = new StringBuilder();
	private String identity = "2";// 区分身份
	private final String MAX = HkCMDConstant.MAX;
	private final String MIN = HkCMDConstant.MIN;
	private final String DW = HkCMDConstant.DW;
	public static int sbNumS = 7;
	/**
	 * 图片旋转
	 */
	private Animation operatingAnim;
	private int CGQ_LX [];//传感器顺序
	private int[] Hk_tabBg_normal = XtAppConstant.Hk_tabBg_normal;
	private int[] Hk_tabBg_pressed = XtAppConstant.Hk_tabBg_pressed;
	private Map<Integer, Button> btnMap = new HashMap<>();// BtnMap
	private Map<Integer, ImageView> tabIVMap = new HashMap<>();// ImageButtonMap
	private int WENDU = HkCMDConstant.WENDU_TAG;//温度1
	private int SHIDU = HkCMDConstant.SHIDU_TAG;//湿度2
	private int ANQI = HkCMDConstant.ANQI_TAG;//氨气3
	private int PH = HkCMDConstant.PH_TAG;//PH4
	
	private int WENDU_CGQ = Integer.valueOf(XtAppConstant.CGQBD_SBLX_WD);//温度传感器0
	private int SHIDU_CGQ = Integer.valueOf(XtAppConstant.CGQBD_SBLX_SD);//湿度传感器1
	private int ANQI_CGQ = Integer.valueOf(XtAppConstant.CGQBD_SBLX_AQ);//氨气
	private int PH_CGQ = Integer.valueOf(XtAppConstant.CGQBD_SBLX_PH);//PH传感器4
	
	private int whichChart = WENDU;//用来区分哪个图表，1:温度,2,湿度,3:氨气
	private int whichLine = 1;//哪个统计图的哪个条线
	private HkSshjxsFragmentSupport mHk_sshjxsFragmentSupport = new HkSshjxsFragmentSupport();
	private ImageView iv_retore;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_hk_sshjxs_fragment, null);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		initView(view);
		initData();
		return view;
	}
	
	private void initData() {
		setConnectTimeoutListener(this);
	}

	private void initView(View view) {
		deleteAllWdSdAq();
		iv_retore = (ImageView) view.findViewById(R.id.iv_retore);
		hs_btn = (HorizontalScrollView)view.findViewById(R.id.hs_btn);
		yxzt_sv = (ScrollView) view.findViewById(R.id.yxzt_sv);
		cgqBtnLL = (LinearLayout) view.findViewById(R.id.cgqBtnLL);
		ll_bottom_tab = (LinearLayout) view.findViewById(R.id.ll_bottom_tab);
		bluetoothSearchIv = (ImageView) view.findViewById(R.id.bluetoothSearchIv);
		blueToothStateRL = (RelativeLayout) view.findViewById(R.id.blueToothStateRL);
		hkgzTv = (TextView) view.findViewById(R.id.hkgzTv);
		chartRL = (RelativeLayout) view.findViewById(R.id.chartRL);
		iv_refresh_fault = (ImageView) view.findViewById(R.id.iv_refresh_fault);
		iv_refresh_fault.setOnClickListener(this);
		operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.img_rotate);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		setBtn = (Button) view.findViewById(R.id.setBtn);
		bluetoothSearchIv.setOnClickListener(this);
		iv_retore.setOnClickListener(this);
		setBtn.setOnClickListener(this);
		//提示信息窗口宽高
		mPopTipsWidth = ToolUnit.dipTopx(100);
		mPopTipsHeight = ToolUnit.dipTopx(60);
		//pop提示信息
		mUpLeftTipView = LayoutInflater.from(getActivity()).inflate(R.layout.chat_tips_up_left, null);
		tv_tips_ul = (TextView)mUpLeftTipView.findViewById(R.id.tv_tips);
		mUpRightTipView = LayoutInflater.from(getActivity()).inflate(R.layout.chat_tips_up_right, null);
		tv_tips_ur = (TextView)mUpRightTipView.findViewById(R.id.tv_tips);
	}
	
	/**
	 * 删除数据库数据
	 */
	private void deleteAllWdSdAq() {
		mHk_sshjxsFragmentSupport.deleteAllWendu(getActivity());
		mHk_sshjxsFragmentSupport.deleteAllShidu(getActivity());
		mHk_sshjxsFragmentSupport.deleteAllAnqi(getActivity());
		mHk_sshjxsFragmentSupport.deleteAllPh(getActivity());
	}
	/**
	 * 设置TopBtn的背景
	 * @param i
	 */
	private void setTopBtnBg(int i) {
		for (int j = 1; j <= btnMap.size(); j++) {
			if (i == j) {
				btnMap.get(j-1).setBackground(getResources().getDrawable(R.drawable.light_blue_bg));
			} else {
				btnMap.get(j-1).setBackground(getResources().getDrawable(R.drawable.light_gray_bg));
			}
		}
	}
	/**
	 * 设置温度，湿度，氨气topBtn的隐藏于显示
	 * @param type
	 */
	private void setTopBtnIsShow(int type) {
		for (int i = 0; i < btnMap.size(); i++) {//先全部隐藏
			btnMap.get(i).setVisibility(View.GONE);
		}
		for (int i = 0; i < CGQ_LX.length; i++) {
			if (type == WENDU) {//温度
				if (CGQ_LX[i] == WENDU_CGQ) {
					btnMap.get(i).setVisibility(View.VISIBLE);
				}
			}
			if (type == SHIDU) {//湿度
				if (CGQ_LX[i] == SHIDU_CGQ) {
					btnMap.get(i).setVisibility(View.VISIBLE);
				}
			}
			if (type == ANQI) {//氨气
				if (CGQ_LX[i] == ANQI_CGQ) {
					btnMap.get(i).setVisibility(View.VISIBLE);
				}
			}
			if (type == PH) {//氨气
				if (CGQ_LX[i] == PH_CGQ) {
					btnMap.get(i).setVisibility(View.VISIBLE);
				}
			}
		}
	}

	/**
	 * 得到图表的数据
	 * @param v
	 */
	private void getCharData(int whichChart,View v,LineChart xychart,GraphicalView mChartView) {
		GraphicalView gv = (GraphicalView) v;
		//将view转换为可以监听的GraphicalView
		SeriesSelection ss = gv.getCurrentSeriesAndPoint();
		//获得被点击的系列和点
		if (ss == null) return ;
		int whichPoint = ss.getPointIndex();//哪个点
		double[] point = new double[]{ss.getXValue(),ss.getValue()};
		//获得当前被点击点的X位置和Y数值
		final double[] dest = xychart.toScreenPoint(point);
		int x = (int)dest[0];
		int y =  (int)dest[1];
		//获取当前控件距离顶部的坐标-高度
		int[] location = new int[2];  
        v.getLocationOnScreen(location);

        //先关闭泡泡窗口
        dismissPopupWindow();
        
		//左侧宽度不够显示pop，则显示右侧
        String wsdTag = "温度:";
        String xStr = "";
        if (whichChart == WENDU) {
        	wsdTag = "温度:";
        	xStr = HkWdChart.x[whichPoint];
        } 
        if(whichChart == SHIDU) {
        	wsdTag = "湿度:";
        	xStr = HkSdChart.x[whichPoint];
		}
        if(whichChart == ANQI) {
        	wsdTag = "氨气:";
        	xStr = HkAqChart.x[whichPoint];
		}
        if(whichChart == PH) {
        	wsdTag = "PH:";
        	xStr = HkPhChart.x[whichPoint];
		}
		if(getDisplayMetrics().widthPixels - x < mPopTipsWidth ){
			tv_tips_ur.setText("时间:" + xStr +"\n"+ wsdTag + point[1]);
			//右上方
			popwindowR( mUpRightTipView,x-mPopTipsWidth + 1,y+location[1]-mPopTipsHeight);
		}else{
			tv_tips_ul.setText("时间:" + xStr +"\n"+ wsdTag + point[1]);
			//左上方--OK精准点
			popwindowL(mUpLeftTipView, x ,y+location[1]-mPopTipsHeight);
		}
	}
	/*
	 * 温度,湿度，氨气第一个传感器的序号
	 * 当做点击三者进入的默认显示
	 * @return
	 */
	private int initShowData (int type) {
		int m = 0;
		for (int i =0;i < CGQ_LX.length; i++) {
			if(type == WENDU && CGQ_LX[i] == WENDU_CGQ){//温度
				m = i + 1;
				break;
			}
			if(type == SHIDU && CGQ_LX[i] == SHIDU_CGQ){//湿度
				m = i + 1;
				break;
			}
			if(type ==ANQI && CGQ_LX[i] == ANQI_CGQ){//氨气
				m = i + 1;
				break;
			}
			if(type ==PH && CGQ_LX[i] == PH_CGQ){//PH
				m = i + 1;
				break;
			}
		}
		return m;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setBtn:
			String password = mSpUtil.getHkPassword();
			if (CommonUtil.isNotEmpty(password)) {
				if(YzzsApplication.isConnected) {
					isTimeOut = true;
					startTime(HkCMDConstant.LOGIN_TIME, "获取数据");
					sendLoginCMD();
				} else {
					ToastMsg(getActivity(), getString(R.string.disconnected));
				}
			} else {
                toHkMmyzFragment();
			}
			break;
		case R.id.bluetoothSearchIv:
			toBluetoothScan();
			break;
		case R.id.iv_refresh_fault:
			if(YzzsApplication.isConnected) {
				showLoading("正在刷新运行状态...");
				refreshGzEx();
				startTime(1000, "故障刷新");
				isTimeOut = true;
				iv_refresh_fault.startAnimation(operatingAnim);
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		case R.id.iv_retore:
			restoreDialog();
			break;
		}
		super.onClick(v);
	}
	
	/**
	 * 发送密码验证命令
	 */
	private void sendLoginCMD() {
		showLoading("正在获取数据...");
		int lenthMmsrEt = mSpUtil.getHkPassword().length();
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
					.intTobyte(Integer.parseInt(mSpUtil.getHkPassword().substring(i - 8, i - 7)));
		}
		byteData[14] = 1;//校验位
		byteData[15] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);//e
		byteData[16] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//n
		byteData[17] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
		sendUnpackData(byteData,XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}
	
	private void toHkMmyzFragment() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fragment_container, new HkMmyzFragment());
		ft.addToBackStack(null);
		ft.commit();
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
			// 1 0 7(命令) 0 x(长度) 0 3  00 01  00 02 00 02 00 02 00 02（五档的档位温度） 11(单个循环体长度)  00 01 00 02 sn lx(两个字节) sx(两个字节) 
			int lenth = message[27];//每个循坏体的长度 9
			YzzsApplication.hasBpfj(lenth,mSpUtil);
			int sbNum = message[6];//几个设备
			sbNumS = sbNum;//有几个设备
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
				
				int wdlow1 = YzzsCommonUtil.ChangeByteToPositiveNumber(dwWdData[(i -1) * 4]) ;// 档位温度下限1
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
							sendRestoreCMDEx(GeneralCMDConstant.RESTORE_FACTORY_SET);
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
	
	/**
	 * 发送恢复出厂设置命令
	 * @param initCMD
	 */
	private void sendRestoreCMDEx (int initCMD) {
		showLoading("正在恢复出厂设置");
		int lenth = 12;
		final byte [] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);//h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);//m
		byteData[2] = 1;//设备区分标志
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
	
	private void setInitChangeData(final int tpye){
		//加个延迟是为了防止图表控件残留
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				setTabSelection(tpye);
				whichChart = tpye;
				whichLine = initShowData(tpye);
				setWhichChart(tpye);
				setTopBtnIsShow(tpye);
				setTopBtnBg(whichLine);
				chartRL.removeAllViews();
				chartRL.addView(mChartView);
			}
		}, 1);
	}
	/**
	 * 返回首页
	 */
	private void toBluetoothScan() {
		final Intent intent = new Intent(getActivity(), BluetoothScanActivity.class);
		startActivity(intent);
		getActivity().finish();
	}
	
	/**
	 * 发送刷新故障
	 */
	private void refreshGzEx() {
		int lenth = 12;
		int cmd = HkCMDConstant.GET_YXZT;
		final byte [] byteData = new byte[lenth];
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
		sendUnpackData(byteData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}
	/**
	 * 哪个统计图
	 * @param whichChart
	 */
	private void setWhichChart(final int whichChart) {
		//先关闭泡泡窗口
		dismissPopupWindow();
		if (whichChart == WENDU) {//温度
			xychart = new HkWdChart(whichLine).execute(getActivity());
		}
		if (whichChart == SHIDU) { //湿度
			xychart = new HkSdChart(whichLine).execute(getActivity());
		}
		if (whichChart == ANQI) { //氨气
			xychart = new HkAqChart(whichLine).execute(getActivity());
		}
		if (whichChart == PH) { //PH
			xychart = new HkPhChart(whichLine).execute(getActivity());
		}
		mChartView = new GraphicalView(getActivity(), xychart);
		chartRL.addView(mChartView);
		mChartView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getCharData(whichChart,view, xychart, mChartView);
			}
		});
	}
	
	@Override
	public void onPause() {
		cancelTime();
		MessageReceiver.ehList.remove(this);
		super.onPause();
	}
	
	@Override
	public void onResume() {
		MessageReceiver.ehList.add(this);
		yxztStr.delete(0, yxztStr.length());
		if(YzzsApplication.isConnected) {
			refreshGzEx();
			doReadGear();
		}
		CGQ_LX = IndexActivity.CGQ_LX;
		addTopBtn();
		addTabIv();
		hasSensor();
		if (CGQ_LX.length > 0) {
			setInitChangeData(changeSensorType(CGQ_LX [0]));
		} else {
			setInitChangeData(WENDU);
		}
		super.onResume();
	}
	
	private void doReadGear() {
		sendGetBaseCMD(HkCMDConstant.HK_SBBZ, HkCMDConstant.READ_DEFALUT_GEAR);
	}

	/**
	 * 传感器类型转换
	 * @param firstType    
	 * @return
	 */
	private int changeSensorType (int firstType) {
		int finalType;
		switch (firstType) {
		case 0://温度
			finalType = WENDU;
			break;
		case 1://湿度
			finalType = SHIDU;
			break;
		case 2://氨气
			finalType = ANQI;
			break;
		case 4://PH
			finalType = PH;
			break;
		default:
			finalType = WENDU;
			break;
		}
		return finalType;
	}
	/**
	 * 动态添加传感器类型Tab
	 */
	private void addTabIv() {
		ll_bottom_tab.removeAllViewsInLayout();
		int cgq_lx = HkCMDConstant.CGQ_DATA_LX;
		for (int i = 0; i < cgq_lx; i++) {//支持几种传感器数据类型
			addImageButtonTab(i,cgq_lx);
		}
	}

	private void addImageButtonTab(final int i,int cgq_lx) {
		ImageView IbBtn = new ImageView(getActivity());
		tabIVMap.put(i, IbBtn);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
				(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
		if (cgq_lx <= 5) {//tab 显示项
			layoutParams.weight = 1;
			layoutParams.setMargins(30,20,30,20);
		} else {
			layoutParams.setMargins(100,0,100,0);
		}
		IbBtn.setLayoutParams(layoutParams);
		IbBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setInitChangeData(i +1);
			}
		});
		ll_bottom_tab.addView(IbBtn);
	}

	/**
	 * 动态添加传感器top
	 */
	private void addTopBtn() {
		cgqBtnLL.removeAllViewsInLayout();
		for (int i = 0; i < CGQ_LX.length; i++) {
			// 循环遍历给TextView设置文字和属性
			addTextTab(i);
		}
	}

	private void addTextTab(final int i) {
		Button btn = new Button(getActivity());
		btn.setVisibility(View.GONE);
		btnMap.put(i, btn);
		btn.setText(i+1 + "号");
		btn.setFocusable(true);
		btn.setGravity(Gravity.CENTER);
		btn.setSingleLine();
		btn.setPadding(5, 5, 5,5);
		//btn.setBackgroundColor(getResources().getColor(R.color.gray_light2));
		btn.setBackground(getResources().getDrawable(R.drawable.light_blue_bg));
		btn.setTextSize(18);
		btn.setTextColor(getResources().getColor(R.color.white));
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
				(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(10,0,10,0);
		btn.setLayoutParams(layoutParams);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				whichLine = i + 1;
				chartRL.removeAllViews();
				chartRL.removeAllViewsInLayout();
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						setWhichChart(whichChart);
					}
				}, 1 );
				setTopBtnBg(i +1);
			}
		});
		cgqBtnLL.addView(btn);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	/**
	 * 有没有绑定相应的传感器,有:显示相应的TAB,无:隐藏
	 */
	private void hasSensor() {
		if (CGQ_LX.length == 0) {
			hs_btn.setVisibility(View.GONE);
		} else {
			hs_btn.setVisibility(View.VISIBLE);
			for (int i = 0; i < tabIVMap.size(); i++) {//先全部隐藏
				tabIVMap.get(i).setVisibility(View.GONE);
			}
			for (int cgq_lx_value:CGQ_LX ) {
				if (cgq_lx_value == WENDU_CGQ) {//温度
					tabIVMap.get(0).setVisibility(View.VISIBLE);
				}
				if (cgq_lx_value == SHIDU_CGQ) {//湿度
					tabIVMap.get(1).setVisibility(View.VISIBLE);
				}
				if (cgq_lx_value == ANQI_CGQ) {//氨气
					tabIVMap.get(2).setVisibility(View.VISIBLE);
				}
				if (cgq_lx_value == PH_CGQ) {//PH
					tabIVMap.get(3).setVisibility(View.VISIBLE);
				}
			}
		}
	}
	
	private DisplayMetrics getDisplayMetrics() {
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		WindowManager windowMgr = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		windowMgr.getDefaultDisplay().getMetrics(mDisplayMetrics);
		return mDisplayMetrics;
	}
	
    /**
     * 弹出Pop窗口
     * @param popView pop窗口界面
     * @param xoff 窗口X偏移量
     * @param yoff 窗口Y偏移量
     */
    private   PopupWindow popwindowL(View popView,int xoff,int yoff){
		mUpLeftPopupTip = new PopupWindow(popView,mPopTipsWidth, mPopTipsHeight,true);
		mUpLeftPopupTip.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    	mUpLeftPopupTip.setOutsideTouchable(false);
    	
    	if (mUpLeftPopupTip.isShowing()) {
    		mUpLeftPopupTip.update(xoff,yoff,mPopTipsWidth, mPopTipsHeight);
		} else {
			mUpLeftPopupTip.showAtLocation(mChartView,Gravity.NO_GRAVITY, xoff,yoff);
		}	
    	
        return mUpLeftPopupTip;
    }
    
    /**
     * 弹出Pop窗口
     * @param popView pop窗口界面
     * @param xoff 窗口X偏移量
     * @param yoff 窗口Y偏移量
     */
    private PopupWindow popwindowR(View popView,int xoff,int yoff){
		mUpRightPopupTip = new PopupWindow(popView, mPopTipsWidth, mPopTipsHeight,true);
		mUpRightPopupTip.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		mUpRightPopupTip.setOutsideTouchable(false);
    	
    	if (mUpRightPopupTip.isShowing()) {
    		mUpRightPopupTip.update(xoff,yoff,mPopTipsWidth, mPopTipsHeight);
		} else {
			mUpRightPopupTip.showAtLocation(mChartView,Gravity.NO_GRAVITY, xoff,yoff);
		}	
    	
        return mUpRightPopupTip;
    }

    /**
     * 关闭泡泡窗口
     */
	private void dismissPopupWindow() {
		if (mUpRightPopupTip != null) {
			if (mUpRightPopupTip.isShowing()) {
				mUpRightPopupTip.dismiss();
			}
			mUpRightPopupTip = null;
		}
		if (mUpLeftPopupTip != null) {
			if (mUpLeftPopupTip.isShowing()) {
				mUpLeftPopupTip.dismiss();
			}
			mUpLeftPopupTip = null;
		}
	}
	@Override
	public void onConnected(boolean isConnected) {
		YzzsApplication.isConnected = isConnected;
		if (!isConnected) {//如果断开
			ToastMsg(getActivity(), "蓝牙连接已断开");
			blueToothStateRL.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onReady(boolean isReady) {
		if (isReady) {
			blueToothStateRL.setVisibility(View.GONE);
		}
	}
	
	
	@Override
	public void onNetChange(boolean isNetConnected) {
	}
	
	/**
	 * 切换Tab,更换背景
	 * @param index
	 */
	private void setTabSelection(int index) {
		resetBtn();
		tabIVMap.get(index -1).setImageResource(Hk_tabBg_pressed[index -1]);
	}
	
	/**
	 * 重置tab背景
	 */
	private void resetBtn() {
		for (int i = 0; i < tabIVMap.size(); i++) {
			tabIVMap.get(i).setImageResource(Hk_tabBg_normal[i]);
		}
	}
	
	@Override
	public void onMessage(byte[] message) {
		receivePack(message);
	}
	/**
	 * 读取默认配置档位
	 * @param message
	 */
	private void getReadGearResult(byte[] message) {
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		if (cmd == HkCMDConstant.READ_DEFALUT_GEAR) {
			if(message[5] == 0) {
				dismissLoading();
				if(message[6] == 0) {
					if (message[8] == 0) {
					} else {
						mSpUtil.setHkMrdw(message[8] + "");
					}
				} else {
				}
			}
		}
	}
	/**
	 * 接收包
	 * @param message
	 */
	private void receivePack (byte[] message) {
		if(message[0] == HkCMDConstant.HK_SBBZ) {//环控
			try {
				getReadGearResult(message);
				getRefrehRealDataEx(message);
				getYxztEx(message);
				restoreEx(message);
				getMmyzPack(message);
				getSbpzEx(message);
			} catch (Exception e) {
				dismissLoading();
				hkgzTv.setText("数据格式错误");
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
					sendGetSbpzCMDEx(HkCMDConstant.GET_SBPZ);
				} else {
					dismissLoading();
					ToastMsg(getActivity(), "密码错误");
					toHkMmyzFragment();
					isTimeOut = false;
				}
			}
		}
	}
	
	/**
	 * 发送获取设备配置命令
	 * @param cmd
	 */
	private void sendGetSbpzCMDEx (int cmd) {
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
	 * 恢复出厂设置
	 * 
	 * @param message
	 */
	private void restoreEx(byte[] message) {
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
	
	/**
	 * 运行状态 拼包
	 * @param message
	 */
	private void getYxztEx(byte[] message) {
		getYxztDwgz(message);//档位故障
		getYxztCgqgz(message);//传感器故障
		getYxztSbgz(message);//设备故障
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		if (cmd == HkCMDConstant.GET_YXZT_DWGZ
				|| cmd == HkCMDConstant.GET_YXZT_CGQGZ
				|| cmd == HkCMDConstant.GET_YXZT_SBGZ) 
		{//如果是这三个,则刷新
			yxzt_sv.post(new Runnable() {
				public void run() {
					yxzt_sv.fullScroll(ScrollView.FOCUS_UP);
				}
			});
			dismissLoading();
			isTimeOut = false;
			hkgzTv.setText(yxztStr.toString());
			iv_refresh_fault.clearAnimation();
		}
	}
	
	/**
	 * 得到温湿度数据，刷新图表
	 * @param message
	 */
	private void getRefrehRealDataEx (byte[] message) {
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		if (cmd == HkCMDConstant.GET_WENDU) {//温度
			if (whichChart == WENDU) {
				setWhichChart(WENDU);
			}
		}
		if (cmd == HkCMDConstant.GET_SHIDU) {//湿度
			if (whichChart == SHIDU) {
				setWhichChart(SHIDU);
			}
		}
		if (cmd == HkCMDConstant.GET_ANQI) {//氨气
			if (whichChart == ANQI) {
				setWhichChart(ANQI);
			}
		}
		if (cmd == HkCMDConstant.GET_PH) {//Ph
			if (whichChart == PH) {
				setWhichChart(PH);
			}
		}
	}
	
	/**
	 * 运行状态-档位等故障
	 * @param message
	 */
	private void getYxztDwgz (byte[] message) {
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		if(cmd == HkCMDConstant.GET_YXZT_DWGZ) {//运行状态补充协议-档位故障
			if (message.length > 6) {
				StringBuilder mStringBuffer = new StringBuilder();
				if (message [5] !=0) {//接受失败
					return;
				}
				yxztStr.delete(0, yxztStr.length());
				//1（环控） 0 28(命令)  0 x(长度)  00（是否成功）03（当前档位）00（00：正常，01：断电） 01（网络中断）01（高温）
				message = Arrays.copyOfRange(message, 6, message.length);//去除6位包头加一位成功失败
				//开始解析
				if (message[0] < 6 && message[0] > 0) {//档位时在1到5档之间
					mStringBuffer.append("当前第").append(message[0]).append("档");
					currentDw = message[0];
					if (message [1] == 1) {
						mStringBuffer.append("(默认档位)");
					}
				} else {
					if (message[0] ==  6) {
						mStringBuffer.append("氨气浓度过高,强制通风");
					} else {
						mStringBuffer.append("当前档位异常");
					}
				}
				if (message[2] == 1 ) {//断电
					mStringBuffer.append("\n" + "设备断电");
				}
				if (message[3] == 1 ) {//网络中断
					mStringBuffer.append("\n" + "设备网络中断");
				}
				if (message[4] == 1 ) {//高温报警
					mStringBuffer.append("\n" + "高温报警");
				}
				if (message.length >= 7) {
					if (message[5] == 1 ) {//低温报警
						mStringBuffer.append("\n" + "低温报警");
					}
					
					if (message[6] == 1 ) {//温差报警
						mStringBuffer.append("\n" + "温差过大报警");
					}
				}
				if (mStringBuffer.length() != 0) {
					yxztStr.append(mStringBuffer);
				}
			}
		}
	}
	
	/**
	 * 运行状态-传感器故障
	 * @param message
	 */
	private void getYxztCgqgz (byte[] message) {
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		if(cmd == HkCMDConstant.GET_YXZT_CGQGZ) {//运行状态补充协议-传感器故障
			if (message.length > 6) {
				if (message [5] !=0) {//接受失败
					return;
				}
				StringBuilder mStringBuffer = new StringBuilder();
				message = Arrays.copyOfRange(message, 6, message.length);//去除6位包头
				//开始解析
				String [] cgqGz = getActivity().getResources().getStringArray(R.array.sensor_guzhang_array);
				for (int i = 0;i < message[0];i ++) {//绑定了几个传感器
					if (message[i + 1] != 0 && message[i + 1] != 1) {
						if(message[i + 1] < cgqGz.length) {
							mStringBuffer.append("\n").append(i + 1).append("号传感器").append(cgqGz[message[i + 1]]);
						}
					}
				}
				if (mStringBuffer.length() != 0) {
					yxztStr.append(mStringBuffer);
				}
			}
		}
	}
	
	/**
	 * 运行状态-设备故障
	 * @param message
	 */
	private void getYxztSbgz (byte[] message) {
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		if(cmd == HkCMDConstant.GET_YXZT_SBGZ) {//运行状态补充协议-设备故障
			if (message.length >= 6) {
				if (message [5] !=0) {//接受失败
					return;
				}
				StringBuilder mStringBuffer = new StringBuilder();
				message = Arrays.copyOfRange(message, 6, message.length);//去除6位包头
				if (message[1] == 0) {//设备无故障
					mStringBuffer.append("\n" + "设备正常");
				} else {//有故障
					for (int j = 1;j <= message[1] * 2; j = j + 2) {
						int sblx = YzzsCommonUtil.ChangeByteToPositiveNumber(message[j + 1 ]);
						int sbsx = YzzsCommonUtil.ChangeByteToPositiveNumber(message[j + 2]);
						String [] gzsbData = getActivity().getResources().getStringArray(R.array.shebei_item_array);
						if(sblx < gzsbData.length) {
							String gzsb = gzsbData[sblx];
							mStringBuffer.append("\n").append(sbsx).append("号").append(gzsb).append("故障");
						} 
					}
					if (mStringBuffer.length() != 0) {
						yxztStr.append(mStringBuffer);
					}
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
			// 5 秒钟有收到数据，则停止计时iv_refresh_fault
			isTimeOut = false; // 标志位重新置false
		} else {
			dismissLoading();
			ToastMsg(getActivity(), content + getString(R.string.connect_time_out));
			if(iv_refresh_fault.getAnimation() !=null && iv_refresh_fault.getAnimation().hasStarted()) {
				iv_refresh_fault.clearAnimation();
			}
		}
	}
}