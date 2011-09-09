package org.oddjob.arooa.parsing;

import java.util.List;

import org.oddjob.arooa.utils.ListenerSupportBase;

/**
 * Helper class for {@link ConfigurationOwner}s. Tracks and notifies 
 * listeners.
 * 
 * @author rob
 *
 */
public class ConfigurationOwnerSupport extends ListenerSupportBase<OwnerStateListener> {

	private final ConfigurationOwner source;
	
	private ConfigurationSession session;
	
	public ConfigurationOwnerSupport(ConfigurationOwner owner) {
		this.source = owner;
	}
	
	public void setConfigurationSession(ConfigurationSession session) {
		this.session = session;
		
		List<OwnerStateListener> copy = copy();
		for (OwnerStateListener listener : copy) {
			listener.sessionChanged(new ConfigOwnerEvent(source));
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
