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

	public enum Change {
		SESSION_CREATED,
		SESSION_DESTROYED
	}
	
	private final Change change;
	
	public ConfigOwnerEvent(ConfigurationOwner source, Change change) {
		super(source);
		
		if (change == null) {
			throw new NullPointerException("No change type");
		}
		
		this.change = change;
	}

	@Override
	public ConfigurationOwner getSource() {
		return (ConfigurationOwner) super.getSource();
	}
	
	
	public Change getChange() {
		return change;
	}
	
}
