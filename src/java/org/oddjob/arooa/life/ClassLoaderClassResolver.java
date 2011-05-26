package org.oddjob.arooa.life;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.oddjob.arooa.ClassResolver;

/**
 * A {@link ClassResolver} that just delegates to a standard
 * <code>ClassLoader</code>
 * 
 * @author rob
 *
 */
public class ClassLoaderClassResolver implements ClassResolver {

	private final ClassLoader classLoader;
	
	public ClassLoaderClassResolver(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	public Class<?> findClass(String className) {
		try {
			return Class.forName(className, true, classLoader);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	public URL getResource(String resource) {
		return classLoader.getResource(resource);
	}
	
	public URL[] getResources(String resource) {
		List<URL> results = new ArrayList<URL>();
		try {
			Enumeration<URL> e = classLoader.getResources(resource);
			while (e.hasMoreElements()) {
				results.add(e.nextElement());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return results.toArray(new URL[0]);
	}
	
	@Override
	public ClassLoader[] getClassLoaders() {
		return new ClassLoader[] { classLoader };
	}
}
