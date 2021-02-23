package com.huimv.android.basic.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.huimv.android.basic.widget.LoadingDialog;


public class BaseActivity extends AppCompatActivity implements
		OnClickListener, OnDismissListener {
	private LoadingDialog loading;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onHomeBackBtnClick();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		//alert("未定义点击事件");
	}

	@Override
	public void onDismiss(DialogInterface dialog) {

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (loading != null) {
			if (loading.isShowing()) {
				loading.dismiss();
			}
			loading = null;
		}
		super.onDestroy();
	}

	protected void onHomeBackBtnClick() {
		this.finish();
	}

	private LoadingDialog getLoadingDialog() {
		if (loading == null) {
			loading = new LoadingDialog(this);
			loading.setOnDismissListener(this);
		}
		return loading;
	}

	protected void alert(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	public void showLoading() {
		showLoading("加载中...");
	}

	public SharedPreferences getSp(String sp_name,Context context) {
		return context.getSharedPreferences(sp_name, Context.MODE_PRIVATE);
	}
	
	/**
	 * 默认情况下,允许返回键可以取消,点击屏幕其他地方不允许取消
	 * 
	 * @param msg
	 */
	public void showLoading(String msg) {
		showLoading(msg, false, true);
	}

	public void showLoading(String msg, boolean cancleAble,
			boolean backToCancleAble) {
		LoadingDialog dialog = getLoadingDialog();
		dialog.setCancelable(cancleAble);
		dialog.setBackToCancle(backToCancleAble);
		dialog.setProgressText(msg);
		dialog.show();
	}

	public void dismissLoading() {
		if (loading != null) {
			loading.dismiss();
		}
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
