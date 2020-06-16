package org.oddjob.arooa.json;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaParser;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.Location;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Parse an {@link ArooaConfiguration} into a JSON string.
 */
public class JsonArooaParser implements ArooaParser {

    private final JsonGenerator jsonGenerator;

    private final Closeable done;

    public JsonArooaParser(OutputStream out) {
        this.jsonGenerator = Json.createGenerator(out);
        done = out;
    }

    public JsonArooaParser(Consumer<String> stringConsumer) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.jsonGenerator = Json.createGenerator(out);
        done = () -> stringConsumer.accept(new String(out.toByteArray()));
    }

    @Override
    public ConfigurationHandle parse(ArooaConfiguration configuration) throws ArooaParseException {

        AtomicReference<ConfigurationTree> treeRef = new AtomicReference<>();

        ArooaParser treeParser = new ConfigurationTreeArooaParser(treeRef::set);

        ConfigurationHandle handle = treeParser.parse(configuration);

        parseStart(treeRef.get());

        jsonGenerator.close();

        try {
            done.close();
        } catch (IOException e) {
            throw new ArooaParseException("Failed to close",
                    new Location(configuration.toString(), 0,0));
        }

        return handle;
    }

    void parseStart(ConfigurationTree tree) {

        jsonGenerator.writeStartObject();

        parse(tree);
        jsonGenerator.writeEnd();
    }

    void parse(ConfigurationTree tree) {

        jsonGenerator.write(JsonConfiguration.ELEMENT_FIELD, tree.getElement().getTag());
        tree.getText().ifPresent(text -> jsonGenerator.write(JsonConfiguration.TEXT_FIELD, text));

        for (String name : tree.getElement().getAttributes().getAttributeNames()) {
            jsonGenerator.write(name, tree.getElement().getAttributes().get(name));
        }

        for (String name: tree.getChildNames()) {

            List<ConfigurationTree> children = tree.getChildConfigurations(name);
            if (children.size() == 1) {
                jsonGenerator.writeStartObject(name);
                parse(children.get(0));
            }
            else {
                jsonGenerator.writeStartArray(name);
                children.forEach(this::parseStart);
            }
            jsonGenerator.writeEnd();
        }
    }

}
