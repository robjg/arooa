package org.oddjob.arooa;

import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.ServiceHelper;
import org.oddjob.arooa.runtime.Evaluator;
import org.oddjob.arooa.runtime.ExpressionParser;

/**
 * Encapsulate the tools an {@link ArooaSession} might require.
 */
public interface ArooaTools {

	/**
	 * Get an {@link ArooaConverter} to use.
	 * 
	 * @return An ArooaConverter. Will not be null.
	 */
	ArooaConverter getArooaConverter();
	
	/**
	 * Get a {@link PropertyAccessor} that does type
	 * conversion during the setting of properties.
	 * 
	 * @return A PropertyAccessor. Will not be null.
	 */
	PropertyAccessor getPropertyAccessor();

	/**
	 * Get an {@link ExpressionParser} to use.
	 * 
	 * @return An ExpressionParser. Will not be null.
	 */
	ExpressionParser getExpressionParser();
		
	/**
	 * Get an {@link Evaluator} to use.
	 * 
	 * @return An Evaluator. Will not be null.
	 */
	Evaluator getEvaluator();
	
	/**
	 * Get a {@link ServiceHelper}.
	 * 
	 * @return A Service Helper. Will not be null.
	 */
	ServiceHelper getServiceHelper();
}
