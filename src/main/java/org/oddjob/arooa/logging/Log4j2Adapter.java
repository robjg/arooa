package org.oddjob.arooa.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    private final LoggerContext loggerContext;

    public Log4j2Adapter(LoggerContext loggerContext) {
        this.loggerContext = loggerContext;
    }

    public static AppenderService forSlf4j() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object loggerFactory = LoggerFactory.getILoggerFactory();
        Method method = loggerFactory.getClass().getMethod("getLoggerContexts");
        Set<?> contexts = (Set<?>) method.invoke(loggerFactory);
        LoggerContext loggerContext = Objects.requireNonNull(
                (LoggerContext) contexts.iterator().next(), "No LoggerContext found");
        return new Log4j2Adapter(loggerContext);
    }

    @Override
    public AppenderAdapter appenderAdapterFor(String loggerName) {

        final Logger logger = Optional.ofNullable(loggerName)
                .map(loggerContext::getLogger)
                .orElse(loggerContext.getLogger(LogManager.ROOT_LOGGER_NAME));

        return new Log4J2AppenderAdapter(logger);
    }

    class Log4J2AppenderAdapter implements AppenderAdapter {

        private final Logger logger;

        Log4J2AppenderAdapter(Logger logger) {
            this.logger = logger;
        }

        @Override
        public AppenderAdapter setLevel(LogLevel level) {
            Level log4j2level = LOG4J_FROM_LEVELS.get(level);

            Configuration config = loggerContext.getConfiguration();
            LoggerConfig loggerConfig = config.getLoggerConfig(logger.getName());
            loggerConfig.setLevel(log4j2level);
            loggerContext.updateLoggers();

            return this;
        }

        @Override
        public AppenderAdapter addAppender(Appender appender, Layout layout) {

            appenders.computeIfAbsent(
                    appender,
                    key -> {
                        Log4j2Appender log4jAppender = new Log4j2Appender(
                                "Log4j2Adaptor:" + appender,
                                null,
                                ((Log4j2Layout) layout).layout,
                                key);
                        log4jAppender.start();
                        logger.addAppender(log4jAppender);
                        return log4jAppender;
                    });

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
        return new Log4j2Layout(layout);
    }

    @Override
    public void configure(String logConfigFileName) {
        loggerContext.stop();
        loggerContext.setConfigLocation(Path.of(logConfigFileName).toUri());
        loggerContext.start();
    }

    static class Log4j2Layout implements Layout {

        private final  org.apache.logging.log4j.core.Layout<? extends Serializable> layout;

        Log4j2Layout(org.apache.logging.log4j.core.Layout<? extends Serializable> layout) {
            this.layout = layout;
        }
    }

    private static class Log4j2Appender extends AbstractAppender {

        private final Appender appender;

        Log4j2Appender(String name,
                       Filter filter,
                       org.apache.logging.log4j.core.Layout<? extends Serializable> layout,
                       Appender appender) {
            super(name, filter, layout, false, new Property[0]);
            this.appender = appender;
        }

        @Override
        public void append(LogEvent log4jEvent) {
            this.appender.append(new AdaptedOddjobLoggingEvent(
                    log4jEvent.getLoggerName(),
            LOG4J_TO_LEVELS.get(log4jEvent.getLevel()),
            log4jEvent.getContextData(),
            getLayout().toSerializable(log4jEvent).toString(),
            Optional.ofNullable(log4jEvent.getThrownProxy())
                    .map(ThrowableProxyAdapter::new)
                    .orElse(null)
            ));
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

        private final String loggerName;
        private final LogLevel logLevel;
        private final ReadOnlyStringMap mdcs;
        private final String message;
        private final ThrowableProxy throwableProxy;

        AdaptedOddjobLoggingEvent(String loggerName,
                                  LogLevel logLevel,
                                  ReadOnlyStringMap mdcs,
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
            return this.mdcs.getValue(mdc);
        }

        @Override
        public String getLoggerName() {
            return this.loggerName;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public ThrowableProxy getThrowable() {
            return this.throwableProxy;
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
