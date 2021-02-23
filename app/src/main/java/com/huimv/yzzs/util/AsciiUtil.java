package com.huimv.yzzs.util;
/**
 * 
 * @author jiangwei
 *Ascii工具类
 */
public class AsciiUtil {
	/**
	 * @deprecated String 转 Ascii
	 * @param value
	 * @return
	 */
	public static String stringToAscii(String value) {
		StringBuilder sbu = new StringBuilder();
		char[] chars = value.toCharArray();   
		for (int i = 0; i < chars.length; i++) {  
			if(i != chars.length - 1)  
			{
				sbu.append((int)chars[i]).append(",");  
			}
			else {
				sbu.append((int)chars[i]);  
			}
		}
		return sbu.toString();  
	}

	/**
	 * Ascii 转 String
	 * @param value
	 * @return
     */
	public static String asciiToString(String value) {
		StringBuilder sbu = new StringBuilder();
		String[] chars = value.split(",");
		for (String charValue:chars) {
			sbu.append((char) Integer.parseInt(charValue));
		}
		return sbu.toString();
	}
}
