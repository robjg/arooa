package org.oddjob.arooa.types;

import org.junit.Test;

import org.junit.Assert;

public class ArooaObjectTest extends Assert {

   @Test
	public void testEquals() {
		
		Object test1 = new ArooaObject(null);
		Object test2 = new ArooaObject("42");
		Object test3 = new ArooaObject("42");
		Object test4 = new ArooaObject(new Integer(42));
		Object test5 = new ArooaObject(null);

		assertEquals(false, test1.equals(test2));
		assertEquals(true, test1.equals(test5));
		assertEquals(true, test2.equals(test3));
		assertEquals(false, test2.equals(test4));
	}
	
   @Test
	public void testHashCode() {
		
		Object test1 = new ArooaObject(null);
		Object test2 = new ArooaObject(new Integer(42));
		
		assertEquals(0, test1.hashCode());
		assertEquals(42, test2.hashCode());
		
	}
}
