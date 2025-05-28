package org.oddjob.arooa.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;


/**
 * Adapter for Logback.
 * 
 * @author rob
 *
 */
public class LogbackAdapter implements AppenderService {


	private final ConcurrentMap<Appender, LogbackAppender> appenders = 
			new ConcurrentHashMap<>();
	
	private final LoggerContext context;

	public LogbackAdapter() {
		this.context = (LoggerContext) LoggerFactory.getILoggerFactory();
	}

	public LogbackAdapter(LoggerContext context) {
		this.context = context;
	}

	@Override
	public AppenderAdapter appenderAdapterFor(String loggerName) {

		Logger logger = Optional.ofNullable(loggerName)
				.map(context::getLogger)
				.orElse(context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME));
		
		return new AppenderAdapter() {
						
			@Override
			public AppenderAdapter setLevel(LogLevel level) {
				logger.setLevel(LOG4J_FROM_LEVELS.get(level));
				return this;
			}
			
			@Override
			public AppenderAdapter addAppender(Appender appender, Layout layout) {

				// Might be a bug in Logback - when it's not adapting something
				// get MDCs throws an NPE.
				Function<ILoggingEvent, Map<String, String>> mdcExtractor =
						context.getMDCAdapter() == null ?
								e -> Collections.emptyMap() :
								e -> e.getMDCPropertyMap();

				appenders.computeIfAbsent(
						appender,
						key -> {
							LogbackAppender logbackAppender = new LogbackAppender(
									key,
									((LogbackLayout) layout).layout,
									mdcExtractor);
							logbackAppender.setName(this.toString());
							logbackAppender.setContext(context);
							logbackAppender.start();
							logger.addAppender(logbackAppender);
							return logbackAppender;
						});
				return this;
			}

			@Override
			public AppenderAdapter removeAppender(Appender appender) {
				
				Optional.ofNullable(appenders.remove(appender))
						.ifPresent(a -> { 
							logger.detachAppender(a); 
							a.stop();
                        });
				return this;
			}
			
		};
	}
	
	@Override
	public Layout layoutFor(String pattern) {

		PatternLayout layout = new PatternLayout();
		layout.setPattern(pattern);
		layout.setContext(context);
		layout.start();
		
		return new LogbackLayout(layout);
	}

	static class LogbackLayout implements Layout{

		private final PatternLayout layout;

        LogbackLayout(PatternLayout layout) {
            this.layout = layout;
        }
    }

	@Override
	public void configure(String logConfigFileName) {
		
		
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

		private final PatternLayout patternLayout;

		private final Function<ILoggingEvent, Map<String, String>> mdcExtractor;

		LogbackAppender(Appender appender,
						PatternLayout patternLayout,
						Function<ILoggingEvent, Map<String, String>> mdcExtractor) {
			this.appender = appender;
			this.patternLayout = patternLayout;
			this.mdcExtractor = mdcExtractor;
		}

		@Override
		protected void append(ILoggingEvent logbackEvent) {
			LoggingEvent oddjobEvent = new AdaptedOddjobLoggingEvent(
					logbackEvent.getLoggerName(),
					LOG4J_TO_LEVELS.get(logbackEvent.getLevel()),
					mdcExtractor.apply(logbackEvent),
					patternLayout.doLayout(logbackEvent),
					Optional.ofNullable(logbackEvent.getThrowableProxy())
							.map(ThrowableProxyAdapter::new)
							.orElse(null)
			);

			this.appender.append(oddjobEvent);
		}
		
	}
	
	
	private static final Map<ch.qos.logback.classic.Level, LogLevel> LOG4J_TO_LEVELS
		= new HashMap<>();

	private static final Map<LogLevel, ch.qos.logback.classic.Level> LOG4J_FROM_LEVELS
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

		private final String loggerName;
		private final LogLevel logLevel;
		private final Map<String, String> mdcs;
		private final String message;
		private final ThrowableProxy throwableProxy;

		AdaptedOddjobLoggingEvent(String loggerName,
								  LogLevel logLevel,
								  Map<String, String> mdcs,
								  String message,
								  ThrowableProxy throwableProxy) {
			this.loggerName = loggerName;
			this.logLevel = logLevel;
			this.mdcs = mdcs;
			this.message = message;
			this.throwableProxy = throwableProxy;
		}
		
		@Override
		public LogLevel getLevel() {
			return logLevel;
		}

		@Override
		public String getMdc(String mdc) {
			return mdcs.get(mdc);
		}
		
		@Override
		public String getLoggerName() {
			return loggerName;
		}

		@Override
		public String getMessage() {
			return message;
		}

		@Override
		public ThrowableProxy getThrowable() {
			return throwableProxy;
		}

		@Override
		public String toString() {
			return "AdaptedOddjobLoggingEvent{" +
					"loggerName=" + loggerName +
					", message=" + message +
					'}';
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
					.map(ThrowableProxyAdapter::new)
					.orElse(null);
		}

		@Override
		public StackTraceElement[] getStackTraceElementArray() {
			
			return Arrays.stream(iThrowableProxy.getStackTraceElementProxyArray())
						.map(StackTraceElementProxy::getStackTraceElement)
						.toArray(StackTraceElement[]::new);
		}
		
		@Override
		public ThrowableProxy[] getSuppressed() {

			return Arrays.stream(iThrowableProxy.getSuppressed())
					.map(ThrowableProxyAdapter::new)
					.toArray(ThrowableProxyAdapter[]::new);
		}
	}
	
}
