package org.oddjob.arooa.utils;

import java.util.Date;

/**
 * Allows a class to wrap a Calendar when setting date.
 * 
 * @see TimeParser
 * 
 * @author rob
 *
 */
public interface CalendarAdaptor {

	/**
	 * Set a field in the Calendar. The same as that from the Calendar class.
	 * 
	 * @param field The field, as used by Calendar.
	 * @param value The value, as used by Calendar.
	 */
	public void set(int field, int value);
	
	/**
	 * Get the date of the underlying Calendar.
	 * 
	 * @return
	 */
	public Date getDate();
}
