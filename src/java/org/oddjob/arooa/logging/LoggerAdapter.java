package org.oddjob.arooa.logging;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

/**
 * Adapt an underlying logging framework.
 * 
 * @author rob
 *
 */
abstract public class LoggerAdapter {	
	
	private static final LoggerAdapter delegate;
	
	
	static {
		ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
		String className = loggerFactory.getClass().getName();
		String concreateClassName;
		if ("org.slf4j.impl.Log4jLoggerFactory".equals(className)) {
			concreateClassName = "org.oddjob.arooa.logging.Log4jLoggerAdapter";
			
		}
		else {
			throw new IllegalStateException("No Appender for " + className);
		}
		
		try {
			delegate = (LoggerAdapter) Class.forName(concreateClassName).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}	
	
	/**
	 * Provide an {@link AppenderAdapter} for the underlying logger.
	 * 
	 * @param loggerName The name of the logger.
	 * 
	 * @return An Appender Adapter. Never null.
	 */
	public static AppenderAdapter appenderAdapterFor(String loggerName) {
		return delegate._appenderAdapterFor(loggerName);
	}
	
	/**
	 * Provide an {@link AppenderAdapter} for the underlying Root logger.
	 * 
	 * @return An Appender Adapter. Never null.
	 */
	public static AppenderAdapter appenderAdapterForRoot() {
		return delegate._appenderAdapterFor((String) null);
	}
	
	/**
	 * Provide an {@link AppenderAdapter} for the underlying logger.
	 * 
	 * @param loggerName The class that will provide the name of the logger.
	 * 
	 * @return An Appender Adapter. Never null.
	 */
	public static AppenderAdapter appenderAdapterFor(Class<?> cl) {
		return delegate._appenderAdapterFor(cl.getName());
	}

	/**
	 * Provide a layout for the given pattern.
	 * 
	 * @param pattern The pattern. This must match the underlying framework used.
	 * 
	 * @return A Layout.
	 */
	public static Layout layoutFor(String pattern) {
		return delegate._layoutFor(pattern);
	}

	abstract protected AppenderAdapter _appenderAdapterFor(String loggerName);
	
	protected AppenderAdapter _appenderAdapterForRoot() {
		return _appenderAdapterFor((String) null);
	}
	
	protected AppenderAdapter _appenderAdapterFor(Class<?> cl) {
		return _appenderAdapterFor(cl.getName());
	}

	abstract protected Layout _layoutFor(String pattern);
}
