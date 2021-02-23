package com.huimv.yzzs.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Arrays;


/**
 * @author jiangwei
 * 
 */
public class YzzsCommonUtil {
	Context context;
	String url;
	Spanned msg;
	public YzzsCommonUtil(Context context, Spanned msg, String url) {
		this.context = context;
		this.msg = msg;
		this.url = url;
	}
	
	/** 更新 */
	public void exitDialog() {
		new AlertDialog.Builder(context)
				.setMessage(msg)
				.setTitle("更新提示")
				.setPositiveButton("更新", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Uri u = Uri.parse(url);
						Intent it = new Intent(Intent.ACTION_VIEW, u);
						context.startActivity(it);
						android.os.Process.killProcess(android.os.Process
								.myPid());// 获取当前进程PID，并杀掉
					}
				})
			/*	.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})*/.create().show();
	}
	
	/**
	 * 根据 flag 将 str 格式化补0
	 * @param str
	 * @param length 补上之后的总长度
	 * @param flag：1、左补0 2、右补0
	 * @return
	 */
	public static String formatStringAdd0(String str, int length, int flag) {
		if (str == null || "".equals(str) || str.length() >= length) {
			return str;
		}

		int j = str.length();
		for (int  i = 0; i < length - j; i++) {
			str = flag == 1 ? "0" + str : str + "0";
		}
		
		return str;
	}
	
	/**
	 * int 转化为byte
	 * @param intData
	 * @return byte
	 */
	public static byte intTobyte (int intData) {
		String str2 = Integer.toBinaryString(intData);//转化为二级制
		String add0 = formatStringAdd0(str2, 8, 1);//二级制前面补0为8为标准二级制
		return BitUtil.decodeBinaryString(add0);//数据长度
	}
	/**
	 * 从一个数组中获取最大值或最小值
	 * @param res1：数组1不为空
	 * @param flag：1、获取最大值	2、获取最小值
	 * @return
	 */
	public static double getMaxMin(double[] res1, int flag) {
		double[] temp1 = res1.clone();
		Arrays.sort(temp1);
		if (flag == 1) {
			return temp1[temp1.length-1] ;
		} else {
			return temp1[0];
		}
	}
	
	/**
	 * 得到各个设备的最大支持数量
	 * @param sbLxMax
	 * @param sblx
	 * @return
	 */
	public static int getSbMax (byte[] sbLxMax,int sblx) {
		int  sbMax = 0;
		for (int i = 0; i < sbLxMax.length; i = i + 2) {
			if (sbLxMax[i] == sblx) {
				sbMax  = sbLxMax[i +1];
				break;
			}
		}
		return sbMax;
	}
	
	/**
	 * byte 负数转正数
	 * @param data
	 * @return
	 */
	public static int ChangeByteToPositiveNumber (byte data) {
		
		return data &0xFF;
	}
	
	/**
	 * byte 负数转正数
	 * @param data
	 * @return
	 */
	public static int ChangeIntToPositiveNumber (int data) {
		
		return data &0xFF;
	}
	
	/**
	 * 两个字节转化为int CMD
	 * @param headData
	 * @param bottomData
	 * @return CMD
	 */
	public  static int getCMD (byte headData, byte bottomData) {
		return ChangeByteToPositiveNumber(headData) * 256 + ChangeByteToPositiveNumber(bottomData);
	}
	
	/**
	 * 两个字节转化为int CMD
	 * @param headData
	 * @param bottomData
	 * @return CMD
	 */
	public static int getCMD (int headData, int bottomData) {
		return ChangeIntToPositiveNumber(headData) * 256 + ChangeIntToPositiveNumber(bottomData);
	}
	
	/**
	 * 版本信息
	 */
	public static int getVersionFromSp (SharePreferenceUtil  mSpUtil) {
		return mSpUtil == null ? 0 : Integer.valueOf(mSpUtil.getVersion().replace("V", "").replace(".", ""));
	}

	/**
	 * 限制输入N位小数
	 * @param editText
	 */
	public static void setPricePoint(final EditText editText,final int lenth) {
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > lenth) {
						s = s.toString().subSequence(0,
								s.toString().indexOf(".") + lenth + 1);
						editText.setText(s);
						editText.setSelection(s.length());
					}
				}
				if (s.toString().trim().substring(0).equals(".")) {
					s = "0" + s;
					editText.setText(s);
					editText.setSelection(2);
				}

				if (s.toString().startsWith("0")
						&& s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, 2).equals(".")) {
						editText.setText(s.subSequence(0, 1));
						editText.setSelection(1);
						return;
					}
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	/**
	 * 计算显示的变频百分比
	 * @return
	 */
	public static int countShowBpPge(String bppge){
		return (int)((Integer.parseInt(bppge) - 40)/0.6);
	}

	/**
	 * 计算真实的变频百分比
	 * @return
	 */
	public static int countRealBpPge(String bppge){
		return (int)((Integer.parseInt(bppge) + 40)*0.6);
	}
}
