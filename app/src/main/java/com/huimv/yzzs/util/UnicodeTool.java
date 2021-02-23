package com.huimv.yzzs.util;

import java.lang.Character.UnicodeBlock;

public class UnicodeTool {
	/**
	  * 字符串转换unicode
	  */
	 public static String string2Unicode(String s) {      
		String as[] = new String[s.length()];
		String s1 = "";
     for (int i = 0; i < s.length(); i++) {
         as[i] = Integer.toHexString(s.charAt(i) & 0xffff);
         s1 = s1 + "\\u" + as[i];
     }
     return s1;
     }
	/**
	 * 中文转换成 unicode
	 * 
	 * @param inStr
	 * @return
	 */
	public static String encodeUnicode(String inStr) {
		char[] myBuffer = inStr.toCharArray();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < inStr.length(); i++) {
			char ch = myBuffer[i];
			if ((int) ch < 10) {
				sb.append("000" + (int) ch);
				continue;
			}
			UnicodeBlock ub = UnicodeBlock.of(ch);
			if (ub == UnicodeBlock.BASIC_LATIN) {
				// 英文及数字等
				short s = (short) myBuffer[i];
				String hexS = Integer.toHexString(s).replace("ffff", "");
				String unicode = "00" + hexS;
				sb.append(unicode);
			} else if (ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
				// 全角半角字符
				int j = (int) myBuffer[i] - 65248;
				sb.append((char) j);
			} else {
				// 汉字
				short s = (short) myBuffer[i];
				String hexS = Integer.toHexString(s).replace("ffff", "");
				String unicode = "" + hexS;
				sb.append(unicode.toLowerCase());
			}
		}
		return sb.toString();
	}

	/**
	 * unicode 转换成 中文
	 * 
	 * @author fanhui 2007-3-15
	 * @param theString
	 * @return
	 */
	public static String decodeUnicode(String theString) {
		char aChar;
		int len = theString.length();
		StringBuilder outBuffer = new StringBuilder(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
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
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
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
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}
	
	/**
	 * unnicode  加\\u标志
	 * @param str
	 * @return
	 */
	public static String addUnicodeU (String str) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < str.length() / 4 ; i++) {
			String c = str.substring(i * 4 ,i * 4 + 4);
			b.append(c.replace(c, "\\u" + c));
		}
		return b.toString();
	}
}
