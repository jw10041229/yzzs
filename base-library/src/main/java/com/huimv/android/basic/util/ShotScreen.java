package com.huimv.android.basic.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

public class ShotScreen {
	private static final String TAG = "shotscreen";

	// 截屏
	@SuppressWarnings("deprecation")
	public static Bitmap takeScreenShot(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		Rect rect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		int statusBarHeight = rect.top;
		System.out.println(statusBarHeight);

		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay()
				.getHeight();

		Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, statusBarHeight, width,
				height - statusBarHeight);
		view.destroyDrawingCache();
		return bitmap2;
	}

	private static void savePic(Bitmap bitmap, String filename) {
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(filename);
			if (fileOutputStream != null) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		} catch (FileNotFoundException e) {
			Log.d(TAG, "Exception:FileNotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d(TAG, "IOException:IOException");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param a
	 */
	@SuppressLint("SdCardPath")
	@SuppressWarnings("unused")
	public static void shoot(Activity a) {
		if (android.os.Environment.MEDIA_MOUNTED != "mounted") {
			ShotScreen.savePic(ShotScreen.takeScreenShot(a), "/sdcard/抽样.png");
		} else {
			ShotScreen.savePic(ShotScreen.takeScreenShot(a),
					"/data/data/" + a.getPackageName() + "/抽样.png");
		}
	}
}
