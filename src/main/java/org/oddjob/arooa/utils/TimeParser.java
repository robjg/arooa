package org.oddjob.arooa.utils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Parser for time. The format must be either:
 * <ul>
 * <li>HH:mm</li>
 * <li>HH:mm:ss</li>
 * <li>HH:mm:ss.SSS</li>
 * </ul>
 * The parser provides a Date based on the given Calendar or 
 * {@link CalendarAdaptor}.
 * 
 * @author rob
 *
 */
public class TimeParser {

	private final Pattern regexp = Pattern.compile(
		"(\\d+):(\\d\\d)(:(\\d\\d)(\\.\\d+)?)?");
	
	private CalendarAdaptor adaptor;
	
	/**
	 * Constructor.
	 * 
	 * @param adaptor The adaptor to base the date on.
	 */
	public TimeParser(CalendarAdaptor adaptor) {
		this.adaptor = adaptor; 
	}
	
	/**
	 * Constructor.
	 * 
	 * @param calendar The calendar to base the date on.
	 */
	public TimeParser(final Calendar calendar) {
		this(new CalendarAdaptor() {
			
			@Override
			public void set(int field, int value) {
				calendar.set(field, value);
			}
			@Override
			public Date getDate() {
				return calendar.getTime();
			}
		});
	}

	/**
	 * Constructor. The date will be base on the current date
	 * in the given time zone.
	 * 
	 * @param timeZone The given time zone.
	 */
	public TimeParser(TimeZone timeZone) {
		this(Calendar.getInstance(timeZone));
	}
	
	/**
	 * Constructor. The date will be base on the current date.
	 */
	public TimeParser() {
		this(Calendar.getInstance());
	}
	
	/**
	 * Parse the given time string.
	 * 
	 * @param text The time.
	 * 
	 * @return The date base on the provided calendar or 
	 * {@link CalendarAdaptor}.
	 * 
	 * @throws ParseException
	 */
	public Date parse(String text) throws ParseException {
		
		Matcher matcher = regexp.matcher(text);
		
		if (!matcher.matches()) {
			throw new ParseException("Unable to parse time: " + text, 0);
		}

		String hours = matcher.group(1);
		String minutes = matcher.group(2);
		String seconds = matcher.group(4);
		String milliseconds = matcher.group(5);
		
		adaptor.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hours));
		adaptor.set(Calendar.MINUTE, Integer.parseInt(minutes));
		if (seconds != null) {
			adaptor.set(Calendar.SECOND, Integer.parseInt(seconds));
		}
		else {
			adaptor.set(Calendar.SECOND, 0);
		}
		if (milliseconds != null) {
			adaptor.set(Calendar.MILLISECOND, (int) (Double.parseDouble(milliseconds) * 1000));
		}
		else {
			adaptor.set(Calendar.MILLISECOND, 0);
		}
		
		return adaptor.getDate();
	}	

}
