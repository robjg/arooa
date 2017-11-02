package org.oddjob.arooa.standard;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.MockBeanRegistry;
import org.oddjob.arooa.runtime.ParsedExpression;
import org.oddjob.arooa.runtime.PropertyManager;

public class AttributeRuntimeTest extends Assert {

	private class ColourAttributeConfiguration extends MockInstanceConfiguration {
		public ColourAttributeConfiguration() {
			super(new SimpleArooaClass(Object.class), new Object(), new MutableAttributes());
		}
		
		String colour;
		@Override
		public void setProperty(String name, Object value, ArooaContext context) throws ArooaException {
			assertEquals("colour", name);
			colour = (String) value;
		}
	}

	private class OurSession extends MockArooaSession {
		
		@Override
		public BeanRegistry getBeanRegistry() {
			return new MockBeanRegistry() {
				@SuppressWarnings("unchecked")
				@Override
				public <T> T lookup(String path, Class<T> required) {
					assertEquals("red", path);
					assertEquals(String.class, required);
					return (T) "blue";
				}
			};
		}

		@Override
		public PropertyManager getPropertyManager() {
			return new MockPropertyManager() {
				@Override
				public String lookup(String propertyName) {
					return null;
				}
			};
		}
		
		@Override
		public ArooaTools getTools() {
			return new StandardTools();
		}
	}
	
	private class OurContext extends MockArooaContext {
		
		@Override
		public ArooaSession getSession() {
			return new OurSession();
		}
	}
	
   @Test
	public void testConstantAttribute() throws ArooaPropertyException {
		
		ColourAttributeConfiguration parentRuntime = new ColourAttributeConfiguration();
		
		ParsedExpression expression = 
			new StandardPropertyHelper().parse("blue");
		
		AttributeRuntime test = new AttributeRuntime(
				parentRuntime, "colour", expression, String.class);
		
		assertNull(parentRuntime.colour);
		
		test.init(new OurContext());
		
		assertEquals("blue", parentRuntime.colour);

		// test has no affect
		test.configure(new OurContext());
		
		assertEquals("blue", parentRuntime.colour);
	}
	
   @Test
	public void testVariableAttribute() {
		
		ColourAttributeConfiguration parentRuntime = new ColourAttributeConfiguration();
		
		ParsedExpression expression = 
			new StandardPropertyHelper().parse("${red}");
		
		AttributeRuntime test = new AttributeRuntime(
				parentRuntime, "colour", expression, String.class);
		
		assertNull(parentRuntime.colour);
		
		test.init(new OurContext());
		
		assertEquals(null, parentRuntime.colour);

		test.configure(new OurContext());
		
		assertEquals("blue", parentRuntime.colour);
	}
}
