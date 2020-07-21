/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.etc;

import org.oddjob.arooa.*;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.Unknown;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.TextInput;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.*;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.runtime.AbstractRuntimeConfiguration;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.arooa.xml.XMLInterceptor;

import java.util.Objects;

/**
 * A {@link DesignInstance} that is {@link Unknown}. This captures
 */
public class UnknownInstance
        implements DesignInstance, Unknown {

    private String xml = "";

    private final ArooaContext arooaContext;

    private final ArooaElement element;

    public UnknownInstance(ArooaElement element, ArooaContext parentContext) {
        Objects.requireNonNull(element);
        Objects.requireNonNull(parentContext);

        this.element = element;
        this.arooaContext = new UnknownContext(parentContext);
    }

    public ArooaElement element() {
        return element;
    }

    public String toString() {
        return "XML";
    }

    public void setXml(String xml) {
        if (xml == null) {
            throw new NullPointerException("The XML.");
        }
        this.xml = xml;
    }

    public String getXml() {
        return this.xml;
    }

    public Form detail() {
        return new TextInput("XML",
                new TextInput.TextSource() {
                    @Override
                    public String getText() {
                        return xml;
                    }

                    @Override
                    public void setText(String text) {
                        xml = text;
                    }
                });
    }

    public ArooaContext getArooaContext() {
        return arooaContext;
    }

    /**
     * An {@link ArooaContext} that defines its own:
     * {@link ConfigurationNode} so that our {@link UnknownInstance} can be parsed back from the XML,
     * {@link RuntimeConfiguration} so that we can capture the XML from the {@link XMLInterceptor},
     * {@link ArooaHandler} which is that of the {@link XMLInterceptor}.
     */
    class UnknownContext implements ArooaContext {

        private final ArooaContext parentContext;

        private final ConfigurationNode<ArooaContext> configurationNode =
                new AbstractConfigurationNode() {

                    @Override
                    public void addText(String text) {
                        firstElementContext.getConfigurationNode().addText(text);
                    }

                    @Override
                    public ArooaContext getContext() {
                        return UnknownContext.this;
                    }

                    @Override
                    public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext)
                            throws ArooaParseException {

                        if (xml.trim().length() > 0) {
                            XMLConfiguration config = new XMLConfiguration(
                                    UnknownInstance.this.toString(), xml);
                            return config.parse(parentContext);
                        } else {
                            return null;
                        }
                    }

                };

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
                                    arooaContext.getConfigurationNode());

                            if (index < 0) {
                                throw new IllegalStateException(
                                        "Configuration node not added to parent.");
                            }

                            parentRuntime.setIndexedProperty(null, index, UnknownInstance.this);
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
                                        arooaContext.getConfigurationNode());

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
                        // Value null when XMLInterceptor Runtime destroyed.
                        if (value == null) {
                            setXml("Destroyed - You Should Never See This!");
                        } else {
                            setXml((String) value);
                        }
                    }
                };

        private final ArooaContext firstElementContext;

        public UnknownContext(ArooaContext parentContext) {
            this.parentContext = parentContext;

            ArooaContext interceptContext = new XMLInterceptor("xml")
                    .intercept(this);

            firstElementContext = interceptContext.getArooaHandler().onStartElement(
                    element, interceptContext);
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

        public ConfigurationNode<ArooaContext> getConfigurationNode() {
            return configurationNode;
        }

        public PrefixMappings getPrefixMappings() {
            return parentContext.getPrefixMappings();
        }
    }

}
