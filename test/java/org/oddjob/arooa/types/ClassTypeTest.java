package org.oddjob.arooa.types;

import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.standard.StandardArooaSession;

public class ClassTypeTest extends TestCase {

	public void testClassForWithNoLoader() throws ArooaConversionException {
		
		ArooaSession session = new StandardArooaSession();
		
		ClassType test = new ClassType();
		
		test.setArooaSession(session);
		test.setName(String.class.getName());
		
		assertEquals(String.class, test.toValue());
	}
	
	public void testClassForWithBadLoader() {
		
		ClassType test = new ClassType();
		
		test.setName(ValueType.class.getName());
		test.setClassLoader(
				new URLClassLoader(new URL[0], null));
		
		try {
			test.toValue();
			fail("Exception expected.");
		}
		catch (ArooaConversionException e) {
			assertEquals(ClassNotFoundException.class,
					e.getCause().getClass());
		}
	}
	
	public void testLoadingStringType() throws ArooaConversionException {
		
		ClassType test = new ClassType();
		
		test.setName(String[].class.getName());
		
		test.setClassLoader(getClass().getClassLoader());
		
		Class<?> result = test.toValue();
		
		assertEquals(String[].class, result);
	}	
}
