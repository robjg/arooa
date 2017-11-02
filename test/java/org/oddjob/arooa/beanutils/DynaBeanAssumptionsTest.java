package org.oddjob.arooa.beanutils;

import org.junit.Test;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;

import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.LazyDynaMap;
import org.apache.commons.beanutils.PropertyUtils;

public class DynaBeanAssumptionsTest extends Assert {

   @Test
	public void testDescrptors1() {
		
		PropertyDescriptor[] descriptors =
			PropertyUtils.getPropertyDescriptors(LazyDynaMap.class);
		
		assertEquals(7, descriptors.length);
		
		Set<String> names = new HashSet<String>();
		for (PropertyDescriptor descriptor: descriptors) {
			names.add(descriptor.getName());
		}

		assertTrue(names.contains("class"));
		assertTrue(names.contains("dynaClass"));
		assertTrue(names.contains("dynaProperties"));
		assertTrue(names.contains("map"));
		assertTrue(names.contains("name"));
		assertTrue(names.contains("restricted"));
		assertTrue(names.contains("returnNull"));

	}

   @Test
	public void testDynaProperties() {

		LazyDynaMap dynaBean = new LazyDynaMap();
		
		assertTrue(PropertyUtils.isReadable(dynaBean, "fruit"));
		assertTrue(PropertyUtils.isWriteable(dynaBean, "fruit"));
		
		DynaProperty[] dynaProperties = dynaBean.getDynaClass().getDynaProperties();
		
		assertEquals(0, dynaProperties.length);
		
		DynaProperty dynaProperty = dynaBean.getDynaClass().getDynaProperty("fruit"); 
		
		assertEquals(Object.class, dynaProperty.getType());
		assertFalse(dynaProperty.isIndexed());
		assertFalse(dynaProperty.isMapped());
	}

   @Test
	public void testSimpleProperties() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		LazyDynaMap dynaBean = new LazyDynaMap();
		
		assertEquals(null, PropertyUtils.getProperty(dynaBean, "fruit"));
		
		dynaBean.set("fruit", "Apple");
		
		assertEquals("Apple", PropertyUtils.getProperty(dynaBean, "fruit"));
		
		DynaProperty[] dynaProperties = dynaBean.getDynaProperties();
		
		assertEquals(1, dynaProperties.length);
		
		assertEquals("fruit", dynaProperties[0].getName());
		assertFalse(dynaProperties[0].isIndexed());
		assertFalse(dynaProperties[0].isMapped());
		assertEquals(String.class, dynaProperties[0].getType());
		assertNull(dynaProperties[0].getContentType());
		
	}
	
   @Test
	public void testIndexedProperties() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		LazyDynaMap dynaBean = new LazyDynaMap();
		
		dynaBean.set("fruit", 0, "Apple");
		
		assertEquals("Apple", PropertyUtils.getProperty(dynaBean, "fruit[0]"));
		
		DynaProperty[] dynaProperties = dynaBean.getDynaProperties();
		
		assertEquals(1, dynaProperties.length);
		
		assertEquals("fruit", dynaProperties[0].getName());
		assertTrue(dynaProperties[0].isIndexed());
		assertFalse(dynaProperties[0].isMapped());
		assertEquals(ArrayList.class, dynaProperties[0].getType());
		assertNull(dynaProperties[0].getContentType());
	}
	
   @Test
	public void testMappedProperties() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		LazyDynaMap dynaBean = new LazyDynaMap();
		
		dynaBean.set("fruit", "crunchy", "Apple");
		
		assertEquals("Apple", PropertyUtils.getProperty(dynaBean, "fruit(crunchy)"));

		DynaProperty[] dynaProperties = dynaBean.getDynaProperties();
		
		assertEquals(1, dynaProperties.length);
		
		assertEquals("fruit", dynaProperties[0].getName());
		assertFalse(dynaProperties[0].isIndexed());
		assertTrue(dynaProperties[0].isMapped());
		assertEquals(HashMap.class, dynaProperties[0].getType());
		assertNull(dynaProperties[0].getContentType());
	}
}
