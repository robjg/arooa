package org.oddjob.arooa.beanutils;

import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.oddjob.arooa.reflect.ArooaNoPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;

public class DynaBeanOverview implements BeanOverview {

	private final DynaClass dynaClass;
		
	public DynaBeanOverview(DynaClass dynaClass) {
		this.dynaClass = dynaClass;
	}
	
	public String[] getProperties() {
		DynaProperty[] properties = dynaClass.getDynaProperties();
		String[] names = new String[properties.length];
		
		for (int i = 0; i < properties.length; ++i) {
			names[i] = properties[i].getName();
		}
		
		return names;
	}
	
	public Class<?> getPropertyType(String property)
			throws ArooaNoPropertyException {
		DynaProperty dynaProperty = dynaClass.getDynaProperty(property);
		if (dynaProperty == null) {
			throw new ArooaNoPropertyException(property, dynaClass.getClass());
		}
		if (dynaProperty.isIndexed() || dynaProperty.isMapped()) {
			return dynaProperty.getContentType();
		}
		return dynaProperty.getType();
	}
	
	public boolean hasReadableProperty(String property) {
		DynaProperty dynaProperty = dynaClass.getDynaProperty(property);
		if (dynaProperty == null) {
			return false;
		}
		return true;
	}
	
	public boolean hasWriteableProperty(String property) {
		DynaProperty dynaProperty = dynaClass.getDynaProperty(property);
		if (dynaProperty == null) {
			return false;
		}
		return true;
	}
	
	public boolean isIndexed(String property) throws ArooaNoPropertyException {
		DynaProperty dynaProperty = dynaClass.getDynaProperty(property);
		if (dynaProperty == null) {
			throw new ArooaNoPropertyException(property, dynaClass.getClass());
		}
		return dynaProperty.isIndexed();
	}
	
	public boolean isMapped(String property) throws ArooaNoPropertyException {
		DynaProperty dynaProperty = dynaClass.getDynaProperty(property);
		if (dynaProperty == null) {
			throw new ArooaNoPropertyException(property, dynaClass.getClass());
		}
		return dynaProperty.isMapped();
	}
	
}
