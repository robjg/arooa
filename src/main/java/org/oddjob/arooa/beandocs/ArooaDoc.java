package org.oddjob.arooa.beandocs;

/**
 * A group of {@link BeanDoc} normally for either Values or Components.
 * 
 * @author rob
 *
 */
public interface ArooaDoc {

	/**
	 * Get all containing {@link BeanDoc}s as an array.
	 * 
	 * @return The docs. Never null.
	 */
	BeanDoc[] getBeanDocs();

	/**
	 * Get an inividual bean doc {@link BeanDoc}s by namespace prefix
	 * and tag.
	 * 
	 * @param prefixed The namespace prefix. May be null if there is
	 * no prefix.
	 * 
	 * @param tag The tag.
	 * 
	 * @return The doc. May bye null..
	 */
	BeanDoc beanDocFor(String prefixed, String tag);
}
