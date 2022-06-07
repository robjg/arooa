package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.deploy.annotations.*;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rob
 * @oddjob.description Provide a definition for a property within
 * an {@link BeanDefinitionBean}.
 * <p>
 * Providing property definitions within a BeanDefinition is an alternative
 * to using annotations in the Java code or providing an
 * {@link ArooaBeanDescriptor} Arooa class file.
 */
public class PropertyDefinitionBean {

    public enum PropertyType {
        ATTRIBUTE(ArooaAttribute.class),
        ELEMENT(ArooaElement.class),
        TEXT(ArooaText.class),
        COMPONENT(ArooaComponent.class),
        HIDDEN(ArooaHidden.class);

        private final Class<? extends Annotation> annotation;


        PropertyType(Class<? extends Annotation> annotation) {
            this.annotation = annotation;
        }
    }

    private static final Map<String, PropertyType> annotationsToPropertyTypes =
            new HashMap<>();

    static {
        for (PropertyType propertyType : PropertyType.values()) {
            annotationsToPropertyTypes.put(
					propertyType.annotation.getName(), propertyType);
        }
    }

    /**
     * @oddjob.property
     * @oddjob.description The name of the property.
     * @oddjob.required Yes.
     */
    private String name;

    /**
     * @oddjob.property
     * @oddjob.description The type of the property. One
     * of ATTRIBUTE, ELEMENT, TEXT, COMPONENT, HIDDEN.
     * @oddjob.required Yes.
     */
    private PropertyType type;

    /**
     * @oddjob.property
     * @oddjob.description Not used at present.
     * @oddjob.required No.
     */
    private String flavour;

    /**
     * @oddjob.property
     * @oddjob.description Is the property set automatically by the
     * framework. True/False.
     * @oddjob.required No. Defaults to false.
     */
    private boolean auto;

    /**
     * @oddjob.property
     * @oddjob.description An annotation for the property.
     * @oddjob.required No.
     */
    private String annotation;

    /**
     * No Arg Constructor.
     */
    public PropertyDefinitionBean() {
    }

    /**
     * Constructor when used from code.
     *
     * @param name
     * @param type
     */
    public PropertyDefinitionBean(
            String name, PropertyType type) {
        this.name = name;
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    boolean isComponentProperty() {
        return type == PropertyType.COMPONENT;
    }

    boolean isTextProperty() {
        return type == PropertyType.TEXT;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    public PropertyType getType() {
        return type;
    }

    public ConfiguredHow getConfiguredHow() {
        // If it's just an annotation property then this is null.
        if (type == null) {
            return null;
        }
        switch (type) {
            case ATTRIBUTE:
                return ConfiguredHow.ATTRIBUTE;
            case ELEMENT:
            case COMPONENT:
                return ConfiguredHow.ELEMENT;
            case TEXT:
                return ConfiguredHow.TEXT;
            case HIDDEN:
                return ConfiguredHow.HIDDEN;
            default:
                throw new IllegalArgumentException("Unknown type " + type);
        }
    }

    @Override
    public String toString() {
        return "PropertyDefinitionBean: name=" + name +
                ", type=" + type + ".";
    }

    public String getFlavour() {
        return flavour;
    }

    public void setFlavour(String flavour) {
        this.flavour = flavour;
    }

    public boolean getAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;

        // Just for consistency ensure annotations have same affect as
        // setting type. Annotations came way later, had they come first
        // we wouldn't have bothered with the type.
//        Optional.ofNullable(annotationsToPropertyTypes.get(annotation))
//                .ifPresent(this::setType);
    }
}

