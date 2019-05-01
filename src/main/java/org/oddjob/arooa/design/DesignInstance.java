package org.oddjob.arooa.design;

import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;

/**
 * A Design for an component or value instance in a configuration.
 * 
 * @author rob
 *
 */
public interface DesignInstance {
	
	/**
	 * Required for producing the XML.
	 * 
	 * @return The element.
	 */
	ArooaElement element();
	
	/**
	 * The method is overridden by sub classes which have a detailed
	 * definition for their configuration. For a {@link DesignAttributeProperty} which
	 * hasn't got detail this method should never be called, and as such
	 * it should throw a RuntimeException if it is.
	 * 
	 * @return The DesignDefinition for their configuration. Never null.
	 */
	Form detail();
		
	/**
	 * Get the {@link ArooaContext} associated with this instance.
	 * 
	 * @return The context. Never null.
	 */
	ArooaContext getArooaContext();
	
}
