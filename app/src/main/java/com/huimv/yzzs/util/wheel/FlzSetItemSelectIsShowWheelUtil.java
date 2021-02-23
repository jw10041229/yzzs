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
 * 区分滚轮选项的item内容；1：普通用户看到的选项；2：调试人员能看到的选项；
 * @author jiangwei
 *
 */
public class FlzSetItemSelectIsShowWheelUtil {
	private Activity context;
	private String titleStr;
	private int expenses_id;
	private int itemArrFlag; // 区分滚轮选项的item内容；1：普通用户看到的选项；2：调试人员能看到的选项；
	/**
	 * WheelView控件
	 */
	private WheelView mItem;
	public FlzSetItemSelectIsShowWheelUtil(Activity context, String titleStr, int itemArrFlag, OnItemConfirmClickSetListener mOnItemConfirmClickSetListener) {
		this.context = context;
		this.titleStr = titleStr;
		this.itemArrFlag = itemArrFlag;
		this.mOnItemConfirmClickSetListener = mOnItemConfirmClickSetListener;  
	}
	//创建接口  
    public interface OnItemConfirmClickSetListener {
        void onItemConfirm(int position);
    } 
	 //声明接口对象  
    private OnItemConfirmClickSetListener mOnItemConfirmClickSetListener;  
    //设置监听器 也就是实例化接口  
/*    public void setOnClickListener(final OnConfirmClickSetListener mOnConfirmClickSetListener) {  
        this.mOnConfirmClickSetListener = mOnConfirmClickSetListener;  
    } */
	public void showDialog() {
		expenses_id = 0;
		View view = context.getLayoutInflater().inflate(
				R.layout.wheel_citys, null);
		mItem = (WheelView) view.findViewById(R.id.id_province);
		if(itemArrFlag == 1){ // 普通用户
			mItem.setViewAdapter(new ArrayWheelAdapter<>(context, context.getResources().getStringArray(R.array.flz_set_item_array_customer)));
		} else if(itemArrFlag == 2){ // 调试人员
			mItem.setViewAdapter(new ArrayWheelAdapter<>(context, context.getResources().getStringArray(R.array.flz_set_item_array_debug)));
		}
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
							if (mOnItemConfirmClickSetListener != null) {  
								mOnItemConfirmClickSetListener.onItemConfirm(expenses_id);
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
