package org.oddjob.arooa.utils;

import junit.framework.TestCase;

public class ClassUtilsTest extends TestCase {

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
