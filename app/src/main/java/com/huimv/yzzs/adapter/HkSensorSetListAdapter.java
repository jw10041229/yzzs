package com.huimv.yzzs.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huimv.android.basic.widget.switchButton.SwitchButton;
import com.huimv.yzzs.R;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.model.HkSensorData;
import com.huimv.yzzs.util.wheel.SensorItemSelectAzwzWheelUtil;

import java.util.List;

public class HkSensorSetListAdapter extends BaseAdapter implements
		SensorItemSelectAzwzWheelUtil.OnConfirmClickListener {
	// 开关状态
	private static final String KGZT_OPEN = "1"; // 打开
	private static final int TV_POS_AZWZ = 2;

	private class ViewHolder {
		ImageView ivIcon;
		TextView tvNo;
		EditText etCyjg;
		EditText etCgqdz;
		SwitchButton sbtn;
		ImageView iv_sensor_location_Icon;
		TextView tv_sensor_location;
	}

	private Context context;
	private List<HkSensorData> mHk_CgqbdDataList;
	private SensorItemSelectAzwzWheelUtil mSensorItemSelectWheelUtil;

	public HkSensorSetListAdapter(Context context, List<HkSensorData> mHk_CgqbdDataList) {
		this.context = context;
		this.mHk_CgqbdDataList = mHk_CgqbdDataList;
	}

	@Override
	public int getCount() {
		return mHk_CgqbdDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mHk_CgqbdDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.cgqbd_list_item, null);
			holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
			holder.tvNo = (TextView) convertView.findViewById(R.id.tv_no);
			holder.etCyjg = (EditText) convertView.findViewById(R.id.et_cyjg);
			holder.etCgqdz = (EditText) convertView.findViewById(R.id.et_cgqdz);
			holder.sbtn = (SwitchButton) convertView.findViewById(R.id.sbtn);
			holder.iv_sensor_location_Icon = (ImageView) convertView.findViewById(R.id.iv_location_icon);
			holder.tv_sensor_location = (TextView) convertView.findViewById(R.id.tv_sensor_location);
			// 将 item 的位置保存
			holder.ivIcon.setTag(position);
			holder.etCyjg.setTag(position);
			holder.etCgqdz.setTag(position);
			holder.sbtn.setTag(position);
			holder.iv_sensor_location_Icon.setTag(position);
			holder.tv_sensor_location.setTag(position);
			// 增加各组件的监听事件（暂时不用）
			 initOnlisteners(holder);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.sbtn.setTag(position);
			holder.etCyjg.setTag(position);
			holder.etCgqdz.setTag(position);
			holder.ivIcon.setTag(position);
			holder.iv_sensor_location_Icon.setTag(position);
			holder.tv_sensor_location.setTag(position);
		}
		HkSensorData mHk_CgqbdData = mHk_CgqbdDataList.get(position);
		String sblx = mHk_CgqbdData.getSblx(); // 设备类型
		String cyjg = mHk_CgqbdData.getCyjg(); // 采样间隔
		String cgqdz = mHk_CgqbdData.getCgqdz(); // 传感器地址
		String kgzt = mHk_CgqbdData.getKgzt(); // 开关状态
		String azwz = mHk_CgqbdData.getAzwz(); // 设备安装位置
		// 图标
		if (XtAppConstant.CGQBD_SBLX_WD.equals(sblx)) {
			holder.ivIcon.setImageResource(R.drawable.icon_wd);
		}
		if (XtAppConstant.CGQBD_SBLX_SD.equals(sblx)) {
			holder.ivIcon.setImageResource(R.drawable.icon_sd);
		}
		if (XtAppConstant.CGQBD_SBLX_AQ.equals(sblx)) {
			holder.ivIcon.setImageResource(R.drawable.icon_aq);
		}
		if (XtAppConstant.CGQBD_SBLX_PH.equals(sblx)) {
			holder.ivIcon.setImageResource(R.drawable.icon_ph);
		}
		if (XtAppConstant.CGQBD_SBLX_DLJCB.equals(sblx)) {
			holder.ivIcon.setImageResource(R.drawable.icon_dljcq);
		}
		// 设备编号（从 1 开始排序）
		holder.tvNo.setText(String.valueOf(Integer.valueOf(position + 1)));
		// 采样间隔
		holder.etCyjg.setText(cyjg);
		// 传感器地址
		holder.etCgqdz.setText(cgqdz);
		// 开关状态
		if (KGZT_OPEN.equals(kgzt)) {
			holder.sbtn.setChecked(true);
		} else {
			holder.sbtn.setChecked(false);
		}
		// 安装位置
		String[] azwzItems = context.getResources().getStringArray(R.array.hk_sensor_item_array_location);
		holder.tv_sensor_location.setText(azwzItems[Integer.valueOf(azwz)]);
		holder.tv_sensor_location.clearFocus();
		return convertView;
	}
	private void initOnlisteners(ViewHolder holder) {
		// 安装位置增加点击事件
		holder.tv_sensor_location.setOnClickListener(
				new MyOnClickListener(holder, TV_POS_AZWZ, "请选择安装位置", R.array.hk_sensor_item_array_location));
	}

	/**
	 * 图标点击事件监听内部类
	 */
	private class MyOnClickListener implements OnClickListener {

		private ViewHolder holder; // 对应 ListView 中的 item 位置
		private int whichItemPos; // 对应 图标和安装位置 的 pos

		MyOnClickListener(ViewHolder holder, int whichItemPos, String titleStr, int wheelItemArrId) {
			mSensorItemSelectWheelUtil = new SensorItemSelectAzwzWheelUtil((Activity) context, titleStr,
					wheelItemArrId, HkSensorSetListAdapter.this);
			this.whichItemPos = whichItemPos;
			this.holder = holder;
		}

		@Override
		public void onClick(View v) {
			// 得到 ListView 中的 item 位置
			int ListViewItemPos = (Integer) holder.ivIcon.getTag();
			mSensorItemSelectWheelUtil.showDialog(ListViewItemPos, whichItemPos,"请选择安装位置");
		}

	}

	@Override
	public void onConfirm(int wheelItemPos, int ListViewItemPos, int whichItemPos) {
		// 切换 安装位置 textview 的点击事件
		if (whichItemPos == TV_POS_AZWZ) {
			SwitchAzwzListener(wheelItemPos, ListViewItemPos);
		}
	}

	/**
	 * 切换安装位置的点击事件
	 * 
	 * @param wheelItemPos
	 *            wheel 中 item 的位置
	 * @param ListViewItemPos
	 *            list中 item 的位置
	 */
	private void SwitchAzwzListener(int wheelItemPos, int ListViewItemPos) {
		HkSensorData mHk_CgqbdData = mHk_CgqbdDataList.get(ListViewItemPos);
		switch (wheelItemPos) {
		case 0:
			mHk_CgqbdData.setAzwz(XtAppConstant.CGQBD_AZWZ_SNKZ);
			break;

		case 1:
			mHk_CgqbdData.setAzwz(XtAppConstant.CGQBD_AZWZ_SNDM);
			break;

		case 2:
			mHk_CgqbdData.setAzwz(XtAppConstant.CGQBD_AZWZ_SWKZ);
			break;

		case 3:
			mHk_CgqbdData.setAzwz(XtAppConstant.CGQBD_AZWZ_SWDM);
			break;

		case 4:
			mHk_CgqbdData.setAzwz(XtAppConstant.CGQBD_AZWZ_SM);
			break;

		case 5:
			mHk_CgqbdData.setAzwz(XtAppConstant.CGQBD_AZWZ_SD);
			break;
		}
		notifyDataSetChanged(); // 刷新数据
	}
}
