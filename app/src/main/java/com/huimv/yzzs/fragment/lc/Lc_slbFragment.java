package com.huimv.yzzs.fragment.lc;/*package com.huimv.yzzs.lc.fragment;

import java.util.HashMap;
import java.util.Map;

import com.huimv.android.base.BaseFragment;
import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.IndexActivity;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.constant.LcCMDConstant;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.receiver.MessageReceiver.EventHandler;
import com.huimv.yzzs.util.SharePreferenceUtil;
import com.huimv.yzzs.util.YzzsCommonUtil;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;
*//**
 * 水料比
 * @author jiangwei
 *
 *//*
public class Lc_slbFragment extends BaseFragment implements EventHandler,ViewFactory{
	private final static String TAG = Lc_slbFragment.class.getSimpleName();
	private TextView tv_save;
	private ImageSwitcher isDw;
	private SharePreferenceUtil mSpUtil;
	private ImageView ivDwSub, ivDwAdd;
	private Map<String, Integer> imgDwIdsMap;// 档位图片集
	private EditText et_dk1_open_time,et_dk2_open_time,et_dk3_open_time,et_dk4_open_time,et_dk5_open_time,et_dk6_open_time,
				et_dk7_open_time,et_dk8_open_time,et_dk9_open_time,et_dk10_open_time;
	private EditText et_dk1_close_time,et_dk2_close_time,et_dk3_close_time,et_dk4_close_time,et_dk5_close_time,et_dk6_close_time,
				et_dk7_close_time,et_dk8_close_time,et_dk9_close_time,et_dk10_close_time;
	private int currentDwPos = 1;
	private int dkNum = 10;// 有几个端口在用
	private int count = 0;
	private LinearLayout dkLL4, dkLL5, dkLL9, dkLL10;
	private TextView tv_back;
	@Override
	public View onInitView(LayoutInflater inflater, ViewGroup rootView,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_lc_slb_fragment, null);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		initView(view);
		return view;
	}
	
	private void initView(View view) {
		tv_back = (TextView) view.findViewById(R.id.tv_back);
		tv_save = (TextView) view.findViewById(R.id.tv_save);
		// 得到所有端口的 EditText
		et_dk1_open_time = (EditText) view.findViewById(R.id.et_dk1_open_time);
		et_dk1_close_time = (EditText) view.findViewById(R.id.et_dk1_close_time);
		et_dk2_open_time = (EditText) view.findViewById(R.id.et_dk2_open_time);
		et_dk2_close_time = (EditText) view.findViewById(R.id.et_dk2_close_time);
		et_dk3_open_time = (EditText) view.findViewById(R.id.et_dk3_open_time);
		et_dk3_close_time = (EditText) view.findViewById(R.id.et_dk3_close_time);
		et_dk4_open_time = (EditText) view.findViewById(R.id.et_dk4_open_time);
		et_dk4_close_time = (EditText) view.findViewById(R.id.et_dk4_close_time);
		et_dk5_open_time = (EditText) view.findViewById(R.id.et_dk5_open_time);
		et_dk5_close_time = (EditText) view.findViewById(R.id.et_dk5_close_time);
		et_dk6_open_time = (EditText) view.findViewById(R.id.et_dk6_open_time);
		et_dk6_close_time = (EditText) view.findViewById(R.id.et_dk6_close_time);
		et_dk7_open_time = (EditText) view.findViewById(R.id.et_dk7_open_time);
		et_dk7_close_time = (EditText) view.findViewById(R.id.et_dk7_close_time);
		et_dk8_open_time = (EditText) view.findViewById(R.id.et_dk8_open_time);
		et_dk8_close_time = (EditText) view.findViewById(R.id.et_dk8_close_time);
		et_dk9_open_time = (EditText) view.findViewById(R.id.et_dk9_open_time);
		et_dk9_close_time = (EditText) view.findViewById(R.id.et_dk9_close_time);
		et_dk10_open_time = (EditText) view.findViewById(R.id.et_dk10_open_time);
		et_dk10_close_time = (EditText) view.findViewById(R.id.et_dk10_close_time);
		
		dkLL4 = (LinearLayout) view.findViewById(R.id.dkLL4);
		dkLL5 = (LinearLayout) view.findViewById(R.id.dkLL5);
		dkLL9 = (LinearLayout) view.findViewById(R.id.dkLL9);
		dkLL10 = (LinearLayout) view.findViewById(R.id.dkLL10);
		isDw = (ImageSwitcher) view.findViewById(R.id.is_dw);
		ivDwSub = (ImageView) view.findViewById(R.id.iv_dw_sub);
		ivDwAdd = (ImageView) view.findViewById(R.id.iv_dw_add);
		isDw.setFactory(this);
		// 组件的相关监听事件
		ivDwSub.setOnClickListener(this);
		ivDwAdd.setOnClickListener(this);
		tv_save.setOnClickListener(this);
		tv_back.setOnClickListener(this);
		imgDwIdsMap = new HashMap<String, Integer>();
		imgDwIdsMap.put("1", R.drawable.dw_gf);
		imgDwIdsMap.put("2", R.drawable.dw_df);
		// 默认设置为第一档位
		isDw.setImageResource(imgDwIdsMap.get(String.valueOf(1)));
	}
	*//**
	 * LL是否可见
	 * @param isVisible
	 *//*
	private void setLLVisibility(int isVisible) {
		dkLL4.setVisibility(isVisible);
		dkLL5.setVisibility(isVisible);
		dkLL9.setVisibility(isVisible);
		dkLL10.setVisibility(isVisible);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_dw_sub:
			if (currentDwPos == 1) {
				Toast.makeText(getActivity(), "已经是最低档位", Toast.LENGTH_SHORT).show();
				return;
			}
			if (currentDwPos > 1) {
				isDw.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.left_in));
				isDw.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.right_out));
				currentDwPos--;
				setLLVisibility(View.VISIBLE);
				setDwAndTemp(currentDwPos);
			}
			break;

		case R.id.iv_dw_add:
			if (currentDwPos == 2) {
				Toast.makeText(getActivity(), "已经是最高档位", Toast.LENGTH_SHORT).show();
				return;
			}
			if (currentDwPos < imgDwIdsMap.size()) {
				isDw.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.right_in));
				isDw.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.lift_out));
				currentDwPos++;
				setLLVisibility(View.GONE);
				setDwAndTemp(currentDwPos);
			}
			break;
		case R.id.tv_save:
			showLoading("正在保存");
			count = 0;
			handler.postDelayed(mRunnable, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
			break;
		case R.id.tv_back:
			getFragmentManager().popBackStack();
			break;
		}
		super.onClick(v);
	}
	
	*//**
	 * 通过档位的当前位置，设置档位图片和温度的下限值
	 * 
	 * @param currentPos
	 *            当前档位的位置
	 *//*
	private void setDwAndTemp(int currentDwPos) {
		// 设置当前档位的图片
		isDw.setImageResource(imgDwIdsMap.get(String.valueOf(currentDwPos)));
		setData(currentDwPos);
	}

	*//**
	 * ImageSwicher 填充图片
	 *//*
	@Override
	public View makeView() {
		final ImageView i = new ImageView(getActivity());
		i.setBackgroundColor(0xff000000);
		i.setScaleType(ImageView.ScaleType.CENTER_CROP);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		// 设置ImageSwicher的填充ImageView的背景颜色是透明
		i.setBackgroundColor(Color.TRANSPARENT);
		return i;
	}
	
	Handler handler = new Handler();
	Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			count += 1;
			sendData(count);
			handler.postDelayed(this, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
			if (count == dkNum) {
				handler.removeCallbacks(mRunnable);
			}
		}
		*//**
		 * 发送数据
		 * @param count
		 *//*
		private void sendData(int i) {
			final byte[] byteData = new byte[20];
			byteData[0] = 2;
			byteData[1] = LcCMDConstant.SAVE_SLB;
			byteData[2] = 9;
			byteData[3] = 10;// 一共几个包
			byteData[4] = YzzsCommonUtil.intTobyte(i);// 当前第几个
			byteData[5] = 2;// 5个档位
			byteData[6] = YzzsCommonUtil.intTobyte(currentDwPos);// 当前第几个档位
			if (i == 1) {
				setByteData(byteData, et_dk1_open_time, et_dk1_close_time);
			}
			if (i == 2) {
				setByteData(byteData, et_dk2_open_time, et_dk2_close_time);
			}
			if (i == 3) {
				setByteData(byteData, et_dk3_open_time, et_dk3_close_time);
			}
			if (i == 4) {
				setByteData(byteData, et_dk4_open_time, et_dk4_close_time);
			}
			if (i == 5) {
				setByteData(byteData, et_dk5_open_time, et_dk5_close_time);
			}
			if (i == 6) {
				setByteData(byteData, et_dk6_open_time, et_dk6_close_time);
			}
			if (i == 7) {
				setByteData(byteData, et_dk7_open_time, et_dk7_close_time);
			}
			if (i == 8) {
				setByteData(byteData, et_dk8_open_time, et_dk8_close_time);
			}
			if (i == 9) {
				setByteData(byteData, et_dk9_open_time, et_dk9_close_time);
			}
			if (i == 10) {
				setByteData(byteData, et_dk10_open_time, et_dk10_close_time);
			}
			IndexActivity.mBluetoothLeService.WriteValue(byteData);
		}
	};
	*//**
	 * 设置上传的et值
	 * @param byteData
	 * @param et_open
	 * @param et_close
	 *//*
	private void setByteData(byte[] byteData,EditText et_open,EditText et_close) {
		byteData[7] = YzzsCommonUtil.intTobyte(Integer.valueOf(et_open.getText().toString()) / 256);// 开时间1
		byteData[8] = YzzsCommonUtil.intTobyte(Integer.valueOf(et_open.getText().toString()) % 256);// 开时间2
		byteData[9] = YzzsCommonUtil.intTobyte(Integer.valueOf(et_close.getText().toString()) / 256);// 关时间2
		byteData[10] = YzzsCommonUtil.intTobyte(Integer.valueOf(et_close.getText().toString()) % 256);// 关时间2
	}
	*//**
	 * 设置数据
	 * @param currentDwPos2
	 *//*
	private void setData(int currentDwPos) {
		String data = "";
		if (currentDwPos == 1) {
			data = mSpUtil.getGfData();
		}
		if (currentDwPos == 2) {
			data = mSpUtil.getDfData();
		}
		int m = 3;
		et_dk1_open_time.setText(Integer.valueOf(data.substring(0*m, m)) + "");
		et_dk1_close_time.setText(Integer.valueOf(data.substring(m, 2*m)) + "");

		et_dk2_open_time.setText(Integer.valueOf(data.substring(2*m, 3*m)) + "");
		et_dk2_close_time.setText(Integer.valueOf(data.substring(3*m, 4*m)) + "");

		et_dk3_open_time.setText(Integer.valueOf(data.substring(4*m, 5*m)) + "");
		et_dk3_close_time.setText(Integer.valueOf(data.substring(5*m, 6*m)) + "");

		et_dk4_open_time.setText(Integer.valueOf(data.substring(6*m, 7*m)) + "");
		et_dk4_close_time.setText(Integer.valueOf(data.substring(7*m, 8*m)) + "");

		et_dk5_open_time.setText(Integer.valueOf(data.substring(8*m, 9*m)) + "");
		et_dk5_close_time.setText(Integer.valueOf(data.substring(9*m, 10*m)) + "");

		et_dk6_open_time.setText(Integer.valueOf(data.substring(10*m, 11*m)) + "");
		et_dk6_close_time.setText(Integer.valueOf(data.substring(11*m, 12*m)) + "");

		et_dk7_open_time.setText(Integer.valueOf(data.substring(12*m, 13*m)) + "");
		et_dk7_close_time.setText(Integer.valueOf(data.substring(13*m, 14*m)) + "");
		
		et_dk8_open_time.setText(Integer.valueOf(data.substring(14*m, 15*m)) + "");
		et_dk8_close_time.setText(Integer.valueOf(data.substring(15*m, 16*m)) + "");

		et_dk9_open_time.setText(Integer.valueOf(data.substring(16*m, 17*m)) + "");
		et_dk9_close_time.setText(Integer.valueOf(data.substring(17*m, 18*m)) + "");

		et_dk10_open_time.setText(Integer.valueOf(data.substring(18*m, 19*m)) + "");
		et_dk10_close_time.setText(Integer.valueOf(data.substring(19*m, 20*m)) + "");
	}
	@Override
	public void onPause() {
		MessageReceiver.ehList.remove(this);
		super.onPause();
	}

	@Override
	public void onResume() {
		MessageReceiver.ehList.add(this);
		currentDwPos = 1;
		setData(currentDwPos);
		//setLLVisibility(View.VISIBLE);
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
		slb_bcResp(message);
	}
	
	*//**
	 * 保存水料比数据保存反馈
	 * @param message
	 *//*
	private void slb_bcResp(byte[] message) {
		if (message[0] == 2) {//是不是分栏站的命令
			if (message[1] == LcCMDConstant.SAVE_SLB) {//保存水料比
				dismissLoading();
				if (message[5] == 0) {//保存成功
					saveData2Sp(currentDwPos);
					ToastMsg(getActivity(), "保存成功");
				} else {//保存失败
					ToastMsg(getActivity(), "保存失败,请重试");
				}
			}
		}
	}
	*//**
	 * 保存成功后存入SP
	 * @param currentDwPos2
	 *//*
	private void saveData2Sp(int currentDwPos2) {
		StringBuffer data = new StringBuffer();
		String dk1 = YzzsCommonUtil.formatStringAdd0(et_dk1_open_time.getText().toString(), 3, 1)
				+ YzzsCommonUtil.formatStringAdd0(et_dk1_close_time.getText().toString(), 3, 1);

		String dk2 = YzzsCommonUtil.formatStringAdd0(et_dk2_open_time.getText().toString(), 3, 1)
				+ YzzsCommonUtil.formatStringAdd0(et_dk2_close_time.getText().toString(), 3, 1);

		String dk3 = YzzsCommonUtil.formatStringAdd0(et_dk3_open_time.getText().toString(), 3, 1)
				+ YzzsCommonUtil.formatStringAdd0(et_dk3_close_time.getText().toString(), 3, 1);

		String dk4 = YzzsCommonUtil.formatStringAdd0(et_dk4_open_time.getText().toString(), 3, 1)
				+ YzzsCommonUtil.formatStringAdd0(et_dk4_close_time.getText().toString(), 3, 1);

		String dk5 = YzzsCommonUtil.formatStringAdd0(et_dk5_open_time.getText().toString(), 3, 1)
				+ YzzsCommonUtil.formatStringAdd0(et_dk5_close_time.getText().toString(), 3, 1);

		String dk6 = YzzsCommonUtil.formatStringAdd0(et_dk6_open_time.getText().toString(), 3, 1)
				+ YzzsCommonUtil.formatStringAdd0(et_dk6_close_time.getText().toString(), 3, 1);

		String dk7 = YzzsCommonUtil.formatStringAdd0(et_dk7_open_time.getText().toString(), 3, 1)
				+ YzzsCommonUtil.formatStringAdd0(et_dk7_close_time.getText().toString(), 3, 1);
		
		String dk8 = YzzsCommonUtil.formatStringAdd0(et_dk8_open_time.getText().toString(), 3, 1)
				+ YzzsCommonUtil.formatStringAdd0(et_dk8_close_time.getText().toString(), 3, 1);

		String dk9 = YzzsCommonUtil.formatStringAdd0(et_dk9_open_time.getText().toString(), 3, 1)
				+ YzzsCommonUtil.formatStringAdd0(et_dk9_close_time.getText().toString(), 3, 1);

		String dk10 = YzzsCommonUtil.formatStringAdd0(et_dk10_open_time.getText().toString(), 3, 1)
				+ YzzsCommonUtil.formatStringAdd0(et_dk10_close_time.getText().toString(), 3, 1);
		data.append(dk1 + dk2 + dk3 + dk4 + dk5 + dk6 + dk7 + dk8 + dk9 + dk10);
		if (currentDwPos == 1) {
			mSpUtil.setGfData(data.toString());
		}
		if (currentDwPos == 2) {
			mSpUtil.setDfData(data.toString());
		}
		data.delete(0, data.length());
	}
}
*/