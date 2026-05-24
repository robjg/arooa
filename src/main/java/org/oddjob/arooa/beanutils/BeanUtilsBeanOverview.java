/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.beanutils;

import org.apache.commons.beanutils.MappedPropertyDescriptor;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.oddjob.arooa.reflect.ArooaNoPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyExceptionBuilder;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

class BeanUtilsBeanOverview implements BeanOverview {

	private final Class<?> beanClass;

	private final Map<String, PropertyOverview> descriptors =
		new LinkedHashMap<String, PropertyOverview>();

	BeanUtilsBeanOverview(Class<?> beanClass, PropertyUtilsBean propertyUtilsBean) {
		this.beanClass = beanClass;
		PropertyDescriptor[] descriptors = propertyUtilsBean
				.getPropertyDescriptors(beanClass);
		for (int i = 0; i < descriptors.length; i++) {
			PropertyDescriptor pd = descriptors[i];
			PropertyOverview overview;
			if (pd instanceof IndexedPropertyDescriptor) {
				overview = PropertyOverview.ofIndexed((IndexedPropertyDescriptor) pd);
			} else if (pd instanceof MappedPropertyDescriptor) {
				overview = PropertyOverview.ofMapped((MappedPropertyDescriptor) pd);
			} else {
				overview = PropertyOverview.ofSimple(pd);
			}
			this.descriptors.put(pd.getName(), overview);
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
    				MappedPropertyDescriptor descriptor = new MappedPropertyDescriptor(
    						propertyName, beanClass);
    				this.descriptors.put(propertyName, PropertyOverview.ofMapped(descriptor));
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
	
	public Type getPropertyType(String property) throws ArooaNoPropertyException {
		PropertyOverview overview = descriptors.get(property);
		if (overview == null) {
			throw new PropertyExceptionBuilder(
					).forClass(beanClass
					).withOverview(this
					).noPropertyException(property);
		}
		return overview.getPropertyType();
	}

	public boolean hasReadableProperty(String property) {
		PropertyOverview overview = descriptors.get(property);
		if (overview == null) {
			return false;
		}
		return overview.isReadable();
	}

	public boolean hasWriteableProperty(String property) {
		PropertyOverview overview = descriptors.get(property);
		if (overview == null) {
			return false;
		}
		return overview.isWritable();
	}

	public boolean isIndexed(String property) throws ArooaNoPropertyException {
		PropertyOverview overview = descriptors.get(property);
		if (overview == null) {
			throw new ArooaNoPropertyException(property, beanClass,
					getProperties());
		}
		return overview.isIndexed();
	}

	public boolean isMapped(String property) throws ArooaNoPropertyException {
		PropertyOverview overview = descriptors.get(property);
		if (overview == null) {
			throw new ArooaNoPropertyException(property, beanClass,
					getProperties());
		}
		return overview.isMapped();
	}
}
