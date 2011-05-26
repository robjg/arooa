package org.oddjob.arooa.design.view;

import java.awt.Component;

public interface SwingFormView {

	
	/**
	 * The component should be rendered for use in a table cell.
	 * 
	 * @return The component.
	 */
	public Component cell();
	
	
	/**
	 * The component should be rendered for use in a model dialog. With a border
	 * and sized correctly.
	 * 
	 * @return The component.
	 */
	public Component dialog();
	
}
