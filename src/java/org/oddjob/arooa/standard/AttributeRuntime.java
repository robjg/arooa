/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.runtime.ParsedExpression;

/**
 * Holds an attribute name value pair, and handles set the
 * property on the parent runtime.
 * 
 * @author rob
 *
 */
class AttributeRuntime {

	private final String attribute;
	private final ParsedExpression evaluator;
	
	private final Class<?> type;
	
	private final InstanceConfiguration parentRuntime;
	
	public AttributeRuntime(InstanceConfiguration parentRuntime,
			String attribute,
			ParsedExpression evaluator,
			Class<?> type) 
	throws ArooaException {
		this.parentRuntime = parentRuntime;
		this.attribute = attribute;
		this.evaluator = evaluator;
		this.type = type;
	}
	
	public void init(ArooaContext context) 
	throws ArooaPropertyException {
		
		if (!evaluator.isConstantAttribute()) {
			return;
		}
		
		try {
			Object newValue = evaluator.evaluateAsAttribute(
					context.getSession(), type);
			
			parentRuntime.setProperty(attribute, newValue, context);
		} catch (ArooaConversionException e) {
			throw new ArooaPropertyException(
					attribute, e);
		}
	}
	
	public void configure(ArooaContext context) 
	throws ArooaPropertyException {
				
		if (evaluator.isConstantAttribute()) {
			return;
		}
		
		try {
			Object newValue = evaluator.evaluateAsAttribute(
				context.getSession(), type);
		
			parentRuntime.setProperty(attribute, newValue, context);		
		} catch (ArooaConversionException e) {
			throw new ArooaPropertyException(
					attribute, e);
		}
	}	
}
