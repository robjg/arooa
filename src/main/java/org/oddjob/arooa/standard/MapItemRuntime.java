/**
 * 
 */
package org.oddjob.arooa.standard;

import java.util.Map;
import java.util.Set;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

class MapItemRuntime extends InstanceRuntime {

	private final String key;
	
	MapItemRuntime(
			String key, 
			InstanceConfiguration item,
			ArooaContext context) {
		super(item, context);
		this.key = key;
	}
	
	ParentPropertySetter getParentPropertySetter() {
		return new ParentPropertySetter() {
			public void parentSetProperty(Object value) 
			throws ArooaPropertyException {
				RuntimeConfiguration parentRuntime = getParentContext().getRuntime();

					if (key == null) {
						if (value == null) {
							// probably destroy... this needs reworking.
							return;
						}
						
						// property must be map
						
						ArooaConverter converter = getContext().getSession(
								).getTools().getArooaConverter();
						
						try {
							value = converter.convert(value, Map.class);
						} catch (ArooaConversionException e) {
							throw new ArooaException("Key must be provided for mapped property.");
						}
						
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) value;
						for (Map.Entry<String, Object> mapEntry : map.entrySet()) {
							parentRuntime.setMappedProperty(
									null,
									mapEntry.getKey(),
									mapEntry.getValue());
						}
					}
					else {
						parentRuntime.setMappedProperty(
								null, 
								key, 
								value);
					}		
			
			}
		};
	}
	
}