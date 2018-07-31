package org.oddjob.arooa.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

public class ListSetterHelperTest extends Assert {

   @Test
	public void testAddAndRemove() {
		
		List<String> list = new ArrayList<String>();
		
		ListSetterHelper<String> test = 
			new ListSetterHelper<String>(list);
		
		test.set(0, "apple");
		test.set(1, null);
		test.set(2, "orange");
		
		assertEquals("apple", list.get(0));
		assertEquals(null, list.get(1));
		assertEquals("orange", list.get(2));

		test.set(0, null);
		
		assertEquals(null, list.get(0));
		assertEquals("orange", list.get(1));
		
		test.set(0, null);
		
		assertEquals("orange", list.get(0));
		
		test.set(0, null);
		
		assertEquals(0, list.size());
	}
}
