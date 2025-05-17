package org.oddjob.arooa.logging;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Adapter for Log4J.
 * 
 * @author rob
 *
 */
@SuppressWarnings("deprecation")
public class Log4jAdapter implements AppenderService {

	private final ConcurrentMap<Appender, AppenderSkeleton> appenders = 
			new ConcurrentHashMap<>();

	@Override
	public AppenderAdapter appenderAdapterFor(String loggerName) {

		Logger logger = Optional.ofNullable(loggerName)
				.map(name -> Logger.getLogger(name))
				.orElse(Logger.getRootLogger());
		
		return new AppenderAdapter() {
						
			@Override
			public AppenderAdapter setLevel(LogLevel level) {
				logger.setLevel(LOG4J_FROM_LEVELS.get(level));
				return this;
			}
			
			@Override
			public AppenderAdapter addAppender(Appender appender) {
				
				AppenderSkeleton log4jAppender = appenders.computeIfAbsent(
						appender, key -> new Log4jAppender(key));
				log4jAppender.setName(this.toString());
				logger.addAppender(log4jAppender);
				return this;
			}

			@Override
			public AppenderAdapter removeAppender(Appender appender) {
				
				Optional.ofNullable(appenders.get(appender))
						.ifPresent(a -> logger.removeAppender(a));
				return this;
			}
			
		};
	}
	
	@Override
	public Layout layoutFor(String pattern) {
		org.apache.log4j.Layout layout = new PatternLayout(pattern);
		return new Layout() {
			@Override
			public String format(LoggingEvent event) {
				return layout.format(((AdaptedOddjobLoggingEvent) event).log4jEvent);
			}
		};
	}
	
	@Override
	public void configure(String logConfigFileName) {
		System.setProperty("log4j.defaultInitOverride", "true");
	    PropertyConfigurator.configure(logConfigFileName);
	}

	private static class Log4jAppender extends AppenderSkeleton {

		private final Appender appender;
		
		Log4jAppender(Appender appender) {
			this.appender = appender;
		}
		
		@Override
		protected void append(org.apache.log4j.spi.LoggingEvent event) {	
			this.appender.append(new AdaptedOddjobLoggingEvent(event));
		}
		
		@Override
		public boolean requiresLayout() {
			return false;
		}
		
		@Override
		public void close() {
		}			
	}
	
	
	private static Map<org.apache.log4j.Priority, LogLevel> LOG4J_TO_LEVELS
		= new HashMap<>();

	private static Map<LogLevel, org.apache.log4j.Level> LOG4J_FROM_LEVELS
		= new HashMap<>();

	static {
		LOG4J_TO_LEVELS.put(org.apache.log4j.Level.ALL, LogLevel.INFO);
		LOG4J_TO_LEVELS.put(org.apache.log4j.Level.TRACE, LogLevel.TRACE);
		LOG4J_TO_LEVELS.put(org.apache.log4j.Level.DEBUG, LogLevel.DEBUG);
		LOG4J_TO_LEVELS.put(org.apache.log4j.Priority.DEBUG, LogLevel.DEBUG);
		LOG4J_TO_LEVELS.put(org.apache.log4j.Level.INFO, LogLevel.INFO);
		LOG4J_TO_LEVELS.put(org.apache.log4j.Priority.INFO, LogLevel.INFO);
		LOG4J_TO_LEVELS.put(org.apache.log4j.Level.WARN, LogLevel.WARN);
		LOG4J_TO_LEVELS.put(org.apache.log4j.Priority.WARN, LogLevel.WARN);
		LOG4J_TO_LEVELS.put(org.apache.log4j.Level.ERROR, LogLevel.ERROR);
		LOG4J_TO_LEVELS.put(org.apache.log4j.Priority.ERROR, LogLevel.ERROR);
		LOG4J_TO_LEVELS.put(org.apache.log4j.Level.FATAL, LogLevel.FATAL);
		LOG4J_TO_LEVELS.put(org.apache.log4j.Priority.FATAL, LogLevel.FATAL);
		LOG4J_TO_LEVELS.put(org.apache.log4j.Level.OFF, LogLevel.FATAL);
		
		LOG4J_FROM_LEVELS.put(LogLevel.TRACE, org.apache.log4j.Level.TRACE);
		LOG4J_FROM_LEVELS.put(LogLevel.DEBUG, org.apache.log4j.Level.DEBUG);
		LOG4J_FROM_LEVELS.put(LogLevel.INFO, org.apache.log4j.Level.INFO);
		LOG4J_FROM_LEVELS.put(LogLevel.WARN, org.apache.log4j.Level.WARN);
		LOG4J_FROM_LEVELS.put(LogLevel.ERROR, org.apache.log4j.Level.ERROR);
		LOG4J_FROM_LEVELS.put(LogLevel.FATAL, org.apache.log4j.Level.FATAL);
	}
	
	static class AdaptedOddjobLoggingEvent implements LoggingEvent {
		
		private final org.apache.log4j.spi.LoggingEvent log4jEvent;
		
		AdaptedOddjobLoggingEvent(org.apache.log4j.spi.LoggingEvent log4jEvent) {
			this.log4jEvent = log4jEvent;
		}
		
		@Override
		public LogLevel getLevel() {
			return LOG4J_TO_LEVELS.get(log4jEvent.getLevel());
		}

		@Override
		public String getMdc(String mdc) {
			return (String) log4jEvent.getMDC(mdc);
		}
		
		@Override
		public String getLoggerName() {
			return log4jEvent.getLoggerName();
		}

		@Override
		public String getMessage() {
			return log4jEvent.getMessage().toString();
		}

		@Override
		public String getThreadName() {
			return log4jEvent.getThreadName();
		}

		@Override
		public Object[] getArgumentArray() {
			return null;
		}

		@Override
		public long getTimeStamp() {
			return log4jEvent.getTimeStamp();
		}

		@Override
		public ThrowableProxy getThrowable() {
			return Optional.ofNullable(log4jEvent.getThrowableInformation())
					.map(ti -> ti.getThrowable())
					.map(t -> new ThrowableProxyAdapter(t))
					.orElse(null);
		}		
	}
	
	static class ThrowableProxyAdapter implements ThrowableProxy {
		
		private final Throwable throwable;
		
		public ThrowableProxyAdapter(Throwable iThrowableProxy) {
			Objects.requireNonNull(iThrowableProxy);
			this.throwable = iThrowableProxy;
		}
		
		@Override
		public String getMessage() {
			return throwable.getMessage();
		}
		
		@Override
		public String getClassName() {
			return throwable.getClass().getName();
		}
		
		@Override
		public ThrowableProxy getCause() {
			return Optional.ofNullable(throwable.getCause())
					.map(t -> new ThrowableProxyAdapter(t))
					.orElse(null);
		}

		@Override
		public StackTraceElement[] getStackTraceElementArray() {
			
			return throwable.getStackTrace();
		}
		
		@Override
		public ThrowableProxy[] getSuppressed() {

			return Arrays.stream(throwable.getSuppressed())
					.map(e-> new ThrowableProxyAdapter(e))
					.toArray(i -> new ThrowableProxyAdapter[i]);
		}
	}
}
