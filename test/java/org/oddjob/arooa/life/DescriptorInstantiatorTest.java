package org.oddjob.arooa.life;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockElementMappings;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;

public class DescriptorInstantiatorTest extends TestCase {

	public interface Fruit {
		
	}
	
	public static class Apple implements Fruit {
		
	}
	

	private class OurArooaDescriptor extends MockArooaDescriptor {
		
		@Override
		public ElementMappings getElementMappings() {
			return new MappingsSwitch(new MockElementMappings(), 
					new MockElementMappings() {
					@Override
					public ArooaElement[] elementsFor(
							InstantiationContext parentContext) {
						return new ArooaElement[] {
								new ArooaElement("apple")
						};
					}
					
					@Override
					public ArooaClass mappingFor(ArooaElement element,
							InstantiationContext parentContext) {
						assertEquals(new ArooaElement("apple"), element);
						return new SimpleArooaClass(Apple.class);
					}
					
				});
		}
	}
	
	public void testPossibleElements() {
		
		ElementMappings test = 
				new OurArooaDescriptor().getElementMappings();
		
		ArooaElement[] elements = test.elementsFor(
				new InstantiationContext(ArooaType.VALUE, 
						new SimpleArooaClass(Fruit.class))); 
		
		assertEquals(1, elements.length);
		assertEquals(new ArooaElement("apple"), elements[0]);		
		
		elements = test.elementsFor(
				new InstantiationContext(ArooaType.VALUE, 
						new SimpleArooaClass(String.class))); 
		
		// Everything can be a String.
		assertEquals(1, elements.length);
	}
	
}
