package org.oddjob.arooa.standard;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.MockArooaTools;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.MockBeanOverview;
import org.oddjob.arooa.reflect.MockPropertyAccessor;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeListener;

public class SimpleInstanceRuntimeTest extends TestCase {

	private class OurCreateTestSession extends MockArooaSession {

		@Override
		public ArooaDescriptor getArooaDescriptor() {
			return new MockArooaDescriptor() {
				@Override
				public ArooaBeanDescriptor getBeanDescriptor(
						ArooaClass forClass, PropertyAccessor accessor) {
					if (forClass.forClass() == MockObject.class) {
						return null;
					}
					fail("Unexpected: " + forClass);
					return null;
				}
			};
			
		}

		@Override
		public ArooaTools getTools() {
			return new MockArooaTools() {

				@Override
				public PropertyAccessor getPropertyAccessor() {
					return new MockPropertyAccessor() {
						@Override
						public ArooaClass getClassName(Object bean) {
							return new SimpleArooaClass(bean.getClass());
						}
						@Override
						public BeanOverview getBeanOverview(
								Class<?> forClass) throws ArooaException {
							return new MockBeanOverview() {
								@Override
								public String[] getProperties() {
									return new String[0];
								}
							};
						}
					};					
				}
			};
		}
		
	}


	private class ParentContext extends MockArooaContext {
		OurCreateTestSession session = new OurCreateTestSession();
	
		Object value;
		
		@Override
		public ArooaSession getSession() {
			return session;
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {
				@Override
				public void addRuntimeListener(
						RuntimeListener listener) {
				}
				
				@Override
				public void setProperty(String name, Object value)
						throws ArooaException {
					assertEquals(null, name);
					ParentContext.this.value = value;
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
			};
		}
	}
	
	private class OurContext extends MockArooaContext {
		OurCreateTestSession session = new OurCreateTestSession();

		RuntimeConfiguration runtime;
		
		@Override
		public ArooaSession getSession() {
			return session;
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return runtime;
		}
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return new MockConfigurationNode() {
			};
		}
	}	
	
	private class MockObject {
		// public void setFruit();
	}
	
	/**
	 * Test that an SimpleInstanceRuntime sets parent property correctly.
	 *
	 */
	public void testSetProperty() {

		ParentContext parentContext = new ParentContext();
		
		ObjectConfiguration instance = new ObjectConfiguration(
				new SimpleArooaClass(MockObject.class), new MockObject(), new MutableAttributes());
		
		SimpleInstanceRuntime test = new SimpleInstanceRuntime(
				instance, parentContext);
		
		OurContext ourContext = new OurContext();
		ourContext.runtime = test;

		test.setContext(ourContext);
		
		assertNull("Parent property not set.", parentContext.value);

		test.init();
		
		assertNull("Parent property not set.", parentContext.value);
		
		test.configure();
		
		assertNotNull("Parent property set.", parentContext.value);
	}
	
}
