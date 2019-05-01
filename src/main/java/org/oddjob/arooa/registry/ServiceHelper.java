package org.oddjob.arooa.registry;

import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.parsing.ArooaContext;

/**
 * Provided by {@link ArooaTools} so that jobs can find services easily.
 * 
 * @author rob
 *
 */
public interface ServiceHelper {

	/**
	 * Provide an {@link ServiceFinder} that will use the given context
	 * as a basis for finding service.
	 * 
	 * @param context The context. Must not be null.
	 * 
	 * @return A Service Finder. Will not be null.
	 */
	ServiceFinder serviceFinderFor(ArooaContext context);
}
