/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.parsing;

import java.net.URI;

/**
 * Abstraction of the mappings from prefix to URI.
 * 
 * @author rob
 *
 */
public interface PrefixMappings {
	
	public void add(PrefixMappings otherMappings)
	throws DuplicateMappingsException;
	
	public void put(String prefix, URI uri)
	throws DuplicateMappingsException;

	public String getPrefixFor(URI uri);

	public URI getUriFor(String prefix);

	public String[] getPrefixes();
	
	public QTag getQName(ArooaElement element);

}