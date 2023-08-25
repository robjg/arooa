package org.oddjob.arooa;

import org.oddjob.arooa.life.ClassLoaderClassResolver;

import java.net.URL;

/**
 * Facade for ClassLoader related activities. Provides for loading of classes and resources across Oddballs.
 *
 * @see ArooaDescriptor
 *
 * @author rob
 *
 */
public interface ClassResolver {

	/**
	 * Find a class.
	 * 
	 * @param className The fully qualified class name.
	 * @return The class, or null if it can't be found.
	 */
	Class<?> findClass(String className);

	/**
	 * Find a resource.
	 * 
	 * @param resource The resource name.
	 * @return The URL of the first found, or null if none
	 * can be found.
	 */
	URL getResource(String resource);
	
	/**
	 * Find all resources by name. The resulting array
	 * should not contain duplicates.
	 * 
	 * @param resource The resource name.
	 * @return An array of results. May be empty but not null.
	 */
	URL[] getResources(String resource);


	/**
	 * Provide internal class loaders used.
	 *
	 * @return An Array of class loaders.
	 */
	ClassLoader[] getClassLoaders();

	/**
	 * Provide a wrapper for the class loader that loaded this class. This is a convenience method where
	 * a Class Resolver is required but not for Oddballs. This doesn't check the Context class
	 * loader - Maybe it should?
	 *
	 * @return A default Class Resolver.
	 */
	static ClassResolver getDefaultClassResolver() {

		return new ClassLoaderClassResolver(ClassResolver.class.getClassLoader());
	}

}
