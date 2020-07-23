package org.oddjob.arooa.xml;

import org.junit.Test;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.NamespaceMappings;
import org.oddjob.arooa.parsing.ParseContext;
import org.oddjob.arooa.parsing.SimplePrefixMappings;

import java.net.URI;

import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

public class XMLArooaParserTest {

    static final String EOL = System.getProperty("line.separator");

    @Test
    public void testRoundTrip() throws Exception {

        String xml = "<comp>" +
                "<a><b/></a>" +
                "<x><y/></x>" +
                "</comp>";

        XMLArooaParser parser = new XMLArooaParser(NamespaceMappings.empty());
        ConfigurationHandle<?> handle = parser.parse(new XMLConfiguration("Test", xml));

        String expected = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + EOL +
                "<comp>" + EOL +
                "    <a>" + EOL +
                "        <b/>" + EOL +
                "    </a>" + EOL +
                "    <x>" + EOL +
                "        <y/>" + EOL +
                "    </x>" + EOL +
                "</comp>" + EOL;

        assertThat(parser.getXml(), isIdenticalTo(expected));

        ParseContext<?> docContext = handle.getDocumentContext();

        parser.parse(docContext.getConfigurationNode());

        assertThat(parser.getXml(), isIdenticalTo(expected));
    }

    @Test
    public void testRoundTripNS() throws Exception {

        SimplePrefixMappings namespaceMappings = new SimplePrefixMappings();
        namespaceMappings.put("fruit", new URI("http://www.rgordon.co.uk/fruit"));
        namespaceMappings.put("", new URI("http://www.rgordon.co.uk/arooa"));

        String xml = "<comp xmlns='http://www.rgordon.co.uk/arooa'" +
                "		xmlns:fruit='http://www.rgordon.co.uk/fruit'>" +
                "<a><fruit:b/></a>" +
                "<x><fruit:y/></x>" +
                "</comp>";

        XMLArooaParser parser1 = new XMLArooaParser();
        ConfigurationHandle<?> handle = parser1.parse(new XMLConfiguration("Test", xml));

        String expected = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + EOL +
                "<comp xmlns=\"http://www.rgordon.co.uk/arooa\"" + EOL +
                "      xmlns:fruit=\"http://www.rgordon.co.uk/fruit\">" + EOL +
                "    <a>" + EOL +
                "        <fruit:b/>" + EOL +
                "    </a>" + EOL +
                "    <x>" + EOL +
                "        <fruit:y/>" + EOL +
                "    </x>" + EOL +
                "</comp>" + EOL;

        assertThat(parser1.getXml(), isIdenticalTo(expected));

        ParseContext<?> docContext = handle.getDocumentContext();

		XMLArooaParser parser2 = new XMLArooaParser(docContext.getPrefixMappings());

        parser2.parse(docContext.getConfigurationNode());

        assertThat(parser2.getXml(), isIdenticalTo(expected));
    }
}
