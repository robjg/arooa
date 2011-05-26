package org.oddjob.arooa.beanutils;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.deploy.ArooaDescriptorDescriptorFactory;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.standard.StandardFragmentParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class MagicBeanDescriptorFactoryTest extends TestCase {
	
	public static class Stuff {
		
		Object person;

		public Object getPerson() {
			return person;
		}

		public void setPerson(Object person) {
			this.person = person;
		}
	}

	private void testSimpleShared(String definition) throws ArooaParseException, ArooaPropertyException, ArooaConversionException {
		
		String xml = 
			"<test id='t' xmlns:magic='http://rgordon.co.uk/oddjob/magic'>" +
			" <person>" +
			"  <magic:person name='John' age='22'/>" +
			" </person>" +
			"</test>";

		
		StandardFragmentParser parser = 
			new StandardFragmentParser(
					new ArooaDescriptorDescriptorFactory().createDescriptor(
							getClass().getClassLoader()));
		
		parser.parse(new XMLConfiguration("XML", definition));
		
		MagicBeanDescriptorFactory mbdf = 
			(MagicBeanDescriptorFactory) parser.getRoot();
		
		parser.getSession().getComponentPool().configure(mbdf);
		
		ArooaDescriptor descriptor = mbdf.createDescriptor(
				getClass().getClassLoader());
		
		Stuff stuff = new Stuff();
		
		StandardArooaParser parser2 = new StandardArooaParser(
				stuff, descriptor);
		
		parser2.parse(new XMLConfiguration("XML", xml));
		
		ArooaSession session = parser2.getSession();

		session.getComponentPool().configure(stuff);
		
		int age = session.getBeanRegistry().lookup(
				"t.person.age", int.class);
		String name = session.getBeanRegistry().lookup(
				"t.person.name", String.class);
		
		assertEquals(22, age);
		assertEquals("John", name);
	}
	
	public void testSimple() throws ArooaParseException, ArooaPropertyException, ArooaConversionException {
		
		String magic =
			"<bean class='org.oddjob.arooa.beanutils.MagicBeanDescriptorFactory'>" +
			" <definitions>" +
			"  <is name='person'>" +
			"   <properties>" +
			"    <is name='name' type='java.lang.String'/>" +
			"    <is name='age' type='java.lang.Integer'/>" +
			"   </properties>" +
			"  </is>" +
			" </definitions>" +
			"</bean>";
		
		testSimpleShared(magic);
	}
	
	public void testSimpleWithMagicElements() throws ArooaParseException, ArooaPropertyException, ArooaConversionException {
		
		String magic =
			"<arooa:magic-beans xmlns:arooa='http://rgordon.co.uk/oddjob/arooa'>" +
			" <definitions>" +
			"  <arooa:magic-bean name='person'>" +
			"   <properties>" +
			"    <arooa:magic-property name='name' type='java.lang.String'/>" +
			"    <arooa:magic-property name='age' type='java.lang.Integer'/>" +
			"   </properties>" +
			"  </arooa:magic-bean>" +
			" </definitions>" +
			"</arooa:magic-beans>";
		
		testSimpleShared(magic);
	}
	
	public void testWithPropertyAccessor() 
	throws IllegalAccessException, InstantiationException {
		
		MagicBeanDefinition def = new MagicBeanDefinition();
		def.setName("OurMagicBean");

		MagicBeanProperty prop1 = new MagicBeanProperty();
		prop1.setName("fruit");
		prop1.setType(String.class.getName());
		
		MagicBeanProperty prop2 = new MagicBeanProperty();
		prop2.setName("quantity");
		prop2.setType(Integer.class.getName());
		
		def.setProperties(0, prop1);
		def.setProperties(1, prop2);
		
		ArooaClass cl = def.createMagic(this.getClass().getClassLoader());
		
		Object test = cl.newInstance();
		
		PropertyAccessor accessor = 
			new BeanUtilsPropertyAccessor().accessorWithConversions(new DefaultConverter());
		
		accessor.setProperty(test, "fruit", "Apple");
		accessor.setProperty(test, "quantity", "5");
		
		assertEquals("Apple", accessor.getProperty(test, "fruit"));
		assertEquals(new Integer(5), accessor.getProperty(test, "quantity"));
		
	}
	
	public void testClassIdentifier() throws IllegalAccessException, InstantiationException {
		
		MagicBeanDefinition def = new MagicBeanDefinition();
		def.setName("OurMagicBean");
		
		MagicBeanProperty prop1 = new MagicBeanProperty();
		prop1.setName("fruit");
		prop1.setType(String.class.getName());
		
		MagicBeanProperty prop2 = new MagicBeanProperty();
		prop2.setName("quantity");
		prop2.setType(Integer.class.getName());
		
		def.setProperties(0, prop1);
		def.setProperties(1, prop2);
		
		ArooaClass cl = def.createMagic(this.getClass().getClassLoader());
		
		Object test = cl.newInstance();
		
		PropertyAccessor accessor = new BeanUtilsPropertyAccessor();

		ArooaClass identifier = accessor.getClassName(test);
		
		BeanOverview overview = identifier.getBeanOverview(accessor);

		assertEquals(DynaBeanOverview.class, overview.getClass());
		
		assertEquals(true, overview.hasReadableProperty("fruit"));
		assertEquals(true, overview.hasWriteableProperty("fruit"));
		
		assertEquals(true, overview.hasReadableProperty("quantity"));
		assertEquals(true, overview.hasWriteableProperty("quantity"));
		
		assertEquals(false, overview.hasReadableProperty("stuff"));
		assertEquals(false, overview.hasWriteableProperty("stuff"));
	}
}
