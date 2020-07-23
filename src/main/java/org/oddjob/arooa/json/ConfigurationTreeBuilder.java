package org.oddjob.arooa.json;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.*;
import org.oddjob.arooa.runtime.ConfigurationNode;

import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Builds an {@link ConfigurationTree}.
 */
abstract public class ConfigurationTreeBuilder<T extends ConfigurationTreeBuilder<T>> {

    private ArooaElement element;

    private String text;

    private final Map<String, String> attributes = new LinkedHashMap<>();

    private final Map<String, List<ConfigurationTree>> children = new LinkedHashMap<>();

    private final NamespaceMappings namespaceMappings;

    protected ConfigurationTreeBuilder(NamespaceMappings namespaceMappings) {
        Objects.requireNonNull(namespaceMappings);
        this.namespaceMappings = namespaceMappings;
    }

    public static OfNamespaceMappings ofNamespaceMappings(NamespaceMappings namespaceMappings) {
        return new OfNamespaceMappings(namespaceMappings);
    }

    public static WithElement withElement(NamespaceMappings namespaceMappings) {
        return new WithElement(namespaceMappings);
    }

    public T setText(String text) {
        this.text = text;
        return castThis();
    }

    public T addAttribute(String name, String value) {
        Objects.requireNonNull(name);
        if (value != null) {
            attributes.put(name, value);
        }
        return castThis();
    }

    public T addChild(String name, ConfigurationTree child) {
        this.children.computeIfAbsent(name, k -> new ArrayList<>()).add(child);
        return castThis();
    }

    protected void setTheElement(ArooaElement arooaElement) {
        this.element = arooaElement;
    }

    protected NamespaceMappings getNamespaceMappings() {
        return this.namespaceMappings;
    }

    public ConfigurationTree build() {
        return new Impl(this);
    }

    abstract public T newInstance();

    @SuppressWarnings("unchecked")
    T castThis() {
        return (T) this;
    }

    public static class OfNamespaceMappings {

        private final NamespaceMappings namespaceMappings;

        OfNamespaceMappings(NamespaceMappings namespaceMappings) {
            this.namespaceMappings = namespaceMappings;
        }

        public WithQualifiedTag withTags() {
            return new WithQualifiedTag(namespaceMappings);
        }

        public WithElement withElements() {
            return new WithElement(namespaceMappings);
        }
    }

    public static class WithElement extends ConfigurationTreeBuilder<WithElement> {

        protected WithElement(NamespaceMappings namespaceMappings) {
            super(namespaceMappings);
        }

        public WithElement newInstance() {
            return new WithElement(getNamespaceMappings());
        }

        public WithElement setElement(ArooaElement arooaElement) {
            URI uri = arooaElement.getUri();
            if (uri!= null
                    && getNamespaceMappings().getPrefixFor(uri) == null) {
                throw new IllegalArgumentException("No prefix for " + uri);
            }
            setTheElement(arooaElement);
            return this;
        }
    }

    public static class WithQualifiedTag extends ConfigurationTreeBuilder<WithQualifiedTag> {

        private WithQualifiedTag(NamespaceMappings namespaceMappings) {
            super(namespaceMappings);
        }

        public WithQualifiedTag newInstance() {
            return new WithQualifiedTag(getNamespaceMappings());
        }

        public WithQualifiedTag setTag(String tag) {
            setTheElement(getNamespaceMappings().elementFor(
                    Objects.requireNonNull(tag, "No Element")));
            return this;
        }

    }

    private static class Impl implements ConfigurationTree {

        private final NamespaceMappings namespaceMappings;

        private final ArooaElement element;

        private final Map<String, List<ConfigurationTree>> children;

        private final String text;

        Impl(ConfigurationTreeBuilder<?> builder) {

            this.namespaceMappings = builder.namespaceMappings;

            ArooaElement element = Objects.requireNonNull(builder.element,
                    "No Element");

            for (Map.Entry<String, String> entry: builder.attributes.entrySet()) {
                element = element.addAttribute(entry.getKey(), entry.getValue());
            }
            this.element = element;
            this.text = builder.text;
            this.children = new LinkedHashMap<>(builder.children);
        }

        @Override
        public ArooaElement getElement() {
            return this.element;
        }

        @Override
        public Optional<String> getText() {
            return Optional.ofNullable(this.text);
        }

        @Override
        public Set<String> getChildNames() {
            return this.children.keySet();
        }

        @Override
        public List<ConfigurationTree> getChildConfigurations(String name) {
            return this.children.get(name);
        }

        @Override
        public ArooaConfiguration toConfiguration(SaveOperation saveMethod) {

            return new ArooaConfiguration() {
                @Override
                public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parseParentContext) {

                    parseParentContext.getPrefixMappings().add(Impl.this.namespaceMappings);

                    parseTree(parseParentContext, Impl.this);

                    AtomicReference<P> documentContext = new AtomicReference<>();

                    ChildCatcher.watchRootContext(parseParentContext, documentContext::set);

                    return new ConfigurationHandle<P>() {
                        @Override
                        public void save() throws ArooaParseException {
                            Optional.ofNullable(saveMethod)
                                    .orElseThrow(() -> new UnsupportedOperationException("Unable to save"))
                                    .save(documentContext.get().getConfigurationNode());
                        }

                        @Override
                        public P getDocumentContext() {
                            return documentContext.get();
                        }
                    };
                }
            };
        }
    }

    static <P extends ParseContext<P>> void parseTree(P parentContext, ConfigurationTree tree) {

        ParseHandle<P> handle = parentContext.getElementHandler()
                .onStartElement(tree.getElement(), parentContext);

        P currentContext = handle.getContext();

        for (String name: tree.getChildNames()) {

            ParseHandle<P> nameHandle = currentContext.getElementHandler()
                    .onStartElement(new ArooaElement(name), currentContext);

            P nameContext = nameHandle.getContext();

            for (ConfigurationTree child: tree.getChildConfigurations(name)) {
                parseTree(nameContext, child);
            }

            ConfigurationNode<P> nameConfigurationNode = nameContext.getConfigurationNode();
            if (nameConfigurationNode == null) {
                throw new IllegalArgumentException("Null Configuration Node for Context " + currentContext);
            }

            nameHandle.init();
        }

        tree.getText().ifPresent(handle::addText);

        handle.init();
    }

}

