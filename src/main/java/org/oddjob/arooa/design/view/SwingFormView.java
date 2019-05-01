package org.oddjob.arooa.design.view;

import java.awt.Component;

import org.oddjob.arooa.design.screem.Form;

/**
 * The Swing manifestation for a {@link Form}.
 * 
 * @see SwingFormFactory
 * 
 * @author rob
 *
 */
public interface SwingFormView {

	
	/**
	 * The component should be rendered for use in a table cell.
	 * 
	 * @return The component.
	 */
	Component cell();
	
	
	/**
	 * The component should be rendered for use in a model dialog. With a border
	 * and sized correctly.
	 * 
	 * @return The component.
	 */
	Component dialog();
	
}
