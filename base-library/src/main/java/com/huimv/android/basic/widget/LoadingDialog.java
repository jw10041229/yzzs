package com.huimv.android.basic.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.huimv.android.basic.R;


/**
 * 加载中的对话框,背景透明
 * 
 * @author ye
 * 
 */
public class LoadingDialog extends Dialog {
	private Context context;
	private TextView progressText;

	public LoadingDialog(Context context) {
		this(context, R.style.leaf_loading_dialog);
	}

	public LoadingDialog(Context context, int theme) {
		super(context, theme);
		setCancelable(false);
		this.context = context;
		ini();
	}

	public void ini() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.leaf_item_loading_dialog, null);
		setContentView(view);
		progressText = (TextView) this.findViewById(R.id.leaf_progress_text);
	}

	public void setProgressText(String text) {
		progressText.setText(text);
	}

	private boolean backToCancle = true;

	public void setBackToCancle(boolean flag) {
		backToCancle = flag;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (backToCancle) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {// 按下键盘上返回按钮
				this.dismiss();
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}
}
