package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaParseException;

/**
 * Allows changes to registered components to happen
 * in an atomic fashion.
 * <p>
 * This functionality is required to support drag and drop
 * with the same registry session. A component is dropped before
 * it is deleted. This would cause an exception. The transaction
 * must ensure this isn't the case.
 * 
 * @author rob
 *
 */
public interface DragTransaction {

	/**
	 * Save changes to the configuration.
	 */
	void commit() throws ArooaParseException;
	
	/**
	 * Abandons changes to the configuration.
	 */
	void rollback();
}
