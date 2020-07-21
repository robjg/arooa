package org.oddjob.arooa.xml;

import org.oddjob.arooa.*;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.life.ComponentPersister;
import org.oddjob.arooa.life.ComponentProxyResolver;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.NamespaceMappings;
import org.oddjob.arooa.parsing.RootContext;
import org.oddjob.arooa.parsing.SimplePrefixMappings;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.runtime.PropertyManager;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

/**
 * An {@link ArooaParser} that parses an {@link ArooaConfiguration}
 * into an XML string.
 * <p>
 * Once the {@link #parse(ArooaConfiguration)} method has been
 * called the XML is available using the {@link #getXml()}
 * method.
 *
 * @author rob
 */
public class XMLArooaParser implements ArooaParser<ArooaContext> {

    private final NamespaceMappings namespaceMappings;

    private XmlHandler2 handler;

    public XMLArooaParser() {
        this(null);
    }

    public XMLArooaParser(NamespaceMappings namespaceMappings) {
        this.namespaceMappings = namespaceMappings;
    }

    @Override
    public ConfigurationHandle parse(ArooaConfiguration configuration) throws ArooaParseException {

        Objects.requireNonNull(configuration);

        handler = new XmlHandler2();


        RootContext context = Optional.ofNullable(namespaceMappings)
                .map(
                        mappings -> new RootContext(
                                null,
                                new XMLParserSession(namespaceMappings),
                                handler))
                .orElseGet(
                        () -> new RootContext(
                                null,
                                new SimplePrefixMappings(),
                                handler
                        ));

        return configuration.parse(context);
    }

    public String getXml() {
        if (handler == null) {
            return null;
        }
        return handler.getXml();
    }
}


class XMLParserSession implements ArooaSession {

    private final NamespaceMappings namespaceMappings;

    XMLParserSession(NamespaceMappings namespaceMappings) {
        this.namespaceMappings = namespaceMappings;
    }

    @Override
    public ArooaDescriptor getArooaDescriptor() {
        return new ArooaDescriptor() {
            @Override
            public ConversionProvider getConvertletProvider() {
                throw new UnsupportedOperationException("Not required for XMLParser.");
            }

            @Override
            public ElementMappings getElementMappings() {
                throw new UnsupportedOperationException("Not required for XMLParser.");
            }

            @Override
            public String getPrefixFor(URI namespace) {
                return namespaceMappings.getPrefixFor(namespace);
            }

            @Override
            public ClassResolver getClassResolver() {
                throw new UnsupportedOperationException("Not required for XMLParser.");
            }

            @Override
            public ArooaBeanDescriptor getBeanDescriptor(ArooaClass forClass, PropertyAccessor accessor) {
                throw new UnsupportedOperationException("Not required for XMLParser.");
            }

            @Override
            public String[] getPrefixes() {
                return namespaceMappings.getPrefixes();
            }

            @Override
            public URI getUriFor(String prefix) {
                return namespaceMappings.getUriFor(prefix);
            }
        };
    }

    @Override
    public ComponentPersister getComponentPersister() {
        throw new UnsupportedOperationException("Not required for XMLParser.");
    }

    @Override
    public ComponentPool getComponentPool() {
        return new ComponentPool() {

            @Override
            public void configure(Object component) {
                throw new UnsupportedOperationException("Not required for XMLParser.");
            }

            @Override
            public ArooaContext contextFor(Object component) {
                throw new UnsupportedOperationException("Not required for XMLParser.");
            }

            @Override
            public String getIdFor(Object either) {
                throw new UnsupportedOperationException("Not required for XMLParser.");
            }

            @Override
            public Iterable<ComponentTrinity> allTrinities() {
                throw new UnsupportedOperationException("Not required for XMLParser.");
            }

            @Override
            public ComponentTrinity trinityForId(String id) {
                throw new UnsupportedOperationException("Not required for XMLParser.");
            }

            @Override
            public ComponentTrinity trinityFor(Object either) {
                throw new UnsupportedOperationException("Not required for XMLParser.");
            }

            @Override
            public ComponentTrinity trinityForContext(ArooaContext context) {
                throw new UnsupportedOperationException("Not required for XMLParser.");
            }

            @Override
            public String registerComponent(ComponentTrinity trinity, String id) {
                throw new UnsupportedOperationException("Not required for XMLParser.");
            }

            @Override
            public boolean remove(Object component) {
                throw new UnsupportedOperationException("Not required for XMLParser.");
            }

            @Override
            public void save(Object component) {
                throw new UnsupportedOperationException("Not required for XMLParser.");
            }
        };
    }

    @Override
    public BeanRegistry getBeanRegistry() {
        throw new UnsupportedOperationException("Not required for XMLParser.");
    }

    @Override
    public PropertyManager getPropertyManager() {
        throw new UnsupportedOperationException("Not required for XMLParser.");
    }

    @Override
    public ComponentProxyResolver getComponentProxyResolver() {
        throw new UnsupportedOperationException("Not required for XMLParser.");
    }

    @Override
    public ArooaTools getTools() {
        throw new UnsupportedOperationException("Not required for XMLParser.");
    }
}
