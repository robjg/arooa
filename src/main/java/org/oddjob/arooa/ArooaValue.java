/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa;


/**
 * A configuration value that provides some other value at runtime via a conversion.
 * <ol>
 *   <li>The provide value can be configured from xml more easily.</li>
 *   <li>The {@code ArooaValue} can manifest itself in different
 *   ways. For instance text could be either an {@code java.lang.String} or an
 *   {@code java.io.InputStream}.</li>
 * </ol>
 * <p>
 * TODO: Should this interface also provide a list of supported types?
 * 
 * @author Rob Gordon.
 */
public interface ArooaValue {
	
}
