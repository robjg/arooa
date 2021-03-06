/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa;

import org.oddjob.arooa.parsing.ParseContext;

/**
 * An ArooaConfiguration is something that accepts
 * a {@link ParseContext} and uses that context to provide
 * a {@link ConfigurationHandle}.
 * <p>
 * The result is typically generated by iterating over or parsing
 * whatever the configuration encapsulates using as its
 * starting point the {@link org.oddjob.arooa.parsing.ElementHandler}
 * provided by the parent context.
 * <p>
 * An ArooaConfiguration is intended to be used in conjunction with
 * an {@link ArooaParser} which provides the context.
 *
 * @author rob
 */
public interface ArooaConfiguration {

    /**
     * Parse the encapsulated configuration.
     *
     * @param parseParentContext The context in which this configuration will be
     *                     parsed. This context will provide the
     *                     {@link org.oddjob.arooa.parsing.ArooaHandler}, and therefore
     *                     subsequent child contexts.
     * @param <P> The type of the context in which parsing occurs.
     * @return A {@link ConfigurationHandle}. The handle provides access to the context created
     * as the result of the parse and a way of saving this context back into the configuration.
     * @throws ArooaParseException If parsing fails.
     */
    <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parseParentContext)
            throws ArooaParseException;
}
