package org.oddjob.arooa.json;

import org.oddjob.arooa.*;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;

import java.util.function.Consumer;

/**
 * Parse an {@link ArooaConfiguration} into an {@link ConfigurationTree}.
 */
public class ConfigurationTreeArooaParser implements ArooaParser {

    private final Consumer<ConfigurationTree> treeConsumer;

    public ConfigurationTreeArooaParser(Consumer<ConfigurationTree> treeConsumer) {
        this.treeConsumer = treeConsumer;
    }

    @Override
    public ConfigurationHandle parse(ArooaConfiguration configuration) throws ArooaParseException {

        return configuration.parse(
                MinimumParseContext.createRootContext(
                        new ElementHandler(this.treeConsumer)));
    }

    static class ElementHandler implements ArooaHandler {

        private final Consumer<ConfigurationTree> treeConsumer;

        ElementHandler(Consumer<ConfigurationTree> treeConsumer) {
            this.treeConsumer = treeConsumer;
        }


        @Override
        public ArooaContext onStartElement(ArooaElement element, ArooaContext parentContext) throws ArooaConfigurationException {

            ConfigurationTreeBuilder treeBuilder = ConfigurationTreeBuilder.newInstance();
            treeBuilder.setTag(element.getTag());

            for (String name : element.getAttributes().getAttributeNames()) {
                treeBuilder.addAttribute(name, element.getAttributes().get(name));
            }

            return MinimumParseContext.
                    withOptions()
                    .childHandler(new KeyHandler(treeBuilder))
                    .initCallback(() -> this.treeConsumer.accept(treeBuilder.build()))
                    .textCallback(treeBuilder::setText)
                    .createChild(element, parentContext);
        }

    }

    static class KeyHandler implements ArooaHandler {

        private final ConfigurationTreeBuilder treeBuilder;

        KeyHandler(ConfigurationTreeBuilder treeBuilder) {
            this.treeBuilder = treeBuilder;
        }

        @Override
        public ArooaContext onStartElement(ArooaElement element, ArooaContext parentContext)
                throws ArooaConfigurationException {

            return MinimumParseContext.withOptions()
                    .childHandler(new ElementHandler(
                    tree -> this.treeBuilder.addChild(element.getTag(), tree)))
            .createChild(element, parentContext);
        }
    }
}
