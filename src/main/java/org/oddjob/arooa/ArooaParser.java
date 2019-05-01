/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa;


/**
 * Something that is able to parse an {@link ArooaConfiguration}.
 * <p>
 * The parser will either produce a {@link ConfigurationHandle} object or throw
 * an {@link ArooaParseException} if parsing fails.
 * <p>
 *
 * @author rob
 */
public interface ArooaParser {
	
	/**
	 * Parse an {@link ArooaConfiguration}.
	 * 
	 * @param configuration The configuration.
	 * @return A {@link ConfigurationHandle}.
	 * 
	 * @throws ArooaParseException If parsing fails.
	 */
	ConfigurationHandle parse(ArooaConfiguration configuration)
	throws ArooaParseException ;
		
}