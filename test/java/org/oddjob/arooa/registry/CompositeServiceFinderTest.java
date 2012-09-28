package org.oddjob.arooa.registry;

import junit.framework.TestCase;

public class CompositeServiceFinderTest extends TestCase {

	private static class Cheese {}
	
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
