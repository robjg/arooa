package org.oddjob.arooa.logging;

/**
 * Provides an abstraction for different logging frameworks to
 * provide an appender which is used to capture a jobs log
 * messages when it uses SLF4J.
 */
public interface AppenderService {

    AppenderAdapter appenderAdapterFor(String loggerName);

    default AppenderAdapter appenderAdapterForRoot() {
        return appenderAdapterFor((String) null);
    }

    default AppenderAdapter appenderAdapterFor(Class<?> cl) {
        return appenderAdapterFor(cl.getName());
    }

    Layout layoutFor(String pattern);

    void configure(String logConfigFileName);
}
