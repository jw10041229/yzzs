package com.huimv.yzzs.fragment.general;

import java.util.List;

import com.huimv.yzzs.R;
import com.huimv.yzzs.application.YzzsApplication;
import com.huimv.yzzs.base.YzzsBaseFragment;
import com.huimv.yzzs.constant.LcCMDConstant;
import com.huimv.yzzs.db.entity.Da_bt;
import com.huimv.yzzs.receiver.MessageReceiver;
import com.huimv.yzzs.support.general.BluetoothScanActivitySupport;
import com.huimv.yzzs.util.SharePreferenceUtil;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 蓝牙别名修改
 * 
 * @author zwl
 *
 */
public class LybmFragment extends YzzsBaseFragment {
	private Button changeLybmBtn;
	private EditText etLybm;
	private SharePreferenceUtil mSpUtil;
	private ImageView ivBack;
	private TextView tvBack;
	private String newLymc; // 保存修改的新的蓝牙名称
	private int whichDev = -1;
	@Override
	public View onYzzsInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_lybm_fragment, null);
		mSpUtil = YzzsApplication.getInstance().getSpUtil();
		initView(view);
		initData();
		return view;
	}

	private void initData() {
		whichDev = getArguments().getInt("whichDev");
	}

	private void initView(View view) {
		ivBack = (ImageView) view.findViewById(R.id.iv_back);
		tvBack = (TextView) view.findViewById(R.id.tv_back);
		etLybm = (EditText) view.findViewById(R.id.lybmEt);
		etLybm.setInputType(InputType.TYPE_CLASS_NUMBER); // 输入类型
		etLybm.setInputType(InputType.TYPE_CLASS_TEXT); // 输入类型
		etLybm.setFilters(new InputFilter[] { new InputFilter.LengthFilter(11) }); // 最大输入长度
		// 从数据库中获取蓝牙别名
		String lybm = BluetoothScanActivitySupport.getLybmByLydz(getActivity(), mSpUtil.getLydz());
		// 如果蓝牙别名为null，lymcEt 也显示为 null，即：没有设置过蓝牙别名 --- 直接显示蓝牙名称
		if ("".equals(lybm) || lybm == null) {
			etLybm.setText(mSpUtil.getLymc());
		} else {
			etLybm.setText(lybm); // 存在蓝牙别名，则直接显示蓝牙别名
		}
		changeLybmBtn = (Button) view.findViewById(R.id.changeLybmBtn);
		ivBack.setOnClickListener(this);
		tvBack.setOnClickListener(this);
		changeLybmBtn.setOnClickListener(this);
	}

	@Override
	public void onPause() {
		MessageReceiver.ehList.remove(this);
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			getFragmentManager().popBackStack();
			break;
		case R.id.changeLybmBtn:
			showLoading("正在保存蓝牙别名...");
			saveNewLybm2DB(); // 将修改的新别名保存到本地数据库
			break;
		}
		super.onClick(v);
	}

	/**
	 * 将新的蓝牙别名保存到数据库
	 */
	private void saveNewLybm2DB() {
		if (whichDev != LcCMDConstant.LC_SBBZ) {
			//TODO 料槽这里赋值
		}
		String lcid = "lcid001"; // 料槽id
		String sblx = "sblx001"; // 设备类型
		String jqid = mSpUtil.getJqid(); // 机器id
		String lydz = mSpUtil.getLydz();// 蓝牙地址
		// 新的蓝牙名称
		newLymc = etLybm.getText().toString().trim();// 新蓝牙别名
		if ("".equals(newLymc) || newLymc == null) {
			ToastMsg(getActivity(), "蓝牙别名不能为空");
		} else {
			boolean isExist = false; // 标记这个蓝牙是否已经设置过别名，即在数据库是否已经存在
			List<Da_bt> daBtList = BluetoothScanActivitySupport.getAllBt(getActivity());
			Da_bt newDaBt = new Da_bt();
			newDaBt.setJqid(jqid);
			newDaBt.setSblx(sblx);
			newDaBt.setLcid(lcid);
			newDaBt.setLydz(lydz);
			newDaBt.setLybm(newLymc);
			newDaBt.setSjbz("2"); // 数据被修改或新的别名数据，则数据标志位置 2
			for (Da_bt daBt : daBtList) {
				if (lydz.equals(daBt.getLydz())) {
					isExist = true;
				}
			}
			long result;
			if (isExist) {
				// TODO 此蓝牙在数据库中，已经设置过别名，更新
				long id = BluetoothScanActivitySupport.getIdByLymc(getActivity(), lydz);
				result = BluetoothScanActivitySupport.updateBtById(getActivity(), newDaBt, id);
			} else {
				// 此蓝牙在数据库中，没有设置过别名，直接保存
				result = BluetoothScanActivitySupport.saveBt2DB(getActivity(), newDaBt);
			}
			if (result != 0) {
				dismissLoading();
				ToastMsg(getActivity(), "蓝牙别名保存成功");
			}
		}
	}
}