package org.oddjob.arooa.design;

import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaPropertyException;

/**
 * Creates a {@link DesignInstance}.
 * 
 * @author rob
 *
 */
public interface DesignFactory {

	/**
	 * Create a {@link DesignInstance}.
	 * 
	 * @param element The ArooaElement corresponding to the design.
	 * @param parentContext The parent ArooaContext in which to 
	 * create the design.
	 * 
	 * @return The design.
	 */
	public DesignInstance createDesign(
			ArooaElement element, 
			ArooaContext parentContext)
	throws ArooaPropertyException;	
}
