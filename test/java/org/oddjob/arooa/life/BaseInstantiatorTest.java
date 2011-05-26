package org.oddjob.arooa.life;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.reflect.ArooaClass;

public class BaseInstantiatorTest extends TestCase {

	public static class Fruit {
		
	}
	
	
	public void testClassElement() {
				
		MutableAttributes attrs = new MutableAttributes();
		attrs.set("class", Fruit.class.getName());
		
		ArooaElement element = new ArooaElement("bean", attrs);
		
		BaseElementMappings test = new BaseElementMappings();

		ArooaClass result = test.mappingFor(element, 
				new InstantiationContext(ArooaType.VALUE, null, 
						new ClassLoaderClassResolver(
			    				getClass().getClassLoader())));
		
		assertEquals(Fruit.class, ((SimpleArooaClass) result).forClass());
	}

	public void testIsElement() {
		
		ArooaElement element = new ArooaElement("is");
		
		BaseElementMappings test = new BaseElementMappings();

		ArooaClass result = test.mappingFor(element, 
				new InstantiationContext(ArooaType.VALUE, 
						new SimpleArooaClass(Fruit.class), 
						new ClassLoaderClassResolver(
			    				getClass().getClassLoader())));
		
		assertEquals(Fruit.class, result.forClass());
	}

}
