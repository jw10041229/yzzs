package com.huimv.yzzs.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huimv.android.basic.util.CommonUtil;
import com.huimv.android.basic.widget.switchButton.SwitchButton;
import com.huimv.yzzs.R;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.model.SbbdData;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class SbbdListAdapter extends BaseAdapter{

	// 开关状态
	private static final String KGZT_OPEN = "1"; // 打开
	private static final String KGZT_CLOSE = "0"; // 关闭
	
	// sbtn 在 Item 中的位置：1 - 使能开关；2 - 霍尔开关
	private static final int SBTN_POS_SNKG = 1;
	private static final int SBTN_POS_HEKG = 2;
	private static final int SBTN_POS_FJISBINGDING = 3;
	private class ViewHolder {
		ImageView ivIcon; // 图标
		TextView tvSbxh; // 设备序号
		EditText etGlz; // 功率
		SwitchButton sbtnSn; // 使能
		SwitchButton sbtnHe; // 霍尔
		SwitchButton sbtnFjIsBingding; //风机是否绑定
	}

	private Context context;
	private List<SbbdData> mHk_bdDataList = new ArrayList<>();

	private int index = -1; // 记录listveiw中的哪一行 item

	public SbbdListAdapter(Context context, List<SbbdData> mHk_bdDataList) {
		this.context = context;
		this.mHk_bdDataList = mHk_bdDataList;
	}

	@Override
	public int getCount() {
		return mHk_bdDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mHk_bdDataList.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.hkbd_list_item, parent,false);
			holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
			holder.tvSbxh = (TextView) convertView.findViewById(R.id.tv_sbxh);
			holder.etGlz = (EditText) convertView.findViewById(R.id.et_glz);
			holder.sbtnSn = (SwitchButton) convertView.findViewById(R.id.sbtn_sn);
			holder.sbtnHe = (SwitchButton) convertView.findViewById(R.id.sbtn_he);
			holder.sbtnFjIsBingding = (SwitchButton) convertView.findViewById(R.id.sbtn_fj_is_bingding);
			// 将 item 的位置保存
			holder.ivIcon.setTag(position);
			holder.etGlz.setTag(position);
			holder.sbtnSn.setTag(position);
			holder.sbtnHe.setTag(position);
			holder.sbtnFjIsBingding.setTag(position);
			// 设置 et 的触摸事件
			holder.etGlz.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					index = position;
					return false;
				}
			});
			// 增加各组件的监听事
			initOnlisteners(holder);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.ivIcon.setTag(position);
			holder.etGlz.setTag(position);
			holder.sbtnSn.setTag(position);
			holder.sbtnHe.setTag(position);
			holder.sbtnFjIsBingding.setTag(position);
		}
		SbbdData mHk_bdData = mHk_bdDataList.get(position);
		String sblx = mHk_bdData.getSbLx(); // 设备类型
		String sbxh = mHk_bdData.getSbXh(); // 设备序号
		String glz = mHk_bdData.getGlz(); // 采样间隔
		String snkg = mHk_bdData.getTdKg(); // 传感器地址
		String hekg = mHk_bdData.getHeKg(); // 开关状态
		String fjIsBd = mHk_bdData.getFjIsBd(); // 风机绑定开关

		// 图标
		if (XtAppConstant.SBBD_SBLX_FJ.equals(sblx)) {
			holder.ivIcon.setImageResource(R.drawable.fj_on);
		}
		if (XtAppConstant.SBBD_SBLX_SL.equals(sblx)) {
			holder.ivIcon.setImageResource(R.drawable.sl_on);
		}
		
		if (XtAppConstant.SBBD_SBLX_QK_SL.equals(sblx)) {
			holder.ivIcon.setImageResource(R.drawable.sl_on);
		}
		// 料线
		if (XtAppConstant.SBBD_SBLX_LX.equals(sblx)) {
			holder.ivIcon.setImageResource(R.drawable.lx_on);
		}
		//喷雾
		if (XtAppConstant.SBBD_SBLX_PW.equals(sblx)) {
			holder.ivIcon.setImageResource(R.drawable.pw_on);
		}
		//地热
		if (XtAppConstant.SBBD_SBLX_DR.equals(sblx)) {
			holder.ivIcon.setImageResource(R.drawable.dr_on);
		}
		//料槽
		if (XtAppConstant.SBBD_SBLX_LC.equals(sblx)) {
			holder.ivIcon.setImageResource(R.drawable.lx_on);
		}
		//变频风机
		if (XtAppConstant.SBBD_SBLX_BPFJ.equals(sblx)) {
			holder.ivIcon.setImageResource(R.drawable.fj_hz_on);
		}

		//加热器
		if (XtAppConstant.SBBD_SBLX_JRQ.equals(sblx)) {
			holder.ivIcon.setImageResource(R.drawable.jrq_on);
		}
		//卷帘
		if (XtAppConstant.SBBD_SBLX_JL.equals(sblx)) {
			holder.ivIcon.setImageResource(R.drawable.jl_on);
			onClickListener(holder,position);
		}
		//天窗
		if (XtAppConstant.SBBD_SBLX_TC.equals(sblx)) {
			holder.ivIcon.setImageResource(R.drawable.tc_on);
			onClickListener(holder,position);
		}
		if (XtAppConstant.SBBD_SBLX_BPFJ.equals(sblx) ||
				XtAppConstant.SBBD_SBLX_FJ.equals(sblx)) {
			holder.sbtnFjIsBingding.setVisibility(View.VISIBLE);
		} else {
			holder.sbtnFjIsBingding.setVisibility(View.INVISIBLE);
		}
		//onClickListener(holder,position);
		// 设备序号
		holder.tvSbxh.setText(String.valueOf(Integer.valueOf(sbxh)));
		// 功率值
		//holder.etGlz.setText(glz);
		// 使能开关
