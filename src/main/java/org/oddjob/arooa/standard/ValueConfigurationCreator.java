package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.handlers.ElementAction;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;

public class ValueConfigurationCreator 
implements ElementAction<InstanceConfiguration>{

	public InstanceConfiguration onElement(ArooaElement element, ArooaContext context) {

		ElementMappings mappings = context.getSession(
				).getArooaDescriptor().getElementMappings();
		
		if (mappings == null) {
			throw new NullPointerException(
					"No Value Element Mappings in Descriptor.");
		}

		ArooaClass classIdentifier = mappings.mappingFor(element, 
				new InstantiationContext(context));
		
		if (classIdentifier == null) {
			throw new ArooaException("No definition for [" + 
					element + "]");
		}

		Object object = classIdentifier.newInstance();
		
		InstanceConfiguration instance = new ObjectConfiguration(
				classIdentifier, object, element.getAttributes());

		return instance;
	}
}
