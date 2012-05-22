package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * Encapsulate all the supported methods for providing an
 * {@link ArooaBeanDescriptor}.
 * 
 * @author rob
 *
 */
public class SupportedBeanDescriptorProvider 
implements BeanDescriptorProvider {

	@Override
	public ArooaBeanDescriptor getBeanDescriptor(
			ArooaClass arooaClass,
			PropertyAccessor accessor) {

		ArooaBeanDescriptor beanDescriptor = new ClassBeanDescriptorProvider(
		).getBeanDescriptor(arooaClass, accessor);

		if (beanDescriptor == null) {
			beanDescriptor = 
				new AnnotatedBeanDescriptorProvider().getBeanDescriptor(
						arooaClass, accessor);
		}

		if (beanDescriptor == null) {
			throw new NullPointerException(
					"AnnotatedBeanDescriptorProvider failed to provide a descriptor.");
		}
		
		return beanDescriptor;
	}
}
