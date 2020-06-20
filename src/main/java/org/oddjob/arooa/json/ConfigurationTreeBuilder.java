package org.oddjob.arooa.json;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ChildCatcher;
import org.oddjob.arooa.parsing.NamespaceMappings;
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

    public static WithQualifiedTag withTag(NamespaceMappings namespaceMappings) {
        return new WithQualifiedTag(namespaceMappings);
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

    T castThis() {
        return (T) this;
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

        Impl(ConfigurationTreeBuilder builder) {

            this.namespaceMappings = builder.namespaceMappings;

            ArooaElement element = Objects.requireNonNull(builder.element,
                    "No Element");

            Map<String, String> atts = builder.attributes;
            for (Map.Entry<String, String> entry: atts.entrySet()) {
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

            return parseParentContext -> {

                parseParentContext.getPrefixMappings().add(this.namespaceMappings);

                parseTree(parseParentContext, Impl.this);

                AtomicReference<ArooaContext> documentContext = new AtomicReference<>();

                ChildCatcher.watchRootContext(parseParentContext, documentContext::set);

                return new ConfigurationHandle() {
                    @Override
                    public void save() throws ArooaParseException {
                        Optional.ofNullable(saveMethod)
                                .orElseThrow(() -> new UnsupportedOperationException("Unable to save"))
                                .save(documentContext.get().getConfigurationNode());
                    }

                    @Override
                    public ArooaContext getDocumentContext() {
                        return documentContext.get();
                    }
                };
            };
        }

    }

    static void parseTree(ArooaContext parentContext, ConfigurationTree tree) {

        ArooaContext currentContext = parentContext.getArooaHandler()
                .onStartElement(tree.getElement(), parentContext);

        for (String name: tree.getChildNames()) {

            ArooaContext nameContext = currentContext.getArooaHandler()
                    .onStartElement(new ArooaElement(name), currentContext);

            for (ConfigurationTree child: tree.getChildConfigurations(name)) {
                parseTree(nameContext, child);
            }

            ConfigurationNode nameConfigurationNode = nameContext.getConfigurationNode();
            if (nameConfigurationNode == null) {
                throw new IllegalArgumentException("Null Configuration Node for Context " + currentContext);
            }

            // order is important here:
            // add node before init() so indexed properties
            // know their index.
            int index = currentContext.getConfigurationNode()
                    .insertChild(nameConfigurationNode);

            try {
                nameContext.getRuntime().init();
            } catch (RuntimeException e) {
                currentContext.getConfigurationNode().removeChild(index);
                throw e;
            }
        }

        ConfigurationNode currentConfigurationNode = currentContext.getConfigurationNode();
        if (currentConfigurationNode == null) {
            throw new IllegalArgumentException("Null Configuration Node for Context " + currentContext);
        }

        tree.getText().ifPresent(currentConfigurationNode::addText);

        // order is important here:
        // add node before init() so indexed properties
        // know their index.
        int index = parentContext.getConfigurationNode()
                .insertChild(currentConfigurationNode);

        try {
            currentContext.getRuntime().init();
        } catch (RuntimeException e) {
            parentContext.getConfigurationNode().removeChild(index);
            throw e;
        }
    }

}

