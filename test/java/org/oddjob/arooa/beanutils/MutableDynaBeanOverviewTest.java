package org.oddjob.arooa.beanutils;

import org.junit.Test;

import org.junit.Assert;

import org.apache.commons.beanutils.LazyDynaMap;
import org.apache.commons.beanutils.MutableDynaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;

public class MutableDynaBeanOverviewTest extends Assert {

   @Test
	public void testEmpty() 
	throws ArooaPropertyException {
		
		LazyDynaMap dynaBean = new LazyDynaMap();
		
		MutableDynaBeanOverview test = new MutableDynaBeanOverview(
				(MutableDynaClass) dynaBean.getDynaClass());
		
		assertTrue(test.hasReadableProperty("fruit"));
		assertTrue(test.hasWriteableProperty("fruit"));
		assertFalse(test.isIndexed("fruit"));
		assertFalse(test.isMapped("fruit"));
		
		assertEquals(Object.class, test.getPropertyType("fruit"));

		String[] properties = test.getProperties();
		
		assertEquals(0, properties.length);
	}
	
   @Test
	public void testSimple() {
		
		LazyDynaMap dynaBean = new LazyDynaMap();
		
		MutableDynaBeanOverview test = new MutableDynaBeanOverview(
				(MutableDynaClass) dynaBean.getDynaClass());

		dynaBean.set("fruit", "Apple");
		
		assertTrue(test.hasReadableProperty("fruit"));
		assertTrue(test.hasWriteableProperty("fruit"));
		assertFalse(test.isIndexed("fruit"));
		assertFalse(test.isMapped("fruit"));
		
		assertEquals(String.class, test.getPropertyType("fruit"));

		String[] properties = test.getProperties();
		
		assertEquals(1, properties.length);
		assertEquals("fruit", properties[0]);
	}
	
   @Test
	public void testIndexed() {
		
		LazyDynaMap dynaBean = new LazyDynaMap();
		
		MutableDynaBeanOverview test = new MutableDynaBeanOverview(
				(MutableDynaClass) dynaBean.getDynaClass());
		
		dynaBean.set("fruit", 0, "Apple");
		
		assertTrue(test.hasReadableProperty("fruit"));
		assertTrue(test.hasWriteableProperty("fruit"));
		assertTrue(test.isIndexed("fruit"));
		assertFalse(test.isMapped("fruit"));
		
		// This is a bug - should be a String. The DynaProperty 
		// constructor should test for List type as well as Array.
		assertEquals(null, test.getPropertyType("fruit"));

		String[] properties = test.getProperties();
		
		assertEquals(1, properties.length);
		assertEquals("fruit", properties[0]);
	}
	
   @Test
	public void testMapped() {
		
		LazyDynaMap dynaBean = new LazyDynaMap();
		
		MutableDynaBeanOverview test = new MutableDynaBeanOverview(
				(MutableDynaClass) dynaBean.getDynaClass());
		
		dynaBean.set("fruit", "crunchy", "Apple");
		
		assertTrue(test.hasReadableProperty("fruit"));
		assertTrue(test.hasWriteableProperty("fruit"));
		assertFalse(test.isIndexed("fruit"));
		assertTrue(test.isMapped("fruit"));
		
		// Wrong - DynaBean doesn't check for Maps.		
		assertEquals(null, test.getPropertyType("fruit"));

		String[] properties = test.getProperties();
		
		assertEquals(1, properties.length);
		assertEquals("fruit", properties[0]);
	}
	
}
