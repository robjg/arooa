package org.oddjob.arooa;

import org.oddjob.arooa.parsing.ArooaContext;

/**
 * An intercepter of parsing that provides an alternative {@link ArooaContext}
 * for the processing of child elements.
 * 
 * @author rob
 *
 */
public interface ParsingInterceptor {

	/**
	 * Intercept current parsing. 
	 * 
	 * @param suggestedContext A suggested context which may be over ridden.
	 * 
	 * @return The new context. Never null (The suggested context should be
	 * returned).
	 */
	public ArooaContext intercept(ArooaContext suggestedContext)
	throws ArooaConfigurationException;
	
}
