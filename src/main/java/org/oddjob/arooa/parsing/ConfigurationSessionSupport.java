package org.oddjob.arooa.parsing;

import java.util.List;

import org.oddjob.arooa.utils.ListenerSupportBase;

/**
 * Helper class for {@link ConfigurationSession}s. Tracks and notifies 
 * listeners.
 * 
 * @author rob
 *
 */
public class ConfigurationSessionSupport extends ListenerSupportBase<SessionStateListener> {

	private final ConfigurationSession source;
	
	public ConfigurationSessionSupport(ConfigurationSession session) {
		this.source = session;
	}
	
	public void saved() {
		List<SessionStateListener> copy = copy();
		for (SessionStateListener listener : copy) {
			listener.sessionSaved(new ConfigSessionEvent(source));
		}
	}
	
	public void modified() {
		List<SessionStateListener> copy = copy();
		for (SessionStateListener listener : copy) {
			listener.sessionModified(new ConfigSessionEvent(source));
		}
	}
	
	public void addSessionStateListener(SessionStateListener listener) {
		addListener(listener);
	}
	
	public void removeSessionStateListener(SessionStateListener listener) {
		removeListener(listener);
	}
}
