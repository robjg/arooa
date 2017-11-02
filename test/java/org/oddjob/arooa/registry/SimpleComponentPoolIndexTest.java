package org.oddjob.arooa.registry;

import org.junit.Test;

import org.oddjob.arooa.ComponentTrinity;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.MockArooaContext;

import org.junit.Assert;

public class SimpleComponentPoolIndexTest extends Assert {
	
   @Test
	public void testAddWithId() {
	
		Object component = new Object();
		Object proxy = new Object();
		ArooaContext context = new MockArooaContext();
		
		ComponentTrinity trinity = new ComponentTrinity(
				component, proxy, context);
		
		SimpleComponentPool.AllWayIndex test = 
			new SimpleComponentPool.AllWayIndex();
		
		test.add(trinity, "a");
		
		assertTrue(test.contains("a"));
		
		assertEquals("a", test.idFor(trinity));

		assertEquals(trinity, test.trinityFor(component));
		assertEquals(trinity, test.trinityFor(proxy));
		assertEquals(trinity, test.trinityForId("a"));
		
		ComponentTrinity result = null;
		for (ComponentTrinity it : test.trinities()) {
			result = it;
		}
		
		assertEquals(trinity, result);
		
		test.remove(trinity);
		
		assertNull(test.trinityFor(component));
		assertNull(test.trinityFor(proxy));
		assertNull(test.trinityForId("a"));
	}
	
	
   @Test
	public void testAddWithNoId() {
		
		Object component = new Object();
		Object proxy = new Object();
		ArooaContext context = new MockArooaContext();
		
		ComponentTrinity trinity = new ComponentTrinity(
				component, proxy, context);
		
		SimpleComponentPool.AllWayIndex test = 
			new SimpleComponentPool.AllWayIndex();
		
		test.add(trinity, null);
		
		assertFalse(test.contains("a"));
		
		assertEquals(null, test.idFor(trinity));

		assertEquals(trinity, test.trinityFor(component));
		assertEquals(trinity, test.trinityFor(proxy));
		
		assertEquals(context, test.trinityFor(component).getTheContext());
		assertEquals(context, test.trinityFor(proxy).getTheContext());
		
		assertEquals(null, test.trinityForId("a"));
		
		ComponentTrinity result = null;
		for (ComponentTrinity it : test.trinities()) {
			result = it;
		}
		
		assertEquals(trinity, result);
		
		test.remove(trinity);
		
		assertNull(test.trinityFor(component));
		assertNull(test.trinityFor(proxy));
		assertNull(test.trinityForId("a"));
	}
	
   @Test
	public void testDuplicateIds() {
		
		ArooaContext context = new MockArooaContext();
		
		Object component1 = new Object();
		Object component2 = new Object();
		Object component3 = new Object();

		SimpleComponentPool.AllWayIndex test = 
			new SimpleComponentPool.AllWayIndex();
		
		test.add(new ComponentTrinity(
				component1, component1, context), "a");
		test.add(new ComponentTrinity(
				component2, component2, context), "a");
		test.add(new ComponentTrinity(
				component3, component3, context), "a");
		
		assertSame(component1, 
				test.trinityForId("a").getTheComponent());
		assertSame(component2, 
				test.trinityForId("a2").getTheComponent());
		assertSame(component3, 
				test.trinityForId("a3").getTheComponent());
	}
}
