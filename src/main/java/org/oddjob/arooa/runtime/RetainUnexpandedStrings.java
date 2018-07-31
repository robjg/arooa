package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;

/**
 * A {@link SubstitutionPolicy} that adds the ${} back onto any unresolved
 * evaluations. This simulates Ant behaviour for undefined properties.
 * 
 * @author rob
 *
 */
public class RetainUnexpandedStrings implements SubstitutionPolicy {

	@Override
	public Evaluator modify(final Evaluator existingEvaluator) {
		return new Evaluator() {
			
			@Override
			public <T> T evaluate(String propertyExpression, ArooaSession session,
					Class<T> type) throws ArooaPropertyException,
					ArooaConversionException {
				T evaluation = existingEvaluator.evaluate(propertyExpression, session, type);
						
				if (String.class.isAssignableFrom(type) && evaluation == null) {
					return type.cast("${" + propertyExpression + "}");
				}
				else {
					return evaluation;
				}
			}
		};
	}
	
}
