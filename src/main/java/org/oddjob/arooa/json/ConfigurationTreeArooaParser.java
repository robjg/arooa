package org.oddjob.arooa.json;

import org.oddjob.arooa.*;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.NamespaceMappings;

import java.net.URI;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Parse an {@link ArooaConfiguration} into an {@link ConfigurationTree}.
 */
public class ConfigurationTreeArooaParser implements ArooaParser {

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
    public ConfigurationHandle parse(ArooaConfiguration configuration) throws ArooaParseException {

        return configuration.parse(
                MinimumParseContext.createRootContext(
                        new ElementHandler(this.namespaceMappings, this.treeConsumer)));
    }

    static class ElementHandler implements ArooaHandler {

        private final NamespaceMappings namespaceMappings;

        private final Consumer<ConfigurationTree> treeConsumer;

        ElementHandler(NamespaceMappings namespaceMappings, Consumer<ConfigurationTree> treeConsumer) {
            this.namespaceMappings = namespaceMappings;
            this.treeConsumer = treeConsumer;
        }


        @Override
        public ArooaContext onStartElement(ArooaElement element, ArooaContext parentContext) throws ArooaConfigurationException {

            ConfigurationTreeBuilder.WithElement treeBuilder = ConfigurationTreeBuilder
                    .withElement(this.namespaceMappings)
                    .setElement(element);

            for (String name : element.getAttributes().getAttributeNames()) {
                treeBuilder.addAttribute(name, element.getAttributes().get(name));
            }

            return MinimumParseContext.
                    withOptions()
                    .childHandler(new KeyHandler(namespaceMappings, treeBuilder))
                    .initCallback(() -> this.treeConsumer.accept(treeBuilder.build()))
                    .textCallback(treeBuilder::setText)
                    .createChild(element, parentContext);
        }

    }

    static class KeyHandler implements ArooaHandler {

        private final NamespaceMappings namespaceMappings;

        private final ConfigurationTreeBuilder<?> treeBuilder;

        KeyHandler(NamespaceMappings namespaceMappings, ConfigurationTreeBuilder<?> treeBuilder) {
            this.namespaceMappings = namespaceMappings;
            this.treeBuilder = treeBuilder;
        }

        @Override
        public ArooaContext onStartElement(ArooaElement element, ArooaContext parentContext)
                throws ArooaConfigurationException {

            URI uri = element.getUri();
            if (uri != null) {
                throw new ArooaConfigurationException("Unexpected URI on key element " + element);
            }

            return MinimumParseContext.withOptions()
                    .childHandler(new ElementHandler(
                            namespaceMappings,
                            tree -> this.treeBuilder.addChild(element.getTag(), tree)))
                    .createChild(element, parentContext);
        }
    }

    public ConfigurationTree getConfigurationTree() {
        return configurationTree;
    }
}
