package org.oddjob.arooa.design;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.deploy.BeanDescriptorHelper;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * A {@link DesignFactory} that creates a {@link DesignInstance}
 * from the properties of a class and it's 
 * @link {@link ArooaBeanDescriptor#getConfiguredHow(String)}
 * values.
 * 
 * @author rob
 */
public class GenericDesignFactory implements DesignFactory {

	private final ArooaClass arooaClass;
	
	/**
	 * Constructor.
	 * 
	 * @param forClass The class. Must not be null.
	 */
	public GenericDesignFactory(ArooaClass forClass) {
		if (forClass == null) {
			throw new NullPointerException("No Class.");
		}
		this.arooaClass = forClass;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.design.DesignFactory#createDesign(boolean, org.oddjob.arooa.parsing.ArooaElement, org.oddjob.arooa.parsing.ArooaContext)
	 */
	public DesignInstance createDesign( 
			ArooaElement element, 
			ArooaContext parentContext) 
	throws ArooaPropertyException {

		boolean componentInstance = 
			parentContext.getArooaType() == ArooaType.COMPONENT; 
		
		GenericDesignInstance design;
		if (componentInstance) {
			design = new DesignComponentInstance(
					element, arooaClass, parentContext);
		} else {
			design = new DesignValueInstance(
					element, arooaClass, parentContext);
		}
		
		design.children(designProperties(design));
		
		return design;
	}
	
	/**
	 * Create the {@link DesignProperty}s for a design.
	 * 
	 * @param design The design.
	 * 
	 * @return Array of design properties.
	 */
	public DesignProperty[] designProperties(DesignInstance design) {
		
		ArooaContext parentContext = design.getArooaContext().getParent();
		
		boolean componentInstance = 
				parentContext.getArooaType() == ArooaType.COMPONENT; 
		
		ArooaSession session = parentContext.getSession();
		
		PropertyAccessor accessor = session.getTools().getPropertyAccessor();
		
		BeanOverview overview = arooaClass.getBeanOverview(
				accessor);
				
		List<DesignProperty> designProperties = 
				new ArrayList<DesignProperty>();
			
		List<String> properties = new ArrayList<String>(
				Arrays.asList(overview.getProperties()));

		if (componentInstance) {
			properties.remove("id");
		}

		ArooaBeanDescriptor arooaBeanDescriptor = 
				session.getArooaDescriptor().getBeanDescriptor(
						arooaClass, accessor);

		BeanDescriptorHelper propertyHelper = new BeanDescriptorHelper(arooaBeanDescriptor);

		for (String property: properties) {

			if (!overview.hasWriteableProperty(property)) {
				continue;
			}

			Class<?> propertyClassName = overview.getPropertyType(property);

			if (propertyHelper.isHidden(property)) {
				continue;
			}

				if (propertyHelper.isAttribute(property)) {
					
					designProperties.add(new SimpleTextAttribute(property, design));				
					continue;
				}
					
				if (propertyHelper.isText(property)) {
					
					designProperties.add(new SimpleTextProperty(property));				
					continue;
				}
			
				ArooaType type = propertyHelper.getArooaType(property);
										
				DesignElementProperty elementProperty; 
				
				if (overview.isIndexed(property)) {
					elementProperty = new IndexedDesignProperty(
							property, propertyClassName, type, design);
				}
				else if (overview.isMapped(property)) {
					elementProperty = new MappedDesignProperty(
							property, propertyClassName, type, design);
				}
				else {
					elementProperty = new SimpleDesignProperty(
							property, propertyClassName, type, design);
				}
				
				designProperties.add(elementProperty);
			}
			
		return designProperties.toArray(new DesignProperty[0]);
	}
	
}
