package org.oddjob.arooa.types;

import org.junit.Test;

import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Assert;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.standard.StandardArooaSession;

public class ClassTypeTest extends Assert {

   @Test
	public void testClassForWithNoLoader() throws ArooaConversionException, ClassNotFoundException {
		
		ArooaSession session = new StandardArooaSession();
		
		ClassType test = new ClassType();
		
		test.setArooaSession(session);
		test.setName(String.class.getName());
		
		assertEquals("ClassType: java.lang.String", test.toString());
		
		assertEquals(String.class, test.toClass());
	}
	
   @Test
	public void testClassForWithBadLoader() {
		
		ClassType test = new ClassType();
		
		test.setName(ValueType.class.getName());
		test.setClassLoader(
				new URLClassLoader(new URL[0], null));
		
		try {
			test.toClass();
			fail("Exception expected.");
		}
		catch (ClassNotFoundException e) {
			// expected
		}
	}
	
   @Test
	public void testLoadingStringType() throws ArooaConversionException, ClassNotFoundException {
		
		ClassType test = new ClassType();
		
		test.setName(String[].class.getName());
		
		test.setClassLoader(getClass().getClassLoader());
		
		Class<?> result = test.toClass();
		
		assertEquals(String[].class, result);
	}	
}
