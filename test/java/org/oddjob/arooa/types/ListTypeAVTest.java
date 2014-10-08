package org.oddjob.arooa.types;

import java.util.List;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.life.ClassLoaderClassResolver;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardFragmentParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class ListTypeAVTest extends TestCase {

	public interface Fruit {
		String getColour();
		
	}
	
	public static class FruitAV implements ArooaValue {
		String colour;
		
		public FruitAV() {
		}
		
		public FruitAV(String colour) {
			this.colour = colour;
		}
		
		public void setColour(String colour) {
			this.colour = colour;
		}
	}
	
	public class Conversions implements ConversionProvider {

		public void registerWith(ConversionRegistry registry) {
			registry.register(FruitAV.class, Fruit.class, 
					new Convertlet<FruitAV, Fruit>() {
				public Fruit convert(final FruitAV from) {
					return new Fruit() {
						public String getColour() {
							return from.colour;
						}
					};
				}
			});
			registry.register(FruitAV.class, String.class, 
					new Convertlet<FruitAV, String>() {
				public String convert(final FruitAV from) {
					return from.colour;
				}
			});
		}
	}

	private class OurDescriptor extends MockArooaDescriptor {
		
		@Override
		public ConversionProvider getConvertletProvider() {
			return new Conversions();
		}
		
		@Override
		public ElementMappings getElementMappings() {
			return null;
		}
		
		@Override
		public ArooaBeanDescriptor getBeanDescriptor(ArooaClass forClass,
				PropertyAccessor accessor) {
			return null;
		}
		
    	@Override
    	public ClassResolver getClassResolver() {
    		return new ClassLoaderClassResolver(
    				getClass().getClassLoader());
    	}
	}
	
	
	public void testStringArray() 
	throws ArooaParseException, NoConversionAvailableException, 
	ConversionFailedException {
		
		String xml= 
			" <list>" +
			"   <values>" +
			"    <bean class='" + FruitAV.class.getName() + "' colour='orange'/>" +
			"    <bean class='" + FruitAV.class.getName() + "' colour='red'/>" +
			"  </values>" +
			" </list>";
		
		StandardFragmentParser parser = new StandardFragmentParser(
						new OurDescriptor());
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		ListType test = (ListType) parser.getRoot();
		
		ArooaConverter converter = 
			parser.getSession().getTools().getArooaConverter();
		
		String[] results = converter.convert(test, String[].class);
		
		assertEquals(2, results.length);
		
		assertEquals("orange", results[0]);
		assertEquals("red", results[1]);
	}
	
	@SuppressWarnings("unchecked")
	public void testStringList() throws ArooaParseException, NoConversionAvailableException, ConversionFailedException {
		
		String xml= 
			" <list>" +
			"   <elementType>" +
			"    <class name='java.lang.String'/>" +
			"   </elementType>" +
			"   <values>" +
			"    <bean class='" + FruitAV.class.getName() + "' colour='orange'/>" +
			"    <bean class='" + FruitAV.class.getName() + "' colour='red'/>" +
			"  </values>" +
			" </list>";
		
		StandardFragmentParser parser = new StandardFragmentParser(
				new OurDescriptor());
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		ListType test = (ListType) parser.getRoot();
		
		ArooaConverter converter = 
			parser.getSession().getTools().getArooaConverter();
		
		List<String> results = converter.convert(test, List.class);
		
		assertEquals(2, results.size());
		
		assertEquals("orange", results.get(0));
		assertEquals("red", results.get(1));
	}
}
