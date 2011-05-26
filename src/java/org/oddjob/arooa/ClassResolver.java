package org.oddjob.arooa;

import java.net.URL;

/**
 * Facade for ClassLoader related activities. 
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
	public Class<?> findClass(String className);

	/**
	 * Find a resource.
	 * 
	 * @param resource The resource name.
	 * @return The URL of the first found, or null if none
	 * can be found.
	 */
	public URL getResource(String resource);
	
	/**
	 * Find all resources by name. The resulting array
	 * should not contain duplicates.
	 * 
	 * @param resource The resource name.
	 * @return An array of results. May be empty but not null.
	 */
	public URL[] getResources(String resource); 
	
	
	
	public ClassLoader[] getClassLoaders();
}
