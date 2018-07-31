package org.oddjob.arooa.registry;

import org.junit.Test;

import org.junit.Assert;

public class PathBreakdownTest extends Assert {

   @Test
	public void testSingleId() {
		
		PathBreakdown test = new PathBreakdown("a");
		
		assertFalse(test.isNested());
		assertFalse(test.isProperty());
		
		assertEquals("a", test.getId());
	}
	
   @Test
	public void testProperty() {
		
		PathBreakdown test = new PathBreakdown("a.b");
		
		assertFalse(test.isNested());
		assertTrue(test.isProperty());
		
		assertEquals("a", test.getId());
		assertEquals("b", test.getProperty());
	}
	
   @Test
	public void testNested() {
		
		PathBreakdown test = new PathBreakdown("a/b");
		
		assertTrue(test.isNested());
		assertFalse(test.isProperty());
		
		assertEquals("a", test.getId());
		assertEquals("b", test.getNestedPath());
	}
	
   @Test
	public void testNestedProperty() {
		
		PathBreakdown test = new PathBreakdown("a/b.c");
		
		assertTrue(test.isNested());
		assertTrue(test.isProperty());
		
		assertEquals("a", test.getId());
		assertEquals("b.c", test.getNestedPath());
	}
}
