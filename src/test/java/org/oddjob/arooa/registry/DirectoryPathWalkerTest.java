package org.oddjob.arooa.registry;

import org.junit.Test;

import org.oddjob.arooa.reflect.ArooaPropertyException;

import org.junit.Assert;

public class DirectoryPathWalkerTest extends Assert {

   @Test
	public void testSimplePath() throws InvalidIdException, ArooaPropertyException {
		
		Object comp = new Object();
		
		SimpleBeanRegistry cr = new SimpleBeanRegistry();
		
		cr.register("foo", comp);

		DirectoryPathWalker test = new DirectoryPathWalker(cr);
				
		// check we can look it up again by path.
		assertEquals(comp, test.objectForPath(new Path("foo")));
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
	
	/** Test a hierarchy */
   @Test
	public void testHierarchy() {
		Component comp1 = new Component("comp1");

		SimpleBeanRegistry r1 = new SimpleBeanRegistry();
		r1.register("a", comp1);
		
		SimpleBeanRegistry r2 = new SimpleBeanRegistry();		
		comp1.directory = r2;
		
		Object comp2 = new Component("comp2");
		
		r2.register("b", comp2);
		
		DirectoryPathWalker test = new DirectoryPathWalker(r1);
				
		// check we can get object for path
		assertEquals(comp2, test.objectForPath(new Path("a/b")));
	}
}
