package org.oddjob.arooa.design.actions;

import javax.swing.JComponent;

/**
 * Something that is able to contribute actions to a menu system.
 * 
 * @author rob
 *
 */
public interface ActionContributor {

	/**
	 * Contribute actions using the given registry.
	 * 
	 * @param actionRegistry
	 */
	public void contributeTo(ActionRegistry actionRegistry);
	
	/**
	 * Add the accelerator keys to the given component if applicable.
	 * 
	 * @param component
	 */
	public void addKeyStrokes(JComponent component);

}
