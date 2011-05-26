package org.oddjob.arooa.deploy;

import junit.framework.TestCase;

import org.oddjob.arooa.MockElementMappings;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;

public class ClassMappingsListTest extends TestCase {

	private class Mappings1 extends MockElementMappings {

		@Override
		public ArooaClass mappingFor(ArooaElement element,
				InstantiationContext parentContext) {

			if ("apple".equals(element.getTag())) {
				return new SimpleArooaClass(String.class);
			}
			return null;
		}
	};
	
	private class Mappings2 extends MockElementMappings {
		
		@Override
		public ArooaClass mappingFor(ArooaElement element,
				InstantiationContext parentContext) {

			if ("carrot".equals(element.getTag())) {
				return new SimpleArooaClass(Number.class);
			}
			if ("apple".equals(element.getTag())) {
				return new SimpleArooaClass(Number.class);
			}
			return null;
		}
	};
	
	public void testElementSearchOrder() {
		
		ClassMappingsList test = new ClassMappingsList();
		
		test.addMappings(new Mappings2());
		test.addMappings(new Mappings1());
		
		ArooaClass result;
		
		result = test.mappingFor(new ArooaElement("apple"), null);
		
		assertEquals(String.class, result.forClass());
		
		result = test.mappingFor(new ArooaElement("carrot"), null);
		
		assertEquals(Number.class, result.forClass());
	}
}
