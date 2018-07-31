package org.oddjob.arooa.standard;
import org.junit.Before;

import org.junit.Test;

import java.util.Properties;

import org.junit.Assert;

public class StandardPropertyManagerTest extends Assert {

	Properties someProperties;
	
	Properties otherProperties;
	
    @Before
    public void setUp() throws Exception {

		
		someProperties = new Properties();
		
		someProperties.setProperty("snack.fruit", "apple");
		someProperties.setProperty("java.version", "-1");
		
		otherProperties = new Properties();
		
		otherProperties.setProperty("snack.fruit", "orange");
	}
	
   @Test
	public void testNoParentNoProperties() {
		
		StandardPropertyManager test = new StandardPropertyManager();
		
		test.addPropertyLookup(new StandardPropertyLookup(
				someProperties, "someProperties"));
		test.addPropertyLookup(new StandardPropertyLookup(
				otherProperties, "otherProperties"));
		
		assertEquals(System.getProperty("java.version"), 
				test.lookup("java.version"));
		
		assertEquals("apple", 
				test.lookup("snack.fruit"));
	}
	
   @Test
	public void testPropertiesNoParent() {
		
		StandardPropertyManager test = new StandardPropertyManager(
				someProperties, "someProperties");
		
		test.addPropertyLookup(new StandardPropertyLookup(
				otherProperties, "otherProperties"));
		
		assertEquals(System.getProperty("java.version"), 
				test.lookup("java.version"));
		
		assertEquals("apple", 
				test.lookup("snack.fruit"));
	}
	
   @Test
	public void testPropertiesAndParent() {
		
		StandardPropertyManager parent = new StandardPropertyManager(
				someProperties, "someProperties");
		
		StandardPropertyManager test = new StandardPropertyManager(
				parent, otherProperties, "otherProperties");
				
		assertEquals(System.getProperty("java.version"), 
				test.lookup("java.version"));
		
		assertEquals("apple", 
				test.lookup("snack.fruit"));
	}
	
   @Test
	public void testOverridesAndParent() {
		
		StandardPropertyManager parent = new StandardPropertyManager(
				someProperties, "someProperties");
		
		StandardPropertyManager test = new StandardPropertyManager(
				parent);
				
		assertEquals(System.getProperty("java.version"), 
				test.lookup("java.version"));
		
		assertEquals("apple", 
				test.lookup("snack.fruit"));
		
		test.addPropertyOverride(new MockPropertyLookup() {
			@Override
			public String lookup(String propertyName) {
				return otherProperties.getProperty(propertyName);
			}
		});
		
		assertEquals("orange", 
				test.lookup("snack.fruit"));
	}
}
