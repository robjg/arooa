package org.oddjob.arooa.standard;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

public class AttributeSetterTest extends TestCase {

	public static class Apple {
		
		String colour;
		
		public void setColour(String colour) {
			this.colour = colour;
		}
		
	}
	
	public static class Banana {

		int curvature;
		
		public void setCurvature(int curvature) {
			this.curvature = curvature;
		}
	}
		
	private class OurContext extends MockArooaContext {
		StandardArooaSession session = new StandardArooaSession();
		
		ArooaClass arooaClass;
		
		@Override
		public ArooaSession getSession() {
			return session;
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {
				@Override
				public ArooaClass getClassIdentifier() {
					return arooaClass;
				}
			};
		}
	}
	
	public void testSimpleSetAttribute() throws ArooaPropertyException {
		
		Apple apple = new Apple();
		
		MutableAttributes attrs = new MutableAttributes();
		attrs.set("colour", "red");
		
		MockInstanceConfiguration check = new MockInstanceConfiguration(
				new SimpleArooaClass(apple.getClass()), apple, attrs);
		
		OurContext context = new OurContext();
		context.arooaClass = new SimpleArooaClass(Apple.class);

		// session only needed with optional attributes.
		check.getAttributeSetter().init(context);
		
		assertEquals("colour", "red", apple.colour);
	}	
	
	public void testOptionalAttribute() {
		
		Banana banana = new Banana();
		
		MutableAttributes attrs = new MutableAttributes();
		attrs.set("colour", "red");
		attrs.set("curvature", "30");
		
		MockInstanceConfiguration check = new MockInstanceConfiguration(
				new SimpleArooaClass(banana.getClass()), 
				banana, attrs);
		
		check.getAttributeSetter().addOptionalAttribute("colour");
		
		OurContext context = new OurContext();
		context.arooaClass = new SimpleArooaClass(Banana.class);
		
		check.getAttributeSetter().init(context);
		
		assertEquals(30, banana.curvature);
	}
	
	public void testNoPropertyAttribute() {
		
		Banana banana = new Banana();
		
		MutableAttributes attrs = new MutableAttributes();
		attrs.set("colour", "red");
		attrs.set("curvature", "30");
		
		MockInstanceConfiguration check = new MockInstanceConfiguration(
				new SimpleArooaClass(banana.getClass()), 
				banana, attrs);
				
		OurContext context = new OurContext();
		context.arooaClass = new SimpleArooaClass(Banana.class);
		
		try {
			check.getAttributeSetter().init(context);
			fail("Should fail.");
		}
		catch (ArooaPropertyException e) {
			assertEquals("colour", e.getProperty());
		}
		
	}
}
