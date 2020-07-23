package org.oddjob.arooa.json;

import org.oddjob.arooa.*;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.NamespaceMappings;
import org.oddjob.arooa.parsing.SimpleParseContext;

import java.net.URI;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Parse an {@link ArooaConfiguration} into an {@link ConfigurationTree}.
 */
public class ConfigurationTreeArooaParser implements ArooaParser<SimpleParseContext> {

    private final NamespaceMappings namespaceMappings;

    private final Consumer<ConfigurationTree> treeConsumer;

    private ConfigurationTree configurationTree;

    public ConfigurationTreeArooaParser(NamespaceMappings namespaceMappings) {
        this(namespaceMappings, null);
    }

    public ConfigurationTreeArooaParser(NamespaceMappings namespaceMappings, Consumer<ConfigurationTree> treeConsumer) {
        this.namespaceMappings = Objects.requireNonNull(namespaceMappings);
        if (treeConsumer == null) {
            this.treeConsumer = ct -> this.configurationTree = ct;
        } else {
            this.treeConsumer = treeConsumer;
        }
    }

    @Override
    public ConfigurationHandle<SimpleParseContext> parse(ArooaConfiguration configuration) throws ArooaParseException {

        return configuration.parse(
                SimpleParseContext.createRootContext()
                        .withActions(new ElementFunction(this.namespaceMappings, this.treeConsumer))
                        .andNamespaceMappings(this.namespaceMappings));
    }

    static class ElementFunction implements SimpleParseContext.ActionFunction {

        private final NamespaceMappings namespaceMappings;

        private final Consumer<ConfigurationTree> treeConsumer;

        ElementFunction(NamespaceMappings namespaceMappings, Consumer<ConfigurationTree> treeConsumer) {
            this.namespaceMappings = namespaceMappings;
            this.treeConsumer = treeConsumer;
        }

        @Override
        public SimpleParseContext.Actions onElement(ArooaElement element) {

            ConfigurationTreeBuilder.WithElement treeBuilder = ConfigurationTreeBuilder
                    .ofNamespaceMappings(this.namespaceMappings)
                    .withElements();

            treeBuilder.setElement(element);

            for (String name : element.getAttributes().getAttributeNames()) {
                treeBuilder.addAttribute(name, element.getAttributes().get(name));
            }

            return SimpleParseContext.actions()
                    .withChildActions(new KeyFunction(namespaceMappings, treeBuilder))
                    .withInitAction(() -> this.treeConsumer.accept(treeBuilder.build()))
                    .withTextConsumer(treeBuilder::setText)
                    .create();
        }
    }

    static class KeyFunction implements SimpleParseContext.ActionFunction {

        private final NamespaceMappings namespaceMappings;

        private final ConfigurationTreeBuilder<?> treeBuilder;

        KeyFunction(NamespaceMappings namespaceMappings, ConfigurationTreeBuilder<?> treeBuilder) {
            this.namespaceMappings = namespaceMappings;
            this.treeBuilder = treeBuilder;
        }

        @Override
        public SimpleParseContext.Actions onElement(ArooaElement element) {

            URI uri = element.getUri();
            if (uri != null) {
                throw new ArooaConfigurationException("Unexpected URI on key element " + element);
            }

            return SimpleParseContext.actions()
            .withChildActions(new ElementFunction(
                            namespaceMappings,
                            tree -> this.treeBuilder.addChild(element.getTag(), tree)))
                    .create();
        }
    }

    public ConfigurationTree getConfigurationTree() {
        return configurationTree;
    }
}
