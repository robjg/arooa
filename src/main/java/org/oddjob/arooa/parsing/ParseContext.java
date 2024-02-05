package org.oddjob.arooa.parsing;

import org.oddjob.arooa.runtime.ConfigurationNode;

/**
 * A context in which an {@link org.oddjob.arooa.ArooaConfiguration} is parsed.
 * <p/>
 * The context parameterised to allow richer contexts to be parsed for specific purposes
 * such as bean creation or design.
 * <p/>
 * param <P> The type of the context.
 *
 * @see ArooaContext
 */
public interface ParseContext<P extends ParseContext<P>> {

    /**
     * Get the parent {@link ArooaContext}
     *
     * @return The parent context. This will be null for the root context.
     */
    P getParent();

    /**
     * Get the {@link ElementHandler} that will be used to process any child
     * elements.
     *
     * @return A handler. Never null.
     */
    ElementHandler<P> getElementHandler();

    /**
     * Get the prefix mappings for this context.
     *
     * @return The prefix mappings.
     */
    PrefixMappings getPrefixMappings();

    /**
     * Get the {@link ConfigurationNode} for this context.
     *
     * @return A RuntimeNode. Never null.
     */
    ConfigurationNode<P> getConfigurationNode();

    /**
     * Destroy this context.
     */
    void destroy();
}
