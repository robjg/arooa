package org.oddjob.arooa.beanutils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.log4j.Logger;
import org.oddjob.arooa.reflect.ArooaNoPropertyException;
import org.oddjob.arooa.reflect.ArooaPropertyException;

public class DynaBeanBeanOverviewTest extends TestCase {

	private static final Logger logger = Logger.getLogger(
			DynaBeanBeanOverviewTest.class);
	
	class OurDynaClass implements DynaClass {

		private final Map<String, DynaProperty> props = 
			new LinkedHashMap<String, DynaProperty>();
		
		{
			props.put("simple", new DynaProperty("simple", String.class));
			props.put("indexed", new DynaProperty("indexed", List.class, String.class));
			props.put("mapped", new DynaProperty("mapped", Map.class, String.class));
		}
		
		public DynaProperty[] getDynaProperties() {
			
			return props.values().toArray(new DynaProperty[0]);
		}
		
		public DynaProperty getDynaProperty(String arg0) {
			return props.get(arg0);
		}
		
		public String getName() {
			throw new RuntimeException("Unexpected.");
		}
		
		public DynaBean newInstance() throws IllegalAccessException,
				InstantiationException {
			throw new RuntimeException("Unexpected.");
		}
	}
	
	
	
	public void testNoProperty() {
		
		DynaBeanOverview test = new DynaBeanOverview(
				new OurDynaClass());
		
		assertFalse(test.hasReadableProperty("fruit"));
		assertFalse(test.hasWriteableProperty("fruit"));
		
		try {
			test.isIndexed("fruit");
			fail("No property expected.");
		} catch (ArooaNoPropertyException e) {
			// expected
		}
		
		try {
			test.isMapped("fruit");
			fail("No property expected.");
		} catch (ArooaNoPropertyException e) {
			// expected
		}

		try {
			test.getPropertyType("fruit");
			fail("No property expected.");
		} catch (ArooaNoPropertyException e) {
			// expected
		}
		
	}
	
	public void testProperties() {
		
		DynaBeanOverview test = new DynaBeanOverview(
				new OurDynaClass());
				
		String[] properties = test.getProperties();

		for (String p: properties) {
			logger.info("Property: " + p);
		}
		assertEquals(3, properties.length);
		assertEquals("simple", properties[0]);
		assertEquals("indexed", properties[1]);
		assertEquals("mapped", properties[2]);		
	}
	
	public void testSimple() 
	throws ArooaPropertyException {
		
		DynaBeanOverview test = new DynaBeanOverview(
				new OurDynaClass());
		
		assertTrue(test.hasReadableProperty("simple"));
		assertTrue(test.hasWriteableProperty("simple"));
		assertFalse(test.isIndexed("simple"));
		assertFalse(test.isMapped("simple"));
		
		assertEquals(String.class, test.getPropertyType("simple"));

	}
	
	public void testIndexed() {
		
		DynaBeanOverview test = new DynaBeanOverview(
				new OurDynaClass());
		
		assertTrue(test.hasReadableProperty("indexed"));
		assertTrue(test.hasWriteableProperty("indexed"));
		assertTrue(test.isIndexed("indexed"));
		assertFalse(test.isMapped("indexed"));
		
		assertEquals(String.class, test.getPropertyType("indexed"));

	}
	
	public void testMapped() {
		
		DynaBeanOverview test = new DynaBeanOverview(
				new OurDynaClass());
		
		assertTrue(test.hasReadableProperty("mapped"));
		assertTrue(test.hasWriteableProperty("mapped"));
		assertFalse(test.isIndexed("mapped"));
		assertTrue(test.isMapped("mapped"));
		
		assertEquals(String.class, test.getPropertyType("mapped"));
	}
	
}
