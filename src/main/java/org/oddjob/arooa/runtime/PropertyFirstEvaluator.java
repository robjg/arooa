package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.NullConversions;
import org.oddjob.arooa.reflect.ArooaPropertyException;

/**
 * Evaluates the property expression first as an a.b.c type property
 * then as a property of an object.
 * 
 * @author rob
 *
 */
public class PropertyFirstEvaluator implements Evaluator {

	public <T> T evaluate(String propertyExpression, 
			ArooaSession session, Class<T> cl) 
	throws ArooaPropertyException, ArooaConversionException {
		
		if (propertyExpression == null) {
			return NullConversions.nullConversionFor(cl);
		}
		
		if (propertyExpression.length() == 0) {
			return NullConversions.nullConversionFor(cl);
		}
		
		String value = session.getPropertyManager().lookup(propertyExpression);
		
		if (value == null) {
			return session.getBeanRegistry().lookup(
            		propertyExpression, cl);
		}
		else {
			ArooaConverter converter = session.getTools().getArooaConverter();
			return converter.convert(value, cl);
		}		
	}
}
