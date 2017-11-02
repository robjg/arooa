package org.oddjob.arooa.beanutils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import org.oddjob.arooa.beanutils.PropertyPath;
import org.oddjob.arooa.beanutils.PropertyPath.FragmentVisitor;
import org.oddjob.arooa.reflect.ArooaPropertyException;

public class PropertyPathTest extends Assert {

	class MockFragmentVisitor implements FragmentVisitor {
		List<String> names = new ArrayList<String>();
		int intermediate;
		int simple;
		int mapped;
		int indexed;

		int index;
		String key;
		
		public void onIntermediateProperty(String name) {
			++intermediate;
			names.add(name);
		}
		
		public void onSimpleProperty(String name) {
			++simple;
			names.add(name);
		}
		
		public void onIndexedProperty(String name, int index) {
			++indexed;
			this.index = index;
			names.add(name);
		}
		
		public void onMappedProperty(String name, String key) {
			++mapped;
			this.key = key;
			names.add(name);
		}
		
		String getName(int index) {
			return (String) names.get(index);
		}
	}
	
   @Test
	public void testNull() throws ArooaPropertyException {
		
		PropertyPath test = new PropertyPath(null);
		MockFragmentVisitor results = new MockFragmentVisitor();

		test.iterate(results);
		
		assertEquals(0, results.names.size());
	}
	
   @Test
	public void testEmpty() {
		
		PropertyPath test = new PropertyPath("");
		MockFragmentVisitor results = new MockFragmentVisitor();

		test.iterate(results);
			
		assertEquals(1, results.names.size());
		assertEquals(1, results.simple);
		assertEquals("", results.getName(0));
	}

   @Test
	public void testSingle() {
		
		PropertyPath test = new PropertyPath("abc");

		MockFragmentVisitor results = new MockFragmentVisitor();

		test.iterate(results);
		
		assertEquals(1, results.simple);
		assertEquals("abc", results.getName(0));
	}
	
   @Test
	public void testSimple() {
		
		PropertyPath test = new PropertyPath("a.b.c");

		MockFragmentVisitor results = new MockFragmentVisitor();

		test.iterate(results);
		
		assertEquals(2, results.intermediate);
		assertEquals(1, results.simple);
		
		assertEquals("a", results.getName(0));
		assertEquals("b", results.getName(1));
		assertEquals("c", results.getName(2));
	}
	
   @Test
	public void testIndexed() {
		
		PropertyPath test = new PropertyPath("a.b.c[23]");

		MockFragmentVisitor results = new MockFragmentVisitor();

		test.iterate(results);
		
		assertEquals(2, results.intermediate);
		assertEquals(1, results.indexed);
		
		assertEquals("a", results.getName(0));
		assertEquals("b", results.getName(1));
		assertEquals("c", results.getName(2));
		
		assertEquals(23, results.index);
	}
	
   @Test
	public void testIndexed2() {
		
		PropertyPath test = new PropertyPath("a[23].b.c");

		MockFragmentVisitor results = new MockFragmentVisitor();

		test.iterate(results);
		
		assertEquals(2, results.intermediate);
		assertEquals(1, results.simple);
		
		assertEquals("a[23]", results.getName(0));
		assertEquals("b", results.getName(1));
		assertEquals("c", results.getName(2));
	}
	
   @Test
	public void testMapped() {
		
		PropertyPath test = new PropertyPath("a.b(xyz).c");

		MockFragmentVisitor results = new MockFragmentVisitor();

		test.iterate(results);
		
		assertEquals(2, results.intermediate);
		assertEquals(1, results.simple);
		
		assertEquals("a", results.getName(0));
		assertEquals("b(xyz)", results.getName(1));
		assertEquals("c", results.getName(2));
	}
}
