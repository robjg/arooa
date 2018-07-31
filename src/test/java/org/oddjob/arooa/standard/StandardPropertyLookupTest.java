package org.oddjob.arooa.standard;

import org.junit.Test;

import java.util.Properties;
import java.util.Set;

import org.junit.Assert;

public class StandardPropertyLookupTest extends Assert {

   @Test
	public void testLookupSourceAndNames() {
		
		Properties props = new Properties();
		props.setProperty("favourite.fruit", "apple");
		
		StandardPropertyLookup test = new StandardPropertyLookup(
				props, "TEST");
		
		assertEquals("apple", test.lookup("favourite.fruit"));
		
		assertEquals("TEST", test.sourceFor("favourite.fruit").toString());
		
		Set<String> names = test.propertyNames();
		
		assertEquals(1, names.size());
		assertTrue(names.contains("favourite.fruit"));
	}
}
