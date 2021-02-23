package com.huimv.android.basic.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.net.FileNameMap;
import java.net.URLConnection;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * 一些关于android的通用操作
 * 
 * @author jw
 * 
 */
// @SuppressLint("NewApi")
public final class AndroidUtil {
	private static final String TAG = AndroidUtil.class.getSimpleName();

	/**
	 * 判断网络连接是否已开
	 * 
	 * @permission ACCESS_NETWORK_STATE
	 * @param context
	 *            activity.getApplicationContext()
	 * @return
	 */
	public static boolean isConn(Context context) {
		boolean bisConnFlag = false;
		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = conManager.getActiveNetworkInfo();
		if (network != null) {
			bisConnFlag = conManager.getActiveNetworkInfo().isAvailable();
		}
		return bisConnFlag;
	}

	/**
	 * 打开设置网络界面
	 * 
	 * @param context
	 *            activity
	 */
	public static void setNetworkMethod(final Context context) {
		// 提示对话框
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("网络设置提示").setMessage("网络连接不可用,是否进行设置?")
				.setPositiveButton("设置", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = IntentUtil.getNetWorkIntent();
						context.startActivity(intent);
						dialog.dismiss();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	/**
	 * 判断sd卡是否可用
	 * 
	 * @return
	 */
	public static boolean isSdAvailable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * 取得sd卡的路径
	 * 
	 * @return
	 */
	public static String getSdPath() {
		String sdpath = Environment.getExternalStorageDirectory()
				+ File.pathSeparator;
		return sdpath;
	}

	/**
	 * SD卡剩余空间
	 * 
	 * @return
	 */
	public static long getSDAvailableSize() {
		// 取得SD卡文件路径
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		// 获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSize();
		// 空闲的数据块的数量
		long freeBlocks = sf.getAvailableBlocks();
		// 返回SD卡空闲大小
		// return freeBlocks * blockSize; //单位Byte
		// return (freeBlocks * blockSize)/1024; //单位KB
		return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
	}

	/**
	 * 取得版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		return verCode;
	}

	/**
	 * 取得版本名称
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		String versionName = null;
		try {
			versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		return versionName;
	}

	/***
	 * 动态设置listview的高度
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		try {
			ListAdapter listAdapter = listView.getAdapter();
			if (listAdapter == null) {
				return;
			}
			int totalHeight = 0;
			for (int i = 0; i < listAdapter.getCount(); i++) {
				View listItem = listAdapter.getView(i, null, listView);
				if (listItem == null) {
					continue;
				}
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}
			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight
					+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
			// params.height += 5;// if without this statement,the listview will
			// be
			// a
			// little short
			// listView.getDividerHeight()获取子项间分隔符占用的高度
			// params.height最后得到整个ListView完整显示需要的高度
			listView.setLayoutParams(params);
		} catch (Exception e) {
		}
	}

	/**
	 * 取得状态栏高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	/**
	 * 将view转化为bitmap
	 * 
	 * @param view
	 * @return
	 */
	public static Bitmap convertViewToBitmap(View view) {
		view.setDrawingCacheEnabled(true);
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}

	public static void openPhone(Context context, String phone) {
		Intent intent = new Intent(Intent.ACTION_DIAL,
				Uri.parse("tel:" + phone));
		context.startActivity(intent);
	}

	// 获取图片缩小的图片
	public static Bitmap scaleBitmap(String src, int max) {
		// 获取图片的高和宽
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 这一个设置使 BitmapFactory.decodeFile获得的图片是空的,但是会将图片信息写到options中
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(src, options);
		// 计算比例 为了提高精度,本来是要640 这里缩为64
		max = max / 10;
		int be = options.outWidth / max;
		if (be % 10 != 0)
			be += 10;
		be = be / 10;
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;
		// 设置可以获取数据
		options.inJustDecodeBounds = false;
		// 获取图片
		return BitmapFactory.decodeFile(src, options);
	}

	public static void installApk(Context context, String fullFileName) {
		File apkfile = new File(fullFileName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		context.startActivity(i);
	}

	public static void installApkByUri(Context context, Uri uri) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(uri, "application/vnd.android.package-archive");
		context.startActivity(i);
	}

	/**
	 * 根据bitmap生成file
	 * 
	 * @param b
	 * @return
	 */
	public static File getFileFromBitmap(Bitmap b) {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ByteArrayOutputStream baos = null;
		File resultFile = null;
		try {
			baos = new ByteArrayOutputStream();
			b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] byteArray = baos.toByteArray();
			resultFile = File.createTempFile("head", ".jpg");
			fos = new FileOutputStream(resultFile);
			bos = new BufferedOutputStream(fos);
			bos.write(byteArray);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return resultFile;
	}

	/**
	 * 根据值, 设置spinner默认选中:
	 * 
	 * @param spinner
	 * @param value
	 */
	public static void setSpinnerItemSelectedByValue(Spinner spinner,
			String value) {
		SpinnerAdapter apsAdapter = spinner.getAdapter(); // 得到SpinnerAdapter对象
		int k = apsAdapter.getCount();
		for (int i = 0; i < k; i++) {
			if (value.equals(apsAdapter.getItem(i).toString())) {
				spinner.setSelection(i, true);// 默认选中项
				break;
			}
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param url
	 * @param downloadTitle
	 * @param fileName
	 * @return
	 */
	public static long downloadFile(Context applicationContext, String url,
			String downloadTitle, String fileName) {
		Uri resource = Uri.parse(url);
		Request request = new Request(resource);
		request.setAllowedNetworkTypes(Request.NETWORK_MOBILE
				| Request.NETWORK_WIFI);
		request.setAllowedOverRoaming(false);
		// 设置文件类型
		MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
		String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap
				.getFileExtensionFromUrl(url));
		request.setMimeType(mimeString);
		// 在通知栏中显示
		// request.setShowRunningNotification(true);
		request.setVisibleInDownloadsUi(true);
		// sdcard的目录下的download文件夹
		request.setDestinationInExternalPublicDir(
				Environment.DIRECTORY_DOWNLOADS, fileName);
		request.setTitle(downloadTitle);
		DownloadManager downloadManager = (DownloadManager) applicationContext
				.getSystemService(Context.DOWNLOAD_SERVICE);
		return downloadManager.enqueue(request);
	}

	/**
	 * 取消下载
	 * 
	 * @param applicationContext
	 * @param downloadId
	 * @return
	 */
	public static boolean cancleDownload(Context applicationContext,
			long downloadId) {
		DownloadManager downloadManager = (DownloadManager) applicationContext
				.getSystemService(Context.DOWNLOAD_SERVICE);
		int i = downloadManager.remove(downloadId);
		if (i > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static String getMimeType(String fileUrl) {
		try {
			FileNameMap fileNameMap = URLConnection.getFileNameMap();
			return fileNameMap.getContentTypeFor(fileUrl);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 判断是否为wifi
	 * 
	 * @param applicationContext
	 * @param downloadId
	 * @return
	 */
	public static boolean isWifi(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}
}
