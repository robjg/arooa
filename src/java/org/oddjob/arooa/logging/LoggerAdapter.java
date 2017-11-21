package org.oddjob.arooa.logging;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

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
	
	public static AppenderAdapter appenderAdapterFor(String loggerName) {
		return delegate._appenderAdapterFor(loggerName);
	}
	
	public static AppenderAdapter appenderAdapterForRoot() {
		return delegate._appenderAdapterFor((String) null);
	}
	
	public static AppenderAdapter appenderAdapterFor(Class<?> cl) {
		return delegate._appenderAdapterFor(cl.getName());
	}

	public static Layout layoutFor(String pattern) {
		return delegate._layoutFor(pattern);
	}

	abstract public AppenderAdapter _appenderAdapterFor(String loggerName);
	
	public AppenderAdapter _appenderAdapterForRoot() {
		return _appenderAdapterFor((String) null);
	}
	
	public AppenderAdapter _appenderAdapterFor(Class<?> cl) {
		return _appenderAdapterFor(cl.getName());
	}

	abstract public Layout _layoutFor(String pattern);
}
