package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaNoPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

public class PropertyDefinitionImpl implements PropertyDefinition {

	private final String property;
	
	private final Class<?> type;
	
	PropertyDefinitionImpl(String property, ArooaContext parentContext) 
	throws ArooaNoPropertyException, ArooaException {
		this.property = property;
		RuntimeConfiguration parentRuntime = parentContext.getRuntime();

		PropertyAccessor propertyAccessor = parentContext.getSession(
				).getTools().getPropertyAccessor();
		
		BeanOverview overview = parentRuntime.getClassIdentifier(
				).getBeanOverview(propertyAccessor);
		
		type = overview.getPropertyType(property);

	}
	
	public String getPropertyName() {
		return property;
	}
	
	public ArooaClass getPropertyType() {
		return new SimpleArooaClass(type);
	}
}
