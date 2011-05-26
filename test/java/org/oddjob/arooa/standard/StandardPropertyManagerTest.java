package org.oddjob.arooa.standard;

import java.util.Properties;

import junit.framework.TestCase;

import org.oddjob.arooa.runtime.PropertyLookup;

public class StandardPropertyManagerTest extends TestCase {

	Properties someProperties;
	
	Properties otherProperties;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		someProperties = new Properties();
		
		someProperties.setProperty("snack.fruit", "apple");
		someProperties.setProperty("java.version", "-1");
		
		otherProperties = new Properties();
		
		otherProperties.setProperty("snack.fruit", "orange");
	}
	
	public void testNoParentNoProperties() {
		
		StandardPropertyManager test = new StandardPropertyManager();
		
		test.addPropertyLookup(new StandardPropertyLookup(someProperties));
		test.addPropertyLookup(new StandardPropertyLookup(otherProperties));
		
		assertEquals(System.getProperty("java.version"), 
				test.lookup("java.version"));
		
		assertEquals("apple", 
				test.lookup("snack.fruit"));
	}
	
	public void testPropertiesNoParent() {
		
		StandardPropertyManager test = new StandardPropertyManager(
				someProperties);
		
		test.addPropertyLookup(new StandardPropertyLookup(otherProperties));
		
		assertEquals(System.getProperty("java.version"), 
				test.lookup("java.version"));
		
		assertEquals("apple", 
				test.lookup("snack.fruit"));
	}
	
	public void testPropertiesAndParent() {
		
		StandardPropertyManager parent = new StandardPropertyManager(
				someProperties);
		
		StandardPropertyManager test = new StandardPropertyManager(
				parent, otherProperties);
				
		assertEquals(System.getProperty("java.version"), 
				test.lookup("java.version"));
		
		assertEquals("apple", 
				test.lookup("snack.fruit"));
	}
	
	public void testOverridesAndParent() {
		
		StandardPropertyManager parent = new StandardPropertyManager(
				someProperties);
		
		StandardPropertyManager test = new StandardPropertyManager(
				parent, null);
				
		assertEquals(System.getProperty("java.version"), 
				test.lookup("java.version"));
		
		assertEquals("apple", 
				test.lookup("snack.fruit"));
		
		test.addPropertyOverride(new PropertyLookup() {
			@Override
			public String lookup(String propertyName) {
				return otherProperties.getProperty(propertyName);
			}
		});
		
		assertEquals("orange", 
				test.lookup("snack.fruit"));
	}
}
