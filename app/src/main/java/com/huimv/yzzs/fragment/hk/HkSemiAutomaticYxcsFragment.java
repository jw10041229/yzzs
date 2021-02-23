package com.huimv.yzzs.fragment.hk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.IndexActivity;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.base.YzzsBaseFragment.ConnectTimeoutListener;
import com.huimv.yzzs.constant.HkCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.model.HkYxcsData;
import com.huimv.yzzs.model.HkYxcsDatas;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.huimv.yzzs.util.wheel.ItemSelectWheelUtil;

import java.util.ArrayList;
import java.util.List;

import static com.huimv.yzzs.constant.XtAppConstant.SBBD_SBLX_BPFJ;
import static com.huimv.yzzs.constant.XtAppConstant.SBBD_SBLX_JL;
import static com.huimv.yzzs.constant.XtAppConstant.SBBD_SBLX_TC;

/**
 *运行参数
 * 
 * @author jiangwei
 */
public class HkSemiAutomaticYxcsFragment extends YzzsBaseFragment implements EventHandler, ViewFactory,
		OnSeekBarChangeListener ,ConnectTimeoutListener{
	//private static final String TAG = HkSemiAutomaticYxcsFragment.class.getSimpleName();
	private ImageSwitcher isDw;
	private ImageView ivDwSub, ivDwAdd;
	private SeekBar seekBarTemp;
	private LinearLayout llSbTemp; // SeekBar 的外布局
	private TextView tvTempLow, tvTempTop;
	private TextView tvSave, tvReset,tv_Reset_current,tv_reset_current_cancel,tv_mrdw;
	private TextView tv_run_per;
	private SparseIntArray imgDwIdsMap;// 档位图片集
	private int currentDwPos = 1;// 当前档位
	private final int dwNum = IndexActivity.numDw;// 分5档
	private SharePreferenceUtil mSpUtil;
	private int SbNum ;
	private String[] dwData; // 5档的数据
	private final ArrayList<HkYxcsDatas> mHk_YxcsList = new ArrayList<>();
	private ArrayList<HkYxcsData> mHk_YxcsDataList;
	private final List<String> bindDataFromBtList = new ArrayList<>(); // 保存蓝牙端发送过来的各个包数据
	private final String Dev_fengji = XtAppConstant.SBBD_SBLX_FJ;// 风机
	private final String Dev_shilian = XtAppConstant.SBBD_SBLX_SL;// 湿帘
	private final String Dev_bbfj = SBBD_SBLX_BPFJ;// 变频风机
    private final String Dev_tc = SBBD_SBLX_TC;// 天窗
    private final String Dev_jl = SBBD_SBLX_JL;// 卷帘
	private final String MAX = HkCMDConstant.MAX;
	private final String MIN = HkCMDConstant.MIN;
	private final String DW = HkCMDConstant.DW;
	private ListView yxcsListView;
	private YxcsListAdapter mYxcsListAdapter;
	// EditText 在 Listview Item 中的位置：1 -开时间；2 - 关时间
	private static final int ET_POS_K = 1;
	private static final int ET_POS_G = 2;
	private boolean isTimeOut = true;
	private boolean isResetCurrent = false;
	private boolean isHasBpfjVersion = false;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		View view = inflater.inflate(R.layout.activity_hk_semi_automatic_yxcs_fragment, null,false);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		initViews(view);
		initOnListeners();
		return view;
	}

	/**
	 * 刷新dwData与list中的数据
	 */
	private void notifyDwData () {
		getDwData();
		getInitDwDkData();
	}
	
	/**
	 * 得到每档的端口数据,放入数组
	 */
	private void getDwData () {
		dwData = mSpUtil.getDwdk().split(XtAppConstant.SEPARSTOR);
	}

	/**
	 * 五档档位端口的数据(解析档位端口数据)
	 */
	private void getInitDwDkData() {
		tv_run_per.setVisibility(View.GONE);
		mHk_YxcsList.clear();
		for (int i = 0; i < dwNum; i++) {// 五档
			mHk_YxcsDataList = new ArrayList<>();
			if (dwData.length == 5) {
				String dwNdk = dwData[i];
				int lenth = isHasBpfjVersion ? 16 : 13;
				boolean hasBfb = false;
				for (int j = 0; j < HkSshjxsFragment.sbNumS; j++) {// 几个设备
					HkYxcsData mHk_YxcsData = new HkYxcsData();
					mHk_YxcsData.setTdkg(dwNdk.substring(j * lenth, j * lenth + 1));
					mHk_YxcsData.setKsj(dwNdk.substring(j * lenth + 1, j * lenth + 5));
					mHk_YxcsData.setGsj(dwNdk.substring(j * lenth + 5, j * lenth + 9));
					mHk_YxcsData.setLx(dwNdk.substring(j * lenth + 9, j * lenth + 11));
					mHk_YxcsData.setSx(dwNdk.substring(j * lenth + 11, j * lenth + 13));
					if (isHasBpfjVersion) {
						mHk_YxcsData.setBbpge(dwNdk.substring(j * lenth + 13, j * lenth + 16));
					}
					String hasBbfStr = dwNdk.substring(j * lenth + 9, j * lenth + 11);
					if (hasBbfStr.equals(SBBD_SBLX_BPFJ) || hasBbfStr.equals(SBBD_SBLX_TC)  ||  hasBbfStr.equals(SBBD_SBLX_JL) ) {//如果有变频风机,天窗,卷帘，怎显示
						hasBfb = true;
					}
					mHk_YxcsDataList.add(mHk_YxcsData);
				}
				if (hasBfb) {
					tv_run_per.setVisibility(View.VISIBLE);
				}
			}
			HkYxcsDatas mHk_Yxcs = new HkYxcsDatas();
			mHk_Yxcs.setmHk_yxcsList(mHk_YxcsDataList);
			mHk_YxcsList.add(mHk_Yxcs);
		}
	}

	private void initViews(View view) {
		tv_mrdw = (TextView) view.findViewById(R.id.tv_mrdw);
		yxcsListView = (ListView) view.findViewById(R.id.yxcsListView);
		tvSave = (TextView) view.findViewById(R.id.tv_save);
		tvReset = (TextView) view.findViewById(R.id.tv_reset);
		tvTempLow = (TextView) view.findViewById(R.id.tv_temp_low);
		tvTempTop = (TextView) view.findViewById(R.id.tv_temp_top);
		isDw = (ImageSwitcher) view.findViewById(R.id.is_dw);
		ivDwSub = (ImageView) view.findViewById(R.id.iv_dw_sub);
		ivDwAdd = (ImageView) view.findViewById(R.id.iv_dw_add);
		seekBarTemp = (SeekBar) view.findViewById(R.id.sb_temp);
		llSbTemp = (LinearLayout) view.findViewById(R.id.ll_sb_temp);
		tv_Reset_current = (TextView) view.findViewById(R.id.tv_reset_current);
		tv_reset_current_cancel = (TextView) view.findViewById(R.id.tv_reset_current_cancel);
		tv_run_per = (TextView) view.findViewById(R.id.tv_run_per);
		isDw.setFactory(this);
	}

	private void initOnListeners() {
		setConnectTimeoutListener(this);
		// 组件的相关监听事件
		ivDwSub.setOnClickListener(this);
		ivDwAdd.setOnClickListener(this);
		seekBarTemp.setOnSeekBarChangeListener(this);
		tvSave.setOnClickListener(this);
		tvReset.setOnClickListener(this);
		tv_Reset_current.setOnClickListener(this);
		tv_reset_current_cancel.setOnClickListener(this);
	}


	/**
	 * 将数据保存到Sp里
	 */
	private void saveData2Sp(int currentDwPos) {
		StringBuilder sbdata = new StringBuilder();
		StringBuilder data = new StringBuilder();
		for (int i = 0; i < SbNum; i++) {
			String isHasbpfjStr = "";
			if (isHasBpfjVersion) {
				isHasbpfjStr = YzzsCommonUtil.formatStringAdd0(mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(i).getBbpge(), 3, 1);
			}
			data.append((mHk_YxcsList.get(currentDwPos- 1).getmHk_yxcsList().get(i).getTdkg()
							+ YzzsCommonUtil.formatStringAdd0(mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(i).getKsj(), 4, 1 )
							+ YzzsCommonUtil.formatStringAdd0(mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(i).getGsj(), 4, 1))
							+ YzzsCommonUtil.formatStringAdd0(mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(i).getLx(), 2, 1)
							+ YzzsCommonUtil.formatStringAdd0(mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(i).getSx(), 2, 1)
							+ isHasbpfjStr
							);
		}
		dwData[currentDwPos - 1] = data.toString();
		for (String dwDataValue:dwData ) {
			sbdata.append(dwDataValue).append(XtAppConstant.SEPARSTOR);
		}
		mSpUtil.setDwdk(sbdata.toString());
		notifyDwData();
		mYxcsListAdapter.notifyDataSetChanged();
	}

	/**
	 * 初始化图片
	 */
	private void initParam() {
		isResetCurrent  = false;
		// 控制档位的图片集合
		imgDwIdsMap = new SparseIntArray();
		imgDwIdsMap.put(1, R.drawable.dw_one);
		imgDwIdsMap.put(2, R.drawable.dw_two);
		imgDwIdsMap.put(3, R.drawable.dw_three);
		imgDwIdsMap.put(4, R.drawable.dw_four);
		imgDwIdsMap.put(5, R.drawable.dw_five);
		showDefalutGear();
		tv_Reset_current.setVisibility(View.VISIBLE);
		tv_reset_current_cancel.setVisibility(View.GONE);
		// 默认设置为第一档位
		isDw.setImageResource(imgDwIdsMap.get(currentDwPos));
		seekBarTemp.setMax(100);
		tvTempLow.setText(p10(mSpUtil.getDwWd(DW + currentDwPos + MIN)));
		tvTempTop.setText(p10(mSpUtil.getDwWd(DW + currentDwPos + MAX)));
		if (isSavedOver()) {
			ivDwAdd.setVisibility(View.VISIBLE);
			ivDwSub.setVisibility(View.VISIBLE);
			seekBarTemp.setVisibility(View.INVISIBLE);
			llSbTemp.setVisibility(View.GONE); // 隐藏seekbar
			tvReset.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onPause() {
		MessageReceiver.ehList.remove(this);
		bindDataFromBtList.clear();
		super.onPause();
	}

	@Override
	public void onResume() {
		MessageReceiver.ehList.add(this);
		try {
			isHasBpfjVersion = mSpUtil.getHkIsHasBpfjVersion().equals("1");
			SbNum = HkSshjxsFragment.sbNumS;
			if(mSpUtil.getDwSave().equals(String.valueOf(IndexActivity.numDw))) {
				currentDwPos = HkSshjxsFragment.currentDw;
			}
			initParam();
			notifyDwData();
			mYxcsListAdapter = new YxcsListAdapter();
			yxcsListView.setAdapter(mYxcsListAdapter);
		} catch (Exception e) {
			ToastMsg(getActivity(), "数据解析错误,请重试");
			getFragmentManager().popBackStack();
		}
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_reset_current_cancel:
			setViewVisiable();
			tvTempTop.setText(p10(mSpUtil.getDwWd(DW + currentDwPos + MAX)));
			break;
		case R.id.iv_dw_sub:
			if (currentDwPos == 1) {
				ToastMsg(getActivity(), "已经是最低档位");
				return;
			}
			if (currentDwPos > 1) {
				isDw.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.left_in));
				isDw.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.right_out));
				currentDwPos--;
				setDwAndTemp(currentDwPos);
				notifyDwData();
				mYxcsListAdapter.notifyDataSetChanged();
			}
			break;

		case R.id.iv_dw_add:
			if (currentDwPos == dwNum) {
				ToastMsg(getActivity(), "已经是最高档位");
				return;
			}
			if (currentDwPos < imgDwIdsMap.size()) {
				isDw.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.right_in));
				isDw.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.lift_out));
				currentDwPos++;
				setDwAndTemp(currentDwPos);
				notifyDwData();
				mYxcsListAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.tv_save:
			if (Double.valueOf(tvTempTop.getText().toString()) <= Double.valueOf(tvTempLow.getText().toString())) {
				ToastMsg(getActivity(), "温度上限必须大于温度下限");
				return;
			}
			if (SbNum == 0) {
				ToastMsg(getActivity(), "设备为空,请添加设备");
				return;
			}
			if (!dwTempIsOK()) {
				return;
			}
			if(YzzsApplication.isConnected) {
				sendData();
				startTime(XtAppConstant.SEND_CMD_TIMEOUT, "数据保存");
				isTimeOut = true;
			} else {
				ToastMsg(getActivity(), getString(R.string.disconnected));
			}
			break;
		case R.id.tv_reset:
			czpzDialog();
			break;
		case R.id.tv_reset_current:
			czddpzDialog();
			break;
		}
	}
	
	/**
	 * 显示默认挡位
	 */
	private void showDefalutGear () {
		String mrdw = mSpUtil.getHkMrdw().trim();
		if (mrdw.equals(currentDwPos + "")) {
			tv_mrdw.setVisibility(View.VISIBLE);
		} else {
			tv_mrdw.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * 重置当前挡位温度
	 */
	private void resetCurrent() {
		tvReset.setVisibility(View.GONE);
		isResetCurrent = true;
		tv_Reset_current.setVisibility(View.GONE);
		seekBarTemp.setVisibility(View.VISIBLE);
		seekBarTemp.setProgress((int) (2 * Double.valueOf(tvTempTop.getText().toString())));
		llSbTemp.setVisibility(View.VISIBLE); // 显示 seekbar
		ivDwAdd.setVisibility(View.INVISIBLE);
		ivDwSub.setVisibility(View.INVISIBLE);
		tv_reset_current_cancel.setVisibility(View.VISIBLE);
	}

	/**
	 * 确定重置配置之后的初始化数据
	 */
	private void setCzpzData() {
		tvSave.setVisibility(View.VISIBLE);
		tvReset.setVisibility(View.GONE);
		ivDwAdd.setVisibility(View.INVISIBLE);
		ivDwSub.setVisibility(View.INVISIBLE);
		seekBarTemp.setVisibility(View.VISIBLE);
		llSbTemp.setVisibility(View.VISIBLE); // 显示 seekbar
		mSpUtil.setDwSave("-1");
		currentDwPos = 1;
		mSpUtil.setDwWd(DW + currentDwPos + MIN, "0");
		mSpUtil.setDwWd(DW + currentDwPos + MAX, "0");
		tvTempLow.setText(p10(mSpUtil.getDwWd(DW + currentDwPos + MIN)));
		tvTempTop.setText(p10(mSpUtil.getDwWd(DW + currentDwPos + MAX)));
		isDw.setImageResource(imgDwIdsMap.get(1));
		seekBarTemp.setProgress((int)(2 * Double.valueOf(p10(mSpUtil.getDwWd(DW + currentDwPos + MAX)))));
		mYxcsListAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 确定单档重置配置
	 */
	private void czddpzDialog() {
		new AlertDialog.Builder(getActivity()).setMessage("确定重置当前档位温度?").setTitle("重置当前档位温度提示")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						resetCurrent();
						dialog.dismiss();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}
	/**
	 * 确定重置配置
	 */
	private void czpzDialog() {
		new AlertDialog.Builder(getActivity()).setMessage("重置配置将会清除所有的档位数据,请慎重执行").setTitle("重置配置提示")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						tv_Reset_current.setVisibility(View.GONE);
						isResetCurrent = false;
						setCzpzData();
						dialog.dismiss();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}

	/**
	 * 通过档位的当前位置，设置档位图片和温度的下限值
	 * 
	 * @param currentDwPos
	 *            当前档位的位置
	 */
	private void setDwAndTemp(int currentDwPos) {
		showDefalutGear();
		// 设置当前档位的图片
		isDw.setImageResource(imgDwIdsMap.get(currentDwPos));
		// 设置当前档位对应的温度下限值
		tvTempLow.setText(p10(mSpUtil.getDwWd(DW + currentDwPos + MIN)));
		tvTempTop.setText(p10(mSpUtil.getDwWd(DW + currentDwPos + MAX)));
	}

	/**
	 * ImageSwicher 填充图片
	 */
	@Override
	public View makeView() {
		final ImageView i = new ImageView(getActivity());
		i.setBackgroundColor(0xff000000);
		i.setScaleType(ImageView.ScaleType.CENTER_CROP);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		// 设置ImageSwicher的填充ImageView的背景颜色是透明
		i.setBackgroundColor(Color.TRANSPARENT);
		return i;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		tvTempTop.setText(((double) progress) / 2 + "");
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if (Double.valueOf(tvTempTop.getText().toString()) < Double.valueOf(tvTempLow.getText().toString())) {
			ToastMsg(getActivity(), "温度上限必须大于温度下限");
			return;
		}
		dwTempIsOK();
	}
	
	/**
	 * 判断数据
	 * @return
	 */
	private boolean dwTempIsOK () {
		boolean isOK;
		if (isSavedOver() && isResetCurrent) {
			if (currentDwPos == dwNum) {
				isOK = true;
			} else {
				double topTemp = Double.valueOf(tvTempTop.getText().toString());
				double maxTemp = Double.valueOf(mSpUtil.getDwWd(DW + (currentDwPos + 1) + MAX)) /10;
				String lowTemp = tvTempLow.getText().toString();
				if (topTemp < maxTemp) {
					isOK = true;
				} else {
					ToastMsg(getActivity(), currentDwPos + "档档位温度区间为:" +  lowTemp+ "--" + (maxTemp - 0.5));
					isOK = false;
				}
			}
		} else {
			isOK = true;
		}
		return isOK;
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

	/**
	 * 发送配置
	 * 
	 */
	private void sendData() {
		byte[] allData = packData(HkCMDConstant.SAVE_SBPZ);
		// 大数组拼接完成,分20包发送
		showLoading("正在保存");
		sendUnpackData(allData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}

	/**
	 * 拼成一个大数据包
	 * @throws InterruptedException 
	 */
	private byte [] packData(int cmd) {
		int sbNum = HkSshjxsFragment.sbNumS;//有几个设备
		int lenth = isHasBpfjVersion ? 12 : 9;//当个循环体有效数据长度
		int lenthData = lenth * sbNum;//循环体的长度
		byte [] Data = new byte [lenthData];//循环体
		int lenthAll = lenthData + 14 + 4;//总数据长度 = 循环体长度 + 前14位 + 后4位协议尾 
		
		byte [] allData = new byte [lenthAll];//大数据包
		allData [0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);//h
		allData [1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);//m
		allData [2] = 1;//环控
		allData [3] = YzzsCommonUtil.intTobyte(cmd / 256);//命令1
		allData [4] = YzzsCommonUtil.intTobyte(cmd % 256);//命令2
		allData [5] = YzzsCommonUtil.intTobyte(lenthAll / 256);//总长度
		allData [6] = YzzsCommonUtil.intTobyte(lenthAll % 256);//总长度
		
		allData [7] = YzzsCommonUtil.intTobyte(sbNum);//
		allData [8] = YzzsCommonUtil.intTobyte(currentDwPos);//当前第几档
		allData [9] = YzzsCommonUtil.intTobyte(Integer.valueOf(m10(tvTempLow.getText().toString(), 0)) / 256);// 温度下限1
		allData [10] = YzzsCommonUtil.intTobyte(Integer.valueOf(m10(tvTempLow.getText().toString(), 0)) % 256);// 温度下限2
		allData [11] = YzzsCommonUtil.intTobyte(Integer.valueOf(m10(tvTempTop.getText().toString(), 0)) / 256);// 温度上限1
		allData [12] = YzzsCommonUtil.intTobyte(Integer.valueOf(m10(tvTempTop.getText().toString(), 0)) % 256);// 温度上限2
		allData [13] = YzzsCommonUtil.intTobyte(lenth);//当个循环体长度
		for (int i = 1; i <= sbNum; i++) {//几个循环体
			byte [] DataSige = new byte [lenth];//单个循环体
			DataSige[0] = YzzsCommonUtil.intTobyte(Integer.valueOf(mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(i - 1).getKsj()) / 256);// 开时间1
			DataSige[1] = YzzsCommonUtil.intTobyte(Integer.valueOf(mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(i - 1).getKsj()) % 256);// 开时间2
			DataSige[2] = YzzsCommonUtil.intTobyte(Integer.valueOf(mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(i - 1).getGsj()) / 256);// 关时间2
			DataSige[3] = YzzsCommonUtil.intTobyte(Integer.valueOf(mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(i - 1).getGsj()) % 256);// 关时间2
			DataSige[4] = YzzsCommonUtil.intTobyte(Integer.valueOf(mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(i - 1).getTdkg()));// 使能
			DataSige[5] = YzzsCommonUtil.intTobyte(Integer.valueOf(mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(i - 1).getLx().substring(0,1)));//类型
			DataSige[6] = YzzsCommonUtil.intTobyte(Integer.valueOf(mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(i - 1).getLx().substring(1,2)));//类型
			DataSige[7] = YzzsCommonUtil.intTobyte(Integer.valueOf(mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(i - 1).getSx().substring(0,1)));//类型
			DataSige[8] = YzzsCommonUtil.intTobyte(Integer.valueOf(mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(i - 1).getSx().substring(1,2)));//类型
			if (isHasBpfjVersion) {
				DataSige[9] = YzzsCommonUtil.intTobyte(Integer.valueOf(mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(i - 1).getBbpge().substring(0,1)));//百分比
				DataSige[10] = YzzsCommonUtil.intTobyte(Integer.valueOf(mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(i - 1).getBbpge().substring(1,2)));//百分比
				DataSige[11] = YzzsCommonUtil.intTobyte(Integer.valueOf(mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(i - 1).getBbpge().substring(2,3)));//百分比
			}
			for (int j = 0; j < DataSige.length; j++) {//单个循环体拼入循环体数组
				Data[(i-1)*DataSige.length +j] = DataSige[j];
			}
		}
		System.arraycopy(Data, 0, allData, 14, Data.length);
		
		allData [allData.length - 4] = 111;//校验位
		allData [allData.length - 3] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);//协议尾e
		allData [allData.length - 2] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);//h
		allData [allData.length - 1] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);//d
		return allData;
	}

	/**
	 * 数据乘以10 add1Or0 是否需要加1
	 * 
	 * @return
	 */
	private String m10(String str, int add1Or0) {
		
		return String.valueOf((int) (Double.valueOf(str) * 10 + add1Or0));
	}

	/**
	 * 数据除以10
	 * 
	 * @return
	 */
	private String p10(String str) {
		return String.valueOf(Double.valueOf(str) / 10);
	}

	@Override
	public void onMessage(byte[] message) {
		getJoiningPack(message);
	}

	/**
	 * 拼包
	 * 
	 * @param message
	 */
	private void getJoiningPack(byte[] message) {
		if (message[0] == HkCMDConstant.HK_SBBZ) {// 环控
			try {
				int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);// 命令类型
				// 档位保存
				if (cmd == HkCMDConstant.SAVE_SBPZ) {
					saveDwbcEx(message);
				}
			} catch (Exception e) {
				dismissLoading();
				ToastMsg(getActivity(), "数据异常");
			}
		}
	}

	/**
	 * 是否5档保存完成
	 * 
	 * @return
	 */
	private boolean isSavedOver() {
		return mSpUtil.getDwSave().equals(String.valueOf(dwNum));
	}
	
	/**
	 * 接收保存结果
	 * @param message
	 */
	private void saveDwbcEx(byte[] message) {
		int cmd = YzzsCommonUtil.getCMD(message [1], message [2]);
			if (cmd == HkCMDConstant.SAVE_SBPZ) {// 档位保存
				if (message[5] == 0) {// 成功与否
					isTimeOut = false;
					if (message[6] == currentDwPos) {// 返回的是否为当前保存的档位
						if (!isSavedOver()) { // 如果等于5说明已经保存成功,下面的就是修改了
							if (currentDwPos == dwNum) {
								mSpUtil.setDwWd(DW + currentDwPos + MIN, m10(tvTempLow.getText().toString(), 0));
								mSpUtil.setDwWd(DW + currentDwPos + MAX, m10(tvTempTop.getText().toString(), 0));
								tvTempLow.setText(p10(mSpUtil.getDwWd(DW + currentDwPos + MIN)));
								mSpUtil.setDwSave(String.valueOf(currentDwPos));
								saveData2Sp(currentDwPos);
								mYxcsListAdapter.notifyDataSetChanged();
								ToastMsg(getActivity(), "档位温度设置成功");
								dismissLoading();
								tv_Reset_current.setVisibility(View.VISIBLE);
							} else {
								mSpUtil.setDwWd(DW + currentDwPos + MIN, m10(tvTempLow.getText().toString(), 0));
								mSpUtil.setDwWd(DW + currentDwPos + MAX, m10(tvTempTop.getText().toString(), 0));
								mSpUtil.setDwWd(DW + (currentDwPos + 1) + MIN, m10(tvTempTop.getText().toString(), 0));
								tvTempLow.setText(p10(mSpUtil.getDwWd(DW + (currentDwPos + 1) + MIN)));
								tvTempTop.setText(p10(mSpUtil.getDwWd(DW + (currentDwPos + 1) + MIN)));
								mSpUtil.setDwSave(String.valueOf(currentDwPos));
								saveData2Sp(currentDwPos);
								currentDwPos++;
								isDw.setImageResource(imgDwIdsMap.get(currentDwPos));
								mYxcsListAdapter.notifyDataSetChanged();
								dismissLoading();
							}
		
						} else {
							if (isResetCurrent) {
								mSpUtil.setDwWd(DW + currentDwPos + MIN, m10(tvTempLow.getText().toString(), 0));
								mSpUtil.setDwWd(DW + currentDwPos + MAX, m10(tvTempTop.getText().toString(), 0));
								mSpUtil.setDwWd(DW + (currentDwPos + 1) + MIN, m10(tvTempTop.getText().toString(), 0));
								setViewVisiable();
							} else {
								tv_Reset_current.setVisibility(View.VISIBLE);
							}
							saveData2Sp(currentDwPos);
							dismissLoading();
							ToastMsg(getActivity(), "保存成功");
						}
					}
				} else {
					dismissLoading();
					ToastMsg(getActivity(), "保存失败,请重试");
				}
				if (isSavedOver()) {
					setViewVisiable();
				}
			}
	}
	private void setViewVisiable() {
		ivDwAdd.setVisibility(View.VISIBLE);
		ivDwSub.setVisibility(View.VISIBLE);
		seekBarTemp.setVisibility(View.INVISIBLE);
		llSbTemp.setVisibility(View.GONE); // 隐藏 seekbar
		tvReset.setVisibility(View.VISIBLE);
		tv_Reset_current.setVisibility(View.VISIBLE);
		tv_reset_current_cancel.setVisibility(View.GONE);
	}
	/**
	 * 设备适配器
	 * 
	 * @author jiangwei
	 *
	 */
	private class YxcsListAdapter extends BaseAdapter {
		private LayoutInflater mInflator;

		public YxcsListAdapter() {
			super();
			mInflator = getActivity().getLayoutInflater();
		}

		@Override
		public int getCount() {
			return mHk_YxcsList.get(0).getmHk_yxcsList().size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private int whichEt = -1;// 哪个eT
		private int index = -1;// 哪一行

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			if (convertView == null) {
				convertView = mInflator.inflate(R.layout.hk_semi_automatic_listview_item, null);
				viewHolder = new ViewHolder();
				viewHolder.dkIcon = (ImageView) convertView.findViewById(R.id.dkIcon);
				viewHolder.et_dk_open_time = (EditText) convertView.findViewById(R.id.et_dk_open_time);
				viewHolder.et_dk_close_time = (EditText) convertView.findViewById(R.id.et_dk_close_time);
				viewHolder.tv_dk_open_pge = (TextView) convertView.findViewById(R.id.tv_dk_open_pge);
				viewHolder.sxTv = (TextView) convertView.findViewById(R.id.sxTv);
				viewHolder.snTv = (TextView) convertView.findViewById(R.id.snTv);
				viewHolder.et_dk_open_time.setTag(position);
				viewHolder.et_dk_close_time.setTag(position);
				viewHolder.tv_dk_open_pge.setTag(position);
				viewHolder.sxTv.setTag(position);
				viewHolder.dkIcon.setTag(position);
				viewHolder.snTv.setTag(position);
				viewHolder.et_dk_open_time.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_UP) {
							whichEt = ET_POS_K;
							index = (Integer) v.getTag();
						}
						return false;
					}
				});
				
				viewHolder.et_dk_close_time.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_UP) {
							whichEt = ET_POS_G;
							index = (Integer) v.getTag();
						}
						return false;
					}
				});
				viewHolder.et_dk_open_time.addTextChangedListener(new EtTextWatcher(viewHolder, ET_POS_K));
				viewHolder.et_dk_close_time.addTextChangedListener(new EtTextWatcher(viewHolder, ET_POS_G));
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
				viewHolder.et_dk_open_time.setTag(position);
				viewHolder.et_dk_close_time.setTag(position);
				viewHolder.dkIcon.setTag(position);
				viewHolder.sxTv.setTag(position);
				viewHolder.snTv.setTag(position);
				viewHolder.tv_dk_open_pge.setTag(position);
			}
			HkYxcsData mHk_YxcsData = mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(position);
			String ksj = mHk_YxcsData.getKsj();// 开时间
			String gsj = mHk_YxcsData.getGsj();// 关时间
			String sn = mHk_YxcsData.getTdkg();// 使能
			final String lx = String.valueOf(mHk_YxcsData.getLx());// 类型
			String sx = mHk_YxcsData.getSx();// 顺序
			String bppge = mHk_YxcsData.getBbpge();//变频百分比
			viewHolder.tv_dk_open_pge.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String [] bfbCs = null;
					if (!lx.equals(Dev_bbfj)) {
						bfbCs = getActivity().getResources().getStringArray(R.array.hk_cssz_bfb);
					} else {
						bfbCs = getActivity().getResources().getStringArray(R.array.hk_cssz_bpfj_bfb);
					}
					final String[] finalBfbCs = bfbCs;
					new ItemSelectWheelUtil(getActivity(), "设置参数", finalBfbCs, new ItemSelectWheelUtil.OnConfirmClickListener() {
						@Override
						public void onConfirm(int position, int i) {
							String bfb = finalBfbCs[position];
							viewHolder.tv_dk_open_pge.setText(Integer.valueOf(bfb).toString());
							mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get((int)viewHolder.tv_dk_open_pge.getTag()).setBbpge(YzzsCommonUtil.formatStringAdd0(bfb,3,1));
						}
					}).showDialog(0,"设置参数");
				}
			});
			if (CommonUtil.isNotEmpty(bppge)) {
				viewHolder.tv_dk_open_pge.setText(Integer.valueOf(YzzsCommonUtil.countShowBpPge(bppge)).toString());
			}
			if (lx.equals(Dev_fengji)) {
				viewHolder.dkIcon.setImageResource(R.drawable.fj_on);
			} else {
				viewHolder.et_dk_open_time.setVisibility(View.VISIBLE);
				viewHolder.et_dk_close_time.setVisibility(View.VISIBLE);
			}
			if (lx.equals(Dev_shilian)) {
				viewHolder.dkIcon.setImageResource(R.drawable.sl_on);
			} else {
				viewHolder.et_dk_open_time.setVisibility(View.VISIBLE);
				viewHolder.et_dk_close_time.setVisibility(View.VISIBLE);
			}
			//变频风机。天窗，卷帘
			if (lx.equals(Dev_bbfj) || lx.equals(Dev_tc) || lx.equals(Dev_jl)) {
				//天窗,卷帘
				if (lx.equals(Dev_tc) || lx.equals(Dev_jl)) {
					//天窗
					if (lx.equals(Dev_tc)) {
						viewHolder.dkIcon.setImageResource(R.drawable.tc_on);
				}
					//卷帘
					if (lx.equals(Dev_jl)) {
						viewHolder.dkIcon.setImageResource(R.drawable.jl_on);
					}
					viewHolder.et_dk_open_time.setVisibility(View.INVISIBLE);
					viewHolder.et_dk_close_time.setVisibility(View.INVISIBLE);
				} else {
					viewHolder.et_dk_open_time.setVisibility(View.VISIBLE);
					viewHolder.et_dk_close_time.setVisibility(View.VISIBLE);
				}
				//变频风机
				if (lx.equals(Dev_bbfj)) {
					viewHolder.dkIcon.setImageResource(R.drawable.fj_hz_on);
					viewHolder.et_dk_open_time.setVisibility(View.VISIBLE);
					viewHolder.et_dk_close_time.setVisibility(View.VISIBLE);
				}
				viewHolder.tv_dk_open_pge.setVisibility(View.VISIBLE);
			} else {
				viewHolder.tv_dk_open_pge.setVisibility(View.INVISIBLE );
			}

			viewHolder.sxTv.setBackground(getResources().getDrawable(R.drawable.sbsx_text_sn_style));

			if(mSpUtil.getDwSave().equals(String.valueOf(IndexActivity.numDw))) {
				viewHolder.et_dk_open_time.setEnabled(true);
				viewHolder.et_dk_close_time.setEnabled(true);
				viewHolder.et_dk_open_time.setTextColor(getResources().getColor(R.color.black));
				viewHolder.et_dk_close_time.setTextColor(getResources().getColor(R.color.black));
				if (lx.equals(Dev_bbfj) || lx.equals(Dev_tc) || lx.equals(Dev_jl)) {//如果是天窗，卷帘,变频风机
					viewHolder.tv_dk_open_pge.setClickable(true);//light_blue
					viewHolder.tv_dk_open_pge.setTextColor(getResources().getColor(R.color.light_blue));
				} else {
					viewHolder.tv_dk_open_pge.setClickable(false);//light_blue
				}
			} else {
				viewHolder.tv_dk_open_pge.setClickable(false);
				viewHolder.et_dk_open_time.setEnabled(false);
				viewHolder.et_dk_close_time.setEnabled(false);
				viewHolder.et_dk_open_time.setTextColor(getResources().getColor(R.color.gray));
				viewHolder.et_dk_close_time.setTextColor(getResources().getColor(R.color.gray));
				viewHolder.tv_dk_open_pge.setTextColor(getResources().getColor(R.color.gray));
			}
			viewHolder.et_dk_open_time.setText(Integer.valueOf(CommonUtil.isEmpty(ksj) ? "0" : ksj).toString());
			viewHolder.et_dk_close_time.setText(Integer.valueOf(CommonUtil.isEmpty(gsj) ? "0" : gsj).toString());
			viewHolder.sxTv.setText(String.valueOf(Integer.valueOf(sx)));
			viewHolder.snTv.setText(sn);

			if (index != -1 && index == position) {
				// 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
				if (whichEt != -1 && whichEt == ET_POS_K) {
					viewHolder.et_dk_open_time.requestFocus();
					viewHolder.et_dk_open_time.setSelection(viewHolder.et_dk_open_time.getText().length());
				}
				if (whichEt != -1 && whichEt == ET_POS_G) {
					viewHolder.et_dk_close_time.requestFocus();
					viewHolder.et_dk_close_time.setSelection(viewHolder.et_dk_close_time.getText().length());
				}
			}
			if (tv_run_per.getVisibility() == View.GONE) {
				viewHolder.tv_dk_open_pge.setVisibility(View.GONE);
			}
			return convertView;
		}

		/**
		 * EditText 触摸改变事件监听内部类
		 *
		 */
		private class EtTextWatcher implements TextWatcher {
			private int whichEditTextPos; // 当前 item 中对应哪个 EditText
			private ViewHolder viewHolder;
			public EtTextWatcher(ViewHolder viewHolder,int whichEditTextPos) {
				this.viewHolder = viewHolder;
				this.whichEditTextPos = whichEditTextPos;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				int position = (Integer) viewHolder.et_dk_open_time.getTag();
				if (whichEditTextPos == ET_POS_K) {// 开
					mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(position).setKsj(CommonUtil.isEmpty(s) ? "0": s.toString());
				}
				if (whichEditTextPos == ET_POS_G) {// 关
					mHk_YxcsList.get(currentDwPos - 1).getmHk_yxcsList().get(position).setGsj(CommonUtil.isEmpty(s) ? "0": s.toString());
				}
			}
		}
	}

	class ViewHolder {
		ImageView dkIcon;
		TextView sxTv;
		TextView snTv;
		TextView tv_dk_open_pge;
		EditText et_dk_open_time;
		EditText et_dk_close_time;
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