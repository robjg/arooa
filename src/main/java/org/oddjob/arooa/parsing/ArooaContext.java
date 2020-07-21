package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

/**
 * Provides a 'context' for parsing an element. An element is processed/parsed/
 * ({@link ArooaHandler}ed) within an ArooaContext which is that elements
 * parent context. This creates a {@link RuntimeConfiguration} for the element.
 * <p>
 * Once the RuntimeConfiguration has been created, a new context can be created
 * which is the context of the element just processed.
 * <p>
 * The RuntimeConfiguration is then given the opportunity of providing an
 * alternative context via the inherited
 * {@link ParsingInterceptor#intercept(ArooaContext)} method.
 * <p>
 *
 * @author rob
 */
public interface ArooaContext extends ParseContext<ArooaContext> {

    /**
     * Get the type of bean or property this is a context for (a component
     * or a value)
     *
     * @return The type. Never null.
     */
    ArooaType getArooaType();

    /**
     * Get the parent {@link ArooaContext}
     *
     * @return The parent context. This will be null for the root context.
     */
    ArooaContext getParent();

    /**
     * Get the current {@link RuntimeConfiguration} for this context.
     *
     * @return A RuntimeConfiguration. This may be null for the root context.
     */
    RuntimeConfiguration getRuntime();

    /**
     * Get the {@link ConfigurationNode} for this context.
     *
     * @return A RuntimeNode. Never null.
     */
    ConfigurationNode<ArooaContext> getConfigurationNode();

    /**
     * Get the {@link ArooaHandler} that will be used to process any child
     * elements.
     *
     * @return An ArooaHandler. Never null.
     */
    ArooaHandler getArooaHandler();

    // Defaulted until all ArooaHandlers are removed.
    @Override
    default ElementHandler<ArooaContext> getElementHandler() {
        ArooaHandler handler = getArooaHandler();

        return ((element, parentContext) -> {
            ArooaContext childContext = handler.onStartElement(element, parentContext);
            return new ArooaParseHandle(childContext);
        });
    }

    /**
     * Get the prefix mappings for this context.
     *
     * @return The prefix mappings.
     */
    PrefixMappings getPrefixMappings();

    /**
     * Get the session for this context.
     *
     * @return The session. Never null.
     */
    ArooaSession getSession();

	/**
	 * Destroy this context.
	 */
	@Override
    default void destroy() {
        ArooaContext parentContext = this.getParent();

        int index = parentContext.getConfigurationNode().indexOf(
                this.getConfigurationNode());

        if (index < 0) {
            throw new IllegalStateException(
                    "Attempting to cut a configuration node that is not a child of it's parent.");
        }

        this.getRuntime().destroy();

        parentContext.getConfigurationNode().removeChild(
                index);
    }
}