package org.oddjob.arooa.registry;



public class BeanDirectoryCrawler {

	private final BeanDirectory directory;
	
	public BeanDirectoryCrawler(BeanDirectory directory) {
		this.directory = directory;
	}
	
	/**
	 * Find the directory for a bean.
	 * 
	 * @param selected The current path being built up in this recursive call.
	 * @param component The componet being searched for.
	 * 
	 * @return The path relative to this registry or null if it is
	 * is not acessable.
	 */
	public BeanDirectory registryForComponent(Object component) {
		String id = directory.getIdFor(component); 
		if (id != null) {
			return directory;
		}
		
		// go round all the child directories recursing down until we find a
		// directory for our component.
		for (BeanDirectoryOwner owner : directory.getAllByType(BeanDirectoryOwner.class)) {
			
			String childId = directory.getIdFor(owner);
			if (childId == null) {
				// a directory owner without an id - so no path downwards
				// from here.
				return null;
			}
			
			BeanDirectory child = owner.provideBeanDirectory();
			BeanDirectoryCrawler next = new BeanDirectoryCrawler(child);
			BeanDirectory result = next.registryForComponent(component);
			
			if (result != null) {
				return result;
			}
		}
		return null;
	}
	
	private Path pathForObject(Path pathSoFar, BeanDirectory directory,
			Object component) {
		
		String id = directory.getIdFor(component); 
		if (id != null) {
			return pathSoFar.addId(id);
		}
		
		// go round all the child directories recursing down until we find a
		// directory for our component.
		for (BeanDirectoryOwner owner : directory.getAllByType(BeanDirectoryOwner.class)) {
			
			String childId = directory.getIdFor(owner);
			if (childId == null) {
				// a directory owner without an id - so no path downwards
				// from here.
				continue;
			}
			
			BeanDirectory child = owner.provideBeanDirectory();
			if (child == null) {
				continue;
			}
			
			Path result = pathForObject(pathSoFar.addId(childId), child, component);
			if (result != null) {
				return result;
			}
		}
		
		return null;
	}

	/**
	 * Get the path to a given component that may or may not be
	 * in this registery's hierarchy.
	 * 
	 * @param component The component.
	 * @return The path or null if it can't be found.
	 */
	public Path pathForObject(Object component) {

		if (component == null) {
			throw new NullPointerException("Component must not be null!");
		}

		return pathForObject(new Path(), directory, component);
	}
	
}
