package org.oddjob.arooa.design.view.multitype;

import java.awt.Component;

import org.oddjob.arooa.design.view.SwingItemView;

/**
 * Represent the value of a row.
 * 
 * @author rob
 *
 */
public interface EditableValue {

	/**
	 * The component editor for the value. This will be the cell
	 * of a {@link SwingItemView}.
	 * 
	 * @return The editor component.
	 */
	public Component getEditor();
	
	/**
	 * For future expansion.
	 */
	public void abort();
	
	/**
	 * For future expansion.
	 */
	public void commit();
}
