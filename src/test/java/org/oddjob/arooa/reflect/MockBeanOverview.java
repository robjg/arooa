package org.oddjob.arooa.reflect;

abstract public class MockBeanOverview implements BeanOverview {

	public Class<?> getBeanClass() {
		throw new RuntimeException("Unsupported");
	}
	
	public String[] getProperties() {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}
	
	public Class<?> getPropertyType(String property) {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}
	
	public boolean hasReadableProperty(String property) {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}
	
	public boolean hasWriteableProperty(String property) {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}
	
	public boolean isIndexed(String property) {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}
	
	public boolean isMapped(String property) {
		throw new RuntimeException("Unexpected from " + 
				this.getClass().getName());
	}
	
}
