package org.oddjob.arooa.logging;

/**
 * For the Service Loader to load an {@link AppenderService} for Log4J.
 */
public class Log4jAdapterFactory implements AppenderServiceFactory {

    @Override
    public AppenderService appenderServiceFor(String slf4jImplClassName) {
        return "org.slf4j.impl.Log4jLoggerFactory".equals(slf4jImplClassName) ?
                new Log4jAdapter() : null;
    }

}
