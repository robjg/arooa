/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.xml;

import java.util.LinkedHashMap;
import java.util.Map;

import org.oddjob.arooa.parsing.ArooaAttributes;
import org.xml.sax.Attributes;

public class XMLArooaAttributes implements ArooaAttributes {
	private final Map<String, String> attributes = 
		new LinkedHashMap<String, String>();
	
	
	public XMLArooaAttributes(String uri, Attributes attrs) {
		for (int i = 0; i < attrs.getLength(); i++) {
			String attrUri = attrs.getURI(i);
			if (attrUri != null
				&& !attrUri.equals("")
				&& !attrUri.equals(uri)) {
				continue; // Ignore attributes from unknown uris
			}
			String key = attrs.getLocalName(i);
			String value = attrs.getValue(i);

			attributes.put(key, value);
		}
	}
	
	public String get(String name) {
		return (String) attributes.get(name);
	}
	
	public String[] getAttributeNames() {
		return (String[]) attributes.keySet().toArray(new String[0]);
	}
	
	public String remove(String name) {
		return (String) attributes.remove(name);
	}
	
}
