package org.oddjob.arooa.registry;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

public class DirectoryServiceFinderTest extends TestCase {

	private static final Logger logger = 
			Logger.getLogger(DirectoryServiceFinderTest.class);
	
	interface FruitService {}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
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
	
	public void testTwoMatches() {
		
		SimpleBeanRegistry directory = new SimpleBeanRegistry();
		
		AppleService appleService = new AppleService();
		
		OrangeService orangeService = new OrangeService();
		
		
		directory.register("apples", appleService);
		
		directory.register("oranges", orangeService);
		
		DirectoryServiceFinder test = new DirectoryServiceFinder(
				directory);
		
		Object result = test.find(FruitService.class, null);
		
		assertEquals(appleService, result);
	}
}