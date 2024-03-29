package org.oddjob.arooa.deploy;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.types.XMLConfigurationType;

import java.net.URI;

public class ArooaDescriptorDescriptorTest extends Assert {

   @Test
	public void testClassMappings() throws Exception {
		
		ArooaDescriptorDescriptor test = new ArooaDescriptorDescriptor();
		
		ElementMappings mappings = 
			test.getElementMappings();
		
		ArooaClass classIdentifier;
		
		classIdentifier = mappings.mappingFor(				
				new ArooaElement(new URI("http://rgordon.co.uk/oddjob/arooa"), 
						"descriptors"),
				new InstantiationContext(ArooaType.VALUE, null));
		
		assertEquals(ListDescriptorBean.class, 
				classIdentifier.forClass());
		
		classIdentifier = mappings.mappingFor(				
				new ArooaElement(new URI("http://rgordon.co.uk/oddjob/arooa"), 
						"descriptor"),
				new InstantiationContext(ArooaType.VALUE, null));
		
		assertEquals(ArooaDescriptorBean.class, 
				classIdentifier.forClass());
		
		classIdentifier = mappings.mappingFor(				
				new ArooaElement(new URI("http://rgordon.co.uk/oddjob/arooa"), 
						"bean-def"),
				new InstantiationContext(ArooaType.VALUE, null));
		
		assertEquals(BeanDefinitionBean.class,
				classIdentifier.forClass());
		
		classIdentifier = mappings.mappingFor(				
				new ArooaElement(new URI("http://rgordon.co.uk/oddjob/arooa"), 
						"property"),
				new InstantiationContext(ArooaType.VALUE, null));
		
		assertEquals(PropertyDefinitionBean.class,
				classIdentifier.forClass());
		
		classIdentifier = mappings.mappingFor(				
				new ArooaElement(new URI("http://rgordon.co.uk/oddjob/arooa"), 
						"configuration"),
				new InstantiationContext(ArooaType.VALUE, null));
		
		assertEquals(XMLConfigurationType.class, 
				classIdentifier.forClass());
	}
	
   @Test
	public void testSupports() throws Exception {
		
		ArooaDescriptorDescriptor test = new ArooaDescriptorDescriptor();
		
		ElementMappings mappings = test.getElementMappings();
		
		ArooaElement[] element = mappings.elementsFor(
				new InstantiationContext(ArooaType.VALUE, 
						new SimpleArooaClass(ArooaDescriptorFactory.class),
						new DefaultConverter()));
		
		assertEquals(3, element.length);
		
		assertEquals(
				new ArooaElement(new URI("http://rgordon.co.uk/oddjob/arooa"), "descriptors"), 
				element[0]);
		assertEquals(
				new ArooaElement(new URI("http://rgordon.co.uk/oddjob/arooa"), "descriptor"), 
				element[1]);
		assertEquals(
				new ArooaElement(new URI("http://rgordon.co.uk/oddjob/arooa"), "magic-beans"), 
				element[2]);
	}
	
}
