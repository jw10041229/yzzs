package com.huimv.android.basic.util;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 * 
 * @author hongjun.hu
 * @version 1.0
 * @history
 */
public class DateUtil {
	/**
	 * 定义一天的毫妙数
	 */
	public static final long MILLSECOND_OF_DAY = 86400000;
	public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm";
	public static final String TIME_FORMATS = "yyyy-MM-dd HH:mm:ss";
	public static final String TIME_FORMATSS = "mm:ss.SSS";
	public static String getCurrentDate(int format) {
		String formatStr = null;
		if (format ==1) {
			formatStr = TIME_FORMAT;
		} else if (format ==0){
			formatStr = TIME_FORMATS;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		return sdf.format(new Date());
	}
	/**
	 * 格式化日期
	 * 
	 * @param strDate
	 *            符合格式的字符串
	 * @return 格式后的日期
	 */
	public static Date parser(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
		try {
			return sdf.parse(strDate);
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * 格式化日期
	 * 
	 * @param strDate
	 *            符合格式的字符串
	 * @return 格式后的日期
	 */
	public static Date parsers(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMATS);
		try {
			return sdf.parse(strDate);
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * 格式化日期
	 * 
	 * @param strDate
	 *            符合格式的字符串
	 * @return 格式后的日期
	 */
	public static Date parser(Date testDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(testDate);
		calendar.clear(Calendar.MILLISECOND);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.HOUR_OF_DAY);
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
		try {
			return sdf.parse(sdf.format(calendar.getTime()));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 格式化日期
	 * 
	 * @param strDate
	 *            符合格式的字符串
	 * @return 格式后的日期
	 */
	public static Date parserTo(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
		try {
			return sdf.parse(strDate);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * date to string ,默认 格式 yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static String parserDateToDefaultString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
		try {
			return sdf.format(date);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * date to string ,自定义格式
	 * 
	 * @param date
	 * @return
	 */
	public static String parserDateToPatternString(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.format(date);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 将long类型的时间转换成标准格式（yyyy-MM-dd HH:mm:ss）
	 * @param longTime
	 * @return 格式后的日期字符串
	 */
	public static String parser(long longTime) {
		DateFormat format = new SimpleDateFormat(TIME_FORMATS);
		try {
			return format.format(new Date(longTime));
		} catch (Exception e) {
			return null;
		}
	}
	public static String parser2(long longTime) {
		DateFormat format = new SimpleDateFormat(TIME_FORMATSS);
		try {
			return format.format(new Date(longTime));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 得到当前月份的周期开始日期
	 * 
	 * @param currentDate
	 *            当前日期
	 * @return 当前月份的周期开始日期
	 */
	public static Date getCurBeginCycleDate(Date currentDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);

		String year = "" + calendar.get(Calendar.YEAR);
		String month = (calendar.get(Calendar.MONTH) + 1) + "";
		if (month.length() < 2) {
			month = "0" + month;
		}
		String dateStr = year + "-" + month + "-01 00:00:00";
		return DateUtil.parser(dateStr);
	}

	/**
	 * 取得当前周期的周期结束日期
	 * 
	 * @param currentDate
	 *            当前日期
	 * @return 当前周期的周期结束日期
	 */
	public static Date getCurEndCycleDate(Date currentDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);

		String year = "" + calendar.get(Calendar.YEAR);
		String month = (calendar.get(Calendar.MONTH) + 2) + "";
		if (month.length() < 2) {
			month = "0" + month;
		}
		String dateStr = year + "-" + month + "-01 23:59:59";
		calendar.setTime(DateUtil.parser(dateStr));
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return calendar.getTime();
	}

	/**
	 * 得到下nextCycle周期的月份
	 * 
	 * @param currentDate
	 *            当前日期
	 * @param nextCycle
	 *            下nextCycle周期
	 * @return 下nextCycle周期
	 */
	public static Date getNextCycleDate(Date currentDate, long nextCycle) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);

		String year = "" + calendar.get(Calendar.YEAR);
		nextCycle++;
		String month = (calendar.get(Calendar.MONTH) + nextCycle) + "";
		if (month.length() < 2) {
			month = "0" + month;
		}
		String dateStr = year + "-" + month + "-01 00:00:00";
		return DateUtil.parser(dateStr);
	}

	/**
	 * 获取开始和结束日期之间的间隔日期
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @param roundingMode
	 *            舍入方式 见 BigDecimal的定义
	 * @return 相隔的日期数
	 */
	public static long getDaysBetweenDate(Date startDate, Date endDate,
			int roundingMode) {
		BigDecimal bStart = new BigDecimal(startDate.getTime());
		BigDecimal bEnd = new BigDecimal(endDate.getTime());
		BigDecimal bUnit = new BigDecimal(MILLSECOND_OF_DAY);
		return (bEnd.subtract(bStart)).divide(bUnit, roundingMode).longValue();
	}

	/**
	 * 获取开始和结束日期之间的间隔日期
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return 相隔的日期数
	 */
	public static long getDaysBetweenDateWithoutTime(Date startDate,
			Date endDate) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(startDate);
		cal2.setTime(endDate);

		cal1.clear(Calendar.MILLISECOND);
		cal1.clear(Calendar.SECOND);
		cal1.clear(Calendar.MINUTE);
		cal1.clear(Calendar.HOUR_OF_DAY);

		cal2.clear(Calendar.MILLISECOND);
		cal2.clear(Calendar.SECOND);
		cal2.clear(Calendar.MINUTE);
		cal2.clear(Calendar.HOUR_OF_DAY);

		return (cal2.getTime().getTime() - cal1.getTime().getTime()) / MILLSECOND_OF_DAY;
	}

	/**
	 * 获得明天的日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date getTomorrowDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

	/**
	 * 获取两个日期之间相差的月份数
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @param flag
	 *            false 为全月舍
	 * @return 返回的月份数
	 */
	public static long getMonthsBetweenDate(Date startDate, Date endDate,
			boolean flag) {
		Calendar cal1 = Calendar.getInstance();

		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(startDate);
		cal2.setTime(endDate);
		if (endDate.before(startDate)) {
			cal1.setTime(endDate);
			cal2.setTime(startDate);
		}

		cal1.clear(Calendar.MILLISECOND);
		cal1.clear(Calendar.SECOND);
		cal1.clear(Calendar.MINUTE);
		cal1.clear(Calendar.HOUR_OF_DAY);

		cal2.clear(Calendar.MILLISECOND);
		cal2.clear(Calendar.SECOND);
		cal2.clear(Calendar.MINUTE);
		cal2.clear(Calendar.HOUR_OF_DAY);

		return getMonthsBetweenDate(cal1, cal2, flag);
	}

	/**
	 * 获取两个日期之间相差的月份数
	 * 
	 * @param cal1
	 *            开始日期
	 * @param cal2
	 *            结束日期
	 * @param flag
	 *            false 为全月舍
	 * @return 返回的月份数
	 */
	public static long getMonthsBetweenDate(Calendar cal1, Calendar cal2,
			boolean flag) {
		long month = 0L;
		while (cal1.before(cal2)) {
			cal1.add(Calendar.MONTH, 1);
			month++;
			if (flag) {
				if ((cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH))
						&& (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
						&& (cal1.get(Calendar.DAY_OF_MONTH) > cal2
								.get(Calendar.DAY_OF_MONTH))) {
					month--;
					break;
				}
				if ((cal1.get(Calendar.MONTH) > cal2.get(Calendar.MONTH))
						&& (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))) {
					month--;
					break;
				}
			}
		}
		return month;
	}

	/**
	 * 获得日期的年或月或日
	 * 
	 * @param date
	 * @param field
	 * @return
	 */
	public static long getDateField(Date date, int field) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (field == Calendar.MONTH) {
			return cal.get(field) + 1;
		} else {
			return cal.get(field);
		}
	}

	/**
	 * 计算从出生时间到指定时间的年龄 年龄=当前年份-出生年份-if((当前日期>=出生日期),0,1)）
	 * 
	 * @param birthday
	 *            出生时间
	 * @param endDate
	 *            计算的终止时间
	 * @return int
	 */
	public static int getAge(Date birthday, Date endDate) {
		boolean endDateDYBirthdayMonthAndDay = true;
		if (getDateField(endDate, Calendar.MONTH) > getDateField(birthday,
				Calendar.MONTH)) {
			endDateDYBirthdayMonthAndDay = true;
		} else if (getDateField(endDate, Calendar.MONTH) == getDateField(
				birthday, Calendar.MONTH)) {
			if (getDateField(endDate, Calendar.DAY_OF_MONTH) >= getDateField(
					birthday, Calendar.DAY_OF_MONTH)) {
				endDateDYBirthdayMonthAndDay = true;
			} else {
				endDateDYBirthdayMonthAndDay = false;
			}
		} else if (getDateField(endDate, Calendar.MONTH) < getDateField(
				birthday, Calendar.MONTH)) {
			endDateDYBirthdayMonthAndDay = false;
		}

		int age = new Long(
				(getDateField(endDate, Calendar.YEAR) - getDateField(birthday,
						Calendar.YEAR))
						- (endDateDYBirthdayMonthAndDay == true ? 0 : 1))
				.intValue();

		return age;
	}

	final static long[] lunarInfo = new long[] { 0x04bd8, 0x04ae0, 0x0a570,
			0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
			0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0,
			0x0ada2, 0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50,
			0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, 0x06566,
			0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0,
			0x1c8d7, 0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4,
			0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0, 0x0b550,
			0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950,
			0x06aa0, 0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260,
			0x0f263, 0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0,
			0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6,
			0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40,
			0x0af46, 0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0, 0x074a3,
			0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960,
			0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0,
			0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9,
			0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0,
			0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65,
			0x0d530, 0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0,
			0x1d0b6, 0x0d250, 0x0d520, 0x0dd45, 0x0b5a0, 0x056d0, 0x055b2,
			0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0 };

	final public static int lYearDays(int y) {
		// ====== 传回农历 y年的总天数
		int i, sum = 348;
		for (i = 0x8000; i > 0x8; i >>= 1) {
			if ((lunarInfo[y - 1900] & i) != 0)
				sum += 1;
		}
		return (sum + leapDays(y));
	}

	final public static int leapDays(int y) {
		// ====== 传回农历 y年闰月的天数
		if (leapMonth(y) != 0) {
			if ((lunarInfo[y - 1900] & 0x10000) != 0) {
				return 30;
			} else {
				return 29;
			}
		} else {
			return 0;
		}
	}

	/**
	 * 传回农历 y年闰哪个月 1-12 , 没闰传回 0
	 * 
	 * @param y
	 * @return
	 */
	final public static int leapMonth(int y) {
		return (int) (lunarInfo[y - 1900] & 0xf);
	}

	/**
	 * 传回农历 y年m月的总天数
	 * 
	 * @param y
	 * @param m
	 * @return
	 */
	final public static int monthDays(int y, int m) {
		if ((lunarInfo[y - 1900] & (0x10000 >> m)) == 0)
			return 29;
		else
			return 30;
	}

	/**
	 * 
	 * @param y
	 * @param m
	 * @param d
	 * @return
	 */
	@SuppressWarnings("deprecation")
	final public static long[] calElement(int y, int m, int d) {
		long[] nongDate = new long[7];
		int i = 0, temp = 0, leap = 0;
		Date baseDate = new Date(0, 0, 31);
		Date objDate = new Date(y - 1900, m - 1, d);
		long offset = (objDate.getTime() - baseDate.getTime()) / 86400000L;
		nongDate[5] = offset + 40;
		nongDate[4] = 14;
		for (i = 1900; i < 2050 && offset > 0; i++) {
			temp = lYearDays(i);
			offset -= temp;
			nongDate[4] += 12;
		}
		if (offset < 0) {
			offset += temp;
			i--;
			nongDate[4] -= 12;
		}
		nongDate[0] = i;
		nongDate[3] = i - 1864;
		leap = leapMonth(i); // 闰哪个月
		nongDate[6] = 0;
		for (i = 1; i < 13 && offset > 0; i++) {
			// 闰月
			if (leap > 0 && i == (leap + 1) && nongDate[6] == 0) {
				--i;
				nongDate[6] = 1;
				temp = leapDays((int) nongDate[0]);
			} else {
				temp = monthDays((int) nongDate[0], i);
			}
			// 解除闰月
			if (nongDate[6] == 1 && i == (leap + 1))
				nongDate[6] = 0;
			offset -= temp;
			if (nongDate[6] == 0)
				nongDate[4]++;
		}
		if (offset == 0 && leap > 0 && i == leap + 1) {
			if (nongDate[6] == 1) {
				nongDate[6] = 0;
			} else {
				nongDate[6] = 1;
				--i;
				--nongDate[4];
			}
		}
		if (offset < 0) {
			offset += temp;
			--i;
			--nongDate[4];
		}
		nongDate[1] = i;
		nongDate[2] = offset + 1;
		return nongDate;
	}

	/**
	 * 根据指定日期获取农历
	 * 
	 * @param date
	 *            日期
	 * @return 农历
	 */
	public static String getNongLi(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		int year = cld.get(Calendar.YEAR);
		int month = cld.get(Calendar.MONTH) + 1;
		int day = cld.get(Calendar.DAY_OF_MONTH);
		long[] l = calElement(year, month, day);
		String n = "";
		switch ((int) (l[1])) {
		case 1:
			n = "一";
			break;
		case 2:
			n = "二";
			break;
		case 3:
			n = "三";
			break;
		case 4:
			n = "四";
			break;
		case 5:
			n = "五";
			break;
		case 6:
			n = "六";
			break;
		case 7:
			n = "七";
			break;
		case 8:
			n = "八";
			break;
		case 9:
			n = "九";
			break;
		case 10:
			n = "十";
			break;
		case 11:
			n = "十一";
			break;
		case 12:
			n = "十二";
			break;
		}
		String m = "";
		if (l[2] == 10)
			return "初十";
		int two = (int) ((l[2]) / 10);
		if (two == 0)
			m = "初";
		if (two == 1)
			m = "十";
		if (two == 2)
			m = "廿";
		int one = (int) (l[2] % 10);
		switch (one) {
		case 1:
			m += "一";
			break;
		case 2:
			m += "二";
			break;
		case 3:
			m += "三";
			break;
		case 4:
			m += "四";
			break;
		case 5:
			m += "五";
			break;
		case 6:
			m += "六";
			break;
		case 7:
			m += "七";
			break;
		case 8:
			m += "八";
			break;
		case 9:
			m += "九";
			break;
		}
		return n + "月" + m;
	}

	/**
	 * 根据指定日期获取星期
	 * 
	 * @param date
	 *            日期
	 * @return 农历
	 */
	public static String getWeek(Date date) {
		String week = "";
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		switch (cld.get(Calendar.DAY_OF_WEEK)) {
		case 1:
			week += "日";
			break;
		case 2:
			week += "一";
			break;
		case 3:
			week += "二";
			break;
		case 4:
			week += "三";
			break;
		case 5:
			week += "四";
			break;
		case 6:
			week += "五";
			break;
		case 7:
			week += "六";
			break;
		}
		return week;
	}

	public static void main(String[] args) {
		System.out.println("农历" + DateUtil.getCurBeginCycleDate(new Date())
				+ DateUtil.getCurEndCycleDate(new Date()));
	}
}
