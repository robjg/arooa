package org.oddjob.arooa.design;

import org.oddjob.arooa.parsing.ArooaContext;

/**
 * The design for a property that is configured as an element.
 * 
 * @author rob
 *
 */
public interface DesignElementProperty extends DesignProperty, DesignNotifier {

	/**
	 * Get the {@link ArooaContext} associated with the element.
	 * 
	 * @return The ArooaContext. Never Null.
	 */
	ArooaContext getArooaContext();
}
