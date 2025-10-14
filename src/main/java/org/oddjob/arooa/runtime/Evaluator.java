package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;

/**
 * Something that's able to evaluate a property expression. 
 * <p>
 * This isn't a very descriptive name, it might change to something better.
 * 
 * @author rob
 *
 */
public interface Evaluator {

	/**
	 * Evaluate the property expression.
	 * 
	 * @param propertyExpression The property expression such as a.b.c.
	 * @param session The Arooa Session
	 * @param type The Type to evaluate the expression to.
	 * 
	 * @return The evaluation.
	 * 
	 * @throws ArooaPropertyException If any property reference fails other than being null.
	 * @throws ArooaConversionException If conversion to the type fails.
	 */
	<T> T evaluate(String propertyExpression,
				   ArooaSession session,
				   Class<T> type)
	throws ArooaPropertyException, ArooaConversionException;
}
