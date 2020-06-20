/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.parsing;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;


public class SimplePrefixMappings implements PrefixMappings {
	
	private final Map<URI, String> byUri = new LinkedHashMap<URI, String>();

	private final Map<String, URI> byPrefix = new LinkedHashMap<String, URI>();

	@Override
	public void add(NamespaceMappings otherMappings)
	throws DuplicateMappingsException {
		for (String prefix: otherMappings.getPrefixes()) {
			put(prefix, otherMappings.getUriFor(prefix));
		}
		
	}

	@Override
	public void put(String prefix, URI uri)
	throws DuplicateMappingsException {
		if (uri == null) {
			throw new NullPointerException("Null URI");
		}
		
		String existingPrefix = byUri.get(uri);
		
		if (existingPrefix != null && !existingPrefix.equals(prefix)) {
			throw new DuplicateMappingsException(
					"A prefix mapping can't be changed in an Arooa Configuration.");
		}
		
		URI existingUri = byPrefix.get(prefix);
		
		if (existingUri != null && !existingUri.equals(uri)) {
			throw new DuplicateMappingsException(
					"A prefix mapping can't be changed in an Arooa Configuration.");
		}
		
		byUri.put(uri, prefix);
		byPrefix.put(prefix, uri);
	}

	@Override
	public String getPrefixFor(URI uri) {
		return byUri.get(uri);
	}

	@Override
	public URI getUriFor(String prefix) {
		return byPrefix.get(prefix);
	}

	@Override
	public String[] getPrefixes() {
		return (String[]) byPrefix.keySet().toArray(new String[0]);
	}
	
}
