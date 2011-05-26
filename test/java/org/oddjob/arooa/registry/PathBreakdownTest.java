package org.oddjob.arooa.registry;

import junit.framework.TestCase;

public class PathBreakdownTest extends TestCase {

	public void testSingleId() {
		
		PathBreakdown test = new PathBreakdown("a");
		
		assertFalse(test.isNested());
		assertFalse(test.isProperty());
		
		assertEquals("a", test.getId());
	}
	
	public void testProperty() {
		
		PathBreakdown test = new PathBreakdown("a.b");
		
		assertFalse(test.isNested());
		assertTrue(test.isProperty());
		
		assertEquals("a", test.getId());
		assertEquals("b", test.getProperty());
	}
	
	public void testNested() {
		
		PathBreakdown test = new PathBreakdown("a/b");
		
		assertTrue(test.isNested());
		assertFalse(test.isProperty());
		
		assertEquals("a", test.getId());
		assertEquals("b", test.getNestedPath());
	}
	
	public void testNestedProperty() {
		
		PathBreakdown test = new PathBreakdown("a/b.c");
		
		assertTrue(test.isNested());
		assertTrue(test.isProperty());
		
		assertEquals("a", test.getId());
		assertEquals("b.c", test.getNestedPath());
	}
}
