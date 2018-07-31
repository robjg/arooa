/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa;


/**
 * An ArooaConfigurationParser is able to parse an {@link ArooaConfiguration}. 
 * <p>
 * The parser will either produce a {@link ArooaText} object or throw
 * an {@link ArooaParseException} if parsing fails.
 * <p>
 *
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
	public ConfigurationHandle parse(ArooaConfiguration configuration)
	throws ArooaParseException ;
		
}