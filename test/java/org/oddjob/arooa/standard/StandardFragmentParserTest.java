package org.oddjob.arooa.standard;

import javax.inject.Inject;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockElementMappings;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.life.ArooaLifeAware;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.ServiceProvider;
import org.oddjob.arooa.registry.Services;
import org.oddjob.arooa.xml.XMLConfiguration;

public class StandardFragmentParserTest extends TestCase {

	public static class Snack implements ArooaLifeAware {
		String type;
		
		boolean initialised = false;
		boolean configured = false;
		boolean destroy = false;
		
		public void setType(String type) {
			this.type = type;
		}
		
		@Override
		public void initialised() {
			initialised = true;
		}
		
		@Override
		public void configured() {
			configured = true;
		}
		
		@Override
		public void destroy() {
			destroy = true;
		}
	}
	
	public static class Apple implements ArooaLifeAware {
		String colour;
		
		boolean initialised = false;
		boolean configured = false;
		boolean destroy = false;

		@Inject
		public void setColour(String colour) {
			this.colour = colour;
		}
		
		@Override
		public void initialised() {
			initialised = true;
		}
		
		@Override
		public void configured() {
			configured = true;
		}
		
		@Override
		public void destroy() {
			destroy = true;
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
		
		ConfigurationHandle handle = parser.parse(
				new XMLConfiguration("TEST", xml));
		
		Object result = parser.getRoot();
		
		assertEquals(Apple.class, result.getClass());
		
		Apple apple = (Apple) result;
		
		assertEquals("red", apple.colour);
		
		assertEquals(true, apple.initialised);
		assertEquals(true, apple.configured);
		assertEquals(false, apple.destroy);
		
		handle.getDocumentContext().getRuntime().destroy();
		
		assertEquals(true, apple.destroy);
	}
	
	public void testComponentFragment() throws ArooaParseException {
		
		String xml = "<snack type='healthy'/>";

		StandardFragmentParser parser = new StandardFragmentParser(
				new OurDescriptor());
		parser.setArooaType(ArooaType.COMPONENT);
		
		ConfigurationHandle handle = parser.parse(
				new XMLConfiguration("TEST", xml));
		
		Object result = parser.getRoot();
		
		assertEquals(Snack.class, result.getClass());
		
		Snack snack = (Snack) result;
		
		assertEquals("healthy", snack.type);
		
		assertEquals(true, snack.initialised);
		assertEquals(true, snack.configured);
		assertEquals(false, snack.destroy);
		
		handle.getDocumentContext().getRuntime().destroy();
		
		assertEquals(true, snack.destroy);
	}
	
	private class OurServiceProvider implements ServiceProvider {
		
		@Override
		public Services getServices() {
			return new Services() {
				@Override
				public Object getService(String serviceName)
						throws IllegalArgumentException {
					assertEquals(serviceName, "colour-service");
					return "red";
				}
				
				@Override
				public String serviceNameFor(Class<?> theClass, String flavour) {
					assertEquals(String.class, theClass);
					return "colour-service";
				}
			};
		}		
	}
	
	public void testAutoConfigure() throws ArooaParseException {
		
		String xml = "<apple/>";
		
		ArooaSession session = new StandardArooaSession(
				new OurDescriptor());
		
		session.getBeanRegistry().register("colours", new OurServiceProvider());
		
		StandardFragmentParser parser = new StandardFragmentParser(session);
		
		parser.parse(
				new XMLConfiguration("TEST", xml));
		
		Apple apple = (Apple) parser.getRoot();
		
		assertEquals("red", apple.colour);
		
	}

	
}
