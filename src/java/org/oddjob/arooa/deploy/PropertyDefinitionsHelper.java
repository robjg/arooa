package org.oddjob.arooa.deploy;

import java.util.LinkedHashMap;
import java.util.Map;

import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.reflect.ArooaClass;

/**
 * Helper class to provide an {@link ArooaBeanDescriptor}. This 
 * is the main implementation and as such should really have a different
 * name.
 * <p>
 * 
 * @see DefaultBeanDescriptorProvider
 * @see AnnotatedBeanDescriptorProvider
 * @see ArooaDescriptorBean
 * 
 * @author rob
 *
 */
public class PropertyDefinitionsHelper implements ArooaBeanDescriptor {

	/** The class wrapper this is for. */
	private final ArooaClass classIdentifier;
	
	/** accumulated property definitions. */
	private final Map<String, PropertyDefinition> properties = 
		new LinkedHashMap<String, PropertyDefinition>();

	/** support annotations. */
	private final ArooaAnnotationsHelper annotations;
	
	/** The component property. */
	private String componentProperty;
	
	/** The text property. */
	private String textProperty;

	/** The parsing interceptor. */
	private ParsingInterceptor parsingInterceptor;
	
	/**
	 * Constructor
	 * 
	 * @param classFor
	 */
	public PropertyDefinitionsHelper(ArooaClass classFor) {
		if (classFor == null) {
			throw new NullPointerException("Class identifier is null.");
		}
		this.classIdentifier = classFor;
		this.annotations = new ArooaAnnotationsHelper(classFor);
	}
	
	/**
	 * Get the class identifier this is the descriptor for.
	 * 
	 * @return The class identifier.
	 */
	public ArooaClass getClassIdentifier() {
		return classIdentifier;
	}
	
	
	/**
	 * Accumulate definitions of the bean.
	 * 
	 * @param propertyDefinition
	 */
	public void addPropertyDefinition(PropertyDefinition propertyDefinition) {
		if (propertyDefinition.isComponentProperty()) {
			setComponentProperty(propertyDefinition.getName());
		}
		if (propertyDefinition.isTextProperty()) {
			setTextProperty(propertyDefinition.getName());
		}

		String propertyName =  propertyDefinition.getName();
		if (propertyName == null) {
			throw new NullPointerException("No name for a property definition.");
		}
		
		PropertyDefinition existing = properties.get(propertyName);		
		if (existing == null) {
			properties.put(propertyName, propertyDefinition);
		}
		else {
			if (propertyDefinition.getType() != null) {
				existing.setType(propertyDefinition.getType());
			}
			if (propertyDefinition.getFlavour() != null) {
				existing.setFlavour(propertyDefinition.getFlavour());
			}
			if (propertyDefinition.getAuto() != null) {
				existing.setAuto(propertyDefinition.getAuto());
			}
		}
		
		annotations.addPropertyDefinition(propertyDefinition);
	}
	
	/**
	 * Retrieve a property. Throw an exception if it doesn't exist.
	 * 
	 * @param property
	 * @return
	 */
	private PropertyDefinition retrive(String property) {
		PropertyDefinition propertyDefinition = properties.get(property);
		if (propertyDefinition == null) {
			throw new NullPointerException(
					"No configuration information for property [" + 
					property + "] of class [" + classIdentifier.forClass() + 
					"] - Is this property defined correctly?");
		}
		return propertyDefinition;
	}
	
	/**
	 * Set the property type. Used by 
	 * {@link AnnotatedBeanDescriptorProvider}.
	 * 
	 * @param property
	 * @param propertyType
	 */
	public void setPropertyType(String property, 
			PropertyDefinition.PropertyType propertyType) {
		PropertyDefinition propertyDefinition = retrive(property);
		propertyDefinition.setType(propertyType);
		
		if (propertyDefinition.isComponentProperty()) {
			setComponentProperty(propertyDefinition.getName());
		}
		if (propertyDefinition.isTextProperty()) {
			setTextProperty(propertyDefinition.getName());
		}
	}
	
	/**
	 * Set the component property.
	 * 
	 * @param property
	 */
	public void setComponentProperty(String property) {
		if (componentProperty != null) {
			throw new IllegalStateException("Component property of " + 
					componentProperty + " can't be changed to " +
					property);
		}
		componentProperty = property;
		
	}
	
	/**
	 * Set the text property.
	 * 
	 * @param property
	 */
	public void setTextProperty(String property) {
		if (textProperty != null) {
			throw new IllegalStateException("Text property of " + 
					textProperty + " can't be changed to " +
					property);
		}
		textProperty = property;
	}
	
	/**
	 * Set the auto property. Used by 
	 * {@link AnnotatedBeanDescriptorProvider}.
	 * 
	 * @param property The property name.
	 */
	public void setAuto(String property) {
		retrive(property).setAuto(true);
	}
	
	/**
	 * Set the flavour for a property.
	 *  
	 * @param property
	 * @param flavour
	 */
	public void setFlavour(String property, String flavour) {
		retrive(property).setFlavour(flavour);
	}
	
	/**
	 * Set the parsing interceptor. Used by 
	 * {@link AnnotatedBeanDescriptorProvider}.
	 * 
	 * @param interceptor
	 */
	public void setParsingInterceptor(ParsingInterceptor interceptor) {
		this.parsingInterceptor = interceptor;
	}
	
	/**
	 * Add an annotation.
	 * 
	 * @param annotation
	 */
	public void addAnnotationDefinition(AnnotationDefinition annotation) {
		this.annotations.addAnnotationDefintion(annotation);
	}
	
	/**
	 * Called to merge an {@link BeanDefinition} from an 
	 * {@link ArooaDescriptorBean} into what has been provided already.
	 * 
	 * @param beanDef
	 */
	public void mergeFromBeanDefinition(BeanDefinition beanDef) {

		for (PropertyDefinition def: beanDef.toPropertyDefinitions()) {
			addPropertyDefinition(def);
		}
		
		for (AnnotationDefinition def: beanDef.toAnnotationDefinitions()) {
			addAnnotationDefinition(def);
		}
		
		setParsingInterceptor(beanDef.getInterceptor());
	}
	
	public String getComponentProperty() {
		return componentProperty;
	}
	
	public String getTextProperty() {
		return textProperty;
	}

	public ConfiguredHow getConfiguredHow(String property) {
		PropertyDefinition propertyDefinition = properties.get(property);
		if (propertyDefinition == null) {
			return null;
		}
	
		return propertyDefinition.getConfiguredHow();
	}
	
	public String getFlavour(String property) {
		PropertyDefinition propertyDefinition = properties.get(property);
		if (propertyDefinition == null) {
			return null;
		}
	
		return propertyDefinition.getFlavour();
	}
	
	public boolean isAuto(String property) {
		PropertyDefinition propertyDefinition = properties.get(property);
		if (propertyDefinition == null) {
			return false;
		}
	
		Boolean auto = propertyDefinition.getAuto(); 
		return auto == Boolean.TRUE;
	}

	public ParsingInterceptor getParsingInterceptor() {
		return parsingInterceptor;
	}

	@Override
	public ArooaAnnotations getAnnotations() {
		return annotations;
	}
	
}
