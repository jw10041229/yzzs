package com.huimv.yzzs.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class JxDate extends Date {
    public final static int ALIGN_TO_NIGHT = 0;
    public final static int ALIGN_TO_MORNIING = 1;

    public JxDate() {
        super();
    }

    public JxDate(long date) {
        super(date);
    }

    /***********************************************************************************************
     *
     *

    public static void test() {
        Date now = JxDate.Now();
        Log.e("log", "time:["+JxDate.DateTimeToString(now)+"]");
        Log.e("log", "time:["+JxDate.WeekdayString(now)+"]");
        Log.e("log", "time:["+JxDate.DateTimeToStringEx(now)+"]");

        Date time = JxDate.dayIncreases(now, 2);
        Log.e("log", "time:["+JxDate.DateTimeToString(time)+"]");
        time = JxDate.hourIncreases(now, 2);
        Log.e("log", "time:["+JxDate.DateTimeToString(time)+"]");

        time = JxDate.dayIncreases(now, 2);
        time = JxDate.dateAlignToMorning(time);
        Log.e("log", "time:["+JxDate.DateTimeToString(time)+"]");
        time = JxDate.dateAlignToNight(time);
        Log.e("log", "time:["+JxDate.DateTimeToString(time)+"]");

        time = JxString.StartDateFromString(JxDate.DateTimeToString(now));
        Log.e("log", "time:["+JxDate.DateTimeToString(time)+"]");
        time = JxString.EndDateFromString(JxDate.DateTimeToString(now));
        Log.e("log", "time:["+JxDate.DateTimeToString(time)+"]");
        time = JxString.DateWithTimeFromString(JxDate.DateTimeToString(now), "10:10:08");
        Log.e("log", "time:["+JxDate.DateTimeToString(time)+"]");
    }

    /***********************************************************************************************
     *
     */

/*    public static Date dateAlign(final Date time, final int alignMode) {
        if(alignMode == ALIGN_TO_MORNIING)
            return dateAlignToMorning(time);
        else if(alignMode == ALIGN_TO_NIGHT)
            return dateAlignToNight(time);
        return time;
    }*/

 /*   // 从凌晨算起
    public static Date dateAlignToMorning(final Date time)
    {
        SimpleDateFormat formatter = new JxSimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String strTime = String.format(Locale.getDefault(), "%s 00:00:00", formatter.format(time));
        return JxString.DateTimeFromString(strTime);
    }
    // 时间到子夜
    public static Date dateAlignToNight(final Date time)
    {
        SimpleDateFormat formatter = new JxSimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String strTime = String.format(Locale.getDefault(), "%s 23:59:59", formatter.format(time));
        return JxString.DateTimeFromString(strTime);
    }*/

    public static JxDate Now() {
        Date now = new Date();
        //旧的就是当前的时区，新的就是目标的时区
        TimeZone oldZone = TimeZone.getDefault();
        TimeZone newZone = TimeZone.getTimeZone("GMT+8");
        now = new Date(now.getTime() - (oldZone.getRawOffset()-newZone.getRawOffset()));
        //Log.e("now", DateTimeToString(now));
        return new JxDate(now.getTime());
    }

    /***********************************************************************************************
     *
     */

    public static String DateToString(final Date time)
    {
        SimpleDateFormat formatter = new JxSimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(time);
    }
    public static String DateToStringEx(final Date time)
    {
        SimpleDateFormat formatter = new JxSimpleDateFormat("MM-dd", Locale.getDefault());
        return formatter.format(time);
    }

    public static String TimeToString(final Date time)
    {
        SimpleDateFormat formatter = new JxSimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return formatter.format(time);
    }
    public static String TimeToStringEx(final Date time)
    {
        SimpleDateFormat formatter = new JxSimpleDateFormat("HH:mm", Locale.getDefault());
        return formatter.format(time);
    }
    public static String DateTimeToString(final Date time)
    {
        SimpleDateFormat formatter = new JxSimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return formatter.format(time);
    }
    public static String DateTimeToStringEx(final Date time)
    {
        SimpleDateFormat formatter = new JxSimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return formatter.format(time);
    }
    public static String MillisecondToString(final Date time)
    {
        SimpleDateFormat formatter = new JxSimpleDateFormat("SSSS", Locale.getDefault());
        return formatter.format(time);
    }

    public static String WeekdayString(final Date time)
    {
        final String dayNames[] = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(time);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1;
        if(dayOfWeek<0)
            dayOfWeek=0;
        return dayNames[dayOfWeek];
    }

    public static String YearToString(final Date time)
    {
        SimpleDateFormat formatter = new JxSimpleDateFormat("yyyy", Locale.getDefault());
        return formatter.format(time);
    }
    public static String MonthToString(final Date time)
    {
        SimpleDateFormat formatter = new JxSimpleDateFormat("MM", Locale.getDefault());
        return formatter.format(time);
    }
    public static String DayToString(final Date time)
    {
        SimpleDateFormat formatter = new JxSimpleDateFormat("dd", Locale.getDefault());
        return formatter.format(time);
    }
    public static String HourToString(final Date time)
    {
        SimpleDateFormat formatter = new JxSimpleDateFormat("HH", Locale.getDefault());
        return formatter.format(time);
    }
    public static String MinuteToString(final Date time)
    {
        SimpleDateFormat formatter = new JxSimpleDateFormat("mm", Locale.getDefault());
        return formatter.format(time);
    }
    public static String SecondToString(final Date time)
    {
        SimpleDateFormat formatter = new JxSimpleDateFormat("ss", Locale.getDefault());
        return formatter.format(time);
    }

    /***********************************************************************************************
     *
     */

    static class JxSimpleDateFormat  extends SimpleDateFormat {
        JxSimpleDateFormat(String pattern, Locale locale) {
            super(pattern, locale);

//            TimeZone timeZone = TimeZone.getTimeZone("GMT+8");
//            super.setTimeZone(timeZone);
        }
    }

    /***********************************************************************************************
     *
     */

    public static Date monthIncreases(final Date time, final int month) {
        return dayIncreases(time, month*30);
    }

    public static Date dayIncreases(final Date time, final int day) {
        return new Date((time.getTime()/1000+day*24*60*60)*1000);
    }

    public static Date hourIncreases(final Date time, final int hour) {
        return new Date((time.getTime()/1000+hour*60*60)*1000);
    }

    /***********************************************************************************************
     *
     */

    public static long timeIntervalSinceNow(final Date time) {
        Date now = Now();
        // 计算时间差
        //
        // milliseconds >> second
        return  (time.getTime() - now.getTime()) / 1000;
    }

    // 和当前时间的时间差字符串
    public static String intervalString(final Date time) {
        Date now = Now();
        // 计算时间差
        //
        // milliseconds >> second
        long timeInterval = -timeIntervalSinceNow(time);
        if(timeInterval/86400 > 1)
            return String.format(Locale.getDefault(), "%d天前", (int)(timeInterval/86400));
        else if(timeInterval > 86400)
            return "昨天";//String.format(Locale.getDefault(), "昨天");
        else if(timeInterval<86400) {
            if(timeInterval/3600 > 1)
                return String.format(Locale.getDefault(), "%d小时前", (int)(timeInterval/3600));
            else if(timeInterval/60 > 1)
                return String.format(Locale.getDefault(), "%d分钟前", (int)(timeInterval/60));
            else
                return "刚刚";//String.format(Locale.getDefault(), "刚刚");
        }

        return DateToStringEx(time);
    }

}
