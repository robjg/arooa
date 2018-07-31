package org.oddjob.arooa.registry;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.Assert;

public class CompositeServiceFinderTest extends Assert {

	private static final Logger logger = 
			LoggerFactory.getLogger(CompositeServiceFinderTest.class);
	
	@Rule public TestName name = new TestName();

	public String getName() {
        return name.getMethodName();
    }

	private static class Cheese {}
	
    @Before
    public void setUp() throws Exception {

		
		logger.info("-----------------------  " + getClass().getName() + 
				"#" + getName() + "  -----------------------"); 
	}
	
   @Test
	public void testFirstHasService() {
		
		ServiceFinder test = new CompositeServiceFinder(new ServiceFinder[] {
			new ServiceFinder() {
				
				@Override
				public <T> T find(Class<T> cl, String flavour) {
					assertEquals(Cheese.class, cl);
					assertEquals("smelly", flavour);
					return cl.cast(new Cheese());
				}
			},
			new ServiceFinder() {
				
				@Override
				public <T> T find(Class<T> cl, String flavour) {
					throw new RuntimeException("Unexpected");
				}
			}
		});
		
		assertNotNull(test.find(Cheese.class, "smelly"));
	}
	
   @Test
	public void testSecondHasService() {
		
		ServiceFinder test = new CompositeServiceFinder(new ServiceFinder[] {
			new ServiceFinder() {
				
				@Override
				public <T> T find(Class<T> cl, String flavour) {
					assertEquals(Cheese.class, cl);
					assertEquals("smelly", flavour);
					return null;
				}
			},
			new ServiceFinder() {
				
				@Override
				public <T> T find(Class<T> cl, String flavour) {
					assertEquals(Cheese.class, cl);
					assertEquals("smelly", flavour);
					return cl.cast(new Cheese());
				}
			}
		});
		
		assertNotNull(test.find(Cheese.class, "smelly"));
	}

   @Test
	public void testNoneHasService() {
		
		ServiceFinder test = new CompositeServiceFinder(new ServiceFinder[] {
			new ServiceFinder() {
				
				@Override
				public <T> T find(Class<T> cl, String flavour) {
					assertEquals(Cheese.class, cl);
					assertEquals("smelly", flavour);
					return null;
				}
			},
			new ServiceFinder() {
				
				@Override
				public <T> T find(Class<T> cl, String flavour) {
					assertEquals(Cheese.class, cl);
					assertEquals("smelly", flavour);
					return null;
				}
			}
		});
		
		assertNull(test.find(Cheese.class, "smelly"));
	}
}
