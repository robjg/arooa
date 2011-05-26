package org.oddjob.arooa.design;

import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;

/**
 * Helper class that creates a design.
 * 
 * @author rob
 *
 */
public class DescriptorDesignFactory implements DesignFactory {
	
	public DesignInstance createDesign(ArooaElement element,
			ArooaContext parentContext) 
	throws ArooaPropertyException {
		
		ArooaType type = parentContext.getArooaType();
		
		ElementMappings mappings = 
			parentContext.getSession().getArooaDescriptor(
					).getElementMappings();
								
		if (mappings == null) {
			throw new NullPointerException(
					"No Element Mappings for type " + type);
		}

		DesignInstance design = null;

		InstantiationContext instantiationContext = 
			new InstantiationContext(parentContext);
		
		DesignFactory designFactory = mappings.designFor(element, 
				instantiationContext);
		
		if (designFactory != null) {
			design = designFactory.createDesign(element, parentContext);
		}
		
		if (design != null) {
			return design;
		}
		
		ArooaClass arooaClass = mappings.mappingFor(element, 
				instantiationContext);
		
		if (arooaClass == null) {
			throw new NullPointerException(
					"No Class Mapping for Element " + element + 
					" of type " + type + ".");
		}
		
		DesignFactory factory = new GenericDesignFactory(
				arooaClass);
		
		return factory.createDesign(element, parentContext);
	}
}
