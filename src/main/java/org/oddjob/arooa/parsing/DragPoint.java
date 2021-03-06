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
	DragTransaction beginChange(ChangeHow how);

	/**
	 * True if this DragPoint supports the cut operation.
	 * 
	 * @return true/false.
	 */
	boolean supportsCut();
	
	/**
	 * True if this DragPoint supports the paste operation.
	 * 
	 * @return true/false.
	 */
	boolean supportsPaste();

	/**
	 * Copy this {@code DragPoint}s configuration and remove it.
	 * <p>
	 *     This is used by the Web Front end. The Swing UI uses {@link #delete()}.
	 * </p>
	 */
	String cut();

	/**
	 * Provide a copy of the configuration at from this DragPoint
	 * as XML.
	 * 
	 * @return Text XML.
	 */
	String copy();
	
	/**
	 * Remove this DragPoint from it's underlying configuration and
	 * remove any components in the configuration from the 
	 * {@link ComponentPool}
	 * <p>
	 *     This is used by the swing GUI {@link org.oddjob.arooa.design.designer.ArooaTransferHandler}
	 *     to perform the CUT operation.
	 * </p>
	 * <p>
	 *     This operation must be done within the context of a transaction.
	 * </p>
	 */
	void delete();
		
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
	void paste(int index, String config)
	throws ArooaParseException;

	/**
	 * List the possible children a Drag Point can have for Add Job
	 * functionality. {@link #supportsPaste()} must be true for this
	 * to work.
	 *
	 * @return All that child tags that could be added with a paste
	 * operation.
	 */
	QTag[] possibleChildren();
}
