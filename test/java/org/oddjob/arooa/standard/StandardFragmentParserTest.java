package org.oddjob.arooa.standard;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockElementMappings;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.xml.XMLConfiguration;

public class StandardFragmentParserTest extends TestCase {

	public static class Snack {
		String type;
		
		public void setType(String type) {
			this.type = type;
		}
	}
	
	public static class Apple {
		String colour;
		
		public void setColour(String colour) {
			this.colour = colour;
		}
	}
	
	private class OurDescriptor extends MockArooaDescriptor {

		@Override
		public ConversionProvider getConvertletProvider() {
			return null;
		}
		
		@Override
		public ElementMappings getElementMappings() {
			return new MappingsSwitch(
				new MockElementMappings() {
					@Override
					public ArooaClass mappingFor(ArooaElement element,
			    			InstantiationContext parentContext) {
						assertEquals("snack", element.getTag());
						return new SimpleArooaClass(Snack.class);
					}
				},
				new MockElementMappings() {
					@Override
					public ArooaClass mappingFor(ArooaElement element,
			    			InstantiationContext parentContext) {
						assertEquals("apple", element.getTag());
						return new SimpleArooaClass(Apple.class);
					}
				});
		}
		
		@Override
		public ArooaBeanDescriptor getBeanDescriptor(
				ArooaClass classIdentifier, PropertyAccessor accessor) {
			return null;
		}
	}
	
	public void testValueFragment() throws ArooaParseException {
		
		String xml = "<apple colour='red'/>";
		
		ArooaSession session = new StandardArooaSession(
				new OurDescriptor());
		
		StandardFragmentParser parser = new StandardFragmentParser(session);
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		Object result = parser.getRoot();
		
		assertEquals(Apple.class, result.getClass());
		
		Apple apple = (Apple) result;
		
		assertEquals("red", apple.colour);
	}
	
	public void testComponentFragment() throws ArooaParseException {
		
		String xml = "<snack type='healthy'/>";

		StandardFragmentParser parser = new StandardFragmentParser(
				new OurDescriptor());
		parser.setArooaType(ArooaType.COMPONENT);
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		Object result = parser.getRoot();
		
		assertEquals(Snack.class, result.getClass());
		
		Snack snack = (Snack) result;
		
		assertEquals("healthy", snack.type);
	}
}
