package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.xml.XMLArooaParser;

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

	private final HandleConfigurationSession delegate;
	
	private final ConfigurationHandle handle;
	
	public ConfigConfigurationSession(ArooaSession session, ArooaConfiguration configuration) {
		
		XMLArooaParser parser = new XMLArooaParser();
		
		try {
			handle = parser.parse(configuration);
		} catch (ArooaParseException e) {
			throw new RuntimeException(e);
		}

		delegate = new HandleConfigurationSession(session, handle);
		
	}
	
	public DragPoint dragPointFor(Object component) {
		
		return new DragConfiguration(handle.getDocumentContext().getConfigurationNode());

	}
	
	public boolean isModified() {
		return delegate.isModified();
	}
	
	public void addSessionStateListener(SessionStateListener listener) {
		delegate.addSessionStateListener(listener);
	}
	
	public void removeSessionStateListener(SessionStateListener listener) {
		delegate.removeSessionStateListener(listener);
	}
	
	public void save() throws ArooaParseException {
		delegate.save();
	}
	
	public ArooaDescriptor getArooaDescriptor() {
		return delegate.getArooaDescriptor();
	}
	
}
