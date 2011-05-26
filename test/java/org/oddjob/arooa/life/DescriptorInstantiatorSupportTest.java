package org.oddjob.arooa.life;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.DefaultConversionProvider;
import org.oddjob.arooa.convert.DefaultConversionRegistry;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.deploy.ArooaDescriptorBean;
import org.oddjob.arooa.deploy.BeanDefinition;
import org.oddjob.arooa.parsing.ArooaElement;

public class DescriptorInstantiatorSupportTest extends TestCase {

	
	public interface Fruit {
		
	}
	
	public static class Apple implements Fruit {
		
		public void setRotten(boolean rotten) {
			
		}
	}
	
	ArooaDescriptor descriptor;
	
	@Override
	protected void setUp() throws Exception {
		ArooaDescriptorBean df = new ArooaDescriptorBean();
				
		BeanDefinition def = new BeanDefinition();
		
		def.setClassName(Apple.class.getName());
		def.setElement("apple");
		
		df.setValues(0, def);
		
		descriptor = df.createDescriptor(getClass().getClassLoader());
	}
		
	public void testSimpleTypes() {
			
		ElementMappings test = 
			descriptor.getElementMappings();

		ArooaConverter converter = new DefaultConverter();
		
		ArooaElement[] result;

		result = test.elementsFor(
				new InstantiationContext(ArooaType.VALUE,
						new SimpleArooaClass(Apple.class),
						converter));

		assertEquals(new ArooaElement("apple"), result[0]);
		
		result = test.elementsFor(
				new InstantiationContext(ArooaType.VALUE,
						new SimpleArooaClass(Fruit.class),
						converter));

		assertEquals(new ArooaElement("apple"), result[0]);
		
		result = test.elementsFor(
				new InstantiationContext(ArooaType.VALUE,
						new SimpleArooaClass(String.class),
						converter));
		
		assertEquals(new ArooaElement("apple"), result[0]);
		
		result = test.elementsFor(
				new InstantiationContext(ArooaType.VALUE,
						new SimpleArooaClass(File.class),
						converter));
		
		assertEquals(0, result.length);		
	}
	
	public static class Conversions implements ConversionProvider { 
		public void registerWith(ConversionRegistry registry) {
			registry.register(Apple.class, URL.class, 
					new Convertlet<Apple, URL>() {
				public URL convert(Apple from)
						throws ConvertletException {
					return null;
				}
			});
			registry.register(Apple.class, URL[].class, 
					new Convertlet<Apple, URL[]>() {
				public URL[] convert(Apple from)
						throws ConvertletException {
					return null;
				}
			});
		}
	}
	
	public void testArooaValue() {
		
		ElementMappings test = 
				descriptor.getElementMappings();
				
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		new Conversions().registerWith(registry);
		
		ArooaConverter converter = new DefaultConverter(registry);
		
		ArooaElement[] result;

		result = test.elementsFor(
				new InstantiationContext(ArooaType.VALUE,
						new SimpleArooaClass(URL.class),
						converter));

		assertEquals(new ArooaElement("apple"), result[0]);
		
		result = test.elementsFor(
				new InstantiationContext(ArooaType.VALUE,
						new SimpleArooaClass(URL[].class),
						converter));

		assertEquals(new ArooaElement("apple"), result[0]);

		result = test.elementsFor(
				new InstantiationContext(ArooaType.VALUE,
						new SimpleArooaClass(Object[].class),
						converter));

		assertEquals(new ArooaElement("apple"), result[0]);
		
		result = test.elementsFor(
				new InstantiationContext(ArooaType.VALUE,
						new SimpleArooaClass(String.class),
						converter));

		assertEquals(new ArooaElement("apple"), result[0]);
		
		result = test.elementsFor(
				new InstantiationContext(ArooaType.VALUE,
						new SimpleArooaClass(InputStream.class),
						converter));

		assertEquals(new ArooaElement("apple"), result[0]);
		
		result = test.elementsFor(
				new InstantiationContext(ArooaType.VALUE,
						new SimpleArooaClass(Number.class)));

		assertEquals(0, result.length);
		
		result = test.elementsFor(
				new InstantiationContext(ArooaType.VALUE,
						new SimpleArooaClass(ArooaValue.class),
						converter));

		assertEquals(new ArooaElement("apple"), result[0]);
		
		result = test.elementsFor(
				new InstantiationContext(ArooaType.VALUE,
						new SimpleArooaClass(Apple.class),
						converter));

		assertEquals(new ArooaElement("apple"), result[0]);
	}
	
}
