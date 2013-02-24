package org.oddjob.arooa.registry;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.reflect.PropertyAccessor;

public class LinkedBeanRegistyTest extends TestCase {

	public void testAddAndRetrieve() {
		
		PropertyAccessor accessor = new BeanUtilsPropertyAccessor();
		ArooaConverter converter = new DefaultConverter();
		
		SimpleBeanRegistry parent = new SimpleBeanRegistry(
				accessor, converter);
		
		LinkedBeanRegistry test = new LinkedBeanRegistry(
				parent, accessor, converter);
		
		parent.register("a", "apple");
		
		assertEquals("apple", test.lookup("a"));
		assertEquals("a", test.getIdFor("apple"));
		
		test.register("b", "banana");
		
		assertEquals("banana", test.lookup("b"));
		assertEquals(null, parent.lookup("b"));
		
		assertEquals("b", test.getIdFor("banana"));
		assertEquals(null, parent.getIdFor("banana"));
		
		Iterable<Object> all = test.getAllByType(Object.class);
		List<Object> results = new ArrayList<Object>();
		
		for (Object o : all) {
			results.add(o);
		}
		
		assertEquals("banana", results.get(0));
		assertEquals("apple", results.get(1));
		
	}
}
