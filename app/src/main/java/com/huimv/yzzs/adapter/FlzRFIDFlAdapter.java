package com.huimv.yzzs.adapter;

import java.util.List;

import com.huimv.yzzs.R;
import com.huimv.yzzs.model.FlzRFIDFlData;
import com.huimv.yzzs.util.wheel.ItemSelectWheelUtil;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class FlzRFIDFlAdapter extends BaseAdapter {
	private Context context;
	private String channelName [];
	private List<FlzRFIDFlData> mFlz_RFID_FlDataList;
	public FlzRFIDFlAdapter(Context context, List<FlzRFIDFlData> mFlz_RFID_FlDataList) {
		this.context = context;
		this.mFlz_RFID_FlDataList = mFlz_RFID_FlDataList;
		channelName = context.getResources().getStringArray(R.array.flz_channel_item_array);
	}
	
	/**
	 * 添加
	 */
	public void addRFID () {
		FlzRFIDFlData mFlz_RFID_FlData = new FlzRFIDFlData();
		mFlz_RFID_FlData.setId("0");
		mFlz_RFID_FlData.setChannelTag("1");
		mFlz_RFID_FlData.setRFID("");
		mFlz_RFID_FlDataList.add(mFlz_RFID_FlData);
		notifyData();
	}
	
	/**
	 * 刷新
	 */
	private void notifyData( ) {
		notifyDataSetChanged();
	}
	/**
	 * 设置监听
	 * @param textVew
	 * @param flag
	 */
	private void doOnClickListener(TextView textVew,int flag) {
		textVew.setOnClickListener(new MyClickListener(flag,textVew));
	}
	
	@Override
	public int getCount() {
		return mFlz_RFID_FlDataList.size();
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
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.flz_rfid_cl_item, null);
			viewHolder.tv_id = (TextView) convertView.findViewById(R.id.tv_id);
			viewHolder.tv_tv_channel_tag = (TextView) convertView.findViewById(R.id.tv_channel_tag);
			viewHolder.et_RFID_value = (EditText) convertView.findViewById(R.id.et_RFID_value);
			viewHolder.et_RFID_value.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						whichEt = 1;
						index = (Integer) v.getTag();
					}
					return false;
				}
			});
			viewHolder.et_RFID_value.addTextChangedListener(new EtTextWatcher(viewHolder, 1));
			doOnClickListener(viewHolder.tv_tv_channel_tag, 1);
			//viewHolder.et_RFID_value.setTag(position);
			//viewHolder.tv_tv_channel_tag.setTag(position);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tv_tv_channel_tag.setTag(position);
		viewHolder.et_RFID_value.setTag(position);
		viewHolder.tv_id.setText(String.valueOf(position + 1));
		int channelTag = Integer.valueOf(mFlz_RFID_FlDataList.get(position).getChannelTag());
		if ( 0 < channelTag && channelTag < 4) {
			viewHolder.tv_tv_channel_tag.setText(channelName[channelTag - 1]);
		} else {
			mFlz_RFID_FlDataList.get(position).setChannelTag("1");
			viewHolder.tv_tv_channel_tag.setText(channelName[0]);
		}
		viewHolder.et_RFID_value.setText(mFlz_RFID_FlDataList.get(position).getRFID());
		if(mFlz_RFID_FlDataList.get(position).getRFID().length() !=15) {
			viewHolder.et_RFID_value.setTextColor(ContextCompat.getColor(context,R.color.red));
			viewHolder.et_RFID_value.setTextColor(ContextCompat.getColor(context,R.color.red));
		} else {
			viewHolder.et_RFID_value.setTextColor(ContextCompat.getColor(context,R.color.light_blue));
		}
		if (index != -1 && index == position) {
			// 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
			if (whichEt != -1 && whichEt == 1) {
				viewHolder.et_RFID_value.requestFocus();
				viewHolder.et_RFID_value.setSelection(viewHolder.et_RFID_value.getText().length());
			}
		}
		//System.out.println("position= " + position);
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
			int position = (Integer) viewHolder.et_RFID_value.getTag();
			if (whichEditTextPos == 1) {// 第一个Et
				mFlz_RFID_FlDataList.get(position).setRFID(s.toString());
			}
		}
	}
	
	private class MyClickListener implements OnClickListener {
		private TextView textView;
		private int flag = -1;
		private int itemPosition = -1;//第几个item
		public MyClickListener(int flag,View view) {
			this.textView = (TextView) view;
			this.flag = flag;
		}

		@Override
		public void onClick(View v) {
			if(flag == 1) {
				new ItemSelectWheelUtil((Activity)context, "", channelName, new ItemSelectWheelUtil.OnConfirmClickListener() {
					
					@Override
					public void onConfirm(int position, int i) {
						itemPosition = (Integer) textView.getTag();
						if (itemPosition != -1) {
							mFlz_RFID_FlDataList.get(itemPosition).setChannelTag(String.valueOf(position + 1));
							textView.setText(channelName[position]);
						}
					}
				}).showDialog(0, "请选择出口通道");
			}
		}
}
	private class ViewHolder {
		TextView tv_id;
		TextView tv_tv_channel_tag;
		EditText et_RFID_value;
	}
}
