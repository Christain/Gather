package com.gather.android.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

	/**
	 * 
	 * 获取格式化的时间
	 * 
	 * @param format
	 *            yyyy-MM-dd HH:mm; MM-dd HH:mm;HH:mm;yyyy-MM-dd;yyyy年M月d日
	 *            HH:mm;M月d日 HH:mm;H:mm;...
	 * @param timeMills
	 * @return
	 */
	public static String getFormatedTime(String format, long timeMills) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(timeMills);
	}
	
	/**
	 * 活动列表时间格式
	 * @param oldStyleTime
	 * @return
	 */
	public static String getActListTime(String oldStyleTime) {
		if (oldStyleTime.contains("-") && oldStyleTime.length() > 5) {
			SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ParsePosition pos = new ParsePosition(0);
			Date strtodate = oldFormat.parse(oldStyleTime, pos);
			SimpleDateFormat format = new SimpleDateFormat("M月d日");
			String dateString = format.format(strtodate);
			return dateString;
		} else {
			return "";
		}
	}
	
	/**
	 * 活动详情时间格式
	 * @param oldStyleTime
	 * @return
	 */
	public static String getActDetailTime(String oldStyleTime) {
		if (oldStyleTime.contains("-") && oldStyleTime.length() > 5) {
			SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ParsePosition pos = new ParsePosition(0);
			Date strtodate = oldFormat.parse(oldStyleTime, pos);
			SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");
			String dateString = format.format(strtodate);
			return dateString;
		} else {
			return "";
		}
	}

	public static long getStringtoLong(String time) {
		SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = oldFormat.parse(time, pos);
		return strtodate.getTime();
	}

	/**
	 * 获取用户年龄
	 * @param birthday
	 * @return
	 */
	public static int getUserAge(String birthday) {
		if (birthday.contains("-")) {
			SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd");
			ParsePosition pos = new ParsePosition(0);
			Date date = oldFormat.parse(birthday, pos);
			Date dateNow = new Date(System.currentTimeMillis());
			if (dateNow.getMonth()+1 < date.getMonth() +1) {
				return dateNow.getYear() - date.getYear() - 1;
			} else if (dateNow.getMonth() == date.getMonth()) {
				if (dateNow.getDate() < date.getDate()) {
					return dateNow.getYear() - date.getYear() - 1;
				} else {
					return dateNow.getYear() - date.getYear();
				}
			} else {
				return dateNow.getYear() - date.getYear();
			}
		} else {
			return 0;
		}
	}

	/**
	 * 获取微博格式化的时间
	 * 
	 * @param timeMills
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getRefreshFormatedTime(long timeSeconds) {
		long timeMills = timeSeconds;
		long millsNow = System.currentTimeMillis();
		long dMills = (millsNow - timeMills) / 1000;// 秒
		Date dateNow = new Date(millsNow);
		Date date = new Date(timeMills);
		if (dateNow.getYear() != date.getYear()) {
			return getFormatedTime("yyyy年M月d日  HH:mm", timeMills);
		}
		if (dMills <= 172800) {
			if (dateNow.getDay() == date.getDay()) {
				return "" + getFormatedTime("HH:mm", timeMills);
			} else {
				return "昨天" + getFormatedTime("HH:mm", timeMills);
			}
		}
		if (dMills < 259200) {
			return "前天" + getFormatedTime("HH:mm", timeMills);
		} else {
			return getFormatedTime("M月d日 HH:mm", timeMills);
		}
	}
}
