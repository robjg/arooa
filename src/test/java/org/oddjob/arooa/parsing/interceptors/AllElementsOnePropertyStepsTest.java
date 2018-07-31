package org.oddjob.arooa.parsing.interceptors;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.runtime.RuntimeEvent;
import org.oddjob.arooa.runtime.RuntimeListener;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.MockConfigurationNode;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.ConfigurationNode;

public class AllElementsOnePropertyStepsTest extends Assert {

	class SnackRuntime extends MockRuntimeConfiguration {		
		RuntimeListener listener;
		
		@Override
		public void addRuntimeListener(RuntimeListener listener) {
			this.listener = listener;
		}
	}
	
	/**
	 * Context for snack.
	 */
	class SnackContext extends MockArooaContext {
		SnackRuntime snack;
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return snack;
		}
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return new MockConfigurationNode();
		}
		
		@Override
		public ArooaHandler getArooaHandler() {
			return new ArooaHandler() {
				public ArooaContext onStartElement(ArooaElement element, ArooaContext parentContext) throws ArooaException {
					
					assertEquals("fruit", element.getTag());								

					final FruitContext fruitContext = new FruitContext();
					FruitRuntime fruitRuntime = new FruitRuntime();
					fruitContext.runtime = fruitRuntime;
					
					parentContext.getRuntime().addRuntimeListener(
							new RuntimeListener() {
								public void beforeInit(RuntimeEvent event)
										throws ArooaException {
									fruitContext.getRuntime().init();
								}
								public void afterInit(RuntimeEvent event)
										throws ArooaException {
								}
								public void beforeConfigure(RuntimeEvent event)
										throws ArooaException {
									fruitContext.getRuntime().configure();
								}
								public void afterConfigure(
										RuntimeEvent event)
										throws ArooaException {
								}
								public void beforeDestroy(RuntimeEvent event)
										throws ArooaException {
									fruitContext.getRuntime().destroy();
								}
								public void afterDestroy(
										RuntimeEvent event)
										throws ArooaException {
								}
							});
					
					return fruitContext;
				}
			};
		}
	}
	
	class FruitRuntime extends MockRuntimeConfiguration {
		RuntimeConfiguration childRuntime;
		
		boolean inited;
		boolean configured;

		RuntimeListener listener;
		
		@Override
		public void addRuntimeListener(RuntimeListener listener) {
			this.listener = listener;
		}
		
		public void init() throws ArooaException {
			listener.beforeInit(new RuntimeEvent(this));
			inited = true;
		}
		
		@Override
		public void configure() {
			listener.beforeConfigure(new RuntimeEvent(this));
			this.configured = true;
		}	
	}
	
	class FruitContext extends MockArooaContext {
		
		FruitRuntime runtime;
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return runtime;
		}
		
		@Override
		public ArooaHandler getArooaHandler() {
			return new ArooaHandler() {
				
				public ArooaContext onStartElement(ArooaElement element,
						ArooaContext parentContext) throws ArooaException {
					
					assertEquals("apple", element.getTag());
					assertEquals("red", element.getAttributes().get("colour"));

					final AppleContext appleContext = new AppleContext();
					AppleRuntime appleRuntime = new AppleRuntime();
					appleContext.runtime = appleRuntime;
					
					parentContext.getRuntime().addRuntimeListener(
							new RuntimeListener() {
								public void beforeInit(RuntimeEvent event)
										throws ArooaException {
									appleContext.getRuntime().init();
								}
								public void afterInit(RuntimeEvent event)
										throws ArooaException {
								}
								
								public void beforeConfigure(RuntimeEvent event)
										throws ArooaException {
									appleContext.getRuntime().configure();
								}
								public void afterConfigure(
										RuntimeEvent event)
										throws ArooaException {
								}
								
								public void beforeDestroy(RuntimeEvent event)
										throws ArooaException {
									appleContext.getRuntime().destroy();
								}
								public void afterDestroy(
										RuntimeEvent event)
										throws ArooaException {
								}
							});
					
					return appleContext;					
				}
				
			};
		}
	}
	
	class AppleRuntime extends MockRuntimeConfiguration  {
		boolean appleInited;
		boolean configured;

		@Override
		public void init() throws ArooaException {
			appleInited = true;
		}
		
		@Override
		public void configure() {
			configured = true;
		}
	}
	
	class AppleContext extends MockArooaContext {
		
		AppleRuntime runtime;
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return runtime;
		}
		
	}
	
	
	/**
	 * This test simulate the situation where snack has a property
	 * fruit, but it's the only property. It accepts any value.
	 * 
	 * <snack>
	 *   <apple colour="red"/>
	 * </snack>
	 */
   @Test
	public void testAllInSteps() {
		
		OnePropertyInterceptor test = new OnePropertyInterceptor(); 
		test.setProperty("fruit");
		
		SnackContext snackContext = new SnackContext();
		SnackRuntime snackRuntime = new SnackRuntime();
		snackContext.snack = snackRuntime;

		ArooaContext interceptedContext = test.intercept(snackContext);
				
		MutableAttributes attributes = new MutableAttributes();
		attributes.set("colour", "red");
		
		// This simulates parser action on seeing apple element.
		ArooaContext appleContext = interceptedContext.getArooaHandler().onStartElement(
				new ArooaElement("apple", attributes), interceptedContext);
		
		AppleRuntime appleRuntime = (AppleRuntime) appleContext.getRuntime();
		
		
		snackRuntime.listener.beforeInit(new RuntimeEvent(snackRuntime));
		
		assertTrue("Apple inited", appleRuntime.appleInited);

		snackRuntime.listener.beforeConfigure(new RuntimeEvent(snackRuntime));
		
		assertTrue("Apple configured", appleRuntime.configured);
	}
	
}
