package org.oddjob.arooa.utils;

import org.junit.Test;

import org.junit.Assert;

public class ClassUtilsTest extends Assert {

   @Test
	public void testClassFor() throws ClassNotFoundException {
		
		assertEquals(String.class,
				ClassUtils.classFor(
						"java.lang.String", getClass().getClassLoader()));
		
		assertEquals(int.class,
				ClassUtils.classFor(
						int.class.getName(), getClass().getClassLoader()));
		
		assertEquals(int[].class,
				ClassUtils.classFor(
						int[].class.getName(), getClass().getClassLoader()));
		
		assertEquals(int[][].class,
				ClassUtils.classFor(
						int[][].class.getName(), getClass().getClassLoader()));
	}
	
	// To visually check the error message.
   @Test
	public void testError() {
		
		try {
			ClassUtils.classFor("A.Flying.Pig", getClass().getClassLoader());
			fail("Should fail.");
		}
		catch (ClassNotFoundException e) {
			// expected.
		}
	}
}
