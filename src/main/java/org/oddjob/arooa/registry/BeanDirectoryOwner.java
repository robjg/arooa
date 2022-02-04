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
	BeanDirectory provideBeanDirectory();
}
