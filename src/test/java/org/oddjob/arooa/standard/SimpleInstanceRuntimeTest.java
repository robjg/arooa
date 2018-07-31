package org.oddjob.arooa.standard;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeListener;

public class SimpleInstanceRuntimeTest extends Assert {

	ArooaSession session = new StandardArooaSession();

	private class ParentContext extends MockArooaContext {
	
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
   @Test
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
