package org.oddjob.arooa.deploy;

import java.util.HashSet;
import java.util.Set;

import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

public class DefaultBeanDescriptorProvider implements BeanDescriptorProvider {

	private static final Set<Class<?>> ATTRIBUTE_TYPES =
		new HashSet<Class<?>>();
	
	static {
		ATTRIBUTE_TYPES.add(String.class);
		ATTRIBUTE_TYPES.add(Integer.class);
		ATTRIBUTE_TYPES.add(Short.class);
		ATTRIBUTE_TYPES.add(Long.class);
		ATTRIBUTE_TYPES.add(Double.class);
		ATTRIBUTE_TYPES.add(Float.class);
		ATTRIBUTE_TYPES.add(Byte.class);
		ATTRIBUTE_TYPES.add(Boolean.class);
		ATTRIBUTE_TYPES.add(Character.class);
		ATTRIBUTE_TYPES.add(Number.class);
	}
	
	public PropertyDefinitionsHelper getBeanDescriptor(
			ArooaClass arooaClass, PropertyAccessor accessor) 
	throws ArooaPropertyException {
		
		BeanOverview beanOverview = arooaClass.getBeanOverview(accessor);
		
		String[] properties = beanOverview.getProperties();

		PropertyDefinitionsHelper defs = 
				new PropertyDefinitionsHelper(arooaClass);
		
		for (int i = 0; i < properties.length; ++i) {
			
			String property = properties[i];
		
			if (!beanOverview.hasWriteableProperty(property)) {
				continue;
			}
			
			PropertyDefinition def;
			
			if (beanOverview.isIndexed(property) || 
					beanOverview.isMapped(property)) {
				def = new PropertyDefinition(property, 
						PropertyDefinition.PropertyType.ELEMENT);
			}
			else {
				Class<?> propertyType = beanOverview.getPropertyType(property);
	
				if (propertyType.isPrimitive() || 
						propertyType.isEnum() ||
						ATTRIBUTE_TYPES.contains(propertyType)) {					
					def = new PropertyDefinition(property, 
							PropertyDefinition.PropertyType.ATTRIBUTE);
				}
				else {
					def = new PropertyDefinition(property, 
							PropertyDefinition.PropertyType.ELEMENT);
				}
			}			

			defs.addPropertyDefinition(def);
		}
		
		return defs;
	}
}
