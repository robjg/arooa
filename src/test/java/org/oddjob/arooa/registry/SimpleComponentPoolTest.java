package org.oddjob.arooa.registry;

import org.junit.Test;

import org.junit.Assert;

import org.mockito.Mockito;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ComponentTrinity;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.life.ComponentPersistException;
import org.oddjob.arooa.life.ComponentPersister;
import org.oddjob.arooa.life.MockComponentPersister;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.standard.StandardArooaSession;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;

public class SimpleComponentPoolTest extends Assert {

	public static class Fruit {
		
	}
	
	public static class FruitProxy {
		
	}

	private class OurComponentPerister extends MockComponentPersister {
		boolean saved;
		boolean removed;
		
		public void persist(String id, Object proxy, ArooaSession session) {
			assertEquals("fruit", id);
			assertTrue(proxy instanceof FruitProxy);
			saved = true;
		}
		
		public void remove(String id, ArooaSession session) {
			assertEquals("fruit", id);
			removed = true;
		}
		
	}
	
	private class OurSession extends MockArooaSession {
		OurComponentPerister persister = new OurComponentPerister();
		BeanRegistry registry = new SimpleBeanRegistry();
		
		@Override
		public ComponentPersister getComponentPersister() {
			return persister;
		}
		
		@Override
		public BeanRegistry getBeanRegistry() {
			return registry;
		}
	}
	
	private class OurContext extends MockArooaContext {
		
		OurSession session = new OurSession();
		
		@Override
		public ArooaSession getSession() {
			return session;
		}
	}
	
   @Test
	public void testLookup() {
		
		SimpleComponentPool test = new SimpleComponentPool();
		
		OurContext context = new OurContext();
		
		Object component = new Fruit();
		Object proxy = new FruitProxy();
		
		test.registerComponent(
				new ComponentTrinity(
						component, 
						proxy, 
						context)
				, "fruit");
		
		assertEquals(proxy, test.trinityForId("fruit").getTheProxy());
		assertEquals(context, test.trinityForId("fruit").getTheContext());
		assertEquals(component, test.trinityForId("fruit").getTheComponent());
	}
	
   @Test
	public void testNoIdLookup() {
		
		SimpleComponentPool test = new SimpleComponentPool();
		
		OurContext context = new OurContext();
		
		Object component = new Fruit();
		Object proxy = new FruitProxy();
		
		test.registerComponent(
				new ComponentTrinity(
						component, 
						proxy, 
						context)
				, null);
		
		assertEquals(context, test.contextFor(component));
		assertEquals(context, test.contextFor(proxy));
	}
	
   @Test
	public void testSave() throws ComponentPersistException {
		
		SimpleComponentPool test = new SimpleComponentPool();
		
		OurContext context = new OurContext();
		
		Object component = new Fruit();
		
		test.registerComponent(
				new ComponentTrinity(
						component, 
						new FruitProxy(), 
						context)
				, "fruit");
		
		test.save(component);
		
		assertTrue(context.session.persister.saved);
		
		test.remove(component);
		
		// check remove
		assertTrue(context.session.persister.removed);		
	}
	
   @Test
	public void testNoIdNoSave() throws ComponentPersistException {
		
		SimpleComponentPool test = new SimpleComponentPool();
		
		OurContext context = new OurContext();
		
		Object component = new Fruit();
		
		test.registerComponent(
				new ComponentTrinity(
						component, 
						new FruitProxy(), 
						context)
				, null);
		
		test.save(component);
		
		assertFalse(context.session.persister.saved);
	}
	
   @Test
	public void testNoPersisterNoSave() throws ComponentPersistException {
		
		SimpleComponentPool test = new SimpleComponentPool();
		
		Object component = new Fruit();
		
		test.registerComponent(
				new ComponentTrinity(
						component, 
						new FruitProxy(), 
						new MockArooaContext() {
							@Override
							public ArooaSession getSession() {
								return new StandardArooaSession();
							}
						})
				, "fruit");
		
		test.save(component);
	}
	
	private class ConfigureContext extends MockArooaContext {

		boolean configured;
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {
				@Override
				public void configure() {
					configured = true;
				}
			};
		}
		
		@Override
		public ArooaSession getSession() {
			return new MockArooaSession() {
				@Override
				public BeanRegistry getBeanRegistry() {
					return new MockBeanRegistry() {
						@Override
						public void register(String id, Object component) {
						}
						@Override
						public Object lookup(String path) {
							return null;
						}
					};
				}
			};
		}
	}
	
   @Test
	public void testConfigure() {
		
		SimpleComponentPool test = new SimpleComponentPool();
		
		ConfigureContext context = new ConfigureContext();
		
		Object component = new Fruit();
		Object proxy = new FruitProxy();
		
		test.registerComponent(
				new ComponentTrinity(
						component, 
						proxy, 
						context)
				, "fruit");
		
		context.configured = false;
		
		test.configure(component);
		
		assertTrue(context.configured);
		
		context.configured = false;
		
		test.configure(proxy);
		
		assertTrue(context.configured);
	}

    @Test
    public void whenSameIdThenRegisteredWithNewId() {

	    SimpleComponentPool test = new SimpleComponentPool();

        OurContext ourContext = new OurContext();

        ComponentTrinity trinity1 =
                new ComponentTrinity(new Object(), new Object(), ourContext);
        ComponentTrinity trinity2 =
                new ComponentTrinity(new Object(), new Object(), ourContext);

	    String id1 = test.registerComponent(
	            trinity1,
                "someId");

        String id2 = test.registerComponent(
                trinity2,
                "someId");

        assertThat(id1, is("someId"));
        assertThat(id2, is("someId2"));

        assertThat(test.trinityForId("someId"), is(trinity1));
        assertThat(test.trinityForId("someId2"), is(trinity2));
    }
}
