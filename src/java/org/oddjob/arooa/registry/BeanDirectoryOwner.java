package org.oddjob.arooa.registry;


/**
 * An owner of a {@link BeanDirectory}.
 * 
 * @author rob
 *
 */
public interface BeanDirectoryOwner {

	/**
	 * Get the {@link BeanDirectory}. This method may return null if
	 * the BeanDirectory isn't available.
	 * 
	 * @return The <code>BeanDirectory</code> or null.
	 */
	public BeanDirectory provideBeanDirectory();
	
	
	/**
	 * Get the {@link PropertyLookup}. This method may return null if
	 * one isn't available. 
	 * 
	 * @return The <code>PropertyLookup</code> or null.
	 */
	// TODO: Support this?
//	public PropertyLookup getPropertyLookup();
}
