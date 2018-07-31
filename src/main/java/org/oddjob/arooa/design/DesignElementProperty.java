package org.oddjob.arooa.design;

import org.oddjob.arooa.parsing.ArooaContext;

/**
 * The design for a property that is configured as an element.
 * 
 * @author rob
 *
 */
public interface DesignElementProperty extends DesignProperty {

	/**
	 * Get the {@link ArooaContext} associated with the element.
	 * 
	 * @return The ArooaContext. Never Null.
	 */
	public ArooaContext getArooaContext();
	
	/**
	 * Add a {@link DesignListener}. The listener will be notified when
	 * instances of either components or types are added to this property.
	 * 
	 * @param listener The listener. Must not be null.
	 */
	public void addDesignListener(DesignListener listener);
	
	/**
	 * Remove a {@link DesignListener}. 
	 * 
	 * @param listener The listener. Must not be null.
	 */
	public void removeDesignListener(DesignListener listener);	
}
