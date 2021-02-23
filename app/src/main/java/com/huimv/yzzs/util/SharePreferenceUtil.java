package com.huimv.yzzs.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
	public static final String MESSAGE_YZZS_KEY = "SPmessage";
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	public SharePreferenceUtil(Context context, String file) {
		sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		editor = sp.edit();
	}
	//清除Sp
	public void clearSP() {
		editor.clear();
		editor.commit();
	}
	// 蓝牙名称
	public void setLymc(String lymc) {
		editor.putString("lymc", lymc);
		editor.commit();
	}
	
	public String getLymc() {
		return sp.getString("lymc", "1号舍");
	}
	
	// 蓝牙地址
	public void setLydz(String lydz) {
		editor.putString("lydz", lydz);
		editor.commit();
	}
	
	public String getLydz() {
		return sp.getString("lydz", "D0:B5");
	}
	
	// 机器id
	public void setJqid(String jqid) {
		editor.putString("jqid", jqid);
		editor.commit();
	}
	
	public String getJqid() {
		return sp.getString("jqid", "00000011");
	}

	public void setZjtdfx(String zjtdfx) {// 直角通道方向
		editor.putString("zjtdfx", zjtdfx);
		editor.commit();
	}

	public String getZjtdfx() {
		return sp.getString("zjtdfx", "0");
	}

	public void setMrtdfx(String mrtdfx) {// 默认通道方向
		editor.putString("mrtdfx", mrtdfx);
		editor.commit();
	}

	public String getMrtdfx() {
		return sp.getString("mrtdfx", "");
	}


	public void setDwSave(String dwSave) {// 已经保存了几档
		editor.putString("dwSave", dwSave);
		editor.commit();
	}

	public String getDwSave() {
		return sp.getString("dwSave", "5");
	}
	/**
	 * 档位温度最高最低
	 * @param key
	 * @param value
	 */
	public void setDwWd(String key,String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public String getDwWd(String key) {
		return sp.getString(key, "0");
	}



	/**
	 * 7个端口的数据，一个端口9位，第一位为端口开关，第2-5为开的时间，6-9为关的时间
	 * 
	 * @return
	 */
	public void setDwdk(String dwdk) {
		editor.putString("dwdk", dwdk);
		editor.commit();
	}

	public String getDwdk() {
		return sp.getString("dwdk", "1003001700501100500150050210070013005031009001100504101100090050510030017006011005001500602#1003001700501100500150050210070013005031009001100504101100090050510030017006011005001500602#1000500000501100050000050210070000005031009000000504100050000050510005000006011000500000602#1003001700501100500150050210070013005031009001100504101100090050510030017006011005001500602#1003001700501100500150050210070013005031009001100504101100090050510030017006011005001500602#");
	}

	
	/**
	 * 格式：1 01 1234 123 0
	 * 
	 * 1:设备类型 （1：温度；2：湿度；3：温湿度；4：氨气）
	 * 01:设备编号
	 * 1234:采样间隔 
	 * 123：传感器地址 
	 * 0:开关 （1：打开；0：关闭）
	 */
	// 传感器绑定数据 --- 5 个通道的传感器绑定数据，各个通道之间用 # 分隔
	// 格式：10112341231#20212341230#30312341231#40412341230#10512341231
	public void setCgqbdData(String cgqbdData){
		editor.putString("cgqbd", cgqbdData);
		editor.commit();
	}
	
	public String getCgqbdData(){
		return sp.getString("cgqbd", "00010020000100#00020020000100#01010020000100#02010020000100#02020020000100#04010020000100#");
	}
	
	// 用户姓名
	public void setYhxm(String yhxm){
		editor.putString("yhxm", yhxm);
		editor.commit();
	}
	
	public String getYhxm(){
		return sp.getString("yhxm", "");
	}
	
	// 用户密码（密码是MD5加密后的密码）
	public void setYhmm(String yhmm){
		editor.putString("yhmm", yhmm);
		editor.commit();
	}
	
	public String getYhmm(){
		return sp.getString("yhmm", "");
	}
	
	
	// 环控用户密码
	public void setHkPassword(String password){
		editor.putString("hkpassword", password);
		editor.commit();
	}
	
	public String getHkPassword(){
		return sp.getString("hkpassword", "");
	}
	
	// 分栏站用户密码
	public void setFlzPassword(String password){
		editor.putString("flzpassword", password);
		editor.commit();
	}
	
	public String getCdzPassword(){
		return sp.getString("cdzpassword", "");
	}

	// 測定站用户密码
	public void setCdzPassword(String password){
		editor.putString("cdzpassword", password);
		editor.commit();
	}

	public String getFlzPassword(){
		return sp.getString("flzpassword", "");
	}
	/**
	 * 1 01200 1 1 01 
	 * 
	 * 1:设备类型  01200:功率  1:开关  1:霍尔开关  01:设备顺序
	 * @param sbbdtd
	 */
	public void setSbbdTd(String sbbdtd) {
		editor.putString("sbbdtd", sbbdtd);
		editor.commit();
	}

	public String getSbbdTd() {
		return sp.getString("sbbdtd", "05010000010#05020000010#05030000010#05040000010#05050000010#06010000010#06020000010#");
	}
	/**
	 * 1 01200 1 1 01 
	 * 
	 * 1:设备类型  01200:功率  1:开关  1:霍尔开关  01:设备顺序
	 * @param lcSbbd
	 */
	public void setLcSbbd(String lcSbbd) {
		editor.putString("lcSbbd", lcSbbd);
		editor.commit();
	}

	public String getLcSbbd() {
		return sp.getString("lcSbbd", "");
	}
	/**
	 * 全控运行参数
	 * @param qkyxcs
	 */
	public void setQkyxcs(String qkyxcs) {
		editor.putString("qkyxcs", qkyxcs);
		editor.commit();
	}

	public String getQkyxcs() {
		return sp.getString("qkyxcs", "");
	}
	
	/**
	 * lc料槽个数
	 * @param ziLcNum
	 */
	public void setZiLcNum(String ziLcNum) {
		editor.putString("ziLcNum", ziLcNum);
		editor.commit();
	}

	public String getZiLcNum() {
		return sp.getString("ziLcNum", "-1");
	}
	
	/**
	 * 版本信息
	 */
	public void setVersion(String version) {
		editor.putString("version", version);
		editor.commit();
	}

	public String getVersion() {
		return sp.getString("version", "");
	}
	
	/**
	 * 默认档位
	 * @param mrdw
	 */
	public void setHkMrdw(String mrdw) {// 默认挡位
		editor.putString("mrdw", mrdw);
		editor.commit();
	}

	public String getHkMrdw() {
		return sp.getString("mrdw", "0");
	}

	/**
	 * 手持机轮训展示时间设置
	 * @param
	 */
	public void setScjTime(String lxsj) {// 轮训时间
		editor.putString("lxsj", lxsj);
		editor.commit();
	}
	//默认十秒
	public String getScjTime() {
		return sp.getString("lxsj", "10000");
	}


	/**
	 *
	 */
	public void setHkIsHasBpfjVersion(String isHasBpfj) {// 是否有变频风机版本
		editor.putString("isHasBpfj", isHasBpfj);
		editor.commit();
	}
	//默认十秒
	public String getHkIsHasBpfjVersion() {
		return sp.getString("isHasBpfj", "0");//0没有变频风机，1有变频风机
	}
}
