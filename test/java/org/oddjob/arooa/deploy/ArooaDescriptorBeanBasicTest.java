package org.oddjob.arooa.deploy;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.beandocs.MappingsContents;
import org.oddjob.arooa.deploy.BeanDefinitionsTest.Apple;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.types.BeanType;

public class ArooaDescriptorBeanBasicTest extends TestCase {
	
	public void testElements() {

		ArooaDescriptorBean test = new ArooaDescriptorBean();
		
		BeanDefinition definition = new BeanDefinition();
		definition.setClassName(Apple.class.getName());
		definition.setElement("apple");

		test.setComponents(0, definition);		
		
		ArooaDescriptor result = test.createDescriptor(
				getClass().getClassLoader());
		
		ElementMappings mappings = result.getElementMappings();
		
		ArooaElement[] elements = mappings.elementsFor(
				new InstantiationContext(ArooaType.COMPONENT, 
						new SimpleArooaClass(Object.class))); 
		
		assertEquals(1, elements.length);
		assertEquals(new ArooaElement("apple"), elements[0]);		
		
		// sanity check because of a bug in the reference.
		assertNull(mappings.mappingFor(BeanType.ELEMENT,
				new InstantiationContext(ArooaType.COMPONENT, null)));
		
		MappingsContents contents = mappings.getBeanDoc(ArooaType.COMPONENT);
		
		ArooaElement[] elementContents = contents.allElements();
		assertEquals(1, elementContents.length);
		assertEquals(new ArooaElement("apple"), 
				elementContents[0]);		
	}
	
}
