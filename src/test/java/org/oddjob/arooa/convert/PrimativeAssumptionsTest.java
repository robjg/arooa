package org.oddjob.arooa.convert;

import org.junit.Test;

import org.junit.Assert;

/**
 * Checking primitive conversion now that 1.5 is here...
 * 
 * @author rob
 *
 */
public class PrimativeAssumptionsTest extends Assert {

	
	
   @Test
	public void testIsAssignable() {

		// So all this works.
		Boolean b = true;
		boolean b2 = b;
		b = b2;
		
		// but that is the compiler, so this doesn't...
		
		assertFalse(Boolean.TYPE.isAssignableFrom(Boolean.class));
		assertFalse(Boolean.class.isAssignableFrom(Boolean.TYPE));
		
		// and this doesn't work either.
		assertFalse(Boolean.TYPE.isInstance(new Boolean(true)));
		
		// but this does - because the true gets autoboxed..
		assertTrue(Boolean.class.isInstance((boolean) true));

		// and this doesn't compile
//		assertTrue(b2 instanceof Boolean);
	}
	
	Class<?> cl;
	
	void foo(Object o) {
		cl = o.getClass();
	}

   @Test
	public void testAutoboxing() {

		// Autoboxing happens before call... but we new that.
		
		int i = 1;
		foo(i);
		
		assertEquals(Integer.class, cl);
	}
	
   @Test
	public void testSuperClass() {
		
		// is it object - no it's null?
		assertEquals(null, Boolean.TYPE.getSuperclass());
	}
}
