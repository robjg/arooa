package org.oddjob.arooa;

import org.oddjob.arooa.beandocs.MappingsContents;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;

public class MockElementMappings implements ElementMappings {

	@Override
	public ArooaElement[] elementsFor(
			InstantiationContext parentContext) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	@Override
	public ArooaClass mappingFor(ArooaElement element,
			InstantiationContext parentContext) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	@Override
	public DesignFactory designFor(ArooaElement element, 
			InstantiationContext parentContext) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	@Override
	public MappingsContents getBeanDoc(ArooaType arooaType) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
}
