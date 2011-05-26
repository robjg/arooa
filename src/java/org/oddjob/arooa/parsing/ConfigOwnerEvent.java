package org.oddjob.arooa.parsing;

import java.util.EventObject;

/**
 * Event for a {@link ConfigurationOwner}. 
 * 
 * @author rob
 *
 */
public class ConfigOwnerEvent extends EventObject {
	private static final long serialVersionUID = 2009090200L;
	
	public ConfigOwnerEvent(ConfigurationOwner source) {
		super(source);
	}

	@Override
	public ConfigurationOwner getSource() {
		return (ConfigurationOwner) super.getSource();
	}
}
