package org.oddjob.arooa.registry;

import org.junit.Test;

import org.oddjob.arooa.ComponentTrinity;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.MockArooaContext;

import org.junit.Assert;

import java.util.stream.StreamSupport;

import static org.hamcrest.CoreMatchers.is;

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

        assertNull("No id expected", test.idFor(trinity));

        assertEquals(trinity, test.trinityFor(component));
        assertEquals(trinity, test.trinityFor(proxy));

        assertEquals(context, test.trinityFor(component).getTheContext());
        assertEquals(context, test.trinityFor(proxy).getTheContext());

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
    public void whenDuplicateIdsTheException() {

        ArooaContext context = new MockArooaContext();

        Object component1 = new Object();
        Object component2 = new Object();

        SimpleComponentPool.AllWayIndex test =
                new SimpleComponentPool.AllWayIndex();

        test.add(new ComponentTrinity(
                component1, component1, context), "a");

        try {
            test.add(new ComponentTrinity(
                    component2, component2, context), "a");
            fail("Should fail when registered twice.");
        } catch (IllegalStateException e) {
            // expected
        }

        assertSame(component1,
                   test.trinityForId("a").getTheComponent());
    }

    @Test
    public void whenAddRemoveAddAgainThenOk() {

        ArooaContext context = new MockArooaContext();

        Object component1 = new Object();

        SimpleComponentPool.AllWayIndex test =
                new SimpleComponentPool.AllWayIndex();

        test.add(new ComponentTrinity(
                component1, component1, context), "a");

        test.remove(test.trinityFor(component1));

        test.add(new ComponentTrinity(
                component1, component1, context), "a");

        assertThat(StreamSupport.stream(
                test.trinities().spliterator(), false).count(),
                   is(1L));
    }
}
