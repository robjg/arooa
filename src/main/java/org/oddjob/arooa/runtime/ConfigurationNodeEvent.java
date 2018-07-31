package org.oddjob.arooa.runtime;

import java.util.EventObject;

/**
 * An Event for changes in the structure of 
 * a {@link ConfigurationNode}.
 * 
 * @author rob
 *
 */
public class ConfigurationNodeEvent extends EventObject {
	private static final long serialVersionUID = 20080205;

	/** The child node changed. */
	private final ConfigurationNode child;
	
	/** The position of the change. */
	private final int index;

	/**
	 * Constructor.
	 * 
	 * @param source
	 * @param index
	 * @param child
	 */
	public ConfigurationNodeEvent(ConfigurationNode source,
			int index,
			ConfigurationNode child) {
		super(source);
		this.index = index;
		this.child = child;
	}
	
	/**
	 * Get the index of the change.
	 * 
	 * @return
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Get the child that changed.
	 * 
	 * @return
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
