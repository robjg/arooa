package org.oddjob.arooa;

import java.net.URL;

public class MockClassResolver implements ClassResolver {

	public Class<?> findClass(String className) {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
	public URL getResource(String resource) {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
	public URL[] getResources(String resource) {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
	@Override
	public ClassLoader[] getClassLoaders() {
		throw new RuntimeException("Unexpected from " + getClass());
	}
}
