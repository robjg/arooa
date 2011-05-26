/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.registry;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;

import junit.framework.TestCase;

/**
 * 
 */
public class SimpleBeanRegistryTest extends TestCase {

	public void testAddRemove() throws InvalidIdException, ArooaPropertyException {
		
		Object c1 = new Object();
		
		SimpleBeanRegistry r1 = new SimpleBeanRegistry();
		
		r1.register("a", c1);

		assertEquals(c1, r1.lookup("a"));
		assertEquals("a", r1.getIdFor(c1));
		
		Object c2 = new Object();
		
		r1.register("a", c2);

		assertEquals(c1, r1.lookup("a"));
		assertEquals("a", r1.getIdFor(c1));
		
		
		r1.remove(c1);
		
		assertNull(r1.lookup("a"));
		assertNull(r1.getIdFor(c1));
		
		
		r1.register("a", c2);
		
		assertEquals(c2, r1.lookup("a"));
		assertEquals("a", r1.getIdFor(c2));
		
	}
	
	public static class ThingWithProperty {
		public String getCount() {
			return "42";
		}
	}
	
	public void testGetProperty() throws ArooaConversionException {
		
		ThingWithProperty c1 = new ThingWithProperty();
		
		SimpleBeanRegistry r1 = new SimpleBeanRegistry();
		
		r1.register("a", c1);

		assertEquals("42", r1.lookup("a.count"));
		assertEquals(new Integer(42), r1.lookup("a.count", Integer.class));
	}
	
	class Component extends MockBeanDirectoryOwner {
		final String name;
		BeanDirectory directory;
		
		Component(String name) {
			this.name = name;
		}
		public String toString() {
			return name;
		}
		public BeanDirectory provideBeanDirectory() {
			return directory;
		}
	}
	
	public void testRegistryOwners() {
		
		Component c1 = new Component("comp1");
		
		SimpleBeanRegistry r1 = new SimpleBeanRegistry();
		
		r1.register("a", c1);

		BeanDirectoryOwner result = null;
		
		for (BeanDirectoryOwner owner : r1.getAllByType(BeanDirectoryOwner.class)) {
			result = owner;
		}
		
		assertEquals(c1, result);
	}
		
	public void testReservedCharactesException() {
		
		SimpleBeanRegistry test = new SimpleBeanRegistry();
		
		try {
			test.register("favourite.fruit", "apple");
			fail("Should fail.");
		}
		catch (InvalidIdException e) {
			// Expected.
		}
		
		try {
			test.register("favourite/fruit", "apple");
			fail("Should fail.");
		}
		catch (InvalidIdException e) {
			// Expected.
		}
		
		try {
			test.register("favourite[fruit]", "apple");
			fail("Should fail.");
		}
		catch (InvalidIdException e) {
			// Expected.
		}
		
		try {
			test.register("favourite(fruit)", "apple");
			fail("Should fail.");
		}
		catch (InvalidIdException e) {
			// Expected.
		}
	}
}
