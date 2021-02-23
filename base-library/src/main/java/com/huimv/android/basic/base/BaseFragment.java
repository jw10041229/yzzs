package com.huimv.android.basic.base;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.huimv.android.basic.widget.LoadingDialog;

public abstract class BaseFragment extends Fragment implements OnClickListener, OnDismissListener {
	private View rootView;// 缓存Fragment view
	private LoadingDialog loading;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = onInitView(inflater, viewGroup, savedInstanceState);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;
	}

	public abstract View onInitView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState);

	private LoadingDialog getLoadingDialog() {
		if (loading == null) {
			loading = new LoadingDialog(this.getActivity());
			loading.setOnDismissListener(this);
		}
		return loading;
	}

	public SharedPreferences getSp(String sp_name) {
		return getActivity().getSharedPreferences(sp_name, Context.MODE_PRIVATE);
	}

	public SharedPreferences getDefaultSp() {
		return getSp("default");
	}

	@Override
	public void onClick(View v) {
		// alert("未定义点击事件");
	}

	protected void alert(String msg) {
		Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT).show();
	}

	public void showLoading() {
		showLoading("加载中...");
	}

	/**
	 * 默认情况下,允许返回键可以取消,点击屏幕其他地方不允许取消
	 * 
	 * @param msg
	 */
	public void showLoading(String msg) {
		showLoading(msg, false, true);
	}

	public void showLoading(String msg, boolean cancleAble, boolean backToCancleAble) {
		LoadingDialog dialog = getLoadingDialog();
		dialog.setCancelable(cancleAble);
		dialog.setBackToCancle(backToCancleAble);
		dialog.setProgressText(msg);
		dialog.show();
	}

	public void dismissLoading() {
		getLoadingDialog().dismiss();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {

	}

	public void ToastMsg(Context context, String msg) {
		showToast(context, msg, 1000);
	}

	/*
	 * 显示toast，自己定义显示长短。 param1:activity 传入context param2:word 我们需要显示的toast的内容
	 * param3:time length long类型，我们传入的时间长度（如500）
	 */
	private void showToast(final Context context, final String word, final long time) {
		((Activity) context).runOnUiThread(new Runnable() {
			public void run() {
				final Toast toast = Toast.makeText(context, word, Toast.LENGTH_LONG);
				toast.show();
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						toast.cancel();
					}
				}, time);
			}
		});
	}
}
