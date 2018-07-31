package org.oddjob.arooa.reflect;

/**
 * Provide a {@link BeanView} for a given {@link ArooaClass}
 * 
 * @author rob
 *
 */
public interface BeanViews {
	
	/**
	 * Provide a view.
	 * 
	 * @param arooaClass
	 * 
	 * @return The view, or null if there isn't one.
	 */
	public BeanView beanViewFor(ArooaClass arooaClass);

}
