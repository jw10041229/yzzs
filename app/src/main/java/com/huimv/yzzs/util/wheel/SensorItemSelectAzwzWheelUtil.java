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
/**
 * 传感器安装位置
 * @author jiangwei
 *
 */
public class SensorItemSelectAzwzWheelUtil {
	private Activity context;
	private String titleStr;
	private int wheelItemArrId; // arr 资源文件id
	private int expenses_id;
	/**
	 * WheelView控件
	 */
	private WheelView mItem;

	public SensorItemSelectAzwzWheelUtil(Activity context, String titleStr, int wheelItemArrId,
			OnConfirmClickListener mOnConfirmClickListener) {
		this.context = context;
		this.titleStr = titleStr;
		this.wheelItemArrId = wheelItemArrId;
		this.mOnConfirmClickListener = mOnConfirmClickListener;
	}

	// 创建接口
	public interface OnConfirmClickListener {
		/**
		 * wheel 点击事件
		 * 
		 * @param wheelItemPosition
		 *            wheel 中各item的位置
		 * @param showDialogPos
		 *            传感器添加和删除的标记位（由showDialog作为参数传递）
		 * @param whichItemPos
		 *            点击事件中 图标和安装位置 的标记位（由showDialog作为参数传递）
		 */
		void onConfirm(int wheelItemPosition, int showDialogPos, int whichItemPos);
	}

	// 声明接口对象
	private OnConfirmClickListener mOnConfirmClickListener;

	// 设置监听器 也就是实例化接口
	/*
	 * public void setOnClickListener(final OnConfirmClickListener
	 * mOnConfirmClickListener) { this.mOnConfirmClickListener =
	 * mOnConfirmClickListener; }
	 */
	/**
	 * 
	 * @param listViewItemPosition
	 *            对应 ListView 中的 pisition
	 * @param whichItemPos
	 *            用于区分是哪个组件被点击
	 */
	public void showDialog(final int listViewItemPosition, final int whichItemPos,final String title) {
		titleStr = title;
		expenses_id = 0;
		View view = context.getLayoutInflater().inflate(R.layout.wheel_citys, null);
		mItem = (WheelView) view.findViewById(R.id.id_province);
		mItem.setViewAdapter(
				new ArrayWheelAdapter<>(context, context.getResources().getStringArray(this.wheelItemArrId)));
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
							mOnConfirmClickListener.onConfirm(expenses_id, listViewItemPosition, whichItemPos);
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
