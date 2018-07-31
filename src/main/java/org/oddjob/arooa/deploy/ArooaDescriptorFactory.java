package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaDescriptor;

/**
 * Something that can create an {@link ArooaDescriptor}.
 * 
 * @author rob
 *
 */
public interface ArooaDescriptorFactory {

	/**
	 * Create an ArooaDesciptor.
	 * 
	 * @param classLoader The classLoader to use.
	 * 
	 * @return An ArooaDescriptor. May be null.
	 */
	public ArooaDescriptor createDescriptor(ClassLoader classLoader);
	
}
