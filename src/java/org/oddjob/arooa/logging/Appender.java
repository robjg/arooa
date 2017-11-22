package org.oddjob.arooa.logging;

/**
 * Implementations may be add to an {@link AppenderAdapter} to capture log messages.
 * 
 * @author rob
 *
 */
public interface Appender {

	/**
	 * Called by the underlying Log implementation with event.
	 * 
	 * @param event The log event. Never null.
	 */
	void append(LoggingEvent event);
	
}
