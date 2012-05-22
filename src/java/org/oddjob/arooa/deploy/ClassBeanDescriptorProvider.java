package org.oddjob.arooa.deploy;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * A {@link BeanDescriptorProvider} that looks for a class of
 * the same name as the bean plus Arooa to provide the descriptor.
 * 
 * @author rob
 *
 */
public class ClassBeanDescriptorProvider implements BeanDescriptorProvider {

	private static final Logger logger = Logger.getLogger(
			ClassBeanDescriptorProvider.class);
	
	public final static String NAME_EXTENSION="Arooa";
	
	public ArooaBeanDescriptor getBeanDescriptor(
			ArooaClass classIdentifier, PropertyAccessor accessor) {
		
		Class<?> forClass = classIdentifier.forClass();

		String className = forClass.getName() + NAME_EXTENSION;
		
		Class<?> cl = null;
		try {
			cl = Class.forName(className, true, forClass.getClassLoader());
		}
		catch (ClassNotFoundException e) {
			return null;
		}
		
		if (! (ArooaBeanDescriptor.class.isAssignableFrom(cl))) {
			throw new ArooaException(className + 
					" (" +
					cl.getClassLoader().toString() + 
					") is not an instance of " +
					ArooaBeanDescriptor.class.getName() + 
					" (" +
					ArooaBeanDescriptor.class.getClassLoader().toString() + 
					")"
				);
		}

		Object beanDescriptor = null;
		
		try {
			beanDescriptor = cl.newInstance();
		} catch (Exception e) {
			throw new ArooaException(e);
		}
				
		logger.debug("Found ArooaBeanDescriptor by class [" +  
				className + "]");
		
		return new LinkedBeanDescriptor(
				(ArooaBeanDescriptor) beanDescriptor,
				new DefaultBeanDescriptorProvider(
				).getBeanDescriptor(classIdentifier, accessor));
	}
}
