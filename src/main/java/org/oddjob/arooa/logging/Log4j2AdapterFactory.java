package org.oddjob.arooa.logging;

import java.lang.reflect.InvocationTargetException;

/**
 * For the Service Loader to load an {@link AppenderService} for Log4J 2.
 */
public class Log4j2AdapterFactory implements AppenderServiceFactory {

    @Override
    public AppenderService appenderServiceFor(String slf4jImplClassName) {
        try {
            return "org.apache.logging.slf4j.Log4jLoggerFactory".equals(slf4jImplClassName) ?
                    Log4j2Adapter.forSlf4j() : null;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Failed creating AppenderService for Log4j2", e);
        }
    }

}
