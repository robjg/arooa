package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.registry.ChangeHow;
import org.oddjob.arooa.registry.ComponentPool;

/**
 * A point in a configuration that supports drag and drop/cut and paste.
 * 
 * @author rob
 *
 */
public interface DragPoint extends ArooaConfiguration {

	/**
	 * Begin a change. Changes must happen in a transaction because when
	 * dragging a component within the same session the cut must be 
	 * done before the paste otherwise duplicate IDs would occur.
	 * 
	 * @param how Should an existing transaction be in progress or not.
	 * @return A transaction. Never null.
	 */
	public DragTransaction beginChange(ChangeHow how);

	/**
	 * True if this DragPoint supports the cut operation.
	 * 
	 * @return true/false.
	 */
	public boolean supportsCut();
	
	/**
	 * True if this DragPoint supports the paste operation.
	 * 
	 * @return true/false.
	 */
	public boolean supportsPaste();
	
	/**
	 * Provide a copy of the configuration at from this DragPoint
	 * as XML.
	 * 
	 * @return Text XML.
	 */
	public String copy();
	
	/**
	 * Remove this DragPoint from it's underlying configuration and
	 * remove any components in the configuration from the 
	 * {@link ComponentPool}
	 * <p>
	 * This operation must be done within the context of a transaction.
	 */
	public void cut();
		
	/**
	 * Parse an XML Text configuration and add the resultant component
	 * to this DragPoint with the given index.
	 * <p>
	 * This operation must be done within the context of a transaction.
	 * 
	 * @param index The index. -1 will append.
	 * @param config The configuration.
	 * 
	 * @throws ArooaParseException If the configuration could not be
	 * parsed.
	 */
	public void paste(int index, String config) 
	throws ArooaParseException;
	
}
