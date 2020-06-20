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
public interface PrefixMappings extends NamespaceMappings {
	
	void add(NamespaceMappings otherMappings)
	throws DuplicateMappingsException;
	
	void put(String prefix, URI uri)
	throws DuplicateMappingsException;


}