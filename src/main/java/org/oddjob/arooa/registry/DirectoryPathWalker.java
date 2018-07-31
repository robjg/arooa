package org.oddjob.arooa.registry;

import org.oddjob.arooa.reflect.ArooaPropertyException;

public class DirectoryPathWalker {

	private final BeanDirectory directory;
	
	public DirectoryPathWalker(BeanDirectory directory) {
		this.directory = directory;
	}

	public BeanDirectory directoryForPath(Path path) 
	throws ArooaPropertyException {
		
		if (path == null) {
			return null;
		}
		
		Path childPath = path.getChildPath();
		if (childPath == null) {
			return directory;
		}
		
		String id = path.getRoot();
		Object component = directory.lookup(id); 
		
		BeanDirectory child = null;
		if (component instanceof BeanDirectoryOwner) {
			child = ((BeanDirectoryOwner) component).provideBeanDirectory();
		}
			
		if (child == null) {
			return null;
		}
		
		return new DirectoryPathWalker(child).directoryForPath(childPath);
	}
	
	/**
	 * Get an object for the given path.
	 * 
	 * @param path The path.
	 * @return An object or null if it hasn't been registered.
	 */
	public Object objectForPath(Path path) 
	throws ArooaPropertyException {
		if (path == null) {
			return null;
		}
		Path childPath  = path.getChildPath();
		if (childPath == null) {
			return null;
		}
		String id = path.getRoot();
		Object component = directory.lookup(id); 
		if (childPath.size() == 0) {
			return component;
		}
		
		BeanDirectory child = null;
		if (component instanceof BeanDirectoryOwner) {
			child = ((BeanDirectoryOwner) component).provideBeanDirectory();
		}
			
		if (child == null) {
			return null;
		}
		
		return new DirectoryPathWalker(child).objectForPath(childPath);
	}	
}
