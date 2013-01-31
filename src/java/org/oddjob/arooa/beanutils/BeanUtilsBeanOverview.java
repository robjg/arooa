/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.beanutils;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.beanutils.MappedPropertyDescriptor;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.oddjob.arooa.reflect.ArooaNoPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyExceptionBuilder;

class BeanUtilsBeanOverview implements BeanOverview {

	private final Class<?> beanClass;

	private final Map<String, PropertyDescriptor> descriptors = 
		new LinkedHashMap<String, PropertyDescriptor>();

	BeanUtilsBeanOverview(Class<?> beanClass, PropertyUtilsBean propertyUtilsBean) {
		this.beanClass = beanClass;
		PropertyDescriptor[] descriptors = propertyUtilsBean
				.getPropertyDescriptors(beanClass);
		for (int i = 0; i < descriptors.length; i++) {
			this.descriptors.put(descriptors[i].getName(), descriptors[i]);
		}
    	// now find mapped properties.
    	Method[] methods = beanClass.getMethods();
    	for (int i = 0; i < methods.length; ++i) {
    		Method method = methods[i];

    		// methods beginning with get could be properties.    		
    		if (!method.getName().startsWith("get")
    				&& !method.getName().startsWith("set")) {
    			continue;
    		}
    		
    		String propertyName = method.getName().substring(3);
    		// get on it's own is not a property
    		if (propertyName.length() == 0) {
    			continue;
    		}
    		
    		// lowercase first letter
    		propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
    		
    		Class<?>[] args = method.getParameterTypes();
    		
    		// is mapped property?
    		if (
    				(method.getName().startsWith("get")
    				&& Void.TYPE != method.getReturnType()
    				&& args.length == 1
    				&& args[0] == String.class)
    			||
    				(args.length == 2
        			&& args[0] == String.class
        			&& Void.TYPE == method.getReturnType())
        			) {
    			method = MethodUtils.getAccessibleMethod(method);
    			if (method == null) {
    				continue;
    			}
    			try {
    				this.descriptors.put(propertyName, new MappedPropertyDescriptor(
    						propertyName, beanClass));
    			} catch (IntrospectionException e) {
    				// really don't think this should happed
    				// so it's ok to through a runtime exception
    				throw new RuntimeException(e);
    			}
    		}
    	}
    	
	}

	Class<?> getBeanClass() {
		return beanClass;
	}

	public String[] getProperties() {
		return this.descriptors.keySet().toArray(
				new String[this.descriptors.size()]);
	}
	
	public Class<?> getPropertyType(String property) throws ArooaNoPropertyException {
		PropertyDescriptor propertyDescriptor = descriptors.get(property);
		if (propertyDescriptor == null) {
			throw new PropertyExceptionBuilder(
					).forClass(beanClass
					).withOverview(this
					).noPropertyException(property);
		}
		if (propertyDescriptor instanceof IndexedPropertyDescriptor) {
			return ((IndexedPropertyDescriptor) 
					propertyDescriptor).getIndexedPropertyType();
		}
		if (propertyDescriptor instanceof MappedPropertyDescriptor) {
			return ((MappedPropertyDescriptor) 
					propertyDescriptor).getMappedPropertyType();
		}
		return propertyDescriptor.getPropertyType();
	}

	public boolean hasReadableProperty(String property) {
		PropertyDescriptor descriptor = (PropertyDescriptor) descriptors
				.get(property);
		if (descriptor == null) {
			return false;
		}
		Method m;
		if (descriptor instanceof IndexedPropertyDescriptor) {
			m = ((IndexedPropertyDescriptor) 
					descriptor).getIndexedReadMethod();
		}
		else if (descriptor instanceof MappedPropertyDescriptor) {
			m = ((MappedPropertyDescriptor) 
					descriptor).getMappedReadMethod();
		}
		else {
			m = descriptor.getReadMethod();			
		}
		m = MethodUtils.getAccessibleMethod(m);
		return !(m == null);
	}

	public boolean hasWriteableProperty(String property) {
		PropertyDescriptor descriptor = (PropertyDescriptor) descriptors
				.get(property);
		if (descriptor == null) {
			return false;
		}
		Method m;
		if (descriptor instanceof IndexedPropertyDescriptor) {
			m = ((IndexedPropertyDescriptor) 
					descriptor).getIndexedWriteMethod();
		}
		else if (descriptor instanceof MappedPropertyDescriptor) {
			m = ((MappedPropertyDescriptor) 
					descriptor).getMappedWriteMethod();
		}
		else {
			m = descriptor.getWriteMethod();			
		}
		if (m == null) {
			return false;
		}
		m = MethodUtils.getAccessibleMethod(m);
		return !(m == null);
	}

	public boolean isIndexed(String property) throws ArooaNoPropertyException {
		PropertyDescriptor propertyDescriptor = descriptors.get(property);
		if (propertyDescriptor == null) {
			throw new ArooaNoPropertyException(property, beanClass,
					getProperties());
		}
		return propertyDescriptor instanceof IndexedPropertyDescriptor;
	}

	public boolean isMapped(String property) throws ArooaNoPropertyException {
		PropertyDescriptor propertyDescriptor = descriptors.get(property);
		if (propertyDescriptor == null) {
			throw new ArooaNoPropertyException(property, beanClass,
					getProperties());
		}
		return propertyDescriptor instanceof MappedPropertyDescriptor;
	}
}
