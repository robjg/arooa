package org.oddjob.arooa.json;

import org.junit.Test;
import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.NamespaceMappings;
import org.oddjob.arooa.parsing.SimpleParseContext;
import org.oddjob.arooa.parsing.SimplePrefixMappings;
import org.oddjob.arooa.xml.XMLArooaParser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class ConfigurationTreeBuilderTest {

    @Test
    public void testSingleElementParse() throws ArooaParseException {

        NamespaceMappings namespaceMappings = new SimplePrefixMappings();

        XMLArooaParser parser = new XMLArooaParser(namespaceMappings);

        ConfigurationTree tree = ConfigurationTreeBuilder
                .ofNamespaceMappings(namespaceMappings)
                .withTags()
                .setTag("a")
                .addAttribute("colour", "green")
                .build();

        AtomicReference<ArooaConfiguration> saved = new AtomicReference<>();
        ArooaConfiguration configuration = tree.toConfiguration(saved::set);

        ConfigurationHandle<SimpleParseContext> handle = parser.parse(configuration);

        String expected = "<a colour='green'/>";

        assertThat(parser.getXml(), isSimilarTo(expected));

        handle.save();

        XMLArooaParser parser2 = new XMLArooaParser(namespaceMappings);

        parser2.parse(saved.get());

        assertThat(parser2.getXml(), isSimilarTo(expected));
    }

    @Test
    public void testTextParse() throws ArooaParseException {

        NamespaceMappings namespaceMappings = new SimplePrefixMappings();

        XMLArooaParser parser = new XMLArooaParser(namespaceMappings);

        ConfigurationTree tree = ConfigurationTreeBuilder
                .ofNamespaceMappings(namespaceMappings)
                .withTags()
                .setTag("a")
                .setText("Apple")
                .build();

        AtomicReference<ArooaConfiguration> saved = new AtomicReference<>();
        ArooaConfiguration configuration = tree.toConfiguration(saved::set);

        ConfigurationHandle<SimpleParseContext> handle = parser.parse(configuration);

        String expected = "<a>Apple</a>";

        assertThat(parser.getXml(), isSimilarTo(expected).ignoreWhitespace());

        handle.save();

        XMLArooaParser parser2 = new XMLArooaParser(namespaceMappings);

        parser2.parse(saved.get());

        assertThat(parser2.getXml(), isSimilarTo(expected).ignoreWhitespace());
    }

    @Test
    public void testSimpleTreeParse() throws ArooaParseException {

        NamespaceMappings namespaceMappings = new SimplePrefixMappings();

        XMLArooaParser parser = new XMLArooaParser(namespaceMappings);

        ConfigurationTreeBuilder.WithQualifiedTag treeBuilder =
                ConfigurationTreeBuilder.ofNamespaceMappings(namespaceMappings)
                        .withTags();

        ConfigurationTree tree =
                treeBuilder.setTag("a")
                        .addChild("b", treeBuilder.newInstance()
                                .setTag("c")
                                .build())
                        .addChild("b", treeBuilder.newInstance()
                                .setTag("c")
                                .build())
                        .build();

        AtomicReference<ArooaConfiguration> saved = new AtomicReference<>();
        ArooaConfiguration configuration = tree.toConfiguration(saved::set);

        ConfigurationHandle<SimpleParseContext> handle = parser.parse(configuration);

        String expected = "<a><b><c/><c/></b></a>";

        assertThat(parser.getXml(), isSimilarTo(expected).ignoreWhitespace());

        handle.save();

        XMLArooaParser parser2 = new XMLArooaParser(namespaceMappings);

        parser2.parse(saved.get());

        assertThat(parser2.getXml(), isSimilarTo(expected).ignoreWhitespace());
    }

    @Test
    public void testWithNamespace() throws URISyntaxException {

        URI uri = new URI("arooa:foo");

        SimplePrefixMappings namespaceMappings = new SimplePrefixMappings();
        namespaceMappings.put("some", uri);

        ConfigurationTreeBuilder.WithQualifiedTag treeBuilder =
                ConfigurationTreeBuilder
                        .ofNamespaceMappings(namespaceMappings)
                        .withTags();

        treeBuilder.setTag("some:a");

        ConfigurationTree result = treeBuilder.build();

        assertThat(result.getElement(), is(new ArooaElement(uri, "a")));
    }

    @Test
    public void testWithElement() throws URISyntaxException {

        URI uri = new URI("arooa:foo");

        SimplePrefixMappings namespaceMappings = new SimplePrefixMappings();
        namespaceMappings.put("some", uri);

        ConfigurationTreeBuilder.WithElement treeBuilder =
                ConfigurationTreeBuilder.ofNamespaceMappings(namespaceMappings)
                .withElements();

        treeBuilder.setElement(new ArooaElement(uri, "a"));

        ConfigurationTree result = treeBuilder.build();

        assertThat(result.getElement(), is(new ArooaElement(uri, "a")));
    }
}
