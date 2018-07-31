package org.oddjob.arooa.design;

import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.QTag;
import org.oddjob.arooa.parsing.QTagConfiguration;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.types.BeanType;

/**
 * This badly named class provides support to a {@link DesignElementProperty}
 * for handling {@link DesignInstance}s.
 * 
 * @author rob
 *
 */
public class InstanceSupport {

	private static final Logger logger = LoggerFactory.getLogger(InstanceSupport.class);
	
	/** The property context. */
	private final ArooaContext arooaContext;
	
	/** Tag to Element mappings for the supported tags
	 * for the property. In order for GUI selection boxes. */
	private final TreeMap<QTag, ArooaElement> mappings = new TreeMap<QTag, ArooaElement>();

	/**
	 * Constructor.
	 * 
	 * @param property
	 */
	public InstanceSupport(DesignElementProperty property) {
		this.arooaContext = property.getArooaContext();
		
		ArooaClass propertyTypeClassName = 
			arooaContext.getRuntime().getClassIdentifier();
		
		ArooaElement[] elements = null;
		
		// this fails quite often so trap the error.
		try { 
			elements = arooaContext.getSession().getArooaDescriptor(
					).getElementMappings().elementsFor(
							new InstantiationContext(arooaContext));
		}
		catch (Exception e) {
			logger.error("Failed creating InstanceSupport for property [" + 
					property.property() + "], of class [" + 
					propertyTypeClassName + "]", e);
			
			elements = new ArooaElement[] { BeanType.ELEMENT };
		}
		
		for (int i = 0; i < elements.length; ++i) {
			mappings.put(new QTag(elements[i], arooaContext), elements[i]);
		}		
	}

	
	public QTag[] getTags() {
		return mappings.keySet().toArray(new QTag[0]);
	}
	
	public void removeInstance(DesignInstance design) 
	throws ArooaConfigurationException {
		ArooaContext designContext = design.getArooaContext(); 
		
		int index = arooaContext.getConfigurationNode().indexOf(
				designContext.getConfigurationNode());
		
		if (index < 0) {
			throw new IllegalStateException(
					"Attempting to remove a configuration node that is not a child of it's parent.");
		}

		designContext.getRuntime().destroy();
		
		arooaContext.getConfigurationNode().removeChild(index);		
	}
	
	public void insertTag(int index, QTag tag) 
	throws ArooaParseException {
		if (tag == null) {
			throw new NullPointerException("New selected type can not be null.");
		}
		
		arooaContext.getConfigurationNode().setInsertPosition(index);
		
		new QTagConfiguration(tag).parse(arooaContext);
	}
	
	public static QTag tagFor(DesignInstance instance) {
		return instance.getArooaContext().getPrefixMappings().getQName(
				instance.element());
	}
	
}
