package com.huimv.android.basic.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串的通用操作
 * 
 * @author jw
 * 
 */
public class StringUtil {

	public static String decodeUnicode(String theString) {
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			char aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - 48;
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 97;
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 65;
							break;
						case ':':
						case ';':
						case '<':
						case '=':
						case '>':
						case '?':
						case '@':
						case 'G':
						case 'H':
						case 'I':
						case 'J':
						case 'K':
						case 'L':
						case 'M':
						case 'N':
						case 'O':
						case 'P':
						case 'Q':
						case 'R':
						case 'S':
						case 'T':
						case 'U':
						case 'V':
						case 'W':
						case 'X':
						case 'Y':
						case 'Z':
						case '[':
						case '\\':
						case ']':
						case '^':
						case '_':
						case '`':
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}
					}

					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else {
				outBuffer.append(aChar);
			}
		}
		return outBuffer.toString();
	}

	public static String[] split(String line, String delim) {
		if (line == null) {
			return new String[0];
		}
		List<String> list = new ArrayList<String>();
		StringTokenizer t = new StringTokenizer(line, delim);

		while (t.hasMoreTokens()) {
			list.add(t.nextToken());
		}

		return list.toArray(new String[list.size()]);
	}

	public static String transLog(String logInfo) {
		return transChiTo8859(logInfo);
	}

	public static String transChiTo8859(String chi) {
		if (isEmpty(chi)) {
			return "";
		}
		String result = null;
		try {
			byte[] temp = chi.getBytes("GBK");
			result = new String(temp, "ISO-8859-1");
		} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
		}
		return result;
	}

	public static String parseEnter(String html) {
		String reg = "[\r\n]";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(html);
		String s = m.replaceAll("\003\002\001");
		return s;
	}

	public static String parseHtml(String html) {
		if (html != null) {
			return html.replaceAll("\r", "<br>");
		}
		return "";
	}

	public static boolean getBinIsOne(int num, int idx) {
		String str = "0000000000" + Integer.toBinaryString(num);
		str = str.substring(str.length() - 10);
		if (str.substring(10 - idx, 11 - idx).equalsIgnoreCase("1")) {
			return true;
		}
		return false;
	}

	public static boolean checkSelected(String str, int i) {
		if (str == null)
			return false;
		try {
			String[] splits = split(str, ",");
			for (String s_num : splits) {
				int num = Integer.parseInt(s_num);
				if (num == i)
					return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public String getCityByArea(String area) {
		if ((area != null) && (area.length() > 1)) {
			return area.substring(0, area.length());
		}
		return "";
	}

	public Object getObjByList(List<Object> list, int index) {
		if ((list == null) || (list.isEmpty())) {
			return new Object();
		}
		return list.get(index);
	}

	public static boolean isEmpty(String str) {
		if ((str != null) && (!str.trim().equalsIgnoreCase(""))) {
			return false;
		}
		return true;
	}

	public String getRichTextValue(String richText, String key) {
		if (richText != null) {
			String[] arr = split(richText, "");
			for (String str : arr) {
				String[] obj = split(str, "\001");
				if ((obj != null) && (obj.length == 2)
						&& (key.equalsIgnoreCase(obj[0]))) {
					return obj[1];
				}
			}

		}

		return "";
	}

	public String getHotelHouseInfo(String richText) {
		if (richText == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		String[] arr = split(richText, "\002");
		for (String str : arr) {
			String[] obj = split(str, "\001");
			for (String fieldStr : obj) {
				String[] fields = fieldStr.split("=");
				if (fields[0].equalsIgnoreCase("houseType"))
					sb.append(fields[1]).append(":");
				else if (fields[0].equalsIgnoreCase("housePrice"))
					sb.append(fields[1]).append(" * ");
				else if (fields[0].equalsIgnoreCase("houseNum")) {
					sb.append(fields[1]);
				}
			}
			sb.append("<br>");
		}
		return sb.toString();
	}

	public int getTouristFee(String feeText, int type) {
		int addRet = 0;
		int subRet = 0;
		if (feeText != null) {
			String[] arr = split(feeText, "");
			for (String str : arr) {
				String[] obj = split(str, "\001");
				if ((obj != null) && (obj.length == 2)) {
					if (("price10".equalsIgnoreCase(obj[0]))
							|| ("price11".equalsIgnoreCase(obj[0]))
							|| ("price12".equalsIgnoreCase(obj[0]))
							|| ("price13".equalsIgnoreCase(obj[0]))
							|| ("price14".equalsIgnoreCase(obj[0]))
							|| ("price15".equalsIgnoreCase(obj[0])))
						try {
							int i = Integer.parseInt(obj[1]);
							subRet += i;
						} catch (Exception localException) {
						}
					else
						try {
							int i = Integer.parseInt(obj[1]);
							addRet += i;
						} catch (Exception localException1) {
						}
				}
			}
		}
		if (type == 1) {
			return addRet;
		}
		return subRet;
	}

	public static String getReceiveStat(int receiveStat) {
		String returnStr = "";
		switch (receiveStat) {
		case 0:
			returnStr = "收客中";
			break;
		case 1:
			returnStr = "取消";
			break;
		case 2:
			returnStr = "停售";
			break;
		case 3:
			returnStr = "代理停售";
		}

		return returnStr;
	}

	public static String formatC(char x) {
		String a = "";
		switch (x) {
		case '0':
			a = "零";
			break;
		case '1':
			a = "壹";
			break;
		case '2':
			a = "贰";
			break;
		case '3':
			a = "叁";
			break;
		case '4':
			a = "肆";
			break;
		case '5':
			a = "伍";
			break;
		case '6':
			a = "陆";
			break;
		case '7':
			a = "柒";
			break;
		case '8':
			a = "捌";
			break;
		case '9':
			a = "玖";
		}

		return a;
	}

	public static String getHourMinitus(String hours, String minitues) {
		String str = "";
		if ((hours == null) || (hours.equalsIgnoreCase(""))) {
			return "";
		}
		str = " " + hours;
		if ((minitues != null) && (!minitues.trim().equalsIgnoreCase(""))) {
			str = str + ":" + minitues;
		}
		return str;
	}

	public static String getTime(Date startDate, Date endDate) {
		if ((startDate != null) && (endDate != null)) {
			int date = (int) (endDate.getTime() - startDate.getTime()) / 86400000;
			return String.valueOf(date);
		}
		return "";
	}

	public static String getTimeIncludeHead(Date startDate, Date endDate) {
		if ((startDate != null) && (endDate != null)) {
			int date = (int) (endDate.getTime() - startDate.getTime()) / 86400000 + 1;
			return String.valueOf(date);
		}
		return "";
	}

	public static String readByURL(String url) {
		StringBuffer sb = new StringBuffer();
		try {
			String sCurrentLine = "";

			URL l_url = new URL(url);
			HttpURLConnection l_connection = (HttpURLConnection) l_url
					.openConnection();

			l_connection.connect();

			InputStream l_urlStream = l_connection.getInputStream();

			BufferedReader l_reader = new BufferedReader(new InputStreamReader(
					l_urlStream, "UTF8"));

			while ((sCurrentLine = l_reader.readLine()) != null) {
				sb.append(sCurrentLine).append("\r\n");
			}
			l_reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public static String decodeUrlByUtf8(String data) {
		try {
			return URLDecoder.decode(data, "utf8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String encodeUrlByUtf8(String name) {
		if (name == null)
			return "";
		try {
			name = URLEncoder.encode(name, "UTF-8");
		} catch (Exception localException) {
		}
		return name;
	}

	public static int parseInt(String s) {
		if (s == null)
			return 0;
		try {
			return Integer.parseInt(s);
		} catch (Exception localException) {
		}
		return 0;
	}

	public static int checkURI(String uri) {
		if (uri == null) {
			return -1;
		}

		if (uri.contains("index.action"))
			return 1;
		if (uri.contains("chujing.action"))
			return 4;
		if (uri.contains("guonei.action"))
			return 5;
		if (uri.contains("buy.action"))
			return 6;
		if (uri.contains("gift.action"))
			return 7;
		if (uri.contains("elong.action"))
			return 8;
		if (uri.contains("taobaoke.action"))
			return 9;
		if (uri.contains("web/user/")) {
			return 10;
		}

		return 0;
	}

	// private static String getFileName(String fileName) {
	// if ((fileName != null) && (!fileName.equalsIgnoreCase(""))) {
	// int index = fileName.indexOf(".");
	// return fileName.substring(0, index);
	// }
	// return null;
	// }

	public static String getSplitString(String input) {
		StringBuffer str = new StringBuffer();
		if (input != null) {
			for (int i = 0; i < input.length(); i++) {
				char a = input.charAt(i);
				if ((i != 0) && (a >= 'A') && (a <= 'Z')) {
					a = (char) (a + ' ');
					str.append("_");
				}
				str.append(a);
			}
		}
		return str.toString();
	}

}
