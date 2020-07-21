package org.oddjob.arooa.parsing;

import org.oddjob.arooa.runtime.ConfigurationNode;

import java.util.Objects;

/**
 * Parse Handle for an {@link ArooaContext}.
 */
public class ArooaParseHandle implements ParseHandle<ArooaContext> {

    private final ArooaContext context;

    public ArooaParseHandle(ArooaContext context) {
        Objects.requireNonNull(this.context = context);
    }

    @Override
    public ArooaContext getContext() {
        return this.context;
    }

    @Override
    public void addText(String text) {

        ConfigurationNode<ArooaContext> currentConfigurationNode =
                Objects.requireNonNull(context.getConfigurationNode(),
                        "Null Configuration Node for Context " + context);

        currentConfigurationNode.addText(text);
    }

    @Override
    public int init() {
        ArooaContext parentContext = Objects.requireNonNull(context.getParent(),
                "No parent Context for " + context);

        ConfigurationNode<ArooaContext> currentConfigurationNode =
                Objects.requireNonNull(context.getConfigurationNode(),
                        "Null Configuration Node for Context " + context);

        ConfigurationNode<ArooaContext> parentConfigurationNode =
                Objects.requireNonNull(parentContext.getConfigurationNode(),
                        "Null Configuration Node for Parent Context " + parentContext);

        // order is important here:
        // add node before init() so indexed properties
        // know their index.

        int index = parentConfigurationNode
                .insertChild(currentConfigurationNode);

        try {
            this.context.getRuntime().init();
        } catch (RuntimeException e) {
            parentContext.getConfigurationNode().removeChild(index);
            throw e;
        }

        return index;
    }

}
