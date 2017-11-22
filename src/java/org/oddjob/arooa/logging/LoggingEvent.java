package org.oddjob.arooa.logging;

/**
 * A Logging event.
 * 
 * @author rob
 *
 */
public interface LoggingEvent {

	/**
	 * Get the log level.
	 * 
	 * @return The log level. Never null.
	 */
    LogLevel getLevel();

    /**
     * Get the Mapped Diagnostic Context.
     * 
     * @param mdc
     * 
     * @return What the context is mapped to or null.
     */
    String getMdc(String mdc);
    
    /**
     * Get the name of the logger that created this event.
     * 
     * @return The name. May be null for the root.
     */
    String getLoggerName();

    /**
     * Get the message.
     * 
     * @return
     */
    String getMessage();

    /**
     * Get the thread name.
     * 
     * @return
     */
    String getThreadName();

    
    /**
     * Not sure this is used.
     * 
     * @return
     */
    Object[] getArgumentArray();

    /**
     * Get the time the event was created.
     * @return
     */
    long getTimeStamp();

    /**
     * Get the throwable.
     * 
     * @return
     */
    Throwable getThrowable();
}
