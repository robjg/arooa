package org.oddjob.arooa.design;

import org.oddjob.arooa.design.screem.FormItem;

/**
 * The design for the configuration of a property.
 * 
 * @author rob
 *
 */
public interface DesignProperty {

	/**
	 * The name of the property.
	 * 
	 * @return The name of the property. Never null.
	 */
	public String property();
	
	/**
	 * Provide the {@link FormItem} model that represents the view for the 
	 * design of this property.
	 * 
	 * @return The FormItem. Never null.
	 */
	public FormItem view();
	

	/**
	 * Used by the Field Selection View to work out which group to
	 * check box.
	 * 
	 * @return
	 */
	public boolean isPopulated();	
	
}
