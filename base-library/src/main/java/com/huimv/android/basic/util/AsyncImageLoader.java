package com.huimv.android.basic.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

/**
 * 异步加载图片的类
 * 
 * @author ye
 * 
 */
public class AsyncImageLoader {
	public static final String TAG = "AsyncImageLoader";
	// 保存正在下载的图片URL集合，避免重复下载用
	private static Set<String> sDownloadingSet;
	// 软引用内存缓存
	private static Map<String, SoftReference<Bitmap>> sImageCache;
	// 图片三种获取方式管理者，网络URL获取、内存缓存获取、外部文件缓存获取
	private static LoaderImpl impl;
	// 线程池相关
	private static ExecutorService sExecutorService;

	// 通知UI线程图片获取ok时使用
	private Handler handler;

	/**
	 * 异步加载图片完毕的回调接口
	 */
	public static abstract class ImageCallback {
		/**
		 * 回调函数
		 * 
		 * @param bitmap
		 *            : may be null!
		 * @param imageUrl
		 */
		public abstract void onImageLoaded(Bitmap bitmap, String imageUrl);

		public void onBeforeHttploadImage() {

		}
	}

	static {
		sDownloadingSet = new HashSet<String>();
		sImageCache = new Hashtable<String, SoftReference<Bitmap>>();
		impl = new LoaderImpl(sImageCache);
	}

	public AsyncImageLoader() {
		handler = new Handler();
		startThreadPoolIfNecessary();
	}

	/**
	 * 是否缓存图片至/data/data/package/cache/目录 默认不缓存
	 */
	public static void setCache2File(boolean flag) {
		impl.setCache2File(flag);
	}

	public static void setMaxImgSize(int maxImgSize) {
		impl.setMaxImgSize(maxImgSize);
	}

	/**
	 * 设置缓存路径，setCache2File(true)时有效
	 */
	public static void setCachedDir(String dir) {
		impl.setCachedDir(dir);
	}

	/** 开启线程池 */
	public static void startThreadPoolIfNecessary() {
		if (sExecutorService == null || sExecutorService.isShutdown()
				|| sExecutorService.isTerminated()) {
			sExecutorService = Executors.newFixedThreadPool(3);
			// sExecutorService = Executors.newSingleThreadExecutor();
		}
	}

	/**
	 * 异步下载图片，并缓存到memory中
	 * 
	 * @param url
	 * @param callback
	 *            see ImageCallback interface
	 */
	public void downloadImage(final String url, final ImageCallback callback) {
		downloadImage(url, true, callback);
	}

	/**
	 * 
	 * @param url
	 * @param cache2Memory
	 *            是否缓存至memory中
	 * @param callback
	 */
	public synchronized void downloadImage(final String url,
			final boolean cache2Memory, final ImageCallback callback) {
		if (sDownloadingSet.contains(url)) {

			Log.d(TAG, "image is in loading");
			return;
		} else {
			Log.d(TAG, "begain loading image:" + url);
		}
		if (url == null || "".equals(url.trim())) {
			if (callback != null) {
				callback.onImageLoaded(null, url);
				return;
			}
		}
		Bitmap bitmap = impl.getBitmapFromMemory(url);
		if (bitmap != null) {
			if (callback != null) {
				callback.onImageLoaded(bitmap, url);
			}
		} else {
			if (callback != null) {
				callback.onBeforeHttploadImage();
			}
			// 从网络端下载图片
			sDownloadingSet.add(url);
			sExecutorService.submit(new Runnable() {
				@Override
				public void run() {
					try {
						final Bitmap bitmap = impl.getBitmapFromUrl(url,
								cache2Memory);
						loadImg(callback, bitmap, url);
					} catch (Exception e) {
						Log.e(TAG, e.getMessage(), e);
					}
				}
			});
		}
	}

