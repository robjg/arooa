package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaException;

public class ModificationRefusedException extends ArooaException {
	private static final long serialVersionUID = 2009013000L;

	private final ConfigurationNodeEvent configurationEvent;
	
	public ModificationRefusedException(ConfigurationNodeEvent configurationEvent) {
		this(null, configurationEvent);
	}

	public ModificationRefusedException(String message,
			ConfigurationNodeEvent configurationEvent) {
		super(message);
		this.configurationEvent = configurationEvent;
	}
	
	public ConfigurationNodeEvent getConfigurationEvent() {
		return configurationEvent;
	}
}
