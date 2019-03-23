package cn.com.cyber.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {
	/** yyyyMMdd */
	public static final String YMD = "yyyyMMdd";
	/** yyyy/MM/dd */
	public static final String YMD_SLASH = "yyyy/MM/dd";
	/** yyyy-MM-dd */
	public static final String YMD_DASH = "yyyy-MM-dd";
	/** yyyy-MM */
	public static final String YM_DASH = "yyyy-MM";
	/** yyyy-MM-dd H:m */
	public static final String YMD_DASH_WITH_TIME = "yyyy-MM-dd HH:mm:ss";
	/** yyyy/dd/MM */
	public static final String YDM_SLASH = "yyyy/dd/MM";
	/** yyyy-dd-MM */
	public static final String YDM_DASH = "yyyy-dd-MM";
	/** HHmm */
	public static final String HM = "HHmm";
	/** HH:mm */
	public static final String HM_COLON = "HH:mm";
	/** 24 * 60 * 60 * 1000L */
	public static final long DAY = 24 * 60 * 60 * 1000L;

	private static final Map<String, DateFormat> DFS = new HashMap<String, DateFormat>();

	private static final Logger log = LoggerFactory.getLogger(DateUtil.class);

	private DateUtil() {
	}

	public static DateFormat getFormat(String pattern) {
		DateFormat format = DFS.get(pattern);
		if (format == null) {
			format = new SimpleDateFormat(pattern);
			DFS.put(pattern, format);
		}
		return format;
	}

	public static Date parse(String source, String pattern) {
		if (source == null) {
			return null;
		}
		Date date;
		try {
			DateFormat format = getFormat(pattern);
			log.info("parse:source {}   ", source);
			date = format.parse(source);
		} catch (ParseException e) {
			if (log.isDebugEnabled()) {
				log.debug(source + " doesn't match " + pattern);
			}
			return null;
		}
		return date;
	}

	public static String format(Date date, String pattern) {
		if (date == null) {
			return null;
		}
		return getFormat(pattern).format(date);
	}

	/**
	 * 将String EEE MMM dd HH:mm:ss Z yyyy 转换成date
	 * 
	 * @param source
	 * @return
	 */
	public static String fomatUK(String source) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
			Date date = formatter.parse(source);
			source = format(date, DateUtil.YMD_DASH);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return source;

	}

	/**
	 * @param year
	 *            年
	 * @param month
	 *            月(1-12)
	 * @param day
	 *            日(1-31)
	 * @return 输入的年、月、日是否是有效日期
	 */
	public static boolean isValid(int year, int month, int day) {
		if (month > 0 && month < 13 && day > 0 && day < 32) {
			// month of calendar is 0-based
			int mon = month - 1;
			Calendar calendar = new GregorianCalendar(year, mon, day);
			if (calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == mon
					&& calendar.get(Calendar.DAY_OF_MONTH) == day) {
				return true;
			}
		}
		return false;
	}

	private static Calendar convert(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar;
	}

	/**
	 * 返回指定年数位移后的日期
	 */
	public static Date yearOffset(Date date, int offset) {
		return offsetDate(date, Calendar.YEAR, offset);
	}

	/**
	 * 返回指定月数位移后的日期
	 */
	public static Date monthOffset(Date date, int offset) {
		return offsetDate(date, Calendar.MONTH, offset);
	}

	/**
	 * 返回指定天数位移后的日期
	 */
	public static Date dayOffset(Date date, int offset) {
		return offsetDate(date, Calendar.DATE, offset);
	}
	
	/**
	 * 返回指定天数位移后的日期
	 */
	public static Date hourOffset(Date date, int offset) {
		Calendar calendar = convert(date);
		calendar.add(Calendar.HOUR, offset); //减填负数
		return calendar.getTime();
	}

	/**
	 * 返回指定日期相应位移后的日期
	 * 
	 * @param date
	 *            参考日期
	 * @param field
	 *            位移单位，见 {@link Calendar}
	 * @param offset
	 *            位移数量，正数表示之后的时间，负数表示之前的时间
	 * @return 位移后的日期
	 */
	public static Date offsetDate(Date date, int field, int offset) {
		Calendar calendar = convert(date);
		calendar.add(field, offset);
		return calendar.getTime();
	}

	/**
	 * 返回当月第一天的日期
	 */
	public static Date firstDay(Date date) {
		Calendar calendar = convert(date);
		calendar.set(Calendar.DATE, 1);
		return calendar.getTime();
	}

	/**
	 * 返回当月最后一天的日期
	 */
	public static Date lastDay(Date date) {
		Calendar calendar = convert(date);
		calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
		return calendar.getTime();
	}

	/**
	 * 返回两个日期间的差异天数
	 * 
	 * @param date1
	 *            参照日期
	 * @param date2
	 *            比较日期
	 * @return 参照日期与比较日期之间的天数差异，正数表示参照日期在比较日期之后，0表示两个日期同天，负数表示参照日期在比较日期之前
	 */
	public static int dayDiff(Date date1, Date date2) {
		long diff = date1.getTime() - date2.getTime();
		return (int) (diff / DAY);
	}

	/**
	 * 当前月的天数
	 * 
	 * @param date
	 * @return
	 */
	public static int numberOfDays(Date date) {
		log.info("numberOfDays {}", format(date, YMD_DASH));
		Date firstDay = firstDay(date);
		Date lastDay = lastDay(date);
		return dayDiff(lastDay, firstDay) + 1;
	}

	/**
	 * 获取当前日期的00:00:00
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDate(Date date) {
		Calendar cal = convert(date);
		int yy = cal.get(Calendar.YEAR);
		int MM = cal.get(Calendar.MONTH);
		int dd = cal.get(Calendar.DATE);
		Calendar cal1 = Calendar.getInstance();
		cal1.set(yy, MM, dd, 00, 00, 00);
		return cal1.getTime();
	}

	/**
	 * 获取当前日期的周一
	 * 
	 * @return
	 */
	public static Date getMonday(Date date) {
		Calendar c = convert(date);
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0)
			day_of_week = 7;
		c.add(Calendar.DATE, -day_of_week + 1);
		return c.getTime();
	}

	/**
	 * 得到本周周日
	 * 
	 * @return yyyy-MM-dd
	 */
	public static Date getSunday(Date date) {
		Calendar c = convert(date);
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0)
			day_of_week = 7;
		c.add(Calendar.DATE, -day_of_week + 7);
		return c.getTime();
	}

	/**
	 * 获取mongoDate
	 * 
	 * @param date
	 * @return
	 */
	public static Date setMongoDate(Date date) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			format.setCalendar(new GregorianCalendar(new SimpleTimeZone(0, "GMT")));
			String str = DateUtil.format(date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 获取mongoDate
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMongoDate(Date date) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			format.setCalendar(new GregorianCalendar(new SimpleTimeZone(8, "GMT")));
			String str = DateUtil.format(date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 根据timemills 返回date 对象
	 * @param timemills
	 * @return
	 */
	public static Date parseDateFromLong(long timemills){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timemills);
		return cal.getTime();
	}

	public static void main(String[] args) {
		// String dd
		// ="http://36588.com.cn/website/upload/goods/2016/1/26/519ce245-761a-49ab-bfa3-bda454b05063.jpeg,http://36588.com.cn/website/upload/goods/2016/1/26/5903f9cb-d4f7-41e1-92a2-b9b894eca940.jpeg,http://36588.com.cn/website/upload/goods/2016/1/26/2887b1c1-e29e-47fe-8743-8236a5b6bff8.jpeg,http://36588.com.cn/website/upload/goods/2016/1/26/70c7c87e-9bb5-4834-8467-a077c72de389.jpeg,http://36588.com.cn/website/upload/goods/2015/7/6/918f3942-53d7-4629-9a7f-564c41121133.jpeg";
		// String[] s = dd.split(",");
		// System.out.println(s.length);\
		// getDate(new Date());
		
		parse("2017-12-05", YMD_DASH);
		System.out.println(parse("2017-12-05", YMD_DASH));
	}
}
