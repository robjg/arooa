package org.oddjob.arooa.standard;

import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;

import java.util.Objects;

/**
 * A {@link org.oddjob.arooa.runtime.ConfigurationNode} for an instance
 * (component or value)
 */
class InstanceConfigurationNode extends StandardConfigurationNode {

    private final InstanceRuntime instanceRuntime;

    public InstanceConfigurationNode(ArooaElement element,
                                     InstanceRuntime instanceRuntime) {
        super(() -> new ArooaElement(
                element.getUri(),
                element.getTag(),
                instanceRuntime.getInstanceConfiguration().getAttributes()));

        this.instanceRuntime = instanceRuntime;
    }

    public void addText(String text) {
        instanceRuntime.getInstanceConfiguration().addText(text);
    }

    @Override
    public String getText() {
        return instanceRuntime.getInstanceConfiguration().getText();
    }

    public ArooaContext getContext() {
        return instanceRuntime.getContext();
    }
}
