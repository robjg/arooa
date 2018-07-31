package org.oddjob.arooa.beanutils;

import org.oddjob.arooa.reflect.ArooaNoPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;

class UnknownBeanOverview implements BeanOverview {

	public String[] getProperties() {
		return new String[0];
	}
	
	public Class<?> getPropertyType(String property)
			throws ArooaNoPropertyException {
		return Object.class;
	}
	
	public boolean hasReadableProperty(String property) {
		return true;
	}
	
	public boolean hasWriteableProperty(String property) {
		return true;
	}
	
	public boolean isIndexed(String property) throws ArooaNoPropertyException {
		return false;
	}
	
	public boolean isMapped(String property) throws ArooaNoPropertyException {
		return false;
	}
	
}
