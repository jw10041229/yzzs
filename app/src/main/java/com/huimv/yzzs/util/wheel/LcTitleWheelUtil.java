package com.huimv.yzzs.util.wheel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import com.huimv.wheel.adapters.ArrayWheelAdapter;
import com.huimv.wheel.widget.OnWheelChangedListener;
import com.huimv.wheel.widget.OnWheelScrollListener;
import com.huimv.wheel.widget.WheelView;
import com.huimv.yzzs.R;



public class LcTitleWheelUtil {
	private Activity context;
	private String titleStr;
	private int expenses_id;
	private String[] items;
	/**
	 * WheelView控件
	 */
	private WheelView mItem;
	public LcTitleWheelUtil(Activity context, String titleStr, String [] items, OnTitleConfirmClickListener mOnTitleConfirmClickListener) {
		this.context = context;
		this.titleStr = titleStr;
		this.items = items;
		this.mOnTitleConfirmClickListener = mOnTitleConfirmClickListener;
	}
	//创建接口  
    public interface OnTitleConfirmClickListener {
        void onTitleConfirm(int position);
    } 
	 //声明接口对象  
    private OnTitleConfirmClickListener mOnTitleConfirmClickListener;
    //设置监听器 也就是实例化接口  
/*    public void setOnClickListener(final OnConfirmClickListener mOnTitleConfirmClickListener) {  
        this.mOnTitleConfirmClickListener = mOnTitleConfirmClickListener;
    } */
	public void showDialog() {
		expenses_id = 0;
		View view = context.getLayoutInflater().inflate(
				R.layout.wheel_citys, null);
		mItem = (WheelView) view.findViewById(R.id.id_province);
		mItem.setViewAdapter(new ArrayWheelAdapter<>(context, items));
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
	new AlertDialog.Builder(context).setView(view)
			.setTitle(titleStr)
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							if (mOnTitleConfirmClickListener != null) {  
								mOnTitleConfirmClickListener.onTitleConfirm(expenses_id);
							}
							dialog.dismiss();
						}
					})
			.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.dismiss();
						}
					}).create().show();
	}
}
