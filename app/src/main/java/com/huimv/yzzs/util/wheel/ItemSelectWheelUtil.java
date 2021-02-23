package com.huimv.yzzs.util.wheel;

import com.huimv.wheel.adapters.ArrayWheelAdapter;
import com.huimv.wheel.widget.OnWheelChangedListener;
import com.huimv.wheel.widget.OnWheelScrollListener;
import com.huimv.wheel.widget.WheelView;
import com.huimv.yzzs.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

public class ItemSelectWheelUtil {
	private Activity context;
	private String titleStr;
	private int expenses_id;
	private String[] item;
	/**
	 * WheelView控件
	 */
	private WheelView mItem;

	public ItemSelectWheelUtil(Activity context, String titleStr, String[] item,
			OnConfirmClickListener mOnConfirmClickListener) {
		this.context = context;
		this.titleStr = titleStr;
		this.item = item;
		this.mOnConfirmClickListener = mOnConfirmClickListener;
	}

	// 创建接口
	public interface OnConfirmClickListener {
		void onConfirm(int position, int i);
	}

	// 声明接口对象
	private OnConfirmClickListener mOnConfirmClickListener;

	// 设置监听器 也就是实例化接口
	/*
	 * public void setOnClickListener(final OnConfirmClickListener
	 * mOnConfirmClickListener) { this.mOnConfirmClickListener =
	 * mOnConfirmClickListener; }
	 */
	public void showDialog(final int i, String title) {
		titleStr = title;
		expenses_id = 0;
		View view = context.getLayoutInflater().inflate(R.layout.wheel_citys, null);
		mItem = (WheelView) view.findViewById(R.id.id_province);
		mItem.setViewAdapter(new ArrayWheelAdapter<>(context, item));
		mItem.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				expenses_id = newValue;
			}
		});
		mItem.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				expenses_id = mItem.getCurrentItem();
			}
		});
		new AlertDialog.Builder(context).setView(view).setTitle(titleStr)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mOnConfirmClickListener != null) {
							mOnConfirmClickListener.onConfirm(expenses_id, i);
						}
						dialog.dismiss();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}
}
