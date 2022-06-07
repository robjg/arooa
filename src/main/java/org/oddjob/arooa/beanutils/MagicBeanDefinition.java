package org.oddjob.arooa.beanutils;

import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.beanutils.MagicBeanDescriptorProperty.PropertyType;
import org.oddjob.arooa.deploy.DefaultBeanDescriptorProvider;
import org.oddjob.arooa.deploy.NoAnnotations;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaNoPropertyException;
import org.oddjob.arooa.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rob
 * @oddjob.description Definition for a Magic Bean, which is a bean that can be defined
 * dynamically.
 */
public class MagicBeanDefinition {

    /**
     * @oddjob.property
     * @oddjob.description The name of the element.
     * @oddjob.required Yes.
     */
    private String element;

    /**
     * @oddjob.property
     * @oddjob.description The bean properties. This is a list
     * of {@link MagicBeanDescriptorProperty}s.
     * @oddjob.required No.
     */
    private final List<MagicBeanDescriptorProperty> properties =
            new ArrayList<>();

    public String getElement() {
        return element;
    }

    public void setElement(String name) {
        this.element = name;
    }

    public void setProperties(int index, MagicBeanDescriptorProperty property) {

        if (property == null) {
            properties.remove(index);
        } else {
            properties.add(index, property);
        }
    }

    public MagicBeanDescriptorProperty getProperties(int index) {
        return properties.get(index);
    }

    public Pair<ArooaClass, ArooaBeanDescriptor> createMagic(ClassLoader loader) {

        MagicBeanClassCreator classCreator = new MagicBeanClassCreator(
                "DescriptorMagicBean-" + element);

        Map<String, ConfiguredHow> configuredHowByProperty = new HashMap<>();

        String textProperty = null;

        for (MagicBeanDescriptorProperty prop : properties) {

            String className = prop.getType();
            Class<?> propertyClass;
            if (className == null) {
                propertyClass = String.class;
            } else {
                try {
                    propertyClass = Class.forName(className, true, loader);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Property class not found [" + className
                            + "] for property [" + prop.getName() + "] of MagicBean class [" + element + "]");
                }
            }

            String property = prop.getName();

            classCreator.addProperty(property, propertyClass);

            if (prop.getConfigured() != null) {
                PropertyType type = prop.getConfigured();
                switch (type) {
                    case ATTRIBUTE:
                        configuredHowByProperty.put(property,
                                ConfiguredHow.ATTRIBUTE);
                        break;
                    case ELEMENT:
                        configuredHowByProperty.put(property,
                                ConfiguredHow.ELEMENT);
                        break;
                    case TEXT:
                        textProperty = property;
                        configuredHowByProperty.put(property,
                                ConfiguredHow.TEXT);
                        break;
                }
            } else {
                if (DefaultBeanDescriptorProvider.isAttribute(propertyClass)) {
                    configuredHowByProperty.put(property, ConfiguredHow.ATTRIBUTE);
                } else {
                    configuredHowByProperty.put(property, ConfiguredHow.ELEMENT);
                }
            }
        }

        return Pair.of(classCreator.create(),
                new MagicBeanDescriptor(textProperty, configuredHowByProperty));
    }


    static class MagicBeanDescriptor implements ArooaBeanDescriptor {

        private final String textProperty;

        private final Map<String, ConfiguredHow> configuredHowByProperty;

        MagicBeanDescriptor(String textProperty, Map<String, ConfiguredHow> configuredHowByProperty) {
            this.textProperty = textProperty;
            this.configuredHowByProperty = configuredHowByProperty;
        }


        @Override
        public boolean isAuto(String property) {
            return false;
        }

        @Override
        public String getTextProperty() {
            return textProperty;
        }

        @Override
        public ParsingInterceptor getParsingInterceptor() {
            return null;
        }

        @Override
        public String getFlavour(String property) {
            return null;
        }

        @Override
        public ConfiguredHow getConfiguredHow(String property) {
            ConfiguredHow how =
                    configuredHowByProperty.get(property);
            if (how == null) {
                throw new ArooaNoPropertyException(property,
                        MagicBeanClass.class, configuredHowByProperty.keySet().toArray(new String[0]));
            }
            return how;
        }

        @Override
        public String getComponentProperty() {
            return null;
        }

        @Override
        public ArooaAnnotations getAnnotations() {
            return new NoAnnotations();
        }

    }
}
