/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.registry;

import java.io.Serializable;

/**
 * Represent the path to a component.
 * 
 * @author Rob Gordon.
 */
public class Path implements Serializable {
	private static final long serialVersionUID = 20051117;
	
	/** The path separator. */
	public static final String PATH_SEPARATOR = "/";

	private final String element;

	private final Path parent;
	
	/**
	 * Constructor for an empty path.
	 */
	public Path() {
		this(null);
	}

	public Path(String path) {
		if (path == null || path.length() == 0) {
			element = null;
			parent = null;
		}
		else {
			int index = path.lastIndexOf(PATH_SEPARATOR);
			// ignore trailing slashes.
			if (index == path.length() - 1) {
				path = path.substring(0, index);
				index = path.lastIndexOf(PATH_SEPARATOR);
			}
			if (index < 0) {
				element = path;
				parent = new Path();
			}
			else {
				element = path.substring(index + 1);
				parent = new Path(path.substring(0, index));
			}
		}
	}
	
	/**
	 * Constructor for a path which takes a path in the form a/b/c.
	 * 
	 * @param path The path as a String.
	 */
	private Path(Path parent, String element) {
		this.parent = parent;
		this.element = element;
	}
	
	/**
	 * Return the size or depth of this path. a/b/c has a size of 3.
	 * 
	 * @return The size of this path.
	 */
	public int size() {
		if (parent == null) {
			return 0;
		}
		return parent.size() + 1;
	}
	
	
	/**
	 * Get the id of the top element in the path. This can be used to lookup
	 * the component which contains the component which is the next part
	 * of the path.
	 * 
	 * @return The id of the topmost element. 
	 */
	public String getRoot() {
		if (parent == null) {
			return null;
		}
		if (parent.parent == null) {
			return element;
		}
		return parent.getRoot();
	}

	public String getId() {
		return element;
	}
	
	/**
	 * Get the path below the topmost element. This can be used to traverse
	 * down
	 * to lookup a component 
	 * 
	 * @return The child path or null if this path is a single element.
	 */
	public Path getChildPath() {
		
		if (parent == null) {
			return null;
		}
		if (parent.parent == null) {
			return new Path();
		}
		
		return parent.getChildPath().addId(element);
	}
	
	/**
	 * Create a new path by adding a new path element identified by they id to
	 * this path. If the id is null a new null path element will be added.
	 * 
	 * @param id The id of the new path element. May be null.
	 * @return The new path. 
	 */
	public Path addId(String id) {
		if (id == null) {
			return this;
		}
		return new Path(this, id);
	}

	
	public Path getParent() {
		return parent;
	}
	
	/**
	 * Create a new path by adding a path to this path.
	 * 
	 * @param extra The extra path.
	 * @return The new path.
	 */
	public Path addPath(Path extra) {

		if (extra == null) {
			return this;
		}
		
		return addPath(extra.parent).addId(extra.element);
	}
	
	/**
	 * Resolve the other path relative to this path. if this path is a/b and
	 * other is a/b/c/d the resultant relative path is c/d.
	 *   
	 * @param other The other path.
	 * @return The relative path, null if the other path is not in this hierarchy.
	 */
	public Path relativeTo(Path other) {
		
		if (other == null) {
			return null;
		}
		
		if (other.equals(this)) {
			return new Path();
		}
				
		Path previousResult = relativeTo(other.parent);
		
		if (previousResult == null) {
			return null;
		}
		
		return previousResult.addId(other.element);
		
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof Path)) {
			return false;
		}
		
		Path other = (Path) obj;

		
		if (other.parent == null && parent == null) {
			return true; 	
		}
		
		if (other.parent == null || parent == null) {
			return false;
		}
		
		if (! other.element.equals(this.element)) {
			return false;
		}
		
		return other .parent.equals(parent);
	}
	
	@Override
	public int hashCode() {
		if (parent == null) {
			return 0;
		}
		return parent.hashCode() + element.hashCode();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (parent == null) {
			return "";
		}
		String parentString = parent.toString();
		if (parentString.length() > 0 ) {
			return parentString + PATH_SEPARATOR + element;
		}
		else {
			return element;
		}
	}
}
