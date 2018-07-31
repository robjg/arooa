package org.oddjob.arooa.deploy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.beandocs.MappingsContents;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;

/**
 * Amalgamate {@link ElementMappings}. Used by {@link ListDescriptor}
 *  
 * @author rob
 *
 */
public class ClassMappingsList implements ElementMappings {

	private final List<ElementMappings> mappingsList = 
		new ArrayList<ElementMappings>();

	public void addMappings(ElementMappings mappings) {
		if (mappings == null) {
			throw new NullPointerException("Mappings");
		}
		this.mappingsList.add(0, mappings);
	}	
	
	public ArooaClass mappingFor(ArooaElement element, 
			InstantiationContext parentContext) {

		for (ElementMappings mappings: mappingsList) {
			ArooaClass mapping = mappings.mappingFor(
					element, parentContext); 
			if (mapping != null) {
				return mapping;
			}
		}
			
		return null;
	}

	public DesignFactory designFor(ArooaElement element,
			InstantiationContext parentContext) {

		for (ElementMappings mappings: mappingsList) {
			DesignFactory design = mappings.designFor(
					element, parentContext); 
			
			if (design != null) {
				return design;
			}
		}
			
		return null;
	}

	public ArooaElement[] elementsFor(
			InstantiationContext propertyContext) {
		Collection<ArooaElement> all = new LinkedHashSet<ArooaElement>();
		
		for (ElementMappings mappings: mappingsList) {
			ArooaElement[] supports = mappings.elementsFor(
					propertyContext);
			
			if (supports != null) {
				all.addAll(Arrays.asList(supports));
			}
		}
		
		return all.toArray(new ArooaElement[0]);
	}
	
	@Override
	public MappingsContents getBeanDoc(final ArooaType arooaType) {
		return new MappingsContents() {
			
			@Override
			public ArooaElement[] allElements() {
				List<ArooaElement> allElements = new ArrayList<ArooaElement>();
				for (ElementMappings mappings : mappingsList) {
					MappingsContents beanDoc = mappings.getBeanDoc(arooaType);
					if (beanDoc == null) {
						continue;
					}
					allElements.addAll(Arrays.asList(beanDoc.allElements()));
				}
				return allElements.toArray(new ArooaElement[allElements.size()]);
			}
			
			@Override
			public ArooaClass documentClass(ArooaElement element) {
				for (ElementMappings mappings : mappingsList) {
					MappingsContents beanDoc = mappings.getBeanDoc(arooaType);
					if (beanDoc == null) {
						continue;
					}
					ArooaClass documentClass = beanDoc.documentClass(element);
					if (documentClass != null) {
						return documentClass;
					}
				}
				return null;
			}
		};
	}

	
}
