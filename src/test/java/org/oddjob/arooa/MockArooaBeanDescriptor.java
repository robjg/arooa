package org.oddjob.arooa;


import java.lang.annotation.Annotation;

public class MockArooaBeanDescriptor implements ArooaBeanDescriptor {

	@Override
	public ParsingInterceptor getParsingInterceptor() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public String getTextProperty() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public String getComponentProperty() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public ConfiguredHow getConfiguredHow(String property) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public String getFlavour(String property) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public Annotation getQualifier(String property) {
		throw new RuntimeException("Unexpected from class: " +
				this.getClass().getName());
	}

	@Override
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
