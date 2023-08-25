package org.oddjob.arooa.life;

import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.utils.ClassUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
		
		Class<?> maybe = ClassUtils.primitiveNameToTypeMap.get(className);
		if (maybe != null) {
			return maybe;
		}
		
		try {
			return Class.forName(className, true, classLoader);
		} 
		catch (ClassNotFoundException e) {
			return null;
		}
		catch (NoClassDefFoundError e) {
			// When the case doesn't match on Windows.
			return null;
		}
	}
	
	public URL getResource(String resource) {
		return classLoader.getResource(resource);
	}
	
	public URL[] getResources(String resource) {
		List<URL> results = new ArrayList<>();
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
