package org.oddjob.arooa.json;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ChildCatcher;
import org.oddjob.arooa.runtime.ConfigurationNode;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Builds an {@link ConfigurationTree}.
 */
public class ConfigurationTreeBuilder {

    private String tag;

    private String text;

    private final Map<String, String> attributes = new LinkedHashMap<>();

    private final Map<String, List<ConfigurationTree>> children = new LinkedHashMap<>();

    public static ConfigurationTreeBuilder newInstance() {
        return new ConfigurationTreeBuilder();
    }

    public ConfigurationTreeBuilder setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public ConfigurationTreeBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public ConfigurationTreeBuilder addAttribute(String name, String value) {
        attributes.put(name, value);
        return this;
    }

    public ConfigurationTreeBuilder addChild(String name, ConfigurationTree child) {
        this.children.computeIfAbsent(name, k -> new ArrayList<>()).add(child);
        return this;
    }

    public ConfigurationTree build() {
        return new Impl(this);
    }

    private static class Impl implements ConfigurationTree {

        private final ArooaElement element;

        private final Map<String, List<ConfigurationTree>> children;

        private final String text;

        Impl(ConfigurationTreeBuilder builder) {

            ArooaElement element = new ArooaElement(
                     Objects.requireNonNull(builder.tag, "No Element"));
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

            return parseParentContext -> {
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

