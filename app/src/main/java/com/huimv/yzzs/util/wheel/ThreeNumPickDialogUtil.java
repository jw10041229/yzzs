package com.huimv.yzzs.util.wheel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import com.huimv.wheel.adapters.ArrayWheelAdapter;
import com.huimv.wheel.widget.OnWheelChangedListener;
import com.huimv.wheel.widget.WheelView;
import com.huimv.yzzs.R;


public class ThreeNumPickDialogUtil {
	private String mProvinceNum = "0";
	private String mCityNum = "0";
	private String mAreaNum = "0";
	private Activity context;
	private String titleStr;
	/**
	 * 省的WheelView控件
	 */
	private WheelView mProvince;
	/**
	 * 市的WheelView控件
	 */
	private WheelView mCity;
	/**
	 * 区的WheelView控件
	 */
	private WheelView mArea;
	//创建接口  
    public interface OnContinueTimeClickListener {
        void OnContinueTime(String time);
    } 
	 //声明接口对象  
    private OnContinueTimeClickListener mOnContinueTimeClickListener;

	public ThreeNumPickDialogUtil(Activity context, String titleStr, OnContinueTimeClickListener mOnContinueTimeClickListener) {
		this.context = context;
		this.mOnContinueTimeClickListener = mOnContinueTimeClickListener;
		this.titleStr = titleStr;
	}
	public void showDialog() {
		View view = context.getLayoutInflater().inflate(
				R.layout.wheel_citys, null);
		mProvince = (WheelView) view.findViewById(R.id.id_province);
		mCity = (WheelView) view.findViewById(R.id.id_city);
		mArea = (WheelView) view.findViewById(R.id.id_area);
		mCity.setVisibility(View.VISIBLE);
		mArea.setVisibility(View.VISIBLE);
		String array [] = context.getResources().getStringArray(R.array.qk_ycsc_cxsj_array);
		mProvince.setViewAdapter(new ArrayWheelAdapter<>(context, array));
		mCity.setViewAdapter(new ArrayWheelAdapter<>(context, array));
		mArea.setViewAdapter(new ArrayWheelAdapter<>(context, array));

		mProvince.addChangingListener(new OnWheelChangedListener() {
			
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				mProvinceNum = newValue + "";
			}
		});
	mCity.addChangingListener(new OnWheelChangedListener() {
			
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				mCityNum = newValue + "";
			}
		});
	mArea.addChangingListener(new OnWheelChangedListener() {
		
		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			mAreaNum = newValue + "";
		}
	});
	new AlertDialog.Builder(context).setView(view)
			.setTitle(titleStr)
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							//editText.setText(mProvinceNum + mCityNum + mAreaNum);
							mOnContinueTimeClickListener.OnContinueTime(mProvinceNum + mCityNum + mAreaNum);
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
