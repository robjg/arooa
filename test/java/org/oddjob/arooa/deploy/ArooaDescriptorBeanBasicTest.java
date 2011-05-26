package org.oddjob.arooa.deploy;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.deploy.BeanDefinitionsTest.Apple;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;

public class ArooaDescriptorBeanBasicTest extends TestCase {
	
	public void testElements() {

		ArooaDescriptorBean test = new ArooaDescriptorBean();
		
		BeanDefinition definition = new BeanDefinition();
		definition.setClassName(Apple.class.getName());
		definition.setElement("apple");

		test.setComponents(0, definition);		
		
		ArooaDescriptor result = test.createDescriptor(
				getClass().getClassLoader());
		
		
		ArooaElement[] elements = result.getElementMappings().elementsFor(
				new InstantiationContext(ArooaType.COMPONENT, 
						new SimpleArooaClass(Object.class))); 
		
		assertEquals(1, elements.length);
		assertEquals(new ArooaElement("apple"), elements[0]);		
		
	}
	
}
