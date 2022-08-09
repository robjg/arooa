package org.oddjob.arooa.parsing;

import org.oddjob.arooa.utils.ListenerSupportBase;

import java.util.List;

/**
 * Helper class for {@link ConfigurationOwner}s. Tracks and notifies 
 * listeners.
 * 
 * @author rob
 *
 */
public class ConfigurationOwnerSupport extends ListenerSupportBase<OwnerStateListener> {

	private final ConfigurationOwner source;
	
	private volatile ConfigurationSession session;
	
	public ConfigurationOwnerSupport(ConfigurationOwner owner) {
		this.source = owner;
	}
	
	public void setConfigurationSession(ConfigurationSession session) {
		if (this.session == session) {
			return;
		}
		
		this.session = session;
		
		List<OwnerStateListener> copy = copy();
		for (OwnerStateListener listener : copy) {
			ConfigOwnerEvent event;
			if (session == null) {
				event = new ConfigOwnerEvent(source, 
						ConfigOwnerEvent.Change.SESSION_DESTROYED);
			}
			else {
				event = new ConfigOwnerEvent(source, 
						ConfigOwnerEvent.Change.SESSION_CREATED);
			}
			listener.sessionChanged(event);
		}
	}
	
	public ConfigurationSession provideConfigurationSession() {
		return session;
	}
	
	public void addOwnerStateListener(OwnerStateListener listener) {
		addListener(listener);
	}
	
	public void removeOwnerStateListener(OwnerStateListener listener) {
		removeListener(listener);
	}	
}
