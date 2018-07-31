package org.oddjob.arooa.logging;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;


/**
 * Adapter for Logback.
 * 
 * @author rob
 *
 */
public class LogbackLoggerAdapter extends LoggerAdapter {

	
	
	private final ConcurrentMap<Appender, LogbackAppender> appenders = 
			new ConcurrentHashMap<>();
	
	private final Context context = (Context) LoggerFactory.getILoggerFactory();

	@Override
	public AppenderAdapter _appenderAdapterFor(String loggerName) {

		Logger logger = (Logger) Optional.ofNullable(loggerName)
				.map(name -> LoggerFactory.getLogger(name))
				.orElse(LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME));
		
		return new AppenderAdapter() {
						
			@Override
			public AppenderAdapter setLevel(LogLevel level) {
				logger.setLevel(LOG4J_FROM_LEVELS.get(level));
				return this;
			}
			
			@Override
			public AppenderAdapter addAppender(Appender appender) {
				
				LogbackAppender logbackAppender = appenders.computeIfAbsent(
						appender, key -> new LogbackAppender(key));
				logbackAppender.setName(this.toString());
				logbackAppender.setContext(context);
				logbackAppender.start();
				logger.addAppender(logbackAppender);
				return this;
			}

			@Override
			public AppenderAdapter removeAppender(Appender appender) {
				
				Optional.ofNullable(appenders.get(appender))
						.ifPresent(a -> { 
							logger.detachAppender(a); 
							a.stop(); 
							return;});
				return this;
			}
			
		};
	}
	
	@Override
	public Layout _layoutFor(String pattern) {
		PatternLayout layout = new PatternLayout();
		layout.setPattern(pattern);
		layout.setContext(context);
		layout.start();
		
		return new Layout() {
			@Override
			public String format(LoggingEvent event) {
				return layout.doLayout(((AdaptedOddjobLoggingEvent) event).logbackEvent);
			}
		};
	}
	
	@Override
	protected void _configre(String logConfigFileName) {
		
		
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
	    
	    try {
	      JoranConfigurator configurator = new JoranConfigurator();
	      configurator.setContext(context);
	      // Call context.reset() to clear any previous configuration, e.g. default 
	      // configuration. For multi-step configuration, omit calling context.reset().
	      context.reset(); 
	      configurator.doConfigure(logConfigFileName);
	    } catch (JoranException je) {
	      // StatusPrinter will handle this
	    }
	    StatusPrinter.printInCaseOfErrorsOrWarnings(context);
	}

	private static class LogbackAppender extends AppenderBase<ILoggingEvent>{

		private final Appender appender;
		
		LogbackAppender(Appender appender) {
			this.appender = appender;
		}

		
		@Override
		protected void append(ILoggingEvent event) {	
			this.appender.append(new AdaptedOddjobLoggingEvent(event));
		}
		
	}
	
	
	private static Map<ch.qos.logback.classic.Level, LogLevel> LOG4J_TO_LEVELS
		= new HashMap<>();

	private static Map<LogLevel, ch.qos.logback.classic.Level> LOG4J_FROM_LEVELS
		= new HashMap<>();

	static {
		LOG4J_TO_LEVELS.put(ch.qos.logback.classic.Level.ALL, LogLevel.INFO);
		LOG4J_TO_LEVELS.put(ch.qos.logback.classic.Level.TRACE, LogLevel.TRACE);
		LOG4J_TO_LEVELS.put(ch.qos.logback.classic.Level.DEBUG, LogLevel.DEBUG);
		LOG4J_TO_LEVELS.put(ch.qos.logback.classic.Level.INFO, LogLevel.INFO);
		LOG4J_TO_LEVELS.put(ch.qos.logback.classic.Level.WARN, LogLevel.WARN);
		LOG4J_TO_LEVELS.put(ch.qos.logback.classic.Level.ERROR, LogLevel.ERROR);
		LOG4J_TO_LEVELS.put(ch.qos.logback.classic.Level.OFF, LogLevel.FATAL);
		
		LOG4J_FROM_LEVELS.put(LogLevel.TRACE, ch.qos.logback.classic.Level.TRACE);
		LOG4J_FROM_LEVELS.put(LogLevel.DEBUG, ch.qos.logback.classic.Level.DEBUG);
		LOG4J_FROM_LEVELS.put(LogLevel.INFO, ch.qos.logback.classic.Level.INFO);
		LOG4J_FROM_LEVELS.put(LogLevel.WARN, ch.qos.logback.classic.Level.WARN);
		LOG4J_FROM_LEVELS.put(LogLevel.ERROR, ch.qos.logback.classic.Level.ERROR);
		LOG4J_FROM_LEVELS.put(LogLevel.FATAL, ch.qos.logback.classic.Level.ERROR);
	}
	
	static class AdaptedOddjobLoggingEvent implements LoggingEvent {
		
		private final ILoggingEvent logbackEvent;
		
		AdaptedOddjobLoggingEvent(ILoggingEvent logbackEvent) {
			this.logbackEvent = logbackEvent;
		}
		
		@Override
		public LogLevel getLevel() {
			return LOG4J_TO_LEVELS.get(logbackEvent.getLevel());
		}

		@Override
		public String getMdc(String mdc) {
			return (String) logbackEvent.getMDCPropertyMap().get(mdc);
		}
		
		@Override
		public String getLoggerName() {
			return logbackEvent.getLoggerName();
		}

		@Override
		public String getMessage() {
			return logbackEvent.getMessage().toString();
		}

		@Override
		public String getThreadName() {
			return logbackEvent.getThreadName();
		}

		@Override
		public Object[] getArgumentArray() {
			return null;
		}

		@Override
		public long getTimeStamp() {
			return logbackEvent.getTimeStamp();
		}

		@Override
		public ThrowableProxy getThrowable() {
			return Optional.ofNullable(logbackEvent.getThrowableProxy())
					.map(t -> new ThrowableProxyAdapter(t))
					.orElse(null);
		}		
	}
	
	static class ThrowableProxyAdapter implements ThrowableProxy {
		
		private final IThrowableProxy iThrowableProxy;
		
		public ThrowableProxyAdapter(IThrowableProxy iThrowableProxy) {
			Objects.requireNonNull(iThrowableProxy);
			this.iThrowableProxy = iThrowableProxy;
		}
		
		@Override
		public String getMessage() {
			return iThrowableProxy.getMessage();
		}
		
		@Override
		public String getClassName() {
			return iThrowableProxy.getClassName();
		}
		
		@Override
		public ThrowableProxy getCause() {
			return Optional.ofNullable(iThrowableProxy.getCause())
					.map(t -> new ThrowableProxyAdapter(t))
					.orElse(null);
		}

		@Override
		public StackTraceElement[] getStackTraceElementArray() {
			
			return Arrays.stream(iThrowableProxy.getStackTraceElementProxyArray())
						.map(e-> e.getStackTraceElement())
						.toArray(i -> new StackTraceElement[i]);
		}
		
		@Override
		public ThrowableProxy[] getSuppressed() {

			return Arrays.stream(iThrowableProxy.getSuppressed())
					.map(e-> new ThrowableProxyAdapter(e))
					.toArray(i -> new ThrowableProxyAdapter[i]);
		}
	}
	
}
