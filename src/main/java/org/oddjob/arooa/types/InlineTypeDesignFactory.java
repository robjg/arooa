package org.oddjob.arooa.types;

import org.oddjob.arooa.*;
import org.oddjob.arooa.design.DescriptorDesignFactory;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignParser;
import org.oddjob.arooa.design.designer.ArooaDesignerForm;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.*;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.runtime.AbstractRuntimeConfiguration;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.xml.XMLInterceptor;

/**
 * A {@link DesignFactory} for an {@link InlineType}.
 */
public class InlineTypeDesignFactory implements DesignFactory {
    @Override
    public DesignInstance createDesign(ArooaElement element, ArooaContext parentContext) throws ArooaPropertyException {
        return new InlineDesignInstance(element, parentContext);
    }
}

class InlineDesignInstance implements DesignInstance {

    private final ArooaContext arooaContext;

    InlineDesignInstance(ArooaElement element, ArooaContext parentContext) {
        if (!element.equals(InlineType.ELEMENT)) {
            throw new IllegalArgumentException("Unexpected Element " + element);
        }

        this.arooaContext = new InlineTypeDesignContext(parentContext);
    }

    @Override
    public ArooaElement element() {
        return InlineType.ELEMENT;
    }

    @Override
    public Form detail() {

        ArooaContext thisContext = getArooaContext();

        InlineType.ConfigurationDefinition configurationDefinition =
                (InlineType.ConfigurationDefinition) thisContext.getSession()
                        .getBeanRegistry()
                        .lookup(InlineType.INLINE_CONFIGURATION_DEFINITION);

        if (configurationDefinition == null) {
            configurationDefinition = InlineType.configurationDefinition(
                    BeanType.ELEMENT,
                    new DescriptorDesignFactory());
        }

        ArooaContext childContext =
                new ChildCatcher(thisContext, 0).getChild();

        ArooaConfiguration configuration;

        if (childContext == null) {
            try {
                new ElementConfiguration(configurationDefinition.rootElement()).parse(thisContext);
                childContext =
                        new ChildCatcher(thisContext, 0).getChild();
            } catch (ArooaParseException e) {
                throw new RuntimeException(e);
            }
        }

        configuration = childContext.getConfigurationNode();

        DesignParser parser = new DesignParser(thisContext.getSession(),
                configurationDefinition.rootDesignFactory());

        parser.setArooaType(ArooaType.COMPONENT);
        parser.setExpectedDocumentElement(configurationDefinition.rootElement());

        try {
            ConfigurationHandle configHandle = parser.parse(configuration);

            return new ArooaDesignerForm(parser,
                    () -> {
                        thisContext.getRuntime().destroy();
                        configHandle.save();
                        thisContext.getRuntime().init();

                        return true;
                    });
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArooaContext getArooaContext() {
        return arooaContext;
    }

    class InlineTypeDesignContext implements ArooaContext {

        private final ArooaContext parentContext;

        private final ArooaContext firstElementContext;

        private final RuntimeConfiguration runtime =
                new AbstractRuntimeConfiguration() {

                    private boolean initialised;

                    public ArooaClass getClassIdentifier() {
                        return new SimpleArooaClass(Object.class);
                    }

                    public void init() throws ArooaConfigurationException {
                        firstElementContext.getRuntime().init();

                        fireBeforeInit();

                        RuntimeConfiguration parentRuntime = parentContext.getRuntime();

                        // check it's not the root
                        if (parentRuntime != null) {

                            int index = parentContext.getConfigurationNode().indexOf(
                                    InlineTypeDesignContext.this.getConfigurationNode());

                            if (index < 0) {
                                throw new IllegalStateException(
                                        "Configuration node not added to parent.");
                            }

                            parentRuntime.setIndexedProperty(null, index, InlineDesignInstance.this);
                        }

                        fireAfterInit();

                        initialised = true;
                    }
                    public void configure() {
                        fireBeforeConfigure();
                        fireAfterConfigure();
                    }
                    public void destroy() throws ArooaConfigurationException {

                        fireBeforeDestroy();

                        if (initialised) {
                            firstElementContext.getRuntime().destroy();

                            RuntimeConfiguration parentRuntime = parentContext.getRuntime();

                            // check it's not the root
                            if (parentRuntime != null) {

                                int index = parentContext.getConfigurationNode().indexOf(
                                        InlineTypeDesignContext.this.getConfigurationNode());

                                if (index < 0) {
                                    throw new IllegalStateException(
                                            "Configuration node not added to parent.");
                                }

                                parentContext.getRuntime().setIndexedProperty(null, index, null);
                            }
                            initialised = false;
                        }

                        fireAfterDestroy();
                    }

                    public void setIndexedProperty(String name, int index,
                                                   Object value) throws ArooaException {
                        throw new UnsupportedOperationException();
                    }
                    public void setMappedProperty(String name, String key,
                                                  Object value) throws ArooaException {
                        throw new UnsupportedOperationException();
                    }
                    public void setProperty(String name, Object value)
                            throws ArooaException {
                        throw new UnsupportedOperationException();
                    }
                };

        public InlineTypeDesignContext(ArooaContext parentContext) {
            this.parentContext = parentContext;

            ArooaContext interceptContext = new XMLInterceptor()
                    .intercept(this);

            firstElementContext = interceptContext.getArooaHandler().onStartElement(
                    InlineType.ELEMENT, interceptContext);
        }

        public ArooaType getArooaType() {
            return null;
        }

        public ArooaContext getParent() {
            return parentContext;
        }

        public ArooaSession getSession() {
            return parentContext.getSession();
        }

        public RuntimeConfiguration getRuntime() {
            return runtime;
        }

        public ArooaHandler getArooaHandler() {
            return firstElementContext.getArooaHandler();
        }

        public ConfigurationNode getConfigurationNode() {
            return firstElementContext.getConfigurationNode();
        }

        public PrefixMappings getPrefixMappings() {
            return parentContext.getPrefixMappings();
        }
    }
}

