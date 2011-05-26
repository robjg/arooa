package org.oddjob.arooa.beandocs;

import java.util.Map;

import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;

/**
 * Helper class to provide {@link MappingsContents} for the common mappings
 * implmenetations.
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
		return mappings.keySet().toArray(new ArooaElement[mappings.size()]);
	}
	
	@Override
	public ArooaClass documentClass(ArooaElement element) {
		return mappings.get(element);
	}
	
}
