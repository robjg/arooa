package org.oddjob.arooa.life;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.convert.EmptyArooaConverter;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;

public class ElementsForIdentifier {
	

	private final Map<ArooaElement, ? extends ArooaClass> mappings;
	
	public ElementsForIdentifier(
			Map<ArooaElement, ? extends ArooaClass> mappings) {
		this.mappings = mappings;
	}	
	
	public ArooaElement[] elementsFor(InstantiationContext instantiationContext) {

		if (mappings == null) {
			return null;
		}

		ArooaClass arooaClass = instantiationContext.getArooaClass();
			
		Class<?> requiredClass;
		if (arooaClass == null) {
			requiredClass = Object.class;
		}
		else {
			requiredClass = arooaClass.forClass();
		}
		
		ArooaConverter conversions =
			instantiationContext.getArooaConverter();
		if (conversions == null) {
			conversions = new EmptyArooaConverter();
		}
		
		List<ArooaElement> supports = new ArrayList<ArooaElement>();

		for (Map.Entry<ArooaElement, ? extends ArooaClass> entry
				: mappings.entrySet()) {
			
			Class<?> elementClass = entry.getValue().forClass();
			
			ConversionPath<?, ?> conversionPath = 
				conversions.findConversion(elementClass, requiredClass);

			if (conversionPath != null) {
				supports.add(entry.getKey());
			}
		}
		return supports.toArray(new ArooaElement[supports.size()]);
	}
}
