package org.oddjob.arooa;



public class MockArooaBeanDescriptor implements ArooaBeanDescriptor {
	
	public ParsingInterceptor getParsingInterceptor() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	public String getTextProperty() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public String getComponentProperty() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public ConfiguredHow getConfiguredHow(String property) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public String getFlavour(String property) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public boolean isAuto(String property) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	@Override
	public ArooaAnnotations getAnnotations() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
}
