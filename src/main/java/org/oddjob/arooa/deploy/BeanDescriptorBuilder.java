package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;

import java.util.*;

/**
 * Helper class to provide an {@link ArooaBeanDescriptor}. This
 * is the main implementation and as such should really have a different
 * name.
 * <p>
 *
 * @author rob
 * @see DefaultBeanDescriptorProvider
 * @see AnnotatedBeanDescriptorContributor
 * @see ArooaDescriptorBean
 */
public class BeanDescriptorBuilder
        implements BeanDescriptorAccumulator {

    /**
     * The class wrapper this is for.
     */
    private final ArooaClass classIdentifier;

    private final Map<String, ConfiguredHow> configuredHowMap =
            new HashMap<>();

    private final Map<String, String> flavours =
            new HashMap<>();

    private final Set<String> autos = new HashSet<>();

    /**
     * The component property.
     */
    private String componentProperty;

    /**
     * The text property.
     */
    private String textProperty;

    /**
     * The parsing interceptor.
     */
    private ParsingInterceptor parsingInterceptor;

    private ArooaAnnotations arooaAnnotations;

    /**
     * Constructor
     *
     * @param classFor
     */
    public BeanDescriptorBuilder(ArooaClass classFor) {
        this.classIdentifier = Objects.requireNonNull(classFor, "Class identifier is null.");
    }

    /**
     * Get the class identifier this is the descriptor for.
     *
     * @return The class identifier.
     */
    @Override
    public ArooaClass getClassIdentifier() {
        return classIdentifier;
    }

    @Override
    public void addElementProperty(String propertyName) {
        configuredHowMap.put(propertyName, ConfiguredHow.ELEMENT);
    }

    @Override
    public void addAttributeProperty(String propertyName) {
        configuredHowMap.put(propertyName, ConfiguredHow.ATTRIBUTE);
    }

    @Override
    public void addHiddenProperty(String propertyName) {
        configuredHowMap.put(propertyName, ConfiguredHow.HIDDEN);
    }

    @Override
    public void setAuto(String propertyName) {
        autos.add(propertyName);
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
        configuredHowMap.put(property, ConfiguredHow.ELEMENT);
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
        configuredHowMap.put(property, ConfiguredHow.TEXT);
    }

    /**
     * Set the flavour for a property.
     *
     * @param property
     * @param flavour
     */
    public void setFlavour(String property, String flavour) {
        flavours.put(property, flavour);
    }

    /**
     * Set the parsing interceptor. Used by
     * {@link AnnotatedBeanDescriptorContributor}.
     *
     * @param interceptor
     */
    public void setParsingInterceptor(ParsingInterceptor interceptor) {
        this.parsingInterceptor = interceptor;
    }


    public BeanDescriptorBuilder setArooaAnnotations(ArooaAnnotations arooaAnnotations) {
        this.arooaAnnotations = arooaAnnotations;
        return this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " for " + classIdentifier;
    }

    public ArooaBeanDescriptor build() {

        Map<String, PropertyDefinition> properties = new LinkedHashMap<>();
        for (Map.Entry<String, ConfiguredHow> entry : this.configuredHowMap.entrySet()) {
            String propertyName = entry.getKey();
            ConfiguredHow configuredHow = entry.getValue();

            properties.put(propertyName,
                    new PropertyDefinition(configuredHow,
                            flavours.get(propertyName), autos.contains(propertyName)));
        }

        return new Immutable(this.classIdentifier,
                properties,
                componentProperty,
                textProperty,
                this.parsingInterceptor,
                this.arooaAnnotations);
    }


    static class Immutable implements ArooaBeanDescriptor {

        private final ArooaClass classIdentifier;

        /**
         * accumulated property definitions.
         */
        private final Map<String, PropertyDefinition> properties;

        /**
         * The component property.
         */
        private final String componentProperty;

        /**
         * The text property.
         */
        private final String textProperty;

        /**
         * The parsing interceptor.
         */
        private final ParsingInterceptor parsingInterceptor;

        private final ArooaAnnotations arooaAnnotations;

        Immutable(ArooaClass classIdentifier,
                  Map<String, PropertyDefinition> properties,
                  String componentProperty,
                  String textProperty,
                  ParsingInterceptor parsingInterceptor,
                  ArooaAnnotations arooaAnnotations) {
            this.classIdentifier = classIdentifier;
            this.properties = properties;
            this.componentProperty = componentProperty;
            this.textProperty = textProperty;
            this.parsingInterceptor = parsingInterceptor;
            this.arooaAnnotations = Optional.ofNullable(arooaAnnotations)
                    .orElseGet(NoAnnotations::new);
        }

        @Override
        public String getComponentProperty() {
            return componentProperty;
        }

        @Override
        public String getTextProperty() {
            return textProperty;
        }


        @Override
        public ConfiguredHow getConfiguredHow(String property) {
            PropertyDefinition propertyDefinition = properties.get(property);
            if (propertyDefinition == null) {
                throw new ArooaPropertyException(property, "No writeable property [" + property + "]");
            }

            return propertyDefinition.configuredHow;
        }

        @Override
        public String getFlavour(String property) {
            PropertyDefinition propertyDefinition = properties.get(property);
            if (propertyDefinition == null) {
                return null;
            }

            return propertyDefinition.flavour;
        }

        @Override
        public boolean isAuto(String property) {
            PropertyDefinition propertyDefinition = properties.get(property);
            if (propertyDefinition == null) {
                return false;
            }
            return propertyDefinition.auto;
        }

        @Override
        public ParsingInterceptor getParsingInterceptor() {
            return parsingInterceptor;
        }

        @Override
        public ArooaAnnotations getAnnotations() {
            return arooaAnnotations;
        }

        @Override
        public String toString() {
            return "ArooaBeanDescriptor for " + classIdentifier;
        }
    }

    static class PropertyDefinition {

        private final ConfiguredHow configuredHow;

        private final String flavour;

        private final boolean auto;

        PropertyDefinition(ConfiguredHow configuredHow, String flavour, boolean auto) {
            this.configuredHow = configuredHow;
            this.flavour = flavour;
            this.auto = auto;
        }
    }
}