package org.oddjob.arooa.json;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.Location;
import org.oddjob.arooa.parsing.NamespaceMappings;

import javax.json.Json;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

/**
 * An {@link ArooaConfiguration} based on a source of JSON.
 */
public class JsonConfiguration implements ArooaConfiguration {

    public static final String ELEMENT_FIELD = "@element";

    public static final String TEXT_FIELD = "@text";

    private NamespaceMappings namespaceMappings;

    interface SourceFactory {
        JsonParser createInput() throws IOException;

        void save(ArooaConfiguration rootConfiguration) throws ArooaParseException;
    }


    private final SourceFactory sourceFactory;


    public JsonConfiguration(String jsonString) {
        this.sourceFactory = new SourceFactory() {
            @Override
            public JsonParser createInput() throws IOException {
                return Json.createParser(new StringReader(jsonString));
            }

            @Override
            public void save(ArooaConfiguration rootConfiguration) throws ArooaParseException {

            }

            @Override
            public String toString() {
                return jsonString;
            }
        };
    }

    public JsonConfiguration withNamespaceMappings(NamespaceMappings namespaceMappings) {
        this.namespaceMappings = namespaceMappings;
        return this;
    }

    /*
     * (non-Javadoc)
     * @see org.oddjob.arooa.ArooaConfiguration#parse(org.oddjob.arooa.parsing.ArooaContext)
     */
    public ConfigurationHandle parse(ArooaContext parentContext)
            throws ArooaParseException {

        JsonParser jsonParser;
        try {
            jsonParser = this.sourceFactory.createInput();
        }
        catch (IOException e) {
            throw new ArooaException(e);
        }

        if (!jsonParser.hasNext() || jsonParser.next() != Event.START_OBJECT) {
            throw new ArooaParseException("Invalid JSON",
                    toArooaLocation(jsonParser));
        }

        NamespaceMappings namespaceMappings =
                Optional.ofNullable(this.namespaceMappings)
                .orElseGet(() -> parentContext.getSession().getArooaDescriptor());

        try {
            ConfigurationTree tree = recurse(jsonParser,
                    namespaceMappings);

            return tree.toConfiguration(sourceFactory::save).parse(parentContext);
        }
        catch (ArooaParseException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw new ArooaParseException(e.getMessage(),
                    toArooaLocation(jsonParser),
                    e);
        }
    }

    ConfigurationTree recurse(JsonParser jsonParser, NamespaceMappings prefixMapping) throws ArooaParseException {

        ConfigurationTreeBuilder.WithQualifiedTag treeBuilder = ConfigurationTreeBuilder
                .withTag(prefixMapping);

        String key = null;

        while(jsonParser.hasNext()) {

            Event e = jsonParser.next();

            switch (e) {
                case KEY_NAME:
                    key = jsonParser.getString();
                    break;
                case VALUE_STRING:
                case VALUE_NUMBER:
                case VALUE_TRUE:
                case VALUE_FALSE:
                    String value = jsonParser.getString();
                    if (ELEMENT_FIELD.equals(key)) {
                        treeBuilder.setTag(value);
                    }
                    else if (TEXT_FIELD.equals(key)) {
                        treeBuilder.setText(value);
                    }
                    else {
                        treeBuilder.addAttribute(key, value);
                    }
                    key = null;
                    break;
                case START_OBJECT:
                    if (key == null) {
                        throw new ArooaParseException("No key", toArooaLocation(jsonParser));
                    }
                    treeBuilder.addChild(key, recurse(jsonParser, prefixMapping));
                    break;
                case END_OBJECT:
                    return treeBuilder.build();
                case START_ARRAY:
                case END_ARRAY:
                        // Arrays allowed so long as key is set.
                    if (key == null) {
                        throw new ArooaParseException("Arrays without keys currently unsupported",
                                toArooaLocation(jsonParser));
                    }
                    break;
                default:
                    throw new ArooaParseException("Can't handle " + e,
                            toArooaLocation(jsonParser));
            }
        }
        throw new ArooaParseException("Unexpected end of stream",
                toArooaLocation(jsonParser));
    }

     Location toArooaLocation(JsonParser jsonParser) {
         JsonLocation jsonLocation = jsonParser.getLocation();
         return new Location(sourceFactory.toString(),
                 new Long(jsonLocation.getLineNumber()).intValue(),
                 new Long(jsonLocation.getColumnNumber()).intValue());
    }
}
