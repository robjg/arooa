package org.oddjob.arooa.json;

import jakarta.json.Json;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;
import org.oddjob.arooa.parsing.NamespaceMappings;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Builder for {@link JsonArooaParser}.
 */
public class JsonArooaParserBuilder {

    private NamespaceMappings namespaceMappings;

    private boolean prettyPrinting;

    private Writer writer;

    private OutputStream outputStream;

    private Consumer<String> stringConsumer;


    public JsonArooaParserBuilder withNamespaceMappings(NamespaceMappings namespaceMappings) {
        this.namespaceMappings = namespaceMappings;
        return this;
    }

    public JsonArooaParserBuilder withPrettyPrinting() {
        this.prettyPrinting = true;
        return this;
    }

    public JsonArooaParserBuilder withWriter(Writer writer) {
        this.writer = writer;
        return this;
    }

    public JsonArooaParserBuilder withOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        return this;
    }

    public JsonArooaParserBuilder withStringConsumer(Consumer<String> stringConsumer) {
        this.stringConsumer = stringConsumer;
        return this;
    }


    public JsonArooaParser build() {

        NamespaceMappings namespaceMappings =
                Optional.ofNullable(this.namespaceMappings)
                        .orElse(NamespaceMappings.empty());

        Map<String, Object> config = new HashMap<>();
        if (this.prettyPrinting) {
            config.put(JsonGenerator.PRETTY_PRINTING, true);
        }

        JsonGeneratorFactory factory = Json.createGeneratorFactory(config);

        JsonGenerator generator= Optional.ofNullable(this.outputStream)
                .map(factory::createGenerator)
                .orElse(null);

        if (generator == null) {

            generator = Optional.ofNullable(this.writer)
                    .map(factory::createGenerator)
                    .orElse(null);
        }

        if (generator == null) {

            generator = Optional.ofNullable(this.stringConsumer)
                    .map(c -> factory.createGenerator(new StringWriter() {
                        @Override
                        public void close() throws IOException {
                            super.close();
                            c.accept(this.toString());
                        }
                    }))
                    .orElse(null);
        }

        if (generator == null) {
            throw new IllegalArgumentException("Nothing to create JSON generator with.");
        }

        return new JsonArooaParser(namespaceMappings, generator);
    }
}
