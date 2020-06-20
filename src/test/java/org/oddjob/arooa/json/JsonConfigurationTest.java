package org.oddjob.arooa.json;

import org.json.JSONException;
import org.junit.Test;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.SimplePrefixMappings;
import org.oddjob.arooa.standard.StandardFragmentParser;
import org.oddjob.arooa.utils.ListSetterHelper;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JsonConfigurationTest {

    @Test
    public void testParseFromSimpleString() throws ArooaParseException, JSONException {

        String jsonString = "{\n" +
                "    \"@element\": \"oddjob\"," +
                "    \"id\": \"this\",\n" +
                "    \"job\": {\n" +
                "        \"@element\": \"echo\",\n" +
                "        \"@text\": \"Hello World\",\n" +
                "        \"name\": \"Greeting\",\n" +
                "        \"out\": {\n" +
                "            \"@element\": \"buffer\"\n" +
                "        }\n" +
                "    }\n" +
                "}";

        JsonConfiguration test = new JsonConfiguration(jsonString)
                .withNamespaceMappings(new SimplePrefixMappings());

        XMLArooaParser parser = new XMLArooaParser();

        ConfigurationHandle handle = parser.parse(test);

        AtomicReference<String> result = new AtomicReference<>();

        JsonArooaParser jsonParser = new JsonArooaParser(new SimplePrefixMappings(), result::set);

        jsonParser.parse(handle.getDocumentContext().getConfigurationNode());

        JSONAssert.assertEquals(
                jsonString, result.get(), JSONCompareMode.LENIENT);
    }

    public static class ColourBean {

        private final List<String> colours = new ArrayList<>();

        public void setColours(int index, String colour) {
            new ListSetterHelper<>(colours).set(index, colour);
        }
    }

    @Test
    public void testCreateBean() throws ArooaParseException, JSONException {

        String jsonString = "{" +
                "\"@element\": \"bean\"," +
                "\"class\": \"" + ColourBean.class.getName() + "\"," +
                "\"colours\": [" +
                "  {\"@element\": \"value\"," +
                "   \"value\": \"Red\" }," +
                "  {\"@element\": \"value\"," +
                "   \"value\": \"Green\" }" +
                " ] }";

        StandardFragmentParser parser = new StandardFragmentParser();

        ConfigurationHandle handle = parser.parse(new JsonConfiguration(jsonString));

        ColourBean bean = (ColourBean) parser.getRoot();

        assertThat(bean.colours, is(Arrays.asList("Red", "Green")));

        AtomicReference<String> result = new AtomicReference<>();

        JsonArooaParser jsonParser = new JsonArooaParser(new SimplePrefixMappings(), result::set);

        jsonParser.parse(handle.getDocumentContext().getConfigurationNode());

        JSONAssert.assertEquals(
                jsonString, result.get(), JSONCompareMode.LENIENT);
    }

}