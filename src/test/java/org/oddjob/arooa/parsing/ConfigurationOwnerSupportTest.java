package org.oddjob.arooa.parsing;

import org.junit.Test;

import org.junit.Assert;

public class ConfigurationOwnerSupportTest extends Assert {

	
   @Test
	public void testNotifications() {		
				
		final ConfigurationOwnerSupport test = 
			new ConfigurationOwnerSupport(new MockConfigurationOwner());
		
		class OurListener implements OwnerStateListener {
			ConfigurationSession session;
			ConfigOwnerEvent event;
			
			@Override
			public void sessionChanged(ConfigOwnerEvent event) {
				this.event = event;
				this.session = test.provideConfigurationSession();
			}
		}
		
		OurListener listener = new OurListener();
		
		test.addOwnerStateListener(listener);
		
		test.setConfigurationSession(new MockConfigurationSession());
		
		assertEquals(ConfigOwnerEvent.Change.SESSION_CREATED, 
				listener.event.getChange());
		assertNotNull(listener.session);
		
		test.setConfigurationSession(null);
		
		assertEquals(ConfigOwnerEvent.Change.SESSION_DESTROYED, 
				listener.event.getChange());
		assertNull(listener.session);
		
		
	}
	
}