	private synchronized void loadImg(final ImageCallback callback,
			final Bitmap bitmap, final String url) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				sDownloadingSet.remove(url);
				if (callback != null) {
					try {
						callback.onImageLoaded(bitmap, url);
					} catch (Exception e) {
						Log.e(TAG, e.getMessage(), e);
					}
				}
			}
		});
	}

	/**
	 * 预加载下一张图片，缓存至memory中
	 * 
	 * @param url
	 */
	public void preLoadNextImage(final String url) {
		// 将callback置为空，只将bitmap缓存到memory即可。
		downloadImage(url, null);
	}

	public static class LoaderImpl {
		// 内存中的软应用缓存
		private Map<String, SoftReference<Bitmap>> imageCache;

		// 是否缓存图片至本地文件
		private boolean cache2FileFlag = false;
		// 缓存目录,默认是/data/data/package/cache/目录
		private String cachedDir;
		// 1k
		private int maxImgSize = 100 * 100;

		public LoaderImpl(Map<String, SoftReference<Bitmap>> imageCache) {
			this.imageCache = imageCache;
		}

		/**
		 * 是否缓存图片至外部文件
		 * 
		 * @param flag
		 */
		public void setCache2File(boolean flag) {
			cache2FileFlag = flag;
		}

		public void setMaxImgSize(int maxImgSize) {
			this.maxImgSize = maxImgSize;
		}

		/**
		 * 设置缓存图片到外部文件的路径
		 * 
		 * @param cacheDir
		 */
		public void setCachedDir(String cacheDir) {
			this.cachedDir = cacheDir;
		}

		/**
		 * 从网络端下载图片
		 * 
		 * @param url
		 *            网络图片的URL地址
		 * @param cache2Memory
		 *            是否缓存(缓存在内存中)
		 * @return bitmap 图片bitmap结构
		 * 
		 */
		public Bitmap getBitmapFromUrl(String url, boolean cache2Memory) {
			Bitmap bitmap = null;
			HttpURLConnection conn = null;
			InputStream is = null;
			try {
				URL u = new URL(url);
				conn = (HttpURLConnection) u.openConnection();
				is = conn.getInputStream();
				bitmap = BitmapFactory.decodeStream(is);
				if (cache2Memory) {
					// 1.缓存bitmap至内存软引用中
					imageCache.put(url, new SoftReference<Bitmap>(bitmap));
					int size = bitmap.getHeight() * bitmap.getWidth();
					if (size < maxImgSize) {
						imageCache.put(url, new SoftReference<Bitmap>(bitmap));
					}
				}
				if (cache2FileFlag && cachedDir != null
						&& !"".equals(cachedDir.trim())) {
					// 2.缓存bitmap至/data/data/packageName/cache/文件夹中
					String fileName = getMD5Str(url);
					String filePath = this.cachedDir + "/" + fileName;
					FileOutputStream fos = new FileOutputStream(filePath);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				}
				return bitmap;
			} catch (IOException e) {
				Log.i(TAG, "image load error:" + url);
				return null;
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (Throwable e) {
						Log.e(TAG, e.getMessage(), e);
					}
				}
				if (conn != null) {
					conn.disconnect();
				}
			}
		}

		/**
		 * 从内存缓存中获取bitmap
		 * 
		 * @param url
		 * @return bitmap or null.
		 */
		public Bitmap getBitmapFromMemory(String url) {
			Bitmap bitmap = null;
			if (url == null) {
				return null;
			}
			if (imageCache.containsKey(url)) {
				SoftReference<Bitmap> bitmapRef = imageCache.get(url);
				if (bitmapRef != null) {
					bitmap = bitmapRef.get();
					return bitmap;
				}
			}
			// 从外部缓存文件读取
			if (cache2FileFlag) {
				bitmap = getBitmapFromFile(url);
				if (bitmap != null) {
					imageCache.put(url, new SoftReference<Bitmap>(bitmap));
				}
			}

			return bitmap;
		}

		/**
		 * 从外部文件缓存中获取bitmap
		 * 
		 * @param url
		 * @return
		 */
		private Bitmap getBitmapFromFile(String url) {
			Bitmap bitmap = null;
			String fileName = getMD5Str(url);
			if (fileName == null)
				return null;

			String filePath = cachedDir + "/" + fileName;

			try {
				FileInputStream fis = new FileInputStream(filePath);
				bitmap = BitmapFactory.decodeStream(fis);
			} catch (FileNotFoundException e) {
				Log.i(TAG, "image load from file error:" + url);
				bitmap = null;
			}
			return bitmap;
		}

		/**
		 * MD5 加密
		 */
		private String getMD5Str(String str) {
			MessageDigest messageDigest = null;
			try {
				messageDigest = MessageDigest.getInstance("MD5");
				messageDigest.reset();
				messageDigest.update(str.getBytes("UTF-8"));
			} catch (Exception e) {
				Log.e(TAG, "getMD5 error");
				return null;
			}

			byte[] byteArray = messageDigest.digest();
			StringBuffer md5StrBuff = new StringBuffer();
			for (int i = 0; i < byteArray.length; i++) {
				if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
					md5StrBuff.append("0").append(
							Integer.toHexString(0xFF & byteArray[i]));
				} else {
					md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
				}
			}
			return md5StrBuff.toString();
		}

		/**
		 * MD5 加密 private static String getMD5Str(Object...objects){
		 * StringBuilder stringBuilder=new StringBuilder(); for (Object object :
		 * objects) { stringBuilder.append(object.toString()); } return
		 * getMD5Str(stringBuilder.toString()); }
		 */
	}
}
