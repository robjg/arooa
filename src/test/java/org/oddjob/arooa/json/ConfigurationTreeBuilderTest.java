package org.oddjob.arooa.json;

import org.junit.Test;
import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.xml.XMLArooaParser;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class ConfigurationTreeBuilderTest {

    @Test
    public void testSingleElementParse() throws ArooaParseException {

        XMLArooaParser parser = new XMLArooaParser();

        ConfigurationTree tree = ConfigurationTreeBuilder.newInstance()
                .setTag("a")
                .addAttribute("colour", "green")
                .build();

        AtomicReference<ArooaConfiguration> saved = new AtomicReference<>();
        ArooaConfiguration configuration = tree.toConfiguration(saved::set);

        ConfigurationHandle handle = parser.parse(configuration);

        String expected = "<a colour='green'/>";

        assertThat(parser.getXml(), isSimilarTo(expected));

        handle.save();

        XMLArooaParser parser2 = new XMLArooaParser();

        parser2.parse(saved.get());

        assertThat(parser2.getXml(), isSimilarTo(expected));
    }

    @Test
    public void testTextParse() throws ArooaParseException {

        XMLArooaParser parser = new XMLArooaParser();

        ConfigurationTree tree = ConfigurationTreeBuilder.newInstance()
                .setTag("a")
                .setText("Apple")
                .build();

        AtomicReference<ArooaConfiguration> saved = new AtomicReference<>();
        ArooaConfiguration configuration = tree.toConfiguration(saved::set);

        ConfigurationHandle handle = parser.parse(configuration);

        String expected = "<a>Apple</a>";

        assertThat(parser.getXml(), isSimilarTo(expected));

        handle.save();

        XMLArooaParser parser2 = new XMLArooaParser();

        parser2.parse(saved.get());

        assertThat(parser2.getXml(), isSimilarTo(expected));
    }

    @Test
    public void testSimpleTreeParse() throws ArooaParseException {

        XMLArooaParser parser = new XMLArooaParser();

        ConfigurationTree tree = ConfigurationTreeBuilder.newInstance()
                .setTag("a")
                .addChild("b", ConfigurationTreeBuilder.newInstance()
                        .setTag("c")
                        .build())
                .addChild("b", ConfigurationTreeBuilder.newInstance()
                        .setTag("c")
                        .build())
                .build();

        AtomicReference<ArooaConfiguration> saved = new AtomicReference<>();
        ArooaConfiguration configuration = tree.toConfiguration(saved::set);

        ConfigurationHandle handle = parser.parse(configuration);

        String expected = "<a><b><c/><c/></b></a>";

        assertThat(parser.getXml(), isSimilarTo(expected).ignoreWhitespace());

        handle.save();

        XMLArooaParser parser2 = new XMLArooaParser();

        parser2.parse(saved.get());

        assertThat(parser2.getXml(), isSimilarTo(expected).ignoreWhitespace());
    }
}
