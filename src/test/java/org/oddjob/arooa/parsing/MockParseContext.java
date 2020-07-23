package org.oddjob.arooa.parsing;

import org.oddjob.arooa.runtime.ConfigurationNode;

public class MockParseContext<P extends ParseContext<P>> implements ParseContext<P> {

    @Override
    public P getParent() {
        throw new RuntimeException("Unexpected from class " +
                getClass().getName());
    }

    @Override
    public ElementHandler<P> getElementHandler() {
        throw new RuntimeException("Unexpected from class " +
                getClass().getName());
    }

    @Override
    public PrefixMappings getPrefixMappings() {
        throw new RuntimeException("Unexpected from class " +
                getClass().getName());
    }

    @Override
    public ConfigurationNode<P> getConfigurationNode() {
        throw new RuntimeException("Unexpected from class " +
                getClass().getName());
    }

    @Override
    public void destroy() {
        throw new RuntimeException("Unexpected from class " +
                getClass().getName());
    }
}
