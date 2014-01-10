package org.oddjob.arooa.utils;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class IterablesTest extends TestCase {

	public void testToArray() {

		List<Integer> list = new ArrayList<Integer>();
		list.add(new Integer(2));
		list.add(new Integer(3));
		
		Number[] result = Iterables.toArray(list, Number.class);

		assertEquals(new Integer(2), result[0]);
		assertEquals(new Integer(3), result[1]);
	}
	
	// Why class has to be Class<?>
	public void testToArrayOfParameterisedType() {

		List<List<Integer>> list = new ArrayList<List<Integer>>();
		list.add(new ArrayList<Integer>());
		list.add(new ArrayList<Integer>());
		
		List<Integer>[] result = Iterables.toArray(list, List.class);

		assertEquals(2, result.length);
	}
	
	public void testToArrayWhenNull() {
		
		try {
			Iterables.toArray(null, List.class);
			fail("Should throw NPE.");
		}
		catch (NullPointerException e) {
			// expected
		}
	}
	
	public void testToString() {
		
		List<Integer> list = new ArrayList<Integer>();
		list.add(new Integer(2));
		list.add(new Integer(3));
		
		String result = Iterables.toString(list);

		assertEquals("[2, 3]", result);
	}
	
	public void testToStringWhenNull() {
		
		String result = Iterables.toString(null);

		assertEquals("null", result);
	}
}
