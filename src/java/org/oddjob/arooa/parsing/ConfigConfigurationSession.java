package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;

/**
 * A {@link ConfigurationSession} for an {@link ArooaConfiguration}.
 * <p>
 * This need some work because saving is straight through from 
 * editor so this doesn't support modification notifications or the
 * ability to save.
 * 
 * @author rob
 *
 */
public class ConfigConfigurationSession implements ConfigurationSession {

	private final ArooaSession session;
	
	private final ArooaConfiguration configuration;

	public ConfigConfigurationSession(ArooaSession session, ArooaConfiguration configuration) {
		this.session = session;
		this.configuration = configuration;
	}
	
	public DragPoint dragPointFor(Object component) {
		
		return new DragConfiguration(configuration);
	}
	
	public boolean isModified() {
		return false;
	}
	
	public void addSessionStateListener(SessionStateListener listener) {
	}
	
	public void removeSessionStateListener(SessionStateListener listener) {
	}
	
	public void save() throws ArooaParseException {
		
	}
	
	public ArooaDescriptor getArooaDescriptor() {
		return session.getArooaDescriptor();
	}
	
}
