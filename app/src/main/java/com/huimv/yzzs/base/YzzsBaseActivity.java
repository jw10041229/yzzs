package com.huimv.yzzs.base;

import android.os.Handler;

import com.huimv.android.basic.base.BaseActivity;
import com.huimv.yzzs.R;
import com.huimv.yzzs.activity.IndexActivity;
import com.huimv.yzzs.constant.XtAppConstant;
import com.huimv.yzzs.util.YzzsCommonUtil;
import com.orhanobut.logger.Logger;

import java.util.Arrays;

/**
 * 养殖助手BaseActivity
 * 
 * @author jiangwei
 *
 */
public class YzzsBaseActivity extends BaseActivity {
	private Handler handler = new Handler();
	private String content = "";
	// 创建接口
	public interface ConnectTimeoutListener {
		void Timeout(String content);
	}

	// 声明接口对象
	private ConnectTimeoutListener mConnectTimeoutListener;

	public void setConnectTimeoutListener(final ConnectTimeoutListener mConnectTimeoutListener) {
		
		this.mConnectTimeoutListener = mConnectTimeoutListener;
	}
	/**
	 * 开始计时
	 */
	public void startTime(int time,String content) {
		this.content = content;
		cancelTime();
		handler.postDelayed(mRunnable, time); // 开始 n 秒钟定时，n 秒后，执行 run 方法
	}
	/**
	 * 取消计时
	 */
	public void cancelTime() {
		handler.removeCallbacks(mRunnable);
	}
	
	Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			mConnectTimeoutListener.Timeout(content);
		}
	};
	/**
	 * 发送基础12个长度的命令
	 * 
	 * @param dev
	 * @param cmd
	 */
	public void sendGetBaseCMD(int dev, int cmd) {
		int lenth = 12;
		final byte[] byteData = new byte[lenth];
		byteData[0] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadH);// h
		byteData[1] = YzzsCommonUtil.intTobyte(XtAppConstant.packHeadM);// m
		byteData[2] = YzzsCommonUtil.intTobyte(dev);
		byteData[3] = YzzsCommonUtil.intTobyte(cmd / 256);// 命令高位
		byteData[4] = YzzsCommonUtil.intTobyte(cmd % 256);// 命令低位
		byteData[5] = YzzsCommonUtil.intTobyte(lenth / 256);// 长度低位
		byteData[6] = YzzsCommonUtil.intTobyte(lenth % 256);// 长度高位
		byteData[7] = 0;// 后面值得长度
		byteData[8] = 1;// 校验位
		byteData[9] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomE);// e
		byteData[10] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomN);// n
		byteData[11] = YzzsCommonUtil.intTobyte(XtAppConstant.packBottomD);// d
		sendUnpackData(byteData, XtAppConstant.SEND_DELAY_PACK_INTERVAL);
	}
	/**
	 * 发送命令
	 * 
	 * @param strValue
	 */
	public void sendBaseCMD(byte[] strValue) {
		boolean isConnect = IndexActivity.mBluetoothLeService.WriteValue(strValue);
		if (!isConnect) {
			ToastMsg(this, getString(R.string.disconnected));
			dismissLoading();
		}
	}

	/**
	 * 大数组 拆包发送
	 * 
	 * @param allData
	 * @throws InterruptedException
	 */
	public void sendUnpackData(byte[] allData, final int delayTime) {
		int howPack1 = allData.length % 20;// 取余，判断是不是20的倍数
		int howPack2 = allData.length / 20;// 取整，几个20的倍数
		for (int j = 0; j < howPack2; j++) {
			final byte[] data = Arrays.copyOfRange(allData, j * 20, (j + 1) * 20);
			try {
				Thread.sleep(delayTime);
				sendBaseCMD(data);
				Logger.d("发送数据", Arrays.toString(data));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (howPack1 != 0) {// 刚好20个字节
			final byte[] data = Arrays.copyOfRange(allData, allData.length - howPack1, allData.length);
			try {
				Thread.sleep(delayTime);
				sendBaseCMD(data);
				Logger.d("发送数据", Arrays.toString(data));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
