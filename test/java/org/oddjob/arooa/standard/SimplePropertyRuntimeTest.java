package org.oddjob.arooa.standard;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.handlers.ElementAction;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.ConfigurationNodeListener;
import org.oddjob.arooa.runtime.MockConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeEvent;
import org.oddjob.arooa.runtime.RuntimeListener;

public class SimplePropertyRuntimeTest extends TestCase {

	public static class MockChild {
		
	}

	private class EmptySession extends MockArooaSession {
		
		@Override
		public ArooaDescriptor getArooaDescriptor() {
			return new MockArooaDescriptor() {
				@Override
				public ArooaBeanDescriptor getBeanDescriptor(
						ArooaClass forClass, PropertyAccessor accessor) {
					return null;
				}
			};
		}
		
		@Override
		public ArooaTools getTools() {
			return new StandardTools();
		}

	}



	
	private class ParentContext extends MockArooaContext {
		Object property;

		RuntimeListener listener;
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {
				@Override
				public void addRuntimeListener(
						RuntimeListener listener) {
					ParentContext.this.listener = listener;
				}
				
				@Override
				public void setProperty(String name, Object value) throws ArooaException {
					assertEquals("poperty name", "fruit", name);
					property = value;
				}
			};
		}
	}
	
	private class ChildConfiguration extends MockInstanceConfiguration {
		final Object wrappedObject = new MockChild();
		
		String elementTag;
		
		boolean inited;
		boolean configured;

		public ChildConfiguration() {
			super(new SimpleArooaClass(Object.class), 
					new Object(), 
					new MutableAttributes());
		}
		
		@Override
		Object getWrappedObject() {
			return wrappedObject;
		}

		@Override
		Object getObjectToSet() {
			return wrappedObject;
		}
		
		@Override
		void init(InstanceRuntime ourWrapper, ArooaContext context)
				throws ArooaException {
			this.inited = true;
			ourWrapper.getParentPropertySetter(
					).parentSetProperty(wrappedObject);		
		}
				
		@Override
		void listenerConfigure(InstanceRuntime ourWrapper, ArooaContext context)
				throws ArooaException {
			this.configured = true;
		}
	}
	
	private class TestContext extends MockArooaContext {
		SimplePropertyRuntime runtime;
		
		@Override
		public ArooaType getArooaType() {
			return ArooaType.VALUE;
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return runtime;
		}
				
		@Override
		public ArooaHandler getArooaHandler() {
			return runtime.getHandler();
		}
		
		@Override
		public PrefixMappings getPrefixMappings() {
			return null;
		}
		
		@Override
		public ArooaSession getSession() {
			return new EmptySession();
		}
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return new MockConfigurationNode() {
				@Override
				public int indexOf(ConfigurationNode child) {
					return 0;
				}
				
				@Override
				public void addNodeListener(ConfigurationNodeListener listener) {
				}
			};
		}
	}
	
	
	public void testAll() {

		ParentContext parentContext = new ParentContext();
		
		final ChildConfiguration child = new ChildConfiguration();
				
		ElementAction<InstanceConfiguration> elementAction =
			new ElementAction<InstanceConfiguration>() {
			public InstanceConfiguration onElement(ArooaElement element, ArooaContext context) {
				child.elementTag = element.getTag();
				return child;
			}
		};	
		
		SimplePropertyRuntime test = new SimplePropertyRuntime(
				elementAction, 
				new MockPropertyDefinition() {
					@Override
					public String getPropertyName() {
						return "fruit";
					}
					@Override
					public ArooaClass getPropertyType() {
						return new SimpleArooaClass(MockChild.class);
					}
				},
				parentContext);
		
		TestContext testContext = new TestContext();
		testContext.runtime = test;
		
		test.setContext(testContext);
			
		ArooaContext created = testContext.getArooaHandler(
				).onStartElement(
					new ArooaElement("fruit"),
					testContext);
		
		assertEquals("parent property", null, parentContext.property);
		
		created.getRuntime().init();
		
		assertEquals("child element", "fruit", child.elementTag	);
		
		assertFalse("configured", child.configured);
		
		test.init();		
		assertNotNull(parentContext.listener);
		
		assertTrue("Initialised.", child.inited);
				
		assertFalse("configured", child.configured);
		assertEquals("parent property", 
				child.wrappedObject, parentContext.property);

		parentContext.listener.beforeConfigure(
				new RuntimeEvent(parentContext.getRuntime()));
		
		assertTrue("configured.", child.configured);

	}
	
}
