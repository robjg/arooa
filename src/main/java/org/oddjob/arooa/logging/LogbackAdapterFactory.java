package org.oddjob.arooa.logging;

/**
 * For the Service Loader to load an {@link AppenderService} for Logback.
 */
public class LogbackAdapterFactory implements AppenderServiceFactory {

    @Override
    public AppenderService appenderServiceFor(String slf4jImplClassName) {
        return "ch.qos.logback.classic.LoggerContext".equals(slf4jImplClassName) ?
                new LogbackAdapter() : null;
    }
}
