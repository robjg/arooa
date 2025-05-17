package org.oddjob.arooa.logging;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.ServiceLoader;

/**
 * Adapt an underlying logging framework.
 * 
 * @author rob
 *
 */
public class LoggerAdapter {
	
	private static final AppenderService delegate;

	static {
		ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
		String className = loggerFactory.getClass().getName();

		ServiceLoader<AppenderServiceFactory> serviceLoader = ServiceLoader.load(AppenderServiceFactory.class);

		AppenderService last = null;
		for (AppenderServiceFactory factory : serviceLoader) {
			last = Optional.ofNullable(factory.appenderServiceFor(className)).orElse(last);
		}
		if (last == null) {
			throw new IllegalStateException("No Appender for " + className);
		}
		else {
			delegate = last;
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
		return delegate.appenderAdapterFor(loggerName);
	}
	
	/**
	 * Provide an {@link AppenderAdapter} for the underlying Root logger.
	 * 
	 * @return An Appender Adapter. Never null.
	 */
	public static AppenderAdapter appenderAdapterForRoot() {
		return delegate.appenderAdapterFor((String) null);
	}
	
	/**
	 * Provide an {@link AppenderAdapter} for the underlying logger.
	 * 
	 * @param cl The class that will provide the name of the logger.
	 * 
	 * @return An Appender Adapter. Never null.
	 */
	public static AppenderAdapter appenderAdapterFor(Class<?> cl) {
		return delegate.appenderAdapterFor(cl.getName());
	}

	/**
	 * Provide a layout for the given pattern.
	 * 
	 * @param pattern The pattern. This must match the underlying framework used.
	 * 
	 * @return A Layout.
	 */
	public static Layout layoutFor(String pattern) {
		return delegate.layoutFor(pattern);
	}

	public static void configure(String logConfigFileName) {
		delegate.configure(logConfigFileName);
	}

	// No non-static methods.
	private LoggerAdapter() {}
}
