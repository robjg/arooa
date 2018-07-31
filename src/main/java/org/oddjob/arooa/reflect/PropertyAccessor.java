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
	public void setProperty(Object bean, String expression, Object value) 
	throws ArooaPropertyException; 
	
	/**
	 * Set a property on a bean.
	 * 
	 * @param bean The bean. Must not be null.
	 * @param name The name. Must not be null.
	 * @param value The value. Can be null.
	 */
	public void setSimpleProperty(Object bean, String name, Object value) 
	throws ArooaPropertyException; 
	
	/**
	 * Set a mapped property on a bean.
	 * 
	 * @param bean The bean. Must not be null.
	 * @param name The name. Must not be null.
	 * @param key The mapped property's key. Must not be null.
	 * @param value The value. Can be null.
	 */
	public void setMappedProperty(Object bean, String name, String key, Object value) 
	throws ArooaPropertyException;
	
	/**
	 * Set an indexed property on a bean.
	 * 
	 * @param bean The bean. Must not be null.
	 * @param name The name. Must not be null.
	 * @param index The indexed property's index. 0 based.
	 * @param value The value. Can be null.
	 */
	public void setIndexedProperty(Object bean, String name, int index, Object value) 
	throws ArooaPropertyException;

	/**
	 * Get the {@link ArooaClass} for a bean.
	 * 
	 * @param bean
	 * @return
	 */
	public ArooaClass getClassName(Object bean);
	
	/**
	 * Provide {@link org.oddjob.arooo.BeanOverview}. Note that this
	 * will provide the overview for the Java class. If using a bean
	 * that might be a dynabean then use 
	 * <code>getArooaClass(bean).getBeanOverview()</code> instead.
	 * 
	 * @param forClassCl
	 * @return
	 * @throws ArooaException
	 */	
	public BeanOverview getBeanOverview(Class<?> forClass)  
	throws ArooaException;
		
	/**
	 * Get a property.
	 * 
	 * @param bean The bean.
	 * @param The property.
	 * @return The property value.
	 */
	public Object getProperty(Object bean, String property) 
	throws ArooaPropertyException;
	
	/**
	 * Get a property of a required type.
	 * 
	 * @param bean The bean.
	 * @param The property.
	 * @return The property value.
	 */
	public <T> T getProperty(Object bean, 
			String property, Class<T> required) 
	throws ArooaPropertyException, ArooaConversionException;
	

	/**
	 * Provide an accessor that performs conversions.
	 * 
	 * @param converter The converter.
	 * @return An accessor that performs conversions.
	 */
	public PropertyAccessor accessorWithConversions(
			ArooaConverter converter);
}
