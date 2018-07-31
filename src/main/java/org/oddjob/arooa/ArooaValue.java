/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa;


/**
 * A PropertyProxy wraps an underlying type so that:
 * <ol>
 *   <li>The wrapped type can be configured from xml
 *   parse events.</li>
 *   <li>The type can manifest itself in deifferent
 *   ways. For innstance text could be either a 
 *   <code>java.lang.String</code> or a <code>java.io.InputStream</code>. 
 * </ol>
 * <p>
 * TODO: Should this interface also provide a list of supported types?
 * 
 * @author Rob Gordon.
 */
public interface ArooaValue {
	
}
