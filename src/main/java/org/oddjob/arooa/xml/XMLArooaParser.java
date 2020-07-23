package org.oddjob.arooa.xml;

import org.oddjob.arooa.*;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.life.ComponentPersister;
import org.oddjob.arooa.life.ComponentProxyResolver;
import org.oddjob.arooa.parsing.*;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.runtime.PropertyManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

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

    private Supplier<String> handler;

    public XMLArooaParser() {
        this(null);
    }

    public XMLArooaParser(NamespaceMappings namespaceMappings) {
        this.namespaceMappings =
                Optional.ofNullable(namespaceMappings)
                        .orElse(new SimplePrefixMappings());
    }

    @Override
    public ConfigurationHandle parse(ArooaConfiguration configuration) throws ArooaParseException {

        Objects.requireNonNull(configuration);



//        RootContext context = Optional.ofNullable(namespaceMappings)
//                .map(
//                        mappings -> new RootContext(
//                                null,
//                                new XMLParserSession(namespaceMappings),
//                                handler))
//                .orElseGet(
//                        () -> new RootContext(
//                                null,
//                                new SimplePrefixMappings(),
//                                handler
//                        ));


        PrefixMappings prefixMappings = new FallbackPrefixMappings(namespaceMappings);

        XmlCallbacks callbacks = new XmlCallbacks(prefixMappings);

        handler = callbacks::getXml;

        SimpleParseContext context = SimpleParseContext.createRootContext()
                .withCallbacks(callbacks.start())
                .andPrefixMappings(prefixMappings);

        return configuration.parse(context);
    }

    public String getXml() {
        if (handler == null) {
            return null;
        }
        return handler.get();
    }
}

class XmlCallbacks {

    private final UriMapping uriMapping;

    private final Document document;


    XmlCallbacks(UriMapping uriMapping) {

        this.uriMapping = uriMapping;

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

        builderFactory.setNamespaceAware(true);

        DocumentBuilder builder;
        try {
            builder = builderFactory.newDocumentBuilder();
            this.document = builder.newDocument();
        } catch (ParserConfigurationException e) {
            throw new ArooaException(e);
        }
    }

    SimpleParseContext.CallbackFunction start() {

        return element -> new Instance(document, elementFrom(element));
    }

    Element elementFrom(ArooaElement element) {

        URI uri = element.getUri();
        String uriString = null;
        if (uri != null) {
            uriString = uri.toString();
        }

        QTag tag = uriMapping.getQName(element);

        Element elementNode = document.createElementNS(
                uriString, tag.toString());

        ArooaAttributes attrs = element.getAttributes();
        String[] attributeNames = attrs.getAttributeNames();
        for (String attributeName : attributeNames) {
            elementNode.setAttribute(
                    attributeName, attrs.get(attributeName));
        }

        return elementNode;
    }

    class Instance implements SimpleParseContext.Callbacks {

        private final Node parent;

        private final Element current;

        Instance(Node parent, Element current) {
            this.parent = parent;
            this.current = current;
        }

        @Override
        public void onText(String text) {
            Text textNode = document.createCDATASection(text);
            current.appendChild(textNode);
        }

        @Override
        public void onInit() {
            parent.appendChild(current);
        }

        @Override
        public void onDestroy() {
            parent.removeChild(current);
        }

        @Override
        public SimpleParseContext.CallbackFunction childCallbacks() {
            return element -> new Instance(current, elementFrom(element));
        }
    }

    public String getXml() {

        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer serializer;
        try {
            serializer = tfactory.newTransformer();
            //Setup indenting to "pretty print"
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            StringWriter writer = new StringWriter();
            serializer.transform(new DOMSource(document), new StreamResult(writer));

            return writer.toString();

        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
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
