package org.oddjob.arooa;

import org.oddjob.arooa.parsing.ArooaContext;

/**
 * Something returned by an {@link ArooaParser}
 * as the result of parsing an {@link ArooaConfiguration}.
 * It can be envisaged as something that provides just a
 * grasp of the results of the parse. 
 * 
 * @author rob
 */
public interface ConfigurationHandle {

	/**
	 * Save the parsed configuration tree back into
	 * the underlying configuration.
	 * 
	 * @throws ArooaParseException
	 */
	public void save() throws ArooaParseException;
	
	/**
	 * Get the {@link ArooaContext} that corresponds to
	 * the document element or it's equivalent.
	 * 
	 * @return The ArooaContext.
	 */
	public ArooaContext getDocumentContext();
}
