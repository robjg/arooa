package org.oddjob.arooa.json;

import org.junit.Test;
import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.SimplePrefixMappings;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class ConfigurationTreeParserTest {

    @Test
    public void testWithNamespace() throws ArooaParseException, URISyntaxException {

        String xml = "<some:a xmlns:some='arooa:foo'/>";

        XMLConfiguration xmlConfiguration = new XMLConfiguration("XML", xml);

        URI uri = new URI("arooa:foo");

        SimplePrefixMappings namespaceMappings = new SimplePrefixMappings();
        namespaceMappings.put("some", uri);

        ConfigurationTreeArooaParser treeArooaParser =
                new ConfigurationTreeArooaParser(namespaceMappings);

        treeArooaParser.parse(xmlConfiguration);

        ConfigurationTree result = treeArooaParser.getConfigurationTree();

        assertThat(result.getElement(), is(new ArooaElement(uri, "a")));

        ArooaConfiguration treeConfiguration = result.toConfiguration(c -> {});

        XMLArooaParser xmlArooaParser = new XMLArooaParser();

        xmlArooaParser.parse(treeConfiguration);

        assertThat(xmlArooaParser.getXml(), isSimilarTo(xml).ignoreWhitespace());
    }
}
