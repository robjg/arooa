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
	public ArooaElement element();
	
	/**
	 * The method is overridden by sub classes which have a detailed
	 * definition for their configuration. For DesignElement which 
	 * hasn't got detail this method should never be called, and as such
	 * it should throw a RuntimeException if it is.
	 * 
	 * @return The DesignDefinition for their configuration. Never null.
	 */
	public Form detail();
		
	/**
	 * Get the {@link ArooaContext} associated with this instance.
	 * 
	 * @return The context. Never null.
	 */
	public ArooaContext getArooaContext();
	
	/**
	 * Get the id of the instance, if there is one.
	 * 
	 * @return The id. May be null.
	 */
	public String getId();
	
	/**
	 * Set the id of the instance.
	 * 
	 * @param id The id. May be null.
	 */
	public void setId(String id);
}
