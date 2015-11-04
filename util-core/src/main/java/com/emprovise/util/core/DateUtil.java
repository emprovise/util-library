package com.emprovise.util.core;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateUtil {
	
	private static final String[] MONTH_NAMES = new DateFormatSymbols().getMonths();

    public static Date getCurrentDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
    
	public static Date getDate(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, int millisecond){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MILLISECOND, millisecond);		
		Date date = cal.getTime();
		return date;
	}

	public static String getCurrentDateTime(String dateFormat){
		return new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime());
	}

	public static Date getPreviousDate(int noOfDays){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -noOfDays);
		return calendar.getTime();
	}

	public static Date addDays(Date day, int daysToAdd) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(day);		
		cal.add(Calendar.DATE, daysToAdd);
		return cal.getTime();
	}
    
    public static Date addMonths(Date day, int monthsToAdd) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(day);		
		cal.add(Calendar.MONTH, monthsToAdd);
		return cal.getTime();
	}

	public static Date addYears(Date day, int yearsToAdd) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(day);		
		cal.add(Calendar.YEAR, yearsToAdd);
		return cal.getTime();
	}

	public static Long getDifference(Date date1, Date date2, TimeUnit timeUnit){
		long difference = date2.getTime() - date1.getTime();

		if(timeUnit != null) {
			return timeUnit.convert(difference, TimeUnit.MILLISECONDS);
		}

		return null;
	}

	public static String formatDate(Date date, String formatTo){
		String strDate = "";
		if (null != date) {
			strDate = new SimpleDateFormat(formatTo).format(date);
		}
		return strDate;
	}

	public static String formatDate(String strDate, String formatFrom, String formatTo) throws ParseException {
		if(null != strDate && strDate.trim().length() > 0){
			SimpleDateFormat formatterFrom = new SimpleDateFormat(formatFrom);
			SimpleDateFormat formatterTo = new SimpleDateFormat(formatTo);
			Date date = formatterFrom.parse(strDate);
			strDate = formatterTo.format(date);
		}		
		return strDate;
	}

	public static Date convertToDate(String strdate, String format) throws ParseException {
		Date frmDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		if (strdate != null && !strdate.isEmpty()) {
			frmDate = sdf.parse(strdate);
		}
		return frmDate;
	}

	public static boolean isValidFormat(String format, String date) {
		if (date != null) {
			try {
				new SimpleDateFormat(format).parse(date);
				return true;
			} catch (ParseException e) {
				return false;
			}
		}
		return false;
	}

	public static boolean isSameDay(Date d1, Date d2){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(d1).equals(sdf.format(d2));
	}

	public static boolean isWithinRange(Date dateToCheck, Date startDate, Date endDate) {
		   return !(dateToCheck.before(startDate) || dateToCheck.after(endDate));
	}
	
	public static boolean isTimerExpired(Date timerStartTime, int timerDurationInHours){
		if(timerStartTime == null){
			return true;
		}

		Calendar cal = Calendar.getInstance();
		Date currentTime = cal.getTime();

		cal.setTime(timerStartTime);
		cal.add(Calendar.HOUR, timerDurationInHours);
		Date timeLapsed = cal.getTime();

		if(timeLapsed.compareTo(currentTime) > 0){
			return false;
		}
		return true;
	}
	
	public static Date convertTimeZone(Date date, TimeZone fromTimeZone , TimeZone toTimeZone)
    {  
        long fromTZDst = 0;

        if(fromTimeZone.inDaylightTime(date)) {
            fromTZDst = fromTimeZone.getDSTSavings();
        }  
  
        long fromTZOffset = fromTimeZone.getRawOffset() + fromTZDst;
        long toTZDst = 0;

        if(toTimeZone.inDaylightTime(date)) {
            toTZDst = toTimeZone.getDSTSavings();
        }

        long toTZOffset = toTimeZone.getRawOffset() + toTZDst;
        return new Date(date.getTime() + (toTZOffset - fromTZOffset));
    }  

	public static XMLGregorianCalendar convertDateToXMLGregorianCalendar(Date date) throws DatatypeConfigurationException {
		if(date != null){
			GregorianCalendar gregorianCalendar = new GregorianCalendar();
			gregorianCalendar.setTime(date);
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
		}
		return null;
	}
	
	public static Date nextDayOfWeek(Date date, int dayOfWeek) {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);	
		
        int diff = dayOfWeek - cal.get(Calendar.DAY_OF_WEEK);
        if (!(diff > 0)) {
            diff += 7;
        }
        cal.add(Calendar.DAY_OF_MONTH, diff);
        return cal.getTime();
    }
	
	public static Date startOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}
	
	public static Date endOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	public static String monthName(int monthNumber) {
		return MONTH_NAMES[monthNumber];
	}
	
	public static String monthName(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return monthName(cal.get(Calendar.MONTH));
	}
	
	public static Integer monthNumber(String monthName) throws Exception{
		Integer monthNumber = null;
		Date date = new SimpleDateFormat("MMM").parse(monthName);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		monthNumber = cal.get(Calendar.MONTH);
		
		return monthNumber;
	}

	public String formatDurationFromSeconds(Long timeInSecond) {
		Long day = TimeUnit.DAYS.convert(timeInSecond, TimeUnit.SECONDS);
		Long hours = (timeInSecond - TimeUnit.DAYS.toSeconds(day)) / TimeUnit.HOURS.toSeconds(1);
		Long minutes = (timeInSecond % TimeUnit.HOURS.toSeconds(1)) / TimeUnit.MINUTES.toSeconds(1);
		Long seconds = timeInSecond % TimeUnit.MINUTES.toSeconds(1);
		return day + "-" + hours + ":" + minutes + ":" + seconds;
	}
}
