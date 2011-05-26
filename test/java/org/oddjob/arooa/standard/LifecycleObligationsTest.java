package org.oddjob.arooa.standard;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.ComponentTrinity;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.deploy.NullArooaDescriptor;
import org.oddjob.arooa.life.ArooaContextAware;
import org.oddjob.arooa.life.ArooaLifeAware;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.registry.MockBeanRegistry;
import org.oddjob.arooa.registry.MockComponentPool;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeListener;

public class LifecycleObligationsTest extends TestCase {

	private List<String> events = new ArrayList<String>();
	
	private class ParentContext extends MockArooaContext {
		RuntimeListener listener;
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {
				@Override
				public void addRuntimeListener(
						RuntimeListener listener) {
					assertNull(ParentContext.this.listener);
					ParentContext.this.listener = listener;
				}
				@Override
				public void removeRuntimeListener(RuntimeListener listener) {
				}
				@Override
				public void setProperty(String name, Object value)
						throws ArooaException {
					assertNull(name);
					events.add("Property set: " + value);					
				}
			};
		}
		
		@Override
		public ArooaSession getSession() {
			return new MockArooaSession() {
				
				@Override
				public ArooaTools getTools() {
					return new StandardTools();
				}
			};
		}
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return new MockConfigurationNode() {
				@Override
				public int indexOf(ConfigurationNode child) {
					return 0;
				}
				@Override
				public void removeChild(int index) {
				}
			};
		}
	}
	
	private class OurContext extends MockArooaContext {
		
		RuntimeConfiguration runtime; 
		
		ArooaContext registeredContext;
		
		@Override
		public ArooaSession getSession() {
			return new MockArooaSession() {
				@Override
				public ArooaDescriptor getArooaDescriptor() {
					return new NullArooaDescriptor();
				}
				
				@Override
				public ComponentPool getComponentPool() {
					return new MockComponentPool() {
						@Override
						public void registerComponent(ComponentTrinity trinity,
								String id) {
							registeredContext = trinity.getTheContext();
						}
						@Override
						public void remove(Object component) {
						}
					};
				}
				
				@Override
				public ArooaTools getTools() {
					return new StandardTools();
				}
				
				@Override
				public BeanRegistry getBeanRegistry() {
					return new MockBeanRegistry() {
						@Override
						public void remove(Object component) {
						}
					};
				}
			};
		}
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return new MockConfigurationNode() {
			};
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			if (runtime == null) {
				throw new RuntimeException("Unsupported.");
			}
			return runtime;
		}
	}
	
	private class OurLiffecycleThing implements ArooaLifeAware {
		
		public void initialised() {
			events.add("Thing initialised");
		}
		
		public void configured() {
			events.add("Thing configured");
		}
		
		public void destroy() {
			events.add("Thing destroy");
		}
		
		@Override
		public String toString() {
			return "OurThing";
		}
	}
	
	public void testLifecycle() {
		OurLiffecycleThing check = new OurLiffecycleThing();
		
		ParentContext parentContext = new ParentContext();
		
		InstanceRuntime rt = new SimpleInstanceRuntime(
				new ObjectConfiguration(
						new SimpleArooaClass(check.getClass()), 
						check, new MutableAttributes()),
				parentContext);
		
		OurContext ourContext = new OurContext();
		ourContext.runtime = rt;
		
		rt.setContext(ourContext);

		assertEquals(0, events.size());
		
		ourContext.getRuntime().init();
		
		assertEquals("Thing initialised", events.get(0));
		assertEquals(1, events.size());
						
		ourContext.getRuntime().configure();
		
		assertEquals("Thing configured", events.get(1));
		
		assertEquals("Property set: OurThing", events.get(2));
		assertEquals(3, events.size());
		
		ourContext.getRuntime().destroy();
		
		assertEquals("Thing destroy", events.get(3));
		assertEquals("Property set: null", events.get(4));
		assertEquals(5, events.size());
	}
	
	public void testProxyLifecycle() {
		OurLiffecycleThing check = new OurLiffecycleThing();

		ParentContext parentContext = new ParentContext();
		
		PropertyAccessor accessor = parentContext.getSession(
			).getTools().getPropertyAccessor();

		SimpleArooaClass.class.getClassLoader();
		
		ArooaClass arooaClass = accessor.getClassName(check);
		
		assertNotNull(arooaClass);
		
		InstanceRuntime rt = new SimpleInstanceRuntime(
				new ComponentConfiguration(
						arooaClass,
						new Object(), 
						check, 
						new MutableAttributes()),
				parentContext);
		
		OurContext ourContext = new OurContext();
		ourContext.runtime = rt;
		rt.setContext(ourContext);
		
		assertEquals(0, events.size());
		
		assertNull(ourContext.registeredContext);

		ourContext.getRuntime().init();
		
		assertTrue(ourContext == ourContext.registeredContext);
		
		assertEquals("Thing initialised", events.get(0));
		assertEquals("Property set: OurThing", events.get(1));
		assertEquals(2, events.size());
		
		ourContext.getRuntime().configure();
		
		assertEquals("Thing configured", events.get(2));
		
		assertEquals(3, events.size());
		
		ourContext.getRuntime().destroy();
		
		assertEquals("Thing destroy", events.get(3));
		assertEquals("Property set: null", events.get(4));
		assertEquals(5, events.size());
	}
	
	private class OurSessionThing implements ArooaSessionAware {
		ArooaSession session;
		
		public void setArooaSession(ArooaSession session) {
			this.session = session;
		}
	}
	
	public void testSession() {
		OurSessionThing check = new OurSessionThing();
		
		ParentContext parentContext = new ParentContext();
		
		InstanceRuntime rt = new SimpleInstanceRuntime(
				new ObjectConfiguration(
						new SimpleArooaClass(check.getClass()), 
						check, new MutableAttributes()),
				parentContext);
		
		rt.setContext(new OurContext());
		
		assertNotNull(check.session);
	}

	private class OurContextThing implements ArooaContextAware {
		ArooaContext context;

		public void setArooaContext(ArooaContext context) {
			this.context = context;
		}
	}
	
	public void testContext() {
		OurContextThing check = new OurContextThing();
		
		ParentContext parentContext = new ParentContext();
		
		InstanceRuntime rt = new SimpleInstanceRuntime(
				new ObjectConfiguration(
						new SimpleArooaClass(check.getClass()), 
						check, new MutableAttributes()),
				parentContext);
		
		rt.setContext(new OurContext());
		
		assertNotNull(check.context);
	}
}
