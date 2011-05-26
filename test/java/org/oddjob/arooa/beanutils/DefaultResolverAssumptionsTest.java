package org.oddjob.arooa.beanutils;

import org.apache.commons.beanutils.expression.DefaultResolver;

import junit.framework.TestCase;

/**
 * Learning how the Resolver works.
 * 
 * @author rob
 *
 */
public class DefaultResolverAssumptionsTest extends TestCase {

	public void testGetProperty() {
		
		DefaultResolver test = new DefaultResolver();
		
		assertEquals("fruit", test.getProperty("fruit"));
		assertEquals("fruit", test.getProperty("fruit.apple"));
		assertEquals("fruit", test.getProperty("fruit[0].apple[0]"));
		assertEquals("fruit", test.getProperty("fruit(cox).apple(cox)"));
	}
	
	public void testNext() {
		
		DefaultResolver test = new DefaultResolver();
		
		assertEquals("fruit", test.next("fruit"));
		assertEquals("fruit", test.next("fruit.apple"));
		assertEquals("fruit[0]", test.next("fruit[0].apple[0]"));
		assertEquals("fruit(cox)", test.next("fruit(cox).apple(cox)"));
	}
	
	public void testRemove() {
		
		DefaultResolver test = new DefaultResolver();
		
		assertEquals(null, test.remove("fruit"));
		assertEquals(null, test.remove("fruit[0]"));
		assertEquals(null, test.remove("fruit(cox)"));
		assertEquals("apple", test.remove("fruit.apple"));
		assertEquals("apple[0]", test.remove("fruit[0].apple[0]"));
		assertEquals("apple(cox)", test.remove("fruit(cox).apple(cox)"));
	}
	
	public void testIsIndexed() {
		
		DefaultResolver test = new DefaultResolver();
		
		assertTrue(test.isIndexed("fruit[0]"));
		assertFalse(test.isIndexed("fruit.apple"));
		assertFalse(test.isIndexed("fruit.apple[0]"));
		assertFalse(test.isIndexed("fruit.apple(cox)"));
	}
	
	public void testIsMapped() {
		
		DefaultResolver test = new DefaultResolver();
		
		assertTrue(test.isMapped("fruit(cox)"));
		assertFalse(test.isMapped("fruit.apple"));
		assertFalse(test.isMapped("fruit.apple[0]"));
		assertFalse(test.isMapped("fruit.apple(cox)"));
	}
	
}
