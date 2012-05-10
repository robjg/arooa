package org.oddjob.arooa.standard;

import java.util.Properties;
import java.util.Set;

import junit.framework.TestCase;

public class StandardPropertyLookupTest extends TestCase {

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
