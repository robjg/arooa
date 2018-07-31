package org.oddjob.arooa.logging;

/**
 * Adapt the underlying logging infrastructure to accept an appender.
 * 
 * @see LoggerAdapter
 * 
 * @author rob
 *
 */
public interface AppenderAdapter {

	/**
	 * Set the log level at the point this adapter has been created.
	 * 
	 * @param level The level.
	 * 
	 * @return This.
	 */
	AppenderAdapter setLevel(LogLevel level);
	
	/**
	 * Add an Appender that will receive log events at the point the adapter has been created.
	 * 
	 * @param appender An Appender.
	 * 
	 * @return This.
	 */
	AppenderAdapter addAppender(Appender appender);
	
	/**
	 * Remove the Appender from the point the adapter has been created.
	 * 
	 * @param appender An Appender.
	 * 
	 * @return This.
	 */
	AppenderAdapter removeAppender(Appender appender);

}
