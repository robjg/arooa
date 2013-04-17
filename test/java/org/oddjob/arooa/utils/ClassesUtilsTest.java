package org.oddjob.arooa.utils;

import junit.framework.TestCase;

public class ClassesUtilsTest extends TestCase {

	public void testClassFor() throws ClassNotFoundException {
		
		assertEquals(String.class,
				ClassesUtils.classFor(
						"java.lang.String", getClass().getClassLoader()));
		
		assertEquals(int.class,
				ClassesUtils.classFor(
						int.class.getName(), getClass().getClassLoader()));
		
		assertEquals(int[].class,
				ClassesUtils.classFor(
						int[].class.getName(), getClass().getClassLoader()));
		
		assertEquals(int[][].class,
				ClassesUtils.classFor(
						int[][].class.getName(), getClass().getClassLoader()));
	}
}
