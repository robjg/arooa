package org.oddjob.arooa.deploy;

import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.util.HashSet;
import java.util.Set;

/**
 * Provide a default descriptor with configured how based on the types of 
 * the property. String and Primitive type properties are attributes,
 * everything else is elements.
 *  
 * @author rob
 *
 */
public class DefaultBeanDescriptorProvider {

	private static final Set<Class<?>> ATTRIBUTE_TYPES =
			new HashSet<>();
	
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

	public void findConfiguredHow(PropertyAccessor accessor,
								  ConfiguredHowAccumulator accumulator)
	throws ArooaPropertyException {

		ArooaClass arooaClass = accumulator.getClassIdentifier();

		BeanOverview beanOverview = arooaClass.getBeanOverview(accessor);
		
		String[] properties = beanOverview.getProperties();

		for (String property : properties) {

			if (!beanOverview.hasWriteableProperty(property)) {
				continue;
			}

			PropertyDefinitionBean def;

			if (beanOverview.isIndexed(property) ||
					beanOverview.isMapped(property)) {
				accumulator.addElementProperty(property);
			} else {
				Class<?> propertyType = beanOverview.getPropertyType(property);

				// this happens with Proxies. It's a bug Oddjob isn't
				// affected by it, so fix later.
				if (propertyType == null) {
					throw new NullPointerException("No property type for [" +
							property + "] of " + arooaClass);
				}

				if (isAttribute(propertyType)) {
					accumulator.addAttributeProperty(property);
				} else {
					accumulator.addElementProperty(property);
				}
			}
		}
	}

	public static boolean isAttribute(Class<?> propertyType) {
		return propertyType.isPrimitive() ||
				propertyType.isEnum() ||
				ATTRIBUTE_TYPES.contains(propertyType);
	}
}
