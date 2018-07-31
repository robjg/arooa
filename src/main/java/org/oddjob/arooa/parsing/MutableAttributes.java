/**
 * 
 */
package org.oddjob.arooa.parsing;

import java.util.LinkedHashMap;
import java.util.Map;

public class MutableAttributes implements ArooaAttributes {

	private final Map<String, String> atts = new LinkedHashMap<String, String>();

	public MutableAttributes() {
		
	}
	
	public MutableAttributes(ArooaAttributes attributes) {
		String[] names = attributes.getAttributNames();
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
	
	public String[] getAttributNames() {
		return (String[]) atts.keySet().toArray(new String[0]);
	}
	
	public String remove(String name) {
		return atts.remove(name);
	}
}