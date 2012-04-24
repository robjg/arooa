package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;

/**
 * Something that's able to evaluate a property expression.
 * 
 * @author rob
 *
 */
public interface Evaluator {

	/**
	 * Evaluate the property expression.
	 * 
	 * @param propertyExpression The property expression such as a.b.c.
	 * @param session
	 * @param type
	 * 
	 * @return The evaluation.
	 * 
	 * @throws ArooaPropertyException
	 * @throws ArooaConversionException
	 */
	public <T> T evaluate(String propertyExpression, 
			ArooaSession session, Class<T> type) 
	throws ArooaPropertyException, ArooaConversionException;
}
