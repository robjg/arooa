package org.oddjob.arooa.runtime;

import java.util.EventObject;
import java.util.Objects;

/**
 * An Event for changes in the structure of a {@link ConfigurationNode}.
 *
 * @author rob
 */
public class ConfigurationNodeEvent extends EventObject {
    private static final long serialVersionUID = 20080205;

    /**
     * The child node changed.
     */
    private final ConfigurationNode child;

    /**
     * The position of the change.
     */
    private final int index;

    /**
     * Constructor.
     *
     * @param source The source of the event. Never null.
     * @param index  The index of the child being inserted/removed.
     * @param child  The child being inserted or removed. Never null.
     */
    public ConfigurationNodeEvent(ConfigurationNode source,
                                  int index,
                                  ConfigurationNode child) {
        super(source);
        Objects.requireNonNull(child);

        this.index = index;
        this.child = child;
    }

    /**
     * Get the index of the change.
     *
     * @return The index of the change.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get the child that changed.
     *
     * @return The child. Never null.
     */
    public ConfigurationNode getChild() {
        return child;
    }

    /**
     * Get the source of the change.
     *
     * @return The ConfigurationChange that is the source of the change.
     */
    public ConfigurationNode getSource() {
        return (ConfigurationNode) super.getSource();
    }
}
