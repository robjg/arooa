package org.oddjob.arooa.json;

import jakarta.json.stream.JsonGenerator;
import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaParser;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.NamespaceMappings;
import org.oddjob.arooa.parsing.SimpleParseContext;

import java.util.List;

/**
 * Parse an {@link ArooaConfiguration} into a JSON string.
 */
public class JsonArooaParser implements ArooaParser<SimpleParseContext> {

    private final NamespaceMappings namespaceMappings;

    private final JsonGenerator jsonGenerator;

    public JsonArooaParser(NamespaceMappings namespaceMappings, JsonGenerator jsonGenerator) {
        this.namespaceMappings = namespaceMappings;
        this.jsonGenerator = jsonGenerator;
    }

    @Override
    public ConfigurationHandle<SimpleParseContext> parse(ArooaConfiguration configuration) throws ArooaParseException {

        ConfigurationTreeArooaParser treeParser = new ConfigurationTreeArooaParser(namespaceMappings);

        ConfigurationHandle<SimpleParseContext> handle = treeParser.parse(configuration);

        parseStart(treeParser.getConfigurationTree());

        jsonGenerator.close();

        return handle;
    }

    void parseStart(ConfigurationTree tree) {

        jsonGenerator.writeStartObject();

        parse(tree);
        jsonGenerator.writeEnd();
    }

    void parse(ConfigurationTree tree) {

        jsonGenerator.write(JsonConfiguration.ELEMENT_FIELD,
                namespaceMappings.getQName(tree.getElement()).toString());
        tree.getText().ifPresent(text -> jsonGenerator.write(JsonConfiguration.TEXT_FIELD, text));

        for (String name : tree.getElement().getAttributes().getAttributeNames()) {
            jsonGenerator.write(name, tree.getElement().getAttributes().get(name));
        }

        for (String name : tree.getChildNames()) {

            List<ConfigurationTree> children = tree.getChildConfigurations(name);
            if (children.size() == 1) {
                jsonGenerator.writeStartObject(name);
                parse(children.get(0));
            } else {
                jsonGenerator.writeStartArray(name);
                children.forEach(this::parseStart);
            }
            jsonGenerator.writeEnd();
        }
    }
}
