package org.oddjob.arooa.beanutils;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;

/**
 * @oddjob.description A User defined bean.
 * 
 * @author rob
 *
 */
public class MagicBean implements Serializable, DynaBean {
	private static final long serialVersionUID = 2010030100L;
	
	private final MagicBeanClass beanClass;

	private final Map<String, Object> values = 
		Collections.synchronizedMap(new HashMap<String, Object>());
	
	public MagicBean(MagicBeanClass beanClass) {
		this.beanClass = beanClass;
	}
	
	@Override
	public Object get(String name) {
		DynaProperty prop = beanClass.getDynaProperty(name);
		if (prop == null) {
			throw new IllegalArgumentException("No property [" + 
					name + "] in class [" + beanClass.getName() + "].");
		}
		
		return values.get(name);
	}
	
	@Override
	public void set(String name, Object value) {
		DynaProperty prop = beanClass.getDynaProperty(name);
		if (prop == null) {
			throw new IllegalArgumentException("No property [" + 
					name + "] in class [" + beanClass.getName() + "].");
		}
		if (value == null) {
			values.remove(name);
		} else {
			if (!((Class<?>) prop.getType()).isAssignableFrom(value.getClass())) {
				throw new ClassCastException(value.getClass() +
						" connot be cast to " + prop.getType());
			}
			values.put(name, value);
		}
	}
	
	@Override
	public Object get(String name, int index) {
		throw new UnsupportedOperationException(
				"Inexed Properties not supported yet.");
	}
	
	@Override
	public void set(String name, int index, Object value) {
		throw new UnsupportedOperationException(
				"Inexed Properties not supported yet.");
	}
	
	@Override
	public Object get(String name, String key) {
		throw new UnsupportedOperationException(
				"Mapped Properties not supported yet.");
	}
	
	@Override
	public void set(String name, String key, Object value) {
		throw new UnsupportedOperationException(
				"Mapped Properties not supported yet.");
	}
	
	@Override
	public boolean contains(String name, String key) {
		throw new UnsupportedOperationException(
				"Mapped Properties not supported yet.");
	}
	
	@Override
	public void remove(String name, String key) {
		throw new UnsupportedOperationException(
				"Mapped Properties not supported yet.");
	}
	
	@Override
	public MagicBeanClass getDynaClass() {
		return beanClass;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + beanClass.getMagicBeanName() + 
				"@" + Integer.toHexString(hashCode());
	}
}
