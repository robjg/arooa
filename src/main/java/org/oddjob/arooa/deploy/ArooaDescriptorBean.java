/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.deploy;

import org.oddjob.arooa.*;
import org.oddjob.arooa.beandocs.MappingsBeanDoc;
import org.oddjob.arooa.beandocs.MappingsContents;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionProviderFactory;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.life.ClassLoaderClassResolver;
import org.oddjob.arooa.life.ElementsForIdentifier;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.utils.ClassUtils;
import org.oddjob.arooa.utils.ListSetterHelper;

import java.net.URI;
import java.util.*;

/**
 * A bean style implementation of an {@link ArooaDescriptorFactory}.
 * As such it is able to be configured using a {@link StandardArooaParser}.
 *
 * @author rob
 * @oddjob.description A definition of an Arooa descriptor.
 * @oddjob.example See the Dev Guide. There is an example of a custom descriptor
 * <a href="http://rgordon.co.uk/projects/oddjob/devguide/oddballs.html">here</a>.
 * @oddjob.example The descriptor for the JMX client and server. This is the internal descriptor
 * used by Oddjob.
 * <p>
 * {@oddjob.xml.resource org/oddjob/jmx/jmx.xml}
 */
public class ArooaDescriptorBean
        implements ArooaDescriptorFactory {

    /**
     * @oddjob.property
     * @oddjob.description The name space that applies to
     * all elements defined in definitions.
     * @oddjob.required No.
     */
    private URI namespace;

    /**
     * @oddjob.property
     * @oddjob.description The default prefix for the name space.
     * @oddjob.required Yes if name space is provided.
     */
    private String prefix;

    /**
     * @oddjob.property conversions
     * @oddjob.description List of class names that must implement
     * the {@link ConversionProvider} interface.
     * @oddjob.required No.
     */
    private final List<ConversionProviderFactory> convertlets =
            new ArrayList<>();

    /**
     * @oddjob.property
     * @oddjob.description A list of {@link BeanDefinitionBean}s for components.
     * @oddjob.required No.
     */
    private final List<BeanDefinitionBean> components =
            new ArrayList<>();

    /**
     * @oddjob.property
     * @oddjob.description A list of {@link BeanDefinitionBean}s for values.
     * @oddjob.required No.
     */
    private final List<BeanDefinitionBean> values =
            new ArrayList<>();

    /**
     * Setter for conversions.
     *
     * @param index
     * @param convertletProvider
     */
    public void setConversions(int index,
                               ConversionProviderFactory convertletProvider) {
        convertlets.add(index, convertletProvider);
    }

    /**
     * Setter for components.
     *
     * @param index     The index of the component definition.
     * @param component The component definition.
     */
    public void setComponents(int index, BeanDefinitionBean component) {
        new ListSetterHelper<>(this.components).set(index, component);
    }

    /**
     * Setter for values.
     *
     * @param index The index of the value definition.
     * @param value The value definition.
     */
    public void setValues(int index, BeanDefinitionBean value) {
        new ListSetterHelper<>(this.values).set(index, value);
    }

    /**
     * Internal class to hold mappings.
     */
    static class Mappings implements ElementMappings {

        private final Map<ArooaElement, SimpleArooaClass> mappings =
                new LinkedHashMap<>();

        private final Map<ArooaElement, DesignFactory> designs =
                new LinkedHashMap<>();

        @Override
        public ArooaClass mappingFor(ArooaElement element,
                                     InstantiationContext parentContext) {

            return mappings.get(element);
        }

        @Override
        public DesignFactory designFor(ArooaElement element,
                                       InstantiationContext parentContext) {

            return designs.get(element);
        }

        @Override
        public ArooaElement[] elementsFor(
                InstantiationContext propertyContext) {
            return new ElementsForIdentifier(
                    mappings).elementsFor(propertyContext);
        }

        @Override
        public MappingsContents getBeanDoc(ArooaType arooaType) {
            return new MappingsBeanDoc(mappings);
        }
    }

    private void populateMappings(Mappings mappings,
                                  BeanDefinitions definitions,
                                  Map<ArooaClass, BeanDefinitionBean> definitionsMap,
                                  ClassLoader classLoader) {

        URI namespace = definitions.getNamespace();

        for (BeanDefinitionBean beanDefinition : definitions.getDefinitions()) {

            if (beanDefinition.getElement() == null) {
                throw new ArooaException("No Element in Class Mappings.");
            }

            ArooaElement element = new ArooaElement(
                    namespace,
                    beanDefinition.getElement());

            SimpleArooaClass classIdentifier;

            try {
                Class<?> theClass = ClassUtils.classFor(
                        beanDefinition.getClassName(),
                        classLoader);

                classIdentifier = new SimpleArooaClass(theClass);

                mappings.mappings.put(element, classIdentifier);
            } catch (ClassNotFoundException e) {
                throw new ArooaException("Failed loading class for element " +
                        element + " class [" + beanDefinition.getClassName() +
                        "] using class loader [" + classLoader + "]",
                        e);
            }

            DesignFactory designFactory = beanDefinition.getDesign();

            if (beanDefinition.getDesignFactory() != null) {

                try {
                    designFactory = (DesignFactory) ClassUtils.classFor(
                            beanDefinition.getDesignFactory(),
                            classLoader).getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new ArooaException("Failed loading design class for element " +
                            element + " class [" + beanDefinition.getClassName() +
                            "] using class loader [" + classLoader + "]",
                            e);
                }
            }

            if (designFactory != null) {
                mappings.designs.put(element, designFactory);
            }

            definitionsMap.put(classIdentifier, beanDefinition);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.oddjob.arooa.deploy.ArooaDescriptorFactory#createDescriptor(java.lang.ClassLoader)
     */
    public ArooaDescriptor createDescriptor(final ClassLoader classLoader) {

        final Map<ArooaClass, BeanDefinitionBean> beanDefinitions =
                new HashMap<>();

        final Map<ArooaClass, ArooaBeanDescriptor> beanDescriptors =
                new HashMap<>();

        final Mappings componentMappings = new Mappings();

        BeanDefinitions components = new BeanDefinitions(
                namespace, prefix, this.components);

        populateMappings(componentMappings, components,
                beanDefinitions,
                classLoader);

        final Mappings valueMappings = new Mappings();

        BeanDefinitions values = new BeanDefinitions(
                namespace, prefix, this.values);

        populateMappings(valueMappings, values,
                beanDefinitions,
                classLoader);

        return new ArooaDescriptor() {

            @Override
            public ElementMappings getElementMappings() {
                return new MappingsSwitch(componentMappings, valueMappings);
            }

            @Override
            public ArooaBeanDescriptor getBeanDescriptor(
                    ArooaClass forClass, PropertyAccessor accessor) {

                ArooaBeanDescriptor beanDescriptor =
                        beanDescriptors.get(forClass);

                if (beanDescriptor != null) {
                    return beanDescriptor;
                }

                BeanDefinitionBean beanDefinition =
                        beanDefinitions.get(forClass);

                if (beanDefinition == null) {
                    return null;
                }

                beanDescriptor = SupportedBeanDescriptorProvider
                        .withBeanDefinition(beanDefinition).getBeanDescriptor(
                                forClass, accessor);

                beanDescriptors.put(forClass, beanDescriptor);

                return beanDescriptor;
            }

            @Override
            public ConversionProvider getConvertletProvider() {
                return registry -> {
                    for (ConversionProviderFactory providerFactory : convertlets) {
                        ConversionProvider conversionProvider =
                                providerFactory.createConversionProvider(classLoader);
                        conversionProvider.registerWith(registry);
                    }
                };
            }

            @Override
            public String getPrefixFor(URI namespace) {
                if (namespace == null) {
                    return null;
                }

                if (namespace.equals(getNamespace())) {
                    return getPrefix();
                }

                return null;
            }

            @Override
            public URI getUriFor(String prefix) {
                if (prefix == null) {
                    return null;
                }

                if (prefix.equals(getPrefix())) {
                    return getNamespace();
                }

                return null;
            }

            @Override
            public String[] getPrefixes() {
                return Optional.ofNullable(getPrefix())
                        .map(prefix -> new String[]{prefix})
                        .orElseGet(() -> new String[0]);
            }

            @Override
            public ClassResolver getClassResolver() {
                return new ClassLoaderClassResolver(classLoader);
            }
        };
    }

    public URI getNamespace() {
        return namespace;
    }

    @ArooaAttribute
    public void setNamespace(URI namespace) {
        this.namespace = namespace;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
