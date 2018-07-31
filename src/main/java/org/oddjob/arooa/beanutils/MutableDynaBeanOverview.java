package org.oddjob.arooa.beanutils;

import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.MutableDynaClass;
import org.oddjob.arooa.reflect.ArooaNoPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;

/**
 * Creates a {@link BeanOverview} from BeanUtils <code>
 * MutableDynaClass</code>.
 * <p>
 * This differs from the standard {@link DynaBeanOverview}
 * in that properties always appear as if they exist.
 * 
 * @author rob
 *
 */
class MutableDynaBeanOverview implements BeanOverview {

	private final MutableDynaClass dynaClass;
	
	public MutableDynaBeanOverview(MutableDynaClass dynaClass) {
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
	
		Class<?> propertyType;
		if (dynaProperty.isIndexed() || dynaProperty.isMapped()) {
			propertyType = dynaProperty.getContentType();
		}
		else {
			propertyType = dynaClass.getDynaProperty(property).getType();
		}
		
		if (propertyType == null) {
			return null;
		}
		
		return propertyType;
	}
	
	public boolean hasReadableProperty(String property) {
		return true;
	}
	
	public boolean hasWriteableProperty(String property) {
		return true;
	}
	
	public boolean isIndexed(String property) throws ArooaNoPropertyException {
		
		DynaProperty dynaProperty = dynaClass.getDynaProperty(property);				
		return dynaProperty.isIndexed();
	}
	
	public boolean isMapped(String property) throws ArooaNoPropertyException {
		
		DynaProperty dynaProperty = dynaClass.getDynaProperty(property);				
		return dynaProperty.isMapped();
	}
}
