package org.oddjob.arooa.parsing;

public class MockConfigurationOwner implements ConfigurationOwner {

	public ConfigurationSession provideConfigurationSession() {
		throw new RuntimeException("Unexpected from class " +
				getClass().getName());
	}
	
	public void addOwnerStateListener(OwnerStateListener listener) {
		throw new RuntimeException("Unexpected from class " +
				getClass().getName());
	}
	
	public void removeOwnerStateListener(OwnerStateListener listener) {
		throw new RuntimeException("Unexpected from class " +
				getClass().getName());
	}
}
