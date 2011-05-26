package org.oddjob.arooa.deploy;

import java.net.URI;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.types.XMLConfigurationType;

public class ArooaDescriptorDescriptorTest extends TestCase {

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
		
		assertEquals(BeanDefinition.class, 
				classIdentifier.forClass());
		
		classIdentifier = mappings.mappingFor(				
				new ArooaElement(new URI("http://rgordon.co.uk/oddjob/arooa"), 
						"property"),
				new InstantiationContext(ArooaType.VALUE, null));
		
		assertEquals(PropertyDefinition.class, 
				classIdentifier.forClass());
		
		classIdentifier = mappings.mappingFor(				
				new ArooaElement(new URI("http://rgordon.co.uk/oddjob/arooa"), 
						"configuration"),
				new InstantiationContext(ArooaType.VALUE, null));
		
		assertEquals(XMLConfigurationType.class, 
				classIdentifier.forClass());
	}
	
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
