package org.oddjob.arooa.runtime;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.reflect.ArooaClass;

public class AbstractRuntimeConfigurationTest extends Assert {

	class OurRuntime extends AbstractRuntimeConfiguration {

		public void init() {
			fireBeforeInit();
			
			fireAfterInit();
		}

		public void configure() {
			fireBeforeConfigure();
			
			fireAfterConfigure();
		}

		public void destroy() {
			fireBeforeDestroy();
			
			fireAfterDestroy();
		}

		public ArooaClass getClassIdentifier() {
			throw new RuntimeException("Unexpected.");
		}

		public void setIndexedProperty(String name, int index, Object value)
				throws ArooaException {
			throw new RuntimeException("Unexpected.");
		}

		public void setMappedProperty(String name, String key, Object value)
				throws ArooaException {
			throw new RuntimeException("Unexpected.");
		}

		public void setProperty(String name, Object value)
				throws ArooaException {
			throw new RuntimeException("Unexpected.");
		}
		
	}
	
   @Test
	public void testConcurrentRemoveListener() {

		final OurRuntime test = new OurRuntime();
		
		test.addRuntimeListener(new RuntimeListenerAdapter() {
			@Override
			public void afterDestroy(RuntimeEvent event) throws ArooaException {
				test.removeRuntimeListener(this);
			}
			
			@Override
			public void afterConfigure(RuntimeEvent event)
					throws ArooaException {
				throw new RuntimeException("Unexpected!");
			}
		});
		
		test.destroy();
		
		test.configure();
	}
	
	
   @Test
	public void testFireMethods() {

		final int results[] = new int[1];
		
		final OurRuntime test = new OurRuntime();
		
		test.addRuntimeListener(new RuntimeListener() {

			public void beforeInit(RuntimeEvent event) throws ArooaException {
				assertEquals(0, results[0]);
				results[0] = 1;
			}
			
			public void afterInit(RuntimeEvent event) throws ArooaException {
				assertEquals(1, results[0]);
				results[0] = results[0] | 2;
			}

			public void beforeConfigure(RuntimeEvent event)
					throws ArooaException {
				assertEquals(3, results[0]);
				results[0] = results[0] | 4;
			}

			public void afterConfigure(RuntimeEvent event)
					throws ArooaException {
				assertEquals(7, results[0]);
				results[0] = results[0] | 8;
			}

			public void beforeDestroy(RuntimeEvent event) throws ArooaException {
				assertEquals(15, results[0]);
				results[0] = results[0] | 16;
			}
			
			public void afterDestroy(RuntimeEvent event) throws ArooaException {
				assertEquals(31, results[0]);
				results[0] = results[0] | 32;
			}
		});
				
		test.init();
		test.configure();
		test.destroy();
		
		assertEquals(63, results[0]);
	}
}
