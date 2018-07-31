/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.view;

import java.awt.Container;

/**
 * Define something cabable of producing a a GUI Component in various situations.
 * 
 */
public interface SwingItemView {

	
	/**
	 * The class should add
	 * it's component to the container which has a GridBagLayout.
	 * 
	 * @param container The container to add the component to.
	 * @param row The row to add the component at.
	 * @param The column to add the component at.
	 * 
	 * @return The row the next component should be added at.
	 */
	public int inline(Container container, 
			int row, 
			int column,
			boolean selectionInGroup);
	
	/**
	 * Used when a component is part of a selection.
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled);	
}
