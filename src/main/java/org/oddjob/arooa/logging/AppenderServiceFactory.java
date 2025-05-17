package org.oddjob.arooa.logging;

/**
 * Used by the Java Service Locator to provide an {@link AppenderService}.
 */
public interface AppenderServiceFactory {

    /**
     * Returns an Appender Service if the class name is compatible.
     * @param slf4jImplClassName The SL4J implementation class name.
     * @return An Appender Service or null if this factory is not for the implementation.
     */
    AppenderService appenderServiceFor(String slf4jImplClassName);
}
