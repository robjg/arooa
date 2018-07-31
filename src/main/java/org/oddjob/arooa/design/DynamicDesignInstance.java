package org.oddjob.arooa.design;

import org.oddjob.arooa.types.BeanType;


/**
 * A {@link DesignInstance} where the class changes.
 * 
 * @see BeanType
 * 
 * @author rob
 *
 */
public interface DynamicDesignInstance extends DesignInstance {

	public String getClassName();
	
	public void setClassName(String className);

	public DesignProperty[] getBeanProperties();
}
