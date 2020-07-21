package org.oddjob.arooa.parsing;

import org.oddjob.arooa.*;
import org.oddjob.arooa.xml.XMLArooaParser;

/**
 * A {@link ConfigurationSession} for an {@link ArooaConfiguration}.
 * <p>
 * This {@code ConfigurationSession} doesn't support component by component 
 * {@link DragPoint}s. Only the {@code DragPoint} for the root node is 
 * returned.
 * <p>
 * To support saving the configuration to the underlying configuration
 * structure - not directly to source of the configuration (to the file)
 * an intermediate the configuration is parsed and an intermediate 
 * {@link HandleConfigurationSession} is used.
 * 
 * @see HandleConfigurationSession
 * 
 * @author rob
 *
 */
public class ConfigConfigurationSession implements ConfigurationSession {

	private final HandleConfigurationSession delegate;
	
	private final ConfigurationHandle<?> handle;
	
	public ConfigConfigurationSession(ArooaSession session, ArooaConfiguration configuration) {
		
		XMLArooaParser parser = new XMLArooaParser(session.getArooaDescriptor());
		
		try {
			handle = parser.parse(configuration);
		} catch (ArooaParseException e) {
			throw new RuntimeException(e);
		}

		delegate = new HandleConfigurationSession(session, handle);
		
	}
	
	public DragPoint dragPointFor(Object component) {
		
		return new DragConfiguration(handle.getDocumentContext().getConfigurationNode(),
				delegate.getArooaDescriptor());

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
