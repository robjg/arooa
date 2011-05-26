/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.deploy;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.standard.ExtendedTools;
import org.oddjob.arooa.standard.StandardTools;

/**
 * 
 */
abstract public class ArooaDescriptorTestBase extends TestCase {

	public static class Week {
		
	}
	
	public static class Monday {
		
	}
	
	public static class Tuesday {
		
	}
	
	public static class HealthySnack {
		
	}
	
	abstract ArooaDescriptor getTest(ClassLoader loader);
	
	public void testMappings() throws ArooaParseException, URISyntaxException {
		
		
		ElementMappings mapping = 
			getTest(getClass().getClassLoader()).getElementMappings();
		
		ArooaClass result = mapping.mappingFor(
				new ArooaElement(
						new URI("http://rgordon.co.uk/test"), "tuesday"),
				new InstantiationContext(ArooaType.COMPONENT, null));
		
		assertEquals(Tuesday.class,
				result.forClass());
	}

	public void testBeanDescriptor() {

		ArooaDescriptor test = getTest(getClass().getClassLoader());
		
		ArooaBeanDescriptor appleDescriptor =
			test.getBeanDescriptor(
					new SimpleArooaClass(Apple.class), 
					new BeanUtilsPropertyAccessor());
		
		assertEquals("description", 
				new BeanDescriptorHelper(appleDescriptor).getTextProperty());

		ArooaBeanDescriptor weekDescriptor = 
			test.getBeanDescriptor(
					new SimpleArooaClass(Week.class),
					new BeanUtilsPropertyAccessor());
			
		assertEquals("days", 
				new BeanDescriptorHelper(weekDescriptor).getComponentProperty());
		
		assertNull(test.getBeanDescriptor(
				new SimpleArooaClass(Orange.class),
				new BeanUtilsPropertyAccessor()));
	}
	
	public void testElements() throws URISyntaxException {
		
		ArooaElement[] elements = getTest(
				getClass().getClassLoader()).getElementMappings().elementsFor(
						new InstantiationContext(ArooaType.VALUE, 
						new SimpleArooaClass(Object.class)));
		
		assertEquals(new ArooaElement(new URI("http://rgordon.co.uk/test"), 
				"snack"), elements[0]);
		assertEquals(new ArooaElement(new URI("http://rgordon.co.uk/test"), 
				"orange"), elements[1]);
		assertEquals(2, elements.length);
		
		elements = getTest(
				getClass().getClassLoader()).getElementMappings().elementsFor(
						new InstantiationContext(ArooaType.COMPONENT, 
						new SimpleArooaClass(Object.class)));
		
		assertEquals(3, elements.length);
		
	}
	
	public static class Apple implements ArooaValue {

	}
	
	public static class Orange {}
	
	public static class FruitConversions implements ConversionProvider {
		public void registerWith(ConversionRegistry registry) {
			registry.register(Apple.class, Orange.class, 
					new Convertlet<Apple, Orange>() {
				public Orange convert(Apple from)
						throws ConvertletException {
					return new Orange();
				}
			});
		}
	}
	
	public void testConversions() throws NoConversionAvailableException, ConversionFailedException {
		
		ArooaTools tools = new ExtendedTools(new StandardTools(),
				getTest(getClass().getClassLoader()));
		
		Object result = tools.getArooaConverter().convert(
				new Apple(), Orange.class);
		
		assertTrue(result instanceof Orange);
	}
}