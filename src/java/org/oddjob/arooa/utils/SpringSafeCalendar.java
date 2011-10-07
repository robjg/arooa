package org.oddjob.arooa.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Provide a wrapper for a Calendar that moves all times in the Spring
 * transition to Daylight Saving Time missing hour to the end of the boundary.
 * The standard SimpleDateFormat parser moves 1:45am to 2:45am which 
 * means that 1:45 to 2:15 becomes 2:45 to 2:15 which is a backward interval.
 * This adaptor will mean these times become 2:00 to 2:15.
 * <p>
 * All other times remain unchanged.
 * 
 * @author rob
 *
 */
public class SpringSafeCalendar implements CalendarAdaptor {

	private final Calendar calendar;
	
	boolean inMissingHour;
	
	public SpringSafeCalendar() {
		this(Calendar.getInstance());
	}
	
	public SpringSafeCalendar(Date date, TimeZone timeZone) {
		this.calendar = Calendar.getInstance(timeZone); 
		calendar.setTime(date);
	}
	
	public SpringSafeCalendar(Calendar calendar) {
		this.calendar = Calendar.getInstance(calendar.getTimeZone());
		this.calendar.clear();
		this.calendar.set(calendar.get(Calendar.YEAR), 
				calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.DATE));
	}
	
	@Override
	public void set(int field, int value) {
		
		if (field == Calendar.HOUR_OF_DAY) {
			calendar.set(Calendar.HOUR_OF_DAY, value);
			if (value != calendar.get(Calendar.HOUR_OF_DAY)) {
				inMissingHour = true;
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
			}
			else {
				inMissingHour = false;
			}
			return;
		}
		if (inMissingHour) {
			if (field == Calendar.MINUTE) {
				return;
			}
			if (field == Calendar.SECOND ) {
				return;
			}
			if (field == Calendar.MILLISECOND) {
				return;
			}
			
		}
		
		calendar.set(field, value);
	}

	@Override
	public Date getDate() {
		return calendar.getTime();
	}
}
