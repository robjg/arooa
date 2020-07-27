/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa;


import org.oddjob.arooa.parsing.ParseContext;

/**
 * Something that is able to parse an {@link ArooaConfiguration}.
 * <p>
 * The parser will either produce a {@link ConfigurationHandle} object or throw
 * an {@link ArooaParseException} if parsing fails.
 * <p>
 *
 * @param <P> The Type of Parse Context The Parser Uses.
 *
 * @author rob
 */
public interface ArooaParser<P extends ParseContext<P>> {
	
	/**
	 * Parse an {@link ArooaConfiguration}.
	 * 
	 * @param configuration The configuration.
	 * @return A {@link ConfigurationHandle}.
	 * 
	 * @throws ArooaParseException If parsing fails.
	 */
	ConfigurationHandle<P> parse(ArooaConfiguration configuration)
	throws ArooaParseException ;
		
}