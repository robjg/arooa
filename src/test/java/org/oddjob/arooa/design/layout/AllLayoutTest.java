package org.oddjob.arooa.design.layout;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.deploy.ArooaDescriptorDescriptor;
import org.oddjob.arooa.deploy.ListDescriptor;
import org.oddjob.arooa.deploy.URLDescriptorFactory;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLArooaParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class AllLayoutTest {

    @Test
    public void testFromAFormAndBack() throws ArooaParseException, IOException {

        URL descriptorUrl = Objects.requireNonNull(
                DesignerForEverythingMain.class.getResource("ThingDescriptor.xml"));

        ArooaDescriptor descriptor = new URLDescriptorFactory(descriptorUrl)
                .createDescriptor(DesignerForEverythingMain.class.getClassLoader());

        InstantiationContext instantiationContext = new InstantiationContext(
                ArooaType.COMPONENT, new SimpleArooaClass(ThingWithEveryLayout.class));

        ArooaElement everything = new ArooaElement("thing");

        DesignFactory designFactory = descriptor.getElementMappings()
                .designFor(everything, instantiationContext);

        ArooaSession session = new StandardArooaSession(
                new ListDescriptor(new ArooaDescriptorDescriptor(), descriptor));

        DesignableInfo designableInfo = new DesignToLayoutConfig(session)
                .configurationFor(designFactory, everything);


        ArooaConfiguration configuration = designableInfo.getDesignConfiguration();

        XMLArooaParser parser = new XMLArooaParser();

        parser.parse(configuration);

        String expected = convertInputStreamToString(
                getClass().getResourceAsStream("ThingDesignForm.xml"));

        assertThat(parser.getXml(), isSimilarTo(expected).ignoreWhitespace());

        Map<String, String[]> propertyOptions = designableInfo.getPropertyOptions();

        assertThat(propertyOptions.keySet(),
                Matchers.containsInAnyOrder("single", "indexed", "mapped"));

        assertThat(Arrays.asList(propertyOptions.get("single")),
                Matchers.hasItems("value", "bean", "arooa:descriptor"));
    }

    private static String convertInputStreamToString(InputStream inputStream)
            throws IOException {

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        return result.toString(StandardCharsets.UTF_8.name());

    }
}
