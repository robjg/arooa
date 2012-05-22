package org.oddjob.arooa.parsing.interceptors;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockElementMappings;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.deploy.NoAnnotations;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class AllElementsOnePropertyInterceptorTest extends TestCase {

	
	public static class Snack {
		Fruit fruit;

		public Fruit getFruit() {
			return fruit;
		}

		public void setFruit(Fruit fruit) {
			this.fruit = fruit;
		}
	}

	public static interface Fruit {
		
	}
	
	
	public static class Apple implements Fruit {
		String colour;

		public String getColour() {
			return colour;
		}

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
		public ArooaBeanDescriptor getBeanDescriptor(
				ArooaClass arooaClass, PropertyAccessor accessor) {
			if (new SimpleArooaClass(Snack.class).equals(
					arooaClass)) {
				return new MockArooaBeanDescriptor() {
					@Override
					public ParsingInterceptor getParsingInterceptor() {
						OnePropertyInterceptor pi = new OnePropertyInterceptor();
						pi.setProperty("fruit");
						return pi;
					}
					@Override
					public String getComponentProperty() {
						return null;
					}
					@Override
					public ConfiguredHow getConfiguredHow(String property) {
						return ConfiguredHow.ELEMENT;
					}
					@Override
					public boolean isAuto(String property) {
						return false;
					}
					@Override
					public ArooaAnnotations getAnnotations() {
						return new NoAnnotations();
					}
				};
			}
			else if (new SimpleArooaClass(
					Apple.class).equals(arooaClass)) {
				return null;
			}
			else if (new SimpleArooaClass(
					Fruit.class).equals(arooaClass)) {
				return null;
			}
			throw new RuntimeException("Unexpected:" + arooaClass);
		}
		@Override
		public ElementMappings getElementMappings() {
			return new MappingsSwitch(null, new MockElementMappings() {
					@Override
					public ArooaClass mappingFor(ArooaElement element,
			    			InstantiationContext parentContext) {
						assertEquals("value element", "apple", 
								element.getTag());
						return new SimpleArooaClass(Apple.class);
					}
				});
		}
	}
	
	public void testInStandardParser() throws ArooaParseException {
		
		
		String xml = "<snack>" +
				"<apple colour='red'/>" +
				"</snack>";
		
		Snack snack = new Snack();
		
		StandardArooaParser parser = new StandardArooaParser(snack,
				new OurDescriptor());
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		parser.getSession().getComponentPool().configure(snack);
		
		assertNotNull(snack.getFruit());
		Apple apple = (Apple) snack.getFruit();
		assertEquals("red", apple.getColour());
	}
	
	public void testNoChild() throws ArooaParseException {
		
		
		String xml = "<snack/>";
		
		Snack snack = new Snack();
		
		StandardArooaParser parser = new StandardArooaParser(snack,
				new OurDescriptor());
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		assertNull(snack.getFruit());
	}
	
	private class IndexedDescriptor extends MockArooaDescriptor {
		
		@Override
		public ConversionProvider getConvertletProvider() {
			return null;
		}
		
		@Override
		public ArooaBeanDescriptor getBeanDescriptor(
				ArooaClass classIdentifier, PropertyAccessor accessor) {
			if (new SimpleArooaClass(
					IndexedSnack.class).equals(classIdentifier)) {
				return new MockArooaBeanDescriptor() {
					@Override
					public ParsingInterceptor getParsingInterceptor() {
						OnePropertyInterceptor pi = new OnePropertyInterceptor();
						pi.setProperty("fruit");
						return pi;
					}
					@Override
					public String getComponentProperty() {
						return null;
					}
					@Override
					public ConfiguredHow getConfiguredHow(String property) {
						return ConfiguredHow.ELEMENT;
					}
					@Override
					public boolean isAuto(String property) {
						return false;
					}
					@Override
					public ArooaAnnotations getAnnotations() {
						return new NoAnnotations();
					}
				};
			}
			else if (new SimpleArooaClass(
					Apple.class).equals(classIdentifier)) {
				return null;
			}
			else if (new SimpleArooaClass(
					Fruit.class).equals(classIdentifier)) {
				return null;
			}
			throw new RuntimeException("Unexpected");
		}
		
		@Override
		public ElementMappings getElementMappings() {
			return new MappingsSwitch(null, new MockElementMappings() {
					@Override
					public ArooaClass mappingFor(ArooaElement element,
			    			InstantiationContext parentContext) {
						assertEquals("value element", "apple", 
								element.getTag());
						return new SimpleArooaClass(Apple.class);
					}
				});
		}
	}

	public static class IndexedSnack {
		Fruit fruit;
		int index;

		public void setFruit(int index, Fruit fruit) {
			this.index = index;
			this.fruit = fruit;
		}
	}
	
	public void testIndexedProperty() throws ArooaParseException {
				
		String xml = "<snack>" +
				"<apple colour='red'/>" +
				"</snack>";
		
		IndexedSnack snack = new IndexedSnack();
		
		StandardArooaParser parser = new StandardArooaParser(snack,
				new IndexedDescriptor());
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		parser.getSession().getComponentPool().configure(snack);
		
		assertNotNull(snack.fruit);
		assertEquals(0, snack.index);
		Apple apple = (Apple) snack.fruit;
		assertEquals("red", apple.getColour());
	}
	
}
