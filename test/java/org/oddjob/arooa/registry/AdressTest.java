package org.oddjob.arooa.registry;

import junit.framework.TestCase;

public class AdressTest extends TestCase {

	public void testFields() {
		
		Address test = new Address(
					new ServerId("//test"),
					new Path("x/y"));
			
		assertEquals("x/y", test.getPath().toString());
			
		assertEquals("//test", test.getServerId().toString());
		
		assertEquals("//test:x/y", test.toString());
	}
	
	public void testEquals() {
		
		Address test1 = new Address(
				new ServerId("//foo"), new Path("apples"));
		
		assertEquals(test1, test1);
		
		Address test2 = new Address(
				new ServerId("//foo"), new Path("apples"));
		
		assertEquals(test1, test2);
		
		assertEquals(test1.hashCode(), test2.hashCode());
		
		Address test3 = new Address(
				new ServerId("//far"), new Path("apples"));
		
		assertFalse(test1.equals(test3));
		
		Address test4 = new Address(
				new ServerId("//foo"), new Path("pears"));
		
		assertFalse(test1.equals(test4));
		
		assertFalse(test1.equals(null));
	}
}
