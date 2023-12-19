package org.oddjob.arooa;

import org.oddjob.arooa.beandocs.MappingsContents;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;


/**
 * Encapsulate the relationship between a set of 
 * XML elements and their Java Object counterparts.
 * 
 * @author rob
 *
 */
public interface ElementMappings {

	/**
	 * Provide a mapping from an element to the name of a java class.
	 * 
	 * @param element The element.
	 * @param context The InstantiationContext.
	 * 
	 * @return The ArooaClass. Null if no mappings exists.
	 */
    ArooaClass mappingFor(ArooaElement element,
                          InstantiationContext context);
	
	/**
	 * Provide a mapping from an element to the name of a java class
	 * that is a DesignFactory for the element.
	 * 
	 * @param element The element.
	 * @param context The InstantiationContext.
	 * 
	 * @return The DesignFactory. May be null.
	 */
    DesignFactory designFor(ArooaElement element,
                            InstantiationContext context);
	
	/**
	 * Provide a list of all elements in this mapping that support
	 * the given context.
	 * 
	 * @return The elements. May be null.
	 */
    ArooaElement[] elementsFor(InstantiationContext context);

	/**
	 * Get the BeanDoc {@link MappingsContents} for this mapping.
	 * 
	 * @param arooaType The type of Mapping.
	 * @return The BeanDoc. May be null.
	 */
    MappingsContents getBeanDoc(ArooaType arooaType);
}
