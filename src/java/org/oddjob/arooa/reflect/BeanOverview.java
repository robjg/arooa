/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.reflect;

/**
 * A Bean Overview is a very light look at the characteristics of
 * a bean.
 * <p>
 *
 */
public interface BeanOverview {

	public String[] getProperties();
	
	public boolean hasWriteableProperty(String property);
	
	public boolean hasReadableProperty(String property);
	
	public Class<?> getPropertyType(String property) throws ArooaNoPropertyException;
	
	public boolean isIndexed(String property) throws ArooaNoPropertyException;
	
	public boolean isMapped(String property) throws ArooaNoPropertyException;
	
}
