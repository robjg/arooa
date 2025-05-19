package org.oddjob.arooa.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Adapter for Log4J2.
 *
 * @author rob
 */
public class Log4j2Adapter implements AppenderService {

    private final ConcurrentMap<Appender, Log4j2Appender> appenders =
            new ConcurrentHashMap<>();

    @Override
    public AppenderAdapter appenderAdapterFor(String loggerName) {

        final Logger logger = (Logger) Optional.ofNullable(loggerName)
                .map(LogManager::getLogger)
                .orElse(LogManager.getRootLogger());

        return new Log4J2AppenderAdapter(logger);
    }

    class Log4J2AppenderAdapter implements AppenderAdapter {

        private final Logger logger;

        private Level level;

        Log4J2AppenderAdapter(Logger logger) {
            this.logger = logger;
        }

        @Override
        public AppenderAdapter setLevel(LogLevel level) {
            this.level = LOG4J_FROM_LEVELS.get(level);
            return this;
        }

        @Override
        public AppenderAdapter addAppender(Appender appender) {

            Log4j2Appender log4jAppender = appenders.computeIfAbsent(
                    appender, key -> new Log4j2Appender(
                            logger.getName(),
                            ThresholdFilter.createFilter(Objects.requireNonNullElse(level, Level.INFO), Filter.Result.ACCEPT, Filter.Result.DENY),
                            null,
                            key));
            log4jAppender.start();
            logger.addAppender(log4jAppender);
            return this;
        }

        @Override
        public AppenderAdapter removeAppender(Appender appender) {

            Log4j2Appender log4j2Appender = appenders.remove(appender);
            if (log4j2Appender != null) {
                log4j2Appender.stop();
                logger.removeAppender(log4j2Appender);
            }
            return this;
        }
    }

    @Override
    public Layout layoutFor(String pattern) {
        org.apache.logging.log4j.core.Layout<? extends Serializable> layout =
                PatternLayout.newBuilder().withPattern(pattern).build();
        return event -> layout.toSerializable(((AdaptedOddjobLoggingEvent) event).log4jEvent).toString();
    }

    @Override
    public void configure(String logConfigFileName) {
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext();
        loggerContext.stop();
        loggerContext.setConfigLocation(Path.of(logConfigFileName).toUri());
        loggerContext.start();
    }

    private static class Log4j2Appender extends AbstractAppender {

        private final Appender appender;

        Log4j2Appender(String name,
                       Filter filter,
                       org.apache.logging.log4j.core.Layout<? extends Serializable> layout,
                       Appender appender) {
            super(name, filter, layout);
            this.appender = appender;
        }

        @Override
        public void append(LogEvent event) {
            this.appender.append(new AdaptedOddjobLoggingEvent(event));
        }
    }

    private static final Map<org.apache.logging.log4j.Level, LogLevel> LOG4J_TO_LEVELS
            = new HashMap<>();

    private static final Map<LogLevel, org.apache.logging.log4j.Level> LOG4J_FROM_LEVELS
            = new HashMap<>();

    static {
        LOG4J_TO_LEVELS.put(org.apache.logging.log4j.Level.ALL, LogLevel.INFO);
        LOG4J_TO_LEVELS.put(org.apache.logging.log4j.Level.TRACE, LogLevel.TRACE);
        LOG4J_TO_LEVELS.put(org.apache.logging.log4j.Level.DEBUG, LogLevel.DEBUG);
        LOG4J_TO_LEVELS.put(org.apache.logging.log4j.Level.INFO, LogLevel.INFO);
        LOG4J_TO_LEVELS.put(org.apache.logging.log4j.Level.WARN, LogLevel.WARN);
        LOG4J_TO_LEVELS.put(org.apache.logging.log4j.Level.ERROR, LogLevel.ERROR);
        LOG4J_TO_LEVELS.put(org.apache.logging.log4j.Level.FATAL, LogLevel.FATAL);
        LOG4J_TO_LEVELS.put(org.apache.logging.log4j.Level.OFF, LogLevel.FATAL);

        LOG4J_FROM_LEVELS.put(LogLevel.TRACE, org.apache.logging.log4j.Level.TRACE);
        LOG4J_FROM_LEVELS.put(LogLevel.DEBUG, org.apache.logging.log4j.Level.DEBUG);
        LOG4J_FROM_LEVELS.put(LogLevel.INFO, org.apache.logging.log4j.Level.INFO);
        LOG4J_FROM_LEVELS.put(LogLevel.WARN, org.apache.logging.log4j.Level.WARN);
        LOG4J_FROM_LEVELS.put(LogLevel.ERROR, org.apache.logging.log4j.Level.ERROR);
        LOG4J_FROM_LEVELS.put(LogLevel.FATAL, org.apache.logging.log4j.Level.FATAL);
    }

    static class AdaptedOddjobLoggingEvent implements LoggingEvent {

        private final LogEvent log4jEvent;

        AdaptedOddjobLoggingEvent(LogEvent log4jEvent) {
            this.log4jEvent = log4jEvent;
        }

        @Override
        public LogLevel getLevel() {
            return LOG4J_TO_LEVELS.get(log4jEvent.getLevel());
        }

        @Override
        public String getMdc(String mdc) {
            return log4jEvent.getContextMap().get(mdc);
        }

        @Override
        public String getLoggerName() {
            return log4jEvent.getLoggerName();
        }

        @Override
        public String getMessage() {
            return log4jEvent.getMessage().getFormattedMessage();
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
            return log4jEvent.getTimeMillis();
        }

        @Override
        public ThrowableProxy getThrowable() {
            return Optional.ofNullable(log4jEvent.getThrownProxy())
                    .map(ThrowableProxyAdapter::new)
                    .orElse(null);
        }
    }

    static class ThrowableProxyAdapter implements ThrowableProxy {

        private final org.apache.logging.log4j.core.impl.ThrowableProxy throwableProxy;

        public ThrowableProxyAdapter(org.apache.logging.log4j.core.impl.ThrowableProxy iThrowableProxy) {
            Objects.requireNonNull(iThrowableProxy);
            this.throwableProxy = iThrowableProxy;
        }

        @Override
        public String getMessage() {
            return throwableProxy.getMessage();
        }

        @Override
        public String getClassName() {
            return throwableProxy.getClass().getName();
        }

        @Override
        public ThrowableProxy getCause() {
            return Optional.ofNullable(throwableProxy.getCauseProxy())
                    .map(ThrowableProxyAdapter::new)
                    .orElse(null);
        }

        @Override
        public StackTraceElement[] getStackTraceElementArray() {

            return throwableProxy.getStackTrace();
        }

        @Override
        public ThrowableProxy[] getSuppressed() {

            return Arrays.stream(throwableProxy.getSuppressedProxies())
                    .map(ThrowableProxyAdapter::new)
                    .toArray(ThrowableProxyAdapter[]::new);
        }
    }
}
