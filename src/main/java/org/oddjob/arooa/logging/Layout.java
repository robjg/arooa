package org.oddjob.arooa.logging;

/**
 * Something that can format a log event.
 * 
 * @author rob
 *
 */
public interface Layout {

	/**
	 * Format the event.
	 * 
	 * @param event The event. Must not be null.
	 * 
	 * @return The formatted event.
	 */
	String format(LoggingEvent event);
}
