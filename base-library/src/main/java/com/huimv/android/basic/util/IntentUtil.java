package com.huimv.android.basic.util;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 取得常用打开文件的intent
 * 
 * @author ye
 * 
 */
public class IntentUtil {
	/**
	 * 卸载
	 * 
	 * @param packageName包名
	 * @return
	 */
	public static Intent getUnInstallIntent(String packageName) {
		try {
			Uri packageURI = Uri.parse("package:" + packageName);
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
					packageURI);
			uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			return uninstallIntent;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 安装
	 * 
	 * @param localUrl本地路径
	 * @return
	 */
	public static Intent getInstallIntent(String localUrl) {
		try {
			Uri resource = Uri.parse(localUrl);
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.setDataAndType(resource,
					"application/vnd.android.package-archive");
			return i;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 启动应用app
	 * 
	 * @param packageName
	 * @param activityName
	 * @return
	 */
	public static Intent getLaunchAppIntent(String packageName,
			String activityName) {
		try {
			Intent intent = new Intent();
			intent.setComponent(new ComponentName(packageName, activityName));
			return intent;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 封装请求Gallery的intent
	 * 
	 * @return
	 */
	public static Intent getPhotoPickIntent() {
		try {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			intent.putExtra("crop", "true");
			// intent.putExtra("aspectX", 1);
			// intent.putExtra("aspectY", 2);
			// intent.putExtra("outputX", 277);
			// intent.putExtra("outputY", 373);
			/* 设置比例 1:1 */
			// intent.putExtra("aspectX", 4);
			// intent.putExtra("aspectY", 3);
			intent.putExtra("return-data", true);
			return intent;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 相机
	 * 
	 * @return
	 */
	public static Intent getCamIntent() {
		try {
			return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 设置网络
	 * 
	 * @return
	 */
	public static Intent getNetWorkIntent() {
		Intent intent = null;
		// 判断手机系统的版本 即API大于10 就是3.0或以上版本
		if (android.os.Build.VERSION.SDK_INT > 10) {
			intent = new Intent(
					android.provider.Settings.ACTION_WIRELESS_SETTINGS);
		} else {
			intent = new Intent();
			ComponentName component = new ComponentName("com.android.settings",
					"com.android.settings.WirelessSettings");
			intent.setComponent(component);
			intent.setAction("android.intent.action.VIEW");
		}
		return intent;
	}
}
