/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.parsing;

import java.io.Serializable;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represent an element, which is an abstract of an XML element
 * but XML agnostic, so that an Arooa configuration could be anything,
 * not just XML.
 * 
 * @author rob
 *
 */
public class ArooaElement implements Serializable {
	private static final long serialVersionUID = 2011060800L;
	
	private final URI uri;
	private final String tag;
	private final SimpleAttributes attributes;
	
	public ArooaElement(URI uri, String tag, 
			ArooaAttributes attributes) {
		
		if (tag == null) {
			throw new NullPointerException("Tag name can not be null.");
		}
		
		this.uri = uri;
		this.tag = tag;
		if (attributes == null) {
			this.attributes = new SimpleAttributes();
		}
		else {
			this.attributes = new SimpleAttributes(attributes);
		}
	}

	public ArooaElement(URI uri, String tag) {
		this(uri, tag, null);
	}

	public ArooaElement(String tag) {
		this(null, tag);
	}
	
	public ArooaElement(String tag, ArooaAttributes attributes) {
		this(null, tag, attributes);
	}
	
	public String getTag() {
		return tag;
	}
	
	public URI getUri() {
		return uri;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof ArooaElement)) {
			return false;
		}

		ArooaElement other = (ArooaElement) obj;
		
		if (!tag.equals(other.tag)) {
			return false;
		}
		
		if (uri != null) {
			return uri.equals(other.uri);
		}

		return other.uri == null;
	}
	
	@Override
	public int hashCode() {
		return tag.hashCode();
	}
	
	public ArooaElement removeAttribute(String name) {
		return new ArooaElement(uri, tag, attributes.remove(name));
	}
	
	public ArooaElement addAttribute(String name, String value) {
		return new ArooaElement(uri, tag, attributes.add(name, value));
	}
		
	public String toString() {
		if (uri == null) {
			return tag; 			
		} else {
			return uri.toString() + ":" + tag;
		}
	}
	
	public ArooaAttributes getAttributes() {
		return attributes;
	}
	
	static class SimpleAttributes implements ArooaAttributes, Serializable {
		private static final long serialVersionUID = 2011060800L;
		
		private final Map<String, String> atts = new LinkedHashMap<>();

		SimpleAttributes() {
		}
		
		SimpleAttributes(ArooaAttributes attributes) {
			String[] names = attributes.getAttributeNames();
			for (String name: names) {
				atts.put(name, attributes.get(name));
			}
		}
		
		SimpleAttributes add(String name, String value) {
			
			SimpleAttributes copy = new SimpleAttributes(this);
			copy.atts.put(name, value);
			return copy;
		}
		
		public String get(String name) {
			return atts.get(name)	;
		}
		
		public String[] getAttributeNames() {
			return atts.keySet().toArray(new String[0]);
		}
		
		SimpleAttributes remove(String name) {
			SimpleAttributes copy = new SimpleAttributes(this);
			copy.atts.remove(name);
			return copy;
		}
		
	}
}
