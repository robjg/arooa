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
import org.oddjob.arooa.deploy.AnnotatedBeanDescriptorProvider;
import org.oddjob.arooa.deploy.ClassBeanDescriptorProvider;
import org.oddjob.arooa.deploy.DefaultBeanDescriptorProvider;
import org.oddjob.arooa.deploy.LinkedClassMapping;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.life.BaseElementMappings;
import org.oddjob.arooa.life.ClassLoaderClassResolver;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * Provides default behaviour for Arooa configuration. This includes
 * support for the &lt;value&gt;, &lt;list&gt;, &lt;import&gt;
 * and &lt;xml&gt; elements.
 * 
 * elements.
 * 
 * @author rob
 *
 */
public class StandardArooaDescriptor implements ArooaDescriptor {

	private final Map<ArooaClass, ArooaBeanDescriptor>
		beanDescriptors = new HashMap<ArooaClass, ArooaBeanDescriptor>();

	private MappingsSwitch mappings = 
		new MappingsSwitch(new BaseElementMappings(), 
				new LinkedClassMapping(
						new DefaultValuesMappings(), 
						new BaseElementMappings()));
	
	
	private final ClassResolver classResolver;
	
	public StandardArooaDescriptor() {
		this(StandardArooaDescriptor.class.getClassLoader());
	}
	
	public StandardArooaDescriptor(
			ClassLoader classLoader) {
		classResolver = new ClassLoaderClassResolver(classLoader);
	}
	
	@Override
	public ConversionProvider getConvertletProvider() {
		return new DefaultConversionProvider();
	}
	
	@Override
	public ElementMappings getElementMappings() {		
		return mappings;
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
			
		beanDescriptor = new ClassBeanDescriptorProvider(
		).getBeanDescriptor(arooaClass, accessor);

		if (beanDescriptor == null) {
			beanDescriptor = 
				new AnnotatedBeanDescriptorProvider().getBeanDescriptor(
						arooaClass, accessor);
		}

		if (beanDescriptor == null) {
			beanDescriptor = new DefaultBeanDescriptorProvider(
					).getBeanDescriptor(arooaClass, accessor);
		}
		
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
