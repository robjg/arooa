package org.oddjob.arooa.json;

import org.json.JSONException;
import org.junit.Test;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.forms.FormsLookup;
import org.oddjob.arooa.forms.FormsLookupFromDescriptorTest;
import org.oddjob.arooa.utils.FileUtils;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;

public class JsonArooaParserTest {

    @Test
    public void parseWithNamespace() throws ArooaParseException, IOException, URISyntaxException, JSONException {

        XMLConfiguration xmlConfiguration = new XMLConfiguration(
                FormsLookupFromDescriptorTest.class.getResource("FormsLookupFromConfigurationExpected.xml"));

        StringWriter stringWriter = new StringWriter();

        JsonArooaParser test = new JsonArooaParserBuilder()
                .withNamespaceMappings(FormsLookup.formsNamespaces())
                .withPrettyPrinting()
                .withWriter(stringWriter)
                .build();

        test.parse(xmlConfiguration);

        String expected = FileUtils.readToString(
                getClass().getResource("JsonArooaParserNamespaceExpected.json"));

        JSONAssert.assertEquals(
                stringWriter.toString(),
                expected,
                JSONCompareMode.LENIENT);

    }
}
