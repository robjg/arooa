/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa;

import org.oddjob.arooa.parsing.ArooaContext;

/**
 * An ArooaConfiguration is something that accepts 
 * an {@link ArooaContext} and uses that context to provide 
 * a {@link ConfigurationHandle}.
 * <p>
 * The result is typically generated by iterating over or parsing
 * whatever the configuration encapsulates using as it's
 * starting point the {@link ArooaHandler} provided by the parent context.
 * <p>
 * An ArooaConfiguration is intended to be used in conjunction with
 * an {@link ArooaParser} which provides the context.
 *  
 * @author rob
 */
public interface ArooaConfiguration {

	/**
	 * Parse the encapsulated configuration.
	 * 
	 * @param parentContext The parent context to use.
	 * 
	 * @return A {@link ConfigurationHandle}.
	 * 
	 * @throws ArooaParseException
	 */
	public ConfigurationHandle parse(ArooaContext parentContext)
	throws ArooaParseException;
}
