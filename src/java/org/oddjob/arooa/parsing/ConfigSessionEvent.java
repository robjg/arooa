package org.oddjob.arooa.parsing;

import java.util.EventObject;

/**
 * Event for a {@link ConfigurationSession}
 * 
 * @author rob
 *
 */
public class ConfigSessionEvent extends EventObject {
	private static final long serialVersionUID = 2009090200L;
	
	public ConfigSessionEvent(ConfigurationSession source) {
		super(source);
	}

	@Override
	public ConfigurationSession getSource() {
		return (ConfigurationSession) super.getSource();
	}
}
