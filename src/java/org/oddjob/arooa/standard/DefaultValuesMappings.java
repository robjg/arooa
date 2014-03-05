package org.oddjob.arooa.standard;

import java.util.HashMap;
import java.util.Map;

import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.beandocs.MappingsBeanDoc;
import org.oddjob.arooa.beandocs.MappingsContents;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.life.ElementsForIdentifier;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.types.ClassType;
import org.oddjob.arooa.types.ConvertType;
import org.oddjob.arooa.types.IdentifiableValueType;
import org.oddjob.arooa.types.ImportType;
import org.oddjob.arooa.types.ListType;
import org.oddjob.arooa.types.MapType;
import org.oddjob.arooa.types.ValueType;
import org.oddjob.arooa.types.XMLType;

class DefaultValuesMappings implements ElementMappings {

	private final Map<ArooaElement, SimpleArooaClass> classNames = 
		new HashMap<ArooaElement, SimpleArooaClass>();
	
	private final Map<ArooaElement, DesignFactory> designNames = 
		new HashMap<ArooaElement, DesignFactory>();
	
	{
		classNames.put(ClassType.ELEMENT, 
				new SimpleArooaClass(ClassType.class));
		classNames.put(ConvertType.ELEMENT, 
				new SimpleArooaClass(ConvertType.class));
		classNames.put(ListType.ELEMENT, 
				new SimpleArooaClass(ListType.class));
		classNames.put(MapType.ELEMENT, 
				new SimpleArooaClass(MapType.class));
		classNames.put(ValueType.ELEMENT, 
				new SimpleArooaClass(ValueType.class));
		classNames.put(XMLType.ELEMENT, 
				new SimpleArooaClass(XMLType.class));
		classNames.put(ImportType.ELEMENT, 
				new SimpleArooaClass(ImportType.class));
		classNames.put(IdentifiableValueType.ELEMENT, 
				new SimpleArooaClass(IdentifiableValueType.class));
		
		designNames.put(ValueType.ELEMENT, 
				new ValueType.ValueDesignFactory());
		designNames.put(XMLType.ELEMENT,
				new XMLType.XMLDesignFactory());
	}
	
	@Override
	public ArooaClass mappingFor(ArooaElement element, 
			InstantiationContext parentContext) {

		return classNames.get(element);
	}
	
	@Override
	public DesignFactory designFor(ArooaElement element, 
			InstantiationContext parentContext) {
		return designNames.get(element);
	}

	@Override
	public ArooaElement[] elementsFor(
			InstantiationContext parentContext) {

		return new ElementsForIdentifier(
				classNames).elementsFor(parentContext);
	}

	@Override
	public MappingsContents getBeanDoc(ArooaType arooaType) {
		return new MappingsBeanDoc(classNames);
	};
}
