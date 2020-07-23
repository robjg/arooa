package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

import java.util.Optional;

/**
 * A Context for the parsing. This context is kind of like a
 * seed context from which everything grows. It has no
 * RuntimeConfiguration or RuntimeNode as these require the
 * document node of the configuration to be parsed.
 *
 * @author rob
 */
public class RootContext implements ArooaContext {

    private final ArooaSession session;
    private final ArooaHandler rootHandler;

    private final ArooaType type;

    private final PrefixMappings prefixMappings;

    /**
     * Configuration node is needed to track children.
     */
    private final ConfigurationNode<ArooaContext> configurationNode =
            new AbstractConfigurationNode<ArooaContext>() {

                @Override
                public ArooaContext getContext() {
                    return RootContext.this;
                }

                @Override
                public void addText(String text) {
                    throw new UnsupportedOperationException("Should be Impossible!");
                }

                @Override
                public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext) {
                    throw new UnsupportedOperationException("Should be Impossible!");
                }
            };

    /**
     * Constructor.
     *
     * @param type        The type. May be null depending on parser
     *                    (XMLArooaParser for instance).
     * @param session     The session. Must be null depending on parser.
     * @param rootHandler The handler. Must not be null.
     */
    public RootContext(
            ArooaType type,
            ArooaSession session,
            ArooaHandler rootHandler) {

        if (rootHandler == null) {
            throw new NullPointerException("No Handler");
        }

        this.type = type;
        this.rootHandler = rootHandler;
        this.session = session;
        this.prefixMappings = new FallbackPrefixMappings(
                Optional.ofNullable(session)
                        .map(ArooaSession::getArooaDescriptor)
                        .orElse(null));
    }

    public RootContext(
            ArooaType type,
            PrefixMappings prefixMappings,
            ArooaHandler rootHandler) {

        if (rootHandler == null) {
            throw new NullPointerException("No Handler");
        }

        this.type = type;
        this.rootHandler = rootHandler;
        this.session = null;
        this.prefixMappings = prefixMappings;
    }

    public ArooaType getArooaType() {
        return type;
    }

    public ArooaContext getParent() {
        return null;
    }

    public RuntimeConfiguration getRuntime() {
        return null;
    }

    public PrefixMappings getPrefixMappings() {
        return prefixMappings;
    }

    public ArooaSession getSession() {
        return session;
    }

    public ConfigurationNode<ArooaContext> getConfigurationNode() {
        return configurationNode;
    }

    public ArooaHandler getArooaHandler() {
        return rootHandler;
    }

    @Override
    public String toString() {
        return "RootContext{" +
                "type=" + type +
                ", rootHandler=" + rootHandler +
                '}';
    }
}
