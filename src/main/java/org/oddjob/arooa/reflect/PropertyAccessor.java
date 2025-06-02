/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.reflect;


import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;

/**
 * Property access.
 */
public interface PropertyAccessor {
	
	/**
	 * Set a property using a property expression.
	 * 
	 * @param bean The bean. Must not be null.
	 * @param expression The name. Must not be null.
	 * @param value The value. Can be null.
	 */
    void setProperty(Object bean, String expression, Object value)
	throws ArooaPropertyException; 
	
	/**
	 * Set a property on a bean.
	 * 
	 * @param bean The bean. Must not be null.
	 * @param name The name. Must not be null.
	 * @param value The value. Can be null.
	 */
    void setSimpleProperty(Object bean, String name, Object value)
	throws ArooaPropertyException; 
	
	/**
	 * Set a mapped property on a bean.
	 * 
	 * @param bean The bean. Must not be null.
	 * @param name The name. Must not be null.
	 * @param key The mapped property's key. Must not be null.
	 * @param value The value. Can be null.
	 */
    void setMappedProperty(Object bean, String name, String key, Object value)
	throws ArooaPropertyException;
	
	/**
	 * Set an indexed property on a bean.
	 * 
	 * @param bean The bean. Must not be null.
	 * @param name The name. Must not be null.
	 * @param index The indexed property's index. 0 based.
	 * @param value The value. Can be null.
	 */
    void setIndexedProperty(Object bean, String name, int index, Object value)
	throws ArooaPropertyException;

	/**
	 * Get the {@link ArooaClass} for a bean.
	 * 
	 * @param bean The bean.
	 * @return An Arooa Class
	 */
    ArooaClass getClassName(Object bean);
	
	/**
	 * Provide {@link BeanOverview}. Note that this
	 * will provide the overview for the Java class. If using a bean
	 * that might be a DynaBean then use
	 * {@link #getClassName(Object)} and then {@link ArooaClass#getBeanOverview(PropertyAccessor)} instead.
	 * 
	 * @param forClass The class to provide the overview for.
	 * @return An overview. Never null.
	 * @throws ArooaException If something goes wrong.
	 */
    BeanOverview getBeanOverview(Class<?> forClass)
	throws ArooaException;
		
	/**
	 * Get a property. The property is a full property expression.
	 * 
	 * @param bean The bean.
	 * @param property The property expression.
	 * @return The property value.
	 */
    Object getProperty(Object bean, String property)
	throws ArooaPropertyException;
	
	/**
	 * Get a property of a required type.
	 * 
	 * @param bean The bean.
	 * @param property The property expression.
	 * @return The property value.
	 */
    <T> T getProperty(Object bean,
                      String property, Class<T> required)
	throws ArooaPropertyException, ArooaConversionException;

	/**
	 * Get a simple property. A none nested, none indexed, none mapped property.
	 *
	 * @param bean The bean.
	 * @param property The property name.
	 * @return The property value.
	 */
	Object getSimpleProperty(Object bean, String property)
			throws ArooaPropertyException;

	/**
	 * Get an indexed property.
	 *
	 * @param bean The bean.
	 * @param property The property name.
	 * @param index The index.
	 *
	 * @return The property value.
	 */
	Object getIndexedProperty(Object bean, String property, int index)
			throws ArooaPropertyException;

	/**
	 * Get a property. The property is a full property expression.
	 *
	 * @param bean The bean.
	 * @param property The property name.
	 * @param key The key
	 *               .
	 * @return The property value.
	 */
	Object getMappedProperty(Object bean, String property, String key)
			throws ArooaPropertyException;
	/**
	 * Provide an accessor that performs conversions.
	 * 
	 * @param converter The converter.
	 * @return An accessor that performs conversions.
	 */
    PropertyAccessor accessorWithConversions(
            ArooaConverter converter);
}
