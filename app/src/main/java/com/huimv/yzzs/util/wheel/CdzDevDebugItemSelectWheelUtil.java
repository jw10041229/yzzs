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

public class CdzDevDebugItemSelectWheelUtil {
	private Activity context;
	private String titleStr;
	private int expenses_id;
	/**
	 * WheelView控件
	 */
	private WheelView mItem;
	public CdzDevDebugItemSelectWheelUtil(Activity context, String titleStr, onDevDebugConfirmClickListener mOnConfirmClickListener) {
		this.context = context;
		this.titleStr = titleStr;
		this.mOnConfirmClickListener = mOnConfirmClickListener;  
	}
	//创建接口  
    public interface onDevDebugConfirmClickListener {
         void onDevDebugConfirm(int position);
    } 
	 //声明接口对象  
    private onDevDebugConfirmClickListener mOnConfirmClickListener;  
    //设置监听器 也就是实例化接口  
/*    public void setOnClickListener(final OnConfirmClickListener mOnConfirmClickListener) {  
        this.mOnConfirmClickListener = mOnConfirmClickListener;  
    } */
	public void showDialog() {
		expenses_id = 0;
		View view = context.getLayoutInflater().inflate(
				R.layout.wheel_citys, null);
		mItem = (WheelView) view.findViewById(R.id.id_province);
		mItem.setViewAdapter(new ArrayWheelAdapter<>(context, context.getResources().getStringArray(R.array.cdz_debug_wheel_string_item_array)));
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
							if (mOnConfirmClickListener != null) {  
								mOnConfirmClickListener.onDevDebugConfirm(expenses_id);
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
