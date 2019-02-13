package org.oddjob.arooa.handlers;

import org.oddjob.arooa.life.ArooaElementException;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;

/**
 * Performs an action with an element. Typically this will
 * be creating some kind of parsed value for the element.
 * 
 * @author rob
 *
 * @param <R> The type of thing created for the element.
 */
public interface ElementAction<R> {
	
	/**
	 * Perform an action on the given element.
	 * 
	 * @param element The element.
	 * @param context The context.
	 * 
	 * @return The thing created as a result.
	 * 
	 * @throws ArooaElementException If something went wrong.
	 */
	R onElement(ArooaElement element, ArooaContext context)
	throws ArooaElementException;
}