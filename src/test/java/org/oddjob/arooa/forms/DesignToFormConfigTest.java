package org.oddjob.arooa.forms;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.deploy.ArooaDescriptorDescriptor;
import org.oddjob.arooa.deploy.ListDescriptor;
import org.oddjob.arooa.deploy.URLDescriptorFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignParser;
import org.oddjob.arooa.design.DesignSeedContext;
import org.oddjob.arooa.design.GenericDesignFactory;
import org.oddjob.arooa.design.layout.DesignerForEverythingMain;
import org.oddjob.arooa.design.screem.NullForm;
import org.oddjob.arooa.json.JsonArooaParser;
import org.oddjob.arooa.json.JsonArooaParserBuilder;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.utils.FileUtils;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.xmlunit.matchers.CompareMatcher;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;

public class DesignToFormConfigTest {

    @Test
    public void test() throws ArooaParseException, IOException, URISyntaxException {


        URL descriptorUrl = Objects.requireNonNull(
                DesignerForEverythingMain.class.getResource("ThingDescriptor.xml"));

        ArooaDescriptor descriptor = new URLDescriptorFactory(descriptorUrl)
                .createDescriptor(DesignerForEverythingMain.class.getClassLoader());

        ArooaSession session = new StandardArooaSession(
                new ListDescriptor(new ArooaDescriptorDescriptor(), descriptor));

        File configFile = new File(DesignerForEverythingMain.class.getResource("ThingConfig.xml").getFile());

        XMLConfiguration config = new XMLConfiguration(configFile);

        DesignParser parser = new DesignParser(session);
        parser.setArooaType(ArooaType.COMPONENT);

        parser.parse(config);

        DesignInstance designInstance = parser.getDesign();

        DesignToFormConfig test = new DesignToFormConfig();

        ArooaConfiguration result = test.configurationFor(designInstance);

        XMLArooaParser xmlParser = new XMLArooaParser(FormsLookup.formsNamespaces());
        xmlParser.parse(result);

        String expected = FileUtils.readToString(
                getClass().getResource("FormsLookupFromConfigurationExpected.xml"));

        assertThat(xmlParser.getXml(), CompareMatcher.isSimilarTo(expected));
    }


    @Test
    public void testBean() throws ArooaParseException, IOException, URISyntaxException {


        URL descriptorUrl = Objects.requireNonNull(
                DesignToFormConfigTest.class.getResource("FruitDescriptor.xml"));

        ArooaDescriptor descriptor = new URLDescriptorFactory(descriptorUrl)
                .createDescriptor(DesignerForEverythingMain.class.getClassLoader());

        ArooaSession session = new StandardArooaSession(
                new ListDescriptor(new ArooaDescriptorDescriptor(), descriptor));

        File configFile = new File(DesignToFormConfigTest
                .class.getResource("AppleBagConfig.xml").getFile());

        XMLConfiguration config = new XMLConfiguration(configFile);

        DesignParser parser = new DesignParser(session);
        parser.setArooaType(ArooaType.COMPONENT);

        parser.parse(config);

        DesignInstance designInstance = parser.getDesign();

        DesignToFormConfig test = new DesignToFormConfig();

        ArooaConfiguration result = test.configurationFor(designInstance);

        XMLArooaParser xmlParser = new XMLArooaParser(FormsLookup.formsNamespaces());
        xmlParser.parse(result);

        System.out.println(xmlParser.getXml());

        String expected = FileUtils.readToString(
                getClass().getResource("DesignToFormBeanExpected.xml"));

        assertThat(xmlParser.getXml(), CompareMatcher.isSimilarTo(expected));

    }

    public class ThingWithNoProps {
    }

    @Test
    public void testForNullForm() throws ArooaParseException, JSONException {

        GenericDesignFactory factory = new GenericDesignFactory(
                new SimpleArooaClass(ThingWithNoProps.class));

        DesignInstance design = factory.createDesign(
                new ArooaElement("foo"),
                new DesignSeedContext(ArooaType.COMPONENT, new StandardArooaSession()));

        assertThat(design.detail() instanceof NullForm, Matchers.is(true));

        DesignToFormConfig test = new DesignToFormConfig();

        ArooaConfiguration config = test.configurationFor(design);

        StringWriter stringWriter = new StringWriter();

        JsonArooaParser parser = new JsonArooaParserBuilder()
                .withNamespaceMappings(FormsLookup.formsNamespaces())
                .withPrettyPrinting()
                .withWriter(stringWriter)
                .build();

        parser.parse(config);

        String expected = "{ \"@element\": \"" + DesignToFormConfig.FORMS_FORM + "\"," +
                "\"element\": \"foo\"" +
                "}";

        JSONAssert.assertEquals(
                stringWriter.toString(),
                expected,
                JSONCompareMode.LENIENT);
    }
}