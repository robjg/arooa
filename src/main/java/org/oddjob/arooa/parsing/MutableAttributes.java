/**
 * 
 */
package org.oddjob.arooa.parsing;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Mutable {@link ArooaAttributes}.
 */
public class MutableAttributes implements ArooaAttributes {

	private final Map<String, String> atts = new LinkedHashMap<>();

	public MutableAttributes() {

	}

	public MutableAttributes(ArooaAttributes attributes) {
        Objects.requireNonNull(attributes);

		String[] names = attributes.getAttributeNames();
		for (String name: names) {
			atts.put(name, attributes.get(name));
		}
	}

	public void set(String name, String value) {
		this.atts.put(name, value);
	}

	public String get(String name) {
		return atts.get(name)	;
	}

	public String[] getAttributeNames() {
		return atts.keySet().toArray(new String[0]);
	}

	public String remove(String name) {
		return atts.remove(name);
	}
}