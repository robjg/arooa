package org.oddjob.arooa.parsing;

import org.junit.Test;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.MockNamespaceDescriptor;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class QTagConfigurationTest {

    @Test
    public void testParse() throws Exception {

        SimplePrefixMappings namespaceMappings = new SimplePrefixMappings();
        namespaceMappings.put("fruit", new URI("http://fruit"));

        QTagConfiguration test = new QTagConfiguration(
                new QTag("fruit",
                        new ArooaElement(new URI("http://fruit"), "apple")));

        XMLArooaParser parser = new XMLArooaParser(namespaceMappings);

        parser.parse(test);

        String expected = "<fruit:apple xmlns:fruit=\"http://fruit\"/>" + System.getProperty("line.separator");

        assertThat(parser.getXml(), isSimilarTo(expected));
    }

    /**
     * Try to simulate what happens when a new node is added to an existing configuration
     * and then saved.
     *
     * @throws ArooaParseException
     * @throws URISyntaxException
     */
    @Test
    public void testParse1() throws Exception {

		SimplePrefixMappings namespaceMappings = new SimplePrefixMappings();
		namespaceMappings.put("fruit", new URI("http://fruit"));

		ArooaDescriptor descriptor = new MockNamespaceDescriptor(namespaceMappings);

        Object root = new Object();

        XMLConfiguration config = new XMLConfiguration("TEST", "<veg/>");

        final AtomicReference<String> savedXML = new AtomicReference<>();
        config.setSaveHandler(savedXML::set);

        StandardArooaParser parser = new StandardArooaParser(root, descriptor);

        ConfigurationHandle<ArooaContext> handle = parser.parse(config);

        QTagConfiguration test = new QTagConfiguration(
                new QTag("fruit",
                        new ArooaElement(new URI("http://fruit"), "apple")));

        CutAndPasteSupport.ReplaceResult<ArooaContext> replaceResult =
                CutAndPasteSupport.replace(handle.getDocumentContext().getParent(),
                        handle.getDocumentContext(), test);

        assertThat(replaceResult.getException(), nullValue());

        handle.save();

        String expected = "<fruit:apple xmlns:fruit=\"http://fruit\"/>";

        assertThat(savedXML.get(), isSimilarTo(expected).ignoreWhitespace());
    }
}
