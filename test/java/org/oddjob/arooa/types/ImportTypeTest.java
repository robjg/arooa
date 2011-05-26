package org.oddjob.arooa.types;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.registry.InvalidIdException;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class ImportTypeTest extends TestCase {

	public static class Apple {
		
		String colour;
		
		public void setColour(String colour) {
			this.colour = colour;
		}
	}
	
	public static class AppleArooa extends MockArooaBeanDescriptor {
		
		@Override
		public ConfiguredHow getConfiguredHow(String property) {
			return ConfiguredHow.ELEMENT;
		}
		
		@Override
		public ParsingInterceptor getParsingInterceptor() {
			return null;
		}
		
		@Override
		public String getComponentProperty() {
			return null;
		}
		
		@Override
		public boolean isAuto(String property) {
			return false;
		}
	}
	
	public void testXML() throws ArooaParseException, InvalidIdException {
		
		Apple root = new Apple();
		
		String xml = "<whatever>" +
				" <colour>" +
				"  <bean class='" + ImportType.class.getName() + "'" +
						" xml='${xml}'/>" +
				" </colour>" +
				"</whatever>";
		
		StandardArooaParser parser = new StandardArooaParser(root);

		parser.parse(new XMLConfiguration("TEST", xml));
		
		ArooaSession session = parser.getSession();
		
		String moreXML = "<value value='red'/>";
		
		session.getBeanRegistry().register("xml", moreXML);
		session.getComponentPool().configure(root);
		
		assertEquals("red", root.colour); 
	}
	
	public void testResource() throws ArooaParseException {
		
		Apple root = new Apple();
		
		String xml = "<whatever>" +
				" <colour>" +
				"  <import resource='org/oddjob/arooa/types/ImportTypeTest.xml'/>" +
				" </colour>" +
				"</whatever>";
		
		StandardArooaParser parser = new StandardArooaParser(root);

		parser.parse(new XMLConfiguration("TEST", xml));
		
		ArooaSession session = parser.getSession();
		
		session.getComponentPool().configure(root);
		
		assertEquals("red", root.colour); 
		
	}
	
	public void testNoResource() throws NoConversionAvailableException, ConversionFailedException {

		class OurSession extends MockArooaSession {
		};
		
		ImportType test = new ImportType();
		test.setArooaSession(new OurSession());
		test.setResource("IDontExist");

		ArooaConverter converter = new DefaultConverter();
		
		try {
			converter.convert(test, Object.class);
			fail("Should throw exception.");
		} catch (ArooaConversionException e) {
			// expected.
		}
		
	}
	
	public static class Snack {

		Object component;
		Apple apple;
		
		@ArooaComponent
		public void setComponent(Object component) {
			this.component = component;
		}
		
		public Object getComponent() {
			return component;
		}
		
		@ArooaAttribute
		public void setApple(Apple apple) {
			this.apple = apple;
		}
	}
	
	public void testComponent() throws ArooaParseException {
		
		Snack root = new Snack();
		
		String xml = "<snack id='this' apple='${this.component}'>" +
				" <component>" +
				"  <bean id='import' class='" + ImportType.class.getName() + "'" +
				"         resource='org/oddjob/arooa/types/ImportTypeTest2.xml'/>" +
				" </component>" +
				"</snack>";
		
		StandardArooaParser parser = new StandardArooaParser(root);

		parser.parse(new XMLConfiguration("TEST", xml));
		
		ArooaSession session = parser.getSession();
		
		// No conversion
		assertEquals(ImportType.class, root.component.getClass()); 
		assertNull(root.apple);
		
		ComponentPool pool = session.getComponentPool(); 
		
		pool.configure(root);
		
		assertEquals("red", root.apple.colour); 
		
	}
}