/*		if (KGZT_OPEN.equals(snkg)) {
			holder.sbtnSn.setChecked(true);
		} else {
			holder.sbtnSn.setChecked(false);
		}*/
		// 霍尔开关
		if (KGZT_OPEN.equals(hekg)) {
			holder.sbtnHe.setChecked(true);
		} else {
			holder.sbtnHe.setChecked(false);
		}
		//风机绑定
		if (KGZT_OPEN.equals(fjIsBd)) {
			holder.sbtnFjIsBingding.setChecked(true);
		} else {
			holder.sbtnFjIsBingding.setChecked(false);
		}
		//holder.etGlz.clearFocus();
/*		if (index != -1 && index == position) {
			// 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
			holder.etGlz.requestFocus();
			holder.etGlz.setSelection(holder.etGlz.getText().length());
		}*/
		return convertView;
	}

	private void initOnlisteners(ViewHolder holder) {
		holder.sbtnHe.setOnCheckedChangeListener(new MyOnCheckedChangeListener(holder, SBTN_POS_HEKG));
		holder.sbtnFjIsBingding.setOnCheckedChangeListener(new MyOnCheckedChangeListener(holder, SBTN_POS_FJISBINGDING));
	}

	/**
	 * 切换按钮改变事件的监听内部类
	 */
	private class MyOnCheckedChangeListener implements OnCheckedChangeListener {
		private ViewHolder holder; 
		private int whichSbtnPos; // 记录哪个sbtn

		private MyOnCheckedChangeListener(ViewHolder holder, int whichSbtnPos) {
			this.holder = holder;
			this.whichSbtnPos = whichSbtnPos;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// 得到在 ListView 中的 item 位置
			int position = (Integer) holder.sbtnSn.getTag();
			SbbdData mHk_bdData = mHk_bdDataList.get(position);
			
			if(buttonView.isChecked()){
				if(whichSbtnPos == SBTN_POS_SNKG){
					mHk_bdData.setTdKg(KGZT_OPEN);
				}
				if(whichSbtnPos == SBTN_POS_HEKG){
					mHk_bdData.setHeKg(KGZT_OPEN);
				}
				if(whichSbtnPos == SBTN_POS_FJISBINGDING){
					mHk_bdData.setFjIsBd(KGZT_OPEN);
				}
			} else {
				if(whichSbtnPos == SBTN_POS_SNKG){
					mHk_bdData.setTdKg(KGZT_CLOSE);
				}
				if(whichSbtnPos == SBTN_POS_HEKG){
					mHk_bdData.setHeKg(KGZT_CLOSE);
				}
				if(whichSbtnPos == SBTN_POS_FJISBINGDING){
					mHk_bdData.setFjIsBd(KGZT_CLOSE);
				}
			}
		}
	}
	//天窗，卷帘点击弹出运行时间设置
	private void onClickListener(ViewHolder holder, final int position) {
		holder.ivIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog(position);
			}
		});
	}

	private void AlertDialog (final int position) {
		final EditText etYxsj = new EditText(context);
		etYxsj.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_PHONE);
		etYxsj.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)}); //最大输入长度
		etYxsj.setGravity(Gravity.CENTER);
		String oldYxcx = mHk_bdDataList.get(position).getYxsj();
		if (CommonUtil.isEmpty(oldYxcx)) {
			oldYxcx = "000";
		}
		etYxsj.setText(oldYxcx);
		new AlertDialog.Builder(context).setMessage("请输入运行时间").setTitle("运行参数").setView(etYxsj)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String content = String.valueOf(Integer.valueOf(etYxsj.getText().toString().trim()));
						if (CommonUtil.isEmpty(content)) {
							Toast.makeText(context, "参数不能为空", Toast.LENGTH_SHORT).show();
							dialog.dismiss();
							return;
						}
						mHk_bdDataList.get(position).setYxsj(YzzsCommonUtil.formatStringAdd0(content,3,1));
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create().show();
	}
}
