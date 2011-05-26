package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.beandocs.MappingsContents;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;

public class LinkedClassMapping implements ElementMappings {

	private final ClassMappingsList delegate;
	
	public LinkedClassMapping(ElementMappings primary, ElementMappings secondary) {
		this.delegate = new ClassMappingsList();
		if (secondary != null) {
			this.delegate.addMappings(secondary);
		}
		if (primary != null) {
			this.delegate.addMappings(primary);
		}
	}
	
	public ArooaClass mappingFor(ArooaElement element, InstantiationContext parentContext) {

		return delegate.mappingFor(element, parentContext);
	}

	@Override
	public DesignFactory designFor(ArooaElement element,
			InstantiationContext parentContext) {

		return delegate.designFor(element, parentContext);
	}

	public ArooaElement[] elementsFor(
			InstantiationContext propertyContext) {
		return delegate.elementsFor(propertyContext);
	}	
	
	@Override
	public MappingsContents getBeanDoc(ArooaType arooaType) {
		return delegate.getBeanDoc(arooaType);
	}
}
