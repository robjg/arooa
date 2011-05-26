package org.oddjob.arooa.registry;


public class MockBeanRegistry implements BeanRegistry {

	public String getIdFor(Object component) {
		throw new RuntimeException("Unexpected in " + 
				this.getClass().getName() + ".");
	}

	public Object lookup(String path) {
		throw new RuntimeException("Unexpected in " + 
				this.getClass().getName() + ".");
	}

	public <T> T lookup(String path, Class<T> required) {
		throw new RuntimeException("Unexpected in " + 
				this.getClass().getName() + ".");
	}
	
	public void register(String id, Object component) {
		throw new RuntimeException("Unexpected in " + 
				this.getClass().getName() + ".");
	}

	public void remove(Object component) {
		throw new RuntimeException("Unexpected in " + 
				this.getClass().getName() + ".");
	}

	public <T> Iterable<T> getAllByType(Class<T> type) {
		throw new RuntimeException("Unexpected in " + 
				this.getClass().getName() + ".");
	}
}
