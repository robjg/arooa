package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.beandocs.MappingsContents;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;

/**
 * Provide a consistent {@link ElementMappings} view for the two
 * individual mappings that are loaded in an {@link ArooaDescriptorBean}.
 * 
 * @author rob
 *
 */
public class MappingsSwitch implements ElementMappings {
	
	private final ElementMappings componentMappings;
	
	private final ElementMappings valueMappings;
	
	public MappingsSwitch(
			ElementMappings componentMappings,
			ElementMappings valueMappings) {
		this.componentMappings = componentMappings;
		this.valueMappings = valueMappings;
	}
	
	@Override
	public ArooaClass mappingFor(ArooaElement element,
			InstantiationContext context) {
		switch (context.getArooaType()) {
		case COMPONENT:
			if (componentMappings == null) {
				return null;
			}
			return componentMappings.mappingFor(element, context);
		case VALUE:
			if (valueMappings == null) {
				return null;
			}
			return valueMappings.mappingFor(element, context);
		}
		return null;
	}
	
	@Override
	public DesignFactory designFor(ArooaElement element,
			InstantiationContext context) {
		switch (context.getArooaType()) {
		case COMPONENT:
			if (componentMappings == null) {
				return null;
			}
			return componentMappings.designFor(element, context);
		case VALUE:
			if (valueMappings == null) {
				return null;
			}
			return valueMappings.designFor(element, context);
		}
		return null;
	}
	
	@Override
	public ArooaElement[] elementsFor(InstantiationContext context) {
		switch (context.getArooaType()) {
		case COMPONENT:
			if (componentMappings == null) {
				return null;
			}
			return componentMappings.elementsFor(context);
		case VALUE:
			if (valueMappings == null) {
				return null;
			}
			return valueMappings.elementsFor(context);
		}
		return null;
	}
	
	@Override
	public MappingsContents getBeanDoc(ArooaType arooaType) {
		switch (arooaType) {
		case COMPONENT:
			if (componentMappings == null) {
				return null;
			}
			return componentMappings.getBeanDoc(arooaType);
		case VALUE:
			if (valueMappings == null) {
				return null;
			}
			return valueMappings.getBeanDoc(arooaType);
		}
		return null;
	}
	
	
}
