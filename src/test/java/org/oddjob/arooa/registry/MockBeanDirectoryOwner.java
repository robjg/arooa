package org.oddjob.arooa.registry;


public class MockBeanDirectoryOwner implements BeanDirectoryOwner {

	@Override
	public BeanDirectory provideBeanDirectory() {
		throw new RuntimeException("Unexpectd from " + getClass().getName());
	}
	
//	@Override
//	public PropertyLookup getPropertyLookup() {
//		throw new RuntimeException("Unexpectd from " + getClass().getName());
//	}
}
