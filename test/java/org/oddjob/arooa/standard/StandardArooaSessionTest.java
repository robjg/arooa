package org.oddjob.arooa.standard;

import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockElementMappings;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.deploy.ArooaDescriptorDescriptor;
import org.oddjob.arooa.deploy.ArooaDescriptorFactory;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.arooa.types.ValueType;
import org.oddjob.arooa.xml.XMLConfiguration;

public class StandardArooaSessionTest extends TestCase {

	public void testNoDescriptor() {
		
		final StandardArooaSession test = new StandardArooaSession();
		
		ComponentPool componentPool = test.getComponentPool();
		
		assertNotNull(componentPool);
		
		ArooaDescriptor descriptor = test.getArooaDescriptor();
		
		assertNotNull(descriptor);
		
		ArooaTools tools = test.getTools();
		
		assertNotNull(tools);
		
		ArooaElement element = new ArooaElement("bean");

		ArooaClass arooaClass = descriptor.getElementMappings().mappingFor(element, 
						new InstantiationContext(ArooaType.VALUE, null,
								descriptor.getClassResolver()));
		
		assertEquals(Object.class, arooaClass.forClass());
	}
	
	public void testSimpleConversion() throws NoConversionAvailableException, ConversionFailedException {
	
		StandardArooaSession test = new StandardArooaSession();
		
		ArooaConverter converter = test.getTools().getArooaConverter();
		
		ValueType value = new ValueType();
		value.setValue(new ArooaObject("Apple"));
		
		String result = converter.convert(value, String.class);
		
		assertEquals("Apple", result);
	}
	
	
	private class OurDescriptor extends MockArooaDescriptor {
		
		@Override
		public ConversionProvider getConvertletProvider() {
			return new ConversionProvider() {
				
				@Override
				public void registerWith(ConversionRegistry registry) {
					registry.register(String.class, Integer.class, 
							new Convertlet<String, Integer>() {
						@Override
						public Integer convert(String from)
								throws ConvertletException {
							return new Integer(42);
						}
					});
				}
			};
		}
		
		@Override
		public ElementMappings getElementMappings() {
			return new MappingsSwitch(null, new MockElementMappings() {
					@Override
					public ArooaClass mappingFor(ArooaElement element, 
							InstantiationContext parentContext) {
						return new SimpleArooaClass(Number.class);
					}
				});
		}
	}
	
	public void testWithDescriptor() 
	throws NoConversionAvailableException, ConversionFailedException {
		
		final StandardArooaSession test = new StandardArooaSession(
				new OurDescriptor());
		
		ComponentPool componentPool = test.getComponentPool();
		
		assertNotNull(componentPool);
		
		ArooaDescriptor descriptor = test.getArooaDescriptor();
		
		assertNotNull(descriptor);
		
		ArooaTools tools = test.getTools();
		
		assertNotNull(tools);
		
		ArooaElement element = new ArooaElement("class");

		ArooaClass arooaClass = descriptor.getElementMappings(
				).mappingFor(element, new InstantiationContext(
						ArooaType.VALUE, null));
		
		assertEquals(Number.class, arooaClass.forClass());
		
		Integer converted = test.getTools().getArooaConverter(
				).convert("Hello", Integer.class);
		
		assertEquals(new Integer(42), converted);
	}
	
	public static class Snack {
		
		private Fruit fruit;

		private Snack subSnack;
		
		public Fruit getFruit() {
			return fruit;
		}

		public void setFruit(Fruit fruit) {
			this.fruit = fruit;
		}

		public Snack getSubSnack() {
			return subSnack;
		}

		public void setSubSnack(Snack subSnack) {
			this.subSnack = subSnack;
		}
	}

	public static class Fruit {

	}
	
	
	String EOL = System.getProperty("line.separator");
	
	String descriptor =
		"<arooa:descriptor xmlns:arooa='http://rgordon.co.uk/oddjob/arooa'>" + EOL +
		"  <values>" + EOL +
		"        <arooa:bean-def element='fruit' className='" + Fruit.class.getName() + "'/>" + EOL +
		"  </values>" + EOL +
		"  <components>" + EOL +
		"        <arooa:bean-def element='snack' className='" + Snack.class.getName() + "'>" + EOL + 
		"          <properties>" +
		"            <arooa:property name='subSnack' type='COMPONENT'/>" +
		"          </properties>" +
		"        </arooa:bean-def>" + EOL + 
		"  </components>" + EOL +
		"</arooa:descriptor>" + EOL;

	public void testWithRealDescriptor() throws URISyntaxException, ArooaParseException {

		StandardFragmentParser descriptorParser = new StandardFragmentParser(
						new ArooaDescriptorDescriptor());
		
		descriptorParser.parse(new XMLConfiguration("TEST", descriptor));
		
		ArooaDescriptorFactory descriptorFactory = 
			(ArooaDescriptorFactory) descriptorParser.getRoot();
		
		ArooaDescriptor ourDescriptor = descriptorFactory.createDescriptor(
				getClass().getClassLoader());
				
		StandardArooaSession session = new StandardArooaSession(ourDescriptor);
		
		ArooaDescriptor descriptor = session.getArooaDescriptor();
		
		ArooaClass snack = descriptor.getElementMappings().mappingFor(
						new ArooaElement("snack"), 
						new InstantiationContext(ArooaType.COMPONENT, null));
		
		assertEquals(Snack.class, snack.forClass());
				
		PropertyAccessor accessor = session.getTools().getPropertyAccessor();
		
		BeanOverview overview = snack.getBeanOverview(accessor);
		
		assertTrue(overview.hasWriteableProperty("fruit"));
		assertTrue(overview.hasWriteableProperty("subSnack"));
		assertEquals(Fruit.class, overview.getPropertyType("fruit"));
		
		ArooaBeanDescriptor beanDescriptor = descriptor.getBeanDescriptor(
				snack, accessor);
		
		assertEquals("subSnack", beanDescriptor.getComponentProperty());
	}

}
