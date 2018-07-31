package org.oddjob.arooa.standard;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.DefaultConversionProvider;
import org.oddjob.arooa.deploy.ClassMappingsList;
import org.oddjob.arooa.deploy.SupportedBeanDescriptorProvider;
import org.oddjob.arooa.life.ClassLoaderClassResolver;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * Default behaviour for Arooa configuration without default mappings.
 * 
 * @author rob
 *
 */
public class BaseArooaDescriptor implements ArooaDescriptor {

	/** Cache bean descriptors. */
	private final Map<ArooaClass, ArooaBeanDescriptor>
		beanDescriptors = new HashMap<ArooaClass, ArooaBeanDescriptor>();

	/** The class resolver. */
	private final ClassResolver classResolver;
	
	public BaseArooaDescriptor() {
		this(BaseArooaDescriptor.class.getClassLoader());
	}
	
	public BaseArooaDescriptor(
			ClassLoader classLoader) {
		classResolver = new ClassLoaderClassResolver(classLoader);
	}
	
	@Override
	public ConversionProvider getConvertletProvider() {
		return new DefaultConversionProvider();
	}
	
	@Override
	public ElementMappings getElementMappings() {		
		return new ClassMappingsList();
	}

	@Override
	public ArooaBeanDescriptor getBeanDescriptor(
			ArooaClass arooaClass,
			PropertyAccessor accessor) {

		ArooaBeanDescriptor beanDescriptor = beanDescriptors.get(
				arooaClass); 
		
		if (beanDescriptor != null) {
			return beanDescriptor;
		}		
		
		beanDescriptor = new SupportedBeanDescriptorProvider(
				).getBeanDescriptor(arooaClass, accessor);
		
		beanDescriptors.put(arooaClass, beanDescriptor);		
		
		return beanDescriptor;
	}
	
	@Override
	public String getPrefixFor(URI namespace) {
		return null;
	}

	@Override
	public ClassResolver getClassResolver() {
		return classResolver;
	}
}
