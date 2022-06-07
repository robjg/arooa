package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * Something capable of providing an {@link ArooaBeanDescriptor}.
 * 
 * @author rob
 *
 */
public interface BeanDescriptorProvider {

	/**
	 * Provide the descriptor.
	 *
	 * @param forClass The class.
	 * @param accessor Used to access the bean properties for default
	 * types.
	 * 
	 * @return The bean descriptor, null if this provider
	 * isn't capable of providing it.
	 */
	ArooaBeanDescriptor getBeanDescriptor(
			ArooaClass forClass, PropertyAccessor accessor);
}
