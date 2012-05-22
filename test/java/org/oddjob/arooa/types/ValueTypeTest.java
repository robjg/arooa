/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.types;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.DefaultConversionRegistry;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.convertlets.ArooaValueConvertlets;
import org.oddjob.arooa.convert.convertlets.StringConvertlets;
import org.oddjob.arooa.deploy.BeanDescriptorHelper;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.standard.StandardArooaDescriptor;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;

/**
 * Tests for ValueType.
 */
public class ValueTypeTest extends TestCase {

	/**
	 * Test first principles.
	 * 
	 * @throws Exception
	 */
	public void testText() throws Exception {
		ValueType test = new ValueType();
		test.setValue(new ArooaObject("Hello World"));
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new ValueType.Conversions().registerWith(registry);
		new ArooaObject.Conversions().registerWith(registry);
		
		final ArooaConverter converter = new DefaultConverter(registry);
		
		assertEquals("Text", "Hello World", 
				converter.convert(test, String.class));
		
		// sanity check on DefaultConverter
		assertEquals("Object", "Hello World", 
				converter.convert(test, Object.class));
	}

	/**
	 * Test some conversion. These are as much tests on DefaultConverter.
	 * 
	 * @throws Exception
	 */
	public void testConversions() throws Exception {
		ValueType test = new ValueType();
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new ValueType.Conversions().registerWith(registry);
		new ArooaObject.Conversions().registerWith(registry);
		new StringConvertlets().registerWith(registry);
		
		ArooaConverter converter = new DefaultConverter(registry);
		
		test.setValue(new ArooaObject("true"));
		assertTrue(InputStream.class.isInstance(
				converter.convert(test, InputStream.class)));
	}
	
	public void testAsNumbers() throws Exception {
		
		DefaultConverter converter = new DefaultConverter();
		
		ValueType test = new ValueType();
		test.setValue(new ArooaObject("127"));
		
		Byte b = converter.convert(test, Byte.class);
		assertEquals(127, b.byteValue());

		test.setValue(new ArooaObject("123.4"));
		Float f =  converter.convert(test, Float.class);
		
		assertEquals(new Float(123.4), f);
	}
	
	public void testAsObject() throws Exception {
		Object o = new Object();
		
		ValueType test = new ValueType();
		test.setValue(new ArooaObject(o));

		DefaultConverter converter = new DefaultConverter();
		
		Object result = converter.convert(test, Object.class);
		
		assertEquals(o, result);
	}

	class MockValueType implements ArooaValue {
		String value = "Apple";
	}
	
	class OurConvertlet implements Convertlet<MockValueType, String> {
		public String convert(MockValueType from) throws ConvertletException {
			return from.value;
		}
	}
	/**
	 * Test that when ValueType is being used as a reference
	 * to another ArooaValue, the correct conversions take
	 * place. 
	 * 
	 * @throws Exception
	 */
	public void testAsArooaValues() throws Exception {
		MockValueType inner = new MockValueType();
		
		ValueType test = new ValueType();
		test.setValue(inner);
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new ValueType.Conversions().registerWith(registry);
		registry.register(MockValueType.class, String.class, new OurConvertlet());

		DefaultConverter converter = new DefaultConverter(registry);
		
		MockValueType result1 = converter.convert(
				test, MockValueType.class);
		
		assertEquals(inner, result1);
		
		String result2 = converter.convert(
				test, String.class);
		
		assertEquals("Apple", result2);
	}
	
	
	public static class Foo {
		
	}
	
	public static class Container {
		Map<String, Object> map = new HashMap<String, Object>();
		public void setMap(String name, Object value) {
			this.map.put(name, value);
		}
	}

	public static class Root {
		Object[] sequential = new Object[3];
		
		@ArooaComponent
		public void setSequential(int index, Object value) {
			sequential[index] = value;
		}
	}
	
	/**
	 * Test that a value can be used as a reference within a map element.
	 *
	 */
	public void testRefWithinMapInOddjob() throws Exception {
		
		String xml = "<oddjob>" +
				"  <sequential>" +
				"    <bean id='foo' class='"+ Foo.class.getName() + "'/>" +
				"    <bean id='c' class='"+ Container.class.getName() + "'>" +
				"      <map>" +
				"        <value key='foo' value='${foo}'/>" +
				"      </map>" +
				"    </bean>" +
				"  </sequential>" +
				"</oddjob>";
				
		Root root = new Root();
		
		XMLConfiguration config = new XMLConfiguration("test", xml);
		StandardArooaParser parser = new StandardArooaParser(root, 
				new StandardArooaDescriptor());
		
		parser.parse(config);
		
		ArooaSession session = parser.getSession();
		
		Container c = session.getBeanRegistry().lookup(
				"c", Container.class);
		
		session.getComponentPool().configure(c);
		
		Object value = c.map.get("foo");
		
		assertNotNull(value);
		assertTrue(value instanceof Foo);		
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new ArooaValueConvertlets().registerWith(registry);
		ArooaConverter converter = new DefaultConverter(registry);
		
		Object conversion = converter.convert(value, Object.class); 

		assertTrue(conversion instanceof Foo);
	}
	
	public void testValueIsAnAttriubte() {

		StandardArooaSession session = new StandardArooaSession();
		ArooaDescriptor descriptor = session.getArooaDescriptor();
		
		ArooaBeanDescriptor beanDescriptor = descriptor.getBeanDescriptor(
				new SimpleArooaClass(ValueType.class),
				session.getTools().getPropertyAccessor());

		BeanDescriptorHelper sort = new BeanDescriptorHelper(beanDescriptor);
		
		assertEquals(ConfiguredHow.ATTRIBUTE, 
				sort.getConfiguredHow("value"));
	}
}
