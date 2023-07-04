package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;

import java.util.Map;

/**
 * Helper class to provide {@link MappingsContents} for the common mappings
 * implementations.
 * 
 * @author rob
 *
 */
public class MappingsBeanDoc implements MappingsContents {

	private final Map<ArooaElement, ? extends ArooaClass> mappings;
	
	public MappingsBeanDoc(Map<ArooaElement, ? extends ArooaClass> mappings) {
		this.mappings = mappings;
	}
	
	@Override
	public ArooaElement[] allElements() {
		return mappings.keySet().toArray(new ArooaElement[0]);
	}
	
	@Override
	public ArooaClass documentClass(ArooaElement element) {
		return mappings.get(element);
	}
	
}
