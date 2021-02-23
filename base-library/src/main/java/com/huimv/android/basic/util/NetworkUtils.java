package com.huimv.android.basic.util;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * A collection of network related utilities
 */
@SuppressLint("DefaultLocale")
public class NetworkUtils {

	static final String TAG = "NetworkState";
	// 网络类型
	public static final int COMMU_NULL = 0;// 没有网络
	public static final int COMMU_SERVER_FAIL = 1;// 主站挂掉
	public static final int COMMU_WIFI = 2;// wifi连接
	public static final int COMMU_NET = 3;// net接入点
	public static final int COMMU_CMWAP = 4;// 移动代理
	public static final int COMMU_UNIWAP = 5;// 联通代理
	public static final int COMMU_CTWAP = 6;// 电信代理
	public static final int COMMU_3GWAP = 7;// 联通3G网络

	// http用得基本操作
	public static final int HTTPREQUEST_GET = 1;
	public static final int HTTPREQUEST_POST = 2;
	public static final int HTTPREQUEST_PUT = 3;
	public static final int HTTPREQUEST_DELETE = 4;
	// 代理的网关地址
	protected final static String PROXY_CMWAP = "10.0.0.172";
	protected final static String PROXY_UNIWAP = "10.0.0.172";
	protected final static String PROXY_CTWAP = "10.0.0.200";

	/**
	 * 搜索可用的网络
	 * 
	 * @param context
	 * @return
	 */
	public static int searchCommuType(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo active = manager.getActiveNetworkInfo();
		int commType = COMMU_NULL;
		if (active != null) {
			int type = active.getType();
			if (type == ConnectivityManager.TYPE_WIFI) {
				commType = COMMU_WIFI;
			} else if (type == ConnectivityManager.TYPE_MOBILE) {
				String extraInfo = active.getExtraInfo();
				if (extraInfo != null && !"".equals(extraInfo)) {
					extraInfo = extraInfo.toLowerCase();
					if (extraInfo.equals("cmwap")) {
						commType = COMMU_CMWAP;
					} else if (extraInfo.equals("uniwap")) {
						commType = COMMU_UNIWAP;
					} else if (extraInfo.equals("ctwap")) {
						commType = COMMU_CTWAP;
					} else if (extraInfo.equals("3gwap")) {
						commType = COMMU_3GWAP;
					} else if (extraInfo.equals("#777")) {// 电信3g
						commType = COMMU_NET;
					} else {
						commType = COMMU_NET;
					}
				} else {
					commType = COMMU_NET;
				}
			}
		}
		return commType;

	}

	/**
	 * Checks if a network connection is available.
	 * 
	 * @param context
	 * @return true if connect is available
	 */
	public static boolean isNetworkAvailable(final Context context) {
		final ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null) {
			return true;
		}
		return false;
	}

	/**
	 * Disables HTTP connection reuse in Donut and below, as it was buggy From:
	 * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
	 */
	@SuppressWarnings("deprecation")
	public static void disableConnectionReuseIfNecessary() {
		if (Integer.parseInt(Build.VERSION.SDK) <= Build.VERSION_CODES.DONUT) {
			System.setProperty("http.keepAlive", "false");
		}
	}

	/**
	 * Enables built-in http cache beginning in ICS From:
	 * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
	 * 
	 * @param application
	 */
	public static void enableHttpResponseCache(final Application application) {
		try {
			final long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
			final File httpCacheDir = new File(application.getCacheDir(),
					"http");
			Class.forName("android.net.http.HttpResponseCache")
					.getMethod("install", File.class, long.class)
					.invoke(null, httpCacheDir, httpCacheSize);
		} catch (final Exception httpResponseCacheNotAvailable) {
		}
	}
}