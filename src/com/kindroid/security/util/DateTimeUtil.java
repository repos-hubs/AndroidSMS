package com.kindroid.security.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.format.DateUtils;

import com.kindroid.kincent.KindroidMessengerApplication;

/**
 * 日期时间处理工具类
 *
 */
public class DateTimeUtil {
	
	private long lNow = System.currentTimeMillis();
	private Calendar cNow = Calendar.getInstance();
	private Date dNow = new Date(lNow);
	private Timestamp tNow = new Timestamp(lNow);
	private java.sql.Date today = new java.sql.Date(lNow);
	private java.sql.Time now = new java.sql.Time(lNow);
//	private static SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public static enum DateTag {
		HALF_YEAR_AGO, ONE_YEAR_AGO,
		LAST_MONTH_START, LAST_MONTH_END,
		THIS_MONTH_START, THIS_MONTH_END,
		LAST_WEEK_START, LAST_WEEK_END,
        THIS_WEEK_START, THIS_WEEK_END,
        YESTERDAY_START, YESTERDAY_END,
        TODAY_START, TODAY_END, NOW
	};

	/**
	 * 默认构造方法
	 */
	public DateTimeUtil() {
		
	}

	/*private DateTime(long lNow, Calendar cNow, Date dNow, Timestamp tNow,
			java.sql.Date today, Time now) {
		this.lNow = lNow;
		this.cNow = cNow;
		this.dNow = dNow;
		this.tNow = tNow;
		this.today = today;
		this.now = now;
	}*/

	/**
	 * 得到年
	 * @param c
	 * @return
	 */
	public static int getYear(Calendar c) {
		if (c != null) {
			return c.get(Calendar.YEAR);
		} else {
			return Calendar.getInstance().get(Calendar.YEAR);
		}
	}
	
	/**
	 * 得到月份
	 * 注意，这里的月份依然是从0开始的
	 * @param c
	 * @return
	 */
	public static int getMonth(Calendar c) {
		if (c != null) {
			return c.get(Calendar.MONTH);
		} else {
			return Calendar.getInstance().get(Calendar.MONTH);
		}
	}
	
	/**
	 * 得到日期
	 * @param c
	 * @return
	 */
	public static int getDate(Calendar c) {
		if (c != null) {
			return c.get(Calendar.DATE);
		} else {
			return Calendar.getInstance().get(Calendar.DATE);
		}
	}
	
	/**
	 * 得到星期
	 * @param c
	 * @return
	 */
	public static int getDay(Calendar c) {
		if (c != null) {
			return c.get(Calendar.DAY_OF_WEEK);
		} else {
			return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		}
	}
	
	/**
	 * 得到小时
	 * @param c
	 * @return
	 */
	public static int getHour(Calendar c) {
		if (c != null) {
			return c.get(Calendar.HOUR);
		} else {
			return Calendar.getInstance().get(Calendar.HOUR);
		}
	}
	
	/**
	 * 得到分钟
	 * @param c
	 * @return
	 */
	public static int getMinute(Calendar c) {
		if (c != null) {
			return c.get(Calendar.MINUTE);
		} else {
			return Calendar.getInstance().get(Calendar.MINUTE);
		}
	}
	
	/**
	 * 得到秒
	 * @param c
	 * @return
	 */
	public static int getSecond(Calendar c) {
		if (c != null) {
			return c.get(Calendar.SECOND);
		} else {
			return Calendar.getInstance().get(Calendar.SECOND);
		}
	}
	
	/**
	 * 得到指定或者当前时间前n天的Calendar
	 * @param c
	 * @param n
	 * @return
	 */
	public static Calendar beforeNDays(Calendar c, int n) {
		//偏移量，给定n天的毫秒数
		long offset = n*24*60*60*1000;
		Calendar calendar = null;
		if (c != null) {
			calendar = c;
		} else {
			calendar = Calendar.getInstance();
		}
		
		calendar.setTimeInMillis(calendar.getTimeInMillis() - offset);
		return calendar;
	}
	
	/**
	 * 得到指定或者当前时间后n天的Calendar
	 * @param c
	 * @param n
	 * @return
	 */
	public static Calendar afterNDays(Calendar c, int n) {
		//偏移量，给定n天的毫秒数
		long offset = n*24*60*60*1000;
		Calendar calendar = null;
		if (c != null) {
			calendar = c;
		} else {
			calendar = Calendar.getInstance();
		}
		
		calendar.setTimeInMillis(calendar.getTimeInMillis() + offset);
		return calendar;
	}
	
	/**
	 * 昨天
	 * @param c
	 * @return
	 */
	public static Calendar yesterday(Calendar c) {
		long offset = 1*24*60*60*1000;
		Calendar calendar = null;
		if (c != null) {
			calendar = c;
		} else {
			calendar = Calendar.getInstance();
		}
		
		calendar.setTimeInMillis(calendar.getTimeInMillis() - offset);
		return calendar;
	}
	
	/**
	 * 明天
	 * @param c
	 * @return
	 */
	public static Calendar tomorrow(Calendar c) {
		long offset = 1*24*60*60*1000;
		Calendar calendar = null;
		if (c != null) {
			calendar = c;
		} else {
			calendar = Calendar.getInstance();
		}
		
		calendar.setTimeInMillis(calendar.getTimeInMillis() + offset);
		return calendar;
	}
	
	/**
	 * 得到指定或者当前时间前offset毫秒的Calendar
	 * @param c
	 * @param offset
	 * @return
	 */
	public static Calendar before(Calendar c, long offset) {
		Calendar calendar = null;
		if (c != null) {
			calendar = c;
		} else {
			calendar = Calendar.getInstance();
		}
		
		calendar.setTimeInMillis(calendar.getTimeInMillis() - offset);
		return calendar;
	}
	
	/**
	 * 得到指定或者当前时间前offset毫秒的Calendar
	 * @param c
	 * @param offset
	 * @return
	 */
	public static Calendar after(Calendar c, long offset) {
		Calendar calendar = null;
		if (c != null) {
			calendar = c;
		} else {
			calendar = Calendar.getInstance();
		}
		
		calendar.setTimeInMillis(calendar.getTimeInMillis() - offset);
		return calendar;
	}
	
	/**
	 * 日期格式化
	 * @param c
	 * @param pattern
	 * @return
	 */
	public static String format(Calendar c, String pattern) {
		Calendar calendar = null;
		if (c != null) {
			calendar = c;
		} else {
			calendar = Calendar.getInstance();
		}
		if (pattern == null || pattern.equals("")) {
			pattern = "yyyy年MM月dd日 HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		
		return sdf.format(calendar.getTime());
	}
	
	/**
	 * Date类型转换到Calendar类型
	 * @param d
	 * @return
	 */
	public static Calendar Date2Calendar(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return c;
	}
	
	/**
	 * Calendar类型转换到Date类型
	 * @param c
	 * @return
	 */
	public static Date Calendar2Date(Calendar c) {
		return c.getTime();
	}
	
	/**
	 * Date类型转换到Timestamp类型
	 * @param d
	 * @return
	 */
	public static Timestamp Date2Timestamp(Date d) {
		return new Timestamp(d.getTime());
	}
	
	/**
	 * Calendar类型转换到Timestamp类型
	 * @param c
	 * @return
	 */
	public static Timestamp Calendar2Timestamp(Calendar c) {
		return new Timestamp(c.getTimeInMillis());
	}
	
	/**
	 * Timestamp类型转换到Calendar类型
	 * @param ts
	 * @return
	 */
	public static Calendar Timestamp2Calendar(Timestamp ts) {
		Calendar c = Calendar.getInstance();
		c.setTime(ts);
		return c;
	}
	
	/**
	 * 得到当前时间的字符串表示
	 * 格式2010-02-02 12:12:12
	 * @return
	 */
	public static String getTimeString() {
		return format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 标准日期格式字符串解析成Calendar对象
	 * @param s
	 * @return
	 */
	public static Calendar pars2Calender(String s) {
		Timestamp ts = Timestamp.valueOf(s);
		return Timestamp2Calendar(ts);
	}
	
	public static Date String2Date(String dateStr) {
		Date date = null;
		try {
			date = dateFormate.parse(dateStr);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		} 
		return date;
	}
	
	public static String time2String(long time) {
		return dateFormate.format(time);
	}
	
	/**
     * 功能：计算时间
     * 上月开始、上月结束、本月开始、本月结束、上周开始、上周结束、本周开始、本周结束、昨天开始、昨天结束、今天开始、今天结束
     * 格式：2007-06-01 00:00:00  2007-06-30 23:59:59
     * 本月结束、本周结束、今天结束 都指的是当前日期时间
     * 一周按照：星期一至星期天来计算
     * @param tag
     * @return String
     */
	public static String getStartAndEndDate(DateTag tag){
		  
		String resultString;
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        java.text.DateFormat formatFull = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = formatFull.format(date);
        java.text.DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        switch (tag){
	        case ONE_YEAR_AGO://一年前
	        	cal.add(Calendar.MONTH, -12); 
	        	cal.set(Calendar.DATE, 1); 
	        	String oneYearAgo = format.format(cal.getTime()) + " 00:00:00";//一年前开始（一年前1号）
	        	cal.clear();
	        	resultString = oneYearAgo;
	        	break;
	        case HALF_YEAR_AGO://半年前
	        	cal.add(Calendar.MONTH, -6); 
	        	cal.set(Calendar.DATE, 1); 
	        	String halfYearAgo = format.format(cal.getTime()) + " 00:00:00";//半年前开始（1号）
	        	cal.clear();
	        	resultString = halfYearAgo;
	        	break;
            case LAST_MONTH_START://上月开始
                cal.add(Calendar.MONTH, -1); 
                cal.set(Calendar.DATE, 1); 
                String lastMonthStart = format.format(cal.getTime()) + " 00:00:00";//上月开始（上月1号）
                cal.clear();
                resultString = lastMonthStart;
                break;
            case LAST_MONTH_END://上月结束
                cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -1); 
                cal.set(Calendar.DATE,cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                String lastMonthEnd = format.format(cal.getTime()) + " 23:59:59";//上月结束（上月最后一天）
                cal.clear();
                resultString = lastMonthEnd;
                break;
            case THIS_MONTH_START://本月开始
                cal = Calendar.getInstance();
                cal.add(Calendar.MONDAY,0);
                cal.set(Calendar.DAY_OF_MONTH, 1); 
                String thisMonthStart = format.format(cal.getTime()) + " 00:00:00";//本月开始（本月1号）
                cal.clear();
                resultString = thisMonthStart;
                break;
            case THIS_MONTH_END://本月结束
                String thisMonthEnd = now;//本月结束（即：当前时间）
                resultString = thisMonthEnd;
                break;
            case LAST_WEEK_START://上周开始
                cal = Calendar.getInstance();
                cal.add(Calendar.WEEK_OF_YEAR, -1);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                String lastWeekStart = format.format(cal.getTime()) + " 00:00:00";//上周开始（即：上周一）
                cal.clear();
                resultString = lastWeekStart;
                break;
            case LAST_WEEK_END://上周结束
                cal = Calendar.getInstance();
                cal.add(Calendar.WEEK_OF_YEAR, 0);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                String lastWeekEnd = format.format(cal.getTime()) + " 23:59:59";//上周结束（即：上周日）
                cal.clear();
                resultString = lastWeekEnd;
                break;
            case THIS_WEEK_START://本周开始
                cal = Calendar.getInstance();
                cal.add(Calendar.WEEK_OF_YEAR, 0);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                String thisWeekStart = format.format(cal.getTime()) + " 00:00:00";//本周开始（即：本周一）
                cal.clear();
                resultString = thisWeekStart;
                break;
            case THIS_WEEK_END://本周结束
                String thisWeekEnd = now;//本周结束(即：当前时间)
                resultString = thisWeekEnd;
                break;
            case YESTERDAY_START://昨天开始
                cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, -1);
                format = new java.text.SimpleDateFormat("yyyy-MM-dd");
                String yesterdayStart = format.format(cal.getTime());
                yesterdayStart = yesterdayStart + " 00:00:00";//昨天开始
                cal.clear();
                resultString = yesterdayStart;
                break;
            case YESTERDAY_END://昨天结束
                cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, -1);
                format = new java.text.SimpleDateFormat("yyyy-MM-dd");
                String yesterdayEnd= format.format(cal.getTime());
                yesterdayEnd = yesterdayEnd + " 23:59:59";//昨天结束
                cal.clear();
                resultString = yesterdayEnd;
                break;
            case TODAY_START://今天开始
                String todayStart=format.format(date) + " 00:00:00";//今天开始
                resultString = todayStart;
                break;
            case TODAY_END://今天结束
                String todayEnd = now;//今天结束（即；当前时间）
                resultString = todayEnd;
                break;
            default://没有匹配的，返回当前时间
                resultString = now;
        }
        return resultString;
    }
	
	public static String long2String(long time, String pattern) {
		Timestamp st = new Timestamp(time);
		java.util.Date date = new java.util.Date(st.getTime());
		DateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	} 
	
	public static String formatTimeStampString(long when, boolean fullFormat) {
		android.text.format.Time then = new android.text.format.Time();
	    then.set(when);
	    android.text.format.Time now = new android.text.format.Time();
	    now.setToNow();

	    // Basic settings for formatDateTime() we want for all cases.
	    int format_flags = DateUtils.FORMAT_NO_NOON_MIDNIGHT |
	                       DateUtils.FORMAT_ABBREV_ALL |
	                       DateUtils.FORMAT_CAP_AMPM;

	    // If the message is from a different year, show the date and year.
	    if (then.year != now.year) {
	        format_flags |= DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE;
	    } else if (then.yearDay != now.yearDay) {
	        // If it is from a different day than today, show only the date.
	        format_flags |= DateUtils.FORMAT_SHOW_DATE;
	    } else {
	        // Otherwise, if the message is from today, show the time.
	        format_flags |= DateUtils.FORMAT_SHOW_TIME;
	    }

	    // If the caller has asked for full details, make sure to show the date
	    // and time no matter what we've determined above (but still make showing
	    // the year only happen if it is a different year from today).
	    if (fullFormat) {
	        format_flags |= (DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
	    }

	    return DateUtils.formatDateTime(KindroidMessengerApplication.context, when, format_flags);
	}
	
	//================以下是get和set方法=========================//
	
	public long getLNow() {
		return lNow;
	}

	public void setLNow(long now) {
		lNow = now;
	}

	public Calendar getCNow() {
		return cNow;
	}

	public void setCNow(Calendar now) {
		cNow = now;
	}

	public Date getDNow() {
		return dNow;
	}

	public void setDNow(Date now) {
		dNow = now;
	}

	public Timestamp getTNow() {
		return tNow;
	}

	public void setTNow(Timestamp now) {
		tNow = now;
	}

	public java.sql.Date getToday() {
		return today;
	}

	public void setToday(java.sql.Date today) {
		this.today = today;
	}

	public java.sql.Time getNow() {
		return now;
	}

	public void setNow(java.sql.Time now) {
		this.now = now;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
