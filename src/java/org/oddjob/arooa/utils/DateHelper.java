/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.oddjob.arooa.ArooaConstants;

/**
 * Date Helper Utilities 
 * 
 * @author Rob Gordon.
 */
public class DateHelper {

	/**
	 * Parse a date and time. The input can either be just a date 
	 * or a date and a time.
	 * 
	 * @param text The date time.
	 * @return The date for the given text in the current time zone.
	 * @throws ParseException If the text isn't in a recognized
	 * date/time foramt.
	 */
	public static Date parseDateTime(String text) throws ParseException {
		return parseDateTime(text, TimeZone.getDefault());
	}
	
	/**
	 * Parse a date and time in the given time zone. The input can
	 * either be just a date or a date and time.
	 * 
	 * @param text The date time.
	 * @param timeZoneId The time zone identifier.
	 * @return The date for the given text in the specified time zone. 
	 * @throws ParseException If the text isn't in a recognized 
	 * date/time foramt.
	 */
	public static Date parseDateTime(String text, String timeZoneId) 
	throws ParseException {
		TimeZone timeZone = TimeZone.getDefault();
		if (timeZoneId != null) {
			timeZone = TimeZone.getTimeZone(timeZoneId);
		}
		return parseDateTime(text, timeZone);
	}
	
	/**
	 * Parse a date and time in the given time zone. The input can
	 * either be just a date or a date and time.
	 * 
	 * @param text The date time
	 * @param timeZone The timeZone.
	 * @return The date for the given text in the specified time zone.
	 * @throws ParseException If the text isn't in a recognized 
	 * date/time foramt.
	 */
	public static Date parseDateTime(String text, TimeZone timeZone) 
	throws ParseException {
		if (text.indexOf(' ') == -1) {
			return parseDate(text, timeZone);
		}
		else {
			try {
				return parse(text, ArooaConstants.DATE_FORMAT + " " 
						+ ArooaConstants.TIME_FORMAT1, timeZone);
			} catch (ParseException e) {
				try {
					return parse(text, ArooaConstants.DATE_FORMAT + " " 
							+ ArooaConstants.TIME_FORMAT2, timeZone);
				} catch (ParseException e2) {
					return parse(text, ArooaConstants.DATE_FORMAT + " " 
							+ ArooaConstants.TIME_FORMAT3, timeZone);					
				}
			}
		}
	}

	/**
	 * Parse a date using the default time zone.
	 * 
	 * @param text A date.
	 * @return The date for the given text.
	 * @throws ParseException If the date isn't in the recognized 
	 * date format. 
	 */
	public static Date parseDate(String text) throws ParseException {
		return parseDate(text, TimeZone.getDefault());
	}
	
	/**
	 * Parse a date using the given time zone.
	 * 
	 * @param text The date text.
	 * @param timeZoneId The time zone identifier.
	 * 
	 * @return The date for the given text in the specified time zone.
	 * @throws ParseException If the date isn't in the recognized
	 * date format.
	 */
	public static Date parseDate(String text, String timeZoneId) throws ParseException {
		TimeZone timeZone = TimeZone.getDefault();
		if (timeZoneId != null) {
			timeZone = TimeZone.getTimeZone(timeZoneId);
		}
		return parseDate(text, timeZone);
	}
	
	/**
	 * Parse a date using the given time zone.
	 * 
	 * @param text The date text.
	 * @param timeZone The time zone.
	 * @return The date for the given text in the specified time zone.
	 * @throws ParseException If the date isn't in the recognized
	 * date format.
	 */
	public static Date parseDate(String text, TimeZone timeZone) throws ParseException {
		return parse(text, ArooaConstants.DATE_FORMAT, timeZone);		
	}
	
	/**
	 * Parse a time into a number of milliseconds.
	 * 
	 * @param text The time.
	 * @return The time as milliseconds.
	 * @throws ParseException If pasing fails.
	 */
	public static long parseTime(String text) throws ParseException {
		TimeZone timeZone = TimeZone.getTimeZone("GMT+00");
		Date d = null;
		try {
			d = parse(text, ArooaConstants.TIME_FORMAT1, timeZone);
		} catch (ParseException e) {
			try {
				d = parse(text, ArooaConstants.TIME_FORMAT2, timeZone);
			} catch (ParseException e2) {
					d = parse(text, ArooaConstants.TIME_FORMAT3, timeZone);				
			}
		}
		return d.getTime();
	}
	
	/**
	 * Format a date into just text representing just the date.
	 * 
	 * @param date The date
	 * @return The text equivalent.
	 */
	public static String formatDate(Date date) {
		return new SimpleDateFormat(ArooaConstants.DATE_FORMAT).format(date);
	}
	
	/**
	 * Format a date into full date/time text.
	 * 
	 * @param date The date
	 * @return The text equivalent.
	 */
	public static String formatDateTime(Date date) {
		return new SimpleDateFormat(ArooaConstants.DATE_FORMAT + " " + 
				ArooaConstants.TIME_FORMAT1).format(date);
	}
	
	public static String formatDateTimeInteligently(Date date) {		
		if (date == null) {
			return null;
		}
		
		if (date.getTime() % 1000 == 0) {
			// no milliseconds - then miss them off.
			return new SimpleDateFormat(
					ArooaConstants.DATE_FORMAT + " " + 
					ArooaConstants.TIME_FORMAT2).format(date);
		}
		else {
			return new SimpleDateFormat(
					ArooaConstants.DATE_FORMAT + " " + 
					ArooaConstants.TIME_FORMAT1).format(date);
		}
	}
	
	/**
	 * Helper function. Not public.
	 * 
	 * @param text
	 * @param format
	 * @param timeZone
	 * @return
	 * @throws ParseException
	 */
	static Date parse(String text, String format, TimeZone timeZone) throws ParseException {
		SimpleDateFormat f = new SimpleDateFormat(format);
		f.setTimeZone(timeZone);

		Date d = f.parse(text);
		
		Calendar c = Calendar.getInstance();
		c.setTimeZone(timeZone);
		c.setTime(d);
		
		return c.getTime();
	}
	
}


