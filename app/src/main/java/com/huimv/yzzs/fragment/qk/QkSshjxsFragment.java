package com.huimv.yzzs.fragment.qk;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.BluetoothScanActivity;
import com.huimv.yzzs.activity.IndexActivity;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.chart.HkPhChart;
import com.huimv.yzzs.chart.QkSlChart;
import com.huimv.yzzs.chart.QkSyChart;
import com.huimv.yzzs.constant.HkCMDConstant;
import com.huimv.yzzs.constant.QkCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.support.qk.QkSshjxsFragmentSupport;
import com.huimv.yzzs.util.ToolUnit;
import com.huimv.yzzs.util.YzzsCommonUtil;

import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.model.SeriesSelection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/**
 * 全控实时环境显示
 * @author jiangwei
 *
 */
@SuppressLint("UseSparseArrays")
public class QkSshjxsFragment extends YzzsBaseFragment implements EventHandler,OnClickListener{
	//private final static String TAG = QkSshjxsFragment.class.getSimpleName();
	private final StringBuilder yxztStr = new StringBuilder();
	private RelativeLayout blueToothStateRL;
	private Button setBtn;
	private ImageView bluetoothSearchIv;
	private ImageView iv_lanya;
	private ScrollView yxzt_sv;
	private RelativeLayout chartRL;
	private PopupWindow mUpLeftPopupTip,mUpRightPopupTip;//提示信息pop(Left/Right)
	private View mUpLeftTipView,mUpRightTipView;//提示信息PopView
	private int mPopTipsWidth;//pop提示窗口宽度
	private int mPopTipsHeight;//pop提示窗口高度
	private TextView tv_tips_ul,tv_tips_ur;//提示信息Textview
	private TextView hkgzTv;//故障
	private TextView tv_title_time;
	private LineChart xychart;
	private ImageView refresh_iv;
	private GraphicalView mChartView;
	private HorizontalScrollView hs_btn;
	private LinearLayout sensorBtnLL,ll_bottom_tab;
	private int CGQ_LX [];//传感器顺序
	private final int[] Qk_tabBg_normal = XtAppConstant.Qk_tabBg_normal;
	private final int[] Qk_tabBg_pressed = XtAppConstant.Qk_tabBg_pressed;
	private final Map<Integer, Button> btnMap = new HashMap<>();// BtnMap
	private final Map<Integer, ImageView> tabIVMap = new HashMap<>();// ImageButtonMap
	
	private final int SHUIYA = QkCMDConstant.SHUIYA_TAG;//水压
	private final int SHUILIU = QkCMDConstant.SHUILIU_TAG;//水流
	private final int PH = QkCMDConstant.PH_TAG;//Ph
	private final int SHUIYA_CGQ = Integer.valueOf(XtAppConstant.CGQBD_SBLX_SY);//水压传感器
	private final int SHILIU_CGQ = Integer.valueOf(XtAppConstant.CGQBD_SBLX_SL);//水流传感器
	private final int PH_CGQ = Integer.valueOf(XtAppConstant.CGQBD_SBLX_PH);//PH传感器
	
