package org.oddjob.arooa.life;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oddjob.arooa.ClassResolver;

/**
 * A {@link ClassResolver} that just delegates to a standard
 * <code>ClassLoader</code>
 * 
 * @author rob
 *
 */
public class ClassLoaderClassResolver implements ClassResolver {

	private static final Map<String, Class<?>> PRIMATIVES =
			new HashMap<String, Class<?>>();

	static {
		PRIMATIVES.put(boolean.class.getName(), boolean.class);
		PRIMATIVES.put(byte.class.getName(), byte.class);
		PRIMATIVES.put(short.class.getName(), short.class);
		PRIMATIVES.put(char.class.getName(), char.class);
		PRIMATIVES.put(int.class.getName(), int.class);
		PRIMATIVES.put(long.class.getName(), long.class);
		PRIMATIVES.put(float.class.getName(), float.class);
		PRIMATIVES.put(double.class.getName(), double.class);
	}
	
	private final ClassLoader classLoader;
	
	public ClassLoaderClassResolver(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	public Class<?> findClass(String className) {
		
		Class<?> maybe = PRIMATIVES.get(className);
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
