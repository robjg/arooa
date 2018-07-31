package org.oddjob.arooa.registry;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.oddjob.arooa.ComponentTrinity;
import org.oddjob.arooa.parsing.MockArooaContext;

public class ComponentsServiceFinderTest extends Assert {

	private static final Logger logger = 
			LoggerFactory.getLogger(ComponentsServiceFinderTest.class);
	
	@Rule public TestName name = new TestName();

	public String getName() {
        return name.getMethodName();
    }

	interface FruitService {}
	
   @Before
   public void setUp() throws Exception {

		
		logger.info("-----------------------  " + getClass().getName() + 
				"#" + getName() + "  -----------------------"); 
	}
	
	private class TheServices implements Services {
		
		private final FruitService fruitService;

		public TheServices(FruitService fruitService) {
			this.fruitService = fruitService;
		}
		
		@Override
		public Object getService(String serviceName)
				throws IllegalArgumentException {
			if ("FRUIT".equals(serviceName)) {
					return fruitService;
			}
			else {
				throw new IllegalArgumentException(serviceName);
			}
		}
		@Override
		public String serviceNameFor(Class<?> theClass, String flavour) {
			if (theClass.isAssignableFrom(FruitService.class)) {
				return "FRUIT";
			}
			return null;
		}
	}
	
	class AppleService implements ServiceProvider, FruitService { 
		
		@Override
		public Services getServices() {
			return new TheServices(this);
		}
	}
	
	
	class OrangeService implements ServiceProvider, FruitService { 
		
		@Override
		public Services getServices() {
			return new TheServices(this);
		}
	}
	
   @Test
	public void testTwoMatches() {
		
		SimpleComponentPool components = new SimpleComponentPool();
		
		AppleService appleService = new AppleService();
		
		OrangeService orangeService = new OrangeService();
		
		components.registerComponent(
				new ComponentTrinity(appleService, 
						appleService, new MockArooaContext()), 
				null);
		
		components.registerComponent(
				new ComponentTrinity(orangeService, 
						orangeService, new MockArooaContext()), 
				null);
		
		ComponentsServiceFinder test = new ComponentsServiceFinder(
				components);
		
		Object result = test.find(FruitService.class, null);
		
		assertEquals(appleService, result);
	}
}
