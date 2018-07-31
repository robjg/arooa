package org.oddjob.arooa.standard;

import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.reflect.ArooaPropertyException;

/**
 * Wrapper for Simple Instances. Instances where the property
 * is not an indexed or mapped property.
 * 
 * @author rob
 *
 */
class SimpleInstanceRuntime extends InstanceRuntime {

	public SimpleInstanceRuntime(
			InstanceConfiguration instance,
			ArooaContext context) {
		super(instance, context);
	}

	@Override
	ParentPropertySetter getParentPropertySetter() {
		return new ParentPropertySetter() {
			public void parentSetProperty(Object value) 
			throws ArooaPropertyException {
				
				getParentContext().getRuntime().setProperty(null, value);
			}
		};
	}
	
}
