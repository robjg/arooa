package org.oddjob.arooa.registry;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.MockArooaTools;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.convert.MockArooaConverter;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.MockPropertyAccessor;
import org.oddjob.arooa.reflect.PropertyAccessor;

public class FullPathLookupTest extends Assert {

	public static class Fruit {
		
		public String getColour() {
			return "red";
		}
	}
	
	class OurSession extends MockArooaSession {
		
		BeanRegistry registry = new SimpleBeanRegistry();
		
		@Override
		public BeanRegistry getBeanRegistry() {
			return registry;
		}
		
		@Override
		public ArooaTools getTools() {
			return new MockArooaTools() {
				@Override
				public PropertyAccessor getPropertyAccessor() {
					return new BeanUtilsPropertyAccessor();
				}
			};
		}
	}
	
   @Test
	public void testRegistryLookup() throws ArooaPropertyException {
		
		OurSession session = new OurSession();
		
		session.registry.register("fruit", new Fruit());

		BeanDirectory lookup = session.getBeanRegistry();
		
		assertNotNull(lookup.lookup("fruit"));
		assertNotNull(lookup.lookup("fruit/"));
		
		// I think this should work
//		assertNotNull(lookup.lookup("/fruit"));
	}
	
	
	class Component extends MockBeanDirectoryOwner {
		BeanDirectory directory;
		
		public BeanDirectory provideBeanDirectory() {
			return directory;
		}
	}
	
   @Test
	public void testNestedLookup() {
		
		SimpleBeanRegistry test = new SimpleBeanRegistry(
				new MockPropertyAccessor(),
				new MockArooaConverter());
		
		Component c1 = new Component();
		
		test.register("outer", c1);
		
		OurSession session = new OurSession();
		
		session.registry.register("fruit", new Fruit());

		BeanDirectory lookup = session.getBeanRegistry();
		
		assertNotNull(lookup.lookup("fruit"));
		assertNotNull(lookup.lookup("fruit/"));
		
		// I think this should work
//		assertNotNull(lookup.lookup("/fruit"));
	}
	
	
   @Test
	public void testFullLookup() {
		
		OurSession session = new OurSession();
		
		session.registry.register("fruit", new Fruit());

		BeanDirectory lookup = session.getBeanRegistry();
		
		assertEquals("red", lookup.lookup("fruit.colour"));
	}
}
