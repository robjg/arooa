package org.oddjob.arooa.registry;

import org.junit.Test;

import org.junit.Assert;

public class BeanDirectoryCrawlerTest extends Assert {

	/** Test a single registry with no children. 
	 * @throws InvalidIdException */
   @Test
	public void testSingle() throws InvalidIdException {
		Object comp = new Object();
		SimpleBeanRegistry cr = new SimpleBeanRegistry();
		cr.register("foo", comp);

		BeanDirectoryCrawler test = new BeanDirectoryCrawler(cr);
		
		// check we can get the path. 
		assertEquals("foo", test.pathForObject(comp).toString());
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
		
		BeanDirectoryCrawler test = new BeanDirectoryCrawler(r1);
		
		// check we can get the path. 
		assertEquals("a/b", test.pathForObject(comp2).toString());
	}
	
	
   @Test
	public void testPathForObjectInNullPath () {
		
		Component c1 = new Component("comp1");
	
		SimpleBeanRegistry r1 = new SimpleBeanRegistry();
		
		SimpleBeanRegistry r2 = new SimpleBeanRegistry();
		
		r2.register("b", c1);
		
		BeanDirectoryCrawler test = new BeanDirectoryCrawler(r1);
		
		assertNull(test.pathForObject(c1));

	}
	
}
