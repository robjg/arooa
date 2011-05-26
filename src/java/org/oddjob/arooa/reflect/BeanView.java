package org.oddjob.arooa.reflect;

/**
 * Provide extra information about a bean. The need for this information came
 * from Java's <code>Class.getMethod()</code> and 
 * <code>BeanUtils.getPropertyDescriptors()</code> not guaranteeing the order 
 * of properties.
 * 
 * @author rob
 *
 */
public interface BeanView {

	public String[] getProperties();
	
	public String titleFor(String property);
	
}