	private int whichChart = SHUIYA;//用来区分哪个图表，3:水压,9水流
	private int whichLine = 1;//哪个统计图的哪个条线
	private final QkSshjxsFragmentSupport mQk_sshjxsFragmentSupport = new QkSshjxsFragmentSupport();
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_qk_sshjxs_fragment, null);
		initView(view);
		return view;
	}
	
	private void initView(View view) {
		deleteAllSensorData();
		hs_btn = (HorizontalScrollView)view.findViewById(R.id.hs_btn);
		yxzt_sv = (ScrollView) view.findViewById(R.id.yxzt_sv);
		sensorBtnLL = (LinearLayout) view.findViewById(R.id.cgqBtnLL);
		ll_bottom_tab = (LinearLayout) view.findViewById(R.id.ll_bottom_tab);
		bluetoothSearchIv = (ImageView) view.findViewById(R.id.bluetoothSearchIv);
		blueToothStateRL = (RelativeLayout) view.findViewById(R.id.blueToothStateRL);
		hkgzTv = (TextView) view.findViewById(R.id.hkgzTv);
		chartRL = (RelativeLayout) view.findViewById(R.id.chartRL);
		refresh_iv = (ImageView) view.findViewById(R.id.refresh_iv);
		refresh_iv.setOnClickListener(this);
		setBtn = (Button) view.findViewById(R.id.setBtn);
		iv_lanya = (ImageView) view.findViewById(R.id.iv_lanya);
		iv_lanya.setOnClickListener(this);
		bluetoothSearchIv.setOnClickListener(this);
		setBtn.setOnClickListener(this);
		tv_title_time = (TextView) view.findViewById(R.id.tv_title_time);
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
	 * 删除水压水流数据库数据
	 */
	private void deleteAllSensorData() {
		mQk_sshjxsFragmentSupport.qk_deleteAllshuiya(getActivity());
		mQk_sshjxsFragmentSupport.qk_deleteAllshuiliu(getActivity());
		mQk_sshjxsFragmentSupport.qk_deleteAllPh(getActivity());
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
			if (type == SHUIYA) {//水压
				if (CGQ_LX[i] == SHUIYA_CGQ) {
					btnMap.get(i).setVisibility(View.VISIBLE);
				}
			}
			if (type == SHUILIU) {//水流
				if (CGQ_LX[i] == SHILIU_CGQ) {
					btnMap.get(i).setVisibility(View.VISIBLE);
				}
			}
			if (type == PH) {//水流
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
        String wsdTag = "水压:";
        String xStr = "";
        if (whichChart == SHUIYA) {
        	wsdTag = "水压:";
        	xStr = QkSyChart.x[whichPoint];
        } 
        if(whichChart == SHUILIU) {
        	wsdTag = "水流:";
        	xStr = QkSlChart.x[whichPoint];
		}
        if(whichChart == PH) {
        	wsdTag = "Ph:";
        	xStr = HkPhChart.x[whichPoint];
		}
		if(getDisplayMetrics().widthPixels - x < mPopTipsWidth ){
			tv_tips_ur.setText("时间:" + xStr +"\n"+ wsdTag + point[1]);
			//右上方
			popwindowR(mUpRightTipView,x-mPopTipsWidth + 1,y+location[1]-mPopTipsHeight);
		}else{
			tv_tips_ul.setText("时间:" + xStr +"\n"+ wsdTag + point[1]);
			//左上方--OK精准点
			popwindowL(mUpLeftTipView, x ,y+location[1]-mPopTipsHeight);
		}
	}
	/*
	 * 水压 ，水流第一个传感器的序号
	 * 当做点击三者进入的默认显示
	 * @return
	 */
	private int initShowData (int type) {
		int m = 0;
		for (int i =0;i < CGQ_LX.length; i++) {
			if(type == SHUIYA && CGQ_LX[i] == SHUIYA_CGQ){//水压
				m = i + 1;
				break;
			}
			if(type == SHUILIU && CGQ_LX[i] == SHILIU_CGQ){//水流
				m = i + 1;
				break;
			}
			if(type == PH && CGQ_LX[i] == PH_CGQ){//Ph
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
			FragmentManager fm = getActivity().getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.fragment_container, new QkMmyzFragment());
			//ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.addToBackStack(null);
			ft.commit();
			break;
		case R.id.bluetoothSearchIv:
			toBluetoothScan();
			break;
		case R.id.refresh_iv:
			showLoading("正在刷新运行状态...");
			refreshGz();
			break;
		}
		super.onClick(v);
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
	private void refreshGz() {
		yxztStr.delete(0, yxztStr.length());
		int lenth = 12;
		int cmd = QkCMDConstant.GET_YXZT;
		final byte [] byteData = new byte[lenth];
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
		sendUnpackData(byteData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}
	/**
	 * 哪个统计图
	 * @param whichChart
	 */
	private void setWhichChart(final int whichChart) {
		//先关闭泡泡窗口
		dismissPopupWindow();
		chartRL.removeAllViewsInLayout();
		if (whichChart == SHUIYA) {//水压
			xychart = new QkSyChart(whichLine).execute(getActivity());
		}
		if (whichChart == SHUILIU) { //水流
			xychart = new QkSlChart(whichLine).execute(getActivity());
		}
		if (whichChart == PH) { //Ph
			xychart = new HkPhChart(whichLine).execute(getActivity());
		}
		mChartView = new GraphicalView(getActivity(), xychart);
		chartRL.addView(mChartView);
		mChartView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				getCharData(whichChart,view, xychart, mChartView);
			}
		});
	}
	
	@Override
	public void onPause() {
		MessageReceiver.ehList.remove(this);
		super.onPause();
	}
	
	@Override
	public void onResume() {
		MessageReceiver.ehList.add(this);
		yxztStr.delete(0, yxztStr.length());
		if (YzzsApplication.isConnected) {
			refreshGz();
		}
		CGQ_LX = IndexActivity.CGQ_LX;
		addTopBtn();
		addTabIv();
		hasSensor();
		if (CGQ_LX.length > 0) {
			setInitChangeData(changeSensorType(CGQ_LX [0]));
		} else {
			setInitChangeData(SHUIYA);
		}
		super.onResume();
	}
	
	/**
	 * 传感器类型转换
	 * @param firstType    
	 * @return
	 */
	private int changeSensorType (int firstType) {
		int finalType;
		switch (firstType) {
		case 3://温度
			finalType = SHUIYA;
			break;
		case 4://PH
			finalType = PH;
			break;
		default:
			finalType = SHUIYA;
			break;
		}
		return finalType;
	}
	/**
	 * 动态添加传感器类型Tab
	 */
	private void addTabIv() {
		ll_bottom_tab.removeAllViewsInLayout();
		int cgq_lx = QkCMDConstant.CGQ_DATA_LX;
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
		IbBtn.setOnClickListener(new OnClickListener() {
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
		sensorBtnLL.removeAllViewsInLayout();
		for (int i = 0; i < CGQ_LX.length; i++) {
			// 循环遍历给TextView设置文字和属性
			addTextTab(i);
		}
	}

	private void addTextTab(final int i) {
		Button btn = new Button(getActivity());
		btnMap.put(i, btn);
		btn.setText(i+1 + "号");
		btn.setFocusable(true);
		btn.setGravity(Gravity.CENTER);
		btn.setSingleLine();
		//btn.setBackgroundColor(getResources().getColor(R.color.gray_light2));
		btn.setBackground(getResources().getDrawable(R.drawable.light_blue_bg));
		btn.setTextSize(18);
		btn.setTextColor(getResources().getColor(R.color.white));
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
				(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(10,0,10,0);
		btn.setLayoutParams(layoutParams);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				whichLine = i +1;
				setWhichChart(whichChart);
				setTopBtnBg(i +1);
			}
		});
		sensorBtnLL.addView(btn);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	/**
	 * 有没有绑定相应的传感器,有:显示相应的TAB,无:隐藏
	 * @return
	 */
	private void hasSensor() {
		if (CGQ_LX.length == 0) {
			hs_btn.setVisibility(View.GONE);
			//如果没有传感器，则显示水压
			tabIVMap.get(0).setVisibility(View.VISIBLE);
		} else {
			hs_btn.setVisibility(View.VISIBLE);
			ll_bottom_tab.setVisibility(View.VISIBLE);
			for (int i = 0; i < tabIVMap.size(); i++) {//先全部隐藏
				tabIVMap.get(i).setVisibility(View.GONE);
			}
			for (int cgqLx:CGQ_LX ) {
				if (cgqLx == SHUIYA_CGQ) {//水压
					tabIVMap.get(0).setVisibility(View.VISIBLE);
				}
				if (cgqLx == SHILIU_CGQ) {//水流
					tabIVMap.get(1).setVisibility(View.VISIBLE);
				}
				if (cgqLx == PH_CGQ) {//Ph
					tabIVMap.get(2).setVisibility(View.VISIBLE);
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
    private PopupWindow popwindowL(View popView,int xoff,int yoff){
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
    public PopupWindow popwindowR(View popView,int xoff,int yoff){
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
		if (!isConnected) {//如果断开
			ToastMsg(getActivity(), getString(R.string.disconnected));
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
		tabIVMap.get(index -1).setImageResource(Qk_tabBg_pressed[index -1]);
	}
	
	/**
	 * 重置tab背景
	 */
	private void resetBtn() {
		for (int i = 0; i < tabIVMap.size(); i++) {
			tabIVMap.get(i).setImageResource(Qk_tabBg_normal[i]);
		}
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
				getRefrehRealData(message);
				getYxzt(message);
				getTitleTime(message);
			} catch (Exception e) {
				dismissLoading();
				hkgzTv.setText("数据格式错误");
			}
		}
	}
	/**
	 * 读取系统时间
	 * @param message
	 */
	private void getTitleTime(byte[] message) {
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		if (cmd == QkCMDConstant.GET_SYS_TIME) {
			if(message[5] == 0) {
				dismissLoading();
				if(message[6] == 0) {
					int lenth = message [7];
					StringBuilder sb = new StringBuilder();
					for (int i = 8; i < lenth + 8; i++) {
						sb.append(message [i]);
					}
					if (lenth == 4) {
						tv_title_time.setText("全控:" + sb.insert(2, ":"));
					}
				}
			}
		}
	}

	/**
	 * 运行状态 拼包
	 * @param message
	 */
	private void getYxzt(byte[] message) {
		getYxztDwgz(message);//档位故障
		getYxztCgqgz(message);//传感器故障
		getYxztSbgz(message);//设备故障
		dismissLoading();
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
		if (cmd == QkCMDConstant.GET_YXZT_XTGZ
				|| cmd == QkCMDConstant.GET_YXZT_CGQGZ
				|| cmd == QkCMDConstant.GET_YXZT_SBGZ
			) {//如果是这三个,则刷新
				yxzt_sv.post(new Runnable() {
					public void run() {
						yxzt_sv.fullScroll(ScrollView.FOCUS_DOWN);
					}
				});
				if (yxztStr.length() ==0) {
					hkgzTv.setText("设备无故障");
				} else {
					hkgzTv.setText(yxztStr.toString());
				}
		}
	}
	
	/**
	 * 得到温湿度数据，刷新图表
	 * @param message
	 */
	private void getRefrehRealData (byte[] message) {
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
			if (cmd == QkCMDConstant.GET_SHUIYA) {//水压
				if (whichChart == SHUIYA) {
					setWhichChart(SHUIYA);
				}
			}
			if (cmd == QkCMDConstant.GET_SHUILIU) {//水流
				if (whichChart == SHUILIU) {
					setWhichChart(SHUILIU);
				}
			}
			if (cmd == QkCMDConstant.GET_PH) {//Ph
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
		if(cmd == QkCMDConstant.GET_YXZT_XTGZ) {//运行状态补充协议-系统故障
			if (message.length > 6) {
				StringBuilder mStringBuilder = new StringBuilder();
				if (message [5] !=0) {//接受失败
					return;
				}
				message = Arrays.copyOfRange(message, 6, message.length);//去除6位包头加一位成功失败
				yxztStr.delete(0, yxztStr.length());
				//开始解析
				/*if (message[0] < 6 && message[0] > 0) {//档位时在1到5档之间
					mStringBuilder.append("当前第" + message[0] + "档");
					if (message [1] == 1) {
						mStringBuilder.append("(默认档位)");
					}
				} else {
					mStringBuilder.append("当前档位异常");
				}*/
				if (message[2] == 1 ) {//断电
					if (mStringBuilder.length() == 0) {
						mStringBuilder.append("设备断电");
					} else {
						mStringBuilder.append("\n" +"设备断电");
					}
				}
				if (message[3] == 1 ) {//网络中断
					if (mStringBuilder.length() == 0) {
						mStringBuilder.append("设备网络中断");
					} else {
						mStringBuilder.append("\n" + "设备网络中断");
					}
				}
				if (message[4] == 1 ) {//高温报警
					mStringBuilder.append("\n" + "高温报警");
				}
/*				if(true) {//其他报警
					
				}*/
				if (mStringBuilder.length() != 0) {
					yxztStr.append(mStringBuilder);
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
		if(cmd == QkCMDConstant.GET_YXZT_CGQGZ) {//运行状态补充协议-传感器故障
			if (message.length > 6) {
				if (message [5] !=0) {//接受失败
					return;
				}
				StringBuilder mStringBuilder = new StringBuilder();
				message = Arrays.copyOfRange(message, 6, message.length);//去除6位包头
				//开始解析
				String [] cgqGz = getActivity().getResources().getStringArray(R.array.sensor_guzhang_array);
				for (int i = 0;i < message[0];i ++) {//绑定了几个传感器
					if (message[i + 1] != 0 && message[i + 1] != 1) {
						if(message[i + 1] < cgqGz.length) {
							mStringBuilder.append("\n" + (i + 1) + "号传感器" + cgqGz[message[i + 1]]);
						}
					}
				}
				if (mStringBuilder.length() != 0) {
					yxztStr.append(mStringBuilder);
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
				StringBuilder mStringBuilder = new StringBuilder();
				message = Arrays.copyOfRange(message, 6, message.length);//去除6位包头
				if (message[1] == 0) {//设备无故障
					mStringBuilder.append("\n" + "设备正常");
				} else {//有故障
					for (int j = 1;j <= message[1] * 2; j = j + 2) {
						int sblx = YzzsCommonUtil.ChangeByteToPositiveNumber(message[j + 1 ]);
						int sbsx = YzzsCommonUtil.ChangeByteToPositiveNumber(message[j + 2]);
						String [] gzsbData = getActivity().getResources().getStringArray(R.array.shebei_item_array);
						if(sblx < gzsbData.length) {
							String gzsb = gzsbData[sblx];
							mStringBuilder.append("\n" + sbsx + "号" + gzsb + "故障");
						}
					}
					if (mStringBuilder.length() != 0) {
						yxztStr.append(mStringBuilder);
					}
				}
			}
		}
	}
}