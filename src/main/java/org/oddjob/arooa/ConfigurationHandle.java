package org.oddjob.arooa;

import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ParseContext;

/**
 * Something returned by an {@link ArooaParser}
 * as the result of parsing an {@link ArooaConfiguration}.
 *
 * @author rob
 */
public interface ConfigurationHandle<P extends ParseContext<P>> {

	/**
	 * Save the parsed configuration tree back into
	 * the underlying configuration.
	 * 
	 * @throws ArooaParseException
	 */
	void save() throws ArooaParseException;
	
	/**
	 * Get the {@link ArooaContext} that corresponds to
	 * the document element or it's equivalent.
	 * 
	 * @return The ArooaContext.
	 */
	P getDocumentContext();
}
