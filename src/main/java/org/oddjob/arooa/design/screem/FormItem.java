/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.screem;

import org.oddjob.arooa.design.DesignProperty;

/**
 * Defines the interface for a model that allows a visual configuration 
 * to be presented for DesignElements.
 * 
 * @see {@link DesignProperty}
 * 
 */
public interface FormItem {

	/**
	 * Set the title that will be displayed on the view for this form
	 * item. This should really be set on the {@link DesignProperty} that 
	 * creates this model.
	 * 
	 * @param title The String title.
	 * 
	 * @return this, for method chaining.
	 */
	public FormItem setTitle(String title);
	
	/**
	 * The title will either be the title for a field or the title round a
	 * group or the title on a dialog depending on how the visual component
	 * is presented.
	 * 
	 * @return The title.
	 */
	public String getTitle();
	
	/**
	 * Used by certain summary views to indicate that there is more
	 * detail.
	 * 
	 * @return true if the model has some data, false if it doesn't.
	 */
	public boolean isPopulated();
}
