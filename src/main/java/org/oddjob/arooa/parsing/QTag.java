package org.oddjob.arooa.parsing;

import java.net.URI;

/**
 * Represents a element as a prefix and a tag name.
 * 
 * @author rob
 *
 */
public class QTag implements Comparable<QTag> {
	
	public static final QTag NULL_TAG = new QTag("");
	
	private final String prefix;
	private final ArooaElement element;
	
	public QTag(String tag) {
		this(null, new ArooaElement(tag));
	}

	public QTag(ArooaElement element, ParseContext<?> arooaContext) {
		this(element, arooaContext.getPrefixMappings());
	}

	public QTag(ArooaElement element, UriMapping uriMapping) {
		
		URI uri = element.getUri();
		
		if (uri == null) {
			this.prefix = "";
		}
		else {
			String prefix = uriMapping.getPrefixFor(uri);
			
			if (prefix == null) {
				throw new IllegalStateException("No prefix for " + uri);
			}

			this.prefix = prefix;			
		}

		this.element = element;
	}
	
	public QTag(String prefix, ArooaElement element) {
		if (prefix == null) {
			this.prefix = "";
		}
		else {
			this.prefix = prefix;
		}
		
		if (element == null) {
			throw new NullPointerException("Element.");
		}
		
		this.element = element;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getTag() {
		return element.getTag();
	}
	
	public ArooaElement getElement() {
		return element;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		if (! (obj instanceof QTag)) {
			return false;
		}
		
		QTag other = (QTag) obj;
				
		return other.element.equals(element);
	}
	
	@Override
	public int hashCode() {
		return element.hashCode();
	}
	
	public int compareTo(QTag o) {
		int prefixCompare = prefix.compareTo(o.prefix); 
		if ( prefixCompare != 0) {
			return prefixCompare;
		}
		return element.getTag().compareTo(o.element.getTag());
	}
	
	@Override
	public String toString() {
		if (prefix.length() == 0) {
			return element.getTag();
		}
		return prefix + ":" + element.getTag();
	}
}
