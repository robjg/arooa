package org.oddjob.arooa.life;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;

import org.oddjob.OurDirs;

public class ClassLoaderClassResolverTest extends TestCase {

	public static class Apple {
		
	}
	
	public void testFindClass() {
		
		ClassLoaderClassResolver test = new ClassLoaderClassResolver(
				getClass().getClassLoader());
		
		assertEquals(Apple.class, 
				test.findClass(Apple.class.getName()));
		assertEquals(String.class, 
				test.findClass(String.class.getName()));
		
	}
	
	public void testFindPrimatives() {
		
		ClassLoaderClassResolver test = new ClassLoaderClassResolver(
				getClass().getClassLoader());
		
		assertEquals(boolean.class, test.findClass(boolean.class.getName()));
		assertEquals(byte.class, test.findClass(byte.class.getName()));
		assertEquals(short.class, test.findClass(short.class.getName()));
		assertEquals(char.class, test.findClass(char.class.getName()));
		assertEquals(int.class, test.findClass(int.class.getName()));
		assertEquals(long.class, test.findClass(long.class.getName()));
		assertEquals(float.class, test.findClass(float.class.getName()));
		assertEquals(double.class, test.findClass(double.class.getName()));
	}
	
	public void testFindClassClassLoader() throws MalformedURLException {
		OurDirs ourDirs = new OurDirs();
		
		File classes = new File(ourDirs.base(), "build/test/classes");
		if (!classes.exists()) {
			classes = new File(ourDirs.base(), "classes");
		}
		if (!classes.exists()) {
			throw new IllegalStateException("No classes!");
		}
		
		URLClassLoader classLoader = new URLClassLoader(new URL[] {
				classes.toURI().toURL()
		}, null);

		ClassLoaderClassResolver test = 
			new ClassLoaderClassResolver(classLoader);
		
		Class<?> cl = test.findClass(Apple.class.getName());
		
		assertNotNull("Class found with classloader " + test, cl);
		
		assertEquals(classLoader, cl.getClassLoader());
	}
	
	
	public void testGetResource() {
	
		ClassLoaderClassResolver test = new ClassLoaderClassResolver(
				getClass().getClassLoader());
		
		URL result = test.getResource(
				"org/oddjob/arooa/life/ResourceToFind.properties");
		
		assertNotNull(result);
	}
	
	public void testGetResources() {
		
		ClassLoaderClassResolver test = new ClassLoaderClassResolver(
				getClass().getClassLoader());
		
		URL[] result = test.getResources(
			"org/oddjob/arooa/life/ResourceToFind.properties");

		assertEquals(1, result.length);
	}
	
	/**
	 * It appears the ClassLoader.loadClass won't find arrays but
	 * Class.forName will.
	 * 
	 * @throws ClassNotFoundException
	 */
	public void testFindArrayClass() throws ClassNotFoundException {
		
		String name = String[].class.getName();
		
		// works
		Class.forName(name, true, getClass().getClassLoader());
		
		try {
			getClass().getClassLoader().loadClass(name);
			fail("This throws an exception.");
		}
		catch (ClassNotFoundException e) {
			// expected.
		}
		
		ClassLoaderClassResolver resolver = new ClassLoaderClassResolver(getClass().getClassLoader());
		
		Class<?> result = resolver.findClass(name);
		
		assertSame(String[].class, result);
	}
}
